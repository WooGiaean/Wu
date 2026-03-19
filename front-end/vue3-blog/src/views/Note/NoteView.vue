<template>
  <div class="container" id="note">
    <leftNav></leftNav>

    <!-- 右侧笔记内容 -->
    <main class="right-column">
      <div class="content-wrapper">
        <router-link to="/notes" class="back-link">← 返回笔记列表</router-link>

        <article v-if="note">
          <header>
            <h1>{{note.noteTopic}}</h1>
            <div class="note-meta">发布于 {{formatDate(note.noteCreateTime)}}</div>
          </header>

          <main>
            <div class="note-content">{{note.noteContent}}</div>
          </main>

          <div class="actions">
            <button class="btn btn-info" @click="editNote(note.noteId)">
              ✏️ 编辑笔记
            </button>

            <button class="btn btn-delete" @click="deleteNote(note.noteId)">
              🗑️ 删除笔记
            </button>
          </div>
        </article>
        <div v-else-if="loading" class="loading">笔记加载中...</div>
        <div v-else class="empty-state">笔记不存在或已被删除</div>
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
const note = ref(null)
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

// 编辑笔记
const editNote = (id) => {
  console.log("准备进行修改笔记页面")
  // 这里可以添加编辑页面的路由
  alert('编辑功能开发中')
}

// 删除笔记
const deleteNote = (id) => {
  if(confirm('确定要删除这篇笔记吗？')) {
    axios.delete('/notes/delete/'+id)
      .then(res => {
        console.log(res.data.code==1?"删除成功":"删除失败")
        if(res.data.code==1) {
          router.push('/notes')
        } else {
          alert('删除失败：' + (res.data.msg || '未知错误'))
        }
      })
      .catch(err => {
        console.log(err)
        alert('删除失败，请重试')
      })
  }
}

// 页面加载时获取笔记详情
onMounted(() => {
  // 获取笔记ID
  const id = route.params.id
  if (!id) {
    alert("笔记id无效")
    router.push('/notes')
    return
  }
  console.log("笔记id为："+id)

  // 获取笔记详情
  loading.value = true
  axios.get('/notes/noteId?id='+id)
    .then(res => {
      if (res.data.code === 1) {
        note.value = res.data.data
      } else {
        alert('获取笔记失败：' + (res.data.msg || '未知错误'))
        router.push('/notes')
      }
    })
    .catch(err => {
      console.error('获取笔记失败', err)
      alert('获取笔记失败，请重试')
      router.push('/notes')
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

header {
  margin-bottom: 30px;
  border-bottom: 1px solid var(--border-color);
  padding-bottom: 20px;
}

h1 {
  font-size: 24px;
  color: var(--text-primary);
  margin-bottom: 10px;
  font-weight: 600;
}

.note-meta {
  color: var(--text-secondary);
  font-size: 14px;
}

.back-link {
  display: inline-block;
  margin-bottom: 20px;
  color: var(--accent-color);
  text-decoration: none;
  font-weight: 500;
  transition: all 0.3s ease;
}

.back-link:hover {
  color: var(--text-primary);
  text-decoration: underline;
}

.note-content {
  font-size: 16px;
  color: var(--text-primary);
  line-height: 1.8;
  margin-bottom: 30px;
  white-space: pre-line;
}

.actions {
  margin-top: 30px;
  padding-top: 20px;
  border-top: 1px solid var(--border-color);
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

/* 响应式设计 */
@media (max-width: 768px) {
  h1 {
    font-size: 20px;
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