<template>
  <AppLayout>
    <div class="articles-page">
      <div class="page-header">
        <div class="page-title">
          <Reading class="title-icon" />
          <h1>所有文章</h1>
        </div>
        <el-button type="primary" class="add-btn" @click="showAddModal = true">
          <Plus class="btn-icon" />
          新增文章
        </el-button>
      </div>

      <div class="search-bar">
        <el-input
          v-model="keyword"
          placeholder="搜索文章标题或内容..."
          prefix-icon="Search"
          size="large"
          class="search-input"
          @keyup.enter="search"
        />
        <el-button type="primary" class="search-btn" @click="search">
          <Search class="btn-icon" />
          搜索
        </el-button>
      </div>

      <div v-if="loading" class="loading-state">
        <el-skeleton :rows="6" animated />
      </div>

      <div v-else-if="articles.length === 0" class="empty-state">
        <el-empty description="暂无文章，快去写一篇吧！" />
      </div>

      <div v-else class="articles-list">
        <el-card
          v-for="(article, index) in articles"
          :key="article.articleId"
          class="article-card"
          @click="goToArticle(article.articleId)"
          :style="{ animationDelay: `${index * 0.05}s` }"
        >
          <div class="article-content">
            <div class="article-thumbnail" v-if="getArticleThumbnail(article)">
              <img :src="getArticleThumbnail(article)" :alt="article.articleTitle" />
            </div>
            
            <div class="article-info">
              <h3 class="article-title">{{ article.articleTitle }}</h3>
              
              <div class="article-meta">
                <span class="meta-item">
                  <Calendar class="meta-icon" />
                  {{ formatDate(article.articleCreateTime) }}
                </span>
                <span class="meta-item">
                  <View class="meta-icon" />
                  {{ article.articleReadCount || 0 }}
                </span>
                <span class="meta-item">
                  <ChatDotRound class="meta-icon" />
                  {{ article.articleCommentCount || 0 }}
                </span>
                <span class="meta-item">
                  <Star class="meta-icon" />
                  {{ article.articleLikeCount || 0 }}
                </span>
              </div>
              
              <p class="article-summary">{{ article.articleSummary || '暂无摘要' }}</p>
              
              <div class="article-tags">
                <el-tag
                  v-for="category in article.categoryList"
                  :key="category.categoryId"
                  size="small"
                  type="success"
                  effect="plain"
                  @click.stop="handleCategorySelect(category.categoryId)"
                >
                  {{ category.categoryName }}
                </el-tag>
                <el-tag
                  v-for="tag in article.tagList"
                  :key="tag.tagId"
                  size="small"
                  type="info"
                  effect="plain"
                >
                  {{ tag.tagName }}
                </el-tag>
              </div>
            </div>
          </div>
        </el-card>
      </div>
    </div>

    <el-dialog
      v-model="showAddModal"
      title="新增文章"
      width="800px"
      top="5vh"
      class="add-article-dialog"
      :close-on-click-modal="false"
    >
      <div class="article-form">
        <el-form :model="newArticle" label-width="80px">
          <el-form-item label="标题" required>
            <el-input
              v-model="newArticle.title"
              placeholder="请输入博文标题"
              size="large"
            />
          </el-form-item>

          <el-form-item label="分类" required>
            <el-checkbox-group v-model="selectedCategoryIds">
              <el-checkbox
                v-for="category in categories"
                :key="category.categoryId"
                :label="category.categoryId"
                size="large"
              >
                {{ category.categoryName }}
              </el-checkbox>
            </el-checkbox-group>
          </el-form-item>

          <el-form-item label="内容" required>
            <div ref="vditorRef" class="vditor-container"></div>
          </el-form-item>

          <el-form-item label="概要" required>
            <el-input
              v-model="newArticle.summary"
              type="textarea"
              :rows="3"
              placeholder="请输入文章概要"
            />
          </el-form-item>

          <el-form-item label="封面">
            <div class="upload-section">
              <div class="upload-area" @click="triggerUpload">
                <input
                  type="file"
                  ref="uploadInput"
                  class="upload-input"
                  accept="image/*"
                  @change="handleImageUpload"
                />
                <div v-if="!newArticle.thumbnail" class="upload-placeholder">
                  <DocumentAdd class="upload-icon" />
                  <span>点击上传封面图片</span>
                </div>
                <img v-else :src="`/uploaded-images/${newArticle.thumbnail}`" class="upload-preview" />
              </div>
              <el-button
                v-if="newArticle.thumbnail"
                type="danger"
                text
                @click="removeImage"
              >
                移除图片
              </el-button>
            </div>
          </el-form-item>
        </el-form>
      </div>

      <template #footer>
        <el-button @click="closeModal">取消</el-button>
        <el-button type="primary" @click="submitNew">提交</el-button>
      </template>
    </el-dialog>
  </AppLayout>
