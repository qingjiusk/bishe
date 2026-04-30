<template>
  <div class="cart">
    <h2>{{ t('cart.title') }}</h2>
    <div v-loading="loading">
      <el-empty v-if="cartItems.length === 0" :description="t('cart.empty')" />
      <div v-else>
        <div class="cart-list">
          <div v-for="item in cartItems" :key="item.id" class="cart-item">
            <el-checkbox v-model="item.selected" @change="updateSelection" />
            <div class="item-image">
              <img :src="getImageUrl(item.productImage)" :alt="item.productName" />
            </div>
            <div class="item-info">
              <h3>{{ item.productName }}</h3>
              <div class="item-price">¥{{ item.productPrice }}</div>
            </div>
            <div class="item-quantity">
              <el-input-number
                v-model="item.quantity"
                :min="1"
                @change="updateQuantity(item.id, item.quantity)"
              />
            </div>
            <div class="item-total">¥{{ (item.productPrice * item.quantity).toFixed(2) }}</div>
            <el-button type="danger" :icon="Delete" @click="removeItem(item.id)" />
          </div>
        </div>
        <div class="cart-footer">
          <div class="selected-count">
            {{ t('cart.selectedCount') }} <span>{{ selectedCount }}</span> {{ t('cart.items') }}
          </div>
          <div class="total-price">
            {{ t('cart.total') }}: <span>¥{{ totalPrice }}</span>
          </div>
          <el-button type="primary" size="large" @click="showCheckout" :disabled="selectedCount === 0">
            {{ t('cart.checkout') }}
          </el-button>
        </div>
      </div>
    </div>

    <!-- 结算弹窗 -->
    <el-dialog v-model="checkoutVisible" :title="t('cart.checkout')" width="500px" :close-on-click-modal="false">
      <div class="checkout-summary">
        <p>{{ t('cart.selectedCount') }} <strong>{{ selectedCount }}</strong> {{ t('cart.items') }}</p>
        <p class="checkout-total">{{ t('cart.total') }}: <strong>¥{{ totalPrice }}</strong></p>
      </div>
      <el-form :model="checkoutForm" label-position="top">
        <el-form-item :label="t('order.deliveryType')">
          <el-radio-group v-model="checkoutForm.deliveryType">
            <el-radio :value="1">{{ t('order.pickup') }}</el-radio>
            <el-radio :value="2">{{ t('order.delivery') }}</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item :label="t('order.address')">
          <el-input v-model="checkoutForm.address" :placeholder="t('order.addressPlaceholder')" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="checkoutVisible = false">{{ t('common.cancel') }}</el-button>
        <el-button type="warning" @click="handleCheckout" :loading="submitting">
          {{ t('payment.pay') }}
        </el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useI18n } from 'vue-i18n'
import { getCartList, updateCartQuantity, removeFromCart } from '@/api/cart'
import { createOrder } from '@/api/order'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Delete } from '@element-plus/icons-vue'

const router = useRouter()
const { t } = useI18n()
const loading = ref(false)
const cartItems = ref([])
const checkoutVisible = ref(false)
const submitting = ref(false)
const checkoutForm = ref({
  deliveryType: 1,
  address: '校内自取'
})

const selectedCount = computed(() => {
  return cartItems.value.filter(item => item.selected).length
})

const totalPrice = computed(() => {
  return cartItems.value
    .filter(item => item.selected)
    .reduce((sum, item) => sum + item.productPrice * item.quantity, 0)
    .toFixed(2)
})

const loadCart = async () => {
  loading.value = true
  try {
    const res = await getCartList()
    cartItems.value = res.data.records.map(item => ({
      ...item,
      selected: true
    }))
  } catch (error) {
    console.error('加载购物车失败:', error)
  } finally {
    loading.value = false
  }
}

const updateQuantity = async (cartId, quantity) => {
  try {
    await updateCartQuantity(cartId, quantity)
  } catch (error) {
    console.error('更新数量失败:', error)
  }
}

const removeItem = async (cartId) => {
  try {
    await ElMessageBox.confirm(t('cart.confirmDelete'), t('cart.confirmDeleteTitle'), {
      confirmButtonText: t('admin.confirm'),
      cancelButtonText: '取消',
      type: 'warning'
    })
    await removeFromCart(cartId)
    ElMessage.success(t('cart.removeSuccess'))
    loadCart()
  } catch (error) {
    if (error !== 'cancel') {
      console.error('删除失败:', error)
    }
  }
}

const updateSelection = () => {
  // handled by v-model
}

const getImageUrl = (thumbnail) => {
  if (!thumbnail) return '/placeholder.png'
  if (thumbnail.startsWith('http')) return thumbnail
  return `/api${thumbnail}`
}

const showCheckout = () => {
  checkoutVisible.value = true
}

const handleCheckout = async () => {
  submitting.value = true
  try {
    const res = await createOrder(null, checkoutForm.value.deliveryType, checkoutForm.value.address)
    checkoutVisible.value = false
    ElMessage.success(t('order.createSuccess') || '下单成功，即将跳转支付')
    router.push({ name: 'PayPage', query: { orderNo: res.data.orderNo } })
  } catch (error) {
    ElMessage.error(t('order.createFailed') || '下单失败')
  } finally {
    submitting.value = false
  }
}

onMounted(() => {
  loadCart()
})
</script>

<style scoped>
.cart h2 {
  margin-bottom: 20px;
}

.cart-item {
  display: flex;
  align-items: center;
  padding: 20px;
  border: 1px solid #eee;
  border-radius: 8px;
  margin-bottom: 10px;
  gap: 20px;
}

.item-image {
  width: 80px;
  height: 80px;
}

.item-image img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  border-radius: 4px;
}

.item-info {
  flex: 1;
}

.item-info h3 {
  margin: 0 0 10px 0;
}

.item-price {
  font-size: 18px;
  color: #f56c6c;
  font-weight: bold;
}

.item-quantity {
  width: 150px;
}

.item-total {
  width: 100px;
  text-align: right;
  font-size: 18px;
  font-weight: bold;
}

.cart-footer {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 20px;
  background-color: #f5f5f5;
  border-radius: 8px;
  margin-top: 20px;
}

.selected-count span,
.total-price span {
  font-size: 24px;
  font-weight: bold;
  color: #f56c6c;
}

.checkout-summary {
  background: #f9f9f9;
  padding: 16px;
  border-radius: 8px;
  margin-bottom: 16px;
}

.checkout-total {
  font-size: 18px;
  margin-top: 8px;
}

.checkout-total strong {
  color: #f56c6c;
}
</style>
