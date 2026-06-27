/** 数据类型选项 - 全局统一 */
export const DATA_TYPE_OPTIONS = [
  { label: '电量', value: 'energy' },
  { label: '功率', value: 'power' },
  { label: '电压', value: 'voltage' },
  { label: '电流', value: 'current' },
  { label: '温度', value: 'temperature' },
  { label: '压力', value: 'pressure' },
  { label: '流量', value: 'flow' },
  { label: '总电量', value: 'total_energy' },
  { label: '有功功率', value: 'active_power' },
  { label: '无功功率', value: 'reactive_power' },
  { label: '视在功率', value: 'apparent_power' },
  { label: '功率因数', value: 'power_factor' },
  { label: '频率', value: 'frequency' },
]

/** 数据类型 label 映射 */
export const DATA_TYPE_MAP: Record<string, string> = Object.fromEntries(
  DATA_TYPE_OPTIONS.map(o => [o.value, o.label])
)

/** 报警级别 */
export const ALARM_LEVEL_OPTIONS = [
  { label: '一般', value: 'INFO' },
  { label: '警告', value: 'WARN' },
  { label: '严重', value: 'CRITICAL' },
]
export const ALARM_LEVEL_MAP: Record<string, string> = Object.fromEntries(ALARM_LEVEL_OPTIONS.map(o => [o.value, o.label]))
export const ALARM_LEVEL_TAG_MAP: Record<string, string> = { INFO: 'info', WARN: 'warning', CRITICAL: 'danger' }

/** 报警处理状态 */
export const HANDLE_STATUS_OPTIONS = [
  { label: '未处理', value: 'UNHANDLED' },
  { label: '已处理', value: 'HANDLED' },
  { label: '已忽略', value: 'IGNORED' },
]
export const HANDLE_STATUS_MAP: Record<string, string> = Object.fromEntries(HANDLE_STATUS_OPTIONS.map(o => [o.value, o.label]))
export const HANDLE_STATUS_TAG_MAP: Record<string, string> = { UNHANDLED: 'danger', HANDLED: 'success', IGNORED: 'info' }

/** 核算期次 */
export const ACCOUNTING_PERIOD_OPTIONS = [
  { label: '年度', value: 'YEARLY' },
  { label: '季度', value: 'QUARTERLY' },
  { label: '月度', value: 'MONTHLY' },
]
export const ACCOUNTING_PERIOD_MAP: Record<string, string> = Object.fromEntries(ACCOUNTING_PERIOD_OPTIONS.map(o => [o.value, o.label]))
export const ACCOUNTING_PERIOD_TAG_MAP: Record<string, string> = { YEARLY: '', QUARTERLY: 'warning', MONTHLY: 'info' }

/** 因子类别 */
export const FACTOR_CATEGORY_OPTIONS = [
  { label: '范围一-燃料排放', value: 'SCOPE1_FUEL' },
  { label: '范围二-电力排放', value: 'SCOPE2_GRID' },
  { label: '范围三-其他排放', value: 'SCOPE3_OTHER' },
]
export const FACTOR_CATEGORY_MAP: Record<string, string> = { SCOPE1_FUEL: '燃料排放', SCOPE2_GRID: '电力排放', SCOPE3_OTHER: '其他排放' }
export const FACTOR_CATEGORY_TAG_MAP: Record<string, string> = { SCOPE1_FUEL: 'danger', SCOPE2_GRID: 'warning', SCOPE3_OTHER: 'info' }

/** 碳资产类型 */
export const CARBON_ASSET_TYPE_OPTIONS = [
  { label: '配额', value: 'QUOTA' },
  { label: 'CCER', value: 'CCER' },
  { label: '碳汇', value: 'CARBON_SINK' },
]
export const CARBON_ASSET_TYPE_MAP: Record<string, string> = Object.fromEntries(CARBON_ASSET_TYPE_OPTIONS.map(o => [o.value, o.label]))
export const CARBON_ASSET_TYPE_TAG_MAP: Record<string, string> = { QUOTA: '', CCER: 'success', CARBON_SINK: 'warning' }

