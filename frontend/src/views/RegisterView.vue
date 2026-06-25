<template>
  <section class="form-shell">
    <div class="form-card">
      <h1>创建账号</h1>
      <form @submit.prevent="submit">
        <label>
          <span>用户名</span>
          <input v-model="form.username" autocomplete="username" required />
        </label>
        <label>
          <span>密码</span>
          <input v-model="form.password" type="password" autocomplete="new-password" required minlength="6" />
        </label>
        <label>
          <span>邮箱</span>
          <input v-model="form.email" type="email" required />
        </label>
        <label>
          <span>确认密码</span>
          <input v-model="form.confirmPassword" type="password" autocomplete="new-password" required minlength="6" />
        </label>
        <button class="accent-button" :disabled="loading">{{ loading ? '注册中...' : '注册' }}</button>
      </form>
    </div>
  </section>
</template>

<script setup>
import { reactive, ref } from 'vue'
import Pagination from '../components/Pagination.vue'
import { useRouter } from 'vue-router'
import { register } from '../api/mall'

const router = useRouter()
const loading = ref(false)
const form = reactive({
  username: '',
  email: '',
  password: '',
  confirmPassword: ''
})

async function submit() {
  if (form.password !== form.confirmPassword) {
    window.alert('两次输入的密码不一致，请重新输入')
    return
  }

  loading.value = true
  try {
    const payload = {
      username: form.username,
      password: form.password,
      displayName: form.username,
      email: form.email
    }
    await register(payload)
    window.alert('注册成功，请登录。')
    router.push('/login')
  } catch (error) {
    window.alert(error)
  } finally {
    loading.value = false
  }
}
</script>
