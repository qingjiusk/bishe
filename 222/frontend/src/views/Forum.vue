<template>
  <div class="forum">
    <div class="forum-header">
      <h2>{{ t('forum.title') }}</h2>
      <el-button type="primary" @click="$router.push('/forum/create')" v-if="userStore.token">
        <el-icon><Edit /></el-icon>
        {{ t('forum.createPost') }}
      </el-button>
    </div>

    <!-- 分类筛选 -->
    <el-radio-group v-model="selectedCategory" @change="loadPosts" class="category-filter">
      <el-radio-button value="">{{ t('forum.all') }}</el-radio-button>
      <el-radio-button value="交流">{{ t('forum.chat') }}</el-radio-button>
      <el-radio-button value="求助">{{ t('forum.help') }}</el-radio-button>
      <el-radio-button value="交易">{{ t('forum.trade') }}</el-radio-button>
      <el-radio-button value="活动">{{ t('forum.activity') }}</el-radio-button>
    </el-radio-group>

    <!-- 帖子列表 -->
    <div class="post-list" v-loading="loading">
      <div v-if="posts.length === 0 && !loading" class="empty-state">
        <el-empty :description="t('forum.noPosts')" />
      </div>
      <div v-for="post in posts" :key="post.id" class="post-item" @click="goToDetail(post.id)">
        <div class="post-category">
          <el-tag size="small" type="primary">{{ post.category }}</el-tag>
        </div>
        <div class="post-main">
          <h3 class="post-title">{{ post.title }}</h3>
          <div class="post-meta">
            <span class="post-author">
              <el-icon><User /></el-icon> {{ post.author }}
            </span>
            <span class="post-time">{{ formatTime(post.createTime) }}</span>
            <span class="post-stats">
              <el-icon><View /></el-icon> {{ post.viewCount }}
              <el-icon style="margin-left: 12px"><ChatDotRound /></el-icon> {{ post.replyCount }}
            </span>
          </div>
        </div>
      </div>
    </div>

    <el-pagination
      v-model:current-page="currentPage"
      :page-size="pageSize"
      :total="total"
      layout="total, prev, pager, next"
      @current-change="loadPosts"
      class="pagination"
    />
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useI18n } from 'vue-i18n'
import { useUserStore } from '@/stores/user'
import { getForumList } from '@/api/forum'
import { User, View, ChatDotRound, Edit } from '@element-plus/icons-vue'

const router = useRouter()
const { t } = useI18n()
const userStore = useUserStore()
const loading = ref(false)
const posts = ref([])
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)
const selectedCategory = ref('')

const loadPosts = async () => {
  loading.value = true
  try {
    const res = await getForumList(currentPage.value, pageSize.value, selectedCategory.value)
    posts.value = res.data.records
    total.value = res.data.total
  } catch (error) {
    console.error('加载论坛帖子失败:', error)
  } finally {
    loading.value = false
  }
}

const formatTime = (time) => {
  if (!time) return ''
  return time.replace('T', ' ').substring(0, 16)
}

const goToDetail = (id) => {
  router.push(`/forum/${id}`)
}

onMounted(() => {
  loadPosts()
})
</script>

<style scoped>
.forum {
  max-width: 900px;
  margin: 0 auto;
}

.forum-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 20px;
}

.forum-header h2 {
  color: #333;
}

.category-filter {
  margin-bottom: 20px;
}

.post-list {
  min-height: 300px;
}

.empty-state {
  display: flex;
  justify-content: center;
  padding: 60px 0;
}

.post-item {
  display: flex;
  align-items: flex-start;
  gap: 16px;
  padding: 16px 0;
  border-bottom: 1px solid #eee;
  cursor: pointer;
  transition: background 0.2s;
}

.post-item:hover {
  background: #f9f9f9;
  padding-left: 12px;
  padding-right: 12px;
  margin: 0 -12px;
  border-radius: 4px;
}

.post-category {
  flex-shrink: 0;
  padding-top: 2px;
}

.post-main {
  flex: 1;
}

.post-title {
  font-size: 16px;
  color: #333;
  margin-bottom: 8px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
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
  gap: 4px;
}

.post-stats {
  display: flex;
  align-items: center;
  gap: 4px;
  margin-left: auto;
}

.pagination {
  margin-top: 20px;
  display: flex;
  justify-content: center;
}
</style>
