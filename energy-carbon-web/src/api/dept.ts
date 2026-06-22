import request from '@/utils/request'
import type { Result, Dept } from '@/types'
import type { DeptApi } from '@/types/api'

/** 获取部门列表 */
export function getDeptList(params?: DeptApi.ListParams) {
  return request.get<DeptApi.ListResult>('/system/dept/list', { params })
}

/** 获取部门详情 */
export function getDeptDetail(id: number) {
  return request.get<DeptApi.DetailResult>(`/system/dept/${id}`)
}

/** 新增部门 */
export function createDept(data: DeptApi.SaveParams) {
  return request.post<DeptApi.SaveResult>('/system/dept', data)
}

/** 修改部门 */
export function updateDept(data: DeptApi.SaveParams) {
  return request.put<DeptApi.SaveResult>('/system/dept', data)
}

/** 删除部门 */
export function deleteDept(id: number) {
  return request.delete<DeptApi.DeleteResult>(`/system/dept/${id}`)
}

/** 获取部门树（用于下拉选择） */
export function getDeptTree(params?: DeptApi.ListParams) {
  return request.get<DeptApi.ListResult>('/system/dept/tree', { params })
}

/** 修改部门状态 */
export function updateDeptStatus(id: number, status: number) {
  return request.put<Result<null>>('/system/dept/status', { id, status })
}

/** 获取部门用户列表 */
export function getDeptUsers(deptId: number) {
  return request.get<Result<Dept>>(`/system/dept/users/${deptId}`)
}
