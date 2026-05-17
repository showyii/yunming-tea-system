<!--
  个人中心页（UserCenter.vue）
  云茗茶馆的用户个人中心页面，管理个人资料和账户安全。
  页面功能：
  - 顶部品牌说明区
  - 个人资料表单：昵称、性别、生日、地址、签名
  - 修改密码表单：原密码、新密码、确认密码
  - 表单提交与校验
-->
<template>
  <div class="page">
    <!-- 页面顶部导航栏 -->
    <NavBar />

    <div class="container center-page">
      <!-- ========== 顶部：品牌说明区 ========== -->
      <section class="center-hero paper-panel">
        <div class="center-hero__copy">
          <h1>把自己的茶友资料安顿在这里，之后下单与赴约都会更从容</h1>
          <p>昵称、地址、签名这些常用信息都可以先整理好。往后无论买茶、预约还是报名茶事，都会省心一些。</p>
        </div>

        <div class="center-hero__aside">
          <strong>账户说明</strong>
          <p>把常用资料补充完整后，后面下单、收货和到店接待都会更顺。改好以后，也可以继续回到订单、活动或雅间页接着安排。</p>
        </div>
      </section>

      <!-- ========== 双列表单区 ========== -->
      <section class="desktop-section">
        <div class="center-grid">
          <!-- 左侧：个人资料表单 -->
          <section class="card paper-panel">
            <div class="card-header">
              <h3>个人资料</h3>
              <p>把常用信息先写好，往后买茶、预约与到店时都会更方便一些。</p>
            </div>

            <el-form :model="profile" label-width="80px" :disabled="profiling">
              <!-- 昵称 -->
              <el-form-item label="昵称">
                <el-input v-model="profile.nickname" placeholder="给自己起个茶名" />
              </el-form-item>
              <!-- 性别：单选组 -->
              <el-form-item label="性别">
                <el-radio-group v-model="profile.gender">
                  <el-radio :label="0">未知</el-radio>
                  <el-radio :label="1">男</el-radio>
                  <el-radio :label="2">女</el-radio>
                </el-radio-group>
              </el-form-item>
              <!-- 生日：日期选择器 -->
              <el-form-item label="生日">
                <el-date-picker v-model="profile.birthday" type="date" placeholder="选择生日" value-format="YYYY-MM-DD" />
              </el-form-item>
              <!-- 地址 -->
              <el-form-item label="地址">
                <el-input v-model="profile.address" placeholder="你的地址" />
              </el-form-item>
              <!-- 个性签名：多行文本 -->
              <el-form-item label="签名">
                <el-input v-model="profile.signature" type="textarea" :rows="3" placeholder="写句茶语..." />
              </el-form-item>
              <!-- 保存按钮 -->
              <el-form-item>
                <el-button type="primary" @click="saveProfile" :loading="profiling">保存资料</el-button>
              </el-form-item>
            </el-form>
          </section>

          <!-- 右侧：修改密码表单 -->
          <section class="card paper-panel">
            <div class="card-header">
              <h3>修改密码</h3>
              <p>更新登录密码以保护账户安全，避免订单、预约与活动记录被误用。</p>
            </div>

            <el-form :model="pwdForm" :rules="pwdRules" ref="pwdRef" label-width="80px">
              <!-- 原密码 -->
              <el-form-item label="原密码" prop="oldPassword">
                <el-input v-model="pwdForm.oldPassword" type="password" placeholder="请输入原密码" />
              </el-form-item>
              <!-- 新密码：6-50位 -->
              <el-form-item label="新密码" prop="newPassword">
                <el-input v-model="pwdForm.newPassword" type="password" placeholder="6-50 位新密码" />
              </el-form-item>
              <!-- 确认密码：需与新密码一致 -->
              <el-form-item label="确认密码" prop="confirmPassword">
                <el-input v-model="pwdForm.confirmPassword" type="password" placeholder="再次输入新密码" />
              </el-form-item>
              <!-- 修改密码按钮 -->
              <el-form-item>
                <el-button type="danger" @click="changePwd" :loading="pwding">修改密码</el-button>
              </el-form-item>
            </el-form>
          </section>
        </div>
      </section>
    </div>

    <!-- 页面底部页脚栏 -->
    <FooterBar />
  </div>
</template>

<script setup>
/**
 * 个人中心页脚本逻辑
 * 负责：加载用户资料、保存资料、修改密码
 */
import { onMounted, reactive, ref } from 'vue'
import { ElMessage } from 'element-plus'
import NavBar from '@/components/NavBar.vue'
import FooterBar from '@/components/FooterBar.vue'
import { authApi } from '@/api/auth'

