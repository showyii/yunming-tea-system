<template>
  <div class="home-page">
    <NavBar />

    <section class="hero-stage">
      <div class="hero-stage__media">
        <div
          v-for="(slide, index) in heroSlides"
          :key="slide.id"
          class="hero-stage__slide"
          :class="{ 'is-active': index === currentHeroIndex }"
        >
          <img :src="slide.image" :alt="slide.name">
        </div>
      </div>
      <div class="hero-stage__veil"></div>
      <div class="container hero-layout">
        <div class="hero-copy">
          <span v-if="activeHeroSlide.kicker" class="hero-kicker">{{ activeHeroSlide.kicker }}</span>
          <h1>{{ activeHeroSlide.title }}</h1>
          <p>{{ activeHeroSlide.description }}</p>
          <div class="hero-actions">
            <router-link to="/products" class="wood-button is-accent">浏览全部茶品</router-link>
            <router-link to="/rooms" class="wood-button">预约馆内雅间</router-link>
          </div>
          <div class="hero-notes">
            <span v-for="note in activeHeroSlide.notes" :key="note">{{ note }}</span>
          </div>
        </div>
      </div>

      <div class="hero-carousel-dots">
        <button
          v-for="(slide, index) in heroSlides"
          :key="slide.id"
          type="button"
          class="hero-carousel-dots__item"
          :class="{ active: index === currentHeroIndex }"
          :aria-label="`切换到轮播 ${index + 1}`"
          @click="setSlide(index)"
        ></button>
      </div>
    </section>

    <section class="container desktop-section">
      <div class="section-title">
        <h2>六大茶类快捷入口</h2>
        <p>首页只保留一层轻量入口，帮你按风味快速进入；如果还想先理解差异，再慢慢做决定，可以进入完整分类导购页。</p>
      </div>

      <TeaCategoryShowcase
        :categories="categories"
        :loading="catLoading"
        variant="shortcut"
        link-text="快速进入该类茶品"
      />

      <div class="category-shortcut-note paper-panel">
        <div>
          <span>想先看懂再挑</span>
          <strong>茶品分类页现在会补充风味、适合场景和代表茶参考</strong>
        </div>
        <router-link to="/categories">进入分类导购页</router-link>
      </div>
    </section>

    <section class="container desktop-section">
      <div class="curated-layout">
        <div class="curated-main">
          <div class="section-title section-title-left">
            <h2>精选茶品</h2>
            <p>这里挑出几款馆里常被问起的茶，适合先随手看看，也适合从中找一款带走，慢慢喝出自己的偏爱。</p>
          </div>

          <div class="featured-product-grid" v-loading="hotLoading">
            <article
              v-for="item in featuredProducts"
              :key="item.id"
              class="featured-product-card paper-panel"
              @click="$router.push(`/products/${item.id}`)"
            >
              <div class="featured-product-card__image">
                <img :src="resolveProductImage(item, item.mainImage)" :alt="item.name">
                <span class="featured-product-card__badge">热销</span>
              </div>
              <div class="featured-product-card__body">
                <span class="featured-product-card__category">{{ item.categoryName || '茶品甄选' }}</span>
                <h3>{{ item.name }}</h3>
                <p>{{ item.subtitle || '适合日常饮用、馆内茶席与礼赠场景的常备茶品。' }}</p>
                <div class="featured-product-card__meta">
                  <span class="price">¥{{ item.price }}</span>
                  <span class="sales">已售 {{ item.sales }}</span>
                </div>
              </div>
            </article>
          </div>
        </div>

        <aside class="curated-side">
          <section class="curated-panel paper-panel" v-loading="newLoading">
            <div class="curated-panel__header">
              <strong>当季上新</strong>
            </div>
            <article
              v-for="item in seasonalProducts"
              :key="item.id"
              class="mini-product"
              @click="$router.push(`/products/${item.id}`)"
            >
              <img :src="resolveProductImage(item, item.mainImage)" :alt="item.name">
              <div>
                <strong>{{ item.name }}</strong>
                <p>{{ item.subtitle || '适合新客尝鲜与节令品饮。' }}</p>
              </div>
              <em>¥{{ item.price }}</em>
            </article>
          </section>

          <section class="curated-panel paper-panel" v-loading="recLoading">
            <div class="curated-panel__header">
              <strong>馆主推荐</strong>
            </div>
            <article
              v-for="item in housePicks"
              :key="item.id"
              class="mini-product"
              @click="$router.push(`/products/${item.id}`)"
            >
              <img :src="resolveProductImage(item, item.mainImage)" :alt="item.name">
              <div>
                <strong>{{ item.name }}</strong>
                <p>{{ item.subtitle || '更适合老茶客复购与待客之选。' }}</p>
              </div>
              <em>¥{{ item.price }}</em>
            </article>
          </section>
        </aside>
      </div>
    </section>

    <section class="container desktop-section">
      <div class="brand-closing paper-panel">
        <div class="brand-closing__copy">
          <h2>让好茶、好器与好空间，在同一页里自然相遇</h2>
          <p>若你已经挑好了茶，也不妨再看看馆内雅间与近期茶事。也许下一次来云茗，正好能把喝茶这件事留得更久一些。</p>
        </div>

        <div class="brand-closing__actions">
          <router-link to="/rooms" class="closing-link">
            <strong>包间预约</strong>
            <span>查看馆内雅间与时段预订</span>
          </router-link>
          <router-link to="/activities" class="closing-link">
            <strong>茶事活动</strong>
            <span>浏览近期雅集、课程与主题活动</span>
          </router-link>
        </div>
      </div>
    </section>

    <FooterBar />
  </div>
