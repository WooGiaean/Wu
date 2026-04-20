import { ref } from 'vue'
import {defineStore, mapActions} from 'pinia'


export const useUserInfoStore = defineStore('userInfoStore', ()=>{
  //定义状态：用户信息
  const userInfo = ref({})
  //定义状态：token
  const token = ref('')
  //定义用户信息过期时间
  const expireTime = ref(null)

  //初始化用户信息和token
  const setUserAndToken = (userInfoData,tokenData) => {
    userInfo.value = userInfoData
    token.value = tokenData
    //设置过期时间:24小时
    expireTime.value=Date.now()+4*60*60*1000

    localStorage.setItem('user', JSON.stringify(userInfo.value))
    localStorage.setItem('token', token.value)

  }
  //获取用户信息
  const getUserInfo = () => {
    return userInfo.value
  }
  //获取token
  const getToken = () => {
    return token.value
  }

  //初始化用户信息和token从localStorage获取
  const initUserAndToken = () => {
    const savedUser = localStorage.getItem('user')
    const savedToken = localStorage.getItem('token')

    if (savedUser && savedToken) {
      try {
        userInfo.value = JSON.parse(savedUser)
        token.value = savedToken
      } catch (error) {
        console.error('解析用户信息失败:', error)
        clearUserInfo()
      }
    }
  }

  //退出登录
  const clearUserInfo = () => {
    userInfo.value = null
    token.value = null
    expireTime.value = null
    localStorage.removeItem('user')
    localStorage.removeItem('token')
    console.log('用户已退出登录')
  }

  return {userInfo, token, setUserAndToken,getUserInfo,getToken,clearUserInfo,initUserAndToken}
})
