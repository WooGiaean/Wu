import { createRouter, createWebHistory } from 'vue-router'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/',
      redirect: '/blog'
    },
    {
      path: '/blog',
      name: 'blog',
      component: () => import('@/views/Home/PublicBlogViewNew.vue')
    },
    {
      path: '/home',
      name: 'home',
      component: () => import('@/views/Home/HomeView.vue'),
      meta: { requiresAuth: true }
    },
    {
      path: '/profile',
      name: 'profile',
      component: () => import('@/views/Home/ProfileView.vue'),
      meta: { requiresAuth: true }
    },
    {
      path: '/articles',
      name: 'articles',
      component: () => import('@/views/Article/ArticlesView.vue'),
      meta: { requiresAuth: true }
    },
    {
      path: '/articles/:id',
      name: 'article',
      component: () => import('@/views/Article/ArticleView.vue'),
      meta: { requiresAuth: true }
    },
    {
      path: '/articles/edit/:id',
      name: 'edit-article',
      component: () => import('@/views/Article/EditArticleView.vue'),
      meta: { requiresAuth: true }
    },
    {
      path: '/notes',
      name: 'notes',
      component: () => import('@/views/Note/NotesView.vue'),
      meta: { requiresAuth: true }
    },
    {
      path: '/notes/:id',
      name: 'note',
      component: () => import('@/views/Note/NoteView.vue'),
      meta: { requiresAuth: true }
    },
    {
      path: '/login',
      name: 'login',
      component: () => import('@/views/Login/LoginView.vue')
    },
    {
      path: '/register',
      name: 'register',
      component: () => import('@/views/Login/RegisterView.vue')
    },
    {
      path: '/forgot-password',
      name: 'forgot-password',
      component: () => import('@/views/Login/ForgotPasswordView.vue')
    },
    {
      path: '/admin',
      name: 'admin',
      component: () => import('../views/Admin/AdminView.vue'),
      meta: { requiresAuth: true }
    },
    {
       path: '/temp',
      name: 'temp',
      component: () => import('../views/temp/testPage.vue'),
      //meta: { requiresAuth: true }
    },
    {
      path: '/tempAdmin',
      name: 'adminNew',
      component: () => import('../views/Admin/AdminViewNew.vue'),
    }
  ]
})

/*
* 全局前置守卫会在导航切换开始之前执行（所有导航跳转）
* */
router.beforeEach((to, from) => {
  // 判断用户是否登录
  const localStorageUser = localStorage.getItem('user');
  const localStorageToken = localStorage.getItem('token');

  //进行页面跳转的判断
  if(to.meta.requiresAuth && (!localStorageUser || !localStorageToken)){
    //如果不是公共页面且未登录，跳转到登录页
    return {path:'/login'};
  }

})


export default router
