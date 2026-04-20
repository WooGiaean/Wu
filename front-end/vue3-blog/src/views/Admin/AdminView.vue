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
<!--      <div class="sidebar-footer">
        <button id="theme-toggle" class="btn btn-sm btn-outline-secondary w-100 mb-2">
          🌙 深色模式
        </button>
      </div>-->

    </div>



    <!-- 主要内容 -->
    <div class="main-content">
      <!-- 统计卡片区域 -->
      <div class="stats-grid">
        <!-- 用户总数 -->
        <el-card class="stat-card">
          <div class="stat-content">
            <div class="stat-icon user-icon"></div>
            <div class="stat-info">
              <div class="stat-number">{{ stats.userCount }}</div>
              <div class="stat-label">用户总数</div>
            </div>
          </div>
        </el-card>
        <!-- 文章总数 -->
        <el-card class="stat-card">
          <div class="stat-content">
            <div class="stat-icon article-icon"></div>
            <div class="stat-info">
              <div class="stat-number">{{ stats.articleCount }}</div>
              <div class="stat-label">文章总数</div>
            </div>
          </div>
        </el-card>
        <!-- 笔记总数 -->
        <el-card class="stat-card">
          <div class="stat-content">
            <div class="stat-icon note-icon"></div>
            <div class="stat-info">
              <div class="stat-number">{{ stats.noteCount }}</div>
              <div class="stat-label">笔记总数</div>
            </div>
          </div>
        </el-card>
        <!-- 评论总数 -->
        <el-card class="stat-card">
          <div class="stat-content">
            <div class="stat-icon comment-icon"></div>
            <div class="stat-info">
              <div class="stat-number">{{ stats.commentCount }}</div>
              <div class="stat-label">评论总数</div>
            </div>
          </div>
        </el-card>
      </div>

      <!-- 工具栏区域 -->
      <div class="toolbar">
        <!-- 面包屑 -->
        <el-breadcrumb separator="/">
          <el-breadcrumb-item :to="{ path: '/' }">首页</el-breadcrumb-item>
          <el-breadcrumb-item>用户管理</el-breadcrumb-item>
        </el-breadcrumb>

        <!-- 搜索和筛选 -->
        <div class="toolbar-actions">
          <el-input
            v-model="searchKeyword"
            placeholder="搜索用户名、昵称或邮箱"
            style="width: 250px"
            clearable
          ></el-input>

          <el-select v-model="filterRole" placeholder="筛选角色" style="width: 150px" clearable>
            <el-option label="全部" value="" />
            <el-option label="普通用户" value="user" />
            <el-option label="管理员" value="admin" />
          </el-select>

          <el-button type="primary" @click="openAddDialog">新增</el-button>
          <el-button :disabled="selectedUsers.length === 0" @click="batchDelete">
            批量删除
          </el-button>
        </div>
      </div>

      <!-- 内容区域 -->
      <div class="content-area">
        <div class="container-fluid">
          <div class="row">
            <div class="col-12">
              <!-- 用户展示列表 -->
              <el-card v-if="currentPage === 'user'" class="content-card">
                <template #header>
                  <div class="card-header">
                    <h5>用户管理</h5>
                    <div class="header-actions">
                      <el-button type="primary" @click="showAddUserModal = true">添加用户</el-button>
                      <el-button :disabled="selectedUsers.length === 0" @click="batchDeleteUsers">批量删除</el-button>
                    </div>
                  </div>
                </template>

                <!-- Element Plus 表格 -->
                <el-table
                  :data="pageUsers"
                  style="width: 100%"
                  stripe
                  @selection-change="handleSelectionChange"
                >
                  <el-table-column type="selection" width="55" />
                  <el-table-column prop="userId" label="用户ID" width="80" />
                  <el-table-column prop="userName" label="用户名" />
                  <el-table-column prop="userEmail" label="邮箱" />
                  <el-table-column prop="userRole" label="角色" width="100">
                    <template #default="scope">
                      <el-tag :type="scope.row?.userRole === 'admin' ? 'danger' : 'primary'" size="small">
                        {{ scope.row?.userRole === 'admin' ? '管理员' : '普通用户' }}
                      </el-tag>
                    </template>
                  </el-table-column>
                  <el-table-column prop="userRegisterTime" label="注册时间" />
                  <el-table-column prop="articleCount" label="文章数" width="80" />
                  <el-table-column prop="noteCount" label="笔记数" width="80" />
                  <el-table-column prop="userStatus" label="状态" width="80">
                    <template #default="scope">
                      <el-tag :type="scope.row?.userStatus === 1 ? 'success' : 'danger'" size="small">
                        {{ scope.row?.userStatus === 1 ? '活跃' : '禁用' }}
                      </el-tag>
                    </template>
                  </el-table-column>
                  <el-table-column label="操作" width="180" fixed="right">
                    <template #default="scope">
                      <el-button type="primary" link @click="editUser(scope.row)">编辑</el-button>
                      <el-button type="info" link @click="viewUser(scope.row)">详情</el-button>
                      <el-button type="danger" link @click="deleteUser(scope.row?.userId)">删除</el-button>
                    </template>
                  </el-table-column>
                </el-table>

                <!-- 空状态 -->
                <el-empty v-if="pageUsers.length === 0" description="暂无用户数据" />
              </el-card>

              <!-- 文章展示列表 -->
              <el-card v-if="currentPage === 'article'" class="content-card">
                <template #header>
                  <div class="card-header">
                    <h5>文章管理</h5>
                    <el-button type="primary">添加文章</el-button>
                  </div>
                </template>

                <el-table :data="articles" style="width: 100%" stripe>
                  <el-table-column prop="articleId" label="文章ID" width="80" />
                  <el-table-column prop="articleTitle" label="文章标题" show-overflow-tooltip />
                  <el-table-column prop="blogger" label="作者" width="120" />
                  <el-table-column prop="articleUpdateTime" label="更新时间" width="180" />
                  <el-table-column prop="articleStatus" label="状态" width="100">
                    <template #default="scope">
                      <el-tag :type="scope.row?.articleStatus === 1 ? 'success' : 'info'" size="small">
                        {{ scope.row?.articleStatus === 1 ? '发布' : '草稿' }}
                      </el-tag>
                    </template>
                  </el-table-column>
                  <el-table-column label="操作" width="150" fixed="right">
                    <template #default="scope">
                      <el-button type="primary" link>编辑</el-button>
                      <el-button type="danger" link>删除</el-button>
                    </template>
                  </el-table-column>
                </el-table>

                <el-empty v-if="articles.length === 0" description="暂无文章数据" />
              </el-card>

              <!-- 笔记展示列表 -->
              <el-card v-if="currentPage === 'note'" class="content-card">
                <template #header>
                  <div class="card-header">
                    <h5>笔记管理</h5>
                    <el-button type="primary">添加笔记</el-button>
                  </div>
                </template>

                <el-table :data="notes" style="width: 100%" stripe>
                  <el-table-column prop="noteId" label="笔记ID" width="80" />
                  <el-table-column prop="noteTopic" label="笔记标题" show-overflow-tooltip />
                  <el-table-column prop="noteAuthor" label="作者" width="120" />
                  <el-table-column prop="noteUpdateTime" label="更新时间" width="180" />
                  <el-table-column label="操作" width="150" fixed="right">
                    <template #default="scope">
                      <el-button type="primary" link>编辑</el-button>
                      <el-button type="danger" link>删除</el-button>
                    </template>
                  </el-table-column>
                </el-table>

                <el-empty v-if="notes.length === 0" description="暂无笔记数据" />
              </el-card>


            </div>


  <!--          <div class="pagination-container" v-if="currentPageType.totalPages >= 1">
            <div class="pagination">
              &lt;!&ndash; 上一页按钮 &ndash;&gt;
              <button
                      id="prev-page"
                      class="page-btn"
                      :disabled="currentPageType.nowPage === 1"
                      @click="prePage()"
              >
                <span class="icon">❮</span> 上一页
              </button>

              &lt;!&ndash; 页码显示区域 &ndash;&gt;
              <div class="page-numbers">
                &lt;!&ndash; 中间页码 &ndash;&gt;
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

                &lt;!&ndash; 省略号（如果需要） &ndash;&gt;
                <span v-if="pageToShow.length > 2 && pageToShow[pageToShow.length - 1] !== currentPageType.totalPages"
                      class="page-item ellipsis">...</span>

                &lt;!&ndash; 尾页 &ndash;&gt;
              </div>

              &lt;!&ndash; 下一页按钮 &ndash;&gt;
              <button
                      id="next-page"
                      class="page-btn"
                      :disabled="currentPageType.nowPage === currentPageType.totalPages"
                      @click="nextPage()"
              >
                下一页 <span class="icon">❯</span>
              </button>
            </div>
          </div>-->

            <!-- 分页组件 -->
            <el-pagination
              v-model:current-page="paginationCurrentPage"
              v-model:page-size="paginationPageSize"
              :page-sizes="[10, 20, 50, 100]"
              :total="paginationTotal"
              layout="total, sizes, prev, pager, next, jumper"
              background
              style="margin-top: 20px; justify-content: flex-end"
            />


          </div>
        </div>
      </div>
    </div>


    <!-- 添加用户对话框 -->
    <el-dialog v-model="showAddUserModal" title="添加用户" width="500px" append-to-body>
      <el-form :model="newUser" label-width="100px">
        <el-form-item label="用户名" required>
          <el-input v-model="newUser.userName" placeholder="请输入用户名" />
        </el-form-item>
        <el-form-item label="密码" required>
          <el-input v-model="newUser.userPassword" type="password" placeholder="请输入密码" show-password />
        </el-form-item>
        <el-form-item label="邮箱" required>
          <el-input v-model="newUser.userEmail" placeholder="请输入邮箱" />
        </el-form-item>
        <el-form-item label="角色">
          <el-select v-model="newUser.userRole" placeholder="选择角色" style="width: 100%">
            <el-option label="普通用户" value="user" />
            <el-option label="管理员" value="admin" />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="showAddUserModal = false">取消</el-button>
          <el-button type="primary" @click="addUser">确定</el-button>
        </span>
      </template>
    </el-dialog>

    <!-- 编辑用户对话框 -->
    <el-dialog v-model="showEditUserModal" title="编辑用户" width="500px" append-to-body>
      <el-form :model="editUserForm" label-width="100px">
        <el-form-item label="用户名" required>
          <el-input v-model="editUserForm.userName" placeholder="请输入用户名" />
        </el-form-item>
        <el-form-item label="邮箱" required>
          <el-input v-model="editUserForm.userEmail" placeholder="请输入邮箱" />
        </el-form-item>
        <el-form-item label="角色">
          <el-select v-model="editUserForm.userRole" placeholder="选择角色" style="width: 100%">
            <el-option label="普通用户" value="user" />
            <el-option label="管理员" value="admin" />
          </el-select>
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="editUserForm.userStatus" placeholder="选择状态" style="width: 100%">
            <el-option label="活跃" :value="1" />
            <el-option label="禁用" :value="0" />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="showEditUserModal = false">取消</el-button>
          <el-button type="primary" @click="updateUser">确定</el-button>
        </span>
      </template>
    </el-dialog>

    <!-- 查看用户详情对话框 -->
    <el-dialog v-model="showViewUserModal" title="用户详情" width="500px" append-to-body>
      <el-descriptions :column="2" border>
        <el-descriptions-item label="用户ID">{{ viewUserForm.userId }}</el-descriptions-item>
        <el-descriptions-item label="用户名">{{ viewUserForm.userName }}</el-descriptions-item>
        <el-descriptions-item label="邮箱">{{ viewUserForm.userEmail }}</el-descriptions-item>
        <el-descriptions-item label="角色">
          <el-tag :type="viewUserForm.userRole === 'admin' ? 'danger' : 'primary'" size="small">
            {{ viewUserForm.userRole === 'admin' ? '管理员' : '普通用户' }}
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="注册时间">{{ viewUserForm.userRegisterTime }}</el-descriptions-item>
        <el-descriptions-item label="状态">
          <el-tag :type="viewUserForm.userStatus === 1 ? 'success' : 'danger'" size="small">
            {{ viewUserForm.userStatus === 1 ? '活跃' : '禁用' }}
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="文章数">{{ viewUserForm.articleCount || 0 }}</el-descriptions-item>
        <el-descriptions-item label="笔记数">{{ viewUserForm.noteCount || 0 }}</el-descriptions-item>
      </el-descriptions>
      <template #footer>
        <el-button @click="showViewUserModal = false">关闭</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import axiosAPI from '@/utils/api/axios.js'
