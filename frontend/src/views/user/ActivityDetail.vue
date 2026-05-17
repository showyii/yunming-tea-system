<!--
  茶事活动详情页（ActivityDetail.vue）
  云茗茶馆的单场茶事活动详情页面，展示活动信息并支持报名。
  页面功能：
  - 面包屑导航
  - 活动信息区：封面图、标题、摘要、地点、时间、人数、费用
  - 报名/状态显示：可报名时显示"立即报名"按钮，已报名显示标签
  - 活动说明区：富文本详情 + 参与建议侧栏
-->
<template>
  <div class="page">
    <!-- 页面顶部导航栏 -->
    <NavBar />

    <div class="container detail-page" v-loading="loading">
      <!-- ========== 面包屑导航 ========== -->
      <div class="breadcrumb">
        <router-link to="/">首页</router-link>
        <span>/</span>
        <router-link to="/activities">茶事活动</router-link>
        <span>/</span>
        <span>{{ activity.title }}</span>
      </div>

      <!-- ========== 活动详情主区域（双列：封面图 + 信息） ========== -->
      <section class="detail-hero paper-panel" v-if="activity.id">
        <!-- 左侧：活动封面图 -->
        <div class="detail-hero__media">
          <img :src="resolveActivityImage(activity, activity.coverImage)" :alt="activity.title">
        </div>

        <!-- 右侧：活动信息 -->
        <div class="detail-hero__copy">
          <h1>{{ activity.title }}</h1>
          <p class="detail-hero__summary">把时间、地点与参与方式都先看明白，再安心决定是否赴这一场茶席相聚。</p>

          <!-- 活动元信息 -->
          <div class="detail-hero__meta">
            <span>地点：{{ activity.location }}</span>
            <span>时间：{{ formatDate(activity.startTime) }} 至 {{ formatDate(activity.endTime) }}</span>
            <span>人数：{{ activity.currentParticipants }}/{{ activity.maxParticipants }} 人</span>
          </div>

          <!-- 费用 -->
          <div class="detail-hero__fee">
            <span v-if="activity.fee > 0">¥{{ activity.fee }}</span>
            <span v-else class="free">免费参加</span>
          </div>

          <!-- 报名/状态操作区 -->
          <div class="detail-hero__action">
            <!-- 未报名且活动进行中：显示报名按钮 -->
            <el-button
              v-if="!activity.isSignedUp && activity.status === 1"
              type="primary"
              size="large"
              @click="doSignup"
              :loading="signing"
            >
              立即报名
            </el-button>
            <!-- 已报名：显示成功标签 -->
            <el-tag v-else-if="activity.isSignedUp" type="success" size="large">已报名</el-tag>
            <!-- 其他状态：显示活动状态文字 -->
            <span v-else class="tip">{{ activity.statusText }}</span>
          </div>
        </div>
      </section>

      <!-- ========== 活动说明区 ========== -->
      <section class="detail-body paper-panel" v-if="activity.id">
        <div class="detail-body__content">
          <div class="detail-body__heading">
            <h2>活动说明</h2>
          </div>
          <!-- 富文本活动描述 -->
          <div class="detail-desc" v-html="activity.description || '暂无详细描述'"></div>
        </div>

        <!-- 参与建议侧栏 -->
        <aside class="detail-body__aside paper-subpanel">
          <strong>参与建议</strong>
          <p>若你想把这次赴约安排得更完整，可以先确认报名，再看看是否需要另约一间雅室，与同行好友多留一段品茗时光。</p>
          <router-link to="/rooms">查看馆内雅间</router-link>
        </aside>
      </section>
    </div>

    <!-- 页面底部页脚栏 -->
    <FooterBar />
  </div>
</template>

<script setup>
/**
 * 茶事活动详情页脚本逻辑
 * 负责：加载活动详情、执行报名操作
 */
