<template>
  <div class="monitor-realtime">
    <!-- 顶部工具栏 -->
    <el-card class="toolbar-card" shadow="never">
      <div class="toolbar">
        <div class="toolbar-left">
          <el-button type="primary" :icon="Refresh" @click="handleRefresh" :loading="refreshLoading">
            刷新
          </el-button>
          <el-radio-group v-model="dataType" @change="handleDataTypeChange">
            <el-radio-button label="power">功率</el-radio-button>
            <el-radio-button label="voltage">电压</el-radio-button>
            <el-radio-button label="current">电流</el-radio-button>
            <el-radio-button label="energy">电量</el-radio-button>
          </el-radio-group>
        </div>
        <div class="toolbar-right">
          <el-select v-model="timeRange" placeholder="时间范围" style="width: 140px" @change="handleTimeRangeChange">
            <el-option label="最近15分钟" value="15m" />
            <el-option label="最近1小时" value="1h" />
            <el-option label="最近6小时" value="6h" />
            <el-option label="最近24小时" value="24h" />
          </el-select>
          <el-tag :type="wsConnected ? 'success' : 'danger'" class="ws-status">
            {{ wsConnected ? '实时连接' : '连接断开' }}
          </el-tag>
        </div>
      </div>
    </el-card>

    <div class="content-wrapper">
      <!-- 左侧能源树 -->
      <el-card class="tree-card" shadow="never">
        <template #header>
          <div class="tree-header">
            <span>能源设备树</span>
            <el-input
              v-model="treeFilterText"
              placeholder="搜索设备"
              clearable
              size="small"
              style="width: 140px"
            />
          </div>
        </template>
        <el-tree
          ref="treeRef"
          :data="deviceTree"
          :props="treeProps"
          :filter-node-method="filterNode"
          highlight-current
          default-expand-all
          @node-click="handleNodeClick"
        >
          <template #default="{ node, data }">
            <div class="tree-node">
              <el-icon v-if="data.type === 'unit'" class="node-icon"><OfficeBuilding /></el-icon>
              <el-icon v-else-if="data.type === 'device'" class="node-icon"><SetUp /></el-icon>
              <el-icon v-else class="node-icon"><Odometer /></el-icon>
              <span class="node-label" :class="data.type">{{ node.label }}</span>
              <el-tag v-if="data.type === 'meter'" size="small" :type="getNodeStatusType(data)">
                {{ getNodeStatusText(data) }}
              </el-tag>
            </div>
          </template>
        </el-tree>
      </el-card>

      <!-- 右侧内容区 -->
      <div class="right-content">
        <!-- 数据卡片 -->
        <el-card class="data-card" shadow="never">
          <template #header>
            <div class="data-header">
              <span>实时数据 - {{ selectedNode?.name || '请选择节点' }}</span>
              <span class="update-time" v-if="lastUpdateTime">更新时间: {{ lastUpdateTime }}</span>
            </div>
          </template>
          <div class="data-cards" v-if="selectedNode && selectedNode.type === 'meter'">
            <div class="metric-card" v-for="(item, index) in realtimeDataList" :key="index">
              <div class="metric-label">{{ item.label }}</div>
              <div class="metric-value" :style="{ color: item.color }">
                {{ formatValue(item.value) }}
                <span class="metric-unit">{{ item.unit }}</span>
              </div>
              <div class="metric-trend" v-if="item.trend !== undefined">
                <el-icon :class="item.trend >= 0 ? 'up' : 'down'">
                  <ArrowUp v-if="item.trend >= 0" />
                  <ArrowDown v-else />
                </el-icon>
                <span>{{ Math.abs(item.trend).toFixed(2) }}%</span>
              </div>
            </div>
          </div>
          <el-empty v-else description="请选择一个计量器具查看实时数据" />
        </el-card>

        <!-- 实时曲线图 -->
        <el-card class="chart-card" shadow="never">
          <template #header>
            <div class="chart-header">
              <span>实时曲线 - {{ dataTypeLabel }}</span>
              <div class="chart-legend" v-if="chartSeries.length > 0">
                <span v-for="series in chartSeries" :key="series.name" class="legend-item">
                  <span class="legend-dot" :style="{ background: series.color }"></span>
                  {{ series.name }}
                </span>
              </div>
            </div>
          </template>
          <div ref="chartRef" class="chart-container"></div>
        </el-card>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted, onUnmounted, watch, nextTick, computed } from 'vue'
