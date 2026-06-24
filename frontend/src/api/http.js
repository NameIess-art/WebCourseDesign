import axios from 'axios'
import router from '../router'
import { clearAuth, getToken } from '../utils/auth'

const http = axios.create({
  baseURL: '/api',
  timeout: 10000
})

http.interceptors.request.use((config) => {
  const token = getToken()
  if (token) {
    config.headers.Authorization = `Bearer ${token}`
  }
  const merchantId = localStorage.getItem('merchantId')
  if (merchantId) {
    config.headers['X-Merchant-Id'] = merchantId
  }
  return config
})

http.interceptors.response.use(
  (response) => response.data,
  (error) => {
    if (error.response?.status === 401) {
      clearAuth()
      router.push('/login')
    }
    return Promise.reject(error.response?.data?.message || error.message)
  }
)

export default http
