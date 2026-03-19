<template>
  <div class="container" id="notes-list">
    <leftNav></leftNav>

    <!-- 右侧笔记列表 -->
    <main class="right-column">
      <div class="content-wrapper">
        <!-- 页面标题 -->
        <div class="section-header">
          <h2>✍️ 我的笔记</h2>
        </div>

        <!-- 导航和新增部分 -->
        <div class="controls">
          <input type="text" v-model="keyword" class="search-bar" placeholder="搜索笔记..." @keyup.enter="search" />
          <button class="btn btn-primary" @click="search">搜索</button>
          <button class="btn btn-secondary" @click="showAddModal = true">新增</button>
        </div>

        <!-- 笔记列表展示页面 -->
        <div v-if="loading" class="loading">笔记加载中...</div>
        <div v-else-if="notes.length === 0" class="empty-state">暂无笔记，快去写一篇吧！</div>
        <div v-else>
          <section v-for="(item, index) in notes" :key="item.noteId"
                   @click="turnToNote(item.noteId)">
            <article class="note-item">
              <h2 class="note-title">
                {{ item.noteTopic }}
              </h2>
              <p class="note-summary">{{ item.noteContent }}</p>
              <span class="note-date">{{ formatDate(item.noteCreateTime) }}</span>
            </article>
          </section>
        </div>
      </div>
    </main>

    <!-- 新增笔记模态框 -->
    <div class="modal" v-if="showAddModal" @click="closeModal">
      <div class="modal-dialog" @click.stop>
        <div class="modal-content">
          <!-- 标题头部 -->
          <div class="modal-header">
            <h5 class="modal-title">新增笔记</h5>
            <button type="button" class="btn-close" @click="closeModal">&times;</button>
          </div>

          <!-- 中间提交内容 -->
          <div class="modal-body">
            <form id="noteForm">
              <div class="mb-3">
                <label for="noteTopic" class="form-label">笔记标题</label>
                <input type="text" v-model="newNote.topic" class="form-control" id="noteTopic" required>
              </div>
              <div class="mb-3">
                <label for="noteContent" class="form-label">笔记内容</label>
                <textarea class="form-control" v-model="newNote.content" id="noteContent" rows="6" required></textarea>
              </div>
            </form>
          </div>

          <!-- 底部按钮 -->
          <div class="modal-footer">
            <button type="button" class="btn btn-primary" @click="insertNew">保存笔记</button>
            <button type="button" class="btn btn-secondary" @click="closeModal">取消</button>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import axios from 'axios'
import leftNav from '@/components/layout/leftNav.vue'
import '../../assets/css/home.css'

const router = useRouter()

// 数据状态
const keyword = ref('')
const notes = ref([])
const loading = ref(true)
const showAddModal = ref(false)
const newNote = ref({
  topic: '',
  content: ''
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

// 跳转到具体笔记页面
const turnToNote = (id) => {
  console.log("点击了id为{}的笔记", id)
  router.push(`/notes/${id}`)
}

// 关键字查询笔记
const search = () => {
  const word = keyword.value.trim()
  if (!word) {
    alert("请输入关键字！")
    return
  }
  
  loading.value = true
  axios.post('/notes/query', {
    noteTopic: word,
    noteContent: word
  })
    .then(res => {
      console.log("请求转发成功")
      console.log(res.data.data.records)
      const result = res.data
      if (result.code == 1 || result.success) {
        console.log("后端数据返回成功")
        notes.value = result.data.records || []
        console.log(result.data.records)
      } else {
        console.log("后端数据返回失败", result.msg || result.message)
        notes.value = []
        alert("没有该笔记！")
      }
    }).catch(err => {
      console.error('请求转发失败', err)
      notes.value = []
      loading.value = false
    }).finally(() => {
      loading.value = false
    })
}

// 添加新笔记
const insertNew = () => {
  console.log("点击了新增按钮")
  axios.post('/notes/insert', {
    noteTopic: newNote.value.topic,
    noteContent: newNote.value.content,
  }).then(res => {
    console.log("请求转发成功")
    const result = res.data
    console.log(result)
    if (result.code == 1 || result.success) {
      alert("添加成功！")
      showAddModal.value = false
      // 重新加载笔记列表
      getNotes()
    } else {
      alert("添加失败：" + (result.msg || "Unknown errors"))
    }
  }).catch(err => {
    console.error('请求转发失败', err)
    alert("添加失败，请重试")
  })
}

// 获取笔记列表
const getNotes = () => {
  loading.value = true
  axios.get('/notes/list')
    .then(res => {
      console.log(res.data.data.records)
      notes.value = res.data.data.records || []
    })
    .catch(err => {
      console.error('加载失败', err)
      notes.value = []
    })
    .finally(() => {
      loading.value = false
    })
}

// 关闭模态框
const closeModal = () => {
  showAddModal.value = false
  // 清空表单
  newNote.value = {
    topic: '',
    content: ''
  }
}

// 页面加载时渲染数据
onMounted(() => {
  getNotes()
})
</script>

<style scoped>
.controls {
  display: flex;
  justify-content: space-between;
  margin-bottom: 20px;
  flex-wrap: wrap;
  gap: 10px;
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

.note-item {
  padding: 20px;
  margin-bottom: 20px;
  border-radius: 8px;
  background: var(--card-bg);
  transition: all 0.3s ease;
  border: 1px solid var(--border-color);
  box-shadow: var(--card-shadow);
  cursor: pointer;
}

.note-item:hover {
  transform: translateY(-3px);
  background: var(--bg-secondary);
  box-shadow: var(--hover-shadow);
  border-color: var(--accent-color);
}

.note-title {
  margin: 0 0 10px;
  font-size: 18px;
  color: var(--text-primary);
  font-weight: 600;
}

.note-summary {
  color: var(--text-secondary);
  line-height: 1.5;
  margin-bottom: 15px;
  font-size: 14px;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.note-date {
  color: var(--text-secondary);
  font-size: 12px;
  display: block;
  text-align: right;
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
  max-width: 600px;
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

/* 响应式设计 */
@media (max-width: 768px) {
  .controls {
    flex-direction: column;
  }
  
  .search-bar {
    width: 100%;
  }
  
  .btn {
    width: 100%;
  }
}
</style>