<template>
  <AppLayout>
    <div class="article-page">
      <router-link to="/articles" class="back-link">
        ←  返回文章列表
      </router-link>

      <div v-if="loading" class="loading-state">
        <el-skeleton :rows="8" animated />
      </div>

      <div v-else-if="!article" class="empty-state">
        <el-empty description="文章不存在或已被删除" />
      </div>

      <div v-else class="article-content">
        <header class="article-header">
          <h1 class="article-title">{{ article.articleTitle }}</h1>

          <div class="article-meta">
            <span class="meta-item">
              <User class="meta-icon" />
              {{ article.userNickname }}
            </span>
            <span class="meta-divider">|</span>
            <span class="meta-item">
              <Calendar class="meta-icon" />
              {{ formatDate(article.articleCreateTime) }}
            </span>
            <span class="meta-divider">|</span>
            <span class="meta-item">
              <View class="meta-icon" />
              {{ article.articleReadCount || 0 }}
            </span>
            <span class="meta-divider">|</span>
            <span class="meta-item">
              <ChatDotRound class="meta-icon" />
              {{ article.articleCommentCount || 0 }}
            </span>
          </div>

          <!-- 标签展示区域  -->
          <div class="article-tags">
            <el-tag v-for="category in article.categoryList" :key="category.categoryId" size="small" type="primary"
              effect="plain" @click="goToCategory(category.categoryId)">
              {{ category.categoryName }}
            </el-tag>
            <el-tag v-for="tag in article.tagList" :key="tag.tagId" size="small" type="info" effect="plain">
              {{ tag.tagName }}
            </el-tag>
          </div>

        </header>

        <div v-if="article.articleThumbnail" class="article-cover">
          <img :src="`/uploaded-images/${article.articleThumbnail}`" :alt="article.articleTitle" />
        </div>


        <main class="article-body" v-html="articleHtml"></main>

        <!--  编辑和删除按钮  -->
        <div class="actions">
          <button class="btn btn-info" @click="openEditView(article)">
            <EditPen class="btn-icon" /> 编辑文章
          </button>

          <button class="btn btn-delete" @click="deleteArticle(article.articleId)">
            <Delete class="btn-icon" /> 删除文章
          </button>
        </div>

        <footer class="article-footer">
          <div class="like-section">
            <button class="like-btn" :class="{ liked: isLiked }" @click="handleLike">
              <Star class="like-icon" />
              <span class="like-count">{{ article.articleLikeCount || 0 }}</span>
            </button>
          </div>
        </footer>

        <!--  评论区域 -->
        <section class="comment-section">
          <div class="section-header">
            <div class="section-title">
              <ChatDotRound class="section-icon" />
              <h2>评论 ({{ comments.length }})</h2>
            </div>
          </div>

          <div v-if="comments.length === 0" class="empty-comments">
            <el-empty description="暂无评论，快来发表第一条评论吧！" />
          </div>

          <!-- 评论列表 -->
          <div v-else class="comments-list">
            <div v-for="comment in comments" :key="comment.commentId" class="comment-item">
              <div class="comment-avatar">
                <el-avatar :size="48" :src="comment.userAvatar">
                  <User />
                </el-avatar>
              </div>
              <div class="comment-content">
                <div class="comment-header">
                  <span class="comment-author">{{ comment.userNickname }}</span>
                  <span class="comment-time">{{ formatDate(comment.commentCreateTime) }}</span>
                </div>
                <p class="comment-text">{{ comment.commentContent }}</p>
              </div>
            </div>
          </div>

          <!--  新增评论表单  -->
          <div class="comment-form">
            <el-form :model="commentForm">
              <el-form-item>
                <el-input v-model="commentForm.content" type="textarea" :rows="3" placeholder="写下你的评论..."
                  class="comment-input" />
              </el-form-item>
              <el-form-item class="form-actions">
                <el-button type="primary" class="submit-btn" @click="submitComment" :loading="submitting">
                  发表评论
                </el-button>
              </el-form-item>
            </el-form>
          </div>

        </section>

      </div>
    </div>
  </AppLayout>
</template>

