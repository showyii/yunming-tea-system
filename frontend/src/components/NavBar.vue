<!--
  导航栏组件 NavBar.vue
  固定在页面顶部的导航栏，包含：
  - 品牌 Logo 和名称
  - 主导航链接（首页、分类、商品、包间、活动）
  - 用户操作区（登录/注册 或 购物车/订单/收藏/用户中心）
  使用 sticky 定位，滚动时始终可见
-->
<template>
  <!-- 导航栏容器 -->
  <header class="navbar">
    <div class="container nav-inner">
      <!-- 品牌区域：点击回到首页 -->
      <router-link to="/" class="brand">
        <span class="brand-icon">茗</span>
        <span class="brand-copy">
          <strong>云茗茶馆</strong>
          <small>YUNMING TEA HOUSE</small>
        </span>
      </router-link>

      <!-- 主导航菜单 -->
      <nav class="nav-links">
        <router-link to="/">首页</router-link>
        <router-link to="/categories">茶品分类</router-link>
        <router-link to="/products">全部茶品</router-link>
        <router-link to="/rooms">包间预约</router-link>
        <router-link to="/activities">茶事活动</router-link>
      </nav>

      <!-- 用户操作区域 -->
      <div class="nav-actions">
        <!-- 已登录状态：显示功能入口 -->
        <template v-if="user">
          <router-link to="/cart" class="action-link">
            <el-icon><ShoppingCart /></el-icon>
            <span>购物车</span>
          </router-link>
          <router-link to="/orders" class="action-link">
            <el-icon><Document /></el-icon>
            <span>订单</span>
          </router-link>
          <router-link to="/favorites" class="action-link">
            <el-icon><Star /></el-icon>
            <span>收藏</span>
          </router-link>
          <router-link to="/user-center" class="action-button action-button-primary">
            <el-icon><User /></el-icon>
            <span>{{ user.username }}</span>
          </router-link>
          <!-- 退出登录链接 -->
          <a href="#" class="logout-link" @click.prevent="logout">退出</a>
        </template>
        <!-- 未登录状态：显示登录/注册入口 -->
        <template v-else>
          <router-link to="/login" class="action-link">登录</router-link>
          <router-link to="/register" class="action-button action-button-primary">注册</router-link>
        </template>
      </div>
    </div>
  </header>
</template>

<script setup>
// Vue 3 Composition API：onMounted 生命周期钩子，ref 响应式引用
import { onMounted, ref } from 'vue'
// useRouter 用于编程式导航
import { useRouter } from 'vue-router'

const router = useRouter() // 获取路由实例
const user = ref(null) // 当前登录用户信息（null 表示未登录）

/**
 * 从 localStorage 加载用户登录状态
 * 页面刷新后通过此方法恢复登录状态
 */
const loadUser = () => {
  const token = localStorage.getItem('token') // 检查是否有 Token
  user.value = token
    ? { username: localStorage.getItem('username') || '茶友' } // 有 Token：读取用户名，兜底"茶友"
    : null // 无 Token：设为 null（未登录）
}

/**
 * 退出登录
 * 清除本地存储的凭证，重置用户状态，跳回首页
 */
const logout = () => {
  localStorage.removeItem('token') // 清除 JWT Token
  localStorage.removeItem('username') // 清除缓存的用户名
  user.value = null // 将用户状态置空，触发模板重新渲染
  router.push('/') // 跳转到首页
}

// 组件挂载完成后立即加载用户状态
onMounted(loadUser)
</script>

<style scoped>
/* ===== 导航栏容器 ===== */
.navbar {
  position: sticky; /* 粘性定位：滚动时固定 */
  top: 0;
  z-index: 100; /* 确保在最上层 */
  background: rgba(251, 247, 239, 0.86); /* 半透明背景 */
  backdrop-filter: blur(14px); /* 背景模糊效果（毛玻璃） */
  border-bottom: 1px solid rgba(204, 188, 161, 0.38);
}

/* 底部装饰线 */
.navbar::after {
  content: "";
  position: absolute;
  left: 0;
  right: 0;
  bottom: 0;
  height: 1px;
  background: linear-gradient(90deg, transparent, rgba(140, 107, 63, 0.3), transparent);
}

.nav-inner {
  min-height: var(--nav-height); /* 导航栏高度 CSS 变量 */
  display: grid;
  grid-template-columns: auto 1fr auto; /* 三列布局：品牌 | 导航 | 操作 */
  align-items: center;
  gap: 28px;
}

/* ===== 品牌 Logo ===== */
.brand {
  display: inline-flex;
  align-items: center;
  gap: 14px;
  min-width: 220px;
}

.brand-icon {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  width: 42px;
  height: 42px;
  border: 1px solid rgba(159, 83, 24, 0.42);
  border-radius: 12px 12px 12px 4px; /* 不规则圆角，更有特色 */
  color: var(--color-red);
  font-family: var(--font-display);
  font-size: 18px;
  letter-spacing: 0.16em;
  background: rgba(255, 250, 242, 0.9);
}

.brand-copy {
  display: flex;
  flex-direction: column;
  gap: 3px;
}

.brand-copy strong {
  color: var(--color-wood);
  font-family: var(--font-display);
  font-size: 22px;
  font-weight: 600;
  letter-spacing: 0.15em;
}

.brand-copy small {
  color: var(--color-ink-muted);
  font-size: 10px;
  letter-spacing: 0.32em;
}

/* ===== 导航链接 ===== */
.nav-links {
  display: flex;
  justify-content: center;
  gap: 34px;
}

.nav-links a {
  position: relative;
  padding: 8px 0;
  color: var(--color-ink-soft);
  font-size: 15px;
  letter-spacing: 0.08em;
}

/* 下划线动画效果 */
.nav-links a::after {
  content: "";
  position: absolute;
  left: 50%;
  bottom: -2px;
  width: 0; /* 默认宽度为 0（隐藏） */
  height: 1px;
  background: var(--color-red);
  transform: translateX(-50%); /* 居中 */
  transition: width var(--transition-base); /* 宽度变化过渡动画 */
}

/* 悬停或激活时显示下划线 */
.nav-links a:hover,
.nav-links a.router-link-active {
  color: var(--color-wood);
}

.nav-links a:hover::after,
.nav-links a.router-link-active::after {
  width: 26px; /* 展开下划线 */
}

/* ===== 用户操作区 ===== */
.nav-actions {
  display: flex;
  align-items: center;
  justify-content: flex-end;
  gap: 16px;
}

.action-link,
.logout-link {
  display: inline-flex;
  align-items: center;
  gap: 6px;
  color: var(--color-ink-soft);
  font-size: 14px;
  letter-spacing: 0.04em;
}

.action-link:hover,
.logout-link:hover {
  color: var(--color-red); /* 悬停变红 */
}

/* 操作按钮通用样式 */
.action-button {
  display: inline-flex;
  align-items: center;
  gap: 6px;
  min-height: 38px;
  padding: 0 16px;
  border: 1px solid rgba(140, 107, 63, 0.2);
  background: rgba(255, 250, 242, 0.7);
  color: var(--color-wood);
}

.action-button:hover,
.action-button.router-link-active {
  border-color: rgba(159, 83, 24, 0.28);
  color: var(--color-red);
}

/* 主要操作按钮（注册/用户中心） */
.action-button-primary {
  border-color: rgba(159, 83, 24, 0.28);
  background: var(--color-red);
  color: #fffaf2;
}

.action-button-primary:hover,
.action-button-primary.router-link-active {
  color: #fffaf2;
  background: var(--color-red-soft);
  border-color: var(--color-red-soft);
}
</style>
