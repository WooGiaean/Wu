<script setup>
import router from '@/router/index.js'
import { allStores } from "@/stores/index.js"
import axiosAPI from '@/utils/api/axios.js'
import { getDefaultUserAvatar } from '@/utils/common/setPics.js'
import { ArrowDown, ChatDotRound, EditPen, House, Menu, Reading, SwitchButton, User } from '@element-plus/icons-vue'
import { computed, onMounted, ref } from 'vue'
import { useRoute } from 'vue-router'

const route = useRoute()
const userStore = allStores.useUserInfoStore();
const articlesStore = allStores.useArticlesStore();
const notesStore = allStores.useNotesStore();
// 计算当前路径（获取当前路径）
const currentPath = computed(() => route.path)

//设置用户头像以及默认头像
/*const changeUserAvatar=computed(()=>{
  if(userInfo.value && userInfo.value.userAvatar){
      return `/uploaded-images/${userInfo.value.userAvatar}`
  }else{
  return new URL('@/assets/images/blog_avatar-3.png', import.meta.url).href
  }
})*/

// 用户信息
const userInfo = ref('')
const userNickName = ref('')

// 分类数据
const categories = ref([])


// 展开的父分类ID
const expandedCategories = ref([])

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
        articlesStore.clearArticles()
        notesStore.clearNotes()
        await router.push('/')
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
//  router.push(`/articles?category=${categoryId}`)
  router.push('articles')
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

  const savedUser = userStore.getUserInfo()
  console.log(savedUser.userName)
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
      categories.value = response.data.data.records || []
    }
  } catch (error) {
    console.error('获取分类数据失败:', error)
  }
}

// 获取父级分类
const parentCategories = computed(() => {
  return categories.value.filter(cat => cat.categoryParentId === 0)
})

// 获取子级分类
const getChildren = (parentId) => {
  return categories.value.filter(cat => cat.categoryParentId === parentId)
}

// 切换展开状态
const toggleCategory = (categoryId) => {
  const index = expandedCategories.value.indexOf(categoryId)
  if (index === -1) {
    expandedCategories.value.push(categoryId)
  } else {
    expandedCategories.value.splice(index, 1)
  }
}

// 判断是否展开
const isExpanded = (categoryId) => {
  return expandedCategories.value.includes(categoryId)
}


