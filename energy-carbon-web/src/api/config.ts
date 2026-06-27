import request from '@/utils/request'

export const getConfigList = (params?: any) => request.get('/system/config/list', { params })
export const getConfigDetail = (id: number) => request.get(`/system/config/${id}`)
export const createConfig = (data: any) => request.post('/system/config', data)
export const updateConfig = (id: number, data: any) => request.put(`/system/config/${id}`, data)
export const deleteConfig = (id: number) => request.delete(`/system/config/${id}`)
