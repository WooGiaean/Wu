import { createRouter, createWebHistory } from 'vue-router'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/',
      redirect: '/home'
    },
    {
      path: '/home',
      name: 'home',
      component: () => import('../views/Home/HomeView.vue')
    },
    {
      path: '/profile',
      name: 'profile',
      component: () => import('../views/Home/ProfileView.vue')
    },
    {
      path: '/articles',
      name: 'articles',
      component: () => import('../views/Article/ArticlesView.vue')
    },
    {
      path: '/articles/:id',
      name: 'article',
      component: () => import('../views/Article/ArticleView.vue')
    },
    {
      path: '/notes',
      name: 'notes',
      component: () => import('../views/Note/NotesView.vue')
    },
    {
      path: '/notes/:id',
      name: 'note',
      component: () => import('../views/Note/NoteView.vue')
    },
    {
      path: '/login',
      name: 'login',
      component: () => import('../views/Login/LoginView.vue')
    },
    {
      path: '/register',
      name: 'register',
      component: () => import('../views/Login/RegisterView.vue')
    },
    {
      path: '/admin',
      name: 'admin',
      component: () => import('../views/Admin/AdminView.vue')
    }
  ]
})

router.beforeEach((to, from, next) => {
  // 判断用户是否登录
  const sessionUser= sessionStorage.getItem('user');

  //定义需要登录的页面
  const publicURL=['/loin','register']

  //进行页面跳转的判断
  if(!publicURL.includes(to.path)&&!sessionUser){
    //如果不是公共页面且未登录，跳转到登录页
    next('/login');
  }else{
    next();
  }

})


export default router
