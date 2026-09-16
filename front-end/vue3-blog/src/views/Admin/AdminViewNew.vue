<template>
  <div id="main_Content">
    <!-- 左侧边栏 -->
    <div class="sidebar">
      <div class="sidebar-header">
        <h4>管理员中心</h4>
        <img src="@/assets/images/blog_avater1.png" alt="管理员头像" class="sidebar-avatar">
        <p style="margin: 0; color: #8B6A4F;">管理员</p>
      </div>
      <ul class="sidebar-nav">
        <li><a href="#" @click.prevent="switchPage('dashboard')" :class="{ active: currentPage === 'dashboard' }">仪表盘</a>
        </li>
        <li><a href="#" @click.prevent="switchPage('user')" :class="{ active: currentPage === 'user' }">用户管理</a></li>
        <li><a href="#" @click.prevent="switchPage('article')" :class="{ active: currentPage === 'article' }">文章管理</a>
        </li>
        <li><a href="#" @click.prevent="switchPage('note')" :class="{ active: currentPage === 'note' }">笔记管理</a></li>
        <li><a href="#" @click.prevent="switchPage('category')" :class="{ active: currentPage === 'category' }">分类管理</a>
        </li>
        <li><a href="#" @click.prevent="handleLogout">退出登录</a></li>
      </ul>
    </div>

    <!-- 主要内容 -->
    <div class="main-content">
     
      <!-- 工具栏区域 -->
      <div class="toolbar">
        <!-- 面包屑 -->
        <div class="breadcrumb">
          <a href="/">首页</a>
          <span class="breadcrumb-separator">/</span>
          <span>{{ pageTitle }}</span>
        </div>
        <!-- 搜索和筛选 -->
        <div class="toolbar-actions">
          <div class="filter-section">
            <select v-if="currentPage === 'user'" v-model="filterRole" class="form-select"
              style="width:130px; margin-right: 10px">
              <option value="">全部</option>
              <option value="user">普通用户</option>
              <option value="admin">管理员</option>
            </select>
            <input v-model="searchKeyword" type="text" placeholder="搜索关键词" class="form-input"
              style="width: 220px; margin-right: 10px" @keyup.enter="handleSearch">
            <button class="btn btn-primary" @click="handleSearch" style="margin-right: 10px">搜索
            </button>
            <button class="btn btn-default" @click="resetFilter">重置</button>
          </div>
          <div class="action-section">
            <button v-if="currentPage === 'user' || currentPage === 'category'" class="btn btn-primary"
              @click="openAddDialog" style="margin-right: 10px">新增
            </button>
            <button class="btn btn-default" :disabled="selectedItems.length === 0" @click="batchDelete">批量删除
            </button>
          </div>
        </div>
      </div>


      <!-- 内容区域 -->
      <div class="content-area">
        <div class="container-wrapper">
          <div class="table-section">
            <!-- 仪表盘 -->
            <div v-if="currentPage === 'dashboard'" class="content-card">
              <div class="card-header">
                <h5>仪表盘</h5>
              </div>
              <div class="card-body">
                <div class="dashboard-content">
                  <!--                  <p>欢迎来到管理员仪表盘</p>
                                    <p>这里将展示系统概览和关键指标</p>-->
                  <!-- 统计卡片区域 -->
                  <div class="stats-grid">
                    <!-- 用户总数 -->
                    <div class="stat-card user-stat" @click="switchPage('user')" style="cursor: pointer;">
                      <div class="stat-content">
                        <div class="stat-icon user-icon">
                          <UserFilled />
                        </div>
                        <div class="stat-info">
                          <div class="stat-number user-number">{{ stats.userCount }}</div>
                          <div class="stat-label">用户总数</div>
                        </div>
                      </div>
                    </div>
                    <!-- 文章总数 -->
                    <div class="stat-card article-stat" @click="switchPage('article')" style="cursor: pointer;">
                      <div class="stat-content">
                        <div class="stat-icon article-icon">
                          <Notebook />
                        </div>
                        <div class="stat-info">
                          <div class="stat-number article-number">{{ stats.articleCount }}</div>
                          <div class="stat-label">文章总数</div>
                        </div>
                      </div>
                    </div>
                    <!-- 笔记总数 -->
                    <div class="stat-card note-stat" @click="switchPage('note')" style="cursor: pointer;">
                      <div class="stat-content">
                        <div class="stat-icon note-icon">
                          <EditPen />
                        </div>
                        <div class="stat-info">
                          <div class="stat-number note-number">{{ stats.noteCount }}</div>
                          <div class="stat-label">笔记总数</div>
                        </div>
                      </div>
                    </div>
                    <!-- 评论总数 -->
                    <div class="stat-card comment-stat" @click="switchPage('comment')" style="cursor: pointer;">
                      <div class="stat-content">
                        <div class="stat-icon comment-icon">
                          <ChatDotRound />
                        </div>
                        <div class="stat-info">
                          <div class="stat-number comment-number">{{ stats.commentCount }}</div>
                          <div class="stat-label">评论总数</div>
                        </div>
                      </div>
                    </div>
                    <!-- 分类总数 -->
                    <div class="stat-card category-stat" @click="switchPage('category')" style="cursor: pointer;">
                      <div class="stat-content">
                        <div class="stat-icon category-icon">
                          <Folder />
                        </div>
                        <div class="stat-info">
                          <div class="stat-number category-number">{{ stats.categoryCount }}</div>
                          <div class="stat-label">分类总数</div>
                        </div>
                      </div>
                    </div>
                  </div>

                  <!--   快速操作区域      -->
                  <div class="quick-actions">
                    <h4>快速操作</h4>
                    <div class="action-buttons">
                      <button class="btn btn-primary btn-user" @click="showAddUserModal = true">
                        添加用户
                      </button>
                      <button class="btn btn-primary" @click="showAddArticleModal = true">添加文章</button>
                      <button class="btn btn-primary" @click="showAddNoteModal = true">添加笔记</button>
                      <button class="btn btn-primary" @click="showAddCategoryModal = true">添加分类</button>
                    </div>
                  </div>

                  <!--    最近动态              -->


                </div>
              </div>

            </div>
            <!-- 用户展示列表 -->
            <div v-if="currentPage === 'user'" class="content-card">
              <div class="card-header">
                <h5>用户管理</h5>
                <div class="header-actions">
                  <button class="btn btn-primary" @click="showAddUserModal = true">添加用户</button>
                  <button class="btn btn-default" :disabled="selectedItems.length === 0" @click="batchDeleteUsers">批量删除
                  </button>
                </div>
              </div>
              <div class="card-body">
                <table class="data-table">
                  <thead>
                    <tr>
                      <th class="checkbox-column">
                        <input type="checkbox" @change="selectAll($event.target.checked)">
                      </th>
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
                      <td class="checkbox-column">
                        <input type="checkbox" :checked="selectedItems.some(item => item.userId === user.userId)"
                          @change="toggleSelection(user, $event.target.checked)">
                      </td>
                      <td>{{ user.userId }}</td>
                      <td>{{ user.userName }}</td>
                      <td>{{ user.userEmail }}</td>
                      <td>
                        <span :class="['tag', user.userRole === 'admin' ? 'tag-danger' : 'tag-primary']">
                          {{ user.userRole === 'admin' ? '管理员' : '普通用户' }}
                        </span>
                      </td>
                      <td>{{ user.userRegisterTime }}</td>
                      <td>{{ user.articleCount }}</td>
                      <td>{{ user.noteCount }}</td>
                      <td>
                        <span :class="['tag', user.userStatus === 1 ? 'tag-success' : 'tag-danger']">
                          {{ user.userStatus === 1 ? '活跃' : '禁用' }}
                        </span>
                      </td>
                      <td class="action-buttons">
                        <button class="btn btn-sm btn-primary" @click="editUser(user)">编辑</button>
                        <button class="btn btn-sm btn-info" @click="viewUser(user)">详情</button>
                        <button class="btn btn-sm btn-danger" @click="deleteUser(user.userId)">删除
                        </button>
                      </td>
                    </tr>
                  </tbody>
                </table>
                <div v-if="pageUsers.length === 0" class="empty-state">
                  <p>暂无用户数据</p>
                </div>
              </div>
            </div>
            <!-- 文章展示列表 -->
            <div v-if="currentPage === 'article'" class="content-card">
              <div class="card-header">
                <h5>文章管理</h5>
                <div class="header-actions">
                  <button class="btn btn-default" :disabled="selectedItems.length === 0"
                    @click="batchDeleteArticles">批量删除
                  </button>
                </div>
              </div>
              <div class="card-body">
                <table class="data-table">
                  <thead>
                    <tr>
                      <th class="checkbox-column">
                        <input type="checkbox" @change="selectAll($event.target.checked)">
                      </th>
                      <th>文章ID</th>
                      <th>文章标题</th>
                      <th>作者</th>
                      <th>更新时间</th>
                      <th>状态</th>
                      <th>阅读量</th>
                      <th>操作</th>
                    </tr>
                  </thead>
                  <tbody>
                    <tr v-for="article in articles" :key="article.articleId">
                      <td class="checkbox-column">
                        <input type="checkbox"
                          :checked="selectedItems.some(item => item.articleId === article.articleId)"
                          @change="toggleSelection(article, $event.target.checked)">
                      </td>
                      <td>{{ article.articleId }}</td>
                      <td class="text-left">{{ truncateTitle(article.articleTitle) }}</td>
                      <td>{{ article.blogger }}</td>
                      <td>{{ article.articleUpdateTime }}</td>
                      <td>
                        <span :class="['tag', article.articleStatus === 1 ? 'tag-success' : 'tag-info']">
                          {{ article.articleStatus === 1 ? '发布' : '草稿' }}
                        </span>
                      </td>
                      <td>{{ article.articleReadCount }}</td>
                      <td class="action-buttons">
                        <button class="btn btn-sm btn-info" @click="viewArticle(article)">详情</button>
                        <button class="btn btn-sm btn-danger" @click="deleteArticle(article.articleId)">删除
                        </button>
                      </td>
                    </tr>
                  </tbody>
                </table>
                <div v-if="articles.length === 0" class="empty-state">
                  <p>暂无文章数据</p>
                </div>
              </div>
            </div>
            <!-- 笔记展示列表 -->
            <div v-if="currentPage === 'note'" class="content-card">
              <div class="card-header">
                <h5>笔记管理</h5>
                <div class="header-actions">
                  <button class="btn btn-default" :disabled="selectedItems.length === 0" @click="batchDeleteNotes">批量删除
                  </button>
                </div>
              </div>
              <div class="card-body">
                <table class="data-table">
                  <thead>
                    <tr>
                      <th class="checkbox-column">
                        <input type="checkbox" @change="selectAll($event.target.checked)">
                      </th>
                      <th>笔记ID</th>
                      <th>笔记标题</th>
                      <th>作者</th>
                      <th>更新时间</th>
                      <th>操作</th>
                    </tr>
                  </thead>
                  <tbody>
                    <tr v-for="note in notes" :key="note.noteId">
                      <td class="checkbox-column">
                        <input type="checkbox" :checked="selectedItems.some(item => item.noteId === note.noteId)"
                          @change="toggleSelection(note, $event.target.checked)">
                      </td>
                      <td>{{ note.noteId }}</td>
                      <td class="text-left">{{ note.noteTopic }}</td>
                      <td>{{ note.noteAuthor }}</td>
                      <td>{{ note.noteUpdateTime }}</td>
                      <td class="action-buttons">
                        <button class="btn btn-sm btn-info" @click="viewNote(note)">详情</button>
                        <button class="btn btn-sm btn-danger" @click="deleteNote(note.noteId)">删除</button>
                      </td>
                    </tr>
                  </tbody>
                </table>
                <div v-if="notes.length === 0" class="empty-state">
                  <p>暂无笔记数据</p>
                </div>
              </div>
            </div>
            <!-- 分类管理 -->
            <div v-if="currentPage === 'category'" class="content-card">
              <div class="card-header">
                <h5>分类管理</h5>
                <div class="header-actions">
                  <button class="btn btn-primary" @click="showAddCategoryModal = true">添加分类</button>
                  <button class="btn btn-default" :disabled="selectedItems.length === 0"
                    @click="batchDeleteCategories">批量删除
                  </button>
                </div>
              </div>
              <div class="card-body">
                <table class="data-table">
                  <thead>
                    <tr>
                      <th class="checkbox-column">
                        <input type="checkbox" @change="selectAll($event.target.checked)">
                      </th>
                      <th>分类ID</th>
                      <th>分类名称</th>
                      <th>父类ID</th>
                      <th>关联文章数</th>
                      <th>操作</th>
                    </tr>
                  </thead>
                  <tbody>
                    <tr v-for="category in (categories || [])" :key="category.categoryId">
                      <td class="checkbox-column">
                        <input type="checkbox"
                          :checked="selectedItems.some(item => item.categoryId === category.categoryId)"
                          @change="toggleSelection(category, $event.target.checked)">
                      </td>
                      <td>{{ category.categoryId }}</td>
                      <td class="text-left">{{ category.categoryName }}</td>
                      <td class="text-left">{{ category.categoryParentId }}</td>

                      <td>{{ category.linkArticleCount || 0 }}</td>
                      <td class="action-buttons">
                        <button class="btn btn-sm btn-info" @click="viewCategory(category)">详情</button>
                        <button class="btn btn-sm btn-primary" @click="editCategory(category)">编辑</button>
                        <button class="btn btn-sm btn-danger" @click="deleteCategory(category.categoryId)">删除
                        </button>
                      </td>
                    </tr>
                  </tbody>
                </table>
                <div v-if="!categories || categories.length === 0" class="empty-state">
                  <p>暂无分类数据</p>
                </div>
              </div>
            </div>
            <!-- 数据统计 -->
          </div>

          <!-- 分页组件 -->
          <div v-if="currentPage !== 'dashboard' && currentPage !== 'stat'" class="pagination">
            <div class="pagination-info">
              <span>共 {{ total }} 条</span>
            </div>
            <div class="pagination-controls">
              <button class="btn btn-sm btn-default" :disabled="paginationCurrentPage === 1"
                @click="handleCurrentChange(paginationCurrentPage - 1)">上一页
              </button>
              <span class="pagination-pages">
                <span v-for="page in pageNumbers" :key="page"
                  :class="['pagination-page', { active: page === paginationCurrentPage }]"
                  @click="handleCurrentChange(page)">
                  {{ page }}
                </span>
              </span>
              <button class="btn btn-sm btn-default" :disabled="paginationCurrentPage === totalPages"
                @click="handleCurrentChange(paginationCurrentPage + 1)">下一页
              </button>
            </div>
            <div class="pagination-size">
              <span>每页显示：</span>
              <select v-model="paginationPageSize" @change="handleSizeChange(paginationPageSize)"
                class="form-select-sm">
                <option value="10">10条</option>
                <option value="20">20条</option>
                <option value="50">50条</option>
                <option value="100">100条</option>
              </select>
            </div>
            <div class="pagination-jump">
              <span>跳转到第</span>
              <input type="number" v-model="jumpPage" class="form-input-sm" min="1" :max="totalPages">
              <span>页</span>
              <button class="btn btn-sm btn-default" @click="jumpToPage">跳转</button>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>

  <!-- 添加用户对话框 -->
  <div v-if="showAddUserModal" class="modal-overlay" @click="showAddUserModal = false">
    <div class="modal-container" @click.stop>
      <div class="modal-header">
        <h3 class="modal-title">添加用户</h3>
        <button type="button" class="modal-close" @click="showAddUserModal = false">×</button>
      </div>
      <div class="modal-body">
        <div class="form-container">
          <div class="form-item">
            <label class="form-label">用户名</label>
            <input v-model="newUser.userName" type="text" class="form-input" placeholder="请输入用户名">
          </div>
          <div class="form-item">
            <label class="form-label">密码</label>
            <input v-model="newUser.userPassword" type="password" class="form-input" placeholder="请输入密码">
          </div>
          <div class="form-item">
            <label class="form-label">邮箱</label>
            <input v-model="newUser.userEmail" type="email" class="form-input" placeholder="请输入邮箱">
          </div>
          <div class="form-item">
            <label class="form-label">角色</label>
            <select v-model="newUser.userRole" class="form-select">
              <option value="user">普通用户</option>
              <option value="admin">管理员</option>
            </select>
          </div>
          <div class="form-item">
            <label class="form-label">状态</label>
            <select v-model="newUser.userStatus" class="form-select">
              <option value="1">活跃</option>
              <option value="0">禁用</option>
            </select>
          </div>
        </div>
      </div>
      <div class="modal-footer">
        <button type="button" class="btn btn-default" @click="showAddUserModal = false">取消
        </button>
        <button type="button" class="btn btn-primary" @click="addUser">确定</button>
      </div>
    </div>
  </div>

  <!-- 编辑用户对话框 -->
  <div v-if="showEditUserModal" class="modal-overlay" @click="showEditUserModal = false">
    <div class="modal-container" @click.stop>
      <div class="modal-header">
        <h3 class="modal-title">编辑用户</h3>
        <button type="button" class="modal-close" @click="showEditUserModal = false">×</button>
      </div>
      <div class="modal-body">
        <div class="form-container">
          <div class="form-item">
            <label class="form-label">用户名</label>
            <input v-model="editUserForm.userName" type="text" class="form-input" placeholder="请输入用户名">
          </div>
          <div class="form-item">
            <label class="form-label">邮箱</label>
            <input v-model="editUserForm.userEmail" type="email" class="form-input" placeholder="请输入邮箱">
          </div>
          <div class="form-item">
            <label class="form-label">角色</label>
            <select v-model="editUserForm.userRole" class="form-select">
              <option value="user">普通用户</option>
              <option value="admin">管理员</option>
            </select>
          </div>
          <div class="form-item">
            <label class="form-label">状态</label>
            <select v-model="editUserForm.userStatus" class="form-select">
              <option value="1">活跃</option>
              <option value="0">禁用</option>
            </select>
          </div>
        </div>
      </div>
      <div class="modal-footer">
        <button type="button" class="btn btn-default" @click="showEditUserModal = false">取消
        </button>
        <button type="button" class="btn btn-primary" @click="updateUser">确定</button>
      </div>
    </div>
  </div>

  <!-- 添加文章对话框 -->
  <div v-if="showAddArticleModal" class="modal-overlay" @click="showAddArticleModal = false">
    <div class="modal-container article-modal" @click.stop>
      <div class="modal-header">
        <h3 class="modal-title">添加文章</h3>
        <button type="button" class="modal-close" @click="showAddArticleModal = false">×</button>
      </div>
      <div class="modal-body">
        <div class="form-container">
          <div class="form-item">
            <label class="form-label">文章标题</label>
            <input v-model="newArticle.articleTitle" type="text" class="form-input" placeholder="请输入文章标题">
          </div>
          <div class="form-item">
            <label class="form-label">文章摘要</label>
            <textarea v-model="newArticle.articleSummary" class="form-textarea" placeholder="请输入文章摘要"
              rows="3"></textarea>
          </div>
          <div class="form-item">
            <label class="form-label">文章内容</label>
            <textarea v-model="newArticle.articleContent" class="form-textarea" placeholder="请输入文章内容"
              rows="6"></textarea>
          </div>
          <div class="form-item">
            <label class="form-label">封面图片</label>
            <input v-model="newArticle.articleThumbnail" type="text" class="form-input" placeholder="请输入图片路径（可选）">
          </div>
          <div class="form-item">
            <label class="form-label">状态</label>
            <select v-model="newArticle.articleStatus" class="form-select">
              <option value="1">发布</option>
              <option value="0">草稿</option>
            </select>
          </div>
        </div>
      </div>
      <div class="modal-footer">
        <button type="button" class="btn btn-default" @click="showAddArticleModal = false">取消</button>
        <button type="button" class="btn btn-primary" @click="addArticle">确定</button>
      </div>
    </div>
  </div>

  <!-- 编辑文章对话框 -->
  <div v-if="showEditArticleModal" class="modal-overlay" @click="showEditArticleModal = false">
    <div class="modal-container article-modal" @click.stop>
      <div class="modal-header">
        <h3 class="modal-title">编辑文章</h3>
        <button type="button" class="modal-close" @click="showEditArticleModal = false">×</button>
      </div>
      <div class="modal-body">
        <div class="form-container">
          <div class="form-item">
            <label class="form-label">文章标题</label>
            <input v-model="editArticleForm.articleTitle" type="text" class="form-input" placeholder="请输入文章标题">
          </div>
          <div class="form-item">
            <label class="form-label">文章摘要</label>
            <textarea v-model="editArticleForm.articleSummary" class="form-textarea" placeholder="请输入文章摘要"
              rows="3"></textarea>
          </div>
          <div class="form-item">
            <label class="form-label">文章内容</label>
            <textarea v-model="editArticleForm.articleContent" class="form-textarea" placeholder="请输入文章内容"
              rows="6"></textarea>
          </div>
          <div class="form-item">
            <label class="form-label">封面图片</label>
            <input v-model="editArticleForm.articleThumbnail" type="text" class="form-input" placeholder="请输入图片路径">
          </div>
          <div class="form-item">
            <label class="form-label">状态</label>
            <select v-model="editArticleForm.articleStatus" class="form-select">
              <option value="1">发布</option>
              <option value="0">草稿</option>
            </select>
          </div>
        </div>
      </div>
      <div class="modal-footer">
        <button type="button" class="btn btn-default" @click="showEditArticleModal = false">取消</button>
        <button type="button" class="btn btn-primary" @click="updateArticle">确定</button>
      </div>
    </div>
  </div>

  <!-- 添加笔记对话框 -->
  <div v-if="showAddNoteModal" class="modal-overlay" @click="showAddNoteModal = false">
    <div class="modal-container note-modal" @click.stop>
      <div class="modal-header">
        <h3 class="modal-title">添加笔记</h3>
        <button type="button" class="modal-close" @click="showAddNoteModal = false">×</button>
      </div>
      <div class="modal-body">
        <div class="form-container">
          <div class="form-item">
            <label class="form-label">笔记标题</label>
            <input v-model="newNote.noteTopic" type="text" class="form-input" placeholder="请输入笔记标题">
          </div>
          <div class="form-item">
            <label class="form-label">笔记内容</label>
            <textarea v-model="newNote.noteContent" class="form-textarea" placeholder="请输入笔记内容" rows="8"></textarea>
          </div>
        </div>
      </div>
      <div class="modal-footer">
        <button type="button" class="btn btn-default" @click="showAddNoteModal = false">取消</button>
        <button type="button" class="btn btn-primary" @click="addNote">确定</button>
      </div>
    </div>
  </div>

  <!-- 编辑笔记对话框 -->
  <div v-if="showEditNoteModal" class="modal-overlay" @click="showEditNoteModal = false">
    <div class="modal-container note-modal" @click.stop>
      <div class="modal-header">
        <h3 class="modal-title">编辑笔记</h3>
        <button type="button" class="modal-close" @click="showEditNoteModal = false">×</button>
      </div>
      <div class="modal-body">
        <div class="form-container">
          <div class="form-item">
            <label class="form-label">笔记标题</label>
            <input v-model="editNoteForm.noteTopic" type="text" class="form-input" placeholder="请输入笔记标题">
          </div>
          <div class="form-item">
            <label class="form-label">笔记内容</label>
            <textarea v-model="editNoteForm.noteContent" class="form-textarea" placeholder="请输入笔记内容"
              rows="8"></textarea>
          </div>
        </div>
      </div>
      <div class="modal-footer">
        <button type="button" class="btn btn-default" @click="showEditNoteModal = false">取消</button>
        <button type="button" class="btn btn-primary" @click="updateNote">确定</button>
      </div>
    </div>
  </div>

  <!-- 添加分类对话框 -->
  <div v-if="showAddCategoryModal" class="modal-overlay" @click="showAddCategoryModal = false">
    <div class="modal-container" @click.stop>
      <div class="modal-header">
        <h3 class="modal-title">添加分类</h3>
        <button type="button" class="modal-close" @click="showAddCategoryModal = false">×</button>
      </div>
      <div class="modal-body">
        <div class="form-container">
          <div class="form-item">
            <label class="form-label">分类名称</label>
            <input v-model="newCategory.categoryName" type="text" class="form-input" placeholder="请输入分类名称">
          </div>
        </div>
      </div>
      <div class="modal-footer">
        <button type="button" class="btn btn-default" @click="showAddCategoryModal = false">取消</button>
        <button type="button" class="btn btn-primary" @click="addCategory">确定</button>
      </div>
    </div>
  </div>

  <!-- 编辑分类对话框 -->
  <div v-if="showEditCategoryModal" class="modal-overlay" @click="showEditCategoryModal = false">
    <div class="modal-container" @click.stop>
      <div class="modal-header">
        <h3 class="modal-title">编辑分类</h3>
        <button type="button" class="modal-close" @click="showEditCategoryModal = false">×</button>
      </div>
      <div class="modal-body">
        <div class="form-container">
          <div class="form-item">
            <label class="form-label">分类名称</label>
            <input v-model="editCategoryForm.categoryName" type="text" class="form-input" placeholder="请输入分类名称">
          </div>
        </div>
      </div>
      <div class="modal-footer">
        <button type="button" class="btn btn-default" @click="showEditCategoryModal = false">取消</button>
        <button type="button" class="btn btn-primary" @click="updateCategory">确定</button>
      </div>
    </div>
  </div>

  <!-- 查看分类详情对话框 -->
  <div v-if="showViewCategoryModal" class="modal-overlay" @click="showViewCategoryModal = false">
    <div class="modal-container" @click.stop>
      <div class="modal-header">
        <h3 class="modal-title">分类详情</h3>
        <button type="button" class="modal-close" @click="showViewCategoryModal = false">×</button>
      </div>
      <div class="modal-body">
        <div class="detail-container">
          <div class="detail-item">
            <span class="detail-label">分类ID:</span>
            <span class="detail-value">{{ viewCategoryForm.categoryId }}</span>
          </div>
          <div class="detail-item">
            <span class="detail-label">分类名称:</span>
            <span class="detail-value">{{ viewCategoryForm.categoryName }}</span>
          </div>
          <!--          <div class="detail-item">
            <span class="detail-label">创建时间:</span>
            <span class="detail-value">{{ viewCategoryForm.categoryCreateTime || '-' }}</span>
          </div>-->
          <div class="detail-item">
            <span class="detail-label">关联文章数:</span>
            <span class="detail-value">{{ viewCategoryForm.articleCount || 0 }}</span>
          </div>
        </div>
      </div>
      <div class="modal-footer">
        <button type="button" class="btn btn-default" @click="showViewCategoryModal = false">关闭</button>
      </div>
    </div>
  </div>

  <!-- 查看用户详情对话框 -->
  <div v-if="showViewUserModal" class="modal-overlay" @click="showViewUserModal = false">
    <div class="modal-container" @click.stop>
      <div class="modal-header">
        <h3 class="modal-title">用户详情</h3>
        <button type="button" class="modal-close" @click="showViewUserModal = false">×</button>
      </div>
      <div class="modal-body">
        <div class="detail-container">
          <div class="detail-item">
            <span class="detail-label">用户ID:</span>
            <span class="detail-value">{{ viewUserForm.userId }}</span>
          </div>
          <div class="detail-item">
            <span class="detail-label">用户名:</span>
            <span class="detail-value">{{ viewUserForm.userName }}</span>
          </div>
          <div class="detail-item">
            <span class="detail-label">邮箱:</span>
            <span class="detail-value">{{ viewUserForm.userEmail }}</span>
          </div>
          <div class="detail-item">
            <span class="detail-label">角色:</span>
            <span class="detail-value">{{
              viewUserForm.userRole === 'admin' ? '管理员' : '普通用户'
            }}</span>
          </div>
          <div class="detail-item">
            <span class="detail-label">状态:</span>
            <span class="detail-value">{{ viewUserForm.userStatus === 1 ? '活跃' : '禁用' }}</span>
          </div>
          <div class="detail-item">
            <span class="detail-label">注册时间:</span>
            <span class="detail-value">{{ viewUserForm.userRegisterTime }}</span>
          </div>
          <div class="detail-item">
            <span class="detail-label">文章数:</span>
            <span class="detail-value">{{ viewUserForm.articleCount || 0 }}</span>
          </div>
          <div class="detail-item">
            <span class="detail-label">笔记数:</span>
            <span class="detail-value">{{ viewUserForm.noteCount || 0 }}</span>
          </div>
        </div>
      </div>
      <div class="modal-footer">
        <button type="button" class="btn btn-default" @click="showViewUserModal = false">关闭
        </button>
      </div>
    </div>
  </div>

  <!-- 查看文章详情对话框 -->
  <div v-if="showViewArticleModal" class="modal-overlay" @click="showViewArticleModal = false">
    <div class="modal-container view-article-modal" @click.stop>
      <div class="modal-header">
        <h3 class="modal-title">文章详情</h3>
        <button type="button" class="modal-close" @click="showViewArticleModal = false">×</button>
      </div>
      <div class="modal-body">
        <div class="article-detail-content">
          <div v-if="viewArticleForm.articleThumbnail" class="article-thumbnail-wrapper">
            <img :src="getArticleThumbnail(viewArticleForm)" alt="文章缩略图" class="article-detail-thumbnail"
              style="width:100%;border-radius: 10px">
          </div>
          <div class="detail-row">
            <div class="detail-item half">
              <span class="detail-label">文章ID:</span>
              <span class="detail-value">{{ viewArticleForm.articleId }}</span>
            </div>
            <div class="detail-item half">
              <span class="detail-label">作者:</span>
              <span class="detail-value">{{ viewArticleForm.userName || '未知' }}</span>
            </div>
          </div>
          <div class="detail-row">
            <div class="detail-item half">
              <span class="detail-label">状态:</span>
              <span class="detail-value">
                <span
                  :class="['status-tag', viewArticleForm.articleStatus === 1 ? 'status-published' : 'status-draft']">
                  {{ viewArticleForm.articleStatus === 1 ? '已发布' : '草稿' }}
                </span>
              </span>
            </div>
            <div class="detail-item half">
              <span class="detail-label">阅读量:</span>
              <span class="detail-value">{{ viewArticleForm.articleReadCount || 0 }}</span>
            </div>
          </div>
          <div class="detail-row">
            <div class="detail-item half">
              <span class="detail-label">评论数:</span>
              <span class="detail-value">{{ viewArticleForm.articleCommentCount || 0 }}</span>
            </div>
            <div class="detail-item half">
              <span class="detail-label">点赞数:</span>
              <span class="detail-value">{{ viewArticleForm.articleLikeCount || 0 }}</span>
            </div>
          </div>
          <div class="detail-row">
            <div class="detail-item half">
              <span class="detail-label">创建时间:</span>
              <span class="detail-value">{{ viewArticleForm.articleCreateTime || '-' }}</span>
            </div>
            <div class="detail-item half">
              <span class="detail-label">更新时间:</span>
              <span class="detail-value">{{ viewArticleForm.articleUpdateTime || '-' }}</span>
            </div>
          </div>
          <div class="detail-item full">
            <span class="detail-label">文章标题:</span>
            <span class="detail-value article-title">{{ viewArticleForm.articleTitle }}</span>
          </div>
          <div class="detail-item full">
            <span class="detail-label">文章摘要:</span>
            <span class="detail-value article-summary">{{ viewArticleForm.articleSummary || '暂无摘要' }}</span>
          </div>
          <div class="detail-item full">
            <span class="detail-label">文章内容:</span>
            <div class="article-content-wrapper">
              <div class="article-content" v-html="viewArticleForm.articleContent"></div>
            </div>
          </div>
        </div>
      </div>
      <div class="modal-footer">
        <button type="button" class="btn btn-default" @click="showViewArticleModal = false">关闭</button>
      </div>
    </div>
  </div>

  <!-- 查看笔记详情对话框 -->
  <div v-if="showViewNoteModal" class="modal-overlay" @click="showViewNoteModal = false">
    <div class="modal-container view-note-modal" @click.stop>
      <div class="modal-header">
        <h3 class="modal-title">笔记详情</h3>
        <button type="button" class="modal-close" @click="showViewNoteModal = false">×</button>
      </div>
      <div class="modal-body">
        <div class="note-detail-content">
          <div class="detail-row">
            <div class="detail-item half">
              <span class="detail-label">笔记ID:</span>
              <span class="detail-value">{{ viewNoteForm.noteId }}</span>
            </div>
            <div class="detail-item half">
              <span class="detail-label">作者:</span>
              <span class="detail-value">{{ viewNoteForm.noteAuthor || '未知' }}</span>
            </div>
          </div>
          <div class="detail-row">
            <div class="detail-item half">
              <span class="detail-label">创建时间:</span>
              <span class="detail-value">{{ viewNoteForm.noteCreateTime || '-' }}</span>
            </div>
            <div class="detail-item half">
              <span class="detail-label">更新时间:</span>
              <span class="detail-value">{{ viewNoteForm.noteUpdateTime || '-' }}</span>
            </div>
          </div>
          <div class="detail-item full">
            <span class="detail-label">笔记标题:</span>
            <span class="detail-value note-title">{{ viewNoteForm.noteTopic }}</span>
          </div>
          <div class="detail-item full">
            <span class="detail-label">笔记内容:</span>
            <div class="note-content-wrapper">
              <div class="note-content">{{ viewNoteForm.noteContent }}</div>
            </div>
          </div>
        </div>
      </div>
      <div class="modal-footer">
        <button type="button" class="btn btn-default" @click="showViewNoteModal = false">关闭</button>
      </div>
    </div>
  </div>

  <!-- 确认对话框 -->
  <div v-if="showConfirmModal" class="modal-overlay" @click="cancelConfirm">
    <div class="modal-container confirm-modal" @click.stop>
      <div class="modal-header confirm-header">
        <div class="confirm-icon">
          <Warning />
        </div>
        <h3 class="modal-title">{{ confirmTitle }}</h3>
        <button type="button" class="modal-close" @click="cancelConfirm">×</button>
      </div>
      <div class="modal-body">
        <p class="confirm-message">{{ confirmMessage }}</p>
      </div>
      <div class="modal-footer">
        <button type="button" class="btn btn-default" @click="cancelConfirm">取消</button>
        <button type="button" class="btn btn-danger" @click="confirmAction">确定删除</button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { useUserInfoStore } from '@/stores/modules/userInfo.js'
