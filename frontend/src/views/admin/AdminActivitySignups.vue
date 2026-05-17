<!--
  AdminActivitySignups.vue - 活动报名管理页面
  用途：查看各活动的用户报名情况，包括报名状态（已报名/已签到/已取消）。
  功能：按活动筛选报名列表、查看报名用户及状态、前端分页展示。
  说明：页面加载时默认选中第一个活动显示其报名数据，
        切换活动时重新加载该活动的报名列表。
        分页在前端完成（计算属性 pagedData），不需要后端分页。
-->
<template>
  <div class="admin-page">
    <!-- ==================== 页面头部：标题 + 活动选择器 ==================== -->
    <div class="page-header">
      <div class="page-header__meta">
        <span class="page-header__eyebrow">Activity Attendance</span>
        <h3>活动报名管理</h3>
      </div>
      <div class="page-actions">
        <!-- 下拉选择要查看的活动 -->
        <el-select v-model="selectedActivity" class="admin-inline-filter" placeholder="选择活动" @change="onActivityChange">
          <el-option v-for="a in activities" :key="a.id" :label="a.title" :value="a.id" />
        </el-select>
      </div>
    </div>

    <!-- ==================== 报名数据表格 ==================== -->
    <div class="admin-card-table">
      <el-table :data="pagedData" v-loading="loading" border stripe>
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="activityTitle" label="活动" width="180" />
        <el-table-column prop="username" label="用户" width="120" />
        <!-- 状态列：已报名(蓝) / 已签到(绿) / 已取消(灰) -->
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="signupStatusType(row.status)" size="small">{{ signupStatusText(row.status) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="报名时间" width="180" />
      </el-table>
    </div>

    <!-- ==================== 前端分页组件 ==================== -->
    <div class="pagination">
      <el-pagination v-model:current-page="page" :total="total" :page-size="size"
        layout="total, prev, pager, next" />
    </div>
  </div>
</template>

<script setup>
// ==================== 依赖导入 ====================
import { ref, computed, onMounted } from 'vue'
import { adminActivitySignupApi, adminActivityApi } from '@/api/admin'

// ==================== 响应式数据 ====================
const tableData = ref([])            // 全部报名数据（当前选中活动的所有报名记录）
const loading = ref(false)           // 表格加载状态
const activities = ref([])           // 活动下拉列表数据
const selectedActivity = ref(null)   // 当前选中的活动ID
const page = ref(1)                  // 当前页码
const size = ref(10)                 // 每页条数

// ==================== 计算属性 ====================
// 总记录数 = 当前活动报名总数（用于分页组件）
const total = computed(() => tableData.value.length)

// 报名状态的文本映射：0-已报名, 1-已签到, 2-已取消
const signupStatusText = (status) => ['已报名', '已签到', '已取消'][status] || '未知'

// 报名状态对应的标签颜色类型
const signupStatusType = (status) => ['primary', 'success', 'info'][status] || 'info'

// 前端分页：根据当前页码和每页条数截取表格数据
const pagedData = computed(() => {
  const start = (page.value - 1) * size.value
  return tableData.value.slice(start, start + size.value)
})

// ==================== 方法 ====================

// 初始加载：获取活动列表，默认选中第一个活动加载其报名数据
const loadData = async () => {
  loading.value = true
  try {
    activities.value = await adminActivityApi.list()
    if (activities.value.length > 0) {
      selectedActivity.value = activities.value[0].id      // 默认选中第一个活动
      tableData.value = await adminActivitySignupApi.list({ activityId: activities.value[0].id })
    }
  } finally { loading.value = false }
}

// 切换活动时重新加载对应活动的报名数据
const onActivityChange = async (id) => {
  loading.value = true
  page.value = 1    // 切换活动时重置页码
  try { tableData.value = await adminActivitySignupApi.list({ activityId: id }) } finally { loading.value = false }
}

// ==================== 生命周期：页面加载时获取数据 ====================
onMounted(loadData)
</script>
