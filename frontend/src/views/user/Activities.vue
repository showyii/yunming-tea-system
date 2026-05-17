<!--
  茶事活动列表页（Activities.vue）
  云茗茶馆的茶事活动列表页面，展示近期茶会、课程与雅集。
  页面功能：
  - 顶部品牌区介绍茶事活动
  - 参与提示与雅间入口
  - 活动列表：每项展示封面图、标题、地点、日期、人数、费用、报名状态
  - 空活动提示
-->
<template>
  <div class="page">
    <!-- 页面顶部导航栏 -->
    <NavBar />

    <div class="container activity-page">
      <!-- ========== 顶部：品牌介绍区 ========== -->
      <section class="inner-hero paper-panel">
        <div class="inner-hero__copy">
          <h1>从时令茶会到静心雅集，慢慢挑一场适合此刻的相聚</h1>
          <p>这里收着云茗近期的茶会、课程与雅集。你可以先看看主题、时间和地点，再选一场愿意停下来坐一坐的茶事。</p>
        </div>

        <div class="inner-hero__aside">
          <strong>参与提示</strong>
          <p>建议先留意活动时间、地点与剩余名额；若想把这次相聚安排得更从容，也可以顺手看看馆内雅间与到店环境。</p>
          <router-link to="/rooms" class="inner-hero__link">查看馆内雅间</router-link>
        </div>
      </section>

      <!-- ========== 活动列表区 ========== -->
      <section class="desktop-section">
        <div class="section-title section-title-left">
          <h2>近期茶事</h2>
          <p>每一场茶事都写清了时间、地点、人数与费用，方便你按自己的节奏慢慢挑选，也更容易找到合适的相聚理由。</p>
        </div>

        <div class="activity-list" v-loading="loading">
          <!-- 单个活动卡片：点击跳转到活动详情 -->
          <article
            v-for="a in activities"
            :key="a.id"
            class="activity-card paper-panel"
            @click="$router.push(`/activities/${a.id}`)"
          >
            <!-- 活动封面图 + 状态角标 -->
            <div class="activity-card__image">
              <img :src="resolveActivityImage(a, a.coverImage)" :alt="a.title">
              <span class="activity-card__status">{{ a.statusText }}</span>
            </div>

            <div class="activity-card__body">
              <!-- 活动标题 -->
              <div class="activity-card__header">
                <h3>{{ a.title }}</h3>
              </div>

              <!-- 活动元信息 -->
              <div class="activity-card__meta">
                <span>地点：{{ a.location }}</span>
                <span>日期：{{ formatDate(a.startTime) }}</span>
                <span>人数：{{ a.currentParticipants }}/{{ a.maxParticipants }}</span>
              </div>

              <!-- 底部：费用 + 报名状态标签 -->
              <div class="activity-card__bottom">
                <span class="activity-card__fee" v-if="a.fee > 0">¥{{ a.fee }}</span>
                <span class="activity-card__fee free" v-else>免费</span>
                <el-tag v-if="a.isSignedUp" type="success" size="small">已报名</el-tag>
              </div>
            </div>
          </article>

          <!-- 空状态提示 -->
          <div v-if="!loading && activities.length === 0" class="empty paper-panel">暂无活动</div>
        </div>
      </section>
    </div>

    <!-- 页面底部页脚栏 -->
    <FooterBar />
  </div>
</template>

<script setup>
/**
 * 茶事活动列表页脚本逻辑
 * 负责：加载活动列表数据
 */
import { onMounted, ref } from 'vue'
import NavBar from '@/components/NavBar.vue'
import FooterBar from '@/components/FooterBar.vue'
import { activityApi } from '@/api/booking'
import { resolveActivityImage } from '@/utils/localImage'

// ===== 响应式变量 =====
const activities = ref([])    // 活动列表
const loading = ref(false)    // 页面加载状态

/**
 * 格式化时间戳为中文日期字符串
 * @param {string|number} t - 时间戳
 * @returns {string} 格式化后的日期，空值返回空字符串
 */
const formatDate = (t) => (t ? new Date(t).toLocaleDateString('zh-CN') : '')

/**
 * 组件挂载后：加载活动列表
 * 成功后填充 activities，无论成功与否都关闭 loading
 */
onMounted(async () => {
  loading.value = true
  activityApi.getList().then((r) => {
    activities.value = r
  }).finally(() => {
    loading.value = false
  })
})
</script>

<style scoped>
/* ===== 页面整体布局 ===== */
.page {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
}

.activity-page {
  flex: 1;
  padding-top: 28px;
}

/* ===== 顶部品牌介绍面板 ===== */
.inner-hero {
  display: grid;
  grid-template-columns: minmax(0, 1.42fr) minmax(320px, 0.78fr);
  gap: 28px;
  padding: 34px 36px;
  align-items: center;
}

.inner-hero__eyebrow {
  color: var(--color-red);
  font-size: 12px;
  letter-spacing: 0.32em;
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
.inner-hero__aside p {
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

.inner-hero__aside strong {
  display: block;
  margin-bottom: 10px;
  color: var(--color-wood);
  font-family: var(--font-display);
  font-size: 24px;
  font-weight: 600;
  letter-spacing: 0.06em;
}

.inner-hero__link {
  display: inline-block;
  margin-top: 18px;
  color: var(--color-red);
  font-size: 13px;
  letter-spacing: 0.16em;
}

/* ===== 活动列表（单列网格） ===== */
.activity-list {
  display: grid;
  gap: 22px;
}

/* 活动卡片：图片左 + 信息右，悬停上浮效果 */
.activity-card {
  display: grid;
  grid-template-columns: 300px 1fr;
  overflow: hidden;
  cursor: pointer;
  transition: transform var(--transition-base), box-shadow var(--transition-base), border-color var(--transition-base);
}

.activity-card:hover {
  transform: translateY(-3px);
  box-shadow: var(--shadow-md);
  border-color: rgba(159, 83, 24, 0.18);
}

/* 活动封面图容器 */
.activity-card__image {
  position: relative;
  min-height: 220px;
  background: #ece0cd;
}

.activity-card__image img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

/* 活动状态角标 */
.activity-card__status {
  position: absolute;
  top: 16px;
  left: 16px;
  padding: 6px 10px;
  background: rgba(47, 79, 62, 0.92);
  color: #fffaf2;
  font-size: 11px;
  letter-spacing: 0.12em;
}

.activity-card__body {
  display: flex;
  flex-direction: column;
  justify-content: space-between;
  padding: 26px;
}

.activity-card__eyebrow {
  color: var(--color-red);
  font-size: 11px;
  letter-spacing: 0.18em;
  text-transform: uppercase;
}

.activity-card__header h3 {
  margin: 10px 0 0;
  color: var(--color-ink);
  font-family: var(--font-display);
  font-size: 32px;
  font-weight: 600;
  letter-spacing: 0.05em;
}

/* 活动元信息：地点、日期、人数 */
.activity-card__meta {
  display: flex;
  flex-wrap: wrap;
  gap: 12px 20px;
  margin-top: 18px;
  color: var(--color-ink-soft);
  font-size: 13px;
}

/* 底部：费用 + 报名状态 */
.activity-card__bottom {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-top: 22px;
}

.activity-card__fee {
  color: var(--color-red);
  font-size: 28px;
  font-weight: 700;
}

/* 免费活动使用绿色 */
.activity-card__fee.free {
  color: var(--color-green);
}

/* ===== 空状态提示 ===== */
.empty {
  padding: 48px 24px;
  text-align: center;
  color: var(--color-ink-soft);
}
</style>