import axiosAPI from '@/utils/api/axios.js'
import { ChatDotRound, EditPen, Folder, Notebook, UserFilled, Warning } from '@element-plus/icons-vue'
import { computed, onMounted, reactive, ref } from 'vue'
// 移除Element Plus消息组件，使用自定义组件

const userStore = useUserInfoStore()
// 页面状态
const currentPage = ref('dashboard')
// 搜索和筛选
const searchKeyword = ref('')
const filterRole = ref('')
// 选择的项目
const selectedItems = ref([])
// 分页相关
const paginationCurrentPage = ref(1)
const paginationPageSize = ref(10)
const total = ref(0)
// 统计数据
const stats = ref({
  userCount: 0,
  articleCount: 0,
  noteCount: 0,
  commentCount: 0,
  categoryCount: 0
})
// 用户管理相关
const users = ref([])
const showAddUserModal = ref(false)
const showEditUserModal = ref(false)
const showViewUserModal = ref(false)
// 文章管理相关
const articles = ref([])
const showAddArticleModal = ref(false)
const showEditArticleModal = ref(false)
// 笔记管理相关
const notes = ref([])
const showAddNoteModal = ref(false)
const showEditNoteModal = ref(false)
// 分类管理相关
const categories = ref([])
const showAddCategoryModal = ref(false)
const showEditCategoryModal = ref(false)
const showViewCategoryModal = ref(false)
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
const showViewArticleModal = ref(false)
const showViewNoteModal = ref(false)
const viewArticleForm = ref({})
const viewNoteForm = ref({})
// 确认对话框状态
const showConfirmModal = ref(false)
const confirmTitle = ref('')
const confirmMessage = ref('')
const confirmCallback = ref(null)
// 文章表单数据
const newArticle = ref({
  articleTitle: '',
  articleContent: '',
  articleSummary: '',
  articleThumbnail: '',
  articleStatus: 1
})
const editArticleForm = ref({
  articleId: '',
  articleTitle: '',
  articleContent: '',
  articleSummary: '',
  articleThumbnail: '',
  articleStatus: 1
})
// 笔记表单数据
const newNote = ref({
  noteTopic: '',
  noteContent: ''
})
const editNoteForm = ref({
  noteId: '',
  noteTopic: '',
  noteContent: ''
})
// 分类表单数据
const newCategory = ref({
  categoryName: ''
})
const editCategoryForm = ref({
  categoryId: '',
  categoryName: ''
})
const viewCategoryForm = ref({})
// 表单验证规则
const userRules = reactive({
  userName: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  userPassword: [{ required: true, message: '请输入密码', trigger: 'blur' }],
  userEmail: [
    { required: true, message: '请输入邮箱', trigger: 'blur' },
    { type: 'email', message: '请输入有效的邮箱地址', trigger: 'blur' }
  ],
  userRole: [{ required: true, message: '请选择角色', trigger: 'change' }]
})
// 计算属性
const pageUsers = computed(() => {
  return users.value
})
const pageTitle = computed(() => {
  const titles = {
    dashboard: '仪表盘',
    user: '用户管理',
    article: '文章管理',
    note: '笔记管理',
    category: '分类管理',
    stat: '数据统计'
  }
  return titles[currentPage.value] || '仪表盘'
})
// 分页相关计算属性
const totalPages = computed(() => {
  return Math.ceil(total.value / paginationPageSize.value)
})
const pageNumbers = computed(() => {
  const pages = []
  const current = paginationCurrentPage.value
  const total = totalPages.value

  // 显示当前页附近的页码
  let start = Math.max(1, current - 2)
  let end = Math.min(total, current + 2)

  // 确保至少显示5个页码
  if (end - start < 4) {
    if (start === 1) {
      end = Math.min(5, total)
    } else if (end === total) {
      start = Math.max(1, total - 4)
    }
  }

  for (let i = start; i <= end; i++) {
    pages.push(i)
  }
  return pages
})
// 跳转页面相关
const jumpPage = ref(1)
const jumpToPage = () => {
  let page = parseInt(jumpPage.value)
  if (isNaN(page)) {
    page = 1
  }
  page = Math.max(1, Math.min(page, totalPages.value))
  handleCurrentChange(page)
  jumpPage.value = page
}
// 方法
const switchPage = (page) => {
  currentPage.value = page
  paginationCurrentPage.value = 1
  switch (page) {
    case 'dashboard':
      getStats()
      break
    case 'user':
      getAllUsers()
      break
    case 'article':
      getAllArticles()
      break
    case 'note':
      getAllNotes()
      break
    case 'category':
      getAllCategories()
      break
    case 'stat':
      getStats()
      break
  }
}
// 获取统计数据
const getStats = async () => {
  try {
    const response = await axiosAPI.get('/admin/manage/statistics')
    if (response.data.code === 1) {
      const statistics = response.data.data
      stats.value.userCount = statistics.userCount || 0
      stats.value.articleCount = statistics.articleCount || 0
      stats.value.noteCount = statistics.noteCount || 0
      stats.value.commentCount = statistics.commentCount || 0
      stats.value.categoryCount = statistics.categoryCount || 0
    } else {
      // ElMessage.error('获取统计数据失败：' + response.data.msg)
      alert('获取统计数据失败：' + response.data.msg)
    }
  } catch (error) {
    console.error('获取统计数据失败:', error)
    //ElMessage.error('获取统计数据失败')
  }
  // 获取分类总数
  await getCategoryCount()
}
// 获取分类总数
const getCategoryCount = async () => {
  try {
    const response = await axiosAPI.get('/admin/manage/categories')
    if (response.data.code === 1) {
      if (Array.isArray(response.data.data)) {
        stats.value.categoryCount = response.data.data.length
      } else {
        stats.value.categoryCount = response.data.data.total || 0
      }
    }
  } catch (error) {
    console.error('获取分类总数失败:', error)
  }
}
// 获取所有用户
const getAllUsers = async () => {
  try {
    const response = await axiosAPI.get('/admin/manage/users', {
      params: {
        page: paginationCurrentPage.value,
        pageSize: paginationPageSize.value
      }
    })
    if (response.data.code === 1) {
      users.value = response.data.data.records
      total.value = response.data.data.total
      stats.value.userCount = response.data.data.total
    } else {
      //ElMessage.error('获取用户列表失败：' + response.data.msg)
      alert('获取用户列表失败：' + response.data.msg)
      users.value = []
      total.value = 0
    }
  } catch (error) {
    console.error('获取用户列表失败:', error)
    //ElMessage.error('获取用户列表失败')

  }
}
// 获取所有文章
const getAllArticles = async () => {
  try {
    const response = await axiosAPI.get('/admin/manage/articles', {
      params: {
        page: paginationCurrentPage.value,
        pageSize: paginationPageSize.value
      }
    })
    if (response.data.code === 1) {
      articles.value = response.data.data.records
      total.value = response.data.data.total
      stats.value.articleCount = response.data.data.total
    } else {
      // ElMessage.error('获取文章列表失败：' + response.data.msg)
      alert('获取文章列表失败：' + response.data.msg)
      articles.value = []
      total.value = 0
    }
  } catch (error) {
    console.error('获取文章列表失败:', error)
    //ElMessage.error('获取文章列表失败')
  }
}
// 获取所有笔记
const getAllNotes = async () => {
  try {
    const response = await axiosAPI.get('/admin/manage/notes', {
      params: {
        page: paginationCurrentPage.value,
        pageSize: paginationPageSize.value
      }
    })
    if (response.data.code === 1) {
      notes.value = response.data.data.records
      total.value = response.data.data.total
      stats.value.noteCount = response.data.data.total
    } else {
      //ElMessage.error('获取笔记列表失败：' + response.data.msg)
      alert('获取笔记列表失败：' + response.data.msg)
      notes.value = []
      total.value = 0
    }
  } catch (error) {
    console.error('获取笔记列表失败:', error)
    //ElMessage.error('获取笔记列表失败')
  }
}
// 分页方法
const handleSizeChange = (size) => {
  paginationPageSize.value = size
  switch (currentPage.value) {
    case 'user':
      getAllUsers()
      break
    case 'article':
      getAllArticles()
      break
    case 'note':
      getAllNotes()
      break
    case 'category':
      getAllCategories()
      break
  }
}
const handleCurrentChange = (current) => {
  paginationCurrentPage.value = current
  switch (currentPage.value) {
    case 'user':
      getAllUsers()
      break
    case 'article':
      getAllArticles()
      break
    case 'note':
      getAllNotes()
      break
    case 'category':
      getAllCategories()
      break
  }
}
// 搜索方法
const handleSearch = async () => {
  try {
    let response
    switch (currentPage.value) {
      case 'user':
        console.log('搜索用户')
        response = await axiosAPI.post('/admin/manage/search/users', {
          userName: searchKeyword.value,
          page: 1,
          pageSize: paginationPageSize.value
        })
        break
      case 'article':
        console.log('搜索文章')
        response = await axiosAPI.post('/admin/manage/search/articles', {}, {
          params: {
            page: 1,
            pageSize: paginationPageSize.value,
            keyword: searchKeyword.value
          }
        })
        break
      case 'note':
        console.log('搜索笔记')
        response = await axiosAPI.post('/admin/manage/search/notes', {}, {
          params: {
            page: 1,
            pageSize: paginationPageSize.value,
            keyword: searchKeyword.value
          }
        })
        break
      case 'category':
        console.log('搜索分类')
        response = await axiosAPI.get('/admin/manage/categories', {
          params: {
            page: 1,
            pageSize: paginationPageSize.value,
            keyword: searchKeyword.value
          }
        })
        break
      default:
        return
    }

    if (response.data.code === 1) {
      if (currentPage.value === 'user') {
        users.value = response.data.data.records
      } else if (currentPage.value === 'article') {
        articles.value = response.data.data.records
      } else if (currentPage.value === 'note') {
        notes.value = response.data.data.records
      } else if (currentPage.value === 'category') {
        categories.value = response.data.data.records
      }
      total.value = response.data.data.total
      paginationCurrentPage.value = 1
    } else {
      alert('搜索失败：' + response.data.msg)
    }
  } catch (error) {
    console.error('搜索失败:', error)
    alert('搜索失败')
  }
}

