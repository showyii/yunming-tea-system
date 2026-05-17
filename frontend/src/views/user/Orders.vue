<!--
  我的订单页（Orders.vue）
  云茗茶馆的用户订单列表页面，展示所有订单并支持状态筛选。
  页面功能：
  - 顶部概览区：当前筛选状态、当前页记录数
  - 状态标签切换：全部 / 待付款 / 已付款 / 已发货 / 已完成 / 已取消
  - 订单卡片列表：订单编号、状态、商品预览、金额、操作按钮
  - 支持查看详情、取消订单、模拟付款
  - 分页组件
-->
<template>
  <div class="page">
    <!-- 页面顶部导航栏 -->
    <NavBar />

    <div class="container orders-page" v-loading="loading">
      <!-- ========== 顶部：概览区 ========== -->
      <section class="inner-hero paper-panel">
        <div class="inner-hero__copy">
          <h1>把每一笔买茶记录稳稳收好，金额、状态与后续安排都看得明白</h1>
          <p>无论是刚下单、已经付款，还是正在等茶寄到，这里都会把每一笔订单的进度安静列好，方便你随时回来看看。</p>
        </div>

        <!-- 右侧订单概览 -->
        <div class="inner-hero__aside">
          <strong>订单概览</strong>
          <div class="hero-summary">
            <span>当前筛选</span>
            <b>{{ currentTabLabel }}</b>
          </div>
          <div class="hero-summary">
            <span>当前页记录</span>
            <b>{{ orders.length }}</b>
          </div>
          <p>可以先按状态筛选，再决定是继续支付、查看详情，还是取消尚未进入处理流程的订单。</p>
        </div>
      </section>

      <!-- ========== 订单记录区 ========== -->
      <section class="desktop-section">
        <div class="section-title section-title-left">
          <h2>订单记录</h2>
          <p>按状态筛一筛，就能更快找到正在等付款、等发货，或已经收妥的那一单，回看和处理都会更顺手。</p>
        </div>

        <!-- 状态标签切换栏（圆角胶囊按钮） -->
        <div class="status-tabs paper-panel">
          <button
            v-for="status in statusTabs"
            :key="String(status.value)"
            :class="{ active: currentStatus === status.value }"
            @click="currentStatus = status.value; page = 1; loadData()"
          >
            {{ status.label }}
          </button>
        </div>

        <!-- 订单列表 -->
        <div v-if="orders.length > 0" class="order-list">
          <!-- 单个订单卡片 -->
          <article v-for="order in orders" :key="order.id" class="order-card paper-panel">
            <!-- 订单头部：订单编号 + 状态标签 -->
            <div class="order-card__header">
              <div>
                <span class="order-card__eyebrow">Order No.</span>
                <div class="order-card__no">{{ order.orderNo }}</div>
              </div>
              <span class="order-card__status" :class="statusClass(order.status)">{{ statusText(order.status) }}</span>
            </div>

            <!-- 订单主体：商品封面图 + 摘要信息 + 金额（点击跳转详情） -->
            <div class="order-card__body" @click="$router.push(`/orders/${order.id}`)">
              <div class="order-card__image">
                <img :src="resolveProductImage(order, order.firstProductImage)" alt="">
              </div>

              <div class="order-card__copy">
                <h3>共 {{ order.itemCount }} 件茶品，等待后续处理</h3>
                <div class="order-card__meta">
                  <span>下单时间 {{ formatTime(order.createTime) }}</span>
                  <span>可进入详情查看收货与支付信息</span>
                </div>
              </div>

              <div class="order-card__amount">¥{{ order.totalAmount }}</div>
            </div>

            <!-- 订单操作按钮区 -->
            <div class="order-card__actions">
              <el-button @click="$router.push(`/orders/${order.id}`)">查看详情</el-button>
              <!-- 待付款状态：显示取消订单 + 立即付款 -->
              <el-button v-if="order.status === 0" type="warning" @click="doCancel(order)">取消订单</el-button>
              <el-button v-if="order.status === 0" type="primary" @click="doPay(order)">立即付款</el-button>
            </div>
          </article>
        </div>

        <!-- 空状态提示 -->
        <div v-else-if="!loading" class="empty-state paper-panel">
          <h3>{{ currentStatus !== null ? '暂无此类订单' : '还没有任何订单' }}</h3>
          <p>等你从茶品页带走喜欢的茶后，这里就会慢慢记下每一笔购买与寄送进度。</p>
          <router-link to="/products" class="inner-hero__link">去逛逛茶品</router-link>
        </div>

        <!-- 分页组件 -->
        <div class="pagination" v-if="total > size">
          <el-pagination
            v-model:current-page="page"
            background
            layout="prev, pager, next"
            :total="total"
            :page-size="size"
            @current-change="loadData"
          />
        </div>
      </section>
    </div>

    <!-- 页面底部页脚栏 -->
    <FooterBar />
  </div>
