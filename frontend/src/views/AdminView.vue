<template>
  <section class="admin-layout">
    <div class="section-card">
      <div class="section-head">
        <h1>经营概览</h1>
        <button class="ghost-button" @click="loadData">刷新</button>
      </div>
      <div v-if="dashboard" class="stat-grid">
        <div class="stat-card"><span>用户数</span><strong>{{ dashboard.userCount }}</strong></div>
        <div class="stat-card"><span>商品数</span><strong>{{ dashboard.productCount }}</strong></div>
        <div class="stat-card"><span>订单数</span><strong>{{ dashboard.orderCount }}</strong></div>
        <div class="stat-card"><span>有效营收</span><strong>￥{{ dashboard.totalRevenue }}</strong></div>
      </div>
    </div>

    <section class="section-card">
      <div class="section-head">
        <h2>商品管理</h2>
        <button class="accent-button" @click="fillDemo">填入示例</button>
      </div>
      <form class="admin-form" @submit.prevent="saveProduct">
        <input v-model="form.name" placeholder="商品名称" required />
        <input v-model="form.subtitle" placeholder="商品副标题" required />
        <input v-model="form.imageUrl" placeholder="图片 URL" required />
        <input v-model.number="form.price" type="number" step="0.01" min="0.01" placeholder="价格" required />
        <input v-model.number="form.stock" type="number" min="0" placeholder="库存" required />
        <select v-model.number="form.categoryId" required>
          <option v-for="category in categories" :key="category.id" :value="category.id">{{ category.name }}</option>
        </select>
        <textarea v-model="form.description" placeholder="商品详情" required></textarea>
        <label class="inline-meta">
          <input v-model="form.active" type="checkbox" />
          <span>上架销售</span>
        </label>
        <button class="accent-button">{{ form.id ? '更新商品' : '创建商品' }}</button>
      </form>

      <div class="stack-list">
        <article v-for="product in products" :key="product.id" class="row-card">
          <div class="row-main">
            <strong>{{ product.name }}</strong>
            <span>{{ product.categoryName }} · ￥{{ product.price }} · 库存 {{ product.stock }} · {{ product.active ? '已上架' : '已下架' }}</span>
          </div>
          <div class="row-actions">
            <button class="ghost-button" @click="editProduct(product)">编辑</button>
            <button v-if="product.active" class="ghost-button" @click="removeProduct(product.id)">下架</button>
          </div>
        </article>
      </div>
    </section>

    <section class="section-card">
      <div class="section-head"><h2>订单履约</h2></div>
      <div class="stack-list">
        <article v-for="order in orders" :key="order.id" class="row-card">
          <div class="row-main">
            <strong>{{ order.orderNo }}</strong>
            <span>{{ statusText[order.status] || order.status }} · ￥{{ order.totalAmount }}</span>
          </div>
          <div class="row-actions">
            <button v-if="order.status === 'PAID'" class="accent-button" @click="ship(order.id)">发货</button>
          </div>
        </article>
      </div>
    </section>

    <section class="section-card">
      <div class="section-head">
        <h2>运营与营销</h2>
        <button class="accent-button" @click="createDemoActivity">新增活动</button>
      </div>
      <div class="dual-grid">
        <div class="stack-list">
          <article v-for="activity in marketing" :key="activity.id" class="row-card">
            <div class="row-main">
              <strong>{{ activity.title }} · {{ activity.type }}</strong>
              <span>{{ activity.ruleText }} · {{ activity.status }}</span>
            </div>
          </article>
        </div>
        <div class="stat-grid">
          <div v-if="reports" class="stat-card"><span>访客</span><strong>{{ reports.visitorCount }}</strong></div>
          <div v-if="reports" class="stat-card"><span>转化</span><strong>{{ reports.conversionCount }}</strong></div>
          <div v-if="reports" class="stat-card"><span>库存预警</span><strong>{{ reports.stockWarningCount }}</strong></div>
          <div v-if="reports" class="stat-card"><span>营销效果</span><p>{{ reports.marketingEffect }}</p></div>
        </div>
      </div>
    </section>

    <section class="section-card">
      <div class="section-head"><h2>权限风控与配置中心</h2></div>
      <div class="dual-grid">
        <div class="stack-list">
          <article v-for="risk in risks" :key="risk.id" class="row-card">
            <div class="row-main">
              <strong>{{ risk.target }} · {{ risk.riskType }}</strong>
              <span>{{ risk.description }} · {{ risk.status }}</span>
            </div>
            <button v-if="risk.status !== 'RESOLVED'" class="ghost-button" @click="resolve(risk.id)">处理</button>
          </article>
        </div>
        <div class="stack-list">
          <article v-for="config in configs" :key="config.id" class="row-card">
            <div class="row-main">
              <strong>{{ config.key }}</strong>
              <span>{{ config.value }} · {{ config.description }}</span>
            </div>
          </article>
        </div>
      </div>
    </section>

    <section class="section-card">
      <div class="section-head">
        <h2>高并发专属模块</h2>
        <button class="accent-button" :disabled="!products.length" @click="createDemoSeckill">创建秒杀</button>
      </div>
      <div v-if="concurrency" class="product-grid">
        <article class="stat-card"><span>库存策略</span><p>{{ concurrency.stockDeductStrategy }}</p></article>
        <article class="stat-card"><span>限流策略</span><p>{{ concurrency.rateLimitStrategy }}</p></article>
        <article class="stat-card"><span>缓存策略</span><p>{{ concurrency.cacheStrategy }}</p></article>
        <article class="stat-card"><span>异步策略</span><p>{{ concurrency.mqStrategy }}</p></article>
      </div>
      <div v-if="board" class="tag-list board-tags">
        <span v-for="item in board.highConcurrencyModules" :key="item" class="tag-chip">{{ item }}</span>
      </div>
    </section>
  </section>
