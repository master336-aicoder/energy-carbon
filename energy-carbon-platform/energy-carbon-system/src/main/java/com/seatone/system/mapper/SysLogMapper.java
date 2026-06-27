package com.seatone.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.seatone.system.entity.SysLog;
import org.apache.ibatis.annotations.Mapper;

/**
 * 操作日志 Mapper
 *
 * @author seatone
 * @since 2026-06-27
 */
@Mapper
public interface SysLogMapper extends BaseMapper<SysLog> {
}
