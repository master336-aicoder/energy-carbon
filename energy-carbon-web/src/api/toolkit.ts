import request from '@/utils/request'

// 绿色工厂
export const getGreenFactoryList = (params?: any) => request.get('/toolkit/green-factory/list', { params })
export const getGreenFactoryDetail = (id: number) => request.get(`/toolkit/green-factory/${id}`)
export const createGreenFactory = (data: any) => request.post('/toolkit/green-factory', data)
export const updateGreenFactory = (id: number, data: any) => request.put(`/toolkit/green-factory/${id}`, data)
export const deleteGreenFactory = (id: number) => request.delete(`/toolkit/green-factory/${id}`)

// 削峰填谷
export const getPeakShiftingList = (params?: any) => request.get('/toolkit/peak-shifting/list', { params })
export const getPeakShiftingDetail = (id: number) => request.get(`/toolkit/peak-shifting/${id}`)
export const createPeakShifting = (data: any) => request.post('/toolkit/peak-shifting', data)
export const addPeakShifting = createPeakShifting
export const updatePeakShifting = (id: number, data: any) => request.put(`/toolkit/peak-shifting/${id}`, data)
export const deletePeakShifting = (id: number) => request.delete(`/toolkit/peak-shifting/${id}`)

// --- 旧API兼容（电价管理） ---
export const getElectricityPriceList = (params?: any) => request.get('/toolkit/electricity-price/list', { params })
export const addElectricityPrice = (data: any) => request.post('/toolkit/electricity-price', data)
export const updateElectricityPrice = (id: number, data: any) => request.put(`/toolkit/electricity-price/${id}`, data)
export const setElectricityPriceStatus = (id: number, status: number) => request.put(`/toolkit/electricity-price/${id}/status`, { status })
export const deleteElectricityPrice = (id: number) => request.delete(`/toolkit/electricity-price/${id}`)
export interface ElectricityPrice { id: number; priceName: string; priceType: string; price: number; status: number }

// 削峰填谷配置
export const getPeakShiftingConfigList = (params?: any) => request.get('/toolkit/peak-shifting/config/list', { params })
export const addPeakShiftingConfig = (data: any) => request.post('/toolkit/peak-shifting/config', data)
export const updatePeakShiftingConfig = (id: number, data: any) => request.put(`/toolkit/peak-shifting/config/${id}`, data)
export const deletePeakShiftingConfig = (id: number) => request.delete(`/toolkit/peak-shifting/config/${id}`)
export const getPeakShiftingSuggestion = () => request.get('/toolkit/peak-shifting/suggestion')
export interface PeakShifting { id: number; planName: string; planDate: string; status: number }
export interface PeakShiftingConfig { id: number; configName: string; peakHours: string; offPeakHours: string }

// --- 旧API兼容（绿色工厂详细） ---
export const uploadGreenFactoryItem = (data: any) => request.post('/toolkit/green-factory/item', data)
export const submitGreenFactory = (id: number) => request.put(`/toolkit/green-factory/${id}/submit`)
export interface GreenFactory { id: number; factoryName: string; assessmentYear: number; score: number; level: string; status: number }
