<template>
  <div class="product-detail" v-loading="loading">
    <el-breadcrumb separator="/">
      <el-breadcrumb-item @click="$router.push('/')">{{ t('common.home') }}</el-breadcrumb-item>
      <el-breadcrumb-item>{{ t('product.detail') }}</el-breadcrumb-item>
    </el-breadcrumb>
    <div v-if="product" class="detail-content">
      <el-row :gutter="20">
        <el-col :span="12">
          <div class="product-image">
            <img :src="getImageUrl(product.thumbnail)" :alt="product.name" />
          </div>
        </el-col>
        <el-col :span="12">
          <div class="product-info">
            <h1>{{ product.name }}</h1>
            <div class="price">¥{{ product.price }}</div>
            <div class="meta">
              <el-tag>{{ product.condition }}</el-tag>
              <el-tag type="info">{{ t('product.viewCount') }} {{ product.viewCount }}</el-tag>
            </div>
            <div class="description">
              <h3>{{ t('product.description') }}</h3>
              <p>{{ product.description }}</p>
            </div>
            <div class="seller">
              <h3>{{ t('product.sellerInfo') }}</h3>
              <div class="seller-info">
                <el-avatar :size="40">{{ product.sellerName?.charAt(0) }}</el-avatar>
                <div>
                  <div>{{ product.sellerName }}</div>
                  <div class="seller-phone">{{ product.sellerPhone }}</div>
                </div>
              </div>
            </div>
            <div class="location">
              <h3>{{ t('product.location') }}</h3>
              <p>{{ product.location }}</p>
            </div>
            <div class="actions">
              <el-button type="primary" size="large" @click="addToCart">
                <el-icon><ShoppingCart /></el-icon>
                {{ t('product.addToCart') }}
              </el-button>
              <el-button size="large" @click="toggleFavorite">
                <el-icon><Star /></el-icon>
                {{ isFav ? t('product.cancelFavorite') : t('product.favorite') }}
              </el-button>
            </div>
          </div>
        </el-col>
      </el-row>
      <el-divider />
      <div class="comments">
        <h2>{{ t('product.comments') }}</h2>
        <div class="comment-input" v-if="userStore.token">
          <el-input
            v-model="commentContent"
            type="textarea"
            :rows="3"
            :placeholder="t('product.commentPlaceholder')"
          />
          <el-button type="primary" @click="submitComment" style="margin-top: 10px">
            {{ t('product.submitComment') }}
          </el-button>
        </div>
        <div v-else>
          <el-link type="primary" @click="$router.push('/login')">{{ t('product.pleaseLogin') }}</el-link>
        </div>
        <div class="comment-list">
          <div v-for="comment in comments" :key="comment.id" class="comment-item">
            <div class="comment-user">{{ comment.userName }}</div>
            <div class="comment-content">{{ comment.content }}</div>
            <div class="comment-time">{{ comment.createTime }}</div>
            <div v-if="comment.reply" class="comment-reply">
              <div class="reply-label">{{ t('product.sellerInfo') }}：</div>
              <div>{{ comment.reply }}</div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import { useI18n } from 'vue-i18n'
import { useUserStore } from '@/stores/user'
import { ElMessage } from 'element-plus'
import { getProductDetail } from '@/api/product'
import { addToCart as addToCartApi } from '@/api/cart'
import { addFavorite, removeFavorite, isFavorite as checkFavorite } from '@/api/favorite'
import { addComment, getProductComments } from '@/api/comment'
import { ShoppingCart, Star } from '@element-plus/icons-vue'

const route = useRoute()
const { t } = useI18n()
const userStore = useUserStore()
const loading = ref(false)
const product = ref(null)
const isFav = ref(false)
const commentContent = ref('')
const comments = ref([])

const loadProduct = async () => {
  loading.value = true
  try {
    const res = await getProductDetail(route.params.id)
    product.value = res.data
    if (userStore.token) {
      const favRes = await checkFavorite(route.params.id)
      isFav.value = favRes.data
    }
    loadComments()
  } catch (error) {
    console.error('加载商品详情失败:', error)
  } finally {
    loading.value = false
  }
}

const loadComments = async () => {
  try {
    const res = await getProductComments(route.params.id)
    comments.value = res.data.records
  } catch (error) {
    console.error('加载留言失败:', error)
  }
}

const getImageUrl = (thumbnail) => {
  if (!thumbnail) return '/placeholder.png'
  if (thumbnail.startsWith('http')) return thumbnail
  return `/api${thumbnail}`
}

const addToCart = async () => {
  if (!userStore.token) {
    ElMessage.warning(t('product.pleaseLogin'))
    return
  }
  try {
    await addToCartApi(product.value.id)
    ElMessage.success(t('product.addToCartSuccess'))
  } catch (error) {
    console.error('添加购物车失败:', error)
  }
}

const toggleFavorite = async () => {
  if (!userStore.token) {
    ElMessage.warning(t('product.pleaseLogin'))
    return
  }
  try {
    if (isFav.value) {
      await removeFavorite(product.value.id)
      ElMessage.success(t('product.cancelFavoriteSuccess'))
    } else {
      await addFavorite(product.value.id)
      ElMessage.success(t('product.favoriteSuccess'))
    }
    isFav.value = !isFav.value
  } catch (error) {
    console.error('操作失败:', error)
  }
}

const submitComment = async () => {
  if (!commentContent.value.trim()) {
    ElMessage.warning(t('product.pleaseInputComment'))
    return
  }
  try {
    await addComment(product.value.id, commentContent.value)
    ElMessage.success(t('product.commentSuccess'))
    commentContent.value = ''
    loadComments()
  } catch (error) {
    console.error('留言失败:', error)
  }
}

onMounted(() => {
  loadProduct()
})
</script>

<style scoped>
.product-detail {
  padding: 20px 0;
}

.detail-content {
  margin-top: 20px;
}

.product-image img {
  width: 100%;
  border-radius: 8px;
}

.product-info h1 {
  margin-top: 0;
  font-size: 24px;
}

.price {
  font-size: 32px;
  color: #f56c6c;
  font-weight: bold;
  margin: 20px 0;
}

.meta {
  display: flex;
  gap: 10px;
  margin-bottom: 20px;
}

.description, .seller, .location {
  margin: 20px 0;
}

.description h3, .seller h3, .location h3 {
  font-size: 16px;
  margin-bottom: 10px;
}

.seller-info {
  display: flex;
  align-items: center;
  gap: 10px;
}

.seller-phone {
  font-size: 12px;
  color: #999;
}

.actions {
  margin-top: 30px;
  display: flex;
  gap: 10px;
}

.comments {
  margin-top: 40px;
}

.comment-input {
  margin-bottom: 20px;
}

.comment-list {
  margin-top: 20px;
}

.comment-item {
  padding: 15px;
  border-bottom: 1px solid #eee;
}

.comment-user {
  font-weight: bold;
  margin-bottom: 5px;
}

.comment-content {
  margin-bottom: 5px;
}

.comment-time {
  font-size: 12px;
  color: #999;
}

.comment-reply {
  margin-top: 10px;
  padding: 10px;
  background-color: #f5f5f5;
  border-radius: 4px;
}

.reply-label {
  font-weight: bold;
  color: #409eff;
}
</style>