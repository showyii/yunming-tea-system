<!--
  AdminOrders.vue - 订单管理页面
  用途：管理茶馆所有订单，支持按状态筛选、查看详情、更新订单状态。
  功能：按状态Tab筛选订单（全部/待付款/已付款/已发货/已完成/已取消）、
        查看订单详情（含订单商品明细）、发货操作、完成操作、分页展示。
  说明：订单状态流转：待付款(0) -> 已付款(1) -> 已发货(2) -> 已完成(3)，可取消(4)。
-->
<template>
  <div class="admin-page">
    <!-- ==================== 页面头部 ==================== -->
    <div class="page-header">
      <div class="page-header__meta">
        <span class="page-header__eyebrow">Order Fulfillment</span>
        <h3>订单管理</h3>
      </div>
    </div>

    <!-- ==================== 状态筛选 Tab 切换 ==================== -->
    <el-tabs v-model="statusFilter" @tab-change="loadData">
      <el-tab-pane label="全部" name="" />
      <el-tab-pane label="待付款" name="0" />
      <el-tab-pane label="已付款" name="1" />
      <el-tab-pane label="已发货" name="2" />
      <el-tab-pane label="已完成" name="3" />
      <el-tab-pane label="已取消" name="4" />
    </el-tabs>

    <!-- ==================== 订单数据表格 ==================== -->
    <div class="admin-card-table">
      <el-table :data="tableData" v-loading="loading" border stripe>
        <el-table-column prop="id" label="订单号" width="100" />
        <el-table-column prop="username" label="用户" width="120" />
        <el-table-column prop="payAmount" label="金额" width="100" />
        <!-- 状态列：用不同颜色的标签区分订单状态 -->
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag size="small" :type="statusTagType(row.status)">
              {{ ['待付款', '已付款', '已发货', '已完成', '已取消'][row.status] }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="下单时间" width="180" />
        <!-- 操作列：详情/发货/完成 -->
        <el-table-column label="操作" width="280" fixed="right">
          <template #default="{ row }">
            <el-button size="small" @click="openDetail(row.id)">详情</el-button>
            <!-- 已付款状态显示"发货"按钮 -->
            <el-button v-if="row.status === 1" size="small" type="primary" @click="changeStatus(row.id, 2)">发货</el-button>
            <!-- 已发货状态显示"完成"按钮 -->
            <el-button v-if="row.status === 2" size="small" type="success" @click="changeStatus(row.id, 3)">完成</el-button>
          </template>
        </el-table-column>
      </el-table>
    </div>

    <!-- ==================== 分页组件 ==================== -->
    <div class="pagination">
      <el-pagination
        v-model:current-page="page"
        :total="total"
        :page-size="size"
        layout="total, prev, pager, next"
        @current-change="loadData"
      />
    </div>

    <!-- ==================== 订单详情弹窗 ==================== -->
    <el-dialog v-model="detailVisible" title="订单详情" width="760px">
      <div v-if="detail" v-loading="detailLoading" class="detail-panel">
        <!-- 订单基本信息：网格布局展示 -->
        <div class="detail-meta">
          <div><strong>订单号</strong><span>{{ detail.id }}</span></div>
          <div><strong>用户</strong><span>{{ detail.username }}</span></div>
          <div><strong>金额</strong><span>¥{{ detail.payAmount }}</span></div>
          <div><strong>状态</strong><span>{{ ['待付款', '已付款', '已发货', '已完成', '已取消'][detail.status] }}</span></div>
          <div><strong>时间</strong><span>{{ detail.createTime }}</span></div>
        </div>
        <!-- 订单商品明细表格 -->
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
// ==================== 依赖导入 ====================
import { onMounted, ref } from 'vue'
import { ElMessage } from 'element-plus'
import { adminOrderApi } from '@/api/admin'

// ==================== 响应式数据 ====================
const tableData = ref([])            // 订单列表数据
const loading = ref(false)           // 表格加载状态
const page = ref(1)                  // 当前页码
const size = ref(10)                 // 每页条数
const total = ref(0)                 // 总记录数
const statusFilter = ref('')         // 当前选中的状态筛选（空字符串=全部）

// 订单详情弹窗相关状态
const detailVisible = ref(false)     // 详情弹窗是否可见
const detail = ref(null)             // 订单详情数据
const detailLoading = ref(false)     // 详情加载状态

// ==================== 工具方法 ====================
// 根据订单状态返回对应的标签类型（颜色）
// 状态：0-待付款(warning橙), 1-已付款(success绿), 2-已发货(primary蓝), 3-已完成(info灰), 4-已取消(danger红)
const statusTagType = (status) => ['warning', 'success', 'primary', 'info', 'danger'][status] || 'info'

// ==================== 方法 ====================

// 加载订单列表（支持按状态筛选和分页）
const loadData = async () => {
  loading.value = true
  try {
    const params = { page: page.value, size: size.value }
    // 如果选了具体状态（非空字符串），则附加 status 参数
    if (statusFilter.value !== '') params.status = statusFilter.value
    const res = await adminOrderApi.list(params)
    tableData.value = res.records
    total.value = res.total
  } finally {
    loading.value = false
  }
}

// 更新订单状态（如：发货、完成）
const changeStatus = async (id, status) => {
  await adminOrderApi.updateStatus(id, { status })
  ElMessage.success('状态已更新')
  loadData()  // 刷新列表
}

// 打开订单详情弹窗
const openDetail = async (id) => {
  detailVisible.value = true
  detailLoading.value = true
  try {
    detail.value = await adminOrderApi.getById(id)
  } finally {
    detailLoading.value = false
  }
}

// ==================== 生命周期：页面加载时获取数据 ====================
onMounted(loadData)
</script>

<style scoped>
/* ==================== 订单详情面板样式 ==================== */
.detail-panel {
  display: grid;
  gap: 16px;
}

/* 基本信息网格：双列布局 */
.detail-meta {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 12px;
}

/* 信息小卡片 */
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

/* ==================== 响应式：小屏幕单列布局 ==================== */
@media (max-width: 768px) {
  .detail-meta {
    grid-template-columns: 1fr;
  }
}
</style>
