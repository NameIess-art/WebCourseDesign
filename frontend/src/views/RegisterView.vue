<template>
  <section class="form-shell">
    <div class="form-card">
      <h1>创建账号</h1>
      <form @submit.prevent="submit">
        <label>
          <span>显示名称</span>
          <input v-model="form.displayName" required />
        </label>
        <label>
          <span>用户名</span>
          <input v-model="form.username" required />
        </label>
        <label>
          <span>邮箱</span>
          <input v-model="form.email" type="email" required />
        </label>
        <label>
          <span>密码</span>
          <input v-model="form.password" type="password" required minlength="6" />
        </label>
        <button class="accent-button">注册</button>
      </form>
    </div>
  </section>
</template>

<script setup>
import { reactive } from 'vue'
import { useRouter } from 'vue-router'
import { register } from '../api/mall'

const router = useRouter()
const form = reactive({
  displayName: '',
  username: '',
  email: '',
  password: ''
})

async function submit() {
  try {
    await register(form)
    window.alert('注册成功，请登录。')
    router.push('/login')
  } catch (error) {
    window.alert(error)
  }
}
</script>
