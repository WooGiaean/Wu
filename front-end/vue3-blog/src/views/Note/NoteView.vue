<template>
  <AppLayout>
    <div class="content-wrapper">
      <router-link to="/notes" class="back-link">← 返回笔记列表</router-link>

      <article v-if="note">
        <header>
          <h1>{{note.noteTopic}}</h1>
          <div class="note-meta">发布于 {{formatDate(note.noteCreateTime)}}</div>
        </header>

        <main>
          <div class="note-content markdown-body" v-html="noteHtml"></div>
        </main>

        <div class="actions">
          <button class="btn btn-info" @click="openEditModal(note)">
            <EditPen class="btn-icon" /> 编辑笔记
          </button>

          <button class="btn btn-delete" @click="deleteNote(note.noteId)">
            <Delete class="btn-icon" /> 删除笔记
          </button>
        </div>
      </article>
      <div v-else-if="loading" class="loading">笔记加载中...</div>
      <div v-else class="empty-state">笔记不存在或已被删除</div>
    </div>

    <!-- 编辑笔记对话框 -->
    <div v-if="showEditModal" class="modal-overlay" @click="closeEditModal">
      <div class="modal-container" @click.stop>
        <div class="modal-header">
          <h3 class="modal-title">编辑笔记</h3>
          <button type="button" class="modal-close" @click="closeEditModal">×</button>
        </div>
        <div class="modal-body">
          <div class="form-container">
            <div class="form-item">
              <label class="form-label">笔记标题</label>
              <input v-model="editForm.noteTopic" type="text" class="form-input" placeholder="请输入笔记标题">
            </div>
            <div class="form-item">
              <label class="form-label">笔记内容</label>
              <textarea v-model="editForm.noteContent" class="form-textarea" placeholder="请输入笔记内容" rows="8"></textarea>
            </div>
          </div>
        </div>
        <div class="modal-footer">
          <button type="button" class="btn btn-default" @click="closeEditModal">取消</button>
          <button type="button" class="btn btn-primary" @click="saveEdit">保存修改</button>
        </div>
      </div>
    </div>
  </AppLayout>
</template>

<script setup>
import AppLayout from '@/components/layout/AppLayout.vue'
import router from '@/router/index.js'
import axiosAPI from '@/utils/api/axios.js'
import { Delete, EditPen } from '@element-plus/icons-vue'
import { marked } from 'marked'
import { computed, onMounted, ref } from 'vue'
import { useRoute } from 'vue-router'

const route = useRoute()


// 将 Markdown 转为 HTML
const noteHtml = computed(() => {
  return note.value ? marked.parse(note.value.noteContent || '') : ''
})

// 数据状态
const note = ref(null)
const loading = ref(true)

