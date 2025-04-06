import instance, { get, post, put, del } from './request'
import { handleGlobalError, handleBusinessError, BusinessErrorType } from './errorHandler'
import * as api from './api'

export {
  // 请求实例和方法
  instance as axios,
  get,
  post,
  put,
  del,
  
  // 错误处理
  handleGlobalError,
  handleBusinessError,
  BusinessErrorType,
  
  // API接口
  api
}

export default {
  axios: instance,
  get,
  post,
  put,
  del,
  api
} 