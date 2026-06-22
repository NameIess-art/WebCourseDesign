<template>
  <div class="layout-shell">
    <aside class="sidebar" :class="{ open: drawerOpen }" aria-label="主导航">
      <div class="sidebar-header">
        <button class="brand-button" @click="go('/')">MallSystem</button>
        <span class="role-pill">{{ roleLabel }}</span>
      </div>

      <nav class="sidebar-nav">
        <section class="sidebar-group">
          <p>用户前台</p>
          <button
            v-for="item in userLinks"
            :key="item.to + item.label"
            class="sidebar-link"
            :class="{ active: isActive(item) }"
            @click="go(item.to)"
          >
            <span class="nav-dot"></span>
            {{ item.label }}
          </button>
        </section>

        <section v-if="canMerchant" class="sidebar-group">
          <p>商家后台</p>
          <button
            v-for="item in merchantLinks"
            :key="item.to + item.label"
            class="sidebar-link"
            :class="{ active: isActive(item) }"
            @click="go(item.to)"
          >
            <span class="nav-dot"></span>
            {{ item.label }}
          </button>
        </section>

        <section v-if="canPlatform" class="sidebar-group">
          <p>运营总后台</p>
          <button
            v-for="item in platformLinks"
            :key="item.to + item.label"
            class="sidebar-link"
            :class="{ active: isActive(item) }"
            @click="go(item.to)"
          >
            <span class="nav-dot"></span>
            {{ item.label }}
          </button>
        </section>
      </nav>
    </aside>

    <button
      v-if="drawerOpen"
      class="sidebar-scrim"
      aria-label="关闭导航"
      @click="drawerOpen = false"
    ></button>

    <div class="layout-main">
      <header class="content-topbar">
        <button class="mobile-menu-button" @click="drawerOpen = true">菜单</button>
        <div>
          <strong>{{ pageTitle }}</strong>
          <p>{{ pageSubtitle }}</p>
        </div>
        <div class="user-area">
          <template v-if="user">
            <span>{{ user.username }}</span>
            <button class="ghost-button" @click="logout">退出</button>
          </template>
          <template v-else>
            <button class="ghost-button" @click="go('/login')">登录</button>
            <button class="accent-button compact" @click="go('/register')">注册</button>
          </template>
        </div>
      </header>

      <main class="page-shell">
        <slot />
      </main>
    </div>
  </div>
</template>

<script setup>
import { computed, ref, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { clearAuth, getUser } from '../utils/auth'

const router = useRouter()
const route = useRoute()
const drawerOpen = ref(false)
const user = ref(getUser())
const canMerchant = computed(() => ['ADMIN', 'MERCHANT'].includes(user.value?.role))
const canPlatform = computed(() => user.value?.role === 'ADMIN')

const userLinks = [
  { label: '首页推荐', to: '/' },
  { label: '分类导航', to: '/#categories' },
  { label: '活动会场', to: '/activities' },
  { label: '购物车', to: '/cart' },
  { label: '我的订单', to: '/orders' },
  { label: '会员中心', to: '/features' },
  { label: '限时秒杀', to: '/features#seckill' }
]

const merchantLinks = [
  { label: '商家首页', to: '/merchant' },
  { label: '商品管理', to: '/merchant#products' },
  { label: '订单处理', to: '/merchant#orders' },
  { label: '营销工具', to: '/merchant#marketing' },
  { label: '数据看板', to: '/merchant#dashboard' }
]

const platformLinks = [
  { label: '平台首页', to: '/platform' },
  { label: '活动运营', to: '/platform#activities' },
  { label: '权限风控', to: '/platform#risk' },
  { label: '配置中心', to: '/platform#configs' },
  { label: '数据分析', to: '/platform#analytics' }
]

const roleLabel = computed(() => {
  if (user.value?.role === 'ADMIN') return '平台管理员'
  if (user.value?.role === 'MERCHANT') return '商家'
  if (user.value?.role === 'USER') return '会员'
  return '游客'
})

const pageTitle = computed(() => {
  if (route.path.startsWith('/merchant')) return '商家后台'
  if (route.path.startsWith('/platform')) return '运营总后台'
  if (route.path.startsWith('/activities')) return '活动会场'
  if (route.path.startsWith('/cart')) return '购物车'
  if (route.path.startsWith('/orders')) return '我的订单'
  if (route.path.startsWith('/features')) return '会员与服务中心'
  if (route.path.startsWith('/products')) return '商品详情'
  if (route.path.startsWith('/login')) return '登录'
  if (route.path.startsWith('/register')) return '注册'
  return '商城首页'
})

const pageSubtitle = computed(() => {
  if (route.path.startsWith('/merchant')) return '集中处理商品、订单、营销和经营数据'
  if (route.path.startsWith('/platform')) return '统一运营活动、权限、配置、风控和数据分析'
  if (route.path.startsWith('/activities')) return '查看平台审核通过的营销活动'
  if (route.path.startsWith('/cart')) return '确认商品、优惠、积分抵扣和收货地址'
  if (route.path.startsWith('/orders')) return '跟踪支付、发货、物流和售后进度'
  return '高并发电商系统演示'
})

watch(
  () => route.fullPath,
  () => {
    user.value = getUser()
    drawerOpen.value = false
  }
)

function isActive(item) {
  const [path, hash] = item.to.split('#')
  if (hash) return route.path === path && route.hash === `#${hash}`
  return route.path === path && !route.hash
}

function go(to) {
  router.push(to)
}

function logout() {
  clearAuth()
  user.value = null
  router.push('/login')
}
</script>
