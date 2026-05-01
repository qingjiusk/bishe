import request from './request'

export const createOrder = (addressId, deliveryType, address = null) => {
  return request.post('/order/create', { addressId, deliveryType, address })
}

export const getBuyerOrders = (status, page = 1, size = 10) => {
  return request.get('/order/buyer/list', { params: { status, page, size } })
}

export const getSellerOrders = (status, page = 1, size = 10) => {
  return request.get('/order/seller/list', { params: { status, page, size } })
}

export const getOrderDetail = (orderNo) => {
  return request.get(`/order/${orderNo}`)
}

export const confirmReceive = (orderId) => {
  return request.put(`/order/${orderId}/confirm`)
}

export const requestRefund = (orderId, reason) => {
  return request.post(`/order/${orderId}/refund`, { reason })
}

export const getRefundOrders = (page = 1, size = 10) => {
  return request.get('/admin/refunds', { params: { page, size } })
}

export const approveRefund = (orderId) => {
  return request.put(`/admin/refund/${orderId}/approve`)
}

export const rejectRefund = (orderId, reply) => {
  return request.put(`/admin/refund/${orderId}/reject`, { reply })
}