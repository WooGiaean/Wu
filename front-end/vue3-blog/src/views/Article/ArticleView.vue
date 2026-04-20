<template>
  <div class="container" id="article">
    <!-- 左侧侧边栏 -->
    <aside class="sidebar">
      <!-- 分类导航 -->
      <div class="sidebar-section">
        <h3>📁 文章分类</h3>
        <ul class="category-list">
          <li class="category-item" @click="goToArticles()">
            全部
          </li>
          <li v-for="category in categories" :key="category.categoryId" class="category-item"
              @click="goToCategory(category.categoryId)">
            {{ category.categoryName }}
          </li>
        </ul>
      </div>

      <!-- 热门标签 -->
      <div class="sidebar-section">
        <h3>🏷️ 热门标签</h3>
        <div class="tag-cloud">
          <span v-for="tag in tags" :key="tag.tagId" class="tag" @click="goToTag(tag.tagId)">
            {{ tag.tagName }}
          </span>
        </div>
      </div>

      <!-- 最近文章 -->
      <div class="sidebar-section">
        <h3>📅 最近文章</h3>
        <ul class="recent-articles">
          <li v-for="article in recentArticles" :key="article.articleId" class="recent-article-item" @click="goToArticle(article.articleId)">
            <span class="recent-title">{{ article.articleTitle }}</span>
            <span class="recent-date">{{ formatDate(article.articleCreateTime) }}</span>
          </li>
        </ul>
      </div>
    </aside>

    <!-- 右侧文章内容 -->
    <main class="main-content">
      <div class="content-wrapper">
        <article v-if="article">
          <header>
            <h1>{{article.articleTitle}}</h1>
            <p class="meta">发布于：{{formatDate(article.articleCreateTime)}} | 作者：{{article.blogger}}</p>
          </header>

          <!-- 文章分类和标签 -->
          <div class="article-meta">
            <div class="meta-item">
              <span class="meta-label">分类：</span>
              <div class="article-categories" v-if="article.categoryList && article.categoryList.length > 0">
                <span class="article-category" v-for="category in article.categoryList" :key="category.categoryId" @click="goToCategory(category.categoryId)">
                  {{category.categoryName}}
                </span>
              </div>
              <span v-else>暂无分类</span>
            </div>

            <div class="meta-item">
              <span class="meta-label">标签：</span>
              <div class="article-tags" v-if="article.tagList && article.tagList.length > 0">
                <span class="article-tag" v-for="tag in article.tagList" :key="tag.tagId" @click="goToTag(tag.tagId)">
                  {{tag.tagName}}
                </span>
              </div>
              <span v-else>暂无标签</span>
            </div>
          </div>

          <!-- 文章内容 -->
          <div class="content" v-html="articleContentHtml"></div>

          <!-- 文章缩略图 -->
          <div class="article-thumbnail" v-if="article.articleThumbnail">
            <img :src="'/uploaded-images/'+article.articleThumbnail"
                 :alt="article.articleTitle"
                 class="thumbnail-img">
          </div>

          <!-- 编辑和删除文章按钮的位置 -->
          <div class="actions">
            <button class="btn btn-info" @click="editArticle(article.articleId)">
              ✏️ 编辑文章
            </button>

            <button class="btn btn-delete" @click="confirmDelete(article.articleId)">
              🗑️ 删除文章
            </button>
          </div>

          <!-- 评论区 -->
          <div class="comments-section">
            <h3>💬 评论区</h3>

            <!-- 发表评论 -->
            <div class="comment-form">
              <textarea v-model="newComment.content" placeholder="写下你的评论..." rows="4"></textarea>
              <div class="form-actions">
                <label class="checkbox-label">
                  <input type="checkbox" v-model="newComment.isAnonymous"> 匿名评论
                </label>
                <input v-if="newComment.isAnonymous" v-model="newComment.anonymousName" placeholder="匿名昵称" class="anonymous-input">
                <button class="btn btn-primary" @click="submitComment">发表评论</button>
              </div>
            </div>

            <!-- 评论列表 -->
            <div class="comments-list" v-if="comments.length > 0">
              <div class="comment-item" v-for="comment in paginatedComments" :key="comment.commentId">
                <div class="comment-header">
                  <span class="comment-author">{{comment.anonymousName || userNickName || '匿名用户'}}</span>
                  <span class="comment-time">{{formatDate(comment.createTime)}}</span>
                </div>
                <div class="comment-content">{{comment.content}}</div>
              </div>
            </div>
            <div v-else class="empty-comments">暂无评论，快来发表第一条评论吧！</div>

            <!-- 分页 -->
            <div class="pagination" v-if="totalPages > 1">
              <button class="page-btn" @click="currentPage = 1" :disabled="currentPage === 1">首页</button>
              <button class="page-btn" @click="currentPage--" :disabled="currentPage === 1">上一页</button>
              <span class="page-info">第 {{currentPage}} 页，共 {{totalPages}} 页</span>
              <button class="page-btn" @click="currentPage++" :disabled="currentPage === totalPages">下一页</button>
              <button class="page-btn" @click="currentPage = totalPages" :disabled="currentPage === totalPages">末页</button>
            </div>
          </div>

          <!-- 回到首页 -->
          <router-link to="/articles" class="back-home">← 回到文章列表</router-link>
        </article>
        <div v-else-if="loading" class="loading">文章加载中...</div>
        <div v-else class="empty-state">文章不存在或已被删除</div>
      </div>
    </main>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import {  useRoute } from 'vue-router'