// 重置筛选条件
const resetFilter = async () => {
  searchKeyword.value = ''
  filterRole.value = ''
  paginationCurrentPage.value = 1
  switch (currentPage.value) {
    case 'user':
      getAllUsers()
      break
    case 'article':
      getAllArticles()
      break
    case 'note':
      getAllNotes()
      break
    case 'category':
      getAllCategories()
      break
  }
}
// 表格选择变化处理
const handleSelectionChange = (selection) => {
  selectedItems.value = selection
}
// 全选功能
const selectAll = (checked) => {
  let items = []
  switch (currentPage.value) {
    case 'user':
      items = users.value
      break
    case 'article':
      items = articles.value
      break
    case 'note':
      items = notes.value
      break
    case 'category':
      items = categories.value
      break
  }
  if (checked) {
    selectedItems.value = [...items]
  } else {
    selectedItems.value = []
  }
}
// 单个选择功能
const toggleSelection = (item, checked) => {
  const index = selectedItems.value.findIndex(selected => {
    if (currentPage.value === 'user') {
      return selected.userId === item.userId
    } else if (currentPage.value === 'article') {
      return selected.articleId === item.articleId
    } else if (currentPage.value === 'note') {
      return selected.noteId === item.noteId
    } else if (currentPage.value === 'category') {
      return selected.categoryId === item.categoryId
    }
    return false
  })

  if (checked && index === -1) {
    selectedItems.value.push(item)
  } else if (!checked && index !== -1) {
    selectedItems.value.splice(index, 1)
  }
}
// 打开添加对话框
const openAddDialog = () => {
  switch (currentPage.value) {
    case 'user':
      showAddUserModal.value = true
      break
    case 'article':
      showAddArticleModal.value = true
      break
    case 'note':
      showAddNoteModal.value = true
      break
    case 'category':
      showAddCategoryModal.value = true
      break
  }
}
// 打开确认对话框
const openConfirm = (title, message, callback) => {
  confirmTitle.value = title
  confirmMessage.value = message
  confirmCallback.value = callback
  showConfirmModal.value = true
}
// 取消确认
const cancelConfirm = () => {
  showConfirmModal.value = false
  confirmCallback.value = null
}
// 确认操作
const confirmAction = async () => {
  if (confirmCallback.value) {
    await confirmCallback.value()
  }
  showConfirmModal.value = false
  confirmCallback.value = null
}
// 批量删除
const batchDelete = () => {
  switch (currentPage.value) {
    case 'user':
      batchDeleteUsers()
      break
    case 'article':
      batchDeleteArticles()
      break
    case 'note':
      batchDeleteNotes()
      break
    case 'category':
      batchDeleteCategories()
      break
  }
}
// 用户操作：删除单个用户
const deleteUser = (id) => {
  openConfirm(
    '删除确认',
    '确定要删除该用户吗？此操作不可恢复。',
    async () => {
      try {
        const response = await axiosAPI.delete('/admin/manage/delete/user', { params: { id } })
        if (response.data.code === 1) {
          alert('删除成功')
          await getAllUsers()
        } else {
          alert('删除失败：' + response.data.msg)
        }
      } catch (error) {
        console.error('删除失败:', error)
        alert('删除失败')
      }
    }
  )
}
// 用户操作：批量删除用户
const batchDeleteUsers = async () => {
  if (selectedItems.value.length === 0) {
    alert('请先选择要删除的用户')
    return
  }

  openConfirm(
    '批量删除确认',
    `确定要删除选中的 ${selectedItems.value.length} 个用户吗？此操作不可恢复。`,
    async () => {
      const ids = selectedItems.value.map(item => item.userId).join(',')
      try {
        const response = await axiosAPI.delete('/admin/manage/delete/users', {
          params: { ids }
        })
        if (response.data.code === 1) {
          alert('批量删除成功')
          await getAllUsers()
          selectedItems.value = []
        } else {
          alert('批量删除失败：' + response.data.msg)
        }
      } catch (error) {
        console.error('批量删除失败:', error)
        alert('批量删除失败')
      }
    }
  )
}
// 用户操作：添加用户
const addUser = async () => {
  try {
    const response = await axiosAPI.post('/admin/manage/add/user', newUser.value)
    if (response.data.code === 1) {
      alert('添加用户成功')
      await getAllUsers()
      newUser.value = {
        userName: '',
        userPassword: '',
        userEmail: '',
        userRole: 'user',
        userStatus: 1
      }
      showAddUserModal.value = false
    } else {
      alert('添加用户失败：' + response.data.msg)
    }
  } catch (error) {
    console.error('添加用户失败:', error)
    alert('添加用户失败')
  }
}
// 用户操作：编辑用户
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
// 用户操作：更新用户
const updateUser = async () => {
  try {
    const response = await axiosAPI.put('/admin/manage/update/user', editUserForm.value)
    if (response.data.code === 1) {
      alert('更新用户成功')
      await getAllUsers()
      showEditUserModal.value = false
    } else {
      alert('更新用户失败：' + response.data.msg)
    }
  } catch (error) {
    console.error('更新用户失败:', error)
    alert('更新用户失败')
  }
}
// 用户操作：查看用户详情
const viewUser = (user) => {
  viewUserForm.value = user
  showViewUserModal.value = true
}


