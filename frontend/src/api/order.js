// 订单相关 API 接口
// 导入封装好的 Axios 请求实例
import request from './request'

/**
 * 订单 API
 * 管理用户订单（下单、查询、取消、支付）
 */
export const orderApi = {
  // 提交下单请求：传入收货信息和购物车选中商品
  submit(data) {
    return request.post('/orders', data)
  },
  // 获取当前用户的订单列表（支持分页和状态筛选）
  getList(params) {
    return request.get('/orders', { params })
  },
  // 获取订单详情（含所有订单项和收货信息）
  getDetail(id) {
    return request.get(`/orders/${id}`)
  },
  // 取消订单（仅待付款状态的订单可取消）
  cancel(id) {
    return request.put(`/orders/${id}/cancel`)
  },
  // 模拟支付（实际项目中应对接微信/支付宝支付）
  pay(id) {
    return request.put(`/orders/${id}/pay`)
  }
}