import { useUserInfoStore } from '@/stores/modules/userInfo.js'
import { ElMessage, ElMessageBox,ElButton
  ,ElTable,ElTableColumn,ElPagination,ElForm,ElFormItem,ElInput,ElSelect,ElOption
  ,ElTag,ElCheckbox,ElDialog,ElDescriptions,ElDescriptionsItem,ElCard,ElRow,ElCol,ElBreadcrumb,ElBreadcrumbItem
,ElEmpty   } from 'element-plus'


const userStore = useUserInfoStore()

// 统计数据
const stats = ref({
  userCount: 0,
  articleCount: 0,
  noteCount: 0,
  commentCount: 0
})// 页面状态
const currentPage = ref('user')

// 搜索和筛选
const searchKeyword = ref('')
const filterRole = ref('')

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

// 分页组件变量
const paginationCurrentPage = ref(1)
const paginationPageSize = ref(10)
const paginationTotal = ref(0)

// 表单数据(添加新用户)
const newUser = ref({
  userName: '',
  userPassword: '',
  userEmail: '',
  userRole: 'user',
  userStatus: 1
})
//编译用户表单
const editUserForm = ref({
  userId: '',
  userName: '',
  userEmail: '',
  userRole: '',
  userStatus: ''
})
//查看用户详情表单
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
    const response = await axiosAPI.get('/admin/manage/users', {
      params: {
        page: usersPage.value.nowPage,
        pageSize: usersPage.value.pageSize
      }
    })

    if (response.data.code === 1) {
      ElMessage.success('获取用户列表成功')
      users.value = response.data.data.records
      stats.value.userCount = response.data.data.total
      usersPage.value.totalCount = response.data.data.total
      usersPage.value.totalPages = Math.ceil(usersPage.value.totalCount / usersPage.value.pageSize)
    } else {
      ElMessage.error('获取用户列表失败：' + response.data.msg)
      users.value = []
    }
  } catch (error) {
    console.error(error)
    ElMessage.error('获取用户列表失败')
  } finally {
    usersPage.value.loading = false
  }
}

