package com.seatone.carbon.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.seatone.carbon.dto.CarbonAccountingDTO;
import com.seatone.carbon.dto.EmissionCalcDTO;
import com.seatone.carbon.dto.EmissionReportDTO;
import com.seatone.carbon.entity.CarbonAccounting;
import com.seatone.carbon.entity.CarbonAccountingDetail;
import com.seatone.carbon.entity.CarbonEmissionFactor;
import com.seatone.carbon.service.CarbonAccountingDetailService;
import com.seatone.carbon.service.CarbonAccountingService;
import com.seatone.carbon.service.CarbonEmissionFactorService;
import com.seatone.common.result.PageResult;
import com.seatone.common.result.Result;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.BeanUtils;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import org.mockito.ArgumentCaptor;

/**
 * 碳排放核算控制器单元测试
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("碳排放核算控制器测试")
class CarbonAccountingControllerTest {

    @Mock
    private CarbonAccountingService accountingService;

    @Mock
    private CarbonAccountingDetailService detailService;

    @Mock
    private CarbonEmissionFactorService factorService;

    @InjectMocks
    private CarbonAccountingController controller;

    private CarbonAccounting accounting;
    private CarbonAccountingDetail detail;

    @BeforeEach
    void setUp() {
        accounting = new CarbonAccounting();
        accounting.setId(1L);
        accounting.setAccountingName("2026年1月核算");
        accounting.setAccountingYear(2026);
        accounting.setAccountingPeriod("01");
        accounting.setEnergyUnitId(1L);
        accounting.setScopeType("范围一");
        accounting.setTotalEmission(new BigDecimal("100.0000"));
        accounting.setEmissionUnit("tCO2e");
        accounting.setStatus("DRAFT");

        detail = new CarbonAccountingDetail();
        detail.setId(1L);
        detail.setAccountingId(1L);
        detail.setActivityName("测试活动");
        detail.setActivityData(new BigDecimal("10"));
        detail.setActivityUnit("t");
        detail.setEmissionFactor(new BigDecimal("2.5"));
        detail.setFactorUnit("tCO2e/t");
        detail.setEmission(new BigDecimal("25.0000"));
        detail.setEmissionUnit("tCO2e");
        detail.setCategory("类别一：直接温室气体排放");
        detail.setCategoryCode("CATEGORY_1");
        detail.setScopeType("范围一");
        detail.setScopeTypeCode("SCOPE_1");
    }

    private boolean isOk(Result<?> result) {
        return result.getCode() != null && result.getCode() == 200;
    }

    @Test
    @DisplayName("分页查询核算记录")
    void testList() {
        Page<CarbonAccounting> page = new Page<>();
        page.setRecords(Collections.singletonList(accounting));
        page.setCurrent(1);
        page.setSize(10);
        page.setTotal(1);
        when(accountingService.page(any(Page.class), any(LambdaQueryWrapper.class))).thenReturn(page);

        Result<PageResult<CarbonAccountingDTO>> result = controller.list(1L, 10L, null, 2026, null, null);

        assertTrue(isOk(result));
        assertNotNull(result.getData());
        assertEquals(1, result.getData().getRecords().size());
    }

    @Test
    @DisplayName("分页查询-带全部筛选条件")
    void testListWithAllFilters() {
        Page<CarbonAccounting> page = new Page<>();
        page.setRecords(Collections.singletonList(accounting));
        page.setCurrent(1);
        page.setSize(10);
        page.setTotal(1);
        when(accountingService.page(any(Page.class), any(LambdaQueryWrapper.class))).thenReturn(page);

        Result<PageResult<CarbonAccountingDTO>> result = controller.list(1L, 10L, "核算", 2026, "01", "DRAFT");

        assertTrue(isOk(result));
        assertNotNull(result.getData());
        assertEquals(1, result.getData().getRecords().size());
    }

    @Test
    @DisplayName("根据ID查询核算详情")
    void testGetById() {
        when(accountingService.getById(1L)).thenReturn(accounting);
        when(detailService.list(any(LambdaQueryWrapper.class))).thenReturn(Collections.singletonList(detail));

        Result<CarbonAccountingDTO> result = controller.getById(1L);

        assertTrue(isOk(result));
        assertNotNull(result.getData());
        assertEquals("2026年1月核算", result.getData().getAccountingName());
        assertEquals(1, result.getData().getDetails().size());
    }

    @Test
    @DisplayName("根据ID查询-记录不存在")
    void testGetByIdNotFound() {
        when(accountingService.getById(1L)).thenReturn(null);

        Result<CarbonAccountingDTO> result = controller.getById(1L);

        assertFalse(isOk(result));
        assertEquals("核算记录不存在", result.getMessage());
    }

    @Test
    @DisplayName("新增核算记录")
    void testAdd() {
        CarbonAccountingDTO dto = new CarbonAccountingDTO();
        BeanUtils.copyProperties(accounting, dto);
        when(accountingService.save(any(CarbonAccounting.class))).thenReturn(true);

        Result<Void> result = controller.add(dto);

        assertTrue(isOk(result));
        verify(accountingService, times(1)).save(any(CarbonAccounting.class));
    }

    @Test
    @DisplayName("修改核算记录")
    void testUpdate() {
        CarbonAccountingDTO dto = new CarbonAccountingDTO();
        BeanUtils.copyProperties(accounting, dto);
        when(accountingService.updateById(any(CarbonAccounting.class))).thenReturn(true);

        Result<Void> result = controller.update(1L, dto);

        assertTrue(isOk(result));
        verify(accountingService, times(1)).updateById(any(CarbonAccounting.class));
    }

    @Test
    @DisplayName("删除核算记录")
    void testDelete() {
        when(accountingService.removeById(1L)).thenReturn(true);

        Result<Void> result = controller.delete(1L);

        assertTrue(isOk(result));
        verify(accountingService, times(1)).removeById(1L);
    }

    @Test
    @DisplayName("提交核算记录")
    void testSubmit() {
        when(accountingService.getById(1L)).thenReturn(accounting);
        when(accountingService.updateById(any(CarbonAccounting.class))).thenReturn(true);

        Result<Void> result = controller.submit(1L);

        assertTrue(isOk(result));
        assertEquals("SUBMITTED", accounting.getStatus());
    }

    @Test
    @DisplayName("提交核算记录-不存在")
    void testSubmitNotFound() {
        when(accountingService.getById(1L)).thenReturn(null);

        Result<Void> result = controller.submit(1L);

        assertFalse(isOk(result));
    }

    @Test
    @DisplayName("审核核算记录")
    void testApprove() {
        when(accountingService.getById(1L)).thenReturn(accounting);
        when(accountingService.updateById(any(CarbonAccounting.class))).thenReturn(true);

        Result<Void> result = controller.approve(1L);

        assertTrue(isOk(result));
        assertEquals("APPROVED", accounting.getStatus());
    }

    @Test
    @DisplayName("审核核算记录-不存在")
    void testApproveNotFound() {
        when(accountingService.getById(1L)).thenReturn(null);

        Result<Void> result = controller.approve(1L);

        assertFalse(isOk(result));
    }

    @Test
    @DisplayName("排放计算-使用指定因子")
    void testCalculateWithFactorId() {
        CarbonEmissionFactor factor = new CarbonEmissionFactor();
        factor.setFactorValue(new BigDecimal("2.0"));
        factor.setFactorUnit("tCO2e/t");

        EmissionCalcDTO calcDTO = new EmissionCalcDTO();
        calcDTO.setAccountingId(1L);
        EmissionCalcDTO.ActivityDataItem item = new EmissionCalcDTO.ActivityDataItem();
        item.setActivityName("测试");
        item.setActivityData(new BigDecimal("5"));
        item.setActivityUnit("t");
        item.setFactorId(1L);
        item.setCategory("类别一：直接温室气体排放");
        calcDTO.setActivities(Collections.singletonList(item));

        when(accountingService.getById(1L)).thenReturn(accounting);
        when(factorService.getById(1L)).thenReturn(factor);
        when(detailService.save(any(CarbonAccountingDetail.class))).thenReturn(true);
        when(accountingService.updateById(any(CarbonAccounting.class))).thenReturn(true);

        Result<Void> result = controller.calculate(calcDTO);

        assertTrue(isOk(result));
        verify(detailService, times(1)).save(any(CarbonAccountingDetail.class));
        assertEquals(0, new BigDecimal("10.0000").compareTo(accounting.getTotalEmission()));
    }

    @Test
    @DisplayName("排放计算-使用能源类型默认因子")
    void testCalculateWithEnergyTypeFactor() {
        CarbonEmissionFactor factor = new CarbonEmissionFactor();
        factor.setFactorValue(new BigDecimal("3.0"));
        factor.setFactorUnit("tCO2e/t");

        EmissionCalcDTO calcDTO = new EmissionCalcDTO();
        calcDTO.setAccountingId(1L);
        EmissionCalcDTO.ActivityDataItem item = new EmissionCalcDTO.ActivityDataItem();
        item.setActivityName("测试");
        item.setEnergyTypeId(1L);
        item.setActivityData(new BigDecimal("2"));
        item.setActivityUnit("t");
        item.setCategory("类别二：输入能源间接排放");
        calcDTO.setActivities(Collections.singletonList(item));

        when(accountingService.getById(1L)).thenReturn(accounting);
        when(factorService.getOne(any())).thenReturn(factor);
        when(detailService.save(any(CarbonAccountingDetail.class))).thenReturn(true);
        when(accountingService.updateById(any(CarbonAccounting.class))).thenReturn(true);

        Result<Void> result = controller.calculate(calcDTO);

        assertTrue(isOk(result));
        assertEquals(0, new BigDecimal("6.0000").compareTo(accounting.getTotalEmission()));
    }

    @Test
    @DisplayName("排放计算-无因子")
    void testCalculateWithoutFactor() {
        EmissionCalcDTO calcDTO = new EmissionCalcDTO();
        calcDTO.setAccountingId(1L);
        EmissionCalcDTO.ActivityDataItem item = new EmissionCalcDTO.ActivityDataItem();
        item.setActivityName("测试");
        item.setActivityData(new BigDecimal("2"));
        item.setActivityUnit("t");
        item.setCategory("类别三：其他间接排放");
        calcDTO.setActivities(Collections.singletonList(item));

        when(accountingService.getById(1L)).thenReturn(accounting);
        when(detailService.save(any(CarbonAccountingDetail.class))).thenReturn(true);
        when(accountingService.updateById(any(CarbonAccounting.class))).thenReturn(true);

        Result<Void> result = controller.calculate(calcDTO);

        assertTrue(isOk(result));
        assertEquals(0, BigDecimal.ZERO.compareTo(accounting.getTotalEmission()));
    }

    @Test
    @DisplayName("排放计算-核算不存在")
    void testCalculateAccountingNotFound() {
        EmissionCalcDTO calcDTO = new EmissionCalcDTO();
        calcDTO.setAccountingId(1L);
        when(accountingService.getById(1L)).thenReturn(null);

        Result<Void> result = controller.calculate(calcDTO);

        assertFalse(isOk(result));
        assertEquals("核算记录不存在", result.getMessage());
    }

    @Test
    @DisplayName("排放计算-因子不存在")
    void testCalculateFactorNotFound() {
        EmissionCalcDTO calcDTO = new EmissionCalcDTO();
        calcDTO.setAccountingId(1L);
        EmissionCalcDTO.ActivityDataItem item = new EmissionCalcDTO.ActivityDataItem();
        item.setActivityName("测试");
        item.setFactorId(99L);
        item.setCategory("类别一：直接温室气体排放");
        calcDTO.setActivities(Collections.singletonList(item));

        when(accountingService.getById(1L)).thenReturn(accounting);
        when(factorService.getById(99L)).thenReturn(null);
        when(detailService.save(any(CarbonAccountingDetail.class))).thenReturn(true);
        when(accountingService.updateById(any(CarbonAccounting.class))).thenReturn(true);

        Result<Void> result = controller.calculate(calcDTO);

        assertTrue(isOk(result));
    }

    @Test
    @DisplayName("排放计算-活动数据为空")
    void testCalculateWithNullActivityData() {
        CarbonEmissionFactor factor = new CarbonEmissionFactor();
        factor.setFactorValue(new BigDecimal("2.0"));
        factor.setFactorUnit("tCO2e/t");

        EmissionCalcDTO calcDTO = new EmissionCalcDTO();
        calcDTO.setAccountingId(1L);
        EmissionCalcDTO.ActivityDataItem item = new EmissionCalcDTO.ActivityDataItem();
        item.setActivityName("测试");
        item.setFactorId(1L);
        item.setCategory("类别一：直接温室气体排放");
        calcDTO.setActivities(Collections.singletonList(item));

        when(accountingService.getById(1L)).thenReturn(accounting);
        when(factorService.getById(1L)).thenReturn(factor);
        when(detailService.save(any(CarbonAccountingDetail.class))).thenReturn(true);
        when(accountingService.updateById(any(CarbonAccounting.class))).thenReturn(true);

        Result<Void> result = controller.calculate(calcDTO);

        assertTrue(isOk(result));
        assertEquals(0, BigDecimal.ZERO.compareTo(accounting.getTotalEmission()));
    }

    @Test
    @DisplayName("排放计算-分类为空")
    void testCalculateWithNullCategory() {
        EmissionCalcDTO calcDTO = new EmissionCalcDTO();
        calcDTO.setAccountingId(1L);
        EmissionCalcDTO.ActivityDataItem item = new EmissionCalcDTO.ActivityDataItem();
        item.setActivityName("测试");
        item.setActivityData(new BigDecimal("2"));
        item.setCategory(null);
        calcDTO.setActivities(Collections.singletonList(item));

        when(accountingService.getById(1L)).thenReturn(accounting);
        when(detailService.save(any(CarbonAccountingDetail.class))).thenReturn(true);
        when(accountingService.updateById(any(CarbonAccounting.class))).thenReturn(true);

        Result<Void> result = controller.calculate(calcDTO);

        assertTrue(isOk(result));
        ArgumentCaptor<CarbonAccountingDetail> captor = ArgumentCaptor.forClass(CarbonAccountingDetail.class);
        verify(detailService, times(1)).save(captor.capture());
        assertEquals("CATEGORY_1", captor.getValue().getCategoryCode());
        assertEquals("范围一", captor.getValue().getScopeType());
        assertEquals("SCOPE_1", captor.getValue().getScopeTypeCode());
    }

    @Test
    @DisplayName("获取所有排放报告")
    void testGetReportAll() {
        when(accountingService.list()).thenReturn(Collections.singletonList(accounting));
        when(detailService.list(any(LambdaQueryWrapper.class))).thenReturn(Collections.singletonList(detail));

        Result<List<EmissionReportDTO>> result = controller.getReportAll();

        assertTrue(isOk(result));
        assertEquals(1, result.getData().size());
    }

    @Test
    @DisplayName("根据ID获取排放报告")
    void testGetReport() {
        when(accountingService.getById(1L)).thenReturn(accounting);
        when(detailService.list(any(LambdaQueryWrapper.class))).thenReturn(Collections.singletonList(detail));

        Result<EmissionReportDTO> result = controller.getReport(1L);

        assertTrue(isOk(result));
        assertNotNull(result.getData());
    }

    @Test
    @DisplayName("根据ID获取排放报告-不存在")
    void testGetReportNotFound() {
        when(accountingService.getById(1L)).thenReturn(null);

        Result<EmissionReportDTO> result = controller.getReport(1L);

        assertFalse(isOk(result));
    }

    @Test
    @DisplayName("构建排放报告-范围二分类")
    void testBuildEmissionReportScope2() {
        CarbonAccountingDetail scope2Detail = new CarbonAccountingDetail();
        scope2Detail.setAccountingId(1L);
        scope2Detail.setEmission(new BigDecimal("30.0000"));
        scope2Detail.setCategory("类别二：输入能源间接排放");

        when(accountingService.getById(1L)).thenReturn(accounting);
        when(detailService.list(any(LambdaQueryWrapper.class))).thenReturn(Collections.singletonList(scope2Detail));

        Result<EmissionReportDTO> result = controller.getReport(1L);

        assertTrue(isOk(result));
        assertEquals(0, new BigDecimal("30.0000").compareTo(result.getData().getScope2Emission()));
    }

    @Test
    @DisplayName("构建排放报告-范围三分类")
    void testBuildEmissionReportScope3() {
        CarbonAccountingDetail scope3Detail = new CarbonAccountingDetail();
        scope3Detail.setAccountingId(1L);
        scope3Detail.setEmission(new BigDecimal("20.0000"));
        scope3Detail.setCategory("类别三：其他间接排放");

        when(accountingService.getById(1L)).thenReturn(accounting);
        when(detailService.list(any(LambdaQueryWrapper.class))).thenReturn(Collections.singletonList(scope3Detail));

        Result<EmissionReportDTO> result = controller.getReport(1L);

        assertTrue(isOk(result));
        assertEquals(0, new BigDecimal("20.0000").compareTo(result.getData().getScope3Emission()));
    }

    @Test
    @DisplayName("构建排放报告-空明细")
    void testBuildEmissionReportEmptyDetails() {
        when(accountingService.getById(1L)).thenReturn(accounting);
        when(detailService.list(any(LambdaQueryWrapper.class))).thenReturn(Collections.emptyList());

        Result<EmissionReportDTO> result = controller.getReport(1L);

        assertTrue(isOk(result));
        assertEquals(0, BigDecimal.ZERO.compareTo(result.getData().getTotalEmission()));
    }

    @Test
    @DisplayName("构建排放报告-排放量为空")
    void testBuildEmissionReportNullEmission() {
        CarbonAccountingDetail nullEmissionDetail = new CarbonAccountingDetail();
        nullEmissionDetail.setAccountingId(1L);
        nullEmissionDetail.setEmission(null);
        nullEmissionDetail.setCategory("类别一：直接温室气体排放");

        when(accountingService.getById(1L)).thenReturn(accounting);
        when(detailService.list(any(LambdaQueryWrapper.class))).thenReturn(Collections.singletonList(nullEmissionDetail));

        Result<EmissionReportDTO> result = controller.getReport(1L);

        assertTrue(isOk(result));
        assertEquals(0, BigDecimal.ZERO.compareTo(result.getData().getTotalEmission()));
    }
}
