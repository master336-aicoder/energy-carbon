package com.seatone.system.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.seatone.system.entity.SysNoticeDispatch;

import java.util.List;

/**
 * 通知公告下发 Service
 *
 * @author seatone
 * @since 2026-06-27
 */
public interface SysNoticeDispatchService extends IService<SysNoticeDispatch> {

    /**
     * 下发通知公告到指定目标用户
     * 根据下发类型查询目标用户，为每个用户创建一条消息记录
     *
     * @param noticeId     通知公告ID
     * @param dispatchType 下发类型（1全部 2机构 3角色 4用户）
     * @param targetIds    目标ID列表（dispatchType为2时为机构ID，3时为角色ID，4时为用户ID）
     */
    void dispatch(Long noticeId, Integer dispatchType, List<Long> targetIds);
}
