<template>
  <div class="efficiency-energy-flow">
    <!-- 筛选栏 -->
    <el-card class="filter-card">
      <el-form :model="queryParams" inline>
        <el-form-item label="用能单元">
          <el-tree-select v-model="queryParams.energyUnitId" :data="unitTree" :props="{ label: 'deptName', value: 'id' }" placeholder="选择用能单元" clearable style="width: 200px" />
        </el-form-item>
        <el-form-item label="时间范围">
          <el-date-picker v-model="dateRange" type="daterange" range-separator="至" start-placeholder="开始日期" end-placeholder="结束日期" value-format="YYYY-MM-DD" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">查询</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <!-- 桑基图 -->
    <el-card class="chart-card">
      <template #header><span>能源流动桑基图</span></template>
      <div ref="sankeyChartRef" style="height: 500px"></div>
    </el-card>

    <!-- 能流数据表格 -->
    <el-card class="table-card">
      <template #header>
        <div class="table-header">
          <span>能流明细数据</span>
          <el-button type="primary" size="small" @click="handleExport">导出</el-button>
        </div>
      </template>
      <el-table :data="tableData" border stripe v-loading="loading">
        <el-table-column prop="source" label="来源" min-width="160" />
        <el-table-column prop="target" label="去向" min-width="160" />
        <el-table-column prop="value" label="能量值" align="right" width="140">
          <template #default="{ row }">{{ Number(row.value).toLocaleString() }} {{ row.unit || 'kgce' }}</template>
        </el-table-column>
        <el-table-column prop="ratio" label="占比" align="right" width="120">
          <template #default="{ row }">{{ row.ratio }}%</template>
        </el-table-column>
      </el-table>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, onUnmounted } from 'vue'
import echarts from '@/utils/echarts'
import { ElMessage } from 'element-plus'

const unitTree = ref([{ id: 1, deptName: '一分厂' }, { id: 2, deptName: '二分厂' }, { id: 3, deptName: '三分厂' }])
const queryParams = ref({ energyUnitId: 1 as number | undefined, startDate: undefined as string | undefined, endDate: undefined as string | undefined })
const dateRange = ref<string[]>([])
const loading = ref(false)
const tableData = ref<Array<Record<string, unknown>>>([])
const sankeyChartRef = ref<HTMLDivElement>()
let sankeyChart: echarts.ECharts | null = null

// 模拟能流数据（桑基图节点和连接）
const sankeyData = {
  nodes: [
    { name: '外购电力' }, { name: '外购天然气' }, { name: '外购蒸汽' }, { name: '外购水' },
    { name: '配电系统' }, { name: '锅炉系统' }, { name: '换热系统' },
    { name: '生产线A' }, { name: '生产线B' }, { name: '照明系统' }, { name: '空调系统' },
    { name: '产品A' }, { name: '产品B' }, { name: '损耗' }
  ],
  links: [
    { source: '外购电力', target: '配电系统', value: 8000 },
    { source: '外购天然气', target: '锅炉系统', value: 5000 },
    { source: '外购蒸汽', target: '换热系统', value: 3000 },
    { source: '外购水', target: '锅炉系统', value: 1000 },
    { source: '配电系统', target: '生产线A', value: 3500 },
    { source: '配电系统', target: '生产线B', value: 2500 },
    { source: '配电系统', target: '照明系统', value: 800 },
    { source: '配电系统', target: '空调系统', value: 1200 },
    { source: '锅炉系统', target: '生产线A', value: 3000 },
    { source: '锅炉系统', target: '生产线B', value: 2000 },
    { source: '锅炉系统', target: '损耗', value: 1000 },
    { source: '换热系统', target: '生产线A', value: 1500 },
    { source: '换热系统', target: '生产线B', value: 1000 },
    { source: '换热系统', target: '损耗', value: 500 },
    { source: '生产线A', target: '产品A', value: 6000 },
    { source: '生产线A', target: '损耗', value: 2000 },
    { source: '生产线B', target: '产品B', value: 4000 },
    { source: '生产线B', target: '损耗', value: 1500 },
    { source: '照明系统', target: '损耗', value: 800 },
    { source: '空调系统', target: '损耗', value: 1200 }
  ]
}

