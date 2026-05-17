<!--
  包间预约页（Rooms.vue）
  云茗茶馆的馆内雅间列表与预约页面，帮助用户挑选和预订茶室空间。
  页面功能：
  - 顶部品牌区介绍雅间功能
  - 预订提示与活动入口
  - 雅间网格展示（2列）：图片、名称、类型、容量、设施、价格
  - 预约弹窗：选择日期、时间（开始+结束）、人数、备注
  - 实时费用计算
-->
<template>
  <div class="page">
    <!-- 页面顶部导航栏 -->
    <NavBar />

    <div class="container room-page">
      <!-- ========== 顶部：品牌介绍区 ========== -->
      <section class="inner-hero paper-panel">
        <div class="inner-hero__copy">
          <h1>挑一间合适的雅室，把喝茶、待客与相聚都安放下来</h1>
          <p>馆内雅室适合饮茶小坐，也适合待客会面、小型相聚与主题雅集。先看看每间房的容纳人数与设施，再决定合适的到店时段。</p>
        </div>

        <div class="inner-hero__aside">
          <strong>预订提示</strong>
          <p>建议先按人数选空间，再比较设施与每小时价格；若只是想找一间安静坐一坐的地方，也可以先从中小包间开始看起。</p>
          <router-link to="/activities" class="inner-hero__link">查看近期茶事活动</router-link>
        </div>
      </section>

      <!-- ========== 雅间列表区 ========== -->
      <section class="desktop-section">
        <div class="section-title section-title-left">
          <h2>馆内雅间</h2>
          <p>每间雅室都写明了人数、设施、价格与可约状态，方便你按来意慢慢比较，挑一间坐着舒服、待客也合适的空间。</p>
        </div>

        <div class="room-grid" v-loading="loading">
          <!-- 单个雅间卡片 -->
          <article v-for="room in rooms" :key="room.id" class="room-card paper-panel">
            <!-- 雅间封面图 + 类型角标 -->
            <div class="room-card__image">
              <img :src="resolveRoomImage(room, room.image)" :alt="room.name">
              <span class="room-card__type">{{ typeText(room.type) }}</span>
            </div>
            <!-- 雅间信息区 -->
            <div class="room-card__body">
              <div class="room-card__heading">
                <div>
                  <span class="room-card__eyebrow">馆内空间</span>
                  <h3>{{ room.name }}</h3>
                </div>
                <span class="room-card__capacity">容纳 {{ room.capacity }} 人</span>
              </div>

              <p class="room-card__desc">{{ room.description || '适合日常饮茶、待客会谈与轻量雅集的馆内空间。' }}</p>

              <!-- 设施与预约方式 -->
              <div class="room-card__meta">
                <span v-if="room.facilities">{{ room.facilities }}</span>
                <span>可按小时预约</span>
              </div>

              <!-- 底部：价格 + 预约按钮 -->
              <div class="room-card__bottom">
                <span class="room-card__price">¥{{ room.pricePerHour }} <small>/小时</small></span>
                <el-button type="primary" @click="openBook(room)" :disabled="room.status === 0">
                  {{ room.status === 0 ? '维护中' : '立即预约' }}
                </el-button>
              </div>
            </div>
          </article>
        </div>
      </section>
    </div>

    <!-- ========== 预约弹窗 ========== -->
    <el-dialog v-model="showBook" title="预约包间" width="520px">
      <el-form :model="form" :rules="rules" ref="formRef" label-width="80px">
        <!-- 包间名称（只读显示） -->
        <el-form-item label="包间">{{ selectedRoom?.name }}</el-form-item>
        <!-- 预约日期：选择器，限制不可选过去的日期 -->
        <el-form-item label="日期" prop="bookingDate">
          <el-date-picker
            v-model="form.bookingDate"
            type="date"
            placeholder="选择日期"
            :disabled-date="disabledDate"
            value-format="YYYY-MM-DD"
            style="width:100%"
          />
        </el-form-item>
        <!-- 时间段选择：开始时间 + 至 + 结束时间 -->
        <el-form-item label="时间" required>
          <div class="time-row">
            <!-- 开始时间下拉 -->
            <el-select
              v-model="form.startTime"
              placeholder="开始"
              style="width:45%"
              @change="onStartTimeChange"
            >
              <el-option
                v-for="t in startTimeOptions"
                :key="t"
                :label="t"
                :value="t"
              />
            </el-select>
            <span>至</span>
            <!-- 结束时间下拉（仅显示开始时间之后的选项） -->
            <el-select
              v-model="form.endTime"
              placeholder="结束"
              style="width:45%"
            >
              <el-option
                v-for="t in endTimeOptions"
                :key="t"
                :label="t"
                :value="t"
              />
            </el-select>
          </div>
        </el-form-item>
        <!-- 到店人数 -->
        <el-form-item label="人数" prop="guestCount">
          <el-input-number v-model="form.guestCount" :min="1" :max="selectedRoom?.capacity || 10" />
        </el-form-item>
        <!-- 备注（选填） -->
        <el-form-item label="备注">
          <el-input v-model="form.remark" type="textarea" :rows="2" placeholder="选填" />
        </el-form-item>
      </el-form>
      <!-- 预计费用预览 -->
      <div class="booking-price" v-if="selectedRoom && form.startTime && form.endTime && calcHours() > 0">
        预计费用：<span>¥{{ (selectedRoom.pricePerHour * calcHours()).toFixed(2) }}</span>
        （{{ calcHours() }} 小时 × ¥{{ selectedRoom.pricePerHour }}/小时）
      </div>
      <template #footer>
        <el-button @click="showBook = false">取消</el-button>
        <el-button type="primary" @click="doBook" :loading="booking">确认预约</el-button>
      </template>
    </el-dialog>

    <!-- 页面底部页脚栏 -->
    <FooterBar />
  </div>
