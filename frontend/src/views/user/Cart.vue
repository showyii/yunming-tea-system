<!--
  购物车页（Cart.vue）
  云茗茶馆的购物车页面，用于管理待结算茶品并完成下单。
  页面功能：
  - 顶部概览区：茶品件数、应付金额、继续浏览入口
  - 待结算茶单列表：每项支持修改数量、删除、查看详情
  - 结算面板：汇总信息 + 去结算按钮
  - 下单弹窗：收货人、手机号、地址、备注
  - 空购物车提示
-->
<template>
  <div class="page">
    <!-- 页面顶部导航栏 -->
    <NavBar />

    <div class="container cart-page" v-loading="loading">
      <!-- ========== 顶部：概览区 ========== -->
      <section class="inner-hero paper-panel">
        <div class="inner-hero__copy">
          <h1>先把想带走的茶暂时放在这里，再慢慢核对这一单的分量</h1>
          <p>选好的茶先收进这一页，数量、价格和小计都会一并列清，方便你下单前再从容看一遍。</p>
        </div>

        <!-- 右侧结算速览 -->
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

      <!-- ========== 待结算茶单区 ========== -->
      <section class="desktop-section">
        <div class="section-title section-title-left">
          <h2>待结算茶单</h2>
          <p>想增减数量、删去暂时不需要的茶，或直接准备下单，都可以在这里顺手完成，不必来回切换。</p>
        </div>

        <!-- 有商品时：双列布局（茶单 + 结算面板） -->
        <div v-if="items.length > 0" class="cart-layout">
          <!-- 茶单列表 -->
          <div class="cart-list">
            <!-- 单个购物车条目卡片 -->
            <article v-for="item in items" :key="item.id" class="cart-card paper-panel">
              <!-- 商品图片：点击跳转详情 -->
              <button class="cart-card__image" @click="$router.push(`/products/${item.productId}`)">
                <img :src="resolveProductImage(item, item.productImage)" :alt="item.productName">
              </button>

              <div class="cart-card__body">
                <!-- 商品名称 + 单价、库存 -->
                <div class="cart-card__copy">
                  <h3 @click="$router.push(`/products/${item.productId}`)">{{ item.productName }}</h3>
                  <div class="cart-card__meta">
                    <span>单价 ¥{{ item.price }}</span>
                    <span>库存 {{ item.stock }}</span>
                  </div>
                </div>

                <!-- 操作区：数量修改 + 小计 + 删除 -->
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

          <!-- 结算面板（侧栏，粘性定位） -->
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

        <!-- 空购物车提示 -->
        <div v-else-if="!loading" class="empty-state paper-panel">
          <h3>购物车还是空的</h3>
          <p>先去挑几款适合日常品饮、待客或礼赠的茶，再统一回到这里慢慢整理订单。</p>
          <router-link to="/products" class="inner-hero__link">去逛逛茶品</router-link>
        </div>
      </section>

      <!-- ========== 下单确认弹窗 ========== -->
      <el-dialog v-model="showSubmit" title="确认订单信息" width="520px">
        <el-form ref="formRef" :model="orderForm" :rules="rules" label-width="84px">
          <!-- 收货人姓名 -->
          <el-form-item label="收货人" prop="receiverName">
            <el-input v-model="orderForm.receiverName" placeholder="请输入收货人姓名" />
          </el-form-item>
          <!-- 收货人手机号 -->
          <el-form-item label="手机号" prop="receiverPhone">
            <el-input v-model="orderForm.receiverPhone" placeholder="请输入收货人手机号" />
          </el-form-item>
          <!-- 收货地址 -->
          <el-form-item label="收货地址" prop="receiverAddress">
            <el-input v-model="orderForm.receiverAddress" placeholder="请输入收货地址" />
          </el-form-item>
          <!-- 备注（选填） -->
          <el-form-item label="备注">
            <el-input v-model="orderForm.remark" type="textarea" :rows="2" placeholder="选填" />
          </el-form-item>
        </el-form>
        <!-- 弹窗底部汇总信息 -->
        <div class="dialog-summary">
          共 {{ totalQuantity }} 件商品，应付 <span>¥{{ total.toFixed(2) }}</span>
        </div>
        <template #footer>
          <el-button @click="showSubmit = false">取消</el-button>
          <el-button type="primary" :loading="submitting" @click="doSubmit">确认下单</el-button>
        </template>
      </el-dialog>
    </div>

    <!-- 页面底部页脚栏 -->
    <FooterBar />
  </div>
