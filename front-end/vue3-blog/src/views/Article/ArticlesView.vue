<template>
  <div class="container" id="app">
    <leftNav></leftNav>

    <!-- 右侧文章列表 -->
    <main class="right-column">
      <div class="content-wrapper">
        <!-- 页面标题 -->
        <div class="section-header">
          <h2>📚 所有文章</h2>
        </div>

        <!-- 搜索区域 -->
        <div class="search-section">
          <input type="text" v-model="keyword" class="search-bar" placeholder="搜索文章..." @keyup.enter="search" />
          <button class="btn btn-primary" @click="search">搜索</button>
          <button class="btn btn-secondary" @click="showAddModal = true">新增</button>
        </div>

        <!-- 分类展示区域 -->
        <div class="category-section">
          <div class="category-tags">
            <a href="#" class="category-tag" :class="{ active: selectedCategoryId === null }"
               @click.prevent="filterByCategory(null)">全部</a>
            <a href="#" v-for="c in categories" :key="c.categoryId"
               class="category-tag"
               :class="{ active: selectedCategoryId === c.categoryId }"
               @click.prevent="filterByCategory(c.categoryId)">
                {{c.categoryName}}
            </a>
          </div>
        </div>

        <!-- 文章列表 -->
        <div v-if="loading" class="loading">文章加载中...</div>
        <div v-else-if="articles.length === 0" class="empty-state">暂无文章，快去写一篇吧！</div>
        <div v-else>
          <article class="blog-item" v-for="article in articles" :key="article.articleId"
                   @click="showSpecificArticle(article.articleId)">
            <!-- 图片添加默认图逻辑 -->
            <img :src="article.articleThumbnail ? `/uploaded-images/${article.articleThumbnail}` : 'https://picsum.photos/seed/blog/300/200'"
                 class="blog-image"
                 :alt="article.articleTitle">
<!--                 onerror="this.src='https://picsum.photos/seed/error/300/200'"-->

            <div class="blog-content">
              <h3>{{article.articleTitle}}</h3>
              <p>{{article.articleSummary}}</p>
              <div class="blog-meta">
                <span>📅 {{ formatDate(article.articleCreateTime) }}</span>
                <span>👁️ {{article.articleReadCount}}</span>
                <span>💬 {{article.articleCommentCount}}</span>
                <span>❤️ {{article.articleLikeCount}}</span>
              </div>

              <!-- 文章分类标签 -->
              <div class="article-categories" v-if="article.categoryList && article.categoryList.length > 0">
                <span class="article-category" v-for="category in article.categoryList" :key="category.categoryId">
                  {{category.categoryName}}
                </span>
              </div>

              <!-- 文章标签 -->
              <div class="article-tags" v-if="article.tagList && article.tagList.length > 0">
                <span class="article-tag" v-for="tag in article.tagList" :key="tag.tagId">
                  {{tag.tagName}}
                </span>
              </div>
            </div>
          </article>
        </div>
      </div>
    </main>

    <!-- 弹窗页面(用于添加新文章) -->
    <div class="modal" v-if="showAddModal" @click="closeModal">
      <div class="modal-dialog" @click.stop>
        <div class="modal-content">
          <!-- 头部标题 -->
          <div class="modal-header">
            <h5 class="modal-title">新增文章</h5>
            <button type="button" class="btn-close" @click="closeModal">&times;</button>
          </div>

          <div class="modal-body">
            <!-- 表单开始 -->
            <form id="addArticleForm">
              <!-- 文章标题输入框 -->
              <div class="mb-3">
                <label for="title" class="form-label">标题</label>
                <input type="text" class="form-control" id="title" v-model="newArticle.title" placeholder="请输入博文标题">
              </div>

              <!-- 博客名称输入框 -->
              <div class="mb-3">
                <label for="blog-input" class="form-label">博客</label>
                <input type="text" class="form-control" id="blog-input" v-model="newArticle.blogger" placeholder="请输入博客名称">
              </div>

              <!-- 文章分类选择 -->
              <div class="mb-3">
                <label class="form-label">文章分类</label>
                <div class="category-checkboxes">
                  <div class="form-check" v-for="category in categories" :key="category.categoryId">
                    <input type="checkbox" class="form-check-input"
                           :id="'category-' + category.categoryId"
                           :value="category.categoryId"
                           v-model="selectedCategoryIds">
                    <label class="form-check-label" :for="'category-' + category.categoryId">
                      {{category.categoryName}}
                    </label>
                  </div>
                </div>
              </div>

              <!-- 文章内容 -->
              <div class="mb-3">
                <label class="form-label">内容</label>
                <textarea class="form-control" v-model="newArticle.content" rows="6" placeholder="请输入文章内容"></textarea>
              </div>

              <!-- 文章概要 -->
              <div class="mb-3">
                <label for="summary-input" class="form-label">博文概要</label>
                <input type="text" class="form-control" id="summary-input" v-model="newArticle.summary" placeholder="概要信息">
              </div>

              <!-- 图片上传 -->
              <div class="mb-3">
                <label for="coverImage" class="form-label">封面图片（可选）</label>
                <input type="file" class="form-control" id="coverImage" accept="image/*" @change="handleImageUpload"/>
              </div>
            </form>
            <!-- 表单结束 -->
          </div>

          <!-- 底部提交按钮以及取消按钮  -->
          <div class="modal-footer">
            <button type="button" class="btn btn-secondary" @click="closeModal">关闭</button>
            <button type="button" class="btn btn-primary" @click="submitNew">提交</button>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import axiosAPI from '@/utils/api/axios.js'
