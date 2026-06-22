package com.seatone.system.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.seatone.system.entity.SysOperLog;
import com.seatone.system.mapper.SysOperLogMapper;
import com.seatone.system.service.SysOperLogService;
import org.springframework.stereotype.Service;

@Service
public class SysOperLogServiceImpl extends ServiceImpl<SysOperLogMapper, SysOperLog> implements SysOperLogService {

    @Override
    public void clearLog() {
        baseMapper.delete(null);
    }
}
