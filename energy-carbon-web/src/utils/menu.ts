import type { Menu, AppRoute } from '@/types'

/**
 * 后端菜单树转换为前端路由
 * @param menus 后端菜单列表
 * @returns 前端路由配置
 */
export function menusToRoutes(menus: Menu[]): AppRoute[] {
  const routes: AppRoute[] = []

  for (const menu of menus) {
    if (menu.type === 'button') continue
    if (menu.status !== 1) continue

    const route: AppRoute = {
      path: menu.path || '',
      name: menu.name,
      meta: {
        title: menu.title,
        icon: menu.icon,
        hidden: menu.hidden,
        keepAlive: menu.keepAlive,
        permission: menu.permission
      }
    }

    if (menu.component) {
      route.component = menu.component
    }

    if (menu.external && menu.externalLink) {
      route.path = menu.externalLink
      route.meta = { ...route.meta, external: true }
    }

    if (menu.children && menu.children.length > 0) {
      const childRoutes = menusToRoutes(menu.children)
      if (childRoutes.length > 0) {
        route.children = childRoutes
        if (!route.component && !route.redirect) {
          route.redirect = childRoutes[0].path
        }
      }
    }

    routes.push(route)
  }

  return routes.sort((a, b) => {
    const sortA = (a.meta?.sort as number) || 0
    const sortB = (b.meta?.sort as number) || 0
    return sortA - sortB
  })
}

/**
 * 后端菜单树转换为 Element Plus 菜单结构
 * @param menus 后端菜单列表
 * @returns Element Plus 菜单结构
 */
export function menusToElementMenus(menus: Menu[]) {
  const result: any[] = []

  for (const menu of menus) {
    if (menu.type === 'button') continue
    if (menu.status !== 1) continue

    const item: any = {
      index: menu.path || menu.id.toString(),
      title: menu.title,
      icon: menu.icon,
      hidden: menu.hidden,
      children: undefined
    }

    if (menu.children && menu.children.length > 0) {
      const children = menusToElementMenus(menu.children)
      if (children.length > 0) {
        item.children = children
      }
    }

    result.push(item)
  }

  return result
}

/**
 * 扁平化菜单列表（提取所有菜单项为一维数组）
 * @param menus 菜单树
 * @returns 扁平化菜单数组
 */
export function flattenMenus(menus: Menu[]): Menu[] {
  const result: Menu[] = []

  for (const menu of menus) {
    result.push(menu)
    if (menu.children && menu.children.length > 0) {
      result.push(...flattenMenus(menu.children))
    }
  }

  return result
}

/**
 * 根据菜单列表提取所有权限标识
 * @param menus 菜单树
 * @returns 权限标识数组
 */
export function extractPermissions(menus: Menu[]): string[] {
  const permissions: string[] = []
  const flatMenus = flattenMenus(menus)

  for (const menu of flatMenus) {
    if (menu.permission) {
      permissions.push(menu.permission)
    }
  }

  return [...new Set(permissions)]
}

/**
 * 查找菜单路径对应的菜单信息
 * @param menus 菜单树
 * @param path 路径
 * @returns 菜单信息或 undefined
 */
export function findMenuByPath(menus: Menu[], path: string): Menu | undefined {
  for (const menu of menus) {
    if (menu.path === path) {
      return menu
    }
    if (menu.children && menu.children.length > 0) {
      const found = findMenuByPath(menu.children, path)
      if (found) return found
    }
  }
  return undefined
}

/**
 * 生成动态组件导入函数
 * @param componentPath 组件路径
 * @returns 组件导入函数
 */
export function loadComponent(componentPath: string) {
  const modules = import.meta.glob('@/views/**/*.vue')
  const path = `/src/views/${componentPath}.vue`
  return modules[path] || (() => Promise.resolve({ default: { render: () => null } }))
}

/**
 * 将后端路由组件路径转换为前端可加载的路由配置
 * @param routes AppRoute 数组
 * @returns Vue Router 可用的路由配置
 */
export function generateAsyncRoutes(routes: AppRoute[]) {
  const asyncRoutes: any[] = []

  for (const route of routes) {
    const item: any = {
      path: route.path,
      name: route.name,
      meta: route.meta,
      redirect: route.redirect
    }

    if (route.component) {
      item.component = loadComponent(route.component)
    }

    if (route.children && route.children.length > 0) {
      item.children = generateAsyncRoutes(route.children)
    }

    asyncRoutes.push(item)
  }

  return asyncRoutes
}
