// Node.js 内置测试运行器
import test from 'node:test'
// Node.js 内置断言库（严格模式）
import assert from 'node:assert/strict'

// 导入被测试的函数
import { resolveRequestErrorMessage } from './errorMessage.js'

// 测试用例1：验证优先使用后端返回的业务错误消息
test('prefers backend message from failed login response', () => {
  // 模拟一次登录失败的 Axios 错误对象
  const error = {
    response: {
      status: 400, // HTTP 状态码 400
      data: {
        code: 400, // 业务错误码
        message: '用户名或密码错误' // 后端返回的中文错误提示
      }
    },
    message: 'Request failed with status code 400' // Axios 通用错误消息
  }

  // 断言：应返回后端友好的错误消息，而非 Axios 的通用消息
  assert.equal(resolveRequestErrorMessage(error), '用户名或密码错误')
})

// 测试用例2：验证没有后端消息时，回退到 Axios 错误消息
test('falls back to generic error message when response message is absent', () => {
  // 模拟网络错误（没有 response 数据）
  const error = {
    message: 'Network Error' // 浏览器/Axios 的网络错误消息
  }

  // 断言：应返回 Axios 的错误消息
  assert.equal(resolveRequestErrorMessage(error), 'Network Error')
})
