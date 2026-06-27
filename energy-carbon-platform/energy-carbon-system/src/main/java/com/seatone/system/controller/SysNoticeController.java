package com.seatone.system.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.seatone.common.result.PageResult;
import com.seatone.common.result.Result;
import com.seatone.common.utils.SecurityUtils;
import com.seatone.system.dto.NoticeDispatchDTO;
import com.seatone.system.entity.SysNotice;
import com.seatone.system.entity.SysNoticeDispatch;
import com.seatone.system.service.SysNoticeDispatchService;
import com.seatone.system.service.SysNoticeService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 通知公告控制器
 * 提供通知公告的增删改查、分页查询、启用公告列表、未读公告、已读标记、下发等功能
 *
 * @author seatone
 * @since 2026-06-27
 */
@Api(tags = "通知公告")
@RestController
@RequestMapping("/system/notice")
public class SysNoticeController {

    @Autowired
    private SysNoticeService noticeService;

    @Autowired
    private SysNoticeDispatchService dispatchService;

    /**
     * 分页查询公告列表
     * 支持按标题模糊查询
     *
     * @param current     页码，默认1
     * @param size        每页条数，默认10
     * @param noticeTitle 公告标题（模糊查询）
     * @param status      状态
     * @return 分页公告列表
     */
    @ApiOperation("分页查询公告列表")
    @GetMapping("/list")
    public Result<PageResult<SysNotice>> list(
            @ApiParam("页码") @RequestParam(defaultValue = "1") Long current,
            @ApiParam("每页条数") @RequestParam(defaultValue = "10") Long size,
            @ApiParam("公告标题") @RequestParam(required = false) String noticeTitle,
            @ApiParam("状态") @RequestParam(required = false) Integer status) {
        Page<SysNotice> page = new Page<>(current, size);
        LambdaQueryWrapper<SysNotice> wrapper = new LambdaQueryWrapper<>();
        if (noticeTitle != null && !noticeTitle.isEmpty()) {
            wrapper.like(SysNotice::getNoticeTitle, noticeTitle);
        }
        if (status != null) {
            wrapper.eq(SysNotice::getStatus, status);
        }
        wrapper.orderByDesc(SysNotice::getCreateTime);
        Page<SysNotice> resultPage = noticeService.page(page, wrapper);
        return Result.ok(PageResult.of(resultPage.getCurrent(), resultPage.getSize(), resultPage.getTotal(), resultPage.getRecords()));
    }

    /**
     * 根据ID查询公告
     *
     * @param id 公告ID
     * @return 公告详情
     */
    @ApiOperation("根据ID查询公告")
    @GetMapping("/{id}")
    public Result<SysNotice> getById(@ApiParam("公告ID") @PathVariable Long id) {
        SysNotice notice = noticeService.getById(id);
        if (notice == null) {
            return Result.error("公告不存在");
        }
        return Result.ok(notice);
    }

    /**
     * 新增公告
     *
     * @param notice 公告数据
     * @return 操作结果
     */
    @ApiOperation("新增公告")
    @PostMapping
    public Result<Void> add(@RequestBody SysNotice notice) {
        noticeService.save(notice);
        return Result.ok();
    }

    /**
     * 修改公告
     *
     * @param id     公告ID
     * @param notice 公告数据
     * @return 操作结果
     */
    @ApiOperation("修改公告")
    @PutMapping("/{id}")
    public Result<Void> update(@ApiParam("公告ID") @PathVariable Long id, @RequestBody SysNotice notice) {
        notice.setId(id);
        noticeService.updateById(notice);
        return Result.ok();
    }

    /**
     * 删除公告
     *
     * @param id 公告ID
     * @return 操作结果
     */
    @ApiOperation("删除公告")
    @DeleteMapping("/{id}")
    public Result<Void> delete(@ApiParam("公告ID") @PathVariable Long id) {
        noticeService.removeById(id);
        return Result.ok();
    }

    /**
     * 获取当前用户未读的公告列表
     *
     * @return 未读公告列表
     */
    @ApiOperation("获取当前用户未读的公告列表")
    @GetMapping("/unread/list")
    public Result<List<SysNotice>> getUnreadList() {
        Long userId = SecurityUtils.getCurrentUserId();
        List<SysNotice> list = noticeService.getUnreadList(userId);
        return Result.ok(list);
    }

    /**
     * 获取当前用户未读公告数量
     *
     * @return 未读公告数量
     */
    @ApiOperation("获取当前用户未读公告数量")
    @GetMapping("/unread/count")
    public Result<Integer> getUnreadCount() {
        Long userId = SecurityUtils.getCurrentUserId();
        int count = noticeService.countUnread(userId);
        return Result.ok(count);
    }

    /**
     * 标记某公告为已读
     *
     * @param id 公告ID
     * @return 操作结果
     */
    @ApiOperation("标记某公告为已读")
    @PostMapping("/{id}/read")
    public Result<Void> markAsRead(@ApiParam("公告ID") @PathVariable Long id) {
        Long userId = SecurityUtils.getCurrentUserId();
        noticeService.markAsRead(id, userId);
        return Result.ok();
    }

    /**
     * 下发通知公告
     * 根据下发类型和目标ID列表，将通知公告推送到目标用户的个人消息信箱
     *
     * @param id     公告ID
     * @param dto    下发请求体（dispatchType + targetIds）
     * @return 操作结果
     */
    @ApiOperation("下发通知公告")
    @PostMapping("/{id}/dispatch")
    public Result<Void> dispatch(
            @ApiParam("公告ID") @PathVariable Long id,
            @RequestBody NoticeDispatchDTO dto) {
        dispatchService.dispatch(id, dto.getDispatchType(), dto.getTargetIds());
        return Result.ok();
    }

    /**
     * 分页查询下发记录列表
     *
     * @param current      页码，默认1
     * @param size         每页条数，默认10
     * @param noticeId     通知公告ID（可选）
     * @param dispatchType 下发类型（可选）
     * @return 分页下发记录列表
     */
    @ApiOperation("分页查询下发记录列表")
    @GetMapping("/dispatch/list")
    public Result<PageResult<SysNoticeDispatch>> dispatchList(
            @ApiParam("页码") @RequestParam(defaultValue = "1") Long current,
            @ApiParam("每页条数") @RequestParam(defaultValue = "10") Long size,
            @ApiParam("通知公告ID") @RequestParam(required = false) Long noticeId,
            @ApiParam("下发类型") @RequestParam(required = false) Integer dispatchType) {
        Page<SysNoticeDispatch> page = new Page<>(current, size);
        LambdaQueryWrapper<SysNoticeDispatch> wrapper = new LambdaQueryWrapper<>();
        if (noticeId != null) {
            wrapper.eq(SysNoticeDispatch::getNoticeId, noticeId);
        }
        if (dispatchType != null) {
            wrapper.eq(SysNoticeDispatch::getDispatchType, dispatchType);
        }
        wrapper.orderByDesc(SysNoticeDispatch::getCreateTime);
        Page<SysNoticeDispatch> resultPage = dispatchService.page(page, wrapper);
        return Result.ok(PageResult.of(resultPage.getCurrent(), resultPage.getSize(), resultPage.getTotal(), resultPage.getRecords()));
    }
}
