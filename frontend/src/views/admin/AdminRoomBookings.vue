<!--
  AdminRoomBookings.vue - 包间预约管理页面
  用途：管理用户对茶馆包间的预约记录，支持确认、完成、取消操作。
  功能：预约列表展示、按状态操作（确认/完成/取消预约）、前端分页。
  说明：预约状态流转：待确认(0) -> 已确认(1) -> 已完成(2)，可取消(3)。
        分页在前端完成，页面加载时一次性获取所有预约记录。
-->
<template>
  <div class="admin-page">
    <!-- ==================== 页面头部 ==================== -->
    <div class="page-header">
      <div class="page-header__meta">
        <span class="page-header__eyebrow">Room Reservations</span>
        <h3>包间预约管理</h3>
      </div>
    </div>

    <!-- ==================== 预约数据表格 ==================== -->
    <div class="admin-card-table">
      <el-table :data="pagedData" v-loading="loading" border stripe>
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="roomName" label="包间" width="120" />
        <el-table-column prop="username" label="用户" width="120" />
        <el-table-column prop="bookingDate" label="日期" width="120" />
        <el-table-column prop="startTime" label="开始时间" width="100" />
        <el-table-column prop="endTime" label="结束时间" width="100" />
        <!-- 状态列：待确认(橙) / 已确认(蓝) / 已完成(绿) / 已取消(灰) -->
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="bookingStatusType(row.status)" size="small">{{ bookingStatusText(row.status) }}</el-tag>
          </template>
        </el-table-column>
        <!-- 操作列：根据当前状态显示不同操作按钮 -->
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="{ row }">
            <!-- 待确认状态：显示"确认"和"取消"按钮 -->
            <el-button v-if="row.status === 0" size="small" type="primary" @click="changeStatus(row.id, 1)">确认</el-button>
            <el-button v-if="row.status === 1" size="small" type="success" @click="changeStatus(row.id, 2)">完成</el-button>
            <el-button v-if="row.status === 0" size="small" type="danger" @click="changeStatus(row.id, 3)">取消</el-button>
          </template>
        </el-table-column>
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
import { ElMessage } from 'element-plus'
import { adminRoomBookingApi } from '@/api/admin'

// ==================== 响应式数据 ====================
const tableData = ref([])            // 全部预约数据
const loading = ref(false)           // 表格加载状态
const page = ref(1)                  // 当前页码
const size = ref(10)                 // 每页条数

// ==================== 计算属性 ====================
// 总记录数（用于分页组件）
const total = computed(() => tableData.value.length)

// 预约状态的文本映射：0-待确认, 1-已确认, 2-已完成, 3-已取消
const bookingStatusText = (status) => ['待确认', '已确认', '已完成', '已取消'][status] || '未知'

// 预约状态对应的标签颜色类型
const bookingStatusType = (status) => ['warning', 'primary', 'success', 'info'][status] || 'info'

// 前端分页：根据当前页码和每页条数截取表格数据
const pagedData = computed(() => {
  const start = (page.value - 1) * size.value
  return tableData.value.slice(start, start + size.value)
})

// ==================== 方法 ====================

// 加载全部预约记录
const loadData = async () => {
  loading.value = true
  try { tableData.value = await adminRoomBookingApi.list() }
  finally { loading.value = false }
}

// 更新预约状态（确认/完成/取消）
const changeStatus = async (id, status) => {
  await adminRoomBookingApi.updateStatus(id, { status })
  ElMessage.success('状态已更新')
  loadData()  // 刷新列表
}

// ==================== 生命周期：页面加载时获取数据 ====================
onMounted(loadData)
</script>
