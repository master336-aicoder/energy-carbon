import request from '@/utils/request'

// 能效基准
export const getBenchmarkList = (params?: any) => request.get('/efficiency/benchmark/list', { params })
export const getBenchmarkDetail = (id: number) => request.get(`/efficiency/benchmark/${id}`)
export const createBenchmark = (data: any) => request.post('/efficiency/benchmark', data)
export const updateBenchmark = (id: number, data: any) => request.put(`/efficiency/benchmark/${id}`, data)
export const deleteBenchmark = (id: number) => request.delete(`/efficiency/benchmark/${id}`)

// 能效分析
export const getEfficiencyAnalysisList = (params?: any) => request.get('/efficiency/analysis/list', { params })
export const getEfficiencyAnalysisDetail = (id: number) => request.get(`/efficiency/analysis/${id}`)
export const createEfficiencyAnalysis = (data: any) => request.post('/efficiency/analysis', data)
export const updateEfficiencyAnalysis = (id: number, data: any) => request.put(`/efficiency/analysis/${id}`, data)
export const deleteEfficiencyAnalysis = (id: number) => request.delete(`/efficiency/analysis/${id}`)
