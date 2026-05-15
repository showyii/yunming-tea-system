import request from './request'

export const cartApi = {
  add(data) {
    return request.post('/cart', data)
  },
  update(data) {
    return request.put('/cart', data)
  },
  delete(id) {
    return request.delete(`/cart/${id}`)
  },
  getList() {
    return request.get('/cart')
  }
}
