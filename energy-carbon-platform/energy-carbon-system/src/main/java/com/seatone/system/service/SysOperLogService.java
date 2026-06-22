package com.seatone.system.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.seatone.system.entity.SysOperLog;

public interface SysOperLogService extends IService<SysOperLog> {

    void clearLog();
}
