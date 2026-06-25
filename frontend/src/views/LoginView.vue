<template>
  <section class="form-shell">
    <div class="form-card">
      <h1>欢迎回来</h1>
      <p class="muted">演示账号：用户 demo / 123，商家 merchant / 123，管理员 admin / 123。</p>
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
  username: 'admin',
  password: '123'
})

async function submit() {
  // 设置加载状态，防止用户连续点击登录按钮造成重复请求。
  loading.value = true
  try {
    // 将用户名和密码表单直接作为请求体发送给登录接口。
    const res = await login(form)
    // 保存后端返回的登录令牌和用户信息，后续接口由请求拦截器自动携带令牌。
    saveAuth(res.data)
    // 根据角色进入不同首页：平台管理员、商家后台、普通用户商城首页。
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
  // 演示项目通过弹窗收集邮箱，再调用后端忘记密码接口。
  const email = window.prompt('请输入您绑定的邮箱地址：')
  if (!email || !email.trim()) return

  try {
    // 后端根据邮箱生成新密码，并把新密码作为响应数据返回。
    const res = await forgotPassword({ email: email.trim() })
    window.alert(`重置成功！您的新密码是：${res.data}\n请使用新密码登录并及时在右上角修改密码。`)
  } catch (error) {
    window.alert(error)
  }
}
</script>
