import request from './request'

export const addFavorite = (productId) => {
  return request.post(`/favorite/add/${productId}`)
}

export const removeFavorite = (productId) => {
  return request.delete(`/favorite/remove/${productId}`)
}

export const isFavorite = (productId) => {
  return request.get(`/favorite/check/${productId}`)
}

export const getFavoriteList = (page = 1, size = 10) => {
  return request.get('/favorite/list', { params: { page, size } })
}