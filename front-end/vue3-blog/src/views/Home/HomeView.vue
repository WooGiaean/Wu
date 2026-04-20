<template>
  <div class="container" id="mainP">
    <!-- 左侧边栏 -->
    <leftNav />

    <!-- 右侧主内容区 -->
    <main class="main">
      <!-- 笔记区域 -->
      <h2 style="font-size: 24px; margin-bottom: 20px; color: var(--text-primary);">📝 我的笔记</h2>
      <div v-if="loading" class="loading">
<!--        笔记加载中...-->
        <el-skeleton rows="5" animated></el-skeleton>
      </div>
      <div v-else-if="notes.length === 0" class="empty-state">
<!--        暂无笔记，快去写一篇吧！-->
        <el-empty description="暂无笔记，快去写一篇吧！"></el-empty>
      </div>
      <div v-else class="notes-grid">
        <el-card
          v-for="note in notes"
          :key="note.noteId"
          class="note-card"
          @click="intoSpecificNote(note.noteId)">

          <template #header>
            <div class="note-header">
              <h3 class="note-title">{{ note.noteTopic }}</h3>
            </div>
          </template>
          <p class="note-description">{{ note.noteContent ? note.noteContent.substring(0, 100) + '...' : '无内容' }}</p>
          <div class="note-meta">
            <span>{{ formatDate(note.noteCreateTime) }}</span>
          </div>
        </el-card>


<!--        <div class="note-card" v-for="(note,index) in notes" :key="note.noteId"
             @click="intoSpecificNote(note.noteId)">
          <div class="note-content">
            <h3 class="note-title">{{ note.noteTopic }}</h3>
            <p class="note-description">{{ note.noteContent ? note.noteContent.substring(0, 100) + '...' : '无内容' }}</p>
            <div class="note-meta">
              <span>{{ formatDate(note.noteCreateTime) }}</span>
            </div>
          </div>

        </div>-->


      </div>

      <!-- 博客展示区域 -->
      <h2 style="font-size: 24px; margin: 40px 0 20px; color: var(--text-primary);">🔥 最近博客</h2>
      <div v-if="loading" class="loading">
        文章加载中...
        <el-skeleton rows="5" animated></el-skeleton>
      </div>
      <div v-else-if="blogs.length === 0" class="empty-state">
<!--        暂无文章，敬请期待！-->
        <el-empty description="暂无文章，快去写一篇吧！"></el-empty>
      </div>
      <div v-else>

        <el-card
          v-for="blog in blogs"
          :key="blog.articleId"
          class="article-card"
          @click="intoSpecificBlog(blog.articleId)"
        >
          <template #header>
            <div class="article-header">
              <h3 class="article-title">{{ blog.articleTitle }}</h3>
            </div>
          </template>
          <div class="article-meta">
            <span>📅 {{ formatDate(blog.articleCreateTime) }}</span>
          </div>
          <div class="article-content">{{ blog.articleSummary }}</div>

          <div v-if="blog.articleThumbnail" class="article-thumbnail">
             <img :src="`/uploaded-images/${blog.articleThumbnail}`" alt="缩略图">
          </div>

          <p>
            {{blog.blogger}}
          </p>

        </el-card>


<!--        <article class="article-card" v-for="blog in blogs" :key="blog.articleId"
                 @click="intoSpecificBlog(blog.articleId)">
          <h3 class="article-title">{{ blog.articleTitle }}</h3>
          <div class="article-meta">
            <span>📅 {{ formatDate(blog.articleCreateTime) }}</span>
          </div>
          <div class="article-content">{{ blog.articleSummary }}</div>
        </article>-->

      </div>

    </main>
  </div>

  <!-- 页脚个人信息 -->
  <FooterInfo :userInfo="userInfo"></FooterInfo>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import  router  from '@/router/index.js'
import axiosAPI from '@/utils/api/axios'
//import '@/assets/css/home.css'
import { useUserInfoStore } from '@/stores/modules/userInfo.js'
import { ElCard, ElSkeleton, ElEmpty } from 'element-plus'


import leftNav from '@/components/layout/leftNav.vue' //左侧导航栏组件
import FooterInfo from "@/components/layout/footerInfo.vue";  //页脚个人信息组件


const userStore = useUserInfoStore()
// 数据状态
//博文列表数据
const blogs = ref([])
//用户信息数据
const userInfo = ref({})
//用户昵称
const userNickName = ref('')
//用户笔记信息
const notes = ref([])
//加载状态
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

