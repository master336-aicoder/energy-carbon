package com.seatone.common.base;

import lombok.Data;

/**
 * 基础查询参数
 * 包含数据权限 SQL 条件字段
 */
@Data
public class BaseQuery {

    /**
     * 数据权限 SQL 条件
     * 由 DataScopeAspect 自动拼接
     */
    private String dataScope;

    /**
     * 页码
     */
    private Integer pageNum = 1;

    /**
     * 每页大小
     */
    private Integer pageSize = 10;

    /**
     * 排序字段
     */
    private String orderByColumn;

    /**
     * 排序方向（asc/desc）
     */
    private String isAsc = "asc";
}
