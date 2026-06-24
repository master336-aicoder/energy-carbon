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
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT '__ID',
    username VARCHAR(50) NOT NULL COMMENT '___',
    password VARCHAR(100) NOT NULL COMMENT '__',
    nickname VARCHAR(50) DEFAULT NULL COMMENT '__',
    phone VARCHAR(20) DEFAULT NULL COMMENT '___',
    email VARCHAR(50) DEFAULT NULL COMMENT '__',
    avatar VARCHAR(200) DEFAULT NULL COMMENT '____',
    dept_id BIGINT DEFAULT NULL COMMENT '__ID',
    status TINYINT DEFAULT 1 COMMENT '___1__ 0___',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '____',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '____',
    create_by BIGINT DEFAULT NULL COMMENT '___',
    update_by BIGINT DEFAULT NULL COMMENT '___',
    PRIMARY KEY (id),
    UNIQUE KEY uk_username (username),
    KEY idx_dept_id (dept_id),
    KEY idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';

-- 2. 角色表 sys_role
DROP TABLE IF EXISTS sys_role;
CREATE TABLE sys_role (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT '__ID',
    role_name VARCHAR(50) NOT NULL COMMENT '____',
    role_code VARCHAR(50) NOT NULL COMMENT '____',
    remark VARCHAR(500) DEFAULT NULL COMMENT '__',
    sort INT DEFAULT 0 COMMENT '__',
    status TINYINT DEFAULT 1 COMMENT '___1__ 0___',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '____',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '____',
    create_by BIGINT DEFAULT NULL COMMENT '___',
    update_by BIGINT DEFAULT NULL COMMENT '___',
    PRIMARY KEY (id),
    UNIQUE KEY uk_role_code (role_code),
    KEY idx_role_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='角色表';

-- 3. 菜单表 sys_menu
DROP TABLE IF EXISTS sys_menu;
CREATE TABLE sys_menu (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT '__ID',
    menu_name VARCHAR(50) NOT NULL COMMENT '____',
    parent_id BIGINT DEFAULT 0 COMMENT '___ID',
    sort INT DEFAULT 0 COMMENT '__',
    path VARCHAR(200) DEFAULT NULL COMMENT '____',
    component VARCHAR(200) DEFAULT NULL COMMENT '____',
    icon VARCHAR(50) DEFAULT NULL COMMENT '____',
    menu_type TINYINT DEFAULT 1 COMMENT '_____M__ C__ F___',
    status TINYINT DEFAULT 1 COMMENT '___1__ 0___',
    permission VARCHAR(100) DEFAULT NULL COMMENT '____',
    hidden TINYINT DEFAULT 0 COMMENT '_____1_ 0__',
    keep_alive TINYINT DEFAULT 0 COMMENT '_____1_ 0__',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '____',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '____',
    create_by BIGINT DEFAULT NULL COMMENT '___',
    update_by BIGINT DEFAULT NULL COMMENT '___',
    PRIMARY KEY (id),
    KEY idx_parent_id (parent_id),
    KEY idx_menu_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='菜单表';

-- 4. 部门表 sys_dept
DROP TABLE IF EXISTS sys_dept;
CREATE TABLE sys_dept (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT '__ID',
    dept_name VARCHAR(50) NOT NULL COMMENT '____',
    parent_id BIGINT DEFAULT 0 COMMENT '___ID',
    sort INT DEFAULT 0 COMMENT '__',
    leader VARCHAR(50) DEFAULT NULL COMMENT '___',
    phone VARCHAR(20) DEFAULT NULL COMMENT '____',
    email VARCHAR(50) DEFAULT NULL COMMENT '__',
    status TINYINT DEFAULT 1 COMMENT '___1__ 0___',
    ancestors VARCHAR(500) DEFAULT NULL COMMENT '____',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '____',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '____',
    create_by BIGINT DEFAULT NULL COMMENT '___',
    update_by BIGINT DEFAULT NULL COMMENT '___',
    PRIMARY KEY (id),
    KEY idx_dept_parent_id (parent_id),
    KEY idx_dept_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='部门表';

-- 5. 字典类型表 sys_dict_type
DROP TABLE IF EXISTS sys_dict_type;
CREATE TABLE sys_dict_type (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT '__ID',
    dict_name VARCHAR(50) NOT NULL COMMENT '____',
    dict_type VARCHAR(50) NOT NULL COMMENT '____',
    remark VARCHAR(500) DEFAULT NULL COMMENT '__',
    status TINYINT DEFAULT 1 COMMENT '___1__ 0___',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '____',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '____',
    create_by BIGINT DEFAULT NULL COMMENT '___',
    update_by BIGINT DEFAULT NULL COMMENT '___',
    PRIMARY KEY (id),
    UNIQUE KEY uk_dict_type (dict_type)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='字典类型表';

-- 6. 字典数据表 sys_dict_data
DROP TABLE IF EXISTS sys_dict_data;
CREATE TABLE sys_dict_data (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT '__ID',
    dict_type VARCHAR(50) NOT NULL COMMENT '____',
    dict_label VARCHAR(50) NOT NULL COMMENT '____',
    dict_value VARCHAR(50) NOT NULL COMMENT '____',
    sort INT DEFAULT 0 COMMENT '__',
    remark VARCHAR(500) DEFAULT NULL COMMENT '__',
    status TINYINT DEFAULT 1 COMMENT '___1__ 0___',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '____',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '____',
    create_by BIGINT DEFAULT NULL COMMENT '___',
    update_by BIGINT DEFAULT NULL COMMENT '___',
    PRIMARY KEY (id),
    KEY idx_dict_data_type (dict_type),
    KEY idx_dict_data_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='字典数据表';

-- 7. 用户角色关联表 sys_user_role
DROP TABLE IF EXISTS sys_user_role;
CREATE TABLE sys_user_role (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT '__ID',
    user_id BIGINT NOT NULL COMMENT '__ID',
    role_id BIGINT NOT NULL COMMENT '__ID',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '____',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '____',
    create_by BIGINT DEFAULT NULL COMMENT '___',
    update_by BIGINT DEFAULT NULL COMMENT '___',
    PRIMARY KEY (id),
    UNIQUE KEY uk_user_role (user_id, role_id),
    KEY idx_user_role_user (user_id),
    KEY idx_user_role_role (role_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户角色关联表';

-- 8. 角色菜单关联表 sys_role_menu
DROP TABLE IF EXISTS sys_role_menu;
CREATE TABLE sys_role_menu (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT '__ID',
    role_id BIGINT NOT NULL COMMENT '__ID',
    menu_id BIGINT NOT NULL COMMENT '__ID',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '____',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '____',
    create_by BIGINT DEFAULT NULL COMMENT '___',
    update_by BIGINT DEFAULT NULL COMMENT '___',
    PRIMARY KEY (id),
    UNIQUE KEY uk_role_menu (role_id, menu_id),
    KEY idx_role_menu_role (role_id),
    KEY idx_role_menu_menu (menu_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='角色菜单关联表';

-- 9. 操作日志表 sys_oper_log
DROP TABLE IF EXISTS sys_oper_log;
CREATE TABLE sys_oper_log (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT '__ID',
    title VARCHAR(100) DEFAULT NULL COMMENT '____',
    oper_type TINYINT DEFAULT 0 COMMENT '_____1__ 2__ 3__ 4__ 5__ 6__ 7__ 8____ 9_____',
    method VARCHAR(200) DEFAULT NULL COMMENT '____',
    request_method VARCHAR(10) DEFAULT NULL COMMENT '____',
    oper_name VARCHAR(50) DEFAULT NULL COMMENT '____',
    oper_url VARCHAR(500) DEFAULT NULL COMMENT '__URL',
    oper_ip VARCHAR(50) DEFAULT NULL COMMENT '__IP__',
    oper_param TEXT DEFAULT NULL COMMENT '____',
    json_result TEXT DEFAULT NULL COMMENT '____',
    status TINYINT DEFAULT 1 COMMENT '_____1__ 0___',
    error_msg TEXT DEFAULT NULL COMMENT '____',
    oper_time DATETIME DEFAULT NULL COMMENT '____',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '____',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '____',
    create_by BIGINT DEFAULT NULL COMMENT '___',
    update_by BIGINT DEFAULT NULL COMMENT '___',
    PRIMARY KEY (id),
    KEY idx_oper_time (oper_time),
    KEY idx_oper_name (oper_name)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='操作日志表';

-- 10. 系统配置表 sys_config
DROP TABLE IF EXISTS sys_config;
CREATE TABLE sys_config (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT '__ID',
    config_name VARCHAR(100) NOT NULL COMMENT '____',
    config_key VARCHAR(100) NOT NULL COMMENT '____',
    config_value VARCHAR(500) DEFAULT NULL COMMENT '____',
    remark VARCHAR(500) DEFAULT NULL COMMENT '__',
    status TINYINT DEFAULT 1 COMMENT '___1__ 0___',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '____',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '____',
    create_by BIGINT DEFAULT NULL COMMENT '___',
    update_by BIGINT DEFAULT NULL COMMENT '___',
    PRIMARY KEY (id),
    UNIQUE KEY uk_config_key (config_key)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='系统配置表';

-- ============================================================
-- 二、基础数据模块（8张表）
-- ============================================================

-- 11. 用能设备表 energy_device
DROP TABLE IF EXISTS energy_device;
CREATE TABLE energy_device (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT '__ID',
    device_code VARCHAR(50) NOT NULL COMMENT '____',
    device_name VARCHAR(100) NOT NULL COMMENT '____',
    device_type VARCHAR(50) DEFAULT NULL COMMENT '____',
    dept_id BIGINT DEFAULT NULL COMMENT '__ID',
    status TINYINT DEFAULT 1 COMMENT '___1__ 0___',
    manufacturer VARCHAR(100) DEFAULT NULL COMMENT '___',
    model VARCHAR(100) DEFAULT NULL COMMENT '__',
    install_date DATE DEFAULT NULL COMMENT '____',
    power_rating DECIMAL(18,4) DEFAULT NULL COMMENT '____(kW)',
    voltage_level VARCHAR(20) DEFAULT NULL COMMENT '____',
    remark VARCHAR(500) DEFAULT NULL COMMENT '__',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '____',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '____',
    create_by BIGINT DEFAULT NULL COMMENT '___',
    update_by BIGINT DEFAULT NULL COMMENT '___',
    PRIMARY KEY (id),
    UNIQUE KEY uk_device_code (device_code),
    KEY idx_device_dept (dept_id),
    KEY idx_device_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用能设备表';

-- 12. 计量器具表 energy_meter
DROP TABLE IF EXISTS energy_meter;
CREATE TABLE energy_meter (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT '__ID',
    meter_code VARCHAR(50) NOT NULL COMMENT '______',
    meter_name VARCHAR(100) NOT NULL COMMENT '______',
    meter_type VARCHAR(50) DEFAULT NULL COMMENT '______',
    device_id BIGINT DEFAULT NULL COMMENT '____ID',
    dept_id BIGINT DEFAULT NULL COMMENT '__ID',
    status TINYINT DEFAULT 1 COMMENT '___1__ 0___',
    accuracy_class VARCHAR(20) DEFAULT NULL COMMENT '____',
    range_min DECIMAL(18,4) DEFAULT NULL COMMENT '____',
    range_max DECIMAL(18,4) DEFAULT NULL COMMENT '____',
    install_date DATE DEFAULT NULL COMMENT '____',
    calibration_date DATE DEFAULT NULL COMMENT '____',
    next_calibration_date DATE DEFAULT NULL COMMENT '______',
    remark VARCHAR(500) DEFAULT NULL COMMENT '__',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '____',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '____',
    create_by BIGINT DEFAULT NULL COMMENT '___',
    update_by BIGINT DEFAULT NULL COMMENT '___',
    PRIMARY KEY (id),
    UNIQUE KEY uk_meter_code (meter_code),
    KEY idx_meter_device (device_id),
    KEY idx_meter_dept (dept_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='计量器具表';

-- 13. 能源品种表 energy_type
DROP TABLE IF EXISTS energy_type;
CREATE TABLE energy_type (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT '__ID',
    type_code VARCHAR(50) NOT NULL COMMENT '____',
    type_name VARCHAR(50) NOT NULL COMMENT '____',
    type_category VARCHAR(50) DEFAULT NULL COMMENT '____',
    unit VARCHAR(20) DEFAULT NULL COMMENT '____',
    conversion_coefficient DECIMAL(18,6) DEFAULT NULL COMMENT '_____',
    carbon_factor DECIMAL(18,6) DEFAULT NULL COMMENT '_____',
    status TINYINT DEFAULT 1 COMMENT '___1__ 0___',
    remark VARCHAR(500) DEFAULT NULL COMMENT '__',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '____',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '____',
    create_by BIGINT DEFAULT NULL COMMENT '___',
    update_by BIGINT DEFAULT NULL COMMENT '___',
    PRIMARY KEY (id),
    UNIQUE KEY uk_type_code (type_code)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='能源品种表';

-- 14. 能源品种模板表 energy_type_template
DROP TABLE IF EXISTS energy_type_template;
CREATE TABLE energy_type_template (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT '__ID',
    template_name VARCHAR(100) NOT NULL COMMENT '____',
    template_code VARCHAR(50) NOT NULL COMMENT '____',
    type_ids VARCHAR(500) DEFAULT NULL COMMENT '____ID__',
    status TINYINT DEFAULT 1 COMMENT '___1__ 0___',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '____',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '____',
    create_by BIGINT DEFAULT NULL COMMENT '___',
    update_by BIGINT DEFAULT NULL COMMENT '___',
    PRIMARY KEY (id),
    UNIQUE KEY uk_template_code (template_code)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='能源品种模板表';

-- 15. 物联网设备表 iot_device
DROP TABLE IF EXISTS iot_device;
CREATE TABLE iot_device (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT '__ID',
    device_code VARCHAR(50) NOT NULL COMMENT '____',
    device_name VARCHAR(100) NOT NULL COMMENT '____',
    gateway_id BIGINT DEFAULT NULL COMMENT '__ID',
    meter_id BIGINT DEFAULT NULL COMMENT '______ID',
    topic VARCHAR(200) DEFAULT NULL COMMENT 'MQTT__',
    data_format VARCHAR(50) DEFAULT NULL COMMENT '____',
    status TINYINT DEFAULT 1 COMMENT '___1__ 0___',
    last_online_time DATETIME DEFAULT NULL COMMENT '______',
    remark VARCHAR(500) DEFAULT NULL COMMENT '__',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '____',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '____',
    create_by BIGINT DEFAULT NULL COMMENT '___',
    update_by BIGINT DEFAULT NULL COMMENT '___',
    PRIMARY KEY (id),
    UNIQUE KEY uk_iot_device_code (device_code),
    KEY idx_iot_gateway (gateway_id),
    KEY idx_iot_meter (meter_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='物联网设备表';

-- 16. 物联网网关表 iot_gateway
DROP TABLE IF EXISTS iot_gateway;
CREATE TABLE iot_gateway (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT '__ID',
    gateway_code VARCHAR(50) NOT NULL COMMENT '____',
    gateway_name VARCHAR(100) NOT NULL COMMENT '____',
    protocol_type VARCHAR(50) DEFAULT NULL COMMENT '____',
    host VARCHAR(100) DEFAULT NULL COMMENT '____',
    port INT DEFAULT NULL COMMENT '___',
    username VARCHAR(50) DEFAULT NULL COMMENT '___',
    password VARCHAR(100) DEFAULT NULL COMMENT '__',
    status TINYINT DEFAULT 1 COMMENT '___1__ 0___',
    last_online_time DATETIME DEFAULT NULL COMMENT '______',
    remark VARCHAR(500) DEFAULT NULL COMMENT '__',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '____',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '____',
    create_by BIGINT DEFAULT NULL COMMENT '___',
    update_by BIGINT DEFAULT NULL COMMENT '___',
    PRIMARY KEY (id),
    UNIQUE KEY uk_gateway_code (gateway_code)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='物联网网关表';

-- 17. 产品种类表 product_type
DROP TABLE IF EXISTS product_type;
CREATE TABLE product_type (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT '__ID',
    product_code VARCHAR(50) NOT NULL COMMENT '____',
    product_name VARCHAR(100) NOT NULL COMMENT '____',
    product_category VARCHAR(50) DEFAULT NULL COMMENT '____',
    unit VARCHAR(20) DEFAULT NULL COMMENT '____',
    carbon_factor DECIMAL(18,6) DEFAULT NULL COMMENT '_____',
    status TINYINT DEFAULT 1 COMMENT '___1__ 0___',
    remark VARCHAR(500) DEFAULT NULL COMMENT '__',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '____',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '____',
    create_by BIGINT DEFAULT NULL COMMENT '___',
    update_by BIGINT DEFAULT NULL COMMENT '___',
    PRIMARY KEY (id),
    UNIQUE KEY uk_product_code (product_code)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='产品种类表';

-- 18. 班组管理表 work_shift
DROP TABLE IF EXISTS work_shift;
CREATE TABLE work_shift (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT '__ID',
    shift_name VARCHAR(50) NOT NULL COMMENT '____',
    shift_code VARCHAR(50) NOT NULL COMMENT '____',
    start_time TIME DEFAULT NULL COMMENT '____',
    end_time TIME DEFAULT NULL COMMENT '____',
    rest_start TIME DEFAULT NULL COMMENT '______',
    rest_end TIME DEFAULT NULL COMMENT '______',
    work_days VARCHAR(50) DEFAULT NULL COMMENT '_____1,2,3,4,5_',
    status TINYINT DEFAULT 1 COMMENT '___1__ 0___',
    remark VARCHAR(500) DEFAULT NULL COMMENT '__',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '____',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '____',
    create_by BIGINT DEFAULT NULL COMMENT '___',
    update_by BIGINT DEFAULT NULL COMMENT '___',
    PRIMARY KEY (id),
    UNIQUE KEY uk_shift_code (shift_code)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='班组管理表';

-- ============================================================
-- 三、监控模块（3张表）
-- ============================================================

-- 19. 历史数据表 monitor_history_data
DROP TABLE IF EXISTS monitor_history_data;
CREATE TABLE monitor_history_data (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT '__ID',
    meter_id BIGINT NOT NULL COMMENT '____ID',
    data_time DATETIME NOT NULL COMMENT '____',
    data_value DECIMAL(18,4) NOT NULL DEFAULT 0 COMMENT '___',
    data_type VARCHAR(20) DEFAULT NULL COMMENT '____',
    unit VARCHAR(20) DEFAULT NULL COMMENT '____',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '____',
    PRIMARY KEY (id),
    KEY idx_meter_time (meter_id, data_time),
    KEY idx_data_time (data_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='历史数据表';

-- 20. 计量抄表记录表 monitor_meter_record
DROP TABLE IF EXISTS monitor_meter_record;
CREATE TABLE monitor_meter_record (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT '__ID',
    meter_id BIGINT NOT NULL COMMENT '____ID',
    record_time DATETIME NOT NULL COMMENT '____',
    start_code DECIMAL(18,4) DEFAULT NULL COMMENT '____',
    end_code DECIMAL(18,4) DEFAULT NULL COMMENT '____',
    consumption DECIMAL(18,4) DEFAULT NULL COMMENT '__',
    cost DECIMAL(18,2) DEFAULT NULL COMMENT '__',
    unit_price DECIMAL(18,4) DEFAULT NULL COMMENT '__',
    operator VARCHAR(50) DEFAULT NULL COMMENT '___',
    remark VARCHAR(500) DEFAULT NULL COMMENT '__',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '____',
    PRIMARY KEY (id),
    KEY idx_record_meter (meter_id),
    KEY idx_record_time (record_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='计量抄表记录表';

-- 21. 状态监测日志表 monitor_status_log
DROP TABLE IF EXISTS monitor_status_log;
CREATE TABLE monitor_status_log (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT '__ID',
    device_id BIGINT NOT NULL COMMENT '__ID',
    device_type VARCHAR(50) DEFAULT NULL COMMENT '____',
    online_status TINYINT DEFAULT 1 COMMENT '_____1__ 0___',
    offline_time DATETIME DEFAULT NULL COMMENT '____',
    online_time DATETIME DEFAULT NULL COMMENT '____',
    duration INT DEFAULT NULL COMMENT '____(_)',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '____',
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
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT '__ID',
    threshold_name VARCHAR(100) NOT NULL COMMENT '____',
    energy_unit_id BIGINT DEFAULT NULL COMMENT '____ID',
    metric_type VARCHAR(50) DEFAULT NULL COMMENT '____',
    compare_type VARCHAR(20) DEFAULT NULL COMMENT '_____GT__/LT__/EQ___',
    threshold_value DECIMAL(18,4) NOT NULL DEFAULT 0 COMMENT '__',
    alarm_level VARCHAR(20) DEFAULT NULL COMMENT '____',
    enabled TINYINT DEFAULT 1 COMMENT '_____1_ 0__',
    notify_method VARCHAR(50) DEFAULT NULL COMMENT '____',
    remark VARCHAR(500) DEFAULT NULL COMMENT '__',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '____',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '____',
    deleted TINYINT DEFAULT 0 COMMENT '_____0____ 1_____',
    PRIMARY KEY (id),
    KEY idx_threshold_unit (energy_unit_id),
    KEY idx_threshold_enabled (enabled)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='报警阈值配置表';

-- 23. 报警记录表 alarm_record
DROP TABLE IF EXISTS alarm_record;
CREATE TABLE alarm_record (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT '__ID',
    threshold_id BIGINT DEFAULT NULL COMMENT '__ID',
    meter_id BIGINT DEFAULT NULL COMMENT '____ID',
    alarm_type VARCHAR(50) DEFAULT NULL COMMENT '____',
    alarm_level VARCHAR(20) DEFAULT NULL COMMENT '____',
    alarm_value DECIMAL(18,4) DEFAULT NULL COMMENT '___',
    threshold_value DECIMAL(18,4) DEFAULT NULL COMMENT '__',
    alarm_time DATETIME NOT NULL COMMENT '____',
    handle_status VARCHAR(20) DEFAULT 'PENDING' COMMENT '_____PENDING___/PROCESSING___/HANDLED___/IGNORED____',
    handler VARCHAR(50) DEFAULT NULL COMMENT '___',
    handle_time DATETIME DEFAULT NULL COMMENT '____',
    handle_remark VARCHAR(500) DEFAULT NULL COMMENT '____',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '____',
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
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT '__ID',
    energy_unit_id BIGINT NOT NULL COMMENT '____ID___ID_',
    meter_id BIGINT DEFAULT NULL COMMENT '____ID',
    energy_type_id BIGINT DEFAULT NULL COMMENT '____ID',
    stat_date DATE NOT NULL COMMENT '____',
    stat_period VARCHAR(10) NOT NULL COMMENT '____(DAY/MONTH/YEAR)',
    consumption DECIMAL(18,4) NOT NULL DEFAULT 0 COMMENT '___',
    unit VARCHAR(20) DEFAULT NULL COMMENT '____',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '____',
    PRIMARY KEY (id),
    KEY idx_unit_date (energy_unit_id, stat_date),
    KEY idx_period (stat_period),
    KEY idx_meter (meter_id),
    KEY idx_energy_type (energy_type_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='能耗数据汇总表';

-- 25. 电费数据表 analysis_electricity_cost
DROP TABLE IF EXISTS analysis_electricity_cost;
CREATE TABLE analysis_electricity_cost (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT '__ID',
    meter_id BIGINT DEFAULT NULL COMMENT '____ID',
    stat_date DATE NOT NULL COMMENT '____',
    peak_power DECIMAL(18,4) DEFAULT 0 COMMENT '_____(kWh)',
    valley_power DECIMAL(18,4) DEFAULT 0 COMMENT '_____(kWh)',
    flat_power DECIMAL(18,4) DEFAULT 0 COMMENT '_____(kWh)',
    peak_cost DECIMAL(18,2) DEFAULT 0 COMMENT '____(_)',
    valley_cost DECIMAL(18,2) DEFAULT 0 COMMENT '____(_)',
    flat_cost DECIMAL(18,2) DEFAULT 0 COMMENT '____(_)',
    total_cost DECIMAL(18,2) DEFAULT 0 COMMENT '___(_)',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '____',
    PRIMARY KEY (id),
    KEY idx_meter_date (meter_id, stat_date),
    KEY idx_stat_date (stat_date)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='电费数据表';

-- 26. 能效数据表 analysis_energy_efficiency
DROP TABLE IF EXISTS analysis_energy_efficiency;
CREATE TABLE analysis_energy_efficiency (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT '__ID',
    energy_unit_id BIGINT NOT NULL COMMENT '____ID___ID_',
    stat_date DATE NOT NULL COMMENT '____',
    output_value DECIMAL(18,4) DEFAULT 0 COMMENT '___',
    output_unit VARCHAR(20) DEFAULT NULL COMMENT '____',
    energy_consumption DECIMAL(18,4) DEFAULT 0 COMMENT '_____(kgce)',
    energy_unit VARCHAR(20) DEFAULT NULL COMMENT '______',
    intensity DECIMAL(18,6) DEFAULT 0 COMMENT '__(__)',
    benchmark_value DECIMAL(18,6) DEFAULT NULL COMMENT '___',
    benchmark_unit VARCHAR(20) DEFAULT NULL COMMENT '____',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '____',
    PRIMARY KEY (id),
    KEY idx_unit_date (energy_unit_id, stat_date),
    KEY idx_stat_date (stat_date)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='能效数据表';

-- 27. 能碳预算表 analysis_energy_budget
DROP TABLE IF EXISTS analysis_energy_budget;
CREATE TABLE analysis_energy_budget (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT '__ID',
    energy_unit_id BIGINT NOT NULL COMMENT '____ID___ID_',
    budget_year INT NOT NULL COMMENT '____',
    budget_type VARCHAR(10) NOT NULL COMMENT '____(ENERGY/CARBON)',
    target_value DECIMAL(18,4) NOT NULL DEFAULT 0 COMMENT '___',
    target_unit VARCHAR(20) DEFAULT NULL COMMENT '____',
    current_value DECIMAL(18,4) DEFAULT 0 COMMENT '___',
    completion_rate DECIMAL(5,2) DEFAULT 0 COMMENT '___(%)',
    status TINYINT DEFAULT 1 COMMENT '___1__ 0___',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '____',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '____',
    deleted TINYINT DEFAULT 0 COMMENT '_____0____ 1_____',
    PRIMARY KEY (id),
    KEY idx_unit_year (energy_unit_id, budget_year),
    KEY idx_budget_type (budget_type)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='能碳预算表';

-- 28. 对标库表 analysis_benchmark
DROP TABLE IF EXISTS analysis_benchmark;
CREATE TABLE analysis_benchmark (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT '__ID',
    benchmark_name VARCHAR(200) NOT NULL COMMENT '____',
    industry VARCHAR(100) DEFAULT NULL COMMENT '____',
    energy_type_id BIGINT DEFAULT NULL COMMENT '____ID',
    energy_category VARCHAR(50) DEFAULT NULL COMMENT '____',
    benchmark_value DECIMAL(18,6) NOT NULL DEFAULT 0 COMMENT '___',
    benchmark_unit VARCHAR(20) DEFAULT NULL COMMENT '____',
    source VARCHAR(200) DEFAULT NULL COMMENT '____',
    effective_date DATE DEFAULT NULL COMMENT '____',
    expire_date DATE DEFAULT NULL COMMENT '____',
    remark VARCHAR(500) DEFAULT NULL COMMENT '__',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '____',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '____',
    deleted TINYINT DEFAULT 0 COMMENT '_____0____ 1_____',
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
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT '__ID',
    accounting_name VARCHAR(200) NOT NULL COMMENT '____',
    accounting_year INT NOT NULL COMMENT '____',
    accounting_period VARCHAR(50) DEFAULT NULL COMMENT '____',
    energy_unit_id BIGINT DEFAULT NULL COMMENT '____ID',
    scope_type VARCHAR(50) DEFAULT NULL COMMENT '____',
    total_emission DECIMAL(18,4) DEFAULT 0 COMMENT '____',
    emission_unit VARCHAR(20) DEFAULT 'tCO2' COMMENT '____',
    status VARCHAR(20) DEFAULT 'DRAFT' COMMENT '__',
    remark VARCHAR(500) DEFAULT NULL COMMENT '__',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '____',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '____',
    create_by BIGINT DEFAULT NULL COMMENT '___',
    update_by BIGINT DEFAULT NULL COMMENT '___',
    deleted TINYINT DEFAULT 0 COMMENT '_____0____ 1_____',
    PRIMARY KEY (id),
    KEY idx_year_period (accounting_year, accounting_period),
    KEY idx_accounting_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='碳排放核算主表';

-- 30. 核算明细表 carbon_accounting_detail
DROP TABLE IF EXISTS carbon_accounting_detail;
CREATE TABLE carbon_accounting_detail (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT '__ID',
    accounting_id BIGINT NOT NULL COMMENT '__ID',
    activity_name VARCHAR(200) NOT NULL COMMENT '____',
    energy_type_id BIGINT DEFAULT NULL COMMENT '____ID',
    activity_data DECIMAL(18,4) DEFAULT 0 COMMENT '____',
    activity_unit VARCHAR(20) DEFAULT NULL COMMENT '______',
    emission_factor DECIMAL(18,6) DEFAULT 0 COMMENT '____',
    factor_unit VARCHAR(20) DEFAULT NULL COMMENT '______',
    emission DECIMAL(18,4) DEFAULT 0 COMMENT '___',
    emission_unit VARCHAR(20) DEFAULT NULL COMMENT '____',
    source_description VARCHAR(500) DEFAULT NULL COMMENT '______',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '____',
    PRIMARY KEY (id),
    KEY idx_detail_accounting (accounting_id),
    KEY idx_detail_energy_type (energy_type_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='核算明细表';

-- 31. 排放因子库表 carbon_emission_factor
DROP TABLE IF EXISTS carbon_emission_factor;
CREATE TABLE carbon_emission_factor (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT '__ID',
    factor_name VARCHAR(200) NOT NULL COMMENT '____',
    factor_category VARCHAR(50) DEFAULT NULL COMMENT '____',
    energy_type_id BIGINT DEFAULT NULL COMMENT '____ID',
    factor_value DECIMAL(18,6) NOT NULL DEFAULT 0 COMMENT '___',
    factor_unit VARCHAR(20) DEFAULT NULL COMMENT '____',
    source VARCHAR(200) DEFAULT NULL COMMENT '____',
    effective_year INT DEFAULT NULL COMMENT '____',
    default_flag TINYINT DEFAULT 0 COMMENT '_____1_ 0__',
    remark VARCHAR(500) DEFAULT NULL COMMENT '__',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '____',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '____',
    create_by BIGINT DEFAULT NULL COMMENT '___',
    update_by BIGINT DEFAULT NULL COMMENT '___',
    deleted TINYINT DEFAULT 0 COMMENT '_____0____ 1_____',
    PRIMARY KEY (id),
    KEY idx_factor_category (factor_category),
    KEY idx_emission_factor_type (energy_type_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='排放因子库表';

-- 32. 碳核算模型表 carbon_accounting_model
DROP TABLE IF EXISTS carbon_accounting_model;
CREATE TABLE carbon_accounting_model (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT '__ID',
    model_name VARCHAR(200) NOT NULL COMMENT '____',
    energy_unit_id BIGINT DEFAULT NULL COMMENT '____ID',
    model_config TEXT DEFAULT NULL COMMENT '____JSON',
    status TINYINT DEFAULT 1 COMMENT '___1__ 0___',
    remark VARCHAR(500) DEFAULT NULL COMMENT '__',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '____',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '____',
    create_by BIGINT DEFAULT NULL COMMENT '___',
    update_by BIGINT DEFAULT NULL COMMENT '___',
    deleted TINYINT DEFAULT 0 COMMENT '_____0____ 1_____',
    PRIMARY KEY (id),
    KEY idx_model_unit (energy_unit_id),
    KEY idx_model_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='碳核算模型表';

-- 33. 产品碳足迹表 carbon_footprint_product
DROP TABLE IF EXISTS carbon_footprint_product;
CREATE TABLE carbon_footprint_product (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT '__ID',
    product_id BIGINT NOT NULL COMMENT '__ID',
    lifecycle_stage VARCHAR(50) DEFAULT NULL COMMENT '______',
    emission DECIMAL(18,4) DEFAULT 0 COMMENT '___',
    emission_unit VARCHAR(20) DEFAULT 'tCO2' COMMENT '____',
    calc_method VARCHAR(100) DEFAULT NULL COMMENT '____',
    data_source VARCHAR(200) DEFAULT NULL COMMENT '____',
    calc_date DATE DEFAULT NULL COMMENT '____',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '____',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '____',
    PRIMARY KEY (id),
    KEY idx_footprint_product (product_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='产品碳足迹表';

-- 34. 碳核查表 carbon_verification
DROP TABLE IF EXISTS carbon_verification;
CREATE TABLE carbon_verification (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT '__ID',
    verification_name VARCHAR(200) NOT NULL COMMENT '____',
    accounting_id BIGINT DEFAULT NULL COMMENT '__ID',
    verification_type VARCHAR(50) DEFAULT NULL COMMENT '____',
    verifier VARCHAR(100) DEFAULT NULL COMMENT '____/__',
    verification_date DATE DEFAULT NULL COMMENT '____',
    data_source_description VARCHAR(500) DEFAULT NULL COMMENT '______',
    attachment_path VARCHAR(500) DEFAULT NULL COMMENT '____',
    verification_result VARCHAR(50) DEFAULT NULL COMMENT '____',
    status VARCHAR(20) DEFAULT 'PENDING' COMMENT '__',
    remark VARCHAR(500) DEFAULT NULL COMMENT '__',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '____',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '____',
    create_by BIGINT DEFAULT NULL COMMENT '___',
    update_by BIGINT DEFAULT NULL COMMENT '___',
    deleted TINYINT DEFAULT 0 COMMENT '_____0____ 1_____',
    PRIMARY KEY (id),
    KEY idx_verification_accounting (accounting_id),
    KEY idx_verification_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='碳核查表';

-- 35. 碳资产表 carbon_asset
DROP TABLE IF EXISTS carbon_asset;
CREATE TABLE carbon_asset (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT '__ID',
    asset_type VARCHAR(20) NOT NULL COMMENT '____(QUOTA/CCER/GREEN_CERT)',
    asset_name VARCHAR(200) NOT NULL COMMENT '____',
    asset_code VARCHAR(50) DEFAULT NULL COMMENT '____',
    quantity DECIMAL(18,4) NOT NULL DEFAULT 0 COMMENT '__',
    unit VARCHAR(20) DEFAULT NULL COMMENT '__',
    acquire_date DATE DEFAULT NULL COMMENT '____',
    expire_date DATE DEFAULT NULL COMMENT '____',
    source VARCHAR(200) DEFAULT NULL COMMENT '__',
    cost DECIMAL(18,2) DEFAULT 0 COMMENT '__(_)',
    status VARCHAR(20) NOT NULL DEFAULT 'AVAILABLE' COMMENT '__(AVAILABLE/USED/FROZEN/EXPIRED)',
    remark VARCHAR(500) DEFAULT NULL COMMENT '__',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '____',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '____',
    create_by BIGINT DEFAULT NULL COMMENT '___',
    update_by BIGINT DEFAULT NULL COMMENT '___',
    deleted TINYINT DEFAULT 0 COMMENT '_____0____ 1_____',
    PRIMARY KEY (id),
    KEY idx_asset_type (asset_type),
    KEY idx_asset_status (status),
    KEY idx_acquire_date (acquire_date)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='碳资产表';

-- 36. 碳资产交易表 carbon_asset_trade
DROP TABLE IF EXISTS carbon_asset_trade;
CREATE TABLE carbon_asset_trade (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT '__ID',
    asset_id BIGINT NOT NULL COMMENT '___ID',
    trade_type VARCHAR(20) NOT NULL COMMENT '____(BUY/SELL/TRANSFER)',
    trade_quantity DECIMAL(18,4) NOT NULL DEFAULT 0 COMMENT '____',
    trade_price DECIMAL(18,2) NOT NULL DEFAULT 0 COMMENT '____(_)',
    trade_amount DECIMAL(18,2) NOT NULL DEFAULT 0 COMMENT '____(_)',
    trade_date DATE NOT NULL COMMENT '____',
    counterparty VARCHAR(200) DEFAULT NULL COMMENT '____',
    status VARCHAR(20) DEFAULT 'COMPLETED' COMMENT '__',
    remark VARCHAR(500) DEFAULT NULL COMMENT '__',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '____',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '____',
    deleted TINYINT DEFAULT 0 COMMENT '_____0____ 1_____',
    PRIMARY KEY (id),
    KEY idx_asset_id (asset_id),
    KEY idx_trade_date (trade_date),
    KEY idx_trade_type (trade_type)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='碳资产交易表';

-- 37. 市场供需信息表 carbon_market_info
DROP TABLE IF EXISTS carbon_market_info;
CREATE TABLE carbon_market_info (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT '__ID',
    info_title VARCHAR(200) NOT NULL COMMENT '____',
    info_category VARCHAR(20) NOT NULL COMMENT '____(POLICY/MARKET/PRICE/FORECAST)',
    content TEXT DEFAULT NULL COMMENT '__',
    source VARCHAR(200) DEFAULT NULL COMMENT '__',
    publish_date DATE DEFAULT NULL COMMENT '____',
    status TINYINT DEFAULT 1 COMMENT '___1__ 0___',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '____',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '____',
    create_by BIGINT DEFAULT NULL COMMENT '___',
    update_by BIGINT DEFAULT NULL COMMENT '___',
    deleted TINYINT DEFAULT 0 COMMENT '_____0____ 1_____',
    PRIMARY KEY (id),
    KEY idx_category (info_category),
    KEY idx_publish_date (publish_date)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='市场供需信息表';

-- 38. CBAM申报表 cbam_declaration
DROP TABLE IF EXISTS cbam_declaration;
CREATE TABLE cbam_declaration (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT '__ID',
    declaration_name VARCHAR(200) NOT NULL COMMENT '____',
    declaration_period VARCHAR(50) NOT NULL COMMENT '____',
    product_name VARCHAR(200) NOT NULL COMMENT '____',
    product_code VARCHAR(50) DEFAULT NULL COMMENT '____',
    cn_code VARCHAR(50) DEFAULT NULL COMMENT 'CN__',
    origin_country VARCHAR(100) DEFAULT NULL COMMENT '___',
    import_quantity DECIMAL(18,4) NOT NULL DEFAULT 0 COMMENT '___',
    unit VARCHAR(20) DEFAULT NULL COMMENT '__',
    direct_emission DECIMAL(18,4) DEFAULT 0 COMMENT '____(tCO2)',
    indirect_emission DECIMAL(18,4) DEFAULT 0 COMMENT '____(tCO2)',
    total_emission DECIMAL(18,4) DEFAULT 0 COMMENT '___(tCO2)',
    emission_unit VARCHAR(20) DEFAULT 'tCO2' COMMENT '____',
    certificate_number VARCHAR(100) DEFAULT NULL COMMENT '____',
    status VARCHAR(20) NOT NULL DEFAULT 'DRAFT' COMMENT '__(DRAFT/SUBMITTED/APPROVED/REJECTED)',
    remark VARCHAR(500) DEFAULT NULL COMMENT '__',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '____',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '____',
    create_by BIGINT DEFAULT NULL COMMENT '___',
    update_by BIGINT DEFAULT NULL COMMENT '___',
    deleted TINYINT DEFAULT 0 COMMENT '_____0____ 1_____',
    PRIMARY KEY (id),
    KEY idx_period (declaration_period),
    KEY idx_cbam_status (status),
    KEY idx_cn_code (cn_code)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='CBAM申报表';

-- 39. CBAM明细表 cbam_declaration_detail
DROP TABLE IF EXISTS cbam_declaration_detail;
CREATE TABLE cbam_declaration_detail (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT '__ID',
    declaration_id BIGINT NOT NULL COMMENT '__ID',
    material_name VARCHAR(200) NOT NULL COMMENT '____',
    material_category VARCHAR(50) DEFAULT NULL COMMENT '____',
    consumption DECIMAL(18,4) DEFAULT 0 COMMENT '___',
    unit VARCHAR(20) DEFAULT NULL COMMENT '__',
    emission_factor DECIMAL(18,6) DEFAULT 0 COMMENT '____',
    emission DECIMAL(18,4) DEFAULT 0 COMMENT '___',
    data_source VARCHAR(200) DEFAULT NULL COMMENT '____',
    PRIMARY KEY (id),
    KEY idx_declaration_id (declaration_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='CBAM明细表';

-- 40. 供应链碳足迹表 supply_chain_carbon
DROP TABLE IF EXISTS supply_chain_carbon;
CREATE TABLE supply_chain_carbon (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT '__ID',
    supplier_name VARCHAR(200) NOT NULL COMMENT '_____',
    supplier_code VARCHAR(50) DEFAULT NULL COMMENT '_____',
    product_name VARCHAR(200) NOT NULL COMMENT '____',
    product_category VARCHAR(50) DEFAULT NULL COMMENT '____',
    carbon_footprint_value DECIMAL(18,4) DEFAULT 0 COMMENT '____',
    unit VARCHAR(20) DEFAULT NULL COMMENT '__',
    cert_type VARCHAR(50) DEFAULT NULL COMMENT '____',
    cert_number VARCHAR(100) DEFAULT NULL COMMENT '____',
    valid_date DATE DEFAULT NULL COMMENT '___',
    data_source VARCHAR(200) DEFAULT NULL COMMENT '____',
    status TINYINT DEFAULT 1 COMMENT '___1__ 0___',
    remark VARCHAR(500) DEFAULT NULL COMMENT '__',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '____',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '____',
    create_by BIGINT DEFAULT NULL COMMENT '___',
    update_by BIGINT DEFAULT NULL COMMENT '___',
    deleted TINYINT DEFAULT 0 COMMENT '_____0____ 1_____',
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
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT '__ID',
    energy_unit_id BIGINT NOT NULL COMMENT '____ID',
    report_type VARCHAR(20) NOT NULL COMMENT '____',
    stat_date DATE NOT NULL COMMENT '____',
    energy_type_id BIGINT DEFAULT NULL COMMENT '____ID',
    consumption DECIMAL(18,4) DEFAULT 0 COMMENT '___',
    consumption_unit VARCHAR(20) DEFAULT NULL COMMENT '_____',
    cost DECIMAL(18,2) DEFAULT 0 COMMENT '__',
    cost_unit VARCHAR(20) DEFAULT NULL COMMENT '____',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '____',
    PRIMARY KEY (id),
    KEY idx_summary_unit (energy_unit_id),
    KEY idx_summary_type_date (report_type, stat_date)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='能耗统计表';

-- 42. 能耗汇总表 report_energy_total
DROP TABLE IF EXISTS report_energy_total;
CREATE TABLE report_energy_total (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT '__ID',
    energy_unit_id BIGINT NOT NULL COMMENT '____ID',
    report_type VARCHAR(20) NOT NULL COMMENT '____',
    stat_date DATE NOT NULL COMMENT '____',
    total_consumption DECIMAL(18,4) DEFAULT 0 COMMENT '____',
    total_cost DECIMAL(18,2) DEFAULT 0 COMMENT '___',
    comparison_period VARCHAR(20) DEFAULT NULL COMMENT '____',
    comparison_change_rate DECIMAL(5,2) DEFAULT NULL COMMENT '__/_____(%)',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '____',
    PRIMARY KEY (id),
    KEY idx_total_unit (energy_unit_id),
    KEY idx_total_type_date (report_type, stat_date)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='能耗汇总表';

-- 43. 节能量表 report_energy_saving
DROP TABLE IF EXISTS report_energy_saving;
CREATE TABLE report_energy_saving (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT '__ID',
    energy_unit_id BIGINT NOT NULL COMMENT '____ID',
    report_type VARCHAR(20) NOT NULL COMMENT '____',
    stat_date DATE NOT NULL COMMENT '____',
    baseline_consumption DECIMAL(18,4) DEFAULT 0 COMMENT '_____',
    actual_consumption DECIMAL(18,4) DEFAULT 0 COMMENT '_____',
    saving_amount DECIMAL(18,4) DEFAULT 0 COMMENT '___',
    saving_rate DECIMAL(5,2) DEFAULT 0 COMMENT '___(%)',
    saving_amount_unit VARCHAR(20) DEFAULT NULL COMMENT '_____',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '____',
    PRIMARY KEY (id),
    KEY idx_saving_unit (energy_unit_id),
    KEY idx_saving_type_date (report_type, stat_date)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='节能量表';

-- 44. 用能成本表 report_cost_stat
DROP TABLE IF EXISTS report_cost_stat;
CREATE TABLE report_cost_stat (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT '__ID',
    energy_unit_id BIGINT NOT NULL COMMENT '____ID',
    report_type VARCHAR(20) NOT NULL COMMENT '____',
    stat_date DATE NOT NULL COMMENT '____',
    energy_type_id BIGINT DEFAULT NULL COMMENT '____ID',
    cost DECIMAL(18,2) DEFAULT 0 COMMENT '__',
    cost_unit VARCHAR(20) DEFAULT NULL COMMENT '____',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '____',
    PRIMARY KEY (id),
    KEY idx_cost_unit (energy_unit_id),
    KEY idx_cost_type_date (report_type, stat_date)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用能成本表';

-- 45. 基准能耗表 baseline_consumption
DROP TABLE IF EXISTS baseline_consumption;
CREATE TABLE baseline_consumption (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT '__ID',
    energy_unit_id BIGINT NOT NULL COMMENT '____ID',
    start_date DATE NOT NULL COMMENT '____',
    end_date DATE NOT NULL COMMENT '____',
    baseline_value DECIMAL(18,4) NOT NULL DEFAULT 0 COMMENT '___',
    baseline_type VARCHAR(50) DEFAULT NULL COMMENT '____',
    remark VARCHAR(500) DEFAULT NULL COMMENT '__',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '____',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '____',
    create_by BIGINT DEFAULT NULL COMMENT '___',
    update_by BIGINT DEFAULT NULL COMMENT '___',
    deleted TINYINT DEFAULT 0 COMMENT '_____0____ 1_____',
    PRIMARY KEY (id),
    KEY idx_baseline_unit (energy_unit_id),
    KEY idx_baseline_date (start_date, end_date)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='基准能耗表';

-- 46. 指标录入表 entry_indicator
DROP TABLE IF EXISTS entry_indicator;
CREATE TABLE entry_indicator (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT '__ID',
    energy_unit_id BIGINT NOT NULL COMMENT '____ID',
    indicator_name VARCHAR(100) NOT NULL COMMENT '____',
    indicator_type VARCHAR(50) DEFAULT NULL COMMENT '____',
    indicator_value DECIMAL(18,4) DEFAULT 0 COMMENT '___',
    indicator_unit VARCHAR(20) DEFAULT NULL COMMENT '____',
    entry_date DATE NOT NULL COMMENT '____',
    entry_person VARCHAR(50) DEFAULT NULL COMMENT '___',
    remark VARCHAR(500) DEFAULT NULL COMMENT '__',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '____',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '____',
    create_by BIGINT DEFAULT NULL COMMENT '___',
    update_by BIGINT DEFAULT NULL COMMENT '___',
    deleted TINYINT DEFAULT 0 COMMENT '_____0____ 1_____',
    PRIMARY KEY (id),
    KEY idx_indicator_unit (energy_unit_id),
    KEY idx_indicator_date (entry_date)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='指标录入表';

-- 47. 产量录入表 entry_production
DROP TABLE IF EXISTS entry_production;
CREATE TABLE entry_production (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT '__ID',
    energy_unit_id BIGINT NOT NULL COMMENT '____ID',
    product_type_id BIGINT DEFAULT NULL COMMENT '____ID',
    production_date DATE NOT NULL COMMENT '____',
    output_quantity DECIMAL(18,4) DEFAULT 0 COMMENT '__',
    output_unit VARCHAR(20) DEFAULT NULL COMMENT '____',
    batch_number VARCHAR(50) DEFAULT NULL COMMENT '___',
    remark VARCHAR(500) DEFAULT NULL COMMENT '__',
    entry_person VARCHAR(50) DEFAULT NULL COMMENT '___',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '____',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '____',
    create_by BIGINT DEFAULT NULL COMMENT '___',
    update_by BIGINT DEFAULT NULL COMMENT '___',
    deleted TINYINT DEFAULT 0 COMMENT '_____0____ 1_____',
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
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT '__ID',
    service_name VARCHAR(200) NOT NULL COMMENT '____',
    service_type VARCHAR(50) NOT NULL COMMENT '____',
    applicant VARCHAR(100) NOT NULL COMMENT '___',
    contact VARCHAR(100) DEFAULT NULL COMMENT '____',
    description TEXT DEFAULT NULL COMMENT '____',
    expected_date DATE DEFAULT NULL COMMENT '____',
    status VARCHAR(20) NOT NULL DEFAULT 'PENDING' COMMENT '__(PENDING/APPROVED/PROCESSING/COMPLETED/REJECTED)',
    result TEXT DEFAULT NULL COMMENT '____',
    remark VARCHAR(500) DEFAULT NULL COMMENT '__',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '____',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '____',
    create_by BIGINT DEFAULT NULL COMMENT '___',
    update_by BIGINT DEFAULT NULL COMMENT '___',
    deleted TINYINT DEFAULT 0 COMMENT '_____0____ 1_____',
    PRIMARY KEY (id),
    KEY idx_service_status (status),
    KEY idx_applicant (applicant),
    KEY idx_expected_date (expected_date)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='AI服务预约记录表';

-- 49. AI知识库文档表 ai_knowledge_doc
DROP TABLE IF EXISTS ai_knowledge_doc;
CREATE TABLE ai_knowledge_doc (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT '__ID',
    doc_name VARCHAR(200) NOT NULL COMMENT '____',
    doc_type VARCHAR(50) NOT NULL COMMENT '____',
    file_path VARCHAR(500) DEFAULT NULL COMMENT '____',
    file_size BIGINT DEFAULT NULL COMMENT '____(__)',
    summary TEXT DEFAULT NULL COMMENT '____',
    upload_user VARCHAR(100) DEFAULT NULL COMMENT '____',
    upload_time DATETIME DEFAULT NULL COMMENT '____',
    status TINYINT DEFAULT 1 COMMENT '___1__ 0___',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '____',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '____',
    deleted TINYINT DEFAULT 0 COMMENT '_____0____ 1_____',
    PRIMARY KEY (id),
    KEY idx_doc_type (doc_type),
    KEY idx_upload_user (upload_user)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='AI知识库文档表';

-- 50. AI报告生成任务表 ai_report_task
DROP TABLE IF EXISTS ai_report_task;
CREATE TABLE ai_report_task (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT '__ID',
    report_name VARCHAR(200) NOT NULL COMMENT '____',
    report_type VARCHAR(50) NOT NULL COMMENT '____(GREEN_FACTORY/CARBON_FOOTPRINT/VERIFICATION/ENERGY_AUDIT)',
    template_id BIGINT DEFAULT NULL COMMENT '__ID',
    params_json TEXT DEFAULT NULL COMMENT '__JSON',
    status VARCHAR(20) NOT NULL DEFAULT 'PENDING' COMMENT '__(PENDING/GENERATING/COMPLETED/FAILED)',
    result_path VARCHAR(500) DEFAULT NULL COMMENT '______',
    error_msg VARCHAR(1000) DEFAULT NULL COMMENT '____',
    progress INT DEFAULT 0 COMMENT '____(%)',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '____',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '____',
    create_by BIGINT DEFAULT NULL COMMENT '___',
    deleted TINYINT DEFAULT 0 COMMENT '_____0____ 1_____',
    PRIMARY KEY (id),
    KEY idx_report_type (report_type),
    KEY idx_report_status (status),
    KEY idx_template_id (template_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='AI报告生成任务表';

-- 51. AI报告模板表 ai_report_template
DROP TABLE IF EXISTS ai_report_template;
CREATE TABLE ai_report_template (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT '__ID',
    template_name VARCHAR(200) NOT NULL COMMENT '____',
    report_type VARCHAR(50) NOT NULL COMMENT '____',
    template_content TEXT DEFAULT NULL COMMENT '____',
    variables VARCHAR(1000) DEFAULT NULL COMMENT '____(JSON)',
    status TINYINT DEFAULT 1 COMMENT '___1__ 0___',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '____',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '____',
    deleted TINYINT DEFAULT 0 COMMENT '_____0____ 1_____',
    PRIMARY KEY (id),
    KEY idx_template_report_type (report_type)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='AI报告模板表';

-- 52. 绿色工厂申报表 toolkit_green_factory
DROP TABLE IF EXISTS toolkit_green_factory;
CREATE TABLE toolkit_green_factory (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT '__ID',
    declaration_name VARCHAR(200) NOT NULL COMMENT '____',
    enterprise_name VARCHAR(200) NOT NULL COMMENT '____',
    declaration_year INT NOT NULL COMMENT '____',
    status VARCHAR(20) NOT NULL DEFAULT 'DRAFT' COMMENT '__(DRAFT/SUBMITTED/APPROVED/REJECTED)',
    progress INT DEFAULT 0 COMMENT '__(%)',
    submit_time DATETIME DEFAULT NULL COMMENT '____',
    remark VARCHAR(500) DEFAULT NULL COMMENT '__',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '____',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '____',
    create_by BIGINT DEFAULT NULL COMMENT '___',
    deleted TINYINT DEFAULT 0 COMMENT '_____0____ 1_____',
    PRIMARY KEY (id),
    KEY idx_factory_status (status),
    KEY idx_factory_year (declaration_year)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='绿色工厂申报表';

-- 53. 申报资料清单表 toolkit_green_factory_item
DROP TABLE IF EXISTS toolkit_green_factory_item;
CREATE TABLE toolkit_green_factory_item (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT '__ID',
    declaration_id BIGINT NOT NULL COMMENT '__ID',
    item_name VARCHAR(200) NOT NULL COMMENT '____',
    item_category VARCHAR(50) DEFAULT NULL COMMENT '____',
    required TINYINT DEFAULT 1 COMMENT '_____1_ 0__',
    uploaded TINYINT DEFAULT 0 COMMENT '______1_ 0__',
    file_path VARCHAR(500) DEFAULT NULL COMMENT '____',
    upload_time DATETIME DEFAULT NULL COMMENT '____',
    remark VARCHAR(500) DEFAULT NULL COMMENT '__',
    PRIMARY KEY (id),
    KEY idx_item_declaration (declaration_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='申报资料清单表';

-- 54. 错峰排产表 toolkit_peak_shifting
DROP TABLE IF EXISTS toolkit_peak_shifting;
CREATE TABLE toolkit_peak_shifting (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT '__ID',
    plan_name VARCHAR(200) NOT NULL COMMENT '____',
    plan_date DATE NOT NULL COMMENT '____',
    shift_type VARCHAR(20) DEFAULT NULL COMMENT '____',
    production_line VARCHAR(100) DEFAULT NULL COMMENT '____',
    target_output DECIMAL(18,4) DEFAULT NULL COMMENT '____',
    actual_output DECIMAL(18,4) DEFAULT NULL COMMENT '____',
    energy_consumption DECIMAL(18,4) DEFAULT NULL COMMENT '__(kWh)',
    energy_cost DECIMAL(18,2) DEFAULT NULL COMMENT '____(_)',
    saving_rate DECIMAL(5,2) DEFAULT NULL COMMENT '___(%)',
    status TINYINT DEFAULT 1 COMMENT '___1__ 0___',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '____',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '____',
    deleted TINYINT DEFAULT 0 COMMENT '_____0____ 1_____',
    PRIMARY KEY (id),
    KEY idx_plan_date (plan_date),
    KEY idx_production_line (production_line)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='错峰排产表';

-- 55. 排产配置表 toolkit_peak_shifting_config
DROP TABLE IF EXISTS toolkit_peak_shifting_config;
CREATE TABLE toolkit_peak_shifting_config (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT '__ID',
    config_name VARCHAR(200) NOT NULL COMMENT '____',
    peak_hours_start TIME DEFAULT NULL COMMENT '_____',
    peak_hours_end TIME DEFAULT NULL COMMENT '_____',
    valley_hours_start TIME DEFAULT NULL COMMENT '_____',
    valley_hours_end TIME DEFAULT NULL COMMENT '_____',
    flat_hours_start TIME DEFAULT NULL COMMENT '_____',
    flat_hours_end TIME DEFAULT NULL COMMENT '_____',
    max_power DECIMAL(18,4) DEFAULT NULL COMMENT '____(kW)',
    priority INT DEFAULT 0 COMMENT '___',
    status TINYINT DEFAULT 1 COMMENT '___1__ 0___',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '____',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '____',
    deleted TINYINT DEFAULT 0 COMMENT '_____0____ 1_____',
    PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='排产配置表';

-- 56. 计算器记录表 toolkit_calculator_record
DROP TABLE IF EXISTS toolkit_calculator_record;
CREATE TABLE toolkit_calculator_record (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT '__ID',
    calc_name VARCHAR(200) NOT NULL COMMENT '____',
    calc_type VARCHAR(20) NOT NULL COMMENT '____(CARBON/ELECTRICITY/COST)',
    input_params_json TEXT DEFAULT NULL COMMENT '____JSON',
    result_json TEXT DEFAULT NULL COMMENT '____JSON',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '____',
    create_by BIGINT DEFAULT NULL COMMENT '___',
    deleted TINYINT DEFAULT 0 COMMENT '_____0____ 1_____',
    PRIMARY KEY (id),
    KEY idx_calc_type (calc_type)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='计算器记录表';

-- 57. 分时电价配置表 toolkit_electricity_price
DROP TABLE IF EXISTS toolkit_electricity_price;
CREATE TABLE toolkit_electricity_price (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT '__ID',
    price_name VARCHAR(200) NOT NULL COMMENT '____',
    effective_date DATE NOT NULL COMMENT '____',
    peak_price DECIMAL(18,4) NOT NULL DEFAULT 0 COMMENT '____(_/kWh)',
    flat_price DECIMAL(18,4) NOT NULL DEFAULT 0 COMMENT '____(_/kWh)',
    valley_price DECIMAL(18,4) NOT NULL DEFAULT 0 COMMENT '____(_/kWh)',
    peak_hours VARCHAR(100) DEFAULT NULL COMMENT '___(_08:00-11:00,18:00-21:00)',
    flat_hours VARCHAR(100) DEFAULT NULL COMMENT '___(_07:00-08:00,11:00-18:00)',
    valley_hours VARCHAR(100) DEFAULT NULL COMMENT '___(_21:00-07:00)',
    status TINYINT DEFAULT 1 COMMENT '___1__ 0___',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '____',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '____',
    deleted TINYINT DEFAULT 0 COMMENT '_____0____ 1_____',
    PRIMARY KEY (id),
    KEY idx_effective_date (effective_date)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='分时电价配置表';