import router from '@/router/index.js'
import axiosAPI from '@/utils/api/axios.js'
import { marked } from 'marked'
import { ElForm, ElFormItem, ElInput, ElButton, ElDialog,
  ElTag, ElMenu, ElMenuItem, ElCard, ElSkeleton, ElEmpty,
  ElCheckbox, ElCheckboxGroup, ElUpload, ElMessage } from 'element-plus'
//import '@/assets/css/home.css'

/*const router = useRouter()*/
const route = useRoute()

// 数据状态
const article = ref(null)
const userNickName = ref('')
const loading = ref(true)
const comments = ref([])
const currentPage = ref(1)
const pageSize = ref(10)
const categories = ref([])
const tags = ref([])
const recentArticles = ref([])
// 新增的评论数据格式
const newComment = ref({
  content: '',
  isAnonymous: false,
  anonymousName: ''
})

// 将markdown转换为HTML
const articleContentHtml = computed(() => {
  if (article.value && article.value.articleContent) {
    return marked(article.value.articleContent)
  }
  return ''
})

// 分页评论计算
const paginatedComments = computed(() => {
  const start = (currentPage.value - 1) * pageSize.value
  const end = start + pageSize.value
  return comments.value.slice(start, end)
})

// 总页数计算
const totalPages = computed(() => {
  return Math.ceil(comments.value.length / pageSize.value)
})

// 格式化日期函数
const formatDate = (timeString) => {
  if(!timeString) return ''
  const date = new Date(timeString)
  const year = date.getFullYear()
  const month = String(date.getMonth() + 1).padStart(2, '0')
  const day = String(date.getDate()).padStart(2, '0')
  return `${year}-${month}-${day}`
}

// 修改文章
const editArticle = (id) => {
  console.log('正在执行修改文章' + id)
  router.push(`/articles/edit/${id}`)
}

// 删除文章
const confirmDelete = (id) => {
  if(confirm('确定要删除这篇文章吗？')) {
    console.log('正在执行删除文章' + id)
    axiosAPI.delete(`/user/articles/${id}`)
      .then(response => {
        console.log(response.data.code == 1 ? '删除成功' : '删除失败')
        if (response.data.code == 1) {
          router.push('/articles')
        } else {
          alert('删除失败：' + (response.data.msg || '未知错误'))
        }
      })
      .catch(error => {
        console.log('请求失败：' + error)
        alert('删除失败，请重试')
      })
  }
}

