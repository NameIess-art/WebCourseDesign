<template>
  <div class="admin-layout">
    <section class="admin-hero">
      <div>
        <p class="eyebrow">商家中心</p>
        <h1>商家后台</h1>
        <p class="muted">处理商品上架、SKU 库存、订单履约、售后、营销工具和经营数据。</p>
        <div v-if="user?.role === 'ADMIN'" style="margin-top: 1rem;">
          <label for="merchant-select" class="muted" style="margin-right: 0.5rem;">当前管理商家：</label>
          <select id="merchant-select" v-model="selectedMerchantId" @change="loadData" style="padding: 0.5rem; border-radius: 4px; border: 1px solid #e2e8f0;">
            <option v-for="m in merchantsList.content" :key="m.id" :value="m.id">{{ m.title }}</option>
          </select>
        </div>
      </div>
      <button class="ghost-button" :disabled="loading" @click="loadData">刷新数据</button>
    </section>

    <div v-if="notice" class="feedback-banner" :class="notice.type">{{ notice.text }}</div>

    <section id="dashboard" class="module-section">
      <div class="section-head"><div><p class="eyebrow">数据面板</p><h2>数据看板</h2></div></div>
      <div class="stat-grid">
        <article class="stat-card"><span>商品数</span><strong>{{ dashboard.productCount ?? 0 }}</strong></article>
        <article class="stat-card"><span>订单数</span><strong>{{ dashboard.orderCount ?? 0 }}</strong></article>
        <article class="stat-card"><span>待支付订单</span><strong>{{ dashboard.pendingOrderCount ?? 0 }}</strong></article>
        <article class="stat-card"><span>营收</span><strong>¥{{ dashboard.totalRevenue ?? reports.revenue ?? 0 }}</strong></article>
        <article class="stat-card"><span>访客</span><strong>{{ reports.visitorCount ?? 0 }}</strong></article>
        <article class="stat-card"><span>转化</span><strong>{{ reports.conversionCount ?? 0 }}</strong></article>
        <article class="stat-card"><span>库存预警</span><strong>{{ reports.stockWarningCount ?? 0 }}</strong></article>
      </div>
    </section>

    <section id="products" class="module-section">
      <div class="section-head">
        <div><p class="eyebrow">商品管理</p><h2>商品管理</h2></div>
        <button class="ghost-button" @click="resetProduct">新建商品</button>
      </div>
      <div class="dual-grid">
        <form class="admin-form form-panel" @submit.prevent="saveProduct">
          <h3>{{ editingProductId ? '编辑商品' : '上架商品' }}</h3>
          <input v-model="productForm.name" placeholder="商品名称" required />
          <input v-model="productForm.subtitle" placeholder="副标题" required />
          <textarea v-model="productForm.description" placeholder="商品描述" required></textarea>
          <input v-model="productForm.imageUrl" placeholder="图片地址" required />
          <div class="form-row">
            <input v-model.number="productForm.price" type="number" min="0.01" step="0.01" placeholder="价格" required />
            <input v-model.number="productForm.stock" type="number" min="0" placeholder="总库存" required />
          </div>
          <div class="form-row">
            <input v-model.number="productForm.categoryId" type="number" min="1" placeholder="分类 ID" required />
            <select v-model="productForm.active">
              <option :value="true">上架</option>
              <option :value="false">下架</option>
            </select>
          </div>
          <input v-model="productForm.promotionTag" placeholder="促销标签，如 秒杀 / 满减" />

          <div class="nested-editor">
            <div class="section-head compact-head">
              <h4>SKU / 规格 / 库存</h4>
              <button type="button" class="ghost-button" @click="addSku">新增 SKU</button>
            </div>
            <div v-for="(sku, index) in productForm.skus" :key="index" class="inline-edit-row">
              <input v-model="sku.skuCode" placeholder="SKU 编码" required />
              <input v-model="sku.specName" placeholder="规格，如 红色/128G" required />
              <input v-model.number="sku.price" type="number" min="0.01" step="0.01" placeholder="SKU 价格" required />
              <input v-model.number="sku.stock" type="number" min="0" placeholder="SKU 库存" required />
              <button type="button" class="ghost-button" @click="removeSku(index)">删除</button>
            </div>
          </div>

          <div class="nested-editor">
            <div class="section-head compact-head">
              <h4>图文详情</h4>
              <button type="button" class="ghost-button" @click="addDetailBlock">新增段落</button>
            </div>
            <div v-for="(block, index) in productForm.detailBlocks" :key="index" class="inline-edit-row">
              <select v-model="block.blockType">
                <option value="TEXT">文本</option>
                <option value="IMAGE">图片</option>
              </select>
              <input v-model="block.content" placeholder="文本内容或图片地址" required />
              <button type="button" class="ghost-button" @click="removeDetailBlock(index)">删除</button>
            </div>
          </div>
          <button class="accent-button" :disabled="loading">{{ editingProductId ? '保存商品' : '创建商品' }}</button>
        </form>

        <div class="stack-list">
          <article v-for="item in products.content" :key="item.id" class="row-card">
            <div class="row-main">
              <strong>{{ item.name }}</strong>
              <span class="muted">¥{{ item.price }} · 库存 {{ item.stock }} · {{ item.active ? '已上架' : '已下架' }}</span>
              <span class="muted">SKU {{ item.skus?.length || 0 }} 个 · {{ item.promotionTag || '无促销标签' }}</span>
            </div>
            <div class="row-actions">
              <button class="ghost-button" @click="editProduct(item)">编辑</button>
              <button class="ghost-button" @click="toggleProduct(item)">{{ item.active ? '下架' : '上架' }}</button>
              <button class="ghost-button" @click="removeProduct(item.id)">删除</button>
            </div>
          </article>
          <div class="pager">
            <button class="ghost-button" :disabled="productPage.first" @click="loadProducts(productPage.page - 1)">上一页</button>
            <span>第 {{ productPage.page + 1 }} / {{ Math.max(productPage.totalPages, 1) }} 页</span>
            <button class="ghost-button" :disabled="productPage.last" @click="loadProducts(productPage.page + 1)">下一页</button>
          </div>
        </div>
      </div>
    </section>

    <section id="orders" class="module-section">
      <div class="section-head">
        <div><p class="eyebrow">订单管理</p><h2>订单处理</h2></div>
        <button class="ghost-button" :disabled="loading" @click="createReconciliationForToday">生成今日对账</button>
      </div>
      <div class="dual-grid">
        <div class="stack-list">
          <article v-for="order in orders.content" :key="order.id" class="order-card">
            <div class="section-head">
              <div>
                <strong>{{ order.orderNo }}</strong>
                <p class="muted">状态 {{ formatStatus(order.status) }} · 审核 {{ order.auditStatus || '待处理' }} · ¥{{ order.totalAmount }}</p>
              </div>
              <div class="row-actions">
                <button class="ghost-button" @click="audit(order, true)">审核通过</button>
                <button class="ghost-button" @click="audit(order, false)">驳回</button>
                <button class="ghost-button" @click="ship(order.id)">发货</button>
              </div>
            </div>
            <form class="inline-form" @submit.prevent="modify(order)">
              <input v-model="order.shippingAddress" placeholder="收货地址" required />
              <input v-model.number="order.totalAmount" type="number" min="0.01" step="0.01" placeholder="订单金额" required />
              <button class="accent-button compact" :disabled="loading">改单</button>
            </form>
          </article>
        </div>
        <div class="stack-list">
          <article class="notice-box">
            <strong>售后处理</strong>
            <span class="muted">退款、退货、换货申请在这里处理，并同步站内消息给用户。</span>
          </article>
          <article v-for="item in afterSales.content" :key="item.id" class="row-card">
            <div class="row-main">
              <strong>{{ item.orderNo }} · {{ formatStatus(item.type) }}</strong>
              <span class="muted">{{ item.reason }} · {{ formatStatus(item.status) }}</span>
            </div>
            <div class="row-actions">
              <button class="ghost-button" @click="processSale(item, 'APPROVED')">通过</button>
              <button class="ghost-button" @click="processSale(item, 'REJECTED')">拒绝</button>
            </div>
          </article>
        </div>
      </div>
    </section>

    <section id="marketing" class="module-section">
      <div class="section-head"><div><p class="eyebrow">营销中心</p><h2>营销工具</h2></div></div>
      <div class="dual-grid">
        <form class="admin-form form-panel" @submit.prevent="saveMarketing">
          <h3>限时活动 / 拼团 / 分销 / 预售 / 组合套餐</h3>
          <input v-model="marketingForm.title" placeholder="活动名称" required />
          <select v-model="marketingForm.type">
            <option value="LIMITED_TIME">限时活动</option>
            <option value="GROUP_BUY">拼团</option>
            <option value="DISTRIBUTION">分销</option>
            <option value="PRE_SALE">预售</option>
            <option value="BUNDLE">组合套餐</option>
          </select>
          <textarea v-model="marketingForm.ruleText" placeholder="活动规则、库存锁定、佣金或尾款说明" required></textarea>
          <button class="accent-button" :disabled="loading">新增活动</button>
        </form>
        <form class="admin-form form-panel" @submit.prevent="saveSeckill">
          <h3>创建秒杀</h3>
          <select v-model.number="seckillForm.productId" required>
            <option disabled :value="null">选择商品</option>
            <option v-for="product in products.content" :key="product.id" :value="product.id">{{ product.name }}</option>
          </select>
          <input v-model.number="seckillForm.seckillPrice" type="number" min="0.01" step="0.01" placeholder="秒杀价" required />
          <input v-model.number="seckillForm.stock" type="number" min="1" placeholder="秒杀库存" required />
          <button class="accent-button" :disabled="loading">创建秒杀</button>
        </form>
      </div>
      <div class="module-grid">
        <article v-for="activity in marketing.content" :key="activity.id" class="row-card">
          <div class="row-main">
            <strong>{{ activity.title }} · {{ formatStatus(activity.type) }}</strong>
            <span class="muted">{{ activity.ruleText }} · {{ formatStatus(activity.status) }}</span>
          </div>
        </article>
        <article v-for="flow in marketingFlows.content" :key="flow.id" class="row-card">
          <div class="row-main">
            <strong>{{ flow.title }} · {{ formatStatus(flow.type) }}</strong>
            <span class="muted">{{ flow.content }} · {{ formatStatus(flow.status) }}</span>
          </div>
        </article>
      </div>
    </section>
  </div>
