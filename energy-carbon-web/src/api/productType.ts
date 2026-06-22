import request from '@/utils/request'
import type { Result, PageResult, ProductType } from '@/types'

/** 产品种类列表查询参数 */
export interface ProductTypeListParams {
  current?: number
  size?: number
  productName?: string
  productCategory?: string
  status?: number
}

/** 产品种类保存参数 */
export interface ProductTypeSaveParams {
  id?: number
  productCode: string
  productName: string
  productCategory?: string
  unit: string
  carbonFactor?: number
  status: number
  remark?: string
}

/** 获取产品种类列表 */
export function getProductTypeList(params: ProductTypeListParams) {
  return request.get<Result<PageResult<ProductType>>>('/monitor/productType/list', { params })
}

/** 获取产品种类详情 */
export function getProductTypeDetail(id: number) {
  return request.get<Result<ProductType>>(`/monitor/productType/${id}`)
}

/** 新增产品种类 */
export function createProductType(data: ProductTypeSaveParams) {
  return request.post<Result<ProductType>>('/monitor/productType', data)
}

/** 修改产品种类 */
export function updateProductType(id: number, data: ProductTypeSaveParams) {
  return request.put<Result<ProductType>>(`/monitor/productType/${id}`, data)
}

/** 删除产品种类 */
export function deleteProductType(id: number) {
  return request.delete<Result<null>>(`/monitor/productType/${id}`)
}

/** 导出产品种类 */
export function exportProductType(params: ProductTypeListParams) {
  return request.get('/monitor/productType/export', {
    params,
    responseType: 'blob'
  })
}

/** 导入产品种类 */
export function importProductType(dataList: ProductTypeSaveParams[]) {
  return request.post<Result<string>>('/monitor/productType/import', dataList)
}

/** 修改产品种类状态 */
export function updateProductTypeStatus(id: number, status: number) {
  return request.put<Result<null>>(`/monitor/productType/${id}/status?status=${status}`)
}
