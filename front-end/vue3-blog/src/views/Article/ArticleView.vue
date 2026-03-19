<template>
  <div class="container" id="article">
    <leftNav></leftNav>

    <!-- 右侧文章内容 -->
    <main class="right-column">
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
                <span class="article-category" v-for="category in article.categoryList" :key="category.categoryId">
                  {{category.categoryName}}
                </span>
              </div>
              <span v-else>暂无分类</span>
            </div>
            
            <div class="meta-item">
              <span class="meta-label">标签：</span>
              <div class="article-tags" v-if="article.tagList && article.tagList.length > 0">
                <span class="article-tag" v-for="tag in article.tagList" :key="tag.tagId">
                  {{tag.tagName}}
                </span>
              </div>
              <span v-else>暂无标签</span>
            </div>
          </div>

          <!-- 文章内容 -->
          <div class="content" v-html="article.articleContent"></div>

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
import { ref, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import axios from 'axios'
import leftNav from '@/components/layout/leftNav.vue'
import '../../assets/css/home.css'

const router = useRouter()
const route = useRoute()

// 数据状态
const article = ref(null)
const userNickName = ref('')
const loading = ref(true)

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
  // 这里可以添加编辑页面的路由
  alert('编辑功能开发中')
}

// 删除文章
const confirmDelete = (id) => {
  if(confirm('确定要删除这篇文章吗？')) {
    console.log('正在执行删除文章' + id)
    axios.delete('/articles/delete/' + id)
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

// 获取当前用户信息
const getCurrentUser = () => {
  axios.get('/home/currentUser', { credentials: 'include' })
    .then(response => {
      if(response.data.data) {
        userNickName.value = response.data.data.userNickname || 'Visitor'
      }
    })
    .catch(error => {
      console.error('获取用户信息失败', error)
    })
}

// 页面加载时显示文章
onMounted(() => {
  // 获取当前用户信息
  getCurrentUser()
  
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
  axios.get('/articles/specificArticle?id=' + id)
    .then(response => {
      if (response.data.code === 1) {
        console.log("成功跳转到具体的文章页面")
        console.log(response.data.data)
        article.value = response.data.data
      } else {
        console.log("跳转失败：" + response.data.msg)
        alert(response.data.msg)
        router.push('/articles')
      }
    })
    .catch(error => {
      console.log("请求失败：" + error)
      router.push('/articles')
    })
    .finally(() => {
      loading.value = false
    })
})
</script>

<style scoped>
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

/* 响应式设计 */
@media (max-width: 768px) {
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
}
</style>