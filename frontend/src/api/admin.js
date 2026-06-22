import http from './http'

export const getDashboard = () => http.get('/admin/dashboard')

export const getAdminProducts = (page = 0, size = 50) => http.get('/admin/products', { params: { page, size } })
export const createProduct = (payload) => http.post('/admin/products', payload)
export const updateProduct = (id, payload) => http.put(`/admin/products/${id}`, payload)
export const deleteProduct = (id) => http.delete(`/admin/products/${id}`)

export const getAdminOrders = (status, page = 0, size = 50) =>
  http.get('/admin/orders', { params: { status, page, size } })
export const shipOrder = (id) => http.patch(`/admin/orders/${id}/ship`)
export const auditOrder = (id, payload) => http.patch(`/admin/orders/${id}/audit`, payload)
export const modifyOrder = (id, payload) => http.patch(`/admin/orders/${id}/modify`, payload)
export const getAdminAfterSales = () => http.get('/admin/after-sales')
export const processAfterSale = (id, payload) => http.patch(`/admin/after-sales/${id}/process`, payload)
export const getReconciliations = () => http.get('/admin/reconciliation')
export const createReconciliation = (bizDate) => http.post('/admin/reconciliation', null, { params: { bizDate } })

export const getOperationBoard = () => http.get('/admin/operation-board')
export const getMarketing = () => http.get('/admin/marketing')
export const createMarketing = (payload) => http.post('/admin/marketing', payload)
export const auditActivity = (id, approved) => http.patch(`/admin/activities/${id}/audit`, null, { params: { approved } })
export const createMarketingFlow = (flowType, payload) => http.post(`/admin/marketing/${flowType}`, payload)
export const getMarketingFlows = () => http.get('/admin/marketing/flows')

export const getConfigs = () => http.get('/admin/configs')
export const updateConfig = (key, payload) => http.put(`/admin/configs/${key}`, payload)
export const deleteConfig = (key) => http.delete(`/admin/configs/${key}`)
export const getDictionaries = () => http.get('/admin/dictionaries')
export const createDictionary = (payload) => http.post('/admin/dictionaries', payload)
export const deleteDictionary = (id) => http.delete(`/admin/dictionaries/${id}`)
export const getAnnouncements = () => http.get('/admin/announcements')
export const createAnnouncement = (payload) => http.post('/admin/announcements', payload)
export const deleteAnnouncement = (id) => http.delete(`/admin/announcements/${id}`)

export const getMerchants = () => http.get('/admin/merchants')
export const createMerchant = (payload) => http.post('/admin/merchants', payload)
export const updateMerchantStatus = (id, status) => http.patch(`/admin/merchants/${id}`, null, { params: { status } })
export const penalizeMerchant = (id, payload) => http.post(`/admin/merchants/${id}/penalties`, payload)

export const getRoles = () => http.get('/admin/roles')
export const createRole = (payload) => http.post('/admin/roles', payload)
export const getPermissions = () => http.get('/admin/permissions')
export const createPermission = (payload) => http.post('/admin/permissions', payload)
export const getContentAudits = () => http.get('/admin/content-audits')
export const createContentAudit = (payload) => http.post('/admin/content-audits', payload)
export const auditContent = (id, approved) => http.patch(`/admin/content-audits/${id}/audit`, null, { params: { approved } })

export const getPromotionRules = () => http.get('/admin/promotion-rules')
export const createPromotionRule = (payload) => http.post('/admin/promotion-rules', payload)
export const getAnalytics = (type) => http.get('/admin/analytics', { params: { type } })

export const getRiskItems = () => http.get('/admin/risk')
export const resolveRisk = (id) => http.patch(`/admin/risk/${id}/resolve`)
export const getReports = () => http.get('/admin/reports')
export const getHighConcurrency = () => http.get('/admin/high-concurrency')
export const createSeckillEvent = (payload) => http.post('/admin/seckill-events', payload)
