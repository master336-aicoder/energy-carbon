package com.seatone.alarm.service;

import com.seatone.alarm.entity.AlarmThreshold;
import com.seatone.alarm.mapper.AlarmThresholdMapper;
import com.seatone.alarm.service.impl.AlarmThresholdServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * 报警阈值服务单元测试
 * 测试报警阈值的CRUD和业务逻辑
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("报警阈值服务测试")
class AlarmThresholdServiceTest {

    @Mock
    private AlarmThresholdMapper alarmThresholdMapper;

    @InjectMocks
    private AlarmThresholdServiceImpl alarmThresholdService;

    private AlarmThreshold testThreshold;

    @BeforeEach
    void setUp() {
        testThreshold = new AlarmThreshold();
        testThreshold.setId(1L);
        testThreshold.setThresholdName("电力消耗上限报警");
        testThreshold.setEnergyUnitId(1L);
        testThreshold.setMetricType("CONSUMPTION");
        testThreshold.setCompareType("GT");
        testThreshold.setThresholdValue(new BigDecimal("10000.0000"));
        testThreshold.setAlarmLevel("HIGH");
        testThreshold.setEnabled(1);
        testThreshold.setNotifyMethod("EMAIL");
        testThreshold.setRemark("超过10000kWh时触发报警");
        testThreshold.setCreateTime(LocalDateTime.now());
        testThreshold.setUpdateTime(LocalDateTime.now());
    }

    @Test
    @DisplayName("新增报警阈值")
    void testAddThreshold() {
        when(alarmThresholdMapper.insert(any(AlarmThreshold.class))).thenReturn(1);

        boolean result = alarmThresholdService.save(testThreshold);

        assertTrue(result);
        verify(alarmThresholdMapper, times(1)).insert(any(AlarmThreshold.class));
    }

    @Test
    @DisplayName("更新报警阈值")
    void testUpdateThreshold() {
        when(alarmThresholdMapper.updateById(any(AlarmThreshold.class))).thenReturn(1);

        testThreshold.setThresholdValue(new BigDecimal("12000.0000"));
        boolean result = alarmThresholdService.updateById(testThreshold);

        assertTrue(result);
        verify(alarmThresholdMapper, times(1)).updateById(any(AlarmThreshold.class));
    }

    @Test
    @DisplayName("删除报警阈值")
    void testDeleteThreshold() {
        when(alarmThresholdMapper.deleteById(1L)).thenReturn(1);

        boolean result = alarmThresholdService.removeById(1L);

        assertTrue(result);
        verify(alarmThresholdMapper, times(1)).deleteById(1L);
    }

    @Test
    @DisplayName("根据ID查询报警阈值")
    void testGetThresholdById() {
        when(alarmThresholdMapper.selectById(1L)).thenReturn(testThreshold);

        AlarmThreshold result = alarmThresholdService.getById(1L);

        assertNotNull(result);
        assertEquals("电力消耗上限报警", result.getThresholdName());
        assertEquals("CONSUMPTION", result.getMetricType());
        assertEquals("GT", result.getCompareType());
        assertEquals("HIGH", result.getAlarmLevel());
    }

    @Test
    @DisplayName("查询所有报警阈值")
    void testListAllThresholds() {
        AlarmThreshold threshold2 = new AlarmThreshold();
        threshold2.setId(2L);
        threshold2.setThresholdName("碳排放预警");
        threshold2.setMetricType("EMISSION");
        threshold2.setCompareType("GT");
        threshold2.setThresholdValue(new BigDecimal("500.0000"));
        threshold2.setAlarmLevel("MEDIUM");

        when(alarmThresholdMapper.selectList(any())).thenReturn(Arrays.asList(testThreshold, threshold2));

        List<AlarmThreshold> result = alarmThresholdService.list();

        assertNotNull(result);
        assertEquals(2, result.size());
    }

    @Test
    @DisplayName("报警阈值启用状态验证")
    void testThresholdEnabled() {
        assertEquals(1, testThreshold.getEnabled());
        assertTrue(testThreshold.getEnabled() == 1);
    }

    @Test
    @DisplayName("报警级别验证")
    void testAlarmLevelValidation() {
        String[] validLevels = {"LOW", "MEDIUM", "HIGH", "CRITICAL"};
        boolean isValid = Arrays.asList(validLevels).contains(testThreshold.getAlarmLevel());
        assertTrue(isValid);
    }

    @Test
    @DisplayName("比较类型验证")
    void testCompareTypeValidation() {
        String[] validTypes = {"GT", "GTE", "LT", "LTE", "EQ"};
        boolean isValid = Arrays.asList(validTypes).contains(testThreshold.getCompareType());
        assertTrue(isValid);
    }
}
