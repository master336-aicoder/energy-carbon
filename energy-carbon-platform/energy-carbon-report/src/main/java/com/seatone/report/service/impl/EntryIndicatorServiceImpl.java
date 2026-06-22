package com.seatone.report.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.seatone.report.entity.EntryIndicator;
import com.seatone.report.mapper.EntryIndicatorMapper;
import com.seatone.report.service.EntryIndicatorService;
import org.springframework.stereotype.Service;

@Service
public class EntryIndicatorServiceImpl extends ServiceImpl<EntryIndicatorMapper, EntryIndicator> implements EntryIndicatorService {
}