</template>

<script setup>
/**
 * 包间预约页脚本逻辑
 * 负责：加载雅间列表、管理预约弹窗状态、时间段选择逻辑、费用计算、提交预约
 */
import { computed, onMounted, reactive, ref } from 'vue'
import { ElMessage } from 'element-plus'
import NavBar from '@/components/NavBar.vue'
import FooterBar from '@/components/FooterBar.vue'
import { roomApi, roomBookingApi } from '@/api/booking'
import { resolveRoomImage } from '@/utils/localImage'

// ===== 雅间列表相关 =====
const rooms = ref([])             // 雅间列表
const loading = ref(false)        // 页面加载状态

// ===== 预约弹窗相关 =====
const showBook = ref(false)       // 预约弹窗显示状态
const booking = ref(false)        // 预约按钮加载状态
const selectedRoom = ref(null)    // 当前选中的雅间对象
const formRef = ref(null)         // 预约表单引用

// ===== 预约表单数据 =====
const form = reactive({
  bookingDate: '',    // 预约日期（YYYY-MM-DD）
  startTime: '',      // 开始时间（HH:00）
  endTime: '',        // 结束时间（HH:00）
  guestCount: 1,      // 到店人数
  remark: ''          // 备注（选填）
})

// ===== 表单校验规则 =====
const rules = {
  bookingDate: [{ required: true, message: '请选择日期', trigger: 'change' }]
}

// ===== 时间段定义（8:00 至 23:00，每小时一个槽位，共16个） =====
const timeSlots = Array.from({ length: 16 }, (_, i) => `${String(8 + i).padStart(2, '0')}:00`)

// 开始时间选项：排除最后一个槽位（因为至少需要1小时）
const startTimeOptions = computed(() => timeSlots.slice(0, -1))

// 结束时间选项：只显示开始时间之后的槽位
const endTimeOptions = computed(() => {
  if (!form.startTime) return timeSlots.slice(1)
  const startIdx = timeSlots.indexOf(form.startTime)
  return timeSlots.slice(startIdx + 1)
})

/**
 * 开始时间变更时的回调
 * 如果结束时间 <= 开始时间，清空结束时间
 */
const onStartTimeChange = () => {
  if (form.endTime && form.endTime <= form.startTime) {
    form.endTime = ''
  }
}

/**
 * 将雅间类型代码转换为中文文本
 * @param {string} t - 类型代码（SMALL/MEDIUM/LARGE/VIP）
 * @returns {string} 中文类型文本
 */
const typeText = (t) => ({ SMALL: '小包间', MEDIUM: '中包间', LARGE: '大包间', VIP: '贵宾间' }[t] || t)

/**
 * 计算预约时长（小时数）
 * 根据开始时间和结束时间计算小时差
 * @returns {number} 时长（小时）
 */
const calcHours = () => {
  if (!form.startTime || !form.endTime) return 0
  const [sh, sm] = form.startTime.split(':').map(Number)
  const [eh, em] = form.endTime.split(':').map(Number)
  return eh - sh + (em - sm) / 60
}

/**
 * 禁用过去的日期（不可选今天之前的日期）
 * @param {Date} time - 日期对象
 * @returns {boolean} 是否禁用
 */
const disabledDate = (time) => time.getTime() < Date.now() - 86400000

/**
 * 打开预约弹窗
 * 重置表单并绑定选中雅间
 * @param {object} room - 选中的雅间对象
 */
const openBook = (room) => {
  selectedRoom.value = room
  form.bookingDate = ''
  form.startTime = ''
  form.endTime = ''
  form.guestCount = 1
  form.remark = ''
  showBook.value = true
}

/**
 * 提交预约
 * 校验表单、时间段合法性后，调用 API 提交预约
 */