</template>

<script setup>
import { formatStatus } from '../utils/format'
import { onMounted, reactive, ref } from 'vue'
import Pagination from '../components/Pagination.vue'
import { getUser } from '../utils/auth'
import {
  auditOrder,
  createMarketing,
  createReconciliation,
  createSeckillEvent,
  createProduct,
  deleteProduct,
  getAdminAfterSales,
  getAdminOrders,
  getAdminProducts,
  getDashboard,
  getMarketing,
  getMarketingFlows,
  getReports,
  getMerchants,
  modifyOrder,
  processAfterSale,
  shipOrder,
  updateProduct
} from '../api/admin'

const loading = ref(false)
const notice = ref(null)
const dashboard = ref({})
const reports = ref({})
const products = ref({ content: [], page: 0, totalPages: 0 })
const orders = ref({ content: [], page: 0, totalPages: 0 })
const afterSales = ref({ content: [], page: 0, totalPages: 0 })
const marketing = ref({ content: [], page: 0, totalPages: 0 })
const marketingFlows = ref({ content: [], page: 0, totalPages: 0 })
const user = ref(getUser())
const merchantsList = ref({ content: [], page: 0, totalPages: 0 })
const selectedMerchantId = ref(localStorage.getItem('merchantId') ? Number(localStorage.getItem('merchantId')) : null)
const editingProductId = ref(null)
const productPage = reactive({ page: 0, size: 20, totalPages: 0, first: true, last: true })

