package com.seatone.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.seatone.system.entity.SysUserMessage;
import com.seatone.system.mapper.SysUserMessageMapper;
import com.seatone.system.service.SysUserMessageService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

/**
 * 用户消息 Service 实现类
 *
 * @author seatone
 * @since 2026-06-27
 */
@Service
public class SysUserMessageServiceImpl extends ServiceImpl<SysUserMessageMapper, SysUserMessage> implements SysUserMessageService {

    @Override
    public Page<SysUserMessage> getList(Long userId, Long current, Long size, Integer readStatus) {
        Page<SysUserMessage> page = new Page<>(current, size);
        LambdaQueryWrapper<SysUserMessage> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysUserMessage::getUserId, userId);
        if (readStatus != null) {
            wrapper.eq(SysUserMessage::getReadStatus, readStatus);
        }
        wrapper.orderByDesc(SysUserMessage::getCreateTime);
        return this.page(page, wrapper);
    }

    @Override
    public int countUnread(Long userId) {
        return baseMapper.countUserUnread(userId);
    }

    @Override
    public SysUserMessage getByIdForUser(Long id, Long userId) {
        return baseMapper.selectByIdAndUser(id, userId);
    }

    @Override
    public void markRead(Long id, Long userId) {
        // 先查询消息是否存在且属于该用户
        SysUserMessage message = baseMapper.selectByIdAndUser(id, userId);
        if (message == null) {
            throw new RuntimeException("消息不存在");
        }
        // 如果已读则跳过
        if (message.getReadStatus() != null && message.getReadStatus() == 1) {
            return;
        }
        // 更新为已读
        message.setReadStatus(1);
        message.setReadTime(LocalDateTime.now());
        baseMapper.updateById(message);
    }
}
