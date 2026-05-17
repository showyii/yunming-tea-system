<!--
  茶品详情页（ProductDetail.vue）
  云茗茶馆的单品茶品详情页面，展示完整的产品信息并支持购买操作。
  页面功能：
  - 面包屑导航
  - 产品信息区：分类标签、名称、价格、库存、销量
  - 图片画廊：主图 + 缩略图切换
  - 购买区：数量选择、加入购物车、去购物车
  - 收藏功能（登录后可用）
  - 详情标签页：商品描述 / 茶友评价（含评价提交和分页）
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
        <router-link to="/products">全部茶品</router-link>
        <span>/</span>
        <span>{{ product.name }}</span>
      </div>

      <!-- ========== 产品详情主区域（左右双列） ========== -->
      <section class="detail-hero paper-panel" v-if="product.id">
        <!-- 左侧：产品信息 -->
        <div class="detail-hero__copy">
          <!-- 分类标签 -->
          <span v-if="product.categoryName" class="detail-hero__eyebrow">{{ product.categoryName }}</span>
          <h1>{{ product.name }}</h1>
          <p>{{ product.subtitle || '馆内甄选茶品，适合日常品饮、待客茶席与礼赠场景。' }}</p>

          <!-- 信息标签：热销 / 新品 / 分类 -->
          <div class="detail-hero__tags">
            <span v-if="product.isHot" class="info-tag hot">热销</span>
            <span v-if="product.isNew" class="info-tag new">新品</span>
            <span class="info-tag cat">{{ product.categoryName || '茶品详情' }}</span>
          </div>

          <!-- 价格区：当前价格 + 原价（划线） -->
          <div class="detail-hero__price">
            <span class="price-current">¥{{ product.price }}</span>
            <span v-if="product.originalPrice" class="price-original">¥{{ product.originalPrice }}</span>
          </div>

          <!-- 库存与销量信息 -->
          <div class="detail-hero__meta">
            <div><strong>库存</strong><span>{{ product.stock }}</span></div>
            <div><strong>销量</strong><span>{{ product.sales }}</span></div>
          </div>

          <!-- 购买操作区 -->
          <div class="detail-purchase paper-subpanel">
            <!-- 数量选择器 -->
            <div class="detail-purchase__row">
              <span>选择数量</span>
              <el-input-number
                v-model="purchaseQuantity"
                :min="1"
                :max="maxPurchaseQuantity"
                size="large"
                :disabled="product.stock < 1"
              />
            </div>
            <!-- 库存提示 -->
            <p class="detail-purchase__tip">
              {{ product.stock > 0 ? `当前库存 ${product.stock} 件，可先加入购物车再统一下单。` : '当前商品库存不足，暂时无法加入购物车。' }}
            </p>
            <!-- 操作按钮：加入购物车 / 去购物车 -->
            <div class="detail-purchase__actions">
              <el-button
                type="primary"
                size="large"
                :loading="addingToCart"
                :disabled="product.stock < 1"
                @click="addToCart"
              >
                加入购物车
              </el-button>
              <el-button size="large" :disabled="!hasAddedToCart" @click="goToCart">去购物车</el-button>
            </div>
          </div>

          <!-- 收藏按钮 -->
          <div class="detail-hero__actions">
            <el-button type="danger" size="large" @click="toggleFavorite">
              <el-icon><Star /></el-icon>
              {{ product.isFavorited ? '已收藏' : '收藏茶品' }}
            </el-button>
          </div>
        </div>

        <!-- 右侧：图片画廊（主图 + 缩略图列表） -->
        <div class="detail-hero__gallery">
          <div class="main-image">
            <img :src="currentImage || resolveProductImage(product, product.mainImage)" :alt="product.name">
          </div>
          <!-- 缩略图列表：多于1张时显示 -->
          <div class="thumb-list" v-if="detailImages.length > 1">
            <button
              v-for="(img, idx) in detailImages"
              :key="idx"
              class="thumb"
              :class="{ active: currentImage === img }"
              @click="currentImage = img"
            >
              <img :src="img" alt="">
            </button>
          </div>
        </div>
      </section>

      <!-- ========== 详情标签页区 ========== -->
      <section class="detail-tabs paper-panel" v-if="product.id">
        <!-- 标签页头部切换按钮 -->
        <div class="tab-headers">
          <button :class="{ active: tab === 'desc' }" @click="tab = 'desc'">商品详情</button>
          <button :class="{ active: tab === 'comments' }" @click="tab = 'comments'">茶友评价（{{ commentTotal }}）</button>
        </div>

        <!-- 商品描述标签页 -->
        <div class="tab-body" v-show="tab === 'desc'">
          <div class="desc-layout">
            <!-- 描述内容（富文本渲染） -->
            <div class="desc-content" v-html="product.description || '暂无描述'"></div>
            <!-- 品饮提示侧栏 -->
            <aside class="desc-aside paper-subpanel">
              <strong>品饮提示</strong>
              <p>若你已经大致有了偏好，不妨结合价格、销量与风味再细看一遍；若还想多比较几款，也可以回到茶单继续慢慢挑。</p>
              <router-link to="/products">返回全部茶品</router-link>
            </aside>
          </div>
        </div>

        <!-- 茶友评价标签页 -->
        <div class="tab-body" v-show="tab === 'comments'">
          <!-- 评价提交表单：仅登录用户可见 -->
          <div class="comment-form paper-subpanel" v-if="isLoggedIn">
            <el-input v-model="newComment" type="textarea" :rows="3" placeholder="写下你对这款茶的品鉴感受..." />
            <div class="comment-form-bottom">
              <el-rate v-model="newRating" :max="5" show-score />
              <el-button type="primary" @click="submitComment" :loading="submitting">发表评价</el-button>
            </div>
          </div>
          <!-- 未登录提示 -->
          <div v-else class="comment-login-tip paper-subpanel">
            <router-link to="/login">登录</router-link>后即可发表评价
          </div>

          <!-- 评价列表 -->
          <div class="comment-list" v-loading="commentLoading">
            <article v-for="c in comments" :key="c.id" class="comment-item paper-subpanel">
              <!-- 用户头像（取用户名首字） -->
              <div class="comment-avatar">{{ c.username?.charAt(0) || '茶' }}</div>
              <div class="comment-body">
                <div class="comment-header">
                  <span class="comment-user">{{ c.username }}</span>
                  <!-- 评分（只读模式） -->
                  <el-rate :model-value="c.rating" disabled size="small" />
                  <span class="comment-time">{{ formatTime(c.createTime) }}</span>
                </div>
                <p class="comment-text">{{ c.content }}</p>
              </div>
            </article>
            <!-- 评价加载失败提示 -->
            <div v-if="!commentLoading && commentLoadFailed" class="empty paper-subpanel">
              评价内容暂时加载失败，你可以稍后再看，商品详情与购买流程不受影响。
            </div>
            <!-- 暂无评价提示 -->
            <div v-else-if="!commentLoading && comments.length === 0" class="empty paper-subpanel">暂无评价，欢迎首评</div>
          </div>
          <!-- 评价分页 -->
          <div class="pagination" v-if="commentTotal > commentSize">
            <el-pagination
              background
              layout="prev, pager, next"
              :total="commentTotal"
              :page-size="commentSize"
              v-model:current-page="commentPage"
              @current-change="loadComments"
            />
          </div>
        </div>
      </section>
    </div>

    <!-- 页面底部页脚栏 -->
    <FooterBar />
  </div>
