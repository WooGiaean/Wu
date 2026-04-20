import { createApp } from 'vue'
import { createPinia } from 'pinia'
import piniaPersist from 'pinia-plugin-persistedstate'
import App from './App.vue'
import router from '@/router/index.js'

const app = createApp(App)
const pinia = createPinia()

// 注册持久化插件
pinia.use(piniaPersist)

app.use(pinia)
app.use(router)

// 在 Pinia 应用之后再使用 store
import { useUserInfoStore } from "@/stores/modules/userInfo.js";
const userInfoStore = useUserInfoStore()
userInfoStore.initUserAndToken()

app.mount('#app')
