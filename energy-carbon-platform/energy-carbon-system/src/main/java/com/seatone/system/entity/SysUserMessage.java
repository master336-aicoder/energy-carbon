package com.seatone.system.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 用户消息实体（个人信箱）
 *
 * @author seatone
 * @since 2026-06-27
 */
@Data
@TableName("sys_user_message")
public class SysUserMessage {

    /**
     * 主键ID，自增
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 通知公告ID
     */
    private Long noticeId;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 通知标题（冗余）
     */
    private String title;

    /**
     * 通知内容（冗余）
     */
    private String content;

    /**
     * 通知类型（1通知 2公告）
     */
    private Integer noticeType;

    /**
     * 已读状态（0未读 1已读）
     */
    private Integer readStatus;

    /**
     * 已读时间
     */
    private LocalDateTime readTime;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;
}