// ===== 个人资料相关 =====
const profile = reactive({
  nickname: '',    // 昵称
  avatar: '',      // 头像（预留字段）
  gender: 0,       // 性别：0=未知 1=男 2=女
  birthday: '',    // 生日（YYYY-MM-DD）
  address: '',     // 地址
  signature: ''    // 个性签名
})
const profiling = ref(false)  // 保存资料按钮加载状态

// ===== 修改密码相关 =====
const pwding = ref(false)     // 修改密码按钮加载状态
const pwdRef = ref(null)      // 密码表单引用

// 密码表单数据
const pwdForm = reactive({ oldPassword: '', newPassword: '', confirmPassword: '' })

// 密码表单校验规则
const pwdRules = {
  oldPassword: [{ required: true, message: '请输入原密码', trigger: 'blur' }],
  newPassword: [{ required: true, min: 6, message: '6-50位', trigger: 'blur' }],
  confirmPassword: [
    { required: true, message: '请确认新密码', trigger: 'blur' },
    // 自定义校验：确认密码必须与新密码一致
    { validator: (r, v, cb) => (v === pwdForm.newPassword ? cb() : cb(new Error('两次密码不一致'))), trigger: 'blur' }
  ]
}

/**
 * 加载用户个人资料
 * 从 API 获取并填充到 profile 对象中
 */
const loadProfile = async () => {
  try {
    const data = await authApi.getProfile()
    profile.nickname = data.nickname || ''
    profile.gender = data.gender ?? 0
    profile.birthday = data.birthday || ''
    profile.address = data.address || ''
    profile.signature = data.signature || ''
  } catch (e) {
    // 请求拦截器已统一处理错误提示
  }
}

/**
 * 保存个人资料
 * 将当前 profile 数据提交到后端
 */
const saveProfile = async () => {
  profiling.value = true
  try {
    await authApi.updateProfile({ ...profile })
    ElMessage.success('资料已保存')
  } finally {
    profiling.value = false
  }
}

/**
 * 修改密码
 * 校验表单后，调用 API 提交新旧密码，成功后清空表单
 */
const changePwd = async () => {
  const valid = await pwdRef.value.validate().catch(() => false)
  if (!valid) return
  pwding.value = true
  try {
    await authApi.changePassword({
      oldPassword: pwdForm.oldPassword,
      newPassword: pwdForm.newPassword
    })
    ElMessage.success('密码已修改')
    // 修改成功后清空表单
    pwdForm.oldPassword = ''
    pwdForm.newPassword = ''
    pwdForm.confirmPassword = ''
  } finally {
    pwding.value = false
  }
}

/**
 * 组件挂载后：加载用户资料
 */
onMounted(loadProfile)
</script>

<style scoped>
/* ===== 页面整体布局 ===== */
.page {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
}

.center-page {
  flex: 1;
  padding-top: 28px;
}

/* ===== 顶部品牌说明面板 ===== */
.center-hero {
  display: grid;
  grid-template-columns: minmax(0, 1.42fr) minmax(320px, 0.78fr);
  gap: 28px;
  padding: 34px 36px;
  align-items: center;
}

.center-hero__eyebrow {
  color: var(--color-red);
  font-size: 12px;
  letter-spacing: 0.32em;
  text-transform: uppercase;
}

.center-hero__copy h1 {
  margin: 12px 0 14px;
  color: var(--color-ink);
  font-family: var(--font-display);
  font-size: 48px;
  font-weight: 600;
  line-height: 1.2;
  letter-spacing: 0.06em;
}

.center-hero__copy p,
.center-hero__aside p {
  margin: 0;
  color: var(--color-ink-soft);
  font-size: 15px;
  line-height: 1.9;
}

.center-hero__aside {
  padding: 24px;
  background: rgba(255, 252, 246, 0.82);
  border: 1px solid rgba(204, 188, 161, 0.4);
}

.center-hero__aside strong {
  display: block;
  margin-bottom: 10px;
  color: var(--color-wood);
  font-family: var(--font-display);
  font-size: 24px;
  font-weight: 600;
  letter-spacing: 0.06em;
}

/* ===== 双列表单区 ===== */
.center-grid {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 24px;
}

.card {
  padding: 26px;
}

.card-header {
  margin-bottom: 20px;
}

.card-header h3 {
  margin: 10px 0 10px;
  color: var(--color-ink);
  font-family: var(--font-display);
  font-size: 30px;
  font-weight: 600;
  letter-spacing: 0.06em;
}

.card-header p {
  margin: 0;
  color: var(--color-ink-soft);
  line-height: 1.85;
}
</style>
