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
        <div style="display: flex; justify-content: space-between; align-items: center; margin-top: -10px; margin-bottom: 10px;">
          <a href="#" @click.prevent="handleForgotPassword" style="font-size: 0.9em; color: var(--accent-color); text-decoration: none;">忘记密码？</a>
        </div>
        <button class="accent-button" :disabled="loading">{{ loading ? '登录中...' : '登录' }}</button>
      </form>
    </div>
  </section>
</template>

<script setup>
import { reactive, ref } from 'vue'
import Pagination from '../components/Pagination.vue'
import { useRouter } from 'vue-router'
import { forgotPassword, login } from '../api/mall'
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

async function handleForgotPassword() {
  const email = window.prompt('请输入您绑定的邮箱地址：')
  if (!email || !email.trim()) return

  try {
    const res = await forgotPassword({ email: email.trim() })
    window.alert(`重置成功！您的新密码是：${res.data}\n请使用新密码登录并及时在右上角修改密码。`)
  } catch (error) {
    window.alert(error)
  }
}
</script>
