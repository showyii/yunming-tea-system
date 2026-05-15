<template>
  <div class="page">
    <NavBar />

    <div class="container list-page">
      <section class="inner-hero paper-panel">
        <div class="inner-hero__copy">
          <h1>在整页茶单里慢慢挑，看见真正想带回去细品的那几款</h1>
          <p>无论你想找日常常备的口粮茶，还是想挑一款待客、送礼的茶，这里都可以按自己的节奏慢慢比较。</p>
        </div>

        <div class="inner-hero__aside">
          <strong>浏览方式</strong>
          <p>可以先按分类缩小范围，再结合价格、销量与关键词一点点收窄；若只是想快速挑礼盒或常备茶，先看热销与推荐会更省心。</p>
          <div class="hero-links">
            <router-link to="/categories">先看茶类</router-link>
            <router-link to="/rooms">看看馆内雅间</router-link>
          </div>
        </div>
      </section>

      <div class="breadcrumb">
        <router-link to="/">首页</router-link>
        <span>/</span>
        <span>全部茶品</span>
      </div>

      <section class="filter-panel paper-panel">
        <div class="filter-panel__heading">
          <div>
            <span>筛选条件</span>
            <strong>按关键词、分类与排序方式慢慢筛茶</strong>
          </div>
          <em>当前共 {{ total }} 款在列茶品</em>
        </div>

        <div class="filter-row">
          <el-input
            v-model="keyword"
            placeholder="搜索茶品名称、风味或礼盒"
            class="search-input"
            clearable
            @keyup.enter="doSearch"
          >
            <template #prefix><el-icon><Search /></el-icon></template>
          </el-input>

          <el-select v-model="categoryId" placeholder="全部分类" clearable @change="onFilterChange">
            <el-option v-for="c in categories" :key="c.id" :label="c.name" :value="c.id" />
          </el-select>

          <el-select v-model="sort" placeholder="默认排序" @change="onFilterChange">
            <el-option label="默认排序" value="" />
            <el-option label="价格由低到高" value="price_asc" />
            <el-option label="价格由高到低" value="price_desc" />
            <el-option label="销量优先" value="sales_desc" />
          </el-select>

          <el-button type="primary" @click="doSearch">查看结果</el-button>
        </div>
      </section>

      <section class="product-grid" v-loading="loading">
        <article
          v-for="p in products"
          :key="p.id"
          class="product-card paper-panel"
          @click="$router.push(`/products/${p.id}`)"
        >
          <div class="product-card__image">
            <img :src="resolveProductImage(p, p.mainImage)" :alt="p.name">
            <div class="product-card__tags">
              <span v-if="p.isHot" class="tag tag-hot">热销</span>
              <span v-if="p.isNew" class="tag tag-new">新品</span>
            </div>
          </div>

          <div class="product-card__body">
            <span class="product-card__category">{{ p.categoryName || '茶品甄选' }}</span>
            <h3>{{ p.name }}</h3>
            <p>{{ p.subtitle || '适合日常品饮、茶席待客与礼赠场景。' }}</p>

            <div class="product-card__meta">
              <span class="product-card__price">¥{{ p.price }}</span>
              <span class="product-card__sales">已售 {{ p.sales }}</span>
            </div>
          </div>
        </article>

        <div v-if="!loading && products.length === 0" class="empty paper-panel">
          当前条件下还没有找到合适茶品，不妨换个茶类、清空关键词，或换一种排序方式再看看。
        </div>
      </section>

      <div class="pagination" v-if="total > 0">
        <el-pagination
          background
          layout="prev, pager, next"
          :total="total"
          :page-size="size"
          v-model:current-page="page"
          @current-change="doSearch"
        />
      </div>
    </div>

    <FooterBar />
  </div>
</template>

<script setup>
import { onMounted, ref } from 'vue'
import { useRoute } from 'vue-router'
import NavBar from '@/components/NavBar.vue'
import FooterBar from '@/components/FooterBar.vue'
import { categoryApi, productApi } from '@/api/product'
import { resolveProductImage } from '@/utils/localImage'

const route = useRoute()
const keyword = ref('')
const categoryId = ref(null)
const sort = ref('')
const page = ref(1)
const size = ref(12)
const products = ref([])
const categories = ref([])
const total = ref(0)
const loading = ref(false)

