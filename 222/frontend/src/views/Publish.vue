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
            action="#"
            list-type="picture-card"
            :auto-upload="false"
            :limit="5"
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

const handleSubmit = async () => {
  const valid = await productFormRef.value.validate()
  if (!valid) return

  if (fileList.value.length === 0) {
    ElMessage.warning('请至少上传一张商品图片')
    return
  }

  loading.value = true
  try {
    // 模拟图片上传，实际项目中需要上传到服务器
    const images = fileList.value.map(file => URL.createObjectURL(file.raw)).join(',')
    productForm.images = images
    productForm.thumbnail = fileList.value[0].url || URL.createObjectURL(fileList.value[0].raw)

    await publishProduct(productForm)
    ElMessage.success(t('publish.publishSuccess'))
    router.push('/my-products')
  } catch (error) {
    console.error('发布失败:', error)
  } finally {
    loading.value = false
  }
}

const handleReset = () => {
  productFormRef.value.resetFields()
  fileList.value = []
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