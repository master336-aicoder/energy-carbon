<template>
  <el-container class="layout-container">
    <el-aside :width="appStore.sidebarCollapsed ? '64px' : '220px'" class="sidebar">
      <div class="logo">
        <img src="/favicon.svg" alt="logo" v-if="!appStore.sidebarCollapsed">
        <span v-if="!appStore.sidebarCollapsed">能碳管理平台</span>
        <img src="/favicon.svg" alt="logo" v-else style="width: 32px;">
      </div>
      <el-menu
        :default-active="$route.path"
        :collapse="appStore.sidebarCollapsed"
        :collapse-transition="false"
        router
        background-color="#1a1a2e"
        text-color="#b8c5d6"
        active-text-color="#0d9488"
      >
        <template v-for="route in menuRoutes" :key="route.path">
          <el-sub-menu v-if="route.children" :index="route.path">
            <template #title>
              <el-icon><component :is="route.meta?.icon" /></el-icon>
              <span>{{ route.meta?.title }}</span>
            </template>
            <el-menu-item
              v-for="child in route.children"
              :key="child.path"
              :index="route.path + '/' + child.path"
            >
              {{ child.meta?.title }}
            </el-menu-item>
          </el-sub-menu>
          <el-menu-item v-else :index="route.path">
            <el-icon><component :is="route.meta?.icon" /></el-icon>
            <template #title>{{ route.meta?.title }}</template>
          </el-menu-item>
        </template>
      </el-menu>
    </el-aside>
    <el-container>
      <el-header class="header">
        <div class="header-left">
          <el-icon class="collapse-btn" @click="appStore.toggleSidebar">
            <Fold v-if="!appStore.sidebarCollapsed" />
            <Expand v-else />
          </el-icon>
          <breadcrumb />
        </div>
        <div class="header-right">
          <el-dropdown @command="handleCommand">
            <span class="user-info">
              {{ userStore.userInfo?.nickname || userStore.userInfo?.username }}
              <el-icon><ArrowDown /></el-icon>
            </span>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item command="profile">个人中心</el-dropdown-item>
                <el-dropdown-item command="settings">系统设置</el-dropdown-item>
                <el-dropdown-item divided command="logout">退出登录</el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </div>
      </el-header>
      <el-main class="main-content">
        <router-view />
      </el-main>
    </el-container>
  </el-container>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { useAppStore } from '@/stores/app'
import type { Menu } from '@/types'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()
const appStore = useAppStore()

const menuRoutes = computed(() => {
  // 优先使用后端返回的菜单数据
  if (userStore.menus && userStore.menus.length > 0) {
    return buildMenuTree(userStore.menus)
  }
  // 降级使用路由配置
  const layoutRoute = router.getRoutes().find(r => r.path === '/')
  return layoutRoute?.children || []
})

interface MenuItem {
  path: string
  name?: string
  meta?: {
    title: string
    icon?: string
    hidden?: boolean
    keepAlive?: boolean
    permission?: string
    sort?: number
  }
  children?: MenuItem[]
}

function buildMenuTree(menus: Menu[]): MenuItem[] {
  const menuMap = new Map<number, MenuItem>()
  const roots: MenuItem[] = []

  // 先创建所有菜单项
  for (const menu of menus) {
    if (menu.type === 'button' || menu.status !== 1) continue
    menuMap.set(menu.id, {
      path: menu.path || '',
      name: menu.name,
      meta: {
        title: menu.title,
        icon: menu.icon,
        hidden: menu.hidden,
        keepAlive: menu.keepAlive,
        permission: menu.permission,
        sort: menu.sort
      }
    })
  }

  // 构建树形结构
  for (const menu of menus) {
    if (menu.type === 'button' || menu.status !== 1) continue
    const item = menuMap.get(menu.id)
    if (!item) continue

    if (menu.parentId === 0) {
      roots.push(item)
    } else {
      const parent = menuMap.get(menu.parentId)
      if (parent) {
        if (!parent.children) parent.children = []
        parent.children.push(item)
      }
    }
  }

  // 排序
  function sortMenus(items: MenuItem[]) {
    items.sort((a, b) => (a.meta?.sort || 0) - (b.meta?.sort || 0))
    for (const item of items) {
      if (item.children) sortMenus(item.children)
    }
  }
  sortMenus(roots)

  return roots
}

function handleCommand(command: string) {
  if (command === 'logout') {
    userStore.logout()
    router.push('/login')
  }
}
</script>

<style scoped lang="scss">
.layout-container {
  height: 100vh;
}

.sidebar {
  background: #1a1a2e;
  transition: width 0.3s;

  .logo {
    height: 60px;
    display: flex;
    align-items: center;
    justify-content: center;
    color: #fff;
    font-size: 18px;
    font-weight: bold;
    border-bottom: 1px solid rgba(255,255,255,0.1);

    img {
      width: 32px;
      margin-right: 10px;
    }
  }

  .el-menu {
    border-right: none;
  }
}

.header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  background: #fff;
  box-shadow: 0 1px 4px rgba(0,0,0,0.08);

  .header-left {
    display: flex;
    align-items: center;

    .collapse-btn {
      font-size: 20px;
      cursor: pointer;
      margin-right: 15px;

      &:hover {
        color: #0d9488;
      }
    }
  }

  .user-info {
    cursor: pointer;
    display: flex;
    align-items: center;
    gap: 5px;
  }
}

.main-content {
  background: #f0f2f5;
  padding: 20px;
}
</style>
