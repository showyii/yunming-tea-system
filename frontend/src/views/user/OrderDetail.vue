<template>
  <div class="page">
    <NavBar />
    <div class="container order-page" v-loading="loading">
      <div class="breadcrumb">
        <router-link to="/">首页</router-link>
        <span>/</span>
        <router-link to="/orders">我的订单</router-link>
        <span>/</span>
        <span>订单详情</span>
      </div>

      <div class="detail-card paper-panel" v-if="order.id">
        <div class="status-bar" :class="statusClass(order.status)">
          <span class="status-icon">
            <span v-if="order.status === 0">待付款</span>
            <span v-else-if="order.status === 1">已付款</span>
            <span v-else-if="order.status === 2">配送中</span>
            <span v-else-if="order.status === 3">已完成</span>
            <span v-else-if="order.status === 4">已取消</span>
          </span>
          <span class="status-text">{{ order.statusText }}</span>
        </div>

        <div class="detail-grid">
          <section class="detail-section">
            <h3>收货信息</h3>
            <p>{{ order.receiverName }} {{ order.receiverPhone }}</p>
            <p>{{ order.receiverAddress }}</p>
            <p v-if="order.remark" class="remark">备注：{{ order.remark }}</p>
          </section>

          <section class="detail-section">
            <h3>订单信息</h3>
            <div class="info-grid">
              <div class="info-row"><span>订单编号</span><span>{{ order.orderNo }}</span></div>
              <div class="info-row"><span>下单时间</span><span>{{ formatTime(order.createTime) }}</span></div>
              <div class="info-row"><span>支付时间</span><span>{{ formatTime(order.payTime) || '-' }}</span></div>
              <div class="info-row"><span>商品总额</span><span>¥{{ order.totalAmount }}</span></div>
              <div class="info-row"><span>实付金额</span><span class="pay-amount">¥{{ order.payAmount }}</span></div>
            </div>
          </section>
        </div>

        <section class="detail-section detail-section-full">
          <h3>商品信息</h3>
          <div class="item-list">
            <article v-for="item in order.items" :key="item.id" class="item-row">
              <div class="item-img">
              <img :src="resolveProductImage(item, item.productImage)" :alt="item.productName">
              </div>
              <div class="item-info">
                <div class="item-name">{{ item.productName }}</div>
                <div class="item-price">¥{{ item.price }} × {{ item.quantity }}</div>
              </div>
              <div class="item-total">¥{{ item.totalPrice }}</div>
            </article>
          </div>
        </section>

        <div class="detail-actions">
          <el-button v-if="order.status === 0" type="warning" @click="doCancel" size="large">取消订单</el-button>
          <el-button v-if="order.status === 0" type="primary" @click="doPay" size="large">立即付款</el-button>
          <router-link to="/orders" class="back-link">返回订单列表</router-link>
        </div>
      </div>
    </div>
    <FooterBar />
  </div>
</template>

<script setup>
import { onMounted, ref } from 'vue'
import { useRoute } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import NavBar from '@/components/NavBar.vue'
import FooterBar from '@/components/FooterBar.vue'
import { orderApi } from '@/api/order'
import { resolveProductImage } from '@/utils/localImage'

const route = useRoute()
const order = ref({})
const loading = ref(false)

const statusClass = (s) => ['status-pending', 'status-paid', 'status-shipped', 'status-done', 'status-cancel'][s] || ''

const loadData = async () => {
  loading.value = true
  try {
    order.value = await orderApi.getDetail(route.params.id)
  } finally {
    loading.value = false
  }
}

const doCancel = async () => {
  try {
    await ElMessageBox.confirm('确定取消此订单？', '提示', { type: 'warning' })
    await orderApi.cancel(order.value.id)
    ElMessage.success('订单已取消')
    loadData()
  } catch (e) {
    // cancelled
  }
}

const doPay = async () => {
  try {
    await ElMessageBox.confirm(`确认支付 ¥${order.value.totalAmount}？\n（模拟支付，不会实际扣款）`, '模拟支付', { type: 'info' })
    await orderApi.pay(order.value.id)
    ElMessage.success('支付成功')
    loadData()
  } catch (e) {
    // cancelled
  }
}

const formatTime = (t) => (t ? new Date(t).toLocaleString('zh-CN') : '')

onMounted(loadData)
</script>

<style scoped>
.page {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
}

.order-page {
  flex: 1;
  max-width: 980px;
  padding-top: 28px;
}

.breadcrumb {
  display: flex;
  gap: 8px;
  margin-bottom: 20px;
  color: var(--color-ink-muted);
  font-size: 13px;
}

.detail-card {
  padding: 24px;
}

.status-bar {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 12px;
  padding: 18px 20px;
  border-radius: 14px;
  margin-bottom: 20px;
  font-weight: 600;
}

.status-pending {
  background: rgba(140, 107, 63, 0.12);
  color: var(--color-gold);
}

.status-paid {
  background: rgba(47, 79, 62, 0.12);
  color: var(--color-green);
}

.status-shipped {
  background: rgba(107, 142, 110, 0.16);
  color: var(--color-bamboo);
}

.status-done {
  background: rgba(92, 64, 51, 0.1);
  color: var(--color-wood);
}

.status-cancel {
  background: rgba(159, 45, 32, 0.1);
  color: var(--color-red);
}

.status-icon {
  font-size: 18px;
  letter-spacing: 0.1em;
}

.detail-grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 18px;
}

.detail-section {
  padding: 18px;
  border-radius: 14px;
  background: rgba(255, 250, 242, 0.84);
}

.detail-section-full {
  margin-top: 18px;
}

.detail-section h3 {
  margin: 0 0 12px;
  color: var(--color-wood);
  font-size: 16px;
  letter-spacing: 0.12em;
}

.detail-section p {
  margin: 0 0 8px;
  color: var(--color-ink-soft);
  line-height: 1.8;
}

.remark {
  color: var(--color-ink-muted);
}

.item-list {
  display: grid;
  gap: 12px;
}

.item-row {
  display: grid;
  grid-template-columns: 72px 1fr auto;
  gap: 14px;
  align-items: center;
  padding: 12px;
  border-radius: 12px;
  background: rgba(245, 241, 232, 0.72);
}

.item-img {
  width: 72px;
  height: 72px;
  overflow: hidden;
  border-radius: 10px;
  background: #efe6d7;
}

.item-img img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.item-name {
  margin-bottom: 6px;
  color: var(--color-ink);
  font-weight: 600;
}

.item-price {
  color: var(--color-ink-soft);
  font-size: 13px;
}

.item-total {
  color: var(--color-red);
  font-size: 18px;
  font-weight: 700;
}

.info-grid {
  display: grid;
  gap: 10px;
}

.info-row {
  display: flex;
  justify-content: space-between;
  gap: 16px;
  color: var(--color-ink-soft);
}

.info-row span:last-child {
  color: var(--color-ink);
}

.pay-amount {
  color: var(--color-red) !important;
  font-size: 20px;
  font-weight: 700;
}

.detail-actions {
  display: flex;
  align-items: center;
  gap: 12px;
  padding-top: 22px;
}

.back-link {
  margin-left: auto;
  color: var(--color-green);
}

@media (max-width: 768px) {
  .detail-grid {
    grid-template-columns: 1fr;
  }

  .item-row {
    grid-template-columns: 72px 1fr;
  }

  .item-total {
    grid-column: 2;
  }

  .detail-actions {
    flex-direction: column;
    align-items: stretch;
  }

  .back-link {
    margin-left: 0;
    text-align: center;
  }
}
</style>