/** 碳资产状态 */
export const CARBON_ASSET_STATUS_OPTIONS = [
  { label: '有效', value: 'VALID' },
  { label: '已使用', value: 'USED' },
  { label: '已过期', value: 'EXPIRED' },
]
export const CARBON_ASSET_STATUS_MAP: Record<string, string> = Object.fromEntries(CARBON_ASSET_STATUS_OPTIONS.map(o => [o.value, o.label]))

/** 申报状态 */
export const DECLARATION_STATUS_OPTIONS = [
  { label: '草稿', value: 'DRAFT' },
  { label: '已提交', value: 'SUBMITTED' },
  { label: '已审核', value: 'APPROVED' },
]
export const DECLARATION_STATUS_MAP: Record<string, string> = Object.fromEntries(DECLARATION_STATUS_OPTIONS.map(o => [o.value, o.label]))
export const DECLARATION_STATUS_TAG_MAP: Record<string, string> = { DRAFT: 'info', SUBMITTED: 'warning', APPROVED: 'success' }

/** CBAM产品类别 */
export const PRODUCT_CATEGORY_OPTIONS = [
  { label: '钢铁', value: 'IRON_STEEL' },
  { label: '铝', value: 'ALUMINIUM' },
  { label: '水泥', value: 'CEMENT' },
  { label: '化肥', value: 'FERTILIZER' },
  { label: '电力', value: 'ELECTRICITY' },
  { label: '氢', value: 'HYDROGEN' },
]

/** 文档类型 */
export const DOC_TYPE_OPTIONS = [
  { label: '技术标准', value: 'STANDARD' },
  { label: '行业报告', value: 'REPORT' },
  { label: '操作手册', value: 'MANUAL' },
  { label: '其他', value: 'OTHER' },
]

/** 计量器具类型 */
export const METER_TYPE_OPTIONS = [
  { label: '电能表', value: 'electric_meter' },
  { label: '水表', value: 'water_meter' },
  { label: '燃气表', value: 'gas_meter' },
  { label: '热量表', value: 'heat_meter' },
  { label: '蒸汽表', value: 'steam_meter' },
]

/** 设备类型 */
export const DEVICE_TYPE_OPTIONS = [
  { label: '电机', value: 'motor' },
  { label: '锅炉', value: 'boiler' },
  { label: '空压机', value: 'air_compressor' },
  { label: '风机', value: 'fan' },
  { label: '泵', value: 'pump' },
  { label: '照明', value: 'lighting' },
  { label: '空调', value: 'ac' },
  { label: '其他', value: 'other' },
]

/** 通用启用/禁用状态 */
export const ENABLE_STATUS_OPTIONS = [
  { label: '启用', value: 1 },
  { label: '禁用', value: 0 },
]
export const ENABLE_STATUS_MAP: Record<number, string> = { 1: '启用', 0: '禁用' }
export const ENABLE_STATUS_TAG_MAP: Record<number, string> = { 1: 'success', 0: 'danger' }

/** 预算类型 */
export const BUDGET_TYPE_OPTIONS = [
  { label: '能耗预算', value: 'ENERGY' },
  { label: '碳排放预算', value: 'CARBON' },
]
export const BUDGET_TYPE_MAP: Record<string, string> = { ENERGY: '能耗', CARBON: '碳排放' }

/** 指标类型 */
export const INDICATOR_TYPE_OPTIONS = [
  { label: '综合能耗', value: 'energy' },
  { label: '单位产值能耗', value: 'intensity' },
]

/** 统计周期 */
export const STAT_PERIOD_OPTIONS = [
  { label: '日', value: 'DAY' },
  { label: '月', value: 'MONTH' },
  { label: '年', value: 'YEAR' },
]
export const STAT_PERIOD_MAP: Record<string, string> = Object.fromEntries(STAT_PERIOD_OPTIONS.map(o => [o.value, o.label]))
