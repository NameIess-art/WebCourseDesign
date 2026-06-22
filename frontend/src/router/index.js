import { createRouter, createWebHistory } from 'vue-router'
import HomeView from '../views/HomeView.vue'
import LoginView from '../views/LoginView.vue'
import RegisterView from '../views/RegisterView.vue'
import ProductView from '../views/ProductView.vue'
import CartView from '../views/CartView.vue'
import OrdersView from '../views/OrdersView.vue'
import AdminView from '../views/AdminView.vue'
import FeatureCenterView from '../views/FeatureCenterView.vue'
import { getToken, getUser } from '../utils/auth'

const routes = [
  { path: '/', component: HomeView },
  { path: '/login', component: LoginView },
  { path: '/register', component: RegisterView },
  { path: '/products/:id', component: ProductView },
  { path: '/cart', component: CartView, meta: { auth: true } },
  { path: '/orders', component: OrdersView, meta: { auth: true } },
  { path: '/features', component: FeatureCenterView, meta: { auth: true } },
  { path: '/admin', component: AdminView, meta: { auth: true, admin: true } }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

router.beforeEach((to) => {
  const token = getToken()
  const user = getUser()
  if (to.meta.auth && !token) {
    return '/login'
  }
  if (to.meta.admin && !['ADMIN', 'MERCHANT'].includes(user?.role)) {
    return '/'
  }
  return true
})

export default router
