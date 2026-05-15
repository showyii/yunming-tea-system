export function resolveRequestErrorMessage(error, fallbackMessage = '请求失败') {
  const responseData = error?.response?.data

  if (typeof responseData?.message === 'string' && responseData.message.trim()) {
    return responseData.message
  }

  if (typeof error?.message === 'string' && error.message.trim()) {
    return error.message
  }

  return fallbackMessage
}
