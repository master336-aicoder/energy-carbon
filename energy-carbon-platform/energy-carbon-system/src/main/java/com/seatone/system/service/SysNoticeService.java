package com.seatone.system.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.seatone.system.entity.SysNotice;

import java.util.List;

/**
 * 通知公告 Service
 *
 * @author seatone
 * @since 2026-06-27
 */
public interface SysNoticeService extends IService<SysNotice> {

    /**
     * 查询启用的公告列表
     *
     * @return 公告列表
     */
    List<SysNotice> getEnabledList();

    /**
     * 查询该用户未读的启用公告
     *
     * @param userId 用户ID
     * @return 未读公告列表
     */
    List<SysNotice> getUnreadList(Long userId);

    /**
     * 统计该用户未读公告数
     *
     * @param userId 用户ID
     * @return 未读公告数量
     */
    int countUnread(Long userId);

    /**
     * 标记某公告为已读（如果已存在则忽略）
     *
     * @param noticeId 公告ID
     * @param userId   用户ID
     */
    void markAsRead(Long noticeId, Long userId);
}
