<template>
  <section class="hero">
    <div>
      <p class="eyebrow">MallSystem</p>
      <h1>高并发电商演示系统</h1>
      <p class="hero-copy">覆盖首页推荐、分类导航、搜索联想、商品详情、收藏加购、优惠结算、支付履约和后台运营。</p>
    </div>
    <div class="search-panel">
      <label>
        <span>搜索商品或分类</span>
        <input v-model="keyword" placeholder="输入商品名称、促销标签或分类" @input="loadSuggest" @keyup.enter="search" />
      </label>
      <button class="accent-button" :disabled="loading" @click="search">搜索</button>
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

  <section id="categories" class="section-card">
    <div class="section-head">
      <h2>商品分类</h2>
      <span>共 {{ categories.length }} 个分类</span>
    </div>
    <div class="tag-list">
      <button class="tag-chip" :class="{ active: !categoryId }" @click="selectCategory(null)">全部</button>
      <button
        v-for="category in categories"
        :key="category.id"
        class="tag-chip"
        :class="{ active: categoryId === category.id }"
        @click="selectCategory(category.id)"
      >
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
      <span>{{ pageInfo.totalElements }} 件商品</span>
    </div>
    <div v-if="loading" class="notice-box">
      <strong>正在加载商品</strong>
      <span class="muted">请稍候，正在同步最新库存与活动信息。</span>
    </div>
    <div v-else-if="products.length" class="product-grid">
      <article v-for="product in products" :key="product.id" class="product-card">
        <img :src="product.imageUrl" :alt="product.name" loading="lazy" />
        <div class="product-body">
          <span class="product-category">{{ product.categoryName }} · {{ product.promotionTag || '日常精选' }}</span>
          <h3>{{ product.name }}</h3>
          <p>{{ product.subtitle }}</p>
          <div class="product-meta">
            <strong>¥{{ product.price }}</strong>
            <span>库存 {{ product.stock }}</span>
          </div>
          <div class="product-actions">
            <button class="ghost-button" @click="openProduct(product.id)">详情</button>
            <button class="accent-button" :disabled="product.stock === 0" @click="quickAdd(product)">
              {{ product.stock === 0 ? '已售罄' : '加购' }}
            </button>
          </div>
        </div>
      </article>
    </div>
    <p v-else class="muted">没有找到符合条件的商品。</p>
    <div class="pager">
      <button class="ghost-button" :disabled="pageInfo.first || loading" @click="changePage(pageInfo.page - 1)">上一页</button>
      <span>第 {{ pageInfo.page + 1 }} / {{ Math.max(pageInfo.totalPages, 1) }} 页</span>
      <button class="ghost-button" :disabled="pageInfo.last || loading" @click="changePage(pageInfo.page + 1)">下一页</button>
    </div>
  </section>
</template>

<script setup>
import { defineComponent, h, onMounted, reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import { addCart, getCategories, getProducts, getRecommendations, suggestSearch } from '../api/mall'
import { getToken } from '../utils/auth'

const router = useRouter()
const categories = ref([])
const products = ref([])
const keyword = ref('')
const categoryId = ref(null)
const loading = ref(false)
const suggestions = ref({ productNames: [], categories: [] })
const recommendations = ref({ hot: [], latest: [], activity: [] })
const pageInfo = reactive({ page: 0, size: 12, totalElements: 0, totalPages: 0, first: true, last: true })

const RecommendationColumn = defineComponent({
  props: { title: String, items: Array },
  emits: ['open'],
  setup(props, { emit }) {
    return () => h('div', { class: 'stack-list' }, [
      h('h3', props.title),
      ...(props.items || []).map(item => h('article', { class: 'row-card', key: item.id }, [
        h('img', { class: 'mini-image', src: item.imageUrl, alt: item.name, loading: 'lazy' }),
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

async function loadProducts(page = pageInfo.page) {
  loading.value = true
  try {
    const res = await getProducts(keyword.value, categoryId.value, page, pageInfo.size)
    const data = res.data
    products.value = data.content || []
    Object.assign(pageInfo, {
      page: data.page ?? 0,
      size: data.size ?? pageInfo.size,
      totalElements: data.totalElements ?? products.value.length,
      totalPages: data.totalPages ?? 1,
      first: data.first ?? true,
      last: data.last ?? true
    })
  } finally {
    loading.value = false
  }
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

async function search() {
  suggestions.value = { productNames: [], categories: [] }
  await loadProducts(0)
}

async function useKeyword(value) {
  keyword.value = value
  await search()
}

async function selectCategory(id) {
  categoryId.value = id
  await loadProducts(0)
}

async function changePage(page) {
  await loadProducts(page)
}

function openProduct(id) {
  router.push(`/products/${id}`)
}

async function quickAdd(product) {
  if (!getToken()) {
    window.alert('请先登录。')
    return
  }
  const sku = product.skus?.[0]
  try {
    await addCart({ productId: product.id, skuId: sku?.id ?? null, quantity: 1 })
    window.alert('已加入购物车。')
  } catch (error) {
    window.alert(error)
  }
}

onMounted(async () => {
  await Promise.all([loadCategories(), loadProducts(0), loadRecommendations()])
})
</script>
