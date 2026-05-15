<template>
  <div class="admin-login-page">
    <div class="login-shell">
      <section class="login-intro">
        <span class="intro-kicker">ADMIN CONSOLE</span>
        <h1>进入云茗茶馆经营后台</h1>
      </section>

      <section class="login-card paper-panel">
        <div class="login-header">
          <span class="seal-badge">后台</span>
          <div>
            <h2>管理登录</h2>
            <p>请输入管理员账号与密码，进入商品、订单、活动与空间管理后台。</p>
          </div>
        </div>

        <el-form ref="formRef" :model="form" :rules="rules" size="large">
          <el-form-item prop="username">
            <el-input v-model="form.username" placeholder="管理员账号" prefix-icon="User" />
          </el-form-item>
          <el-form-item prop="password">
            <el-input v-model="form.password" type="password" placeholder="密码" prefix-icon="Lock" show-password />
          </el-form-item>
          <el-alert
            v-if="errorMessage"
            :title="errorMessage"
            type="error"
            :closable="false"
            show-icon
            class="login-error-alert"
          />
          <el-form-item>
            <el-button type="primary" class="login-btn" @click="doLogin" :loading="loading">登录</el-button>
          </el-form-item>
        </el-form>

        <div class="login-actions">
          <router-link to="/" class="back-home-link">返回首页</router-link>
          <span>管理员账号请在此页登录</span>
        </div>
      </section>
    </div>
  </div>
</template>

<script setup>
import { reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { useAdminStore } from '@/stores/admin'
import { resolveRequestErrorMessage } from '@/api/errorMessage'

const router = useRouter()
const adminStore = useAdminStore()
const loading = ref(false)
const errorMessage = ref('')
const form = reactive({ username: '', password: '' })
const rules = {
  username: [{ required: true, message: '请输入账号', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }]
}

const doLogin = async () => {
  errorMessage.value = ''
  loading.value = true
  try {
    await adminStore.login(form)
    ElMessage.success('登录成功')
    router.push('/admin/dashboard')
  } catch (error) {
    errorMessage.value = resolveRequestErrorMessage(error, '登录失败，请检查账号和密码')
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.admin-login-page {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 28px;
}

.login-shell {
  width: min(1080px, 100%);
  display: grid;
  grid-template-columns: minmax(0, 1fr) minmax(380px, 0.72fr);
  gap: 28px;
  align-items: stretch;
}

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

.login-card {
  padding: 30px;
}

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

.login-btn {
  width: 100%;
  min-height: 46px;
  letter-spacing: 0.2em;
}

.login-error-alert {
  margin-bottom: 18px;
}

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

@media (max-width: 900px) {
  .login-shell {
    grid-template-columns: 1fr;
  }

  .login-intro h1 {
    font-size: 34px;
  }
}
</style>
