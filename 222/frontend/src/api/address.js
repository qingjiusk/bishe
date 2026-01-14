import request from './request'

export const addAddress = (addressData) => {
  return request.post('/address/add', addressData)
}

export const updateAddress = (addressData) => {
  return request.put('/address/update', addressData)
}

export const deleteAddress = (addressId) => {
  return request.delete(`/address/${addressId}`)
}

export const setDefaultAddress = (addressId) => {
  return request.put(`/address/default/${addressId}`)
}

export const getDefaultAddress = () => {
  return request.get('/address/default')
}

export const getUserAddresses = () => {
  return request.get('/address/list')
}