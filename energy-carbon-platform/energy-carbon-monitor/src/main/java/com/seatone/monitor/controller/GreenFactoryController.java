package com.seatone.monitor.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.seatone.common.result.PageResult;
import com.seatone.common.result.Result;
import com.seatone.monitor.dto.GreenFactoryDTO;
import com.seatone.monitor.dto.GreenFactoryItemDTO;
import com.seatone.monitor.entity.ToolkitGreenFactory;
import com.seatone.monitor.entity.ToolkitGreenFactoryItem;
import com.seatone.monitor.service.ToolkitGreenFactoryItemService;
import com.seatone.monitor.service.ToolkitGreenFactoryService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Api(tags = "绿色工厂申报管理")
@RestController
@RequestMapping("/toolkit/green-factory")
public class GreenFactoryController {

    @Autowired
    private ToolkitGreenFactoryService greenFactoryService;

    @Autowired
    private ToolkitGreenFactoryItemService greenFactoryItemService;

    @ApiOperation("分页查询申报列表")
    @GetMapping("/list")
    public Result<PageResult<GreenFactoryDTO>> list(
            @ApiParam("页码") @RequestParam(defaultValue = "1") Long current,
            @ApiParam("每页条数") @RequestParam(defaultValue = "10") Long size,
            @ApiParam("状态") @RequestParam(required = false) String status,
            @ApiParam("申报年度") @RequestParam(required = false) Integer declarationYear) {
        Page<ToolkitGreenFactory> page = new Page<>(current, size);
        LambdaQueryWrapper<ToolkitGreenFactory> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ToolkitGreenFactory::getDeleted, 0);
        if (status != null && !status.isEmpty()) {
            wrapper.eq(ToolkitGreenFactory::getStatus, status);
        }
        if (declarationYear != null) {
            wrapper.eq(ToolkitGreenFactory::getDeclarationYear, declarationYear);
        }
        wrapper.orderByDesc(ToolkitGreenFactory::getCreateTime);
        Page<ToolkitGreenFactory> resultPage = greenFactoryService.page(page, wrapper);
        List<GreenFactoryDTO> records = resultPage.getRecords().stream().map(this::convertToDTO).collect(Collectors.toList());
        return Result.ok(PageResult.of(resultPage.getCurrent(), resultPage.getSize(), resultPage.getTotal(), records));
    }

    @ApiOperation("创建申报")
    @PostMapping
    public Result<GreenFactoryDTO> create(@RequestBody ToolkitGreenFactory factory) {
        factory.setStatus("DRAFT");
        factory.setProgress(0);
        factory.setDeleted(0);
        greenFactoryService.save(factory);

        // 创建默认资料清单
        createDefaultItems(factory.getId());

        return Result.ok(convertToDTO(factory));
    }

    @ApiOperation("获取申报详情")
    @GetMapping("/{id}")
    public Result<GreenFactoryDTO> getById(@ApiParam("申报ID") @PathVariable Long id) {
        ToolkitGreenFactory entity = greenFactoryService.getById(id);
        if (entity == null) {
            return Result.error("申报记录不存在");
        }
        GreenFactoryDTO dto = convertToDTO(entity);

        // 查询资料清单
        LambdaQueryWrapper<ToolkitGreenFactoryItem> itemWrapper = new LambdaQueryWrapper<>();
        itemWrapper.eq(ToolkitGreenFactoryItem::getDeclarationId, id);
        itemWrapper.orderByAsc(ToolkitGreenFactoryItem::getId);
        List<GreenFactoryItemDTO> items = greenFactoryItemService.list(itemWrapper).stream()
                .map(this::convertItemToDTO).collect(Collectors.toList());
        dto.setItems(items);

        return Result.ok(dto);
    }

    @ApiOperation("上传资料")
    @PostMapping("/{id}/upload-item")
    public Result<Void> uploadItem(
            @ApiParam("申报ID") @PathVariable Long id,
            @ApiParam("资料项ID") @RequestParam Long itemId,
            @ApiParam("文件") @RequestParam("file") MultipartFile file) {
        ToolkitGreenFactoryItem item = new ToolkitGreenFactoryItem();
        item.setId(itemId);
        item.setUploaded(1);
        item.setFilePath("/uploads/green-factory/" + file.getOriginalFilename());
        item.setUploadTime(LocalDateTime.now());
        greenFactoryItemService.updateById(item);

        // 更新进度
        updateProgress(id);
        return Result.ok();
    }

    @ApiOperation("更新进度")
    @PutMapping("/{id}/progress")
    public Result<Void> updateProgress(
            @ApiParam("申报ID") @PathVariable Long id,
            @ApiParam("进度") @RequestParam Integer progress) {
        ToolkitGreenFactory factory = new ToolkitGreenFactory();
        factory.setId(id);
        factory.setProgress(progress);
        greenFactoryService.updateById(factory);
        return Result.ok();
    }

    @ApiOperation("提交审核")
    @PutMapping("/{id}/submit")
    public Result<Void> submit(@ApiParam("申报ID") @PathVariable Long id) {
        ToolkitGreenFactory factory = new ToolkitGreenFactory();
        factory.setId(id);
        factory.setStatus("SUBMITTED");
        factory.setSubmitTime(LocalDateTime.now());
        factory.setProgress(100);
        greenFactoryService.updateById(factory);
        return Result.ok();
    }

    @ApiOperation("删除申报")
    @DeleteMapping("/{id}")
    public Result<Void> delete(@ApiParam("申报ID") @PathVariable Long id) {
        ToolkitGreenFactory factory = new ToolkitGreenFactory();
        factory.setId(id);
        factory.setDeleted(1);
        greenFactoryService.updateById(factory);
        return Result.ok();
    }

    private void createDefaultItems(Long declarationId) {
        String[][] defaultItems = {
                {"营业执照副本", "基础资料", "1"},
                {"组织机构代码证", "基础资料", "1"},
                {"企业简介", "基础资料", "1"},
                {"能源管理体系认证证书", "管理体系", "1"},
                {"环境管理体系认证证书", "管理体系", "1"},
                {"质量管理体系认证证书", "管理体系", "0"},
                {"近三年能源消耗统计报表", "能源数据", "1"},
                {"近三年碳排放数据", "能源数据", "1"},
                {"节能改造项目报告", "节能措施", "1"},
                {"绿色制造水平自评价报告", "评价报告", "1"},
                {"第三方评价报告", "评价报告", "0"},
                {"其他支撑材料", "其他", "0"}
        };
        for (String[] item : defaultItems) {
            ToolkitGreenFactoryItem factoryItem = new ToolkitGreenFactoryItem();
            factoryItem.setDeclarationId(declarationId);
            factoryItem.setItemName(item[0]);
            factoryItem.setItemCategory(item[1]);
            factoryItem.setRequired(Integer.parseInt(item[2]));
            factoryItem.setUploaded(0);
            greenFactoryItemService.save(factoryItem);
        }
    }

    private void updateProgress(Long declarationId) {
        LambdaQueryWrapper<ToolkitGreenFactoryItem> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ToolkitGreenFactoryItem::getDeclarationId, declarationId);
        List<ToolkitGreenFactoryItem> items = greenFactoryItemService.list(wrapper);
        long total = items.size();
        long uploaded = items.stream().filter(i -> i.getUploaded() == 1).count();
        int progress = total > 0 ? (int) (uploaded * 100 / total) : 0;

        ToolkitGreenFactory factory = new ToolkitGreenFactory();
        factory.setId(declarationId);
        factory.setProgress(progress);
        greenFactoryService.updateById(factory);
    }

    private GreenFactoryDTO convertToDTO(ToolkitGreenFactory entity) {
        GreenFactoryDTO dto = new GreenFactoryDTO();
        BeanUtils.copyProperties(entity, dto);
        return dto;
    }

    private GreenFactoryItemDTO convertItemToDTO(ToolkitGreenFactoryItem entity) {
        GreenFactoryItemDTO dto = new GreenFactoryItemDTO();
        BeanUtils.copyProperties(entity, dto);
        return dto;
    }
}
