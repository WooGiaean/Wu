<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import  router  from '@/router/index.js'
import axiosAPI from '@/utils/api/axios.js'
import { useUserInfoStore } from '@/stores/modules/userInfo.js'
import { ElAvatar } from 'element-plus'

const route = useRoute()
const userStore = useUserInfoStore();
// 计算当前路径（获取当前路径）
const currentPath = computed(() => route.path)

//设置用户头像以及默认头像
const changeUserAvatar=computed(()=>{
  if(userInfo.value && userInfo.value.userAvatar){
      return `/uploaded-images/${userInfo.value.userAvatar}`
  }else{
  return new URL('@/assets/images/blog_avatar-3.png', import.meta.url).href
  }
})

// 用户信息
const userInfo = ref(null)
const userNickName = ref('')

// 分类数据
const categories = ref([])

// 标签数据
const tags = ref([])

// 最近文章数据
const recentArticles = ref([])

// 退出登录
const logout = async () => {
  if(confirm('确定要退出登录吗？')) {
    try {
      const response = await axiosAPI.post('/logout')
      if (response.data.code === 1) {
        userStore.clearUserInfo()
        await router.push('/login')
      } else {
        alert(response.data.message || "退出失败")
      }
    } catch (error) {
      console.error('退出失败:', error)
      alert('网络错误，退出失败')
    }
  }
}

// 跳转到文章详情
const goToArticle = (id) => {
  router.push(`/articles/${id}`)
}

// 跳转到分类文章列表
const goToCategory = (categoryId) => {
  router.push(`/articles?category=${categoryId}`)
}

// 跳转到标签文章列表
const goToTag = (tagId) => {
  router.push(`/articles?tag=${tagId}`)
}

// 格式化日期
const formatDate = (dateString) => {
  if (!dateString) return ''
  const date = new Date(dateString)
  const year = date.getFullYear()
  const month = String(date.getMonth() + 1).padStart(2, '0')
  const day = String(date.getDate()).padStart(2, '0')
  return `${year}-${month}-${day}`
}

// 获取用户信息
const getUserInfo = async () => {

  const savedUser = userStore.getUserInfo()|| localStorage.getItem('user')
  console.log(savedUser)
  if (savedUser) {
    userInfo.value= typeof savedUser === 'string' ? JSON.parse(savedUser) : savedUser;
  }else{
    try {
      const response = await axiosAPI.get('/home/currentUser', { credentials: 'include' })
      if (response.data.data) {
        userInfo.value = response.data.data
        userNickName.value = response.data.data.userNickname || 'Visitor'
      }
    } catch (error) {
      console.error('获取用户信息失败:', error)
    }
  }

}

// 获取分类数据
const getCategories = async () => {
  try {
    const response = await axiosAPI.get('/category/list')
    if (response.data.data) {
      categories.value = response.data.data
    }
  } catch (error) {
    console.error('获取分类数据失败:', error)
  }
}

// 获取标签数据
const getTags = async () => {
  try {
    const response = await axiosAPI.get('/admin/tags')
    if (response.data.data) {
      tags.value = response.data.data
    }
  } catch (error) {
    console.error('获取标签数据失败:', error)
  }
}

// 获取最近文章
const getRecentArticles = async () => {
  try {
    const response = await axiosAPI.get('/home/latestArticles')
    if (response.data.code === 1 && response.data.data) {
      recentArticles.value = response.data.data.records.slice(0, 5) || []
    } else {
      recentArticles.value = []
    }
  } catch (error) {
    console.error('获取最近文章失败:', error)
  }
}

onMounted(() => {
  getUserInfo()
  getCategories()
  getTags()
  getRecentArticles()
})
</script>

<template>
  <aside class="sidebar">
    <!-- 用户信息 -->
    <div class="card user-info">
<!--      <div class="user-avatar">
&lt;!&ndash;        {{ userInfo && userInfo.userNickname ? userInfo.userNickname.charAt(0).toUpperCase() : '访' }}
 <img :src="changeUserAvatar"  alt="用户头像">
&ndash;&gt;

&lt;!&ndash;        <el-avatar :size="50"  :src="changeUserAvatar" shape="circle" />&ndash;&gt;
      </div>-->

      <el-avatar :size="30"  :src="changeUserAvatar" shape="circle"></el-avatar>

      <h3 class="user-name">{{ userInfo && userInfo.userNickname ? userInfo.userNickname : '访客' }}</h3>
      <p class="user-desc">{{ userInfo && userInfo.userDescription ? userInfo.userDescription : '欢迎访问我的博客' }}</p>
    </div>

    <!-- 导航菜单 -->
    <div class="card">
      <h4 class="sidebar-title">导航菜单</h4>
      <ul class="category-list">
        <li :class="{ active: currentPath === '/home' }">
          <router-link to="/home">🏠 主页</router-link>
        </li>
        <li :class="{ active: currentPath === '/profile' }">
          <router-link to="/profile">👤 关于我</router-link>
        </li>
        <li :class="{ active: currentPath === '/articles' }">
          <router-link to="/articles">📚 博客</router-link>
        </li>
        <li :class="{ active: currentPath === '/notes' }">
          <router-link to="/notes">✍️ 随笔</router-link>
        </li>
        <li :class="{ active: currentPath === '/knowledge' }">
          <router-link to="/knowledge">🧠 知识库</router-link>
        </li>
        <li style="color: #d9534f; cursor: pointer;" @click="logout()">🚪 退出登录</li>
      </ul>
    </div>

    <!-- 分类部分 -->
    <div class="card">
      <h4 class="sidebar-title">文章分类</h4>
      <ul class="category-list">
        <li @click="goToCategory(null)">
          全部
        </li>
        <li v-for="category in categories" :key="category.categoryId" @click="goToCategory(category.categoryId)">
          {{ category.categoryName }}
        </li>
      </ul>
    </div>

    <!-- 标签部分 -->
    <div class="card">
      <h4 class="sidebar-title">热门标签</h4>
      <div class="tags">
        <span v-for="tag in tags" :key="tag.tagId" class="tag" @click="goToTag(tag.tagId)">
          {{ tag.tagName }}
        </span>
      </div>
    </div>

    <!-- 最近文章 -->
    <div class="card">
      <h4 class="sidebar-title">最近文章</h4>
      <ul class="category-list">
        <li v-for="article in recentArticles" :key="article.articleId" @click="goToArticle(article.articleId)">
          {{ article.articleTitle }}
        </li>
      </ul>
    </div>
  </aside>
</template>

<style scoped>
/* 导航菜单样式 */
.category-list {
  list-style: none;
}

.category-list li {
  padding: 6px 0;
  font-size: 15px;
  color: var(--text-primary);
  cursor: pointer;
  transition: color 0.3s ease;
}

.category-list li:hover {
  color: var(--accent-color);
}

.category-list li.active {
  color: var(--accent-color);
  font-weight: 600;
}

.category-list li a {
  text-decoration: none;
  color: inherit;
  display: block;
  width: 100%;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .sidebar {
    width: 100%;
  }
}

.el-avatar {
  overflow: hidden !important;
}
</style>
