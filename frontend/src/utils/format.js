export function formatStatus(status) {
  if (!status) return '未知'
  
  const map = {
    // 订单与售后
    'UNPAID': '未支付',
    'PAID': '已支付',
    'SHIPPED': '已发货',
    'DELIVERED': '已送达',
    'COMPLETED': '已完成',
    'CANCELLED': '已取消',
    'REFUNDED': '已退款',
    'PENDING': '待处理',
    'APPROVED': '已通过',
    'REJECTED': '已拒绝',
    
    // 审核与活动
    'ACTIVE': '正常',
    'INACTIVE': '已禁用',
    'RUNNING': '进行中',
    'PENDING_REVIEW': '待审核',
    
    // 类型与其他
    'TEXT': '文本',
    'IMAGE': '图片',
    'MERCHANT': '商家',
    'USER': '用户',
    'ADMIN': '平台管理员',
    'BASIC': '普通会员',
    'SILVER': '白银会员',
    'GOLD': '黄金会员',
    'PLATINUM': '铂金会员',
    
    'LIMITED_TIME': '限时活动',
    'GROUP_BUY': '拼团',
    'DISTRIBUTION': '分销',
    'PRE_SALE': '预售',
    'BUNDLE': '组合套餐',
    'SECKILL': '限时秒杀',
    'FULL_REDUCTION': '满减活动',
    
    'PRICE_ABNORMAL': '价格异常',
    'CONTENT_AUDIT': '内容审核',
    'MERCHANT_FRAUD': '商家欺诈',
    
    'PLATFORM': '平台',
  }
  
  return map[status] || status
}
