<!--
  收藏茶单页（Favorites.vue）
  云茗茶馆的用户收藏列表页面，展示所有已收藏的茶品。
  页面功能：
  - 顶部概览区：已收藏茶品数量
  - 收藏茶单列表：每项展示封面图、名称、收藏时间、价格
  - 支持查看详情、取消收藏
  - 空收藏提示
-->
<template>
  <div class="page">
    <!-- 页面顶部导航栏 -->
    <NavBar />

    <div class="container favorites-page" v-loading="loading">
      <!-- ========== 顶部：概览区 ========== -->
      <section class="inner-hero paper-panel">
        <div class="inner-hero__copy">
          <h1>把一眼喜欢上的茶先收在这里，想起时随时回来再看看</h1>
          <p>有些茶值得先留一留，等心里想清楚了再决定带走。这一页就帮你把那些心动的茶款暂时收好。</p>
        </div>

        <!-- 右侧收藏概览 -->
        <div class="inner-hero__aside">
          <strong>收藏概览</strong>
          <div class="hero-stat">
            <span>已收藏茶品</span>
            <b>{{ favorites.length }}</b>
          </div>
          <p>若想继续扩充这份清单，可以回到全部茶品页，从分类、价格和热度里继续慢慢挑选。</p>
          <router-link to="/products" class="inner-hero__link">继续浏览茶品</router-link>
        </div>
      </section>

      <!-- ========== 收藏茶单区 ========== -->
      <section class="desktop-section">
        <div class="section-title section-title-left">
          <h2>收藏茶单</h2>
          <p>每张卡片都带着收藏时间与价格，方便你回看、比较，也方便在想好之后直接回到详情页下单。</p>
        </div>

        <!-- 有收藏时：2列网格展示 -->
        <div v-if="favorites.length > 0" class="favorites-grid">
          <!-- 单个收藏条目卡片 -->
          <article v-for="item in favorites" :key="item.id" class="favorite-card paper-panel">
            <!-- 商品封面图：点击跳转详情 -->
            <button class="favorite-card__image" @click="$router.push(`/products/${item.productId}`)">
              <img :src="resolveProductImage(item, item.productImage)" :alt="item.productName">
            </button>

            <div class="favorite-card__body">
              <!-- 商品名称：点击跳转详情 -->
              <h3 @click="$router.push(`/products/${item.productId}`)">{{ item.productName }}</h3>
              <div class="favorite-card__meta">
                <span>收藏于 {{ formatTime(item.createTime) }}</span>
                <span>可直接进入详情继续比较与下单</span>
              </div>
              <div class="favorite-card__footer">
                <span class="favorite-card__price">¥{{ item.price }}</span>
                <div class="favorite-card__actions">
                  <el-button plain @click="$router.push(`/products/${item.productId}`)">查看详情</el-button>
                  <el-button type="danger" @click.stop="remove(item)">取消收藏</el-button>
                </div>
              </div>
            </div>
          </article>
        </div>

        <!-- 空收藏提示 -->
        <div v-else-if="!loading" class="empty-state paper-panel">
          <h3>还没有收藏任何茶品</h3>
          <p>不妨先从六大茶类和全部茶品逛起，把真正想慢慢回看的那几款茶先留在这里。</p>
          <router-link to="/products" class="inner-hero__link">去逛逛茶品</router-link>
        </div>
      </section>
    </div>

    <!-- 页面底部页脚栏 -->
    <FooterBar />
  </div>
</template>

<script setup>
/**
 * 收藏茶单页脚本逻辑
 * 负责：加载收藏列表、取消收藏
 */
import { onMounted, ref } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import NavBar from '@/components/NavBar.vue'
import FooterBar from '@/components/FooterBar.vue'
import { favoriteApi } from '@/api/product'
import { resolveProductImage } from '@/utils/localImage'

// ===== 响应式变量 =====
const favorites = ref([])    // 收藏列表
const loading = ref(false)   // 页面加载状态

