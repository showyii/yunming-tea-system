// 用户认证相关 API 接口
// 导入封装好的 Axios 请求实例（已配置拦截器、基础路径等）
import request from './request'

/**
 * 用户认证 API
 * 包含登录、注册、个人资料管理等功能
 */
export const authApi = {
  // 用户注册：提交用户名、密码、手机号等信息
  register(data) {
    return request.post('/auth/register', data)
  },
  // 用户登录：提交用户名和密码，返回 JWT Token
  login(data) {
    return request.post('/auth/login', data)
  },
  // 获取当前登录用户的个人资料
  getProfile() {
    return request.get('/user/profile')
  },
  // 更新个人资料（昵称、头像、性别、生日等）
  updateProfile(data) {
    return request.put('/user/profile', data)
  },
  // 修改密码（需提供原密码和新密码）
  changePassword(data) {
    return request.put('/user/password', data)
  }
}
