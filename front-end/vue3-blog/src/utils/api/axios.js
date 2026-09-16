import router from '@/router'
import axios from 'axios'
import { ElMessage } from 'element-plus'

// 创建axios实例
const axiosAPI = axios.create({
  baseURL: '/api', // 后端服务地址
  // 'http://localhost:8008/api'  开发环境测试
  timeout: 10000, // 请求超时时间：10s
  withCredentials: true // 允许携带cookie
})

// 请求拦截器:获取请求头中的token并添加到请求头中
axiosAPI.interceptors.request.use(
  config => {
    // 添加token到请求头
    const token = localStorage.getItem('token')

    if (token) {
      config.headers['Authorization'] = `Bearer ${token}`
    }
    return config
  },
  error => {
    // 处理请求错误
    console.error('请求错误:', error)
    return Promise.reject(error)
  }
)

// 响应拦截器:处理响应数据和错误
axiosAPI.interceptors.response.use(
  response => {
    // 处理响应数据
    return response
  },
  error => {
    // 处理响应错误
    if (!error.response) {
      ElMessage.error('网络异常，请检查网络连接')
    } else if (error.response.status === 401) {
      // 已有逻辑 
       // 跳转到登录页面
      localStorage.removeItem('token') // 清除本地存储的token
      localStorage.removeItem('user') // 清除本地存储的用户信息
      router.push('/login')
     } 
     else if (error. response . status >= 500 ) { ElMessage . error ( '服务器异常，请稍后重试' )
    } else {
      ElMessage.error(error.response.data?.message || '请求失败')
    }

    return Promise.reject(error)
  }
)

export default axiosAPI
