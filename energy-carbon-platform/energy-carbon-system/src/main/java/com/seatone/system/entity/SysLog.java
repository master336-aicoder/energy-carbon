package com.seatone.system.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 操作日志实体
 *
 * @author seatone
 * @since 2026-06-27
 */
@Data
@TableName("sys_log")
public class SysLog {

    /**
     * 日志ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 操作模块标题
     */
    private String title;

    /**
     * 业务类型（0其他 1新增 2修改 3删除 4查询 5导出）
     */
    private Integer businessType;

    /**
     * 请求方法
     */
    private String method;

    /**
     * 请求方式（GET/POST/PUT/DELETE）
     */
    private String requestMethod;

    /**
     * 操作人员
     */
    private String operName;

    /**
     * 请求URL
     */
    private String operUrl;

    /**
     * 操作IP地址
     */
    private String operIp;

    /**
     * 操作地点
     */
    private String operLocation;

    /**
     * 请求参数
     */
    private String operParam;

    /**
     * 返回结果
     */
    private String jsonResult;

    /**
     * 操作状态（0正常 1异常）
     */
    private Integer status;

    /**
     * 错误消息
     */
    private String errorMsg;

    /**
     * 操作时间
     */
    private LocalDateTime operTime;

    /**
     * 消耗时间（毫秒）
     */
    private Long costTime;

    /**
     * 浏览器信息
     */
    private String browser;

    /**
     * 操作系统信息
     */
    private String os;

    /**
     * 来源页面（Referer）
     */
    private String referer;

    /**
     * 用户代理（User-Agent）
     */
    private String userAgent;
}
