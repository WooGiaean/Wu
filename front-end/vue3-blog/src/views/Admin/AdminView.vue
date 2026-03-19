<template>
  <div id="main_Content">

    <div class="sidebar">
      <div class="sidebar-header">
        <h4>管理员中心</h4>
        <img src="@/assets/images/blog_avater1.png" alt="管理员头像" class="sidebar-avatar">
        <p style="margin: 0; color: #8B6A4F;">管理员</p>
      </div>

      <ul class="sidebar-nav">
        <li><a href="#">仪表盘</a></li>
        <li><a href="#" @click.prevent="switchPage('user')" :class="{active: currentPage === 'user'}">用户管理</a></li>
        <li><a href="#" @click.prevent="switchPage('article')" :class="{active: currentPage === 'article'}">文章管理</a></li>
        <li><a href="#" @click.prevent="switchPage('note')" :class="{active: currentPage === 'note'}">笔记管理</a></li>
        <li><a href="#">数据统计</a></li>
        <li><a href="/admin/logout">退出登录</a></li>
      </ul>

      <!-- 切换浅/深色模式 -->
      <div class="sidebar-footer">
        <button id="theme-toggle" class="btn btn-sm btn-outline-secondary w-100 mb-2">
          🌙 深色模式
        </button>
      </div>

    </div>

    <!-- 主要内容 -->
    <div class="main-content">

      <div class="container-fluid">

        <div class="row">

          <div class="col-12">

            <!-- 用户展示列表 -->
            <div class="card" v-if="currentPage === 'user'">
              <div class="card-header">
                <!-- 导航头和标题 -->
                <div class="d-flex justify-content-between align-items-center">
                  <h5>用户管理</h5>
                  <div>
                    <button class="btn btn-primary btn-sm me-2" @click="showAddUserModal = true">添加用户</button>
                    <button class="btn btn-danger btn-sm" v-if="selectedUsers.length > 0" @click="batchDeleteUsers">批量删除</button>
                  </div>
                </div>
              </div>
              <div class="card-body">
                <div class="search-bar">
                  <!-- 搜索框 -->
                  <div class="input-group mb-3">
                    <input v-model="searchUser" type="text" class="form-control" placeholder="搜索用户..." aria-label="Search user">
                    <button class="btn btn-outline-secondary" type="button" @click="searchUserByKeyword">搜索</button>
                  </div>
                </div>

                <div class="table-responsive">
                  <!-- 标题和内容 -->
                  <table class="table table-hover">
                    <thead>
                    <!-- 列标题 -->
                    <tr>
                      <th><input type="checkbox" v-model="selectAll" @change="selectAllUsers"></th>
                      <th>用户ID</th>
                      <th>用户名</th>
                      <th>邮箱</th>
                      <th>角色</th>
                      <th>注册时间</th>
                      <th>文章数</th>
                      <th>笔记数</th>
                      <th>状态</th>
                      <th>操作</th>
                    </tr>
                    </thead>

                    <tbody>
                    <tr v-for="user in pageUsers" :key="user.userId">
                      <td><input type="checkbox" v-model="selectedUsers" :value="user.userId"></td>
                      <td>{{ user.userId }}</td>
                      <td><span class="user-name">{{ user.userName }}</span></td>
                      <td>{{ user.userEmail }}</td>
                      <td>{{ user.userRole }}</td>
                      <td>{{ user.userRegisterTime }}</td>
                      <td>{{ user.articleCount }}</td>
                      <td>{{ user.noteCount }}</td>
                      <td>
                        <span class="badge bg-success status-active" v-if="user.userStatus === 1">活跃</span>
                        <span class="badge bg-danger status-active" v-if="user.userStatus === 0">禁用</span>
                      </td>
                      <td>
                        <button class="btn btn-sm btn-outline-primary me-1" @click="editUser(user)" data-bs-toggle="modal" data-bs-target="#editUserModal">编辑</button>
                        <button class="btn btn-sm btn-outline-info me-1" @click="viewUser(user)" data-bs-toggle="modal" data-bs-target="#viewUserModal">详情</button>
                        <button class="btn btn-sm btn-outline-danger" @click="deleteUser(user.userId)">删除</button>
                      </td>
                    </tr>
                    </tbody>
                  </table>

                </div>
              </div>
            </div>

            <!-- 文章展示列表 -->
            <div class="card" v-if="currentPage === 'article'">
              <div class="card-header">
                <!-- 导航头和标题 -->
                <div class="d-flex justify-content-between align-items-center">
                  <h5>文章管理</h5>
                  <button class="btn btn-primary btn-sm">添加文章</button>
                </div>
              </div>
              <div class="card-body">
                <div class="search-bar">
                  <!-- 搜索框 -->
                  <div class="input-group mb-3">
                    <input type="text" class="form-control" placeholder="搜索文章..." aria-label="Search user">
                    <button class="btn btn-outline-secondary" type="button">搜索</button>
                  </div>
                </div>

                <div class="table-responsive">
                  <!-- 标题和内容 -->
                  <table class="table table-hover">
                    <thead>
                    <!-- 列标题 -->
                    <tr>
                      <th>文章ID</th>
                      <th>文章标题</th>
                      <th>用户名</th>
                      <th>创建时间</th>
                      <th>状态</th>
                      <th>操作</th>
                    </tr>
                    </thead>

                    <tbody>
                    <tr v-for="article in articles" :key="article.articleId">
                      <td>{{ article.articleId }}</td>
                      <td>{{ article.articleTitle }}</td>
                      <td><span class="user-name">{{ article.blogger }}</span></td>
                      <td>{{ article.articleUpdateTime }}</td>
                      <td>
                        <span class="badge bg-success status-active" v-if="article.articleStatus === 1">发布</span>
                        <span class="badge bg-danger status-active" v-if="article.articleStatus === 0">草稿</span>
                      </td>
                      <td>
                        <button class="btn btn-sm btn-outline-primary">编辑</button>
                        <button class="btn btn-sm btn-outline-danger">删除</button>
                      </td>
                    </tr>
                    </tbody>
                  </table>

                </div>
              </div>
            </div>

            <!-- 笔记展示列表 -->
            <div class="card" v-if="currentPage === 'note'">
              <div class="card-header">
                <!-- 导航头和标题 -->
                <div class="d-flex justify-content-between align-items-center">
                  <h5>笔记管理</h5>
                  <button class="btn btn-primary btn-sm">添加笔记</button>
                </div>
              </div>
              <div class="card-body">
                <div class="search-bar">
                  <!-- 搜索框 -->
                  <div class="input-group mb-3">
                    <input type="text" class="form-control" placeholder="搜索笔记..." aria-label="Search user">
                    <button class="btn btn-outline-secondary" type="button">搜索</button>
                  </div>
                </div>

                <div class="table-responsive">
                  <!-- 标题和内容 -->
                  <table class="table table-hover">
                    <thead>
                    <!-- 列标题 -->
                    <tr>
                      <th>笔记ID</th>
                      <th>笔记标题</th>
                      <th>用户名</th>
                      <th>创建时间</th>
                      <th>操作</th>
                    </tr>
                    </thead>

                    <tbody>
                    <tr v-for="note in notes" :key="note.noteId">
                      <td>{{ note.noteId }}</td>
                      <td>{{ note.noteTopic }}</td>
                      <td><span class="user-name">{{ note.noteAuthor }}</span></td>
                      <td>{{ note.noteUpdateTime }}</td>
                      <td>
                        <button class="btn btn-sm btn-outline-primary">编辑</button>
                        <button class="btn btn-sm btn-outline-danger">删除</button>
                      </td>
                    </tr>
                    </tbody>
                  </table>

                </div>
              </div>
            </div>


          </div>


          <div class="pagination-container" v-if="currentPageType.totalPages >= 1">
            <div class="pagination">
              <!-- 上一页按钮 -->
              <button
                      id="prev-page"
                      class="page-btn"
                      :disabled="currentPageType.nowPage === 1"
                      @click="prePage()"
              >
                <span class="icon">❮</span> 上一页
              </button>

              <!-- 页码显示区域 -->
              <div class="page-numbers">
                <!-- 中间页码 -->
                <span
                        v-for="page in pageToShow"
                        :key="page"
                        class="page-item"
                        :class="{ 'active': currentPageType.nowPage === page }"
                        @click="page !== '...' ? goToPage(page) : null"
                        :style="{ cursor: page === '...' ? 'default' : 'pointer' }"
                >

                  <p class="page-numbers">
                    {{ page }}
                  </p>
                </span>

                <!-- 省略号（如果需要） -->
                <span v-if="pageToShow.length > 2 && pageToShow[pageToShow.length - 1] !== currentPageType.totalPages"
                      class="page-item ellipsis">...</span>

                <!-- 尾页 -->
              </div>

              <!-- 下一页按钮 -->
              <button
                      id="next-page"
                      class="page-btn"
                      :disabled="currentPageType.nowPage === currentPageType.totalPages"
                      @click="nextPage()"
              >
                下一页 <span class="icon">❯</span>
              </button>
            </div>
          </div>


        </div>
      </div>
    </div>


    <!-- 添加用户模态框 -->
    <div class="modal" v-if="showAddUserModal" tabindex="-1" aria-labelledby="addUserModalLabel" aria-hidden="true">
      <div class="modal-dialog">
        <div class="modal-content">
          <div class="modal-header">
            <h5 class="modal-title" id="addUserModalLabel">添加用户</h5>
            <button type="button" class="btn-close" @click="showAddUserModal = false" aria-label="Close"></button>
          </div>
          <div class="modal-body">
            <form>
              <div class="mb-3">
                <label for="userName" class="form-label">用户名</label>
                <input type="text" class="form-control" id="userName" v-model="newUser.userName" required>
              </div>
              <div class="mb-3">
                <label for="userPassword" class="form-label">密码</label>
                <input type="password" class="form-control" id="userPassword" v-model="newUser.userPassword" required>
              </div>
              <div class="mb-3">
                <label for="userEmail" class="form-label">邮箱</label>
                <input type="email" class="form-control" id="userEmail" v-model="newUser.userEmail" required>
              </div>
              <div class="mb-3">
                <label for="userRole" class="form-label">角色</label>
                <select class="form-select" id="userRole" v-model="newUser.userRole">
                  <option value="user">普通用户</option>
                  <option value="admin">管理员</option>
                </select>
              </div>
            </form>
          </div>
          <div class="modal-footer">
            <button type="button" class="btn btn-secondary" @click="showAddUserModal = false">取消</button>
            <button type="button" class="btn btn-primary" @click="addUser">确定</button>
          </div>
        </div>
      </div>
    </div>

    <!-- 编辑用户模态框 -->
    <div class="modal" v-if="showEditUserModal" tabindex="-1" aria-labelledby="editUserModalLabel" aria-hidden="true">
      <div class="modal-dialog">
        <div class="modal-content">
          <div class="modal-header">
            <h5 class="modal-title" id="editUserModalLabel">编辑用户</h5>
            <button type="button" class="btn-close" @click="showEditUserModal = false" aria-label="Close"></button>
          </div>
          <div class="modal-body">
            <form>
              <div class="mb-3">
                <label for="editUserName" class="form-label">用户名</label>
                <input type="text" class="form-control" id="editUserName" v-model="editUserForm.userName" required>
              </div>
              <div class="mb-3">
                <label for="editUserEmail" class="form-label">邮箱</label>
                <input type="email" class="form-control" id="editUserEmail" v-model="editUserForm.userEmail" required>
              </div>
              <div class="mb-3">
                <label for="editUserRole" class="form-label">角色</label>
                <select class="form-select" id="editUserRole" v-model="editUserForm.userRole">
                  <option value="user">普通用户</option>
                  <option value="admin">管理员</option>
                </select>
              </div>
              <div class="mb-3">
                <label for="editUserStatus" class="form-label">状态</label>
                <select class="form-select" id="editUserStatus" v-model="editUserForm.userStatus">
                  <option value="1">活跃</option>
                  <option value="0">禁用</option>
                </select>
              </div>
            </form>
          </div>
          <div class="modal-footer">
            <button type="button" class="btn btn-secondary" @click="showEditUserModal = false">取消</button>
            <button type="button" class="btn btn-primary" @click="updateUser">确定</button>
          </div>
        </div>
      </div>
    </div>

    <!-- 查看用户详情模态框 -->
    <div class="modal" v-if="showViewUserModal" tabindex="-1" aria-labelledby="viewUserModalLabel" aria-hidden="true">
      <div class="modal-dialog">
        <div class="modal-content">
          <div class="modal-header">
            <h5 class="modal-title" id="viewUserModalLabel">用户详情</h5>
            <button type="button" class="btn-close" @click="showViewUserModal = false" aria-label="Close"></button>
          </div>
          <div class="modal-body">
            <div class="mb-3">
              <label class="form-label">用户ID</label>
              <p class="form-control-plaintext">{{ viewUserForm.userId }}</p>
            </div>
            <div class="mb-3">
              <label class="form-label">用户名</label>
              <p class="form-control-plaintext">{{ viewUserForm.userName }}</p>
            </div>
            <div class="mb-3">
              <label class="form-label">邮箱</label>
              <p class="form-control-plaintext">{{ viewUserForm.userEmail }}</p>
            </div>
            <div class="mb-3">
              <label class="form-label">角色</label>
              <p class="form-control-plaintext">{{ viewUserForm.userRole }}</p>
            </div>
            <div class="mb-3">
              <label class="form-label">注册时间</label>
              <p class="form-control-plaintext">{{ viewUserForm.userRegisterTime }}</p>
            </div>
            <div class="mb-3">
              <label class="form-label">文章数</label>
              <p class="form-control-plaintext">{{ viewUserForm.articleCount }}</p>
            </div>
            <div class="mb-3">
              <label class="form-label">笔记数</label>
              <p class="form-control-plaintext">{{ viewUserForm.noteCount }}</p>
            </div>
            <div class="mb-3">
              <label class="form-label">状态</label>
              <p class="form-control-plaintext">
                <span v-if="viewUserForm.userStatus === 1" class="badge bg-success">活跃</span>
                <span v-else class="badge bg-danger">禁用</span>
              </p>
            </div>
          </div>
          <div class="modal-footer">
            <button type="button" class="btn btn-secondary" @click="showViewUserModal = false">关闭</button>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import axios from 'axios'
