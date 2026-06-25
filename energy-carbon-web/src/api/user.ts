import request from '@/utils/request'
import type { Result } from '@/types'
import type { UserApi } from '@/types/api'

/** 获取用户列表 */
export function getUserList(params: UserApi.ListParams) {
  return request.get<UserApi.ListResult>('/system/user/list', { params })
}

/** 获取用户详情 */
export function getUserDetail(id: number) {
  return request.get<UserApi.DetailResult>(`/system/user/${id}`)
}

/** 新增用户 */
export function createUser(data: UserApi.SaveParams) {
  return request.post<UserApi.SaveResult>('/system/user', data)
}

/** 修改用户 */
export function updateUser(data: UserApi.SaveParams) {
  return request.put<UserApi.SaveResult>('/system/user', data)
}

/** 删除用户 */
export function deleteUser(id: number) {
  return request.delete<UserApi.DeleteResult>(`/system/user/${id}`)
}

/** 批量删除用户 */
export function batchDeleteUser(ids: number[]) {
  return request.delete<Result<null>>('/system/user/batch', { data: ids })
}

/** 重置密码 */
export function resetPassword(data: UserApi.ResetPasswordParams) {
  return request.post<UserApi.ResetPasswordResult>('/system/user/reset-password', data)
}

/** 修改用户状态 */
export function updateUserStatus(data: UserApi.UpdateStatusParams) {
  return request.put<Result<null>>('/system/user/status', data)
}

/** 导出用户 */
export function exportUser(params: UserApi.ListParams) {
  return request.get('/system/user/export', {
    params,
    responseType: 'blob'
  })
}

/** 导入用户模板下载 */
export function downloadUserTemplate() {
  return request.get('/system/user/template', {
    responseType: 'blob'
  })
}

/** 导入用户 */
export function importUser(file: File) {
  const formData = new FormData()
  formData.append('file', file)
  return request.post<Result<string>>('/system/user/import', formData, {
    headers: {
      'Content-Type': 'multipart/form-data'
    }
  })
}
