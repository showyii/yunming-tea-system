<template>
  <div class="page">
    <NavBar />

    <div class="container category-page">
      <section class="inner-hero paper-panel">
        <div class="inner-hero__copy">
          <h1>先顺着茶性与香气看看方向，再挑一类合自己心意的茶慢慢细选</h1>
          <p>绿茶的鲜、红茶的润、乌龙的香、白茶的柔、黑茶的厚、花茶的亲近感，都先替你理在这一页里，方便按心情慢慢找。</p>
        </div>

        <div class="inner-hero__aside">
          <strong>选茶建议</strong>
          <p>若你已经知道自己偏爱清鲜、甘润、焙香、柔和或醇厚，就先从相近方向看起；若还拿不准，也可以把六类都慢慢看一遍。</p>
          <div class="inner-hero__actions">
            <router-link to="/products" class="inner-hero__link">前往全部茶品</router-link>
            <router-link to="/" class="inner-hero__link is-muted">回首页看快捷入口</router-link>
          </div>
        </div>
      </section>

      <section class="guide-summary">
        <article class="guide-summary__card paper-panel">
          <span>适合新手</span>
          <strong>先按体感选方向</strong>
          <p>想喝清一点看绿茶，想喝柔一点看红茶或白茶，想闻香和比层次看乌龙，想喝厚一点看黑茶，想更亲切好入口可以先看花茶。</p>
        </article>
        <article class="guide-summary__card paper-panel">
          <span>适合送礼</span>
          <strong>先选稳妥型茶类</strong>
          <p>不确定对方偏好时，红茶、乌龙和花茶通常更容易被接受；如果知道对方平时就喝茶，再去细看对应类目的代表茶会更准确。</p>
        </article>
        <article class="guide-summary__card paper-panel">
          <span>适合复购</span>
          <strong>先看常备场景</strong>
          <p>办公室常备可优先红茶、绿茶，饭后解腻可看黑茶，茶席待客和慢慢泡饮则更适合乌龙和白茶。</p>
        </article>
      </section>

      <section class="desktop-section">
        <div class="section-title section-title-left">
          <h2>六类看茶</h2>
          <p>{{ activeFilter.description }}</p>
        </div>

        <div class="quick-filter-panel paper-panel">
          <div class="quick-filter-panel__copy">
            <span>看茶方向</span>
            <strong>先按此刻想喝的感觉挑一个方向，页里会留下更贴近这份心意的茶类</strong>
          </div>

          <div class="quick-filter-chip-group">
            <button
              v-for="filter in categoryQuickFilters"
              :key="filter.id"
              type="button"
              class="quick-filter-chip"
              :class="{ 'is-active': activeFilterId === filter.id }"
              @click="activeFilterId = filter.id"
            >
              <strong>{{ filter.label }}</strong>
              <span>{{ filter.description }}</span>
            </button>
          </div>
        </div>

        <TeaCategoryShowcase
          :categories="visibleCategories"
          :loading="loading"
          variant="guide"
          link-text="进入这一类继续选茶"
        />
      </section>

      <section class="compare-panel paper-panel">
        <div class="compare-panel__copy">
          <span>拿不准时</span>
          <h3>不妨先这样看</h3>
          <p>想喝得清鲜一点看绿茶，想入口柔和些看红茶，想闻香气、比层次看乌龙，想清柔慢饮看白茶，想醇厚耐泡看黑茶，想花香亲切些就先看花茶。</p>
        </div>
        <router-link to="/products" class="compare-panel__link">直接去全部茶品做筛选</router-link>
      </section>
    </div>

    <FooterBar />
  </div>
</template>

<script setup>
import { computed, onMounted, ref } from 'vue'
import NavBar from '@/components/NavBar.vue'
import FooterBar from '@/components/FooterBar.vue'
import TeaCategoryShowcase from '@/components/TeaCategoryShowcase.vue'
import { categoryApi, productApi } from '@/api/product'
import { categoryQuickFilters, decorateCategories, getQuickFilterMatches } from '@/utils/teaCategories'

const categories = ref([])
const loading = ref(false)
const activeFilterId = ref('all')

const activeFilter = computed(() => categoryQuickFilters.find((item) => item.id === activeFilterId.value) || categoryQuickFilters[0])
const visibleCategories = computed(() => getQuickFilterMatches(categories.value, activeFilterId.value))

onMounted(async () => {
  loading.value = true
  categoryApi.getAll().then(async (r) => {
    const categoryList = decorateCategories(r)
    const previewResults = await Promise.allSettled(
      categoryList.map((category) => productApi.search({
        categoryId: category.id,
        page: 1,
        size: 3
      }))
    )

    categories.value = categoryList.map((category, index) => {
      const preview = previewResults[index]
      const previewNames = preview.status === 'fulfilled'
        ? (preview.value.records || []).map((item) => item.name).filter(Boolean)
        : []

      return {
        ...category,
        previewNames
      }
    })
  }).finally(() => {
    loading.value = false
  })
})
</script>

