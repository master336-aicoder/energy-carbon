package com.seatone.monitor.iot.mqtt;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.seatone.monitor.iot.timeseries.TimeSeriesService;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
public class MqttMessageHandler implements MqttCallback {

    @Autowired
    private TimeSeriesService timeSeriesService;

    @Override
    public void connectionLost(Throwable cause) {
        log.warn("MQTT connection lost: {}", cause.getMessage());
    }

    @Override
    public void messageArrived(String topic, MqttMessage message) {
        String payload = new String(message.getPayload());
        log.debug("MQTT message arrived - topic: {}, payload: {}", topic, payload);
        try {
            JSONObject json = JSON.parseObject(payload);
            String deviceCode = json.getString("deviceCode");
            String metric = json.getString("metric");
            Double value = json.getDouble("value");
            Long timestamp = json.getLong("timestamp");
            if (timestamp == null) {
                timestamp = Instant.now().toEpochMilli();
            }
            if (deviceCode != null && metric != null && value != null) {
                Map<String, String> tags = new HashMap<>();
                tags.put("topic", topic);
                tags.put("deviceCode", deviceCode);
                tags.put("source", "mqtt");
                timeSeriesService.writePoint(deviceCode, metric, value, timestamp, tags);
            } else {
                log.warn("Invalid MQTT message format: {}", payload);
            }
        } catch (Exception e) {
            log.error("Failed to process MQTT message: {}", e.getMessage());
        }
    }

    @Override
    public void deliveryComplete(IMqttDeliveryToken token) {
    }
}
