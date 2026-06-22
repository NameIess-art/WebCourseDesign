<template>
  <section class="form-shell">
    <div class="form-card">
      <h1>欢迎回来</h1>
      <p>用户账号：demo / demo123；管理员账号：admin / admin123。</p>
      <form @submit.prevent="submit">
        <label>
          <span>用户名</span>
          <input v-model="form.username" required />
        </label>
        <label>
          <span>密码</span>
          <input v-model="form.password" type="password" required />
        </label>
        <button class="accent-button" :disabled="loading">{{ loading ? '登录中...' : '登录' }}</button>
      </form>
    </div>
  </section>
</template>

<script setup>
import { reactive, ref } from 'vue'
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
    router.push(res.data.role === 'ADMIN' ? '/admin' : '/')
  } catch (error) {
    window.alert(error)
  } finally {
    loading.value = false
  }
}
</script>
