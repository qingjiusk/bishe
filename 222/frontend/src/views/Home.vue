<template>
  <div class="home">
    <h2>{{ t('home.latestProducts') }}</h2>
    <el-row :gutter="20" v-loading="loading">
      <el-col :xs="24" :sm="12" :md="8" :lg="6" v-for="product in products" :key="product.id">
        <el-card class="product-card" @click="goToDetail(product.id)">
          <div class="product-image">
            <img :src="getImageUrl(product.thumbnail)" :alt="product.name" />
          </div>
          <div class="product-info">
            <h3 class="product-name">{{ product.name }}</h3>
            <div class="product-price">¥{{ product.price }}</div>
            <div class="product-meta">
              <span class="product-condition">{{ product.condition }}</span>
              <span class="product-location">{{ product.location }}</span>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>
    <el-pagination
      v-model:current-page="currentPage"
      :page-size="pageSize"
      :total="total"
      layout="total, prev, pager, next"
      @current-change="loadProducts"
      class="pagination"
    />
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useI18n } from 'vue-i18n'
import { getProductList } from '@/api/product'

const router = useRouter()
const { t } = useI18n()
const loading = ref(false)
const products = ref([])
const currentPage = ref(1)
const pageSize = ref(12)
const total = ref(0)

const loadProducts = async () => {
  loading.value = true
  try {
    const res = await getProductList(currentPage.value, pageSize.value)
    products.value = res.data.records
    total.value = res.data.total
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

const goToDetail = (id) => {
  router.push(`/product/${id}`)
}

onMounted(() => {
  loadProducts()
})
</script>

<style scoped>
.home h2 {
  margin-bottom: 20px;
  color: #333;
}

.product-card {
  margin-bottom: 20px;
  cursor: pointer;
  transition: transform 0.3s;
}

.product-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
}

.product-image {
  width: 100%;
  height: 200px;
  overflow: hidden;
  border-radius: 4px;
}

.product-image img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.product-info {
  padding: 10px 0;
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
  margin-bottom: 8px;
}

.product-meta {
  display: flex;
  justify-content: space-between;
  font-size: 12px;
  color: #999;
}

.pagination {
  margin-top: 20px;
  display: flex;
  justify-content: center;
}
</style>