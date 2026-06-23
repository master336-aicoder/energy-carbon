package com.seatone.admin;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication(scanBasePackages = "com.seatone")
@MapperScan("com.seatone.**.mapper")
@EnableDiscoveryClient
@EnableFeignClients(basePackages = "com.seatone")
public class EnergyCarbonApplication {

    public static void main(String[] args) {
        SpringApplication.run(EnergyCarbonApplication.class, args);
    }
}
