import http from './http'

export const getDashboard = () => http.get('/admin/dashboard')

// 商品管理：商家后台提交的对象会映射到后端商品请求体。
export const getAdminProducts = (page = 0, size = 10) => http.get('/admin/products', { params: { page, size } })
export const createProduct = (payload) => http.post('/admin/products', payload)
export const updateProduct = (id, payload) => http.put(`/admin/products/${id}`, payload)
export const deleteProduct = (id) => http.delete(`/admin/products/${id}`)

// 订单管理：状态筛选通过查询参数传递，发货、审核、改价通过订单编号操作。
export const getAdminOrders = (status, page = 0, size = 10) =>
  http.get('/admin/orders', { params: { status, page, size } })
export const shipOrder = (id) => http.patch(`/admin/orders/${id}/ship`)
export const auditOrder = (id, payload) => http.patch(`/admin/orders/${id}/audit`, payload)
export const modifyOrder = (id, payload) => http.patch(`/admin/orders/${id}/modify`, payload)
export const getAdminAfterSales = (page = 0, size = 10) => http.get('/admin/after-sales', { params: { page, size } })
export const processAfterSale = (id, payload) => http.patch(`/admin/after-sales/${id}/process`, payload)
export const getReconciliations = (page = 0, size = 10) => http.get('/admin/reconciliation', { params: { page, size } })
export const createReconciliation = (bizDate) => http.post('/admin/reconciliation', null, { params: { bizDate } })

export const getOperationBoard = () => http.get('/admin/operation-board')
export const getMarketing = (page = 0, size = 10) => http.get('/admin/marketing', { params: { page, size } })
export const createMarketing = (payload) => http.post('/admin/marketing', payload)
export const auditActivity = (id, approved) => http.patch(`/admin/activities/${id}/audit`, null, { params: { approved } })
export const createMarketingFlow = (flowType, payload) => http.post(`/admin/marketing/${flowType}`, payload)
export const getMarketingFlows = (page = 0, size = 10) => http.get('/admin/marketing/flows', { params: { page, size } })

export const getConfigs = (page = 0, size = 10) => http.get('/admin/configs', { params: { page, size } })
export const updateConfig = (key, payload) => http.put(`/admin/configs/${key}`, payload)
export const deleteConfig = (key) => http.delete(`/admin/configs/${key}`)
export const getDictionaries = (page = 0, size = 10) => http.get('/admin/dictionaries', { params: { page, size } })
export const createDictionary = (payload) => http.post('/admin/dictionaries', payload)
export const deleteDictionary = (id) => http.delete(`/admin/dictionaries/${id}`)
export const getAnnouncements = (page = 0, size = 10) => http.get('/admin/announcements', { params: { page, size } })
export const createAnnouncement = (payload) => http.post('/admin/announcements', payload)
export const deleteAnnouncement = (id) => http.delete(`/admin/announcements/${id}`)

export const getMerchants = (page = 0, size = 10) => http.get('/admin/merchants', { params: { page, size } })
export const createMerchant = (payload) => http.post('/admin/merchants', payload)
export const updateMerchantStatus = (id, status) => http.patch(`/admin/merchants/${id}`, null, { params: { status } })
export const penalizeMerchant = (id, payload) => http.post(`/admin/merchants/${id}/penalties`, payload)

export const getRoles = (page = 0, size = 10) => http.get('/admin/roles', { params: { page, size } })
export const createRole = (payload) => http.post('/admin/roles', payload)
export const getPermissions = (page = 0, size = 10) => http.get('/admin/permissions', { params: { page, size } })
export const createPermission = (payload) => http.post('/admin/permissions', payload)
export const getContentAudits = (page = 0, size = 10) => http.get('/admin/content-audits', { params: { page, size } })
export const createContentAudit = (payload) => http.post('/admin/content-audits', payload)
export const auditContent = (id, approved) => http.patch(`/admin/content-audits/${id}/audit`, null, { params: { approved } })

export const getPromotionRules = (page = 0, size = 10) => http.get('/admin/promotion-rules', { params: { page, size } })
export const createPromotionRule = (payload) => http.post('/admin/promotion-rules', payload)
export const getAnalytics = (type) => http.get('/admin/analytics', { params: { type } })

export const getRiskItems = (page = 0, size = 10) => http.get('/admin/risk', { params: { page, size } })
export const resolveRisk = (id) => http.patch(`/admin/risk/${id}/resolve`)
export const getReports = () => http.get('/admin/reports')
export const getHighConcurrency = () => http.get('/admin/high-concurrency')
export const createSeckillEvent = (payload) => http.post('/admin/seckill-events', payload)
