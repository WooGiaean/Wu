<template>
  <div class="container" id="mainP">
    <!-- 左侧导航栏 -->
<!--    <aside class="left-column">
      <div class="blog-title">
        <h1>个人博客</h1>
        &lt;!&ndash; 动态显示用户头像 &ndash;&gt;
        <img :src="userInfo && userInfo.userAvatar ? `/touxiang/${userInfo.userAvatar}` : '/touxiang/blog_avatar-3.png'" alt="用户头像" class="avatar"
             onerror="this.src='https://picsum.photos/seed/avatar/100/100'">
        &lt;!&ndash; 显示用户昵称 &ndash;&gt;
        <p class="user-nickname">{{ userNickName }}</p>
      </div>

      &lt;!&ndash; 菜单栏 &ndash;&gt;
      <nav class="nav-menu">
        <div class="nav-item"><a href="/home">🏠 主页</a></div>
        <div class="nav-item"><a href="/profile">👤 关于我</a></div>
        <div class="nav-item"><a href="/articles">📚 博客</a></div>
        <div class="nav-item"><a href="/notes">✍️ 随笔</a></div>
        <div class="nav-item"><a href="#">🧠 知识库</a></div>
        <div class="nav-item" style="color: #d9534f;" @click="logout()">🚪 退出登录</div>
      </nav>
    </aside>-->

    <leftNav></leftNav>

    <!-- 右侧文章及笔记 -->
    <main class="right-column">
      <!-- 内容区域 -->
      <div class="content-wrapper">
        <!-- 笔记区域 -->
        <section class="notes-section">
          <div class="section-header">
            <h2>📝 我的笔记</h2>
            <a href="/notes">查看更多 &rarr;</a>
          </div>

          <div v-if="loading" class="loading">笔记加载中...</div>
          <div v-else-if="notes.length === 0" class="empty-state">暂无笔记，快去写一篇吧！</div>
          <div v-else class="notes-container">
            <div class="note-card" v-for="(note,index) in notes" :key="note.noteId"
                 @click="intoSpecificNote(note.noteId)">
              <h3>{{ note.noteTopic }}</h3>
              <p>{{ formatDate(note.noteCreateTime) }}</p>
            </div>
          </div>
        </section>

        <!-- 分类展示区域 -->
        <section class="category-section">
          <div class="section-header">
            <h2>📝 文章分类</h2>
          </div>

          <div class="category-tags">
            <a href="/articles" class="category-tag active">全部</a>
            <p v-for="c in category" :key="c.categoryId" class="category-tag">
              {{ c.categoryName }}
            </p>
          </div>
        </section>

        <!-- 博客展示区域 -->
        <section class="blogs-section">
          <div class="section-header">
            <h2>🔥 最近博客</h2>
            <a href="/articles">查看更多 &rarr;</a>
          </div>

          <div v-if="loading" class="loading">文章加载中...</div>
          <div v-else-if="blogs.length === 0" class="empty-state">暂无文章，敬请期待！</div>
          <div v-else>
            <article class="blog-item" v-for="blog in blogs" :key="blog.articleId"
                     @click="intoSpecificBlog(blog.articleId)">
              <!-- 图片添加默认图逻辑 -->
              <img :src="blog.articleThumbnail ? `/uploaded-images/${blog.articleThumbnail}` : 'https://picsum.photos/seed/blog/300/200'"
                   class="blog-image"
                   :alt="blog.articleTitle"
                   onerror="this.src='https://picsum.photos/seed/error/300/200'">

              <div class="blog-content">
                <h3>{{ blog.articleTitle }}</h3>
                <p>{{ blog.articleSummary }}</p>
                <div class="blog-meta">
                  <span>📅 {{ formatDate(blog.articleCreateTime) }}</span>
                </div>
              </div>
            </article>
          </div>
        </section>
      </div>
    </main>


  </div>

  <!-- 页脚个人信息 -->

</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import axiosAPI from '@/utils/api/axios'
import '../../assets/css/home.css'
import leftNav from '@/components/layout/leftNav.vue'
const router = useRouter()

// 数据状态
const blogs = ref([])
const userInfo = ref(null)
const userNickName = ref('')
const notes = ref([])
const loading = ref(true)
const category = ref([])

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
const intoSpecificNote = (id) => {
  router.push(`/notes/${id}`)
}

// 跳转到具体博客
const intoSpecificBlog = (id) => {
  router.push(`/articles/${id}`)
}

// 退出登录
/*const logout = async () => {
  if(confirm('确定要退出登录吗？')) {
    try {
      const response = await axiosAPI.post('/admin/logout')
      if (response.data.code === 1) {
        sessionStorage.removeItem("user")
        router.push('/login')
      } else {
        alert(response.data.message || "退出失败")
      }
    } catch (error) {
      console.error('退出失败:', error)
      alert('网络错误，退出失败')
    }
  }
}*/

// 页面加载时获取数据
onMounted(async () => {
  try {
    // 使用 Promise.all 等待所有请求完成再隐藏 loading
    const [articlesRes, userRes, notesRes, categoryRes] = await Promise.all([
      axiosAPI.get('/home/latestArticles', { credentials: 'include' }),
      axiosAPI.get('/home/currentUser', { credentials: 'include' }),
      axiosAPI.get('/home/latestNotes', { credentials: 'include' }),
      axiosAPI.get('/category/list')
    ])

    // 处理文章数据
    if(articlesRes.data.data && articlesRes.data.data.records) {
      console.log("文章数据:", articlesRes.data.data.records)
      blogs.value = articlesRes.data.data.records
    }

    // 处理用户数据
    if(userRes.data.data) {
      console.log("用户数据:", userRes.data.data)
      userInfo.value = userRes.data.data
      userNickName.value = userRes.data.data.userNickname || 'Visitor'
    }

    // 处理笔记数据
    if(notesRes.data.data && notesRes.data.data.records) {
      console.log("笔记数据:", notesRes.data.data.records)
      notes.value = notesRes.data.data.records
    }

    // 分类数据
    if(categoryRes.data.data && categoryRes.data.data.records) {
      category.value = categoryRes.data.data.records
    }
  } catch (error) {
    console.error("页面初始化数据请求失败", error)
  } finally {
    loading.value = false
  }
})
</script>

<style scoped>
/* 组件特定样式 */
</style>
