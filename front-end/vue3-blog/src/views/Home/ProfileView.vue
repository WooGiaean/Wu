<template>
  <div class="container" id="profile">
    <leftNav></leftNav>

    <!-- 右侧个人信息内容 -->
    <main class="right-column">
      <div class="content-wrapper">
        <!-- 页面标题 -->
        <div class="section-header">
          <h2>👤 关于我</h2>
        </div>

        <!-- 个人信息卡片 -->
        <el-divider>
          个人基本信息
        </el-divider>
        <div class="profile-card">
          <!-- 头像区域 -->
          <div class="avatar-section">
            <img :src="getDefaultUserAvatar(userInfo)"
                 alt="用户头像"
                 class="profile-avatar">
            <h3>{{ userInfo.userNickname || '用户' }}</h3>
            <p class="user-email">{{ userInfo.userEmail || '未设置邮箱' }}</p>
<!--            <button class="btn btn-secondary" @click="showEditModal = true">编辑资料</button>-->
            <el-button @click="showEditModal = true" type="primary">
              编辑资料
            </el-button>
          </div>

          <!-- 个人信息详情 -->
          <div class="info-section">
            <h4>个人资料</h4>
            <div class="info-item">
              <span class="info-label">用户名：</span>
              <span class="info-value">{{ userInfo.userName || '未设置' }}</span>
            </div>
            <div class="info-item">
              <span class="info-label">昵称：</span>
              <span class="info-value">{{ userInfo.userNickname || '未设置' }}</span>
            </div>
            <div class="info-item">
              <span class="info-label">邮箱：</span>
              <span class="info-value">{{ userInfo.userEmail || '未设置' }}</span>
            </div>
            <div class="info-item">
              <span class="info-label">注册时间：</span>
              <span class="info-value">{{ formatDate(userInfo.userCreateTime) }}</span>
            </div>
            <div class="info-item">
              <span class="info-label">上次登录：</span>
              <span class="info-value">{{ formatDate(userInfo.userLastLoginTime) }}</span>
            </div>
          </div>
        </div>

        <!-- 最近活动 -->
        <el-divider>
          最近活动
        </el-divider>
        <div class="activity-section">
          <div class="section-header">
            <h3>📊 最近活动</h3>
          </div>
          <div class="activity-stats">
            <div class="stat-card">
              <div class="stat-number">{{ userInfo.articleCount || 0 }}</div>
              <div class="stat-label">文章</div>
            </div>
            <div class="stat-card">
              <div class="stat-number">{{ userInfo.noteCount || 0 }}</div>
              <div class="stat-label">笔记</div>
            </div>
            <div class="stat-card">
              <div class="stat-number">{{ userInfo.commentCount || 0 }}</div>
              <div class="stat-label">评论</div>
            </div>
          </div>
        </div>

        <!-- 最近文章 -->
        <el-divider>
          最新文章
        </el-divider>
        <div class="recent-section">
          <div class="section-header">
            <h3>📚 最近文章</h3>
            <a href="/articles">查看全部 &rarr;</a>
          </div>
          <div v-if="recentArticles.length === 0" class="empty-state">
<!--            暂无文章-->
            <el-empty description="暂无文章"></el-empty>
          </div>

          <div v-else class="recent-articles">
            <div class="recent-item" v-for="article in recentArticles" :key="article.articleId" @click="goToArticle(article.articleId)">
              <h4>{{ article.articleTitle }}</h4>
              <p class="recent-meta">{{ formatDate(article.articleCreateTime) }}</p>
            </div>
          </div>
        </div>

        <!-- 最近笔记 -->
        <el-divider>
          最新笔记
        </el-divider>
        <div class="recent-section">
          <div class="section-header">
            <h3>✍️ 最近笔记</h3>
            <a href="/notes">查看全部 &rarr;</a>
          </div>
          <div v-if="recentNotes.length === 0" class="empty-state">
