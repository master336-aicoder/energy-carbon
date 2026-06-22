import request from '@/utils/request'
import type { Result, PageResult, Role } from '@/types'
import type { RoleApi } from '@/types/api'

/** 获取角色列表 */
export function getRoleList(params: RoleApi.ListParams) {
  return request.get<RoleApi.ListResult>('/system/role/list', { params })
}

/** 获取角色详情 */
export function getRoleDetail(id: number) {
  return request.get<RoleApi.DetailResult>(`/system/role/${id}`)
}

/** 新增角色 */
export function createRole(data: RoleApi.SaveParams) {
  return request.post<RoleApi.SaveResult>('/system/role', data)
}

/** 修改角色 */
export function updateRole(data: RoleApi.SaveParams) {
  return request.put<RoleApi.SaveResult>('/system/role', data)
}

/** 删除角色 */
export function deleteRole(id: number) {
  return request.delete<RoleApi.DeleteResult>(`/system/role/${id}`)
}

/** 批量删除角色 */
export function batchDeleteRole(ids: number[]) {
  return request.delete<Result<null>>('/system/role/batch', { data: ids })
}

/** 分配菜单 */
export function assignRoleMenus(data: RoleApi.AssignMenuParams) {
  return request.post<Result<null>>('/system/role/menus', data)
}

/** 获取角色权限 */
export function getRolePermissions(roleId: number) {
  return request.get<RoleApi.PermissionResult>(`/system/role/permissions/${roleId}`)
}

/** 分配权限 */
export function assignRolePermissions(roleId: number, permissions: string[]) {
  return request.post<Result<null>>('/system/role/permissions', { roleId, permissions })
}

/** 修改角色状态 */
export function updateRoleStatus(id: number, status: number) {
  return request.put<Result<null>>('/system/role/status', { id, status })
}

/** 获取所有角色（用于下拉选择） */
export function getAllRoles() {
  return request.get<Result<Role[]>>('/system/role/all')
}