const doSearch = async () => {
  loading.value = true
  try {
    const params = {
      keyword: keyword.value || undefined,
      categoryId: categoryId.value || undefined,
      sort: sort.value || undefined,
      page: page.value,
      size: size.value
    }
    const res = await productApi.search(params)
    products.value = res.records || []
    total.value = res.total || 0
  } finally {
    loading.value = false
  }
}

const onFilterChange = () => {
  page.value = 1
  doSearch()
}

onMounted(async () => {
  categoryApi.getAll().then((r) => {
    categories.value = r
  })

  if (route.query.categoryId) {
    categoryId.value = Number(route.query.categoryId)
  }
  if (route.query.keyword) {
    keyword.value = route.query.keyword
  }

  doSearch()
})
</script>

<style scoped>
.page {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
}

.list-page {
  flex: 1;
  padding-top: 28px;
}

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

.hero-links {
  display: flex;
  gap: 18px;
  margin-top: 18px;
}

.hero-links a {
  color: var(--color-red);
  font-size: 13px;
  letter-spacing: 0.14em;
}

.breadcrumb {
  display: flex;
  align-items: center;
  gap: 8px;
  margin: 18px 0;
  color: var(--color-ink-muted);
  font-size: 13px;
}

.filter-panel {
  padding: 24px;
}

.filter-panel__heading {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 16px;
  margin-bottom: 18px;
}

.filter-panel__heading span {
  color: var(--color-red);
  font-size: 12px;
  letter-spacing: 0.24em;
}

.filter-panel__heading strong {
  display: block;
  margin-top: 8px;
  color: var(--color-ink);
  font-family: var(--font-display);
  font-size: 24px;
  font-weight: 600;
  letter-spacing: 0.06em;
}

.filter-panel__heading em {
  color: var(--color-ink-muted);
  font-size: 13px;
  font-style: normal;
  letter-spacing: 0.08em;
}

.filter-row {
  display: grid;
  grid-template-columns: minmax(300px, 1fr) 200px 200px 140px;
  gap: 14px;
}

.search-input {
  min-width: 0;
}

.product-grid {
  display: grid;
  grid-template-columns: repeat(4, minmax(0, 1fr));
  gap: 22px;
  margin-top: 28px;
}

.product-card {
  overflow: hidden;
  cursor: pointer;
  transition: transform var(--transition-base), box-shadow var(--transition-base), border-color var(--transition-base);
}

.product-card:hover {
  transform: translateY(-4px);
  box-shadow: var(--shadow-md);
  border-color: rgba(159, 83, 24, 0.18);
}

.product-card__image {
  position: relative;
  aspect-ratio: 1 / 1;
  background: #ece0cd;
  overflow: hidden;
}

.product-card__image img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform 240ms ease;
}

.product-card:hover .product-card__image img {
  transform: scale(1.04);
}

.product-card__tags {
  position: absolute;
  top: 14px;
  left: 14px;
  display: flex;
  gap: 8px;
}

.tag {
  padding: 5px 10px;
  color: #fffaf2;
  font-size: 11px;
  letter-spacing: 0.12em;
}

.tag-hot {
  background: rgba(159, 83, 24, 0.94);
}

.tag-new {
  background: rgba(47, 79, 62, 0.92);
}

.product-card__body {
  padding: 20px;
}

.product-card__category {
  color: var(--color-green);
  font-size: 12px;
  letter-spacing: 0.14em;
  text-transform: uppercase;
}

.product-card__body h3 {
  margin: 12px 0 10px;
  color: var(--color-ink);
  font-family: var(--font-display);
  font-size: 26px;
  font-weight: 600;
  letter-spacing: 0.05em;
}

.product-card__body p {
  margin: 0;
  color: var(--color-ink-soft);
  font-size: 14px;
  line-height: 1.8;
}

.product-card__meta {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-top: 18px;
}

.product-card__price {
  color: var(--color-red);
  font-size: 24px;
  font-weight: 700;
}

.product-card__sales {
  color: var(--color-ink-muted);
  font-size: 13px;
}

.pagination {
  display: flex;
  justify-content: center;
  margin-top: 34px;
}

.empty {
  grid-column: 1 / -1;
  padding: 54px 20px;
  text-align: center;
  color: var(--color-ink-soft);
}
</style>
