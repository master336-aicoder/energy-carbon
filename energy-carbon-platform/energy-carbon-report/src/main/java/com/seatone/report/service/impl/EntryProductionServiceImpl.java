package com.seatone.report.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.seatone.report.entity.EntryProduction;
import com.seatone.report.mapper.EntryProductionMapper;
import com.seatone.report.service.EntryProductionService;
import org.springframework.stereotype.Service;

@Service
public class EntryProductionServiceImpl extends ServiceImpl<EntryProductionMapper, EntryProduction> implements EntryProductionService {
}