</template>

<script setup>
/**
 * 我的订单页脚本逻辑
 * 负责：状态筛选、订单列表加载、取消订单、模拟支付
 */
import { computed, onMounted, ref } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import NavBar from '@/components/NavBar.vue'
import FooterBar from '@/components/FooterBar.vue'
import { orderApi } from '@/api/order'
import { resolveProductImage } from '@/utils/localImage'

// ===== 响应式变量 =====
const orders = ref([])            // 订单列表
const loading = ref(false)        // 页面加载状态
const page = ref(1)               // 当前页码
const size = ref(10)              // 每页条数
const total = ref(0)              // 订单总数
const currentStatus = ref(null)   // 当前筛选状态（null 表示全部）

// ===== 状态标签定义 =====
const statusTabs = [
  { label: '全部', value: null },
  { label: '待付款', value: 0 },
  { label: '已付款', value: 1 },
  { label: '已发货', value: 2 },
  { label: '已完成', value: 3 },
  { label: '已取消', value: 4 }
]

// ===== 计算属性 =====
// 当前选中标签的文字
const currentTabLabel = computed(() => statusTabs.find((item) => item.value === currentStatus.value)?.label || '全部')

/**
 * 将订单状态码转换为中文文本
 * @param {number} value - 订单状态码
 * @returns {string} 状态中文文字
 */
const statusText = (value) => ['待付款', '已付款', '已发货', '已完成', '已取消'][value] || '未知'

/**
 * 将订单状态码转换为 CSS 类名
 * @param {number} value - 订单状态码
 * @returns {string} CSS 类名
 */
const statusClass = (value) => ['status-pending', 'status-paid', 'status-shipped', 'status-done', 'status-cancel'][value] || ''

/**
 * 加载订单列表（根据当前筛选状态和分页参数）
 */
const loadData = async () => {
  loading.value = true
  try {
    const params = { page: page.value, size: size.value }
    if (currentStatus.value !== null) params.status = currentStatus.value
    const result = await orderApi.getList(params)
    orders.value = result.records || []
    total.value = result.total || 0
  } finally {
    loading.value = false
  }
}

/**
 * 取消订单
 * 弹出确认框后调用 API，成功后刷新列表
 * @param {object} order - 订单对象
 */
const doCancel = async (order) => {
  try {
    await ElMessageBox.confirm('确定取消此订单？', '提示', { type: 'warning' })
    await orderApi.cancel(order.id)
    ElMessage.success('订单已取消')
    loadData()
  } catch (e) {
    // 用户取消操作
  }
}

/**
 * 模拟支付
 * 弹出确认框（提示模拟支付，不会实际扣款），调用 API 后刷新列表
 * @param {object} order - 订单对象
 */
const doPay = async (order) => {
  try {
    await ElMessageBox.confirm(`确认支付 ¥${order.totalAmount}？\n（模拟支付，不会实际扣款）`, '模拟支付', { type: 'info' })
    await orderApi.pay(order.id)
    ElMessage.success('支付成功')
    loadData()
  } catch (e) {
    // 用户取消操作
  }
}

/**
 * 格式化时间戳为中文日期时间字符串
 * @param {string|number} value - 时间戳
 * @returns {string} 格式化后的日期时间
 */
const formatTime = (value) => (value ? new Date(value).toLocaleString('zh-CN') : '')

/**
 * 组件挂载后：加载订单列表
 */
onMounted(loadData)
</script>

<style scoped>
/* ===== 页面整体布局 ===== */
.page {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
}

.orders-page {
  flex: 1;
  padding-top: 28px;
}

/* ===== 顶部概览面板 ===== */
.inner-hero {
  display: grid;
  grid-template-columns: minmax(0, 1.34fr) minmax(300px, 0.66fr);
  gap: 28px;
  padding: 34px 36px;
  align-items: stretch;
}

