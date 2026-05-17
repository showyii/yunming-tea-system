<!--
  我的预约页（MyBookings.vue）
  云茗茶馆的用户包间预约记录页面，展示所有雅间预约。
  页面功能：
  - 顶部概览区：当前预约记录数
  - 预约记录列表：每项展示封面图、雅间名称、预约时段、人数、时长、价格、状态
  - 支持取消预约（待确认/已确认状态）
  - 空预约提示
-->
<template>
  <div class="page">
    <!-- 页面顶部导航栏 -->
    <NavBar />

    <div class="container bookings-page" v-loading="loading">
      <!-- ========== 顶部：概览区 ========== -->
      <section class="inner-hero paper-panel">
        <div class="inner-hero__copy">
          <h1>把每一次来馆相聚都安稳记下，时间、人数与雅间安排一目了然</h1>
          <p>这里会留住你的包间预约记录。下次回看时，哪一天来、几人同坐、订的是哪一间雅室，都能很快对上。</p>
        </div>

        <div class="inner-hero__aside">
          <strong>预约概览</strong>
          <div class="hero-stat">
            <span>当前记录</span>
            <b>{{ bookings.length }}</b>
          </div>
          <p>如果你想继续预订新的雅间，可以回到包间预约页，根据人数和环境再次选择适合的空间。</p>
          <router-link to="/rooms" class="inner-hero__link">继续预约包间</router-link>
        </div>
      </section>

      <!-- ========== 预约记录区 ========== -->
      <section class="desktop-section">
        <div class="section-title section-title-left">
          <h2>预约记录</h2>
          <p>每笔预约都写清了时段、人数、价格与备注，方便你随时回看，也方便在行程有变时及时调整安排。</p>
        </div>

        <!-- 有预约时：3列布局（图片 + 信息 + 状态操作） -->
        <div v-if="bookings.length > 0" class="booking-list">
          <!-- 单个预约条目卡片 -->
          <article v-for="item in bookings" :key="item.id" class="booking-card paper-panel">
            <!-- 雅间封面图 -->
            <div class="booking-card__image">
              <img :src="resolveRoomImage(item, item.roomImage)" :alt="item.roomName">
            </div>

            <!-- 预约信息 -->
            <div class="booking-card__body">
              <div>
                <h3>{{ item.roomName }}</h3>
                <div class="booking-card__schedule">
                  {{ item.bookingDate }} {{ item.startTime }} - {{ item.endTime }}
                </div>
              </div>

              <!-- 预约元信息：人数、时长、总价、状态 -->
              <div class="booking-card__meta">
                <span>{{ item.guestCount }} 人</span>
                <span>{{ item.duration }} 小时</span>
                <span>总价 ¥{{ item.totalPrice }}</span>
                <span>{{ item.statusText }}</span>
              </div>

              <p v-if="item.remark" class="booking-card__remark">备注：{{ item.remark }}</p>
            </div>

            <!-- 右侧状态与操作列 -->
            <div class="booking-card__side">
              <div class="booking-card__status">{{ item.statusText }}</div>
              <!-- 待确认或已确认状态：显示取消预约按钮 -->
              <el-button v-if="item.status === 0 || item.status === 1" type="warning" @click="doCancel(item)">取消预约</el-button>
            </div>
          </article>
        </div>

        <!-- 空预约提示 -->
        <div v-else-if="!loading" class="empty-state paper-panel">
          <h3>暂无预约记录</h3>
          <p>等你选好一间合适的雅室后，这里就会慢慢记下每一次来馆的安排与相聚时间。</p>
          <router-link to="/rooms" class="inner-hero__link">去预约包间</router-link>
        </div>
      </section>
    </div>

    <!-- 页面底部页脚栏 -->
    <FooterBar />
  </div>
</template>

<script setup>
/**
 * 我的预约页脚本逻辑
 * 负责：加载预约列表、取消预约
 */