// 编辑对话框状态
const showEditModal = ref(false)
const editForm = ref({
  noteId: '',
  noteTopic: '',
  noteContent: ''
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


// 打开编辑对话框
const openEditModal = (noteData) => {
  editForm.value = {
    noteId: noteData.noteId,
    noteTopic: noteData.noteTopic,
    noteContent: noteData.noteContent
  }
  showEditModal.value = true
}

// 关闭编辑对话框
const closeEditModal = () => {
  showEditModal.value = false
  editForm.value = {
    noteId: '',
    noteTopic: '',
    noteContent: ''
  }
}

// 保存编辑
const saveEdit = async () => {
  if (!editForm.value.noteTopic.trim()) {
    alert('请输入笔记标题')
    return
  }

  try {
    const response = await axiosAPI.put('/user/notes/update', {
      noteId: editForm.value.noteId,
      noteTopic: editForm.value.noteTopic,
      noteContent: editForm.value.noteContent
    })

    if (response.data.code === 1) {
      alert('修改成功')
      // 更新页面显示的笔记数据
      note.value = {
        ...note.value,
        noteTopic: editForm.value.noteTopic,
        noteContent: editForm.value.noteContent
      }
      closeEditModal()
    } else {
      alert('修改失败：' + (response.data.msg || '未知错误'))
    }
  } catch (error) {
    console.error('修改笔记失败:', error)
    alert('修改失败，请重试')
  }
}

// 删除笔记
const deleteNote = (id) => {
  if(confirm('确定要删除这篇笔记吗？')) {
    axiosAPI.delete(`/user/notes/delete/${id}`)
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
  const id =  route.params.id;
  if (!id) {
    alert("笔记id无效")
    router.push('/notes')
    return
  }
  console.log("笔记id为："+id)

  // 获取笔记详情
  loading.value = true
  axiosAPI.get(`/user/notes/${id}`)
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
/* 内容包装器 */
.content-wrapper {
  display: flex;
  flex-direction: column;
  gap: 24px;
}

/* 返回按钮 */
.back-link {
  display: inline-flex;
  align-items: center;
  gap: 8px;
  color: #E1BE97;
  text-decoration: none;
  font-size: 14px;
  padding: 10px 20px;
  background: rgba(225, 190, 151, 0.1);
  border-radius: 25px;
  transition: all 0.3s ease;
  margin-bottom: 10px;
}

.back-link:hover {
  background: rgba(225, 190, 151, 0.2);
  color: #664401;
  transform: translateX(-3px);
}

/* 笔记卡片 */
article {
  background-color: var(--card-bg);
  border-radius: 12px;
  padding: 30px;
  border: 1px solid var(--border-color);
  box-shadow: var(--card-shadow);
}

article header {
  text-align: center;
  margin-bottom: 30px;
  padding-bottom: 20px;
  border-bottom: 2px solid var(--border-color);
}

article h1 {
  font-size: 24px;
  color: var(--text-primary);
  margin-bottom: 15px;
  line-height: 1.3;
  font-weight: 600;
}

/* 笔记元信息 */
.note-meta {
  color: var(--text-secondary);
  font-size: 14px;
  margin-top: 10px;
}

/* 笔记内容 */
.note-content {
  font-size: 16px;
  color: var(--text-primary);
  line-height: 1.8;
  margin-bottom: 30px;
  white-space: pre-line;
}

/* 操作按钮 */
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

/* 加载和空状态 */
.loading, .empty-state {
  text-align: center;
  padding: 60px 40px;
  color: var(--text-secondary);
  background: var(--card-bg);
  border-radius: 12px;
  border: 1px solid var(--border-color);
  box-shadow: var(--card-shadow);
}

/* 对话框样式 */
.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: rgba(0, 0, 0, 0.5);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1000;
}

.modal-container {
  background: var(--card-bg);
  border-radius: 12px;
  width: 90%;
  max-width: 500px;
  box-shadow: 0 20px 40px rgba(0, 0, 0, 0.2);
  overflow: hidden;
}

.modal-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 16px 20px;
  border-bottom: 1px solid var(--border-color);
  background: linear-gradient(135deg, #E1BE97 0%, #664401 100%);
}

.modal-title {
  color: white;
  font-size: 18px;
  font-weight: 600;
  margin: 0;
}

.modal-close {
  background: none;
  border: none;
  color: white;
  font-size: 24px;
  cursor: pointer;
  padding: 0;
  width: 30px;
  height: 30px;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 50%;
  transition: background 0.3s ease;
}

.modal-close:hover {
  background: rgba(255, 255, 255, 0.2);
}

.modal-body {
  padding: 20px;
}

.modal-footer {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
  padding: 16px 20px;
  border-top: 1px solid var(--border-color);
}

/* 表单样式 */
.form-container {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.form-item {
  display: flex;
  flex-direction: column;
}

.form-label {
  font-size: 14px;
  font-weight: 500;
  color: var(--text-primary);
  margin-bottom: 8px;
}

.form-input {
  padding: 10px 14px;
  border: 1px solid var(--border-color);
  border-radius: 8px;
  font-size: 14px;
  background: white;
  color: var(--text-primary);
  transition: all 0.3s ease;
}

.form-input:focus {
  outline: none;
  border-color: var(--accent-color);
  box-shadow: 0 0 0 3px rgba(225, 190, 151, 0.2);
}

.form-textarea {
  padding: 10px 14px;
  border: 1px solid var(--border-color);
  border-radius: 8px;
  font-size: 14px;
  background: white;
  color: var(--text-primary);
  resize: vertical;
  transition: all 0.3s ease;
}

.form-textarea:focus {
  outline: none;
  border-color: var(--accent-color);
  box-shadow: 0 0 0 3px rgba(225, 190, 151, 0.2);
}

/* 按钮样式 */
.btn-default {
  padding: 10px 20px;
  border: 1px solid var(--border-color);
  border-radius: 8px;
  font-size: 14px;
  cursor: pointer;
  background: var(--bg-secondary);
  color: var(--text-primary);
  transition: all 0.3s ease;
}

.btn-default:hover {
  background: var(--bg-primary);
}

.btn-primary {
  padding: 10px 20px;
  border: none;
  border-radius: 8px;
  font-size: 14px;
  cursor: pointer;
  background: linear-gradient(135deg, #E1BE97 0%, #664401 100%);
  color: white;
  transition: all 0.3s ease;
}

.btn-primary:hover {
  transform: translateY(-1px);
  box-shadow: 0 4px 12px rgba(225, 190, 151, 0.4);
}

/* 响应式设计 */
@media (max-width: 1024px) {
  .container {
    padding: 15px;
    gap: 20px;
  }
}

@media (max-width: 768px) {
  .container {
    flex-direction: column;
    padding: 10px;
    gap: 20px;
  }

  .right-column {
    width: 100%;
  }

  article h1 {
    font-size: 20px;
  }

  .actions {
    flex-direction: column;
  }

  .btn {
    width: 100%;
    text-align: center;
  }

  .modal-container {
    width: 95%;
    margin: 10px;
  }
}
</style>
