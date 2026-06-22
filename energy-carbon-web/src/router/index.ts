import { createRouter, createWebHistory } from 'vue-router'
import { useUserStore } from '@/stores/user'

const routes = [
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/views/auth/Login.vue'),
    meta: { public: true }
  },
  {
    path: '/',
    component: () => import('@/layouts/DefaultLayout.vue'),
    redirect: '/dashboard',
    children: [
      {
        path: 'dashboard',
        name: 'Dashboard',
        component: () => import('@/views/dashboard/Index.vue'),
        meta: { title: '首页', icon: 'HomeFilled' }
      },
      {
        path: 'dual-carbon',
        name: 'DualCarbon',
        component: () => import('@/views/dualCarbon/Index.vue'),
        meta: { title: '双碳考核', icon: 'TrendCharts' }
      },
      {
        path: 'monitor',
        meta: { title: '在线监测', icon: 'Monitor' },
        children: [
          { path: 'realtime', name: 'RealTime', component: () => import('@/views/monitor/RealTime.vue'), meta: { title: '实时数据' } },
          { path: 'history', name: 'History', component: () => import('@/views/monitor/History.vue'), meta: { title: '历史数据' } },
          { path: 'device-compare', name: 'DeviceCompare', component: () => import('@/views/monitor/DeviceCompare.vue'), meta: { title: '设备对标' } },
          { path: 'time-compare', name: 'TimeCompare', component: () => import('@/views/monitor/TimeCompare.vue'), meta: { title: '时间对标' } },
          { path: 'meter-reading', name: 'MeterReading', component: () => import('@/views/monitor/MeterReading.vue'), meta: { title: '计量抄表' } },
          { path: 'status', name: 'StatusMonitor', component: () => import('@/views/monitor/StatusMonitor.vue'), meta: { title: '状态监测' } }
        ]
      },
      {
        path: 'alarm',
        meta: { title: '报警管理', icon: 'BellFilled' },
        children: [
          { path: 'config', name: 'AlarmConfig', component: () => import('@/views/alarm/Config.vue'), meta: { title: '报警设置' } },
          { path: 'record', name: 'AlarmRecord', component: () => import('@/views/alarm/Record.vue'), meta: { title: '报警查询' } }
        ]
      },
      {
        path: 'analysis',
        meta: { title: '用能分析', icon: 'DataAnalysis' },
        children: [
          { path: 'consumption', name: 'Consumption', component: () => import('@/views/analysis/Consumption.vue'), meta: { title: '能耗查询' } },
          { path: 'electricity', name: 'Electricity', component: () => import('@/views/analysis/Electricity.vue'), meta: { title: '电费分析' } }
        ]
      },
      {
        path: 'efficiency',
        meta: { title: '能效分析', icon: 'Lightning' },
        children: [
          { path: 'index', name: 'Efficiency', component: () => import('@/views/efficiency/Index.vue'), meta: { title: '能效分析' } },
          { path: 'energy-flow', name: 'EnergyFlow', component: () => import('@/views/efficiency/EnergyFlow.vue'), meta: { title: '能流分析' } }
        ]
      },
      {
        path: 'carbon',
        meta: { title: '碳排放管理', icon: 'Cloudy' },
        children: [
          { path: 'accounting', name: 'CarbonAccounting', component: () => import('@/views/carbon/Accounting.vue'), meta: { title: '碳排放核算' } },
          { path: 'footprint', name: 'CarbonFootprint', component: () => import('@/views/carbon/Footprint.vue'), meta: { title: '产品碳足迹' } },
          { path: 'cbam', name: 'CBAM', component: () => import('@/views/carbon/CBAM.vue'), meta: { title: 'CBAM核算' } },
          { path: 'supply-chain', name: 'SupplyChain', component: () => import('@/views/carbon/SupplyChain.vue'), meta: { title: '供应链管理' } },
          { path: 'asset', name: 'CarbonAsset', component: () => import('@/views/carbon/Asset.vue'), meta: { title: '碳资产' } },
          { path: 'factor', name: 'EmissionFactor', component: () => import('@/views/carbon/Factor.vue'), meta: { title: '碳排放因子' } }
        ]
      },
      {
        path: 'report',
        meta: { title: '统计报表', icon: 'Document' },
        children: [
          { path: 'energy', name: 'EnergyReport', component: () => import('@/views/report/Energy.vue'), meta: { title: '能耗统计表' } },
          { path: 'summary', name: 'SummaryReport', component: () => import('@/views/report/Summary.vue'), meta: { title: '能耗汇总表' } },
          { path: 'saving', name: 'SavingReport', component: () => import('@/views/report/Saving.vue'), meta: { title: '节能量统计表' } },
          { path: 'cost', name: 'CostReport', component: () => import('@/views/report/Cost.vue'), meta: { title: '用能成本统计表' } }
        ]
      },
      {
        path: 'system',
        meta: { title: '基础配置', icon: 'Setting' },
        children: [
          { path: 'user', name: 'UserManage', component: () => import('@/views/system/User.vue'), meta: { title: '用户管理' } },
          { path: 'role', name: 'RoleManage', component: () => import('@/views/system/Role.vue'), meta: { title: '角色管理' } },
          { path: 'unit', name: 'UnitManage', component: () => import('@/views/system/Unit.vue'), meta: { title: '用能单元' } },
          { path: 'device', name: 'DeviceManage', component: () => import('@/views/system/Device.vue'), meta: { title: '用能设备' } },
          { path: 'meter', name: 'MeterManage', component: () => import('@/views/system/Meter.vue'), meta: { title: '计量器具' } }
        ]
      }
    ]
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

router.beforeEach((to, from, next) => {
  const userStore = useUserStore()
  if (!to.meta.public && !userStore.token) {
    next('/login')
  } else {
    next()
  }
})

export default router
