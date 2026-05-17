<!--
  AdminLogin.vue - 管理员登录页面
  用途：提供管理员身份验证入口，输入账号密码后登录进入管理后台。
  说明：使用 adminStore.login() 进行身份验证，登录成功后跳转到 /admin/dashboard。
  登录失败时显示错误提示信息。
-->
<template>
  <div class="admin-login-page">
    <div class="login-shell">
      <!-- ==================== 左侧品牌介绍区域 ==================== -->
      <section class="login-intro">
        <span class="intro-kicker">ADMIN CONSOLE</span>
        <h1>进入云茗茶馆经营后台</h1>
      </section>

      <!-- ==================== 右侧登录表单卡片 ==================== -->
      <section class="login-card paper-panel">
        <!-- 表单头部：印章标识 + 标题说明 -->
        <div class="login-header">
          <span class="seal-badge">后台</span>
          <div>
            <h2>管理登录</h2>
            <p>请输入管理员账号与密码，进入商品、订单、活动与空间管理后台。</p>
          </div>
        </div>

        <!-- 登录表单 -->
        <el-form ref="formRef" :model="form" :rules="rules" size="large">
          <!-- 账号输入 -->
          <el-form-item prop="username">
            <el-input v-model="form.username" placeholder="管理员账号" prefix-icon="User" />
          </el-form-item>
          <!-- 密码输入 -->
          <el-form-item prop="password">
            <el-input v-model="form.password" type="password" placeholder="密码" prefix-icon="Lock" show-password />
          </el-form-item>
          <!-- 登录失败时的错误提示 -->
          <el-alert
            v-if="errorMessage"
            :title="errorMessage"
            type="error"
            :closable="false"
            show-icon
            class="login-error-alert"
          />
          <!-- 登录按钮 -->
          <el-form-item>
            <el-button type="primary" class="login-btn" @click="doLogin" :loading="loading">登录</el-button>
          </el-form-item>
        </el-form>

        <!-- 底部操作链接 -->
        <div class="login-actions">
          <router-link to="/" class="back-home-link">返回首页</router-link>
          <span>管理员账号请在此页登录</span>
        </div>
      </section>
    </div>
  </div>
</template>

<script setup>
// ==================== 依赖导入 ====================
import { reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { useAdminStore } from '@/stores/admin'
import { resolveRequestErrorMessage } from '@/api/errorMessage'

// ==================== 路由与状态 ====================
const router = useRouter()
const adminStore = useAdminStore()

// ==================== 响应式数据 ====================
const loading = ref(false)         // 登录按钮的加载状态
const errorMessage = ref('')       // 登录失败时显示的错误消息

// 登录表单数据（双向绑定）
const form = reactive({ username: '', password: '' })

// 表单校验规则
const rules = {
  username: [{ required: true, message: '请输入账号', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }]
}

// ==================== 方法 ====================
// 执行登录操作
const doLogin = async () => {
  errorMessage.value = ''          // 先清空之前的错误信息
  loading.value = true
  try {
    // 调用 store 中的登录方法，发送认证请求
    await adminStore.login(form)
    ElMessage.success('登录成功')
    router.push('/admin/dashboard') // 登录成功后跳转到数据概览页
  } catch (error) {
    // 解析后端返回的错误信息并显示
    errorMessage.value = resolveRequestErrorMessage(error, '登录失败，请检查账号和密码')
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
/* ==================== 页面整体布局：居中显示 ==================== */
.admin-login-page {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 28px;
}

/* ==================== 登录外壳：左右两栏布局 ==================== */
.login-shell {
  width: min(1080px, 100%);
  display: grid;
  grid-template-columns: minmax(0, 1fr) minmax(380px, 0.72fr);
  gap: 28px;
  align-items: stretch;
}

/* ==================== 左侧品牌介绍样式 ==================== */
.login-intro {
  display: flex;
  flex-direction: column;
  justify-content: center;
  padding: 24px 8px;
}

.intro-kicker {
  color: var(--color-gold);
  font-size: 12px;
  letter-spacing: 0.32em;
}

.login-intro h1 {
  margin: 16px 0 14px;
  color: var(--color-ink);
  font-family: var(--font-display);
  font-size: 46px;
  line-height: 1.2;
  letter-spacing: 0.08em;
}

.login-intro p {
  max-width: 520px;
  color: var(--color-ink-soft);
  font-size: 16px;
  line-height: 1.85;
}

/* ==================== 右侧登录卡片样式 ==================== */
.login-card {
  padding: 30px;
}

/* 表单头部区域 */
.login-header {
  display: flex;
  gap: 16px;
  align-items: center;
  margin-bottom: 26px;
}

.login-header h2 {
  margin: 0 0 6px;
  color: var(--color-ink);
  font-family: var(--font-display);
  font-size: 28px;
  letter-spacing: 0.14em;
}

.login-header p {
  margin: 0;
  color: var(--color-ink-soft);
}

/* 登录按钮：全宽显示 */
.login-btn {
  width: 100%;
  min-height: 46px;
  letter-spacing: 0.2em;
}

/* 错误提示区域 */
.login-error-alert {
  margin-bottom: 18px;
}

/* 底部操作链接区 */
.login-actions {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 12px;
  margin-top: 8px;
  padding-top: 18px;
  border-top: 1px solid rgba(204, 188, 161, 0.22);
  color: var(--color-ink-muted);
  font-size: 12px;
  letter-spacing: 0.08em;
}

.back-home-link {
  color: var(--color-red);
  font-size: 13px;
  letter-spacing: 0.14em;
}

/* ==================== 响应式：小屏幕时单栏布局 ==================== */
@media (max-width: 900px) {
  .login-shell {
    grid-template-columns: 1fr;
  }

  .login-intro h1 {
    font-size: 34px;
  }
}
</style>