</template>

<script setup>
import AppLayout from '@/components/layout/AppLayout.vue'
import { allStores } from '@/stores/index.js'
import axiosAPI from '@/utils/api/axios.js'
import { Calendar, ChatDotRound, DocumentAdd, Plus, Reading, Search, Star, View } from '@element-plus/icons-vue'
import { ElButton, ElCard, ElCheckbox, ElCheckboxGroup, ElDialog, ElEmpty, ElForm, ElFormItem, ElInput, ElMessage, ElSkeleton, ElTag } from 'element-plus'
import Vditor from 'vditor'
import 'vditor/dist/index.css'
import { nextTick, onMounted, onUnmounted, ref, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'

const route = useRoute()
const router = useRouter()
const articlesStore = allStores.useArticlesStore()

const keyword = ref('')
const articles = ref([])
const categories = ref([])
const loading = ref(true)
const selectedCategoryId = ref(null)
const selectedCategoryIds = ref([])
const showAddModal = ref(false)
const newArticle = ref({
  title: '',
  content: '',
  summary: '',
  thumbnail: ''
})

const vditorRef = ref(null)
const vditorInstance = ref(null)
const uploadInput = ref(null)

const getArticleThumbnail = (article) => {
  if (article.articleThumbnail) {
    return `/uploaded-images/${article.articleThumbnail}`
  } else {
    return new URL('@/assets/default/pics/1ad50b1598ddec725db64cabcf27ecc.jpg', import.meta.url).href
  }
}

const formatDate = (timeString) => {
  if (!timeString) return ''
  const date = new Date(timeString)
  const year = date.getFullYear()
  const month = String(date.getMonth() + 1).padStart(2, '0')
  const day = String(date.getDate()).padStart(2, '0')
  return `${year}-${month}-${day}`
}

const getAllArticles = () => {
  loading.value = true
  const cachedArticles = articlesStore.getArticles()
  
  if (cachedArticles.length > 0) {
    articles.value = cachedArticles
    loading.value = false
    return
  }
  
  axiosAPI.get('/user/articles/list')
    .then(response => {
      const res = response.data
      articles.value = (res.data && res.data.records) || []
    })
    .catch(error => {
      console.error('请求失败', error)
      articles.value = []
    })
    .finally(() => {
      loading.value = false
    })
}

const getCategories = () => {
  axiosAPI.get('/user/category/list')
    .then(response => {
      const result = response.data
      categories.value = (result.code === 1 && result.data.records) || []
    })
    .catch(error => {
      console.error('获取分类失败', error)
      categories.value = []
    })
}

const filterByCategory = (categoryId) => {
  selectedCategoryId.value = categoryId
  loading.value = true
  
  if (categoryId === null) {
    getAllArticles()
  } else {
    axiosAPI.get(`/user/category/articles?categoryId=${categoryId}&page=1&pageSize=10`)
      .then(response => {
        articles.value = (response.data.data && response.data.data.records) || []
      })
      .catch(error => {
        console.error('请求失败', error)
        articles.value = []
      })
      .finally(() => {
        loading.value = false
      })
  }
}

const search = () => {
  const keyWord = keyword.value.trim()
  if (!keyWord) {
    ElMessage.warning('请输入搜索关键字')
    return
  }
  
  loading.value = true
  axiosAPI.post('/user/articles/search', {
    articleTitle: keyWord,
    articleContent: keyWord,
    articleSummary: keyWord
  })
    .then(response => {
      articles.value = (response.data.code === 1 && response.data.data) || []
    })
    .catch(error => {
      console.error('搜索失败', error)
      articles.value = []
    })
    .finally(() => {
      loading.value = false
    })
}

const handleCategorySelect = (key) => {
  if (key === 'all') {
    filterByCategory(null)
  } else {
    filterByCategory(parseInt(key))
  }
}

const triggerUpload = () => {
  uploadInput.value?.click()
}

const removeImage = () => {
  newArticle.value.thumbnail = ''
  if (uploadInput.value) {
    uploadInput.value.value = ''
  }
}

const handleImageUpload = (event) => {
  const file = event.target.files[0]
  if (!file) return
  
  if (!file.type.startsWith('image/')) {
    ElMessage.error('请选择图片文件')
    return
  }
  
  if (file.size > 5 * 1024 * 1024) {
    ElMessage.error('图片大小不能超过5MB')
    return
  }
  
  const formData = new FormData()
  formData.append('file', file)
  
  axiosAPI.post('/images/upload', formData, {
    headers: { 'Content-Type': 'multipart/form-data' }
  })
    .then(response => {
      if (response.data.code === 1) {
        newArticle.value.thumbnail = response.data.data
        ElMessage.success('图片上传成功')
      } else {
        ElMessage.error('图片上传失败：' + (response.data.msg || '未知错误'))
      }
    })
    .catch(error => {
      console.error('图片上传失败', error)
      ElMessage.error('图片上传失败')
    })
}

const submitNew = async () => {
  if (!newArticle.value.title.trim()) {
    ElMessage.warning('请输入文章标题')
    return
  }
  
  if (vditorInstance.value) {
    newArticle.value.content = vditorInstance.value.getValue()
  }
  
  try {
    const insertRes = await axiosAPI.post('/user/articles', {
      articleTitle: newArticle.value.title,
      articleContent: newArticle.value.content,
      articleSummary: newArticle.value.summary,
      articleThumbnail: newArticle.value.thumbnail,
      categoryIds: selectedCategoryIds.value
    })
    
    if (insertRes.data.code === 1) {
      ElMessage.success('文章添加成功')
      getAllArticles()
      closeModal()
    } else {
      ElMessage.error('文章添加失败：' + (insertRes.data.msg || '未知错误'))
    }
  } catch (error) {
    console.error('请求失败', error)
    ElMessage.error('请求失败')
  }
}

const goToArticle = (id) => {
  router.push(`/articles/${id}`)
}

const initVditor = () => {
  nextTick(() => {
    if (vditorRef.value && !vditorInstance.value) {
      vditorInstance.value = new Vditor(vditorRef.value, {
        mode: 'sv',
        preview: { show: true, theme: 'light' },
        toolbar: [
          'emoji', 'headings', 'bold', 'italic', 'strike', 'link',
          'image', 'list', 'table', 'code', 'line', 'fullscreen'
        ],
        height: 400,
        cache: { enable: false },
        blur: () => {
          if (vditorInstance.value) {
            newArticle.value.content = vditorInstance.value.getValue()
          }
        }
      })
    }
  })
}

const destroyVditor = () => {
  if (vditorInstance.value) {
    vditorInstance.value.destroy()
    vditorInstance.value = null
  }
}

const closeModal = () => {
  showAddModal.value = false
  newArticle.value = { title: '', content: '', summary: '', thumbnail: '' }
  selectedCategoryIds.value = []
  destroyVditor()
}

onMounted(() => {
  const urlParams = new URLSearchParams(window.location.search)
  const categoryId = urlParams.get('category')
  
  Promise.all([
    getCategories(),
    axiosAPI.get('/home/currentUser', { credentials: 'include' })
  ])
    .then(([_, userRes]) => {
      if (categoryId) {
        filterByCategory(categoryId)
      } else {
        getAllArticles()
      }
    })
    .catch(error => {
      console.error('初始化失败', error)
      getAllArticles()
    })
  
  watch(showAddModal, (newVal) => {
    if (newVal) {
      initVditor()
    } else {
      destroyVditor()
    }
  })
})

onUnmounted(() => {
  destroyVditor()
})
</script>

<style scoped>
.articles-page {
  display: flex;
  flex-direction: column;
  gap: var(--spacing-xl);
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.page-title {
  display: flex;
  align-items: center;
  gap: var(--spacing-sm);
}

.title-icon {
  width: 32px;
  height: 32px;
  color: var(--primary-500);
}

.page-title h1 {
  font-size: var(--text-3xl);
  font-weight: var(--font-bold);
  color: var(--text-primary);
  margin: 0;
}

.add-btn {
  display: flex;
  align-items: center;
  gap: var(--spacing-xs);
  background: linear-gradient(135deg, var(--primary-500) 0%, var(--primary-600) 100%);
  border-radius: var(--radius-lg);
}

.add-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(249, 115, 22, 0.4);
}

.btn-icon {
  margin-right: var(--spacing-xs);
}

.search-bar {
  display: flex;
  gap: var(--spacing-md);
}

.search-input {
  flex: 1;
  border-radius: var(--radius-lg);
}

:deep(.search-input .el-input__wrapper) {
  border-radius: var(--radius-lg);
}

.search-btn {
  border-radius: var(--radius-lg);
}

.loading-state,
.empty-state {
  background: var(--bg-card);
  border-radius: var(--radius-lg);
  padding: var(--spacing-xl);
  border: 1px solid var(--border-color);
}

.articles-list {
  display: flex;
  flex-direction: column;
  gap: var(--spacing-lg);
}

.article-card {
  cursor: pointer;
  transition: all var(--transition-normal);
  animation: fade-in 0.4s ease-out forwards;
  opacity: 0;
}

.article-card:hover {
  transform: translateY(-4px);
  box-shadow: var(--shadow-card-hover);
}

.article-content {
  display: flex;
  gap: var(--spacing-xl);
}

.article-thumbnail {
  width: 280px;
  height: 180px;
  border-radius: var(--radius-lg);
  overflow: hidden;
  flex-shrink: 0;
}

.article-thumbnail img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform var(--transition-normal);
}

