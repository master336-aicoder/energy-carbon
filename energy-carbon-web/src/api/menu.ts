import request from '@/utils/request'
import type { Result } from '@/types'
import type { MenuApi } from '@/types/api'

/** 获取菜单列表 */
export function getMenuList(params?: MenuApi.ListParams) {
  return request.get<MenuApi.ListResult>('/system/menu/list', { params })
}

/** 获取菜单详情 */
export function getMenuDetail(id: number) {
  return request.get<MenuApi.DetailResult>(`/system/menu/${id}`)
}

/** 新增菜单 */
export function createMenu(data: MenuApi.SaveParams) {
  return request.post<MenuApi.SaveResult>('/system/menu', data)
}

/** 修改菜单 */
export function updateMenu(data: MenuApi.SaveParams) {
  return request.put<MenuApi.SaveResult>('/system/menu', data)
}

/** 删除菜单 */
export function deleteMenu(id: number) {
  return request.delete<MenuApi.DeleteResult>(`/system/menu/${id}`)
}

/** 获取当前用户菜单 */
export function getUserMenus() {
  return request.get<MenuApi.UserMenuResult>('/system/menu/user')
}

/** 获取角色菜单ID列表 */
export function getRoleMenuIds(roleId: number) {
  return request.get<Result<number[]>>(`/system/menu/role/${roleId}`)
}

/** 获取菜单树（用于下拉选择） */
export function getMenuTree(params?: MenuApi.ListParams) {
  return request.get<MenuApi.ListResult>('/system/menu/tree', { params })
}
