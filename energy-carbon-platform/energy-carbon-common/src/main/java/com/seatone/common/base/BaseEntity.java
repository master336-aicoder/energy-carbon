package com.seatone.common.base;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 实体基类
 * 提供通用的实体字段，包括主键、创建时间、更新时间、创建人、更新人
 * 所有业务实体类应继承此类以获得通用字段支持
 *
 * @author seatone
 * @since 2026-06-22
 */
@Data
public class BaseEntity {

    /**
     * 主键ID，自增
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 创建时间，插入时自动填充
     */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    /**
     * 更新时间，插入和更新时自动填充
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    /**
     * 创建人ID，插入时自动填充
     */
    @TableField(fill = FieldFill.INSERT)
    private Long createBy;

    /**
     * 更新人ID，插入和更新时自动填充
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Long updateBy;
}
