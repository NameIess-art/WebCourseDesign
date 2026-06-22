<template>
  <section v-if="product" class="product-detail">
    <img :src="product.imageUrl" :alt="product.name" class="detail-image" />
    <div class="detail-body">
      <span class="tag-chip">{{ product.categoryName }}</span>
      <h1>{{ product.name }}</h1>
      <p class="detail-subtitle">{{ product.subtitle }}</p>
      <p>{{ product.description }}</p>
      <div class="tag-list">
        <span class="tag-chip">{{ product.skuCode || 'SKU' }}</span>
        <span class="tag-chip">{{ product.spec || '标准规格' }}</span>
        <span class="tag-chip">{{ product.promotionTag || '活动商品' }}</span>
        <span class="tag-chip">评分 {{ product.rating || '4.80' }}</span>
      </div>
      <div class="detail-price">￥{{ product.price }}</div>
      <div class="quantity-row">
        <label>购买数量（库存 {{ product.stock }}）</label>
        <input v-model.number="quantity" type="number" min="1" :max="product.stock" />
      </div>
      <div class="row-actions">
        <button class="accent-button" :disabled="product.stock === 0" @click="add">
          {{ product.stock === 0 ? '商品已售罄' : '加入购物车' }}
        </button>
        <button class="ghost-button" @click="favorite">收藏</button>
      </div>
    </div>
  </section>

  <section v-if="product" class="section-card">
    <div class="section-head">
      <h2>评价与问答</h2>
      <span>{{ reviews.length }} 条评价 · {{ questions.length }} 个问答</span>
    </div>
    <div class="dual-grid">
      <div class="stack-list">
        <form class="admin-form" @submit.prevent="submitReview">
          <input v-model.number="reviewForm.rating" type="number" min="1" max="5" placeholder="评分 1-5" />
          <input v-model="reviewForm.content" placeholder="写下商品评价" />
          <button class="accent-button">提交评价</button>
        </form>
        <article v-for="review in reviews" :key="review.id" class="row-card">
          <div class="row-main">
            <strong>{{ review.username }} · {{ review.rating }} 星</strong>
            <span>{{ review.content }}</span>
          </div>
        </article>
      </div>
      <div class="stack-list">
        <form class="admin-form" @submit.prevent="submitQuestion">
          <input v-model="questionText" placeholder="向商家提问" />
          <button class="accent-button">提交问题</button>
        </form>
        <article v-for="question in questions" :key="question.id" class="row-card">
          <div class="row-main">
            <strong>{{ question.question }}</strong>
            <span>{{ question.answer }}</span>
          </div>
        </article>
      </div>
    </div>
  </section>
</template>

<script setup>
import { onMounted, reactive, ref } from 'vue'
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
const questionText = ref('')
const reviewForm = reactive({ rating: 5, content: '' })

async function loadProduct() {
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
  if (!getToken()) {
    window.alert('请先登录。')
    return
  }
  if (quantity.value < 1 || quantity.value > product.value.stock) {
    window.alert('请输入有效的购买数量。')
    return
  }
  try {
    await addCart({ productId: product.value.id, quantity: quantity.value })
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
