package com.seatone.monitor.iot.http;

import com.seatone.monitor.iot.timeseries.TimeSeriesService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
public class HttpDataService {

    @Autowired
    private TimeSeriesService timeSeriesService;

    public void processData(Map<String, Object> data) {
        try {
            String deviceCode = (String) data.get("deviceCode");
            String metric = (String) data.get("metric");
            Object valueObj = data.get("value");
            Object tsObj = data.get("timestamp");

            if (deviceCode == null || metric == null || valueObj == null) {
                log.warn("Invalid HTTP data format: {}", data);
                return;
            }

            Double value;
            if (valueObj instanceof Number) {
                value = ((Number) valueObj).doubleValue();
            } else {
                value = Double.parseDouble(valueObj.toString());
            }

            Long timestamp;
            if (tsObj instanceof Number) {
                timestamp = ((Number) tsObj).longValue();
            } else {
                timestamp = Instant.now().toEpochMilli();
            }

            Map<String, String> tags = new HashMap<>();
            tags.put("source", "http");
            tags.put("deviceCode", deviceCode);
            if (data.containsKey("unit")) {
                tags.put("unit", (String) data.get("unit"));
            }

            timeSeriesService.writePoint(deviceCode, metric, value, timestamp, tags);
            log.debug("HTTP data processed - device: {}, metric: {}, value: {}", deviceCode, metric, value);
        } catch (Exception e) {
            log.error("Failed to process HTTP data: {}", e.getMessage());
        }
    }
}