<style scoped>
.page {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
}

.category-page {
  flex: 1;
  padding-top: 28px;
}

.inner-hero {
  display: grid;
  grid-template-columns: minmax(0, 1.4fr) minmax(320px, 0.78fr);
  gap: 28px;
  padding: 34px 36px;
  align-items: center;
}

.inner-hero__copy {
  max-width: 760px;
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
  font-size: 50px;
  font-weight: 600;
  line-height: 1.18;
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
  color: var(--color-red);
  font-size: 13px;
  letter-spacing: 0.16em;
}

.inner-hero__actions {
  display: flex;
  flex-wrap: wrap;
  gap: 18px;
  margin-top: 18px;
}

.inner-hero__link.is-muted {
  color: var(--color-ink-muted);
}

.guide-summary {
  display: grid;
  grid-template-columns: repeat(3, minmax(0, 1fr));
  gap: 20px;
  margin-top: 28px;
}

.guide-summary__card {
  padding: 22px 24px;
}

.guide-summary__card span,
.compare-panel__copy span {
  color: var(--color-red);
  font-size: 11px;
  letter-spacing: 0.18em;
  text-transform: uppercase;
}

.guide-summary__card strong,
.compare-panel__copy h3 {
  display: block;
  margin-top: 10px;
  margin-bottom: 10px;
  color: var(--color-ink);
  font-family: var(--font-display);
  font-size: 26px;
  font-weight: 600;
  letter-spacing: 0.05em;
}

.guide-summary__card p,
.compare-panel__copy p {
  margin: 0;
  color: var(--color-ink-soft);
  font-size: 14px;
  line-height: 1.85;
}

.quick-filter-panel {
  margin-bottom: 24px;
  padding: 24px 26px;
  background:
    radial-gradient(circle at top left, rgba(140, 107, 63, 0.08), transparent 26%),
    linear-gradient(180deg, rgba(248, 244, 236, 0.98), rgba(245, 241, 232, 0.96));
}

.quick-filter-panel__copy {
  margin-bottom: 18px;
}

.quick-filter-panel__copy span {
  color: var(--color-red);
  font-size: 11px;
  letter-spacing: 0.18em;
  text-transform: uppercase;
}

.quick-filter-panel__copy strong {
  display: block;
  margin-top: 10px;
  color: var(--color-ink);
  font-family: var(--font-display);
  font-size: 24px;
  font-weight: 600;
  letter-spacing: 0.05em;
}

.quick-filter-chip-group {
  display: grid;
  grid-template-columns: repeat(4, minmax(0, 1fr));
  gap: 14px;
}

.quick-filter-chip {
  padding: 16px 16px 15px;
  text-align: left;
  border: 1px solid rgba(216, 210, 196, 0.9);
  border-radius: 14px;
  background: rgba(248, 244, 236, 0.9);
  cursor: pointer;
  transition: transform var(--transition-base), border-color var(--transition-base), box-shadow var(--transition-base), background var(--transition-base), color var(--transition-base);
}

.quick-filter-chip:hover {
  transform: translateY(-2px);
  border-color: rgba(140, 107, 63, 0.42);
  box-shadow: var(--shadow-sm);
}

.quick-filter-chip.is-active {
  border-color: rgba(140, 107, 63, 0.62);
  background:
    linear-gradient(180deg, rgba(248, 244, 236, 1), rgba(245, 241, 232, 0.98));
  box-shadow:
    inset 0 0 0 1px rgba(140, 107, 63, 0.12),
    0 10px 24px rgba(47, 42, 36, 0.08);
}

.quick-filter-chip strong {
  display: block;
  color: var(--color-wood);
  font-size: 15px;
  font-weight: 600;
  letter-spacing: 0.04em;
}

.quick-filter-chip span {
  display: block;
  margin-top: 8px;
  color: var(--color-ink-soft);
  font-size: 12px;
  line-height: 1.75;
}

.quick-filter-chip.is-active strong {
  color: var(--color-green);
}

.quick-filter-chip.is-active span {
  color: var(--color-wood-soft);
}

.quick-filter-chip.is-active::before {
  content: "";
  display: block;
  width: 44px;
  height: 3px;
  margin-bottom: 12px;
  background: var(--color-red);
  border-radius: 999px;
}

.compare-panel {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 20px;
  margin-top: 32px;
  padding: 26px 30px;
}

.compare-panel__copy h3 {
  margin-top: 8px;
}

.compare-panel__link {
  flex: 0 0 auto;
  color: var(--color-red);
  font-size: 13px;
  letter-spacing: 0.14em;
}

@media (max-width: 1024px) {
  .inner-hero,
  .guide-summary,
  .quick-filter-chip-group {
    grid-template-columns: 1fr;
  }

  .compare-panel {
    flex-direction: column;
    align-items: flex-start;
  }
}
</style>
