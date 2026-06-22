package com.seatone.admin.generator;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.generator.FastAutoGenerator;
import com.baomidou.mybatisplus.generator.config.OutputFile;
import com.baomidou.mybatisplus.generator.config.rules.DateType;
import com.baomidou.mybatisplus.generator.config.rules.NamingStrategy;
import com.baomidou.mybatisplus.generator.engine.VelocityTemplateEngine;
import com.baomidou.mybatisplus.generator.fill.Column;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * MyBatis Plus 代码生成器
 * 支持所有模块的代码生成
 */
public class CodeGenerator {

    // 数据库配置
    private static final String DB_URL = "jdbc:mysql://localhost:3306/energy_carbon?useUnicode=true&characterEncoding=utf8&zeroDateTimeBehavior=convertToNull&useSSL=true&serverTimezone=GMT%2B8";
    private static final String DB_USERNAME = "root";
    private static final String DB_PASSWORD = "123456";

    // 作者信息
    private static final String AUTHOR = "seatone";

    // 项目根路径
    private static final String PROJECT_PATH = System.getProperty("user.dir");

    public static void main(String[] args) {
        // 交互式生成：选择模块和表
        System.out.println("========== MyBatis Plus 代码生成器 ==========");
        System.out.println("请选择要生成代码的模块:");
        System.out.println("1. system (系统模块)");
        System.out.println("2. monitor (监控模块)");
        System.out.println("3. alarm (告警模块)");
        System.out.println("4. carbon (碳排模块)");
        System.out.println("5. report (报表模块)");
        System.out.println("6. 全部模块");
        System.out.println("============================================");

        // 默认生成系统模块核心表
        generateModule("system", "sys_");
    }

    /**
     * 生成指定模块的代码
     *
     * @param moduleName 模块名称
     * @param tablePrefix 表前缀
     */
    public static void generateModule(String moduleName, String tablePrefix) {
        String modulePath = PROJECT_PATH + "/energy-carbon-" + moduleName;
        String javaPath = modulePath + "/src/main/java";
        String mapperPath = modulePath + "/src/main/resources/mapper";

        FastAutoGenerator.create(DB_URL, DB_USERNAME, DB_PASSWORD)
                // 全局配置
                .globalConfig(builder -> builder
                        .author(AUTHOR)
                        .outputDir(javaPath)
                        .dateType(DateType.ONLY_DATE)
                        .commentDate("yyyy-MM-dd")
                        .disableOpenDir()
                )
                // 包配置
                .packageConfig(builder -> builder
                        .parent("com.seatone")
                        .moduleName(moduleName)
                        .entity("entity")
                        .service("service")
                        .serviceImpl("service.impl")
                        .mapper("mapper")
                        .xml("mapper")
                        .controller("controller")
                        .pathInfo(Collections.singletonMap(OutputFile.xml, mapperPath))
                )
                // 策略配置
                .strategyConfig(builder -> builder
                        // 添加表前缀匹配
                        .addInclude(getTables(moduleName))
                        .addTablePrefix(tablePrefix)
                        // Entity 策略
                        .entityBuilder()
                        .idType(IdType.AUTO)
                        .enableLombok()
                        .enableChainModel()
                        .naming(NamingStrategy.underline_to_camel)
                        .columnNaming(NamingStrategy.underline_to_camel)
                        .addSuperEntityColumns("id", "create_time", "update_time", "create_by", "update_by")
                        .superClass("com.seatone.common.base.BaseEntity")
                        .addIgnoreColumns("deleted")
                        .enableTableFieldAnnotation()
                        .addTableFills(
                                new Column("create_time", FieldFill.INSERT),
                                new Column("update_time", FieldFill.INSERT_UPDATE),
                                new Column("create_by", FieldFill.INSERT),
                                new Column("update_by", FieldFill.INSERT_UPDATE)
                        )
                        .enableActiveRecord()
                        // Service 策略
                        .serviceBuilder()
                        .formatServiceFileName("%sService")
                        .formatServiceImplFileName("%sServiceImpl")
                        // Mapper 策略
                        .mapperBuilder()
                        .superClass(BaseMapper.class)
                        .enableMapperAnnotation()
                        .enableBaseResultMap()
                        .enableBaseColumnList()
                        // Controller 策略
                        .controllerBuilder()
                        .enableRestStyle()
                        .enableHyphenStyle()
                )
                // 模板配置
                .templateEngine(new VelocityTemplateEngine())
                // 执行生成
                .execute();

        System.out.println("模块 [" + moduleName + "] 代码生成完成！");
        System.out.println("Java 代码路径: " + javaPath);
        System.out.println("Mapper XML 路径: " + mapperPath);
    }

