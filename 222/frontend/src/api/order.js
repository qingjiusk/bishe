import request from './request'

export const createOrder = (addressId, deliveryType) => {
  return request.post('/order/create', { addressId, deliveryType })
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