// 获取标签数据
const getTags = async () => {
  try {
    const response = await axiosAPI.get('/tags/list')
    if (response.data.data) {
      tags.value = response.data.data.records || []
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
      <div class="avatar-container">
        <img :src="getDefaultUserAvatar(userInfo)" alt="用户头像" class="user-avatar-img">
      </div>

      <h3 class="user-name">{{ userInfo && userInfo.userNickname ? userInfo.userNickname : '访客' }}</h3>
      <p class="user-desc">{{ userInfo && userInfo.userDescription ? userInfo.userDescription : '欢迎访问我的博客' }}</p>
    </div>

    <!-- 导航菜单 -->
    <div class="card">
      <h4 class="sidebar-title">导航菜单</h4>
      <ul class="category-list">
        <li :class="{ active: currentPath === '/home' }">
          <router-link to="/home"><House class="nav-icon" /> 主页</router-link>
        </li>
        <li :class="{ active: currentPath === '/profile' }">
          <router-link to="/profile"><User class="nav-icon" /> 关于我</router-link>
        </li>
        <li :class="{ active: currentPath === '/articles' }">
          <router-link to="/articles"><Reading class="nav-icon" /> 博客</router-link>
        </li>
        <li :class="{ active: currentPath === '/notes' }">
          <router-link to="/notes"><EditPen class="nav-icon" /> 随笔</router-link>
        </li>
        <li :class="{ active: currentPath === '/comments' }">
          <router-link to="/comments"><ChatDotRound class="nav-icon" /> 评论</router-link>
        </li>
        <li class="logout-item" @click="logout()"><SwitchButton class="nav-icon" /> 退出登录</li>
      </ul>
    </div>

    <!-- 分类部分 -->
    <div class="card">
      <h4 class="sidebar-title">文章分类</h4>
      <ul class="category-tree">

        <!-- 全部 -->
        <li @click="goToCategory(null)" class="parent-item">
          <span class="category-link"><Menu class="category-icon" /> 全部</span>
        </li>

        <!-- 父级分类 -->
        <li
          v-for="category in parentCategories"
          :key="category.categoryId"
          class="parent-item"
          @mouseenter="toggleCategory(category.categoryId)"
          @mouseleave="toggleCategory(category.categoryId)"
          @click="goToCategory(category.categoryId)"
        >
          <span class="category-link">
            <ArrowDown class="expand-icon" :class="{ rotated: isExpanded(category.categoryId) }" />
            {{ category.categoryName }}
          </span>

          <!-- 子级分类 -->
          <ul v-show="isExpanded(category.categoryId)" class="children-list">
            <li
              v-for="child in getChildren(category.categoryId)"
              :key="child.categoryId"
              class="child-item"
              @click.stop="goToCategory(child.categoryId)"
            >
              <span class="category-link">{{ child.categoryName }}</span>
            </li>
          </ul>
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
/* 侧边栏布局 */
.sidebar {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

/* 用户信息卡片 */
.user-info {
  text-align: center;
  padding: 20px;
}

.user-name {
  margin-top: 10px;
  margin-bottom: 5px;
  font-size: 16px;
  font-weight: bold;
  color: var(--text-primary);
}

.user-desc {
  font-size: 13px;
  color: var(--text-secondary);
}

.avatar-container {
  width: 150px;
  height: 150px;
  border-radius: 50%;
  overflow: hidden;
  margin: 0 auto;
  border: 2px solid var(--border-color);
}

.user-avatar-img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}
/* 侧边栏标题 */
.sidebar-title {
  font-size: 15px;
  font-weight: bold;
  margin-bottom: 12px;
  padding-bottom: 8px;
  border-bottom: 2px solid var(--accent-color);
  color: var(--text-primary);
}

/* 标签样式 */
.tags {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}

.tag {
  background: var(--bg-secondary);
  padding: 4px 10px;
  border-radius: 20px;
  font-size: 13px;
  color: var(--text-secondary);
  border: 1px solid var(--border-color);
  cursor: pointer;
  transition: all 0.2s ease;
}

.tag:hover {
  background: var(--accent-color);
  color: white;
  border-color: var(--accent-color);
}

/* 导航菜单样式 */
.category-list {
  list-style: none;
}

.category-list li {
  padding: 8px 0;
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
  display: flex;
  align-items: center;
  gap: 10px;
  width: 100%;
}

.nav-icon {
  width: 18px;
  height: 18px;
}

.logout-item {
  display: flex;
  align-items: center;
  gap: 10px;
  color: #d9534f !important;
}

/* 树形分类样式 */
.category-tree {
  list-style: none;
  padding-left: 0;
}

.category-tree .parent-item {
  padding: 6px 0;
  font-size: 15px;
  color: var(--text-primary);
  cursor: pointer;
  transition: color 0.3s ease;
}

.category-tree .parent-item:hover {
  color: var(--accent-color);
}

.category-tree .category-link {
  display: flex;
  align-items: center;
  gap: 6px;
}

.category-tree .expand-icon {
  width: 14px;
  height: 14px;
  color: var(--text-secondary);
  transition: transform 0.2s ease;
}

.category-tree .expand-icon.rotated {
  transform: rotate(-90deg);
}

.category-icon {
  width: 14px;
  height: 14px;
}

/* 子级分类 */
.category-tree .children-list {
  list-style: none;
  padding-left: 20px;
  margin-top: 4px;
  animation: slideDown 0.2s ease;
}

.category-tree .child-item {
  padding: 4px 0;
  font-size: 14px;
  color: var(--text-secondary);
  cursor: pointer;
  transition: color 0.3s ease;
}

.category-tree .child-item:hover {
  color: var(--accent-color);
  padding-left: 4px;
}



/* 响应式设计 */
@media (max-width: 768px) {
  .sidebar {
    width: 100%;
  }
}

/*.el-avatar {
  overflow: hidden !important;
}*/
</style>
