<template>
  <el-container class="layout-container">
    <el-aside :width="appStore.sidebarCollapsed ? '64px' : '220px'" class="sidebar">
      <div class="logo">
        <img src="/favicon.svg" alt="logo" v-if="!appStore.sidebarCollapsed">
        <span v-if="!appStore.sidebarCollapsed">能碳管理平台</span>
        <img src="/favicon.svg" alt="logo" v-else style="width: 32px;">
      </div>
      <el-menu
        :default-active="activeMenu"
        :collapse="appStore.sidebarCollapsed"
        :collapse-transition="false"
        :router="true"
        background-color="#1a1a2e"
        text-color="#b8c5d6"
        active-text-color="#0d9488"
      >
        <template v-for="route in menuRoutes" :key="route.path">
          <el-sub-menu v-if="route.children && route.children.length > 1" :index="route.path">
            <template #title>
              <DynamicIcon :name="route.meta?.icon" />
              <span>{{ route.meta?.title }}</span>
            </template>
            <el-menu-item
              v-for="child in route.children"
              :key="child.path"
              :index="child.path"
            >
              <DynamicIcon v-if="child.meta?.icon" :name="child.meta.icon" />
              <span>{{ child.meta?.title }}</span>
            </el-menu-item>
          </el-sub-menu>
          <el-menu-item v-else :index="route.path">
            <DynamicIcon :name="route.meta?.icon || route.children?.[0]?.meta?.icon" />
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
          <el-dropdown @command="handleNoticeCommand" trigger="click" class="notice-dropdown">
            <el-badge :value="unreadCount" :hidden="unreadCount === 0" class="notice-badge">
              <el-icon :size="20" class="notice-icon"><Bell /></el-icon>
            </el-badge>
            <template #dropdown>
              <el-dropdown-menu>
                <div class="notice-header">我的消息</div>
                <el-dropdown-item v-for="msg in recentMessages" :key="msg.id" :command="'view:' + msg.id">
                  <div class="notice-item">
                    <div class="notice-title">{{ msg.title }}</div>
                    <div class="notice-time">{{ msg.createTime }}</div>
                  </div>
                </el-dropdown-item>
                <el-dropdown-item v-if="recentMessages.length === 0" disabled>
                  <div class="notice-item">暂无未读消息</div>
                </el-dropdown-item>
                <el-dropdown-item divided command="more">
                  <div class="notice-more">查看更多</div>
                </el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
          <NoticeDetailDialog
            v-model="noticeDialogVisible"
            :itemId="noticeDialogId"
            mode="message"
            :messageList="recentMessages"
            @read="handleMessageRead"
          />
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
import { computed, ref, watch, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { useAppStore } from '@/stores/app'
import { User, Bell } from '@element-plus/icons-vue'
import Breadcrumb from '@/components/Breadcrumb.vue'
import DynamicIcon from '@/components/DynamicIcon.vue'
import { getUnreadMessageCount, getMessageList, markMessageRead } from '@/api/message'
import NoticeDetailDialog from '@/components/NoticeDetailDialog.vue'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()
const appStore = useAppStore()

// 我的消息
const unreadCount = ref(0)
const recentMessages = ref<any[]>([])
const noticeDialogVisible = ref(false)
const noticeDialogId = ref<number | undefined>(undefined)

async function loadMessages() {
  try {
    const countRes = await getUnreadMessageCount()
    unreadCount.value = countRes.data || 0
    if (unreadCount.value > 0) {
      const listRes = await getMessageList({ current: 1, size: 5, readStatus: 0 })
      recentMessages.value = listRes.data?.records || []
    } else {
      recentMessages.value = []
    }
  } catch (e) { /* 静默 */ }
}

function handleNoticeCommand(command: string | number) {
  if (command === 'more') {
    router.push('/alarm/my-message')
  } else if (typeof command === 'string' && command.startsWith('view:')) {
    const id = parseInt(command.replace('view:', ''))
    noticeDialogId.value = id
    noticeDialogVisible.value = true
  }
}

function handleMessageRead(messageId: number) {
  const idx = recentMessages.value.findIndex((m: any) => m.id === messageId)
  if (idx >= 0) {
    recentMessages.value.splice(idx, 1)
  }
  unreadCount.value = Math.max(0, unreadCount.value - 1)
}

onMounted(() => {
  loadMessages()
})

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
 * 从 userStore.userInfo.menus（后端API返回的SysMenu数组）中构建侧边栏菜单树
 * 不再从 router.getRoutes() 获取，路由定义和菜单显示独立管理
 */
const menuRoutes = computed(() => {
  const userMenus = userStore.userInfo?.menus || []
  if (!userMenus || userMenus.length === 0) {
    return []
  }

  // 如果是字符串 "/" 表示超级管理员（兼容旧版），返回空
  if (userMenus.length === 1 && typeof userMenus[0] === 'string' && userMenus[0] === '/') {
    return []
  }

  // 从后端菜单数据构建菜单树
  const menuTree = buildSidebarMenus(userMenus)
  return menuTree
})

/**
 * 从后端 SysMenu 列表构建侧边栏菜单树
 */
function buildSidebarMenus(menus: any[]): any[] {
  // 1. 找到所有顶级菜单（parent_id = 0）
  const topLevelMenus = menus.filter((m: any) => m.parentId === 0)

  // 2. Dashboard 特殊处理：放在最前面
  const dashboard = menus.find((m: any) => m.path === '/dashboard' || m.path === '/index')
  const result: any[] = []

  if (dashboard) {
    result.push({
      path: '/dashboard',
      meta: { title: dashboard.menuName || '首页', icon: dashboard.icon || 'Odometer' },
      children: [{ path: '/dashboard', meta: { title: dashboard.menuName || '首页' } }]
    })
  }

  // 3. 构建目录级菜单（menuType=1 为目录，单独子菜单也显示）
  topLevelMenus.filter((m: any) => m.path !== '/dashboard' && m.path !== '/index').forEach((dir: any) => {
    const children = menus
      .filter((m: any) => m.parentId === dir.id && m.status === 1)
      .map((child: any) => ({
        path: child.path || '',
        meta: { title: child.menuName || '', icon: child.icon || '' }
      }))

    if (children.length > 0) {
      result.push({
        path: dir.path || '',
        meta: { title: dir.menuName || '', icon: dir.icon || '' },
        children
      })
    }
  })

  return result
}

function handleCommand(command: string) {
  if (command === 'logout') {
    userStore.logout()
    router.push('/login')
  } else if (command === 'profile') {
    router.push('/system/profile')
  } else if (command === 'settings') {
    router.push('/system/settings')
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

    .el-sub-menu {
      .el-menu-item {
        padding-left: 50px !important;
      }
    }
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

  .notice-dropdown {
    margin-right: 20px;
    cursor: pointer;

    .notice-icon {
      color: #606266;

      &:hover {
        color: #0d9488;
      }
    }
  }

  .notice-header {
    padding: 8px 16px;
    font-weight: bold;
    font-size: 14px;
    color: #303133;
    border-bottom: 1px solid #ebeef5;
  }

  .notice-item {
    max-width: 240px;

    .notice-title {
      font-size: 13px;
      color: #606266;
      overflow: hidden;
      text-overflow: ellipsis;
      white-space: nowrap;
    }

    .notice-time {
      font-size: 12px;
      color: #909399;
      margin-top: 2px;
    }
  }

  .notice-more {
    text-align: center;
    color: #0d9488;
    font-size: 13px;
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
  padding: 10px;
}
</style>
