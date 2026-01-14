<template>
  <div class="admin-orders">
    <h2>订单管理</h2>
    <el-table :data="orders" v-loading="loading" border>
      <el-table-column prop="orderNo" label="订单号" width="180" />
      <el-table-column prop="totalAmount" label="订单金额" width="120" />
      <el-table-column prop="createTime" label="下单时间" width="180" />
      <el-table-column label="订单状态" width="120">
        <template #default="{ row }">
          <el-tag :type="getStatusType(row.status)">
            {{ getStatusText(row.status) }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="200">
        <template #default="{ row }">
          <template v-if="row.status === 1">
            <el-button size="small" type="primary" @click="shipOrder(row.id)">发货</el-button>
          </template>
          <template v-else-if="row.status === 2">
            <el-button size="small" type="success" @click="confirmOrder(row.id)">确认收货</el-button>
          </template>
          <span v-else>已完成</span>
        </template>
      </el-table-column>
    </el-table>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import axios from 'axios'
import { ElMessage } from 'element-plus'

const loading = ref(false)
const orders = ref([])

const getStatusType = (status) => {
  const types = { 1: 'warning', 2: 'primary', 3: 'success' }
  return types[status] || 'info'
}

const getStatusText = (status) => {
  const texts = { 1: '待发货', 2: '已发货', 3: '已收货' }
  return texts[status] || '未知'
}

const fetchOrders = async () => {
  try {
    loading.value = true
    const res = await axios.get('/api/admin/orders')
    if (res.data.code === 200) {
      orders.value = res.data.data.records
    }
  } catch (error) {
    console.error('获取订单列表失败:', error)
  } finally {
    loading.value = false
  }
}

const shipOrder = async (id) => {
  try {
    const res = await axios.put(`/api/admin/order/${id}/ship`)
    if (res.data.code === 200) {
      ElMessage.success('发货成功')
      fetchOrders() // 重新加载列表
    } else {
      ElMessage.error(res.data.message || '发货失败')
    }
  } catch (error) {
    console.error('发货失败:', error)
    ElMessage.error('发货失败')
  }
}

const confirmOrder = async (id) => {
  try {
    const res = await axios.put(`/api/admin/order/${id}/confirm`)
    if (res.data.code === 200) {
      ElMessage.success('确认收货成功')
      fetchOrders() // 重新加载列表
    } else {
      ElMessage.error(res.data.message || '确认失败')
    }
  } catch (error) {
    console.error('确认收货失败:', error)
    ElMessage.error('确认失败')
  }
}

onMounted(() => {
  fetchOrders()
})
</script>

<style scoped>
.admin-orders h2 {
  margin-bottom: 20px;
}
</style>