const productForm = reactive(defaultProduct())
const marketingForm = reactive({ title: '', type: 'LIMITED_TIME', ruleText: '' })
const seckillForm = reactive({ productId: null, seckillPrice: null, stock: 10 })

onMounted(loadData)

function defaultProduct() {
  return {
    name: '',
    subtitle: '',
    description: '',
    imageUrl: 'https://images.unsplash.com/photo-1516321318423-f06f85e504b3?auto=format&fit=crop&w=900&q=80',
    price: 99,
    stock: 100,
    categoryId: 1,
    active: true,
    skuCode: 'SKU-' + Date.now(),
    spec: '默认规格',
    promotionTag: '',
    skus: [{ skuCode: 'SKU-' + Date.now(), specName: '默认规格', price: 99, stock: 100, active: true }],
    detailBlocks: [{ blockType: 'TEXT', content: '商品图文详情', sortOrder: 1 }]
  }
}

function resetReactive(target, source) {
  Object.keys(target).forEach((key) => delete target[key])
  Object.assign(target, source)
}

async function runAction(successText, action) {
  loading.value = true
  notice.value = null
  try {
    await action()
    notice.value = { type: 'success', text: successText }
    await loadData()
  } catch (error) {
    notice.value = { type: 'error', text: String(error) }
  } finally {
    loading.value = false
  }
}

async function loadProducts(page = productPage.page) {
  const productRes = await getAdminProducts(page, productPage.size)
  const data = productRes.data || {}
  products.value = data || { content: [], page: 0, totalPages: 0 }
  Object.assign(productPage, {
    page: data.page ?? 0,
    size: data.size ?? productPage.size,
    totalPages: data.totalPages ?? 1,
    first: data.first ?? true,
    last: data.last ?? true
  })
}