// 跳转到具体笔记
const intoSpecificNote = async (id) => {
  await router.push(`/notes/${id}`)
}

// 跳转到具体博客
const intoSpecificBlog = async (id) => {
  await router.push(`/articles/${id}`)
}

// 页面加载时获取数据
onMounted(async () => {
  try {
    //从pinia获取用户信息
    const tempUserInfo = userStore.getUserInfo();
    console.log("使用pinia存储的用户对象"+tempUserInfo)
    //如果pinia中没有数据，再请求后端接口
    if(!tempUserInfo) {
      const userRes = await axiosAPI.get('/home/currentUser', { credentials: 'include' })
      // 处理用户数据
        console.log("用户数据:", userRes.data.data)
        userInfo.value = userRes.data.data
        userNickName.value = userRes.data.data.userNickname || 'Visitor'
    }else{
      userInfo.value = tempUserInfo
      userNickName.value = tempUserInfo.userNickname || 'Visitor'
    }
    // 使用 Promise.all 等待所有请求完成再隐藏 loading
    const [articlesRes, notesRes] = await Promise.all([
      axiosAPI.get('/home/latestArticles', { credentials: 'include' }),
      axiosAPI.get('/home/latestNotes', { credentials: 'include' })
    ])

    // 处理文章数据
    if(articlesRes.data.data && articlesRes.data.data.records) {
      console.log("文章数据:", articlesRes.data.data.records)
      blogs.value = articlesRes.data.data.records
    }

    // 处理笔记数据
    if(notesRes.data.data && notesRes.data.data.records) {
      console.log("笔记数据:", notesRes.data.data.records)
      notes.value = notesRes.data.data.records
    }


  } catch (error) {
    console.error("页面初始化数据请求失败", error)
  } finally {
    loading.value = false
  }
})
</script>

<style scoped>
/* 笔记网格 */
.notes-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(300px, 1fr));
  gap: 20px;
}

.note-card, .article-card {
  /*background: var(--card-bg);
  padding: 20px;
  border-radius: 12px;
  border: 1px solid var(--border-color);
  box-shadow: var(--card-shadow);
  cursor: pointer;
  transition: all 0.3s ease;*/
  cursor: pointer;
  transition: all 0.3s ease;
  position: relative;
  margin-bottom: 20px;
  overflow: hidden; /* 防止图片圆角溢出卡片 */
}

/*添加了鼠标悬停效果 .article-card:hover*/
.note-card:hover, .article-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 10px 20px rgba(0,0,0,0.1);
}

.note-header,
.article-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}


.note-title {
  color: var(--text-primary);
  font-size: 16px;
  font-weight: 600;
  margin-bottom: 10px;
}

.note-description {
  color: var(--text-secondary);
  font-size: 14px;
  line-height: 1.5;
  margin-bottom: 15px;
}

.note-meta {
  color: var(--text-secondary);
  font-size: 12px;
}

/*
文章样式
*/

.article-title {
  color: var(--text-primary);
  font-size: 20px;
  font-weight: 600;
  margin-bottom: 10px;
}

.article-meta {
  font-size: 13px;
  color: var(--text-secondary);
  margin-bottom: 15px;
}

.article-content {
  font-size: 15px;
  color: var(--text-secondary);
  line-height: 1.8;
}
.article-thumbnail {
  position: absolute;
  bottom: 10px; /* 距离底部的距离 */
  right: 15px;  /* 距离右侧的距离 */
  width: 200px;  /* 图片容器宽度 */
  height: 120px; /* 图片容器高度 */
  border-radius: 8px; /* 圆角 */
  overflow: hidden; /* 裁剪图片以适应圆角 */
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.15); /* 可选：增加一点阴影提升层次感 */
  //border: 2px solid var(--bg-color); /* 可选：增加边框与背景融合 */
}

.article-thumbnail img {
  width: 100%;
  height: 100%;
  object-fit: cover; /* 保持图片比例并填满容器，超出部分裁剪 */
  display: block;
}

/* 加载和空状态 */
.loading,
.empty-state {
  text-align: center;
  padding: 40px;
  color: var(--text-secondary);
  background: var(--card-bg);
  border-radius: 12px;
  border: 1px solid var(--border-color);
  box-shadow: var(--card-shadow);
}

/* 响应式设计 */
@media (max-width: 768px) {
  .notes-grid {
    grid-template-columns: 1fr;
  }
}
</style>