.article-card:hover .article-thumbnail img {
  transform: scale(1.05);
}

.article-info {
  flex: 1;
  display: flex;
  flex-direction: column;
}

.article-title {
  font-size: var(--text-xl);
  font-weight: var(--font-bold);
  color: var(--text-primary);
  margin: 0 0 var(--spacing-md);
}

.article-meta {
  display: flex;
  gap: var(--spacing-lg);
  margin-bottom: var(--spacing-md);
}

.meta-item {
  display: flex;
  align-items: center;
  gap: var(--spacing-xs);
  font-size: var(--text-sm);
  color: var(--text-tertiary);
}

.meta-icon {
  width: 16px;
  height: 16px;
}

.article-summary {
  font-size: var(--text-base);
  color: var(--text-secondary);
  line-height: var(--leading-relaxed);
  margin-bottom: var(--spacing-lg);
  flex: 1;
}

.article-tags {
  display: flex;
  flex-wrap: wrap;
  gap: var(--spacing-sm);
}

.add-article-dialog {
  border-radius: var(--radius-xl);
}

:deep(.add-article-dialog .el-dialog__header) {
  background: linear-gradient(135deg, var(--primary-50) 0%, var(--secondary-50) 100%);
  border-radius: var(--radius-xl) var(--radius-xl) 0 0;
}