import { onMounted, ref } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import NavBar from '@/components/NavBar.vue'
import FooterBar from '@/components/FooterBar.vue'
import { roomBookingApi } from '@/api/booking'
import { resolveRoomImage } from '@/utils/localImage'

// ===== 响应式变量 =====
const bookings = ref([])      // 预约列表
const loading = ref(false)    // 页面加载状态

/**
 * 加载预约列表数据
 */
const loadData = async () => {
  loading.value = true
  try {
    bookings.value = await roomBookingApi.getList()
  } finally {
    loading.value = false
  }
}

/**
 * 取消预约
 * 弹出确认框后调用 API，成功后刷新列表
 * @param {object} item - 预约条目对象
 */
const doCancel = async (item) => {
  try {
    await ElMessageBox.confirm('确定取消预约？', '提示', { type: 'warning' })
    await roomBookingApi.cancel(item.id)
    ElMessage.success('已取消')
    loadData()
  } catch (e) {
    // 用户取消操作
  }
}

/**
 * 组件挂载后：加载预约列表
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

.bookings-page {
  flex: 1;
  padding-top: 28px;
}

/* ===== 顶部概览面板 ===== */
.inner-hero {
  display: grid;
  grid-template-columns: minmax(0, 1.35fr) minmax(300px, 0.65fr);
  gap: 28px;
  padding: 34px 36px;
  align-items: stretch;
}

.inner-hero__eyebrow,
.booking-card__eyebrow,
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
.empty-state p,
.booking-card__remark {
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

/* 概览统计行 */
.hero-stat {
  display: flex;
  align-items: baseline;
  justify-content: space-between;
  gap: 12px;
  padding: 14px 0;
  margin-bottom: 16px;
  border-top: 1px solid rgba(204, 188, 161, 0.32);
  border-bottom: 1px solid rgba(204, 188, 161, 0.32);
  color: var(--color-ink-soft);
}

.hero-stat b {
  color: var(--color-red);
  font-size: 30px;
  font-weight: 700;
}

.inner-hero__link {
  display: inline-block;
  margin-top: 18px;
  color: var(--color-red);
  font-size: 13px;
  letter-spacing: 0.16em;
}

/* ===== 预约列表 ===== */
.booking-list {
  display: grid;
  gap: 18px;
}

/* 预约卡片：3列（图片 + 信息 + 状态操作） */
.booking-card {
  display: grid;
  grid-template-columns: 220px minmax(0, 1fr) 180px;
  gap: 0;
  overflow: hidden;
}

/* 封面图 */
.booking-card__image {
  min-height: 220px;
  background: #ece0cd;
}

.booking-card__image img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

/* 信息区域 */
.booking-card__body {
  display: flex;
  flex-direction: column;
  justify-content: space-between;
  gap: 16px;
  padding: 24px;
}

.booking-card__body h3 {
  margin: 10px 0 0;
  color: var(--color-ink);
  font-family: var(--font-display);
  font-size: 30px;
  font-weight: 600;
  letter-spacing: 0.05em;
}

/* 预约时段 */
.booking-card__schedule {
  margin-top: 14px;
  color: var(--color-wood);
  font-size: 15px;
}

/* 元信息：人数、时长、总价、状态 */
.booking-card__meta {
  display: flex;
  flex-wrap: wrap;
  gap: 10px 18px;
  color: var(--color-ink-soft);
  font-size: 13px;
}

/* 右侧状态与操作列 */
.booking-card__side {
  display: flex;
  flex-direction: column;
  justify-content: center;
  gap: 18px;
  padding: 24px;
  background: rgba(255, 252, 246, 0.74);
  border-left: 1px solid rgba(204, 188, 161, 0.3);
}

.booking-card__status {
  color: var(--color-red);
  font-size: 22px;
  font-weight: 700;
}

/* ===== 空状态提示 ===== */
.empty-state {
  padding: 54px 32px;
  text-align: center;
}
</style>
