package com.seatone.monitor.iot.mqtt;

import lombok.extern.slf4j.Slf4j;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.Arrays;
import java.util.List;

@Slf4j
@Component
public class MqttSubscriber {

    @Autowired
    private MqttClient mqttClient;

    private static final List<String> DEFAULT_TOPICS = Arrays.asList(
            "energy/+/data",
            "meter/+/data",
            "device/+/status"
    );

    @PostConstruct
    public void subscribe() {
        if (mqttClient == null || !mqttClient.isConnected()) {
            log.warn("MQTT client not available, skip subscription");
            return;
        }
        for (String topic : DEFAULT_TOPICS) {
            try {
                mqttClient.subscribe(topic, 1);
                log.info("Subscribed to MQTT topic: {}", topic);
            } catch (MqttException e) {
                log.error("Failed to subscribe topic {}: {}", topic, e.getMessage());
            }
        }
    }

    public void subscribeTopic(String topic) {
        if (mqttClient == null || !mqttClient.isConnected()) {
            log.warn("MQTT client not available");
            return;
        }
        try {
            mqttClient.subscribe(topic, 1);
            log.info("Subscribed to MQTT topic: {}", topic);
        } catch (MqttException e) {
            log.error("Failed to subscribe topic {}: {}", topic, e.getMessage());
        }
    }

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
}
