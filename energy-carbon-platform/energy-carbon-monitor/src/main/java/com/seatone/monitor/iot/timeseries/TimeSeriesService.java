package com.seatone.monitor.iot.timeseries;

import lombok.extern.slf4j.Slf4j;
import org.influxdb.InfluxDB;
import org.influxdb.dto.Point;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
public class TimeSeriesService {

    @Autowired
    private InfluxDB influxDB;

    public void writePoint(String measurement, String field, Double value, Long timestamp, Map<String, String> tags) {
        if (influxDB == null) {
            log.warn("InfluxDB not available, skip writing point");
            return;
        }
        try {
            Point.Builder builder = Point.measurement(measurement)
                    .time(timestamp, TimeUnit.MILLISECONDS)
                    .addField(field, value);
            if (tags != null) {
                for (Map.Entry<String, String> entry : tags.entrySet()) {
                    if (entry.getValue() != null) {
                        builder.tag(entry.getKey(), entry.getValue());
                    }
                }
            }
            influxDB.write(builder.build());
        } catch (Exception e) {
            log.error("Failed to write point to InfluxDB: {}", e.getMessage());
        }
    }

    public void writePoint(String measurement, Map<String, Object> fields, Long timestamp, Map<String, String> tags) {
        if (influxDB == null) {
            log.warn("InfluxDB not available, skip writing point");
            return;
        }
        try {
            Point.Builder builder = Point.measurement(measurement)
                    .time(timestamp, TimeUnit.MILLISECONDS);
            for (Map.Entry<String, Object> entry : fields.entrySet()) {
                if (entry.getValue() instanceof Number) {
                    builder.addField(entry.getKey(), ((Number) entry.getValue()).doubleValue());
                } else if (entry.getValue() instanceof String) {
                    builder.addField(entry.getKey(), (String) entry.getValue());
                } else if (entry.getValue() instanceof Boolean) {
                    builder.addField(entry.getKey(), (Boolean) entry.getValue());
                }
            }
            if (tags != null) {
                for (Map.Entry<String, String> entry : tags.entrySet()) {
                    if (entry.getValue() != null) {
                        builder.tag(entry.getKey(), entry.getValue());
                    }
                }
            }
            influxDB.write(builder.build());
        } catch (Exception e) {
            log.error("Failed to write point to InfluxDB: {}", e.getMessage());
        }
    }
}
