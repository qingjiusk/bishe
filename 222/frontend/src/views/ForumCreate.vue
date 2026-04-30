<template>
  <div class="forum-create">
    <div class="create-header">
      <el-button @click="$router.back()">
        <el-icon><ArrowLeft /></el-icon>
        {{ t('common.back') }}
      </el-button>
      <h2>{{ t('forum.createPost') }}</h2>
    </div>

    <el-form
      ref="formRef"
      :model="form"
      :rules="rules"
      label-position="top"
      class="create-form"
    >
      <el-form-item :label="t('forum.category')" prop="category">
        <el-select v-model="form.category" style="width: 200px">
          <el-option label="交流" value="交流" />
          <el-option label="求助" value="求助" />
          <el-option label="交易" value="交易" />
          <el-option label="活动" value="活动" />
        </el-select>
      </el-form-item>

      <el-form-item :label="t('forum.postTitle')" prop="title">
        <el-input v-model="form.title" maxlength="100" show-word-limit />
      </el-form-item>

      <el-form-item :label="t('forum.content')" prop="content">
        <el-input
          v-model="form.content"
          type="textarea"
          :rows="12"
          maxlength="5000"
          show-word-limit
        />
      </el-form-item>

      <el-form-item>
        <el-button type="primary" @click="handleSubmit" :loading="submitting">
          {{ t('forum.publish') }}
        </el-button>
        <el-button @click="$router.back()">{{ t('common.cancel') }}</el-button>
      </el-form-item>
    </el-form>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { useI18n } from 'vue-i18n'
import { ElMessage } from 'element-plus'
import { createForum } from '@/api/forum'
import { ArrowLeft } from '@element-plus/icons-vue'

const router = useRouter()
const { t } = useI18n()
const formRef = ref(null)
const submitting = ref(false)

const form = ref({
  category: '交流',
  title: '',
  content: ''
})

const rules = {
  category: [{ required: true, message: '请选择分类', trigger: 'change' }],
  title: [
    { required: true, message: '请输入标题', trigger: 'blur' },
    { min: 2, max: 100, message: '标题长度2-100字符', trigger: 'blur' }
  ],
  content: [
    { required: true, message: '请输入内容', trigger: 'blur' },
    { min: 10, message: '内容至少10个字符', trigger: 'blur' }
  ]
}

const handleSubmit = async () => {
  const valid = await formRef.value.validate().catch(() => false)
  if (!valid) return

  submitting.value = true
  try {
    await createForum(form.value)
    ElMessage.success(t('forum.publishSuccess'))
    router.push('/forum')
  } catch (error) {
    ElMessage.error(t('forum.publishFailed'))
  } finally {
    submitting.value = false
  }
}
</script>

<style scoped>
.forum-create {
  max-width: 800px;
  margin: 0 auto;
}

.create-header {
  display: flex;
  align-items: center;
  gap: 16px;
  margin-bottom: 30px;
}

.create-header h2 {
  color: #333;
}

.create-form {
  background: #fff;
  padding: 24px;
  border-radius: 8px;
  box-shadow: 0 1px 4px rgba(0, 0, 0, 0.06);
}
</style>
