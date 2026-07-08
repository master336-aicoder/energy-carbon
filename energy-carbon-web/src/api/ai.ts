import request from '@/utils/request'

// AI服务（使用 ai_service_record 表，对应后端 /ai/service-record 接口）
export const getAiServiceList = (params?: any) => request.get('/ai/service-record/list', { params })
export const getAiServiceDetail = (id: number) => request.get(`/ai/service-record/${id}`)
export const createAiService = (data: any) => request.post('/ai/service-record', data)
export const updateAiService = (id: number, data: any) => request.put(`/ai/service-record/${id}`, data)
export const deleteAiService = (id: number) => request.delete(`/ai/service-record/${id}`)

// 报告模板
export const getReportTemplateList = (params?: any) => request.get('/ai/report-template/list', { params })
export const getReportTemplateDetail = (id: number) => request.get(`/ai/report-template/${id}`)
export const createReportTemplate = (data: any) => request.post('/ai/report-template', data)
export const updateReportTemplate = (id: number, data: any) => request.put(`/ai/report-template/${id}`, data)
export const deleteReportTemplate = (id: number) => request.delete(`/ai/report-template/${id}`)

// --- 旧API兼容（AI知识库） ---
export const getAiKnowledgeList = (params?: any) => request.get('/ai/knowledge/list', { params })
export const uploadAiKnowledge = (data: any) => request.post('/ai/knowledge/upload', data)
export const deleteAiKnowledge = (id: number) => request.delete(`/ai/knowledge/${id}`)
export interface AiKnowledgeDoc { id: number; fileName: string; category: string; status: number; createTime: string }

// --- 旧API兼容（AI报告） ---
export const createAiReport = (data: any) => request.post('/ai/report', data)
export const getAiReportList = (params?: any) => request.get('/ai/report/list', { params })
export const cancelAiReport = (id: number) => request.put(`/ai/report/${id}/cancel`)
export const deleteAiReport = (id: number) => request.delete(`/ai/report/${id}`)
export const getAllAiReportTemplates = () => request.get('/ai/report-template/all')
export interface AiReportTask { id: number; taskName: string; status: number; createTime: string }
export interface AiReportTemplate { id: number; templateName: string; templateType: string }

// --- 旧API兼容（AI服务预约） ---
export const addAiService = (data: any) => request.post('/ai/service-record', data)
export const cancelAiService = (id: number) => request.put(`/ai/service-record/${id}/cancel`)
export const deleteAiServiceRecord = (id: number) => request.delete(`/ai/service-record/${id}`)
export interface AiServiceRecord { id: number; serviceName: string; status: number; createTime: string }


