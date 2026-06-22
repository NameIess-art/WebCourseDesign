<template>
  <section class="hero">
    <div>
      <p class="eyebrow">Spring Boot + Vue 商城</p>
      <h1>发现好物，轻松下单。</h1>
      <p class="hero-copy">从商品浏览、购物车到订单履约，一站式完成完整购物流程。</p>
    </div>
    <div class="search-panel">
      <input v-model="keyword" placeholder="搜索商品名称" @keyup.enter="loadProducts" />
      <button class="accent-button" @click="loadProducts">搜索</button>
    </div>
  </section>

  <section class="section-card">
    <div class="section-head">
      <h2>商品分类</h2>
      <span>共 {{ categories.length }} 个分类</span>
    </div>
    <div class="tag-list">
      <button class="tag-chip" :class="{ active: !categoryId }" @click="selectCategory(null)">全部</button>
      <button v-for="category in categories" :key="category.id" class="tag-chip"
              :class="{ active: categoryId === category.id }" @click="selectCategory(category.id)">
        {{ category.name }}
      </button>
    </div>
  </section>

  <section class="section-card">
    <div class="section-head">
      <h2>精选商品</h2>
      <span>{{ products.length }} 件商品</span>
    </div>
    <div v-if="products.length" class="product-grid">
      <article v-for="product in products" :key="product.id" class="product-card">
        <img :src="product.imageUrl" :alt="product.name" />
        <div class="product-body">
          <span class="product-category">{{ product.categoryName }}</span>
          <h3>{{ product.name }}</h3>
          <p>{{ product.subtitle }}</p>
          <div class="product-meta">
            <strong>￥{{ product.price }}</strong>
            <span>库存 {{ product.stock }}</span>
          </div>
          <div class="product-actions">
            <button class="ghost-button" @click="$router.push(`/products/${product.id}`)">查看详情</button>
            <button class="accent-button" :disabled="product.stock === 0" @click="quickAdd(product.id)">
              {{ product.stock === 0 ? '已售罄' : '加入购物车' }}
            </button>
          </div>
        </div>
      </article>
    </div>
    <p v-else>没有找到符合条件的商品。</p>
  </section>
</template>

<script setup>
import { onMounted, ref } from 'vue'
import { addCart, getCategories, getProducts } from '../api/mall'
import { getToken } from '../utils/auth'

const categories = ref([])
const products = ref([])
const keyword = ref('')
const categoryId = ref(null)

async function loadCategories() {
  const res = await getCategories()
  categories.value = res.data
}

async function loadProducts() {
  const res = await getProducts(keyword.value, categoryId.value)
  products.value = res.data
}

async function selectCategory(id) {
  categoryId.value = id
  await loadProducts()
}

async function quickAdd(productId) {
  if (!getToken()) {
    window.alert('请先登录。')
    return
  }
  try {
    await addCart({ productId, quantity: 1 })
    window.alert('已加入购物车。')
  } catch (error) {
    window.alert(error)
  }
}

onMounted(async () => {
  await Promise.all([loadCategories(), loadProducts()])
})
</script>
