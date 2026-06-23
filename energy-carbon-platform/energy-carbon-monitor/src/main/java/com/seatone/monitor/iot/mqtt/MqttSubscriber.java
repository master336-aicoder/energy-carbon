package com.seatone.monitor.iot.mqtt;

import lombok.extern.slf4j.Slf4j;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.Arrays;
import java.util.List;

/**
 * MQTT subscriber component.
 * Subscribes to default MQTT topics for IoT device data.
 * Only subscribes if MqttClient bean is available and connected.
 *
 * <p>Default subscribed topics:
 * <ul>
 *   <li>energy/+/data - energy consumption data</li>
 *   <li>meter/+/data - meter reading data</li>
 *   <li>device/+/status - device status updates</li>
 * </ul>
 *
 * @author seatone
 * @since 2026-06-22
 */
@Slf4j
@Component
public class MqttSubscriber {

    /**
     * MQTT client, may be null if MQTT is disabled or connection failed.
     */
    @Autowired(required = false)
    private MqttClient mqttClient;

    /**
     * Default MQTT topics to subscribe.
     */
    private static final List<String> DEFAULT_TOPICS = Arrays.asList(
            "energy/+/data",
            "meter/+/data",
            "device/+/status"
    );

    /**
     * 最大订阅重试次数
     */
    private static final int MAX_RETRY = 5;

    /**
     * 重试间隔（毫秒）
     */
    private static final long RETRY_INTERVAL_MS = 2000;

    /**
     * 订阅前的等待时间（毫秒），确保连接稳定
     */
    private static final long INITIAL_DELAY_MS = 1000;

    /**
     * Subscribe to default topics after bean initialization.
     * Includes initial delay to ensure connection is stable,
     * and retry logic to handle transient connection issues.
     */
    @PostConstruct
    public void subscribe() {
        if (mqttClient == null) {
            log.warn("MQTT client not available, skip subscription");
            return;
        }

        // 使用新线程执行订阅，避免阻塞 Spring 容器启动
        Thread subscribeThread = new Thread(() -> {
            try {
                // 等待连接稳定
                Thread.sleep(INITIAL_DELAY_MS);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                log.warn("MQTT subscribe thread interrupted during initial delay");
                return;
            }

            for (int attempt = 1; attempt <= MAX_RETRY; attempt++) {
                if (mqttClient == null || !mqttClient.isConnected()) {
                    log.warn("MQTT client not connected (attempt {}/{}), retrying in {}ms...",
                            attempt, MAX_RETRY, RETRY_INTERVAL_MS);
                    sleepQuietly(RETRY_INTERVAL_MS);
                    continue;
                }

                log.info("MQTT client is connected, subscribing to {} topics (attempt {})",
                        DEFAULT_TOPICS.size(), attempt);

                boolean allSuccess = true;
                for (String topic : DEFAULT_TOPICS) {
                    try {
                        mqttClient.subscribe(topic, 1);
                        log.info("Subscribed to MQTT topic: {}", topic);
                    } catch (MqttException e) {
                        log.error("Failed to subscribe topic {} (attempt {}): {}", topic, attempt, e.getMessage());
                        allSuccess = false;
                    }
                }

                if (allSuccess) {
                    log.info("All MQTT topics subscribed successfully");
                    return;
                }

                if (attempt < MAX_RETRY) {
                    log.warn("Some topics failed to subscribe, retrying in {}ms...", RETRY_INTERVAL_MS);
                    sleepQuietly(RETRY_INTERVAL_MS);
                }
            }

            log.error("Failed to subscribe to all MQTT topics after {} attempts", MAX_RETRY);
        }, "mqtt-subscribe");

        subscribeThread.setDaemon(true);
        subscribeThread.start();
    }

    /**
     * Subscribe to a specific topic with QoS level 1.
     *
     * @param topic MQTT topic to subscribe
     */
    public void subscribeTopic(String topic) {
        if (mqttClient == null || !mqttClient.isConnected()) {
            log.warn("MQTT client not available, cannot subscribe to {}", topic);
            return;
        }
        try {
            mqttClient.subscribe(topic, 1);
            log.info("Subscribed to MQTT topic: {}", topic);
        } catch (MqttException e) {
            log.error("Failed to subscribe topic {}: {}", topic, e.getMessage());
        }
    }

    /**
     * Unsubscribe from a specific topic.
     *
     * @param topic MQTT topic to unsubscribe
     */
    public void unsubscribeTopic(String topic) {
        if (mqttClient == null || !mqttClient.isConnected()) {
            return;
        }
        try {
            mqttClient.unsubscribe(topic);
            log.info("Unsubscribed from MQTT topic: {}", topic);
        } catch (MqttException e) {
            log.error("Failed to unsubscribe topic {}: {}", topic, e.getMessage());
        }
    }

    /**
     * Check if MQTT client is connected.
     *
     * @return true if connected, false otherwise
     */
    public boolean isConnected() {
        return mqttClient != null && mqttClient.isConnected();
    }

    /**
     * Sleep without throwing InterruptedException.
     *
     * @param millis sleep duration in milliseconds
     */
    private void sleepQuietly(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
