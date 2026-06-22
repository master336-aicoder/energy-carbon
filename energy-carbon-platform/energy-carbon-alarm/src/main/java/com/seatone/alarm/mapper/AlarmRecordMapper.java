package com.seatone.alarm.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.seatone.alarm.entity.AlarmRecord;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface AlarmRecordMapper extends BaseMapper<AlarmRecord> {
}