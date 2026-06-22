import { createRouter, createWebHistory } from 'vue-router'
import type { RouteRecordRaw } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { generateAsyncRoutes, loadComponent } from '@/utils/menu'
import { local, StorageKeys } from '@/utils/storage'
import type { AppRoute } from '@/types'
import { performanceMonitor } from '@/utils/performance'

// 静态路由（无需权限）
const staticRoutes: RouteRecordRaw[] = [
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/views/auth/Login.vue'),
    meta: { public: true, title: '登录' }
  },
  {
    path: '/404',
    name: 'NotFound',
    component: () => import('@/views/auth/Login.vue'),
    meta: { public: true, title: '页面不存在' }
  },
  // 工具包静态路由
  {
    path: '/toolkit/electricity-price',
    name: 'ElectricityPrice',
    component: () => import('@/views/toolkit/ElectricityPrice.vue'),
    meta: { title: '分时电价管理' }
  },
  {
    path: '/toolkit/knowledge',
    name: 'Knowledge',
    component: () => import('@/views/toolkit/Knowledge.vue'),
    meta: { title: 'AI知识库' }
  },
  {
    path: '/toolkit/ai-report',
    name: 'AiReport',
    component: () => import('@/views/toolkit/AiReport.vue'),
    meta: { title: 'AI报告生成' }
  }
]

// 基础布局路由（作为动态路由的父级）
const layoutRoute: RouteRecordRaw = {
  path: '/',
  component: () => import('@/layouts/DefaultLayout.vue'),
  redirect: '/dashboard',
  children: []
}

const router = createRouter({
  history: createWebHistory(),
  routes: [...staticRoutes, layoutRoute],
  scrollBehavior() {
    return { top: 0 }
  }
})

// 白名单路径
const whiteList = ['/login', '/404']

// 是否已添加动态路由
let hasAddedRoutes = false

/**
 * 加载动态路由
 * @param menus 后端返回的菜单列表
 */
function addDynamicRoutes(menus: AppRoute[]) {
  const asyncRoutes = generateAsyncRoutes(menus)

  for (const route of asyncRoutes) {
    const routeRecord: RouteRecordRaw = {
      path: route.path,
      name: route.name,
      meta: route.meta,
      redirect: route.redirect,
      component: route.component ? loadComponent(route.component) : undefined,
      children: route.children
        ? route.children.map(child => ({
            path: child.path,
            name: child.name,
            meta: child.meta,
            redirect: child.redirect,
            component: child.component ? loadComponent(child.component) : undefined,
            children: child.children
              ? child.children.map(grandChild => ({
                  path: grandChild.path,
                  name: grandChild.name,
                  meta: grandChild.meta,
                  component: grandChild.component ? loadComponent(grandChild.component) : undefined
                }))
              : undefined
          }))
        : undefined
    }

    router.addRoute('Layout', routeRecord)
  }
}

/**
 * 重置动态路由
 */
function resetDynamicRoutes() {
  const routes = router.getRoutes()
  for (const route of routes) {
    if (route.name && !['Login', 'NotFound', 'Layout'].includes(route.name as string)) {
      router.removeRoute(route.name)
    }
  }
  hasAddedRoutes = false
}

// 路由守卫
router.beforeEach(async (to, from, next) => {
  // 开始页面加载计时
  performanceMonitor.startPageLoad(to.path)

  const userStore = useUserStore()
  const token = userStore.token || local.get<string>(StorageKeys.TOKEN)

  // 设置页面标题
  document.title = to.meta.title ? `${to.meta.title} - 能碳数智化平台` : '能碳数智化平台'

  // 白名单直接放行
  if (whiteList.includes(to.path)) {
    return next()
  }

  // 未登录
  if (!token) {
    if (to.meta.public) {
      return next()
    }
    return next(`/login?redirect=${encodeURIComponent(to.fullPath)}`)
  }

  // 已登录但访问登录页，重定向到首页
  if (to.path === '/login') {
    return next('/')
  }

  // 已登录，获取用户信息并加载动态路由
  try {
    if (!userStore.userInfo) {
      await userStore.fetchUserInfo()
    }

    // 动态添加路由
    if (!hasAddedRoutes && userStore.menus && userStore.menus.length > 0) {
      const appRoutes = generateAsyncRoutes(userStore.menus as AppRoute[])
      addDynamicRoutes(appRoutes)
      hasAddedRoutes = true
      // 重新导航以确保新路由生效
      return next({ ...to, replace: true })
    }

    // 检查权限
    if (to.meta.permission) {
      const hasPerm = userStore.hasPermission(to.meta.permission as string)
      if (!hasPerm) {
        return next('/404')
      }
    }

    next()
  } catch (error) {
    // 获取用户信息失败，清除登录状态
    userStore.logout()
    resetDynamicRoutes()
    next(`/login?redirect=${encodeURIComponent(to.fullPath)}`)
  }
})

// 全局后置守卫
router.afterEach((to, from) => {
  // 结束页面加载计时
  const duration = performanceMonitor.endPageLoad(to.path)
  if (duration && duration > 1000) {
    console.debug(`[路由] ${to.path} 加载耗时: ${duration.toFixed(0)}ms`)
  }
})

// 全局错误处理
router.onError(error => {
  console.error('路由错误:', error)
})

export function resetRouter() {
  resetDynamicRoutes()
}

export default router
