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
        <div class="stat-card"><span>有效营收</span><strong>¥{{ dashboard.totalRevenue }}</strong></div>
      </div>
    </div>

    <section class="section-card">
      <div class="section-head">
        <h2>商品管理</h2>
        <button class="accent-button" @click="fillDemo">填入示例</button>
      </div>
      <form class="admin-form" @submit.prevent="saveProduct">
        <input v-model="productForm.name" placeholder="商品名称" required />
        <input v-model="productForm.subtitle" placeholder="商品副标题" required />
        <input v-model="productForm.imageUrl" placeholder="图片 URL" required />
        <input v-model.number="productForm.price" type="number" step="0.01" min="0.01" placeholder="价格" required />
        <input v-model.number="productForm.stock" type="number" min="0" placeholder="库存" required />
        <input v-model="productForm.skuCode" placeholder="SPU/SKU 编码" required />
        <input v-model="productForm.spec" placeholder="默认规格" required />
        <input v-model="productForm.promotionTag" placeholder="营销标签" />
        <select v-model.number="productForm.categoryId" required>
          <option v-for="category in categories" :key="category.id" :value="category.id">{{ category.name }}</option>
        </select>
        <textarea v-model="productForm.description" placeholder="商品详情说明" required></textarea>
        <textarea v-model="skuLines" placeholder="SKU 明细：每行 skuCode|规格名|价格|库存，例如 SKU-RED|红色 128G|599|20"></textarea>
        <textarea v-model="detailLines" placeholder="图文详情：每行 类型|内容，例如 TEXT|支持七天无理由退换"></textarea>
        <label class="inline-meta">
          <input v-model="productForm.active" type="checkbox" />
          <span>上架销售</span>
        </label>
        <button class="accent-button">{{ productForm.id ? '更新商品' : '创建商品' }}</button>
      </form>

      <div class="stack-list">
        <article v-for="product in products" :key="product.id" class="row-card">
          <div class="row-main">
            <strong>{{ product.name }}</strong>
            <span>{{ product.categoryName }} · ¥{{ product.price }} · 库存 {{ product.stock }} · {{ product.active ? '已上架' : '已下架' }}</span>
            <span>SKU {{ product.skus?.length || 0 }} 个，详情块 {{ product.detailBlocks?.length || 0 }} 个</span>
          </div>
          <div class="row-actions">
            <button class="ghost-button" @click="editProduct(product)">编辑</button>
            <button v-if="product.active" class="ghost-button" @click="removeProduct(product.id)">下架</button>
          </div>
        </article>
      </div>
    </section>

    <section class="section-card">
      <div class="section-head">
        <h2>订单履约</h2>
        <button class="accent-button" @click="runReconciliation">生成今日对账</button>
      </div>
      <div class="stack-list">
        <article v-for="order in orders" :key="order.id" class="row-card">
          <div class="row-main">
            <strong>{{ order.orderNo }}</strong>
            <span>{{ statusText[order.status] || order.status }} · 审核 {{ order.auditStatus }} · ¥{{ order.totalAmount }}</span>
          </div>
          <div class="row-actions">
            <button class="ghost-button" @click="audit(order.id, true)">审核通过</button>
            <button class="ghost-button" @click="audit(order.id, false)">驳回</button>
            <button class="ghost-button" @click="modify(order)">改单</button>
            <button v-if="order.status === 'PAID'" class="accent-button" @click="ship(order.id)">发货</button>
          </div>
        </article>
      </div>
      <div class="stack-list">
        <h3>售后处理</h3>
        <article v-for="record in afterSales" :key="record.id" class="row-card">
          <div class="row-main">
            <strong>{{ record.orderNo }} · {{ record.type }}</strong>
            <span>{{ record.reason }} · {{ record.status }}</span>
          </div>
          <button class="ghost-button" @click="processSale(record.id)">处理</button>
        </article>
      </div>
    </section>

    <section class="section-card">
      <div class="section-head">
        <h2>运营与营销</h2>
        <button class="accent-button" @click="submitActivity">新增活动</button>
      </div>
      <form class="admin-form action-form" @submit.prevent="submitActivity">
        <input v-model="activityForm.title" placeholder="活动名称" required />
        <select v-model="activityForm.type" required>
          <option value="FULL_REDUCTION">满减</option>
          <option value="SECKILL">秒杀</option>
          <option value="GROUP_BUY">拼团</option>
          <option value="PRE_SALE">预售</option>
          <option value="DISTRIBUTION">分销</option>
          <option value="BUNDLE">组合套餐</option>
        </select>
        <textarea v-model="activityForm.ruleText" placeholder="活动规则，可自定义会场、门槛、权益、排期说明" required></textarea>
      </form>
      <div class="dual-grid">
        <div class="stack-list">
          <article v-for="activity in marketing" :key="activity.id" class="row-card">
            <div class="row-main">
              <strong>{{ activity.title }} · {{ activity.type }}</strong>
              <span>{{ activity.ruleText }} · {{ activity.status }}</span>
            </div>
            <button class="ghost-button" @click="approveActivity(activity.id)">审核通过</button>
          </article>
        </div>
        <div class="stack-list">
          <h3>营销闭环</h3>
          <form class="admin-form" @submit.prevent="submitMarketingFlow">
            <select v-model="flowForm.type">
              <option value="group-buy">拼团</option>
              <option value="presale">预售</option>
              <option value="bundle">组合套餐</option>
              <option value="distribution">分销</option>
            </select>
            <input v-model="flowForm.title" placeholder="流程名称" required />
            <input v-model="flowForm.amount" placeholder="金额/佣金/套餐价" />
            <textarea v-model="flowForm.content" placeholder="流程说明" required></textarea>
            <button class="accent-button">创建营销流程</button>
          </form>
          <article v-for="flow in marketingFlows" :key="flow.id" class="row-card">
            <div class="row-main">
              <strong>{{ flow.title }} · {{ flow.type }}</strong>
              <span>{{ flow.content }} · {{ flow.status }}</span>
            </div>
          </article>
        </div>
      </div>
    </section>

    <section class="section-card">
      <div class="section-head">
        <h2>高并发专属模块</h2>
        <button class="accent-button" @click="submitSeckill">创建秒杀</button>
      </div>
      <form class="admin-form action-form" @submit.prevent="submitSeckill">
        <select v-model.number="seckillForm.productId" required>
          <option disabled value="">选择秒杀商品</option>
          <option v-for="product in products" :key="product.id" :value="product.id">
            {{ product.name }} · 原价 ¥{{ product.price }} · 库存 {{ product.stock }}
          </option>
        </select>
        <input v-model.number="seckillForm.seckillPrice" type="number" step="0.01" min="0.01" placeholder="秒杀价" required />
        <input v-model.number="seckillForm.stock" type="number" min="1" placeholder="秒杀库存" required />
      </form>
      <div v-if="concurrency" class="product-grid">
        <article class="stat-card"><span>库存策略</span><p>{{ concurrency.stockDeductStrategy }}</p></article>
        <article class="stat-card"><span>限流策略</span><p>{{ concurrency.rateLimitStrategy }}</p></article>
        <article class="stat-card"><span>缓存策略</span><p>{{ concurrency.cacheStrategy }}</p></article>
        <article class="stat-card"><span>异步策略</span><p>{{ concurrency.mqStrategy }}</p></article>
      </div>
    </section>

    <section class="section-card">
      <div class="section-head"><h2>权限风控与配置中心</h2></div>
      <div class="dual-grid">
        <div class="stack-list">
          <form class="admin-form" @submit.prevent="saveConfig">
            <input v-model="configForm.key" placeholder="配置 key" required />
            <input v-model="configForm.value" placeholder="配置值" required />
            <input v-model="configForm.description" placeholder="说明" required />
            <button class="accent-button">保存配置</button>
          </form>
          <article v-for="config in configs" :key="config.id" class="row-card">
            <div class="row-main">
              <strong>{{ config.key }}</strong>
              <span>{{ config.value }} · {{ config.description }}</span>
            </div>
          </article>
        </div>
        <div class="stack-list">
          <article v-for="risk in risks" :key="risk.id" class="row-card">
            <div class="row-main">
              <strong>{{ risk.target }} · {{ risk.riskType }}</strong>
              <span>{{ risk.description }} · {{ risk.status }}</span>
            </div>
            <button v-if="risk.status !== 'RESOLVED'" class="ghost-button" @click="resolve(risk.id)">处理</button>
          </article>
        </div>
      </div>
      <div class="product-grid">
        <article class="stat-card">
          <h3>RBAC</h3>
          <p v-for="role in roles" :key="role.id">{{ role.title }} · {{ role.type }}</p>
        </article>
        <article class="stat-card">
          <h3>商家管理</h3>
          <p v-for="merchant in merchants" :key="merchant.id">{{ merchant.title }} · {{ merchant.status }}</p>
        </article>
        <article class="stat-card">
          <h3>内容审核</h3>
          <p v-for="item in audits" :key="item.id">{{ item.title }} · {{ item.status }}</p>
        </article>
        <article class="stat-card">
          <h3>数据分析</h3>
          <p v-for="item in analytics" :key="item.id">{{ item.title }}：{{ item.content }}</p>
        </article>
      </div>
    </section>
  </section>
