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
          <span>¥{{ item.price }} x {{ item.quantity }}，小计 ¥{{ item.subtotal }}</span>
        </div>
        <div class="row-actions">
          <input v-model.number="item.quantity" type="number" min="1" @change="changeQty(item)" />
          <button class="ghost-button" @click="remove(item.id)">移除</button>
        </div>
      </article>
    </div>
    <p v-else>购物车还是空的，去挑选一些商品吧。</p>

    <div class="checkout-box">
      <textarea v-model="shippingAddress" placeholder="请输入收货地址"></textarea>
      <select v-model="couponId">
        <option :value="null">不使用优惠券</option>
        <option v-for="coupon in usableCoupons" :key="coupon.id" :value="coupon.id">
          {{ coupon.name }}：满 ¥{{ coupon.thresholdAmount }} 减 ¥{{ coupon.discountAmount }}
        </option>
      </select>
      <div class="inline-meta">
        <label class="row-main">
          <span>积分抵扣，可用 {{ profile?.points || 0 }} 积分</span>
          <input v-model.number="pointsUsed" type="number" min="0" :max="profile?.points || 0" />
        </label>
      </div>
      <div class="notice-box">
        <span>原价：¥{{ cart.totalAmount || 0 }}</span>
        <span>优惠券：-¥{{ couponDiscount }}</span>
        <span>积分：-¥{{ pointsDiscount }}</span>
        <strong>实付：¥{{ payableAmount }}</strong>
      </div>
      <button class="accent-button" :disabled="!cart.items?.length || !shippingAddress.trim()" @click="submitOrder">
        提交订单
      </button>
    </div>
  </section>
</template>

<script setup>
import { computed, onMounted, reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import { checkout, getCart, getCoupons, getMemberProfile, removeCart, updateCart } from '../api/mall'

const router = useRouter()
const cart = reactive({ items: [], totalAmount: 0 })
const coupons = ref([])
const profile = ref(null)
const shippingAddress = ref('上海市浦东新区示例路 1001 号')
const couponId = ref(null)
const pointsUsed = ref(0)

const usableCoupons = computed(() => coupons.value.filter(item => item.claimed && !item.used))
const selectedCoupon = computed(() => usableCoupons.value.find(item => item.id === couponId.value))
const couponDiscount = computed(() => {
  const coupon = selectedCoupon.value
  if (!coupon) return 0
  return Number(cart.totalAmount || 0) >= Number(coupon.thresholdAmount) ? Number(coupon.discountAmount) : 0
})
const pointsDiscount = computed(() => Math.min(Math.max(Number(pointsUsed.value || 0), 0) / 100,
  Math.max(0, Number(cart.totalAmount || 0) - couponDiscount.value)).toFixed(2))
const payableAmount = computed(() => Math.max(0,
  Number(cart.totalAmount || 0) - couponDiscount.value - Number(pointsDiscount.value)).toFixed(2))

async function loadCart() {
  const [cartRes, couponRes, profileRes] = await Promise.all([getCart(), getCoupons(), getMemberProfile()])
  Object.assign(cart, cartRes.data)
  coupons.value = couponRes.data
  profile.value = profileRes.data
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
  }
}

onMounted(loadCart)
</script>