function handleSearch() {
  if (dateRange.value && dateRange.value.length === 2) {
    queryParams.value.startDate = dateRange.value[0]
    queryParams.value.endDate = dateRange.value[1]
  } else {
    queryParams.value.startDate = undefined
    queryParams.value.endDate = undefined
  }
  initSankeyChart()
  buildTableData()
}

function handleReset() {
  dateRange.value = []
  queryParams.value = { energyUnitId: 1, startDate: undefined, endDate: undefined }
  handleSearch()
}

function initSankeyChart() {
  if (!sankeyChartRef.value) return
  if (!sankeyChart) sankeyChart = echarts.init(sankeyChartRef.value)

  // 计算总能量
  const totalValue = sankeyData.links.filter(l =>
    l.source.startsWith('外购')
  ).reduce((s, l) => s + l.value, 0)

  sankeyChart.setOption({
    tooltip: {
      trigger: 'item',
      formatter: (params: { data: { name: string; value: number; source?: string; target?: string } }) => {
        if (params.data.source && params.data.target) {
          const ratio = ((params.data.value / totalValue) * 100).toFixed(1)
          return `${params.data.source} → ${params.data.target}<br/>能量值: ${params.data.value.toLocaleString()} kgce<br/>占比: ${ratio}%`
        }
        return `${params.data.name}<br/>总进出: ${params.data.value.toLocaleString()} kgce`
      }
    },
    series: [{
      type: 'sankey',
      layout: 'none',
      layoutIterations: 32,
      emphasis: { focus: 'adjacency' },
      nodeAlign: 'left',
      nodeWidth: 20,
      nodeGap: 12,
      draggable: true,
      data: sankeyData.nodes.map(n => ({
        ...n,
        itemStyle: {
          color: getNodeColor(n.name)
        }
      })),
      links: sankeyData.links.map(l => ({
        ...l,
        lineStyle: {
          color: 'source',
          opacity: 0.5,
          curveness: 0.5
        }
      })),
      label: {
        show: true,
        position: 'right',
        fontSize: 12
      },
      lineStyle: {
        color: 'gradient',
        curveness: 0.5
      }
    }]
  })
}

function getNodeColor(name: string): string {
  if (name.startsWith('外购')) return '#3b82f6'
  if (name.includes('系统')) return '#f59e0b'
  if (name.startsWith('生产')) return '#0d9488'
  if (name === '损耗') return '#dc2626'
  if (name.startsWith('产品')) return '#10b981'
  if (name.startsWith('照明')) return '#8b5cf6'
  if (name.startsWith('空调')) return '#6366f1'
  return '#6b7280'
}

function buildTableData() {
  const totalValue = sankeyData.links.filter(l =>
    l.source.startsWith('外购')
  ).reduce((s, l) => s + l.value, 0)

  tableData.value = sankeyData.links.map(l => ({
    source: l.source,
    target: l.target,
    value: l.value,
    unit: 'kgce',
    ratio: ((l.value / totalValue) * 100).toFixed(1)
  }))
}

function handleExport() {
  ElMessage.info('导出功能待实现')
}

onMounted(() => {
  initSankeyChart()
  buildTableData()
  window.addEventListener('resize', () => sankeyChart?.resize())
})
onUnmounted(() => { sankeyChart?.dispose() })
</script>

<style scoped lang="scss">
.efficiency-energy-flow {
  .filter-card { margin-bottom: 16px; }
  .chart-card { margin-bottom: 16px; }
  .table-card { margin-top: 16px; }
  .table-header { display: flex; justify-content: space-between; align-items: center; }
}
</style>