/**
 * 加载收藏列表数据
 */
const loadData = async () => {
  loading.value = true
  try {
    favorites.value = await favoriteApi.getList()
  } finally {
    loading.value = false
  }
}

/**
 * 取消收藏
 * 弹出确认框后调用 API，成功后刷新列表
 * @param {object} item - 收藏条目对象
 */
const remove = async (item) => {
  try {
    await ElMessageBox.confirm(`确定取消收藏「${item.productName}」？`, '提示', { type: 'warning' })
    await favoriteApi.remove(item.productId)
    ElMessage.success('已取消收藏')
    loadData()
  } catch (e) {
    // 用户取消操作或请求拦截器已处理错误
  }
}

/**
 * 格式化时间戳为中文日期字符串
 * @param {string|number} value - 时间戳
 * @returns {string} 格式化后的日期，空值返回空字符串
 */
const formatTime = (value) => (value ? new Date(value).toLocaleDateString('zh-CN') : '')

/**
 * 组件挂载后：加载收藏列表
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

.favorites-page {
  flex: 1;
  padding-top: 28px;
}

/* ===== 顶部概览面板 ===== */
.inner-hero {
  display: grid;
  grid-template-columns: minmax(0, 1.38fr) minmax(300px, 0.62fr);
  gap: 28px;
  padding: 34px 36px;
  align-items: stretch;
}

.inner-hero__eyebrow,
.favorite-card__eyebrow,
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

/* 右侧概览面板：垂直居中布局 */
.inner-hero__aside {
  display: flex;
  flex-direction: column;
  justify-content: center;
  padding: 24px;
  background: rgba(255, 252, 246, 0.82);
  border: 1px solid rgba(204, 188, 161, 0.4);
}

.inner-hero__aside strong,
.empty-state h3 {
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
  margin-bottom: 16px;
  padding: 14px 0;
  border-top: 1px solid rgba(204, 188, 161, 0.4);
  border-bottom: 1px solid rgba(204, 188, 161, 0.4);
  color: var(--color-ink-soft);
}

.hero-stat b {
  color: var(--color-red);
  font-size: 32px;
  font-weight: 700;
}

.inner-hero__link {
  display: inline-block;
  margin-top: 18px;
  color: var(--color-red);
  font-size: 13px;
  letter-spacing: 0.16em;
}

/* ===== 收藏列表（2列网格） ===== */
.favorites-grid {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 22px;
}

/* 收藏条目卡片：图片左 + 信息右 */
.favorite-card {
  display: grid;
  grid-template-columns: 240px 1fr;
  overflow: hidden;
}

.favorite-card__image {
  min-height: 260px;
  padding: 0;
  border: none;
  background: #ece0cd;
  cursor: pointer;
}

.favorite-card__image img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

/* 信息区域 */
.favorite-card__body {
  display: flex;
  flex-direction: column;
  justify-content: space-between;
  gap: 16px;
  padding: 24px;
}

.favorite-card__body h3 {
  margin: 10px 0 0;
  color: var(--color-ink);
  font-family: var(--font-display);
  font-size: 30px;
  font-weight: 600;
  letter-spacing: 0.05em;
  cursor: pointer;
}

.favorite-card__meta {
  display: grid;
  gap: 8px;
  color: var(--color-ink-soft);
  font-size: 13px;
}

/* 底部价格与操作区 */
.favorite-card__footer {
  display: flex;
  align-items: flex-end;
  justify-content: space-between;
  gap: 16px;
  padding-top: 18px;
  border-top: 1px solid rgba(204, 188, 161, 0.32);
}

.favorite-card__price {
  color: var(--color-red);
  font-size: 32px;
  font-weight: 700;
}

.favorite-card__actions {
  display: flex;
  gap: 10px;
}

/* ===== 空状态提示 ===== */
.empty-state {
  padding: 54px 32px;
  text-align: center;
}
</style>
