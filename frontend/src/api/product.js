// 商品、分类、收藏、评论相关 API 接口
// 导入封装好的 Axios 请求实例
import request from './request'

// ========== 商品 API ==========
export const productApi = {
  // 商品搜索/列表：支持关键词、分类、排序、分页等参数
  search(params) {
    return request.get('/products', { params })
  },
  // 获取商品详情（含图片列表、收藏状态等）
  getDetail(id) {
    return request.get(`/products/${id}`)
  },
  // 获取热销商品列表（首页展示用）
  getHot() {
    return request.get('/products/hot/list')
  },
  // 获取新品列表
  getNew() {
    return request.get('/products/new/list')
  },
  // 获取推荐商品列表
  getRecommend() {
    return request.get('/products/recommend/list')
  }
}

// ========== 分类 API ==========
export const categoryApi = {
  // 获取所有分类的平铺列表
  getAll() {
    return request.get('/categories')
  },
  // 获取分类树（含父子层级关系，用于导航菜单）
  getTree() {
    return request.get('/categories/tree')
  }
}

// ========== 收藏 API ==========
export const favoriteApi = {
  // 添加收藏
  add(productId) {
    return request.post(`/favorites/${productId}`)
  },
  // 取消收藏
  remove(productId) {
    return request.delete(`/favorites/${productId}`)
  },
  // 获取当前用户的收藏列表
  getList() {
    return request.get('/favorites')
  },
  // 检查某商品是否已被当前用户收藏
  check(productId) {
    return request.get(`/favorites/check/${productId}`)
  }
}

// ========== 评论 API ==========
export const commentApi = {
  // 发表评论（评分、内容、关联订单）
  add(data) {
    return request.post('/comments', data)
  },
  // 获取某商品的评论列表（支持分页）
  getList(productId, params) {
    return request.get(`/comments/${productId}`, { params })
  },
  // 删除自己的评论
  delete(id) {
    return request.delete(`/comments/${id}`)
  }
}
