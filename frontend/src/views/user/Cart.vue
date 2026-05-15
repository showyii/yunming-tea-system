<template>
  <div class="page">
    <NavBar />

    <div class="container cart-page" v-loading="loading">
      <section class="inner-hero paper-panel">
        <div class="inner-hero__copy">
          <h1>先把想带走的茶暂时放在这里，再慢慢核对这一单的分量</h1>
          <p>选好的茶先收进这一页，数量、价格和小计都会一并列清，方便你下单前再从容看一遍。</p>
        </div>

        <div class="inner-hero__aside">
          <strong>结算速览</strong>
          <div class="hero-metrics">
            <div>
              <span>茶品件数</span>
              <b>{{ totalQuantity }}</b>
            </div>
            <div>
              <span>应付金额</span>
              <b>¥{{ total.toFixed(2) }}</b>
            </div>
          </div>
          <p>如果还想继续比较，也可以回到全部茶品页补充几款茶，再统一回到这里整理结算。</p>
          <router-link to="/products" class="inner-hero__link">继续浏览茶品</router-link>
        </div>
      </section>

      <section class="desktop-section">
        <div class="section-title section-title-left">
          <h2>待结算茶单</h2>
          <p>想增减数量、删去暂时不需要的茶，或直接准备下单，都可以在这里顺手完成，不必来回切换。</p>
        </div>

        <div v-if="items.length > 0" class="cart-layout">
          <div class="cart-list">
            <article v-for="item in items" :key="item.id" class="cart-card paper-panel">
              <button class="cart-card__image" @click="$router.push(`/products/${item.productId}`)">
                <img :src="resolveProductImage(item, item.productImage)" :alt="item.productName">
              </button>

              <div class="cart-card__body">
                <div class="cart-card__copy">
                  <h3 @click="$router.push(`/products/${item.productId}`)">{{ item.productName }}</h3>
                  <div class="cart-card__meta">
                    <span>单价 ¥{{ item.price }}</span>
                    <span>库存 {{ item.stock }}</span>
                  </div>
                </div>

                <div class="cart-card__actions">
                  <div class="qty-editor">
                    <span>数量</span>
                    <el-input-number
                      v-model="item.quantity"
                      :min="1"
                      :max="item.stock"
                      size="small"
                      @change="updateQty(item)"
                    />
                  </div>
                  <div class="subtotal">
                    <span>小计</span>
                    <b>¥{{ (item.price * item.quantity).toFixed(2) }}</b>
                  </div>
                  <el-button text type="danger" @click="removeItem(item)">删除</el-button>
                </div>
              </div>
            </article>
          </div>

          <aside class="checkout-panel paper-panel">
            <h3>本次结算</h3>
            <div class="summary-row">
              <span>商品件数</span>
              <b>{{ totalQuantity }}</b>
            </div>
            <div class="summary-row">
              <span>商品种类</span>
              <b>{{ items.length }}</b>
            </div>
            <div class="summary-row total-row">
              <span>应付金额</span>
              <b>¥{{ total.toFixed(2) }}</b>
            </div>
            <p>确认下单后，就可以继续去订单页查看付款与寄送进度。</p>
            <el-button type="primary" size="large" class="checkout-button" @click="showSubmit = true">去结算</el-button>
          </aside>
        </div>

        <div v-else-if="!loading" class="empty-state paper-panel">
          <h3>购物车还是空的</h3>
          <p>先去挑几款适合日常品饮、待客或礼赠的茶，再统一回到这里慢慢整理订单。</p>
          <router-link to="/products" class="inner-hero__link">去逛逛茶品</router-link>
        </div>
      </section>

      <el-dialog v-model="showSubmit" title="确认订单信息" width="520px">
        <el-form ref="formRef" :model="orderForm" :rules="rules" label-width="84px">
          <el-form-item label="收货人" prop="receiverName">
            <el-input v-model="orderForm.receiverName" placeholder="请输入收货人姓名" />
          </el-form-item>
          <el-form-item label="手机号" prop="receiverPhone">
            <el-input v-model="orderForm.receiverPhone" placeholder="请输入收货人手机号" />
          </el-form-item>
          <el-form-item label="收货地址" prop="receiverAddress">
            <el-input v-model="orderForm.receiverAddress" placeholder="请输入收货地址" />
          </el-form-item>
          <el-form-item label="备注">
            <el-input v-model="orderForm.remark" type="textarea" :rows="2" placeholder="选填" />
          </el-form-item>
        </el-form>
        <div class="dialog-summary">
          共 {{ totalQuantity }} 件商品，应付 <span>¥{{ total.toFixed(2) }}</span>
        </div>
        <template #footer>
          <el-button @click="showSubmit = false">取消</el-button>
          <el-button type="primary" :loading="submitting" @click="doSubmit">确认下单</el-button>
        </template>
      </el-dialog>
    </div>

    <FooterBar />
  </div>
</template>

