import request from './request'

export const addComment = (productId, content) => {
  return request.post(`/comment/add/${productId}`, { content })
}

export const replyComment = (commentId, reply) => {
  return request.put(`/comment/reply/${commentId}`, { reply })
}

export const getProductComments = (productId, page = 1, size = 10) => {
  return request.get(`/comment/product/${productId}`, { params: { page, size } })
}