import '../../assets/css/inforCenter.css'

// 页面状态
const currentPage = ref('user')

// 用户管理相关
const users = ref([])
const searchUser = ref('')
const selectedUsers = ref([])
const selectAll = ref(false)
const showAddUserModal = ref(false)
const showEditUserModal = ref(false)
const showViewUserModal = ref(false)

// 分页相关
const usersPage = ref({
  nowPage: 1,
  pageSize: 10,
  totalCount: 0,
  totalPages: 0,
  loading: false
})

const articles = ref([])
const articlesPage = ref({
  nowPage: 1,
  pageSize: 10,
  totalCount: 0,
  totalPages: 0,
  loading: false
})

const notes = ref([])
const notesPage = ref({
  nowPage: 1,
  pageSize: 10,
  totalCount: 0,
  totalPages: 0,
  loading: false
})

// 表单数据
const newUser = ref({
  userName: '',
  userPassword: '',
  userEmail: '',
  userRole: 'user',
  userStatus: 1
})

const editUserForm = ref({
  userId: '',
  userName: '',
  userEmail: '',
  userRole: '',
  userStatus: ''
})

const viewUserForm = ref({})

// 计算属性
const pageUsers = computed(() => {
  const { nowPage, pageSize } = usersPage.value
  const start = (nowPage - 1) * pageSize
  return users.value.slice(start, start + pageSize)
})

