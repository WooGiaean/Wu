<template>
  <AppLayout>
    <div class="home-page">
      <section class="hero-section">
        <div class="hero-content">
          <h1 class="hero-title">
            <span class="gradient-text">欢迎来到我的博客</span>
          </h1>
          <p class="hero-subtitle">记录生活，分享知识，追逐梦想</p>
          <div class="hero-stats">
            <div class="stat-item">
              <div class="stat-number">{{ blogCount }}</div>
              <div class="stat-label">文章</div>
            </div>
            <div class="stat-divider"></div>
            <div class="stat-item">
              <div class="stat-number">{{ noteCount }}</div>
              <div class="stat-label">笔记</div>
            </div>
            <div class="stat-divider"></div>
            <div class="stat-item">
              <div class="stat-number">{{ userInfo?.userNickname || '访客' }}</div>
              <div class="stat-label">作者</div>
            </div>
          </div>
        </div>
      </section>

      <section class="notes-section">
        <div class="section-header">
          <div class="section-title">
            <EditPen class="section-icon" />
            <h2>我的笔记</h2>
          </div>
          <el-button type="text" class="view-all-btn" @click="$router.push('/notes')">
            查看全部
            <ArrowRight class="arrow-icon" />
          </el-button>
        </div>
        
        <div v-if="loading" class="loading-state">
          <el-skeleton :rows="5" animated />
        </div>
        
        <div v-else-if="notes.length === 0" class="empty-state">
          <el-empty description="暂无笔记，快去写一篇吧！" />
        </div>
        
        <div v-else class="notes-grid">
          <el-card
            v-for="(note, index) in notes"
            :key="note.noteId"
            class="note-card"
            @click="goToNote(note.noteId)"
            :style="{ animationDelay: `${index * 0.1}s` }"
          >
            <template #header>
              <div class="note-header">
                <h3 class="note-title">{{ note.noteTopic }}</h3>
                <span class="note-date">{{ formatDate(note.noteCreateTime) }}</span>
              </div>
            </template>
            <p class="note-content">{{ note.noteContent ? note.noteContent.substring(0, 120) + '...' : '无内容' }}</p>
            <div class="note-footer">
              <el-tag size="small" type="primary" effect="plain">笔记</el-tag>
            </div>
          </el-card>
        </div>
      </section>

      <section class="blogs-section">
        <div class="section-header">
          <div class="section-title">
            <HotWater class="section-icon" />
            <h2>最近博客</h2>
          </div>
          <el-button type="text" class="view-all-btn" @click="$router.push('/articles')">
            查看全部
            <ArrowRight class="arrow-icon" />
          </el-button>
        </div>
        
        <div v-if="loading" class="loading-state">
          <el-skeleton :rows="5" animated />
        </div>
        
        <div v-else-if="blogs.length === 0" class="empty-state">
          <el-empty description="暂无文章，敬请期待！" />
        </div>
        
        <div v-else class="blogs-list">
          <el-card
            v-for="(blog, index) in blogs"
            :key="blog.articleId"
            class="blog-card"
            @click="goToBlog(blog.articleId)"
            :style="{ animationDelay: `${index * 0.1}s` }"
          >
            <div class="blog-content">
              <div class="blog-header">
                <h3 class="blog-title">{{ blog.articleTitle }}</h3>
                <span class="blog-date">{{ formatDate(blog.articleCreateTime) }}</span>
              </div>
              
              <div class="blog-meta">
                <span class="meta-item">
                  <View class="meta-icon" />
                  {{ blog.articleReadCount || 0 }}
                </span>
                <span class="meta-item">
                  <ChatDotRound class="meta-icon" />
                  {{ blog.articleCommentCount || 0 }}
                </span>
                <span class="meta-item">
                  <Star class="meta-icon" />
                  {{ blog.articleLikeCount || 0 }}
                </span>
              </div>
              
              <p class="blog-summary">{{ blog.articleSummary || '暂无摘要' }}</p>
              
              <div v-if="blog.articleThumbnail" class="blog-thumbnail">
                <img :src="`/uploaded-images/${blog.articleThumbnail}`" :alt="blog.articleTitle" />
              </div>
              
              <div class="blog-footer">
                <el-tag
                  v-for="category in blog.categoryList?.slice(0, 3)"
                  :key="category.categoryId"
                  size="small"
                  type="success"
                  effect="plain"
                >
                  {{ category.categoryName }}
                </el-tag>
              </div>
            </div>
          </el-card>
        </div>
      </section>
    </div>
  </AppLayout>
</template>