const truncateTitle = (title, maxLength = 10) => {
  if (!title) return ''
  return title.length > maxLength ? title.substring(0, maxLength) + '...' : title
}

// 文章操作：删除单个文章
const deleteArticle = (id) => {
  openConfirm(
    '删除确认',
    '确定要删除该文章吗？此操作不可恢复。',
    async () => {
      try {
        const response = await axiosAPI.delete(`/admin/manage/delete/article`, { params: { id } })
        if (response.data.code === 1) {
          alert('删除成功')
          await getAllArticles()
        } else {
          alert('删除失败：' + response.data.msg)
        }
      } catch (error) {
        console.error('删除失败:', error)
        alert('删除失败')
      }
    }
  )
}
// 文章操作：批量删除文章
const batchDeleteArticles = async () => {
  if (selectedItems.value.length === 0) {
    alert('请先选择要删除的文章')
    return
  }

  openConfirm(
    '批量删除确认',
    `确定要删除选中的 ${selectedItems.value.length} 篇文章吗？此操作不可恢复。`,
    async () => {
      const ids = selectedItems.value.map(item => item.articleId).join(',')
      try {
        const response = await axiosAPI.delete('/admin/manage/delete/articles', {
          params: { ids }
        })
        if (response.data.code === 1) {
          alert('批量删除成功')
          await getAllArticles()
          selectedItems.value = []
        } else {
          alert('批量删除失败：' + response.data.msg)
        }
      } catch (error) {
        console.error('批量删除失败:', error)
        alert('批量删除失败')
      }
    }
  )
}
// 文章操作：编辑文章
const editArticle = (article) => {
  editArticleForm.value = {
    articleId: article.articleId,
    articleTitle: article.articleTitle,
    articleContent: article.articleContent || '',
    articleSummary: article.articleSummary || '',
    articleThumbnail: article.articleThumbnail || '',
    articleStatus: article.articleStatus || 1
  }
  showEditArticleModal.value = true
}
// 文章操作：查看文章详情
const viewArticle = (article) => {
  viewArticleForm.value = { ...article }
  showViewArticleModal.value = true
}
// 文章操作：获取文章缩略图
const getArticleThumbnail = (article) => {
  if (article.articleThumbnail && article.articleThumbnail.startsWith('http')) {
    return article.articleThumbnail
  }
  if (article.articleThumbnail) {
    return `/uploaded-images/${article.articleThumbnail}`
  }
  return '/default-article.jpg'
}
// 文章操作：添加文章
const addArticle = async () => {
  try {
    const response = await axiosAPI.post('/user/articles', {
      articleTitle: newArticle.value.articleTitle,
      articleContent: newArticle.value.articleContent,
      articleSummary: newArticle.value.articleSummary,
      articleThumbnail: newArticle.value.articleThumbnail,
      articleStatus: newArticle.value.articleStatus
    })
    if (response.data.code === 1) {
      alert('添加文章成功')
      await getAllArticles()
      showAddArticleModal.value = false
      // 重置表单
      newArticle.value = {
        articleTitle: '',
        articleContent: '',
        articleSummary: '',
        articleThumbnail: '',
        articleStatus: 1
      }
    } else {
      alert('添加文章失败：' + response.data.msg)
    }
  } catch (error) {
    console.error('添加文章失败:', error)
    alert('添加文章失败')
  }
}
// 文章操作：更新文章
const updateArticle = async () => {
  try {
    const response = await axiosAPI.put('/user/articles/' + editArticleForm.value.articleId, editArticleForm.value)
    if (response.data.code === 1) {
      alert('更新文章成功')
      await getAllArticles()
      showEditArticleModal.value = false
    } else {
      alert('更新文章失败：' + response.data.msg)
    }
  } catch (error) {
    console.error('更新文章失败:', error)
    alert('更新文章失败')
  }
}
// 笔记操作：删除单个笔记
const deleteNote = (id) => {
  openConfirm(
    '删除确认',
    '确定要删除该笔记吗？此操作不可恢复。',
    async () => {
      try {
        const response = await axiosAPI.delete(`/admin/manage/delete/note`, { params: { id } })
        if (response.data.code === 1) {
          alert('删除成功')
          await getAllNotes()
        } else {
          alert('删除失败：' + response.data.msg)
        }
      } catch (error) {
        console.error('删除失败:', error)
        alert('删除失败')
      }
    }
  )
}
// 笔记操作：批量删除笔记
const batchDeleteNotes = async () => {
  if (selectedItems.value.length === 0) {
    alert('请先选择要删除的笔记')
    return
  }

  openConfirm(
    '批量删除确认',
    `确定要删除选中的 ${selectedItems.value.length} 篇笔记吗？此操作不可恢复。`,
    async () => {
      const ids = selectedItems.value.map(item => item.noteId).join(',')
      try {
        const response = await axiosAPI.delete('/admin/manage/delete/notes', {
          params: { ids }
        })
        if (response.data.code === 1) {
          alert('批量删除成功')
          await getAllNotes()
          selectedItems.value = []
        } else {
          alert('批量删除失败：' + response.data.msg)
        }
      } catch (error) {
        console.error('批量删除失败:', error)
        alert('批量删除失败')
      }
    }
  )
}
// 笔记操作：编辑笔记
const editNote = async (note) => {
  editNoteForm.value = {
    noteId: note.noteId,
    noteTopic: note.noteTopic,
    noteContent: note.noteContent || ''
  }
  showEditNoteModal.value = true

}
// 笔记操作：查看笔记详情
const viewNote = (note) => {
  viewNoteForm.value = { ...note }
  showViewNoteModal.value = true
}
// 笔记操作：添加笔记
const addNote = async () => {
  try {
    const response = await axiosAPI.post('/user/notes/insert', {
      noteTopic: newNote.value.noteTopic,
      noteContent: newNote.value.noteContent
    })
    if (response.data.code === 1) {
      alert('添加笔记成功')
      await getAllNotes()
      showAddNoteModal.value = false
      // 重置表单
      newNote.value = {
        noteTopic: '',
        noteContent: ''
      }
    } else {
      alert('添加笔记失败：' + response.data.msg)
    }
  } catch (error) {
    console.error('添加笔记失败:', error)
    alert('添加笔记失败')
  }
}
// 笔记操作：更新笔记
const updateNote = async () => {
  try {
    const response = await axiosAPI.put('/user/notes/update', {
      noteId: editNoteForm.value.noteId,
      noteTopic: editNoteForm.value.noteTopic,
      noteContent: editNoteForm.value.noteContent
    })
    if (response.data.code === 1) {
      alert('更新笔记成功')
      await getAllNotes()
      showEditNoteModal.value = false
    } else {
      alert('更新笔记失败：' + response.data.msg)
    }
  } catch (error) {
    console.error('更新笔记失败:', error)
    alert('更新笔记失败')
  }
}
// 分类操作：获取所有分类
const getAllCategories = async () => {
  try {
    const response = await axiosAPI.get('/admin/manage/categories', {
      params: {
        page: paginationCurrentPage.value,
        pageSize: paginationPageSize.value
      }
    })
    if (response.data.code === 1) {
      console.log('分类列表:', response.data.data)
      // 如果返回的是数组，直接使用；否则尝试获取records
      if (Array.isArray(response.data.data)) {
        categories.value = response.data.data
        total.value = response.data.data.length
      } else {
        categories.value = response.data.data.records || []
        total.value = response.data.data.total || 0
      }
    } else {
      alert('获取分类列表失败：' + response.data.msg)
      categories.value = []
      total.value = 0
    }
  } catch (error) {
    console.error('获取分类列表失败:', error)
    alert('获取分类列表失败')
  }
}
// 分类操作：删除单个分类
const deleteCategory = (id) => {
  openConfirm(
    '删除确认',
    '确定要删除该分类吗？此操作不可恢复。',
    async () => {
      try {
        const response = await axiosAPI.delete(`/admin/manage/delete/category`, { params: { id } })
        if (response.data.code === 1) {
          alert('删除成功')
          await getAllCategories()
        } else {
          alert('删除失败：' + response.data.msg)
        }
      } catch (error) {
        console.error('删除失败:', error)
        alert('删除失败')
      }
    }
  )
}
// 分类操作：批量删除分类
const batchDeleteCategories = async () => {
  if (selectedItems.value.length === 0) {
    alert('请先选择要删除的分类')
    return
  }

  openConfirm(
    '批量删除确认',
    `确定要删除选中的 ${selectedItems.value.length} 个分类吗？此操作不可恢复。`,
    async () => {
      const ids = selectedItems.value.map(item => item.categoryId).join(',')
      try {
        const response = await axiosAPI.delete('/admin/manage/delete/categories', {
          params: { ids }
        })
        if (response.data.code === 1) {
          alert('批量删除成功')
          await getAllCategories()
          selectedItems.value = []
        } else {
          alert('批量删除失败：' + response.data.msg)
        }
      } catch (error) {
        console.error('批量删除失败:', error)
        alert('批量删除失败')
      }
    }
  )
}
// 分类操作：编辑分类
const editCategory = (category) => {
  editCategoryForm.value = {
    categoryId: category.categoryId,
    categoryName: category.categoryName
  }
  showEditCategoryModal.value = true
}
// 分类操作：查看分类详情
const viewCategory = (category) => {
  viewCategoryForm.value = { ...category }
  showViewCategoryModal.value = true
}
// 分类操作：添加分类
const addCategory = async () => {
  if (!newCategory.value.categoryName.trim()) {
    alert('请输入分类名称')
    return
  }
  try {
    const response = await axiosAPI.post('/admin/manage/add/category', {
      categoryName: newCategory.value.categoryName
    })
    if (response.data.code === 1) {
      alert('添加分类成功')
      await getAllCategories()
      showAddCategoryModal.value = false
      newCategory.value = {
        categoryName: ''
      }
    } else {
      alert('添加分类失败：' + response.data.msg)
    }
  } catch (error) {
    console.error('添加分类失败:', error)
    alert('添加分类失败')
  }
}
// 分类操作：更新分类
const updateCategory = async () => {
  if (!editCategoryForm.value.categoryName.trim()) {
    alert('请输入分类名称')
    return
  }
  try {
    const response = await axiosAPI.put('/admin/manage/update/category', {
      categoryId: editCategoryForm.value.categoryId,
      categoryName: editCategoryForm.value.categoryName
    })
    if (response.data.code === 1) {
      alert('更新分类成功')
      await getAllCategories()
      showEditCategoryModal.value = false
    } else {
      alert('更新分类失败：' + response.data.msg)
    }
  } catch (error) {
    console.error('更新分类失败:', error)
    alert('更新分类失败')
  }
}
// 退出登录
const handleLogout = async () => {
  if (confirm('确定要退出登录吗？')) {

    try {
      const response = await axiosAPI.post('/logout')
      if (response.data.code === 1) {
        userStore.clearUserInfo()
        window.location.href = '/'
      } else {
        alert('退出登录失败：' + response.data.msg)
      }
    } catch (error) {
      console.error('退出登录失败:', error)
      userStore.clearUserInfo()
      window.location.href = '/'
    }

  }


}
// 页面加载时初始化
onMounted(() => {
  getStats()
  switchPage('dashboard')
})
</script>

