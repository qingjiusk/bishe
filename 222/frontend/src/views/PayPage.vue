<template>
  <div class="pay-page" v-loading="loading">
    <div v-if="payForm" v-html="payForm" class="pay-form-container"></div>
    <div v-else class="empty-state">
      <el-empty :description="t('payment.loadFailed')" />
      <el-button type="primary" @click="$router.back()">{{ t('common.back') }}</el-button>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, nextTick } from 'vue'
import { useRoute } from 'vue-router'
import { useI18n } from 'vue-i18n'
import { useUserStore } from '@/stores/user'
import { pay } from '@/api/alipay'
import { ElMessage } from 'element-plus'

const route = useRoute()
const { t } = useI18n()
const userStore = useUserStore()
const loading = ref(true)
const payForm = ref('')
const orderNo = ref(route.query.orderNo || '')

const initPay = async () => {
  if (!userStore.token) {
    ElMessage.warning(t('product.pleaseLogin'))
    loading.value = false
    return
  }
  if (!orderNo.value) {
    ElMessage.error(t('payment.noOrder'))
    loading.value = false
    return
  }
  try {
    const res = await pay(orderNo.value)
    payForm.value = res.data.payForm
    await nextTick()
    // Auto-submit the Alipay form
    const form = document.querySelector('.pay-form-container form')
    if (form) {
      form.submit()
    }
  } catch (e) {
    ElMessage.error(t('payment.payFailed'))
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  initPay()
})
</script>

<style scoped>
.pay-page {
  max-width: 800px;
  margin: 0 auto;
  min-height: 300px;
}

.pay-form-container {
  display: none;
}

.empty-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 60px 0;
  gap: 16px;
}
</style>
