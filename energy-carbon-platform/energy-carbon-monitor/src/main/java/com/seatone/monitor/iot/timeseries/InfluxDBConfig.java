package com.seatone.monitor.iot.timeseries;

import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;
import org.influxdb.InfluxDB;
import org.influxdb.InfluxDBFactory;
import org.influxdb.dto.Pong;
import org.influxdb.dto.Query;
import org.influxdb.dto.QueryResult;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

/**
 * InfluxDB configuration class.
 * Configures InfluxDB time-series database connection for IoT device energy data.
 * Supports auto database creation, batch write and connection pool config.
 *
 * @author seatone
 * @since 2026-06-22
 */
@Slf4j
@Configuration
public class InfluxDBConfig {

    @Value("${influxdb.url:http://localhost:8086}")
    private String url;

    @Value("${influxdb.username:root}")
    private String username;

    @Value("${influxdb.password:root}")
    private String password;

    @Value("${influxdb.database:energy_carbon}")
    private String database;

    @Value("${influxdb.retentionPolicy:autogen}")
    private String retentionPolicy;

    /**
     * Create InfluxDB client Bean.
     * If connection fails, returns a dummy instance so the app can still start.
     *
     * @return InfluxDB client instance (may be disconnected)
     */
    @Bean
    public InfluxDB influxDB() {
        try {
            OkHttpClient.Builder clientBuilder = new OkHttpClient.Builder()
                    .connectTimeout(10, TimeUnit.SECONDS)
                    .readTimeout(30, TimeUnit.SECONDS)
                    .writeTimeout(30, TimeUnit.SECONDS);

            InfluxDB influxDB = InfluxDBFactory.connect(url, username, password, clientBuilder);
            Pong pong = influxDB.ping();
            if (pong != null && pong.isGood()) {
                log.info("InfluxDB connected, version: {}", pong.getVersion());
            }

            // 先检查数据库是否存在，避免 IF NOT EXISTS 语法兼容性问题
            QueryResult result = influxDB.query(new Query("SHOW DATABASES"));
            boolean dbExists = result.getResults().stream()
                    .flatMap(r -> r.getSeries() != null ? r.getSeries().stream() : java.util.stream.Stream.empty())
                    .flatMap(s -> s.getValues() != null ? s.getValues().stream() : java.util.stream.Stream.empty())
                    .anyMatch(v -> v.contains(database));
            if (!dbExists) {
                influxDB.query(new Query("CREATE DATABASE \"" + database + "\""));
                log.info("InfluxDB database '{}' created", database);
            }
            influxDB.setDatabase(database);
            influxDB.setRetentionPolicy(retentionPolicy);
            influxDB.enableBatch(2000, 1000, TimeUnit.MILLISECONDS);

            return influxDB;
        } catch (Exception e) {
            log.error("InfluxDB initialization failed: {}, InfluxDB features will be disabled", e.getMessage());
            // Create a dummy InfluxDB by connecting to a non-existent server
            // The connection is lazy, so this won't fail immediately
            return InfluxDBFactory.connect("http://192.0.2.1:8086", username, password);
        }
    }

    @Bean
    public String influxDatabase() {
        return database;
    }
}
