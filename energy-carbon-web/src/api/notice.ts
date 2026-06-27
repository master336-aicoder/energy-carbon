import request from '@/utils/request'
import type { NoticeApi } from '@/types/api'

/** 获取公告列表（分页） */
export function getNoticeList(params?: NoticeApi.ListParams) {
  return request.get<NoticeApi.ListResult>('/system/notice/list', { params })
}

/** 获取公告详情 */
export function getNoticeDetail(id: number) {
  return request.get<NoticeApi.DetailResult>(`/system/notice/${id}`)
}

/** 新增公告 */
export function createNotice(data: NoticeApi.SaveParams) {
  return request.post<NoticeApi.SaveResult>('/system/notice', data)
}

/** 修改公告 */
export function updateNotice(id: number, data: NoticeApi.SaveParams) {
  return request.put<NoticeApi.SaveResult>(`/system/notice/${id}`, data)
}

/** 删除公告 */
export function deleteNotice(id: number) {
  return request.delete<NoticeApi.DeleteResult>(`/system/notice/${id}`)
}

/** 下发通知公告 */
export function dispatchNotice(noticeId: number, data: { dispatchType: number; targetIds: number[] }) {
  return request.post(`/system/notice/${noticeId}/dispatch`, data)
}

/** 获取下发记录列表 */
export function getDispatchList(params?: any) {
  return request.get('/system/notice/dispatch/list', { params })
}