import { ElMessage } from 'element-plus'
import { Refresh, OfficeBuilding, SetUp, Odometer, ArrowUp, ArrowDown } from '@element-plus/icons-vue'
import * as echarts from 'echarts'
import { getDeviceTree, getRealtimeData, getHistoryData, type DeviceTreeNode } from '@/api/realtime'
import type { RealTimeData } from '@/types'

// 树相关
const treeRef = ref()
const treeFilterText = ref('')
const deviceTree = ref<DeviceTreeNode[]>([])
const selectedNode = ref<DeviceTreeNode | null>(null)
const treeProps = {
  label: 'name',
  children: 'children'
}

// 工具栏
const dataType = ref('power')
const timeRange = ref('1h')
const refreshLoading = ref(false)
const wsConnected = ref(false)
const lastUpdateTime = ref('')

// 图表
const chartRef = ref<HTMLDivElement>()
let chartInstance: echarts.ECharts | null = null
const chartSeries = ref<Array<{ name: string; color: string }>>([])

// 实时数据
const realtimeDataList = ref<Array<{
  label: string
  value: number
  unit: string
  color: string
  trend?: number
}>>([])

// WebSocket
let ws: WebSocket | null = null
let refreshTimer: ReturnType<typeof setInterval> | null = null

const dataTypeLabel = computed(() => {
  const map: Record<string, string> = {
    power: '功率',
    voltage: '电压',
    current: '电流',
    energy: '电量'
  }
  return map[dataType.value] || dataType.value
})

const dataTypeUnit = computed(() => {
  const map: Record<string, string> = {
    power: 'kW',
    voltage: 'V',
    current: 'A',
    energy: 'kWh'
  }
  return map[dataType.value] || ''
})

// 获取时间范围
function getTimeRange(): { startTime: string; endTime: string } {
  const endTime = new Date()
  const startTime = new Date()
  const range = timeRange.value
  if (range === '15m') {
    startTime.setMinutes(endTime.getMinutes() - 15)
  } else if (range === '1h') {
    startTime.setHours(endTime.getHours() - 1)
  } else if (range === '6h') {
    startTime.setHours(endTime.getHours() - 6)
  } else if (range === '24h') {
    startTime.setDate(endTime.getDate() - 1)
  }
  return {
    startTime: startTime.toISOString(),
    endTime: endTime.toISOString()
  }
}

// 初始化图表
function initChart() {
  if (!chartRef.value) return
  chartInstance = echarts.init(chartRef.value)
  const option: echarts.EChartsOption = {
    grid: {
      left: '3%',
      right: '4%',
      bottom: '3%',
      top: '10%',
      containLabel: true
    },
    tooltip: {
      trigger: 'axis',
      axisPointer: {
        type: 'cross'
      }
    },
    xAxis: {
      type: 'time',
      boundaryGap: false,
      axisLine: { lineStyle: { color: '#ccc' } },
      axisLabel: { color: '#666' }
    },
    yAxis: {
      type: 'value',
      name: dataTypeUnit.value,
      axisLine: { lineStyle: { color: '#ccc' } },
      axisLabel: { color: '#666' },
      splitLine: { lineStyle: { color: '#eee' } }
    },
    series: []
  }
  chartInstance.setOption(option)

  window.addEventListener('resize', handleResize)
}

function handleResize() {
  chartInstance?.resize()
}

