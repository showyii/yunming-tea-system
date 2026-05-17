<!--
  注册页（Register.vue）
  云茗茶馆的用户注册页面。
  页面功能：
  - 左侧展示注册后的功能概览（品牌信息区）
  - 右侧提供注册表单：用户名、密码、手机号、邮箱（选填）
  - 注册成功后跳转到登录页
  - 提供登录链接
-->
<template>
  <div class="page">
    <!-- 页面顶部导航栏 -->
    <NavBar />

    <div class="container auth-page">
      <div class="auth-shell">
        <!-- ========== 左侧：品牌信息区，介绍注册后能做什么 ========== -->
        <section class="auth-hero paper-panel">
          <div class="auth-hero__copy">
            <h1>留下一份茶友身份，以后选茶、下单和赴约都会更方便</h1>
            <p>有了账号之后，常用资料、订单记录、雅间预约和茶事报名都会替你稳稳留住，下次再来就更顺手了。</p>
          </div>

          <div class="auth-hero__aside">
            <strong>注册之后</strong>
            <p>注册完成后，你就可以收藏喜欢的茶、查看订单、预约雅间，也能慢慢把选茶与到店相聚连在一起。</p>
          </div>
        </section>

        <!-- ========== 右侧：注册表单区 ========== -->
        <section class="auth-card paper-panel">
          <!-- 表单头部：印鉴图标 + 标题 -->
          <div class="auth-header">
            <span class="seal-badge">新客</span>
            <div>
              <h2>用户注册</h2>
              <p>填写基本信息，创建你的茶友账号</p>
            </div>
          </div>

          <!-- Element Plus 表单：用户名、密码、手机号、邮箱 -->
          <el-form :model="form" :rules="rules" ref="formRef" label-position="top">
            <!-- 用户名：3-50位字符 -->
            <el-form-item label="用户名" prop="username">
              <el-input v-model="form.username" placeholder="3-50 位字符" size="large" />
            </el-form-item>
            <!-- 密码：6-50位字符 -->
            <el-form-item label="密码" prop="password">
              <el-input v-model="form.password" type="password" placeholder="6-50 位字符" size="large" />
            </el-form-item>
            <!-- 手机号：必填 -->
            <el-form-item label="手机号" prop="phone">
              <el-input v-model="form.phone" placeholder="请输入手机号" size="large" />
            </el-form-item>
            <!-- 邮箱：选填 -->
            <el-form-item label="邮箱">
              <el-input v-model="form.email" placeholder="选填" size="large" />
            </el-form-item>
            <!-- 注册提交按钮 -->
            <el-button type="primary" size="large" @click="submit" :loading="loading" class="submit-btn">
              注册
            </el-button>
          </el-form>

          <!-- 底部链接：已有账号去登录 -->
          <p class="auth-link">已有账号？<router-link to="/login">去登录</router-link></p>
        </section>
      </div>
    </div>

    <!-- 页面底部页脚栏 -->
    <FooterBar />
  </div>
</template>

<script setup>
/**
 * 注册页脚本逻辑
 * 负责：表单校验、调用注册 API、成功后跳转登录页
 */
import { reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import NavBar from '@/components/NavBar.vue'
import FooterBar from '@/components/FooterBar.vue'
import { authApi } from '@/api/auth'

// ===== 路由与表单引用 =====
const router = useRouter()
const formRef = ref(null)          // 表单组件引用，用于调用 validate()
const loading = ref(false)         // 注册按钮加载状态

// ===== 表单数据（响应式对象） =====
const form = reactive({ username: '', password: '', phone: '', email: '' })

// ===== 表单校验规则 =====
const rules = {
  // 用户名：必填 + 长度限制 3-50 位
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }, { min: 3, max: 50, message: '3-50位', trigger: 'blur' }],
  // 密码：必填 + 长度限制 6-50 位
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }, { min: 6, max: 50, message: '6-50位', trigger: 'blur' }],
  // 手机号：必填
  phone: [{ required: true, message: '请输入手机号', trigger: 'blur' }]
}

/**
 * 提交注册表单
 * 1. 校验表单字段
 * 2. 调用注册 API
 * 3. 成功后提示并跳转到登录页
 */
const submit = async () => {
  const valid = await formRef.value.validate().catch(() => false)
  if (!valid) return  // 校验不通过，中止提交
  loading.value = true
  try {
    await authApi.register({ ...form })
    ElMessage.success('注册成功，请登录')
    router.push('/login')
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

/* 双列网格布局：左侧品牌信息 + 右侧注册表单 */
.auth-shell {
  display: grid;
  grid-template-columns: minmax(0, 1.08fr) minmax(440px, 0.8fr);
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
  min-height: 560px;
  background:
    linear-gradient(135deg, rgba(255, 252, 246, 0.92), rgba(244, 237, 226, 0.92));
}

.auth-kicker {
  color: var(--color-green);
  font-size: 12px;
  letter-spacing: 0.32em;
}

.auth-hero__copy h1 {
  margin: 16px 0 14px;
  color: var(--color-ink);
  font-family: var(--font-display);
  font-size: 48px;
  line-height: 1.22;
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

/* ===== 右侧注册表单面板 ===== */
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

/* 登录链接 */
.auth-link {
  margin-top: 22px;
  font-size: 13px;
}
</style>
