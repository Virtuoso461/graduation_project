import axios, { AxiosRequestConfig, AxiosResponse } from 'axios'
import { ElMessage } from 'element-plus'

// 创建axios实例
const axiosInstance = axios.create({
  baseURL: 'http://localhost:8080', // 使用固定的baseURL而非环境变量
  timeout: 10000, // 请求超时时间
  withCredentials: true
})

// 请求拦截器
axiosInstance.interceptors.request.use(
  (config) => {
    // 在发送请求之前做一些处理，例如添加token
    const token = localStorage.getItem('token')
    if (token) {
      config.headers['Authorization'] = `Bearer ${token}`
    }
    return config
  },
  (error) => {
    // 处理请求错误
    console.error('Request Error:', error)
    return Promise.reject(error)
  }
)

// 响应拦截器
axiosInstance.interceptors.response.use(
  (response: AxiosResponse) => {
    const res = response.data

    // 判断请求是否成功
    if (response.status === 200) {
      return res
    }

    // 特定错误码处理
    if (res.code === 401) {
      // 未授权，可以在这里处理登出逻辑
      // store.dispatch('user/logout')
      ElMessage.error('登录已过期，请重新登录')
    } else if (res.code === 403) {
      ElMessage.error('没有权限访问')
    } else {
      ElMessage.error(res.message || '请求失败')
    }

    return Promise.reject(new Error(res.message || '请求失败'))
  },
  (error) => {
    console.error('Response Error:', error)
    
    // 处理网络错误或超时
    let message = '未知错误'
    if (error.message.includes('timeout')) {
      message = '请求超时'
    } else if (error.message.includes('Network Error')) {
      message = '网络异常，请检查网络连接'
    } else if (error.response) {
      // 有响应但状态码不是2xx
      switch (error.response.status) {
        case 400:
          message = '请求错误'
          break
        case 401:
          message = '未授权，请登录'
          break
        case 403:
          message = '拒绝访问'
          break
        case 404:
          message = '请求资源不存在'
          break
        case 500:
          message = '服务器内部错误'
          break
        default:
          message = `请求失败 (${error.response.status})`
      }
    }
    
    // 只在生产环境显示错误信息，避免开发环境大量错误提示
    if (import.meta.env.PROD) {
      ElMessage.error(message)
    }
    
    return Promise.reject(error)
  }
)

// 封装GET请求
export function get(url: string, params?: any) {
  return axiosInstance.get(url, { params })
    .catch(error => {
      console.error(`GET ${url} failed:`, error)
      return {}  // 返回空对象而不是抛出错误，让调用者决定如何处理
    })
}

// 封装POST请求
export function post(url: string, data?: any, config?: AxiosRequestConfig) {
  return axiosInstance.post(url, data, config)
    .catch(error => {
      console.error(`POST ${url} failed:`, error)
      return {}  // 返回空对象而不是抛出错误，让调用者决定如何处理
    })
}

// 封装PUT请求
export function put(url: string, data?: any, config?: AxiosRequestConfig) {
  return axiosInstance.put(url, data, config)
    .catch(error => {
      console.error(`PUT ${url} failed:`, error)
      return {}  // 返回空对象而不是抛出错误，让调用者决定如何处理
    })
}

// 封装DELETE请求
export function del(url: string, config?: AxiosRequestConfig) {
  return axiosInstance.delete(url, config)
    .catch(error => {
      console.error(`DELETE ${url} failed:`, error)
      return {}  // 返回空对象而不是抛出错误，让调用者决定如何处理
    })
}

// 导出axios实例供直接使用
export default axiosInstance 