// 获取所有文章
const allArticles = async () => {
  articlesPage.value.loading = true
  try {
    const response = await axiosAPI.get('/admin/manage/articles', {
      params: {
        page: articlesPage.value.nowPage,
        pageSize: articlesPage.value.pageSize
      }
    })

    if (response.data.code === 1) {
      articles.value = response.data.data.records
      stats.value.articleCount = response.data.data.total
      articlesPage.value.totalCount = response.data.data.total
      articlesPage.value.totalPages = Math.ceil(articlesPage.value.totalCount / articlesPage.value.pageSize)
    } else {
      ElMessage.error('获取文章列表失败：' + response.data.msg)
      articles.value = []
    }
  } catch (error) {
    console.error(error)
    ElMessage.error('获取文章列表失败')
  } finally {
    articlesPage.value.loading = false
  }
}

// 获取所有笔记
const allNotes = async () => {
  notesPage.value.loading = true
  try {
    const response = await axiosAPI.get('/admin/manage/notes', {
      params: {
        page: notesPage.value.nowPage,
        pageSize: notesPage.value.pageSize
      }
    })

    if (response.data.code === 1) {
      notes.value = response.data.data.records
      stats.value.noteCount = response.data.data.total
      notesPage.value.totalCount = response.data.data.total
      notesPage.value.totalPages = Math.ceil(notesPage.value.totalCount / notesPage.value.pageSize)
    } else {
      ElMessage.error('获取笔记列表失败：' + response.data.msg)
      notes.value = []
    }
  } catch (error) {
    console.error(error)
    ElMessage.error('获取笔记列表失败')
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

//前一页
const prePage = () => {
  let currentPageData = currentPageType.value
  if (currentPageData.nowPage > 1) {
    currentPageData.nowPage--
    getPageType()
  }
}

//后一页
const nextPage = () => {
  let currentPageData = currentPageType.value
  if (currentPageData.nowPage < currentPageData.totalPages) {
    currentPageData.nowPage++
    getPageType()
  }
}

// 用户操作：删除单个用户
const deleteUser = async (id) => {
  ElMessageBox.confirm('确定要删除该用户吗？此操作不可恢复。', '删除确认', {
    confirmButtonText: '确定删除',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      const response = await axiosAPI.delete('/admin/manage/delete/user', { params: { id } })
      if (response.data.code === 1) {
        ElMessage.success('删除成功')
        await allUsers()
      } else {
        ElMessage.error('删除失败：' + response.data.msg)
      }
    } catch (error) {
      console.error(error)
      ElMessage.error('删除失败')
    }
  }).catch(() => {})
}

// 表格选择变化处理
const handleSelectionChange = (selection) => {
  selectedUsers.value = selection.map(item => item.userId)
}

// 打开添加对话框
const openAddDialog = () => {
  showAddUserModal.value = true
}

// 批量删除
const batchDelete = () => {
  batchDeleteUsers()
}
// 用户操作：搜索用户
const searchUserByKeyword = async () => {
  if (!searchUser.value.trim()) {
    ElMessage.warning('请输入搜索关键词')
    return
  }
  try {
    const response = await axiosAPI.post('/admin/manage/search/users', {
      userName: searchUser.value,
      userNickname: searchUser.value,
      userEmail: searchUser.value
    })

    if (response.data.code === 1) {
      ElMessage.success('搜索成功')
      users.value = response.data.data.records
      usersPage.value.totalCount = response.data.data.total
      usersPage.value.totalPages = Math.ceil(usersPage.value.totalCount / usersPage.value.pageSize)
    } else {
      ElMessage.error('搜索失败：' + response.data.msg)
    }
  } catch (error) {
    console.error(error)
    ElMessage.error('搜索失败')
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

// 用户操作：删除选中的用户（批量）
const batchDeleteUsers = async () => {
  if (selectedUsers.value.length === 0) {
    ElMessage.warning('请先选择要删除的用户')
    return
  }
  ElMessageBox.confirm(`确定要删除选中的 ${selectedUsers.value.length} 个用户吗？此操作不可恢复。`, '批量删除确认', {
    confirmButtonText: '确定删除',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      const response = await axiosAPI.delete('/admin/manage/delete/users', {
        params: {
          ids: selectedUsers.value.join(',')
        }
      })
      if (response.data.code === 1) {
        ElMessage.success('批量删除成功')
        allUsers()
        selectedUsers.value = []
      } else {
        ElMessage.error('批量删除失败：' + response.data.msg)
      }
    } catch (error) {
      console.error(error)
      ElMessage.error('批量删除失败')
    }
  }).catch(() => {})
}

// 用户操作：添加用户
const addUser = async () => {
  if (!newUser.value.userName || !newUser.value.userPassword || !newUser.value.userEmail) {
    ElMessage.warning('请填写完整信息')
    return
  }
  try {
    const response = await axiosAPI.post('/admin/manage/add/user', newUser.value)

    if (response.data.code === 1) {
      ElMessage.success('添加用户成功')
      allUsers()
      newUser.value = {
        userName: '',
        userPassword: '',
        userEmail: '',
        userRole: 'user',
        userStatus: 1
      }
      showAddUserModal.value = false
    } else {
      ElMessage.error('添加用户失败：' + response.data.msg)
    }
  } catch (error) {
    console.error(error)
    ElMessage.error('添加用户失败')
  }
}

// 用户操作：更新用户
const updateUser = async () => {
  try {
    const response = await axiosAPI.put('/admin/manage/update/user', editUserForm.value)

    if (response.data.code === 1) {
      ElMessage.success('更新用户成功')
      allUsers()
      showEditUserModal.value = false
    } else {
      ElMessage.error('更新用户失败：' + response.data.msg)
    }
  } catch (error) {
    console.error(error)
    ElMessage.error('更新用户失败')
  }
}

// 页面加载时初始化
onMounted(() => {
  usersPage.value.nowPage = 1
  getPageType()
})
</script>

<style scoped>
/* 页面布局 */
#main_Content {
  display: flex;
  min-height: 100vh;
}

/* 侧边栏样式 */
.sidebar {
  width: 250px;
  background: var(--card-bg);
  padding: 20px;
  border-right: 1px solid var(--border-color);
  flex-shrink: 0;
}

.sidebar-header {
  text-align: center;
  margin-bottom: 30px;
}

.sidebar-avatar {
  width: 80px;
  height: 80px;
  border-radius: 50%;
  margin: 15px auto;
  display: block;
}

.sidebar-nav {
  list-style: none;
  padding: 0;
  margin: 0;
}

.sidebar-nav li {
  margin-bottom: 5px;
}

.sidebar-nav a {
  display: block;
  padding: 12px 15px;
  color: var(--text-primary);
  text-decoration: none;
  border-radius: 8px;
  transition: all 0.3s ease;
}

.sidebar-nav a:hover,
.sidebar-nav a.active {
  background: var(--accent-color);
  color: white;
}

/* 统计卡片区域 */
.stats-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(280px, 1fr));
  gap: 20px;
  margin-bottom: 20px;
}