<style scoped>
/* 布局核心：弹性铺满 */
#main_Content {
  /* —— 面板专用变量：此前缺失导致面板透明、激活态无高亮色 —— */
  --card-bg: rgba(255, 253, 248, 0.86);
  /* 半透明暖白：面板毛玻璃透出背景图 */
  --card-bg-solid: #FFFDF8;
  /* 不透明暖白：输入框/按钮/弹窗等控件 */
  --bg-color: #FDF8EF;
  --accent-color: #C2410C;
  /* 暖橙棕：与背景图色调呼应 */
  --accent-hover: #9A3412;
  --border-color: #EADFCE;
  --text-primary: #3D3327;
  --text-secondary: #7A6A55;

  display: flex;
  width: 100%;
  min-height: 100vh;
  /* 暖色调背景图 + 半透明遮罩，保证文字与卡片可读 */
  background:
    linear-gradient(rgba(253, 248, 239, 0.82), rgba(250, 243, 232, 0.86)),
    url('@/assets/backgrounds/admin_bg.png') center / cover no-repeat fixed;
  overflow-x: hidden;
}

/* 侧边栏保留 */
.sidebar {
  width: 240px;
  flex-shrink: 0;
  background: var(--card-bg);
  backdrop-filter: blur(12px);
  -webkit-backdrop-filter: blur(12px);
  padding: 20px;
  border-right: 1px solid var(--border-color);
  box-shadow: 2px 0 10px rgba(0, 0, 0, 0.05);
}

