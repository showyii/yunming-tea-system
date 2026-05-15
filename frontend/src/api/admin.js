import axios from 'axios'
import { ElMessage } from 'element-plus'
import { resolveRequestErrorMessage } from './errorMessage'

const adminRequest = axios.create({
  baseURL: '/api/admin',
  timeout: 15000
})

adminRequest.interceptors.request.use(config => {
  const token = localStorage.getItem('admin_token')
  if (token) {
    config.headers.Authorization = `Bearer ${token}`
  }
  return config
})

adminRequest.interceptors.response.use(
  response => {
    const data = response.data
    if (data.code !== 200) {
      ElMessage.error(data.message || '请求失败')
      return Promise.reject(new Error(data.message))
    }
    return data.data
  },
  error => {
    if (error.response?.status === 401) {
      localStorage.removeItem('admin_token')
      ElMessage.error('登录已过期，请重新登录')
    } else {
      ElMessage.error(resolveRequestErrorMessage(error))
    }
    return Promise.reject(error)
  }
)

export const adminAuthApi = {
  login(data) {
    return adminRequest.post('/auth/login', data)
  }
}

export const statisticsApi = {
  dailyOrders(days = 7) { return adminRequest.get('/statistics/daily-orders', { params: { days } }) },
  salesRanking() { return adminRequest.get('/statistics/sales-ranking') },
  categorySales() { return adminRequest.get('/statistics/category-sales') },
  roomBookings() { return adminRequest.get('/statistics/room-bookings') },
  orderStatus() { return adminRequest.get('/statistics/order-status') }
}

export const adminUserApi = {
  list(params) { return adminRequest.get('/users', { params }) },
  updateStatus(id, status) { return adminRequest.put(`/users/${id}/status`, { status }) }
}

export const adminCategoryApi = {
  list() { return adminRequest.get('/categories') },
  getById(id) { return adminRequest.get(`/categories/${id}`) },
  create(data) { return adminRequest.post('/categories', data) },
  update(id, data) { return adminRequest.put(`/categories/${id}`, data) },
  delete(id) { return adminRequest.delete(`/categories/${id}`) }
}

export const adminProductApi = {
  list(params) { return adminRequest.get('/products', { params }) },
  getById(id) { return adminRequest.get(`/products/${id}`) },
  create(data) { return adminRequest.post('/products', data) },
  update(id, data) { return adminRequest.put(`/products/${id}`, data) },
  delete(id) { return adminRequest.delete(`/products/${id}`) },
  getImages(productId) { return adminRequest.get(`/products/${productId}/images`) },
  addImage(productId, data) { return adminRequest.post(`/products/${productId}/images`, data) },
  deleteImage(imageId) { return adminRequest.delete(`/products/images/${imageId}`) }
}

export const adminOrderApi = {
  list(params) { return adminRequest.get('/orders', { params }) },
  getById(id) { return adminRequest.get(`/orders/${id}`) },
  updateStatus(id, status) { return adminRequest.put(`/orders/${id}/status`, { status }) }
}

export const adminCommentApi = {
  list(params) { return adminRequest.get('/comments', { params }) },
  delete(id) { return adminRequest.delete(`/comments/${id}`) }
}

export const adminRoomApi = {
  list() { return adminRequest.get('/rooms') },
  getById(id) { return adminRequest.get(`/rooms/${id}`) },
  create(data) { return adminRequest.post('/rooms', data) },
  update(id, data) { return adminRequest.put(`/rooms/${id}`, data) },
  delete(id) { return adminRequest.delete(`/rooms/${id}`) }
}

export const adminRoomBookingApi = {
  list(params) { return adminRequest.get('/room-bookings', { params }) },
  updateStatus(id, status) { return adminRequest.put(`/room-bookings/${id}/status`, { status }) }
}

export const adminActivityApi = {
  list() { return adminRequest.get('/activities') },
  getById(id) { return adminRequest.get(`/activities/${id}`) },
  create(data) { return adminRequest.post('/activities', data) },
  update(id, data) { return adminRequest.put(`/activities/${id}`, data) },
  delete(id) { return adminRequest.delete(`/activities/${id}`) }
}

export const adminActivitySignupApi = {
  list(params) { return adminRequest.get('/activity-signups', { params }) }
}
