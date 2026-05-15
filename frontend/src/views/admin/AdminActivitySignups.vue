<template>
  <div class="admin-page">
    <div class="page-header">
      <div class="page-header__meta">
        <span class="page-header__eyebrow">Activity Attendance</span>
        <h3>活动报名管理</h3>
      </div>
      <div class="page-actions">
        <el-select v-model="selectedActivity" class="admin-inline-filter" placeholder="选择活动" @change="onActivityChange">
          <el-option v-for="a in activities" :key="a.id" :label="a.title" :value="a.id" />
        </el-select>
      </div>
    </div>
    <div class="admin-card-table">
      <el-table :data="pagedData" v-loading="loading" border stripe>
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="activityTitle" label="活动" width="180" />
        <el-table-column prop="username" label="用户" width="120" />
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="signupStatusType(row.status)" size="small">{{ signupStatusText(row.status) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="报名时间" width="180" />
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
import { adminActivitySignupApi, adminActivityApi } from '@/api/admin'

const tableData = ref([])
const loading = ref(false)
const activities = ref([])
const selectedActivity = ref(null)
const page = ref(1)
const size = ref(10)
const total = computed(() => tableData.value.length)
const signupStatusText = (status) => ['已报名', '已签到', '已取消'][status] || '未知'
const signupStatusType = (status) => ['primary', 'success', 'info'][status] || 'info'
const pagedData = computed(() => {
  const start = (page.value - 1) * size.value
  return tableData.value.slice(start, start + size.value)
})

const loadData = async () => {
  loading.value = true
  try {
    activities.value = await adminActivityApi.list()
    if (activities.value.length > 0) {
      selectedActivity.value = activities.value[0].id
      tableData.value = await adminActivitySignupApi.list({ activityId: activities.value[0].id })
    }
  } finally { loading.value = false }
}

const onActivityChange = async (id) => {
  loading.value = true
  page.value = 1
  try { tableData.value = await adminActivitySignupApi.list({ activityId: id }) } finally { loading.value = false }
}

onMounted(loadData)
</script>
