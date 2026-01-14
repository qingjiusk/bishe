<template>
  <div class="my-products">
    <h2>{{ t('myProducts.title') }}</h2>
    <el-tabs v-model="activeTab" @tab-change="loadProducts">
      <el-tab-pane :label="t('myProducts.all')" name="" />
      <el-tab-pane :label="t('myProducts.pending')" name="0" />
      <el-tab-pane :label="t('myProducts.approved')" name="1" />
      <el-tab-pane :label="t('myProducts.rejected')" name="2" />
    </el-tabs>
    <div v-loading="loading">
      <el-empty v-if="products.length === 0" :description="t('myProducts.empty')" />
      <el-row :gutter="20" v-else>
        <el-col :xs="24" :sm="12" :md="8" :lg="6" v-for="product in products" :key="product.id">
          <el-card class="product-card">
            <div class="product-image">
              <img :src="getImageUrl(product.thumbnail)" :alt="product.name" />
            </div>
            <div class="product-info">
              <h3 class="product-name">{{ product.name }}</h3>
              <div class="product-price">¥{{ product.price }}</div>
              <el-tag :type="getAuditStatusType(product.auditStatus)">
                {{ getAuditStatusText(product.auditStatus) }}
              </el-tag>
              <div class="product-actions">
                <el-button size="small" @click="editProduct(product.id)">{{ t('myProducts.edit') }}</el-button>
                <el-button size="small" type="danger" @click="deleteProduct(product.id)">{{ t('myProducts.delete') }}</el-button>
              </div>
            </div>
          </el-card>
        </el-col>
      </el-row>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useI18n } from 'vue-i18n'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getMyProducts, deleteProduct as deleteProductApi } from '@/api/product'

const router = useRouter()
const { t } = useI18n()
const activeTab = ref('')
const loading = ref(false)
const products = ref([])

const loadProducts = async () => {
  loading.value = true
  try {
    const auditStatus = activeTab.value ? parseInt(activeTab.value) : null
    const res = await getMyProducts(auditStatus)
    products.value = res.data.records
  } catch (error) {
    console.error('加载商品失败:', error)
  } finally {
    loading.value = false
  }
}

const getImageUrl = (thumbnail) => {
  if (!thumbnail) return '/placeholder.png'
  if (thumbnail.startsWith('http')) return thumbnail
  return `/api${thumbnail}`
}

const getAuditStatusType = (status) => {
  const types = {
    0: 'warning',
    1: 'success',
    2: 'danger'
  }
  return types[status] || 'info'
}

const getAuditStatusText = (status) => {
  const texts = {
    0: t('myProducts.statusPending'),
    1: t('myProducts.statusApproved'),
    2: t('myProducts.statusRejected')
  }
  return texts[status] || '未知'
}

const editProduct = (id) => {
  ElMessage.info('编辑功能待实现')
}

const deleteProduct = async (id) => {
  try {
    await ElMessageBox.confirm(t('myProducts.confirmDelete'), t('cart.confirmDeleteTitle'), {
      confirmButtonText: t('admin.confirm'),
      cancelButtonText: '取消',
      type: 'warning'
    })
    await deleteProductApi(id)
    ElMessage.success(t('myProducts.deleteSuccess'))
    loadProducts()
  } catch (error) {
    if (error !== 'cancel') {
      console.error('下架失败:', error)
    }
  }
}

onMounted(() => {
  loadProducts()
})
</script>

<style scoped>
.my-products h2 {
  margin-bottom: 20px;
}

.product-card {
  margin-bottom: 20px;
}

.product-image {
  width: 100%;
  height: 200px;
  overflow: hidden;
  border-radius: 4px;
  margin-bottom: 10px;
}

.product-image img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.product-name {
  font-size: 16px;
  margin-bottom: 10px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.product-price {
  font-size: 20px;
  color: #f56c6c;
  font-weight: bold;
  margin-bottom: 10px;
}

.product-actions {
  margin-top: 10px;
  display: flex;
  gap: 10px;
}
</style>