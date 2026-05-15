<template>
  <div class="admin-page">
    <div class="page-header">
      <div class="page-header__meta">
        <span class="page-header__eyebrow">Room Reservations</span>
        <h3>包间预约管理</h3>
      </div>
    </div>
    <div class="admin-card-table">
      <el-table :data="pagedData" v-loading="loading" border stripe>
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="roomName" label="包间" width="120" />
        <el-table-column prop="username" label="用户" width="120" />
        <el-table-column prop="bookingDate" label="日期" width="120" />
        <el-table-column prop="startTime" label="开始时间" width="100" />
        <el-table-column prop="endTime" label="结束时间" width="100" />
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="bookingStatusType(row.status)" size="small">{{ bookingStatusText(row.status) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="{ row }">
            <el-button v-if="row.status === 0" size="small" type="primary" @click="changeStatus(row.id, 1)">确认</el-button>
            <el-button v-if="row.status === 1" size="small" type="success" @click="changeStatus(row.id, 2)">完成</el-button>
            <el-button v-if="row.status === 0" size="small" type="danger" @click="changeStatus(row.id, 3)">取消</el-button>
          </template>
        </el-table-column>
      </el-table>
    </div>
    <div class="pagination">
      <el-pagination v-model:current-page="page" :total="total" :page-size="size"
        layout="total, prev, pager, next" />
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { adminRoomBookingApi } from '@/api/admin'

const tableData = ref([])
const loading = ref(false)
const page = ref(1)
const size = ref(10)
const total = computed(() => tableData.value.length)
const bookingStatusText = (status) => ['待确认', '已确认', '已完成', '已取消'][status] || '未知'
const bookingStatusType = (status) => ['warning', 'primary', 'success', 'info'][status] || 'info'
const pagedData = computed(() => {
  const start = (page.value - 1) * size.value
  return tableData.value.slice(start, start + size.value)
})

const loadData = async () => {
  loading.value = true
  try { tableData.value = await adminRoomBookingApi.list() }
  finally { loading.value = false }
}

const changeStatus = async (id, status) => {
  await adminRoomBookingApi.updateStatus(id, { status })
  ElMessage.success('状态已更新')
  loadData()
}

onMounted(loadData)
</script>
