import { ref, computed } from 'vue'
import {defineStore} from 'pinia'

export const useArticlesStore = defineStore('articlesStore', ()=>{
  //定义状态：博文列表
  const articles = ref([])

  const setArticles = (articlesData) => {
    articles.value = articlesData
  }

  const getArticles=()=>{
    return articles.value
  }

  return {articles,getArticles}
},{
  persist: true
})
