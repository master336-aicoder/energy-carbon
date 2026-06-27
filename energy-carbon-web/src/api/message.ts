import request from '@/utils/request'
import type { Result } from '@/types'

/** 获取我的消息列表（分页） */
export function getMessageList(params?: any) {
  return request.get('/system/message/list', { params })
}

/** 获取消息详情 */
export function getMessageDetail(id: number) {
  return request.get(`/system/message/${id}`)
}

/** 获取未读消息数量 */
export function getUnreadMessageCount() {
  return request.get<Result<number>>('/system/message/unread/count')
}

/** 标记消息已读 */
export function markMessageRead(id: number) {
  return request.post(`/system/message/${id}/read`)
}
