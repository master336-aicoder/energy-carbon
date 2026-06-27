package com.seatone.system.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.seatone.system.entity.SysUserMessage;

/**
 * 用户消息 Service
 *
 * @author seatone
 * @since 2026-06-27
 */
public interface SysUserMessageService extends IService<SysUserMessage> {

    /**
     * 分页查询某用户的消息列表
     *
     * @param userId     用户ID
     * @param current    页码
     * @param size       每页条数
     * @param readStatus 已读状态（可选，null表示全部）
     * @return 分页消息列表
     */
    Page<SysUserMessage> getList(Long userId, Long current, Long size, Integer readStatus);

    /**
     * 统计某用户的未读消息数
     *
     * @param userId 用户ID
     * @return 未读消息数量
     */
    int countUnread(Long userId);

    /**
     * 查询某用户某条消息详情
     *
     * @param id     消息ID
     * @param userId 用户ID
     * @return 消息详情
     */
    SysUserMessage getByIdForUser(Long id, Long userId);

    /**
     * 标记消息为已读
     *
     * @param id     消息ID
     * @param userId 用户ID
     */
    void markRead(Long id, Long userId);
}
