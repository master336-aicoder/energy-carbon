package com.seatone.system.feign;

import com.seatone.common.result.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * 监控模块 Feign Client 示例
 *
 * 说明：
 * - 由于是单体应用，此 Feign Client 调用的是同一个应用内的接口
 * - 在单体应用中，Feign 会通过本地 HTTP 调用自己
 * - 后续若拆分为微服务架构，只需修改 @FeignClient 的 name 指向对应服务即可
 *
 * 使用方式：
 *   @Autowired
 *   private MonitorFeignClient monitorFeignClient;
 *   Result result = monitorFeignClient.getRealtimeData(deviceId);
 */
@FeignClient(name = "energy-carbon-service", path = "/monitor", contextId = "monitorClient")
public interface MonitorFeignClient {

    @GetMapping("/realtime")
    Result getRealtimeData(@RequestParam("deviceId") Long deviceId);
}
