<template>
  <div class="container" id="app">
    <!-- 左侧边栏 -->
    <leftNav />

    <!-- 右侧文章列表 -->
    <main class="main">
      <!-- 页面标题和新增按钮 -->
      <div style="display: flex; justify-content: space-between; align-items: center; margin-bottom: 20px;">
        <h1 style="font-size: 24px; color: var(--text-primary);">📚 所有文章</h1>
        <el-button type="primary" @click="showAddModal = true">新增</el-button>
      </div>

      <!-- 搜索区域 -->
      <div style="display: flex; gap: 10px; margin-bottom: 20px;">
        <el-input
          v-model="keyword"
          placeholder="搜索文章..."
          style="flex: 1"
          @keyup.enter="search"
        />
        <el-button type="primary" @click="search">搜索</el-button>
      </div>

      <!-- 文章列表 -->
      <div v-if="loading" class="loading">
<!--        <p>加载中...</p>-->
        <el-skeleton rows="5" animated style="width: 100%; height: 20px;" />
      </div>
      <div v-else-if="articles.length === 0" class="empty-state">
<!--        <p>暂无文章，快去写一篇吧！</p>-->
        <el-empty description="暂无文章，快去写一篇吧！" />
      </div>
      <div v-else>
        <article
          v-for="article in articles"
          :key="article.articleId"
          class="article-card"
          @click="showSpecificArticle(article.articleId)"
        >

            <!--   文章缩略图       -->
          <!-- 缩略图区域 -->
          <div class="article-thumbnail">
            <img
              :src="getArticleThumbnail(article)"
              :alt="article.articleTitle"
              class="thumbnail-img"
            >
          </div>

          <h3 class="article-title">{{ article.articleTitle }}</h3>
          <div class="article-meta">
            <span>📅 {{ formatDate(article.articleCreateTime) }}</span>
            <span>👁️ {{ article.articleReadCount }}</span>
            <span>💬 {{ article.articleCommentCount }}</span>
            <span>❤️ {{ article.articleLikeCount }}</span>
          </div>
          <div class="article-content">{{ article.articleSummary }}</div>


          <div class="article-tags">
        <!-- 展示分类 -->
            <span
              v-for="category in article.categoryList"
              :key="category.categoryId"
              class="article-tag"
              @click="handleCategorySelect(category.categoryId)"
            >
              {{ category.categoryName }}
            </span>
            <!-- 展示标签 -->
            <span
              v-for="tag in article.tagList"
              :key="tag.tagId"
              class="article-tag"
            >
              {{ tag.tagName }}
            </span>

          </div>
        </article>
      </div>

      <!-- 弹窗页面(用于添加新文章) -->
      <el-dialog
        v-model="showAddModal"
        title="新增文章"
        width="800px"
        append-to-body
      >
        <el-form :model="newArticle">
          <!-- 文章标题输入框 -->
          <el-form-item label="标题" required>
            <el-input v-model="newArticle.title" placeholder="请输入博文标题" />
          </el-form-item>

          <!-- 文章分类选择 -->
          <el-form-item label="文章分类" required>
            <el-checkbox-group v-model="selectedCategoryIds">
              <el-checkbox
                v-for="category in categories"
                :key="category.categoryId"
                :label="category.categoryId"
              >
                {{ category.categoryName }}
              </el-checkbox>
            </el-checkbox-group>
          </el-form-item>

          <!-- 文章内容 -->
          <el-form-item label="内容" required>
            <div ref="vditorRef" style="border: 1px solid var(--border-color); border-radius: 8px; overflow: hidden;"></div>
          </el-form-item>

          <!-- 文章概要 -->
          <el-form-item label="博文概要" required>
            <el-input v-model="newArticle.summary" placeholder="概要信息" />
          </el-form-item>

          <!-- 图片上传 -->
<!--          action="/api/images/upload"
   :on-error="handleImageUploadError"
-->
          <el-form-item label="封面图片（可选）">
            <el-upload
              class="avatar-uploader"
              :on-success="handleImageUpload"
              :auto-upload="true"
              :show-file-list="false"
            >
              <el-button size="small" type="primary">点击上传</el-button>
            </el-upload>
          </el-form-item>
        </el-form>
        <template #footer>
          <span class="dialog-footer">
            <el-button @click="closeModal">关闭</el-button>
            <el-button type="primary" @click="submitNew">提交</el-button>
          </span>
        </template>
      </el-dialog>
    </main>
  </div>