<script setup>
import { computed, onMounted, reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import NavBar from '@/components/NavBar.vue'
import FooterBar from '@/components/FooterBar.vue'
import { cartApi } from '@/api/cart'
import { orderApi } from '@/api/order'
import { resolveProductImage } from '@/utils/localImage'

const router = useRouter()
const items = ref([])
const loading = ref(false)
const showSubmit = ref(false)
const submitting = ref(false)
const formRef = ref(null)

const orderForm = reactive({
  receiverName: '',
  receiverPhone: '',
  receiverAddress: '',
  remark: ''
})

const rules = {
  receiverName: [{ required: true, message: '请输入收货人', trigger: 'blur' }],
  receiverPhone: [{ required: true, message: '请输入手机号', trigger: 'blur' }],
  receiverAddress: [{ required: true, message: '请输入地址', trigger: 'blur' }]
}

const total = computed(() => items.value.reduce((sum, item) => sum + item.price * item.quantity, 0))
const totalQuantity = computed(() => items.value.reduce((sum, item) => sum + item.quantity, 0))

const loadData = async () => {
  loading.value = true
  try {
    items.value = await cartApi.getList()
  } finally {
    loading.value = false
  }
}

const updateQty = async (item) => {
  try {
    await cartApi.update({ id: item.id, quantity: item.quantity })
  } catch (e) {
    loadData()
  }
}

const removeItem = async (item) => {
  try {
    await ElMessageBox.confirm('确定移除此商品？', '提示', { type: 'warning' })
    await cartApi.delete(item.id)
    ElMessage.success('已移除')
    loadData()
  } catch (e) {
    // cancelled
  }
}

const doSubmit = async () => {
  const valid = await formRef.value.validate().catch(() => false)
  if (!valid) return

  submitting.value = true
  try {
    const orderId = await orderApi.submit({ ...orderForm })
    ElMessage.success('下单成功')
    showSubmit.value = false
    router.push(`/orders/${orderId}`)
  } finally {
    submitting.value = false
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

.cart-page {
  flex: 1;
  padding-top: 28px;
}

.inner-hero {
  display: grid;
  grid-template-columns: minmax(0, 1.34fr) minmax(320px, 0.66fr);
  gap: 28px;
  padding: 34px 36px;
  align-items: stretch;
}

.inner-hero__eyebrow,
.cart-card__eyebrow,
.checkout-panel__eyebrow,
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
.checkout-panel p,
.empty-state p {
  margin: 0;
  color: var(--color-ink-soft);
  font-size: 15px;
  line-height: 1.9;
}

.inner-hero__aside,
.checkout-panel {
  padding: 24px;
  background: rgba(255, 252, 246, 0.82);
  border: 1px solid rgba(204, 188, 161, 0.4);
}

.inner-hero__aside strong,
.checkout-panel h3,
.empty-state h3 {
  margin: 0 0 12px;
  color: var(--color-wood);
  font-family: var(--font-display);
  font-size: 24px;
  font-weight: 600;
  letter-spacing: 0.06em;
}

.hero-metrics {
  display: grid;
  gap: 12px;
  margin-bottom: 16px;
}

.hero-metrics div,
.summary-row {
  display: flex;
  align-items: baseline;
  justify-content: space-between;
  gap: 12px;
  padding: 14px 0;
  border-bottom: 1px solid rgba(204, 188, 161, 0.32);
  color: var(--color-ink-soft);
}

.hero-metrics div:first-child,
.summary-row:first-of-type {
  border-top: 1px solid rgba(204, 188, 161, 0.32);
}

.hero-metrics b,
.summary-row b,
.dialog-summary span {
  color: var(--color-red);
  font-size: 28px;
  font-weight: 700;
}

.inner-hero__link {
  display: inline-block;
  margin-top: 18px;
  color: var(--color-red);
  font-size: 13px;
  letter-spacing: 0.16em;
}

.cart-layout {
  display: grid;
  grid-template-columns: minmax(0, 1.55fr) minmax(300px, 0.7fr);
  gap: 22px;
  align-items: start;
}

.cart-list {
  display: grid;
  gap: 18px;
}

.cart-card {
  display: grid;
  grid-template-columns: 220px 1fr;
  overflow: hidden;
}

.cart-card__image {
  min-height: 230px;
  padding: 0;
  border: none;
  background: #ece0cd;
  cursor: pointer;
}

.cart-card__image img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.cart-card__body {
  display: flex;
  flex-direction: column;
  justify-content: space-between;
  gap: 20px;
  padding: 24px;
}

.cart-card__copy h3 {
  margin: 10px 0 0;
  color: var(--color-ink);
  font-family: var(--font-display);
  font-size: 30px;
  font-weight: 600;
  letter-spacing: 0.05em;
  cursor: pointer;
}

.cart-card__meta {
  display: flex;
  flex-wrap: wrap;
  gap: 10px 18px;
  margin-top: 14px;
  color: var(--color-ink-soft);
  font-size: 13px;
}

.cart-card__actions {
  display: flex;
  align-items: flex-end;
  justify-content: space-between;
  gap: 16px;
  padding-top: 18px;
  border-top: 1px solid rgba(204, 188, 161, 0.32);
}

.qty-editor,
.subtotal {
  display: grid;
  gap: 8px;
  color: var(--color-ink-soft);
  font-size: 13px;
}

.subtotal b {
  color: var(--color-red);
  font-size: 28px;
  font-weight: 700;
}

.checkout-panel {
  position: sticky;
  top: 110px;
}

.total-row b {
  font-size: 34px;
}

.checkout-button {
  width: 100%;
  margin-top: 20px;
}

.dialog-summary {
  padding-top: 12px;
  text-align: right;
  color: var(--color-ink-soft);
}

.empty-state {
  padding: 54px 32px;
  text-align: center;
}
</style>
