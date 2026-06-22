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

/**
 * InfluxDB 配置类
 * 配置 InfluxDB 时序数据库连接，用于存储和查询物联网设备的能耗时序数据
 * 支持自动创建数据库、批量写入和连接池配置
 *
 * @author seatone
 * @since 2026-06-22
 */
@Slf4j
@Configuration
public class InfluxDBConfig {

    /**
     * InfluxDB 连接地址
     * 默认：http://localhost:8086
     */
    @Value("${influxdb.url:http://localhost:8086}")
    private String url;

    /**
     * InfluxDB 用户名
     * 默认：root
     */
    @Value("${influxdb.username:root}")
    private String username;

    /**
     * InfluxDB 密码
     * 默认：root
     */
    @Value("${influxdb.password:root}")
    private String password;

    /**
     * InfluxDB 数据库名称
     * 默认：energy_carbon
     */
    @Value("${influxdb.database:energy_carbon}")
    private String database;

    /**
     * 数据保留策略
     * 默认：autogen
     */
    @Value("${influxdb.retentionPolicy:autogen}")
    private String retentionPolicy;

    /**
     * 创建 InfluxDB 客户端 Bean
     * 配置连接超时、读写超时，自动创建数据库，启用批量写入
     *
     * @return InfluxDB 客户端实例
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

    /**
     * 创建数据库名称 Bean
     * 供其他组件注入使用
     *
     * @return 数据库名称
     */
    @Bean
    public String influxDatabase() {
        return database;
    }
}