/* 响应式设计 */
@media (max-width: 1200px) {
  .stats-grid {
    grid-template-columns: repeat(3, 1fr);
  }
}

@media (max-width: 992px) {
  .stats-grid {
    grid-template-columns: repeat(2, 1fr);
  }
}

@media (max-width: 768px) {
  .stats-grid {
    grid-template-columns: 1fr;
  }
}

.stat-card {
  border-radius: 12px;
  transition: all 0.3s ease;
  background: var(--card-bg);
  border: 1px solid var(--border-color);
  width: 25%;
}

.stat-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 10px 20px rgba(0, 0, 0, 0.1);
}

.stat-content {
  display: flex;
  align-items: center;
  gap: 15px;
}

.stat-icon {
  width: 60px;
  height: 60px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 28px;
  color: white;
}

.user-icon {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}

.article-icon {
  background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%);
}

.note-icon {
  background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%);
}

.comment-icon {
  background: linear-gradient(135deg, #43e97b 0%, #38f9d7 100%);
}

.stat-info {
  flex: 1;
}

.stat-number {
  font-size: 28px;
  font-weight: bold;
  color: var(--text-primary);
  line-height: 1.2;
}

.stat-label {
  font-size: 14px;
  color: var(--text-secondary);
  margin-top: 4px;
}

/* 工具栏 */
.toolbar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
  padding: 15px 20px;
  background: var(--card-bg);
  border-radius: 12px;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.05);
  border: 1px solid var(--border-color);
}

