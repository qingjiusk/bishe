<template>
  <div class="admin-products">
    <h2>商品审核</h2>
    <el-table :data="products" v-loading="loading" border>
      <el-table-column prop="id" label="ID" width="80" />
      <el-table-column label="商品图片" width="120">
        <template #default="{ row }">
          <el-image
            v-if="row.thumbnail"
            :src="getImageUrl(row.thumbnail)"
            :preview-src-list="getImageList(row.images)"
            fit="cover"
            style="width: 80px; height: 80px; border-radius: 4px;"
          />
          <span v-else>无图片</span>
        </template>
      </el-table-column>
      <el-table-column prop="name" label="商品名称" />
      <el-table-column prop="price" label="价格" width="100" />
      <el-table-column prop="createTime" label="发布时间" width="180" />
      <el-table-column label="审核状态" width="120">
        <template #default="{ row }">
          <el-tag :type="getStatusType(row.auditStatus)">
            {{ getStatusText(row.auditStatus) }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="200">
        <template #default="{ row }">
          <template v-if="row.auditStatus === 0">
            <el-button size="small" type="success" @click="auditProduct(row.id, 1)">通过</el-button>
            <el-button size="small" type="danger" @click="auditProduct(row.id, 2)">拒绝</el-button>
          </template>
          <span v-else>已审核</span>
        </template>
      </el-table-column>
    </el-table>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import axios from 'axios'
import { ElMessage, ElMessageBox } from 'element-plus'

const loading = ref(false)
const products = ref([])

const getStatusType = (status) => {
  const types = { 0: 'warning', 1: 'success', 2: 'danger' }
  return types[status] || 'info'
}

const getStatusText = (status) => {
  const texts = { 0: '待审核', 1: '通过', 2: '拒绝' }
  return texts[status] || '未知'
}

const getImageUrl = (thumbnail) => {
  if (!thumbnail) return ''
  if (thumbnail.startsWith('http')) return thumbnail
  return `/api${thumbnail}`
}

const getImageList = (images) => {
  if (!images) return []
  return images.split(',').map(img => {
    if (img.startsWith('http')) return img
    return `/api${img}`
  })
}

const fetchAuditProducts = async () => {
  try {
    loading.value = true
    const res = await axios.get('/api/admin/products/audit')
    if (res.data.code === 200) {
      products.value = res.data.data.records
    }
  } catch (error) {
    console.error('获取待审核商品失败:', error)
  } finally {
    loading.value = false
  }
}

const auditProduct = async (id, status) => {
  try {
    let auditReason = ''
    if (status === 2) {
      const { value } = await ElMessageBox.prompt('请输入拒绝原因', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消'
      })
      auditReason = value
    }

    const res = await axios.put(`/api/admin/product/${id}/audit`, {
      auditStatus: status,
      auditReason: auditReason
    })

    if (res.data.code === 200) {
      ElMessage.success(status === 1 ? '审核通过' : '审核拒绝')
      fetchAuditProducts() // 重新加载列表
    } else {
      ElMessage.error(res.data.message || '审核失败')
    }
  } catch (error) {
    if (error !== 'cancel') {
      console.error('审核失败:', error)
      ElMessage.error('审核失败')
    }
  }
}

onMounted(() => {
  fetchAuditProducts()
})
</script>

<style scoped>
.admin-products h2 {
  margin-bottom: 20px;
}
</style>