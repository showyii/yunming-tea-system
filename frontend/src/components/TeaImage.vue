<template>
  <div class="tea-image" :class="[`tea-image--${variant}`]" :style="{ width, height }">
    <img v-if="src" :src="src" :alt="alt" @error="onError" />
    <div v-else class="tea-image__placeholder">
      <span class="tea-image__char">{{ char }}</span>
    </div>
    <slot name="overlay" />
  </div>
</template>

<script setup>
import { ref } from 'vue'

const props = defineProps({
  src: { type: String, default: '' },
  alt: { type: String, default: '' },
  variant: { type: String, default: 'tea' }, // tea | room | activity | avatar
  width: { type: String, default: '100%' },
  height: { type: String, default: '100%' }
})

const hasError = ref(false)

const charMap = {
  tea: '茶',
  room: '轩',
  activity: '雅',
  avatar: '茗'
}

const char = charMap[props.variant] || '茶'

const onError = () => {
  hasError.value = true
}
</script>

<style scoped>
.tea-image {
  position: relative;
  overflow: hidden;
  background:
    linear-gradient(135deg, #E8DCC8 0%, #F5F0E8 40%, #E0D4C0 100%);
}

.tea-image img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

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
  border-radius: 50%;
}

.tea-image__placeholder {
  width: 100%;
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
  position: relative;
}

.tea-image__placeholder::before {
  content: '';
  position: absolute;
  inset: 12px;
  border: 1px solid rgba(196, 163, 90, 0.15);
  border-radius: 2px;
  pointer-events: none;
}

.tea-image--avatar .tea-image__placeholder::before {
  inset: 4px;
  border-radius: 50%;
  border: 1px solid rgba(255, 255, 255, 0.3);
}

.tea-image__char {
  font-family: 'KaiTi', 'STKaiti', 'ZCOOL XiaoWei', serif;
  font-size: 3em;
  color: rgba(45, 90, 39, 0.18);
  font-weight: 900;
  letter-spacing: 2px;
  user-select: none;
  pointer-events: none;
}
</style>