</template>

<script setup>
import {ref, onMounted, onUnmounted, nextTick, watch, computed} from 'vue'
import { useRoute } from 'vue-router'
import  router  from '@/router/index.js'
import axiosAPI from '@/utils/api/axios.js'
import Vditor from 'vditor'
import 'vditor/dist/index.css'
//import '@/assets/css/home.css'
import leftNav from '@/components/layout/leftNav.vue' //左侧导航栏组件
import { useUserInfoStore } from '@/stores/modules/userInfo.js'
import { ElForm, ElFormItem, ElInput, ElButton, ElDialog,
  ElTag, ElMenu, ElMenuItem, ElCard, ElSkeleton, ElEmpty,
  ElCheckbox, ElCheckboxGroup, ElUpload, ElMessage } from 'element-plus'

const route = useRoute()
const userStore = useUserInfoStore();
//const router = useRouter()

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
  content: '',
  summary: '',
  thumbnail: ''
})

// Vditor 实例
const vditorRef = ref(null)
const vditorInstance = ref(null)

const getArticleThumbnail=(article)=>{
  console.log(article)
  if(article.articleThumbnail){
    return `/uploaded-images/${article.articleThumbnail}`
  }else{
    return new URL('@/assets/default/pics/1ad50b1598ddec725db64cabcf27ecc.jpg', import.meta.url).href
  }
}


// 格式化日期函数
const formatDate = (timeString) => {
  if(!timeString) return ''
  const date = new Date(timeString)
  const year = date.getFullYear()
  const month = String(date.getMonth() + 1).padStart(2, '0')
  const day = String(date.getDate()).padStart(2, '0')
  return `${year}-${month}-${day}`
}

// 获取所有文章:用户下的所有文章
const getAllArticles = () => {
  loading.value = true
  axiosAPI.get('/user/articles/list')
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
  axiosAPI.get('/user/category/articles')
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
    axiosAPI.get(`/user/category/articles?categoryId=${categoryId}&page=1&pageSize=10`)
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
  axiosAPI.post('/user/articles/search', {
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

// 分类选择处理
const handleCategorySelect = (key) => {
  if (key === 'all') {
    filterByCategory(null)
  } else {
    filterByCategory(parseInt(key))
  }
}

// 处理图片上传成功
const handleImageUploadSuccess = (response) => {
  if (response.code == 1) {
    newArticle.value.thumbnail = response.data
  } else {
    ElMessage.error("图片上传失败：" + (response.msg || "Unknown errors"))
  }
}

// 处理图片上传失败
const handleImageUploadError = (error) => {
  console.error("图片上传失败", error)
  ElMessage.error("图片上传失败")
}

// 处理图片上传（保留原方法，以防其他地方调用）
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
        ElMessage.error("图片上传失败：" + (response.data.msg || "Unknown errors"))
      }
    }).catch(error => {
      console.error("图片上传失败", error)
      ElMessage.error("图片上传失败")
    })
  }
}

