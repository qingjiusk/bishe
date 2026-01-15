<template>
  <div class="publish">
    <h2>{{ t('publish.title') }}</h2>
    <el-card>
      <el-form :model="productForm" :rules="rules" ref="productFormRef" label-width="100px">
        <el-form-item :label="t('publish.productName')" prop="name">
          <el-input v-model="productForm.name" :placeholder="t('publish.productNamePlaceholder')" />
        </el-form-item>
        <el-form-item :label="t('publish.category')" prop="categoryId">
          <el-select v-model="productForm.categoryId" :placeholder="t('publish.categoryPlaceholder')" style="width: 100%">
            <el-option
              v-for="category in categories"
              :key="category.id"
              :label="category.name"
              :value="category.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item :label="t('publish.price')" prop="price">
          <el-input-number v-model="productForm.price" :min="0" :precision="2" style="width: 100%" />
        </el-form-item>
        <el-form-item :label="t('publish.condition')" prop="condition">
          <el-select v-model="productForm.condition" :placeholder="t('publish.conditionPlaceholder')" style="width: 100%">
            <el-option :label="t('category.brandNew')" value="全新" />
            <el-option :label="t('category.likeNew')" value="九成新" />
            <el-option :label="t('category.good')" value="八成新" />
            <el-option :label="t('category.fair')" value="七成新" />
            <el-option label="六成新以下" value="六成新以下" />
          </el-select>
        </el-form-item>
        <el-form-item :label="t('publish.location')" prop="location">
          <el-input v-model="productForm.location" :placeholder="t('publish.locationPlaceholder')" />
        </el-form-item>
        <el-form-item :label="t('publish.description')" prop="description">
          <el-input
            v-model="productForm.description"
            type="textarea"
            :rows="5"
            :placeholder="t('publish.descriptionPlaceholder')"
          />
        </el-form-item>
        <el-form-item :label="t('publish.images')" prop="images">
          <el-upload
            v-model:file-list="fileList"
            action="/api/file/upload"
            list-type="picture-card"
            :auto-upload="true"
            :limit="5"
            :on-success="handleUploadSuccess"
            :on-error="handleUploadError"
            :before-upload="beforeUpload"
            :on-remove="handleRemove"
            name="files"
          >
            <el-icon><Plus /></el-icon>
          </el-upload>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSubmit" :loading="loading">
            {{ t('publish.submit') }}
          </el-button>
          <el-button @click="handleReset">{{ t('common.language') }}</el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useI18n } from 'vue-i18n'
import { ElMessage } from 'element-plus'
import { Plus } from '@element-plus/icons-vue'
import { publishProduct } from '@/api/product'
import { getCategoryList } from '@/api/category'

const router = useRouter()
const { t } = useI18n()
const productFormRef = ref(null)
const loading = ref(false)
const categories = ref([])
const fileList = ref([])
const uploadedUrls = ref([])

const productForm = reactive({
  name: '',
  categoryId: null,
  price: 0,
  condition: '',
  location: '',
  description: '',
  images: '',
  thumbnail: ''
})

const rules = computed(() => ({
  name: [{ required: true, message: t('publish.productNamePlaceholder'), trigger: 'blur' }],
  categoryId: [{ required: true, message: t('publish.categoryPlaceholder'), trigger: 'change' }],
  price: [{ required: true, message: t('publish.pricePlaceholder'), trigger: 'blur' }],
  condition: [{ required: true, message: t('publish.conditionPlaceholder'), trigger: 'change' }],
  location: [{ required: true, message: t('publish.locationPlaceholder'), trigger: 'blur' }],
  description: [{ required: true, message: t('publish.descriptionPlaceholder'), trigger: 'blur' }]
}))

const loadCategories = async () => {
  try {
    const res = await getCategoryList()
    categories.value = res.data
  } catch (error) {
    console.error('加载分类失败:', error)
  }
}

// 上传前的验证
const beforeUpload = (file) => {
  const isImage = file.type.startsWith('image/')
  const isLt10M = file.size / 1024 / 1024 < 10

  if (!isImage) {
    ElMessage.error('只能上传图片文件!')
    return false
  }
  if (!isLt10M) {
    ElMessage.error('图片大小不能超过 10MB!')
    return false
  }
  return true
}

// 上传成功回调
const handleUploadSuccess = (response, file, fileList) => {
  if (response.code === 200 && response.data) {
    // 后端返回的是数组，取第一个元素
    if (Array.isArray(response.data) && response.data.length > 0) {
      uploadedUrls.value.push(response.data[0])
    }
    ElMessage.success('图片上传成功')
  } else {
    ElMessage.error(response.message || '图片上传失败')
  }
}

// 上传失败回调
const handleUploadError = (error, file, fileList) => {
  ElMessage.error('图片上传失败: ' + error.message)
}

// 移除文件
const handleRemove = (file, fileList) => {
  // 从 uploadedUrls 中移除对应的 URL
  if (file.response && file.response.code === 200 && file.response.data) {
    const url = Array.isArray(file.response.data) ? file.response.data[0] : file.response.data
    uploadedUrls.value = uploadedUrls.value.filter(item => item !== url)
  }
}

const handleSubmit = async () => {
  const valid = await productFormRef.value.validate()
  if (!valid) return

  if (uploadedUrls.value.length === 0) {
    ElMessage.warning('请至少上传一张商品图片')
    return
  }

  loading.value = true
  try {
    // 使用真实上传的图片URL
    productForm.images = uploadedUrls.value.join(',')
    productForm.thumbnail = uploadedUrls.value[0]

    await publishProduct(productForm)
    ElMessage.success(t('publish.publishSuccess'))
    router.push('/my-products')
  } catch (error) {
    console.error('发布失败:', error)
    ElMessage.error(error.response?.data?.message || '发布失败')
  } finally {
    loading.value = false
  }
}

const handleReset = () => {
  productFormRef.value.resetFields()
  fileList.value = []
  uploadedUrls.value = []
}

onMounted(() => {
  loadCategories()
})
</script>

<style scoped>
.publish h2 {
  margin-bottom: 20px;
}
</style>