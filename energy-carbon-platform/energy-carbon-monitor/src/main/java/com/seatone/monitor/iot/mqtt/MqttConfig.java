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