</template>

<script setup>
import { onMounted, reactive, ref } from 'vue'
import {
  auditActivity,
  auditOrder,
  createMarketing,
  createMarketingFlow,
  createProduct,
  createReconciliation,
  createSeckillEvent,
  deleteProduct,
  getAdminAfterSales,
  getAdminOrders,
  getAdminProducts,
  getAnalytics,
  getConfigs,
  getContentAudits,
  getDashboard,
  getHighConcurrency,
  getMarketing,
  getMarketingFlows,
  getMerchants,
  getRiskItems,
  getRoles,
  modifyOrder,
  processAfterSale,
  resolveRisk,
  shipOrder,
  updateConfig,
  updateProduct
} from '../api/admin'
import { getCategories } from '../api/mall'

const dashboard = ref(null)
const products = ref([])
const orders = ref([])
const categories = ref([])
const marketing = ref([])
const marketingFlows = ref([])
const configs = ref([])
const risks = ref([])
const afterSales = ref([])
const roles = ref([])
const merchants = ref([])
const audits = ref([])
const analytics = ref([])
const concurrency = ref(null)
const skuLines = ref('')
const detailLines = ref('')
const statusText = {
  PENDING_PAYMENT: '待支付',
  PAID: '待发货',
  SHIPPED: '待收货',
  COMPLETED: '已完成',
  CANCELLED: '已取消'
}

