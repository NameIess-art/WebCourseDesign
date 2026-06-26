<template>
  <section class="order-layout">
    <section class="section-card">
      <div class="section-head">
        <h1>我的订单</h1>
        <button class="ghost-button" :disabled="loading" @click="loadOrders(pageInfo.page)">刷新</button>
      </div>

      <div v-if="loading" class="notice-box">
        <strong>正在加载订单</strong>
        <span class="muted">支付、物流和售后状态正在同步。</span>
      </div>
      <div v-else-if="orders.content.length" class="stack-list">
        <article v-for="order in orders.content" :key="order.id" class="order-card">
          <div class="section-head">
            <div>
              <strong>{{ order.orderNo }}</strong>
              <p>{{ formatDate(order.createdAt) }}</p>
            </div>
            <span class="tag-chip">{{ statusText[formatStatus(order.status)] || formatStatus(order.status) }}</span>
          </div>
          <div class="stack-list small-gap">
            <div v-for="item in order.items" :key="item.id" class="inline-meta">
              <span>{{ item.productName }} · {{ item.skuName || '默认规格' }}</span>
              <span>{{ item.quantity }} x ¥{{ item.price }}</span>
            </div>
          </div>
          <div class="notice-box">
            <span>原价 ¥{{ order.originalAmount }}</span>
            <span>优惠 -¥{{ order.discountAmount }}，积分 -¥{{ order.pointsDiscountAmount }}</span>
            <strong>实付 ¥{{ order.totalAmount }}</strong>
            <span v-if="order.paymentChannel">支付渠道：{{ channelText[order.paymentChannel] || order.paymentChannel }}</span>
          </div>
          <div class="order-footer">
            <div class="row-actions" v-if="order.status === 'PENDING_PAYMENT'">
              <button class="accent-button" @click="pay(order.id, 'ALIPAY')">支付宝</button>
              <button class="accent-button" @click="pay(order.id, 'WECHAT')">微信</button>
              <button class="accent-button" @click="pay(order.id, 'BANK')">银行卡</button>
              <button class="ghost-button" @click="cancel(order.id)">取消</button>
            </div>
            <div class="row-actions">
              <button class="ghost-button" @click="viewBill(order)">查看账单</button>
              <button v-if="order.status === '已发货'" class="ghost-button" @click="receive(order.id)">确认收货</button>
              <button class="ghost-button" @click="showLogistics(order.id)">物流</button>
              <button v-if="['已支付','已发货','已完成'].includes(order.status)" class="ghost-button" @click="afterSale(order.id)">售后</button>
            </div>
          </div>
          <div v-if="logistics[order.id]" class="notice-box">
            <strong>{{ logistics[order.id].carrier }} · {{ logistics[order.id].trackingNo }}</strong>
            <p v-for="trace in logistics[order.id].traces" :key="trace">{{ trace }}</p>
          </div>
        </article>
      </div>
      <p v-else class="muted">暂无订单。</p>
      <div class="pager">
        <button class="ghost-button" :disabled="pageInfo.first || loading" @click="loadOrders(pageInfo.page - 1)">上一页</button>
        <span>第 {{ pageInfo.page + 1 }} / {{ Math.max(pageInfo.totalPages, 1) }} 页</span>
        <button class="ghost-button" :disabled="pageInfo.last || loading" @click="loadOrders(pageInfo.page + 1)">下一页</button>
      </div>
    </section>

    <section class="section-card">
      <div class="section-head"><h2>售后记录</h2></div>
      <div class="stack-list">
        <article v-for="record in afterSales.content" :key="record.id" class="row-card">
          <div class="row-main">
            <strong>{{ record.orderNo }} · {{ saleTypeText[formatStatus(record.type)] || formatStatus(record.type) }}</strong>
            <span>{{ record.reason }} · {{ formatStatus(record.status) }}</span>
          </div>
        </article>
        <p v-if="!afterSales.content.length" class="muted">暂无售后记录。</p>
      </div>
    </section>
    
    <BillModal v-model:visible="showBillModal" :order="selectedOrder" />
  </section>
</template>

<script setup>
import { formatStatus } from '../utils/format'
import { onMounted, reactive, ref } from 'vue'
import Pagination from '../components/Pagination.vue'
import BillModal from '../components/BillModal.vue'
import {
  cancelOrder,
  createPayment,
  getAfterSales,
  getLogistics,
  getOrders,
  mockPaymentCallback,
  receiveOrder,
  requestAfterSale
} from '../api/mall'

const orders = ref({ content: [], page: 0, totalPages: 0 })
const afterSales = ref({ content: [], page: 0, totalPages: 0 })
const logistics = ref({})
const loading = ref(false)
const showBillModal = ref(false)
const selectedOrder = ref({})
const pageInfo = reactive({ page: 0, size: 10, totalPages: 0, first: true, last: true })
const statusText = {
  PENDING_PAYMENT: '待支付',
  PAID: '待发货',
  SHIPPED: '待收货',
  COMPLETED: '已完成',
  CANCELLED: '已取消'
}
const channelText = { ALIPAY: '支付宝', WECHAT: '微信', BANK: '银行卡', BALANCE: '余额' }
const saleTypeText = { REFUND: '退款', RETURN: '退货', EXCHANGE: '换货', REFUND_RETURN: '退款退货' }

const formatDate = (value) => new Date(value).toLocaleString('zh-CN')

async function loadOrders(page = 0) {
  loading.value = true
  try {
    const [orderRes, afterSaleRes] = await Promise.all([getOrders(page, pageInfo.size), getAfterSales()])
    const data = orderRes.data
    orders.value = data || { content: [], page: 0, totalPages: 0 }
    afterSales.value = afterSaleRes.data
    Object.assign(pageInfo, {
      page: data.page ?? 0,
      size: data.size ?? pageInfo.size,
      totalPages: data.totalPages ?? 1,
      first: data.first ?? true,
      last: data.last ?? true
    })
  } finally {
    loading.value = false
  }
}

async function pay(id, channel) {
  try {
    const payment = await createPayment({
      orderId: id,
      channel,
      idempotencyKey: `payment-${id}-${channel}-${Date.now()}`
    })
    await mockPaymentCallback({
      paymentNo: payment.data.paymentNo,
      channelTradeNo: `${channel}-${Date.now()}`
    })
    await loadOrders(pageInfo.page)
  } catch (error) {
    window.alert(error)
  }
}

async function cancel(id) {
  if (!window.confirm('确定取消该订单吗？')) return
  await cancelOrder(id)
  await loadOrders(pageInfo.page)
}

async function receive(id) {
  await receiveOrder(id)
  await loadOrders(pageInfo.page)
}

async function showLogistics(id) {
  const res = await getLogistics(id)
  logistics.value = { ...logistics.value, [id]: res.data }
}

async function afterSale(id) {
  const reason = window.prompt('请输入售后原因', '商品不符合预期，申请退款退货')
  if (!reason) return
  await requestAfterSale(id, { type: 'REFUND_RETURN', reason })
  await loadOrders(pageInfo.page)
}

function viewBill(order) {
  selectedOrder.value = order
  showBillModal.value = true
}

onMounted(() => loadOrders(0))
</script>
