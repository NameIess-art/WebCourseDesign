import { createRouter, createWebHistory } from 'vue-router'
import HomeView from '../views/HomeView.vue'
import LoginView from '../views/LoginView.vue'
import RegisterView from '../views/RegisterView.vue'
import ProductView from '../views/ProductView.vue'
import CartView from '../views/CartView.vue'
import OrdersView from '../views/OrdersView.vue'
import MerchantView from '../views/MerchantView.vue'
import PlatformView from '../views/PlatformView.vue'
import FeatureCenterView from '../views/FeatureCenterView.vue'
import ActivitiesView from '../views/ActivitiesView.vue'
import { getToken, getUser } from '../utils/auth'

const routes = [
  { path: '/', component: HomeView },
  { path: '/login', component: LoginView },
  { path: '/register', component: RegisterView },
  { path: '/products/:id', component: ProductView },
  { path: '/cart', component: CartView, meta: { auth: true } },
  { path: '/orders', component: OrdersView, meta: { auth: true } },
  { path: '/features', component: FeatureCenterView, meta: { auth: true } },
  { path: '/activities', component: ActivitiesView },
  { path: '/merchant', component: MerchantView, meta: { auth: true, merchant: true } },
  { path: '/platform', component: PlatformView, meta: { auth: true, platform: true } },
  {
    path: '/admin',
    redirect: () => {
      const user = getUser()
      if (user?.role === 'ADMIN') return '/platform'
      if (user?.role === 'MERCHANT') return '/merchant'
      return '/'
    }
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes,
  scrollBehavior(to) {
    if (to.hash) {
      return { el: to.hash, top: 90, behavior: 'smooth' }
    }
    return { top: 0 }
  }
})

router.beforeEach((to) => {
  const token = getToken()
  const user = getUser()
  if (to.meta.auth && !token) {
    return '/login'
  }
  if (to.meta.merchant && !['ADMIN', 'MERCHANT'].includes(user?.role)) {
    return '/'
  }
  if (to.meta.platform && user?.role !== 'ADMIN') {
    return '/'
  }
  return true
})

export default router