const doBook = async () => {
  const valid = await formRef.value.validate().catch(() => false)
  if (!valid) return
  if (!form.startTime || !form.endTime) {
    ElMessage.warning('请选择时间')
    return
  }
  if (calcHours() < 1) {
    ElMessage.warning('最少预约1小时')
    return
  }
  booking.value = true
  try {
    await roomBookingApi.book({
      roomId: selectedRoom.value.id,
      bookingDate: form.bookingDate,
      startTime: form.startTime,
      endTime: form.endTime,
      guestCount: form.guestCount,
      remark: form.remark
    })
    ElMessage.success('预约成功')
    showBook.value = false
  } finally {
    booking.value = false
  }
}

/**
 * 组件挂载后：加载雅间列表
 */
onMounted(async () => {
  loading.value = true
  roomApi.getList().then((r) => {
    rooms.value = r
  }).finally(() => {
    loading.value = false
  })
})
</script>

<style scoped>
/* ===== 页面整体布局 ===== */
.page {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
}

.room-page {
  flex: 1;
  padding-top: 28px;
}

/* ===== 顶部品牌介绍面板 ===== */
.inner-hero {
  display: grid;
  grid-template-columns: minmax(0, 1.42fr) minmax(320px, 0.78fr);
  gap: 28px;
  padding: 34px 36px;
  align-items: center;
}

.inner-hero__eyebrow {
  color: var(--color-red);
  font-size: 12px;
  letter-spacing: 0.32em;
  text-transform: uppercase;
}

.inner-hero__copy h1 {
  margin: 12px 0 14px;
  color: var(--color-ink);
  font-family: var(--font-display);
  font-size: 48px;
  font-weight: 600;
  line-height: 1.2;
  letter-spacing: 0.06em;
}

.inner-hero__copy p,
.inner-hero__aside p {
  margin: 0;
  color: var(--color-ink-soft);
  font-size: 15px;
  line-height: 1.9;
}

.inner-hero__aside {
  padding: 24px;
  background: rgba(255, 252, 246, 0.82);
  border: 1px solid rgba(204, 188, 161, 0.4);
}

.inner-hero__aside strong {
  display: block;
  margin-bottom: 10px;
  color: var(--color-wood);
  font-family: var(--font-display);
  font-size: 24px;
  font-weight: 600;
  letter-spacing: 0.06em;
}

.inner-hero__link {
  display: inline-block;
  margin-top: 18px;
  color: var(--color-red);
  font-size: 13px;
  letter-spacing: 0.16em;
}

/* ===== 雅间网格（2列） ===== */
.room-grid {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 24px;
}

/* 雅间卡片 */
.room-card {
  overflow: hidden;
}

/* 封面图容器（16:10 比例） */
.room-card__image {
  position: relative;
  aspect-ratio: 16 / 10;
  overflow: hidden;
  background: #ece0cd;
}

.room-card__image img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

/* 雅间类型角标 */
.room-card__type {
  position: absolute;
  top: 16px;
  right: 16px;
  padding: 6px 12px;
  background: rgba(47, 79, 62, 0.92);
  color: #fffaf2;
  font-size: 11px;
  letter-spacing: 0.12em;
}

/* 信息区域 */
.room-card__body {
  padding: 24px;
}

/* 标题区：名称 + 容量 */
.room-card__heading {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  gap: 12px;
}

.room-card__eyebrow {
  color: var(--color-red);
  font-size: 11px;
  letter-spacing: 0.18em;
  text-transform: uppercase;
}

.room-card__heading h3 {
  margin: 8px 0 0;
  color: var(--color-ink);
  font-family: var(--font-display);
  font-size: 30px;
  font-weight: 600;
  letter-spacing: 0.06em;
}

.room-card__capacity {
  color: var(--color-ink-muted);
  font-size: 13px;
  letter-spacing: 0.08em;
}

.room-card__desc {
  margin: 18px 0 16px;
  color: var(--color-ink-soft);
  font-size: 14px;
  line-height: 1.85;
}

/* 设施与预约方式元信息 */
.room-card__meta {
  display: flex;
  gap: 16px;
  flex-wrap: wrap;
  color: var(--color-ink-muted);
  font-size: 13px;
  margin-bottom: 22px;
}

/* 底部：价格 + 预约按钮 */
.room-card__bottom {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 14px;
}

.room-card__price {
  color: var(--color-red);
  font-size: 28px;
  font-weight: 700;
}

.room-card__price small {
  font-size: 12px;
  font-weight: 400;
}

/* ===== 预约弹窗样式 ===== */
/* 时间段选择行 */
.time-row {
  display: flex;
  align-items: center;
  gap: 8px;
  width: 100%;
}

/* 预计费用预览 */
.booking-price {
  padding: 8px 0 0;
  text-align: right;
  color: var(--color-ink-soft);
}

.booking-price span {
  color: var(--color-red);
  font-size: 22px;
  font-weight: 700;
}
</style>