</template>

<script setup>
import { computed, onBeforeUnmount, onMounted, ref } from 'vue'
import NavBar from '@/components/NavBar.vue'
import FooterBar from '@/components/FooterBar.vue'
import TeaCategoryShowcase from '@/components/TeaCategoryShowcase.vue'
import { categoryApi, productApi } from '@/api/product'
import { resolveProductImage } from '@/utils/localImage'
import { decorateCategories } from '@/utils/teaCategories'

const categories = ref([])
const hotProducts = ref([])
const newProducts = ref([])
const recommendProducts = ref([])
const catLoading = ref(false)
const hotLoading = ref(false)
const newLoading = ref(false)
const recLoading = ref(false)
const currentHeroIndex = ref(0)
let heroTimer = null

const heroSlides = [
    {
      id: 'home-hero-1',
      name: '首页轮播图 01',
      kicker: '',
      title: '一盏清茶在手，把选茶、歇脚与相聚都慢慢安放下来',
      description: '从六大茶类的挑选，到馆内雅间的预订，再到当季茶事的赴约，云茗希望你一进来，就能找到适合今天心情的那一处去处。',
      notes: ['先挑茶，再慢慢坐下', '雅间与茶事顺手可看', '像进馆一样自然浏览'],
      image: '/images/home/carousel/hero-01.png',
      fileHint: '当前文件：/frontend/public/images/home/carousel/hero-01.png'
    },
    {
      id: 'home-hero-2',
      name: '首页轮播图 02',
      kicker: '',
      title: '茶品、空间与茶事，都在这一页里缓缓铺展开来',
      description: '有人先为一款茶停下，也有人先被一间雅室吸引。云茗想把这些相遇放在同一页里，让每一步浏览都更顺心一些。',
      notes: ['茶品与空间彼此呼应', '活动入口自然展开', '像逛馆一样慢慢看'],
      image: '/images/home/carousel/hero-02.png',
      fileHint: '当前文件：/frontend/public/images/home/carousel/hero-02.png'
    },
    {
      id: 'home-hero-3',
      name: '首页轮播图 03',
      kicker: '',
      title: '无论是来买茶、约人还是赴一场雅集，都能从容一些',
      description: '你可以先挑一款适合带走的茶，也可以先约一间适合相聚的雅室。每一步都不必着急，顺着自己的兴致慢慢看就好。',
      notes: ['选茶不赶时间', '预约相聚更从容', '赴约前先把信息看清'],
      image: '/images/home/carousel/hero-03.png',
      fileHint: '当前文件：/frontend/public/images/home/carousel/hero-03.png'
    }
]

const featuredProducts = computed(() => hotProducts.value.slice(0, 4))
const seasonalProducts = computed(() => newProducts.value.slice(0, 3))
const housePicks = computed(() => recommendProducts.value.slice(0, 3))
const activeHeroSlide = computed(() => heroSlides[currentHeroIndex.value] || heroSlides[0])
const setSlide = (index) => {
  currentHeroIndex.value = index
}

const goNextSlide = () => {
  currentHeroIndex.value = (currentHeroIndex.value + 1) % heroSlides.length
}

const startHeroAutoplay = () => {
  stopHeroAutoplay()
  heroTimer = window.setInterval(() => {
    goNextSlide()
  }, 5000)
}

const stopHeroAutoplay = () => {
  if (heroTimer) {
    window.clearInterval(heroTimer)
    heroTimer = null
  }
}

