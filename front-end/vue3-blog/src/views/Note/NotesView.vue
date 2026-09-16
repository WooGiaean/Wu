<template>
  <AppLayout>
    <div class="notes-page">
      <div class="page-header">
        <div class="page-title">
          <EditPen class="title-icon" />
          <h1>我的笔记</h1>
        </div>
        <el-button type="primary" class="add-btn" @click="showAddModal = true">
          <Plus class="btn-icon" />
          新建笔记
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
          :style="{ animationDelay: `${index * 0.08}s` }"
        >
          <template #header>
            <div class="note-header">
              <h3 class="note-title">{{ note.noteTopic }}</h3>
              <span class="note-date">{{ formatDate(note.noteCreateTime) }}</span>
            </div>
          </template>
          <p class="note-content">{{ note.noteContent ? note.noteContent.substring(0, 150) + '...' : '无内容' }}</p>
          <div class="note-footer">
            <el-tag size="small" type="primary" effect="plain">笔记</el-tag>
            <div class="note-actions">
              <button class="action-btn" @click.stop="editNote(note.noteId)">
                <EditPen class="action-icon" />
              </button>
              <button class="action-btn" @click.stop="deleteNote(note.noteId)">
                <FolderDelete class="action-icon" />
              </button>
            </div>
          </div>
        </el-card>
      </div>
    </div>

    <el-dialog
      v-model="showAddModal"
      title="新建笔记"
      width="600px"
      top="5vh"
      class="add-note-dialog"
      :close-on-click-modal="false"
    >
      <div class="note-form">
        <el-form :model="newNote">
          <el-form-item label="标题" required>
            <el-input
              v-model="newNote.topic"
              placeholder="请输入笔记标题"
              size="large"
            />
          </el-form-item>

          <el-form-item label="内容" required>
            <div ref="vditorRef" class="vditor-container"></div>
          </el-form-item>
        </el-form>
      </div>

      <template #footer>
        <el-button @click="closeModal">取消</el-button>
        <el-button type="primary" @click="submitNew" :loading="submitting">提交</el-button>
      </template>
    </el-dialog>
  </AppLayout>
</template>

<script setup>
import AppLayout from '@/components/layout/AppLayout.vue'
import axiosAPI from '@/utils/api/axios.js'
import { EditPen, FolderDelete, Plus } from '@element-plus/icons-vue'
import { ElButton, ElCard, ElDialog, ElEmpty, ElForm, ElFormItem, ElInput, ElMessage, ElMessageBox, ElSkeleton, ElTag } from 'element-plus'
import Vditor from 'vditor'
import 'vditor/dist/index.css'
import { nextTick, onMounted, onUnmounted, ref, watch } from 'vue'
import { useRouter } from 'vue-router'

const router = useRouter()

const notes = ref([])
const loading = ref(true)
const showAddModal = ref(false)
const submitting = ref(false)

const newNote = ref({
  topic: '',
  content: ''
})

const vditorRef = ref(null)
const vditorInstance = ref(null)

const formatDate = (timeString) => {
  if (!timeString) return ''
  const date = new Date(timeString)
  const year = date.getFullYear()
  const month = String(date.getMonth() + 1).padStart(2, '0')
  const day = String(date.getDate()).padStart(2, '0')
  return `${year}-${month}-${day}`
}

const getAllNotes = () => {
  loading.value = true
  
  axiosAPI.get('/user/notes/list')
    .then(response => {
      const res = response.data
      notes.value = (res.data && res.data.records) || []
    })
    .catch(error => {
      console.error('请求失败', error)
      notes.value = []
    })
    .finally(() => {
      loading.value = false
    })
}

const goToNote = (id) => {
  router.push(`/notes/${id}`)
}

const editNote = (id) => {
  router.push(`/notes/edit/${id}`)
}

const deleteNote = async (id) => {
  try {
    await ElMessageBox.confirm(
      '确定要删除这条笔记吗？',
      '提示',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )
    
    const response = await axiosAPI.delete(`/user/notes/delete/${id}`)
    if (response.data.code === 1) {
      ElMessage.success('删除成功')
      getAllNotes()
    } else {
      ElMessage.error('删除失败')
    }
  } catch (error) {
    if (error !== 'cancel') {
      console.error('删除失败', error)
      ElMessage.error('删除失败')
    }
  }
}

const submitNew = async () => {
  if (!newNote.value.topic.trim()) {
    ElMessage.warning('请输入笔记标题')
    return
  }
  
  if (vditorInstance.value) {
    newNote.value.content = vditorInstance.value.getValue()
  }
  
  submitting.value = true
  
  try {
    const response = await axiosAPI.post('/user/notes/insert', {
      noteTopic: newNote.value.topic,
      noteContent: newNote.value.content
    })
    
    if (response.data.code === 1) {
      ElMessage.success('笔记添加成功')
      getAllNotes()
      closeModal()
    } else {
      ElMessage.error('笔记添加失败')
    }
  } catch (error) {
    console.error('请求失败', error)
    ElMessage.error('请求失败')
  } finally {
    submitting.value = false
  }
}

const initVditor = () => {
  nextTick(() => {
    if (vditorRef.value && !vditorInstance.value) {
      vditorInstance.value = new Vditor(vditorRef.value, {
        mode: 'sv',
        preview: { show: true, theme: 'light' },
        toolbar: ['emoji', 'headings', 'bold', 'italic', 'link', 'list', 'code', 'line'],
        height: 300,
        cache: { enable: false },
        blur: () => {
          if (vditorInstance.value) {
            newNote.value.content = vditorInstance.value.getValue()
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
  newNote.value = { topic: '', content: '' }
  destroyVditor()
}

onMounted(() => {
  getAllNotes()
  
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
.notes-page {
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

.loading-state,
.empty-state {
  background: var(--bg-card);
  border-radius: var(--radius-lg);
  padding: var(--spacing-xl);
  border: 1px solid var(--border-color);
}

.notes-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(340px, 1fr));
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
  -webkit-line-clamp: 4;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.note-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-top: var(--spacing-md);
}

.note-actions {
  display: flex;
  gap: var(--spacing-sm);
}

.action-btn {
  width: 32px;
  height: 32px;
  border: none;
  background: transparent;
  border-radius: var(--radius-md);
  display: flex;
  justify-content: center;
  align-items: center;
  cursor: pointer;
  transition: all var(--transition-fast);
}

.action-btn:hover {
  background: rgba(249, 115, 22, 0.1);
}

.action-icon {
  width: 16px;
  height: 16px;
  color: var(--text-tertiary);
}

.action-btn:hover .action-icon {
  color: var(--primary-500);
}

.add-note-dialog {
  border-radius: var(--radius-xl);
}

:deep(.add-note-dialog .el-dialog__header) {
  background: linear-gradient(135deg, var(--primary-50) 0%, var(--secondary-50) 100%);
  border-radius: var(--radius-xl) var(--radius-xl) 0 0;
}

:deep(.add-note-dialog .el-dialog__title) {
  font-weight: var(--font-semibold);
}

.note-form {
  max-height: 50vh;
  overflow-y: auto;
}

.vditor-container {
  border: 1px solid var(--border-color);
  border-radius: var(--radius-lg);
  overflow: hidden;
  min-height: 250px;
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
  
  .notes-grid {
    grid-template-columns: 1fr;
  }
}

@media (max-width: 480px) {
  .page-title h1 {
    font-size: var(--text-xl);
  }
}
</style>