const productForm = reactive({
  id: null,
  name: '',
  subtitle: '',
  description: '',
  imageUrl: '',
  price: 199,
  stock: 10,
  skuCode: 'SKU-DEMO',
  spec: 'Standard',
  promotionTag: 'Platform Pick',
  categoryId: 1,
  active: true
})
const activityForm = reactive({ title: '', type: 'FULL_REDUCTION', ruleText: '' })
const seckillForm = reactive({ productId: '', seckillPrice: 99, stock: 5 })
const flowForm = reactive({ type: 'group-buy', title: '', amount: '0', content: '' })
const configForm = reactive({ key: '', value: '', description: '' })

function resetProductForm() {
  Object.assign(productForm, {
    id: null,
    name: '',
    subtitle: '',
    description: '',
    imageUrl: '',
    price: 199,
    stock: 10,
    skuCode: 'SKU-DEMO',
    spec: 'Standard',
    promotionTag: 'Platform Pick',
    categoryId: categories.value[0]?.id || 1,
    active: true
  })
  skuLines.value = ''
  detailLines.value = ''
}

function fillDemo() {
  Object.assign(productForm, {
    id: null,
    name: '护眼桌面灯',
    subtitle: '为学习与办公提供柔和照明',
    description: '三档色温、低频闪，适合书桌和办公桌使用。',
    imageUrl: 'https://images.unsplash.com/photo-1505693416388-ac5ce068fe85?auto=format&fit=crop&w=900&q=80',
    price: 269,
    stock: 18,
    skuCode: 'SKU-LAMP',
    spec: 'White / Standard',
    promotionTag: 'Presale',
    categoryId: categories.value[0]?.id || 1,
    active: true
  })
  skuLines.value = 'SKU-LAMP-W|白色 标准版|269|12\nSKU-LAMP-B|黑色 标准版|279|6'
  detailLines.value = 'TEXT|支持三档色温和触控调光\nTEXT|适合学生、设计师和居家办公场景'
}

function parseSkus() {
  return skuLines.value.split('\n').map(line => line.trim()).filter(Boolean).map(line => {
    const [skuCode, specName, price, stock] = line.split('|')
    return { skuCode, specName, price: Number(price), stock: Number(stock), active: true }
  })
}

function parseDetails() {
  return detailLines.value.split('\n').map((line, index) => line.trim()).filter(Boolean).map((line, index) => {
    const [blockType, content] = line.split('|')
    return { blockType: blockType || 'TEXT', content: content || line, sortOrder: index + 1 }
  })
}

function editProduct(product) {
  Object.assign(productForm, product)
  skuLines.value = (product.skus || []).map(item => `${item.skuCode}|${item.specName}|${item.price}|${item.stock}`).join('\n')
  detailLines.value = (product.detailBlocks || []).map(item => `${item.blockType}|${item.content}`).join('\n')
}

