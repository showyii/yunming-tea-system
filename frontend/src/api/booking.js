import request from './request'

export const roomApi = {
  getList() {
    return request.get('/rooms')
  },
  getDetail(id) {
    return request.get(`/rooms/${id}`)
  }
}

export const roomBookingApi = {
  book(data) {
    return request.post('/room-bookings', data)
  },
  getList() {
    return request.get('/room-bookings')
  },
  cancel(id) {
    return request.put(`/room-bookings/${id}/cancel`)
  }
}

export const activityApi = {
  getList() {
    return request.get('/activities')
  },
  getDetail(id) {
    return request.get(`/activities/${id}`)
  }
}

export const activitySignupApi = {
  signup(activityId) {
    return request.post('/activity-signups', { activityId })
  },
  getList() {
    return request.get('/activity-signups')
  },
  cancel(id) {
    return request.put(`/activity-signups/${id}/cancel`)
  }
}