<!--            暂无笔记-->
            <el-empty description="暂无笔记"></el-empty>
          </div>
          <div v-else class="recent-notes">
            <div class="recent-item" v-for="note in recentNotes" :key="note.noteId" @click="goToNote(note.noteId)">
              <h4>{{ note.noteTopic }}</h4>
              <p class="recent-meta">{{ formatDate(note.noteCreateTime) }}</p>
            </div>
          </div>
        </div>
      </div>
    </main>

    <!-- 编辑资料弹窗 -->
    <div class="modal" v-if="showEditModal" @click="closeEditModal">
      <div class="modal-dialog" @click.stop>
        <div class="modal-content">
          <div class="modal-header">
            <h5 class="modal-title">编辑个人资料</h5>
            <button type="button" class="btn-close" @click="closeEditModal">&times;</button>
          </div>

          <div class="modal-body">
<!--            <form>
              &lt;!&ndash; 昵称 &ndash;&gt;
              <div class="mb-3">
                <label for="nickname" class="form-label">昵称</label>
&lt;!&ndash;                <input type="text" class="form-control" id="nickname" v-model="editForm.userNickname" placeholder="请输入昵称">&ndash;&gt;
                <el-form-item label="昵称">
                  <el-input v-model="userInfo.userNickname" placeholder="昵称" />
                </el-form-item>
              </div>

              &lt;!&ndash; 邮箱 &ndash;&gt;
              <div class="mb-3">
                <label for="email" class="form-label">邮箱</label>
&lt;!&ndash;                <input type="email" class="form-control" id="email" v-model="editForm.userEmail" placeholder="请输入邮箱">&ndash;&gt;
                <el-form-item label="邮箱">
                  <el-input v-model="userInfo.userEmail" placeholder="邮箱" />
                </el-form-item>
              </div>

              &lt;!&ndash; 头像上传 &ndash;&gt;
              <div class="mb-3">
                <label for="avatar" class="form-label">头像</label>
                <input type="file" class="form-control" id="avatar" accept="image/*" @change="handleAvatarUpload">
              </div>
            </form>-->

            <el-form :model="userInfo">
              <el-form-item label="昵称">
                <el-input v-model="userInfo.userNickname" placeholder="昵称" />
              </el-form-item>

              <el-form-item label="邮箱">
                <el-input v-model="userInfo.userEmail" placeholder="邮箱" />
              </el-form-item>

              <el-form-item label="头像">
<!--                <el-input v-model="userInfo.userAvatar" placeholder="头像" />-->

                <el-upload  class="avatar-uploader"

                :show-file-list="false"
                :on-success="handleAvatarUpload"
                :auto-upload="true">
<!--  action="/api/images/upload"-->
                  <el-avatar :src="userInfo.userAvatar? `/uploaded-images/${userInfo.userAvatar}`
                  :'@/assets/images/blog_avatar2.png'">
                  </el-avatar>

                </el-upload>
              </el-form-item>


            </el-form>
          </div>

          <div class="modal-footer">
            <button type="button" class="btn btn-secondary" @click="closeEditModal">取消</button>
            <button type="button" class="btn btn-primary" @click="saveProfile">保存</button>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import  router  from '@/router/index.js'
import axiosAPI from '@/utils/api/axios.js'
import leftNav from '@/components/layout/leftNav.vue'
import { ElForm, ElFormItem, ElInput, ElButton, ElUpload
  , ElMessage, ElSkeleton, ElEmpty, ElAvatar, ElDivider } from 'element-plus'
//import '@/assets/css/home.css'
import { useUserInfoStore } from '@/stores/modules/userInfo.js'
import { getDefaultUserAvatar } from '@/utils/common/setPics.js'



//const router = useRouter()
const userStore = useUserInfoStore()
// 数据状态
const userInfo = ref({})
//const userStats = ref({})
const recentArticles = ref([])
const recentNotes = ref([])
const showEditModal = ref(false)
const editForm = ref({})

// 格式化日期函数
const formatDate = (timeString) => {
  if(!timeString) return '未设置'
  const date = new Date(timeString)
  const year = date.getFullYear()
  const month = String(date.getMonth() + 1).padStart(2, '0')
  const day = String(date.getDate()).padStart(2, '0')
  return `${year}-${month}-${day}`
}

// 获取用户信息

