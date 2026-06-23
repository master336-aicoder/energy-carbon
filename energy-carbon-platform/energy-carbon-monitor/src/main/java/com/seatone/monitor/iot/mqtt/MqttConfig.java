package com.seatone.monitor.iot.mqtt;

import lombok.extern.slf4j.Slf4j;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * MQTT configuration class.
 * Configures MQTT client connection for receiving IoT device energy data.
 * Supports auto reconnect and authentication.
 * If connection fails, a dummy client is created so the app can still start.
 *
 * @author seatone
 * @since 2026-06-22
 */
@Slf4j
@Configuration
public class MqttConfig {

    @Value("${mqtt.broker:tcp://localhost:1883}")
    private String brokerUrl;

    @Value("${mqtt.clientId:energy-carbon-monitor}")
    private String clientId;

    @Value("${mqtt.username:}")
    private String username;

    @Value("${mqtt.password:}")
    private String password;

    @Value("${mqtt.connectionTimeout:30}")
    private int connectionTimeout;

    @Value("${mqtt.keepAliveInterval:60}")
    private int keepAliveInterval;

    @Value("${mqtt.cleanSession:true}")
    private boolean cleanSession;

    @Value("${mqtt.maxReconnectDelay:128}")
    private int maxReconnectDelay;

    @Autowired
    private MqttMessageHandler mqttMessageHandler;

    @Bean
    public MqttClient mqttClient() {
        try {
            MqttClient client = new MqttClient(brokerUrl, clientId, new MemoryPersistence());
            MqttConnectOptions options = new MqttConnectOptions();
            options.setConnectionTimeout(connectionTimeout);
            options.setKeepAliveInterval(keepAliveInterval);
            options.setCleanSession(cleanSession);
            if (username != null && !username.isEmpty()) {
                options.setUserName(username);
            }
            if (password != null && !password.isEmpty()) {
                options.setPassword(password.toCharArray());
            }
            // 自动重连
            options.setAutomaticReconnect(true);
            // 设置最大重连延迟（秒），避免重连间隔过长
            options.setMaxReconnectDelay(maxReconnectDelay);

            // 在 connect 之前设置 callback，确保 connectionLost 等回调能被正确触发
            client.setCallback(mqttMessageHandler);
            log.info("MQTT callback set, connecting to broker: {}", brokerUrl);

            client.connect(options);
            log.info("MQTT client connected successfully to broker: {}, clientId: {}", brokerUrl, clientId);
            return client;
        } catch (MqttException e) {
            log.error("MQTT client initialization failed: {}, MQTT features will be disabled", e.getMessage());
            try {
                return new MqttClient("tcp://192.0.2.1:1883", clientId + "-dummy", new MemoryPersistence());
            } catch (MqttException ex) {
                log.error("Failed to create dummy MQTT client: {}", ex.getMessage());
                try {
                    return new MqttClient("tcp://0.0.0.0:0", clientId + "-dummy2", new MemoryPersistence());
                } catch (MqttException ex2) {
                    throw new RuntimeException("Cannot create MQTT client", ex2);
                }
            }
        }
    }
}
