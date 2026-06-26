import { createRouter, createWebHistory } from 'vue-router'
import type { RouteRecordRaw } from 'vue-router'
import NProgress from 'nprogress'
import 'nprogress/nprogress.css'
import { useUserStore } from '@/stores/user'
import DefaultLayout from '@/layouts/DefaultLayout.vue'

NProgress.configure({ showSpinner: false })

/** 获取 Token（直接从 localStorage 读取原始字符串，避免 JSON.parse 问题） */
function getToken(): string | null {
  return localStorage.getItem('token')
}

// 静态路由（无需权限）
export const constantRoutes: RouteRecordRaw[] = [
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/views/auth/Login.vue'),
    meta: { title: '登录', hidden: true },
  },
  {
    path: '/',
    name: 'Layout',
    component: DefaultLayout,
    redirect: '/dashboard',
    children: [
      {
        path: 'dashboard',
        name: 'Dashboard',
        component: () => import('@/views/dashboard/Index.vue'),
        meta: { title: '首页', icon: 'Odometer', affix: true },
      },
    ],
  },
]

// 动态路由（需要权限，开发环境直接全部加载）
// 注意：这些路由通过 addRoute('Layout', route) 添加为 '/' 的子路由
// 因此不需要再指定 component: DefaultLayout，否则会嵌套布局
export const asyncRoutes: RouteRecordRaw[] = [
  {
    path: '/monitor',
    redirect: '/monitor/realtime',
    meta: { title: '实时监测', icon: 'Monitor' },
    children: [
      {
        path: 'realtime',
        name: 'MonitorRealTime',
        component: () => import('@/views/monitor/RealTime.vue'),
        meta: { title: '实时数据' },
      },
      {
        path: 'history',
        name: 'MonitorHistory',
        component: () => import('@/views/monitor/History.vue'),
        meta: { title: '历史查询' },
      },
      {
        path: 'meter-reading',
        name: 'MeterReading',
        component: () => import('@/views/monitor/MeterReading.vue'),
        meta: { title: '抄表录入' },
      },
      {
        path: 'status',
        name: 'StatusMonitor',
        component: () => import('@/views/monitor/StatusMonitor.vue'),
        meta: { title: '运行状态' },
      },
      {
        path: 'time-compare',
        name: 'TimeCompare',
        component: () => import('@/views/monitor/TimeCompare.vue'),
        meta: { title: '同比环比' },
      },
      {
        path: 'device-compare',
        name: 'DeviceCompare',
        component: () => import('@/views/monitor/DeviceCompare.vue'),
        meta: { title: '设备对比' },
      },
    ],
  },
  {
    path: '/alarm',
    redirect: '/alarm/config',
    meta: { title: '告警管理', icon: 'Bell' },
    children: [
      {
        path: 'config',
        name: 'AlarmConfig',
        component: () => import('@/views/alarm/Config.vue'),
        meta: { title: '告警配置' },
      },
      {
        path: 'record',
        name: 'AlarmRecord',
        component: () => import('@/views/alarm/Record.vue'),
        meta: { title: '告警记录' },
      },
    ],
  },
  {
    path: '/analysis',
    redirect: '/analysis/consumption',
    meta: { title: '能耗分析', icon: 'DataAnalysis' },
    children: [
      {
        path: 'consumption',
        name: 'AnalysisConsumption',
        component: () => import('@/views/analysis/Consumption.vue'),
        meta: { title: '能耗统计' },
      },
      {
        path: 'electricity',
        name: 'AnalysisElectricity',
        component: () => import('@/views/analysis/Electricity.vue'),
        meta: { title: '用电分析' },
      },
      {
        path: 'budget',
        name: 'AnalysisBudget',
        component: () => import('@/views/analysis/Budget.vue'),
        meta: { title: '预算管理' },
      },
    ],
  },
  {
    path: '/carbon',
    redirect: '/carbon/accounting',
    meta: { title: '碳管理', icon: 'Smoking' },
    children: [
      {
        path: 'accounting',
        name: 'CarbonAccounting',
        component: () => import('@/views/carbon/Accounting.vue'),
        meta: { title: '碳核算' },
      },
      {
        path: 'factor',
        name: 'CarbonFactor',
        component: () => import('@/views/carbon/Factor.vue'),
        meta: { title: '排放因子' },
      },
      {
        path: 'footprint',
        name: 'CarbonFootprint',
        component: () => import('@/views/carbon/Footprint.vue'),
        meta: { title: '碳足迹' },
      },
      {
        path: 'asset',
        name: 'CarbonAsset',
        component: () => import('@/views/carbon/Asset.vue'),
        meta: { title: '碳资产' },
      },
      {
        path: 'cbam',
        name: 'CarbonCBAM',
        component: () => import('@/views/carbon/CBAM.vue'),
        meta: { title: 'CBAM' },
      },
      {
        path: 'supply-chain',
        name: 'CarbonSupplyChain',
        component: () => import('@/views/carbon/SupplyChain.vue'),
        meta: { title: '供应链碳' },
      },
    ],
  },
  {
    path: '/report',
    redirect: '/report/energy',
    meta: { title: '报表中心', icon: 'Document' },
    children: [
      {
        path: 'energy',
        name: 'ReportEnergy',
        component: () => import('@/views/report/Energy.vue'),
        meta: { title: '能耗报表' },
      },
      {
        path: 'cost',
        name: 'ReportCost',
        component: () => import('@/views/report/Cost.vue'),
        meta: { title: '成本报表' },
      },
      {
        path: 'saving',
        name: 'ReportSaving',
        component: () => import('@/views/report/Saving.vue'),
        meta: { title: '节能量报表' },
      },
      {
        path: 'summary',
        name: 'ReportSummary',
        component: () => import('@/views/report/Summary.vue'),
        meta: { title: '汇总报表' },
      },
    ],
  },
  {
    path: '/efficiency',
    redirect: '/efficiency',
    meta: { title: '能效对标', icon: 'Aim' },
    children: [
      {
        path: '',
        name: 'EfficiencyIndex',
        component: () => import('@/views/efficiency/Index.vue'),
        meta: { title: '能效对标' },
      },
      {
        path: 'energy-flow',
        name: 'EfficiencyEnergyFlow',
        component: () => import('@/views/efficiency/EnergyFlow.vue'),
        meta: { title: '能源流向' },
      },
    ],
  },
  {
    path: '/dual-carbon',
    redirect: '/dual-carbon',
    meta: { title: '双碳管理', icon: 'Promotion' },
    children: [
      {
        path: '',
        name: 'DualCarbonIndex',
        component: () => import('@/views/dualCarbon/Index.vue'),
        meta: { title: 'AI智慧管理平台' },
      },
    ],
  },
  {
    path: '/toolkit',
    redirect: '/toolkit/ai-report',
    meta: { title: '工具箱', icon: 'Suitcase' },
    children: [
      {
        path: 'ai-report',
        name: 'ToolkitAiReport',
        component: () => import('@/views/toolkit/AiReport.vue'),
        meta: { title: 'AI报告生成' },
      },
      {
        path: 'electricity-price',
        name: 'ToolkitElectricityPrice',
        component: () => import('@/views/toolkit/ElectricityPrice.vue'),
        meta: { title: '分时电价' },
      },
      {
        path: 'knowledge',
        name: 'ToolkitKnowledge',
        component: () => import('@/views/toolkit/Knowledge.vue'),
        meta: { title: 'AI知识库' },
      },
    ],
  },
  {
    path: '/system',
    redirect: '/system/device',
    meta: { title: '系统管理', icon: 'Setting' },
    children: [
      {
        path: 'device',
        name: 'SystemDevice',
        component: () => import('@/views/system/Device.vue'),
        meta: { title: '设备管理' },
      },
      {
        path: 'meter',
        name: 'SystemMeter',
        component: () => import('@/views/system/Meter.vue'),
        meta: { title: '计量器具' },
      },
      {
        path: 'unit',
        name: 'SystemUnit',
        component: () => import('@/views/system/Unit.vue'),
        meta: { title: '用能单元' },
      },
      {
        path: 'role',
        name: 'SystemRole',
        component: () => import('@/views/system/Role.vue'),
        meta: { title: '角色管理' },
      },
      {
        path: 'user',
        name: 'SystemUser',
        component: () => import('@/views/system/User.vue'),
        meta: { title: '用户管理' },
      },
      {
        path: 'profile',
        name: 'SystemProfile',
        component: () => import('@/views/system/Profile.vue'),
        meta: { title: '个人中心', hidden: true },
      },
      {
        path: 'settings',
        name: 'SystemSettings',
        component: () => import('@/views/system/Settings.vue'),
        meta: { title: '系统设置', hidden: true },
      },
    ],
  },
]

