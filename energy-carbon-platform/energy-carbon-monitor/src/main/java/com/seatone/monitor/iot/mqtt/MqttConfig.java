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
 * MQTT 配置类
 * 配置 MQTT 客户端连接，用于接收物联网设备上报的能耗数据
 * 支持自动重连和认证配置
 *
 * @author seatone
 * @since 2026-06-22
 */
@Slf4j
@Configuration
public class MqttConfig {

    /**
     * MQTT Broker 地址
     * 默认：tcp://localhost:1883
     */
    @Value("${mqtt.broker:tcp://localhost:1883}")
    private String brokerUrl;

    /**
     * MQTT 客户端ID
     * 默认：energy-carbon-monitor
     */
    @Value("${mqtt.clientId:energy-carbon-monitor}")
    private String clientId;

    /**
     * MQTT 用户名（可选）
     */
    @Value("${mqtt.username:}")
    private String username;

    /**
     * MQTT 密码（可选）
     */
    @Value("${mqtt.password:}")
    private String password;

    /**
     * 连接超时时间（秒）
     * 默认：30秒
     */
    @Value("${mqtt.connectionTimeout:30}")
    private int connectionTimeout;

    /**
     * 心跳间隔（秒）
     * 默认：60秒
     */
    @Value("${mqtt.keepAliveInterval:60}")
    private int keepAliveInterval;

    /**
     * 是否清除会话
     * 默认：true（每次连接清除之前的会话状态）
     */
    @Value("${mqtt.cleanSession:true}")
    private boolean cleanSession;

    /**
     * MQTT 消息处理器
     */
    @Autowired
    private MqttMessageHandler mqttMessageHandler;

    /**
     * 创建 MQTT 客户端 Bean
     * 配置连接选项并建立与 Broker 的连接
     *
     * @return MQTT 客户端实例，连接失败返回 null
     */
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
            options.setAutomaticReconnect(true);
            client.connect(options);
            log.info("MQTT client connected to broker: {}", brokerUrl);
            client.setCallback(mqttMessageHandler);
            return client;
        } catch (MqttException e) {
            log.error("MQTT client initialization failed: {}", e.getMessage());
            return null;
        }
    }
}
