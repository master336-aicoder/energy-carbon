package com.seatone.common.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springdoc.core.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Swagger / Knife4j 接口文档配置
 * 配置 OpenAPI 信息和接口分组
 */
@Configuration
public class SwaggerConfig {

    /**
     * 配置 OpenAPI 基本信息
     */
    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("能碳管理平台 API 文档")
                        .description("世通AI+能碳管理平台后端接口文档")
                        .version("v1.0.0")
                        .contact(new Contact()
                                .name("seatone")
                                .email("support@seatone.com")
                                .url("https://www.seatone.com"))
                        .license(new License()
                                .name("Apache 2.0")
                                .url("https://www.apache.org/licenses/LICENSE-2.0")))
                // 配置全局安全认证（JWT Token）
                .addSecurityItem(new SecurityRequirement().addList("Authorization"))
                .components(new io.swagger.v3.oas.models.Components()
                        .addSecuritySchemes("Authorization",
                                new SecurityScheme()
                                        .name("Authorization")
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT")
                                        .description("请输入 JWT Token，格式：Bearer {token}")));
    }

    /**
     * 系统管理分组
     */
    @Bean
    public GroupedOpenApi systemApi() {
        return GroupedOpenApi.builder()
                .group("系统管理")
                .pathsToMatch("/system/**", "/auth/**", "/user/**", "/role/**", "/menu/**", "/dept/**", "/dict/**")
                .build();
    }

    /**
     * 监控管理分组
     */
    @Bean
    public GroupedOpenApi monitorApi() {
        return GroupedOpenApi.builder()
                .group("监控管理")
                .pathsToMatch("/monitor/**", "/device/**", "/realtime/**")
                .build();
    }

    /**
     * 告警管理分组
     */
    @Bean
    public GroupedOpenApi alarmApi() {
        return GroupedOpenApi.builder()
                .group("告警管理")
                .pathsToMatch("/alarm/**", "/alarm-rule/**", "/alarm-record/**")
                .build();
    }

    /**
     * 碳排管理分组
     */
    @Bean
    public GroupedOpenApi carbonApi() {
        return GroupedOpenApi.builder()
                .group("碳排管理")
                .pathsToMatch("/carbon/**", "/emission/**", "/carbon-factor/**", "/carbon-report/**")
                .build();
    }

    /**
     * 报表管理分组
     */
    @Bean
    public GroupedOpenApi reportApi() {
        return GroupedOpenApi.builder()
                .group("报表管理")
                .pathsToMatch("/report/**", "/report-template/**", "/report-instance/**")
                .build();
    }

    /**
     * 全部接口分组
     */
    @Bean
    public GroupedOpenApi allApi() {
        return GroupedOpenApi.builder()
                .group("全部接口")
                .pathsToMatch("/**")
                .build();
    }
}
