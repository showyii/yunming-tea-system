<!--
  AdminComments.vue - 评论管理页面
  用途：管理用户对商品的评论内容，可查看和删除评论。
  功能：评论列表展示（分页）、查看评论内容/评分/所属商品/评论用户、
        删除不当评论。
  说明：管理员可以删除包含违规内容的评论，维护社区环境。
-->
<template>
  <div class="admin-page">
    <!-- ==================== 页面头部 ==================== -->
    <div class="page-header">
      <div class="page-header__meta">
        <span class="page-header__eyebrow">Comment Review</span>
        <h3>评论管理</h3>
      </div>
    </div>

    <!-- ==================== 评论数据表格 ==================== -->
    <div class="admin-card-table">
      <el-table :data="tableData" v-loading="loading" border stripe>
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="username" label="用户" width="120" />
        <el-table-column prop="productName" label="商品" width="160" />
        <el-table-column prop="content" label="内容" />
        <el-table-column prop="rating" label="评分" width="80" />
        <el-table-column prop="createTime" label="时间" width="180" />
        <!-- 操作列：删除评论 -->
        <el-table-column label="操作" width="100" fixed="right">
          <template #default="{ row }">
            <el-button size="small" type="danger" @click="doDelete(row.id)">删除</el-button>
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
import { ElMessage, ElMessageBox } from 'element-plus'
import { adminCommentApi } from '@/api/admin'

// ==================== 响应式数据 ====================
const tableData = ref([])            // 评论列表数据
const loading = ref(false)           // 表格加载状态
const page = ref(1)                  // 当前页码
const size = ref(10)                 // 每页条数
const total = ref(0)                 // 总记录数

// ==================== 方法 ====================

// 加载评论列表（分页数据）
const loadData = async () => {
  loading.value = true
  try {
    const res = await adminCommentApi.list({ page: page.value, size: size.value })
    tableData.value = res.records
    total.value = res.total
  } finally { loading.value = false }
}

// 删除评论（需确认后执行）
const doDelete = async (id) => {
  await ElMessageBox.confirm('确定删除？', '提示', { type: 'warning' })
  await adminCommentApi.delete(id)
  ElMessage.success('删除成功')
  loadData()  // 刷新列表
}

// ==================== 生命周期：页面加载时获取数据 ====================
onMounted(loadData)
</script>
