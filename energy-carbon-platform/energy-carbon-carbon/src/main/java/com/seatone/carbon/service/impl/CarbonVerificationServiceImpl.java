package com.seatone.carbon.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.seatone.carbon.entity.CarbonVerification;
import com.seatone.carbon.mapper.CarbonVerificationMapper;
import com.seatone.carbon.service.CarbonVerificationService;
import org.springframework.stereotype.Service;

@Service
public class CarbonVerificationServiceImpl extends ServiceImpl<CarbonVerificationMapper, CarbonVerification> implements CarbonVerificationService {
}