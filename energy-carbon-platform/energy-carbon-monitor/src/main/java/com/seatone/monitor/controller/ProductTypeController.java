package com.seatone.monitor.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.seatone.common.result.PageResult;
import com.seatone.common.result.Result;
import com.seatone.monitor.dto.ProductTypeDTO;
import com.seatone.monitor.entity.ProductType;
import com.seatone.monitor.service.ProductTypeService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.stream.Collectors;

@Api(tags = "产品种类管理")
@RestController
@RequestMapping("/monitor/productType")
public class ProductTypeController {

    @Autowired
    private ProductTypeService productTypeService;

    @ApiOperation("分页查询产品种类列表")
    @GetMapping("/list")
    public Result<PageResult<ProductTypeDTO>> list(
            @ApiParam("页码") @RequestParam(defaultValue = "1") Long current,
            @ApiParam("每页条数") @RequestParam(defaultValue = "10") Long size,
            @ApiParam("产品名称") @RequestParam(required = false) String productName,
            @ApiParam("产品类别") @RequestParam(required = false) String productCategory,
            @ApiParam("状态") @RequestParam(required = false) Integer status) {
        Page<ProductType> page = new Page<>(current, size);
        LambdaQueryWrapper<ProductType> wrapper = new LambdaQueryWrapper<>();
        if (productName != null && !productName.isEmpty()) {
            wrapper.like(ProductType::getProductName, productName);
        }
        if (productCategory != null && !productCategory.isEmpty()) {
            wrapper.eq(ProductType::getProductCategory, productCategory);
        }
        if (status != null) {
            wrapper.eq(ProductType::getStatus, status);
        }
        wrapper.orderByDesc(ProductType::getCreateTime);
        Page<ProductType> resultPage = productTypeService.page(page, wrapper);
        List<ProductTypeDTO> records = resultPage.getRecords().stream().map(this::convertToDTO).collect(Collectors.toList());
        return Result.ok(PageResult.of(resultPage.getCurrent(), resultPage.getSize(), resultPage.getTotal(), records));
    }

    @ApiOperation("查询所有产品种类")
    @GetMapping("/all")
    public Result<List<ProductTypeDTO>> all() {
        LambdaQueryWrapper<ProductType> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ProductType::getStatus, 1);
        List<ProductTypeDTO> list = productTypeService.list(wrapper).stream().map(this::convertToDTO).collect(Collectors.toList());
        return Result.ok(list);
    }

    @ApiOperation("根据ID查询产品种类")
    @GetMapping("/{id}")
    public Result<ProductTypeDTO> getById(@ApiParam("产品种类ID") @PathVariable Long id) {
        ProductType product = productTypeService.getById(id);
        if (product == null) {
            return Result.error("产品种类不存在");
        }
        return Result.ok(convertToDTO(product));
    }

    @ApiOperation("新增产品种类")
    @PostMapping
    public Result<Void> add(@RequestBody ProductTypeDTO dto) {
        ProductType product = new ProductType();
        BeanUtils.copyProperties(dto, product);
        product.setId(null);
        productTypeService.save(product);
        return Result.ok();
    }

    @ApiOperation("修改产品种类")
    @PutMapping("/{id}")
    public Result<Void> update(@ApiParam("产品种类ID") @PathVariable Long id, @RequestBody ProductTypeDTO dto) {
        ProductType product = new ProductType();
        BeanUtils.copyProperties(dto, product);
        product.setId(id);
        productTypeService.updateById(product);
        return Result.ok();
    }

    @ApiOperation("删除产品种类")
    @DeleteMapping("/{id}")
    public Result<Void> delete(@ApiParam("产品种类ID") @PathVariable Long id) {
        productTypeService.removeById(id);
        return Result.ok();
    }

    @ApiOperation("修改产品种类状态")
    @PutMapping("/{id}/status")
    public Result<Void> changeStatus(@ApiParam("产品种类ID") @PathVariable Long id,
                                      @ApiParam("状态") @RequestParam Integer status) {
        ProductType product = new ProductType();
        product.setId(id);
        product.setStatus(status);
        productTypeService.updateById(product);
        return Result.ok();
    }

    @ApiOperation("导出产品种类")
    @GetMapping("/export")
    public void export(HttpServletResponse response,
                       @ApiParam("产品名称") @RequestParam(required = false) String productName,
                       @ApiParam("产品类别") @RequestParam(required = false) String productCategory,
                       @ApiParam("状态") @RequestParam(required = false) Integer status) throws IOException {
        LambdaQueryWrapper<ProductType> wrapper = new LambdaQueryWrapper<>();
        if (productName != null && !productName.isEmpty()) {
            wrapper.like(ProductType::getProductName, productName);
        }
        if (productCategory != null && !productCategory.isEmpty()) {
            wrapper.eq(ProductType::getProductCategory, productCategory);
        }
        if (status != null) {
            wrapper.eq(ProductType::getStatus, status);
        }
        List<ProductType> list = productTypeService.list(wrapper);

        response.setContentType("text/csv;charset=UTF-8");
        response.setHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode("产品种类.csv", StandardCharsets.UTF_8.name()));
        OutputStream out = response.getOutputStream();
        out.write(new byte[]{(byte) 0xEF, (byte) 0xBB, (byte) 0xBF});
        out.write("产品编码,产品名称,产品类别,计量单位,碳排放因子,状态,备注\n".getBytes(StandardCharsets.UTF_8));
        for (ProductType p : list) {
            String line = String.format("%s,%s,%s,%s,%s,%s,%s\n",
                    p.getProductCode() == null ? "" : p.getProductCode(),
                    p.getProductName() == null ? "" : p.getProductName(),
                    p.getProductCategory() == null ? "" : p.getProductCategory(),
                    p.getUnit() == null ? "" : p.getUnit(),
                    p.getCarbonFactor() == null ? "" : p.getCarbonFactor().toString(),
                    p.getStatus() != null && p.getStatus() == 1 ? "正常" : "停用",
                    p.getRemark() == null ? "" : p.getRemark());
            out.write(line.getBytes(StandardCharsets.UTF_8));
        }
        out.flush();
    }

    @ApiOperation("导入产品种类")
    @PostMapping("/import")
    public Result<Void> importData(@RequestBody List<ProductTypeDTO> dtoList) {
        for (ProductTypeDTO dto : dtoList) {
            ProductType product = new ProductType();
            BeanUtils.copyProperties(dto, product);
            product.setId(null);
            productTypeService.save(product);
        }
        return Result.ok();
    }

    private ProductTypeDTO convertToDTO(ProductType product) {
        ProductTypeDTO dto = new ProductTypeDTO();
        BeanUtils.copyProperties(product, dto);
        return dto;
    }
}
