-- ============================================================
-- 世通AI+能碳管理平台 - 统一初始化SQL
-- 包含：建表语句 + 基础数据 + 测试数据
-- 数据库: energy_carbon
-- 编码: UTF-8
-- 创建日期: 2026-06-24
-- 注意: INSERT语句列名与schema_pure.sql完全匹配
-- ============================================================

USE energy_carbon;

-- ============================================================
-- 第一部分：建表语句 (57张表)
-- ============================================================

-- 1. sys_user
DROP TABLE IF EXISTS sys_user;
CREATE TABLE sys_user (
    id BIGINT NOT NULL AUTO_INCREMENT,
    username VARCHAR(50) NOT NULL,
    password VARCHAR(100) NOT NULL,
    nickname VARCHAR(50) DEFAULT NULL,
    phone VARCHAR(20) DEFAULT NULL,
    email VARCHAR(50) DEFAULT NULL,
    avatar VARCHAR(200) DEFAULT NULL,
    dept_id BIGINT DEFAULT NULL,
    status TINYINT DEFAULT 1,
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    create_by BIGINT DEFAULT NULL,
    update_by BIGINT DEFAULT NULL,
    PRIMARY KEY (id),
    UNIQUE KEY uk_username (username),
    KEY idx_dept_id (dept_id),
    KEY idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='系统用户表';

-- 2. sys_role
DROP TABLE IF EXISTS sys_role;
CREATE TABLE sys_role (
    id BIGINT NOT NULL AUTO_INCREMENT,
    role_name VARCHAR(50) NOT NULL,
    role_code VARCHAR(50) NOT NULL,
    remark VARCHAR(500) DEFAULT NULL,
    sort INT DEFAULT 0,
    status TINYINT DEFAULT 1,
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    create_by BIGINT DEFAULT NULL,
    update_by BIGINT DEFAULT NULL,
    PRIMARY KEY (id),
    UNIQUE KEY uk_role_code (role_code),
    KEY idx_role_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='系统角色表';

-- 3. sys_menu
DROP TABLE IF EXISTS sys_menu;
CREATE TABLE sys_menu (
    id BIGINT NOT NULL AUTO_INCREMENT,
    menu_name VARCHAR(50) NOT NULL,
    parent_id BIGINT DEFAULT 0,
    sort INT DEFAULT 0,
    path VARCHAR(200) DEFAULT NULL,
    component VARCHAR(200) DEFAULT NULL,
    icon VARCHAR(50) DEFAULT NULL,
    menu_type TINYINT DEFAULT 1,
    status TINYINT DEFAULT 1,
    permission VARCHAR(100) DEFAULT NULL,
    hidden TINYINT DEFAULT 0,
    keep_alive TINYINT DEFAULT 0,
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    create_by BIGINT DEFAULT NULL,
    update_by BIGINT DEFAULT NULL,
    PRIMARY KEY (id),
    KEY idx_parent_id (parent_id),
    KEY idx_menu_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='系统菜单表';

-- 4. sys_dept
DROP TABLE IF EXISTS sys_dept;
CREATE TABLE sys_dept (
    id BIGINT NOT NULL AUTO_INCREMENT,
    dept_name VARCHAR(50) NOT NULL,
    parent_id BIGINT DEFAULT 0,
    sort INT DEFAULT 0,
    leader VARCHAR(50) DEFAULT NULL,
    phone VARCHAR(20) DEFAULT NULL,
    email VARCHAR(50) DEFAULT NULL,
    status TINYINT DEFAULT 1,
    ancestors VARCHAR(500) DEFAULT NULL,
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    create_by BIGINT DEFAULT NULL,
    update_by BIGINT DEFAULT NULL,
    PRIMARY KEY (id),
    KEY idx_dept_parent_id (parent_id),
    KEY idx_dept_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='部门表';

-- 5. sys_dict_type
DROP TABLE IF EXISTS sys_dict_type;
CREATE TABLE sys_dict_type (
    id BIGINT NOT NULL AUTO_INCREMENT,
    dict_name VARCHAR(50) NOT NULL,
    dict_type VARCHAR(50) NOT NULL,
    remark VARCHAR(500) DEFAULT NULL,
    status TINYINT DEFAULT 1,
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    create_by BIGINT DEFAULT NULL,
    update_by BIGINT DEFAULT NULL,
    PRIMARY KEY (id),
    UNIQUE KEY uk_dict_type (dict_type)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='字典类型表';

-- 6. sys_dict_data
DROP TABLE IF EXISTS sys_dict_data;
CREATE TABLE sys_dict_data (
    id BIGINT NOT NULL AUTO_INCREMENT,
    dict_type VARCHAR(50) NOT NULL,
    dict_label VARCHAR(50) NOT NULL,
    dict_value VARCHAR(50) NOT NULL,
    sort INT DEFAULT 0,
    remark VARCHAR(500) DEFAULT NULL,
    status TINYINT DEFAULT 1,
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    create_by BIGINT DEFAULT NULL,
    update_by BIGINT DEFAULT NULL,
    PRIMARY KEY (id),
    KEY idx_dict_data_type (dict_type),
    KEY idx_dict_data_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='字典数据表';

-- 7. sys_user_role
DROP TABLE IF EXISTS sys_user_role;
CREATE TABLE sys_user_role (
    id BIGINT NOT NULL AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    role_id BIGINT NOT NULL,
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    create_by BIGINT DEFAULT NULL,
    update_by BIGINT DEFAULT NULL,
    PRIMARY KEY (id),
    UNIQUE KEY uk_user_role (user_id, role_id),
    KEY idx_user_role_user (user_id),
    KEY idx_user_role_role (role_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户角色关联表';

-- 8. sys_role_menu
DROP TABLE IF EXISTS sys_role_menu;
CREATE TABLE sys_role_menu (
    id BIGINT NOT NULL AUTO_INCREMENT,
    role_id BIGINT NOT NULL,
    menu_id BIGINT NOT NULL,
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    create_by BIGINT DEFAULT NULL,
    update_by BIGINT DEFAULT NULL,
    PRIMARY KEY (id),
    UNIQUE KEY uk_role_menu (role_id, menu_id),
    KEY idx_role_menu_role (role_id),
    KEY idx_role_menu_menu (menu_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='角色菜单关联表';

-- 9. sys_oper_log
DROP TABLE IF EXISTS sys_oper_log;
CREATE TABLE sys_oper_log (
    id BIGINT NOT NULL AUTO_INCREMENT,
    title VARCHAR(100) DEFAULT NULL,
    oper_type TINYINT DEFAULT 0,
    method VARCHAR(200) DEFAULT NULL,
    request_method VARCHAR(10) DEFAULT NULL,
    oper_name VARCHAR(50) DEFAULT NULL,
    oper_url VARCHAR(500) DEFAULT NULL,
    oper_ip VARCHAR(50) DEFAULT NULL,
    oper_param TEXT DEFAULT NULL,
    json_result TEXT DEFAULT NULL,
    status TINYINT DEFAULT 1,
    error_msg TEXT DEFAULT NULL,
    oper_time DATETIME DEFAULT NULL,
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    create_by BIGINT DEFAULT NULL,
    update_by BIGINT DEFAULT NULL,
    PRIMARY KEY (id),
    KEY idx_oper_time (oper_time),
    KEY idx_oper_name (oper_name)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='操作日志表';

-- 10. sys_config
DROP TABLE IF EXISTS sys_config;
CREATE TABLE sys_config (
    id BIGINT NOT NULL AUTO_INCREMENT,
    config_name VARCHAR(100) NOT NULL,
    config_key VARCHAR(100) NOT NULL,
    config_value VARCHAR(500) DEFAULT NULL,
    remark VARCHAR(500) DEFAULT NULL,
    status TINYINT DEFAULT 1,
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    create_by BIGINT DEFAULT NULL,
    update_by BIGINT DEFAULT NULL,
    PRIMARY KEY (id),
    UNIQUE KEY uk_config_key (config_key)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='系统参数表';

-- 11. energy_device
DROP TABLE IF EXISTS energy_device;
CREATE TABLE energy_device (
    id BIGINT NOT NULL AUTO_INCREMENT,
    device_code VARCHAR(50) NOT NULL,
    device_name VARCHAR(100) NOT NULL,
    device_type VARCHAR(50) DEFAULT NULL,
    dept_id BIGINT DEFAULT NULL,
    status TINYINT DEFAULT 1,
    manufacturer VARCHAR(100) DEFAULT NULL,
    model VARCHAR(100) DEFAULT NULL,
    install_date DATE DEFAULT NULL,
    power_rating DECIMAL(18,4) DEFAULT NULL,
    voltage_level VARCHAR(20) DEFAULT NULL,
    remark VARCHAR(500) DEFAULT NULL,
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    create_by BIGINT DEFAULT NULL,
    update_by BIGINT DEFAULT NULL,
    PRIMARY KEY (id),
    UNIQUE KEY uk_device_code (device_code),
    KEY idx_device_dept (dept_id),
    KEY idx_device_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用能设备表';

-- 12. energy_meter
DROP TABLE IF EXISTS energy_meter;
CREATE TABLE energy_meter (
    id BIGINT NOT NULL AUTO_INCREMENT,
    meter_code VARCHAR(50) NOT NULL,
    meter_name VARCHAR(100) NOT NULL,
    meter_type VARCHAR(50) DEFAULT NULL,
    device_id BIGINT DEFAULT NULL,
    dept_id BIGINT DEFAULT NULL,
    status TINYINT DEFAULT 1,
    accuracy_class VARCHAR(20) DEFAULT NULL,
    range_min DECIMAL(18,4) DEFAULT NULL,
    range_max DECIMAL(18,4) DEFAULT NULL,
    install_date DATE DEFAULT NULL,
    calibration_date DATE DEFAULT NULL,
    next_calibration_date DATE DEFAULT NULL,
    remark VARCHAR(500) DEFAULT NULL,
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    create_by BIGINT DEFAULT NULL,
    update_by BIGINT DEFAULT NULL,
    PRIMARY KEY (id),
    UNIQUE KEY uk_meter_code (meter_code),
    KEY idx_meter_device (device_id),
    KEY idx_meter_dept (dept_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='计量器具表';

-- 13. energy_type
DROP TABLE IF EXISTS energy_type;
CREATE TABLE energy_type (
    id BIGINT NOT NULL AUTO_INCREMENT,
    type_code VARCHAR(50) NOT NULL,
    type_name VARCHAR(50) NOT NULL,
    type_category VARCHAR(50) DEFAULT NULL,
    unit VARCHAR(20) DEFAULT NULL,
    conversion_coefficient DECIMAL(18,6) DEFAULT NULL,
    carbon_factor DECIMAL(18,6) DEFAULT NULL,
    status TINYINT DEFAULT 1,
    remark VARCHAR(500) DEFAULT NULL,
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    create_by BIGINT DEFAULT NULL,
    update_by BIGINT DEFAULT NULL,
    PRIMARY KEY (id),
    UNIQUE KEY uk_type_code (type_code)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='能源品种表';

-- 14. energy_type_template
DROP TABLE IF EXISTS energy_type_template;
CREATE TABLE energy_type_template (
    id BIGINT NOT NULL AUTO_INCREMENT,
    template_name VARCHAR(100) NOT NULL,
    template_code VARCHAR(50) NOT NULL,
    type_ids VARCHAR(500) DEFAULT NULL,
    status TINYINT DEFAULT 1,
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    create_by BIGINT DEFAULT NULL,
    update_by BIGINT DEFAULT NULL,
    PRIMARY KEY (id),
    UNIQUE KEY uk_template_code (template_code)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='能源品种模板表';

-- 15. iot_device
DROP TABLE IF EXISTS iot_device;
CREATE TABLE iot_device (
    id BIGINT NOT NULL AUTO_INCREMENT,
    device_code VARCHAR(50) NOT NULL,
    device_name VARCHAR(100) NOT NULL,
    gateway_id BIGINT DEFAULT NULL,
    meter_id BIGINT DEFAULT NULL,
    topic VARCHAR(200) DEFAULT NULL,
    data_format VARCHAR(50) DEFAULT NULL,
    status TINYINT DEFAULT 1,
    last_online_time DATETIME DEFAULT NULL,
    remark VARCHAR(500) DEFAULT NULL,
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    create_by BIGINT DEFAULT NULL,
    update_by BIGINT DEFAULT NULL,
    PRIMARY KEY (id),
    UNIQUE KEY uk_iot_device_code (device_code),
    KEY idx_iot_gateway (gateway_id),
    KEY idx_iot_meter (meter_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='IoT设备表';

-- 16. iot_gateway
DROP TABLE IF EXISTS iot_gateway;
CREATE TABLE iot_gateway (
    id BIGINT NOT NULL AUTO_INCREMENT,
    gateway_code VARCHAR(50) NOT NULL,
    gateway_name VARCHAR(100) NOT NULL,
    protocol_type VARCHAR(50) DEFAULT NULL,
    host VARCHAR(100) DEFAULT NULL,
    port INT DEFAULT NULL,
    username VARCHAR(50) DEFAULT NULL,
    password VARCHAR(100) DEFAULT NULL,
    status TINYINT DEFAULT 1,
    last_online_time DATETIME DEFAULT NULL,
    remark VARCHAR(500) DEFAULT NULL,
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    create_by BIGINT DEFAULT NULL,
    update_by BIGINT DEFAULT NULL,
    PRIMARY KEY (id),
    UNIQUE KEY uk_gateway_code (gateway_code)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='网关表';

-- 17. product_type
DROP TABLE IF EXISTS product_type;
CREATE TABLE product_type (
    id BIGINT NOT NULL AUTO_INCREMENT,
    product_code VARCHAR(50) NOT NULL,
    product_name VARCHAR(100) NOT NULL,
    product_category VARCHAR(50) DEFAULT NULL,
    unit VARCHAR(20) DEFAULT NULL,
    carbon_factor DECIMAL(18,6) DEFAULT NULL,
    status TINYINT DEFAULT 1,
    remark VARCHAR(500) DEFAULT NULL,
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    create_by BIGINT DEFAULT NULL,
    update_by BIGINT DEFAULT NULL,
    PRIMARY KEY (id),
    UNIQUE KEY uk_product_code (product_code)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='产品种类表';

-- 18. work_shift
DROP TABLE IF EXISTS work_shift;
CREATE TABLE work_shift (
    id BIGINT NOT NULL AUTO_INCREMENT,
    shift_name VARCHAR(50) NOT NULL,
    shift_code VARCHAR(50) NOT NULL,
    start_time TIME DEFAULT NULL,
    end_time TIME DEFAULT NULL,
    rest_start TIME DEFAULT NULL,
    rest_end TIME DEFAULT NULL,
    work_days VARCHAR(50) DEFAULT NULL,
    status TINYINT DEFAULT 1,
    remark VARCHAR(500) DEFAULT NULL,
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    create_by BIGINT DEFAULT NULL,
    update_by BIGINT DEFAULT NULL,
    PRIMARY KEY (id),
    UNIQUE KEY uk_shift_code (shift_code)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='班组表';

-- 19. monitor_history_data
DROP TABLE IF EXISTS monitor_history_data;
CREATE TABLE monitor_history_data (
    id BIGINT NOT NULL AUTO_INCREMENT,
    meter_id BIGINT NOT NULL,
    data_time DATETIME NOT NULL,
    data_value DECIMAL(18,4) NOT NULL DEFAULT 0,
    data_type VARCHAR(20) DEFAULT NULL,
    unit VARCHAR(20) DEFAULT NULL,
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    KEY idx_meter_time (meter_id, data_time),
    KEY idx_data_time (data_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='历史数据表';

-- 20. monitor_meter_record
DROP TABLE IF EXISTS monitor_meter_record;
CREATE TABLE monitor_meter_record (
    id BIGINT NOT NULL AUTO_INCREMENT,
    meter_id BIGINT NOT NULL,
    record_time DATETIME NOT NULL,
    start_code DECIMAL(18,4) DEFAULT NULL,
    end_code DECIMAL(18,4) DEFAULT NULL,
    consumption DECIMAL(18,4) DEFAULT NULL,
    cost DECIMAL(18,2) DEFAULT NULL,
    unit_price DECIMAL(18,4) DEFAULT NULL,
    operator VARCHAR(50) DEFAULT NULL,
    remark VARCHAR(500) DEFAULT NULL,
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    KEY idx_record_meter (meter_id),
    KEY idx_record_time (record_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='抄表记录表';

-- 21. monitor_status_log
DROP TABLE IF EXISTS monitor_status_log;
CREATE TABLE monitor_status_log (
    id BIGINT NOT NULL AUTO_INCREMENT,
    device_id BIGINT NOT NULL,
    device_type VARCHAR(50) DEFAULT NULL,
    online_status TINYINT DEFAULT 1,
    offline_time DATETIME DEFAULT NULL,
    online_time DATETIME DEFAULT NULL,
    duration INT DEFAULT NULL,
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    KEY idx_status_device (device_id),
    KEY idx_status_offline (offline_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='状态日志表';

-- 22. alarm_threshold
DROP TABLE IF EXISTS alarm_threshold;
CREATE TABLE alarm_threshold (
    id BIGINT NOT NULL AUTO_INCREMENT,
    threshold_name VARCHAR(100) NOT NULL,
    energy_unit_id BIGINT DEFAULT NULL,
    metric_type VARCHAR(50) DEFAULT NULL,
    compare_type VARCHAR(20) DEFAULT NULL,
    threshold_value DECIMAL(18,4) NOT NULL DEFAULT 0,
    alarm_level VARCHAR(20) DEFAULT NULL,
    enabled TINYINT DEFAULT 1,
    notify_method VARCHAR(50) DEFAULT NULL,
    remark VARCHAR(500) DEFAULT NULL,
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted TINYINT DEFAULT 0,
    PRIMARY KEY (id),
    KEY idx_threshold_unit (energy_unit_id),
    KEY idx_threshold_enabled (enabled)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='报警阈值表';

-- 23. alarm_record
DROP TABLE IF EXISTS alarm_record;
CREATE TABLE alarm_record (
    id BIGINT NOT NULL AUTO_INCREMENT,
    threshold_id BIGINT DEFAULT NULL,
    meter_id BIGINT DEFAULT NULL,
    alarm_type VARCHAR(50) DEFAULT NULL,
    alarm_level VARCHAR(20) DEFAULT NULL,
    alarm_value DECIMAL(18,4) DEFAULT NULL,
    threshold_value DECIMAL(18,4) DEFAULT NULL,
    alarm_time DATETIME NOT NULL,
    handle_status VARCHAR(20) DEFAULT 'PENDING',
    handler VARCHAR(50) DEFAULT NULL,
    handle_time DATETIME DEFAULT NULL,
    handle_remark VARCHAR(500) DEFAULT NULL,
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    KEY idx_alarm_time (alarm_time),
    KEY idx_alarm_status (handle_status),
    KEY idx_alarm_meter (meter_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='报警记录表';

-- 24. analysis_energy_consumption
DROP TABLE IF EXISTS analysis_energy_consumption;
CREATE TABLE analysis_energy_consumption (
    id BIGINT NOT NULL AUTO_INCREMENT,
    energy_unit_id BIGINT NOT NULL,
    meter_id BIGINT DEFAULT NULL,
    energy_type_id BIGINT DEFAULT NULL,
    stat_date DATE NOT NULL,
    stat_period VARCHAR(10) NOT NULL,
    consumption DECIMAL(18,4) NOT NULL DEFAULT 0,
    unit VARCHAR(20) DEFAULT NULL,
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    KEY idx_unit_date (energy_unit_id, stat_date),
    KEY idx_period (stat_period),
    KEY idx_meter (meter_id),
    KEY idx_energy_type (energy_type_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='能耗数据汇总表';

-- 25. analysis_electricity_cost
DROP TABLE IF EXISTS analysis_electricity_cost;
CREATE TABLE analysis_electricity_cost (
    id BIGINT NOT NULL AUTO_INCREMENT,
    meter_id BIGINT DEFAULT NULL,
    stat_date DATE NOT NULL,
    peak_power DECIMAL(18,4) DEFAULT 0,
    valley_power DECIMAL(18,4) DEFAULT 0,
    flat_power DECIMAL(18,4) DEFAULT 0,
    peak_cost DECIMAL(18,2) DEFAULT 0,
    valley_cost DECIMAL(18,2) DEFAULT 0,
    flat_cost DECIMAL(18,2) DEFAULT 0,
    total_cost DECIMAL(18,2) DEFAULT 0,
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    KEY idx_meter_date (meter_id, stat_date),
    KEY idx_stat_date (stat_date)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='电费数据表';

-- 26. analysis_energy_efficiency
DROP TABLE IF EXISTS analysis_energy_efficiency;
CREATE TABLE analysis_energy_efficiency (
    id BIGINT NOT NULL AUTO_INCREMENT,
    energy_unit_id BIGINT NOT NULL,
    stat_date DATE NOT NULL,
    output_value DECIMAL(18,4) DEFAULT 0,
    output_unit VARCHAR(20) DEFAULT NULL,
    energy_consumption DECIMAL(18,4) DEFAULT 0,
    energy_unit VARCHAR(20) DEFAULT NULL,
    intensity DECIMAL(18,6) DEFAULT 0,
    benchmark_value DECIMAL(18,6) DEFAULT NULL,
    benchmark_unit VARCHAR(20) DEFAULT NULL,
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    KEY idx_unit_date (energy_unit_id, stat_date),
    KEY idx_stat_date (stat_date)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='能效数据表';

-- 27. analysis_energy_budget
DROP TABLE IF EXISTS analysis_energy_budget;
CREATE TABLE analysis_energy_budget (
    id BIGINT NOT NULL AUTO_INCREMENT,
    energy_unit_id BIGINT NOT NULL,
    budget_year INT NOT NULL,
    budget_type VARCHAR(10) NOT NULL,
    target_value DECIMAL(18,4) NOT NULL DEFAULT 0,
    target_unit VARCHAR(20) DEFAULT NULL,
    current_value DECIMAL(18,4) DEFAULT 0,
    completion_rate DECIMAL(5,2) DEFAULT 0,
    status TINYINT DEFAULT 1,
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted TINYINT DEFAULT 0,
    PRIMARY KEY (id),
    KEY idx_unit_year (energy_unit_id, budget_year),
    KEY idx_budget_type (budget_type)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='能碳预算表';

-- 28. analysis_benchmark
DROP TABLE IF EXISTS analysis_benchmark;
CREATE TABLE analysis_benchmark (
    id BIGINT NOT NULL AUTO_INCREMENT,
    benchmark_name VARCHAR(200) NOT NULL,
    industry VARCHAR(100) DEFAULT NULL,
    energy_type_id BIGINT DEFAULT NULL,
    energy_category VARCHAR(50) DEFAULT NULL,
    benchmark_value DECIMAL(18,6) NOT NULL DEFAULT 0,
    benchmark_unit VARCHAR(20) DEFAULT NULL,
    source VARCHAR(200) DEFAULT NULL,
    effective_date DATE DEFAULT NULL,
    expire_date DATE DEFAULT NULL,
    remark VARCHAR(500) DEFAULT NULL,
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted TINYINT DEFAULT 0,
    PRIMARY KEY (id),
    KEY idx_industry (industry),
    KEY idx_energy_type (energy_type_id),
    KEY idx_effective (effective_date, expire_date)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='对标库表';

-- 29. carbon_accounting
DROP TABLE IF EXISTS carbon_accounting;
CREATE TABLE carbon_accounting (
    id BIGINT NOT NULL AUTO_INCREMENT,
    accounting_name VARCHAR(200) NOT NULL,
    accounting_year INT NOT NULL,
    accounting_period VARCHAR(50) DEFAULT NULL,
    energy_unit_id BIGINT DEFAULT NULL,
    scope_type VARCHAR(50) DEFAULT NULL,
    total_emission DECIMAL(18,4) DEFAULT 0,
    emission_unit VARCHAR(20) DEFAULT 'tCO2',
    status VARCHAR(20) DEFAULT 'DRAFT',
    remark VARCHAR(500) DEFAULT NULL,
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    create_by BIGINT DEFAULT NULL,
    update_by BIGINT DEFAULT NULL,
    deleted TINYINT DEFAULT 0,
    PRIMARY KEY (id),
    KEY idx_year_period (accounting_year, accounting_period),
    KEY idx_accounting_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='碳排放核算表';

-- 30. carbon_accounting_detail
DROP TABLE IF EXISTS carbon_accounting_detail;
CREATE TABLE carbon_accounting_detail (
    id BIGINT NOT NULL AUTO_INCREMENT,
    accounting_id BIGINT NOT NULL,
    activity_name VARCHAR(200) NOT NULL,
    energy_type_id BIGINT DEFAULT NULL,
    activity_data DECIMAL(18,4) DEFAULT 0,
    activity_unit VARCHAR(20) DEFAULT NULL,
    emission_factor DECIMAL(18,6) DEFAULT 0,
    factor_unit VARCHAR(20) DEFAULT NULL,
    emission DECIMAL(18,4) DEFAULT 0,
    emission_unit VARCHAR(20) DEFAULT NULL,
    source_description VARCHAR(500) DEFAULT NULL,
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    KEY idx_detail_accounting (accounting_id),
    KEY idx_detail_energy_type (energy_type_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='核算明细表';

-- 31. carbon_emission_factor
DROP TABLE IF EXISTS carbon_emission_factor;
CREATE TABLE carbon_emission_factor (
    id BIGINT NOT NULL AUTO_INCREMENT,
    factor_name VARCHAR(200) NOT NULL,
    factor_category VARCHAR(50) DEFAULT NULL,
    energy_type_id BIGINT DEFAULT NULL,
    factor_value DECIMAL(18,6) NOT NULL DEFAULT 0,
    factor_unit VARCHAR(20) DEFAULT NULL,
    source VARCHAR(200) DEFAULT NULL,
    effective_year INT DEFAULT NULL,
    default_flag TINYINT DEFAULT 0,
    remark VARCHAR(500) DEFAULT NULL,
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    create_by BIGINT DEFAULT NULL,
    update_by BIGINT DEFAULT NULL,
    deleted TINYINT DEFAULT 0,
    PRIMARY KEY (id),
    KEY idx_factor_category (factor_category),
    KEY idx_emission_factor_type (energy_type_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='排放因子表';

-- 32. carbon_accounting_model
DROP TABLE IF EXISTS carbon_accounting_model;
CREATE TABLE carbon_accounting_model (
    id BIGINT NOT NULL AUTO_INCREMENT,
    model_name VARCHAR(200) NOT NULL,
    energy_unit_id BIGINT DEFAULT NULL,
    model_config TEXT DEFAULT NULL,
    status TINYINT DEFAULT 1,
    remark VARCHAR(500) DEFAULT NULL,
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    create_by BIGINT DEFAULT NULL,
    update_by BIGINT DEFAULT NULL,
    deleted TINYINT DEFAULT 0,
    PRIMARY KEY (id),
    KEY idx_model_unit (energy_unit_id),
    KEY idx_model_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='核算模型表';

-- 33. carbon_footprint_product
DROP TABLE IF EXISTS carbon_footprint_product;
CREATE TABLE carbon_footprint_product (
    id BIGINT NOT NULL AUTO_INCREMENT,
    product_id BIGINT NOT NULL,
    lifecycle_stage VARCHAR(50) DEFAULT NULL,
    emission DECIMAL(18,4) DEFAULT 0,
    emission_unit VARCHAR(20) DEFAULT 'tCO2',
    calc_method VARCHAR(100) DEFAULT NULL,
    data_source VARCHAR(200) DEFAULT NULL,
    calc_date DATE DEFAULT NULL,
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    KEY idx_footprint_product (product_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='产品碳足迹表';

-- 34. carbon_verification
DROP TABLE IF EXISTS carbon_verification;
CREATE TABLE carbon_verification (
    id BIGINT NOT NULL AUTO_INCREMENT,
    verification_name VARCHAR(200) NOT NULL,
    accounting_id BIGINT DEFAULT NULL,
    verification_type VARCHAR(50) DEFAULT NULL,
    verifier VARCHAR(100) DEFAULT NULL,
    verification_date DATE DEFAULT NULL,
    data_source_description VARCHAR(500) DEFAULT NULL,
    attachment_path VARCHAR(500) DEFAULT NULL,
    verification_result VARCHAR(50) DEFAULT NULL,
    status VARCHAR(20) DEFAULT 'PENDING',
    remark VARCHAR(500) DEFAULT NULL,
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    create_by BIGINT DEFAULT NULL,
    update_by BIGINT DEFAULT NULL,
    deleted TINYINT DEFAULT 0,
    PRIMARY KEY (id),
    KEY idx_verification_accounting (accounting_id),
    KEY idx_verification_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='碳核查表';

-- 35. carbon_asset
DROP TABLE IF EXISTS carbon_asset;
CREATE TABLE carbon_asset (
    id BIGINT NOT NULL AUTO_INCREMENT,
    asset_type VARCHAR(20) NOT NULL,
    asset_name VARCHAR(200) NOT NULL,
    asset_code VARCHAR(50) DEFAULT NULL,
    quantity DECIMAL(18,4) NOT NULL DEFAULT 0,
    unit VARCHAR(20) DEFAULT NULL,
    acquire_date DATE DEFAULT NULL,
    expire_date DATE DEFAULT NULL,
    source VARCHAR(200) DEFAULT NULL,
    cost DECIMAL(18,2) DEFAULT 0,
    status VARCHAR(20) NOT NULL DEFAULT 'AVAILABLE',
    remark VARCHAR(500) DEFAULT NULL,
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    create_by BIGINT DEFAULT NULL,
    update_by BIGINT DEFAULT NULL,
    deleted TINYINT DEFAULT 0,
    PRIMARY KEY (id),
    KEY idx_asset_type (asset_type),
    KEY idx_asset_status (status),
    KEY idx_acquire_date (acquire_date)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='碳资产表';

-- 36. carbon_asset_trade
DROP TABLE IF EXISTS carbon_asset_trade;
CREATE TABLE carbon_asset_trade (
    id BIGINT NOT NULL AUTO_INCREMENT,
    asset_id BIGINT NOT NULL,
    trade_type VARCHAR(20) NOT NULL,
    trade_quantity DECIMAL(18,4) NOT NULL DEFAULT 0,
    trade_price DECIMAL(18,2) NOT NULL DEFAULT 0,
    trade_amount DECIMAL(18,2) NOT NULL DEFAULT 0,
    trade_date DATE NOT NULL,
    counterparty VARCHAR(200) DEFAULT NULL,
    status VARCHAR(20) DEFAULT 'COMPLETED',
    remark VARCHAR(500) DEFAULT NULL,
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted TINYINT DEFAULT 0,
    PRIMARY KEY (id),
    KEY idx_asset_id (asset_id),
    KEY idx_trade_date (trade_date),
    KEY idx_trade_type (trade_type)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='碳资产交易表';

-- 37. carbon_market_info
DROP TABLE IF EXISTS carbon_market_info;
CREATE TABLE carbon_market_info (
    id BIGINT NOT NULL AUTO_INCREMENT,
    info_title VARCHAR(200) NOT NULL,
    info_category VARCHAR(20) NOT NULL,
    content TEXT DEFAULT NULL,
    source VARCHAR(200) DEFAULT NULL,
    publish_date DATE DEFAULT NULL,
    status TINYINT DEFAULT 1,
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    create_by BIGINT DEFAULT NULL,
    update_by BIGINT DEFAULT NULL,
    deleted TINYINT DEFAULT 0,
    PRIMARY KEY (id),
    KEY idx_category (info_category),
    KEY idx_publish_date (publish_date)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='市场信息表';

-- 38. cbam_declaration
DROP TABLE IF EXISTS cbam_declaration;
CREATE TABLE cbam_declaration (
    id BIGINT NOT NULL AUTO_INCREMENT,
    declaration_name VARCHAR(200) NOT NULL,
    declaration_period VARCHAR(50) NOT NULL,
    product_name VARCHAR(200) NOT NULL,
    product_code VARCHAR(50) DEFAULT NULL,
    cn_code VARCHAR(50) DEFAULT NULL,
    origin_country VARCHAR(100) DEFAULT NULL,
    import_quantity DECIMAL(18,4) NOT NULL DEFAULT 0,
    unit VARCHAR(20) DEFAULT NULL,
    direct_emission DECIMAL(18,4) DEFAULT 0,
    indirect_emission DECIMAL(18,4) DEFAULT 0,
    total_emission DECIMAL(18,4) DEFAULT 0,
    emission_unit VARCHAR(20) DEFAULT 'tCO2',
    certificate_number VARCHAR(100) DEFAULT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'DRAFT',
    remark VARCHAR(500) DEFAULT NULL,
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    create_by BIGINT DEFAULT NULL,
    update_by BIGINT DEFAULT NULL,
    deleted TINYINT DEFAULT 0,
    PRIMARY KEY (id),
    KEY idx_period (declaration_period),
    KEY idx_cbam_status (status),
    KEY idx_cn_code (cn_code)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='CBAM申报表';

-- 39. cbam_declaration_detail
DROP TABLE IF EXISTS cbam_declaration_detail;
CREATE TABLE cbam_declaration_detail (
    id BIGINT NOT NULL AUTO_INCREMENT,
    declaration_id BIGINT NOT NULL,
    material_name VARCHAR(200) NOT NULL,
    material_category VARCHAR(50) DEFAULT NULL,
    consumption DECIMAL(18,4) DEFAULT 0,
    unit VARCHAR(20) DEFAULT NULL,
    emission_factor DECIMAL(18,6) DEFAULT 0,
    emission DECIMAL(18,4) DEFAULT 0,
    data_source VARCHAR(200) DEFAULT NULL,
    PRIMARY KEY (id),
    KEY idx_declaration_id (declaration_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='CBAM明细表';

-- 40. supply_chain_carbon
DROP TABLE IF EXISTS supply_chain_carbon;
CREATE TABLE supply_chain_carbon (
    id BIGINT NOT NULL AUTO_INCREMENT,
    supplier_name VARCHAR(200) NOT NULL,
    supplier_code VARCHAR(50) DEFAULT NULL,
    product_name VARCHAR(200) NOT NULL,
    product_category VARCHAR(50) DEFAULT NULL,
    carbon_footprint_value DECIMAL(18,4) DEFAULT 0,
    unit VARCHAR(20) DEFAULT NULL,
    cert_type VARCHAR(50) DEFAULT NULL,
    cert_number VARCHAR(100) DEFAULT NULL,
    valid_date DATE DEFAULT NULL,
    data_source VARCHAR(200) DEFAULT NULL,
    status TINYINT DEFAULT 1,
    remark VARCHAR(500) DEFAULT NULL,
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    create_by BIGINT DEFAULT NULL,
    update_by BIGINT DEFAULT NULL,
    deleted TINYINT DEFAULT 0,
    PRIMARY KEY (id),
    KEY idx_supplier (supplier_name),
    KEY idx_supplier_code (supplier_code),
    KEY idx_product (product_name)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='供应链碳足迹表';

-- 41. report_energy_summary
DROP TABLE IF EXISTS report_energy_summary;
CREATE TABLE report_energy_summary (
    id BIGINT NOT NULL AUTO_INCREMENT,
    energy_unit_id BIGINT NOT NULL,
    report_type VARCHAR(20) NOT NULL,
    stat_date DATE NOT NULL,
    energy_type_id BIGINT DEFAULT NULL,
    consumption DECIMAL(18,4) DEFAULT 0,
    consumption_unit VARCHAR(20) DEFAULT NULL,
    cost DECIMAL(18,2) DEFAULT 0,
    cost_unit VARCHAR(20) DEFAULT NULL,
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    KEY idx_summary_unit (energy_unit_id),
    KEY idx_summary_type_date (report_type, stat_date)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='能耗汇总表';

-- 42. report_energy_total
DROP TABLE IF EXISTS report_energy_total;
CREATE TABLE report_energy_total (
    id BIGINT NOT NULL AUTO_INCREMENT,
    energy_unit_id BIGINT NOT NULL,
    report_type VARCHAR(20) NOT NULL,
    stat_date DATE NOT NULL,
    total_consumption DECIMAL(18,4) DEFAULT 0,
    total_cost DECIMAL(18,2) DEFAULT 0,
    comparison_period VARCHAR(20) DEFAULT NULL,
    comparison_change_rate DECIMAL(5,2) DEFAULT NULL,
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    KEY idx_total_unit (energy_unit_id),
    KEY idx_total_type_date (report_type, stat_date)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='能耗统计表';

-- 43. report_energy_saving
DROP TABLE IF EXISTS report_energy_saving;
CREATE TABLE report_energy_saving (
    id BIGINT NOT NULL AUTO_INCREMENT,
    energy_unit_id BIGINT NOT NULL,
    report_type VARCHAR(20) NOT NULL,
    stat_date DATE NOT NULL,
    baseline_consumption DECIMAL(18,4) DEFAULT 0,
    actual_consumption DECIMAL(18,4) DEFAULT 0,
    saving_amount DECIMAL(18,4) DEFAULT 0,
    saving_rate DECIMAL(5,2) DEFAULT 0,
    saving_amount_unit VARCHAR(20) DEFAULT NULL,
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    KEY idx_saving_unit (energy_unit_id),
    KEY idx_saving_type_date (report_type, stat_date)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='节能量报表';

-- 44. report_cost_stat
DROP TABLE IF EXISTS report_cost_stat;
CREATE TABLE report_cost_stat (
    id BIGINT NOT NULL AUTO_INCREMENT,
    energy_unit_id BIGINT NOT NULL,
    report_type VARCHAR(20) NOT NULL,
    stat_date DATE NOT NULL,
    energy_type_id BIGINT DEFAULT NULL,
    cost DECIMAL(18,2) DEFAULT 0,
    cost_unit VARCHAR(20) DEFAULT NULL,
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    KEY idx_cost_unit (energy_unit_id),
    KEY idx_cost_type_date (report_type, stat_date)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='成本统计表';

-- 45. baseline_consumption
DROP TABLE IF EXISTS baseline_consumption;
CREATE TABLE baseline_consumption (
    id BIGINT NOT NULL AUTO_INCREMENT,
    energy_unit_id BIGINT NOT NULL,
    start_date DATE NOT NULL,
    end_date DATE NOT NULL,
    baseline_value DECIMAL(18,4) NOT NULL DEFAULT 0,
    baseline_type VARCHAR(50) DEFAULT NULL,
    remark VARCHAR(500) DEFAULT NULL,
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    create_by BIGINT DEFAULT NULL,
    update_by BIGINT DEFAULT NULL,
    deleted TINYINT DEFAULT 0,
    PRIMARY KEY (id),
    KEY idx_baseline_unit (energy_unit_id),
    KEY idx_baseline_date (start_date, end_date)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='能耗基准表';

-- 46. entry_indicator
DROP TABLE IF EXISTS entry_indicator;
CREATE TABLE entry_indicator (
    id BIGINT NOT NULL AUTO_INCREMENT,
    energy_unit_id BIGINT NOT NULL,
    indicator_name VARCHAR(100) NOT NULL,
    indicator_type VARCHAR(50) DEFAULT NULL,
    indicator_value DECIMAL(18,4) DEFAULT 0,
    indicator_unit VARCHAR(20) DEFAULT NULL,
    entry_date DATE NOT NULL,
    entry_person VARCHAR(50) DEFAULT NULL,
    remark VARCHAR(500) DEFAULT NULL,
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    create_by BIGINT DEFAULT NULL,
    update_by BIGINT DEFAULT NULL,
    deleted TINYINT DEFAULT 0,
    PRIMARY KEY (id),
    KEY idx_indicator_unit (energy_unit_id),
    KEY idx_indicator_date (entry_date)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='指标录入表';

-- 47. entry_production
DROP TABLE IF EXISTS entry_production;
CREATE TABLE entry_production (
    id BIGINT NOT NULL AUTO_INCREMENT,
    energy_unit_id BIGINT NOT NULL,
    product_type_id BIGINT DEFAULT NULL,
    production_date DATE NOT NULL,
    output_quantity DECIMAL(18,4) DEFAULT 0,
    output_unit VARCHAR(20) DEFAULT NULL,
    batch_number VARCHAR(50) DEFAULT NULL,
    remark VARCHAR(500) DEFAULT NULL,
    entry_person VARCHAR(50) DEFAULT NULL,
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    create_by BIGINT DEFAULT NULL,
    update_by BIGINT DEFAULT NULL,
    deleted TINYINT DEFAULT 0,
    PRIMARY KEY (id),
    KEY idx_production_unit (energy_unit_id),
    KEY idx_production_date (production_date)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='产量录入表';

-- 48. ai_service_record
DROP TABLE IF EXISTS ai_service_record;
CREATE TABLE ai_service_record (
    id BIGINT NOT NULL AUTO_INCREMENT,
    service_name VARCHAR(200) NOT NULL,
    service_type VARCHAR(50) NOT NULL,
    applicant VARCHAR(100) NOT NULL,
    contact VARCHAR(100) DEFAULT NULL,
    description TEXT DEFAULT NULL,
    expected_date DATE DEFAULT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    result TEXT DEFAULT NULL,
    remark VARCHAR(500) DEFAULT NULL,
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    create_by BIGINT DEFAULT NULL,
    update_by BIGINT DEFAULT NULL,
    deleted TINYINT DEFAULT 0,
    PRIMARY KEY (id),
    KEY idx_service_status (status),
    KEY idx_applicant (applicant),
    KEY idx_expected_date (expected_date)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='AI服务预约表';

-- 49. ai_knowledge_doc
DROP TABLE IF EXISTS ai_knowledge_doc;
CREATE TABLE ai_knowledge_doc (
    id BIGINT NOT NULL AUTO_INCREMENT,
    doc_name VARCHAR(200) NOT NULL,
    doc_type VARCHAR(50) NOT NULL,
    file_path VARCHAR(500) DEFAULT NULL,
    file_size BIGINT DEFAULT NULL,
    summary TEXT DEFAULT NULL,
    upload_user VARCHAR(100) DEFAULT NULL,
    upload_time DATETIME DEFAULT NULL,
    status TINYINT DEFAULT 1,
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted TINYINT DEFAULT 0,
    PRIMARY KEY (id),
    KEY idx_doc_type (doc_type),
    KEY idx_upload_user (upload_user)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='AI知识库表';

-- 50. ai_report_task
DROP TABLE IF EXISTS ai_report_task;
CREATE TABLE ai_report_task (
    id BIGINT NOT NULL AUTO_INCREMENT,
    report_name VARCHAR(200) NOT NULL,
    report_type VARCHAR(50) NOT NULL,
    template_id BIGINT DEFAULT NULL,
    params_json TEXT DEFAULT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    result_path VARCHAR(500) DEFAULT NULL,
    error_msg VARCHAR(1000) DEFAULT NULL,
    progress INT DEFAULT 0,
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    create_by BIGINT DEFAULT NULL,
    deleted TINYINT DEFAULT 0,
    PRIMARY KEY (id),
    KEY idx_report_type (report_type),
    KEY idx_report_status (status),
    KEY idx_template_id (template_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='AI报告任务表';

-- 51. ai_report_template
DROP TABLE IF EXISTS ai_report_template;
CREATE TABLE ai_report_template (
    id BIGINT NOT NULL AUTO_INCREMENT,
    template_name VARCHAR(200) NOT NULL,
    report_type VARCHAR(50) NOT NULL,
    template_content TEXT DEFAULT NULL,
    variables VARCHAR(1000) DEFAULT NULL,
    status TINYINT DEFAULT 1,
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted TINYINT DEFAULT 0,
    PRIMARY KEY (id),
    KEY idx_template_report_type (report_type)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='AI报告模板表';

-- 52. toolkit_green_factory
DROP TABLE IF EXISTS toolkit_green_factory;
CREATE TABLE toolkit_green_factory (
    id BIGINT NOT NULL AUTO_INCREMENT,
    declaration_name VARCHAR(200) NOT NULL,
    enterprise_name VARCHAR(200) NOT NULL,
    declaration_year INT NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'DRAFT',
    progress INT DEFAULT 0,
    submit_time DATETIME DEFAULT NULL,
    remark VARCHAR(500) DEFAULT NULL,
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    create_by BIGINT DEFAULT NULL,
    deleted TINYINT DEFAULT 0,
    PRIMARY KEY (id),
    KEY idx_factory_status (status),
    KEY idx_factory_year (declaration_year)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='绿色工厂申报表';

-- 53. toolkit_green_factory_item
DROP TABLE IF EXISTS toolkit_green_factory_item;
CREATE TABLE toolkit_green_factory_item (
    id BIGINT NOT NULL AUTO_INCREMENT,
    declaration_id BIGINT NOT NULL,
    item_name VARCHAR(200) NOT NULL,
    item_category VARCHAR(50) DEFAULT NULL,
    required TINYINT DEFAULT 1,
    uploaded TINYINT DEFAULT 0,
    file_path VARCHAR(500) DEFAULT NULL,
    upload_time DATETIME DEFAULT NULL,
    remark VARCHAR(500) DEFAULT NULL,
    PRIMARY KEY (id),
    KEY idx_item_declaration (declaration_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='申报资料清单表';

-- 54. toolkit_peak_shifting
DROP TABLE IF EXISTS toolkit_peak_shifting;
CREATE TABLE toolkit_peak_shifting (
    id BIGINT NOT NULL AUTO_INCREMENT,
    plan_name VARCHAR(200) NOT NULL,
    plan_date DATE NOT NULL,
    shift_type VARCHAR(20) DEFAULT NULL,
    production_line VARCHAR(100) DEFAULT NULL,
    target_output DECIMAL(18,4) DEFAULT NULL,
    actual_output DECIMAL(18,4) DEFAULT NULL,
    energy_consumption DECIMAL(18,4) DEFAULT NULL,
    energy_cost DECIMAL(18,2) DEFAULT NULL,
    saving_rate DECIMAL(5,2) DEFAULT NULL,
    status TINYINT DEFAULT 1,
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted TINYINT DEFAULT 0,
    PRIMARY KEY (id),
    KEY idx_plan_date (plan_date),
    KEY idx_production_line (production_line)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='错峰排产表';

-- 55. toolkit_peak_shifting_config
DROP TABLE IF EXISTS toolkit_peak_shifting_config;
CREATE TABLE toolkit_peak_shifting_config (
    id BIGINT NOT NULL AUTO_INCREMENT,
    config_name VARCHAR(200) NOT NULL,
    peak_hours_start TIME DEFAULT NULL,
    peak_hours_end TIME DEFAULT NULL,
    valley_hours_start TIME DEFAULT NULL,
    valley_hours_end TIME DEFAULT NULL,
    flat_hours_start TIME DEFAULT NULL,
    flat_hours_end TIME DEFAULT NULL,
    max_power DECIMAL(18,4) DEFAULT NULL,
    priority INT DEFAULT 0,
    status TINYINT DEFAULT 1,
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted TINYINT DEFAULT 0,
    PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='排产配置表';

-- 56. toolkit_calculator_record
DROP TABLE IF EXISTS toolkit_calculator_record;
CREATE TABLE toolkit_calculator_record (
    id BIGINT NOT NULL AUTO_INCREMENT,
    calc_name VARCHAR(200) NOT NULL,
    calc_type VARCHAR(20) NOT NULL,
    input_params_json TEXT DEFAULT NULL,
    result_json TEXT DEFAULT NULL,
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    create_by BIGINT DEFAULT NULL,
    deleted TINYINT DEFAULT 0,
    PRIMARY KEY (id),
    KEY idx_calc_type (calc_type)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='计算器记录表';

-- 57. toolkit_electricity_price
DROP TABLE IF EXISTS toolkit_electricity_price;
CREATE TABLE toolkit_electricity_price (
    id BIGINT NOT NULL AUTO_INCREMENT,
    price_name VARCHAR(200) NOT NULL,
    effective_date DATE NOT NULL,
    peak_price DECIMAL(18,4) NOT NULL DEFAULT 0,
    flat_price DECIMAL(18,4) NOT NULL DEFAULT 0,
    valley_price DECIMAL(18,4) NOT NULL DEFAULT 0,
    peak_hours VARCHAR(100) DEFAULT NULL,
    flat_hours VARCHAR(100) DEFAULT NULL,
    valley_hours VARCHAR(100) DEFAULT NULL,
    status TINYINT DEFAULT 1,
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted TINYINT DEFAULT 0,
    PRIMARY KEY (id),
    KEY idx_effective_date (effective_date)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='分时电价表';


-- ============================================================
-- 第二部分：基础数据
-- 注意：列名与schema_pure.sql的CREATE TABLE完全匹配
-- ============================================================

-- 1. 部门数据 (sys_dept) - 列名: sort (非order_num)
INSERT INTO sys_dept (id, dept_name, parent_id, sort, leader, phone, email, status, ancestors) VALUES
(1, '总公司', 0, 0, '张三', '13800138000', 'zhangsan@seatone.com', 1, '0'),
(2, '一分厂', 1, 1, '李四', '13800138001', 'lisi@seatone.com', 1, '0,1'),
(3, '二分厂', 1, 2, '王五', '13800138002', 'wangwu@seatone.com', 1, '0,1'),
(4, '三分厂', 1, 3, '赵六', '13800138003', 'zhaoliu@seatone.com', 1, '0,1'),
(5, '生产车间A', 2, 1, '钱七', '13800138004', 'qianqi@seatone.com', 1, '0,1,2'),
(6, '生产车间B', 2, 2, '孙八', '13800138005', 'sunba@seatone.com', 1, '0,1,2'),
(7, '动力车间', 3, 1, '周九', '13800138006', 'zhoujiu@seatone.com', 1, '0,1,3');

-- 2. 用户数据 (sys_user) - 密码: 123456 (BCrypt加密)
INSERT INTO sys_user (id, username, password, nickname, phone, email, avatar, dept_id, status, create_by, update_by) VALUES
(1, 'admin', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EO', '系统管理员', '13800138000', 'admin@seatone.com', NULL, 1, 1, 1, 1),
(2, 'zhangsan', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EO', '张三', '13800138001', 'zhangsan@seatone.com', NULL, 2, 1, 1, 1),
(3, 'lisi', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EO', '李四', '13800138002', 'lisi@seatone.com', NULL, 3, 1, 1, 1);

-- 3. 角色数据 (sys_role) - 列名: role_code (非role_key)
INSERT INTO sys_role (id, role_name, role_code, remark, sort, status, create_by, update_by) VALUES
(1, '超级管理员', 'super_admin', '拥有系统全部权限', 1, 1, 1, 1),
(2, '系统管理员', 'system_admin', '系统管理权限', 2, 1, 1, 1),
(3, '普通用户', 'common_user', '基础查看权限', 3, 1, 1, 1),
(4, '监控用户', 'monitor_user', '监控管理权限', 4, 1, 1, 1),
(5, '碳管用户', 'carbon_user', '碳排放管理权限', 5, 1, 1, 1);

-- 4. 菜单数据 (sys_menu) - 列名: sort (非order_num), permission (非perms)
INSERT INTO sys_menu (id, menu_name, parent_id, sort, path, component, icon, menu_type, status, permission, hidden, keep_alive) VALUES
-- 首页
(1, '首页', 0, 0, '/index', 'index', 'dashboard', 2, 1, 'system:index', 0, 1),
-- 系统管理
(2, '系统管理', 0, 1, '/system', NULL, 'system', 1, 1, NULL, 0, 0),
(3, '用户管理', 2, 1, '/system/user', 'system/user/index', 'user', 2, 1, 'system:user:list', 0, 1),
(4, '角色管理', 2, 2, '/system/role', 'system/role/index', 'peoples', 2, 1, 'system:role:list', 0, 1),
(5, '菜单管理', 2, 3, '/system/menu', 'system/menu/index', 'tree-table', 2, 1, 'system:menu:list', 0, 1),
(6, '部门管理', 2, 4, '/system/dept', 'system/dept/index', 'tree', 2, 1, 'system:dept:list', 0, 1),
(7, '字典管理', 2, 5, '/system/dict', 'system/dict/index', 'dict', 2, 1, 'system:dict:list', 0, 1),
(8, '参数管理', 2, 6, '/system/config', 'system/config/index', 'edit', 2, 1, 'system:config:list', 0, 1),
(9, '通知公告', 2, 7, '/system/notice', 'system/notice/index', 'message', 2, 1, 'system:notice:list', 0, 1),
(10, '操作日志', 2, 8, '/system/log', 'system/log/index', 'log', 2, 1, 'system:log:list', 0, 1),
-- 在线监测
(20, '在线监测', 0, 2, '/monitor', NULL, 'monitor', 1, 1, NULL, 0, 0),
(21, '实时数据', 20, 1, '/monitor/data', 'monitor/data/index', 'data', 2, 1, 'monitor:data:list', 0, 1),
(22, '设备管理', 20, 2, '/monitor/device', 'monitor/device/index', 'device', 2, 1, 'monitor:device:list', 0, 1),
(23, '实时监控', 20, 3, '/monitor/realtime', 'monitor/realtime/index', 'eye-open', 2, 1, 'monitor:realtime:list', 0, 1),
(24, '历史数据', 20, 4, '/monitor/history', 'monitor/history/index', 'chart', 2, 1, 'monitor:history:list', 0, 1),
(25, '设备对标', 20, 5, '/monitor/device-compare', 'monitor/device-compare/index', 'comparison', 2, 1, 'monitor:device-compare:list', 0, 1),
(26, '时段对标', 20, 6, '/monitor/time-compare', 'monitor/time-compare/index', 'time-compare', 2, 1, 'monitor:time-compare:list', 0, 1),
(27, '计量抄表', 20, 7, '/monitor/reading', 'monitor/reading/index', 'reading', 2, 1, 'monitor:reading:list', 0, 1),
(28, '状态监测', 20, 8, '/monitor/status', 'monitor/status/index', 'status', 2, 1, 'monitor:status:list', 0, 1),
(29, '班组管理', 20, 9, '/monitor/work-shift', 'monitor/work-shift/index', 'work-shift', 2, 1, 'monitor:work-shift:list', 0, 1),
-- 报警管理
(30, '报警管理', 0, 3, '/alarm', NULL, 'alarm', 1, 1, NULL, 0, 0),
(31, '报警设置', 30, 1, '/alarm/threshold', 'alarm/threshold/index', 'alarm-rule', 2, 1, 'alarm:threshold:list', 0, 1),
(32, '报警查询', 30, 2, '/alarm/record', 'alarm/record/index', 'alarm-record', 2, 1, 'alarm:record:list', 0, 1),
-- 碳排管理
(40, '碳排管理', 0, 4, '/carbon', NULL, 'carbon', 1, 1, NULL, 0, 0),
(41, '排放核算', 40, 1, '/carbon/accounting', 'carbon/accounting/index', 'accounting', 2, 1, 'carbon:accounting:list', 0, 1),
(42, '排放因子', 40, 2, '/carbon/factor', 'carbon/factor/index', 'factor', 2, 1, 'carbon:factor:list', 0, 1),
(43, '碳资产', 40, 3, '/carbon/asset', 'carbon/asset/index', 'asset', 2, 1, 'carbon:asset:list', 0, 1),
(44, 'CBAM申报', 40, 4, '/carbon/cbam', 'carbon/cbam/index', 'cbam', 2, 1, 'carbon:cbam:list', 0, 1),
(45, '产品碳足迹', 40, 5, '/carbon/footprint', 'carbon/footprint/index', 'footprint', 2, 1, 'carbon:footprint:list', 0, 1),
(46, '供应链碳', 40, 6, '/carbon/supply-chain', 'carbon/supply-chain/index', 'supply-chain', 2, 1, 'carbon:supply-chain:list', 0, 1),
(47, '碳核查', 40, 7, '/carbon/verification', 'carbon/verification/index', 'verification', 2, 1, 'carbon:verification:list', 0, 1),
(48, '碳市场', 40, 8, '/carbon/market', 'carbon/market/index', 'market', 2, 1, 'carbon:market:list', 0, 1),
-- 报表管理
(50, '报表管理', 0, 5, '/report', NULL, 'report', 1, 1, NULL, 0, 0),
(51, '能耗统计', 50, 1, '/report/energy/summary', 'report/energy/summary/index', 'energy-summary', 2, 1, 'report:energy:summary:list', 0, 1),
(52, '能耗汇总', 50, 2, '/report/energy/total', 'report/energy/total/index', 'energy-total', 2, 1, 'report:energy:total:list', 0, 1),
(53, '成本统计', 50, 3, '/report/cost', 'report/cost/index', 'cost-stat', 2, 1, 'report:cost:list', 0, 1),
(54, '节能量报表', 50, 4, '/report/saving', 'report/saving/index', 'saving', 2, 1, 'report:saving:list', 0, 1),
(55, '基准能耗', 50, 5, '/report/baseline', 'report/baseline/index', 'baseline', 2, 1, 'report:baseline:list', 0, 1),
(56, '指标录入', 50, 6, '/report/indicator', 'report/indicator/index', 'indicator', 2, 1, 'report:indicator:list', 0, 1),
(57, '产量录入', 50, 7, '/report/production', 'report/production/index', 'production', 2, 1, 'report:production:list', 0, 1),
-- 分析管理
(60, '分析管理', 0, 6, '/analysis', NULL, 'analysis', 1, 1, NULL, 0, 0),
(61, '能耗分析', 60, 1, '/analysis/consumption', 'analysis/consumption/index', 'consumption', 2, 1, 'analysis:consumption:list', 0, 1),
(62, '电费分析', 60, 2, '/analysis/electricity', 'analysis/electricity/index', 'electricity', 2, 1, 'analysis:electricity:list', 0, 1),
(63, '能效分析', 60, 3, '/analysis/efficiency', 'analysis/efficiency/index', 'efficiency', 2, 1, 'analysis:efficiency:list', 0, 1),
(64, '能碳预算', 60, 4, '/analysis/budget', 'analysis/budget/index', 'budget', 2, 1, 'analysis:budget:list', 0, 1),
(65, '对标库', 60, 5, '/analysis/benchmark', 'analysis/benchmark/index', 'benchmark', 2, 1, 'analysis:benchmark:list', 0, 1),
-- AI服务
(70, 'AI智能服务', 0, 7, '/ai', NULL, 'ai', 1, 1, NULL, 0, 0),
(71, '服务预约', 70, 1, '/ai/service', 'ai/service/index', 'ai-service', 2, 1, 'ai:service:list', 0, 1),
(72, '知识库', 70, 2, '/ai/knowledge', 'ai/knowledge/index', 'ai-knowledge', 2, 1, 'ai:knowledge:list', 0, 1),
(73, '报告生成', 70, 3, '/ai/report', 'ai/report/index', 'ai-report', 2, 1, 'ai:report:list', 0, 1),
(74, '报告模板', 70, 4, '/ai/report-template', 'ai/report-template/index', 'ai-template', 2, 1, 'ai:report-template:list', 0, 1),
-- 工具箱
(80, '能碳工具包', 0, 8, '/toolkit', NULL, 'toolkit', 1, 1, NULL, 0, 0),
(81, '分时电价', 80, 1, '/toolkit/electricity-price', 'toolkit/electricity-price/index', 'electricity-price', 2, 1, 'toolkit:electricity-price:list', 0, 1),
(82, '错峰排产', 80, 2, '/toolkit/peak-shifting', 'toolkit/peak-shifting/index', 'peak-shifting', 2, 1, 'toolkit:peak-shifting:list', 0, 1),
(83, '绿色工厂', 80, 3, '/toolkit/green-factory', 'toolkit/green-factory/index', 'green-factory', 2, 1, 'toolkit:green-factory:list', 0, 1),
(84, '能碳计算器', 80, 4, '/toolkit/calculator', 'toolkit/calculator/index', 'calculator', 2, 1, 'toolkit:calculator:list', 0, 1);

-- 5. 用户角色关联
INSERT INTO sys_user_role (user_id, role_id) VALUES
(1, 1),
(2, 2),
(3, 3);

-- 6. 角色菜单关联 (超级管理员拥有全部菜单权限)
INSERT INTO sys_role_menu (role_id, menu_id) VALUES
(1, 1), (1, 2), (1, 3), (1, 4), (1, 5), (1, 6), (1, 7), (1, 8), (1, 9), (1, 10),
(1, 20), (1, 21), (1, 22), (1, 23), (1, 24), (1, 25), (1, 26), (1, 27), (1, 28), (1, 29),
(1, 30), (1, 31), (1, 32),
(1, 40), (1, 41), (1, 42), (1, 43), (1, 44), (1, 45), (1, 46), (1, 47), (1, 48),
(1, 50), (1, 51), (1, 52), (1, 53), (1, 54), (1, 55), (1, 56), (1, 57),
(1, 60), (1, 61), (1, 62), (1, 63), (1, 64), (1, 65),
(1, 70), (1, 71), (1, 72), (1, 73), (1, 74),
(1, 80), (1, 81), (1, 82), (1, 83), (1, 84);

-- 7. 字典类型
INSERT INTO sys_dict_type (id, dict_name, dict_type, remark, status) VALUES
(1, '用户性别', 'sys_user_sex', '用户性别列表', 1),
(2, '菜单状态', 'sys_show_hide', '菜单状态列表', 1),
(3, '系统状态', 'sys_normal_disable', '系统状态列表', 1),
(4, '操作类型', 'sys_oper_type', '操作类型列表', 1),
(5, '通知类型', 'sys_notice_type', '通知类型列表', 1),
(6, '通知状态', 'sys_notice_status', '通知状态列表', 1),
(7, '设备类型', 'monitor_device_type', '监控设备类型', 1),
(8, '报警级别', 'alarm_level', '报警级别列表', 1),
(9, '报警状态', 'alarm_status', '报警状态列表', 1),
(10, '碳排类型', 'carbon_type', '碳排放范围类型', 1),
(11, '能源分类', 'energy_category', '能源品种分类', 1),
(12, '菜单类型', 'sys_menu_type', '菜单类型列表', 1);

-- 8. 字典数据 - 列名: sort (非dict_sort), dict_label, dict_value
INSERT INTO sys_dict_data (id, dict_type, dict_label, dict_value, sort, remark, status) VALUES
-- 用户性别
(1, 'sys_user_sex', '男', '0', 1, '性别男', 1),
(2, 'sys_user_sex', '女', '1', 2, '性别女', 1),
(3, 'sys_user_sex', '未知', '2', 3, '性别未知', 1),
-- 菜单状态
(4, 'sys_show_hide', '显示', '0', 1, '显示菜单', 1),
(5, 'sys_show_hide', '隐藏', '1', 2, '隐藏菜单', 1),
-- 系统状态
(6, 'sys_normal_disable', '正常', '1', 1, '正常状态', 1),
(7, 'sys_normal_disable', '停用', '0', 2, '停用状态', 1),
-- 操作类型
(8, 'sys_oper_type', '新增', '1', 1, '新增操作', 1),
(9, 'sys_oper_type', '修改', '2', 2, '修改操作', 1),
(10, 'sys_oper_type', '删除', '3', 3, '删除操作', 1),
(11, 'sys_oper_type', '授权', '4', 4, '授权操作', 1),
(12, 'sys_oper_type', '导出', '5', 5, '导出操作', 1),
(13, 'sys_oper_type', '导入', '6', 6, '导入操作', 1),
(14, 'sys_oper_type', '强退', '7', 7, '强退操作', 1),
(15, 'sys_oper_type', '生成代码', '8', 8, '生成操作', 1),
(16, 'sys_oper_type', '清空数据', '9', 9, '清空操作', 1),
-- 通知类型
(17, 'sys_notice_type', '通知', '1', 1, '通知', 1),
(18, 'sys_notice_type', '公告', '2', 2, '公告', 1),
-- 通知状态
(19, 'sys_notice_status', '正常', '0', 1, '正常状态', 1),
(20, 'sys_notice_status', '关闭', '1', 2, '关闭状态', 1),
-- 设备类型
(21, 'monitor_device_type', '电表', '1', 1, '电能表', 1),
(22, 'monitor_device_type', '水表', '2', 2, '水表', 1),
(23, 'monitor_device_type', '气表', '3', 3, '燃气表', 1),
(24, 'monitor_device_type', '传感器', '4', 4, '传感器', 1),
-- 报警级别
(25, 'alarm_level', '紧急', '1', 1, '紧急报警', 1),
(26, 'alarm_level', '重要', '2', 2, '重要报警', 1),
(27, 'alarm_level', '一般', '3', 3, '一般报警', 1),
(28, 'alarm_level', '提示', '4', 4, '提示信息', 1),
-- 报警状态
(29, 'alarm_status', '未处理', '0', 1, '未处理', 1),
(30, 'alarm_status', '处理中', '1', 2, '处理中', 1),
(31, 'alarm_status', '已处理', '2', 3, '已处理', 1),
(32, 'alarm_status', '已忽略', '3', 4, '已忽略', 1),
-- 碳排类型
(33, 'carbon_type', '范围一', '1', 1, '直接排放', 1),
(34, 'carbon_type', '范围二', '2', 2, '间接排放', 1),
(35, 'carbon_type', '范围三', '3', 3, '其他间接排放', 1),
-- 能源分类
(36, 'energy_category', '一次能源', '1', 1, '一次能源', 1),
(37, 'energy_category', '二次能源', '2', 2, '二次能源', 1),
(38, 'energy_category', '可再生能源', '3', 3, '可再生能源', 1),
-- 菜单类型
(39, 'sys_menu_type', '目录', '1', 1, '目录', 1),
(40, 'sys_menu_type', '菜单', '2', 2, '菜单', 1),
(41, 'sys_menu_type', '按钮', '3', 3, '按钮', 1);

-- 9. 系统配置
INSERT INTO sys_config (id, config_name, config_key, config_value, remark, status) VALUES
(1, '主框架页-默认皮肤样式名称', 'sys.index.skinName', 'skin-blue', '蓝色 skin-blue、绿色 skin-green、紫色 skin-purple、红色 skin-red、黄色 skin-yellow', 1),
(2, '用户管理-账号初始密码', 'sys.user.initPassword', '123456', '初始化密码 123456', 1),
(3, '主框架页-侧边栏主题', 'sys.index.sideTheme', 'theme-dark', '深色主题theme-dark，浅色主题theme-light', 1);


-- ============================================================
-- 第三部分：测试数据
-- ============================================================

-- 1. 能源品种数据
INSERT INTO energy_type (id, type_code, type_name, type_category, unit, conversion_coefficient, carbon_factor, status, remark) VALUES
(1, 'ELEC', '电力', '二次能源', 'kWh', 0.122900, 0.609600, 1, '电网电力'),
(2, 'NAT_GAS', '天然气', '一次能源', 'm3', 1.214300, 2.162200, 1, '管道天然气'),
(3, 'STEAM', '蒸汽', '二次能源', 't', 0.085710, 0.110000, 1, '工业蒸汽'),
(4, 'WATER', '自来水', '二次能源', 't', 0.085700, 0.300000, 1, '工业用水'),
(5, 'COAL', '原煤', '一次能源', 't', 0.714300, 1.900300, 1, '原煤'),
(6, 'DIESEL', '柴油', '一次能源', 'L', 1.457100, 2.630000, 1, '轻柴油'),
(7, 'GASOLINE', '汽油', '一次能源', 'L', 1.471400, 2.300100, 1, '车用汽油'),
(8, 'LPG', '液化石油气', '一次能源', 'kg', 1.714300, 2.100000, 1, '液化石油气'),
(9, 'SOLAR', '太阳能', '可再生能源', 'kWh', 0.000000, 0.000000, 1, '光伏发电'),
(10, 'WIND', '风能', '可再生能源', 'kWh', 0.000000, 0.000000, 1, '风力发电');

-- 2. 用能设备数据
INSERT INTO energy_device (id, device_code, device_name, device_type, dept_id, status, manufacturer, model, install_date, power_rating, voltage_level, remark) VALUES
(1, 'DEV-001', '1号变压器', 'transformer', 2, 1, '西门子', 'S11-1000/10', '2024-01-15', 1000.0000, '10kV', '一分厂主变压器'),
(2, 'DEV-002', '2号变压器', 'transformer', 3, 1, 'ABB', 'S11-800/10', '2024-03-20', 800.0000, '10kV', '二分厂主变压器'),
(3, 'DEV-003', '空压机A', 'compressor', 5, 1, '阿特拉斯', 'GA37', '2024-02-10', 220.0000, '380V', '生产车间A空压机'),
(4, 'DEV-004', '空压机B', 'compressor', 6, 1, '英格索兰', 'MM250', '2024-04-05', 185.0000, '380V', '生产车间B空压机'),
(5, 'DEV-005', '中央空调', 'hvac', 2, 1, '格力', 'CVE500', '2023-11-20', 350.0000, '380V', '一分厂中央空调'),
(6, 'DEV-006', '燃气锅炉', 'boiler', 7, 1, '太锅', 'WNS4-1.25', '2023-08-15', 0.0000, NULL, '动力车间燃气锅炉'),
(7, 'DEV-007', '烤漆线1', 'production_line', 4, 1, '世通', 'RC-200', '2024-05-10', 150.0000, '380V', '三分厂烤漆生产线'),
(8, 'DEV-008', '冲压机A', 'press', 5, 1, '扬力', 'J31-400', '2024-01-25', 45.0000, '380V', '生产车间A冲压机');

-- 3. 计量器具数据
INSERT INTO energy_meter (id, meter_code, meter_name, meter_type, device_id, dept_id, status, accuracy_class, range_min, range_max, install_date, calibration_date, next_calibration_date, remark) VALUES
(1, 'MTR-001', '总电表', '电表', 1, 2, 1, '0.5S', 0.0000, 10000.0000, '2024-01-15', '2026-01-15', '2028-01-15', '一分厂总电表'),
(2, 'MTR-002', '二分厂总电表', '电表', 2, 3, 1, '0.5S', 0.0000, 8000.0000, '2024-03-20', '2026-03-20', '2028-03-20', '二分厂总电表'),
(3, 'MTR-003', '空压机A电表', '电表', 3, 5, 1, '0.5', 0.0000, 500.0000, '2024-02-10', '2026-02-10', '2028-02-10', '空压机A专用电表'),
(4, 'MTR-004', '天然气总表', '气表', 6, 7, 1, '1.5', 0.0000, 10000.0000, '2023-08-15', '2025-08-15', '2027-08-15', '天然气总表'),
(5, 'MTR-005', '自来水总表', '水表', NULL, 1, 1, 'B级', 0.0000, 5000.0000, '2023-06-01', '2025-06-01', '2027-06-01', '全厂自来水总表'),
(6, 'MTR-006', '空压机B电表', '电表', 4, 6, 1, '0.5', 0.0000, 500.0000, '2024-04-05', '2026-04-05', '2028-04-05', '空压机B专用电表'),
(7, 'MTR-007', '空调电表', '电表', 5, 2, 1, '0.5', 0.0000, 1000.0000, '2023-11-20', '2025-11-20', '2027-11-20', '中央空调专用电表'),
(8, 'MTR-008', '烤漆线电表', '电表', 7, 4, 1, '0.5', 0.0000, 500.0000, '2024-05-10', '2026-05-10', '2028-05-10', '烤漆生产线电表');

-- 4. 报警阈值数据
INSERT INTO alarm_threshold (id, threshold_name, energy_unit_id, metric_type, compare_type, threshold_value, alarm_level, enabled, notify_method, remark) VALUES
(1, '一分厂日用电量上限', 2, 'daily_electricity', 'GT', 50000.0000, '重要', 1, 'email', '一分厂日用电量超过50000kWh报警'),
(2, '全厂天然气日用量上限', 1, 'daily_gas', 'GT', 5000.0000, '紧急', 1, 'email,sms', '全厂天然气日用量超过5000m3报警'),
(3, '二分厂月电费上限', 3, 'monthly_cost', 'GT', 500000.0000, '重要', 1, 'email', '二分厂月电费超过50万元报警'),
(4, '全厂碳排放月度上限', 1, 'monthly_carbon', 'GT', 1000.0000, '紧急', 1, 'email,sms', '全厂月碳排放超过1000tCO2报警'),
(5, '动力车间蒸汽用量上限', 7, 'daily_steam', 'GT', 50.0000, '一般', 1, 'email', '动力车间日蒸汽用量超过50t报警');

-- 5. 排放因子数据
INSERT INTO carbon_emission_factor (id, factor_name, factor_category, energy_type_id, factor_value, factor_unit, source, effective_year, default_flag, remark) VALUES
(1, '华东电网电力排放因子', '电力', 1, 0.609600, 'tCO2/MWh', '生态环境部2022年公告', 2022, 1, '华东区域电网平均排放因子'),
(2, '天然气排放因子', '化石燃料', 2, 2.162200, 'tCO2/万m3', 'IPCC指南', 2022, 1, '天然气燃烧排放因子'),
(3, '柴油排放因子', '化石燃料', 6, 2.630000, 'tCO2/t', 'IPCC指南', 2022, 1, '轻柴油排放因子'),
(4, '汽油排放因子', '化石燃料', 7, 2.300100, 'tCO2/t', 'IPCC指南', 2022, 1, '车用汽油排放因子'),
(5, '原煤排放因子', '化石燃料', 5, 1.900300, 'tCO2/t', 'IPCC指南', 2022, 1, '原煤排放因子'),
(6, '液化石油气排放因子', '化石燃料', 8, 2.100000, 'tCO2/t', 'IPCC指南', 2022, 1, '液化石油气排放因子'),
(7, '蒸汽排放因子', '间接排放', 3, 0.110000, 'tCO2/t', '行业标准', 2022, 1, '工业蒸汽排放因子'),
(8, '自来水排放因子', '间接排放', 4, 0.300000, 'tCO2/t', '行业标准', 2022, 1, '自来水生产排放因子'),
(9, '华北电网电力排放因子', '电力', 1, 0.714300, 'tCO2/MWh', '生态环境部2022年公告', 2022, 0, '华北区域电网平均排放因子'),
(10, '南方电网电力排放因子', '电力', 1, 0.527100, 'tCO2/MWh', '生态环境部2022年公告', 2022, 0, '南方区域电网平均排放因子');

-- 6. 行业基准数据
INSERT INTO analysis_benchmark (id, benchmark_name, industry, energy_type_id, energy_category, benchmark_value, benchmark_unit, source, effective_date, expire_date, remark) VALUES
(1, '电器制造业单位产值能耗-国标', '电器制造', 1, '二次能源', 0.050000, 'tce/万元', 'GB/T 2589-2020', '2024-01-01', '2027-12-31', '电器制造行业国家标准'),
(2, '电器制造业单位产值碳排放-国标', '电器制造', NULL, NULL, 0.120000, 'tCO2/万元', 'GB/T 32150-2015', '2024-01-01', '2027-12-31', '碳排放强度国标'),
(3, '机械加工行业能效标杆', '机械加工', 1, '二次能源', 0.035000, 'tce/万元', '行业标杆值', '2024-01-01', '2027-12-31', '机械加工行业先进值'),
(4, '山东省万元GDP能耗目标', '综合', NULL, NULL, 0.400000, 'tce/万元', '山东省十四五规划', '2024-01-01', '2025-12-31', '山东省十四五节能目标'),
(5, '企业内部能效基准', '企业内部', 1, '二次能源', 0.045000, 'tce/万元', '企业历史数据', '2024-01-01', '2027-12-31', '企业三年平均值');

-- 7. 能效分析数据
INSERT INTO analysis_energy_efficiency (id, energy_unit_id, stat_date, output_value, output_unit, energy_consumption, energy_unit, intensity, benchmark_value, benchmark_unit) VALUES
(1, 2, '2026-01-31', 5000.0000, '万元', 250.0000, 'tce', 0.050000, 0.050000, 'tce/万元'),
(2, 3, '2026-01-31', 3200.0000, '万元', 144.0000, 'tce', 0.045000, 0.050000, 'tce/万元'),
(3, 4, '2026-01-31', 2800.0000, '万元', 140.0000, 'tce', 0.050000, 0.050000, 'tce/万元'),
(4, 2, '2026-02-28', 5200.0000, '万元', 249.6000, 'tce', 0.048000, 0.050000, 'tce/万元'),
(5, 3, '2026-02-28', 3400.0000, '万元', 149.6000, 'tce', 0.044000, 0.050000, 'tce/万元'),
(6, 4, '2026-02-28', 3000.0000, '万元', 141.0000, 'tce', 0.047000, 0.050000, 'tce/万元'),
(7, 2, '2026-03-31', 5500.0000, '万元', 253.0000, 'tce', 0.046000, 0.050000, 'tce/万元'),
(8, 3, '2026-03-31', 3600.0000, '万元', 151.2000, 'tce', 0.042000, 0.050000, 'tce/万元'),
(9, 4, '2026-03-31', 3100.0000, '万元', 139.5000, 'tce', 0.045000, 0.050000, 'tce/万元');

-- 8. 能耗分析数据
INSERT INTO analysis_energy_consumption (id, energy_unit_id, meter_id, energy_type_id, stat_date, stat_period, consumption, unit) VALUES
(1, 2, 1, 1, '2026-06-01', 'DAY', 18000.0000, 'kWh'),
(2, 2, 1, 1, '2026-06-02', 'DAY', 17500.0000, 'kWh'),
(3, 2, 1, 1, '2026-06-03', 'DAY', 18200.0000, 'kWh'),
(4, 3, 2, 1, '2026-06-01', 'DAY', 12000.0000, 'kWh'),
(5, 3, 2, 1, '2026-06-02', 'DAY', 11800.0000, 'kWh'),
(6, 3, 2, 1, '2026-06-03', 'DAY', 12500.0000, 'kWh'),
(7, 7, 4, 2, '2026-06-01', 'DAY', 3500.0000, 'm3'),
(8, 7, 4, 2, '2026-06-02', 'DAY', 3200.0000, 'm3'),
(9, 7, 4, 2, '2026-06-03', 'DAY', 3600.0000, 'm3'),
(10, 1, 5, 4, '2026-06-01', 'DAY', 500.0000, 't'),
(11, 1, 5, 4, '2026-06-02', 'DAY', 480.0000, 't'),
(12, 1, 5, 4, '2026-06-03', 'DAY', 520.0000, 't');

-- 9. 班组数据
INSERT INTO work_shift (id, shift_name, shift_code, start_time, end_time, rest_start, rest_end, work_days, status, remark) VALUES
(1, '早班', 'MORNING', '06:00:00', '14:00:00', '09:30:00', '10:00:00', '1,2,3,4,5', 1, '早班06:00-14:00'),
(2, '中班', 'AFTERNOON', '14:00:00', '22:00:00', '17:30:00', '18:00:00', '1,2,3,4,5', 1, '中班14:00-22:00'),
(3, '夜班', 'NIGHT', '22:00:00', '06:00:00', '01:00:00', '01:30:00', '1,2,3,4,5', 1, '夜班22:00-06:00');

-- 10. 产品种类数据
INSERT INTO product_type (id, product_code, product_name, product_category, unit, carbon_factor, status, remark) VALUES
(1, 'PROD-001', '高压开关柜', '电气设备', '台', 0.500000, 1, '主要产品'),
(2, 'PROD-002', '低压配电柜', '电气设备', '台', 0.300000, 1, '主要产品'),
(3, 'PROD-003', '控制柜', '电气设备', '台', 0.200000, 1, '辅助产品'),
(4, 'PROD-004', '变压器', '电气设备', '台', 1.200000, 1, '核心产品');

-- 11. 能碳预算数据
INSERT INTO analysis_energy_budget (id, energy_unit_id, budget_year, budget_type, target_value, target_unit, current_value, completion_rate, status) VALUES
(1, 1, 2026, 'ENERGY', 50000.0000, 'tce', 24500.0000, 49.00, 1, '全厂年度能源预算'),
(2, 1, 2026, 'CARBON', 12000.0000, 'tCO2', 5800.0000, 48.33, 1, '全厂年度碳排放预算'),
(3, 2, 2026, 'ENERGY', 20000.0000, 'tce', 9800.0000, 49.00, 1, '一分厂年度能源预算'),
(4, 3, 2026, 'ENERGY', 15000.0000, 'tce', 7200.0000, 48.00, 1, '二分厂年度能源预算'),
(5, 4, 2026, 'ENERGY', 12000.0000, 'tce', 5900.0000, 49.17, 1, '三分厂年度能源预算');

-- 12. 碳排放核算测试数据
INSERT INTO carbon_accounting (id, accounting_name, accounting_year, accounting_period, energy_unit_id, scope_type, total_emission, emission_unit, status, remark) VALUES
(1, '2026年一季度碳排放核算', 2026, 'Q1', 1, 'SCOPE_1', 1200.5000, 'tCO2', 'SUBMITTED', '一季度碳排放核算'),
(2, '2026年二季度碳排放核算', 2026, 'Q2', 1, 'SCOPE_2', 3500.2000, 'tCO2', 'DRAFT', '二季度碳排放核算(草稿)');

INSERT INTO carbon_accounting_detail (id, accounting_id, activity_name, energy_type_id, activity_data, activity_unit, emission_factor, factor_unit, emission, emission_unit, source_description) VALUES
(1, 1, '天然气燃烧', 2, 500000.0000, 'm3', 2.162200, 'tCO2/万m3', 1081.1000, 'tCO2', '天然气消耗记录'),
(2, 1, '柴油发电', 6, 5.0000, 't', 2.630000, 'tCO2/t', 13.1500, 'tCO2', '柴油消耗记录'),
(3, 1, '外购电力', 1, 3500000.0000, 'kWh', 0.609600, 'tCO2/MWh', 2133.6000, 'tCO2', '电费账单'),
(4, 2, '天然气燃烧', 2, 520000.0000, 'm3', 2.162200, 'tCO2/万m3', 1124.3440, 'tCO2', '天然气消耗记录'),
(5, 2, '外购电力', 1, 3800000.0000, 'kWh', 0.609600, 'tCO2/MWh', 2316.4800, 'tCO2', '电费账单');

-- 13. 分时电价配置
INSERT INTO toolkit_electricity_price (id, price_name, effective_date, peak_price, flat_price, valley_price, peak_hours, flat_hours, valley_hours, status) VALUES
(1, '2026年工商业电价', '2026-01-01', 1.2500, 0.7500, 0.3500, '08:00-11:00,17:00-21:00', '07:00-08:00,11:00-17:00,21:00-23:00', '23:00-07:00', 1);

-- 14. 碳资产测试数据
INSERT INTO carbon_asset (id, asset_type, asset_name, asset_code, quantity, unit, acquire_date, expire_date, source, cost, status, remark) VALUES
(1, 'QUOTA', '2026年度碳配额', 'QUOTA-2026-001', 15000.0000, 'tCO2', '2026-01-01', '2026-12-31', '政府分配', 0.00, 'AVAILABLE', '政府免费分配碳配额'),
(2, 'CCER', '风电项目CCER', 'CCER-2026-001', 5000.0000, 'tCO2', '2026-03-15', '2028-03-14', '自愿减排交易', 350000.00, 'AVAILABLE', '风电项目自愿减排量');

-- ============================================================
-- 初始化完成
-- 默认管理员账号: admin / 123456
-- ============================================================