.sidebar-header {
  text-align: center;
  margin-bottom: 30px;
  padding-bottom: 20px;
  border-bottom: 1px solid var(--border-color);
}

.sidebar-avatar {
  width: 80px;
  height: 80px;
  border-radius: 50%;
  margin: 15px auto;
  display: block;
  border: 3px solid var(--accent-color);
}

.sidebar-nav {
  list-style: none;
  padding: 0;
  margin: 0;
}

.sidebar-nav li {
  margin-bottom: 8px;
}

.sidebar-nav a {
  display: block;
  padding: 12px 15px;
  color: var(--text-primary);
  text-decoration: none;
  border-radius: 8px;
  transition: all 0.3s ease;
  font-size: 15px;
}

.sidebar-nav a:hover,
.sidebar-nav a.active {
  background: var(--accent-color);
  color: #fff;
  transform: translateX(5px);
}

/* 主内容区：自适应撑满 */
.main-content {
  flex: 1;
  display: flex;
  flex-direction: column;
  min-width: 0;
  /* 关键：解决flex溢出 */
}

/* 顶部导航保留 */
.top-nav {
  height: 60px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 24px;
  background: var(--card-bg);
  border-bottom: 1px solid var(--border-color);
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.05);
}

.logo {
  font-size: 20px;
  font-weight: bold;
  color: var(--accent-color);
}