</template>

<script setup>
/**
 * 购物车页脚本逻辑
 * 负责：加载购物车列表、修改数量、删除商品、下单提交
 */
import { computed, onMounted, reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import NavBar from '@/components/NavBar.vue'
import FooterBar from '@/components/FooterBar.vue'
import { cartApi } from '@/api/cart'
import { orderApi } from '@/api/order'
import { resolveProductImage } from '@/utils/localImage'

// ===== 路由与表单引用 =====
const router = useRouter()
const items = ref([])             // 购物车商品列表
const loading = ref(false)        // 页面加载状态
const showSubmit = ref(false)     // 下单弹窗显示状态
const submitting = ref(false)     // 下单按钮加载状态
const formRef = ref(null)         // 下单表单引用

// ===== 下单表单数据（响应式对象） =====
const orderForm = reactive({
  receiverName: '',       // 收货人姓名
  receiverPhone: '',      // 收货人手机号
  receiverAddress: '',    // 收货地址
  remark: ''              // 备注（选填）
})

// ===== 下单表单校验规则 =====
const rules = {
  receiverName: [{ required: true, message: '请输入收货人', trigger: 'blur' }],
  receiverPhone: [{ required: true, message: '请输入手机号', trigger: 'blur' }],
  receiverAddress: [{ required: true, message: '请输入地址', trigger: 'blur' }]
}

// ===== 计算属性 =====
// 应付总额 = 所有商品 (单价 × 数量) 之和
const total = computed(() => items.value.reduce((sum, item) => sum + item.price * item.quantity, 0))
// 商品总件数 = 所有商品数量之和
const totalQuantity = computed(() => items.value.reduce((sum, item) => sum + item.quantity, 0))

/**
 * 加载购物车数据
 */
const loadData = async () => {
  loading.value = true
  try {
    items.value = await cartApi.getList()
  } finally {
    loading.value = false
  }
}

/**
 * 更新商品数量
 * 修改后调用 API 同步，失败时重新加载列表以恢复原值
 * @param {object} item - 购物车条目对象
 */
const updateQty = async (item) => {
  try {
    await cartApi.update({ id: item.id, quantity: item.quantity })
  } catch (e) {
    loadData()  // 更新失败时重新加载以恢复原值
  }
}

/**
 * 删除购物车条目
 * 弹出确认框后调用 API，成功后刷新列表
 * @param {object} item - 购物车条目对象
 */
const removeItem = async (item) => {
  try {
    await ElMessageBox.confirm('确定移除此商品？', '提示', { type: 'warning' })
    await cartApi.delete(item.id)
    ElMessage.success('已移除')
    loadData()
  } catch (e) {
    // 用户取消操作或请求失败
  }
}

/**
 * 提交订单
 * 校验表单后调用下单 API，成功后跳转到订单详情页
 */
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

/**
 * 组件挂载后：加载购物车数据
 */
onMounted(loadData)
</script>

<style scoped>
/* ===== 页面整体布局 ===== */
.page {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
}

.cart-page {
  flex: 1;
  padding-top: 28px;
}

/* ===== 顶部概览面板 ===== */
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

/* 概览指标行 */
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

/* ===== 茶单 + 结算面板双列布局 ===== */
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

/* 购物车条目卡片：图片左 + 信息右 */
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

/* 操作区：数量 + 小计 + 删除 */
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

/* ===== 结算面板（粘性定位，滚动时固定在顶部） ===== */
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

/* ===== 下单弹窗 ===== */
.dialog-summary {
  padding-top: 12px;
  text-align: right;
  color: var(--color-ink-soft);
}

/* ===== 空状态提示 ===== */
.empty-state {
  padding: 54px 32px;
  text-align: center;
}
</style>
