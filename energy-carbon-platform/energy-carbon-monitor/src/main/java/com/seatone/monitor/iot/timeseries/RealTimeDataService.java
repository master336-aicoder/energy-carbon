package com.seatone.monitor.iot.timeseries;

import lombok.extern.slf4j.Slf4j;
import org.influxdb.InfluxDB;
import org.influxdb.dto.Query;
import org.influxdb.dto.QueryResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.*;

@Slf4j
@Service
public class RealTimeDataService {

    @Autowired
    private InfluxDB influxDB;

    @Value("${influxdb.database:energy_carbon}")
    private String database;

    public List<Map<String, Object>> queryLatestData(String measurement, int limit) {
        List<Map<String, Object>> result = new ArrayList<>();
        if (influxDB == null) {
            return result;
        }
        try {
            String sql = String.format("SELECT * FROM \"%s\" ORDER BY time DESC LIMIT %d", measurement, limit);
            QueryResult queryResult = influxDB.query(new Query(sql, database));
            if (queryResult.getResults() != null) {
                for (QueryResult.Result r : queryResult.getResults()) {
                    if (r.getSeries() != null) {
                        for (QueryResult.Series series : r.getSeries()) {
                            List<String> columns = series.getColumns();
                            for (List<Object> values : series.getValues()) {
                                Map<String, Object> row = new LinkedHashMap<>();
                                for (int i = 0; i < columns.size(); i++) {
                                    row.put(columns.get(i), values.get(i));
                                }
                                result.add(row);
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            log.error("Failed to query latest data: {}", e.getMessage());
        }
        return result;
    }

    public List<Map<String, Object>> queryRangeData(String measurement, String field, long startTime, long endTime) {
        List<Map<String, Object>> result = new ArrayList<>();
        if (influxDB == null) {
            return result;
        }
        try {
            String sql = String.format(
                    "SELECT time, \"%s\" FROM \"%s\" WHERE time >= %dms AND time <= %dms ORDER BY time ASC",
                    field, measurement, startTime, endTime);
            QueryResult queryResult = influxDB.query(new Query(sql, database));
            if (queryResult.getResults() != null) {
                for (QueryResult.Result r : queryResult.getResults()) {
                    if (r.getSeries() != null) {
                        for (QueryResult.Series series : r.getSeries()) {
                            List<String> columns = series.getColumns();
                            for (List<Object> values : series.getValues()) {
                                Map<String, Object> row = new LinkedHashMap<>();
                                for (int i = 0; i < columns.size(); i++) {
                                    row.put(columns.get(i), values.get(i));
                                }
                                result.add(row);
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            log.error("Failed to query range data: {}", e.getMessage());
        }
        return result;
    }

    public List<Map<String, Object>> queryAllMeasurements() {
        List<Map<String, Object>> result = new ArrayList<>();
        if (influxDB == null) {
            return result;
        }
        try {
            String sql = "SHOW MEASUREMENTS";
            QueryResult queryResult = influxDB.query(new Query(sql, database));
            if (queryResult.getResults() != null) {
                for (QueryResult.Result r : queryResult.getResults()) {
                    if (r.getSeries() != null) {
                        for (QueryResult.Series series : r.getSeries()) {
                            for (List<Object> values : series.getValues()) {
                                Map<String, Object> row = new HashMap<>();
                                row.put("name", values.get(0));
                                result.add(row);
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            log.error("Failed to query measurements: {}", e.getMessage());
        }
        return result;
    }
}
