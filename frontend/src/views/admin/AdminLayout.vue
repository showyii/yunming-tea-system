<template>
  <div class="admin-layout">
    <aside class="sidebar">
      <div class="sidebar-header">
        <div class="sidebar-seal">管</div>
        <div class="sidebar-copy">
          <router-link to="/admin/dashboard">云茗茶馆</router-link>
          <p>新中式经营后台</p>
        </div>
      </div>

      <el-menu
        :default-active="route.path"
        router
        class="sidebar-menu"
        background-color="transparent"
        text-color="#dcd2c1"
        active-text-color="#fffaf2"
      >
        <el-menu-item index="/admin/dashboard">
          <el-icon><DataAnalysis /></el-icon><span>数据概览</span>
        </el-menu-item>
        <el-menu-item index="/admin/users">
          <el-icon><UserFilled /></el-icon><span>用户管理</span>
        </el-menu-item>
        <el-menu-item index="/admin/categories">
          <el-icon><Grid /></el-icon><span>分类管理</span>
        </el-menu-item>
        <el-menu-item index="/admin/products">
          <el-icon><Goods /></el-icon><span>商品管理</span>
        </el-menu-item>
        <el-menu-item index="/admin/orders">
          <el-icon><Document /></el-icon><span>订单管理</span>
        </el-menu-item>
        <el-menu-item index="/admin/comments">
          <el-icon><ChatDotRound /></el-icon><span>评论管理</span>
        </el-menu-item>
        <el-menu-item index="/admin/rooms">
          <el-icon><HomeFilled /></el-icon><span>包间管理</span>
        </el-menu-item>
        <el-menu-item index="/admin/room-bookings">
          <el-icon><Calendar /></el-icon><span>预约管理</span>
        </el-menu-item>
        <el-menu-item index="/admin/activities">
          <el-icon><Tickets /></el-icon><span>活动管理</span>
        </el-menu-item>
        <el-menu-item index="/admin/activity-signups">
          <el-icon><List /></el-icon><span>报名管理</span>
        </el-menu-item>
      </el-menu>

      <div class="sidebar-footer">
        <router-link to="/" class="sidebar-home">返回前台首页</router-link>
        <el-button class="sidebar-logout" @click="doLogout">退出登录</el-button>
      </div>
    </aside>

    <main class="main-content">
      <div class="main-topbar">
        <div>
          <span class="main-topbar__eyebrow">Admin Workspace</span>
          <strong>{{ currentSection }}</strong>
        </div>
        <router-link to="/" class="main-topbar__link">查看前台首页</router-link>
      </div>
      <div class="main-frame">
        <router-view />
      </div>
    </main>
  </div>
</template>

<script setup>
import { useRoute, useRouter } from 'vue-router'
import { computed } from 'vue'
import { useAdminStore } from '@/stores/admin'

const router = useRouter()
const route = useRoute()
const adminStore = useAdminStore()

const sectionNames = {
  '/admin/dashboard': '经营数据总览',
  '/admin/users': '用户与会员管理',
  '/admin/categories': '茶类配置管理',
  '/admin/products': '商品资料管理',
  '/admin/orders': '订单履约管理',
  '/admin/comments': '评论内容管理',
  '/admin/rooms': '包间资料管理',
  '/admin/room-bookings': '包间预约管理',
  '/admin/activities': '茶事活动管理',
  '/admin/activity-signups': '活动报名管理'
}

const currentSection = computed(() => sectionNames[route.path] || '经营后台')

const doLogout = () => {
  adminStore.logout()
  router.push('/admin/login')
}
</script>

<style scoped>
.admin-layout {
  display: flex;
  min-height: 100vh;
}

.sidebar {
  width: 248px;
  flex-shrink: 0;
  display: flex;
  flex-direction: column;
  padding: 20px 16px;
  background:
    linear-gradient(180deg, #314d3d, #23372d 62%, #1d2e26);
  border-right: 1px solid rgba(140, 107, 63, 0.25);
  box-shadow: 10px 0 28px rgba(28, 24, 20, 0.08);
}

.sidebar-header {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 14px 10px 18px;
  border-bottom: 1px solid rgba(220, 210, 193, 0.12);
}

.sidebar-seal {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  width: 44px;
  height: 44px;
  border-radius: 12px 12px 10px 6px;
  background: rgba(159, 45, 32, 0.92);
  color: #fff8f1;
  font-family: var(--font-display);
  letter-spacing: 0.12em;
}

.sidebar-copy a {
  display: block;
  color: #fff8f1;
  font-family: var(--font-display);
  font-size: 20px;
  letter-spacing: 0.16em;
}

.sidebar-copy p {
  margin: 4px 0 0;
  color: rgba(234, 222, 204, 0.7);
  font-size: 11px;
  letter-spacing: 0.24em;
}

.sidebar-menu {
  flex: 1;
  margin-top: 18px;
}

.sidebar-menu :deep(.el-menu-item) {
  margin-bottom: 8px;
  min-height: 46px;
  border-radius: 12px;
  letter-spacing: 0.08em;
}

.sidebar-menu :deep(.el-menu-item:hover) {
  background: rgba(255, 250, 242, 0.08) !important;
}

.sidebar-menu :deep(.el-menu-item.is-active) {
  background:
    linear-gradient(90deg, rgba(159, 45, 32, 0.28), rgba(255, 250, 242, 0.06)) !important;
  border: 1px solid rgba(196, 175, 140, 0.18);
}

.sidebar-footer {
  padding-top: 12px;
  border-top: 1px solid rgba(220, 210, 193, 0.12);
}

.sidebar-home {
  display: block;
  margin-bottom: 12px;
  color: rgba(255, 248, 240, 0.78);
  font-size: 12px;
  letter-spacing: 0.18em;
  text-align: center;
}

.sidebar-logout {
  width: 100%;
  background: rgba(255, 250, 242, 0.1);
  border-color: rgba(220, 210, 193, 0.18);
  color: #fff8f1;
}

.sidebar-logout:hover {
  background: rgba(255, 250, 242, 0.16);
  color: #fff8f1;
}

.main-content {
  flex: 1;
  padding: 24px;
  background:
    linear-gradient(180deg, rgba(248, 244, 236, 0.72), rgba(245, 241, 232, 0.96));
  overflow-y: auto;
}

.main-topbar {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 16px;
  margin-bottom: 18px;
  padding: 0 6px;
}

.main-topbar__eyebrow {
  display: block;
  color: var(--color-red);
  font-size: 11px;
  letter-spacing: 0.24em;
  text-transform: uppercase;
}

.main-topbar strong {
  display: block;
  margin-top: 6px;
  color: var(--color-ink);
  font-family: var(--font-display);
  font-size: 28px;
  font-weight: 600;
  letter-spacing: 0.08em;
}

.main-topbar__link {
  color: var(--color-red);
  font-size: 13px;
  letter-spacing: 0.16em;
}

.main-frame {
  min-height: calc(100vh - 48px);
  padding: 28px;
  border: 1px solid rgba(140, 107, 63, 0.14);
  border-radius: 20px;
  background: rgba(255, 250, 242, 0.58);
  box-shadow: var(--shadow-sm);
}

@media (max-width: 960px) {
  .admin-layout {
    flex-direction: column;
  }

  .sidebar {
    width: 100%;
  }
}
</style>