<script setup>
import AppLayout from '@/components/layout/AppLayout.vue'
import { allStores } from '@/stores/index.js'
import axiosAPI from '@/utils/api/axios.js'
import {
  ArrowRight,
  ChatDotRound,
  EditPen,
  HotWater,
  Star,
  View
} from '@element-plus/icons-vue'
import { ElButton, ElCard, ElEmpty, ElSkeleton, ElTag } from 'element-plus'
import { computed, onMounted, ref } from 'vue'
import { useRouter } from 'vue-router'

const router = useRouter()
const userStore = allStores.useUserInfoStore()
const articlesStore = allStores.useArticlesStore()
const notesStore = allStores.useNotesStore()

const blogs = ref([])
const notes = ref([])
const userInfo = ref({})
const loading = ref(true)

const blogCount = computed(() => blogs.value.length)
const noteCount = computed(() => notes.value.length)

const formatDate = (timeString) => {
  if (!timeString) return ''
  const date = new Date(timeString)
  const year = date.getFullYear()
  const month = String(date.getMonth() + 1).padStart(2, '0')
  const day = String(date.getDate()).padStart(2, '0')
  return `${year}-${month}-${day}`
}

const goToNote = async (id) => {
  await router.push(`/notes/${id}`)
}

const goToBlog = async (id) => {
  await router.push(`/articles/${id}`)
}

const loadArticles = async () => {
  const cachedLatest = articlesStore.getLatestArticles()
  if (cachedLatest.length > 0) {
    blogs.value = cachedLatest
    return
  }
  
  const articlesRes = await axiosAPI.get('/home/latestArticles', { credentials: 'include' })
  if (articlesRes.data.data && articlesRes.data.data.records) {
    blogs.value = articlesRes.data.data.records
    articlesStore.setLatestArticles(articlesRes.data.data.records)
  }
}

const loadNotes = async () => {
  const cachedLatest = notesStore.getLatestNotes()
  if (cachedLatest.length > 0) {
    notes.value = cachedLatest
    return
  }
  
  const notesRes = await axiosAPI.get('/home/latestNotes', { credentials: 'include' })
  if (notesRes.data.data && notesRes.data.data.records) {
    notes.value = notesRes.data.data.records
    notesStore.setLatestNotes(notesRes.data.data.records)
  }
}

onMounted(async () => {
  try {
    const tempUserInfo = userStore.getUserInfo()
    if (tempUserInfo) {
      userInfo.value = tempUserInfo
    } else {
      const userRes = await axiosAPI.get('/home/currentUser', { credentials: 'include' })
      if (userRes.data.data) {
        userInfo.value = userRes.data.data
      }
    }
    
    await Promise.all([loadArticles(), loadNotes()])
  } catch (error) {
    console.error('页面初始化数据请求失败', error)
  } finally {
    loading.value = false
  }
})
</script>

<style scoped>
.home-page {
  display: flex;
  flex-direction: column;
  gap: var(--spacing-2xl);
}

.hero-section {
  background: linear-gradient(135deg, rgba(249, 115, 22, 0.1) 0%, rgba(168, 85, 247, 0.1) 100%);
  border-radius: var(--radius-2xl);
  padding: var(--spacing-3xl);
  text-align: center;
  position: relative;
  overflow: hidden;
}

.hero-section::before {
  content: '';
  position: absolute;
  top: -50%;
  left: -50%;
  width: 200%;
  height: 200%;
  background: radial-gradient(circle at 30% 70%, rgba(249, 115, 22, 0.15) 0%, transparent 50%),
              radial-gradient(circle at 70% 30%, rgba(168, 85, 247, 0.15) 0%, transparent 50%);
  animation: float 10s ease-in-out infinite;
}

@keyframes float {
  0%, 100% { transform: translate(0, 0); }
  50% { transform: translate(20px, 20px); }
}

.hero-content {
  position: relative;
  z-index: 1;
}

.hero-title {
  font-size: var(--text-4xl);
  font-weight: var(--font-extrabold);
  margin: 0 0 var(--spacing-md);
}

.hero-subtitle {
  font-size: var(--text-lg);
  color: var(--text-tertiary);
  margin: 0 0 var(--spacing-xl);
}

.hero-stats {
  display: flex;
  justify-content: center;
  align-items: center;
  gap: var(--spacing-xl);
}

.stat-item {
  display: flex;
  flex-direction: column;
  align-items: center;
}

.stat-number {
  font-size: var(--text-3xl);
  font-weight: var(--font-extrabold);
  color: var(--primary-500);
}

