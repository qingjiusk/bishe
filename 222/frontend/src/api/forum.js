import request from './request'

export const getForumList = (page = 1, size = 10, category = '') => {
  return request.get('/forum/list', { params: { page, size, category } })
}

export const getForumDetail = (id) => {
  return request.get(`/forum/${id}`)
}

export const createForum = (data) => {
  return request.post('/forum/create', data)
}