    /**
     * 生成指定表的代码
     *
     * @param moduleName 模块名称
     * @param tableNames 表名列表
     */
    public static void generateTables(String moduleName, String... tableNames) {
        String modulePath = PROJECT_PATH + "/energy-carbon-" + moduleName;
        String javaPath = modulePath + "/src/main/java";
        String mapperPath = modulePath + "/src/main/resources/mapper";

        FastAutoGenerator.create(DB_URL, DB_USERNAME, DB_PASSWORD)
                .globalConfig(builder -> builder
                        .author(AUTHOR)
                        .outputDir(javaPath)
                        .dateType(DateType.ONLY_DATE)
                        .commentDate("yyyy-MM-dd")
                        .disableOpenDir()
                )
                .packageConfig(builder -> builder
                        .parent("com.seatone")
                        .moduleName(moduleName)
                        .entity("entity")
                        .service("service")
                        .serviceImpl("service.impl")
                        .mapper("mapper")
                        .xml("mapper")
                        .controller("controller")
                        .pathInfo(Collections.singletonMap(OutputFile.xml, mapperPath))
                )
                .strategyConfig(builder -> builder
                        .addInclude(tableNames)
                        .entityBuilder()
                        .idType(IdType.AUTO)
                        .enableLombok()
                        .enableChainModel()
                        .naming(NamingStrategy.underline_to_camel)
                        .columnNaming(NamingStrategy.underline_to_camel)
                        .addSuperEntityColumns("id", "create_time", "update_time", "create_by", "update_by")
                        .superClass("com.seatone.common.base.BaseEntity")
                        .addIgnoreColumns("deleted")
                        .enableTableFieldAnnotation()
                        .addTableFills(
                                new Column("create_time", FieldFill.INSERT),
                                new Column("update_time", FieldFill.INSERT_UPDATE),
                                new Column("create_by", FieldFill.INSERT),
                                new Column("update_by", FieldFill.INSERT_UPDATE)
                        )
                        .enableActiveRecord()
                        .serviceBuilder()
                        .formatServiceFileName("%sService")
                        .formatServiceImplFileName("%sServiceImpl")
                        .mapperBuilder()
                        .superClass(BaseMapper.class)
                        .enableMapperAnnotation()
                        .enableBaseResultMap()
                        .enableBaseColumnList()
                        .controllerBuilder()
                        .enableRestStyle()
                        .enableHyphenStyle()
                )
                .templateEngine(new VelocityTemplateEngine())
                .execute();

        System.out.println("表 " + Arrays.toString(tableNames) + " 代码生成完成！");
    }

    /**
     * 获取模块对应的表名列表
     */
    private static List<String> getTables(String moduleName) {
        switch (moduleName) {
            case "system":
                return Arrays.asList(
                        "sys_user", "sys_role", "sys_menu", "sys_dept",
                        "sys_user_role", "sys_role_menu", "sys_oper_log",
                        "sys_dict_type", "sys_dict_data"
                );
            case "monitor":
                return Arrays.asList(
                        "monitor_device", "monitor_data", "monitor_point"
                );
            case "alarm":
                return Arrays.asList(
                        "alarm_rule", "alarm_record", "alarm_notice"
                );
            case "carbon":
                return Arrays.asList(
                        "carbon_emission", "carbon_factor", "carbon_report"
                );
            case "report":
                return Arrays.asList(
                        "report_template", "report_instance", "report_schedule"
                );
            default:
                return Collections.emptyList();
        }
    }
}
