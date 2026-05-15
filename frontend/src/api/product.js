import request from './request'

export const productApi = {
  search(params) {
    return request.get('/products', { params })
  },
  getDetail(id) {
    return request.get(`/products/${id}`)
  },
  getHot() {
    return request.get('/products/hot/list')
  },
  getNew() {
    return request.get('/products/new/list')
  },
  getRecommend() {
    return request.get('/products/recommend/list')
  }
}

export const categoryApi = {
  getAll() {
    return request.get('/categories')
  },
  getTree() {
    return request.get('/categories/tree')
  }
}

export const favoriteApi = {
  add(productId) {
    return request.post(`/favorites/${productId}`)
  },
  remove(productId) {
    return request.delete(`/favorites/${productId}`)
  },
  getList() {
    return request.get('/favorites')
  },
  check(productId) {
    return request.get(`/favorites/check/${productId}`)
  }
}

export const commentApi = {
  add(data) {
    return request.post('/comments', data)
  },
  getList(productId, params) {
    return request.get(`/comments/${productId}`, { params })
  },
  delete(id) {
    return request.delete(`/comments/${id}`)
  }
}
