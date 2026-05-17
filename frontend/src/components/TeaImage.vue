<!--
  茶叶风格图片组件 TeaImage.vue
  用于统一显示商品/包间/活动/头像的图片
  特点：
  - 图片加载失败时显示带中文标记字的占位图
  - 不同 variant 类型有不同的占位背景色和字符（茶/轩/雅/茗）
  - 支持 slot overlay 在图片上覆盖额外内容
-->
<template>
  <!-- 图片容器，variant 决定占位图风格 -->
  <div class="tea-image" :class="[`tea-image--${variant}`]" :style="{ width, height }">
    <!-- 图片存在时正常显示 -->
    <img v-if="src" :src="src" :alt="alt" @error="onError" />
    <!-- 图片不存在或加载失败时显示中文占位符 -->
    <div v-else class="tea-image__placeholder">
      <span class="tea-image__char">{{ char }}</span>
    </div>
    <!-- 插槽：用于覆盖额外的内容（如标签、遮罩等） -->
    <slot name="overlay" />
  </div>
</template>

<script setup>
import { ref } from 'vue'

// 组件 Props 定义
const props = defineProps({
  src: { type: String, default: '' }, // 图片 URL
  alt: { type: String, default: '' }, // 图片 alt 属性
  variant: { type: String, default: 'tea' }, // 类型：tea | room | activity | avatar
  width: { type: String, default: '100%' }, // 容器宽度
  height: { type: String, default: '100%' } // 容器高度
})

const hasError = ref(false) // 图片加载是否出错

// 各 variant 对应的占位字符映射
const charMap = {
  tea: '茶', // 茶品
  room: '轩', // 包间
  activity: '雅', // 活动
  avatar: '茗' // 头像
}

// 当前 variant 对应的占位字符
const char = charMap[props.variant] || '茶'

/**
 * 图片加载失败时的回调
 * 设置 hasError 为 true 后，模板会显示占位图
 */
const onError = () => {
  hasError.value = true
}
</script>

<style scoped>
/* 图片容器：渐变背景模拟茶叶色调 */
.tea-image {
  position: relative;
  overflow: hidden;
  background:
    linear-gradient(135deg, #E8DCC8 0%, #F5F0E8 40%, #E0D4C0 100%);
}

/* 图片自适应容器 */
.tea-image img {
  width: 100%;
  height: 100%;
  object-fit: cover; /* 裁剪填充，保持比例 */
}

/* ---- 各 variant 的占位图背景纹理 ---- */
.tea-image--tea .tea-image__placeholder {
  background:
    radial-gradient(ellipse at 30% 30%, rgba(45, 90, 39, 0.08) 0%, transparent 50%),
    radial-gradient(ellipse at 70% 70%, rgba(107, 76, 59, 0.06) 0%, transparent 50%);
}

.tea-image--room .tea-image__placeholder {
  background:
    radial-gradient(ellipse at 50% 40%, rgba(107, 76, 59, 0.08) 0%, transparent 50%),
    radial-gradient(ellipse at 30% 70%, rgba(45, 90, 39, 0.05) 0%, transparent 50%);
}

.tea-image--activity .tea-image__placeholder {
  background:
    radial-gradient(ellipse at 40% 30%, rgba(196, 163, 90, 0.08) 0%, transparent 50%),
    radial-gradient(ellipse at 60% 70%, rgba(181, 52, 58, 0.05) 0%, transparent 50%);
}

.tea-image--avatar .tea-image__placeholder {
  background: radial-gradient(circle at 50% 40%, rgba(45, 90, 39, 0.12) 0%, rgba(45, 90, 39, 0.04) 100%);
  border-radius: 50%; /* 头像圆形 */
}

/* 占位容器：居中显示占位字符 */
.tea-image__placeholder {
  width: 100%;
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
  position: relative;
}

/* 占位容器的装饰边框 */
.tea-image__placeholder::before {
  content: '';
  position: absolute;
  inset: 12px; /* 内缩 12px */
  border: 1px solid rgba(196, 163, 90, 0.15);
  border-radius: 2px;
  pointer-events: none; /* 不阻挡交互 */
}

/* 头像占位图的圆形边框 */
.tea-image--avatar .tea-image__placeholder::before {
  inset: 4px;
  border-radius: 50%;
  border: 1px solid rgba(255, 255, 255, 0.3);
}

/* 占位中文字符：使用楷体，半透明显示 */
.tea-image__char {
  font-family: 'KaiTi', 'STKaiti', 'ZCOOL XiaoWei', serif;
  font-size: 3em;
  color: rgba(45, 90, 39, 0.18);
  font-weight: 900;
  letter-spacing: 2px;
  user-select: none; /* 不可选中 */
  pointer-events: none; /* 不响应交互 */
}
</style>