.nav-menu {
  display: flex;
  gap: 24px;
}

.nav-menu a {
  color: var(--text-primary);
  text-decoration: none;
  font-size: 15px;
}

.nav-menu a:hover {
  color: var(--accent-color);
}

/* 统计卡片：自适应网格 */
.stats-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(260px, 1fr));
  gap: 20px;
  padding: 0 20px;
  margin: 20px 0;
}

.stat-card {
  border-radius: 12px;
  background: var(--card-bg);
  border: 1px solid var(--border-color);
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.05);
  transition: transform 0.3s;
}

.stat-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 10px 20px rgba(0, 0, 0, 0.1);
}

.stat-content {
  display: flex;
  align-items: center;
  gap: 15px;
  padding: 20px;
}

.stat-icon {
  width: 60px;
  height: 60px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #fff;
  opacity: 0.9;
}

.stat-icon svg {
  width: 28px;
  height: 28px;
}

/* 用户统计卡片 - 蓝色 */
.user-stat .stat-icon {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}

.user-number {
  font-size: 32px;
  font-weight: bold;
  color: #667eea;
}

/* 文章统计卡片 - 绿色 */
.article-stat .stat-icon {
  background: linear-gradient(135deg, #11998e 0%, #38ef7d 100%);
}

.article-number {
  font-size: 32px;
  font-weight: bold;
  color: #11998e;
}

/* 笔记统计卡片 - 橙色 */
.note-stat .stat-icon {
  background: linear-gradient(135deg, #fc4a1a 0%, #f7b733 100%);
}

.note-number {
  font-size: 32px;
  font-weight: bold;
  color: #fc4a1a;
}

/* 评论统计卡片 - 紫色 */
.comment-stat .stat-icon {
  background: linear-gradient(135deg, #a8edea 0%, #fed6e3 100%);
  color: #6c5ce7;
}

.comment-number {
  font-size: 32px;
  font-weight: bold;
  color: #6c5ce7;
}

/* 分类统计卡片 - 青色 */
.category-stat .stat-icon {
  background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%);
  color: #fff;
}

.category-number {
  font-size: 32px;
  font-weight: bold;
  color: #4facfe;
}

.stat-number {
  font-size: 32px;
  font-weight: bold;
}

.stat-label {
  font-size: 13px;
  color: var(--text-secondary);
  margin-top: 6px;
  text-transform: uppercase;
  letter-spacing: 0.5px;
}

/* 面包屑 */
.breadcrumb {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 14px;
  color: var(--text-secondary);
}

.breadcrumb a {
  color: var(--accent-color);
  text-decoration: none;
}

.breadcrumb a:hover {
  text-decoration: underline;
}

.breadcrumb-separator {
  color: var(--text-secondary);
}

/* 工具栏：左右分布，不挤压 */
.toolbar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 12px 20px;
  margin: 0 20px 20px;
  background: var(--card-bg);
  border-radius: 12px;
  border: 1px solid var(--border-color);
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.05);
  flex-wrap: wrap;
  gap: 12px;
}

.toolbar-actions {
  display: flex;
  align-items: center;
  gap: 20px;
  flex-wrap: wrap;
  width: 100%;
  justify-content: space-between;
}

.filter-section {
  display: flex;
  align-items: center;
  gap: 10px;
  flex-wrap: wrap;
}

.action-section {
  display: flex;
  align-items: center;
  gap: 10px;
  flex-wrap: wrap;
}

/* 表单元素样式 */
.form-input,
.form-select {
  padding: 8px 12px;
  border: 1px solid var(--border-color);
  border-radius: 4px;
  background: var(--card-bg);
  color: var(--text-primary);
  font-size: 14px;
  transition: all 0.3s ease;
}

.form-input:focus,
.form-select:focus {
  outline: none;
  border-color: var(--accent-color);
  box-shadow: 0 0 0 2px rgba(0, 123, 255, 0.25);
}

.form-input-sm {
  padding: 4px 8px;
  font-size: 12px;
  width: 60px;
}

.form-select-sm {
  padding: 4px 8px;
  font-size: 12px;
  width: 80px;
}

