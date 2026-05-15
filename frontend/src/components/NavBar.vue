<template>
  <header class="navbar">
    <div class="container nav-inner">
      <router-link to="/" class="brand">
        <span class="brand-icon">茗</span>
        <span class="brand-copy">
          <strong>云茗茶馆</strong>
          <small>YUNMING TEA HOUSE</small>
        </span>
      </router-link>

      <nav class="nav-links">
        <router-link to="/">首页</router-link>
        <router-link to="/categories">茶品分类</router-link>
        <router-link to="/products">全部茶品</router-link>
        <router-link to="/rooms">包间预约</router-link>
        <router-link to="/activities">茶事活动</router-link>
      </nav>

      <div class="nav-actions">
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
          <a href="#" class="logout-link" @click.prevent="logout">退出</a>
        </template>
        <template v-else>
          <router-link to="/login" class="action-link">登录</router-link>
          <router-link to="/register" class="action-button action-button-primary">注册</router-link>
        </template>
      </div>
    </div>
  </header>
</template>

<script setup>
import { onMounted, ref } from 'vue'
import { useRouter } from 'vue-router'

const router = useRouter()
const user = ref(null)

const loadUser = () => {
  const token = localStorage.getItem('token')
  user.value = token
    ? { username: localStorage.getItem('username') || '茶友' }
    : null
}

const logout = () => {
  localStorage.removeItem('token')
  localStorage.removeItem('username')
  user.value = null
  router.push('/')
}

onMounted(loadUser)
</script>

<style scoped>
.navbar {
  position: sticky;
  top: 0;
  z-index: 100;
  background: rgba(251, 247, 239, 0.86);
  backdrop-filter: blur(14px);
  border-bottom: 1px solid rgba(204, 188, 161, 0.38);
}

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
  min-height: var(--nav-height);
  display: grid;
  grid-template-columns: auto 1fr auto;
  align-items: center;
  gap: 28px;
}

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
  border-radius: 12px 12px 12px 4px;
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

.nav-links a::after {
  content: "";
  position: absolute;
  left: 50%;
  bottom: -2px;
  width: 0;
  height: 1px;
  background: var(--color-red);
  transform: translateX(-50%);
  transition: width var(--transition-base);
}

.nav-links a:hover,
.nav-links a.router-link-active {
  color: var(--color-wood);
}

.nav-links a:hover::after,
.nav-links a.router-link-active::after {
  width: 26px;
}

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
  color: var(--color-red);
}

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
