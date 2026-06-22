package com.seatone.monitor.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.seatone.monitor.entity.ProductType;
import com.seatone.monitor.mapper.ProductTypeMapper;
import com.seatone.monitor.service.ProductTypeService;
import org.springframework.stereotype.Service;

@Service
public class ProductTypeServiceImpl extends ServiceImpl<ProductTypeMapper, ProductType> implements ProductTypeService {
}
