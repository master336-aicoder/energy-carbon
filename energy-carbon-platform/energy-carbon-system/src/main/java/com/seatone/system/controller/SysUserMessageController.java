package com.seatone.system.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.seatone.common.result.PageResult;
import com.seatone.common.result.Result;
import com.seatone.common.utils.SecurityUtils;
import com.seatone.system.entity.SysUserMessage;
import com.seatone.system.service.SysUserMessageService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 用户消息控制器（我的消息）
 *
 * @author seatone
 * @since 2026-06-27
 */
@Api(tags = "用户消息")
@RestController
@RequestMapping("/system/message")
public class SysUserMessageController {

    @Autowired
    private SysUserMessageService userMessageService;

    /**
     * 分页查询当前用户的消息列表
     */
    @ApiOperation("分页查询当前用户的消息列表")
    @GetMapping("/list")
    public Result<PageResult<SysUserMessage>> list(
            @ApiParam("页码") @RequestParam(defaultValue = "1") Long current,
            @ApiParam("每页条数") @RequestParam(defaultValue = "10") Long size,
            @ApiParam("已读状态：0未读 1已读") @RequestParam(required = false) Integer readStatus) {
        Long userId = SecurityUtils.getCurrentUserId();
        Page<SysUserMessage> resultPage = userMessageService.getList(userId, current, size, readStatus);
        return Result.ok(PageResult.of(resultPage.getCurrent(), resultPage.getSize(), resultPage.getTotal(), resultPage.getRecords()));
    }

    /**
     * 根据ID查询消息详情
     */
    @ApiOperation("根据ID查询消息详情")
    @GetMapping("/{id}")
    public Result<SysUserMessage> getById(@ApiParam("消息ID") @PathVariable Long id) {
        Long userId = SecurityUtils.getCurrentUserId();
        SysUserMessage message = userMessageService.getByIdForUser(id, userId);
        if (message == null) {
            return Result.error("消息不存在");
        }
        return Result.ok(message);
    }

    /**
     * 获取当前用户未读消息数量
     */
    @ApiOperation("获取当前用户未读消息数量")
    @GetMapping("/unread/count")
    public Result<Integer> getUnreadCount() {
        Long userId = SecurityUtils.getCurrentUserId();
        int count = userMessageService.countUnread(userId);
        return Result.ok(count);
    }

    /**
     * 标记消息为已读
     */
    @ApiOperation("标记消息为已读")
    @PostMapping("/{id}/read")
    public Result<Void> markAsRead(@ApiParam("消息ID") @PathVariable Long id) {
        Long userId = SecurityUtils.getCurrentUserId();
        userMessageService.markRead(id, userId);
        return Result.ok();
    }
}
