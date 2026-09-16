<script setup>
import { useUserInfoStore } from '@/stores/modules/userInfo.js'
import axiosAPI from '@/utils/api/axios.js'
import { onMounted } from 'vue'
import { RouterView, useRouter } from 'vue-router'
import './assets/css/global.css'
//import '@/assets/main.css'
// 初始化用户信息存储
const userStore = useUserInfoStore()
const router = useRouter()

// 初始化主题
const initTheme = () => {
  const savedTheme = localStorage.getItem('theme')
  if (savedTheme === 'dark') {
    document.documentElement.classList.add('dark-theme')
  }
}

// 退出登录
const logout = async (event) => {
  event.preventDefault()
  try {
    const response = await axiosAPI.post('/auth/logout')
    if (response.data.code === 1) {
      userStore.clearUserInfo()
      router.push('/login')
    }
  } catch (error) {
    console.error('退出失败:', error)
    userStore.clearUserInfo()
    router.push('/login')
  }
}

// 应用启动时初始化用户信息和token从localStorage获取
onMounted(() => {
  initTheme()
  userStore.initUserAndToken()
})
</script>

<template>
  <div class="app">
  
    <!-- 主要内容 -->
    <main class="main-content">
      <RouterView />
    </main>
  </div>
</template>

<style>

</style>
