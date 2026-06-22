package com.seatone.common.result;

import lombok.Data;
import java.util.List;

/**
 * 分页查询结果封装类
 * 用于统一封装分页查询的返回数据结构
 *
 * @param <T> 分页数据记录类型
 * @author seatone
 * @since 2026-06-22
 */
@Data
public class PageResult<T> {

    /**
     * 当前页码
     */
    private Long current;

    /**
     * 每页显示条数
     */
    private Long size;

    /**
     * 总记录数
     */
    private Long total;

    /**
     * 总页数
     */
    private Long pages;

    /**
     * 当前页数据记录列表
     */
    private List<T> records;

    /**
     * 构建分页结果
     *
     * @param current 当前页码
     * @param size    每页条数
     * @param total   总记录数
     * @param records 当前页数据列表
     * @param <T>     数据类型
     * @return 分页结果对象
     */
    public static <T> PageResult<T> of(long current, long size, long total, List<T> records) {
        PageResult<T> result = new PageResult<>();
        result.setCurrent(current);
        result.setSize(size);
        result.setTotal(total);
        result.setRecords(records);
        result.setPages((total + size - 1) / size);
        return result;
    }
}
