package com.seatone.system.controller;

import com.seatone.common.result.Result;
import com.seatone.system.dto.DeptDTO;
import com.seatone.system.entity.SysDept;
import com.seatone.system.service.SysDeptService;
import com.seatone.system.vo.DeptVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@Api(tags = "部门管理")
@RestController
@RequestMapping("/system/dept")
public class SysDeptController {

    @Autowired
    private SysDeptService deptService;

    @ApiOperation("查询部门树")
    @GetMapping("/tree")
    public Result<List<DeptVO>> tree() {
        List<SysDept> list = deptService.list();
        List<DeptVO> voList = list.stream().map(this::convertToVO).collect(Collectors.toList());
        return Result.ok(buildTree(voList));
    }

    @ApiOperation("查询部门列表")
    @GetMapping("/list")
    public Result<List<DeptVO>> list() {
        List<SysDept> list = deptService.list();
        List<DeptVO> voList = list.stream().map(this::convertToVO).collect(Collectors.toList());
        return Result.ok(buildTree(voList));
    }

    @ApiOperation("根据ID查询部门")
    @GetMapping("/{id}")
    public Result<DeptVO> getById(@ApiParam("部门ID") @PathVariable Long id) {
        SysDept dept = deptService.getById(id);
        if (dept == null) {
            return Result.error("部门不存在");
        }
        return Result.ok(convertToVO(dept));
    }

    @ApiOperation("新增部门")
    @PostMapping
    public Result<Void> add(@RequestBody DeptDTO dto) {
        SysDept dept = new SysDept();
        BeanUtils.copyProperties(dto, dept);
        deptService.save(dept);
        return Result.ok();
    }

    @ApiOperation("修改部门")
    @PutMapping("/{id}")
    public Result<Void> update(@ApiParam("部门ID") @PathVariable Long id, @RequestBody DeptDTO dto) {
        SysDept dept = new SysDept();
        BeanUtils.copyProperties(dto, dept);
        dept.setId(id);
        deptService.updateById(dept);
        return Result.ok();
    }

    @ApiOperation("删除部门")
    @DeleteMapping("/{id}")
    public Result<Void> delete(@ApiParam("部门ID") @PathVariable Long id) {
        deptService.removeById(id);
        return Result.ok();
    }

    private DeptVO convertToVO(SysDept dept) {
        DeptVO vo = new DeptVO();
        BeanUtils.copyProperties(dept, vo);
        return vo;
    }

    private List<DeptVO> buildTree(List<DeptVO> list) {
        List<DeptVO> roots = list.stream()
                .filter(d -> d.getParentId() == null || d.getParentId() == 0L)
                .collect(Collectors.toList());
        for (DeptVO root : roots) {
            root.setChildren(getChildren(list, root.getId()));
        }
        return roots;
    }

    private List<DeptVO> getChildren(List<DeptVO> list, Long parentId) {
        return list.stream()
                .filter(d -> parentId.equals(d.getParentId()))
                .peek(d -> d.setChildren(getChildren(list, d.getId())))
                .collect(Collectors.toList());
    }
}
