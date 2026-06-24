-- ============================================================
-- 能碳管理平台数据库初始化脚本 - 完整版
-- 数据库: energy_carbon
-- 包含57张表的CREATE TABLE语句
-- ============================================================

USE energy_carbon;

-- ============================================================
-- 一、系统管理模块（10张表）
-- ============================================================

-- 1. 用户表 sys_user
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
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';

-- 2. 角色表 sys_role
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
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='角色表';

-- 3. 菜单表 sys_menu
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
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='菜单表';

-- 4. 部门表 sys_dept
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

-- 5. 字典类型表 sys_dict_type
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

-- 6. 字典数据表 sys_dict_data
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

-- 7. 用户角色关联表 sys_user_role
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

-- 8. 角色菜单关联表 sys_role_menu
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

-- 9. 操作日志表 sys_oper_log
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

-- 10. 系统配置表 sys_config
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
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='系统配置表';

-- ============================================================
-- 二、基础数据模块（8张表）
-- ============================================================

-- 11. 用能设备表 energy_device
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

-- 12. 计量器具表 energy_meter
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

-- 13. 能源品种表 energy_type
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

-- 14. 能源品种模板表 energy_type_template
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

-- 15. 物联网设备表 iot_device
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
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='物联网设备表';

-- 16. 物联网网关表 iot_gateway
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
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='物联网网关表';

-- 17. 产品种类表 product_type
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

-- 18. 班组管理表 work_shift
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
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='班组管理表';

-- ============================================================
-- 三、监控模块（3张表）
-- ============================================================

-- 19. 历史数据表 monitor_history_data
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

-- 20. 计量抄表记录表 monitor_meter_record
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
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='计量抄表记录表';

-- 21. 状态监测日志表 monitor_status_log
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
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='状态监测日志表';

-- ============================================================
-- 四、报警模块（2张表）
-- ============================================================

-- 22. 报警阈值配置表 alarm_threshold
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
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='报警阈值配置表';

-- 23. 报警记录表 alarm_record
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

-- ============================================================
-- 五、分析模块（5张表）
-- ============================================================

-- 24. 能耗数据汇总表 analysis_energy_consumption
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

-- 25. 电费数据表 analysis_electricity_cost
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

-- 26. 能效数据表 analysis_energy_efficiency
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

-- 27. 能碳预算表 analysis_energy_budget
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

-- 28. 对标库表 analysis_benchmark
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

-- ============================================================
-- 六、碳排放模块（12张表）
-- ============================================================

-- 29. 碳排放核算主表 carbon_accounting
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
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='碳排放核算主表';

-- 30. 核算明细表 carbon_accounting_detail
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

-- 31. 排放因子库表 carbon_emission_factor
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
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='排放因子库表';

-- 32. 碳核算模型表 carbon_accounting_model
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
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='碳核算模型表';

-- 33. 产品碳足迹表 carbon_footprint_product
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

-- 34. 碳核查表 carbon_verification
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

-- 35. 碳资产表 carbon_asset
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

-- 36. 碳资产交易表 carbon_asset_trade
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

-- 37. 市场供需信息表 carbon_market_info
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
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='市场供需信息表';

-- 38. CBAM申报表 cbam_declaration
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

-- 39. CBAM明细表 cbam_declaration_detail
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

-- 40. 供应链碳足迹表 supply_chain_carbon
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

-- ============================================================
-- 七、报表模块（7张表）
-- ============================================================

-- 41. 能耗统计表 report_energy_summary
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
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='能耗统计表';

-- 42. 能耗汇总表 report_energy_total
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
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='能耗汇总表';

-- 43. 节能量表 report_energy_saving
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
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='节能量表';

-- 44. 用能成本表 report_cost_stat
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
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用能成本表';

-- 45. 基准能耗表 baseline_consumption
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
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='基准能耗表';

-- 46. 指标录入表 entry_indicator
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

-- 47. 产量录入表 entry_production
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

-- ============================================================
-- 八、AI与工具包模块（10张表）
-- ============================================================

-- 48. AI服务预约记录表 ai_service_record
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
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='AI服务预约记录表';

-- 49. AI知识库文档表 ai_knowledge_doc
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
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='AI知识库文档表';

-- 50. AI报告生成任务表 ai_report_task
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
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='AI报告生成任务表';

-- 51. AI报告模板表 ai_report_template
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

-- 52. 绿色工厂申报表 toolkit_green_factory
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

-- 53. 申报资料清单表 toolkit_green_factory_item
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

-- 54. 错峰排产表 toolkit_peak_shifting
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

-- 55. 排产配置表 toolkit_peak_shifting_config
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

-- 56. 计算器记录表 toolkit_calculator_record
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

-- 57. 分时电价配置表 toolkit_electricity_price
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
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='分时电价配置表';
