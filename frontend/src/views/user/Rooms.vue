<template>
  <div class="page">
    <NavBar />

    <div class="container room-page">
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

      <section class="desktop-section">
        <div class="section-title section-title-left">
          <h2>馆内雅间</h2>
          <p>每间雅室都写明了人数、设施、价格与可约状态，方便你按来意慢慢比较，挑一间坐着舒服、待客也合适的空间。</p>
        </div>

        <div class="room-grid" v-loading="loading">
          <article v-for="room in rooms" :key="room.id" class="room-card paper-panel">
            <div class="room-card__image">
              <img :src="resolveRoomImage(room, room.image)" :alt="room.name">
              <span class="room-card__type">{{ typeText(room.type) }}</span>
            </div>
            <div class="room-card__body">
              <div class="room-card__heading">
                <div>
                  <span class="room-card__eyebrow">馆内空间</span>
                  <h3>{{ room.name }}</h3>
                </div>
                <span class="room-card__capacity">容纳 {{ room.capacity }} 人</span>
              </div>

              <p class="room-card__desc">{{ room.description || '适合日常饮茶、待客会谈与轻量雅集的馆内空间。' }}</p>

              <div class="room-card__meta">
                <span v-if="room.facilities">{{ room.facilities }}</span>
                <span>可按小时预约</span>
              </div>

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

    <el-dialog v-model="showBook" title="预约包间" width="520px">
      <el-form :model="form" :rules="rules" ref="formRef" label-width="80px">
        <el-form-item label="包间">{{ selectedRoom?.name }}</el-form-item>
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
        <el-form-item label="时间" required>
          <div class="time-row">
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
        <el-form-item label="人数" prop="guestCount">
          <el-input-number v-model="form.guestCount" :min="1" :max="selectedRoom?.capacity || 10" />
        </el-form-item>
        <el-form-item label="备注">
          <el-input v-model="form.remark" type="textarea" :rows="2" placeholder="选填" />
        </el-form-item>
      </el-form>
      <div class="booking-price" v-if="selectedRoom && form.startTime && form.endTime && calcHours() > 0">
        预计费用：<span>¥{{ (selectedRoom.pricePerHour * calcHours()).toFixed(2) }}</span>
        （{{ calcHours() }} 小时 × ¥{{ selectedRoom.pricePerHour }}/小时）
      </div>
      <template #footer>
        <el-button @click="showBook = false">取消</el-button>
        <el-button type="primary" @click="doBook" :loading="booking">确认预约</el-button>
      </template>
    </el-dialog>

    <FooterBar />
  </div>
</template>

<script setup>
import { computed, onMounted, reactive, ref } from 'vue'
import { ElMessage } from 'element-plus'
import NavBar from '@/components/NavBar.vue'
import FooterBar from '@/components/FooterBar.vue'
import { roomApi, roomBookingApi } from '@/api/booking'
import { resolveRoomImage } from '@/utils/localImage'

const rooms = ref([])
const loading = ref(false)
const showBook = ref(false)
const booking = ref(false)
const selectedRoom = ref(null)
const formRef = ref(null)

const form = reactive({
  bookingDate: '',
  startTime: '',
  endTime: '',
  guestCount: 1,
  remark: ''
})

const rules = {
  bookingDate: [{ required: true, message: '请选择日期', trigger: 'change' }]
}

const timeSlots = Array.from({ length: 16 }, (_, i) => `${String(8 + i).padStart(2, '0')}:00`)

const startTimeOptions = computed(() => timeSlots.slice(0, -1))

const endTimeOptions = computed(() => {
  if (!form.startTime) return timeSlots.slice(1)
  const startIdx = timeSlots.indexOf(form.startTime)
  return timeSlots.slice(startIdx + 1)
})

const onStartTimeChange = () => {
  if (form.endTime && form.endTime <= form.startTime) {
    form.endTime = ''
  }
}

const typeText = (t) => ({ SMALL: '小包间', MEDIUM: '中包间', LARGE: '大包间', VIP: '贵宾间' }[t] || t)

const calcHours = () => {
  if (!form.startTime || !form.endTime) return 0
  const [sh, sm] = form.startTime.split(':').map(Number)
  const [eh, em] = form.endTime.split(':').map(Number)
  return eh - sh + (em - sm) / 60
}

const disabledDate = (time) => time.getTime() < Date.now() - 86400000

const openBook = (room) => {
  selectedRoom.value = room
  form.bookingDate = ''
  form.startTime = ''
  form.endTime = ''
  form.guestCount = 1
  form.remark = ''
  showBook.value = true
}

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
.page {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
}

.room-page {
  flex: 1;
  padding-top: 28px;
}

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

.room-grid {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 24px;
}

.room-card {
  overflow: hidden;
}

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

.room-card__body {
  padding: 24px;
}

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

.room-card__meta {
  display: flex;
  gap: 16px;
  flex-wrap: wrap;
  color: var(--color-ink-muted);
  font-size: 13px;
  margin-bottom: 22px;
}

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

.time-row {
  display: flex;
  align-items: center;
  gap: 8px;
  width: 100%;
}

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
