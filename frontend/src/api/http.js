import axios from 'axios'
import router from '../router'
import { clearAuth, getToken } from '../utils/auth'

const http = axios.create({
  // 开发环境由前端开发服务器代理到后端，业务代码只需要写接口路径。
  baseURL: '/api',
  timeout: 10000
})

http.interceptors.request.use((config) => {
  // 登录后保存的令牌会在这里自动加到请求头，后端认证过滤器据此识别用户。
  const token = getToken()
  if (token) {
    config.headers.Authorization = `Bearer ${token}`
  }
  // 管理员后台通过商家编号请求头指定当前正在管理的商家。
  const merchantId = localStorage.getItem('merchantId')
  if (merchantId) {
    config.headers['X-Merchant-Id'] = merchantId
  }
  return config
})

http.interceptors.response.use(
  // 后端统一返回响应对象，页面拿到的就是状态码、提示信息和业务数据结构。
  (response) => response.data,
  (error) => {
    if (error.response?.status === 401) {
      // 登录过期或令牌无效时清理本地状态，并引导用户重新登录。
      clearAuth()
      router.push('/login')
    }
    return Promise.reject(error.response?.data?.message || error.message)
  }
)

export default http
