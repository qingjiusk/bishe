<template>
  <div class="favorite">
    <h2>{{ t('favorite.title') }}</h2>
    <div v-loading="loading">
      <el-empty v-if="favorites.length === 0" :description="t('favorite.empty')" />
      <el-row :gutter="20" v-else>
        <el-col :xs="24" :sm="12" :md="8" :lg="6" v-for="item in favorites" :key="item.id">
          <el-card class="product-card" @click="goToDetail(item.productId)">
            <div class="product-image">
              <img :src="getImageUrl(item.productImage)" :alt="item.productName" />
            </div>
            <div class="product-info">
              <h3 class="product-name">{{ item.productName }}</h3>
              <div class="product-price">¥{{ item.productPrice }}</div>
              <el-button type="danger" size="small" @click.stop="removeFavorite(item.productId)">
                {{ t('favorite.cancelFavorite') }}
              </el-button>
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
import { getFavoriteList, removeFavorite as removeFavoriteApi } from '@/api/favorite'
import { ElMessage, ElMessageBox } from 'element-plus'

const router = useRouter()
const { t } = useI18n()
const loading = ref(false)
const favorites = ref([])

const loadFavorites = async () => {
  loading.value = true
  try {
    const res = await getFavoriteList()
    favorites.value = res.data.records
  } catch (error) {
    console.error('加载收藏失败:', error)
  } finally {
    loading.value = false
  }
}

const removeFavorite = async (productId) => {
  try {
    await ElMessageBox.confirm(t('favorite.confirmCancel'), t('cart.confirmDeleteTitle'), {
      confirmButtonText: t('admin.confirm'),
      cancelButtonText: '取消',
      type: 'warning'
    })
    await removeFavoriteApi(productId)
    ElMessage.success(t('favorite.cancelSuccess'))
    loadFavorites()
  } catch (error) {
    if (error !== 'cancel') {
      console.error('取消收藏失败:', error)
    }
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
  loadFavorites()
})
</script>

<style scoped>
.favorite h2 {
  margin-bottom: 20px;
}

.product-card {
  margin-bottom: 20px;
  cursor: pointer;
  transition: transform 0.3s;
}

.product-card:hover {
  transform: translateY(-5px);
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
</style>