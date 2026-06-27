import request from '@/utils/request'

// 碳市场交易
export const getMarketList = (params?: any) => request.get('/carbon/market/list', { params })
export const getMarketDetail = (id: number) => request.get(`/carbon/market/${id}`)
export const createMarket = (data: any) => request.post('/carbon/market', data)
export const updateMarket = (id: number, data: any) => request.put(`/carbon/market/${id}`, data)
export const deleteMarket = (id: number) => request.delete(`/carbon/market/${id}`)

// 碳排放报告
export const getCarbonReportList = (params?: any) => request.get('/carbon/report/list', { params })
export const getCarbonReportDetail = (id: number) => request.get(`/carbon/report/${id}`)
export const createCarbonReport = (data: any) => request.post('/carbon/report', data)
export const updateCarbonReport = (id: number, data: any) => request.put(`/carbon/report/${id}`, data)
export const deleteCarbonReport = (id: number) => request.delete(`/carbon/report/${id}`)

// 供应链碳足迹
export const getSupplyChainList = (params?: any) => request.get('/carbon/supply-chain/list', { params })
export const getSupplyChainDetail = (id: number) => request.get(`/carbon/supply-chain/${id}`)
export const createSupplyChain = (data: any) => request.post('/carbon/supply-chain', data)
export const updateSupplyChain = (id: number, data: any) => request.put(`/carbon/supply-chain/${id}`, data)
export const deleteSupplyChain = (id: number) => request.delete(`/carbon/supply-chain/${id}`)

// 碳目标管理
export const getTargetList = (params?: any) => request.get('/carbon/target/list', { params })
export const getTargetDetail = (id: number) => request.get(`/carbon/target/${id}`)
export const createTarget = (data: any) => request.post('/carbon/target', data)
export const updateTarget = (id: number, data: any) => request.put(`/carbon/target/${id}`, data)
export const deleteTarget = (id: number) => request.delete(`/carbon/target/${id}`)

// 碳核查
export const getVerificationList = (params?: any) => request.get('/carbon/verification/list', { params })
export const getVerificationDetail = (id: number) => request.get(`/carbon/verification/${id}`)
export const createVerification = (data: any) => request.post('/carbon/verification', data)
export const updateVerification = (id: number, data: any) => request.put(`/carbon/verification/${id}`, data)
export const deleteVerification = (id: number) => request.delete(`/carbon/verification/${id}`)

// --- 旧API兼容（碳因子管理） ---
export const getFactorList = (params?: any) => request.get('/carbon/factor/list', { params })
export const createFactor = (data: any) => request.post('/carbon/factor', data)
export const updateFactor = (id: number, data: any) => request.put(`/carbon/factor/${id}`, data)
export const deleteFactor = (id: number) => request.delete(`/carbon/factor/${id}`)
export const setDefaultFactor = (id: number) => request.put(`/carbon/factor/${id}/default`)

// --- 旧API兼容（碳足迹） ---
export const getFootprintList = (params?: any) => request.get('/carbon/footprint/list', { params })
export const createFootprint = (data: any) => request.post('/carbon/footprint', data)
export const updateFootprint = (id: number, data: any) => request.put(`/carbon/footprint/${id}`, data)
export const deleteFootprint = (id: number) => request.delete(`/carbon/footprint/${id}`)

// CBAM 碳边境调节机制
export const getCbamList = (params?: any) => request.get('/carbon/cbam/list', { params })
export const getCbamDetail = (id: number) => request.get(`/carbon/cbam/${id}`)
export const createCbam = (data: any) => request.post('/carbon/cbam', data)
export const updateCbam = (id: number, data: any) => request.put(`/carbon/cbam/${id}`, data)
export const deleteCbam = (id: number) => request.delete(`/carbon/cbam/${id}`)
export const submitCbam = (id: number) => request.put(`/carbon/cbam/${id}/submit`)

// --- 旧API兼容（碳核算） ---
export const getAccountingList = (params?: any) => request.get('/carbon/accounting/list', { params })
export const createAccounting = (data: any) => request.post('/carbon/accounting', data)
export const deleteAccounting = (id: number) => request.delete(`/carbon/accounting/${id}`)
export const submitAccounting = (id: number) => request.put(`/carbon/accounting/${id}/submit`)
export const approveAccounting = (id: number) => request.put(`/carbon/accounting/${id}/approve`)
export const getAccountingDetail = (id: number) => request.get(`/carbon/accounting/${id}`)
export const getEmissionReport = (params?: any) => request.get('/carbon/accounting/report', { params })
export const calculateEmission = (data: any) => request.post('/carbon/accounting/calculate', data)
