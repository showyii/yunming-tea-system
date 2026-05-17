// 管理后台 API 接口模块
// 导入 Axios 库（管理后台请求不经过通用 request 拦截器，需要独立的 Token 管理）
import axios from 'axios'
// 导入 Element Plus 消息提示组件
import { ElMessage } from 'element-plus'
// 导入请求错误信息解析工具函数
import { resolveRequestErrorMessage } from './errorMessage'

// 创建管理后台专用的 Axios 实例，基础路径指向 /api/admin
const adminRequest = axios.create({
  baseURL: '/api/admin', // 管理后台 API 的基础URL前缀
  timeout: 15000 // 请求超时时间 15 秒
})

// ---------- 请求拦截器：自动在请求头中添加管理员 Token ----------
adminRequest.interceptors.request.use(config => {
  // 从浏览器 localStorage 中读取管理员 Token
  const token = localStorage.getItem('admin_token')
  if (token) {
    // 将 Token 添加到请求头 Authorization 字段，格式为 "Bearer <token>"
    config.headers.Authorization = `Bearer ${token}`
  }
  return config // 必须返回 config，否则请求会被拦截
})

// ---------- 响应拦截器：统一处理响应和错误 ----------
adminRequest.interceptors.response.use(
  // 成功响应处理
  response => {
    const data = response.data // 获取后端返回的数据 { code, message, data }
    // 如果后端返回的状态码不是 200（业务失败），显示错误提示
    if (data.code !== 200) {
      ElMessage.error(data.message || '请求失败')
      return Promise.reject(new Error(data.message)) // 将错误向下传递
    }
    return data.data // 只返回 data.data 部分，简化调用方的代码
  },
  // 错误响应处理（网络错误、超时、HTTP 错误等）
  error => {
    // 如果 HTTP 状态码为 401（未授权），说明 Token 过期或无效
    if (error.response?.status === 401) {
      localStorage.removeItem('admin_token') // 清除过期的 Token
      ElMessage.error('登录已过期，请重新登录') // 提示用户重新登录
    } else {
      // 其他错误：使用工具函数解析错误信息并显示
      ElMessage.error(resolveRequestErrorMessage(error))
    }
    return Promise.reject(error) // 继续抛出错误，由调用方自行处理
  }
)

// ========== 管理员认证 API ==========
export const adminAuthApi = {
  // 管理员登录
  login(data) {
    return adminRequest.post('/auth/login', data)
  }
}

// ========== 数据统计 API ==========
export const statisticsApi = {
  // 获取近 N 天的每日订单统计（默认 7 天）
  dailyOrders(days = 7) { return adminRequest.get('/statistics/daily-orders', { params: { days } }) },
  // 获取商品销量排行榜
  salesRanking() { return adminRequest.get('/statistics/sales-ranking') },
  // 获取各分类销售额占比
  categorySales() { return adminRequest.get('/statistics/category-sales') },
  // 获取包间预约统计
  roomBookings() { return adminRequest.get('/statistics/room-bookings') },
  // 获取各状态订单数量统计
  orderStatus() { return adminRequest.get('/statistics/order-status') }
}

// ========== 用户管理 API ==========
export const adminUserApi = {
  // 获取用户列表（支持分页和筛选参数）
  list(params) { return adminRequest.get('/users', { params }) },
  // 修改用户状态（启用/禁用）
  updateStatus(id, status) { return adminRequest.put(`/users/${id}/status`, { status }) }
}

// ========== 分类管理 API ==========
export const adminCategoryApi = {
  list() { return adminRequest.get('/categories') }, // 获取分类列表
  getById(id) { return adminRequest.get(`/categories/${id}`) }, // 获取单个分类
  create(data) { return adminRequest.post('/categories', data) }, // 创建新分类
  update(id, data) { return adminRequest.put(`/categories/${id}`, data) }, // 更新分类
  delete(id) { return adminRequest.delete(`/categories/${id}`) } // 删除分类
}

// ========== 商品管理 API ==========
export const adminProductApi = {
  list(params) { return adminRequest.get('/products', { params }) }, // 商品列表
  getById(id) { return adminRequest.get(`/products/${id}`) }, // 商品详情
  create(data) { return adminRequest.post('/products', data) }, // 新增商品
  update(id, data) { return adminRequest.put(`/products/${id}`, data) }, // 更新商品
  delete(id) { return adminRequest.delete(`/products/${id}`) }, // 删除商品
  getImages(productId) { return adminRequest.get(`/products/${productId}/images`) }, // 获取商品图片
  addImage(productId, data) { return adminRequest.post(`/products/${productId}/images`, data) }, // 添加商品图片
  deleteImage(imageId) { return adminRequest.delete(`/products/images/${imageId}`) } // 删除商品图片
}

// ========== 订单管理 API ==========
export const adminOrderApi = {
  list(params) { return adminRequest.get('/orders', { params }) }, // 订单列表
  getById(id) { return adminRequest.get(`/orders/${id}`) }, // 订单详情
  updateStatus(id, status) { return adminRequest.put(`/orders/${id}/status`, { status }) } // 修改订单状态
}

// ========== 评论管理 API ==========
export const adminCommentApi = {
  list(params) { return adminRequest.get('/comments', { params }) }, // 评论列表
  delete(id) { return adminRequest.delete(`/comments/${id}`) } // 删除评论
}

// ========== 包间管理 API ==========
export const adminRoomApi = {
  list() { return adminRequest.get('/rooms') }, // 包间列表
  getById(id) { return adminRequest.get(`/rooms/${id}`) }, // 包间详情
  create(data) { return adminRequest.post('/rooms', data) }, // 新增包间
  update(id, data) { return adminRequest.put(`/rooms/${id}`, data) }, // 更新包间
  delete(id) { return adminRequest.delete(`/rooms/${id}`) } // 删除包间
}

// ========== 包间预约管理 API ==========
export const adminRoomBookingApi = {
  list(params) { return adminRequest.get('/room-bookings', { params }) }, // 预约列表
  updateStatus(id, status) { return adminRequest.put(`/room-bookings/${id}/status`, { status }) } // 修改预约状态
}

// ========== 活动管理 API ==========
export const adminActivityApi = {
  list() { return adminRequest.get('/activities') }, // 活动列表
  getById(id) { return adminRequest.get(`/activities/${id}`) }, // 活动详情
  create(data) { return adminRequest.post('/activities', data) }, // 创建活动
  update(id, data) { return adminRequest.put(`/activities/${id}`, data) }, // 更新活动
  delete(id) { return adminRequest.delete(`/activities/${id}`) } // 删除活动
}

// ========== 活动报名管理 API ==========
export const adminActivitySignupApi = {
  list(params) { return adminRequest.get('/activity-signups', { params }) } // 报名记录列表
}