/* 按钮样式 */
.btn {
  padding: 8px 16px;
  border: none;
  border-radius: 4px;
  font-size: 14px;
  cursor: pointer;
  transition: all 0.3s ease;
  font-weight: 500;
}

.btn-primary {
  background: var(--accent-color);
  color: white;
}

.btn-primary:hover {
  background: var(--accent-hover);
}

.btn-default {
  background: var(--card-bg);
  color: var(--text-primary);
  border: 1px solid var(--border-color);
}

.btn-default:hover {
  background: var(--border-color);
}

.btn-info {
  background: #17a2b8;
  color: white;
}

.btn-info:hover {
  background: #138496;
}

.btn-danger {
  background: #dc3545;
  color: white;
}

.btn-danger:hover {
  background: #c82333;
}

.btn-sm {
  padding: 4px 8px;
  font-size: 12px;
}

.btn:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}

/* 内容区域：全屏宽度，无内边距溢出 */
.content-area {
  flex: 1;
  padding: 0 20px 20px;
}

.container-wrapper {
  width: 100%;
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.table-section {
  width: 100%;
}

/* 内容卡片 */
.content-card {
  width: 100%;
  background: var(--card-bg);
  backdrop-filter: blur(12px);
  -webkit-backdrop-filter: blur(12px);
  border-radius: 12px;
  border: 1px solid var(--border-color);
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.05);
  overflow: hidden;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 16px 20px;
  border-bottom: 1px solid var(--border-color);
}

.card-header h5 {
  margin: 0;
  font-size: 18px;
  color: var(--text-primary);
}

.card-body {
  padding: 20px;
}

.header-actions {
  display: flex;
  gap: 10px;
}

/* 表格样式 */
.data-table {
  width: 100%;
  border-collapse: collapse;
  margin: 0;
}

.data-table th,
.data-table td {
  padding: 12px;
  text-align: center;
  border-bottom: 1px solid var(--border-color);
}

.data-table th {
  background: var(--card-bg);
  font-weight: 600;
  color: var(--text-primary);
  border-bottom: 2px solid var(--accent-color);
}

.data-table tr:hover {
  background: rgba(0, 0, 0, 0.02);
}

.checkbox-column {
  width: 50px;
}

.action-buttons {
  display: flex;
  //gap: 105px;
  justify-content: space-around;
}

.action-buttons:hover {}

/* !important */
.text-left {
  text-align: left;
}

/* 标签样式 */
.tag {
  display: inline-block;
  padding: 2px 8px;
  border-radius: 12px;
  font-size: 12px;
  font-weight: 500;
}

.tag-primary {
  background: #007bff;
  color: white;
}

.tag-success {
  background: #28a745;
  color: white;
}

.tag-danger {
  background: #dc3545;
  color: white;
}

.tag-info {
  background: #17a2b8;
  color: white;
}

/* 空状态 */
.empty-state {
  text-align: center;
  padding: 40px;
  color: var(--text-secondary);
  background: var(--card-bg);
  border-radius: 8px;
  border: 1px solid var(--border-color);
}

/* 分页样式 */
.pagination {
  display: flex;
  align-items: center;
  justify-content: space-between;
  background: var(--card-bg);
  border-radius: 8px;
  padding: 12px 20px;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.05);
  border: 1px solid var(--border-color);
  margin-top: 20px;
  flex-wrap: wrap;
  gap: 10px;
}

.pagination-info {
  font-size: 14px;
  color: var(--text-secondary);
}

.pagination-controls {
  display: flex;
  align-items: center;
  gap: 5px;
}

.pagination-pages {
  display: flex;
  gap: 2px;
}

.pagination-page {
  display: inline-block;
  width: 32px;
  height: 32px;
  line-height: 32px;
  text-align: center;
  border-radius: 4px;
  cursor: pointer;
  transition: all 0.3s ease;
  border: 1px solid var(--border-color);
  background: var(--card-bg);
  color: var(--text-primary);
}

.pagination-page:hover {
  border-color: var(--accent-color);
  color: var(--accent-color);
}

.pagination-page.active {
  background: var(--accent-color);
  border-color: var(--accent-color);
  color: white;
}

.pagination-size {
  display: flex;
  align-items: center;
  gap: 5px;
  font-size: 14px;
  color: var(--text-secondary);
}

.pagination-jump {
  display: flex;
  align-items: center;
  gap: 5px;
  font-size: 14px;
  color: var(--text-secondary);
}

/* 响应式 */
@media (max-width: 768px) {
  #main_Content {
    flex-direction: column;
  }

  .sidebar {
    width: 100%;
    border-right: none;
    border-bottom: 1px solid var(--border-color);
  }

  .sidebar-nav {
    display: flex;
    overflow-x: auto;
    gap: 8px;
  }

  .sidebar-nav li {
    white-space: nowrap;
  }

  .stats-grid {
    grid-template-columns: 1fr;
  }

  .toolbar {
    flex-direction: column;
    align-items: stretch;
  }

  .toolbar-actions {
    width: 100%;
    justify-content: space-between;
  }

  .filter-section,
  .action-section {
    width: 100%;
    justify-content: space-between;
  }

  .data-table {
    font-size: 12px;
  }

  .data-table th,
  .data-table td {
    padding: 8px;
  }

  .action-buttons {
    flex-direction: column;
    align-items: center;
  }

  .pagination {
    flex-direction: column;
    align-items: center;
  }

  .pagination-controls {
    order: 1;
  }

  .pagination-size {
    order: 2;
  }

  .pagination-jump {
    order: 3;
  }

  .pagination-info {
    order: 4;
  }
}

/* Bootstrap风格对话框样式 */
/* 遮罩层 */
.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background-color: rgba(0, 0, 0, 0.5);
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 1050;
  animation: fadeIn 0.2s ease;
}

@keyframes fadeIn {
  from {
    opacity: 0;
  }

  to {
    opacity: 1;
  }
}

/* 弹框容器 */
.modal-container {
  background-color: var(--card-bg);
  border-radius: 8px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.15);
  width: 90%;
  max-width: 500px;
  max-height: 90vh;
  display: flex;
  flex-direction: column;
  animation: slideIn 0.3s ease;
}

@keyframes slideIn {
  from {
    transform: translateY(-30px);
    opacity: 0;
  }

  to {
    transform: translateY(0);
    opacity: 1;
  }
}

/* 标题栏 */
.modal-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 16px 20px;
  border-bottom: 1px solid var(--border-color);
}

.modal-title {
  font-size: 18px;
  font-weight: 600;
  color: var(--text-primary);
  margin: 0;
}

.modal-close {
  background: none;
  border: none;
  font-size: 24px;
  color: #999;
  cursor: pointer;
  padding: 0;
  width: 30px;
  height: 30px;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 50%;
  transition: all 0.3s ease;
  line-height: 1;
}

.modal-close:hover {
  background-color: rgba(0, 0, 0, 0.05);
  color: #333;
}

/* 内容区 */
.modal-body {
  padding: 20px;
  overflow-y: auto;
  flex: 1;
}

/* 底部 */
.modal-footer {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
  padding: 16px 20px;
  border-top: 1px solid var(--border-color);
  background-color: rgba(0, 0, 0, 0.02);
}

/* 表单容器 */
.form-container {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.form-item {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.form-label {
  font-size: 14px;
  font-weight: 500;
  color: var(--text-primary);
}

.form-input,
.form-select {
  width: 100%;
  padding: 10px 12px;
  border: 1px solid var(--border-color);
  border-radius: 4px;
  font-size: 14px;
  color: var(--text-primary);
  background-color: var(--card-bg-solid);
  transition: border-color 0.3s ease, box-shadow 0.3s ease;
}

.form-input:focus,
.form-select:focus {
  outline: none;
  border-color: var(--accent-color);
  box-shadow: 0 0 0 3px rgba(102, 126, 234, 0.1);
}

/* 详情容器 */
.detail-container {
  display: flex;
  flex-direction: column;
  gap: 0;
}

.detail-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 12px 0;
  border-bottom: 1px solid var(--border-color);
}

.detail-item:last-child {
  border-bottom: none;
}

.detail-label {
  font-size: 14px;
  font-weight: 500;
  color: var(--text-secondary);
}

.detail-value {
  font-size: 14px;
  color: var(--text-primary);
}

/* 响应式 */
@media (max-width: 576px) {
  .modal-container {
    width: 95%;
    max-width: none;
  }

  .modal-header,
  .modal-body,
  .modal-footer {
    padding: 12px 16px;
  }
}

/* 确认对话框样式 */
.confirm-modal {
  max-width: 400px;
}

.confirm-header {
  background: linear-gradient(135deg, #e74c3c 0%, #c0392b 100%);
  border-radius: 8px 8px 0 0;
}

.confirm-header .modal-title {
  color: white;
  font-size: 18px;
}

.confirm-icon {
  margin-right: 10px;
  display: flex;
  align-items: center;
}

.confirm-icon svg {
  width: 32px;
  height: 32px;
  color: #fff;
}

.confirm-message {
  font-size: 14px;
  color: var(--text-primary);
  line-height: 1.6;
  margin: 0;
  text-align: center;
}

.btn-danger {
  background: linear-gradient(135deg, #e74c3c 0%, #c0392b 100%);
  color: white;
  border: none;
  padding: 10px 20px;
  border-radius: 4px;
  cursor: pointer;
  transition: all 0.3s ease;
}

.btn-danger:hover {
  transform: translateY(-1px);
  box-shadow: 0 4px 12px rgba(231, 76, 60, 0.4);
}

.dialog-footer {
  display: flex;
  gap: 10px;
  justify-content: flex-end;
  padding: 15px 20px 15px;
  border-top: 1px solid var(--border-color);
}
</style>