.stat-label {
  font-size: var(--text-sm);
  color: var(--text-tertiary);
  margin-top: var(--spacing-xs);
}

.stat-divider {
  width: 1px;
  height: 40px;
  background: var(--border-color);
}

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: var(--spacing-xl);
}

.section-title {
  display: flex;
  align-items: center;
  gap: var(--spacing-sm);
}

.section-icon {
  width: 28px;
  height: 28px;
  color: var(--primary-500);
}

.section-title h2 {
  font-size: var(--text-2xl);
  font-weight: var(--font-bold);
  color: var(--text-primary);
  margin: 0;
}

.view-all-btn {
  display: flex;
  align-items: center;
  gap: var(--spacing-xs);
  color: var(--primary-500);
  font-weight: var(--font-medium);
}

.arrow-icon {
  width: 16px;
  height: 16px;
  transition: transform var(--transition-fast);
}

.view-all-btn:hover .arrow-icon {
  transform: translateX(4px);
}

.loading-state,
.empty-state {
  background: var(--bg-card);
  border-radius: var(--radius-lg);
  padding: var(--spacing-xl);
  border: 1px solid var(--border-color);
}

.notes-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(320px, 1fr));
  gap: var(--spacing-lg);
}

.note-card {
  cursor: pointer;
  transition: all var(--transition-normal);
  animation: fade-in 0.4s ease-out forwards;
  opacity: 0;
}

.note-card:hover {
  transform: translateY(-6px);
  box-shadow: var(--shadow-card-hover);
}

.note-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
}

.note-title {
  font-size: var(--text-lg);
  font-weight: var(--font-semibold);
  color: var(--text-primary);
  margin: 0;
}

.note-date {
  font-size: var(--text-xs);
  color: var(--text-muted);
}

.note-content {
  font-size: var(--text-sm);
  color: var(--text-secondary);
  line-height: var(--leading-relaxed);
  margin: var(--spacing-md) 0;
  display: -webkit-box;
  -webkit-line-clamp: 3;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.note-footer {
  margin-top: var(--spacing-md);
}

.blogs-list {
  display: flex;
  flex-direction: column;
  gap: var(--spacing-lg);
}

.blog-card {
  cursor: pointer;
  transition: all var(--transition-normal);
  animation: fade-in 0.4s ease-out forwards;
  opacity: 0;
}

.blog-card:hover {
  transform: translateY(-4px);
  box-shadow: var(--shadow-card-hover);
}

.blog-content {
  display: flex;
  flex-direction: column;
}

.blog-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: var(--spacing-md);
}

.blog-title {
  font-size: var(--text-xl);
  font-weight: var(--font-bold);
  color: var(--text-primary);
  margin: 0;
  flex: 1;
  margin-right: var(--spacing-md);
}

.blog-date {
  font-size: var(--text-xs);
  color: var(--text-muted);
  white-space: nowrap;
}

.blog-meta {
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

.blog-summary {
  font-size: var(--text-base);
  color: var(--text-secondary);
  line-height: var(--leading-relaxed);
  margin-bottom: var(--spacing-md);
}

.blog-thumbnail {
  width: 100%;
  height: 200px;
  border-radius: var(--radius-lg);
  overflow: hidden;
  margin-bottom: var(--spacing-md);
}

.blog-thumbnail img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform var(--transition-normal);
}

.blog-card:hover .blog-thumbnail img {
  transform: scale(1.05);
}

.blog-footer {
  display: flex;
  gap: var(--spacing-sm);
}

@media (max-width: 768px) {
  .hero-section {
    padding: var(--spacing-xl);
  }
  
  .hero-title {
    font-size: var(--text-3xl);
  }
  
  .hero-stats {
    gap: var(--spacing-lg);
  }
  
  .stat-number {
    font-size: var(--text-2xl);
  }
  
  .notes-grid {
    grid-template-columns: 1fr;
  }
  
  .blog-header {
    flex-direction: column;
    gap: var(--spacing-sm);
  }
  
  .blog-title {
    margin-right: 0;
  }
  
  .blog-meta {
    flex-wrap: wrap;
    gap: var(--spacing-md);
  }
  
  .blog-thumbnail {
    height: 150px;
  }
}

@media (max-width: 480px) {
  .hero-section {
    padding: var(--spacing-lg);
  }
  
  .hero-title {
    font-size: var(--text-2xl);
  }
  
  .hero-subtitle {
    font-size: var(--text-base);
  }
  
  .hero-stats {
    flex-direction: column;
    gap: var(--spacing-md);
  }
  
  .stat-divider {
    width: 40px;
    height: 1px;
  }
}
</style>