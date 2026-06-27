package com.seatone.system.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.seatone.common.base.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("ai_service_record")
public class AiServiceRecord extends BaseEntity {

    /**
     * 服务名称
     */
    @NotBlank(message = "服务名称不能为空")
    private String serviceName;

    /**
     * 联系人姓名
     */
    @NotBlank(message = "联系人姓名不能为空")
    private String contactName;

    /**
     * 联系人电话
     */
    @NotBlank(message = "联系人电话不能为空")
    private String contactPhone;

    /**
     * 预约日期
     */
    @NotNull(message = "预约日期不能为空")
    private LocalDate appointmentDate;

    /**
     * 状态（如：PENDING-待处理，CONFIRMED-已确认，CANCELLED-已取消）
     */
    private String status;

    /**
     * 备注
     */
    private String remark;
}
