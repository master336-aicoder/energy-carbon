package com.seatone.monitor.iot.websocket;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

/**
 * WebSocket 配置类
 * 配置实时数据推送的 WebSocket 端点
 * 用于向前端推送物联网设备的实时能耗数据
 *
 * @author seatone
 * @since 2026-06-22
 */
@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {

    /**
     * 实时数据 WebSocket 处理器
     */
    @Autowired
    private RealTimeWebSocketHandler realTimeWebSocketHandler;

    /**
     * 注册 WebSocket 处理器
     * 配置 WebSocket 端点路径和跨域设置
     *
     * @param registry WebSocket 处理器注册器
     */
    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(realTimeWebSocketHandler, "/ws/realtime")
                .setAllowedOrigins("*");
    }
}
