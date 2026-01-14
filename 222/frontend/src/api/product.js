import request from './request'

export const getProductList = (page = 1, size = 10) => {
  return request.get('/product/list', { params: { page, size } })
}

export const getProductsByCategory = (categoryId, page = 1, size = 10) => {
  return request.get(`/product/category/${categoryId}`, { params: { page, size } })
}

export const searchProducts = (keyword, page = 1, size = 10) => {
  return request.get('/product/search', { params: { keyword, page, size } })
}

export const getProductDetail = (id) => {
  return request.get(`/product/${id}`)
}

export const publishProduct = (productData) => {
  return request.post('/product/publish', productData)
}

export const updateProduct = (id, productData) => {
  return request.put(`/product/${id}`, productData)
}

export const deleteProduct = (id) => {
  return request.delete(`/product/${id}`)
}

export const getMyProducts = (auditStatus, page = 1, size = 10) => {
  return request.get('/product/my', { params: { auditStatus, page, size } })
}