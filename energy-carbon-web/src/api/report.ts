import request from '@/utils/request'

// 指标管理
export const getIndicatorList = (params?: any) => request.get('/report/indicator/list', { params })
export const getIndicatorDetail = (id: number) => request.get(`/report/indicator/${id}`)
export const createIndicator = (data: any) => request.post('/report/indicator', data)
export const updateIndicator = (id: number, data: any) => request.put(`/report/indicator/${id}`, data)
export const deleteIndicator = (id: number) => request.delete(`/report/indicator/${id}`)

// 生产报表
export const getProductionList = (params?: any) => request.get('/report/production/list', { params })
export const getProductionDetail = (id: number) => request.get(`/report/production/${id}`)
export const createProduction = (data: any) => request.post('/report/production', data)
export const updateProduction = (id: number, data: any) => request.put(`/report/production/${id}`, data)
export const deleteProduction = (id: number) => request.delete(`/report/production/${id}`)

// 基线管理
export const getBaselineList = (params?: any) => request.get('/report/baseline/list', { params })
export const getBaselineDetail = (id: number) => request.get(`/report/baseline/${id}`)
export const createBaseline = (data: any) => request.post('/report/baseline', data)
export const updateBaseline = (id: number, data: any) => request.put(`/report/baseline/${id}`, data)
export const deleteBaseline = (id: number) => request.delete(`/report/baseline/${id}`)

// 计划管理
export const getScheduleList = (params?: any) => request.get('/report/schedule/list', { params })
export const getScheduleDetail = (id: number) => request.get(`/report/schedule/${id}`)
export const createSchedule = (data: any) => request.post('/report/schedule', data)
export const updateSchedule = (id: number, data: any) => request.put(`/report/schedule/${id}`, data)
export const deleteSchedule = (id: number) => request.delete(`/report/schedule/${id}`)

// --- 旧API兼容（能耗汇总） ---
export const getEnergyTotalList = (params?: any) => request.get('/report/energy-total/list', { params })
export const getEnergyTotalByRange = (params?: any) => request.get('/report/energy-total/range', { params })

// --- 旧API兼容（节能分析） ---
export const getEnergySavingList = (params?: any) => request.get('/report/energy-saving/list', { params })
export const getEnergySavingByRange = (params?: any) => request.get('/report/energy-saving/range', { params })

// --- 旧API兼容（能耗统计） ---
export const getEnergySummaryList = (params?: any) => request.get('/report/energy-summary/list', { params })
export const getEnergySummaryByRange = (params?: any) => request.get('/report/energy-summary/range', { params })
export const exportEnergySummary = (params?: any) => request.get('/report/energy-summary/export', { params, responseType: 'blob' })

// --- 旧API兼容（成本统计） ---
export const getCostStatList = (params?: any) => request.get('/report/cost/list', { params })
export const getCostStatByRange = (params?: any) => request.get('/report/cost/range', { params })