const getUserInfo = () => {
 /* axiosAPI.get('/user/profile')
    .then(response => {
      console.log('用户信息:', response.data.data)
      if (response.data.data) {
        userInfo.value = response.data.data
        editForm.value = { ...response.data.data }
      }
    })
    .catch(error => {
      console.error('获取用户信息失败:', error)
    })*/

  //userInfo.value= JSON.parse(sessionStorage.getItem('user')||'{}')
  const userInfoStore = userStore.getUserInfo();
 // const tempUserId = userInfoStore.userId

  if(!userInfoStore) {
    axiosAPI.get('/user/profile')
      .then(res=>{
        if(res.data.code===1){
          userInfo.value = res.data.data
          editForm.value = { ...res.data.data }
          const token = userStore.getToken()
          userStore.setUserAndToken(res.data.data,token)
        }
      }).catch(err=>{
      console.error('获取用户信息失败:', err)
    })
  }
  userInfo.value = userInfoStore
  editForm.value = { userInfoStore }
  console.log('用户信息:', userInfo.value)
}

// 获取用户统计信息
/*const getUserInfoDetail=()=>{
  axiosAPI.get('/user/info')
    .then(response => {
      console.log('用户统计信息:', response.data.data)
    if (response.data.code===1) {
        userInfo.value = response.data.data
        editForm.value = { ...response.data.data }
      }
    })
}*/

// 获取最近文章
const getRecentArticles = () => {
  axiosAPI.get('/home/latestArticles')
    .then(response => {
      if (response.data.data && response.data.data.records) {
        recentArticles.value = response.data.data.records.slice(0, 3)
      }
    })
    .catch(error => {
      console.error('获取最近文章失败:', error)
      ElMessage.error('获取最近文章失败')
    })
}

// 获取最近笔记
const getRecentNotes = () => {
  axiosAPI.get('/home/latestNotes')
    .then(response => {
      if (response.data.data && response.data.data.records) {
        recentNotes.value = response.data.data.records.slice(0, 3)
      }
    })
    .catch(error => {
      console.error('获取最近笔记失败:', error)
    })
}

// 处理头像上传
const handleAvatarUpload = (event) => {
  const file = event.target.files[0]
  if (file) {
    const formData = new FormData()
    formData.append('file', file)
    axiosAPI.post('/images/upload', formData, {
      headers: {'Content-Type': 'multipart/form-data'}
    }).then(response => {
      if (response.data.code == 1) {
        editForm.value.userAvatar = response.data.data
      } else {
        alert("头像上传失败：" + (response.data.msg || "Unknown errors"))
      }
    }).catch(error => {
      console.error("头像上传失败", error)
      alert("头像上传失败")
    })
  }
}

// 保存个人资料
const saveProfile = async () => {
  try {
    const response = await axiosAPI.put('/user/profile', {
      userNickname: editForm.value.userNickname,
      userEmail: editForm.value.userEmail,
      userAvatar: editForm.value.userAvatar
    })

    if (response.data.code === 1) {
      //alert("资料更新成功")
      ElMessage.success('资料更新成功')
      getUserInfo() // 重新获取用户信息
      closeEditModal()
    } else {
      ElMessage.error('资料更新失败' + (response.data.msg || "Unknown errors"))
      //alert("资料更新失败：" + (response.data.msg || "Unknown errors"))
    }
  } catch (error) {
    console.error("请求发送错误", error)
    //alert("请求发送错误：" + (error.message || "Unknown errors"))
    ElMessage.error('请求发送错误')
  }
}

// 跳转到文章详情
const goToArticle = (id) => {
  router.push(`/articles/${id}`)
}

// 跳转到笔记详情
const goToNote = (id) => {
  router.push(`/notes/${id}`)
}

// 关闭编辑模态框
const closeEditModal = () => {
  showEditModal.value = false
  // 重置表单
  editForm.value = { ...userInfo.value }
}

// 页面加载时获取数据
onMounted(() => {
  getUserInfo()
//  getUserStats()
  getRecentArticles()
  getRecentNotes()
})
</script>

<style scoped>
/* 容器布局 */
.container {
  display: flex;
  flex: 1;
  padding: 20px;
  gap: 30px;
  max-width: 1200px;
  margin: 0 auto;
  width: 100%;
  align-items: flex-start;
}

/* 主内容区域 */
.right-column {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 30px;
}

/* 内容包装器 */
.content-wrapper {
  display: flex;
  flex-direction: column;
  gap: 30px;
}