<script setup>
import AppLayout from '@/components/layout/AppLayout.vue'
import axiosAPI from '@/utils/api/axios.js'
import { Calendar, ChatDotRound, Star, User, View } from '@element-plus/icons-vue'
import { ElAvatar, ElButton, ElEmpty, ElForm, ElFormItem, ElInput, ElSkeleton, ElTag } from 'element-plus'
import { marked } from 'marked'
import { computed, onMounted, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'

const route = useRoute()
const router = useRouter()

const article = ref(null)
const comments = ref([])
const loading = ref(true)
const isLiked = ref(false)
const submitting = ref(false)

const commentForm = ref({
  content: ''
})

const articleHtml = computed(() => {
  if (!article.value?.articleContent) return ''
  return marked.parse(article.value.articleContent)
})

const formatDate = (timeString) => {
  if (!timeString) return ''
  const date = new Date(timeString)
  const year = date.getFullYear()
  const month = String(date.getMonth() + 1).padStart(2, '0')
  const day = String(date.getDate()).padStart(2, '0')
  return `${year}-${month}-${day}`
}

const goToCategory = (categoryId) => {
  router.push(`/articles?category=${categoryId}`)
}

const getArticle = async () => {
  loading.value = true
  const articleId = route.params.id

  try {
    const response = await axiosAPI.get(`/user/articles/${articleId}`)
    const res = response.data

    if (res.code === 1 && res.data) {
      article.value = res.data
    } else {
      article.value = null
    }
  } catch (error) {
    console.error('获取文章详情失败', error)
    article.value = null
  } finally {
    loading.value = false
  }
}

//跳转至编辑文章页面
const openEditView = (article) => {
  if (!article) return
  router.push({ name: 'edit-article', params: { id: article.articleId } })
}

//删除文字
const deleteArticle = async (articleId) => {
  if (confirm('确定要删除这篇文章吗？')) {
    try {
      const response = await axiosAPI.delete(`/user/articles/${articleId}`)
      const res = response.data

      if (res.code === 1) {
        article.value = null
        router.push('/articles')
      }
    } catch (error) {
      console.error('删除文章失败', error)
    }
  }


}

const getComments = async () => {
  const articleId = route.params.id

  try {
    const response = await axiosAPI.get(`/user/comments/article/${articleId}/comments`)
    const res = response.data

    if (res.code === 1 && res.data) {
      comments.value = res.data.records || []
    } else {
      comments.value = []
    }
  } catch (error) {
    console.error('获取评论失败', error)
    comments.value = []
  }
}

const handleLike = async () => {
  if (!article.value) return

  try {
    const response = await axiosAPI.post(`/articles/detail/${article.value.articleId}/like`)
    const res = response.data

    if (res.code === 1) {
      isLiked.value = !isLiked.value
      if (isLiked.value) {
        article.value.articleLikeCount = (article.value.articleLikeCount || 0) + 1
      } else {
        article.value.articleLikeCount = Math.max(0, (article.value.articleLikeCount || 0) - 1)
      }
    }
  } catch (error) {
    console.error('点赞失败', error)
  }
}

const submitComment = async () => {

  if (!commentForm.value.content.trim()) {
    return
  }

  submitting.value = true
  /* 
  /articles/detail/${article.value.articleId}/comments
  */
  try {
    const response = await axiosAPI.post(`/user/comments`, {
      content: commentForm.value.content,
      articleId: article.value.articleId,
      parentId: null
    })

    const res = response.data

    if (res.code === 1) {
      commentForm.value.content = ''
      await getComments()
    }
  } catch (error) {
    console.error('提交评论失败', error)
  } finally {
    submitting.value = false
  }
}

onMounted(async () => {
  await getArticle()
  await getComments()
})
</script>

<style scoped>
.article-page {
  display: flex;
  flex-direction: column;
  gap: var(--spacing-xl);
}

.loading-state {
  background: var(--bg-card);
  border-radius: var(--radius-lg);
  padding: var(--spacing-xl);
  border: 1px solid var(--border-color);
}

.empty-state {
  background: var(--bg-card);
  border-radius: var(--radius-lg);
  padding: var(--spacing-xl);
  border: 1px solid var(--border-color);
}

.article-content {
  display: flex;
  flex-direction: column;
  gap: var(--spacing-xl);
}

.article-header {
  background: var(--bg-card);
  border-radius: var(--radius-xl);
  padding: var(--spacing-2xl);
  border: 1px solid var(--border-color);
}

.article-title {
  font-size: var(--text-3xl);
  font-weight: var(--font-bold);
  color: var(--text-primary);
  margin: 0 0 var(--spacing-lg);
  line-height: var(--leading-tight);
}

.article-meta {
  display: flex;
  align-items: center;
  gap: var(--spacing-md);
  margin-bottom: var(--spacing-lg);
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

.meta-divider {
  color: var(--border-color);
}

.article-tags {
  display: flex;
  flex-wrap: wrap;
  gap: var(--spacing-sm);
}

.article-cover {
  width: 100%;
  height: 350px;
  border-radius: var(--radius-xl);
  overflow: hidden;
}

.article-cover img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.article-body {
  background: var(--bg-card);
  border-radius: var(--radius-xl);
  padding: var(--spacing-2xl);
  border: 1px solid var(--border-color);
  line-height: var(--leading-loose);
}

.article-body :deep(h1),
.article-body :deep(h2),
.article-body :deep(h3),
.article-body :deep(h4),
.article-body :deep(h5),
.article-body :deep(h6) {
  font-weight: var(--font-bold);
  color: var(--text-primary);
  margin-top: var(--spacing-xl);
  margin-bottom: var(--spacing-md);
}

.article-body :deep(h1) {
  font-size: var(--text-2xl);
}

.article-body :deep(h2) {
  font-size: var(--text-xl);
}

.article-body :deep(h3) {
  font-size: var(--text-lg);
}

.article-body :deep(p) {
  margin-bottom: var(--spacing-lg);
  color: var(--text-secondary);
}

.article-body :deep(code) {
  background: rgba(249, 115, 22, 0.1);
  color: var(--primary-600);
  padding: var(--spacing-xs) var(--spacing-sm);
  border-radius: var(--radius-sm);
  font-family: 'Fira Code', monospace;
}

.article-body :deep(pre) {
  background: var(--bg-primary);
  padding: var(--spacing-lg);
  border-radius: var(--radius-lg);
  overflow-x: auto;
  margin: var(--spacing-lg) 0;
  border: 1px solid var(--border-color);
}

.article-body :deep(pre code) {
  background: none;
  padding: 0;
}

.article-body :deep(blockquote) {
  border-left: 4px solid var(--primary-500);
  padding-left: var(--spacing-lg);
  margin: var(--spacing-lg) 0;
  color: var(--text-tertiary);
  font-style: italic;
}

.article-body :deep(img) {
  max-width: 100%;
  border-radius: var(--radius-lg);
  margin: var(--spacing-md) 0;
}

.article-body :deep(ul),
.article-body :deep(ol) {
  margin: var(--spacing-lg) 0;
  padding-left: var(--spacing-xl);
}

.article-body :deep(li) {
  margin-bottom: var(--spacing-sm);
}

.article-footer {
  display: flex;
  justify-content: center;
  padding: var(--spacing-xl);
}

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
  display: inline-flex;
  align-items: center;
  gap: 6px;
}

.btn-icon {
  width: 16px;
  height: 16px;
}

.btn-info {
  background-color: var(--accent-color);
  color: white;
}

.btn-info:hover {
  background-color: #388E3C;
  transform: translateY(-1px);
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.15);
}

