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
        <button class="accent-button" @click="showActivityForm = !showActivityForm">
          {{ showActivityForm ? '收起表单' : '新增活动' }}
        </button>
      </div>
      <form v-if="showActivityForm" class="admin-form action-form" @submit.prevent="submitActivity">
        <input v-model="activityForm.title" placeholder="活动名称，如 端午会员专场" required />
        <select v-model="activityForm.type" required>
          <option value="FULL_REDUCTION">满减活动</option>
          <option value="SECKILL">秒杀活动</option>
          <option value="GROUP_BUY">拼团活动</option>
          <option value="PRE_SALE">预售活动</option>
          <option value="DISTRIBUTION">分销活动</option>
          <option value="BUNDLE">组合套餐</option>
        </select>
        <textarea v-model="activityForm.ruleText" placeholder="活动规则，如 满 399 减 60，会员可叠加优惠券" required></textarea>
        <button class="accent-button">保存活动</button>
      </form>
      <p v-if="activityMessage" class="notice-box">{{ activityMessage }}</p>
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
        <button class="accent-button" :disabled="!products.length" @click="showSeckillForm = !showSeckillForm">
          {{ showSeckillForm ? '收起表单' : '创建秒杀' }}
        </button>
      </div>
      <form v-if="showSeckillForm" class="admin-form action-form" @submit.prevent="submitSeckill">
        <select v-model.number="seckillForm.productId" required>
          <option disabled value="">选择秒杀商品</option>
          <option v-for="product in products" :key="product.id" :value="product.id">
            {{ product.name }} · 原价 ￥{{ product.price }} · 库存 {{ product.stock }}
          </option>
        </select>
        <input v-model.number="seckillForm.seckillPrice" type="number" step="0.01" min="0.01" placeholder="秒杀价" required />
        <input v-model.number="seckillForm.stock" type="number" min="1" placeholder="秒杀库存" required />
        <button class="accent-button">保存秒杀活动</button>
      </form>
      <p v-if="seckillMessage" class="notice-box">{{ seckillMessage }}</p>
      <div v-if="concurrency" class="product-grid">
        <article class="stat-card"><span>库存策略</span><p>{{ concurrency.stockDeductStrategy }}</p></article>
        <article class="stat-card"><span>限流策略</span><p>{{ concurrency.rateLimitStrategy }}</p></article>
        <article class="stat-card"><span>缓存策略</span><p>{{ concurrency.cacheStrategy }}</p></article>
        <article class="stat-card"><span>异步策略</span><p>{{ concurrency.mqStrategy }}</p></article>
      </div>
      <div v-if="board" class="tag-list board-tags">
        <span v-for="item in board.highConcurrencyModules" :key="item" class="tag-chip">{{ item }}</span>
      </div>
      <div v-if="seckillEvents.length" class="stack-list seckill-list">
        <article v-for="event in seckillEvents" :key="event.id" class="row-card">
          <div class="row-main">
            <strong>{{ event.productName }} · 秒杀价 ￥{{ event.seckillPrice }}</strong>
            <span>活动库存 {{ event.stock }} · 已抢 {{ event.sold }} · {{ event.active ? '进行中' : '已停用' }}</span>
          </div>
        </article>
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
import { getCategories, getSeckillEvents } from '../api/mall'

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
const seckillEvents = ref([])
const showActivityForm = ref(false)
const showSeckillForm = ref(false)
const activityMessage = ref('')
const seckillMessage = ref('')
const statusText = {
  PENDING_PAYMENT: '待支付', PAID: '待发货', SHIPPED: '待收货', COMPLETED: '已完成', CANCELLED: '已取消'
}
const form = reactive({
  id: null, name: '', subtitle: '', description: '', imageUrl: '', price: 199, stock: 10, categoryId: 1, active: true
})
const activityForm = reactive({
  title: '',
  type: 'FULL_REDUCTION',
  ruleText: ''
})
const seckillForm = reactive({
  productId: '',
  seckillPrice: 99,
  stock: 5
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
  const [dashboardRes, productRes, orderRes, categoryRes, marketingRes, configRes, riskRes, reportRes, concurrencyRes, boardRes, seckillRes] = await Promise.all([
    getDashboard(), getAdminProducts(), getAdminOrders(), getCategories(), getMarketing(), getConfigs(),
    getRiskItems(), getReports(), getHighConcurrency(), getOperationBoard(), getSeckillEvents()
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
  seckillEvents.value = seckillRes.data
  if (!seckillForm.productId && products.value.length) {
    const product = products.value.find(item => item.active && item.stock > 0) || products.value[0]
    seckillForm.productId = product.id
    seckillForm.seckillPrice = Math.max(1, Number(product.price) * 0.75).toFixed(2)
    seckillForm.stock = Math.max(1, Math.min(5, product.stock || 1))
  }
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

async function resolve(id) {
  await resolveRisk(id)
  await loadData()
}

async function submitActivity() {
  activityMessage.value = ''
  try {
    await createMarketing({
      title: activityForm.title.trim(),
      type: activityForm.type,
      ruleText: activityForm.ruleText.trim()
    })
    Object.assign(activityForm, { title: '', type: 'FULL_REDUCTION', ruleText: '' })
    showActivityForm.value = false
    activityMessage.value = '活动已创建，状态为待审核。'
    await loadData()
  } catch (error) {
    activityMessage.value = `活动创建失败：${error}`
  }
}

async function submitSeckill() {
  seckillMessage.value = ''
  try {
    const product = products.value.find(item => item.id === seckillForm.productId)
    if (!product) {
      throw new Error('请选择商品')
    }
    if (Number(seckillForm.stock) > Number(product.stock)) {
      throw new Error(`秒杀库存不能超过商品库存 ${product.stock}`)
    }
    await createSeckillEvent({
      productId: seckillForm.productId,
      seckillPrice: seckillForm.seckillPrice,
      stock: seckillForm.stock
    })
    showSeckillForm.value = false
    seckillMessage.value = '秒杀活动已创建，可在能力中心查看并抢购。'
    await loadData()
  } catch (error) {
    seckillMessage.value = `秒杀创建失败：${error.message || error}`
  }
}

onMounted(loadData)
</script>