.toolbar-actions {
  display: flex;
  gap: 12px;
  align-items: center;
}

.toolbar-actions .el-input {
  width: 250px;
}

.toolbar-actions .el-select {
  width: 150px;
}

/* 主内容区 */
.main-content {
  flex: 1;
  padding: 20px;
  background: var(--bg-color);
  display: flex;
  flex-direction: column;
  gap: 20px;
}

/* 内容区域 */
.content-area {
  flex: 1;
}

/* 内容卡片 */
.content-card {
  background: var(--card-bg);
  border-radius: 12px;
  border: 1px solid var(--border-color);
}

.content-card :deep(.el-card__header) {
  padding: 15px 20px;
  border-bottom: 1px solid var(--border-color);
}

.content-card :deep(.el-card__body) {
  padding: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.card-header h5 {
  margin: 0;
  color: var(--text-primary);
  font-size: 18px;
  font-weight: 600;
}

.header-actions {
  display: flex;
  gap: 10px;
}

/* 表格样式调整 */
:deep(.el-table) {
  --el-table-border-color: var(--border-color);
  --el-table-header-bg-color: var(--bg-color);
  --el-table-row-hover-bg-color: var(--bg-color);
}

:deep(.el-table th) {
  background-color: var(--bg-color);
  color: var(--text-primary);
  font-weight: 600;
}

:deep(.el-table td) {
  color: var(--text-primary);
}

:deep(.el-table--striped .el-table__body tr.el-table__row--striped td) {
  background: var(--bg-color);
}

/* 分页样式 */
:deep(.el-pagination) {
  --el-pagination-bg-color: var(--card-bg);
  --el-pagination-text-color: var(--text-secondary);
  --el-pagination-button-bg-color: var(--card-bg);
  --el-pagination-hover-color: var(--accent-color);
}

/* 对话框样式 */
:deep(.el-dialog) {
  --el-dialog-bg-color: var(--card-bg);
  border-radius: 12px;
}

:deep(.el-dialog__title) {
  color: var(--text-primary);
}

:deep(.el-descriptions__label) {
  color: var(--text-secondary);
}

:deep(.el-descriptions__content) {
  color: var(--text-primary);
}

/* 标签样式 */
:deep(.el-tag) {
  border-radius: 4px;
}

/* 空状态样式 */
:deep(.el-empty__description) {
  color: var(--text-secondary);
}

/* 按钮样式 */
:deep(.el-button--primary) {
  --el-button-bg-color: var(--accent-color);
  --el-button-border-color: var(--accent-color);
}

:deep(.el-button--primary:hover) {
  --el-button-hover-bg-color: var(--accent-hover);
  --el-button-hover-border-color: var(--accent-hover);
}

/* 搜索框样式 */
:deep(.el-input__wrapper) {
  background-color: var(--card-bg);
  box-shadow: 0 0 0 1px var(--border-color) inset;
}

:deep(.el-input__inner) {
  color: var(--text-primary);
}

:deep(.el-input__prefix) {
  color: var(--text-secondary);
}

/* 选择框样式 */
:deep(.el-select__wrapper) {
  background-color: var(--card-bg);
  box-shadow: 0 0 0 1px var(--border-color) inset;
}

:deep(.el-select__placeholder) {
  color: var(--text-secondary);
}
</style>
