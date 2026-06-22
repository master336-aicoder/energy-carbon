package com.seatone.admin;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "com.seatone")
@MapperScan("com.seatone.**.mapper")
public class EnergyCarbonApplication {

    public static void main(String[] args) {
        SpringApplication.run(EnergyCarbonApplication.class, args);
    }
}
