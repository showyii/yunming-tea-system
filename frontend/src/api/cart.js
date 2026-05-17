// 购物车相关 API 接口
// 导入封装好的 Axios 请求实例
import request from './request'

/**
 * 购物车 API
 * 管理用户购物车中的商品（增删改查）
 */
export const cartApi = {
  // 添加商品到购物车：传入 productId 和 quantity
  add(data) {
    return request.post('/cart', data)
  },
  // 修改购物车中某商品的数量
  update(data) {
    return request.put('/cart', data)
  },
  // 从购物车中删除某件商品
  delete(id) {
    return request.delete(`/cart/${id}`)
  },
  // 获取当前用户购物车中的所有商品
  getList() {
    return request.get('/cart')
  }
}