async function loadData() {
  const [
    dashboardRes, productRes, orderRes, categoryRes, marketingRes, configRes, riskRes, concurrencyRes,
    afterSaleRes, rolesRes, merchantRes, auditRes, analyticsRes, flowRes
  ] = await Promise.all([
    getDashboard(), getAdminProducts(), getAdminOrders(), getCategories(), getMarketing(), getConfigs(), getRiskItems(),
    getHighConcurrency(), getAdminAfterSales(), getRoles(), getMerchants(), getContentAudits(), getAnalytics(), getMarketingFlows()
  ])
  dashboard.value = dashboardRes.data
  products.value = productRes.data
  orders.value = orderRes.data
  categories.value = categoryRes.data
  marketing.value = marketingRes.data
  configs.value = configRes.data
  risks.value = riskRes.data
  concurrency.value = concurrencyRes.data
  afterSales.value = afterSaleRes.data
  roles.value = rolesRes.data
  merchants.value = merchantRes.data
  audits.value = auditRes.data
  analytics.value = analyticsRes.data
  marketingFlows.value = flowRes.data
  if (!seckillForm.productId && products.value.length) {
    const product = products.value.find(item => item.active && item.stock > 0) || products.value[0]
    seckillForm.productId = product.id
    seckillForm.seckillPrice = Math.max(1, Number(product.price) * 0.75).toFixed(2)
    seckillForm.stock = Math.max(1, Math.min(5, product.stock || 1))
  }
}

async function saveProduct() {
  const payload = {
    name: productForm.name,
    subtitle: productForm.subtitle,
    description: productForm.description,
    imageUrl: productForm.imageUrl,
    price: productForm.price,
    stock: productForm.stock,
    skuCode: productForm.skuCode,
    spec: productForm.spec,
    promotionTag: productForm.promotionTag,
    categoryId: productForm.categoryId,
    active: productForm.active,
    skus: parseSkus(),
    detailBlocks: parseDetails()
  }
  if (productForm.id) await updateProduct(productForm.id, payload)
  else await createProduct(payload)
  resetProductForm()
  await loadData()
}

async function removeProduct(id) {
  if (!window.confirm('确定下架该商品吗？')) return
  await deleteProduct(id)
  await loadData()
}

async function ship(id) {
  await shipOrder(id)
  await loadData()
}

async function audit(id, approved) {
  await auditOrder(id, { approved, remark: approved ? 'Merchant audit approved' : 'Merchant audit rejected' })
  await loadData()
}

async function modify(order) {
  const shippingAddress = window.prompt('新的收货地址', order.shippingAddress)
  if (!shippingAddress) return
  const totalAmount = window.prompt('新的订单金额', order.totalAmount)
  await modifyOrder(order.id, { shippingAddress, totalAmount: Number(totalAmount), remark: 'Merchant modified order' })
  await loadData()
}

async function processSale(id) {
  const status = window.prompt('售后状态', 'APPROVED')
  if (!status) return
  await processAfterSale(id, { status, remark: 'Processed by merchant backend' })
  await loadData()
}

async function runReconciliation() {
  await createReconciliation()
  await loadData()
}

async function submitActivity() {
  if (!activityForm.title.trim() || !activityForm.ruleText.trim()) return
  await createMarketing({ ...activityForm })
  Object.assign(activityForm, { title: '', type: 'FULL_REDUCTION', ruleText: '' })
  await loadData()
}

async function approveActivity(id) {
  await auditActivity(id, true)
  await loadData()
}

async function submitMarketingFlow() {
  if (!flowForm.title.trim()) return
  await createMarketingFlow(flowForm.type, {
    title: flowForm.title,
    type: flowForm.amount,
    content: flowForm.content
  })
  Object.assign(flowForm, { type: 'group-buy', title: '', amount: '0', content: '' })
  await loadData()
}

async function submitSeckill() {
  const product = products.value.find(item => item.id === seckillForm.productId)
  if (!product) {
    window.alert('请选择商品。')
    return
  }
  if (Number(seckillForm.stock) > Number(product.stock)) {
    window.alert(`秒杀库存不能超过商品库存 ${product.stock}`)
    return
  }
  await createSeckillEvent({
    productId: seckillForm.productId,
    seckillPrice: seckillForm.seckillPrice,
    stock: seckillForm.stock
  })
  window.alert('秒杀活动已创建。')
  await loadData()
}

async function saveConfig() {
  if (!configForm.key.trim()) return
  await updateConfig(configForm.key, {
    value: configForm.value,
    description: configForm.description
  })
  Object.assign(configForm, { key: '', value: '', description: '' })
  await loadData()
}

async function resolve(id) {
  await resolveRisk(id)
  await loadData()
}

onMounted(loadData)
</script>
