import request from './request'

export const pay = (orderNo) => {
  return request.post('/alipay/pay', { orderNo })
}

export const queryPayStatus = (orderNo) => {
  return request.get(`/alipay/query/${orderNo}`)
}
