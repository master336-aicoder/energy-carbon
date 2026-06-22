package com.seatone.common.result;

import lombok.Data;
import java.util.List;

@Data
public class PageResult<T> {

    private Long current;
    private Long size;
    private Long total;
    private Long pages;
    private List<T> records;

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
