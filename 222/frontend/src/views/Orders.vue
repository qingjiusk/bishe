<template>
  <div class="orders">
    <h2>{{ t('order.title') }}</h2>
    <el-tabs v-model="activeTab" @tab-change="loadOrders">
      <el-tab-pane :label="t('order.allOrders')" name="" />
      <el-tab-pane :label="t('order.pending')" name="1" />
      <el-tab-pane :label="t('order.shipped')" name="2" />
      <el-tab-pane :label="t('order.received')" name="3" />
    </el-tabs>
    <div v-loading="loading">
      <el-empty v-if="orders.length === 0" description="暂无订单" />
      <div v-else class="order-list">
        <el-card v-for="order in orders" :key="order.id" class="order-card">
          <div class="order-header">
            <span>{{ t('order.orderNo') }}: {{ order.orderNo }}</span>
            <el-tag :type="getStatusType(order.status)">{{ getStatusText(order.status) }}</el-tag>
          </div>
          <div class="order-content">
            <div class="order-info">
              <div>{{ t('order.createTime') }}: {{ order.createTime }}</div>
              <div>{{ t('profile.userInfo') }}: {{ order.deliveryType === 1 ? '自取' : '送货上门' }}</div>
              <div>{{ t('profile.userInfo') }}: {{ order.address }}</div>
            </div>
            <div class="order-total">
              <div>{{ t('order.totalAmount') }}: ¥{{ order.totalAmount }}</div>
              <div class="order-actions">
                <el-button
                  v-if="order.status === 1"
                  type="warning"
                  size="small"
                  @click="handlePay(order.orderNo)"
                >
                  {{ t('payment.pay') }}
                </el-button>
                <el-button
                  v-if="order.status === 2"
                  type="primary"
                  size="small"
                  @click="confirmReceive(order.id)"
                >
                  {{ t('order.confirmReceive') }}
                </el-button>
                <el-button
                  v-if="order.refundStatus === 1"
                  type="info"
                  size="small"
                  disabled
                >
                  {{ t('refund.pending') }}
                </el-button>
                <el-button
                  v-if="order.refundStatus === 2"
                  type="success"
                  size="small"
                  disabled
                >
                  {{ t('refund.approved') }}
                </el-button>
                <el-button
                  v-if="order.refundStatus === 3"
                  type="danger"
                  size="small"
                  disabled
                >
                  {{ t('refund.rejected') }}
                </el-button>
                <el-button
                  v-if="canRefund(order)"
                  type="danger"
                  size="small"
                  plain
                  @click="showRefundDialog(order)"
                >
                  {{ t('refund.request') }}
                </el-button>
              </div>
            </div>
          </div>
        </el-card>
      </div>
    </div>

    <el-dialog v-model="refundVisible" :title="t('refund.title')" width="450px">
      <el-form :model="refundForm">
        <el-form-item :label="t('refund.reason')">
          <el-input v-model="refundForm.reason" type="textarea" :rows="4" :placeholder="t('refund.reasonPlaceholder')" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="refundVisible = false">{{ t('common.cancel') }}</el-button>
        <el-button type="danger" @click="submitRefund" :loading="refunding">{{ t('refund.submit') }}</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useI18n } from 'vue-i18n'
import { useUserStore } from '@/stores/user'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getBuyerOrders, confirmReceive as confirmReceiveApi, requestRefund } from '@/api/order'

const { t } = useI18n()
const router = useRouter()
const userStore = useUserStore()
const activeTab = ref('')
const loading = ref(false)
const orders = ref([])

const loadOrders = async () => {
  loading.value = true
  try {
    const status = activeTab.value ? parseInt(activeTab.value) : null
    const res = await getBuyerOrders(status)
    orders.value = res.data.records
  } catch (error) {
    console.error('加载订单失败:', error)
  } finally {
    loading.value = false
  }
}

const getStatusType = (status) => {
  const types = {
    1: 'warning',
    2: 'primary',
    3: 'success'
  }
  return types[status] || 'info'
}

const getStatusText = (status) => {
  const texts = {
    1: t('order.status1'),
    2: t('order.status2'),
    3: t('order.status3')
  }
  return texts[status] || '未知'
}

const refundVisible = ref(false)
const refunding = ref(false)
const refundForm = ref({ orderId: null, reason: '' })
const refundOrderId = ref(null)

const canRefund = (order) => {
  const status = order.refundStatus
  return (order.status === 1 || order.status === 2) && (status === 0 || status === null)
}

const showRefundDialog = (order) => {
  refundOrderId.value = order.id
  refundForm.value.reason = ''
  refundVisible.value = true
}

const submitRefund = async () => {
  refunding.value = true
  try {
    await requestRefund(refundOrderId.value, refundForm.value.reason)
    ElMessage.success(t('refund.success'))
    refundVisible.value = false
    loadOrders()
  } catch (e) {
    ElMessage.error(t('refund.failed'))
  } finally {
    refunding.value = false
  }
}

const handlePay = (orderNo) => {
  router.push({ name: 'PayPage', query: { orderNo } })
}

const confirmReceive = async (orderId) => {
  try {
    await ElMessageBox.confirm('确认已收到商品？', '提示', {
      confirmButtonText: t('admin.confirm'),
      cancelButtonText: '取消',
      type: 'warning'
    })
    await confirmReceiveApi(orderId)
    ElMessage.success(t('order.confirmReceiveSuccess'))
    loadOrders()
  } catch (error) {
    if (error !== 'cancel') {
      console.error('确认收货失败:', error)
    }
  }
}

onMounted(() => {
  loadOrders()
})
</script>

<style scoped>
.orders h2 {
  margin-bottom: 20px;
}

.order-card {
  margin-bottom: 20px;
}

.order-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding-bottom: 10px;
  border-bottom: 1px solid #eee;
  margin-bottom: 10px;
}

.order-content {
  display: flex;
  justify-content: space-between;
}

.order-info {
  flex: 1;
}

.order-info > div {
  margin-bottom: 5px;
  font-size: 14px;
  color: #666;
}

.order-total {
  text-align: right;
}

.order-total > div:first-child {
  font-size: 18px;
  color: #f56c6c;
  font-weight: bold;
  margin-bottom: 10px;
}
</style>