/* 页面标题 */
.section-header {
  text-align: center;
  padding: 30px 0;
  background: var(--card-bg);
  border-radius: 12px;
  border: 1px solid var(--border-color);
  box-shadow: var(--card-shadow);
}

.section-header h2 {
  color: var(--text-primary);
  font-size: 28px;
  font-weight: bold;
}

.section-header h3 {
  color: var(--text-primary);
  font-size: 20px;
  font-weight: 600;
  margin: 0;
}

.section-header a {
  color: var(--accent-color);
  text-decoration: none;
  font-size: 14px;
  font-weight: 500;
  transition: all 0.3s ease;
}

.section-header a:hover {
  text-decoration: underline;
  transform: translateX(5px);
}

.profile-card {
  background: var(--card-bg);
  border-radius: 12px;
  padding: 30px;
  margin-bottom: 30px;
  box-shadow: var(--card-shadow);
  border: 1px solid var(--border-color);
}

.avatar-section {
  text-align: center;
  margin-bottom: 30px;
}

.profile-avatar {
  width: 150px;
  height: 150px;
  border-radius: 50%;
  object-fit: cover;
  margin-bottom: 20px;
  border: 3px solid var(--accent-color);
}

.avatar-section h3 {
  margin: 0 0 10px 0;
  font-size: 24px;
  color: var(--text-primary);
}

.user-email {
  color: var(--text-secondary);
  margin-bottom: 20px;
}

.info-section {
  border-top: 1px solid var(--border-color);
  padding-top: 20px;
}

.info-section h4 {
  margin-bottom: 20px;
  color: var(--text-primary);
}

.info-item {
  display: flex;
  margin-bottom: 15px;
  align-items: center;
}

.info-label {
  width: 100px;
  color: var(--text-secondary);
  font-weight: 500;
}

.info-value {
  color: var(--text-primary);
  flex: 1;
}

.activity-section {
  background: var(--card-bg);
  border-radius: 12px;
  padding: 30px;
  margin-bottom: 30px;
  box-shadow: var(--card-shadow);
  border: 1px solid var(--border-color);
}

.activity-stats {
  display: flex;
  gap: 20px;
  margin-top: 20px;
}

.stat-card {
  flex: 1;
  text-align: center;
  padding: 20px;
  background: var(--bg-primary);
  border-radius: 8px;
  border: 1px solid var(--border-color);
  transition: all 0.3s ease;
  width: 50%;
}

.stat-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 10px 20px rgba(0, 0, 0, 0.1);
}

.stat-number {
  font-size: 28px;
  font-weight: bold;
  color: var(--accent-color);
  margin-bottom: 5px;
}

.stat-label {
  color: var(--text-secondary);
  font-size: 14px;
}

.recent-section {
  background: var(--card-bg);
  border-radius: 12px;
  padding: 30px;
  margin-bottom: 30px;
  box-shadow: var(--card-shadow);
  border: 1px solid var(--border-color);
}

.recent-articles,
.recent-notes {
  margin-top: 20px;
}

.recent-item {
  padding: 15px;
  background: var(--bg-primary);
  border-radius: 8px;
  margin-bottom: 10px;
  border: 1px solid var(--border-color);
  cursor: pointer;
  transition: all 0.3s ease;
}

.recent-item:hover {
  transform: translateX(5px);
  box-shadow: 0 5px 15px rgba(0, 0, 0, 0.1);
}

.recent-item h4 {
  margin: 0 0 5px 0;
  color: var(--text-primary);
  font-size: 16px;
}

.recent-meta {
  color: var(--text-secondary);
  font-size: 12px;
  margin: 0;
}

/* 空状态 */
.empty-state {
  text-align: center;
  padding: 40px;
  color: var(--text-secondary);
  background: var(--bg-primary);
  border-radius: 12px;
  border: 1px solid var(--border-color);
  box-shadow: var(--card-shadow);
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
@media (max-width: 1200px) {
  .container {
    flex-direction: column;
  }

  .left-column {
    width: 100%;
  }
}

@media (max-width: 768px) {
  .container {
    padding: 10px;
    gap: 20px;
  }

  .section-header h2 {
    font-size: 24px;
  }

  .activity-stats {
    flex-direction: column;
  }

  .stat-card {
    margin-bottom: 10px;
  }
}
</style>
