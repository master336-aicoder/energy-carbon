import request from '@/utils/request'
import type { Result, PageResult } from '@/types'

// ========== AI服务预约 ==========

export interface AiServiceRecord {
  id?: number
  serviceName: string
  serviceType: string
  applicant: string
  contact?: string
  description?: string
  expectedDate?: string
  status: string
  result?: string
  remark?: string
  createTime?: string
  updateTime?: string
}

export interface AiServiceParams {
  current?: number
  size?: number
  serviceType?: string
  status?: string
  applicant?: string
}

/** 分页查询服务预约列表 */
export function getAiServiceList(params: AiServiceParams) {
  return request.get<Result<PageResult<AiServiceRecord>>>('/ai/service/list', { params })
}

/** 新增服务预约 */
export function addAiService(data: AiServiceRecord) {
  return request.post<Result<null>>('/ai/service', data)
}

/** 取消预约 */
export function cancelAiService(id: number) {
  return request.put<Result<null>>(`/ai/service/${id}/cancel`)
}

/** 获取预约详情 */
export function getAiServiceDetail(id: number) {
  return request.get<Result<AiServiceRecord>>(`/ai/service/${id}`)
}

/** 更新预约状态 */
export function updateAiServiceStatus(id: number, params: { status: string; result?: string }) {
  return request.put<Result<null>>(`/ai/service/${id}/status`, null, { params })
}

/** 删除预约记录 */
export function deleteAiService(id: number) {
  return request.delete<Result<null>>(`/ai/service/${id}`)
}

// ========== AI知识库 ==========

export interface AiKnowledgeDoc {
  id?: number
  docName: string
  docType: string
  filePath?: string
  fileSize?: number
  summary?: string
  uploadUser?: string
  uploadTime?: string
  status?: number
  createTime?: string
}

export interface AiKnowledgeParams {
  current?: number
  size?: number
  docType?: string
  uploadUser?: string
}

/** 分页查询文档列表 */
export function getAiKnowledgeList(params: AiKnowledgeParams) {
  return request.get<Result<PageResult<AiKnowledgeDoc>>>('/ai/knowledge/list', { params })
}

/** 上传文档 */
export function uploadAiKnowledge(formData: FormData) {
  return request.post<Result<AiKnowledgeDoc>>('/ai/knowledge/upload', formData, {
    headers: { 'Content-Type': 'multipart/form-data' }
  })
}

/** 文档检索 */
export function searchAiKnowledge(keyword: string) {
  return request.get<Result<AiKnowledgeDoc[]>>('/ai/knowledge/search', { params: { keyword } })
}

/** 获取文档详情 */
export function getAiKnowledgeDetail(id: number) {
  return request.get<Result<AiKnowledgeDoc>>(`/ai/knowledge/${id}`)
}

/** 删除文档 */
export function deleteAiKnowledge(id: number) {
  return request.delete<Result<null>>(`/ai/knowledge/${id}`)
}

// ========== AI报告生成 ==========

export interface AiReportTask {
  id?: number
  reportName: string
  reportType: string
  templateId?: number
  templateName?: string
  paramsJson?: string
  status: string
  resultPath?: string
  errorMsg?: string
  progress?: number
  createTime?: string
  updateTime?: string
}

export interface AiReportParams {
  current?: number
  size?: number
  reportType?: string
  status?: string
}

/** 分页查询报告任务列表 */
export function getAiReportList(params: AiReportParams) {
  return request.get<Result<PageResult<AiReportTask>>>('/ai/report/list', { params })
}

/** 创建报告任务 */
export function createAiReport(data: AiReportTask) {
  return request.post<Result<AiReportTask>>('/ai/report', data)
}

/** 查询任务状态 */
export function getAiReportDetail(id: number) {
  return request.get<Result<AiReportTask>>(`/ai/report/${id}`)
}

/** 取消任务 */
export function cancelAiReport(id: number) {
  return request.put<Result<null>>(`/ai/report/${id}/cancel`)
}

/** 删除报告任务 */
export function deleteAiReport(id: number) {
  return request.delete<Result<null>>(`/ai/report/${id}`)
}

// ========== AI报告模板 ==========

export interface AiReportTemplate {
  id?: number
  templateName: string
  reportType: string
  templateContent?: string
  variables?: string
  status?: number
  createTime?: string
  updateTime?: string
}

/** 分页查询模板列表 */
export function getAiReportTemplateList(params: { current?: number; size?: number; reportType?: string }) {
  return request.get<Result<PageResult<AiReportTemplate>>>('/ai/report-template/list', { params })
}

/** 获取所有模板 */
export function getAllAiReportTemplates(reportType?: string) {
  return request.get<Result<AiReportTemplate[]>>('/ai/report-template/all', { params: { reportType } })
}

/** 获取模板详情 */
export function getAiReportTemplateDetail(id: number) {
  return request.get<Result<AiReportTemplate>>(`/ai/report-template/${id}`)
}

/** 新增模板 */
export function addAiReportTemplate(data: AiReportTemplate) {
  return request.post<Result<null>>('/ai/report-template', data)
}

/** 修改模板 */
export function updateAiReportTemplate(id: number, data: AiReportTemplate) {
  return request.put<Result<null>>(`/ai/report-template/${id}`, data)
}

/** 删除模板 */
export function deleteAiReportTemplate(id: number) {
  return request.delete<Result<null>>(`/ai/report-template/${id}`)
}
