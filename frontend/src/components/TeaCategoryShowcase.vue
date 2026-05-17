<!--
  茶叶分类展示卡片组件 TeaCategoryShowcase.vue
  用于展示茶叶分类的引导卡片，支持两种展示模式：
  - shortcut：简洁模式，显示分类标记、名称和标签（用于首页快捷入口）
  - guide：详细模式，显示分类标记、名称、标签、推荐语、场景和代表茶品
  每个分类卡片有独特的强调色（accent），营造差异化视觉
-->
<template>
  <!-- 容器：variant 决定布局方式，v-loading 控制加载状态 -->
  <div class="tea-category-showcase" :class="`tea-category-showcase--${variant}`" v-loading="loading">
    <!-- 遍历分类列表，每个分类渲染一张卡片 -->
    <router-link
      v-for="cat in categories"
      :key="cat.id"
      :to="cat.href"
      class="tea-category-showcase__card"
      :style="{ '--card-accent': cat.accent || '#6f553a' }"
    >
      <!-- 卡片头部：标记字符 + 标题 -->
      <div class="tea-category-showcase__head">
        <span class="tea-category-showcase__mark">{{ cat.mark }}</span>
        <div class="tea-category-showcase__title">
          <strong>{{ cat.name }}</strong>
          <p>{{ variant === 'guide' ? cat.guideTitle : cat.description }}</p>
        </div>
      </div>

      <!-- guide 模式：显示详细标签、推荐、场景 -->
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

      <!-- shortcut 模式：简洁标签展示 -->
      <template v-else>
        <div class="tea-category-showcase__shortcut">
          <span>{{ cat.tags.join(' / ') }}</span>
          <p>{{ cat.recommendation }}</p>
        </div>
      </template>

      <!-- 查看链接 -->
      <span class="tea-category-showcase__link">{{ linkText || cat.linkText || '查看该类茶品' }}</span>
    </router-link>
  </div>
</template>

<script setup>
// 定义组件 Props
defineProps({
  categories: {
    type: Array, // 分类数据数组（需包含名称、标记、标签等字段）
    default: () => []
  },
  loading: {
    type: Boolean, // 是否显示加载状态
    default: false
  },
  variant: {
    type: String, // 展示模式：'shortcut'（简洁）/ 'guide'（详细）
    default: 'shortcut'
  },
  linkText: {
    type: String, // 自定义"查看"链接文本
    default: ''
  }
})
</script>

<style scoped>
/* 分类展示网格 */
.tea-category-showcase {
  display: grid;
  gap: 24px;
}

/* shortcut 模式：三列布局 */
.tea-category-showcase--shortcut {
  grid-template-columns: repeat(3, minmax(0, 1fr));
}

/* guide 模式：两列布局（每张卡片内容更多） */
.tea-category-showcase--guide {
  grid-template-columns: repeat(2, minmax(0, 1fr));
}

/* ===== 卡片样式 ===== */
.tea-category-showcase__card {
  min-height: 0;
  display: flex;
  flex-direction: column;
  justify-content: space-between;
  gap: 18px;
  padding: 28px;
  border-radius: var(--radius-md);
  border: 1px solid rgba(216, 210, 196, 0.92);
  /* 顶部彩色边框，颜色由 --card-accent 变量控制 */
  border-top: 3px solid var(--card-accent);
  /* 渐变背景：从右上角透出主题色 */
  background:
    radial-gradient(circle at top right, color-mix(in srgb, var(--card-accent) 14%, #f8f4ec 86%), transparent 38%),
    linear-gradient(180deg, rgba(248, 244, 236, 0.98), rgba(245, 241, 232, 0.96));
  color: var(--color-wood);
  box-shadow: var(--shadow-sm);
  transition: transform var(--transition-base), box-shadow var(--transition-base), border-color var(--transition-base);
}

/* 悬停效果：上浮 + 阴影加深 */
.tea-category-showcase__card:hover {
  transform: translateY(-4px);
  box-shadow: var(--shadow-md);
  border-color: color-mix(in srgb, var(--card-accent) 36%, #d8d2c4 64%);
}

/* ===== 卡片头部 ===== */
.tea-category-showcase__head {
  display: flex;
  gap: 18px;
  align-items: flex-start;
}

/* 标记字符（分类汉字） */
.tea-category-showcase__mark {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  width: 54px;
  height: 54px;
  flex: 0 0 54px; /* 固定尺寸 */
  border: 1px solid color-mix(in srgb, var(--card-accent) 42%, #d8d2c4 58%);
  background: color-mix(in srgb, var(--card-accent) 10%, #f8f4ec 90%);
  color: var(--card-accent);
  font-family: var(--font-display);
  font-size: 20px;
}

/* 分类名称 */
.tea-category-showcase__title strong {
  display: block;
  font-family: var(--font-display);
  font-size: 36px;
  font-weight: 600;
  letter-spacing: 0.08em;
}

/* 描述文字通用样式 */
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

/* ===== 标签气泡 ===== */
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
  border-radius: 999px; /* 胶囊形状 */
  background: color-mix(in srgb, var(--card-accent) 10%, #f8f4ec 90%);
  border: 1px solid color-mix(in srgb, var(--card-accent) 22%, #d8d2c4 78%);
  color: var(--card-accent);
  font-size: 12px;
  letter-spacing: 0.1em;
}

/* ===== 元数据区（场景 + 代表茶） ===== */
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
  margin-top: auto; /* 推到底部 */
}

/* ===== 查看链接 ===== */
.tea-category-showcase__link {
  display: inline-flex;
  align-items: center;
  gap: 8px;
  color: var(--color-green);
  font-size: 13px;
  letter-spacing: 0.14em;
}

/* 查看链接后的箭头 */
.tea-category-showcase__link::after {
  content: "→";
}

/* ===== 响应式：平板/手机端单列 ===== */
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
