package com.seatone.carbon.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.seatone.carbon.entity.CbamDeclaration;
import com.seatone.carbon.mapper.CbamDeclarationMapper;
import com.seatone.carbon.service.CbamDeclarationService;
import org.springframework.stereotype.Service;

@Service
public class CbamDeclarationServiceImpl extends ServiceImpl<CbamDeclarationMapper, CbamDeclaration> implements CbamDeclarationService {
}
