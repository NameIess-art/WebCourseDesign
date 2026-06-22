<template>
  <header class="topbar">
    <div class="brand" @click="$router.push('/')">商城</div>
    <nav class="nav-links">
      <button class="nav-link" @click="$router.push('/')">首页</button>
      <button class="nav-link" @click="$router.push('/cart')">购物车</button>
      <button class="nav-link" @click="$router.push('/orders')">我的订单</button>
      <button class="nav-link" @click="$router.push('/features')">能力中心</button>
      <button v-if="user?.role === 'ADMIN'" class="nav-link" @click="$router.push('/admin')">管理后台</button>
    </nav>
    <div class="user-area">
      <template v-if="user">
        <span class="user-badge">{{ user.displayName }}</span>
        <button class="ghost-button" @click="logout">退出登录</button>
      </template>
      <template v-else>
        <button class="ghost-button" @click="$router.push('/login')">登录</button>
        <button class="accent-button" @click="$router.push('/register')">注册</button>
      </template>
    </div>
  </header>
</template>

<script setup>
import { ref, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { clearAuth, getUser } from '../utils/auth'

const router = useRouter()
const route = useRoute()
const user = ref(getUser())

watch(() => route.fullPath, () => {
  user.value = getUser()
}, { immediate: true })

function logout() {
  clearAuth()
  user.value = null
  router.push('/')
}
</script>
