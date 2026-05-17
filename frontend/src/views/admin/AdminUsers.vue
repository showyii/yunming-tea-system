<!--
  AdminUsers.vue - 用户管理页面
  用途：管理茶馆系统的注册用户，包括查看用户信息和启用/禁用用户。
  功能：用户列表展示（分页）、按用户名/手机号/邮箱搜索、
        查看用户角色（管理员/普通用户）、切换用户启用/禁用状态。
  说明：管理员可以禁用违规用户使其无法登录系统。
-->
<template>
  <div class="admin-page">
    <!-- ==================== 页面头部：标题 + 搜索框 ==================== -->
    <div class="page-header">
      <div class="page-header__meta">
        <span class="page-header__eyebrow">User Directory</span>
        <h3>用户管理</h3>
      </div>
      <div class="page-actions">
        <!-- 搜索框：支持按用户名/手机号/邮箱搜索，回车或清空时触发搜索 -->
        <el-input
          v-model="keyword"
          class="admin-inline-filter"
          placeholder="搜索用户名 / 手机号 / 邮箱"
          clearable
          @keyup.enter="loadData"
          @clear="loadData"
        />
      </div>
    </div>

    <!-- ==================== 用户数据表格 ==================== -->
    <div class="admin-card-table">
      <el-table :data="tableData" v-loading="loading" border stripe>
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="username" label="用户名" width="150" />
        <el-table-column prop="phone" label="手机号" width="140" />
        <el-table-column prop="email" label="邮箱" />
        <!-- 角色列：管理员(橙色) / 普通用户(灰色) -->
        <el-table-column prop="role" label="角色" width="100">
          <template #default="{ row }">
            <el-tag :type="row.role === 1 ? 'warning' : 'info'" size="small">
              {{ row.role === 1 ? '管理员' : '普通用户' }}
            </el-tag>
          </template>
        </el-table-column>
        <!-- 状态列：正常(绿色) / 禁用(红色) -->
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'danger'" size="small">
              {{ row.status === 1 ? '正常' : '禁用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="注册时间" width="180" />
        <!-- 操作列：切换启用/禁用状态 -->
        <el-table-column label="操作" width="120" fixed="right">
          <template #default="{ row }">
            <el-button size="small" :type="row.status === 1 ? 'warning' : 'success'" @click="toggleStatus(row)">
              {{ row.status === 1 ? '禁用' : '启用' }}
            </el-button>
          </template>
        </el-table-column>
      </el-table>
    </div>

    <!-- ==================== 分页组件 ==================== -->
    <div class="pagination">
      <el-pagination v-model:current-page="page" :total="total" :page-size="size"
        layout="total, prev, pager, next" @current-change="loadData" />
    </div>
  </div>
</template>

<script setup>
// ==================== 依赖导入 ====================
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { adminUserApi } from '@/api/admin'

// ==================== 响应式数据 ====================
const tableData = ref([])            // 用户列表数据
const loading = ref(false)           // 表格加载状态
const page = ref(1)                  // 当前页码
const size = ref(10)                 // 每页条数
const total = ref(0)                 // 总记录数
const keyword = ref('')              // 搜索关键词

// ==================== 方法 ====================

// 加载用户列表（支持关键词搜索和分页）
const loadData = async () => {
  loading.value = true
  try {
    const params = { page: page.value, size: size.value }
    // 如果有关键词，附加 keyword 参数进行模糊搜索
    if (keyword.value) params.keyword = keyword.value
    const res = await adminUserApi.list(params)
    tableData.value = res.records
    total.value = res.total
  } finally { loading.value = false }
}

// 切换用户状态（启用 <-> 禁用）
const toggleStatus = async (row) => {
  // 计算目标状态：当前正常(1)则切换为禁用(0)，当前禁用(0)则切换为正常(1)
  const newStatus = row.status === 1 ? 0 : 1
  await adminUserApi.updateStatus(row.id, { status: newStatus })
  ElMessage.success('状态已更新')
  loadData()  // 刷新列表
}

// ==================== 生命周期：页面加载时获取数据 ====================
onMounted(loadData)
</script>