</template>

<script setup>
/**
 * 茶品详情页脚本逻辑
 * 负责：加载产品详情、图片画廊切换、加入购物车、收藏切换、评价列表与提交
 */
import { computed, onMounted, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import NavBar from '@/components/NavBar.vue'
import FooterBar from '@/components/FooterBar.vue'
import { cartApi } from '@/api/cart'
import { commentApi, favoriteApi, productApi } from '@/api/product'
import { resolveProductGallery, resolveProductImage } from '@/utils/localImage'

// ===== 路由实例 =====
const route = useRoute()
const router = useRouter()

// ===== 产品相关响应式变量 =====
const product = ref({})           // 产品详情对象
const loading = ref(false)        // 页面加载状态
const currentImage = ref('')      // 当前显示的主图 URL
const detailImages = ref([])      // 产品图片画廊列表
const purchaseQuantity = ref(1)   // 购买数量（默认为1）
const addingToCart = ref(false)   // 加入购物车按钮加载状态
const hasAddedToCart = ref(false) // 是否已加入过购物车（控制"去购物车"按钮启用）

// ===== 最大可购买数量（根据库存动态计算，最少为1） =====
const maxPurchaseQuantity = computed(() => Math.max(1, Number(product.value.stock) || 1))

// ===== 评价相关响应式变量 =====
const tab = ref('desc')           // 当前激活的标签页：desc（描述）或 comments（评价）
const newComment = ref('')        // 新评价内容
const newRating = ref(5)          // 新评价评分（默认5星）
const submitting = ref(false)     // 评价提交加载状态
const comments = ref([])          // 评价列表
const commentLoading = ref(false) // 评价列表加载状态
const commentPage = ref(1)        // 评价当前页码
const commentSize = ref(10)       // 评价每页条数
const commentTotal = ref(0)       // 评价总数
const commentLoadFailed = ref(false) // 评价加载是否失败

// ===== 用户登录状态检查 =====
const isLoggedIn = !!localStorage.getItem('token')

/**
 * 加载产品详情
 * 获取产品全部信息、图片画廊，重置购买状态
 */
const loadDetail = async () => {
  loading.value = true
  try {
    product.value = await productApi.getDetail(route.params.id)
    // 解析产品图片列表（包括主图和详情图）
    detailImages.value = resolveProductGallery(product.value)
    // 设置主图为解析后的首张图片
    currentImage.value = resolveProductImage(product.value, product.value.mainImage)
    purchaseQuantity.value = product.value.stock > 0 ? 1 : 1
    hasAddedToCart.value = false  // 每次重新进入页面时重置
  } finally {
    loading.value = false
  }
}

/**
 * 加载评价列表（带分页）
 * 支持分页参数，失败时标记 loadFailed 状态
 */
const loadComments = async () => {
  commentLoading.value = true
  commentLoadFailed.value = false
  try {
    const res = await commentApi.getList(route.params.id, { page: commentPage.value, size: commentSize.value })
    comments.value = res.records || []
    commentTotal.value = res.total || 0
  } catch (error) {
    comments.value = []
    commentTotal.value = 0
    commentLoadFailed.value = true  // 标记加载失败，显示友好提示
  } finally {
    commentLoading.value = false
  }
}

/**
 * 切换收藏状态
 * 登录检查后，根据当前收藏状态执行添加或取消操作
 */
const toggleFavorite = async () => {
  if (!isLoggedIn) {
    ElMessage.warning('请先登录')
    return
  }
  try {
    if (product.value.isFavorited) {
      await favoriteApi.remove(product.value.id)
      product.value.isFavorited = false
      ElMessage.success('已取消收藏')
    } else {
      await favoriteApi.add(product.value.id)
      product.value.isFavorited = true
      ElMessage.success('收藏成功')
    }
  } catch (e) {
    // 请求拦截器已统一处理错误提示
  }
}

/**
 * 提交评价
 * 校验内容非空后，调用 API 提交并刷新评价列表
 */
const submitComment = async () => {
  if (!newComment.value.trim()) {
    ElMessage.warning('请输入评价内容')
    return
  }
  submitting.value = true
  try {
    await commentApi.add({ productId: Number(route.params.id), content: newComment.value, rating: newRating.value })
    ElMessage.success('评价发表成功')
    // 重置表单
    newComment.value = ''
    newRating.value = 5
    commentPage.value = 1
    // 刷新评价列表
    loadComments()
  } finally {
    submitting.value = false
  }
}

/**
 * 加入购物车
 * 登录检查 + 库存检查后，调用 API 添加商品
 */
const addToCart = async () => {
  if (!isLoggedIn) {
    ElMessage.warning('请先登录后再加入购物车')
    router.push('/login')
    return
  }
  if (product.value.stock < 1) {
    ElMessage.warning('当前商品库存不足')
    return
  }

  addingToCart.value = true
  try {
    await cartApi.add({
      productId: Number(route.params.id),
      quantity: purchaseQuantity.value
    })
    hasAddedToCart.value = true
    ElMessage.success('已加入购物车，可前往购物车统一结算')
  } finally {
    addingToCart.value = false
  }
}

/**
 * 跳转到购物车页面
 */
const goToCart = () => {
  if (!isLoggedIn) {
    router.push('/login')
    return
  }
  router.push('/cart')
}

/**
 * 格式化时间戳为中文日期字符串
 * @param {string|number} t - 时间戳
 * @returns {string} 格式化后的日期字符串
 */
const formatTime = (t) => {
  if (!t) return ''
  return new Date(t).toLocaleDateString('zh-CN')
}

/**
 * 组件挂载后：加载产品详情和评价列表
 */
onMounted(() => {
  loadDetail()
  loadComments()
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

/* ===== 产品详情主区域（左右双列） ===== */
.detail-hero {
  display: grid;
  grid-template-columns: minmax(0, 1.06fr) minmax(420px, 0.94fr);
  gap: 30px;
  padding: 32px;
  margin-bottom: 28px;
}

/* 分类标签 */
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
  font-size: 50px;
  font-weight: 600;
  line-height: 1.16;
  letter-spacing: 0.06em;
}

.detail-hero__copy p {
  margin: 0;
  color: var(--color-ink-soft);
  font-size: 15px;
  line-height: 1.9;
}

/* 信息标签区 */
.detail-hero__tags {
  display: flex;
  gap: 8px;
  margin: 20px 0;
  flex-wrap: wrap;
}

.info-tag {
  padding: 6px 12px;
  color: #fffaf2;
  font-size: 12px;
}

.info-tag.hot { background: rgba(159, 83, 24, 0.94); }
.info-tag.new { background: rgba(47, 79, 62, 0.92); }
.info-tag.cat { background: rgba(92, 64, 51, 0.9); }

/* 价格区 */
.detail-hero__price {
  display: flex;
  align-items: baseline;
  gap: 12px;
  margin-bottom: 20px;
}

.price-current {
  color: var(--color-red);
  font-size: 42px;
  font-weight: 700;
}

.price-original {
  color: var(--color-ink-muted);
  text-decoration: line-through;
}

/* 库存/销量信息 */
.detail-hero__meta {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 14px;
  margin-bottom: 24px;
}

.detail-hero__meta div {
  padding: 16px;
  background: rgba(255, 252, 246, 0.84);
  border: 1px solid rgba(204, 188, 161, 0.42);
}

.detail-hero__meta strong,
.detail-hero__meta span {
  display: block;
}

.detail-hero__meta strong {
  margin-bottom: 6px;
  color: var(--color-wood);
  font-size: 13px;
}

.detail-hero__meta span {
  color: var(--color-ink);
  font-size: 18px;
}

/* 购买操作区 */
.detail-purchase {
  margin-bottom: 24px;
}

.detail-purchase__row {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 16px;
  color: var(--color-ink);
}

.detail-purchase__row span {
  color: var(--color-wood);
  font-size: 14px;
  letter-spacing: 0.08em;
}

.detail-purchase__tip {
  margin: 12px 0 0;
  color: var(--color-ink-soft);
  font-size: 13px;
  line-height: 1.8;
}

.detail-purchase__actions {
  display: flex;
  gap: 12px;
  margin-top: 16px;
}

/* ===== 图片画廊 ===== */
.main-image {
  overflow: hidden;
  background: #ece0cd;
  aspect-ratio: 1 / 1;
}

.main-image img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

/* 缩略图列表 */
.thumb-list {
  display: flex;
  gap: 10px;
  margin-top: 14px;
  flex-wrap: wrap;
}

.thumb {
  width: 72px;
  height: 72px;
  padding: 0;
  border: 1px solid transparent;
  overflow: hidden;
  cursor: pointer;
  background: #ece0cd;
}

/* 选中的缩略图使用红色边框标识 */
.thumb.active {
  border-color: var(--color-red);
}

.thumb img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

/* ===== 详情标签页区 ===== */
.detail-tabs {
  padding: 26px;
}

/* 标签页头部切换按钮 */
.tab-headers {
  display: flex;
  gap: 12px;
  margin-bottom: 20px;
}

.tab-headers button {
  min-height: 42px;
  padding: 0 18px;
  border: 1px solid var(--color-border);
  background: rgba(255, 250, 242, 0.75);
  color: var(--color-ink-soft);
  cursor: pointer;
}

/* 激活的标签页按钮 */
.tab-headers button.active {
  background: var(--color-green);
  border-color: var(--color-green);
  color: #fffaf2;
}

/* 描述内容 + 侧栏布局 */
.desc-layout {
  display: grid;
  grid-template-columns: minmax(0, 1fr) 320px;
  gap: 18px;
}

.desc-content,
.paper-subpanel {
  padding: 18px;
  background: rgba(255, 250, 242, 0.84);
}

.desc-content {
  min-height: 220px;
  line-height: 1.9;
  white-space: pre-wrap;
}

.desc-aside strong {
  display: block;
  margin-bottom: 10px;
  color: var(--color-wood);
  font-family: var(--font-display);
  font-size: 22px;
}

.desc-aside p {
  margin: 0;
  color: var(--color-ink-soft);
  line-height: 1.85;
}

.desc-aside a {
  display: inline-block;
  margin-top: 16px;
  color: var(--color-red);
  font-size: 13px;
  letter-spacing: 0.14em;
}

/* ===== 评价表单 ===== */
.comment-form-bottom {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-top: 12px;
}

/* ===== 评价列表 ===== */
.comment-list {
  margin-top: 16px;
}

.comment-item {
  display: flex;
  gap: 14px;
  margin-bottom: 12px;
}

/* 用户头像圆圈 */
.comment-avatar {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  width: 42px;
  height: 42px;
  background: var(--color-green);
  color: #fffaf2;
  flex-shrink: 0;
}

.comment-body {
  flex: 1;
}

.comment-header {
  display: flex;
  gap: 12px;
  align-items: center;
  margin-bottom: 8px;
}

.comment-user {
  color: var(--color-ink);
  font-weight: 600;
}

.comment-time {
  margin-left: auto;
  color: var(--color-ink-muted);
  font-size: 12px;
}

.comment-text {
  margin: 0;
  color: var(--color-ink-soft);
  line-height: 1.75;
}

/* 登录提示与空状态 */
.comment-login-tip,
.empty {
  text-align: center;
  color: var(--color-ink-soft);
}
</style>
