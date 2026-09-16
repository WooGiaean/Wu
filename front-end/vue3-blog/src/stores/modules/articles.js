import { ref } from 'vue'
import {defineStore} from 'pinia'

export const useArticlesStore = defineStore('articlesStore', ()=>{
  //定义状态：博文列表
  const articles = ref([])
  //定义状态：当前文章详情
  const currentArticle = ref(null)
  //定义状态：分页信息
  const pagination = ref({
    page: 1,
    pageSize: 10,
    total: 0
  })
  //定义状态：最新文章列表
  const latestArticles = ref([])

  //设置文章列表
  const setArticles = (articlesData) => {
    articles.value = articlesData
  }

  //获取文章列表
  const getArticles = () => {
    return articles.value
  }


  // ========== 最新文章操作 ==========

  // 设置最新文章
  const setLatestArticles = (articlesData) => {
    latestArticles.value = articlesData
  }

  // 获取最新文章
  const getLatestArticles = () => {
    return latestArticles.value
  }


  //设置当前文章详情
  const setCurrentArticle = (articleData) => {
    currentArticle.value = articleData
  }

  //获取当前文章详情
  const getCurrentArticle = () => {
    return currentArticle.value
  }

  //根据ID获取文章
  const getArticleById = (articleId) => {
    return articles.value.find(article => article.articleId === articleId) || null
  }

  //添加文章
  const addArticle = (articleData) => {
    articles.value.unshift(articleData)
    pagination.value.total++
  }

  //更新文章
  const updateArticle = (articleData) => {
    const index = articles.value.findIndex(article => article.articleId === articleData.articleId)
    if (index !== -1) {
      articles.value[index] = { ...articles.value[index], ...articleData }
    }
    if (currentArticle.value && currentArticle.value.articleId === articleData.articleId) {
      currentArticle.value = { ...currentArticle.value, ...articleData }
    }
  }

  //删除文章
  const deleteArticle = (articleId) => {
    articles.value = articles.value.filter(article => article.articleId !== articleId)
    pagination.value.total--
    if (currentArticle.value && currentArticle.value.articleId === articleId) {
      currentArticle.value = null
    }
  }

  //设置分页信息
  const setPagination = (page, pageSize, total) => {
    pagination.value = { page, pageSize, total }
  }

  //获取分页信息
  const getPagination = () => {
    return pagination.value
  }

  //清空文章数据
  const clearArticles = () => {
    articles.value = []
    latestArticles.value = []
    currentArticle.value = null
    pagination.value = { page: 1, pageSize: 10, total: 0 }
  }

  //获取文章数量
  const getArticleCount = () => {
    return articles.value.length
  }

  //获取分页总数
  const getTotalPages = () => {
    return Math.ceil(pagination.value.total / pagination.value.pageSize)
  }

  //按分类筛选文章
  const getArticlesByCategory = (categoryId) => {
    if (!categoryId) return articles.value
    return articles.value.filter(article => {
      return article.categoryList && article.categoryList.some(cat => cat.categoryId === categoryId)
    })
  }

  //按标签筛选文章
  const getArticlesByTag = (tagId) => {
    if (!tagId) return articles.value
    return articles.value.filter(article => {
      return article.tagList && article.tagList.some(tag => tag.tagId === tagId)
    })
  }

  //按关键词搜索文章
  const searchArticles = (keyword) => {
    if (!keyword) return articles.value
    const lowerKeyword = keyword.toLowerCase()
    return articles.value.filter(article => {
      return article.articleTitle.toLowerCase().includes(lowerKeyword) ||
             article.articleSummary.toLowerCase().includes(lowerKeyword)
    })
  }

  return {
    articles,
    latestArticles,
    currentArticle,
    pagination,
    setArticles,
    getArticles,
    setCurrentArticle,
    getCurrentArticle,
    getArticleById,
    addArticle,
    updateArticle,
    deleteArticle,
    setPagination,
    getPagination,
    clearArticles,
    getArticleCount,
    getTotalPages,
    getArticlesByCategory,
    getArticlesByTag,
    searchArticles,
    setLatestArticles,
    getLatestArticles
  }
},{
  persist: true
})
