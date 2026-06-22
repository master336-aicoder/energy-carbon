package com.seatone.monitor.service;

import com.seatone.monitor.entity.AnalysisConsumption;
import com.seatone.monitor.mapper.AnalysisConsumptionMapper;
import com.seatone.monitor.service.impl.AnalysisConsumptionServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * 能耗分析服务单元测试
 * 测试能耗汇总计算、分组统计等功能
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("能耗分析服务测试")
class AnalysisConsumptionServiceTest {

    @Mock
    private AnalysisConsumptionMapper analysisConsumptionMapper;

    @InjectMocks
    private AnalysisConsumptionServiceImpl analysisConsumptionService;

    private AnalysisConsumption testConsumption;

    @BeforeEach
    void setUp() {
        testConsumption = new AnalysisConsumption();
        testConsumption.setId(1L);
        testConsumption.setEnergyUnitId(1L);
        testConsumption.setMeterId(1L);
        testConsumption.setEnergyTypeId(1L);
        testConsumption.setStatDate(LocalDate.of(2026, 1, 15));
        testConsumption.setStatPeriod("DAY");
        testConsumption.setConsumption(new BigDecimal("2500.5000"));
        testConsumption.setUnit("kWh");
    }

    @Test
    @DisplayName("汇总消耗量 - 正常汇总")
    void testSumConsumption() {
        BigDecimal expectedSum = new BigDecimal("7500.1500");
        when(analysisConsumptionMapper.sumConsumption(anyLong(), anyString(), anyString(), anyString(), anyLong()))
                .thenReturn(expectedSum);

        BigDecimal result = analysisConsumptionService.sumConsumption(
                1L, "DAY", "2026-01-01", "2026-01-31", 1L);

        assertNotNull(result);
        assertEquals(0, expectedSum.compareTo(result));
        verify(analysisConsumptionMapper, times(1)).sumConsumption(anyLong(), anyString(), anyString(), anyString(), anyLong());
    }

    @Test
    @DisplayName("汇总消耗量 - 无数据")
    void testSumConsumption_NoData() {
        when(analysisConsumptionMapper.sumConsumption(anyLong(), anyString(), anyString(), anyString(), anyLong()))
                .thenReturn(null);

        BigDecimal result = analysisConsumptionService.sumConsumption(
                999L, "DAY", "2026-01-01", "2026-01-31", 999L);

        assertNull(result);
    }

    @Test
    @DisplayName("按能源品种分组统计")
    void testGroupByEnergyType() {
        Map<String, Object> group1 = new HashMap<>();
        group1.put("energyTypeId", 1L);
        group1.put("energyTypeName", "电力");
        group1.put("totalConsumption", new BigDecimal("5000.0000"));

        Map<String, Object> group2 = new HashMap<>();
        group2.put("energyTypeId", 2L);
        group2.put("energyTypeName", "天然气");
        group2.put("totalConsumption", new BigDecimal("1200.0000"));

        when(analysisConsumptionMapper.groupByEnergyType(anyLong(), anyString(), anyString(), anyString()))
                .thenReturn(Arrays.asList(group1, group2));

        List<Map<String, Object>> result = analysisConsumptionService.groupByEnergyType(
                1L, "MONTH", "2026-01-01", "2026-01-31");

        assertNotNull(result);
        assertEquals(2, result.size());
        verify(analysisConsumptionMapper, times(1)).groupByEnergyType(anyLong(), anyString(), anyString(), anyString());
    }

    @Test
    @DisplayName("按日期统计趋势")
    void testTrendByDate() {
        Map<String, Object> day1 = new HashMap<>();
        day1.put("statDate", "2026-01-01");
        day1.put("consumption", new BigDecimal("200.0000"));

        Map<String, Object> day2 = new HashMap<>();
        day2.put("statDate", "2026-01-02");
        day2.put("consumption", new BigDecimal("250.0000"));

        when(analysisConsumptionMapper.trendByDate(anyLong(), anyString(), anyString(), anyString(), anyLong()))
                .thenReturn(Arrays.asList(day1, day2));

        List<Map<String, Object>> result = analysisConsumptionService.trendByDate(
                1L, "DAY", "2026-01-01", "2026-01-31", 1L);

        assertNotNull(result);
        assertEquals(2, result.size());
    }

    @Test
    @DisplayName("获取同比数据")
    void testGetSamePeriodConsumption() {
        BigDecimal samePeriod = new BigDecimal("6800.0000");
        when(analysisConsumptionMapper.getSamePeriodConsumption(anyLong(), anyString(), anyString(), anyLong()))
                .thenReturn(samePeriod);

        BigDecimal result = analysisConsumptionService.getSamePeriodConsumption(
                1L, "2026-01-01", "2026-01-31", 1L);

        assertNotNull(result);
        assertEquals(0, samePeriod.compareTo(result));
    }

    @Test
    @DisplayName("获取环比数据")
    void testGetPreviousPeriodConsumption() {
        BigDecimal previousPeriod = new BigDecimal("7200.0000");
        when(analysisConsumptionMapper.getPreviousPeriodConsumption(anyLong(), anyString(), anyString(), anyLong()))
                .thenReturn(previousPeriod);

        BigDecimal result = analysisConsumptionService.getPreviousPeriodConsumption(
                1L, "MONTH", "2026-01-31", 1L);

        assertNotNull(result);
        assertEquals(0, previousPeriod.compareTo(result));
    }

    @Test
    @DisplayName("新增能耗数据")
    void testAddConsumption() {
        when(analysisConsumptionMapper.insert(any(AnalysisConsumption.class))).thenReturn(1);

        boolean result = analysisConsumptionService.save(testConsumption);

        assertTrue(result);
        verify(analysisConsumptionMapper, times(1)).insert(any(AnalysisConsumption.class));
    }

    @Test
    @DisplayName("能耗数据精度验证")
    void testConsumptionPrecision() {
        BigDecimal consumption = testConsumption.getConsumption();
        assertNotNull(consumption);
        assertTrue(consumption.compareTo(BigDecimal.ZERO) > 0);
    }
}
