import request from './request'

export const orderApi = {
  submit(data) {
    return request.post('/orders', data)
  },
  getList(params) {
    return request.get('/orders', { params })
  },
  getDetail(id) {
    return request.get(`/orders/${id}`)
  },
  cancel(id) {
    return request.put(`/orders/${id}/cancel`)
  },
  pay(id) {
    return request.put(`/orders/${id}/pay`)
  }
}
