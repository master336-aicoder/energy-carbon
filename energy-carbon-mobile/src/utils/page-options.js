/**
 * H5页面参数获取工具（兼容Edge等浏览器）
 * uni-app H5模式下，onLoad在某些浏览器中可能不触发
 */

/**
 * 从URL hash中解析查询参数
 */
export const getHashQuery = () => {
  const query = {}
  try {
    const hash = window.location.hash || ''
    const queryIndex = hash.indexOf('?')
    if (queryIndex === -1) return query
    const search = hash.substring(queryIndex + 1)
    const params = new URLSearchParams(search)
    params.forEach((value, key) => {
      query[key] = decodeURIComponent(value)
    })
  } catch (e) {
    console.warn('解析URL参数失败:', e)
  }
  return query
}

/**
 * 获取页面指定参数
 */
export const getPageParam = (key) => {
  const query = getHashQuery()
  return query[key] || null
}

/**
 * 页面参数合并：优先使用onLoad传入的options，其次从URL解析
 */
export const mergePageOptions = (options = {}) => {
  const hashQuery = getHashQuery()
  return { ...hashQuery, ...options }
}
