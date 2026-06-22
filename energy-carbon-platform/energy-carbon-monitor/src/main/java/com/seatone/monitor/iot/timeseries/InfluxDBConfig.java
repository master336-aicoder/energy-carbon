package com.seatone.monitor.iot.timeseries;

import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;
import org.influxdb.InfluxDB;
import org.influxdb.InfluxDBFactory;
import org.influxdb.dto.Pong;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

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

            influxDB.query(new org.influxdb.dto.Query("CREATE DATABASE IF NOT EXISTS \"" + database + "\""));
            influxDB.setDatabase(database);
            influxDB.setRetentionPolicy(retentionPolicy);
            influxDB.enableBatch(2000, 1000, TimeUnit.MILLISECONDS);

            return influxDB;
        } catch (Exception e) {
            log.error("InfluxDB initialization failed: {}", e.getMessage());
            return null;
        }
    }

    @Bean
    public String influxDatabase() {
        return database;
    }
}
