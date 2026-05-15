<template>
  <div class="page">
    <NavBar />

    <div class="container detail-page" v-loading="loading">
      <div class="breadcrumb">
        <router-link to="/">首页</router-link>
        <span>/</span>
        <router-link to="/products">全部茶品</router-link>
        <span>/</span>
        <span>{{ product.name }}</span>
      </div>

      <section class="detail-hero paper-panel" v-if="product.id">
        <div class="detail-hero__copy">
          <span v-if="product.categoryName" class="detail-hero__eyebrow">{{ product.categoryName }}</span>
          <h1>{{ product.name }}</h1>
          <p>{{ product.subtitle || '馆内甄选茶品，适合日常品饮、待客茶席与礼赠场景。' }}</p>

          <div class="detail-hero__tags">
            <span v-if="product.isHot" class="info-tag hot">热销</span>
            <span v-if="product.isNew" class="info-tag new">新品</span>
            <span class="info-tag cat">{{ product.categoryName || '茶品详情' }}</span>
          </div>

          <div class="detail-hero__price">
            <span class="price-current">¥{{ product.price }}</span>
            <span v-if="product.originalPrice" class="price-original">¥{{ product.originalPrice }}</span>
          </div>

          <div class="detail-hero__meta">
            <div><strong>库存</strong><span>{{ product.stock }}</span></div>
            <div><strong>销量</strong><span>{{ product.sales }}</span></div>
          </div>

          <div class="detail-purchase paper-subpanel">
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
            <p class="detail-purchase__tip">
              {{ product.stock > 0 ? `当前库存 ${product.stock} 件，可先加入购物车再统一下单。` : '当前商品库存不足，暂时无法加入购物车。' }}
            </p>
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

          <div class="detail-hero__actions">
            <el-button type="danger" size="large" @click="toggleFavorite">
              <el-icon><Star /></el-icon>
              {{ product.isFavorited ? '已收藏' : '收藏茶品' }}
            </el-button>
          </div>
        </div>

        <div class="detail-hero__gallery">
          <div class="main-image">
            <img :src="currentImage || resolveProductImage(product, product.mainImage)" :alt="product.name">
          </div>
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

      <section class="detail-tabs paper-panel" v-if="product.id">
        <div class="tab-headers">
          <button :class="{ active: tab === 'desc' }" @click="tab = 'desc'">商品详情</button>
          <button :class="{ active: tab === 'comments' }" @click="tab = 'comments'">茶友评价（{{ commentTotal }}）</button>
        </div>

        <div class="tab-body" v-show="tab === 'desc'">
          <div class="desc-layout">
            <div class="desc-content" v-html="product.description || '暂无描述'"></div>
            <aside class="desc-aside paper-subpanel">
              <strong>品饮提示</strong>
              <p>若你已经大致有了偏好，不妨结合价格、销量与风味再细看一遍；若还想多比较几款，也可以回到茶单继续慢慢挑。</p>
              <router-link to="/products">返回全部茶品</router-link>
            </aside>
          </div>
        </div>

        <div class="tab-body" v-show="tab === 'comments'">
          <div class="comment-form paper-subpanel" v-if="isLoggedIn">
            <el-input v-model="newComment" type="textarea" :rows="3" placeholder="写下你对这款茶的品鉴感受..." />
            <div class="comment-form-bottom">
              <el-rate v-model="newRating" :max="5" show-score />
              <el-button type="primary" @click="submitComment" :loading="submitting">发表评价</el-button>
            </div>
          </div>
          <div v-else class="comment-login-tip paper-subpanel">
            <router-link to="/login">登录</router-link>后即可发表评价
          </div>

          <div class="comment-list" v-loading="commentLoading">
            <article v-for="c in comments" :key="c.id" class="comment-item paper-subpanel">
              <div class="comment-avatar">{{ c.username?.charAt(0) || '茶' }}</div>
              <div class="comment-body">
                <div class="comment-header">
                  <span class="comment-user">{{ c.username }}</span>
                  <el-rate :model-value="c.rating" disabled size="small" />
                  <span class="comment-time">{{ formatTime(c.createTime) }}</span>
                </div>
                <p class="comment-text">{{ c.content }}</p>
              </div>
            </article>
            <div v-if="!commentLoading && commentLoadFailed" class="empty paper-subpanel">
              评价内容暂时加载失败，你可以稍后再看，商品详情与购买流程不受影响。
            </div>
            <div v-else-if="!commentLoading && comments.length === 0" class="empty paper-subpanel">暂无评价，欢迎首评</div>
          </div>
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

    <FooterBar />
  </div>