// 获取设备树
async function fetchDeviceTree() {
  try {
    const res = await getDeviceTree()
    deviceTree.value = res.data
  } catch (error) {
    // 使用模拟数据
    deviceTree.value = [
      {
        id: 1,
        name: '生产车间A',
        type: 'unit',
        children: [
          {
            id: 11,
            name: '电机-001',
            type: 'device',
            children: [
              { id: 111, name: '电能表-001', type: 'meter', data: { status: 1, value: 125.6 } },
              { id: 112, name: '电能表-002', type: 'meter', data: { status: 1, value: 89.3 } }
            ]
          },
          {
            id: 12,
            name: '空压机-001',
            type: 'device',
            children: [
              { id: 121, name: '电能表-003', type: 'meter', data: { status: 1, value: 210.5 } }
            ]
          }
        ]
      },
      {
        id: 2,
        name: '生产车间B',
        type: 'unit',
        children: [
          {
            id: 21,
            name: '锅炉-001',
            type: 'device',
            children: [
              { id: 211, name: '燃气表-001', type: 'meter', data: { status: 1, value: 45.2 } },
              { id: 212, name: '蒸汽表-001', type: 'meter', data: { status: 0, value: 0 } }
            ]
          }
        ]
      }
    ]
  }
}

// 节点点击
function handleNodeClick(data: DeviceTreeNode) {
  selectedNode.value = data
  if (data.type === 'meter') {
    fetchRealtimeData(data.id)
    fetchHistoryData(data.id)
    connectWebSocket(data.id)
  }
}

// 获取实时数据
async function fetchRealtimeData(meterId: number) {
  try {
    const res = await getRealtimeData({ meterId, dataType: dataType.value })
    updateRealtimeCards(res.data)
    lastUpdateTime.value = new Date().toLocaleTimeString()
  } catch {
    // 模拟数据
    updateRealtimeCards({
      id: meterId,
      dataType: dataType.value,
      value: Math.random() * 200 + 50,
      unit: dataTypeUnit.value,
      timestamp: new Date().toISOString()
    } as RealTimeData)
    lastUpdateTime.value = new Date().toLocaleTimeString()
  }
}

// 更新实时卡片
function updateRealtimeCards(data: RealTimeData) {
  const baseValue = data.value || 0
  realtimeDataList.value = [
    {
      label: '当前' + dataTypeLabel.value,
      value: baseValue,
      unit: data.unit || dataTypeUnit.value,
      color: '#409eff',
      trend: Math.random() * 10 - 5
    },
    {
      label: '最大值',
      value: baseValue * 1.2,
      unit: data.unit || dataTypeUnit.value,
      color: '#67c23a'
    },
    {
      label: '最小值',
      value: baseValue * 0.8,
      unit: data.unit || dataTypeUnit.value,
      color: '#e6a23c'
    },
    {
      label: '平均值',
      value: baseValue * 0.95,
      unit: data.unit || dataTypeUnit.value,
      color: '#909399'
    }
  ]
}

// 获取历史数据
async function fetchHistoryData(meterId: number) {
  if (!chartInstance) return
  try {
    const { startTime, endTime } = getTimeRange()
    const res = await getHistoryData({
      meterId,
      dataType: dataType.value,
      startTime,
      endTime,
      interval: '1m'
    })
    updateChart(res.data)
  } catch {
    // 模拟数据
    const mockData = generateMockHistoryData()
    updateChart(mockData)
  }
}

// 生成模拟历史数据
function generateMockHistoryData(): RealTimeData[] {
  const data: RealTimeData[] = []
  const now = new Date()
  const { startTime } = getTimeRange()
  const start = new Date(startTime)
  const interval = (now.getTime() - start.getTime()) / 60

  for (let i = 0; i < 60; i++) {
    const time = new Date(start.getTime() + interval * i)
    data.push({
      dataType: dataType.value,
      value: Math.random() * 100 + 50 + Math.sin(i / 10) * 20,
      unit: dataTypeUnit.value,
      timestamp: time.toISOString()
    })
  }
  return data
}

