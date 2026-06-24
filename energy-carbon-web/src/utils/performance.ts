/**
 * 性能监控工具
 * 统计页面加载时间、接口响应时间
 */

// 页面加载性能指标
interface PerformanceMetrics {
  name: string
  startTime: number
  endTime?: number
  duration?: number
}

// 接口响应时间统计
interface ApiMetrics {
  url: string
  method: string
  startTime: number
  endTime?: number
  duration?: number
  status?: number
}

class PerformanceMonitor {
  private pageMetrics: PerformanceMetrics[] = []
  private apiMetrics: ApiMetrics[] = []
  private maxRecords = 100

  /**
   * 开始记录页面加载
   */
  startPageLoad(name: string): void {
    this.pageMetrics.push({
      name,
      startTime: performance.now()
    })
    // 限制记录数量
    if (this.pageMetrics.length > this.maxRecords) {
      this.pageMetrics.shift()
    }
  }

  /**
   * 结束记录页面加载
   */
  endPageLoad(name: string): number | null {
    const metric = [...this.pageMetrics].reverse().find(m => m.name === name && !m.endTime)
    if (metric) {
      metric.endTime = performance.now()
      metric.duration = metric.endTime - metric.startTime
      return metric.duration
    }
    return null
  }

  /**
   * 开始记录接口请求
   */
  startApiRequest(url: string, method: string): void {
    this.apiMetrics.push({
      url,
      method,
      startTime: performance.now()
    })
    if (this.apiMetrics.length > this.maxRecords) {
      this.apiMetrics.shift()
    }
  }

  /**
   * 结束记录接口请求
   */
  endApiRequest(url: string, status: number): number | null {
    const metric = [...this.apiMetrics].reverse().find(m => m.url === url && !m.endTime)
    if (metric) {
      metric.endTime = performance.now()
      metric.duration = metric.endTime - metric.startTime
      metric.status = status

      // 慢请求告警（超过3秒）
      if (metric.duration > 3000) {
        console.warn(`[性能告警] 慢接口: ${metric.method} ${url} - ${metric.duration.toFixed(0)}ms`)
      }

      return metric.duration
    }
    return null
  }

  /**
   * 获取页面加载统计
   */
  getPageMetrics(): PerformanceMetrics[] {
    return this.pageMetrics.filter(m => m.duration !== undefined)
  }

  /**
   * 获取接口响应统计
   */
  getApiMetrics(): ApiMetrics[] {
    return this.apiMetrics.filter(m => m.duration !== undefined)
  }

  /**
   * 获取平均接口响应时间
   */
  getAverageApiTime(): number {
    const completed = this.getApiMetrics()
    if (completed.length === 0) return 0
    const total = completed.reduce((sum, m) => sum + (m.duration || 0), 0)
    return total / completed.length
  }

  /**
   * 获取浏览器性能指标（Navigation Timing）
   */
  getNavigationTiming(): Record<string, number> | null {
    const timing = performance.getEntriesByType('navigation')[0] as PerformanceNavigationTiming
    if (!timing) return null

    return {
      dns: timing.domainLookupEnd - timing.domainLookupStart,
      tcp: timing.connectEnd - timing.connectStart,
      ttfb: timing.responseStart - timing.requestStart,
      download: timing.responseEnd - timing.responseStart,
      domReady: timing.domContentLoadedEventEnd - timing.startTime,
      loadComplete: timing.loadEventEnd - timing.startTime
    }
  }

  /**
   * 打印性能报告
   */
  printReport(): void {
    console.group('[性能报告]')

    // 页面加载
    const pageMetrics = this.getPageMetrics()
    if (pageMetrics.length > 0) {
      console.table(pageMetrics.map(m => ({
        页面: m.name,
        耗时: `${(m.duration || 0).toFixed(0)}ms`
      })))
    }

    // 接口响应
    const apiMetrics = this.getApiMetrics()
    if (apiMetrics.length > 0) {
      console.table(apiMetrics.map(m => ({
        接口: `${m.method} ${m.url}`,
        耗时: `${(m.duration || 0).toFixed(0)}ms`,
        状态: m.status
      })))
    }

    // 平均响应时间
    console.log(`平均接口响应时间: ${this.getAverageApiTime().toFixed(0)}ms`)

    // 浏览器性能
    const navTiming = this.getNavigationTiming()
    if (navTiming) {
      console.log('浏览器性能指标:', navTiming)
    }

    console.groupEnd()
  }

  /**
   * 清除所有记录
   */
  clear(): void {
    this.pageMetrics = []
    this.apiMetrics = []
  }
}

export const performanceMonitor = new PerformanceMonitor()
export default performanceMonitor
