<template>
  <section class="feature-layout">
    <section class="section-card">
      <div class="section-head">
        <div>
          <p class="eyebrow">Customer Center</p>
          <h1>会员与服务中心</h1>
        </div>
        <button class="ghost-button" :disabled="loading" @click="loadAll">刷新</button>
      </div>
      <div v-if="profile" class="stat-grid">
        <div class="stat-card"><span>会员等级</span><strong>{{ profile.level }}</strong></div>
        <div class="stat-card"><span>积分</span><strong>{{ profile.points }}</strong></div>
        <div class="stat-card"><span>未读消息</span><strong>{{ profile.unreadMessages }}</strong></div>
      </div>
      <p class="muted">{{ profile?.rights }}</p>
    </section>

    <section class="section-card">
      <div class="section-head"><h2>收货地址</h2></div>
      <form class="admin-form action-form" @submit.prevent="submitAddress">
        <input v-model="addressForm.receiver" placeholder="收件人" required />
        <input v-model="addressForm.phone" placeholder="手机号" required />
        <input v-model="addressForm.region" placeholder="省市区" required />
        <input v-model="addressForm.detail" placeholder="详细地址" required />
        <label class="inline-meta">
          <input v-model="addressForm.defaultAddress" type="checkbox" />
          <span>设为默认地址</span>
        </label>
        <button class="accent-button">{{ addressForm.id ? '保存地址' : '新增地址' }}</button>
      </form>
      <div class="stack-list">
        <article v-for="address in addresses" :key="address.id" class="row-card">
          <div class="row-main">
            <strong>{{ address.receiver }} · {{ address.phone }}</strong>
            <span>{{ address.region }} {{ address.detail }}</span>
          </div>
          <div class="row-actions">
            <span class="tag-chip" v-if="address.defaultAddress">默认</span>
            <button class="ghost-button" @click="editAddress(address)">编辑</button>
            <button class="ghost-button" @click="removeAddress(address.id)">删除</button>
          </div>
        </article>
      </div>
    </section>

    <section class="section-card">
      <div class="section-head"><h2>优惠券 / 红包 / 满减</h2></div>
      <div class="product-grid">
        <article v-for="coupon in coupons" :key="coupon.id" class="stat-card">
          <span>{{ coupon.name }}</span>
          <strong>¥{{ coupon.discountAmount }}</strong>
          <p class="muted">满 ¥{{ coupon.thresholdAmount }} 可用，库存 {{ coupon.stock }}</p>
          <button class="accent-button" :disabled="coupon.claimed" @click="claim(coupon.id)">
            {{ coupon.claimed ? '已领取' : '领取' }}
          </button>
        </article>
      </div>
    </section>

    <section id="seckill" class="section-card">
      <div class="section-head"><h2>限时秒杀</h2></div>
      <div class="product-grid">
        <article v-for="event in seckillEvents" :key="event.id" class="product-card">
          <img :src="event.imageUrl" :alt="event.productName" loading="lazy" />
          <div class="product-body">
            <span class="product-category">秒杀库存 {{ event.stock }}</span>
            <h3>{{ event.productName }}</h3>
            <div class="product-meta">
              <strong>¥{{ event.seckillPrice }}</strong>
              <span>已抢 {{ event.sold }}</span>
            </div>
            <button class="accent-button" :disabled="event.stock === 0" @click="buySeckill(event.id)">立即抢购</button>
          </div>
        </article>
      </div>
    </section>

    <section class="section-card">
      <div class="section-head"><h2>收藏、积分与消息</h2></div>
      <div class="dual-grid">
        <div class="stack-list">
          <h3>收藏商品</h3>
          <article v-for="favorite in favorites" :key="favorite.id" class="row-card">
            <img :src="favorite.imageUrl" :alt="favorite.productName" class="mini-image" />
            <div class="row-main">
              <strong>{{ favorite.productName }}</strong>
              <span>¥{{ favorite.price }}</span>
            </div>
          </article>
          <p v-if="!favorites.length" class="muted">暂无收藏商品。</p>
        </div>
        <div class="stack-list">
          <h3>积分流水</h3>
          <article v-for="record in pointRecords" :key="record.id" class="row-card">
            <div class="row-main">
              <strong>{{ record.type }} {{ record.points }}</strong>
              <span>{{ record.description }}</span>
            </div>
          </article>
          <p v-if="!pointRecords.length" class="muted">暂无积分流水。</p>
        </div>
      </div>
      <div class="stack-list">
        <h3>站内消息</h3>
        <article v-for="message in messages" :key="message.id" class="row-card">
          <div class="row-main">
            <strong>{{ message.title }}</strong>
            <span>{{ message.content }}</span>
          </div>
          <button v-if="!message.readFlag" class="ghost-button" @click="markRead(message.id)">标为已读</button>
        </article>
        <p v-if="!messages.length" class="muted">暂无站内消息。</p>
      </div>
    </section>
  </section>
</template>

<script setup>
import { onMounted, reactive, ref } from 'vue'
import {
  claimCoupon,
  deleteAddress,
  getAddresses,
  getCoupons,
  getFavorites,
  getMemberMessages,
  getMemberProfile,
  getPointRecords,
  getSeckillEvents,
  purchaseSeckill,
  readMessage,
  saveAddress,
  updateAddress
} from '../api/mall'

const loading = ref(false)
const profile = ref(null)
const addresses = ref([])
const coupons = ref([])
const favorites = ref([])
const messages = ref([])
const pointRecords = ref([])
const seckillEvents = ref([])
const addressForm = reactive({
  id: null,
  receiver: '',
  phone: '',
  region: '',
  detail: '',
  defaultAddress: false
})

function resetAddress() {
  Object.assign(addressForm, { id: null, receiver: '', phone: '', region: '', detail: '', defaultAddress: false })
}

async function loadAll() {
  loading.value = true
  try {
    const [profileRes, addressRes, couponRes, favoriteRes, messageRes, seckillRes, pointRes] = await Promise.all([
      getMemberProfile(),
      getAddresses(),
      getCoupons(),
      getFavorites(),
      getMemberMessages(),
      getSeckillEvents(),
      getPointRecords()
    ])
    profile.value = profileRes.data
    addresses.value = addressRes.data
    coupons.value = couponRes.data
    favorites.value = favoriteRes.data
    messages.value = messageRes.data
    seckillEvents.value = seckillRes.data
    pointRecords.value = pointRes.data
  } finally {
    loading.value = false
  }
}

async function submitAddress() {
  const payload = {
    receiver: addressForm.receiver,
    phone: addressForm.phone,
    region: addressForm.region,
    detail: addressForm.detail,
    defaultAddress: addressForm.defaultAddress
  }
  if (addressForm.id) await updateAddress(addressForm.id, payload)
  else await saveAddress(payload)
  resetAddress()
  await loadAll()
}

function editAddress(address) {
  Object.assign(addressForm, address)
}

async function removeAddress(id) {
  if (!window.confirm('确定删除该地址吗？')) return
  await deleteAddress(id)
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