onMounted(async () => {
  startHeroAutoplay()
  catLoading.value = true
  hotLoading.value = true
  newLoading.value = true
  recLoading.value = true

  const [categoryResult, hotResult, newResult, recommendResult] = await Promise.allSettled([
    categoryApi.getAll(),
    productApi.getHot(),
    productApi.getNew(),
    productApi.getRecommend()
  ])

  if (categoryResult.status === 'fulfilled') {
    categories.value = decorateCategories(categoryResult.value)
  }
  if (hotResult.status === 'fulfilled') {
    hotProducts.value = hotResult.value
  }
  if (newResult.status === 'fulfilled') {
    newProducts.value = newResult.value
  }
  if (recommendResult.status === 'fulfilled') {
    recommendProducts.value = recommendResult.value
  }

  catLoading.value = false
  hotLoading.value = false
  newLoading.value = false
  recLoading.value = false
})

onBeforeUnmount(() => {
  stopHeroAutoplay()
})
</script>

<style scoped>
.home-page {
  min-height: 100vh;
}

.hero-stage {
  position: relative;
  height: 640px;
  overflow: hidden;
  background: linear-gradient(120deg, rgba(28, 24, 20, 0.96), rgba(26, 37, 42, 0.92) 58%, rgba(95, 61, 35, 0.88));
}

.hero-stage__media {
  position: absolute;
  inset: 0;
}

.hero-stage__slide {
  position: absolute;
  inset: 0;
  opacity: 0;
  transition: opacity 520ms ease;
}

.hero-stage__slide.is-active {
  opacity: 1;
}

.hero-stage__slide img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  object-position: center center;
}

.hero-stage__veil {
  position: absolute;
  inset: 0;
  background:
    linear-gradient(90deg, rgba(18, 16, 13, 0.78) 0%, rgba(18, 16, 13, 0.46) 46%, rgba(18, 16, 13, 0.22) 100%),
    radial-gradient(circle at 70% 24%, rgba(255, 255, 255, 0.05), transparent 24%),
    linear-gradient(180deg, rgba(10, 9, 8, 0.16), rgba(10, 9, 8, 0.3));
  pointer-events: none;
}

.hero-layout {
  position: relative;
  z-index: 1;
  min-height: calc(640px - var(--nav-height) - 24px);
  display: flex;
  align-items: center;
  padding-top: 44px;
  padding-bottom: 84px;
}

.hero-copy {
  width: min(100%, 600px);
  color: #fffaf2;
}

.hero-kicker {
  display: inline-block;
  margin-bottom: 18px;
  color: rgba(255, 248, 240, 0.72);
  font-size: 12px;
  letter-spacing: 0.34em;
}

.hero-copy h1 {
  margin: 0;
  font-family: var(--font-display);
  font-size: 72px;
  font-weight: 600;
  line-height: 1.1;
  letter-spacing: 0.06em;
}

.hero-copy p {
  margin: 24px 0 0;
  color: rgba(255, 248, 240, 0.86);
  font-size: 16px;
  line-height: 1.95;
}

.hero-actions {
  display: flex;
  gap: 14px;
  margin-top: 34px;
}

.hero-notes {
  display: flex;
  flex-wrap: wrap;
  gap: 12px 22px;
  margin-top: 34px;
  color: rgba(255, 248, 240, 0.74);
  font-size: 13px;
  letter-spacing: 0.08em;
  position: relative;
}

.hero-carousel-dots {
  position: absolute;
  z-index: 1;
  left: 50%;
  bottom: 24px;
  display: flex;
  gap: 10px;
  transform: translateX(-50%);
}

.hero-carousel-dots__item {
  width: 34px;
  height: 4px;
  border: none;
  background: rgba(255, 248, 240, 0.32);
  cursor: pointer;
}

.hero-carousel-dots__item.active {
  background: #fffaf2;
}

.category-shortcut-note {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 20px;
  margin-top: 22px;
  padding: 20px 24px;
}

.category-shortcut-note span {
  color: var(--color-red);
  font-size: 11px;
  letter-spacing: 0.18em;
  text-transform: uppercase;
}

.category-shortcut-note strong {
  display: block;
  margin-top: 8px;
  color: var(--color-ink);
  font-family: var(--font-display);
  font-size: 24px;
  font-weight: 600;
  letter-spacing: 0.05em;
}

.category-shortcut-note a {
  flex: 0 0 auto;
  color: var(--color-red);
  font-size: 13px;
  letter-spacing: 0.14em;
}

.curated-layout {
  display: grid;
  grid-template-columns: minmax(0, 1.24fr) minmax(320px, 0.76fr);
  gap: 24px;
}

.featured-product-grid {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 20px;
}

.featured-product-card {
  overflow: hidden;
  cursor: pointer;
  transition: transform var(--transition-base), box-shadow var(--transition-base), border-color var(--transition-base);
}

