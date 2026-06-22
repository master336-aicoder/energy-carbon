/**
 * ECharts 按需引入配置
 * 减少打包体积，仅引入项目实际使用的组件
 */
import * as echarts from 'echarts/core'

// 引入必要的图表类型
import { BarChart, LineChart, PieChart, GaugeChart, ScatterChart, RadarChart } from 'echarts/charts'
echarts.use([BarChart, LineChart, PieChart, GaugeChart, ScatterChart, RadarChart])

// 引入必要的组件
import {
  TitleComponent,
  TooltipComponent,
  LegendComponent,
  GridComponent,
  DatasetComponent,
  TransformComponent,
  ToolboxComponent,
  DataZoomComponent,
  VisualMapComponent,
  GraphicComponent,
  MarkLineComponent,
  MarkPointComponent
} from 'echarts/components'
echarts.use([
  TitleComponent,
  TooltipComponent,
  LegendComponent,
  GridComponent,
  DatasetComponent,
  TransformComponent,
  ToolboxComponent,
  DataZoomComponent,
  VisualMapComponent,
  GraphicComponent,
  MarkLineComponent,
  MarkPointComponent
])

// 引入渲染器（必须）
import { CanvasRenderer } from 'echarts/renderers'
echarts.use(CanvasRenderer)

export default echarts