</template>

<script setup>
import { computed, onMounted, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import NavBar from '@/components/NavBar.vue'
import FooterBar from '@/components/FooterBar.vue'
import { cartApi } from '@/api/cart'
import { commentApi, favoriteApi, productApi } from '@/api/product'
import { resolveProductGallery, resolveProductImage } from '@/utils/localImage'

const route = useRoute()
const router = useRouter()
const product = ref({})
const loading = ref(false)
const currentImage = ref('')
const tab = ref('desc')
const newComment = ref('')
const newRating = ref(5)
const submitting = ref(false)
const comments = ref([])
const commentLoading = ref(false)
const commentPage = ref(1)
const commentSize = ref(10)
const commentTotal = ref(0)
const commentLoadFailed = ref(false)
const isLoggedIn = !!localStorage.getItem('token')
const detailImages = ref([])
const purchaseQuantity = ref(1)
const addingToCart = ref(false)
const hasAddedToCart = ref(false)
const maxPurchaseQuantity = computed(() => Math.max(1, Number(product.value.stock) || 1))

const loadDetail = async () => {
  loading.value = true
  try {
    product.value = await productApi.getDetail(route.params.id)
    detailImages.value = resolveProductGallery(product.value)
    currentImage.value = resolveProductImage(product.value, product.value.mainImage)
    purchaseQuantity.value = product.value.stock > 0 ? 1 : 1
    hasAddedToCart.value = false
  } finally {
    loading.value = false
  }
}

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
    commentLoadFailed.value = true
  } finally {
    commentLoading.value = false
  }
}

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
    // handled by interceptor
  }
}

const submitComment = async () => {
  if (!newComment.value.trim()) {
    ElMessage.warning('请输入评价内容')
    return
  }
  submitting.value = true
  try {
    await commentApi.add({ productId: Number(route.params.id), content: newComment.value, rating: newRating.value })
    ElMessage.success('评价发表成功')
    newComment.value = ''
    newRating.value = 5
    commentPage.value = 1
    loadComments()
  } finally {
    submitting.value = false
  }
}

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

const goToCart = () => {
  if (!isLoggedIn) {
    router.push('/login')
    return
  }
  router.push('/cart')
}

const formatTime = (t) => {
  if (!t) return ''
  return new Date(t).toLocaleDateString('zh-CN')
}

onMounted(() => {
  loadDetail()
  loadComments()
})
</script>

<style scoped>
.page {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
}

.detail-page {
  flex: 1;
  padding-top: 28px;
}

.breadcrumb {
  display: flex;
  gap: 8px;
  margin-bottom: 20px;
  color: var(--color-ink-muted);
  font-size: 13px;
}

.detail-hero {
  display: grid;
  grid-template-columns: minmax(0, 1.06fr) minmax(420px, 0.94fr);
  gap: 30px;
  padding: 32px;
  margin-bottom: 28px;
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

.info-tag.hot {
  background: rgba(159, 83, 24, 0.94);
}

.info-tag.new {
  background: rgba(47, 79, 62, 0.92);
}

.info-tag.cat {
  background: rgba(92, 64, 51, 0.9);
}

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

.thumb.active {
  border-color: var(--color-red);
}

.thumb img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.detail-tabs {
  padding: 26px;
}

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

.tab-headers button.active {
  background: var(--color-green);
  border-color: var(--color-green);
  color: #fffaf2;
}

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

.comment-form-bottom {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-top: 12px;
}

.comment-list {
  margin-top: 16px;
}

.comment-item {
  display: flex;
  gap: 14px;
  margin-bottom: 12px;
}

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

.comment-login-tip,
.empty {
  text-align: center;
  color: var(--color-ink-soft);
}
</style>