// 导航函数
const goToArticles = () => {
  router.push('/articles')
}

const goToCategory = (categoryId) => {
  router.push(`/articles?category=${categoryId}`)
}

const goToTag = (tagId) => {
  router.push(`/articles?tag=${tagId}`)
}

// 获取当前用户信息
const getCurrentUser = () => {
  axiosAPI.get('/home/currentUser', { credentials: 'include' })
    .then(response => {
      if(response.data.data) {
        userNickName.value = response.data.data.userNickname || 'Visitor'
      }
    })
    .catch(error => {
      console.error('获取用户信息失败', error)
    })
}

// 获取分类列表
const getCategories = () => {
  axiosAPI.get('/category/list')
    .then(response => {
      if (response.data.code === 1 && response.data.data) {
        categories.value = response.data.data.records || []
      } else {
        categories.value = []
      }
    })
    .catch(error => {
      console.error('获取分类列表失败:', error)
    })
}

// 获取标签列表
const getTags = () => {
  axiosAPI.get('/tags/list')
    .then(response => {
      if (response.data.code === 1 && response.data.data) {
        tags.value = response.data.data.records || []
      } else {
        tags.value = []
      }
    })
    .catch(error => {
      console.error('获取标签列表失败:', error)
    })
}

// 获取最近文章
const getRecentArticles = () => {
  axiosAPI.get('/home/latestArticles')
    .then(response => {
      if (response.data.code === 1 && response.data.data) {
        recentArticles.value = response.data.data.records.slice(0, 5) || []
      } else {
        recentArticles.value = []
      }
    })
    .catch(error => {
      console.error('获取最近文章失败:', error)
    })
}

// 获取评论列表
const getComments = (articleId) => {
  axiosAPI.get(`/comments/article/${articleId}`)
    .then(response => {
      if (response.data.code === 1 && response.data.data) {
        comments.value = response.data.data.records || []
        currentPage.value = 1 // 重置页码
      }
    })
    .catch(error => {
      console.error('获取评论失败', error)
    })
}

// 提交评论
const submitComment = () => {
  if (!newComment.value.content.trim()) {
    alert('评论内容不能为空')
    return
  }

  if (newComment.value.anonymous && !newComment.value.anonymousName.trim()) {
    alert('请输入匿名昵称')
    return
  }

  const commentData = {
    articleId: article.value.articleId,
    content: newComment.value.content,
    isAnonymous: newComment.value.isAnonymous,
    anonymousName: newComment.value.isAnonymous ? newComment.value.anonymousName : ''
  }
  console.log(commentData)
  axiosAPI.post('/user/comments', commentData,{
    headers:{
      'Authorization': 'Bearer ' + localStorage.getItem('token')
    }
  })
    .then(response => {
      console.log('请求转发成功', response.data)
      if (response.data.code === 1) {
        alert('评论发表成功')
        // 清空评论表单
        newComment.value = {
          content: '',
          anonymous: false,
          anonymousName: ''
        }
        // 重新获取评论列表
        getComments(article.value.articleId)
      } else {
        alert('评论发表失败：' + (response.data.msg || '未知错误'))
      }
    })
    .catch(error => {
      console.error('提交评论失败', error)
      alert('评论发表失败，请重试')
    })
}

