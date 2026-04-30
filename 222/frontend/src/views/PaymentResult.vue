<template>
  <div class="payment-result">
    <div class="result-card" v-if="loading">
      <el-icon class="loading-icon" :size="60"><Loading /></el-icon>
      <h2>{{ t('payment.checking') }}</h2>
    </div>
    <div class="result-card success" v-else-if="paySuccess">
      <el-icon class="result-icon" :size="60" color="#67c23a"><CircleCheckFilled /></el-icon>
      <h2>{{ t('payment.success') }}</h2>
      <p>{{ t('payment.orderNo') }}: {{ orderNo }}</p>
      <el-button type="primary" @click="$router.push('/orders')">
        {{ t('payment.viewOrder') }}
      </el-button>
    </div>
    <div class="result-card fail" v-else>
      <el-icon class="result-icon" :size="60" color="#f56c6c"><CircleCloseFilled /></el-icon>
      <h2>{{ t('payment.fail') }}</h2>
      <p>{{ t('payment.retryHint') }}</p>
      <el-button type="primary" @click="$router.push('/orders')">
        {{ t('payment.viewOrder') }}
      </el-button>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import { useI18n } from 'vue-i18n'
import request from '@/api/request'
import { Loading, CircleCheckFilled, CircleCloseFilled } from '@element-plus/icons-vue'

const route = useRoute()
const { t } = useI18n()
const loading = ref(true)
const paySuccess = ref(false)
const orderNo = ref(route.query.out_trade_no || '')

const checkPayStatus = async () => {
  if (!orderNo.value) {
    loading.value = false
    return
  }
  try {
    // If coming back from Alipay, call return handler to record payment
    const tradeNo = route.query.trade_no
    if (tradeNo) {
      await request.post('/alipay/return-handler', {
        out_trade_no: orderNo.value,
        trade_no: tradeNo
      })
      paySuccess.value = true
    } else {
      // Direct access without Alipay params, check local DB
      const res = await request.get(`/alipay/query/${orderNo.value}`)
      paySuccess.value = res.data.paid === true
    }
  } catch (e) {
    console.error('查询支付状态失败:', e)
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  setTimeout(checkPayStatus, 1500)
})
</script>

<style scoped>
.payment-result {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 400px;
}

.result-card {
  text-align: center;
  padding: 40px;
  border-radius: 8px;
  background: #fff;
  box-shadow: 0 2px 12px rgba(0,0,0,0.08);
}

.result-icon {
  margin-bottom: 16px;
}

.result-card h2 {
  margin-bottom: 12px;
  color: #333;
}

.result-card p {
  color: #666;
  margin-bottom: 20px;
}

.loading-icon {
  animation: spin 1s linear infinite;
}

@keyframes spin {
  from { transform: rotate(0deg); }
  to { transform: rotate(360deg); }
}
</style>