:deep(.add-article-dialog .el-dialog__title) {
  font-weight: var(--font-semibold);
}

.article-form {
  max-height: 60vh;
  overflow-y: auto;
}

.upload-section {
  display: flex;
  flex-direction: column;
  gap: var(--spacing-md);
}

.upload-area {
  position: relative;
  width: 200px;
  height: 150px;
  border: 2px dashed var(--border-color);
  border-radius: var(--radius-lg);
  cursor: pointer;
  overflow: hidden;
  transition: all var(--transition-fast);
}

.upload-area:hover {
  border-color: var(--primary-400);
  background: rgba(249, 115, 22, 0.05);
}

.upload-input {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  opacity: 0;
  cursor: pointer;
}

.upload-placeholder {
  width: 100%;
  height: 100%;
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  gap: var(--spacing-sm);
  color: var(--text-muted);
}

.upload-icon {
  width: 32px;
  height: 32px;
}

.upload-preview {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.vditor-container {
  border: 1px solid var(--border-color);
  border-radius: var(--radius-lg);
  overflow: hidden;
  min-height: 300px;
}

@media (max-width: 768px) {
  .page-header {
    flex-direction: column;
    gap: var(--spacing-md);
    align-items: flex-start;
  }
  
  .page-title h1 {
    font-size: var(--text-2xl);
  }
  
  .search-bar {
    flex-direction: column;
  }
  
  .article-content {
    flex-direction: column;
  }
  
  .article-thumbnail {
    width: 100%;
    height: 180px;
  }
  
  .article-meta {
    flex-wrap: wrap;
    gap: var(--spacing-md);
  }
}

@media (max-width: 480px) {
  .page-title h1 {
    font-size: var(--text-xl);
  }
  
  .article-thumbnail {
    height: 150px;
  }
}
</style>