import { onMounted, ref } from 'vue'
import { useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'
import NavBar from '@/components/NavBar.vue'
import FooterBar from '@/components/FooterBar.vue'
import { activityApi, activitySignupApi } from '@/api/booking'
import { resolveActivityImage } from '@/utils/localImage'

// ===== 路由与响应式变量 =====
const route = useRoute()
const activity = ref({})      // 活动详情对象
const loading = ref(false)    // 页面加载状态
const signing = ref(false)    // 报名按钮加载状态

/**
 * 格式化时间戳为中文日期时间字符串
 * @param {string|number} t - 时间戳
 * @returns {string} 格式化后的日期时间，空值返回空字符串
 */
const formatDate = (t) => (t ? new Date(t).toLocaleString('zh-CN') : '')

/**
 * 执行活动报名
 * 调用报名 API，成功后更新本地状态（标记已报名 + 参与人数+1）
 */
const doSignup = async () => {
  signing.value = true
  try {
    await activitySignupApi.signup(activity.value.id)
    ElMessage.success('报名成功')
    activity.value.isSignedUp = true
    activity.value.currentParticipants += 1
  } finally {
    signing.value = false
  }
}

/**
 * 组件挂载后：加载活动详情
 * 根据路由参数中的活动ID获取完整活动数据
 */
onMounted(async () => {
  loading.value = true
  try {
    activity.value = await activityApi.getDetail(route.params.id)
  } finally {
    loading.value = false
  }
})
</script>

<style scoped>
/* ===== 页面整体布局 ===== */
.page {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
}

.detail-page {
  flex: 1;
  padding-top: 28px;
}

/* ===== 面包屑导航 ===== */
.breadcrumb {
  display: flex;
  gap: 8px;
  margin-bottom: 20px;
  color: var(--color-ink-muted);
  font-size: 13px;
}

/* ===== 活动详情主区域（双列：封面图 + 信息） ===== */
.detail-hero {
  display: grid;
  grid-template-columns: minmax(420px, 0.94fr) minmax(0, 1.06fr);
  gap: 28px;
  padding: 30px;
  margin-bottom: 28px;
}

/* 封面图容器 */
.detail-hero__media {
  min-height: 420px;
  background: #ece0cd;
  overflow: hidden;
}

.detail-hero__media img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.detail-hero__eyebrow {
  color: var(--color-red);
  font-size: 12px;
  letter-spacing: 0.28em;
  text-transform: uppercase;
}

.detail-hero__copy h1 {
  margin: 14px 0 12px;
  color: var(--color-ink);
  font-family: var(--font-display);
  font-size: 48px;
  font-weight: 600;
  line-height: 1.18;
  letter-spacing: 0.06em;
}

/* 活动摘要 */
.detail-hero__summary {
  margin: 0;
  color: var(--color-ink-soft);
  font-size: 15px;
  line-height: 1.9;
}

/* 活动元信息：地点、时间、人数 */
.detail-hero__meta {
  display: flex;
  flex-direction: column;
  gap: 12px;
  margin: 20px 0;
  color: var(--color-ink-soft);
  font-size: 14px;
}

/* 费用 */
.detail-hero__fee {
  color: var(--color-red);
  font-size: 34px;
  font-weight: 700;
}

.detail-hero__fee .free {
  color: var(--color-green);
}

/* 报名/状态操作区 */
.detail-hero__action {
  margin-top: 24px;
}

.tip {
  color: var(--color-ink-muted);
  font-size: 16px;
}

/* ===== 活动说明区（双列：描述 + 侧栏） ===== */
.detail-body {
  display: grid;
  grid-template-columns: minmax(0, 1fr) 320px;
  gap: 20px;
  padding: 26px;
}

.detail-body__heading {
  margin-bottom: 16px;
}

.detail-body__heading h2 {
  margin: 10px 0 0;
  color: var(--color-ink);
  font-family: var(--font-display);
  font-size: 34px;
  font-weight: 600;
  letter-spacing: 0.06em;
}

/* 活动描述（富文本渲染） */
.detail-desc,
.paper-subpanel {
  padding: 18px;
  background: rgba(255, 250, 242, 0.84);
}

.detail-desc {
  min-height: 200px;
  color: var(--color-ink-soft);
  line-height: 1.9;
  white-space: pre-wrap;
}

/* 参与建议侧栏 */
.detail-body__aside strong {
  display: block;
  margin-bottom: 10px;
  color: var(--color-wood);
  font-family: var(--font-display);
  font-size: 22px;
}

.detail-body__aside p {
  margin: 0;
  color: var(--color-ink-soft);
  line-height: 1.85;
}

.detail-body__aside a {
  display: inline-block;
  margin-top: 16px;
  color: var(--color-red);
  font-size: 13px;
  letter-spacing: 0.14em;
}
</style>