</template>

<script setup>
import { onMounted, reactive, ref } from 'vue'
import {
  createMarketing,
  createProduct,
  createSeckillEvent,
  deleteProduct,
  getAdminOrders,
  getAdminProducts,
  getConfigs,
  getDashboard,
  getHighConcurrency,
  getMarketing,
  getOperationBoard,
  getReports,
  getRiskItems,
  resolveRisk,
  shipOrder,
  updateProduct
} from '../api/admin'
import { getCategories } from '../api/mall'

const dashboard = ref(null)
const products = ref([])
const orders = ref([])
const categories = ref([])
const marketing = ref([])
const configs = ref([])
const risks = ref([])
const reports = ref(null)
const concurrency = ref(null)
const board = ref(null)
const statusText = {
  PENDING_PAYMENT: '待支付', PAID: '待发货', SHIPPED: '待收货', COMPLETED: '已完成', CANCELLED: '已取消'
}
const form = reactive({
  id: null, name: '', subtitle: '', description: '', imageUrl: '', price: 199, stock: 10, categoryId: 1, active: true
})

function resetForm() {
  Object.assign(form, {
    id: null, name: '', subtitle: '', description: '', imageUrl: '', price: 199, stock: 10,
    categoryId: categories.value[0]?.id || 1, active: true
  })
}

function fillDemo() {
  Object.assign(form, {
    id: null,
    name: '护眼桌面灯',
    subtitle: '为学习与居家办公提供柔和照明',
    description: '三档色温、无频闪，适合书桌及办公桌使用。',
    imageUrl: 'https://images.unsplash.com/photo-1505693416388-ac5ce068fe85?auto=format&fit=crop&w=900&q=80',
    price: 269,
    stock: 18,
    categoryId: categories.value[0]?.id || 1,
    active: true
  })
}

function editProduct(product) {
  Object.assign(form, product)
}

async function loadData() {
  const [dashboardRes, productRes, orderRes, categoryRes, marketingRes, configRes, riskRes, reportRes, concurrencyRes, boardRes] = await Promise.all([
    getDashboard(), getAdminProducts(), getAdminOrders(), getCategories(), getMarketing(), getConfigs(),
    getRiskItems(), getReports(), getHighConcurrency(), getOperationBoard()
  ])
  dashboard.value = dashboardRes.data
  products.value = productRes.data
  orders.value = orderRes.data
  categories.value = categoryRes.data
  marketing.value = marketingRes.data
  configs.value = configRes.data
  risks.value = riskRes.data
  reports.value = reportRes.data
  concurrency.value = concurrencyRes.data
  board.value = boardRes.data
}

async function saveProduct() {
  const payload = {
    name: form.name, subtitle: form.subtitle, description: form.description, imageUrl: form.imageUrl,
    price: form.price, stock: form.stock, categoryId: form.categoryId, active: form.active
  }
  if (form.id) await updateProduct(form.id, payload)
  else await createProduct(payload)
  resetForm()
  await loadData()
}

async function removeProduct(id) {
  if (!window.confirm('确定下架该商品吗？历史订单不会受到影响。')) return
  await deleteProduct(id)
  await loadData()
}

async function ship(id) {
  await shipOrder(id)
  await loadData()
}

async function createDemoActivity() {
  await createMarketing({
    title: '平台会场排期',
    type: 'PRE_SALE',
    ruleText: '预售定金膨胀、尾款提醒、库存锁定'
  })
  await loadData()
}

async function resolve(id) {
  await resolveRisk(id)
  await loadData()
}

async function createDemoSeckill() {
  const product = products.value[0]
  await createSeckillEvent({
    productId: product.id,
    seckillPrice: Math.max(1, Number(product.price) * 0.75),
    stock: Math.min(5, product.stock || 5)
  })
  await loadData()
}

onMounted(loadData)
</script>
