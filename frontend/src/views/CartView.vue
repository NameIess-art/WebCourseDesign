<template>
  <section class="section-card">
    <div class="section-head">
      <h1>购物车</h1>
      <span>合计 ￥{{ cart.totalAmount || 0 }}</span>
    </div>

    <div v-if="cart.items?.length" class="stack-list">
      <article v-for="item in cart.items" :key="item.id" class="row-card">
        <img :src="item.imageUrl" :alt="item.productName" class="mini-image" />
        <div class="row-main">
          <strong>{{ item.productName }}</strong>
          <span>￥{{ item.price }} × {{ item.quantity }}，小计 ￥{{ item.subtotal }}</span>
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
      <button class="accent-button" :disabled="!cart.items?.length || !shippingAddress.trim()" @click="submitOrder">
        提交订单
      </button>
    </div>
  </section>
</template>

<script setup>
import { onMounted, reactive, ref } from 'vue'
import { checkout, getCart, removeCart, updateCart } from '../api/mall'
import { useRouter } from 'vue-router'

const router = useRouter()
const cart = reactive({ items: [], totalAmount: 0 })
const shippingAddress = ref('上海市浦东新区示例路 1001 号')

async function loadCart() {
  const res = await getCart()
  Object.assign(cart, res.data)
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
    await checkout(shippingAddress.value)
    window.alert('订单创建成功。')
    router.push('/orders')
  } catch (error) {
    window.alert(error)
  }
}

onMounted(loadCart)
</script>
