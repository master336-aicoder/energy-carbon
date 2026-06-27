import request from '@/utils/request'

// 监测数据
export const getMonitorDataList = (params?: any) => request.get('/monitor/data/list', { params })
export const getMonitorDataDetail = (id: number) => request.get(`/monitor/data/${id}`)
export const createMonitorData = (data: any) => request.post('/monitor/data', data)
export const updateMonitorData = (id: number, data: any) => request.put(`/monitor/data/${id}`, data)
export const deleteMonitorData = (id: number) => request.delete(`/monitor/data/${id}`)

// 监测设备
export const getDeviceList = (params?: any) => request.get('/monitor/device/list', { params })
export const getDeviceDetail = (id: number) => request.get(`/monitor/device/${id}`)
export const createDevice = (data: any) => request.post('/monitor/device', data)
export const updateDevice = (id: number, data: any) => request.put(`/monitor/device/${id}`, data)
export const deleteDevice = (id: number) => request.delete(`/monitor/device/${id}`)

// 班组管理
export const getWorkShiftList = (params?: any) => request.get('/monitor/work-shift/list', { params })
export const getWorkShiftDetail = (id: number) => request.get(`/monitor/work-shift/${id}`)
export const createWorkShift = (data: any) => request.post('/monitor/work-shift', data)
export const updateWorkShift = (id: number, data: any) => request.put(`/monitor/work-shift/${id}`, data)
export const deleteWorkShift = (id: number) => request.delete(`/monitor/work-shift/${id}`)
