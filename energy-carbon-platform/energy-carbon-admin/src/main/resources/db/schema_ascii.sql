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
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT 'Primary Key ID',
    username VARCHAR(50) NOT NULL COMMENT 'Username',
    password VARCHAR(100) NOT NULL COMMENT 'Password',
    nickname VARCHAR(50) DEFAULT NULL COMMENT 'Nickname',
    phone VARCHAR(20) DEFAULT NULL COMMENT 'Phone Number',
    email VARCHAR(50) DEFAULT NULL COMMENT 'Email',
    avatar VARCHAR(200) DEFAULT NULL COMMENT 'Avatar URL',
    dept_id BIGINT DEFAULT NULL COMMENT 'Department ID',
    status TINYINT DEFAULT 1 COMMENT 'Status（1Normal 0Disabled）',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT 'Create Time',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'Update Time',
    create_by BIGINT DEFAULT NULL COMMENT '创建人',
    update_by BIGINT DEFAULT NULL COMMENT '更新人',
    PRIMARY KEY (id),
    UNIQUE KEY uk_username (username),
    KEY idx_dept_id (dept_id),
    KEY idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';

-- 2. 角色表 sys_role
DROP TABLE IF EXISTS sys_role;
CREATE TABLE sys_role (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT 'Primary Key ID',
    role_name VARCHAR(50) NOT NULL COMMENT 'Role Name称',
    role_code VARCHAR(50) NOT NULL COMMENT 'Role Code',
    remark VARCHAR(500) DEFAULT NULL COMMENT 'Remark',
    sort INT DEFAULT 0 COMMENT 'Sort Order',
    status TINYINT DEFAULT 1 COMMENT 'Status（1Normal 0Disabled）',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT 'Create Time',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'Update Time',
    create_by BIGINT DEFAULT NULL COMMENT '创建人',
    update_by BIGINT DEFAULT NULL COMMENT '更新人',
    PRIMARY KEY (id),
    UNIQUE KEY uk_role_code (role_code),
    KEY idx_role_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='角色表';

-- 3. 菜单表 sys_menu
DROP TABLE IF EXISTS sys_menu;
CREATE TABLE sys_menu (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT 'Primary Key ID',
    menu_name VARCHAR(50) NOT NULL COMMENT 'Menu Name称',
    parent_id BIGINT DEFAULT 0 COMMENT 'Parent Menu ID',
    sort INT DEFAULT 0 COMMENT 'Sort Order',
    path VARCHAR(200) DEFAULT NULL COMMENT '路由Path',
    component VARCHAR(200) DEFAULT NULL COMMENT 'ComponentPath',
    icon VARCHAR(50) DEFAULT NULL COMMENT '菜单Icon',
    menu_type TINYINT DEFAULT 1 COMMENT 'Menu Type（M目录 C菜单 F按钮）',
    status TINYINT DEFAULT 1 COMMENT 'Status（1Normal 0Disabled）',
    permission VARCHAR(100) DEFAULT NULL COMMENT 'Permission Code',
    hidden TINYINT DEFAULT 0 COMMENT '是否隐藏（1是 0否）',
    keep_alive TINYINT DEFAULT 0 COMMENT '是否缓存（1是 0否）',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT 'Create Time',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'Update Time',
    create_by BIGINT DEFAULT NULL COMMENT '创建人',
    update_by BIGINT DEFAULT NULL COMMENT '更新人',
    PRIMARY KEY (id),
    KEY idx_parent_id (parent_id),
    KEY idx_menu_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='菜单表';

-- 4. 部门表 sys_dept
DROP TABLE IF EXISTS sys_dept;
CREATE TABLE sys_dept (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT 'Primary Key ID',
    dept_name VARCHAR(50) NOT NULL COMMENT 'Department Name称',
    parent_id BIGINT DEFAULT 0 COMMENT '父Department ID',
    sort INT DEFAULT 0 COMMENT 'Sort Order',
    leader VARCHAR(50) DEFAULT NULL COMMENT 'Leader',
    phone VARCHAR(20) DEFAULT NULL COMMENT '联系电话',
    email VARCHAR(50) DEFAULT NULL COMMENT 'Email',
    status TINYINT DEFAULT 1 COMMENT 'Status（1Normal 0Disabled）',
    ancestors VARCHAR(500) DEFAULT NULL COMMENT '祖级列表',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT 'Create Time',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'Update Time',
    create_by BIGINT DEFAULT NULL COMMENT '创建人',
    update_by BIGINT DEFAULT NULL COMMENT '更新人',
    PRIMARY KEY (id),
    KEY idx_dept_parent_id (parent_id),
    KEY idx_dept_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='部门表';

-- 5. Dict Type表 sys_dict_type
DROP TABLE IF EXISTS sys_dict_type;
CREATE TABLE sys_dict_type (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT 'Primary Key ID',
    dict_name VARCHAR(50) NOT NULL COMMENT 'Dict Name称',
    dict_type VARCHAR(50) NOT NULL COMMENT 'Dict Type',
    remark VARCHAR(500) DEFAULT NULL COMMENT 'Remark',
    status TINYINT DEFAULT 1 COMMENT 'Status（1Normal 0Disabled）',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT 'Create Time',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'Update Time',
    create_by BIGINT DEFAULT NULL COMMENT '创建人',
    update_by BIGINT DEFAULT NULL COMMENT '更新人',
    PRIMARY KEY (id),
    UNIQUE KEY uk_dict_type (dict_type)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='Dict Type表';

-- 6. 字典数据表 sys_dict_data
DROP TABLE IF EXISTS sys_dict_data;
CREATE TABLE sys_dict_data (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT 'Primary Key ID',
    dict_type VARCHAR(50) NOT NULL COMMENT 'Dict Type',
    dict_label VARCHAR(50) NOT NULL COMMENT 'Dict Label',
    dict_value VARCHAR(50) NOT NULL COMMENT 'Dict Value',
    sort INT DEFAULT 0 COMMENT 'Sort Order',
    remark VARCHAR(500) DEFAULT NULL COMMENT 'Remark',
    status TINYINT DEFAULT 1 COMMENT 'Status（1Normal 0Disabled）',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT 'Create Time',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'Update Time',
    create_by BIGINT DEFAULT NULL COMMENT '创建人',
    update_by BIGINT DEFAULT NULL COMMENT '更新人',
    PRIMARY KEY (id),
    KEY idx_dict_data_type (dict_type),
    KEY idx_dict_data_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='字典数据表';

-- 7. 用户角色关联表 sys_user_role
DROP TABLE IF EXISTS sys_user_role;
CREATE TABLE sys_user_role (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT 'Primary Key ID',
    user_id BIGINT NOT NULL COMMENT 'User ID',
    role_id BIGINT NOT NULL COMMENT 'Role ID',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT 'Create Time',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'Update Time',
    create_by BIGINT DEFAULT NULL COMMENT '创建人',
    update_by BIGINT DEFAULT NULL COMMENT '更新人',
    PRIMARY KEY (id),
    UNIQUE KEY uk_user_role (user_id, role_id),
    KEY idx_user_role_user (user_id),
    KEY idx_user_role_role (role_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户角色关联表';

-- 8. 角色菜单关联表 sys_role_menu
DROP TABLE IF EXISTS sys_role_menu;
CREATE TABLE sys_role_menu (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT 'Primary Key ID',
    role_id BIGINT NOT NULL COMMENT 'Role ID',
    menu_id BIGINT NOT NULL COMMENT 'Menu ID',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT 'Create Time',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'Update Time',
    create_by BIGINT DEFAULT NULL COMMENT '创建人',
    update_by BIGINT DEFAULT NULL COMMENT '更新人',
    PRIMARY KEY (id),
    UNIQUE KEY uk_role_menu (role_id, menu_id),
    KEY idx_role_menu_role (role_id),
    KEY idx_role_menu_menu (menu_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='角色菜单关联表';

-- 9. 操作日志表 sys_oper_log
DROP TABLE IF EXISTS sys_oper_log;
CREATE TABLE sys_oper_log (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT 'Primary Key ID',
    title VARCHAR(100) DEFAULT NULL COMMENT '模块标题',
    oper_type TINYINT DEFAULT 0 COMMENT 'Operation Type（1新增 2修改 3Deleted 4授权 5导出 6导入 7强退 8生成代码 9清空数据）',
    method VARCHAR(200) DEFAULT NULL COMMENT '方法名称',
    request_method VARCHAR(10) DEFAULT NULL COMMENT '请求方式',
    oper_name VARCHAR(50) DEFAULT NULL COMMENT '操作人员',
    oper_url VARCHAR(500) DEFAULT NULL COMMENT '请求URL',
    oper_ip VARCHAR(50) DEFAULT NULL COMMENT 'Operation IP地址',
    oper_param TEXT DEFAULT NULL COMMENT 'Request Params',
    json_result TEXT DEFAULT NULL COMMENT '返回参数',
    status TINYINT DEFAULT 1 COMMENT '操作Status（1Normal 0异常）',
    error_msg TEXT DEFAULT NULL COMMENT 'Error Message',
    oper_time DATETIME DEFAULT NULL COMMENT '操作时间',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT 'Create Time',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'Update Time',
    create_by BIGINT DEFAULT NULL COMMENT '创建人',
    update_by BIGINT DEFAULT NULL COMMENT '更新人',
    PRIMARY KEY (id),
    KEY idx_oper_time (oper_time),
    KEY idx_oper_name (oper_name)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='操作日志表';

-- 10. 系统配置表 sys_config
DROP TABLE IF EXISTS sys_config;
CREATE TABLE sys_config (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT 'Primary Key ID',
    config_name VARCHAR(100) NOT NULL COMMENT 'Param Name称',
    config_key VARCHAR(100) NOT NULL COMMENT 'Param Key名',
    config_value VARCHAR(500) DEFAULT NULL COMMENT 'Param Key值',
    remark VARCHAR(500) DEFAULT NULL COMMENT 'Remark',
    status TINYINT DEFAULT 1 COMMENT 'Status（1Normal 0Disabled）',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT 'Create Time',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'Update Time',
    create_by BIGINT DEFAULT NULL COMMENT '创建人',
    update_by BIGINT DEFAULT NULL COMMENT '更新人',
    PRIMARY KEY (id),
    UNIQUE KEY uk_config_key (config_key)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='系统配置表';

-- ============================================================
-- 二、基础数据模块（8张表）
-- ============================================================

-- 11. 用能设备表 energy_device
DROP TABLE IF EXISTS energy_device;
CREATE TABLE energy_device (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT 'Primary Key ID',
    device_code VARCHAR(50) NOT NULL COMMENT '设备编码',
    device_name VARCHAR(100) NOT NULL COMMENT 'Device Name',
    device_type VARCHAR(50) DEFAULT NULL COMMENT '设备类型',
    dept_id BIGINT DEFAULT NULL COMMENT 'Department ID',
    status TINYINT DEFAULT 1 COMMENT 'Status（1Normal 0Disabled）',
    manufacturer VARCHAR(100) DEFAULT NULL COMMENT '制造商',
    model VARCHAR(100) DEFAULT NULL COMMENT 'Model',
    install_date DATE DEFAULT NULL COMMENT '安装日期',
    power_rating DECIMAL(18,4) DEFAULT NULL COMMENT '额定Power(kW)',
    voltage_level VARCHAR(20) DEFAULT NULL COMMENT '电压等级',
    remark VARCHAR(500) DEFAULT NULL COMMENT 'Remark',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT 'Create Time',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'Update Time',
    create_by BIGINT DEFAULT NULL COMMENT '创建人',
    update_by BIGINT DEFAULT NULL COMMENT '更新人',
    PRIMARY KEY (id),
    UNIQUE KEY uk_device_code (device_code),
    KEY idx_device_dept (dept_id),
    KEY idx_device_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用能设备表';

-- 12. Meter Instrument表 energy_meter
DROP TABLE IF EXISTS energy_meter;
CREATE TABLE energy_meter (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT 'Primary Key ID',
    meter_code VARCHAR(50) NOT NULL COMMENT 'Meter Instrument编码',
    meter_name VARCHAR(100) NOT NULL COMMENT 'Meter Instrument名称',
    meter_type VARCHAR(50) DEFAULT NULL COMMENT 'Meter Instrument类型',
    device_id BIGINT DEFAULT NULL COMMENT 'Belonging DeviceID',
    dept_id BIGINT DEFAULT NULL COMMENT 'Department ID',
    status TINYINT DEFAULT 1 COMMENT 'Status（1Normal 0Disabled）',
    accuracy_class VARCHAR(20) DEFAULT NULL COMMENT 'Precision等级',
    range_min DECIMAL(18,4) DEFAULT NULL COMMENT 'Range Min',
    range_max DECIMAL(18,4) DEFAULT NULL COMMENT 'Range Max',
    install_date DATE DEFAULT NULL COMMENT '安装日期',
    calibration_date DATE DEFAULT NULL COMMENT '校准日期',
    next_calibration_date DATE DEFAULT NULL COMMENT '下次校准日期',
    remark VARCHAR(500) DEFAULT NULL COMMENT 'Remark',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT 'Create Time',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'Update Time',
    create_by BIGINT DEFAULT NULL COMMENT '创建人',
    update_by BIGINT DEFAULT NULL COMMENT '更新人',
    PRIMARY KEY (id),
    UNIQUE KEY uk_meter_code (meter_code),
    KEY idx_meter_device (device_id),
    KEY idx_meter_dept (dept_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='Meter Instrument表';

-- 13. Energy Type表 energy_type
DROP TABLE IF EXISTS energy_type;
CREATE TABLE energy_type (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT 'Primary Key ID',
    type_code VARCHAR(50) NOT NULL COMMENT '能源编码',
    type_name VARCHAR(50) NOT NULL COMMENT '能源名称',
    type_category VARCHAR(50) DEFAULT NULL COMMENT '能源类别',
    unit VARCHAR(20) DEFAULT NULL COMMENT 'Unit',
    conversion_coefficient DECIMAL(18,6) DEFAULT NULL COMMENT '折标煤系数',
    carbon_factor DECIMAL(18,6) DEFAULT NULL COMMENT '碳排放因子',
    status TINYINT DEFAULT 1 COMMENT 'Status（1Normal 0Disabled）',
    remark VARCHAR(500) DEFAULT NULL COMMENT 'Remark',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT 'Create Time',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'Update Time',
    create_by BIGINT DEFAULT NULL COMMENT '创建人',
    update_by BIGINT DEFAULT NULL COMMENT '更新人',
    PRIMARY KEY (id),
    UNIQUE KEY uk_type_code (type_code)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='Energy Type表';

-- 14. Energy Type模板表 energy_type_template
DROP TABLE IF EXISTS energy_type_template;
CREATE TABLE energy_type_template (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT 'Primary Key ID',
    template_name VARCHAR(100) NOT NULL COMMENT '模板名称',
    template_code VARCHAR(50) NOT NULL COMMENT '模板编码',
    type_ids VARCHAR(500) DEFAULT NULL COMMENT 'Energy TypeID列表',
    status TINYINT DEFAULT 1 COMMENT 'Status（1Normal 0Disabled）',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT 'Create Time',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'Update Time',
    create_by BIGINT DEFAULT NULL COMMENT '创建人',
    update_by BIGINT DEFAULT NULL COMMENT '更新人',
    PRIMARY KEY (id),
    UNIQUE KEY uk_template_code (template_code)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='Energy Type模板表';

-- 15. 物联网设备表 iot_device
DROP TABLE IF EXISTS iot_device;
CREATE TABLE iot_device (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT 'Primary Key ID',
    device_code VARCHAR(50) NOT NULL COMMENT '设备编码',
    device_name VARCHAR(100) NOT NULL COMMENT 'Device Name',
    gateway_id BIGINT DEFAULT NULL COMMENT 'GatewayID',
    meter_id BIGINT DEFAULT NULL COMMENT '关联Meter InstrumentID',
    topic VARCHAR(200) DEFAULT NULL COMMENT 'MQTT主题',
    data_format VARCHAR(50) DEFAULT NULL COMMENT '数据格式',
    status TINYINT DEFAULT 1 COMMENT 'Status（1Normal 0Disabled）',
    last_online_time DATETIME DEFAULT NULL COMMENT '最后在线时间',
    remark VARCHAR(500) DEFAULT NULL COMMENT 'Remark',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT 'Create Time',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'Update Time',
    create_by BIGINT DEFAULT NULL COMMENT '创建人',
    update_by BIGINT DEFAULT NULL COMMENT '更新人',
    PRIMARY KEY (id),
    UNIQUE KEY uk_iot_device_code (device_code),
    KEY idx_iot_gateway (gateway_id),
    KEY idx_iot_meter (meter_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='物联网设备表';

-- 16. 物联网Gateway表 iot_gateway
DROP TABLE IF EXISTS iot_gateway;
CREATE TABLE iot_gateway (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT 'Primary Key ID',
    gateway_code VARCHAR(50) NOT NULL COMMENT 'Gateway编码',
    gateway_name VARCHAR(100) NOT NULL COMMENT 'Gateway名称',
    protocol_type VARCHAR(50) DEFAULT NULL COMMENT '协议类型',
    host VARCHAR(100) DEFAULT NULL COMMENT '主机地址',
    port INT DEFAULT NULL COMMENT '端口号',
    username VARCHAR(50) DEFAULT NULL COMMENT 'Username',
    password VARCHAR(100) DEFAULT NULL COMMENT 'Password',
    status TINYINT DEFAULT 1 COMMENT 'Status（1Normal 0Disabled）',
    last_online_time DATETIME DEFAULT NULL COMMENT '最后在线时间',
    remark VARCHAR(500) DEFAULT NULL COMMENT 'Remark',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT 'Create Time',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'Update Time',
    create_by BIGINT DEFAULT NULL COMMENT '创建人',
    update_by BIGINT DEFAULT NULL COMMENT '更新人',
    PRIMARY KEY (id),
    UNIQUE KEY uk_gateway_code (gateway_code)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='物联网Gateway表';

-- 17. 产品种类表 product_type
DROP TABLE IF EXISTS product_type;
CREATE TABLE product_type (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT 'Primary Key ID',
    product_code VARCHAR(50) NOT NULL COMMENT '产品编码',
    product_name VARCHAR(100) NOT NULL COMMENT 'Product Name',
    product_category VARCHAR(50) DEFAULT NULL COMMENT '产品类别',
    unit VARCHAR(20) DEFAULT NULL COMMENT 'Unit',
    carbon_factor DECIMAL(18,6) DEFAULT NULL COMMENT '碳排放因子',
    status TINYINT DEFAULT 1 COMMENT 'Status（1Normal 0Disabled）',
    remark VARCHAR(500) DEFAULT NULL COMMENT 'Remark',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT 'Create Time',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'Update Time',
    create_by BIGINT DEFAULT NULL COMMENT '创建人',
    update_by BIGINT DEFAULT NULL COMMENT '更新人',
    PRIMARY KEY (id),
    UNIQUE KEY uk_product_code (product_code)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='产品种类表';

-- 18. Work Shift管理表 work_shift
DROP TABLE IF EXISTS work_shift;
CREATE TABLE work_shift (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT 'Primary Key ID',
    shift_name VARCHAR(50) NOT NULL COMMENT 'Work Shift名称',
    shift_code VARCHAR(50) NOT NULL COMMENT 'Work Shift编码',
    start_time TIME DEFAULT NULL COMMENT 'Start Time',
    end_time TIME DEFAULT NULL COMMENT 'End Time',
    rest_start TIME DEFAULT NULL COMMENT '休息Start Time',
    rest_end TIME DEFAULT NULL COMMENT '休息End Time',
    work_days VARCHAR(50) DEFAULT NULL COMMENT '工作日（如1,2,3,4,5）',
    status TINYINT DEFAULT 1 COMMENT 'Status（1Normal 0Disabled）',
    remark VARCHAR(500) DEFAULT NULL COMMENT 'Remark',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT 'Create Time',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'Update Time',
    create_by BIGINT DEFAULT NULL COMMENT '创建人',
    update_by BIGINT DEFAULT NULL COMMENT '更新人',
    PRIMARY KEY (id),
    UNIQUE KEY uk_shift_code (shift_code)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='Work Shift管理表';

-- ============================================================
-- 三、监控模块（3张表）
-- ============================================================

-- 19. 历史数据表 monitor_history_data
DROP TABLE IF EXISTS monitor_history_data;
CREATE TABLE monitor_history_data (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT 'Primary Key ID',
    meter_id BIGINT NOT NULL COMMENT 'Meter InstrumentID',
    data_time DATETIME NOT NULL COMMENT '数据时间',
    data_value DECIMAL(18,4) NOT NULL DEFAULT 0 COMMENT '数据值',
    data_type VARCHAR(20) DEFAULT NULL COMMENT '数据类型',
    unit VARCHAR(20) DEFAULT NULL COMMENT 'Unit',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT 'Create Time',
    PRIMARY KEY (id),
    KEY idx_meter_time (meter_id, data_time),
    KEY idx_data_time (data_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='历史数据表';

-- 20. 计量抄表记录表 monitor_meter_record
DROP TABLE IF EXISTS monitor_meter_record;
CREATE TABLE monitor_meter_record (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT 'Primary Key ID',
    meter_id BIGINT NOT NULL COMMENT 'Meter InstrumentID',
    record_time DATETIME NOT NULL COMMENT '抄表时间',
    start_code DECIMAL(18,4) DEFAULT NULL COMMENT '起始读数',
    end_code DECIMAL(18,4) DEFAULT NULL COMMENT '终止读数',
    consumption DECIMAL(18,4) DEFAULT NULL COMMENT '用量',
    cost DECIMAL(18,2) DEFAULT NULL COMMENT '费用',
    unit_price DECIMAL(18,4) DEFAULT NULL COMMENT '单价',
    operator VARCHAR(50) DEFAULT NULL COMMENT '抄表人',
    remark VARCHAR(500) DEFAULT NULL COMMENT 'Remark',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT 'Create Time',
    PRIMARY KEY (id),
    KEY idx_record_meter (meter_id),
    KEY idx_record_time (record_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='计量抄表记录表';

-- 21. Status监测日志表 monitor_status_log
DROP TABLE IF EXISTS monitor_status_log;
CREATE TABLE monitor_status_log (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT 'Primary Key ID',
    device_id BIGINT NOT NULL COMMENT '设备ID',
    device_type VARCHAR(50) DEFAULT NULL COMMENT '设备类型',
    online_status TINYINT DEFAULT 1 COMMENT '在线Status（1在线 0离线）',
    offline_time DATETIME DEFAULT NULL COMMENT '离线时间',
    online_time DATETIME DEFAULT NULL COMMENT '上线时间',
    duration INT DEFAULT NULL COMMENT '持续时间(秒)',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT 'Create Time',
    PRIMARY KEY (id),
    KEY idx_status_device (device_id),
    KEY idx_status_offline (offline_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='Status监测日志表';

-- ============================================================
-- 四、报警模块（2张表）
-- ============================================================

-- 22. 报警阈值配置表 alarm_threshold
DROP TABLE IF EXISTS alarm_threshold;
CREATE TABLE alarm_threshold (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT 'Primary Key ID',
    threshold_name VARCHAR(100) NOT NULL COMMENT '阈值名称',
    energy_unit_id BIGINT DEFAULT NULL COMMENT '用能单元ID',
    metric_type VARCHAR(50) DEFAULT NULL COMMENT '指标类型',
    compare_type VARCHAR(20) DEFAULT NULL COMMENT '比较类型（GT大于/LT小于/EQ等于）',
    threshold_value DECIMAL(18,4) NOT NULL DEFAULT 0 COMMENT '阈值',
    alarm_level VARCHAR(20) DEFAULT NULL COMMENT '报警级别',
    enabled TINYINT DEFAULT 1 COMMENT '是否启用（1是 0否）',
    notify_method VARCHAR(50) DEFAULT NULL COMMENT '通知方式',
    remark VARCHAR(500) DEFAULT NULL COMMENT 'Remark',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT 'Create Time',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'Update Time',
    deleted TINYINT DEFAULT 0 COMMENT 'Delete Flag（0代表Exists 1代表Deleted）',
    PRIMARY KEY (id),
    KEY idx_threshold_unit (energy_unit_id),
    KEY idx_threshold_enabled (enabled)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='报警阈值配置表';

-- 23. 报警记录表 alarm_record
DROP TABLE IF EXISTS alarm_record;
CREATE TABLE alarm_record (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT 'Primary Key ID',
    threshold_id BIGINT DEFAULT NULL COMMENT '阈值ID',
    meter_id BIGINT DEFAULT NULL COMMENT 'Meter InstrumentID',
    alarm_type VARCHAR(50) DEFAULT NULL COMMENT '报警类型',
    alarm_level VARCHAR(20) DEFAULT NULL COMMENT '报警级别',
    alarm_value DECIMAL(18,4) DEFAULT NULL COMMENT '报警值',
    threshold_value DECIMAL(18,4) DEFAULT NULL COMMENT '阈值',
    alarm_time DATETIME NOT NULL COMMENT '报警时间',
    handle_status VARCHAR(20) DEFAULT 'PENDING' COMMENT '处理Status（PENDING未处理/PROCESSING处理中/HANDLED已处理/IGNORED已忽略）',
    handler VARCHAR(50) DEFAULT NULL COMMENT '处理人',
    handle_time DATETIME DEFAULT NULL COMMENT '处理时间',
    handle_remark VARCHAR(500) DEFAULT NULL COMMENT '处理Remark',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT 'Create Time',
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
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT 'Primary Key ID',
    energy_unit_id BIGINT NOT NULL COMMENT '用能单元ID（Department ID）',
    meter_id BIGINT DEFAULT NULL COMMENT 'Meter InstrumentID',
    energy_type_id BIGINT DEFAULT NULL COMMENT 'Energy TypeID',
    stat_date DATE NOT NULL COMMENT '统计日期',
    stat_period VARCHAR(10) NOT NULL COMMENT '统计周期(DAY/MONTH/YEAR)',
    consumption DECIMAL(18,4) NOT NULL DEFAULT 0 COMMENT '消耗量',
    unit VARCHAR(20) DEFAULT NULL COMMENT 'Unit',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT 'Create Time',
    PRIMARY KEY (id),
    KEY idx_unit_date (energy_unit_id, stat_date),
    KEY idx_period (stat_period),
    KEY idx_meter (meter_id),
    KEY idx_energy_type (energy_type_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='能耗数据汇总表';

-- 25. 电费数据表 analysis_electricity_cost
DROP TABLE IF EXISTS analysis_electricity_cost;
CREATE TABLE analysis_electricity_cost (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT 'Primary Key ID',
    meter_id BIGINT DEFAULT NULL COMMENT 'Meter InstrumentID',
    stat_date DATE NOT NULL COMMENT '统计日期',
    peak_power DECIMAL(18,4) DEFAULT 0 COMMENT '峰段用电量(kWh)',
    valley_power DECIMAL(18,4) DEFAULT 0 COMMENT '谷段用电量(kWh)',
    flat_power DECIMAL(18,4) DEFAULT 0 COMMENT '平段用电量(kWh)',
    peak_cost DECIMAL(18,2) DEFAULT 0 COMMENT '峰段电费(元)',
    valley_cost DECIMAL(18,2) DEFAULT 0 COMMENT '谷段电费(元)',
    flat_cost DECIMAL(18,2) DEFAULT 0 COMMENT '平段电费(元)',
    total_cost DECIMAL(18,2) DEFAULT 0 COMMENT '总电费(元)',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT 'Create Time',
    PRIMARY KEY (id),
    KEY idx_meter_date (meter_id, stat_date),
    KEY idx_stat_date (stat_date)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='电费数据表';

-- 26. 能效数据表 analysis_energy_efficiency
DROP TABLE IF EXISTS analysis_energy_efficiency;
CREATE TABLE analysis_energy_efficiency (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT 'Primary Key ID',
    energy_unit_id BIGINT NOT NULL COMMENT '用能单元ID（Department ID）',
    stat_date DATE NOT NULL COMMENT '统计日期',
    output_value DECIMAL(18,4) DEFAULT 0 COMMENT '产出值',
    output_unit VARCHAR(20) DEFAULT NULL COMMENT '产出单位',
    energy_consumption DECIMAL(18,4) DEFAULT 0 COMMENT '能源消耗量(kgce)',
    energy_unit VARCHAR(20) DEFAULT NULL COMMENT '能源消耗单位',
    intensity DECIMAL(18,6) DEFAULT 0 COMMENT '单耗(强度)',
    benchmark_value DECIMAL(18,6) DEFAULT NULL COMMENT '标杆值',
    benchmark_unit VARCHAR(20) DEFAULT NULL COMMENT '标杆单位',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT 'Create Time',
    PRIMARY KEY (id),
    KEY idx_unit_date (energy_unit_id, stat_date),
    KEY idx_stat_date (stat_date)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='能效数据表';

-- 27. 能碳预算表 analysis_energy_budget
DROP TABLE IF EXISTS analysis_energy_budget;
CREATE TABLE analysis_energy_budget (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT 'Primary Key ID',
    energy_unit_id BIGINT NOT NULL COMMENT '用能单元ID（Department ID）',
    budget_year INT NOT NULL COMMENT '预算年度',
    budget_type VARCHAR(10) NOT NULL COMMENT '预算类型(ENERGY/CARBON)',
    target_value DECIMAL(18,4) NOT NULL DEFAULT 0 COMMENT '目标值',
    target_unit VARCHAR(20) DEFAULT NULL COMMENT '目标单位',
    current_value DECIMAL(18,4) DEFAULT 0 COMMENT '当前值',
    completion_rate DECIMAL(5,2) DEFAULT 0 COMMENT '完成率(%)',
    status TINYINT DEFAULT 1 COMMENT 'Status（1Normal 0Disabled）',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT 'Create Time',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'Update Time',
    deleted TINYINT DEFAULT 0 COMMENT 'Delete Flag（0代表Exists 1代表Deleted）',
    PRIMARY KEY (id),
    KEY idx_unit_year (energy_unit_id, budget_year),
    KEY idx_budget_type (budget_type)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='能碳预算表';

-- 28. 对标库表 analysis_benchmark
DROP TABLE IF EXISTS analysis_benchmark;
CREATE TABLE analysis_benchmark (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT 'Primary Key ID',
    benchmark_name VARCHAR(200) NOT NULL COMMENT '对标名称',
    industry VARCHAR(100) DEFAULT NULL COMMENT '所属行业',
    energy_type_id BIGINT DEFAULT NULL COMMENT 'Energy TypeID',
    energy_category VARCHAR(50) DEFAULT NULL COMMENT '能源类别',
    benchmark_value DECIMAL(18,6) NOT NULL DEFAULT 0 COMMENT '标杆值',
    benchmark_unit VARCHAR(20) DEFAULT NULL COMMENT '标杆单位',
    source VARCHAR(200) DEFAULT NULL COMMENT '数据来源',
    effective_date DATE DEFAULT NULL COMMENT '生效日期',
    expire_date DATE DEFAULT NULL COMMENT '失效日期',
    remark VARCHAR(500) DEFAULT NULL COMMENT 'Remark',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT 'Create Time',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'Update Time',
    deleted TINYINT DEFAULT 0 COMMENT 'Delete Flag（0代表Exists 1代表Deleted）',
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
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT 'Primary Key ID',
    accounting_name VARCHAR(200) NOT NULL COMMENT '核算名称',
    accounting_year INT NOT NULL COMMENT '核算年度',
    accounting_period VARCHAR(50) DEFAULT NULL COMMENT '核算周期',
    energy_unit_id BIGINT DEFAULT NULL COMMENT '用能单元ID',
    scope_type VARCHAR(50) DEFAULT NULL COMMENT '范围类型',
    total_emission DECIMAL(18,4) DEFAULT 0 COMMENT '总排放量',
    emission_unit VARCHAR(20) DEFAULT 'tCO2' COMMENT '排放单位',
    status VARCHAR(20) DEFAULT 'DRAFT' COMMENT 'Status',
    remark VARCHAR(500) DEFAULT NULL COMMENT 'Remark',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT 'Create Time',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'Update Time',
    create_by BIGINT DEFAULT NULL COMMENT '创建人',
    update_by BIGINT DEFAULT NULL COMMENT '更新人',
    deleted TINYINT DEFAULT 0 COMMENT 'Delete Flag（0代表Exists 1代表Deleted）',
    PRIMARY KEY (id),
    KEY idx_year_period (accounting_year, accounting_period),
    KEY idx_accounting_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='碳排放核算主表';

-- 30. 核算明细表 carbon_accounting_detail
DROP TABLE IF EXISTS carbon_accounting_detail;
CREATE TABLE carbon_accounting_detail (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT 'Primary Key ID',
    accounting_id BIGINT NOT NULL COMMENT '核算ID',
    activity_name VARCHAR(200) NOT NULL COMMENT '活动名称',
    energy_type_id BIGINT DEFAULT NULL COMMENT 'Energy TypeID',
    activity_data DECIMAL(18,4) DEFAULT 0 COMMENT '活动数据',
    activity_unit VARCHAR(20) DEFAULT NULL COMMENT '活动数据单位',
    emission_factor DECIMAL(18,6) DEFAULT 0 COMMENT '排放因子',
    factor_unit VARCHAR(20) DEFAULT NULL COMMENT '排放因子单位',
    emission DECIMAL(18,4) DEFAULT 0 COMMENT '排放量',
    emission_unit VARCHAR(20) DEFAULT NULL COMMENT '排放单位',
    source_description VARCHAR(500) DEFAULT NULL COMMENT '数据来源描述',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT 'Create Time',
    PRIMARY KEY (id),
    KEY idx_detail_accounting (accounting_id),
    KEY idx_detail_energy_type (energy_type_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='核算明细表';

-- 31. 排放因子库表 carbon_emission_factor
DROP TABLE IF EXISTS carbon_emission_factor;
CREATE TABLE carbon_emission_factor (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT 'Primary Key ID',
    factor_name VARCHAR(200) NOT NULL COMMENT '因子名称',
    factor_category VARCHAR(50) DEFAULT NULL COMMENT '因子类别',
    energy_type_id BIGINT DEFAULT NULL COMMENT 'Energy TypeID',
    factor_value DECIMAL(18,6) NOT NULL DEFAULT 0 COMMENT '因子值',
    factor_unit VARCHAR(20) DEFAULT NULL COMMENT '因子单位',
    source VARCHAR(200) DEFAULT NULL COMMENT '数据来源',
    effective_year INT DEFAULT NULL COMMENT '生效年份',
    default_flag TINYINT DEFAULT 0 COMMENT 'Is Default（1是 0否）',
    remark VARCHAR(500) DEFAULT NULL COMMENT 'Remark',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT 'Create Time',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'Update Time',
    create_by BIGINT DEFAULT NULL COMMENT '创建人',
    update_by BIGINT DEFAULT NULL COMMENT '更新人',
    deleted TINYINT DEFAULT 0 COMMENT 'Delete Flag（0代表Exists 1代表Deleted）',
    PRIMARY KEY (id),
    KEY idx_factor_category (factor_category),
    KEY idx_emission_factor_type (energy_type_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='排放因子库表';

-- 32. 碳核算模型表 carbon_accounting_model
DROP TABLE IF EXISTS carbon_accounting_model;
CREATE TABLE carbon_accounting_model (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT 'Primary Key ID',
    model_name VARCHAR(200) NOT NULL COMMENT '模型名称',
    energy_unit_id BIGINT DEFAULT NULL COMMENT '用能单元ID',
    model_config TEXT DEFAULT NULL COMMENT '模型配置JSON',
    status TINYINT DEFAULT 1 COMMENT 'Status（1Normal 0Disabled）',
    remark VARCHAR(500) DEFAULT NULL COMMENT 'Remark',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT 'Create Time',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'Update Time',
    create_by BIGINT DEFAULT NULL COMMENT '创建人',
    update_by BIGINT DEFAULT NULL COMMENT '更新人',
    deleted TINYINT DEFAULT 0 COMMENT 'Delete Flag（0代表Exists 1代表Deleted）',
    PRIMARY KEY (id),
    KEY idx_model_unit (energy_unit_id),
    KEY idx_model_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='碳核算模型表';

-- 33. 产品碳足迹表 carbon_footprint_product
DROP TABLE IF EXISTS carbon_footprint_product;
CREATE TABLE carbon_footprint_product (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT 'Primary Key ID',
    product_id BIGINT NOT NULL COMMENT '产品ID',
    lifecycle_stage VARCHAR(50) DEFAULT NULL COMMENT '生命周期阶段',
    emission DECIMAL(18,4) DEFAULT 0 COMMENT '排放量',
    emission_unit VARCHAR(20) DEFAULT 'tCO2' COMMENT '排放单位',
    calc_method VARCHAR(100) DEFAULT NULL COMMENT '计算方法',
    data_source VARCHAR(200) DEFAULT NULL COMMENT '数据来源',
    calc_date DATE DEFAULT NULL COMMENT '计算日期',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT 'Create Time',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'Update Time',
    PRIMARY KEY (id),
    KEY idx_footprint_product (product_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='产品碳足迹表';

-- 34. 碳核查表 carbon_verification
DROP TABLE IF EXISTS carbon_verification;
CREATE TABLE carbon_verification (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT 'Primary Key ID',
    verification_name VARCHAR(200) NOT NULL COMMENT '核查名称',
    accounting_id BIGINT DEFAULT NULL COMMENT '核算ID',
    verification_type VARCHAR(50) DEFAULT NULL COMMENT '核查类型',
    verifier VARCHAR(100) DEFAULT NULL COMMENT '核查机构/人员',
    verification_date DATE DEFAULT NULL COMMENT '核查日期',
    data_source_description VARCHAR(500) DEFAULT NULL COMMENT '数据来源描述',
    attachment_path VARCHAR(500) DEFAULT NULL COMMENT '附件Path',
    verification_result VARCHAR(50) DEFAULT NULL COMMENT '核查结果',
    status VARCHAR(20) DEFAULT 'PENDING' COMMENT 'Status',
    remark VARCHAR(500) DEFAULT NULL COMMENT 'Remark',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT 'Create Time',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'Update Time',
    create_by BIGINT DEFAULT NULL COMMENT '创建人',
    update_by BIGINT DEFAULT NULL COMMENT '更新人',
    deleted TINYINT DEFAULT 0 COMMENT 'Delete Flag（0代表Exists 1代表Deleted）',
    PRIMARY KEY (id),
    KEY idx_verification_accounting (accounting_id),
    KEY idx_verification_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='碳核查表';

-- 35. 碳资产表 carbon_asset
DROP TABLE IF EXISTS carbon_asset;
CREATE TABLE carbon_asset (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT 'Primary Key ID',
    asset_type VARCHAR(20) NOT NULL COMMENT '资产类型(QUOTA/CCER/GREEN_CERT)',
    asset_name VARCHAR(200) NOT NULL COMMENT '资产名称',
    asset_code VARCHAR(50) DEFAULT NULL COMMENT '资产编号',
    quantity DECIMAL(18,4) NOT NULL DEFAULT 0 COMMENT '数量',
    unit VARCHAR(20) DEFAULT NULL COMMENT '单位',
    acquire_date DATE DEFAULT NULL COMMENT '获取日期',
    expire_date DATE DEFAULT NULL COMMENT '到期日期',
    source VARCHAR(200) DEFAULT NULL COMMENT '来源',
    cost DECIMAL(18,2) DEFAULT 0 COMMENT '成本(元)',
    status VARCHAR(20) NOT NULL DEFAULT 'AVAILABLE' COMMENT 'Status(AVAILABLE/USED/FROZEN/EXPIRED)',
    remark VARCHAR(500) DEFAULT NULL COMMENT 'Remark',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT 'Create Time',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'Update Time',
    create_by BIGINT DEFAULT NULL COMMENT '创建人',
    update_by BIGINT DEFAULT NULL COMMENT '更新人',
    deleted TINYINT DEFAULT 0 COMMENT 'Delete Flag（0代表Exists 1代表Deleted）',
    PRIMARY KEY (id),
    KEY idx_asset_type (asset_type),
    KEY idx_asset_status (status),
    KEY idx_acquire_date (acquire_date)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='碳资产表';

-- 36. 碳资产交易表 carbon_asset_trade
DROP TABLE IF EXISTS carbon_asset_trade;
CREATE TABLE carbon_asset_trade (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT 'Primary Key ID',
    asset_id BIGINT NOT NULL COMMENT '碳资产ID',
    trade_type VARCHAR(20) NOT NULL COMMENT '交易类型(BUY/SELL/TRANSFER)',
    trade_quantity DECIMAL(18,4) NOT NULL DEFAULT 0 COMMENT '交易数量',
    trade_price DECIMAL(18,2) NOT NULL DEFAULT 0 COMMENT '交易单价(元)',
    trade_amount DECIMAL(18,2) NOT NULL DEFAULT 0 COMMENT '交易金额(元)',
    trade_date DATE NOT NULL COMMENT '交易日期',
    counterparty VARCHAR(200) DEFAULT NULL COMMENT '交易对手',
    status VARCHAR(20) DEFAULT 'COMPLETED' COMMENT 'Status',
    remark VARCHAR(500) DEFAULT NULL COMMENT 'Remark',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT 'Create Time',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'Update Time',
    deleted TINYINT DEFAULT 0 COMMENT 'Delete Flag（0代表Exists 1代表Deleted）',
    PRIMARY KEY (id),
    KEY idx_asset_id (asset_id),
    KEY idx_trade_date (trade_date),
    KEY idx_trade_type (trade_type)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='碳资产交易表';

-- 37. 市场供需信息表 carbon_market_info
DROP TABLE IF EXISTS carbon_market_info;
CREATE TABLE carbon_market_info (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT 'Primary Key ID',
    info_title VARCHAR(200) NOT NULL COMMENT '信息标题',
    info_category VARCHAR(20) NOT NULL COMMENT '信息分类(POLICY/MARKET/PRICE/FORECAST)',
    content TEXT DEFAULT NULL COMMENT '内容',
    source VARCHAR(200) DEFAULT NULL COMMENT '来源',
    publish_date DATE DEFAULT NULL COMMENT '发布日期',
    status TINYINT DEFAULT 1 COMMENT 'Status（1启用 0Disabled）',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT 'Create Time',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'Update Time',
    create_by BIGINT DEFAULT NULL COMMENT '创建人',
    update_by BIGINT DEFAULT NULL COMMENT '更新人',
    deleted TINYINT DEFAULT 0 COMMENT 'Delete Flag（0代表Exists 1代表Deleted）',
    PRIMARY KEY (id),
    KEY idx_category (info_category),
    KEY idx_publish_date (publish_date)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='市场供需信息表';

-- 38. CBAM申报表 cbam_declaration
DROP TABLE IF EXISTS cbam_declaration;
CREATE TABLE cbam_declaration (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT 'Primary Key ID',
    declaration_name VARCHAR(200) NOT NULL COMMENT '申报名称',
    declaration_period VARCHAR(50) NOT NULL COMMENT '申报周期',
    product_name VARCHAR(200) NOT NULL COMMENT 'Product Name',
    product_code VARCHAR(50) DEFAULT NULL COMMENT '产品编码',
    cn_code VARCHAR(50) DEFAULT NULL COMMENT 'CN编码',
    origin_country VARCHAR(100) DEFAULT NULL COMMENT '原产国',
    import_quantity DECIMAL(18,4) NOT NULL DEFAULT 0 COMMENT '进口量',
    unit VARCHAR(20) DEFAULT NULL COMMENT '单位',
    direct_emission DECIMAL(18,4) DEFAULT 0 COMMENT '直接排放(tCO2)',
    indirect_emission DECIMAL(18,4) DEFAULT 0 COMMENT '间接排放(tCO2)',
    total_emission DECIMAL(18,4) DEFAULT 0 COMMENT '总排放(tCO2)',
    emission_unit VARCHAR(20) DEFAULT 'tCO2' COMMENT '排放单位',
    certificate_number VARCHAR(100) DEFAULT NULL COMMENT '证书编号',
    status VARCHAR(20) NOT NULL DEFAULT 'DRAFT' COMMENT 'Status(DRAFT/SUBMITTED/APPROVED/REJECTED)',
    remark VARCHAR(500) DEFAULT NULL COMMENT 'Remark',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT 'Create Time',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'Update Time',
    create_by BIGINT DEFAULT NULL COMMENT '创建人',
    update_by BIGINT DEFAULT NULL COMMENT '更新人',
    deleted TINYINT DEFAULT 0 COMMENT 'Delete Flag（0代表Exists 1代表Deleted）',
    PRIMARY KEY (id),
    KEY idx_period (declaration_period),
    KEY idx_cbam_status (status),
    KEY idx_cn_code (cn_code)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='CBAM申报表';

-- 39. CBAM明细表 cbam_declaration_detail
DROP TABLE IF EXISTS cbam_declaration_detail;
CREATE TABLE cbam_declaration_detail (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT 'Primary Key ID',
    declaration_id BIGINT NOT NULL COMMENT '申报ID',
    material_name VARCHAR(200) NOT NULL COMMENT '材料名称',
    material_category VARCHAR(50) DEFAULT NULL COMMENT '材料类别',
    consumption DECIMAL(18,4) DEFAULT 0 COMMENT '消耗量',
    unit VARCHAR(20) DEFAULT NULL COMMENT '单位',
    emission_factor DECIMAL(18,6) DEFAULT 0 COMMENT '排放因子',
    emission DECIMAL(18,4) DEFAULT 0 COMMENT '排放量',
    data_source VARCHAR(200) DEFAULT NULL COMMENT '数据来源',
    PRIMARY KEY (id),
    KEY idx_declaration_id (declaration_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='CBAM明细表';

-- 40. 供应链碳足迹表 supply_chain_carbon
DROP TABLE IF EXISTS supply_chain_carbon;
CREATE TABLE supply_chain_carbon (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT 'Primary Key ID',
    supplier_name VARCHAR(200) NOT NULL COMMENT '供应商名称',
    supplier_code VARCHAR(50) DEFAULT NULL COMMENT '供应商编码',
    product_name VARCHAR(200) NOT NULL COMMENT 'Product Name',
    product_category VARCHAR(50) DEFAULT NULL COMMENT '产品类别',
    carbon_footprint_value DECIMAL(18,4) DEFAULT 0 COMMENT '碳足迹值',
    unit VARCHAR(20) DEFAULT NULL COMMENT '单位',
    cert_type VARCHAR(50) DEFAULT NULL COMMENT '认证类型',
    cert_number VARCHAR(100) DEFAULT NULL COMMENT '认证编号',
    valid_date DATE DEFAULT NULL COMMENT '有效期',
    data_source VARCHAR(200) DEFAULT NULL COMMENT '数据来源',
    status TINYINT DEFAULT 1 COMMENT 'Status（1Normal 0Disabled）',
    remark VARCHAR(500) DEFAULT NULL COMMENT 'Remark',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT 'Create Time',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'Update Time',
    create_by BIGINT DEFAULT NULL COMMENT '创建人',
    update_by BIGINT DEFAULT NULL COMMENT '更新人',
    deleted TINYINT DEFAULT 0 COMMENT 'Delete Flag（0代表Exists 1代表Deleted）',
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
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT 'Primary Key ID',
    energy_unit_id BIGINT NOT NULL COMMENT '用能单元ID',
    report_type VARCHAR(20) NOT NULL COMMENT '报表类型',
    stat_date DATE NOT NULL COMMENT '统计日期',
    energy_type_id BIGINT DEFAULT NULL COMMENT 'Energy TypeID',
    consumption DECIMAL(18,4) DEFAULT 0 COMMENT '消耗量',
    consumption_unit VARCHAR(20) DEFAULT NULL COMMENT '消耗量单位',
    cost DECIMAL(18,2) DEFAULT 0 COMMENT '费用',
    cost_unit VARCHAR(20) DEFAULT NULL COMMENT '费用单位',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT 'Create Time',
    PRIMARY KEY (id),
    KEY idx_summary_unit (energy_unit_id),
    KEY idx_summary_type_date (report_type, stat_date)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='能耗统计表';

-- 42. 能耗汇总表 report_energy_total
DROP TABLE IF EXISTS report_energy_total;
CREATE TABLE report_energy_total (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT 'Primary Key ID',
    energy_unit_id BIGINT NOT NULL COMMENT '用能单元ID',
    report_type VARCHAR(20) NOT NULL COMMENT '报表类型',
    stat_date DATE NOT NULL COMMENT '统计日期',
    total_consumption DECIMAL(18,4) DEFAULT 0 COMMENT '总消耗量',
    total_cost DECIMAL(18,2) DEFAULT 0 COMMENT '总费用',
    comparison_period VARCHAR(20) DEFAULT NULL COMMENT '对比周期',
    comparison_change_rate DECIMAL(5,2) DEFAULT NULL COMMENT '同比/环比变化率(%)',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT 'Create Time',
    PRIMARY KEY (id),
    KEY idx_total_unit (energy_unit_id),
    KEY idx_total_type_date (report_type, stat_date)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='能耗汇总表';

-- 43. 节能量表 report_energy_saving
DROP TABLE IF EXISTS report_energy_saving;
CREATE TABLE report_energy_saving (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT 'Primary Key ID',
    energy_unit_id BIGINT NOT NULL COMMENT '用能单元ID',
    report_type VARCHAR(20) NOT NULL COMMENT '报表类型',
    stat_date DATE NOT NULL COMMENT '统计日期',
    baseline_consumption DECIMAL(18,4) DEFAULT 0 COMMENT '基准消耗量',
    actual_consumption DECIMAL(18,4) DEFAULT 0 COMMENT '实际消耗量',
    saving_amount DECIMAL(18,4) DEFAULT 0 COMMENT '节能量',
    saving_rate DECIMAL(5,2) DEFAULT 0 COMMENT '节能率(%)',
    saving_amount_unit VARCHAR(20) DEFAULT NULL COMMENT '节能量单位',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT 'Create Time',
    PRIMARY KEY (id),
    KEY idx_saving_unit (energy_unit_id),
    KEY idx_saving_type_date (report_type, stat_date)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='节能量表';

-- 44. 用能成本表 report_cost_stat
DROP TABLE IF EXISTS report_cost_stat;
CREATE TABLE report_cost_stat (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT 'Primary Key ID',
    energy_unit_id BIGINT NOT NULL COMMENT '用能单元ID',
    report_type VARCHAR(20) NOT NULL COMMENT '报表类型',
    stat_date DATE NOT NULL COMMENT '统计日期',
    energy_type_id BIGINT DEFAULT NULL COMMENT 'Energy TypeID',
    cost DECIMAL(18,2) DEFAULT 0 COMMENT '费用',
    cost_unit VARCHAR(20) DEFAULT NULL COMMENT '费用单位',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT 'Create Time',
    PRIMARY KEY (id),
    KEY idx_cost_unit (energy_unit_id),
    KEY idx_cost_type_date (report_type, stat_date)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用能成本表';

-- 45. 基准能耗表 baseline_consumption
DROP TABLE IF EXISTS baseline_consumption;
CREATE TABLE baseline_consumption (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT 'Primary Key ID',
    energy_unit_id BIGINT NOT NULL COMMENT '用能单元ID',
    start_date DATE NOT NULL COMMENT '开始日期',
    end_date DATE NOT NULL COMMENT '结束日期',
    baseline_value DECIMAL(18,4) NOT NULL DEFAULT 0 COMMENT '基准值',
    baseline_type VARCHAR(50) DEFAULT NULL COMMENT '基准类型',
    remark VARCHAR(500) DEFAULT NULL COMMENT 'Remark',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT 'Create Time',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'Update Time',
    create_by BIGINT DEFAULT NULL COMMENT '创建人',
    update_by BIGINT DEFAULT NULL COMMENT '更新人',
    deleted TINYINT DEFAULT 0 COMMENT 'Delete Flag（0代表Exists 1代表Deleted）',
    PRIMARY KEY (id),
    KEY idx_baseline_unit (energy_unit_id),
    KEY idx_baseline_date (start_date, end_date)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='基准能耗表';

-- 46. 指标录入表 entry_indicator
DROP TABLE IF EXISTS entry_indicator;
CREATE TABLE entry_indicator (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT 'Primary Key ID',
    energy_unit_id BIGINT NOT NULL COMMENT '用能单元ID',
    indicator_name VARCHAR(100) NOT NULL COMMENT '指标名称',
    indicator_type VARCHAR(50) DEFAULT NULL COMMENT '指标类型',
    indicator_value DECIMAL(18,4) DEFAULT 0 COMMENT '指标值',
    indicator_unit VARCHAR(20) DEFAULT NULL COMMENT '指标单位',
    entry_date DATE NOT NULL COMMENT '录入日期',
    entry_person VARCHAR(50) DEFAULT NULL COMMENT '录入人',
    remark VARCHAR(500) DEFAULT NULL COMMENT 'Remark',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT 'Create Time',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'Update Time',
    create_by BIGINT DEFAULT NULL COMMENT '创建人',
    update_by BIGINT DEFAULT NULL COMMENT '更新人',
    deleted TINYINT DEFAULT 0 COMMENT 'Delete Flag（0代表Exists 1代表Deleted）',
    PRIMARY KEY (id),
    KEY idx_indicator_unit (energy_unit_id),
    KEY idx_indicator_date (entry_date)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='指标录入表';

-- 47. 产量录入表 entry_production
DROP TABLE IF EXISTS entry_production;
CREATE TABLE entry_production (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT 'Primary Key ID',
    energy_unit_id BIGINT NOT NULL COMMENT '用能单元ID',
    product_type_id BIGINT DEFAULT NULL COMMENT '产品种类ID',
    production_date DATE NOT NULL COMMENT '生产日期',
    output_quantity DECIMAL(18,4) DEFAULT 0 COMMENT '产量',
    output_unit VARCHAR(20) DEFAULT NULL COMMENT '产量单位',
    batch_number VARCHAR(50) DEFAULT NULL COMMENT '批次号',
    remark VARCHAR(500) DEFAULT NULL COMMENT 'Remark',
    entry_person VARCHAR(50) DEFAULT NULL COMMENT '录入人',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT 'Create Time',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'Update Time',
    create_by BIGINT DEFAULT NULL COMMENT '创建人',
    update_by BIGINT DEFAULT NULL COMMENT '更新人',
    deleted TINYINT DEFAULT 0 COMMENT 'Delete Flag（0代表Exists 1代表Deleted）',
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
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT 'Primary Key ID',
    service_name VARCHAR(200) NOT NULL COMMENT '服务名称',
    service_type VARCHAR(50) NOT NULL COMMENT '服务类型',
    applicant VARCHAR(100) NOT NULL COMMENT '申请人',
    contact VARCHAR(100) DEFAULT NULL COMMENT '联系方式',
    description TEXT DEFAULT NULL COMMENT '服务描述',
    expected_date DATE DEFAULT NULL COMMENT '期望日期',
    status VARCHAR(20) NOT NULL DEFAULT 'PENDING' COMMENT 'Status(PENDING/APPROVED/PROCESSING/COMPLETED/REJECTED)',
    result TEXT DEFAULT NULL COMMENT '服务结果',
    remark VARCHAR(500) DEFAULT NULL COMMENT 'Remark',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT 'Create Time',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'Update Time',
    create_by BIGINT DEFAULT NULL COMMENT '创建人',
    update_by BIGINT DEFAULT NULL COMMENT '更新人',
    deleted TINYINT DEFAULT 0 COMMENT 'Delete Flag（0代表Exists 1代表Deleted）',
    PRIMARY KEY (id),
    KEY idx_service_status (status),
    KEY idx_applicant (applicant),
    KEY idx_expected_date (expected_date)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='AI服务预约记录表';

-- 49. AI知识库文档表 ai_knowledge_doc
DROP TABLE IF EXISTS ai_knowledge_doc;
CREATE TABLE ai_knowledge_doc (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT 'Primary Key ID',
    doc_name VARCHAR(200) NOT NULL COMMENT '文档名称',
    doc_type VARCHAR(50) NOT NULL COMMENT '文档类型',
    file_path VARCHAR(500) DEFAULT NULL COMMENT '文件Path',
    file_size BIGINT DEFAULT NULL COMMENT '文件大小(字节)',
    summary TEXT DEFAULT NULL COMMENT '文档摘要',
    upload_user VARCHAR(100) DEFAULT NULL COMMENT '上传用户',
    upload_time DATETIME DEFAULT NULL COMMENT '上传时间',
    status TINYINT DEFAULT 1 COMMENT 'Status（1Normal 0Disabled）',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT 'Create Time',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'Update Time',
    deleted TINYINT DEFAULT 0 COMMENT 'Delete Flag（0代表Exists 1代表Deleted）',
    PRIMARY KEY (id),
    KEY idx_doc_type (doc_type),
    KEY idx_upload_user (upload_user)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='AI知识库文档表';

-- 50. AI报告生成任务表 ai_report_task
DROP TABLE IF EXISTS ai_report_task;
CREATE TABLE ai_report_task (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT 'Primary Key ID',
    report_name VARCHAR(200) NOT NULL COMMENT '报告名称',
    report_type VARCHAR(50) NOT NULL COMMENT '报告类型(GREEN_FACTORY/CARBON_FOOTPRINT/VERIFICATION/ENERGY_AUDIT)',
    template_id BIGINT DEFAULT NULL COMMENT '模板ID',
    params_json TEXT DEFAULT NULL COMMENT '参数JSON',
    status VARCHAR(20) NOT NULL DEFAULT 'PENDING' COMMENT 'Status(PENDING/GENERATING/COMPLETED/FAILED)',
    result_path VARCHAR(500) DEFAULT NULL COMMENT '结果文件Path',
    error_msg VARCHAR(1000) DEFAULT NULL COMMENT '错误信息',
    progress INT DEFAULT 0 COMMENT '生成进度(%)',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT 'Create Time',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'Update Time',
    create_by BIGINT DEFAULT NULL COMMENT '创建人',
    deleted TINYINT DEFAULT 0 COMMENT 'Delete Flag（0代表Exists 1代表Deleted）',
    PRIMARY KEY (id),
    KEY idx_report_type (report_type),
    KEY idx_report_status (status),
    KEY idx_template_id (template_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='AI报告生成任务表';

-- 51. AI报告模板表 ai_report_template
DROP TABLE IF EXISTS ai_report_template;
CREATE TABLE ai_report_template (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT 'Primary Key ID',
    template_name VARCHAR(200) NOT NULL COMMENT '模板名称',
    report_type VARCHAR(50) NOT NULL COMMENT '报告类型',
    template_content TEXT DEFAULT NULL COMMENT '模板内容',
    variables VARCHAR(1000) DEFAULT NULL COMMENT '变量列表(JSON)',
    status TINYINT DEFAULT 1 COMMENT 'Status（1Normal 0Disabled）',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT 'Create Time',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'Update Time',
    deleted TINYINT DEFAULT 0 COMMENT 'Delete Flag（0代表Exists 1代表Deleted）',
    PRIMARY KEY (id),
    KEY idx_template_report_type (report_type)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='AI报告模板表';

-- 52. 绿色工厂申报表 toolkit_green_factory
DROP TABLE IF EXISTS toolkit_green_factory;
CREATE TABLE toolkit_green_factory (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT 'Primary Key ID',
    declaration_name VARCHAR(200) NOT NULL COMMENT '申报名称',
    enterprise_name VARCHAR(200) NOT NULL COMMENT '企业名称',
    declaration_year INT NOT NULL COMMENT '申报年度',
    status VARCHAR(20) NOT NULL DEFAULT 'DRAFT' COMMENT 'Status(DRAFT/SUBMITTED/APPROVED/REJECTED)',
    progress INT DEFAULT 0 COMMENT '进度(%)',
    submit_time DATETIME DEFAULT NULL COMMENT '提交时间',
    remark VARCHAR(500) DEFAULT NULL COMMENT 'Remark',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT 'Create Time',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'Update Time',
    create_by BIGINT DEFAULT NULL COMMENT '创建人',
    deleted TINYINT DEFAULT 0 COMMENT 'Delete Flag（0代表Exists 1代表Deleted）',
    PRIMARY KEY (id),
    KEY idx_factory_status (status),
    KEY idx_factory_year (declaration_year)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='绿色工厂申报表';

-- 53. 申报资料清单表 toolkit_green_factory_item
DROP TABLE IF EXISTS toolkit_green_factory_item;
CREATE TABLE toolkit_green_factory_item (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT 'Primary Key ID',
    declaration_id BIGINT NOT NULL COMMENT '申报ID',
    item_name VARCHAR(200) NOT NULL COMMENT '资料名称',
    item_category VARCHAR(50) DEFAULT NULL COMMENT '资料类别',
    required TINYINT DEFAULT 1 COMMENT '是否必填（1是 0否）',
    uploaded TINYINT DEFAULT 0 COMMENT '是否已上传（1是 0否）',
    file_path VARCHAR(500) DEFAULT NULL COMMENT '文件Path',
    upload_time DATETIME DEFAULT NULL COMMENT '上传时间',
    remark VARCHAR(500) DEFAULT NULL COMMENT 'Remark',
    PRIMARY KEY (id),
    KEY idx_item_declaration (declaration_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='申报资料清单表';

-- 54. 错峰排产表 toolkit_peak_shifting
DROP TABLE IF EXISTS toolkit_peak_shifting;
CREATE TABLE toolkit_peak_shifting (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT 'Primary Key ID',
    plan_name VARCHAR(200) NOT NULL COMMENT '计划名称',
    plan_date DATE NOT NULL COMMENT '计划日期',
    shift_type VARCHAR(20) DEFAULT NULL COMMENT '班次类型',
    production_line VARCHAR(100) DEFAULT NULL COMMENT '产线名称',
    target_output DECIMAL(18,4) DEFAULT NULL COMMENT '目标产量',
    actual_output DECIMAL(18,4) DEFAULT NULL COMMENT '实际产量',
    energy_consumption DECIMAL(18,4) DEFAULT NULL COMMENT '能耗(kWh)',
    energy_cost DECIMAL(18,2) DEFAULT NULL COMMENT '能源成本(元)',
    saving_rate DECIMAL(5,2) DEFAULT NULL COMMENT '节能率(%)',
    status TINYINT DEFAULT 1 COMMENT 'Status（1Normal 0Disabled）',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT 'Create Time',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'Update Time',
    deleted TINYINT DEFAULT 0 COMMENT 'Delete Flag（0代表Exists 1代表Deleted）',
    PRIMARY KEY (id),
    KEY idx_plan_date (plan_date),
    KEY idx_production_line (production_line)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='错峰排产表';

-- 55. 排产配置表 toolkit_peak_shifting_config
DROP TABLE IF EXISTS toolkit_peak_shifting_config;
CREATE TABLE toolkit_peak_shifting_config (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT 'Primary Key ID',
    config_name VARCHAR(200) NOT NULL COMMENT '配置名称',
    peak_hours_start TIME DEFAULT NULL COMMENT '峰时段开始',
    peak_hours_end TIME DEFAULT NULL COMMENT '峰时段结束',
    valley_hours_start TIME DEFAULT NULL COMMENT '谷时段开始',
    valley_hours_end TIME DEFAULT NULL COMMENT '谷时段结束',
    flat_hours_start TIME DEFAULT NULL COMMENT '平时段开始',
    flat_hours_end TIME DEFAULT NULL COMMENT '平时段结束',
    max_power DECIMAL(18,4) DEFAULT NULL COMMENT '最大Power(kW)',
    priority INT DEFAULT 0 COMMENT '优先级',
    status TINYINT DEFAULT 1 COMMENT 'Status（1Normal 0Disabled）',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT 'Create Time',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'Update Time',
    deleted TINYINT DEFAULT 0 COMMENT 'Delete Flag（0代表Exists 1代表Deleted）',
    PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='排产配置表';

-- 56. 计算器记录表 toolkit_calculator_record
DROP TABLE IF EXISTS toolkit_calculator_record;
CREATE TABLE toolkit_calculator_record (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT 'Primary Key ID',
    calc_name VARCHAR(200) NOT NULL COMMENT '计算名称',
    calc_type VARCHAR(20) NOT NULL COMMENT '计算类型(CARBON/ELECTRICITY/COST)',
    input_params_json TEXT DEFAULT NULL COMMENT '输入参数JSON',
    result_json TEXT DEFAULT NULL COMMENT '计算结果JSON',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT 'Create Time',
    create_by BIGINT DEFAULT NULL COMMENT '创建人',
    deleted TINYINT DEFAULT 0 COMMENT 'Delete Flag（0代表Exists 1代表Deleted）',
    PRIMARY KEY (id),
    KEY idx_calc_type (calc_type)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='计算器记录表';

-- 57. 分时电价配置表 toolkit_electricity_price
DROP TABLE IF EXISTS toolkit_electricity_price;
CREATE TABLE toolkit_electricity_price (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT 'Primary Key ID',
    price_name VARCHAR(200) NOT NULL COMMENT '电价名称',
    effective_date DATE NOT NULL COMMENT '生效日期',
    peak_price DECIMAL(18,4) NOT NULL DEFAULT 0 COMMENT '峰段电价(元/kWh)',
    flat_price DECIMAL(18,4) NOT NULL DEFAULT 0 COMMENT '平段电价(元/kWh)',
    valley_price DECIMAL(18,4) NOT NULL DEFAULT 0 COMMENT '谷段电价(元/kWh)',
    peak_hours VARCHAR(100) DEFAULT NULL COMMENT '峰时段(如08:00-11:00,18:00-21:00)',
    flat_hours VARCHAR(100) DEFAULT NULL COMMENT '平时段(如07:00-08:00,11:00-18:00)',
    valley_hours VARCHAR(100) DEFAULT NULL COMMENT '谷时段(如21:00-07:00)',
    status TINYINT DEFAULT 1 COMMENT 'Status（1Normal 0Disabled）',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT 'Create Time',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'Update Time',
    deleted TINYINT DEFAULT 0 COMMENT 'Delete Flag（0代表Exists 1代表Deleted）',
    PRIMARY KEY (id),
    KEY idx_effective_date (effective_date)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='分时电价配置表';
