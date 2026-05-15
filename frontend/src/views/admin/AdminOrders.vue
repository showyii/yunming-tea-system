<template>
  <div class="admin-page">
    <div class="page-header">
      <div class="page-header__meta">
        <span class="page-header__eyebrow">Order Fulfillment</span>
        <h3>订单管理</h3>
      </div>
    </div>

    <el-tabs v-model="statusFilter" @tab-change="loadData">
      <el-tab-pane label="全部" name="" />
      <el-tab-pane label="待付款" name="0" />
      <el-tab-pane label="已付款" name="1" />
      <el-tab-pane label="已发货" name="2" />
      <el-tab-pane label="已完成" name="3" />
      <el-tab-pane label="已取消" name="4" />
    </el-tabs>

    <div class="admin-card-table">
      <el-table :data="tableData" v-loading="loading" border stripe>
        <el-table-column prop="id" label="订单号" width="100" />
        <el-table-column prop="username" label="用户" width="120" />
        <el-table-column prop="payAmount" label="金额" width="100" />
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag size="small" :type="statusTagType(row.status)">
              {{ ['待付款', '已付款', '已发货', '已完成', '已取消'][row.status] }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="下单时间" width="180" />
        <el-table-column label="操作" width="280" fixed="right">
          <template #default="{ row }">
            <el-button size="small" @click="openDetail(row.id)">详情</el-button>
            <el-button v-if="row.status === 1" size="small" type="primary" @click="changeStatus(row.id, 2)">发货</el-button>
            <el-button v-if="row.status === 2" size="small" type="success" @click="changeStatus(row.id, 3)">完成</el-button>
          </template>
        </el-table-column>
      </el-table>
    </div>

    <div class="pagination">
      <el-pagination
        v-model:current-page="page"
        :total="total"
        :page-size="size"
        layout="total, prev, pager, next"
        @current-change="loadData"
      />
    </div>

    <el-dialog v-model="detailVisible" title="订单详情" width="760px">
      <div v-if="detail" v-loading="detailLoading" class="detail-panel">
        <div class="detail-meta">
          <div><strong>订单号</strong><span>{{ detail.id }}</span></div>
          <div><strong>用户</strong><span>{{ detail.username }}</span></div>
          <div><strong>金额</strong><span>¥{{ detail.payAmount }}</span></div>
          <div><strong>状态</strong><span>{{ ['待付款', '已付款', '已发货', '已完成', '已取消'][detail.status] }}</span></div>
          <div><strong>时间</strong><span>{{ detail.createTime }}</span></div>
        </div>
        <el-table :data="detail.items" border size="small">
          <el-table-column prop="productName" label="商品" />
          <el-table-column prop="price" label="单价" width="100" />
          <el-table-column prop="quantity" label="数量" width="80" />
        </el-table>
      </div>
    </el-dialog>
  </div>
</template>

<script setup>
import { onMounted, ref } from 'vue'
import { ElMessage } from 'element-plus'
import { adminOrderApi } from '@/api/admin'

const tableData = ref([])
const loading = ref(false)
const page = ref(1)
const size = ref(10)
const total = ref(0)
const statusFilter = ref('')
const detailVisible = ref(false)
const detail = ref(null)
const detailLoading = ref(false)

const statusTagType = (status) => ['warning', 'success', 'primary', 'info', 'danger'][status] || 'info'

const loadData = async () => {
  loading.value = true
  try {
    const params = { page: page.value, size: size.value }
    if (statusFilter.value !== '') params.status = statusFilter.value
    const res = await adminOrderApi.list(params)
    tableData.value = res.records
    total.value = res.total
  } finally {
    loading.value = false
  }
}

const changeStatus = async (id, status) => {
  await adminOrderApi.updateStatus(id, { status })
  ElMessage.success('状态已更新')
  loadData()
}

const openDetail = async (id) => {
  detailVisible.value = true
  detailLoading.value = true
  try {
    detail.value = await adminOrderApi.getById(id)
  } finally {
    detailLoading.value = false
  }
}

onMounted(loadData)
</script>

<style scoped>
.detail-panel {
  display: grid;
  gap: 16px;
}

.detail-meta {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 12px;
}

.detail-meta div {
  padding: 14px;
  border-radius: 12px;
  background: rgba(245, 241, 232, 0.72);
}

.detail-meta strong,
.detail-meta span {
  display: block;
}

.detail-meta strong {
  margin-bottom: 6px;
  color: var(--color-wood);
  font-size: 13px;
}

.detail-meta span {
  color: var(--color-ink);
}

@media (max-width: 768px) {
  .detail-meta {
    grid-template-columns: 1fr;
  }
}
</style>
