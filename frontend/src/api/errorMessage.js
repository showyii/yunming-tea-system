/**
 * 从 Axios 错误对象中提取可读的错误消息
 *
 * 提取优先级：
 * 1. 后端返回的业务错误消息 response.data.message（最高优先级，对用户最友好）
 * 2. Axios 抛出的通用错误消息 error.message（如 "Network Error"）
 * 3. 调用方传入的兜底消息 fallbackMessage（默认 "请求失败"）
 *
 * @param {Error|Object} error - Axios 错误对象（包含 response、message 等字段）
 * @param {string} fallbackMessage - 兜底错误消息，默认 "请求失败"
 * @returns {string} 解析后的错误提示文本
 */
export function resolveRequestErrorMessage(error, fallbackMessage = '请求失败') {
  // 尝试从后端响应中获取 message 字段（业务异常的错误提示）
  const responseData = error?.response?.data

  // 如果后端返回了非空字符串消息，优先使用（对用户最友好）
  if (typeof responseData?.message === 'string' && responseData.message.trim()) {
    return responseData.message
  }

  // 其次使用 Axios/浏览器抛出的错误消息（如 "Network Error"、"timeout"）
  if (typeof error?.message === 'string' && error.message.trim()) {
    return error.message
  }

  // 都获取不到时，使用兜底默认消息
  return fallbackMessage
}
