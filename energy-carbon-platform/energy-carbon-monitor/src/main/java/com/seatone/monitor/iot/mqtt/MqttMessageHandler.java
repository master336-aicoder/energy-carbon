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

/**
 * MQTT message handler.
 * Implements MqttCallback to process incoming MQTT messages from IoT devices.
 * Parses JSON payload and writes energy data to time-series database.
 *
 * <p>Expected message format:
 * <pre>
 * {
 *   "deviceCode": "DEV001",
 *   "metric": "power",
 *   "value": 123.45,
 *   "timestamp": 1700000000000
 * }
 * </pre>
 *
 * @author seatone
 * @since 2026-06-22
 */
@Slf4j
@Component
public class MqttMessageHandler implements MqttCallback {

    @Autowired(required = false)
    private TimeSeriesService timeSeriesService;

    /**
     * Called when MQTT connection is lost.
     * Logs warning and waits for auto-reconnect.
     *
     * @param cause the reason for disconnection
     */
    @Override
    public void connectionLost(Throwable cause) {
        log.warn("MQTT connection lost: {}", cause.getMessage());
    }

    /**
     * Called when a message arrives from the MQTT broker.
     * Parses JSON payload and writes to time-series database.
     *
     * @param topic   the topic the message was published to
     * @param message the incoming message
     */
    @Override
    public void messageArrived(String topic, MqttMessage message) {
        String payload = new String(message.getPayload());
        log.debug("MQTT message arrived - topic: {}, payload: {}", topic, payload);

        if (timeSeriesService == null) {
            log.warn("TimeSeriesService not available, skip processing MQTT message");
            return;
        }

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
                log.debug("Successfully processed MQTT message for device: {}", deviceCode);
            } else {
                log.warn("Invalid MQTT message format, missing required fields: {}", payload);
            }
        } catch (Exception e) {
            log.error("Failed to process MQTT message: {}, error: {}", payload, e.getMessage());
        }
    }

    /**
     * Called when message delivery is complete.
     * Not used for subscriber-only clients.
     *
     * @param token delivery token
     */
    @Override
    public void deliveryComplete(IMqttDeliveryToken token) {
        // No-op for subscriber-only client
    }
}
