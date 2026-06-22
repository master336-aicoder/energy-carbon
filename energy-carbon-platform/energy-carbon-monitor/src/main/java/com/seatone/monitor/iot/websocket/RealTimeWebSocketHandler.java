package com.seatone.monitor.iot.websocket;

import com.alibaba.fastjson2.JSON;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Slf4j
@Component
public class RealTimeWebSocketHandler extends TextWebSocketHandler {

    private final Set<WebSocketSession> sessions = new CopyOnWriteArraySet<>();
    private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();

    public RealTimeWebSocketHandler() {
        scheduler.scheduleAtFixedRate(this::broadcastHeartbeat, 30, 30, TimeUnit.SECONDS);
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        sessions.add(session);
        log.info("WebSocket connection established: {}, total sessions: {}", session.getId(), sessions.size());
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) {
        String payload = message.getPayload();
        log.debug("WebSocket message received: {}", payload);
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        sessions.remove(session);
        log.info("WebSocket connection closed: {}, total sessions: {}", session.getId(), sessions.size());
    }

    public void broadcastMessage(Map<String, Object> message) {
        if (sessions.isEmpty()) {
            return;
        }
        String json = JSON.toJSONString(message);
        TextMessage textMessage = new TextMessage(json);
        for (WebSocketSession session : sessions) {
            if (session.isOpen()) {
                try {
                    session.sendMessage(textMessage);
                } catch (IOException e) {
                    log.error("Failed to send WebSocket message: {}", e.getMessage());
                }
            }
        }
    }

    private void broadcastHeartbeat() {
        Map<String, Object> heartbeat = new ConcurrentHashMap<>();
        heartbeat.put("type", "heartbeat");
        heartbeat.put("timestamp", System.currentTimeMillis());
        broadcastMessage(heartbeat);
    }

    public int getSessionCount() {
        return sessions.size();
    }
}
