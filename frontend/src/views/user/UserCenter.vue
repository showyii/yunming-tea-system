<template>
  <div class="page">
    <NavBar />

    <div class="container center-page">
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

      <section class="desktop-section">
        <div class="center-grid">
          <section class="card paper-panel">
            <div class="card-header">
              <h3>个人资料</h3>
              <p>把常用信息先写好，往后买茶、预约与到店时都会更方便一些。</p>
            </div>

            <el-form :model="profile" label-width="80px" :disabled="profiling">
              <el-form-item label="昵称">
                <el-input v-model="profile.nickname" placeholder="给自己起个茶名" />
              </el-form-item>
              <el-form-item label="性别">
                <el-radio-group v-model="profile.gender">
                  <el-radio :label="0">未知</el-radio>
                  <el-radio :label="1">男</el-radio>
                  <el-radio :label="2">女</el-radio>
                </el-radio-group>
              </el-form-item>
              <el-form-item label="生日">
                <el-date-picker v-model="profile.birthday" type="date" placeholder="选择生日" value-format="YYYY-MM-DD" />
              </el-form-item>
              <el-form-item label="地址">
                <el-input v-model="profile.address" placeholder="你的地址" />
              </el-form-item>
              <el-form-item label="签名">
                <el-input v-model="profile.signature" type="textarea" :rows="3" placeholder="写句茶语..." />
              </el-form-item>
              <el-form-item>
                <el-button type="primary" @click="saveProfile" :loading="profiling">保存资料</el-button>
              </el-form-item>
            </el-form>
          </section>

          <section class="card paper-panel">
            <div class="card-header">
              <h3>修改密码</h3>
              <p>更新登录密码以保护账户安全，避免订单、预约与活动记录被误用。</p>
            </div>

            <el-form :model="pwdForm" :rules="pwdRules" ref="pwdRef" label-width="80px">
              <el-form-item label="原密码" prop="oldPassword">
                <el-input v-model="pwdForm.oldPassword" type="password" placeholder="请输入原密码" />
              </el-form-item>
              <el-form-item label="新密码" prop="newPassword">
                <el-input v-model="pwdForm.newPassword" type="password" placeholder="6-50 位新密码" />
              </el-form-item>
              <el-form-item label="确认密码" prop="confirmPassword">
                <el-input v-model="pwdForm.confirmPassword" type="password" placeholder="再次输入新密码" />
              </el-form-item>
              <el-form-item>
                <el-button type="danger" @click="changePwd" :loading="pwding">修改密码</el-button>
              </el-form-item>
            </el-form>
          </section>
        </div>
      </section>
    </div>

    <FooterBar />
  </div>
</template>

<script setup>
import { onMounted, reactive, ref } from 'vue'
import { ElMessage } from 'element-plus'
import NavBar from '@/components/NavBar.vue'
import FooterBar from '@/components/FooterBar.vue'
import { authApi } from '@/api/auth'

const profile = reactive({
  nickname: '', avatar: '', gender: 0, birthday: '', address: '', signature: ''
})
const profiling = ref(false)
const pwding = ref(false)
const pwdRef = ref(null)

const pwdForm = reactive({ oldPassword: '', newPassword: '', confirmPassword: '' })
const pwdRules = {
  oldPassword: [{ required: true, message: '请输入原密码', trigger: 'blur' }],
  newPassword: [{ required: true, min: 6, message: '6-50位', trigger: 'blur' }],
  confirmPassword: [
    { required: true, message: '请确认新密码', trigger: 'blur' },
    { validator: (r, v, cb) => (v === pwdForm.newPassword ? cb() : cb(new Error('两次密码不一致'))), trigger: 'blur' }
  ]
}

const loadProfile = async () => {
  try {
    const data = await authApi.getProfile()
    profile.nickname = data.nickname || ''
    profile.gender = data.gender ?? 0
    profile.birthday = data.birthday || ''
    profile.address = data.address || ''
    profile.signature = data.signature || ''
  } catch (e) {
    // handled
  }
}

const saveProfile = async () => {
  profiling.value = true
  try {
    await authApi.updateProfile({ ...profile })
    ElMessage.success('资料已保存')
  } finally {
    profiling.value = false
  }
}

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
    pwdForm.oldPassword = ''
    pwdForm.newPassword = ''
    pwdForm.confirmPassword = ''
  } finally {
    pwding.value = false
  }
}

onMounted(loadProfile)
</script>

<style scoped>
.page {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
}

.center-page {
  flex: 1;
  padding-top: 28px;
}

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