// 页面加载时显示文章
onMounted(() => {
  // 获取当前用户信息
  getCurrentUser()
  // 获取分类、标签和最近文章
  getCategories()
  getTags()
  getRecentArticles()

  // 获取文章ID
  const id = route.params.id
  if (!id) {
    alert("文章id无效")
    router.push('/articles')
    return
  }
  console.log("文章id为：" + id)

  // 获取文章详情
  loading.value = true
  axiosAPI.get(`user/articles/${id}`)
    .then(response => {
      if (response.data.code === 1) {
        console.log("成功跳转到具体的文章页面")
        console.log(response.data.data)
        article.value = response.data.data
        // 获取评论列表
        getComments(article.value.articleId)
      } else {
        console.log("跳转失败：" + response.data.msg)
        //alert(response.data.msg)
        ElMessage.error(response.data.msg)
        router.push('/articles')
      }
    })
    .catch(error => {
      console.log("请求失败：" + error)
      ElMessage.error(error)
      router.push('/articles')
    })
    .finally(() => {
      loading.value = false
    })
})
</script>

<style scoped>
/* 容器布局 */
.container {
  display: flex;
  flex: 1;
  padding: 20px;
  gap: 30px;
  max-width: 1200px;
  margin: 0 auto;
  width: 100%;
  align-items: flex-start;
  min-height: 100vh;
}

/* 侧边栏样式 */
.sidebar {
  width: 250px;
  flex-shrink: 0;
  display: flex;
  flex-direction: column;
  gap: 20px;
}

/* 响应式设计 */
@media (max-width: 1024px) {
  .container {
    padding: 15px;
    gap: 20px;
  }

  .sidebar {
    width: 220px;
  }
}

@media (max-width: 768px) {
  .container {
    flex-direction: column;
    padding: 10px;
    gap: 20px;
  }

  .sidebar {
    width: 100%;
  }

  .main-content {
    width: 100%;
  }
}

.sidebar-section {
  background: var(--card-bg);
  padding: 20px;
  border-radius: 12px;
  border: 1px solid var(--border-color);
  box-shadow: var(--card-shadow);
}

.sidebar-section h3 {
  color: var(--text-primary);
  margin-bottom: 15px;
  font-size: 16px;
  font-weight: 600;
  border-left: 4px solid var(--accent-color);
  padding-left: 10px;
}

.category-list {
  list-style: none;
}

.category-item {
  padding: 10px 0;
  cursor: pointer;
  color: var(--text-secondary);
  transition: all 0.3s ease;
  border-bottom: 1px solid var(--border-color);
}

.category-item:hover {
  color: var(--accent-color);
  padding-left: 10px;
}

.tag-cloud {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}

.tag {
  padding: 4px 12px;
  background: var(--bg-primary);
  color: var(--text-secondary);
  border-radius: 16px;
  font-size: 12px;
  cursor: pointer;
  transition: all 0.3s ease;
  border: 1px solid var(--border-color);
}

.tag:hover {
  background: var(--accent-color);
  color: white;
  border-color: var(--accent-color);
}

.recent-articles {
  list-style: none;
}

.recent-article-item {
  padding: 10px 0;
  cursor: pointer;
  border-bottom: 1px solid var(--border-color);
  transition: all 0.3s ease;
}

.recent-article-item:hover {
  padding-left: 10px;
}