// 更新图表
function updateChart(data: RealTimeData[]) {
  if (!chartInstance) return

  const seriesData = data.map(item => [item.timestamp, item.value])
  const color = '#409eff'

  chartSeries.value = [{
    name: selectedNode.value?.name || '数据',
    color
  }]

  chartInstance.setOption({
    yAxis: {
      name: dataTypeUnit.value
    },
    series: [{
      name: selectedNode.value?.name || '数据',
      type: 'line',
      smooth: true,
      symbol: 'none',
      areaStyle: {
        color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
          { offset: 0, color: 'rgba(64, 158, 255, 0.3)' },
          { offset: 1, color: 'rgba(64, 158, 255, 0.05)' }
        ])
      },
      lineStyle: {
        color,
        width: 2
      },
      itemStyle: {
        color
      },
      data: seriesData
    }]
  })
}

// WebSocket连接
function connectWebSocket(meterId: number) {
  disconnectWebSocket()

  const wsUrl = `${import.meta.env.VITE_WS_BASE_URL || 'ws://localhost:8080/ws'}/realtime?meterId=${meterId}`

  try {
    ws = new WebSocket(wsUrl)

    ws.onopen = () => {
      wsConnected.value = true
    }

    ws.onmessage = (event) => {
      try {
        const data = JSON.parse(event.data)
        if (data.meterId === meterId) {
          updateRealtimeCards(data)
          lastUpdateTime.value = new Date().toLocaleTimeString()
          // 追加到图表
          appendChartData(data)
        }
      } catch {
        // 忽略解析错误
      }
    }

    ws.onclose = () => {
      wsConnected.value = false
    }

    ws.onerror = () => {
      wsConnected.value = false
    }
  } catch {
    wsConnected.value = false
  }
}

function disconnectWebSocket() {
  if (ws) {
    ws.close()
    ws = null
  }
}

function appendChartData(data: RealTimeData) {
  if (!chartInstance) return
  const option = chartInstance.getOption()
  const series = option.series as any[]
  if (series && series.length > 0 && series[0].data) {
    series[0].data.push([data.timestamp, data.value])
    // 保持最近60个点
    if (series[0].data.length > 60) {
      series[0].data.shift()
    }
    chartInstance.setOption({ series })
  }
}

// 刷新
async function handleRefresh() {
  if (!selectedNode.value || selectedNode.value.type !== 'meter') {
    ElMessage.warning('请先选择一个计量器具')
    return
  }
  refreshLoading.value = true
  try {
    await fetchRealtimeData(selectedNode.value.id)
    await fetchHistoryData(selectedNode.value.id)
    ElMessage.success('刷新成功')
  } finally {
    refreshLoading.value = false
  }
}

function handleDataTypeChange() {
  if (selectedNode.value && selectedNode.value.type === 'meter') {
    fetchRealtimeData(selectedNode.value.id)
    fetchHistoryData(selectedNode.value.id)
  }
}

function handleTimeRangeChange() {
  if (selectedNode.value && selectedNode.value.type === 'meter') {
    fetchHistoryData(selectedNode.value.id)
  }
}

// 树过滤
watch(treeFilterText, (val) => {
  treeRef.value?.filter(val)
})

function filterNode(value: string, data: DeviceTreeNode) {
  if (!value) return true
  return data.name.includes(value)
}

// 节点状态
function getNodeStatusType(data: DeviceTreeNode): string {
  if (data.data?.status === 1) return 'success'
  if (data.data?.status === 0) return 'info'
  return 'warning'
}

function getNodeStatusText(data: DeviceTreeNode): string {
  if (data.data?.status === 1) return '正常'
  if (data.data?.status === 0) return '离线'
  return '未知'
}

