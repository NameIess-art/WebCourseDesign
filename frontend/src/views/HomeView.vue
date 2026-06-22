<template>
  <section class="hero">
    <div>
      <p class="eyebrow">商城</p>
      <h1>高并发电商演示系统</h1>
      <p class="hero-copy">覆盖导购、搜索、购物车、优惠结算、模拟支付、会员消息和后台运营。</p>
    </div>
    <div class="search-panel">
      <input v-model="keyword" placeholder="搜索商品名称或分类" @input="loadSuggest" @keyup.enter="loadProducts" />
      <button class="accent-button" @click="loadProducts">搜索</button>
      <div v-if="suggestions.productNames.length || suggestions.categories.length" class="tag-list">
        <button v-for="item in suggestions.productNames" :key="`p-${item}`" class="tag-chip" @click="useKeyword(item)">
          {{ item }}
        </button>
        <button v-for="item in suggestions.categories" :key="`c-${item}`" class="tag-chip" @click="useKeyword(item)">
          {{ item }}
        </button>
      </div>
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

  <section v-if="recommendations.hot?.length" class="section-card">
    <div class="section-head">
      <h2>首页推荐</h2>
      <span>热销 / 新品 / 活动</span>
    </div>
    <div class="dual-grid">
      <RecommendationColumn title="热销商品" :items="recommendations.hot" @open="openProduct" />
      <RecommendationColumn title="活动商品" :items="recommendations.activity" @open="openProduct" />
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
          <span class="product-category">{{ product.categoryName }} · {{ product.promotionTag }}</span>
          <h3>{{ product.name }}</h3>
          <p>{{ product.subtitle }}</p>
          <div class="product-meta">
            <strong>¥{{ product.price }}</strong>
            <span>库存 {{ product.stock }}</span>
          </div>
          <div class="product-actions">
            <button class="ghost-button" @click="openProduct(product.id)">详情</button>
            <button class="accent-button" :disabled="product.stock === 0" @click="quickAdd(product.id)">
              {{ product.stock === 0 ? '已售罄' : '加购' }}
            </button>
          </div>
        </div>
      </article>
    </div>
    <p v-else>没有找到符合条件的商品。</p>
  </section>
</template>

<script setup>
import { defineComponent, h, onMounted, ref } from 'vue'
import { useRouter } from 'vue-router'
import { addCart, getCategories, getProducts, getRecommendations, suggestSearch } from '../api/mall'
import { getToken } from '../utils/auth'

const router = useRouter()
const categories = ref([])
const products = ref([])
const keyword = ref('')
const categoryId = ref(null)
const suggestions = ref({ productNames: [], categories: [] })
const recommendations = ref({ hot: [], latest: [], activity: [] })

const RecommendationColumn = defineComponent({
  props: { title: String, items: Array },
  emits: ['open'],
  setup(props, { emit }) {
    return () => h('div', { class: 'stack-list' }, [
      h('h3', props.title),
      ...(props.items || []).map(item => h('article', { class: 'row-card', key: item.id }, [
        h('img', { class: 'mini-image', src: item.imageUrl, alt: item.name }),
        h('div', { class: 'row-main' }, [
          h('strong', item.name),
          h('span', `¥${item.price} · ${item.promotionTag || item.categoryName}`)
        ]),
        h('button', { class: 'ghost-button', onClick: () => emit('open', item.id) }, '查看')
      ]))
    ])
  }
})

async function loadCategories() {
  const res = await getCategories()
  categories.value = res.data
}

async function loadProducts() {
  const res = await getProducts(keyword.value, categoryId.value)
  products.value = res.data
}

async function loadRecommendations() {
  const res = await getRecommendations()
  recommendations.value = res.data
}

async function loadSuggest() {
  if (!keyword.value.trim()) {
    suggestions.value = { productNames: [], categories: [] }
    return
  }
  const res = await suggestSearch(keyword.value)
  suggestions.value = res.data
}

async function useKeyword(value) {
  keyword.value = value
  suggestions.value = { productNames: [], categories: [] }
  await loadProducts()
}

async function selectCategory(id) {
  categoryId.value = id
  await loadProducts()
}

function openProduct(id) {
  router.push(`/products/${id}`)
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
  await Promise.all([loadCategories(), loadProducts(), loadRecommendations()])
})
</script>
