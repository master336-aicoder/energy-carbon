package com.seatone.monitor.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("toolkit_green_factory_item")
public class ToolkitGreenFactoryItem {

    private Long id;

    private Long declarationId;

    private String itemName;

    private String itemCategory;

    private Integer required;

    private Integer uploaded;

    private String filePath;

    private LocalDateTime uploadTime;

    private String remark;
}