const router = createRouter({
  history: createWebHistory('/admin/'),
  routes: constantRoutes,
})

// 白名单路由（无需登录即可访问）
const whiteList = ['/login']

// 标记动态路由是否已加载
let hasAddedRoutes = false

router.beforeEach(async (to, _from, next) => {
  NProgress.start()
  document.title = `${to.meta.title || ''} - 能碳数智化平台`

  const token = getToken()

  if (token) {
    if (to.path === '/login') {
      // 已登录状态下访问登录页，重定向到首页
      next({ path: '/' })
    } else {
      const userStore = useUserStore()
      if (hasAddedRoutes) {
        // 动态路由已加载，直接放行
        next()
      } else {
        try {
          // 加载所有动态路由（作为根路由 '/' 的 children 挂载）
          asyncRoutes.forEach((route) => router.addRoute('Layout', route))
          // 添加 404 兜底路由（必须在动态路由之后添加）
          router.addRoute({
            path: '/:pathMatch(.*)*',
            name: 'NotFound',
            component: () => import('@/views/error/404.vue'),
            meta: { title: '404', hidden: true },
          })
          hasAddedRoutes = true
          // 使用 replace 确保导航历史正确
          next({ ...to, replace: true })
        } catch (error) {
          // 获取用户信息失败，清除登录状态并跳转到登录页
          userStore.logout()
          next(`/login?redirect=${to.path}`)
        }
      }
    }
  } else {
    // 未登录
    if (whiteList.includes(to.path)) {
      // 在白名单中，直接放行
      next()
    } else {
      // 不在白名单中，重定向到登录页并携带目标路径
      next(`/login?redirect=${to.path}`)
    }
  }
})

router.afterEach(() => {
  NProgress.done()
})

export default router
