<template>
  <section class="section-card">
    <div class="section-head">
      <h1>购物车</h1>
      <span>商品合计 ¥{{ cart.totalAmount || 0 }}</span>
    </div>

    <div v-if="cart.items?.length" class="stack-list">
      <article v-for="item in cart.items" :key="item.id" class="row-card">
        <img :src="item.imageUrl" :alt="item.productName" class="mini-image" />
        <div class="row-main">
          <strong>{{ item.productName }}</strong>
          <span>{{ item.skuName || '默认规格' }} · ¥{{ item.price }} x {{ item.quantity }}，小计 ¥{{ item.subtotal }}</span>
          <span class="muted">当前库存 {{ item.stock }}</span>
        </div>
        <div class="row-actions">
          <input v-model.number="item.quantity" type="number" min="1" :max="item.stock" @change="changeQty(item)" />
          <button class="ghost-button" @click="remove(item.id)">移除</button>
        </div>
      </article>
    </div>
    <p v-else class="muted">购物车还是空的，去挑选一些商品吧。</p>

    <div class="checkout-box">
      <label>
        <span>收货地址</span>
        <select v-if="addresses.content.length" v-model="selectedAddressId" @change="syncAddress">
          <option v-for="address in addresses.content" :key="address.id" :value="address.id">
            {{ address.receiver }} · {{ address.phone }} · {{ address.region }} {{ address.detail }}
          </option>
        </select>
        <textarea v-model="shippingAddress" placeholder="请输入收货地址"></textarea>
      </label>
      <label>
        <span>优惠券 / 红包 / 满减</span>
        <select v-model="couponId">
          <option :value="null">不使用优惠券</option>
          <option v-for="coupon in usableCoupons" :key="coupon.id" :value="coupon.id">
            {{ coupon.name }}：满 ¥{{ coupon.thresholdAmount }} 减 ¥{{ coupon.discountAmount }}
          </option>
        </select>
      </label>
      <label>
        <span>积分抵扣，可用 {{ profile?.points || 0 }} 积分</span>
        <input v-model.number="pointsUsed" type="number" min="0" :max="profile?.points || 0" />
      </label>
      <div class="notice-box">
        <span>原价：¥{{ cart.totalAmount || 0 }}</span>
        <span>优惠券：-¥{{ couponDiscount }}</span>
        <span>积分：-¥{{ pointsDiscount }}</span>
        <strong>实付：¥{{ payableAmount }}</strong>
      </div>
      <button class="accent-button" :disabled="submitting || !cart.items?.length || !shippingAddress.trim()" @click="submitOrder">
        {{ submitting ? '提交中...' : '提交订单' }}
      </button>
    </div>
  </section>
</template>

<script setup>
import { computed, onMounted, reactive, ref } from 'vue'
import Pagination from '../components/Pagination.vue'
import { useRouter } from 'vue-router'
import {
  checkout,
  getAddresses,
  getCart,
  getCoupons,
  getMemberProfile,
  removeCart,
  updateCart
} from '../api/mall'

const router = useRouter()
const cart = reactive({ items: [], totalAmount: 0 })
const coupons = ref({ content: [], page: 0, totalPages: 0 })
const profile = ref(null)
const addresses = ref({ content: [], page: 0, totalPages: 0 })
const selectedAddressId = ref(null)
const shippingAddress = ref('')
const couponId = ref(null)
const pointsUsed = ref(0)
const submitting = ref(false)

const usableCoupons = computed(() => coupons.value.content.filter(item => item.claimed && !item.used))
const selectedCoupon = computed(() => usableCoupons.value.find(item => item.id === couponId.value))
const couponDiscount = computed(() => {
  const coupon = selectedCoupon.value
  if (!coupon) return 0
  return Number(cart.totalAmount || 0) >= Number(coupon.thresholdAmount) ? Number(coupon.discountAmount) : 0
})
const pointsDiscount = computed(() => Math.min(
  Math.max(Number(pointsUsed.value || 0), 0) / 100,
  Math.max(0, Number(cart.totalAmount || 0) - couponDiscount.value)
).toFixed(2))
const payableAmount = computed(() => Math.max(
  0,
  Number(cart.totalAmount || 0) - couponDiscount.value - Number(pointsDiscount.value)
).toFixed(2))

async function loadCart() {
  const [cartRes, couponRes, profileRes, addressRes] = await Promise.all([
    getCart(),
    getCoupons(),
    getMemberProfile(),
    getAddresses()
  ])
  Object.assign(cart, cartRes.data)
  coupons.value = couponRes.data
  profile.value = profileRes.data
  addresses.value = addressRes.data
  if (!shippingAddress.value && addresses.value.length) {
    selectedAddressId.value = addresses.value.find(item => item.defaultAddress)?.id || addresses.value[0].id
    syncAddress()
  }
}

function syncAddress() {
  const address = addresses.value.find(item => item.id === selectedAddressId.value)
  if (address) {
    shippingAddress.value = `${address.receiver} ${address.phone} ${address.region} ${address.detail}`
  }
}

async function changeQty(item) {
  try {
    await updateCart(item.id, item.quantity)
    await loadCart()
  } catch (error) {
    window.alert(error)
    await loadCart()
  }
}

async function remove(id) {
  if (!window.confirm('确定从购物车移除该商品吗？')) return
  await removeCart(id)
  await loadCart()
}

async function submitOrder() {
  submitting.value = true
  try {
    await checkout({
      shippingAddress: shippingAddress.value,
      couponId: couponId.value,
      pointsUsed: Math.max(0, Number(pointsUsed.value || 0)),
      idempotencyKey: `checkout-${Date.now()}-${Math.random().toString(16).slice(2)}`
    })
    window.alert('订单创建成功。')
    router.push('/orders')
  } catch (error) {
    window.alert(error)
  } finally {
    submitting.value = false
  }
}

onMounted(loadCart)
</script>
