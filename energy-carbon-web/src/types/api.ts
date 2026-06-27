import type { Result, PageResult, PageParams, User, Role, Dept, Menu } from './index'

/** 用户管理 API 类型 */
export namespace UserApi {
  export interface ListParams extends PageParams {
    username?: string
    nickname?: string
    status?: number
    deptId?: number
  }

  export type ListResult = Result<PageResult<User>>
  export type DetailResult = Result<User>
  export type SaveResult = Result<User>
  export type DeleteResult = Result<null>
  export type ResetPasswordResult = Result<null>

  export interface SaveParams {
    id?: number
    username: string
    nickname?: string
    password?: string
    email?: string
    phone?: string
    status: number
    deptId?: number
    roleIds?: number[]
  }

  export interface ResetPasswordParams {
    id: number
    newPassword: string
  }

  export interface UpdateStatusParams {
    id: number
    status: number
  }
}

/** 角色管理 API 类型 */
export namespace RoleApi {
  export interface ListParams extends PageParams {
    name?: string
    code?: string
    status?: number
  }

  export type ListResult = Result<PageResult<Role>>
  export type DetailResult = Result<Role>
  export type SaveResult = Result<Role>
  export type DeleteResult = Result<null>

  export interface SaveParams {
    id?: number
    name: string
    code: string
    description?: string
    status: number
    sort?: number
    menuIds?: number[]
    permissionIds?: number[]
  }

  export interface AssignMenuParams {
    roleId: number
    menuIds: number[]
  }

  export interface PermissionResult extends Result<string[]> {
    data: string[]
  }
}

/** 部门管理 API 类型 */
export namespace DeptApi {
  export interface ListParams {
    name?: string
    status?: number
  }

  export type ListResult = Result<Dept[]>
  export type DetailResult = Result<Dept>
  export type SaveResult = Result<Dept>
  export type DeleteResult = Result<null>

  export interface SaveParams {
    id?: number
    parentId: number
    name: string
    code?: string
    sort?: number
    status: number
  }
}

/** 菜单管理 API 类型 */
export namespace MenuApi {
  export interface ListParams {
    status?: number
    type?: string
  }

  export type ListResult = Result<Menu[]>
  export type DetailResult = Result<Menu>
  export type SaveResult = Result<Menu>
  export type DeleteResult = Result<null>

  export interface SaveParams {
    id?: number
    parentId: number
    name: string
    title: string
    path?: string
    component?: string
    icon?: string
    type: string
    permission?: string
    sort?: number
    status: number
    hidden?: boolean
    keepAlive?: boolean
    external?: boolean
    externalLink?: string
  }

  export type UserMenuResult = Result<Menu[]>
}

/** 认证 API 类型 */
export namespace AuthApi {
  export interface LoginParams {
    username: string
    password: string
    captcha?: string
    captchaKey?: string
  }

  export interface LoginResult extends Result<{
    accessToken: string
    refreshToken: string
    expiresIn: number
    tokenType: string
  }> {}

  export interface UserInfoResult extends Result<{
    id: number
    username: string
    nickname?: string
    avatar?: string
    email?: string
    phone?: string
    deptName?: string
    roles: string[]
    permissions: string[]
    menus: Menu[]
  }> {}

  export interface RefreshTokenResult extends Result<{
    accessToken: string
    refreshToken: string
    expiresIn: number
  }> {}

  export interface CaptchaResult extends Result<{
    captchaKey: string
    captchaImage: string
  }> {}
}

/** 操作日志 API 类型 */
export namespace LogApi {
  export interface LogItem {
    id: number
    title?: string
    businessType?: number
    requestMethod?: string
    operName?: string
    operUrl?: string
    operIp?: string
    operLocation?: string
    operParam?: string
    status?: number
    errorMsg?: string
    operTime?: string
    costTime?: number
    browser?: string
    os?: string
    referer?: string
    userAgent?: string
  }

  export interface ListParams extends PageParams {
    operName?: string
    title?: string
    businessType?: number
    status?: number
    startTime?: string
    endTime?: string
  }

  export type ListResult = Result<PageResult<LogItem>>
}

/** 通知公告 API 类型 */
export namespace NoticeApi {
  export interface NoticeItem {
    id: number
    noticeTitle: string
    noticeType?: number
    noticeContent?: string
    status: number
    remark?: string
    createTime?: string
    updateTime?: string
  }

  export interface ListParams extends PageParams {
    noticeTitle?: string
    noticeType?: number
    status?: number
  }

  export type ListResult = Result<PageResult<NoticeItem>>
  export type DetailResult = Result<NoticeItem>
  export type SaveResult = Result<NoticeItem>
  export type DeleteResult = Result<null>
  export type UnreadListResult = Result<NoticeItem[]>

  export interface SaveParams {
    id?: number
    noticeTitle: string
    noticeType?: number
    noticeContent?: string
    status: number
    remark?: string
  }
}
