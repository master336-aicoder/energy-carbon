package com.seatone.system.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 通知公告已读记录实体
 *
 * @author seatone
 * @since 2026-06-27
 */
@Data
@TableName("sys_notice_read")
public class SysNoticeRead {

    /**
     * 主键ID，自增
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 公告ID
     */
    private Long noticeId;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 阅读时间
     */
    private LocalDateTime readTime;
}