function formatValue(value: number): string {
  if (value === undefined || value === null) return '-'
  return value.toFixed(2)
}

// 自动刷新
function startAutoRefresh() {
  refreshTimer = setInterval(() => {
    if (selectedNode.value && selectedNode.value.type === 'meter') {
      fetchRealtimeData(selectedNode.value.id)
    }
  }, 30000) // 30秒刷新一次
}

function stopAutoRefresh() {
  if (refreshTimer) {
    clearInterval(refreshTimer)
    refreshTimer = null
  }
}

onMounted(() => {
  fetchDeviceTree()
  nextTick(() => {
    initChart()
  })
  startAutoRefresh()
})

onUnmounted(() => {
  disconnectWebSocket()
  stopAutoRefresh()
  window.removeEventListener('resize', handleResize)
  chartInstance?.dispose()
})
</script>

<style scoped lang="scss">
.monitor-realtime {
  padding: 20px;
  display: flex;
  flex-direction: column;
  gap: 16px;
  height: calc(100vh - 100px);
}

.toolbar-card {
  flex-shrink: 0;
}

.toolbar {
  display: flex;
  justify-content: space-between;
  align-items: center;

  .toolbar-left {
    display: flex;
    align-items: center;
    gap: 16px;
  }

  .toolbar-right {
    display: flex;
    align-items: center;
    gap: 12px;
  }

  .ws-status {
    font-size: 12px;
  }
}

.content-wrapper {
  display: flex;
  gap: 16px;
  flex: 1;
  min-height: 0;
}

.tree-card {
  width: 280px;
  flex-shrink: 0;
  display: flex;
  flex-direction: column;

  :deep(.el-card__body) {
    flex: 1;
    overflow: auto;
    padding: 12px;
  }
}

.tree-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.tree-node {
  display: flex;
  align-items: center;
  gap: 6px;
  font-size: 13px;

  .node-icon {
    font-size: 14px;
    color: #909399;
  }

  .node-label {
    flex: 1;

    &.unit {
      font-weight: 600;
      color: #303133;
    }

    &.device {
      color: #606266;
    }

    &.meter {
      color: #409eff;
    }
  }
}

.right-content {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 16px;
  min-width: 0;
}

.data-card {
  flex-shrink: 0;

  .data-header {
    display: flex;
    justify-content: space-between;
    align-items: center;

    .update-time {
      font-size: 12px;
      color: #909399;
    }
  }
}

.data-cards {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 16px;
}

.metric-card {
  background: #f5f7fa;
  border-radius: 8px;
  padding: 16px;
  text-align: center;

  .metric-label {
    font-size: 13px;
    color: #909399;
    margin-bottom: 8px;
  }

  .metric-value {
    font-size: 28px;
    font-weight: 600;
    line-height: 1.2;

    .metric-unit {
      font-size: 14px;
      font-weight: normal;
      color: #909399;
      margin-left: 4px;
    }
  }

  .metric-trend {
    margin-top: 8px;
    font-size: 12px;
    color: #909399;
    display: flex;
    align-items: center;
    justify-content: center;
    gap: 4px;

    .up {
      color: #67c23a;
    }

    .down {
      color: #f56c6c;
    }
  }
}

.chart-card {
  flex: 1;
  display: flex;
  flex-direction: column;
  min-height: 0;

  :deep(.el-card__body) {
    flex: 1;
    padding: 12px;
  }

  .chart-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
  }

  .chart-legend {
    display: flex;
    gap: 16px;

    .legend-item {
      display: flex;
      align-items: center;
      gap: 6px;
      font-size: 12px;
      color: #606266;

      .legend-dot {
        width: 8px;
        height: 8px;
        border-radius: 50%;
      }
    }
  }
}

.chart-container {
  width: 100%;
  height: 100%;
}

@media (max-width: 1200px) {
  .data-cards {
    grid-template-columns: repeat(2, 1fr);
  }
}
</style>
