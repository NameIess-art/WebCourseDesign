<template>
  <section v-if="product" class="product-detail">
    <img :src="product.imageUrl" :alt="product.name" class="detail-image" />
    <div class="detail-body">
      <span class="tag-chip">{{ product.categoryName }}</span>
      <h1>{{ product.name }}</h1>
      <p class="detail-subtitle">{{ product.subtitle }}</p>
      <p>{{ product.description }}</p>
      <div class="tag-list">
        <span class="tag-chip">{{ product.promotionTag || '平台精选' }}</span>
        <span class="tag-chip">评分 {{ product.rating || '4.80' }}</span>
        <span class="tag-chip">收藏 {{ product.favoriteCount || 0 }}</span>
        <span class="tag-chip">问答 {{ product.questionCount || 0 }}</span>
      </div>

      <label v-if="product.skus?.length" class="stack-list">
        <span>选择 SKU</span>
        <select v-model.number="selectedSkuId">
          <option v-for="sku in product.skus" :key="sku.id" :value="sku.id" :disabled="!sku.active || sku.stock === 0">
            {{ sku.specName }} · ¥{{ sku.price }} · 库存 {{ sku.stock }}
          </option>
        </select>
      </label>

      <div class="detail-price">¥{{ selectedSku?.price || product.price }}</div>
      <label class="quantity-row">
        <span>购买数量，库存 {{ selectedSku?.stock ?? product.stock }}</span>
        <input v-model.number="quantity" type="number" min="1" :max="selectedSku?.stock || product.stock" />
      </label>
      <div class="row-actions">
        <button class="accent-button" :disabled="!canBuy" @click="add">
          {{ canBuy ? '加入购物车' : '当前规格不可购买' }}
        </button>
        <button class="ghost-button" @click="favorite">收藏</button>
      </div>
    </div>
  </section>

  <section v-if="product?.detailBlocks?.length" class="section-card">
    <div class="section-head"><h2>图文详情</h2></div>
    <div class="stack-list">
      <article v-for="block in product.detailBlocks" :key="block.id" class="notice-box">
        <strong>{{ block.blockType === 'IMAGE' ? '图片' : '文本' }}</strong>
        <img v-if="block.blockType === 'IMAGE'" :src="block.content" alt="商品详情图片" class="detail-block-image" />
        <span v-else>{{ block.content }}</span>
      </article>
    </div>
  </section>

  <section v-if="product" class="section-card">
    <div class="section-head">
      <h2>评价与问答</h2>
      <span>{{ reviews.length }} 条评价 · {{ questions.length }} 个问题</span>
    </div>
    <div class="dual-grid">
      <div class="stack-list">
        <form class="admin-form" @submit.prevent="submitReview">
          <label>
            <span>评分</span>
            <input v-model.number="reviewForm.rating" type="number" min="1" max="5" />
          </label>
          <label>
            <span>评价内容</span>
            <input v-model="reviewForm.content" placeholder="写下商品评价" />
          </label>
          <button class="accent-button">提交评价</button>
        </form>
        <article v-for="review in reviews" :key="review.id" class="row-card">
          <div class="row-main">
            <strong>{{ review.username }} · {{ review.rating }} 星</strong>
            <span>{{ review.content }}</span>
          </div>
        </article>
        <p v-if="!reviews.length" class="muted">暂无评价。</p>
      </div>
      <div class="stack-list">
        <form class="admin-form" @submit.prevent="submitQuestion">
          <label>
            <span>向商家提问</span>
            <input v-model="questionText" placeholder="例如：是否支持七天无理由？" />
          </label>
          <button class="accent-button">提交问题</button>
        </form>
        <article v-for="question in questions" :key="question.id" class="row-card">
          <div class="row-main">
            <strong>{{ question.question }}</strong>
            <span>{{ question.answer || '商家暂未回复' }}</span>
          </div>
        </article>
        <p v-if="!questions.length" class="muted">暂无问答。</p>
      </div>
    </div>
  </section>
</template>

<script setup>
import { computed, onMounted, reactive, ref, watch } from 'vue'
import Pagination from '../components/Pagination.vue'
import { useRoute } from 'vue-router'
import {
  addCart,
  addProductReview,
  askProductQuestion,
  favoriteProduct,
  getProduct,
  getProductQuestions,
  getProductReviews
} from '../api/mall'
import { getToken } from '../utils/auth'

const route = useRoute()
const product = ref(null)
const reviews = ref([])
const questions = ref([])
const quantity = ref(1)
const selectedSkuId = ref(null)
const questionText = ref('')
const reviewForm = reactive({ rating: 5, content: '' })
const selectedSku = computed(() => product.value?.skus?.find(item => item.id === selectedSkuId.value))
const canBuy = computed(() => product.value && (selectedSku.value?.stock ?? product.value.stock) > 0)

watch(product, (value) => {
  // 商品加载完成后默认选中第一个可购买规格，方便用户直接加购。
  selectedSkuId.value = value?.skus?.find(item => item.active && item.stock > 0)?.id || value?.skus?.[0]?.id || null
})

async function loadProduct() {
  // 商品详情、评价和问答互不依赖，可以并发加载减少等待时间。
  const [productRes, reviewRes, questionRes] = await Promise.all([
    getProduct(route.params.id),
    getProductReviews(route.params.id),
    getProductQuestions(route.params.id)
  ])
  product.value = productRes.data
  reviews.value = reviewRes.data
  questions.value = questionRes.data
}

async function add() {
  // 加入购物车必须登录，因为后端购物车数据按用户编号保存。
  if (!getToken()) {
    window.alert('请先登录。')
    return
  }
  // 前端做数量范围预校验，后端仍会基于数据库库存再次校验。
  const stock = selectedSku.value?.stock ?? product.value.stock
  if (quantity.value < 1 || quantity.value > stock) {
    window.alert('请输入有效的购买数量。')
    return
  }
  try {
    // 请求体与后端加入购物车请求对象对应：商品编号、规格编号、购买数量。
    await addCart({ productId: product.value.id, skuId: selectedSku.value?.id ?? null, quantity: quantity.value })
    window.alert('已加入购物车。')
  } catch (error) {
    window.alert(error)
  }
}

async function favorite() {
  if (!getToken()) {
    window.alert('请先登录。')
    return
  }
  await favoriteProduct(product.value.id)
  window.alert('已收藏。')
  await loadProduct()
}

async function submitReview() {
  if (!getToken()) {
    window.alert('请先登录。')
    return
  }
  if (!reviewForm.content.trim()) return
  await addProductReview(product.value.id, { rating: reviewForm.rating, content: reviewForm.content })
  reviewForm.content = ''
  await loadProduct()
}

async function submitQuestion() {
  if (!getToken()) {
    window.alert('请先登录。')
    return
  }
  if (!questionText.value.trim()) return
  await askProductQuestion(product.value.id, { question: questionText.value })
  questionText.value = ''
  await loadProduct()
}

onMounted(loadProduct)
</script>
