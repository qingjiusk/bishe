import request from './request'

export const addToCart = (productId, quantity = 1) => {
  return request.post('/cart/add', { productId, quantity })
}

export const getCartList = (page = 1, size = 10) => {
  return request.get('/cart/list', { params: { page, size } })
}

export const updateCartQuantity = (cartId, quantity) => {
  return request.put(`/cart/${cartId}`, { quantity })
}

export const removeFromCart = (cartId) => {
  return request.delete(`/cart/${cartId}`)
}

export const clearCart = () => {
  return request.delete('/cart/clear')
}