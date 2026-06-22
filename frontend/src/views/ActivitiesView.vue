<template>
  <div class="activity-page">
    <section class="admin-hero">
      <div>
        <p class="eyebrow">Campaign Venue</p>
        <h1>活动会场</h1>
        <p class="muted">展示平台审核通过的满减、秒杀、拼团、预售、分销和组合套餐活动。</p>
      </div>
      <button class="ghost-button" :disabled="loading" @click="loadData">刷新活动</button>
    </section>

    <section class="module-section">
      <div class="section-head">
        <div>
          <p class="eyebrow">Approved</p>
          <h2>可参与活动</h2>
        </div>
        <span class="tag-chip">{{ approvedActivities.length }} 个活动</span>
      </div>

      <div v-if="loading" class="notice-box">
        <strong>正在加载活动</strong>
        <span class="muted">正在同步平台审核后的活动会场。</span>
      </div>

      <div v-else-if="!approvedActivities.length" class="notice-box">
        <strong>暂无可参与活动</strong>
        <span class="muted">商家创建活动并经平台审核通过后，会展示在这里。</span>
      </div>

      <div v-else class="activity-grid">
        <article v-for="activity in approvedActivities" :key="activity.id" class="activity-card">
          <div class="activity-card-head">
            <span class="activity-type">{{ typeLabel(activity.type) }}</span>
            <span class="activity-status">{{ activity.status }}</span>
          </div>
          <h3>{{ activity.title }}</h3>
          <p>{{ activity.ruleText }}</p>
          <div class="activity-timeline">
            <span>开始：{{ formatDate(activity.startAt) }}</span>
            <span>结束：{{ formatDate(activity.endAt) }}</span>
          </div>
          <div class="row-actions">
            <button class="accent-button compact" @click="primaryAction(activity)">{{ actionLabel(activity.type) }}</button>
            <button class="ghost-button" @click="goProducts">浏览商品</button>
          </div>
        </article>
      </div>
    </section>

    <section v-if="recommendations.activity?.length" class="module-section">
      <div class="section-head">
        <div>
          <p class="eyebrow">Products</p>
          <h2>活动推荐商品</h2>
        </div>
      </div>
      <div class="product-grid">
        <article v-for="product in recommendations.activity" :key="product.id" class="product-card">
          <img :src="product.imageUrl" :alt="product.name" loading="lazy" />
          <div class="product-body">
            <span class="product-category">{{ product.categoryName }} · {{ product.promotionTag }}</span>
            <h3>{{ product.name }}</h3>
            <p>{{ product.subtitle }}</p>
            <div class="product-meta">
              <strong>¥{{ product.price }}</strong>
              <span>库存 {{ product.stock }}</span>
            </div>
            <button class="ghost-button" @click="openProduct(product.id)">查看详情</button>
          </div>
        </article>
      </div>
    </section>
  </div>
</template>

<script setup>
import { computed, onMounted, ref } from 'vue'
import { useRouter } from 'vue-router'
import { getActivities, getRecommendations } from '../api/mall'

const router = useRouter()
const loading = ref(false)
const activities = ref([])
const recommendations = ref({ activity: [] })

const approvedActivities = computed(() => activities.value.filter((item) => item.status === 'APPROVED'))

onMounted(loadData)

async function loadData() {
  loading.value = true
  try {
    const [activityRes, recommendationRes] = await Promise.all([getActivities(), getRecommendations()])
    activities.value = activityRes.data || []
    recommendations.value = recommendationRes.data || { activity: [] }
  } finally {
    loading.value = false
  }
}

function typeLabel(type) {
  const map = {
    LIMITED_TIME: '限时活动',
    FULL_REDUCTION: '满减活动',
    SECKILL: '秒杀',
    GROUP_BUY: '拼团',
    DISTRIBUTION: '分销',
    PRE_SALE: '预售',
    BUNDLE: '组合套餐'
  }
  return map[type] || type
}

function actionLabel(type) {
  if (type === 'SECKILL') return '去秒杀'
  if (type === 'FULL_REDUCTION') return '去凑单'
  if (type === 'GROUP_BUY') return '去拼团'
  if (type === 'PRE_SALE') return '看预售'
  return '去参与'
}

function formatDate(value) {
  if (!value) return '待排期'
  return new Date(value).toLocaleString('zh-CN', {
    month: '2-digit',
    day: '2-digit',
    hour: '2-digit',
    minute: '2-digit'
  })
}

function primaryAction(activity) {
  if (activity.type === 'SECKILL') router.push('/features#seckill')
  else router.push('/')
}

function goProducts() {
  router.push('/')
}

function openProduct(id) {
  router.push(`/products/${id}`)
}
</script>
