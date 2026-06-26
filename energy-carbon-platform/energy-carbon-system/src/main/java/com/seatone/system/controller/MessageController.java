package com.seatone.system.controller;

import com.seatone.common.result.Result;
import com.seatone.common.result.PageResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDateTime;
import java.util.*;

@Api(tags = "消息中心")
@RestController
@RequestMapping("/system/message")
public class MessageController {

    private static final List<Map<String, Object>> MESSAGES = new ArrayList<>();
    private static long ID_SEQ = 1;

    static {
        addMsg("alarm", "温度过高告警", "1#车间温度超过设定阈值35°C，请及时处理", "10:30", false);
        addMsg("alarm", "能耗异常告警", "2#电表读数异常波动，超出正常范围20%", "09:15", false);
        addMsg("notice", "系统维护通知", "系统将于今晚22:00进行例行维护", "08:00", true);
        addMsg("task", "巡检任务待完成", "您有一个待执行的设备巡检任务，请尽快处理", "昨天", true);
        addMsg("notice", "月度报表已生成", "2026年6月能耗月报已生成，请查阅", "06-24", true);
    }

    private static void addMsg(String type, String title, String summary, String time, boolean read) {
        Map<String, Object> m = new HashMap<>();
        m.put("id", ID_SEQ++);
        m.put("type", type);
        m.put("title", title);
        m.put("summary", summary);
        m.put("time", time);
        m.put("read", read);
        m.put("createTime", LocalDateTime.now().toString());
        MESSAGES.add(m);
    }

    @ApiOperation("分页查询消息列表")
    @GetMapping("/list")
    public Result<PageResult<Map<String, Object>>> list(
            @ApiParam("页码") @RequestParam(defaultValue = "1") Long current,
            @ApiParam("每页条数") @RequestParam(defaultValue = "10") Long size,
            @ApiParam("类型") @RequestParam(required = false) String type) {
        List<Map<String, Object>> filtered = new ArrayList<>();
        for (Map<String, Object> msg : MESSAGES) {
            if (type == null || type.isEmpty() || type.equals(msg.get("type"))) {
                filtered.add(msg);
            }
        }
        int start = (int)((current - 1) * size);
        int end = Math.min(start + size.intValue(), filtered.size());
        List<Map<String, Object>> list = start < filtered.size() ? filtered.subList(start, end) : new ArrayList<>();
        return Result.ok(PageResult.of(current, size, (long)filtered.size(), list));
    }

    @ApiOperation("标记消息已读")
    @PutMapping("/{id}/read")
    public Result<Void> markRead(@ApiParam("消息ID") @PathVariable Long id) {
        for (Map<String, Object> msg : MESSAGES) {
            if (id.equals(msg.get("id"))) {
                msg.put("read", true);
                break;
            }
        }
        return Result.ok();
    }
}
