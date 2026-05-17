<!--
  我的报名页（MySignups.vue）
  云茗茶馆的用户活动报名记录页面，展示所有已报名的茶事活动。
  页面功能：
  - 顶部概览区：当前报名记录数
  - 报名记录列表：每项展示活动封面图、标题、开始时间、报名状态
  - 支持取消报名（待确认状态）
  - 空报名提示
-->
<template>
  <div class="page">
    <!-- 页面顶部导航栏 -->
    <NavBar />

    <div class="container signups-page" v-loading="loading">
      <!-- ========== 顶部：概览区 ========== -->
      <section class="inner-hero paper-panel">
        <div class="inner-hero__copy">
          <h1>把每一次赴茶会的约定轻轻记下，时间、主题与报名状态都看得清楚</h1>
          <p>这里收着你报过名的茶会、课程与雅集。想回看下一场要赴的约，或临时调整尚未开始的安排，都更方便一些。</p>
        </div>

        <div class="inner-hero__aside">
          <strong>报名概览</strong>
          <div class="hero-stat">
            <span>当前记录</span>
            <b>{{ signups.length }}</b>
          </div>
          <p>如果还想继续参与新的茶会、课程或雅集，可以回到活动页浏览近期开放报名的内容。</p>
          <router-link to="/activities" class="inner-hero__link">继续浏览活动</router-link>
        </div>
      </section>

      <!-- ========== 报名记录区 ========== -->
      <section class="desktop-section">
        <div class="section-title section-title-left">
          <h2>报名记录</h2>
          <p>每条记录都带着活动封面、开始时间与报名状态，方便你按自己的步调回看，也不会错过已经约好的茶事。</p>
        </div>

        <!-- 有报名时：3列布局（图片 + 信息 + 状态操作） -->
        <div v-if="signups.length > 0" class="signup-list">
          <!-- 单个报名条目卡片 -->
          <article v-for="item in signups" :key="item.id" class="signup-card paper-panel">
            <!-- 活动封面图 -->
            <div class="signup-card__image">
              <img :src="resolveActivityImage(item, item.activityCover)" :alt="item.activityTitle">
            </div>

            <!-- 活动信息 -->
            <div class="signup-card__body">
              <h3>{{ item.activityTitle }}</h3>
              <div class="signup-card__meta">
                <span>开始时间 {{ formatDate(item.activityStartTime) }}</span>
                <span>报名状态 {{ item.statusText }}</span>
              </div>
            </div>

            <!-- 右侧状态与操作列 -->
            <div class="signup-card__side">
              <div class="signup-card__status">{{ item.statusText }}</div>
              <!-- 待确认状态：显示取消报名按钮 -->
              <el-button v-if="item.status === 0" type="warning" @click="doCancel(item)">取消报名</el-button>
            </div>
          </article>
        </div>

        <!-- 空报名提示 -->
        <div v-else-if="!loading" class="empty-state paper-panel">
          <h3>暂无报名记录</h3>
          <p>等你选好想参加的茶事后，这里就会慢慢留下你的赴约记录，方便之后随时回看。</p>
          <router-link to="/activities" class="inner-hero__link">去看看活动</router-link>
        </div>
      </section>
    </div>

    <!-- 页面底部页脚栏 -->
    <FooterBar />
  </div>
</template>

<script setup>
/**
 * 我的报名页脚本逻辑
 * 负责：加载报名列表、取消报名
 */
import { onMounted, ref } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import NavBar from '@/components/NavBar.vue'
import FooterBar from '@/components/FooterBar.vue'
import { activitySignupApi } from '@/api/booking'
import { resolveActivityImage } from '@/utils/localImage'

// ===== 响应式变量 =====
const signups = ref([])       // 报名列表
const loading = ref(false)    // 页面加载状态

/**
 * 格式化时间戳为中文日期时间字符串
 * @param {string|number} value - 时间戳
 * @returns {string} 格式化后的日期时间，空值返回空字符串
 */
const formatDate = (value) => (value ? new Date(value).toLocaleString('zh-CN') : '')

/**
 * 加载报名列表数据
 */
const loadData = async () => {
  loading.value = true
  try {
    signups.value = await activitySignupApi.getList()
  } finally {
    loading.value = false
  }
}

/**
 * 取消报名
 * 弹出确认框后调用 API，成功后刷新列表
 * @param {object} item - 报名条目对象
 */
const doCancel = async (item) => {
  try {
    await ElMessageBox.confirm('确定取消报名？', '提示', { type: 'warning' })
    await activitySignupApi.cancel(item.id)
    ElMessage.success('已取消')
    loadData()
  } catch (e) {
    // 用户取消操作
  }
}

/**
 * 组件挂载后：加载报名列表
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

.signups-page {
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
.signup-card__eyebrow,
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

/* ===== 报名列表 ===== */
.signup-list {
  display: grid;
  gap: 18px;
}

/* 报名卡片：3列（图片 + 信息 + 状态操作） */
.signup-card {
  display: grid;
  grid-template-columns: 220px minmax(0, 1fr) 180px;
  overflow: hidden;
}

/* 活动封面图 */
.signup-card__image {
  min-height: 220px;
  background: #ece0cd;
}

.signup-card__image img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

/* 信息区域 */
.signup-card__body {
  display: flex;
  flex-direction: column;
  justify-content: center;
  gap: 16px;
  padding: 24px;
}

.signup-card__body h3 {
  margin: 10px 0 0;
  color: var(--color-ink);
  font-family: var(--font-display);
  font-size: 30px;
  font-weight: 600;
  letter-spacing: 0.05em;
}

/* 元信息：开始时间、报名状态 */
.signup-card__meta {
  display: grid;
  gap: 8px;
  color: var(--color-ink-soft);
  font-size: 13px;
}

/* 右侧状态与操作列 */
.signup-card__side {
  display: flex;
  flex-direction: column;
  justify-content: center;
  gap: 18px;
  padding: 24px;
  background: rgba(255, 252, 246, 0.74);
  border-left: 1px solid rgba(204, 188, 161, 0.3);
}

.signup-card__status {
  color: var(--color-green);
  font-size: 22px;
  font-weight: 700;
}

/* ===== 空状态提示 ===== */
.empty-state {
  padding: 54px 32px;
  text-align: center;
}
</style>
