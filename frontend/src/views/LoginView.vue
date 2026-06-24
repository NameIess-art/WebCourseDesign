<template>
  <section class="form-shell">
    <div class="form-card">
      <h1>欢迎回来</h1>
      <p class="muted">演示账号：用户 demo / demo123，商家 merchant / merchant123，管理员 admin / admin123。</p>
      <form @submit.prevent="submit">
        <label>
          <span>用户名</span>
          <input v-model="form.username" autocomplete="username" required />
        </label>
        <label>
          <span>密码</span>
          <input v-model="form.password" type="password" autocomplete="current-password" required />
        </label>
        <button class="accent-button" :disabled="loading">{{ loading ? '登录中...' : '登录' }}</button>
      </form>
    </div>
  </section>
</template>

<script setup>
import { reactive, ref } from 'vue'
import Pagination from '../components/Pagination.vue'
import { useRouter } from 'vue-router'
import { login } from '../api/mall'
import { saveAuth } from '../utils/auth'

const router = useRouter()
const loading = ref(false)
const form = reactive({
  username: 'demo',
  password: 'demo123'
})

async function submit() {
  loading.value = true
  try {
    const res = await login(form)
    saveAuth(res.data)
    if (res.data.role === 'ADMIN') router.push('/platform')
    else if (res.data.role === 'MERCHANT') router.push('/merchant')
    else router.push('/')
  } catch (error) {
    window.alert(error)
  } finally {
    loading.value = false
  }
}
</script>
