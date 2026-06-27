import request from '@/utils/request'

export const getDictList = (params?: any) => request.get('/system/dict-data/list', { params })
export const getDictDetail = (id: number) => request.get(`/system/dict-data/${id}`)
export const createDict = (data: any) => request.post('/system/dict-data', data)
export const updateDict = (id: number, data: any) => request.put(`/system/dict-data/${id}`, data)
export const deleteDict = (id: number) => request.delete(`/system/dict-data/${id}`)