async function loadData() {
  if (user.value?.role === 'ADMIN') {
    try {
      const merchantRes = await getMerchants()
      merchantsList.value = merchantRes.data || { content: [], page: 0, totalPages: 0 }
      
      // 如果还没有选择商家，默认选择第一个商家。
      if (!selectedMerchantId.value && merchantsList.value.length > 0) {
        selectedMerchantId.value = merchantsList.value[0].id
      }
    } catch (e) {
      console.error("Failed to load merchants", e)
    }
  }

  if (selectedMerchantId.value) {
    localStorage.setItem('merchantId', selectedMerchantId.value)
  } else {
    localStorage.removeItem('merchantId')
    if (user.value?.role === 'ADMIN') {
      return // 管理员必须先选择一个商家才能继续操作。
    }
  }

  try {
    const [dashboardRes, orderRes, afterSaleRes, marketingRes, flowRes, reportRes] = await Promise.all([
      getDashboard(),
      getAdminOrders(),
      getAdminAfterSales(),
      getMarketing(),
      getMarketingFlows(),
      getReports()
    ])
    await loadProducts(productPage.page)

    dashboard.value = dashboardRes.data || {}
    orders.value = orderRes.data || { content: [], page: 0, totalPages: 0 }
    afterSales.value = afterSaleRes.data || { content: [], page: 0, totalPages: 0 }
    marketing.value = marketingRes.data || { content: [], page: 0, totalPages: 0 }
    marketingFlows.value = flowRes.data || { content: [], page: 0, totalPages: 0 }
    reports.value = reportRes.data || {}
  } catch (error) {
    notice.value = { type: 'error', text: String(error) }
  }
}

function resetProduct() {
  editingProductId.value = null
  resetReactive(productForm, defaultProduct())
}

function editProduct(product) {
  editingProductId.value = product.id
  resetReactive(productForm, {
    name: product.name,
    subtitle: product.subtitle,
    description: product.description,
    imageUrl: product.imageUrl,
    price: Number(product.price),
    stock: product.stock,
    categoryId: product.categoryId || 1,
    active: Boolean(product.active),
    skuCode: product.skuCode || `SKU-${product.id}`,
    spec: product.spec || '默认规格',
    promotionTag: product.promotionTag || '',
    skus: product.skus?.length ? product.skus.map((sku) => ({ ...sku, active: sku.active ?? true })) : defaultProduct().skus,
    detailBlocks: product.detailBlocks?.length ? product.detailBlocks.map((block) => ({ ...block })) : defaultProduct().detailBlocks
  })
}

function addSku() {
  productForm.skus.push({ skuCode: 'SKU-' + Date.now(), specName: '新规格', price: productForm.price, stock: productForm.stock, active: true })
}

function removeSku(index) {
  productForm.skus.splice(index, 1)
}

function addDetailBlock() {
  productForm.detailBlocks.push({ blockType: 'TEXT', content: '', sortOrder: productForm.detailBlocks.length + 1 })
}

function removeDetailBlock(index) {
  productForm.detailBlocks.splice(index, 1)
}

async function saveProduct() {
  await runAction(editingProductId.value ? '商品已保存' : '商品已创建', async () => {
    // 商品表单对象与后端商品请求体一致，包含主字段、规格列表和详情段落。
    if (editingProductId.value) await updateProduct(editingProductId.value, productForm)
    else await createProduct(productForm)
    resetProduct()
  })
}

async function toggleProduct(product) {
  await runAction(product.active ? '商品已下架' : '商品已上架', () => updateProduct(product.id, { ...product, active: !product.active }))
}

async function removeProduct(id) {
  await runAction('商品已删除', () => deleteProduct(id))
}

async function audit(order, approved) {
  await runAction(approved ? '订单审核通过' : '订单已驳回', () => auditOrder(order.id, { approved, remark: approved ? '商家审核通过' : '信息不完整，驳回' }))
}

async function ship(id) {
  await runAction('订单已发货', () => shipOrder(id))
}

async function modify(order) {
  await runAction('订单已修改', () => modifyOrder(order.id, {
    shippingAddress: order.shippingAddress || '默认地址',
    totalAmount: order.totalAmount,
    remark: '商家后台改单'
  }))
}

async function processSale(item, status) {
  await runAction('售后状态已更新', () => processAfterSale(item.id, { status, remark: status === 'APPROVED' ? '同意售后' : '拒绝售后' }))
}

async function createReconciliationForToday() {
  await runAction('今日对账已生成', () => createReconciliation(new Date().toISOString().slice(0, 10)))
}

async function saveMarketing() {
  await runAction('营销活动已创建，等待平台审核', async () => {
    await createMarketing(marketingForm)
    marketingForm.title = ''
    marketingForm.ruleText = ''
    marketingForm.type = 'LIMITED_TIME'
  })
}

async function saveSeckill() {
  await runAction('秒杀活动已创建', () => createSeckillEvent(seckillForm))
}
</script>
