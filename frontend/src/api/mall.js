import http from './http'

// 登录、注册：表单数据以请求体传给后端认证接口。
export const login = (payload) => http.post('/auth/login', payload)
export const register = (payload) => http.post('/auth/register', payload)

// 商品浏览：查询条件放在地址参数中，后端按关键字、分类和分页组合查询。
export const getCategories = (page = 0, size = 10) => http.get('/categories', { params: { page, size } })
export const getProducts = (keyword, categoryId, page = 0, size = 20) =>
  http.get('/products', { params: { keyword, categoryId, page, size } })
export const getProduct = (id) => http.get(`/products/${id}`)
export const suggestSearch = (keyword) => http.get('/search/suggest', { params: { keyword } })
export const getRecommendations = (page = 0, size = 10) => http.get('/recommendations', { params: { page, size } })
export const getActivities = (page = 0, size = 10) => http.get('/activities', { params: { page, size } })

// 购物车和结算：加购发送商品、规格、数量；结算发送地址、优惠券、积分和幂等键。
export const getCart = (page = 0, size = 10) => http.get('/cart', { params: { page, size } })
export const addCart = (payload) => http.post('/cart', payload)
export const updateCart = (id, quantity) => http.put(`/cart/${id}`, { quantity })
export const removeCart = (id) => http.delete(`/cart/${id}`)
export const checkout = (payload) => http.post('/orders/checkout', payload)

// 订单状态操作：支付、取消、确认收货都通过路径中的订单编号定位目标订单。
export const getOrders = (page = 0, size = 20) => http.get('/orders', { params: { page, size } })
export const payOrder = (id) => http.patch(`/orders/${id}/pay`)
export const cancelOrder = (id) => http.patch(`/orders/${id}/cancel`)
export const receiveOrder = (id) => http.patch(`/orders/${id}/receive`)

export const createPayment = (payload) => http.post('/payments', payload)
export const mockPaymentCallback = (payload) => http.post('/payments/callback/mock', payload)
export const getPayments = (orderId) => http.get('/payments', { params: { orderId } })

export const getAddresses = (page = 0, size = 10) => http.get('/addresses', { params: { page, size } })
export const saveAddress = (payload) => http.post('/addresses', payload)
export const updateAddress = (id, payload) => http.put(`/addresses/${id}`, payload)
export const deleteAddress = (id) => http.delete(`/addresses/${id}`)

export const getCoupons = (page = 0, size = 10) => http.get('/coupons', { params: { page, size } })
export const claimCoupon = (id) => http.post(`/coupons/${id}/claim`)

export const getFavorites = (page = 0, size = 10) => http.get('/favorites', { params: { page, size } })
export const favoriteProduct = (id) => http.post(`/favorites/${id}`)
export const unfavoriteProduct = (id) => http.delete(`/favorites/${id}`)

export const getProductReviews = (id) => http.get(`/products/${id}/reviews`)
export const addProductReview = (id, payload) => http.post(`/products/${id}/reviews`, payload)
export const getProductQuestions = (id) => http.get(`/products/${id}/questions`)
export const askProductQuestion = (id, payload) => http.post(`/products/${id}/questions`, payload)

export const getMemberProfile = (page = 0, size = 10) => http.get('/member/profile', { params: { page, size } })
export const getMemberPoints = (page = 0, size = 10) => http.get('/member/points', { params: { page, size } })
export const getPointRecords = (page = 0, size = 10) => http.get('/member/point-records', { params: { page, size } })
export const getMemberMessages = (page = 0, size = 10) => http.get('/member/messages', { params: { page, size } })
export const readMessage = (id) => http.patch(`/member/messages/${id}/read`)

export const getLogistics = (id) => http.get(`/orders/${id}/logistics`)
export const requestAfterSale = (id, payload) => http.post(`/orders/${id}/after-sales`, payload)
export const getAfterSales = (page = 0, size = 10) => http.get('/after-sales', { params: { page, size } })

export const getSeckillEvents = (page = 0, size = 10) => http.get('/seckill-events', { params: { page, size } })
export const purchaseSeckill = (id) => http.post(`/seckill-events/${id}/purchase`)

export const updateAccount = (payload) => http.put('/auth/account', payload)
export const forgotPassword = (payload) => http.post('/auth/forgot-password', payload)
