import axios from 'axios'
import router from '@/router'


// 创建axios实例
const axiosAPI = axios.create({
  baseURL: 'http://localhost:8008', // 后端服务地址
  timeout: 10000, // 请求超时时间
  withCredentials: true // 允许携带cookie
})

// 请求拦截器
axiosAPI.interceptors.request.use(
  config => {
    // 可以在这里添加token等认证信息
    return config
  },
  error => {
    // 处理请求错误
    console.error('请求错误:', error)
    return Promise.reject(error)
  }
)

// 响应拦截器
axiosAPI.interceptors.response.use(
  response => {
    // 处理响应数据
    return response
  },
  error => {
    // 处理响应错误
    console.error('响应错误:', error)

    // 处理401未授权错误
    if (error.response && error.response.status === 401) {
      // 跳转到登录页面
      //window.location.href = '/login'

      router.push('/login')
    }

    return Promise.reject(error)
  }
)

export default axiosAPI
