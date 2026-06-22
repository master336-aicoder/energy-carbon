/**
 * localStorage 封装
 */
export const local = {
  /**
   * 设置值
   * @param key 键
   * @param value 值
   */
  set<T>(key: string, value: T): void {
    try {
      const data = JSON.stringify(value)
      localStorage.setItem(key, data)
    } catch (e) {
      console.error('localStorage set error:', e)
    }
  },

  /**
   * 获取值
   * @param key 键
   * @returns 值或 null
   */
  get<T>(key: string): T | null {
    try {
      const data = localStorage.getItem(key)
      if (data === null) return null
      return JSON.parse(data) as T
    } catch (e) {
      console.error('localStorage get error:', e)
      return null
    }
  },

  /**
   * 移除值
   * @param key 键
   */
  remove(key: string): void {
    localStorage.removeItem(key)
  },

  /**
   * 清空
   */
  clear(): void {
    localStorage.clear()
  }
}

/**
 * sessionStorage 封装
 */
export const session = {
  /**
   * 设置值
   * @param key 键
   * @param value 值
   */
  set<T>(key: string, value: T): void {
    try {
      const data = JSON.stringify(value)
      sessionStorage.setItem(key, data)
    } catch (e) {
      console.error('sessionStorage set error:', e)
    }
  },

  /**
   * 获取值
   * @param key 键
   * @returns 值或 null
   */
  get<T>(key: string): T | null {
    try {
      const data = sessionStorage.getItem(key)
      if (data === null) return null
      return JSON.parse(data) as T
    } catch (e) {
      console.error('sessionStorage get error:', e)
      return null
    }
  },

  /**
   * 移除值
   * @param key 键
   */
  remove(key: string): void {
    sessionStorage.removeItem(key)
  },

  /**
   * 清空
   */
  clear(): void {
    sessionStorage.clear()
  }
}

/**
 * 存储键名常量
 */
export const StorageKeys = {
  /** 访问令牌 */
  TOKEN: 'token',
  /** 刷新令牌 */
  REFRESH_TOKEN: 'refreshToken',
  /** 用户信息 */
  USER_INFO: 'userInfo',
  /** 用户菜单 */
  USER_MENUS: 'userMenus',
  /** 用户权限 */
  USER_PERMISSIONS: 'userPermissions',
  /** 主题 */
  THEME: 'theme',
  /** 语言 */
  LANGUAGE: 'language',
  /** 侧边栏折叠状态 */
  SIDEBAR_COLLAPSED: 'sidebarCollapsed',
  /** 标签页 */
  TABS: 'tabs',
  /** 记住我 */
  REMEMBER_ME: 'rememberMe'
} as const
