<template>
  <div class="admin-orders">
    <h2>订单管理</h2>

    <el-tabs v-model="activeTab" @tab-change="handleTabChange">
      <el-tab-pane label="全部订单" name="all" />
      <el-tab-pane label="退款申请" name="refund" />
    </el-tabs>

    <!-- 全部订单 -->
    <el-table v-if="activeTab === 'all'" :data="orders" v-loading="loading" border>
      <el-table-column prop="orderNo" label="订单号" width="180" />
      <el-table-column prop="totalAmount" label="金额" width="100" />
      <el-table-column label="状态" width="100">
        <template #default="{ row }">
          <el-tag :type="getStatusType(row.status)">{{ getStatusText(row.status) }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="退款" width="100">
        <template #default="{ row }">
          <el-tag v-if="row.refundStatus === 1" type="danger">申请中</el-tag>
          <el-tag v-else-if="row.refundStatus === 2" type="success">已退款</el-tag>
          <span v-else>-</span>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="200">
        <template #default="{ row }">
          <template v-if="row.status === 1">
            <el-button size="small" type="primary" @click="shipOrder(row.id)">发货</el-button>
          </template>
          <template v-else-if="row.status === 2">
            <el-button size="small" type="success" @click="confirmOrder(row.id)">确认收货</el-button>
          </template>
          <span v-else>已完成</span>
        </template>
      </el-table-column>
    </el-table>

    <!-- 退款申请 -->
    <el-table v-if="activeTab === 'refund'" :data="refundOrders" v-loading="loading" border>
      <el-table-column prop="orderNo" label="订单号" width="180" />
      <el-table-column prop="totalAmount" label="金额" width="100" />
      <el-table-column label="退款原因" min-width="200">
        <template #default="{ row }">
          {{ row.refundReason || '-' }}
        </template>
      </el-table-column>
      <el-table-column label="操作" width="260">
        <template #default="{ row }">
          <el-button size="small" type="success" @click="approveRefund(row.id)">通过退款</el-button>
          <el-button size="small" type="danger" @click="showReject(row.id)">拒绝</el-button>
        </template>
      </el-table-column>
    </el-table>

    <!-- 拒绝理由弹窗 -->
    <el-dialog v-model="rejectVisible" title="拒绝退款" width="400px">
      <el-input v-model="rejectReason" type="textarea" :rows="3" placeholder="请输入拒绝原因" />
      <template #footer>
        <el-button @click="rejectVisible = false">取消</el-button>
        <el-button type="danger" @click="doReject">确认拒绝</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import axios from 'axios'
import { ElMessage } from 'element-plus'
import { getRefundOrders, approveRefund as approveRefundApi, rejectRefund as rejectRefundApi } from '@/api/order'

const loading = ref(false)
const orders = ref([])
const refundOrders = ref([])
const activeTab = ref('all')
const rejectVisible = ref(false)
const rejectReason = ref('')
const rejectOrderId = ref(null)

const getStatusType = (status) => {
  const types = { 1: 'warning', 2: 'primary', 3: 'success' }
  return types[status] || 'info'
}

const getStatusText = (status) => {
  const texts = { 1: '待发货', 2: '已发货', 3: '已收货' }
  return texts[status] || '未知'
}

const handleTabChange = (tab) => {
  if (tab === 'refund') {
    fetchRefundOrders()
  } else {
    fetchOrders()
  }
}

const fetchOrders = async () => {
  try {
    loading.value = true
    const res = await axios.get('/api/admin/orders')
    if (res.data.code === 200) {
      orders.value = res.data.data.records
    }
  } catch (error) {
    console.error('获取订单列表失败:', error)
  } finally {
    loading.value = false
  }
}

const fetchRefundOrders = async () => {
  try {
    loading.value = true
    const res = await getRefundOrders()
    refundOrders.value = res.data.records
  } catch (error) {
    console.error('获取退款列表失败:', error)
  } finally {
    loading.value = false
  }
}

const shipOrder = async (id) => {
  try {
    await axios.put(`/api/admin/order/${id}/ship`)
    ElMessage.success('发货成功')
    fetchOrders()
  } catch (error) {
    ElMessage.error('发货失败')
  }
}

const confirmOrder = async (id) => {
  try {
    await axios.put(`/api/admin/order/${id}/confirm`)
    ElMessage.success('确认收货成功')
    fetchOrders()
  } catch (error) {
    ElMessage.error('确认失败')
  }
}

const approveRefund = async (id) => {
  try {
    await approveRefundApi(id)
    ElMessage.success('退款已通过')
    fetchRefundOrders()
  } catch (error) {
    ElMessage.error('操作失败')
  }
}

const showReject = (id) => {
  rejectOrderId.value = id
  rejectReason.value = ''
  rejectVisible.value = true
}

const doReject = async () => {
  try {
    await rejectRefundApi(rejectOrderId.value, rejectReason.value)
    ElMessage.success('退款已拒绝')
    rejectVisible.value = false
    fetchRefundOrders()
  } catch (error) {
    ElMessage.error('操作失败')
  }
}

onMounted(() => {
  fetchOrders()
})
</script>

<style scoped>
.admin-orders h2 {
  margin-bottom: 20px;
}
</style>
