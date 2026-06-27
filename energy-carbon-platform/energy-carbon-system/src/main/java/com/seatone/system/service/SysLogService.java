package com.seatone.system.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.seatone.system.entity.SysLog;

/**
 * 操作日志 Service 接口
 *
 * @author seatone
 * @since 2026-06-27
 */
public interface SysLogService extends IService<SysLog> {

    /**
     * 新增操作日志
     *
     * @param log 日志对象
     */
    void addLog(SysLog log);

    /**
     * 分页查询操作日志
     *
     * @param current      页码
     * @param size         每页条数
     * @param operName     操作人
     * @param title        操作模块
     * @param businessType 业务类型
     * @param status       操作状态
     * @param startTime    开始时间
     * @param endTime      结束时间
     * @return 分页结果
     */
    com.baomidou.mybatisplus.extension.plugins.pagination.Page<SysLog> listLogs(
            Long current, Long size, String operName, String title,
            Integer businessType, Integer status, String startTime, String endTime);
}
