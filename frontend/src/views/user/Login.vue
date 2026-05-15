<template>
  <div class="page">
    <NavBar />

    <div class="container auth-page">
      <div class="auth-shell">
        <section class="auth-hero paper-panel">
          <div class="auth-hero__copy">
            <h1>回到茶友账户里，把上次看中的茶和约好的行程继续接上</h1>
            <p>登录之后，收藏、订单、预约和活动记录都会接着留在原处，你可以顺着上次停下来的地方继续慢慢看。</p>
          </div>

          <div class="auth-hero__aside">
            <strong>登录之后</strong>
            <p>你可以继续下单、查看雅间预约、确认活动报名，也可以回到个人中心整理资料与账户设置。</p>
          </div>
        </section>

        <section class="auth-card paper-panel">
          <div class="auth-header">
            <span class="seal-badge">入馆</span>
            <div>
              <h2>用户登录</h2>
              <p>使用你的茶友账号进入云茗茶馆</p>
            </div>
          </div>

          <el-form :model="form" :rules="rules" ref="formRef" label-position="top">
            <el-form-item label="用户名" prop="username">
              <el-input v-model="form.username" placeholder="请输入用户名" size="large" />
            </el-form-item>
            <el-form-item label="密码" prop="password">
              <el-input
                v-model="form.password"
                type="password"
                placeholder="请输入密码"
                size="large"
                @keyup.enter="submit"
              />
            </el-form-item>
            <el-alert
              v-if="errorMessage"
              :title="errorMessage"
              type="error"
              :closable="false"
              show-icon
              class="login-error-alert"
            />
            <el-button type="primary" size="large" @click="submit" :loading="loading" class="submit-btn">
              登录
            </el-button>
          </el-form>

          <p class="auth-link">还没有账号？<router-link to="/register">立即注册</router-link></p>
          <p class="auth-admin-tip">如果你要进入经营后台，请前往 <router-link to="/admin/login">管理员登录</router-link></p>
        </section>
      </div>
    </div>

    <FooterBar />
  </div>
</template>

<script setup>
import { reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import NavBar from '@/components/NavBar.vue'
import FooterBar from '@/components/FooterBar.vue'
import { authApi } from '@/api/auth'
import { resolveRequestErrorMessage } from '@/api/errorMessage'

const router = useRouter()
const formRef = ref(null)
const loading = ref(false)
const errorMessage = ref('')
const form = reactive({ username: '', password: '' })
const rules = {
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }]
}

const submit = async () => {
  const valid = await formRef.value.validate().catch(() => false)
  if (!valid) return
  errorMessage.value = ''
  loading.value = true
  try {
    const res = await authApi.login({ ...form })
    localStorage.setItem('token', res.token)
    localStorage.setItem('username', res.username)
    ElMessage.success('登录成功')
    router.push('/')
  } catch (error) {
    errorMessage.value = resolveRequestErrorMessage(error, '登录失败，请检查账号和密码')
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.page {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
}

.auth-page {
  flex: 1;
  padding-top: 28px;
}

.auth-shell {
  display: grid;
  grid-template-columns: minmax(0, 1.08fr) minmax(420px, 0.78fr);
  gap: 24px;
}

.auth-hero,
.auth-card {
  padding: 32px;
}

.auth-hero {
  display: grid;
  align-content: space-between;
  min-height: 520px;
  background:
    linear-gradient(135deg, rgba(255, 252, 246, 0.92), rgba(244, 237, 226, 0.92));
}

.auth-kicker {
  color: var(--color-red);
  font-size: 12px;
  letter-spacing: 0.32em;
}

.auth-hero__copy h1 {
  margin: 16px 0 14px;
  color: var(--color-ink);
  font-family: var(--font-display);
  font-size: 50px;
  line-height: 1.2;
  letter-spacing: 0.08em;
}

.auth-hero__copy p,
.auth-hero__aside p {
  color: var(--color-ink-soft);
  font-size: 16px;
  line-height: 1.9;
  margin: 0;
}

.auth-hero__aside {
  max-width: 420px;
  padding-top: 32px;
}

.auth-hero__aside strong {
  display: block;
  margin-bottom: 10px;
  color: var(--color-wood);
  font-family: var(--font-display);
  font-size: 24px;
}

.auth-card {
  background: rgba(255, 250, 242, 0.95);
}

.auth-header {
  display: flex;
  gap: 16px;
  align-items: center;
  margin-bottom: 26px;
}

.auth-header h2 {
  margin: 0 0 6px;
  color: var(--color-ink);
  font-family: var(--font-display);
  font-size: 28px;
  letter-spacing: 0.14em;
}

.auth-header p,
.auth-link {
  margin: 0;
  color: var(--color-ink-soft);
}

.submit-btn {
  width: 100%;
  min-height: 46px;
  margin-top: 8px;
  letter-spacing: 0.2em;
}

.login-error-alert {
  margin: 6px 0 14px;
}

.auth-link {
  margin-top: 22px;
  font-size: 13px;
}

.auth-admin-tip {
  margin: 12px 0 0;
  color: var(--color-ink-muted);
  font-size: 12px;
}
</style>
