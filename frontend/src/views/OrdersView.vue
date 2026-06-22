<template>
  <section class="order-layout">
    <section class="section-card">
      <div class="section-head">
        <h1>我的订单</h1>
        <button class="ghost-button" @click="loadOrders">刷新</button>
      </div>

      <div v-if="orders.length" class="stack-list">
        <article v-for="order in orders" :key="order.id" class="order-card">
          <div class="section-head">
            <div>
              <strong>{{ order.orderNo }}</strong>
              <p>{{ formatDate(order.createdAt) }}</p>
            </div>
            <span class="tag-chip">{{ statusText[order.status] || order.status }}</span>
          </div>
          <div class="stack-list small-gap">
            <div v-for="item in order.items" :key="item.id" class="inline-meta">
              <span>{{ item.productName }}</span>
              <span>{{ item.quantity }} x ¥{{ item.price }}</span>
            </div>
          </div>
          <div class="notice-box">
            <span>原价 ¥{{ order.originalAmount }}</span>
            <span>优惠 -¥{{ order.discountAmount }}，积分 -¥{{ order.pointsDiscountAmount }}</span>
            <strong>实付 ¥{{ order.totalAmount }}</strong>
            <span v-if="order.paymentChannel">支付渠道：{{ order.paymentChannel }}</span>
          </div>
          <div class="order-footer">
            <div class="row-actions" v-if="order.status === 'PENDING_PAYMENT'">
              <button class="accent-button" @click="pay(order.id, 'ALIPAY')">支付宝</button>
              <button class="accent-button" @click="pay(order.id, 'WECHAT')">微信</button>
              <button class="accent-button" @click="pay(order.id, 'BANK')">银行卡</button>
              <button class="ghost-button" @click="cancel(order.id)">取消</button>
            </div>
            <div class="row-actions">
              <button v-if="order.status === 'SHIPPED'" class="ghost-button" @click="receive(order.id)">确认收货</button>
              <button class="ghost-button" @click="showLogistics(order.id)">物流</button>
              <button v-if="['PAID','SHIPPED','COMPLETED'].includes(order.status)" class="ghost-button" @click="afterSale(order.id)">售后</button>
            </div>
          </div>
          <div v-if="logistics[order.id]" class="notice-box">
            <strong>{{ logistics[order.id].carrier }} · {{ logistics[order.id].trackingNo }}</strong>
            <p v-for="trace in logistics[order.id].traces" :key="trace">{{ trace }}</p>
          </div>
        </article>
      </div>
      <p v-else>暂无订单。</p>
    </section>

    <section class="section-card">
      <div class="section-head"><h2>售后记录</h2></div>
      <div class="stack-list">
        <article v-for="record in afterSales" :key="record.id" class="row-card">
          <div class="row-main">
            <strong>{{ record.orderNo }} · {{ record.type }}</strong>
            <span>{{ record.reason }} · {{ record.status }}</span>
          </div>
        </article>
      </div>
    </section>
  </section>
</template>

<script setup>
import { onMounted, ref } from 'vue'
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

const orders = ref([])
const afterSales = ref([])
const logistics = ref({})
const statusText = {
  PENDING_PAYMENT: '待支付',
  PAID: '待发货',
  SHIPPED: '待收货',
  COMPLETED: '已完成',
  CANCELLED: '已取消'
}

const formatDate = (value) => new Date(value).toLocaleString('zh-CN')

async function loadOrders() {
  const [orderRes, afterSaleRes] = await Promise.all([getOrders(), getAfterSales()])
  orders.value = orderRes.data
  afterSales.value = afterSaleRes.data
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
    await loadOrders()
  } catch (error) {
    window.alert(error)
  }
}

async function cancel(id) {
  if (!window.confirm('确定取消该订单吗？')) return
  await cancelOrder(id)
  await loadOrders()
}

async function receive(id) {
  await receiveOrder(id)
  await loadOrders()
}

async function showLogistics(id) {
  const res = await getLogistics(id)
  logistics.value = { ...logistics.value, [id]: res.data }
}

async function afterSale(id) {
  const reason = window.prompt('请输入售后原因', '商品不符合预期，申请退款/退货')
  if (!reason) return
  await requestAfterSale(id, { type: 'REFUND_RETURN', reason })
  await loadOrders()
}

onMounted(loadOrders)
</script>
