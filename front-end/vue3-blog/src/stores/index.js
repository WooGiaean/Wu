// Pinia 总入口文件
// 统一导出所有子模块的store，方便页面调用

// 用户信息
import { useUserInfoStore } from './modules/userInfo.js'

// 文章管理
import { useArticlesStore } from './modules/articles.js'

// 笔记管理
import { useNotesStore } from './modules/notes.js'

// 评论管理
import { useCommentsStore } from './modules/comments.js'

// 分类管理
import { useCategoriesStore } from './modules/categories.js'

// 导出所有store供批量使用
export const allStores = {
  useUserInfoStore,
  useArticlesStore,
  useNotesStore,
  useCommentsStore,
  useCategoriesStore
}

//export default stores
