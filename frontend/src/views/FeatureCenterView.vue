<template>
  <section class="feature-layout">
    <section class="section-card">
      <div class="section-head">
        <div>
          <p class="eyebrow">Customer Center</p>
          <h1>会员与服务中心</h1>
        </div>
        <button class="ghost-button" @click="loadAll">刷新</button>
      </div>
      <div v-if="profile" class="stat-grid">
        <div class="stat-card"><span>会员等级</span><strong>{{ profile.level }}</strong></div>
        <div class="stat-card"><span>积分</span><strong>{{ profile.points }}</strong></div>
        <div class="stat-card"><span>未读消息</span><strong>{{ profile.unreadMessages }}</strong></div>
      </div>
      <p class="muted">{{ profile?.rights }}</p>
    </section>

    <section class="section-card">
      <div class="section-head">
        <h2>收货地址</h2>
        <button class="accent-button" @click="saveDemoAddress">新增示例地址</button>
      </div>
      <div class="stack-list">
        <article v-for="address in addresses" :key="address.id" class="row-card">
          <div class="row-main">
            <strong>{{ address.receiver }} · {{ address.phone }}</strong>
            <span>{{ address.region }} {{ address.detail }}</span>
          </div>
          <span class="tag-chip" v-if="address.defaultAddress">默认</span>
        </article>
      </div>
    </section>

    <section class="section-card">
      <div class="section-head"><h2>优惠券 / 红包 / 满减</h2></div>
      <div class="product-grid">
        <article v-for="coupon in coupons" :key="coupon.id" class="stat-card">
          <span>{{ coupon.name }}</span>
          <strong>￥{{ coupon.discountAmount }}</strong>
          <p class="muted">满 ￥{{ coupon.thresholdAmount }} 可用，库存 {{ coupon.stock }}</p>
          <button class="accent-button" :disabled="coupon.claimed" @click="claim(coupon.id)">
            {{ coupon.claimed ? '已领取' : '领取' }}
          </button>
        </article>
      </div>
    </section>

    <section class="section-card">
      <div class="section-head"><h2>限时秒杀</h2></div>
      <div class="product-grid">
        <article v-for="event in seckillEvents" :key="event.id" class="product-card">
          <img :src="event.imageUrl" :alt="event.productName" />
          <div class="product-body">
            <span class="product-category">秒杀库存 {{ event.stock }}</span>
            <h3>{{ event.productName }}</h3>
            <div class="product-meta">
              <strong>￥{{ event.seckillPrice }}</strong>
              <span>已抢 {{ event.sold }}</span>
            </div>
            <button class="accent-button" :disabled="event.stock === 0" @click="buySeckill(event.id)">立即抢购</button>
          </div>
        </article>
      </div>
    </section>

    <section class="section-card">
      <div class="section-head"><h2>收藏与消息</h2></div>
      <div class="dual-grid">
        <div class="stack-list">
          <article v-for="favorite in favorites" :key="favorite.id" class="row-card">
            <img :src="favorite.imageUrl" :alt="favorite.productName" class="mini-image" />
            <div class="row-main">
              <strong>{{ favorite.productName }}</strong>
              <span>￥{{ favorite.price }}</span>
            </div>
          </article>
          <p v-if="!favorites.length" class="muted">暂无收藏商品。</p>
        </div>
        <div class="stack-list">
          <article v-for="message in messages" :key="message.id" class="row-card">
            <div class="row-main">
              <strong>{{ message.title }}</strong>
              <span>{{ message.content }}</span>
            </div>
            <button v-if="!message.readFlag" class="ghost-button" @click="markRead(message.id)">已读</button>
          </article>
        </div>
      </div>
    </section>
  </section>
</template>

<script setup>
import { onMounted, ref } from 'vue'
import {
  claimCoupon,
  getAddresses,
  getCoupons,
  getFavorites,
  getMemberMessages,
  getMemberProfile,
  getSeckillEvents,
  purchaseSeckill,
  readMessage,
  saveAddress
} from '../api/mall'

const profile = ref(null)
const addresses = ref([])
const coupons = ref([])
const favorites = ref([])
const messages = ref([])
const seckillEvents = ref([])

async function loadAll() {
  const [profileRes, addressRes, couponRes, favoriteRes, messageRes, seckillRes] = await Promise.all([
    getMemberProfile(), getAddresses(), getCoupons(), getFavorites(), getMemberMessages(), getSeckillEvents()
  ])
  profile.value = profileRes.data
  addresses.value = addressRes.data
  coupons.value = couponRes.data
  favorites.value = favoriteRes.data
  messages.value = messageRes.data
  seckillEvents.value = seckillRes.data
}

async function saveDemoAddress() {
  await saveAddress({
    receiver: '张三',
    phone: '13800000000',
    region: '上海市浦东新区',
    detail: '示例路 1001 号 8 栋 1801',
    defaultAddress: true
  })
  await loadAll()
}

async function claim(id) {
  try {
    await claimCoupon(id)
    await loadAll()
  } catch (error) {
    window.alert(error)
  }
}

async function markRead(id) {
  await readMessage(id)
  await loadAll()
}

async function buySeckill(id) {
  try {
    await purchaseSeckill(id)
    window.alert('秒杀订单已创建，请到我的订单完成支付。')
    await loadAll()
  } catch (error) {
    window.alert(error)
  }
}

onMounted(loadAll)
</script>
