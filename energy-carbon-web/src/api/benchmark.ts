import request from '@/utils/request'
import type { Result, PageResult } from '@/types'
import type { AnalysisBenchmark } from './analysis'

/** 对标库查询参数 */
export interface BenchmarkListParams {
  current?: number
  size?: number
  benchmarkName?: string
  industry?: string
}

/** 分页查询对标数据 */
export function getBenchmarkList(params: BenchmarkListParams) {
  return request.get<Result<PageResult<AnalysisBenchmark>>>('/monitor/analysis/benchmark/list', { params })
}

/** 查询行业基准 */
export function getBenchmarkByIndustry(industry: string) {
  return request.get<Result<AnalysisBenchmark>>(`/monitor/analysis/benchmark/industry/${industry}`)
}

/** 查询所有生效对标数据 */
export function getAllBenchmarks() {
  return request.get<Result<AnalysisBenchmark[]>>('/monitor/analysis/benchmark/all')
}

/** 新增对标数据 */
export function createBenchmark(data: Partial<AnalysisBenchmark>) {
  return request.post<Result<null>>('/monitor/analysis/benchmark', data)
}

/** 修改对标数据 */
export function updateBenchmark(id: number, data: Partial<AnalysisBenchmark>) {
  return request.put<Result<null>>(`/monitor/analysis/benchmark/${id}`, data)
}

/** 删除对标数据 */
export function deleteBenchmark(id: number) {
  return request.delete<Result<null>>(`/monitor/analysis/benchmark/${id}`)
}