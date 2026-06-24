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
          <el-sub-menu v-if="route.children && route.children.length > 1" :index="route.path">
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
          <el-menu-item v-else :index="route.path + (route.children?.[0]?.path ? '/' + route.children[0].path : '')">
            <el-icon><component :is="route.meta?.icon || route.children?.[0]?.meta?.icon" /></el-icon>
            <template #title>{{ route.meta?.title || route.children?.[0]?.meta?.title }}</template>
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
              <el-avatar :size="32" :src="userStore.userInfo?.avatar" v-if="userStore.userInfo?.avatar">
                <el-icon :size="18"><User /></el-icon>
              </el-avatar>
              <el-avatar :size="32" v-else>
                <el-icon :size="18"><User /></el-icon>
              </el-avatar>
              <span class="username">{{ userStore.userInfo?.nickname || userStore.userInfo?.username || '用户' }}</span>
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
        <router-view v-slot="{ Component }">
          <keep-alive :include="cachedViews" :max="10">
            <component :is="Component" :key="route.path" />
          </keep-alive>
        </router-view>
      </el-main>
    </el-container>
  </el-container>
</template>

<script setup lang="ts">
import { computed, ref, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { useAppStore } from '@/stores/app'
import { User } from '@element-plus/icons-vue'
import Breadcrumb from '@/components/Breadcrumb.vue'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()
const appStore = useAppStore()

// keep-alive 缓存的组件名称列表
const cachedViews = ref<string[]>([])

// 需要缓存的页面名称
const CACHEABLE_NAMES = [
  'Dashboard', 'RealTime', 'History', 'Consumption', 'Accounting',
  'Summary', 'Energy', 'Cost', 'Saving', 'TimeCompare', 'DeviceCompare',
  'StatusMonitor', 'Electricity', 'Budget', 'EnergyFlow', 'EfficiencyIndex',
  'MeterReading', 'AlarmRecord', 'AlarmConfig', 'Footprint', 'Factor',
  'Asset', 'CBAM', 'SupplyChain',
]

// 监听路由变化，维护缓存列表
watch(
  () => route.name,
  (newName) => {
    if (newName && typeof newName === 'string' && CACHEABLE_NAMES.includes(newName)) {
      if (!cachedViews.value.includes(newName)) {
        cachedViews.value.push(newName)
      }
    }
  },
  { immediate: true }
)

/**
 * 菜单路由
 * 从 router.getRoutes() 获取所有已注册路由，过滤出使用 DefaultLayout 的路由
 * 这些路由的 children 即为菜单项
 */
const menuRoutes = computed(() => {
  const allRoutes = router.getRoutes()
  // 找到所有使用 DefaultLayout 作为 component 的路由（即有 children 的菜单路由）
  const filtered = allRoutes.filter(r => {
    // 排除动态路由匹配和隐藏路由
    if (r.meta?.hidden) return false
    // 排除根路由 '/'（首页在 constantRoutes 中单独处理）
    if (r.path === '/') return false
    // 只取顶层菜单路由（path 以 / 开头且不在 children 中的）
    return r.children && r.children.length > 0 && !r.path.includes(':') && r.path.startsWith('/')
  })
  // 去重：根据 path 去重
  const seen = new Set<string>()
  return filtered.filter(r => {
    if (seen.has(r.path)) return false
    seen.add(r.path)
    return true
  })
})

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
  overflow-y: auto;
  overflow-x: hidden;

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
    gap: 8px;

    .username {
      font-size: 14px;
      color: #333;
    }
  }
}

.main-content {
  background: #f0f2f5;
  padding: 20px;
}
</style>
