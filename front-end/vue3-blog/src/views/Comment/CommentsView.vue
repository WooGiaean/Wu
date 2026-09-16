<script setup>
import { ref, onMounted } from 'vue'
import router from '@/router/index.js'
import axiosAPI from '@/utils/api/axios.js'
import AppLayout from '@/components/layout/AppLayout.vue'
import { ElSkeleton, ElEmpty } from 'element-plus'
import { ChatDotRound, Document } from '@element-plus/icons-vue'

// 数据状态
const comments = ref([])
const loading = ref(true)
const currentPage = ref(1)
const pageSize = ref(10)
const totalPages = ref(1)

// 格式化日期函数
const formatDate = (timeString) => {
  if(!timeString) return ''
  const date = new Date(timeString)
  const year = date.getFullYear()
  const month = String(date.getMonth() + 1).padStart(2, '0')
  const day = String(date.getDate()).padStart(2, '0')
  return `${year}-${month}-${day}`
}

// 跳转到文章详情
const goToArticle = (articleId) => {
  console.log("跳转到文章详情 {}",articleId)
  router.push(`/articles/${articleId}`)
}

// 获取评论列表
const getComments = () => {
  loading.value = true

  axiosAPI.get(`/user/comments?page=${currentPage.value}&pageSize=${pageSize.value}`)
    .then(response => {
      if (response.data.code === 1 && response.data.data) {
        comments.value = response.data.data.records || []
        totalPages.value = Math.ceil(response.data.data.total / pageSize.value)
      } else {
        comments.value = []
        totalPages.value = 1
      }
    })
    .catch(error => {
      console.error('获取评论列表失败:', error)
      comments.value = []
    })
    .finally(() => {
      loading.value = false
    })
}

// 页面加载时获取数据
onMounted(() => {
  getComments()
})
</script>

<template>
  <AppLayout>
    <!-- 页面标题 -->
    <div class="page-header">
      <h2><ChatDotRound class="header-icon" /> 我的评论</h2>
      <p>查看和管理我的所有评论</p>
    </div>

    <!-- 评论列表 -->
    <div v-if="loading" class="loading">
      <el-skeleton rows="5" animated></el-skeleton>
    </div>
    <div v-else-if="comments.length === 0" class="empty-state">
      <el-empty description="暂无评论，快去发表评论吧！"></el-empty>
    </div>
    <div v-else class="comments-list">
      <div class="comment-card" v-for="comment in comments" :key="comment.commentId"
           @click="goToArticle(comment.articleId)">
        <!-- 评论内容 -->
        <div class="comment-content">
          <p>{{ comment.content }}</p>
        </div>

        <!-- 评论元信息 -->
        <div class="comment-meta">
          <span class="article-link"><Document class="meta-icon" /> {{ comment.articleTitle }}</span>
          <span class="comment-time">{{ formatDate(comment.createTime) }}</span>
        </div>

      </div>
    </div>

    <!-- 分页 -->
    <div class="pagination" v-if="totalPages > 1">
      <button class="btn btn-secondary" @click="currentPage = 1" :disabled="currentPage === 1">首页</button>
      <button class="btn btn-secondary" @click="currentPage--" :disabled="currentPage === 1">上一页</button>
      <span class="page-info">第 {{ currentPage }} 页，共 {{ totalPages }} 页</span>
      <button class="btn btn-secondary" @click="currentPage++" :disabled="currentPage === totalPages">下一页</button>
      <button class="btn btn-secondary" @click="currentPage = totalPages" :disabled="currentPage === totalPages">末页</button>
    </div>
  </AppLayout>
</template>

<style scoped>
/* 页面标题 */
.page-header {
  text-align: center;
  padding: 30px 0;
  background: var(--card-bg);
  border-radius: 12px;
  border: 1px solid var(--border-color);
  box-shadow: var(--card-shadow);
}

.page-header h2 {
  color: var(--text-primary);
  font-size: 28px;
  font-weight: bold;
  margin: 0 0 10px 0;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 10px;
}

.header-icon {
  width: 28px;
  height: 28px;
  color: var(--primary-color, #F97316);
}

.meta-icon {
  width: 14px;
  height: 14px;
  vertical-align: middle;
  margin-right: 4px;
}

.page-header p {
  color: var(--text-secondary);
  margin: 0;
}

/* 评论列表 */
.comments-list {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

/* 评论卡片 */
.comment-card {
  background: var(--card-bg);
  border-radius: 12px;
  padding: 25px;
  border: 1px solid var(--border-color);
  box-shadow: var(--card-shadow);
  transition: all 0.3s ease;
}

.comment-card:hover {
  transform: translateY(-3px);
  box-shadow: var(--hover-shadow);
  border-color: var(--accent-color);
}

/* 评论内容 */
.comment-content p {
  color: var(--text-primary);
  font-size: 16px;
  line-height: 1.8;
  margin: 0 0 15px 0;
  word-break: break-word;
}

/* 评论元信息 */
.comment-meta {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding-top: 15px;
  border-top: 1px solid var(--border-color);
  flex-wrap: wrap;
  gap: 10px;
}

.article-link {
  color: var(--accent-color);
  cursor: pointer;
  font-size: 14px;
  transition: color 0.3s ease;
}

.article-link:hover {
  text-decoration: underline;
}

.comment-time {
  color: var(--text-secondary);
  font-size: 14px;
}

/* 分页 */
.pagination {
  display: flex;
  justify-content: center;
  align-items: center;
  gap: 10px;
  padding: 20px;
  background: var(--card-bg);
  border-radius: 12px;
  border: 1px solid var(--border-color);
  box-shadow: var(--card-shadow);
}

.page-info {
  color: var(--text-secondary);
  font-size: 14px;
}

/* 加载和空状态 */
.loading, .empty-state {
  padding: 40px;
  background: var(--card-bg);
  border-radius: 12px;
  border: 1px solid var(--border-color);
  box-shadow: var(--card-shadow);
}

/* 响应式设计 */
@media (max-width: 768px) {
  .page-header h2 {
    font-size: 24px;
  }

  .comment-meta {
    flex-direction: column;
    align-items: flex-start;
  }

  .pagination {
    flex-wrap: wrap;
  }
}
</style>
