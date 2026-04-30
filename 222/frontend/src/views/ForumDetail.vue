<template>
  <div class="forum-detail" v-loading="loading">
    <div class="detail-header">
      <el-button @click="$router.push('/forum')">
        <el-icon><ArrowLeft /></el-icon>
        {{ t('common.back') }}
      </el-button>
    </div>

    <div v-if="post" class="post-detail-card">
      <div class="post-header">
        <el-tag size="small" type="primary">{{ post.category }}</el-tag>
        <h1 class="post-title">{{ post.title }}</h1>
        <div class="post-meta">
          <span class="post-author">
            <el-avatar :size="28">{{ post.author?.charAt(0) }}</el-avatar>
            <span>{{ post.author }}</span>
          </span>
          <span class="post-time">{{ formatTime(post.createTime) }}</span>
          <span class="post-stats">
            <el-icon><View /></el-icon> {{ post.viewCount }}
          </span>
        </div>
      </div>

      <div class="post-content" v-html="parsedContent"></div>
    </div>

    <div v-if="!post && !loading" class="empty-state">
      <el-empty :description="t('forum.postNotFound')" />
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useI18n } from 'vue-i18n'
import { getForumDetail } from '@/api/forum'
import { ArrowLeft, View } from '@element-plus/icons-vue'

const route = useRoute()
const router = useRouter()
const { t } = useI18n()
const loading = ref(false)
const post = ref(null)

const parsedContent = computed(() => {
  if (!post.value?.content) return ''
  return post.value.content.replace(/\n/g, '<br>')
})

const formatTime = (time) => {
  if (!time) return ''
  return time.replace('T', ' ').substring(0, 16)
}

const loadDetail = async () => {
  loading.value = true
  try {
    const res = await getForumDetail(route.params.id)
    post.value = res.data
  } catch (error) {
    console.error('加载帖子详情失败:', error)
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  loadDetail()
})
</script>

<style scoped>
.forum-detail {
  max-width: 800px;
  margin: 0 auto;
}

.detail-header {
  margin-bottom: 20px;
}

.post-detail-card {
  background: #fff;
  border-radius: 8px;
  padding: 24px;
  box-shadow: 0 1px 4px rgba(0, 0, 0, 0.06);
}

.post-header {
  border-bottom: 1px solid #eee;
  padding-bottom: 16px;
  margin-bottom: 20px;
}

.post-title {
  font-size: 22px;
  color: #333;
  margin: 12px 0;
}

.post-meta {
  display: flex;
  align-items: center;
  gap: 16px;
  font-size: 13px;
  color: #999;
}

.post-author {
  display: flex;
  align-items: center;
  gap: 8px;
}

.post-stats {
  display: flex;
  align-items: center;
  gap: 4px;
  margin-left: auto;
}

.post-content {
  font-size: 15px;
  line-height: 1.8;
  color: #333;
  min-height: 200px;
  white-space: pre-wrap;
  word-break: break-word;
}

.empty-state {
  display: flex;
  justify-content: center;
  padding: 80px 0;
}
</style>
