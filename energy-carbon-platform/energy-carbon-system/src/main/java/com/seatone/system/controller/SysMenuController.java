package com.seatone.system.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.seatone.common.result.Result;
import com.seatone.system.dto.MenuDTO;
import com.seatone.system.entity.SysMenu;
import com.seatone.system.service.SysMenuService;
import com.seatone.system.vo.MenuVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@Api(tags = "菜单管理")
@RestController
@RequestMapping("/system/menu")
public class SysMenuController {

    @Autowired
    private SysMenuService menuService;

    @ApiOperation("查询菜单列表")
    @GetMapping("/list")
    public Result<List<MenuVO>> list(@ApiParam("菜单名称") @RequestParam(required = false) String menuName,
                                      @ApiParam("状态") @RequestParam(required = false) Integer status) {
        LambdaQueryWrapper<SysMenu> wrapper = new LambdaQueryWrapper<>();
        if (menuName != null && !menuName.isEmpty()) {
            wrapper.like(SysMenu::getMenuName, menuName);
        }
        if (status != null) {
            wrapper.eq(SysMenu::getStatus, status);
        }
        wrapper.orderByAsc(SysMenu::getSort);
        List<SysMenu> list = menuService.list(wrapper);
        List<MenuVO> voList = list.stream().map(this::convertToVO).collect(Collectors.toList());
        return Result.ok(buildTree(voList));
    }

    @ApiOperation("根据ID查询菜单")
    @GetMapping("/{id}")
    public Result<MenuVO> getById(@ApiParam("菜单ID") @PathVariable Long id) {
        SysMenu menu = menuService.getById(id);
        if (menu == null) {
            return Result.error("菜单不存在");
        }
        return Result.ok(convertToVO(menu));
    }

    @ApiOperation("新增菜单")
    @PostMapping
    public Result<Void> add(@RequestBody MenuDTO dto) {
        SysMenu menu = new SysMenu();
        BeanUtils.copyProperties(dto, menu);
        menuService.save(menu);
        return Result.ok();
    }

    @ApiOperation("修改菜单")
    @PutMapping("/{id}")
    public Result<Void> update(@ApiParam("菜单ID") @PathVariable Long id, @RequestBody MenuDTO dto) {
        SysMenu menu = new SysMenu();
        BeanUtils.copyProperties(dto, menu);
        menu.setId(id);
        menuService.updateById(menu);
        return Result.ok();
    }

    @ApiOperation("删除菜单")
    @DeleteMapping("/{id}")
    public Result<Void> delete(@ApiParam("菜单ID") @PathVariable Long id) {
        menuService.removeById(id);
        return Result.ok();
    }

    @ApiOperation("获取菜单树")
    @GetMapping("/tree")
    public Result<List<MenuVO>> tree() {
        List<SysMenu> list = menuService.list(new LambdaQueryWrapper<SysMenu>().orderByAsc(SysMenu::getSort));
        List<MenuVO> voList = list.stream().map(this::convertToVO).collect(Collectors.toList());
        return Result.ok(buildTree(voList));
    }

    private MenuVO convertToVO(SysMenu menu) {
        MenuVO vo = new MenuVO();
        BeanUtils.copyProperties(menu, vo);
        return vo;
    }

    private List<MenuVO> buildTree(List<MenuVO> list) {
        List<MenuVO> roots = list.stream()
                .filter(m -> m.getParentId() == null || m.getParentId() == 0L)
                .collect(Collectors.toList());
        for (MenuVO root : roots) {
            root.setChildren(getChildren(list, root.getId()));
        }
        return roots;
    }

    private List<MenuVO> getChildren(List<MenuVO> list, Long parentId) {
        return list.stream()
                .filter(m -> parentId.equals(m.getParentId()))
                .peek(m -> m.setChildren(getChildren(list, m.getId())))
                .collect(Collectors.toList());
    }
}
