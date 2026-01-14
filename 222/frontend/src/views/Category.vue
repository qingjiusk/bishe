<template>
  <div class="category">
    <h2>{{ categoryName }}</h2>
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
import { ref, computed, onMounted, watch } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { useI18n } from 'vue-i18n'
import { getProductsByCategory } from '@/api/product'

const router = useRouter()
const route = useRoute()
const { t } = useI18n()
const loading = ref(false)
const products = ref([])
const currentPage = ref(1)
const pageSize = ref(12)
const total = ref(0)

const categoryNames = computed(() => ({
  1: t('category.electronics'),
  2: t('category.books'),
  3: t('category.daily'),
  4: t('category.sports'),
  5: t('category.clothing'),
  6: t('category.other')
}))

const categoryName = computed(() => {
  return categoryNames.value[route.params.id] || '分类商品'
})

const loadProducts = async () => {
  loading.value = true
  try {
    const res = await getProductsByCategory(route.params.id, currentPage.value, pageSize.value)
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

watch(() => route.params.id, () => {
  currentPage.value = 1
  loadProducts()
})

onMounted(() => {
  loadProducts()
})
</script>

<style scoped>
.category h2 {
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