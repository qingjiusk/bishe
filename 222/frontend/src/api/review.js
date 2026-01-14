import request from './request'

export const addReview = (orderId, sellerId, rating, content) => {
  return request.post(`/review/add/${orderId}`, { sellerId, rating, content })
}

export const getSellerReviews = (sellerId, page = 1, size = 10) => {
  return request.get(`/review/seller/${sellerId}`, { params: { page, size } })
}