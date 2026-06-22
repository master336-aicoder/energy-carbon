import { createRouter, createWebHistory, RouteRecordRaw } from 'vue-router'
import { useUserStore } from '@/stores/user'

/**
 * 路由配置
 * 定义应用的所有路由规则，包括布局路由、页面路由和嵌套路由
 */
const routes: RouteRecordRaw[] = [
  {
    // 登录页路由
    path: '/login',
    name: 'Login',
    // 使用动态导入实现路由懒加载，减少首屏加载时间
    component: () => import('@/views/login/index.vue'),
    // 登录页不需要认证
    meta: { requiresAuth: false },
  },
  {
    // 主布局路由
    path: '/',
    name: 'Layout',
    component: () => import('@/layouts/MainLayout.vue'),
    // 需要认证
    meta: { requiresAuth: true },
    // 子路由（嵌套路由）
    children: [
      {
        // 首页/仪表盘
        path: '',
        name: 'Dashboard',
        component: () => import('@/views/dashboard/index.vue'),
        meta: { title: '首页', icon: 'HomeFilled' },
      },
      {
        // 实时监测
        path: 'monitor',
        name: 'Monitor',
        component: () => import('@/views/monitor/index.vue'),
        meta: { title: '实时监测', icon: 'Monitor' },
      },
      {
        // 碳排放核算
        path: 'carbon',
        name: 'Carbon',
        component: () => import('@/views/carbon/index.vue'),
        meta: { title: '碳排放核算', icon: 'TrendCharts' },
      },
      {
        // 系统管理
        path: 'system',
        name: 'System',
        component: () => import('@/views/system/index.vue'),
        meta: { title: '系统管理', icon: 'Setting' },
      },
    ],
  },
  {
    // 404 页面（捕获所有未匹配路由）
    path: '/:pathMatch(.*)*',
    name: 'NotFound',
    component: () => import('@/views/error/404.vue'),
  },
]

/**
 * 创建 Vue Router 实例
 * 使用 HTML5 History 模式（需要后端配合配置 fallback）
 */
const router = createRouter({
  // 使用 HTML5 History 模式，URL 更美观（无 # 号）
  // 注意：生产环境需要配置服务器 fallback 到 index.html
  history: createWebHistory(),
  routes,
})

/**
 * 全局前置守卫
 * 在路由跳转前执行，用于权限校验和登录状态检查
 */
router.beforeEach((to, from, next) => {
  // 获取用户 store
  const userStore = useUserStore()
  // 判断目标路由是否需要认证
  if (to.meta.requiresAuth && !userStore.token) {
    // 需要认证但未登录，重定向到登录页
    next('/login')
  } else {
    // 已登录或不需要认证，正常跳转
    next()
  }
})

export default router
