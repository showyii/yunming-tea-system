<template>
  <div class="page">
    <NavBar />

    <div class="container signups-page" v-loading="loading">
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

      <section class="desktop-section">
        <div class="section-title section-title-left">
          <h2>报名记录</h2>
          <p>每条记录都带着活动封面、开始时间与报名状态，方便你按自己的步调回看，也不会错过已经约好的茶事。</p>
        </div>

        <div v-if="signups.length > 0" class="signup-list">
          <article v-for="item in signups" :key="item.id" class="signup-card paper-panel">
            <div class="signup-card__image">
              <img :src="resolveActivityImage(item, item.activityCover)" :alt="item.activityTitle">
            </div>

            <div class="signup-card__body">
              <h3>{{ item.activityTitle }}</h3>
              <div class="signup-card__meta">
                <span>开始时间 {{ formatDate(item.activityStartTime) }}</span>
                <span>报名状态 {{ item.statusText }}</span>
              </div>
            </div>

            <div class="signup-card__side">
              <div class="signup-card__status">{{ item.statusText }}</div>
              <el-button v-if="item.status === 0" type="warning" @click="doCancel(item)">取消报名</el-button>
            </div>
          </article>
        </div>

        <div v-else-if="!loading" class="empty-state paper-panel">
          <h3>暂无报名记录</h3>
          <p>等你选好想参加的茶事后，这里就会慢慢留下你的赴约记录，方便之后随时回看。</p>
          <router-link to="/activities" class="inner-hero__link">去看看活动</router-link>
        </div>
      </section>
    </div>

    <FooterBar />
  </div>
</template>

<script setup>
import { onMounted, ref } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import NavBar from '@/components/NavBar.vue'
import FooterBar from '@/components/FooterBar.vue'
import { activitySignupApi } from '@/api/booking'
import { resolveActivityImage } from '@/utils/localImage'

const signups = ref([])
const loading = ref(false)

const formatDate = (value) => (value ? new Date(value).toLocaleString('zh-CN') : '')

const loadData = async () => {
  loading.value = true
  try {
    signups.value = await activitySignupApi.getList()
  } finally {
    loading.value = false
  }
}

const doCancel = async (item) => {
  try {
    await ElMessageBox.confirm('确定取消报名？', '提示', { type: 'warning' })
    await activitySignupApi.cancel(item.id)
    ElMessage.success('已取消')
    loadData()
  } catch (e) {
    // cancelled
  }
}

onMounted(loadData)
</script>

<style scoped>
.page {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
}

.signups-page {
  flex: 1;
  padding-top: 28px;
}

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

.signup-list {
  display: grid;
  gap: 18px;
}

.signup-card {
  display: grid;
  grid-template-columns: 220px minmax(0, 1fr) 180px;
  overflow: hidden;
}

.signup-card__image {
  min-height: 220px;
  background: #ece0cd;
}

.signup-card__image img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

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

.signup-card__meta {
  display: grid;
  gap: 8px;
  color: var(--color-ink-soft);
  font-size: 13px;
}

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

.empty-state {
  padding: 54px 32px;
  text-align: center;
}
</style>
