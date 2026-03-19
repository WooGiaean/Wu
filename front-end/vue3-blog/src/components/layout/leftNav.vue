<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import axios from 'axios'

const router = useRouter()
const route = useRoute()

// 计算当前路径（获取当前路径）
const currentPath = computed(() => route.path)

// 用户信息
const userInfo = ref(null)
const userNickName = ref('')

// 退出登录
const logout = async () => {
  if(confirm('确定要退出登录吗？')) {
    try {
      const response = await axios.post('/admin/logout')
      if (response.data.code === 1) {
        sessionStorage.removeItem("user")
        router.push('/login')
      } else {
        alert(response.data.message || "退出失败")
      }
    } catch (error) {
      console.error('退出失败:', error)
      alert('网络错误，退出失败')
    }
  }
}

// 获取用户信息
const getUserInfo = async () => {
  try {
    const response = await axios.get('/home/currentUser', { credentials: 'include' })
    if (response.data.data) {
      userInfo.value = response.data.data
      userNickName.value = response.data.data.userNickname || 'Visitor'
    }
  } catch (error) {
    console.error('获取用户信息失败:', error)
  }
}

onMounted(() => {
  getUserInfo()
})
</script>

<template>
  <aside class="left-column">
    <div class="blog-title">
      <h1>个人博客</h1>
      <!-- 动态显示用户头像 -->
      <img :src="userInfo && userInfo.userAvatar ? `/touxiang/${userInfo.userAvatar}` : '/touxiang/blog_avatar-3.png'" alt="用户头像" class="avatar"
           onerror="this.src='https://picsum.photos/seed/avatar/100/100'">
      <!-- 显示用户昵称 -->
      <p class="user-nickname">{{ userNickName }}</p>
    </div>

    <!-- 菜单栏 -->
    <nav class="nav-menu">
      <div class="nav-item" :class="{ active: currentPath === '/home' }">
        <router-link to="/home">🏠 主页</router-link>
      </div>
      <div class="nav-item" :class="{ active: currentPath === '/profile' }">
        <router-link to="/profile">👤 关于我</router-link>
      </div>
      <div class="nav-item" :class="{ active: currentPath === '/articles' }">
        <router-link to="/articles">📚 博客</router-link>
      </div>
      <div class="nav-item" :class="{ active: currentPath === '/notes' }">
        <router-link to="/notes">✍️ 随笔</router-link>
      </div>
      <div class="nav-item">
        <router-link to="/knowledge">🧠 知识库</router-link>
      </div>
      <div class="nav-item" style="color: #d9534f;" @click="logout()">🚪 退出登录</div>
    </nav>
  </aside>
</template>

<style scoped>
/* 导航栏样式 */
.left-column {
  width: var(--sidebar-width);
  background-color: var(--bg-secondary);
  border-radius: 8px;
  box-shadow: var(--card-shadow);
  height: fit-content;
  position: sticky;
  top: 20px;
  flex-shrink: 0;
}

.blog-title {
  background-color: var(--bg-secondary);
  border-bottom: 1px solid var(--border-color);
  text-align: center;
  padding: 40px 20px;
}

.blog-title h1 {
  font-size: 24px;
  color: var(--text-primary);
  margin-bottom: 15px;
}

.avatar {
  width: 80px;
  height: 80px;
  border-radius: 50%;
  object-fit: cover;
  border: 3px solid var(--accent-color);
  margin-bottom: 10px;
  background-color: #f0f0f0;
}

.user-nickname {
  font-weight: bold;
  color: var(--text-primary);
  font-size: 16px;
}

.nav-menu {
  padding: 20px 0;
}

.nav-item {
  padding: 12px 30px;
  font-size: 16px;
  color: var(--text-secondary);
  cursor: pointer;
  transition: all 0.3s ease;
  display: block;
}

.nav-item a {
  text-decoration: none;
  color: inherit;
  display: block;
  width: 100%;
}

.nav-item:hover, .nav-item a:hover, .nav-item.active {
  color: var(--text-primary);
  background-color: var(--bg-primary);
  border-right: 4px solid var(--accent-color);
}
</style>