.inner-hero__eyebrow,
.order-card__eyebrow,
.empty-state__eyebrow {
  color: var(--color-red);
  font-size: 12px;
  letter-spacing: 0.3em;
  text-transform: uppercase;
}

.inner-hero__copy h1 {
  margin: 12px 0 14px;
  color: var(--color-ink);
  font-family: var(--font-display);
  font-size: 48px;
  font-weight: 600;
  line-height: 1.2;
  letter-spacing: 0.06em;
}

.inner-hero__copy p,
.inner-hero__aside p,
.empty-state p {
  margin: 0;
  color: var(--color-ink-soft);
  font-size: 15px;
  line-height: 1.9;
}

.inner-hero__aside {
  padding: 24px;
  background: rgba(255, 252, 246, 0.82);
  border: 1px solid rgba(204, 188, 161, 0.4);
}

.inner-hero__aside strong,
.empty-state h3 {
  display: block;
  margin: 0 0 12px;
  color: var(--color-wood);
  font-family: var(--font-display);
  font-size: 24px;
  font-weight: 600;
  letter-spacing: 0.06em;
}

/* 概览摘要行 */
.hero-summary {
  display: flex;
  align-items: baseline;
  justify-content: space-between;
  gap: 12px;
  padding: 14px 0;
  border-top: 1px solid rgba(204, 188, 161, 0.32);
  color: var(--color-ink-soft);
}

.hero-summary + .hero-summary {
  border-bottom: 1px solid rgba(204, 188, 161, 0.32);
}

.hero-summary b {
  color: var(--color-red);
  font-size: 26px;
  font-weight: 700;
}

/* ===== 状态标签切换栏 ===== */
.status-tabs {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
  padding: 14px;
  margin-bottom: 22px;
}

.status-tabs button {
  min-height: 40px;
  padding: 0 18px;
  border: 1px solid var(--color-border);
  border-radius: 999px;
  background: rgba(255, 250, 242, 0.74);
  color: var(--color-ink-soft);
  cursor: pointer;
  transition: background var(--transition-base), border-color var(--transition-base), color var(--transition-base);
}

.status-tabs button.active {
  background: var(--color-green);
  border-color: var(--color-green);
  color: #fffaf2;
}

/* ===== 订单列表 ===== */
.order-list {
  display: grid;
  gap: 18px;
}

/* 订单卡片 */
.order-card {
  overflow: hidden;
}

/* 订单头部：编号 + 状态 */
.order-card__header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 16px;
  padding: 20px 24px;
  border-bottom: 1px solid rgba(204, 188, 161, 0.3);
  background: rgba(245, 241, 232, 0.56);
}

.order-card__no {
  margin-top: 8px;
  color: var(--color-ink-muted);
  font-family: Consolas, monospace;
  font-size: 13px;
}

.order-card__status {
  font-size: 14px;
  font-weight: 600;
}

/* 不同状态的颜色 */
.status-pending { color: var(--color-gold); }
.status-paid { color: var(--color-green); }
.status-shipped { color: var(--color-green-soft); }
.status-done { color: var(--color-wood); }
.status-cancel { color: var(--color-red); }

/* 订单主体：封面图 + 摘要 + 金额 */
.order-card__body {
  display: grid;
  grid-template-columns: 120px minmax(0, 1fr) 160px;
  gap: 20px;
  align-items: center;
  padding: 24px;
  cursor: pointer;
}

.order-card__image {
  width: 100px;
  height: 100px;
  overflow: hidden;
  background: #ece0cd;
}

.order-card__image img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.order-card__copy h3 {
  margin: 10px 0 0;
  color: var(--color-ink);
  font-family: var(--font-display);
  font-size: 30px;
  font-weight: 600;
  letter-spacing: 0.05em;
}

.order-card__meta {
  display: grid;
  gap: 8px;
  margin-top: 16px;
  color: var(--color-ink-soft);
  font-size: 13px;
}

.order-card__amount {
  color: var(--color-red);
  font-size: 34px;
  font-weight: 700;
  text-align: right;
}

/* 订单操作按钮区 */
.order-card__actions {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
  padding: 0 24px 24px;
}

/* ===== 空状态与引导链接 ===== */
.empty-state {
  padding: 54px 32px;
  text-align: center;
}

.inner-hero__link {
  display: inline-block;
  margin-top: 18px;
  color: var(--color-red);
  font-size: 13px;
  letter-spacing: 0.16em;
}
</style>
