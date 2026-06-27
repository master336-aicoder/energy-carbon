package com.seatone.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.seatone.system.entity.SysNotice;
import com.seatone.system.entity.SysNoticeRead;
import com.seatone.system.mapper.SysNoticeMapper;
import com.seatone.system.mapper.SysNoticeReadMapper;
import com.seatone.system.service.SysNoticeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 通知公告 Service 实现类
 *
 * @author seatone
 * @since 2026-06-27
 */
@Service
public class SysNoticeServiceImpl extends ServiceImpl<SysNoticeMapper, SysNotice> implements SysNoticeService {

    @Autowired
    private SysNoticeReadMapper noticeReadMapper;

    @Override
    public List<SysNotice> getEnabledList() {
        return baseMapper.selectEnabledList();
    }

    @Override
    public List<SysNotice> getUnreadList(Long userId) {
        return noticeReadMapper.selectUnreadList(userId);
    }

    @Override
    public int countUnread(Long userId) {
        return noticeReadMapper.countUnread(userId);
    }

    @Override
    public void markAsRead(Long noticeId, Long userId) {
        // 查询是否已存在已读记录
        Integer count = noticeReadMapper.selectReadStatus(noticeId, userId);
        if (count != null && count > 0) {
            // 已存在则忽略
            return;
        }
        // 插入一条已读记录
        SysNoticeRead noticeRead = new SysNoticeRead();
        noticeRead.setNoticeId(noticeId);
        noticeRead.setUserId(userId);
        noticeRead.setReadTime(LocalDateTime.now());
        noticeReadMapper.insert(noticeRead);
    }
}
