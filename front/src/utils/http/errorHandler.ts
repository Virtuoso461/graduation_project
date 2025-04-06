import { ElMessage } from 'element-plus'
import { AxiosError } from 'axios'

/**
 * HTTP状态码错误信息
 */
const httpStatusMessages: Record<number, string> = {
  400: '请求参数错误',
  401: '用户未授权（未登录或登录已过期）',
  403: '账号已被禁用或没有权限',
  404: '请求的资源不存在',
  405: '不支持当前请求方法',
  408: '请求超时',
  409: '请求冲突',
  429: '请求频率超限',
  500: '服务器内部错误',
  501: '服务未实现',
  502: '网关错误',
  503: '服务不可用',
  504: '网关超时'
}

/**
 * 业务错误类型
 */
export enum BusinessErrorType {
  VALIDATE_ERROR = 'VALIDATE_ERROR', // 验证错误
  AUTH_ERROR = 'AUTH_ERROR',         // 认证错误
  PERMISSION_ERROR = 'PERMISSION_ERROR', // 权限错误
  OPERATION_ERROR = 'OPERATION_ERROR',  // 操作错误
  RESOURCE_ERROR = 'RESOURCE_ERROR',    // 资源错误
  SYSTEM_ERROR = 'SYSTEM_ERROR'         // 系统错误
}

/**
 * 全局错误处理函数
 * @param error Axios错误对象
 * @returns 处理后的错误信息
 */
export const handleGlobalError = (error: AxiosError) => {
  let message = '未知错误'
  
  if (error.response) {
    // 处理HTTP状态错误
    const statusCode = error.response.status
    message = httpStatusMessages[statusCode] || `请求错误(${statusCode})`
    
    // 尝试从响应中获取更详细的错误信息
    const responseData = error.response.data as any
    
    if (responseData) {
      if (typeof responseData === 'string') {
        message = responseData
      } else if (responseData.message) {
        message = responseData.message
      } else if (responseData.error) {
        message = responseData.error
      } else if (Array.isArray(responseData)) {
        message = responseData.join(', ')
      }
    }
    
    // 显示错误消息
    ElMessage.error(message)
  } else if (error.request) {
    // 请求已发送但没有收到响应
    message = '服务器无响应，请检查网络连接'
    ElMessage.error(message)
  } else {
    // 请求配置发生错误
    message = error.message || '请求发送失败'
    ElMessage.error(message)
  }
  
  // 记录错误到控制台
  console.error('API错误:', error)
  
  // 返回处理后的错误信息
  return Promise.reject({
    message,
    error,
    code: error.response?.status
  })
}

/**
 * 处理业务逻辑相关错误
 */
export const handleBusinessError = (response: any) => {
  if (!response.success) {
    const message = response.message || '操作失败'
    ElMessage.error(message)
    return Promise.reject({ message, response })
  }
  return response
} 