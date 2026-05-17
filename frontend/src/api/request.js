// Axios 请求实例封装 - 统一管理前端 HTTP 请求
// 导入 Axios HTTP 客户端库
import axios from 'axios'
// 导入 Element Plus 消息提示组件，用于显示错误信息
import { ElMessage } from 'element-plus'
// 导入错误消息解析工具函数
import { resolveRequestErrorMessage } from './errorMessage'

// 创建 Axios 实例，配置基础参数
const request = axios.create({
  baseURL: '/api', // 所有请求的基础URL前缀，实际请求时会自动拼接
  timeout: 15000 // 请求超时时间，15 秒后未收到响应则自动中断
})

// ---------- 请求拦截器：在请求发出前自动处理 ----------
request.interceptors.request.use(config => {
  // 从浏览器 localStorage 中读取 JWT Token
  const token = localStorage.getItem('token')
  // 如果 Token 存在，将其添加到请求头的 Authorization 字段
  if (token) {
    // 使用 Bearer 认证方案，格式为 "Bearer <token>"
    config.headers.Authorization = `Bearer ${token}`
  }
  return config // 必须返回 config 对象，否则请求不会发出
})

// ---------- 响应拦截器：统一处理后端响应和错误 ----------
request.interceptors.response.use(
  // 成功响应（HTTP 2xx）的处理
  response => {
    const data = response.data // 获取后端返回的 JSON 数据 { code, message, data }
    // 检查业务状态码：code 不等于 200 表示业务失败
    if (data.code !== 200) {
      // 弹出错误提示消息
      ElMessage.error(data.message || '请求失败')
      // 将错误向下传递，调用方可以通过 catch 捕获
      return Promise.reject(new Error(data.message))
    }
    // 业务成功：只返回 data.data，简化调用方代码（不用每次都取 .data.data）
    return data.data
  },
  // 失败响应（网络错误、超时、HTTP 错误等）的处理
  error => {
    // HTTP 401 表示 Token 无效或已过期
    if (error.response?.status === 401) {
      localStorage.removeItem('token') // 清除过期的 Token
      ElMessage.error('登录已过期，请重新登录') // 提示用户
    } else {
      // 其他错误：使用工具函数解析错误信息并显示
      ElMessage.error(resolveRequestErrorMessage(error))
    }
    // 继续抛出错误，由调用方自行处理
    return Promise.reject(error)
  }
)

// 导出封装好的 Axios 实例，供各 API 模块使用
export default request
