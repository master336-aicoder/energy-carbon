package com.seatone.carbon.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.seatone.carbon.entity.CarbonAccountingDetail;
import com.seatone.carbon.mapper.CarbonAccountingDetailMapper;
import com.seatone.carbon.service.CarbonAccountingDetailService;
import org.springframework.stereotype.Service;

@Service
public class CarbonAccountingDetailServiceImpl extends ServiceImpl<CarbonAccountingDetailMapper, CarbonAccountingDetail> implements CarbonAccountingDetailService {
}