const currentPageType = computed(() => {
  switch (currentPage.value) {
    case 'user':
      return usersPage.value
    case 'article':
      return articlesPage.value
    case 'note':
      return notesPage.value
    default:
      return usersPage.value
  }
})

const pageToShow = computed(() => {
  const pages = []
  const maxPage = 5
  const currentPageData = currentPageType.value

  if (!currentPageData) {
    return []
  }

  const nowPage = currentPageData.nowPage
  const totalPages = currentPageData.totalPages

  if (totalPages <= maxPage) {
    for (let i = 1; i <= totalPages; i++) {
      pages.push(i)
    }
  } else {
    const startPage = Math.max(1, nowPage - Math.floor(maxPage / 2))
    const endPage = Math.min(totalPages, startPage + maxPage - 1)

    pages.push(1)

    if (startPage > 2) {
      pages.push('...')
    }

    for (let i = startPage; i <= endPage; i++) {
      pages.push(i)
    }

    if (endPage < totalPages - 1) {
      pages.push('...')
    }

    if (endPage < totalPages) {
      pages.push(totalPages)
    }
  }

  return pages
})

// 方法
const switchPage = (page) => {
  currentPage.value = page
  switch (page) {
    case 'user':
      allUsers()
      break
    case 'article':
      allArticles()
      break
    case 'note':
      allNotes()
      break
  }
}