.recent-title {
  display: block;
  color: var(--text-primary);
  font-size: 14px;
  margin-bottom: 5px;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.recent-date {
  display: block;
  color: var(--text-secondary);
  font-size: 12px;
}

/* 主内容区域 */
.main-content {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 30px;
}

/* 文章样式 */
article {
  background-color: var(--card-bg);
  border-radius: 12px;
  padding: 30px;
  border: 1px solid var(--border-color);
  box-shadow: var(--card-shadow);
}

article header {
  text-align: center;
  margin-bottom: 30px;
  padding-bottom: 20px;
  border-bottom: 2px solid var(--border-color);
}

article h1 {
  font-size: 24px;
  color: var(--text-primary);
  margin-bottom: 15px;
  line-height: 1.3;
  font-weight: 600;
}

.meta {
  color: var(--text-secondary);
  font-size: 14px;
  margin-top: 10px;
}

/* 文章内容 */
.content {
  font-size: 16px;
  color: var(--text-primary);
  line-height: 1.8;
  margin-bottom: 30px;
}

.content p {
  margin-bottom: 20px;
}

.content img {
  max-width: 100%;
  height: auto;
  border-radius: 8px;
  margin: 20px 0;
}

/* 文章分类和标签 */
.article-meta {
  display: flex;
  flex-wrap: wrap;
  gap: 20px;
  margin-bottom: 30px;
  padding: 15px;
  background-color: var(--bg-primary);
  border-radius: 8px;
  border: 1px solid var(--border-color);
}

.meta-item {
  display: flex;
  align-items: center;
  gap: 10px;
  flex-wrap: wrap;
}

.meta-label {
  font-weight: bold;
  color: var(--text-primary);
  font-size: 14px;
}

.article-categories, .article-tags {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}

.article-category, .article-tag {
  display: inline-block;
  padding: 4px 12px;
  border-radius: 14px;
  font-size: 12px;
  text-decoration: none;
  transition: all 0.3s ease;
  cursor: pointer;
}

.article-category {
  background-color: rgba(76, 175, 80, 0.2);
  color: var(--accent-color);
  border: 1px solid var(--accent-color);
}

.article-category:hover {
  background-color: var(--accent-color);
  color: white;
}

.article-tag {
  background-color: var(--bg-secondary);
  color: var(--text-secondary);
  border: 1px solid var(--border-color);
}

.article-tag:hover {
  background-color: var(--accent-color);
  color: white;
  border-color: var(--accent-color);
}

/* 文章缩略图 */
.article-thumbnail {
  margin: 30px 0;
  text-align: center;
}

.thumbnail-img {
  max-width: 100%;
  height: auto;
  border-radius: 8px;
  box-shadow: 0 4px 12px rgba(0,0,0,0.1);
  transition: transform 0.3s ease;
}

.thumbnail-img:hover {
  transform: scale(1.02);
}

/* 操作按钮 */
.actions {
  margin-top: 30px;
  padding-top: 20px;
  border-top: 2px solid var(--border-color);
  display: flex;
  gap: 15px;
}

.btn {
  padding: 10px 20px;
  border: none;
  border-radius: 25px;
  font-size: 14px;
  cursor: pointer;
  transition: all 0.3s ease;
  font-weight: 500;
  text-decoration: none;
  display: inline-block;
}

.btn-info {
  background-color: var(--accent-color);
  color: white;
}

.btn-info:hover {
  background-color: #388E3C;
  transform: translateY(-1px);
  box-shadow: 0 4px 8px rgba(0,0,0,0.15);
}

.btn-delete {
  background-color: var(--bg-secondary);
  color: var(--text-primary);
  border: 1px solid var(--border-color);
}

.btn-delete:hover {
  background-color: var(--bg-primary);
  transform: translateY(-1px);
  box-shadow: 0 4px 8px rgba(0,0,0,0.1);
}

/* 回到首页链接 */
.back-home {
  display: inline-block;
  margin-top: 30px;
  padding: 10px 20px;
  background-color: var(--accent-color);
  color: white;
  text-decoration: none;
  border-radius: 25px;
  transition: all 0.3s ease;
}

.back-home:hover {
  background-color: #388E3C;
  transform: translateY(-1px);
  box-shadow: 0 4px 8px rgba(0,0,0,0.15);
}

/* 评论区 */
.comments-section {
  margin-top: 40px;
  padding-top: 30px;
  border-top: 2px solid var(--border-color);
}

.comments-section h3 {
  color: var(--text-primary);
  margin-bottom: 20px;
  font-size: 18px;
  font-weight: 600;
}

/* 评论表单 */
.comment-form {
  margin-bottom: 30px;
  padding: 20px;
  background-color: var(--bg-primary);
  border-radius: 8px;
  border: 1px solid var(--border-color);
}

.comment-form textarea {
  width: 100%;
  padding: 12px;
  border: 1px solid var(--border-color);
  border-radius: 8px;
  background-color: var(--bg-secondary);
  color: var(--text-primary);
  resize: vertical;
  font-size: 14px;
  font-family: inherit;
  margin-bottom: 15px;
}

.comment-form textarea:focus {
  outline: none;
  border-color: var(--accent-color);
  box-shadow: 0 0 0 2px rgba(76, 175, 80, 0.2);
}

.form-actions {
  display: flex;
  align-items: center;
  gap: 15px;
  flex-wrap: wrap;
}

.checkbox-label {
  display: flex;
  align-items: center;
  gap: 5px;
  font-size: 14px;
  color: var(--text-secondary);
  cursor: pointer;
}

.anonymous-input {
  padding: 8px 12px;
  border: 1px solid var(--border-color);
  border-radius: 6px;
  background-color: var(--bg-secondary);
  color: var(--text-primary);
  font-size: 14px;
  flex: 1;
  min-width: 200px;
}

.anonymous-input:focus {
  outline: none;
  border-color: var(--accent-color);
  box-shadow: 0 0 0 2px rgba(76, 175, 80, 0.2);
}

/* 评论列表 */
.comments-list {
  margin-bottom: 30px;
}

.comment-item {
  padding: 20px;
  background-color: var(--bg-primary);
  border-radius: 8px;
  border: 1px solid var(--border-color);
  margin-bottom: 15px;
  transition: all 0.3s ease;
}

.comment-item:hover {
  box-shadow: 0 4px 12px rgba(0,0,0,0.1);
  transform: translateY(-2px);
}

.comment-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 10px;
}

