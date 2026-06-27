package com.seatone.system.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.seatone.common.base.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 通知公告下发记录实体
 *
 * @author seatone
 * @since 2026-06-27
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys_notice_dispatch")
public class SysNoticeDispatch extends BaseEntity {

    /**
     * 通知公告ID
     */
    private Long noticeId;

    /**
     * 下发类型（1全部 2机构 3角色 4用户）
     */
    private Integer dispatchType;

    /**
     * 下发目标名称（如"全部"/"生产部,技术部"，逗号分隔）
     */
    private String targetName;
}