const getPageType = () => {
  switch (currentPage.value) {
    case 'user':
      allUsers()
      break
    case 'article':
      allArticles()
      break
    case 'note':
      allNotes()
      break
  }
}

// 获取所有用户
const allUsers = async () => {
  usersPage.value.loading = true
  try {
    const response = await axios.get('/manage/users', {
      params: {
        page: usersPage.value.nowPage,
        pageSize: usersPage.value.pageSize
      }
    })

    if (response.data.code === 1) {
      users.value = response.data.data.records
      usersPage.value.totalCount = response.data.data.total
      usersPage.value.totalPages = Math.ceil(usersPage.value.totalCount / usersPage.value.pageSize)
    } else {
      alert('获取用户列表失败: ' + response.data.message)
      users.value = []
    }
  } catch (error) {
    console.error(error)
    alert('错误信息：' + error)
  } finally {
    usersPage.value.loading = false
  }
}

// 获取所有文章
const allArticles = async () => {
  articlesPage.value.loading = true
  try {
    const response = await axios.get('/manage/articles', {
      params: {
        page: articlesPage.value.nowPage,
        pageSize: articlesPage.value.pageSize
      }
    })

    if (response.data.code === 1) {
      articles.value = response.data.data.records
      articlesPage.value.totalCount = response.data.data.total
      articlesPage.value.totalPages = Math.ceil(articlesPage.value.totalCount / articlesPage.value.pageSize)
    } else {
      articles.value = []
    }
  } catch (error) {
    console.error(error)
    alert('错误信息：' + error)
  } finally {
    articlesPage.value.loading = false
  }
}

