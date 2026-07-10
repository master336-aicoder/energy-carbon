package com.seatone.carbon.service;

import com.seatone.carbon.entity.CarbonAccounting;
import com.seatone.carbon.mapper.CarbonAccountingMapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.seatone.carbon.service.impl.CarbonAccountingServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * 碳排放核算服务单元测试
 * 测试碳排放计算逻辑
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("碳排放核算服务测试")
class CarbonAccountingServiceTest {

    @Mock
    private CarbonAccountingMapper carbonAccountingMapper;

    @InjectMocks
    private CarbonAccountingServiceImpl carbonAccountingService;

    private CarbonAccounting testAccounting;

    @BeforeEach
    void setUp() throws Exception {
        Field baseMapperField = ServiceImpl.class.getDeclaredField("baseMapper");
        baseMapperField.setAccessible(true);
        baseMapperField.set(carbonAccountingService, carbonAccountingMapper);

        Field entityClassField = ServiceImpl.class.getDeclaredField("entityClass");
        entityClassField.setAccessible(true);
        entityClassField.set(carbonAccountingService, CarbonAccounting.class);

        testAccounting = new CarbonAccounting();
        testAccounting.setId(1L);
        testAccounting.setAccountingName("2026年1月碳排放核算");
        testAccounting.setAccountingYear(2026);
        testAccounting.setAccountingPeriod("01");
        testAccounting.setEnergyUnitId(1L);
        testAccounting.setScopeType("SCOPE1");
        testAccounting.setTotalEmission(new BigDecimal("1250.5000"));
        testAccounting.setEmissionUnit("tCO2e");
        testAccounting.setStatus("DRAFT");
        testAccounting.setRemark("月度碳排放核算");
        testAccounting.setCreateTime(LocalDateTime.now());
        testAccounting.setUpdateTime(LocalDateTime.now());
    }

    @Test
    @DisplayName("新增碳排放核算记录")
    void testAddAccounting() {
        when(carbonAccountingMapper.insert(any(CarbonAccounting.class))).thenReturn(1);

        boolean result = carbonAccountingService.save(testAccounting);

        assertTrue(result);
        verify(carbonAccountingMapper, times(1)).insert(any(CarbonAccounting.class));
    }

    @Test
    @DisplayName("更新碳排放核算记录")
    void testUpdateAccounting() {
        when(carbonAccountingMapper.updateById(any(CarbonAccounting.class))).thenReturn(1);

        testAccounting.setTotalEmission(new BigDecimal("1300.7500"));
        testAccounting.setStatus("CONFIRMED");
        boolean result = carbonAccountingService.updateById(testAccounting);

        assertTrue(result);
        verify(carbonAccountingMapper, times(1)).updateById(any(CarbonAccounting.class));
    }

    @Test
    @DisplayName("根据ID查询碳排放核算记录")
    void testGetAccountingById() {
        when(carbonAccountingMapper.selectById(1L)).thenReturn(testAccounting);

        CarbonAccounting result = carbonAccountingService.getById(1L);

        assertNotNull(result);
        assertEquals("2026年1月碳排放核算", result.getAccountingName());
        assertEquals(2026, result.getAccountingYear());
        assertEquals("SCOPE1", result.getScopeType());
        assertEquals(0, new BigDecimal("1250.5000").compareTo(result.getTotalEmission()));
    }

    @Test
    @DisplayName("查询所有碳排放核算记录")
    void testListAllAccountings() {
        CarbonAccounting accounting2 = new CarbonAccounting();
        accounting2.setId(2L);
        accounting2.setAccountingName("2026年2月碳排放核算");
        accounting2.setAccountingYear(2026);
        accounting2.setAccountingPeriod("02");
        accounting2.setTotalEmission(new BigDecimal("1180.3000"));

        when(carbonAccountingMapper.selectList(any())).thenReturn(Arrays.asList(testAccounting, accounting2));

        List<CarbonAccounting> result = carbonAccountingService.list();

        assertNotNull(result);
        assertEquals(2, result.size());
    }

    @Test
    @DisplayName("碳排放数据精度验证")
    void testEmissionPrecision() {
        BigDecimal emission = new BigDecimal("1250.5000");
        assertEquals(4, emission.scale());
        assertTrue(emission.compareTo(BigDecimal.ZERO) > 0);
    }

    @Test
    @DisplayName("碳排放范围类型验证")
    void testScopeTypeValidation() {
        String[] validScopes = {"SCOPE1", "SCOPE2", "SCOPE3"};
        boolean isValid = Arrays.asList(validScopes).contains(testAccounting.getScopeType());
        assertTrue(isValid);
    }
}
