<script setup>
import { onMounted } from 'vue'
import { RouterView, useRouter } from 'vue-router'
import { useUserInfoStore } from '@/stores/modules/userInfo.js'
import axiosAPI from '@/utils/api/axios.js'
import './assets/css/global.css'

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
    <!-- 顶部导航栏 -->
    <header class="header">
      <nav class="nav">
        <a href="/home" class="logo">我的博客</a>
        <div class="nav-menu">
          <a href="/home">首页</a>
          <a href="/articles">博客</a>
          <a href="/notes">笔记</a>
          <a href="/profile">关于我</a>
          <a href="/login" v-if="!userStore.getToken()">登录</a>
          <a href="/register" v-if="!userStore.getToken()">注册</a>
          <a href="/login" v-else @click="logout">退出</a>
        </div>
      </nav>
    </header>

    <!-- 主要内容 -->
    <main class="main-content">
      <RouterView />
    </main>
  </div>
</template>

<style>
* {
  margin: 0;
  padding: 0;
  box-sizing: border-box;
}

body {
  font-family: 'Segoe UI', 'Microsoft YaHei', Tahoma, Geneva, Verdana, sans-serif;
  background: var(--bg-gradient);
  color: var(--text-primary);
  min-height: 100vh;
  position: relative;
  overflow-x: hidden;
  transition: background-color 0.3s ease, color 0.3s ease;
}

.app {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
}

.main-content {
  flex: 1;
  display: flex;
  flex-direction: column;
}

/* 星空背景效果 */
body::before {
  content: '';
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background-image:
    radial-gradient(2px 2px at 20px 30px, rgba(255, 255, 255, 0.2), transparent),
    radial-gradient(2px 2px at 40px 70px, rgba(255, 255, 255, 0.15), transparent),
    radial-gradient(2px 2px at 50px 160px, rgba(255, 255, 255, 0.2), transparent),
    radial-gradient(2px 2px at 90px 40px, rgba(255, 255, 255, 0.15), transparent),
    radial-gradient(2px 2px at 130px 80px, rgba(255, 255, 255, 0.1), transparent),
    radial-gradient(2px 2px at 160px 120px, rgba(255, 255, 255, 0.15), transparent);
  background-repeat: repeat;
  background-size: 200px 200px;
  z-index: -1;
  animation: twinkle 10s infinite linear;
}

@keyframes twinkle {
  0% {
    opacity: 0.3;
  }
  50% {
    opacity: 0.8;
  }
  100% {
    opacity: 0.3;
  }
}
</style>
