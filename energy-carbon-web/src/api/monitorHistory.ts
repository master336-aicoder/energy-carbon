import request from '@/utils/request'

export const getMonitorHistoryList = (params?: any) => request.get('/monitor/history/list', { params })
export const getMonitorHistoryChart = (params?: any) => request.get('/monitor/history/chart', { params })
export const getMonitorDataTypes = () => request.get('/monitor/data-type/list')