// 获取所有笔记
const allNotes = async () => {
  notesPage.value.loading = true
  try {
    const response = await axios.get('/manage/notes', {
      params: {
        page: notesPage.value.nowPage,
        pageSize: notesPage.value.pageSize
      }
    })

    if (response.data.code === 1) {
      notes.value = response.data.data.records
      notesPage.value.totalCount = response.data.data.total
      notesPage.value.totalPages = Math.ceil(notesPage.value.totalCount / notesPage.value.pageSize)
    } else {
      notes.value = []
    }
  } catch (error) {
    console.error(error)
    alert('错误信息：' + error)
  } finally {
    notesPage.value.loading = false
  }
}

// 分页方法
const goToPage = (page) => {
  if (page === '...' || page === currentPageType.value.nowPage) return
  currentPageType.value.nowPage = page
  getPageType()
}

const prePage = () => {
  let currentPageData = currentPageType.value
  if (currentPageData.nowPage > 1) {
    currentPageData.nowPage--
    getPageType()
  }
}

const nextPage = () => {
  let currentPageData = currentPageType.value
  if (currentPageData.nowPage < currentPageData.totalPages) {
    currentPageData.nowPage++
    getPageType()
  }
}

// 用户操作
const deleteUser = async (id) => {
  if (confirm('确定要删除用户吗？')) {
    try {
      const response = await axios.delete(`/manage/delete/user?id=${id}`)
      if (response.data.code === 1) {
        alert('删除成功')
        allUsers()
      } else {
        alert('删除失败')
      }
    } catch (error) {
      console.error(error)
    }
  }
}