.btn-delete {
  background-color: var(--bg-secondary);
  color: var(--text-primary);
  border: 1px solid var(--border-color);
}

.btn-delete:hover {
  background-color: var(--bg-primary);
  transform: translateY(-1px);
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
}



.like-section {
  display: flex;
  justify-content: center;
}

.like-btn {
  display: flex;
  align-items: center;
  gap: var(--spacing-sm);
  padding: var(--spacing-md) var(--spacing-xl);
  border: 2px solid var(--primary-300);
  border-radius: var(--radius-full);
  background: transparent;
  color: var(--primary-500);
  font-weight: var(--font-medium);
  cursor: pointer;
  transition: all var(--transition-normal);
}

.like-btn:hover {
  background: rgba(249, 115, 22, 0.1);
  transform: translateY(-2px);
}

.like-btn.liked {
  background: rgba(249, 115, 22, 0.1);
  border-color: var(--primary-500);
}

.like-btn.liked .like-icon {
  color: #ef4444;
}

.like-icon {
  width: 20px;
  height: 20px;
  transition: color var(--transition-fast);
}

.comment-section {
  background: var(--bg-card);
  border-radius: var(--radius-xl);
  padding: var(--spacing-2xl);
  border: 1px solid var(--border-color);
}

.section-header {
  margin-bottom: var(--spacing-xl);
}

.section-title {
  display: flex;
  align-items: center;
  gap: var(--spacing-sm);
}

.section-icon {
  width: 24px;
  height: 24px;
  color: var(--primary-500);
}

.section-title h2 {
  font-size: var(--text-xl);
  font-weight: var(--font-bold);
  color: var(--text-primary);
  margin: 0;
}

.empty-comments {
  padding: var(--spacing-xl);
}

.comments-list {
  display: flex;
  flex-direction: column;
  gap: var(--spacing-lg);
  margin-bottom: var(--spacing-xl);
}

.comment-item {
  display: flex;
  gap: var(--spacing-lg);
  padding: var(--spacing-lg);
  background: var(--bg-primary);
  border-radius: var(--radius-lg);
}

.comment-avatar {
  flex-shrink: 0;
}

.comment-content {
  flex: 1;
}

.comment-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: var(--spacing-sm);
}

.comment-author {
  font-weight: var(--font-semibold);
  color: var(--text-primary);
}

.comment-time {
  font-size: var(--text-xs);
  color: var(--text-muted);
}

.comment-text {
  font-size: var(--text-base);
  color: var(--text-secondary);
  line-height: var(--leading-relaxed);
  margin: 0;
}

.comment-form {
  border-top: 1px solid var(--border-color);
  padding-top: var(--spacing-xl);
}

.comment-input {
  border-radius: var(--radius-lg);
}

:deep(.comment-input .el-input__wrapper) {
  border-radius: var(--radius-lg);
}

.form-actions {
  margin-bottom: 0;
  display: flex;
  justify-content: flex-end;
}

.submit-btn {
  border-radius: var(--radius-lg);
}

@media (max-width: 768px) {
  .article-header {
    padding: var(--spacing-xl);
  }

  .article-title {
    font-size: var(--text-2xl);
  }

  .article-meta {
    flex-wrap: wrap;
    gap: var(--spacing-sm);
  }

  .article-cover {
    height: 200px;
  }

  .article-body {
    padding: var(--spacing-xl);
  }

  .comment-item {
    flex-direction: column;
    gap: var(--spacing-md);
  }
}

@media (max-width: 480px) {
  .article-title {
    font-size: var(--text-xl);
  }

  .article-cover {
    height: 150px;
  }

  .article-body {
    padding: var(--spacing-lg);
  }
}
</style>