import leftNav from '@/components/layout/leftNav.vue'
import '../../assets/css/home.css'

const router = useRouter()

// 数据状态
const keyword = ref('')  //关键词搜索文章
const articles = ref([])  //文章实体
const categories = ref([])  //分类
const userInfo = ref('')  //用户信息
const loading = ref(true) //加载效果
const selectedCategoryId = ref(null)
const selectedCategoryIds = ref([])
const showAddModal = ref(false) //添加文章弹窗的判断
const newArticle = ref({
  title: '',
  blogger: '',
  content: '',
  summary: '',
  thumbnail: ''
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

// 获取所有文章
const getAllArticles = () => {
  loading.value = true
  axiosAPI.get('/articles/list')
    .then(response => {
      const res = response.data
      if (res.data && res.data.records) {
        articles.value = res.data.records || []
      } else {
        articles.value = []
      }
    })
    .catch(error => {
      console.error("请求发送失误", error)
      articles.value = []
    })
    .finally(() => {
      loading.value = false
    })
}

// 分类列表获取
const getCategories = () => {
  axiosAPI.get('/category/list')
    .then(response => {
      const result = response.data
      if(result.code===1){
        categories.value = result.data.records || []
      }else{
        categories.value = []
      }
    }).catch( error => {
      console.error("请求发送错误", error)
      categories.value = []
    })
}

// 根据分类筛选文章
const filterByCategory = (categoryId) => {
  selectedCategoryId.value = categoryId
  loading.value = true

  if (categoryId === null) {
    //显示所有文章
    getAllArticles()
  } else {
    //根据分类ID获取文章
    axiosAPI.get(`/category/articles?categoryId=${categoryId}&page=1&pageSize=10`)
      .then(response => {
        if (response.data.data && response.data.data.records) {
          articles.value = response.data.data.records || []
        } else {
          articles.value = []
        }
      })
      .catch(error => {
        console.error("请求发送失误", error)
        articles.value = []
      })
      .finally(() => {
        loading.value = false
      })
  }
}

// 搜索文章
const search = () => {
  const keyWord = keyword.value.trim()
  if (!keyWord) {
    alert("关键字为空")
    return
  }

  loading.value = true
  //请求转发
  axiosAPI.post('/articles/searchCondition', {
    articleTitle: keyWord,
    articleContent: keyWord,
    articleSummary: keyWord
  }).then(response => {
    if (response.data.code == 1 || response.data.success) {
      articles.value = response.data.data || []
    } else {
      articles.value = []
    }
  }).catch(error => {
    console.error("请求发送错误", error)
    articles.value = []
  }).finally(() => {
    loading.value = false
  })
}

// 处理图片上传
const handleImageUpload = (event) => {
  const file = event.target.files[0]
  if (file) {
    const formData = new FormData()
    formData.append('file', file)
    axiosAPI.post('/images/upload', formData, {
      headers: {'Content-Type': 'multipart/form-data'}
    }).then(response => {
      if (response.data.code == 1) {
        newArticle.value.thumbnail = response.data.data
      } else {
        alert("图片上传失败：" + (response.data.msg || "Unknown errors"))
      }
    }).catch(error => {
      console.error("图片上传失败", error)
      alert("图片上传失败")
    })
  }
}

// 新增文章
const submitNew = async () => {
  try {
    //上传博文内容
    const insertRes = await axiosAPI.post('/articles/insert', {
      articleTitle: newArticle.value.title,
      blogger: newArticle.value.blogger,
      articleContent: newArticle.value.content,
      articleSummary: newArticle.value.summary,
      articleThumbnail: newArticle.value.thumbnail,
      categoryIds: selectedCategoryIds.value
    })

    if (insertRes.data.code === 1) {
      alert("文章添加成功")
      //重新刷新文章列表
      getAllArticles()
      // 清空表单
      newArticle.value = {
        title: '',
        blogger: '',
        content: '',
        summary: '',
        thumbnail: ''
      }
      // 清空选中的分类ID
      selectedCategoryIds.value = []
      // 关闭模态框
      showAddModal.value = false
    } else {
      alert("文章添加失败：" + (insertRes.data.msg || "Unknown errors"))
    }
  } catch (error) {
    console.error("请求发送错误", error)
    alert("请求发送错误：" + (error.message || "Unknown errors"))
  }
}

// 跳转到文章详情页面
const showSpecificArticle = (id) => {
  router.push(`/articles/${id}`)
}

// 关闭模态框
const closeModal = () => {
  showAddModal.value = false
  // 清空表单
  newArticle.value = {
    title: '',
    blogger: '',
    content: '',
    summary: '',
    thumbnail: ''
  }
  // 清空选中的分类ID
  selectedCategoryIds.value = []
}

// 页面加载时获取数据
onMounted(() => {
  // 使用 Promise.all 等待所有请求完成再隐藏 loading
  Promise.all([
    getAllArticles(),
    getCategories(),
    axiosAPI.get('/home/currentUser', { credentials: 'include' })
  ]).then(([_, __, userRes]) => {
    // 处理用户数据
    if(userRes.data.data) {
      userInfo.value = userRes.data.data
    }
  }).catch(error => {
    console.error("页面初始化数据请求失败", error)
  })
})
</script>

<style scoped>
.search-section {
  display: flex;
  gap: 10px;
  margin-bottom: 20px;
  flex-wrap: wrap;
}

.search-bar {
  flex: 1;
  min-width: 200px;
  padding: 10px;
  border: 1px solid var(--border-color);
  border-radius: 8px;
  background: var(--bg-primary);
  color: var(--text-primary);
}

.search-bar:focus {
  outline: none;
  border-color: var(--accent-color);
  box-shadow: 0 0 0 2px rgba(76, 175, 80, 0.2);
}

.modal {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background-color: rgba(0, 0, 0, 0.5);
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 1000;
}

.modal-dialog {
  background: var(--bg-secondary);
  border-radius: 8px;
  width: 90%;
  max-width: 800px;
  max-height: 90vh;
  overflow-y: auto;
  border: 1px solid var(--border-color);
  box-shadow: var(--card-shadow);
}

.modal-header {
  padding: 20px;
  border-bottom: 1px solid var(--border-color);
  display: flex;
  justify-content: space-between;
  align-items: center;
  background: var(--bg-primary);
}

.modal-body {
  padding: 20px;
}

.modal-footer {
  padding: 20px;
  border-top: 1px solid var(--border-color);
  display: flex;
  justify-content: flex-end;
  gap: 10px;
  background: var(--bg-primary);
}

.form-label {
  display: block;
  margin-bottom: 8px;
  font-weight: 500;
  color: var(--text-primary);
}

.form-control {
  width: 100%;
  padding: 10px;
  border: 1px solid var(--border-color);
  border-radius: 8px;
  background: var(--bg-primary);
  color: var(--text-primary);
  margin-bottom: 15px;
}

.form-control:focus {
  outline: none;
  border-color: var(--accent-color);
  box-shadow: 0 0 0 2px rgba(76, 175, 80, 0.2);
}

.category-checkboxes {
  display: flex;
  flex-wrap: wrap;
  gap: 15px;
  margin-bottom: 15px;
}

.form-check {
  display: flex;
  align-items: center;
  gap: 5px;
}

.form-check-input {
  width: auto;
}

.form-check-label {
  color: var(--text-secondary);
  font-size: 14px;
}

.btn-close {
  background: none;
  border: none;
  font-size: 24px;
  cursor: pointer;
  color: var(--text-secondary);
}

.btn-close:hover {
  color: var(--text-primary);
}

.article-categories,
.article-tags {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
  margin-top: 10px;
}

.article-category,
.article-tag {
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
  background-color: var(--bg-primary);
  color: var(--text-secondary);
  border: 1px solid var(--border-color);
}

.article-tag:hover {
  background-color: var(--accent-color);
  color: white;
  border-color: var(--accent-color);
}
</style>