.comment-author {
  font-weight: 600;
  color: var(--text-primary);
  font-size: 14px;
}

.comment-time {
  font-size: 12px;
  color: var(--text-secondary);
}

.comment-content {
  font-size: 14px;
  color: var(--text-primary);
  line-height: 1.6;
  word-break: break-word;
}

.empty-comments {
  text-align: center;
  padding: 40px;
  color: var(--text-secondary);
  font-size: 14px;
  background-color: var(--bg-primary);
  border-radius: 8px;
  border: 1px solid var(--border-color);
}

/* 分页 */
.pagination {
  display: flex;
  justify-content: center;
  align-items: center;
  gap: 10px;
  margin-top: 20px;
}

.page-btn {
  padding: 6px 12px;
  border: 1px solid var(--border-color);
  border-radius: 4px;
  background-color: var(--bg-secondary);
  color: var(--text-primary);
  cursor: pointer;
  font-size: 14px;
  transition: all 0.3s ease;
}

.page-btn:hover:not(:disabled) {
  background-color: var(--accent-color);
  color: white;
  border-color: var(--accent-color);
}

.page-btn:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

.page-info {
  font-size: 14px;
  color: var(--text-secondary);
}

/* 响应式设计 */
@media (max-width: 1200px) {
  .container {
    flex-direction: column;
  }

  .sidebar {
    width: 100%;
    flex-direction: row;
    flex-wrap: wrap;
  }

  .sidebar-section {
    flex: 1;
    min-width: 250px;
  }
}

@media (max-width: 768px) {
  .container {
    padding: 10px;
    gap: 20px;
  }

  .sidebar {
    flex-direction: column;
  }

  .sidebar-section {
    width: 100%;
  }

  article h1 {
    font-size: 20px;
  }

  .article-meta {
    flex-direction: column;
    align-items: flex-start;
  }

  .actions {
    flex-direction: column;
  }

  .btn {
    width: 100%;
    text-align: center;
  }

  .form-actions {
    flex-direction: column;
    align-items: flex-start;
  }

  .anonymous-input {
    width: 100%;
  }

  .pagination {
    flex-wrap: wrap;
  }

  .page-btn {
    flex: 1;
    min-width: 80px;
  }
}
</style>
