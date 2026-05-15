<template>
  <div class="tea-category-showcase" :class="`tea-category-showcase--${variant}`" v-loading="loading">
    <router-link
      v-for="cat in categories"
      :key="cat.id"
      :to="cat.href"
      class="tea-category-showcase__card"
      :style="{ '--card-accent': cat.accent || '#6f553a' }"
    >
      <div class="tea-category-showcase__head">
        <span class="tea-category-showcase__mark">{{ cat.mark }}</span>
        <div class="tea-category-showcase__title">
          <strong>{{ cat.name }}</strong>
          <p>{{ variant === 'guide' ? cat.guideTitle : cat.description }}</p>
        </div>
      </div>

      <template v-if="variant === 'guide'">
        <div class="tea-category-showcase__chips">
          <span v-for="tag in cat.tags" :key="tag">{{ tag }}</span>
        </div>

        <p class="tea-category-showcase__recommendation">{{ cat.recommendation }}</p>

        <div class="tea-category-showcase__meta">
          <div>
            <span class="tea-category-showcase__label">适合场景</span>
            <p>{{ cat.scenarios.join(' / ') }}</p>
          </div>
          <div>
            <span class="tea-category-showcase__label">代表茶</span>
            <p>{{ (cat.previewNames?.length ? cat.previewNames : cat.examples).join(' / ') }}</p>
          </div>
        </div>
      </template>

      <template v-else>
        <div class="tea-category-showcase__shortcut">
          <span>{{ cat.tags.join(' / ') }}</span>
          <p>{{ cat.recommendation }}</p>
        </div>
      </template>

      <span class="tea-category-showcase__link">{{ linkText || cat.linkText || '查看该类茶品' }}</span>
    </router-link>
  </div>
</template>

<script setup>
defineProps({
  categories: {
    type: Array,
    default: () => []
  },
  loading: {
    type: Boolean,
    default: false
  },
  variant: {
    type: String,
    default: 'shortcut'
  },
  linkText: {
    type: String,
    default: ''
  }
})
</script>

<style scoped>
.tea-category-showcase {
  display: grid;
  gap: 24px;
}

.tea-category-showcase--shortcut {
  grid-template-columns: repeat(3, minmax(0, 1fr));
}

.tea-category-showcase--guide {
  grid-template-columns: repeat(2, minmax(0, 1fr));
}

.tea-category-showcase__card {
  min-height: 0;
  display: flex;
  flex-direction: column;
  justify-content: space-between;
  gap: 18px;
  padding: 28px;
  border-radius: var(--radius-md);
  border: 1px solid rgba(216, 210, 196, 0.92);
  border-top: 3px solid var(--card-accent);
  background:
    radial-gradient(circle at top right, color-mix(in srgb, var(--card-accent) 14%, #f8f4ec 86%), transparent 38%),
    linear-gradient(180deg, rgba(248, 244, 236, 0.98), rgba(245, 241, 232, 0.96));
  color: var(--color-wood);
  box-shadow: var(--shadow-sm);
  transition: transform var(--transition-base), box-shadow var(--transition-base), border-color var(--transition-base);
}

.tea-category-showcase__card:hover {
  transform: translateY(-4px);
  box-shadow: var(--shadow-md);
  border-color: color-mix(in srgb, var(--card-accent) 36%, #d8d2c4 64%);
}

.tea-category-showcase__head {
  display: flex;
  gap: 18px;
  align-items: flex-start;
}

.tea-category-showcase__mark {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  width: 54px;
  height: 54px;
  flex: 0 0 54px;
  border: 1px solid color-mix(in srgb, var(--card-accent) 42%, #d8d2c4 58%);
  background: color-mix(in srgb, var(--card-accent) 10%, #f8f4ec 90%);
  color: var(--card-accent);
  font-family: var(--font-display);
  font-size: 20px;
}

.tea-category-showcase__title strong {
  display: block;
  font-family: var(--font-display);
  font-size: 36px;
  font-weight: 600;
  letter-spacing: 0.08em;
}

.tea-category-showcase__title p,
.tea-category-showcase__recommendation,
.tea-category-showcase__meta p,
.tea-category-showcase__shortcut p {
  margin: 0;
  color: var(--color-ink-soft);
  font-size: 14px;
  line-height: 1.85;
}

.tea-category-showcase__title p {
  margin-top: 10px;
}

.tea-category-showcase__chips {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
}

.tea-category-showcase__chips span {
  display: inline-flex;
  align-items: center;
  min-height: 30px;
  padding: 0 12px;
  border-radius: 999px;
  background: color-mix(in srgb, var(--card-accent) 10%, #f8f4ec 90%);
  border: 1px solid color-mix(in srgb, var(--card-accent) 22%, #d8d2c4 78%);
  color: var(--card-accent);
  font-size: 12px;
  letter-spacing: 0.1em;
}

.tea-category-showcase__meta {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 14px;
}

.tea-category-showcase__label,
.tea-category-showcase__shortcut span {
  display: inline-block;
  margin-bottom: 8px;
  color: var(--color-gold);
  font-size: 11px;
  letter-spacing: 0.18em;
  text-transform: uppercase;
}

.tea-category-showcase__shortcut {
  margin-top: auto;
}

.tea-category-showcase__link {
  display: inline-flex;
  align-items: center;
  gap: 8px;
  color: var(--color-green);
  font-size: 13px;
  letter-spacing: 0.14em;
}

.tea-category-showcase__link::after {
  content: "→";
}

@media (max-width: 1024px) {
  .tea-category-showcase--shortcut,
  .tea-category-showcase--guide {
    grid-template-columns: 1fr;
  }

  .tea-category-showcase__title strong {
    font-size: 30px;
  }

  .tea-category-showcase__meta {
    grid-template-columns: 1fr;
  }
}
</style>