// 新增文章
const submitNew = async () => {
  try {
    // 确保获取Vditor的最新内容
    if (vditorInstance.value) {
      newArticle.value.content = vditorInstance.value.getValue()
    }

    //上传博文内容
    const insertRes = await axiosAPI.post('/user/articles', {
      articleTitle: newArticle.value.title,
      //blogger: newArticle.value.blogger,
      articleContent: newArticle.value.content,
      articleSummary: newArticle.value.summary,
      articleThumbnail: newArticle.value.thumbnail,
      categoryIds: selectedCategoryIds.value
    })

    if (insertRes.data.code === 1) {
      alert("文章添加成功")
      console.log("插入成功:"+ insertRes.data)
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

// 初始化 Vditor
const initVditor = () => {
  nextTick(() => {
    if (vditorRef.value && !vditorInstance.value) {
      vditorInstance.value = new Vditor(vditorRef.value, {
        mode: 'sv', // 所见即所得模式
        preview: {
          show: true, // 显示预览
          theme: 'light'
        },
        toolbar: [
          'emoji',
          'headings',
          'bold',
          'italic',
          'strike',
          'link',
          'image',
          'list',
          'table',
          'code',
          'line',
          'fullscreen'
        ],
        height: 400,
        cache: {
          enable: false // 禁用缓存，避免需要cache.id
        },
        blur: () => {
          // 当编辑器失去焦点时，更新内容到 newArticle.content
          if (vditorInstance.value) {
            newArticle.value.content = vditorInstance.value.getValue()
          }
        }
      })
    }
  })
}

// 销毁 Vditor 实例
const destroyVditor = () => {
  if (vditorInstance.value) {
    vditorInstance.value.destroy()
    vditorInstance.value = null
  }
}

// 监听弹窗显示状态，初始化或销毁 Vditor
const handleAddModalChange = (newVal) => {
  if (newVal) {
    // 弹窗显示时初始化 Vditor
    initVditor()
  } else {
    // 弹窗关闭时销毁 Vditor
    destroyVditor()
  }
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
  // 销毁 Vditor 实例
  destroyVditor()
}

// 页面加载时获取数据
onMounted(() => {
  // 处理 URL 参数中的分类
  const urlParams = new URLSearchParams(window.location.search)
  const categoryId = urlParams.get('category')

  const tempId=route.params.id;
  console.log(tempId)

  // 使用 Promise.all 等待所有请求完成再隐藏 loading
  Promise.all([
    getCategories(),
    axiosAPI.get('/home/currentUser', { credentials: 'include' })
  ]).then(([_, userRes]) => {
    // 处理用户数据
    if(userRes.data.data) {
      userInfo.value = userRes.data.data
    }

    // 根据 URL 参数加载文章
    if (categoryId) {
      filterByCategory(categoryId)
    } else {
      getAllArticles()
    }
  }).catch(error => {
    console.error("页面初始化数据请求失败", error)
    getAllArticles()
  })

  // 监听 showAddModal 变化
  watch(showAddModal, handleAddModalChange)
})

// 组件卸载时销毁 Vditor
onUnmounted(() => {
  destroyVditor()
})
</script>

<style scoped>
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
  margin-bottom: 20px;
}

.article-meta {
  display: flex;
  gap: 15px;
  margin-bottom: 15px;
}

.article-meta span {
  font-size: 13px;
  color: var(--text-secondary);
}

/* 文章卡片 */
/*.article-card {
  background: white;
  border-radius: 12px;
  padding: 20px;
  margin-bottom: 20px;
  box-shadow: 0 2px 10px rgba(0,0,0,0.05);
  cursor: pointer;
  transition: all 0.3s ease;
  display: flex;
  flex-direction: column;
  height: 100%;
}*/

.article-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 10px 20px rgba(0,0,0,0.1);
}

/* 缩略图样式 */
.article-thumbnail {
  width: 100%;
  height: 200px;
  overflow: hidden;
  border-radius: 8px;
  margin-bottom: 15px;
}

.thumbnail-img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform 0.3s ease;
}


.article-card:hover .thumbnail-img {
  transform: scale(1.05);
}
/*.article-meta {
  margin-bottom: 12px;
  flex-shrink: 0;
}*/

.article-title {
  margin-bottom: 10px;
  flex-shrink: 0;
}


.article-content {
  flex-grow: 1;
  margin-bottom: 15px;
}

/*.article-tags {
  margin-top: auto;
  padding-top: 10px;
  border-top: 1px solid #eee;
}*/



/*.article-tags {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
  margin-top: 15px;
}

.article-tag {
  background: var(--bg-secondary);
  padding: 3px 8px;
  border-radius: 12px;
  font-size: 12px;
  color: var(--text-secondary);
  border: 1px solid var(--border-color);
}*/


/* 标签 */
.article-tags {
  margin-top: auto;
  padding-top: 10px;
  border-top: 1px solid #eee;
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}

.article-tag {
  background: #e2e8f0;
  padding: 4px 12px;
  border-radius: 20px;
  font-size: 13px;
  color: #4a5568;
  cursor: pointer;
  transition: all 0.2s ease;
}

.article-tag:hover {
  background: #cbd5e0;
  color: #2d3748;
}


/* 响应式设计 */
@media (max-width: 768px) {
  .article-meta {
    flex-direction: column;
    gap: 5px;
  }
}
</style>
