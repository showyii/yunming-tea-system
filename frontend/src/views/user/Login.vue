<!--
  登录页（Login.vue）
  云茗茶馆的用户登录页面。
  页面功能：
  - 左侧展示登录后的功能概览（品牌信息区）
  - 右侧提供用户名+密码登录表单
  - 登录成功后存储 token 和 username 到 localStorage 并跳转到首页
  - 提供注册链接和管理员登录入口
-->
<template>
  <div class="page">
    <!-- 页面顶部导航栏 -->
    <NavBar />

    <div class="container auth-page">
      <div class="auth-shell">
        <!-- ========== 左侧：品牌信息区，介绍登录后能做什么 ========== -->
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

        <!-- ========== 右侧：登录表单区 ========== -->
        <section class="auth-card paper-panel">
          <!-- 表单头部：印鉴图标 + 标题 -->
          <div class="auth-header">
            <span class="seal-badge">入馆</span>
            <div>
              <h2>用户登录</h2>
              <p>使用你的茶友账号进入云茗茶馆</p>
            </div>
          </div>

          <!-- Element Plus 表单：用户名 + 密码 -->
          <el-form :model="form" :rules="rules" ref="formRef" label-position="top">
            <!-- 用户名输入框 -->
            <el-form-item label="用户名" prop="username">
              <el-input v-model="form.username" placeholder="请输入用户名" size="large" />
            </el-form-item>
            <!-- 密码输入框：支持回车键快捷提交 -->
            <el-form-item label="密码" prop="password">
              <el-input
                v-model="form.password"
                type="password"
                placeholder="请输入密码"
                size="large"
                @keyup.enter="submit"
              />
            </el-form-item>
            <!-- 登录错误提示：仅在 errorMessage 有值时显示 -->
            <el-alert
              v-if="errorMessage"
              :title="errorMessage"
              type="error"
              :closable="false"
              show-icon
              class="login-error-alert"
            />
            <!-- 登录提交按钮 -->
            <el-button type="primary" size="large" @click="submit" :loading="loading" class="submit-btn">
              登录
            </el-button>
          </el-form>

          <!-- 底部链接：注册页和管理员登录入口 -->
          <p class="auth-link">还没有账号？<router-link to="/register">立即注册</router-link></p>
          <p class="auth-admin-tip">如果你要进入经营后台，请前往 <router-link to="/admin/login">管理员登录</router-link></p>
        </section>
      </div>
    </div>

    <!-- 页面底部页脚栏 -->
    <FooterBar />
  </div>
</template>

<script setup>
/**
 * 登录页脚本逻辑
 * 负责：表单校验、调用登录 API、存储登录状态、跳转首页
 */
import { reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import NavBar from '@/components/NavBar.vue'
import FooterBar from '@/components/FooterBar.vue'
import { authApi } from '@/api/auth'
import { resolveRequestErrorMessage } from '@/api/errorMessage'

// ===== 路由与表单引用 =====
const router = useRouter()
const formRef = ref(null)          // 表单组件引用，用于调用 validate()
const loading = ref(false)         // 登录按钮加载状态
const errorMessage = ref('')       // 登录错误消息

// ===== 表单数据（响应式对象） =====
const form = reactive({ username: '', password: '' })

// ===== 表单校验规则 =====
const rules = {
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }]
}

/**
 * 提交登录表单
 * 1. 校验表单字段
 * 2. 调用登录 API
 * 3. 成功后将 token 和 username 存入 localStorage
 * 4. 跳转到首页
 */
const submit = async () => {
  const valid = await formRef.value.validate().catch(() => false)
  if (!valid) return  // 校验不通过，中止提交
  errorMessage.value = ''
  loading.value = true
  try {
    const res = await authApi.login({ ...form })
    // 将登录凭据持久化到本地存储
    localStorage.setItem('token', res.token)
    localStorage.setItem('username', res.username)
    ElMessage.success('登录成功')
    router.push('/')
  } catch (error) {
    // 解析错误消息并显示，提供友好的中文提示
    errorMessage.value = resolveRequestErrorMessage(error, '登录失败，请检查账号和密码')
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
/* ===== 页面整体布局 ===== */
.page {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
}

/* 认证页主区域：flex 填充剩余空间 */
.auth-page {
  flex: 1;
  padding-top: 28px;
}

/* 双列网格布局：左侧品牌信息 + 右侧登录表单 */
.auth-shell {
  display: grid;
  grid-template-columns: minmax(0, 1.08fr) minmax(420px, 0.78fr);
  gap: 24px;
}

/* 两个面板的通用内边距 */
.auth-hero,
.auth-card {
  padding: 32px;
}

/* ===== 左侧品牌信息面板 ===== */
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

/* 左侧面板下方的说明区域 */
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

/* ===== 右侧登录表单面板 ===== */
.auth-card {
  background: rgba(255, 250, 242, 0.95);
}

/* 表单头部样式 */
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

/* 提交按钮：全宽、最小高度46px */
.submit-btn {
  width: 100%;
  min-height: 46px;
  margin-top: 8px;
  letter-spacing: 0.2em;
}

/* 登录错误提示样式 */
.login-error-alert {
  margin: 6px 0 14px;
}

/* 注册链接 */
.auth-link {
  margin-top: 22px;
  font-size: 13px;
}

/* 管理员入口提示 */
.auth-admin-tip {
  margin: 12px 0 0;
  color: var(--color-ink-muted);
  font-size: 12px;
}
</style>