.featured-product-card:hover {
  transform: translateY(-4px);
  box-shadow: var(--shadow-md);
  border-color: rgba(159, 83, 24, 0.18);
}

.featured-product-card__image {
  position: relative;
  aspect-ratio: 1 / 1;
  background: #ece0cd;
  overflow: hidden;
}

.featured-product-card__image img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform 240ms ease;
}

.featured-product-card:hover .featured-product-card__image img {
  transform: scale(1.04);
}

.featured-product-card__badge {
  position: absolute;
  top: 16px;
  left: 16px;
  padding: 6px 10px;
  background: rgba(159, 83, 24, 0.94);
  color: #fff7f1;
  font-size: 11px;
  letter-spacing: 0.12em;
}

.featured-product-card__body {
  padding: 22px;
}

.featured-product-card__category {
  color: var(--color-green);
  font-size: 12px;
  letter-spacing: 0.14em;
  text-transform: uppercase;
}

.featured-product-card__body h3 {
  margin: 12px 0 10px;
  color: var(--color-ink);
  font-family: var(--font-display);
  font-size: 28px;
  font-weight: 600;
  letter-spacing: 0.05em;
}

.featured-product-card__body p {
  margin: 0;
  color: var(--color-ink-soft);
  font-size: 14px;
  line-height: 1.85;
}

.featured-product-card__meta {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-top: 20px;
}

.price {
  color: var(--color-red);
  font-size: 26px;
  font-weight: 700;
}

.sales {
  color: var(--color-ink-muted);
  font-size: 13px;
}

.curated-side {
  display: grid;
  gap: 20px;
}

.curated-panel {
  padding: 24px;
}

.curated-panel__header {
  margin-bottom: 18px;
}

.curated-panel__header span {
  color: var(--color-red);
  font-size: 11px;
  letter-spacing: 0.18em;
  text-transform: uppercase;
}

.curated-panel__header strong {
  display: block;
  margin-top: 6px;
  color: var(--color-ink);
  font-family: var(--font-display);
  font-size: 28px;
  font-weight: 600;
  letter-spacing: 0.08em;
}

.mini-product {
  display: grid;
  grid-template-columns: 92px 1fr auto;
  gap: 14px;
  align-items: center;
  padding: 12px 0;
  border-top: 1px solid rgba(140, 107, 63, 0.14);
  cursor: pointer;
}

.mini-product:first-of-type {
  border-top: none;
  padding-top: 0;
}

.mini-product img {
  width: 92px;
  height: 92px;
  object-fit: cover;
  background: #ece0cd;
}

.mini-product strong {
  display: block;
  color: var(--color-ink);
  font-size: 16px;
  margin-bottom: 6px;
}

.mini-product p {
  margin: 0;
  color: var(--color-ink-soft);
  font-size: 13px;
  line-height: 1.7;
}

.mini-product em {
  color: var(--color-red);
  font-size: 18px;
  font-style: normal;
  font-weight: 700;
}

.brand-closing {
  display: grid;
  grid-template-columns: minmax(0, 1.15fr) minmax(320px, 0.85fr);
  gap: 32px;
  padding: 36px;
}

.brand-closing__copy h2 {
  margin: 10px 0 14px;
  color: var(--color-ink);
  font-family: var(--font-display);
  font-size: 40px;
  font-weight: 600;
  line-height: 1.25;
  letter-spacing: 0.06em;
}

.brand-closing__copy p {
  margin: 0;
  color: var(--color-ink-soft);
  font-size: 15px;
  line-height: 1.9;
}

.brand-closing__actions {
  display: grid;
  gap: 14px;
}

.closing-link {
  display: block;
  padding: 22px 24px;
  background: rgba(255, 252, 246, 0.8);
  border: 1px solid rgba(204, 188, 161, 0.48);
  transition: transform var(--transition-base), box-shadow var(--transition-base), border-color var(--transition-base);
}

.closing-link:hover {
  transform: translateY(-2px);
  box-shadow: var(--shadow-sm);
  border-color: rgba(159, 83, 24, 0.2);
}

.closing-link strong {
  display: block;
  color: var(--color-wood);
  font-family: var(--font-display);
  font-size: 24px;
  font-weight: 600;
  letter-spacing: 0.06em;
}

.closing-link span {
  display: block;
  margin-top: 8px;
  color: var(--color-ink-soft);
  font-size: 14px;
}

@media (max-width: 1024px) {
  .hero-layout {
    min-height: calc(640px - var(--nav-height) - 16px);
    padding-bottom: 72px;
  }

  .category-shortcut-note {
    flex-direction: column;
    align-items: flex-start;
  }
}
</style>
