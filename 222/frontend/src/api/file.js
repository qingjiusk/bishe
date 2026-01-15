import request from './request'

// 上传单个文件
export const uploadFile = (file) => {
  const formData = new FormData()
  formData.append('file', file)
  return request.post('/file/upload/single', formData, {
    headers: {
      'Content-Type': 'multipart/form-data'
    }
  })
}

// 上传多个文件
export const uploadFiles = (files) => {
  const formData = new FormData()
  files.forEach(file => {
    formData.append('files', file)
  })
  return request.post('/file/upload', formData, {
    headers: {
      'Content-Type': 'multipart/form-data'
    }
  })
}