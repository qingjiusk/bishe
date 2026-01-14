import request from './request'

export const login = (username, password) => {
  return request.post('/user/login', { username, password })
}

export const register = (userData) => {
  return request.post('/user/register', userData)
}

export const getUserInfo = () => {
  return request.get('/user/info')
}

export const updateUserInfo = (userData) => {
  return request.put('/user/update', userData)
}

export const updatePassword = (oldPassword, newPassword) => {
  return request.put('/user/password', { oldPassword, newPassword })
}