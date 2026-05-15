<template>
  <div class="page">
    <NavBar />

    <div class="container bookings-page" v-loading="loading">
      <section class="inner-hero paper-panel">
        <div class="inner-hero__copy">
          <h1>把每一次来馆相聚都安稳记下，时间、人数与雅间安排一目了然</h1>
          <p>这里会留住你的包间预约记录。下次回看时，哪一天来、几人同坐、订的是哪一间雅室，都能很快对上。</p>
        </div>

        <div class="inner-hero__aside">
          <strong>预约概览</strong>
          <div class="hero-stat">
            <span>当前记录</span>
            <b>{{ bookings.length }}</b>
          </div>
          <p>如果你想继续预订新的雅间，可以回到包间预约页，根据人数和环境再次选择适合的空间。</p>
          <router-link to="/rooms" class="inner-hero__link">继续预约包间</router-link>
        </div>
      </section>

      <section class="desktop-section">
        <div class="section-title section-title-left">
          <h2>预约记录</h2>
          <p>每笔预约都写清了时段、人数、价格与备注，方便你随时回看，也方便在行程有变时及时调整安排。</p>
        </div>

        <div v-if="bookings.length > 0" class="booking-list">
          <article v-for="item in bookings" :key="item.id" class="booking-card paper-panel">
            <div class="booking-card__image">
              <img :src="resolveRoomImage(item, item.roomImage)" :alt="item.roomName">
            </div>

            <div class="booking-card__body">
              <div>
                <h3>{{ item.roomName }}</h3>
                <div class="booking-card__schedule">
                  {{ item.bookingDate }} {{ item.startTime }} - {{ item.endTime }}
                </div>
              </div>

              <div class="booking-card__meta">
                <span>{{ item.guestCount }} 人</span>
                <span>{{ item.duration }} 小时</span>
                <span>总价 ¥{{ item.totalPrice }}</span>
                <span>{{ item.statusText }}</span>
              </div>

              <p v-if="item.remark" class="booking-card__remark">备注：{{ item.remark }}</p>
            </div>

            <div class="booking-card__side">
              <div class="booking-card__status">{{ item.statusText }}</div>
              <el-button v-if="item.status === 0 || item.status === 1" type="warning" @click="doCancel(item)">取消预约</el-button>
            </div>
          </article>
        </div>

        <div v-else-if="!loading" class="empty-state paper-panel">
          <h3>暂无预约记录</h3>
          <p>等你选好一间合适的雅室后，这里就会慢慢记下每一次来馆的安排与相聚时间。</p>
          <router-link to="/rooms" class="inner-hero__link">去预约包间</router-link>
        </div>
      </section>
    </div>

    <FooterBar />
  </div>
</template>

<script setup>
import { onMounted, ref } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import NavBar from '@/components/NavBar.vue'
import FooterBar from '@/components/FooterBar.vue'
import { roomBookingApi } from '@/api/booking'
import { resolveRoomImage } from '@/utils/localImage'

const bookings = ref([])
const loading = ref(false)

const loadData = async () => {
  loading.value = true
  try {
    bookings.value = await roomBookingApi.getList()
  } finally {
    loading.value = false
  }
}

const doCancel = async (item) => {
  try {
    await ElMessageBox.confirm('确定取消预约？', '提示', { type: 'warning' })
    await roomBookingApi.cancel(item.id)
    ElMessage.success('已取消')
    loadData()
  } catch (e) {
    // cancelled
  }
}

onMounted(loadData)
</script>

<style scoped>
.page {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
}

.bookings-page {
  flex: 1;
  padding-top: 28px;
}

.inner-hero {
  display: grid;
  grid-template-columns: minmax(0, 1.35fr) minmax(300px, 0.65fr);
  gap: 28px;
  padding: 34px 36px;
  align-items: stretch;
}

.inner-hero__eyebrow,
.booking-card__eyebrow,
.empty-state__eyebrow {
  color: var(--color-red);
  font-size: 12px;
  letter-spacing: 0.3em;
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
.inner-hero__aside p,
.empty-state p,
.booking-card__remark {
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

.inner-hero__aside strong,
.empty-state h3 {
  display: block;
  margin: 0 0 12px;
  color: var(--color-wood);
  font-family: var(--font-display);
  font-size: 24px;
  font-weight: 600;
  letter-spacing: 0.06em;
}

.hero-stat {
  display: flex;
  align-items: baseline;
  justify-content: space-between;
  gap: 12px;
  padding: 14px 0;
  margin-bottom: 16px;
  border-top: 1px solid rgba(204, 188, 161, 0.32);
  border-bottom: 1px solid rgba(204, 188, 161, 0.32);
  color: var(--color-ink-soft);
}

.hero-stat b {
  color: var(--color-red);
  font-size: 30px;
  font-weight: 700;
}

.inner-hero__link {
  display: inline-block;
  margin-top: 18px;
  color: var(--color-red);
  font-size: 13px;
  letter-spacing: 0.16em;
}

.booking-list {
  display: grid;
  gap: 18px;
}

.booking-card {
  display: grid;
  grid-template-columns: 220px minmax(0, 1fr) 180px;
  gap: 0;
  overflow: hidden;
}

.booking-card__image {
  min-height: 220px;
  background: #ece0cd;
}

.booking-card__image img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.booking-card__body {
  display: flex;
  flex-direction: column;
  justify-content: space-between;
  gap: 16px;
  padding: 24px;
}

.booking-card__body h3 {
  margin: 10px 0 0;
  color: var(--color-ink);
  font-family: var(--font-display);
  font-size: 30px;
  font-weight: 600;
  letter-spacing: 0.05em;
}

.booking-card__schedule {
  margin-top: 14px;
  color: var(--color-wood);
  font-size: 15px;
}

.booking-card__meta {
  display: flex;
  flex-wrap: wrap;
  gap: 10px 18px;
  color: var(--color-ink-soft);
  font-size: 13px;
}

.booking-card__side {
  display: flex;
  flex-direction: column;
  justify-content: center;
  gap: 18px;
  padding: 24px;
  background: rgba(255, 252, 246, 0.74);
  border-left: 1px solid rgba(204, 188, 161, 0.3);
}

.booking-card__status {
  color: var(--color-red);
  font-size: 22px;
  font-weight: 700;
}

.empty-state {
  padding: 54px 32px;
  text-align: center;
}
</style>
