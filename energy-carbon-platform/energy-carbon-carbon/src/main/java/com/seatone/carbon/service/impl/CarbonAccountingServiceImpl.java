package com.seatone.carbon.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.seatone.carbon.entity.CarbonAccounting;
import com.seatone.carbon.mapper.CarbonAccountingMapper;
import com.seatone.carbon.service.CarbonAccountingService;
import org.springframework.stereotype.Service;

/**
 * 碳排放核算服务实现类
 * 继承 MyBatis-Plus ServiceImpl 提供基础CRUD能力
 * 碳排放核算的业务逻辑主要在 CarbonAccountingController 中处理
 *
 * @author seatone
 * @since 2026-06-22
 */
@Service
public class CarbonAccountingServiceImpl extends ServiceImpl<CarbonAccountingMapper, CarbonAccounting> implements CarbonAccountingService {
}
