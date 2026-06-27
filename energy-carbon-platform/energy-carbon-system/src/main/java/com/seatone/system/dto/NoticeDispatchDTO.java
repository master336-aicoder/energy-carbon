package com.seatone.system.dto;

import lombok.Data;

import java.util.List;

/**
 * 通知公告下发请求 DTO
 *
 * @author seatone
 * @since 2026-06-27
 */
@Data
public class NoticeDispatchDTO {

    /**
     * 下发类型（1全部 2机构 3角色 4用户）
     */
    private Integer dispatchType;

    /**
     * 目标ID列表
     * dispatchType=2时为机构ID，3时为角色ID，4时为用户ID
     */
    private List<Long> targetIds;
}
