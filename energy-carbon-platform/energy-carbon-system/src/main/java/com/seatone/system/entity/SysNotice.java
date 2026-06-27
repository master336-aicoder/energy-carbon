package com.seatone.system.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.seatone.common.base.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * 通知公告实体
 *
 * @author seatone
 * @since 2026-06-27
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys_notice")
public class SysNotice extends BaseEntity {

    /**
     * 公告标题
     */
    @NotBlank(message = "公告标题不能为空")
    private String noticeTitle;

    /**
     * 公告类型（1通知 2公告）
     */
    @NotNull(message = "公告类型不能为空")
    private Integer noticeType;

    /**
     * 公告内容（富文本HTML）
     */
    @NotBlank(message = "公告内容不能为空")
    private String noticeContent;

    /**
     * 状态（0停用 1启用）
     */
    private Integer status;

    /**
     * 备注
     */
    private String remark;
}
