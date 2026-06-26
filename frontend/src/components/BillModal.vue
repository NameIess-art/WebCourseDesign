<template>
  <div class="modal-overlay" v-if="visible" @click.self="close">
    <div class="bill-card">
      <div class="bill-header">
        <h2>MallSystem 消费凭证</h2>
        <p class="subtitle">电子发票 / 账单</p>
      </div>

      <div class="bill-body">
        <div class="bill-info">
          <p><span>订单编号：</span><span>{{ order.orderNo }}</span></p>
          <p><span>交易时间：</span><span>{{ formatDate(order.createdAt) }}</span></p>
          <p><span>支付渠道：</span><span>{{ channelText[order.paymentChannel] || order.paymentChannel || '未支付' }}</span></p>
          <p><span>订单状态：</span><span>{{ statusText[order.status] || order.status }}</span></p>
        </div>

        <div class="bill-divider"></div>

        <div class="bill-items">
          <div class="item-header">
            <span>商品</span>
            <span>数量</span>
            <span>金额</span>
          </div>
          <div v-for="item in order.items" :key="item.id" class="item-row">
            <span class="item-name">{{ item.productName }} ({{ item.skuName || '默认规格' }})</span>
            <span class="item-qty">x{{ item.quantity }}</span>
            <span class="item-price">¥{{ item.price }}</span>
          </div>
        </div>

        <div class="bill-divider"></div>

        <div class="bill-summary">
          <div class="summary-row">
            <span>商品总价：</span>
            <span>¥{{ order.originalAmount }}</span>
          </div>
          <div class="summary-row discount" v-if="order.discountAmount > 0">
            <span>活动优惠：</span>
            <span>- ¥{{ order.discountAmount }}</span>
          </div>
          <div class="summary-row discount" v-if="order.pointsDiscountAmount > 0">
            <span>积分抵扣：</span>
            <span>- ¥{{ order.pointsDiscountAmount }}</span>
          </div>
          <div class="summary-row total">
            <span>实付金额：</span>
            <span><strong>¥{{ order.totalAmount }}</strong></span>
          </div>
        </div>
      </div>

      <div class="bill-footer">
        <button class="ghost-button" @click="close">关闭</button>
        <button class="accent-button" @click="printBill">打印账单</button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { defineProps, defineEmits } from 'vue'

const props = defineProps({
  visible: { type: Boolean, default: false },
  order: { type: Object, default: () => ({}) }
})

const emit = defineEmits(['update:visible'])

const channelText = { ALIPAY: '支付宝', WECHAT: '微信', BANK: '银行卡', BALANCE: '余额' }
const statusText = {
  PENDING_PAYMENT: '待支付',
  PAID: '已支付',
  SHIPPED: '已发货',
  COMPLETED: '已完成',
  CANCELLED: '已取消'
}

const formatDate = (value) => value ? new Date(value).toLocaleString('zh-CN') : ''

const close = () => {
  emit('update:visible', false)
}

const printBill = () => {
  window.print()
}
</script>

<style scoped>
.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  width: 100vw;
  height: 100vh;
  background: rgba(0, 0, 0, 0.5);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1000;
}

.bill-card {
  background: #fff;
  width: 400px;
  max-width: 90%;
  border-radius: 8px;
  box-shadow: 0 10px 30px rgba(0, 0, 0, 0.2);
  display: flex;
  flex-direction: column;
  overflow: hidden;
}

/* 打印样式：只打印账单区域 */
@media print {
  body * {
    visibility: hidden;
  }
  .bill-card, .bill-card * {
    visibility: visible;
  }
  .bill-card {
    position: absolute;
    left: 0;
    top: 0;
    width: 100%;
    box-shadow: none;
  }
  .bill-footer {
    display: none;
  }
}

.bill-header {
  text-align: center;
  padding: 20px 20px 10px;
  border-bottom: 2px dashed #eee;
}

.bill-header h2 {
  margin: 0 0 5px 0;
  font-size: 20px;
  color: #333;
}

.subtitle {
  margin: 0;
  color: #888;
  font-size: 14px;
}

.bill-body {
  padding: 20px;
  font-family: monospace, sans-serif;
  color: #444;
}

.bill-info p {
  margin: 5px 0;
  font-size: 14px;
  display: flex;
  justify-content: space-between;
}

.bill-divider {
  border-top: 1px dashed #ccc;
  margin: 15px 0;
}

.item-header {
  display: flex;
  font-weight: bold;
  margin-bottom: 10px;
  font-size: 14px;
}
.item-header span:nth-child(1) { flex: 2; }
.item-header span:nth-child(2) { flex: 1; text-align: center; }
.item-header span:nth-child(3) { flex: 1; text-align: right; }

.item-row {
  display: flex;
  margin-bottom: 8px;
  font-size: 13px;
}
.item-name { flex: 2; word-break: break-all; }
.item-qty { flex: 1; text-align: center; }
.item-price { flex: 1; text-align: right; }

.summary-row {
  display: flex;
  justify-content: space-between;
  margin-bottom: 8px;
  font-size: 14px;
}
.summary-row.discount {
  color: #e53935;
}
.summary-row.total {
  margin-top: 10px;
  font-size: 18px;
  color: #111;
  border-top: 2px solid #eee;
  padding-top: 10px;
}

.bill-footer {
  padding: 15px 20px;
  background: #f9f9f9;
  display: flex;
  justify-content: flex-end;
  gap: 10px;
  border-top: 1px solid #eee;
}
</style>
