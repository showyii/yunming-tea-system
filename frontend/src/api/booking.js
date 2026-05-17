// 预约与活动相关 API 接口
// 包含：包间浏览与预约、活动浏览与报名
import request from './request'

// ========== 茶室包间 API ==========
export const roomApi = {
  // 获取所有可用包间列表
  getList() {
    return request.get('/rooms')
  },
  // 获取单个包间的详细信息
  getDetail(id) {
    return request.get(`/rooms/${id}`)
  }
}

// ========== 包间预约 API ==========
export const roomBookingApi = {
  // 提交包间预约（预约日期、时间段、人数等）
  book(data) {
    return request.post('/room-bookings', data)
  },
  // 获取当前用户的预约记录列表
  getList() {
    return request.get('/room-bookings')
  },
  // 取消某个预约
  cancel(id) {
    return request.put(`/room-bookings/${id}/cancel`)
  }
}

// ========== 茶文化活动 API ==========
export const activityApi = {
  // 获取活动列表
  getList() {
    return request.get('/activities')
  },
  // 获取活动详情（含当前用户报名状态）
  getDetail(id) {
    return request.get(`/activities/${id}`)
  }
}

// ========== 活动报名 API ==========
export const activitySignupApi = {
  // 报名参加某个活动
  signup(activityId) {
    return request.post('/activity-signups', { activityId })
  },
  // 获取当前用户的报名记录
  getList() {
    return request.get('/activity-signups')
  },
  // 取消报名
  cancel(id) {
    return request.put(`/activity-signups/${id}/cancel`)
  }
}
