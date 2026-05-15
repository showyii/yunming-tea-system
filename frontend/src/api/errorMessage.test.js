import test from 'node:test'
import assert from 'node:assert/strict'

import { resolveRequestErrorMessage } from './errorMessage.js'

test('prefers backend message from failed login response', () => {
  const error = {
    response: {
      status: 400,
      data: {
        code: 400,
        message: '用户名或密码错误'
      }
    },
    message: 'Request failed with status code 400'
  }

  assert.equal(resolveRequestErrorMessage(error), '用户名或密码错误')
})

test('falls back to generic error message when response message is absent', () => {
  const error = {
    message: 'Network Error'
  }

  assert.equal(resolveRequestErrorMessage(error), 'Network Error')
})