const searchUserByKeyword = async () => {
  try {
    const response = await axios.post('/manage/search/users', {
      userName: searchUser.value,
      userNickname: searchUser.value,
      userEmail: searchUser.value
    })

    if (response.data.code === 1) {
      users.value = response.data.data.records
      usersPage.value.totalCount = response.data.data.total
      usersPage.value.totalPages = Math.ceil(usersPage.value.totalCount / usersPage.value.pageSize)
    } else {
      alert('搜索失败')
    }
  } catch (error) {
    console.error(error)
    alert('错误信息：' + error)
  } finally {
    searchUser.value = ""
    usersPage.value.loading = false
  }
}

const editUser = (user) => {
  editUserForm.value = {
    userId: user.userId,
    userName: user.userName,
    userEmail: user.userEmail,
    userRole: user.userRole,
    userStatus: user.userStatus
  }
  showEditUserModal.value = true
}

const viewUser = (user) => {
  viewUserForm.value = user
  showViewUserModal.value = true
}

const selectAllUsers = () => {
  if (selectAll.value) {
    selectedUsers.value = pageUsers.value.map(user => user.userId)
  } else {
    selectedUsers.value = []
  }
}

const batchDeleteUsers = async () => {
  if (confirm('确定要删除选中的用户吗？')) {
    try {
      const response = await axios.delete('/manage/delete/users', {
        params: {
          ids: selectedUsers.value.join(',')
        }
      })

      if (response.data.code === 1) {
        alert('批量删除成功')
        allUsers()
        selectedUsers.value = []
        selectAll.value = false
      } else {
        alert('批量删除失败')
      }
    } catch (error) {
      console.error(error)
      alert('错误信息：' + error)
    }
  }
}

const addUser = async () => {
  try {
    const response = await axios.post('/manage/add/user', newUser.value)

    if (response.data.code === 1) {
      alert('添加用户成功')
      allUsers()
      // 重置表单
      newUser.value = {
        userName: '',
        userPassword: '',
        userEmail: '',
        userRole: 'user',
        userStatus: 1
      }
      showAddUserModal.value = false
    } else {
      alert('添加用户失败：' + response.data.message)
    }
  } catch (error) {
    console.error(error)
    alert('错误信息：' + error)
  }
}

const updateUser = async () => {
  try {
    const response = await axios.put('/manage/update/user', editUserForm.value)

    if (response.data.code === 1) {
      alert('更新用户成功')
      allUsers()
      showEditUserModal.value = false
    } else {
      alert('更新用户失败：' + response.data.message)
    }
  } catch (error) {
    console.error(error)
    alert('错误信息：' + error)
  }
}

// 页面加载时初始化
onMounted(() => {
  usersPage.value.nowPage = 1
  getPageType()
})
</script>

<style scoped>
/* 组件特定样式 */
</style>
