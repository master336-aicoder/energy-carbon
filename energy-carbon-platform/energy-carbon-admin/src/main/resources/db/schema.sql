-- ============================================================
-- 能碳管理平台数据库初始化脚本 - 建表语句
-- 数据库: energy_carbon
-- 技术栈: MySQL 8.0+
-- ============================================================

-- 使用数据库
USE energy_carbon;

-- ============================================================
-- 1. 部门表 sys_dept
-- ============================================================
DROP TABLE IF EXISTS sys_dept;
CREATE TABLE sys_dept (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT '部门ID',
    dept_name VARCHAR(50) NOT NULL COMMENT '部门名称',
    parent_id BIGINT DEFAULT 0 COMMENT '父部门ID',
    order_num INT DEFAULT 0 COMMENT '显示排序',
    leader VARCHAR(50) DEFAULT NULL COMMENT '负责人',
    phone VARCHAR(11) DEFAULT NULL COMMENT '联系电话',
    email VARCHAR(50) DEFAULT NULL COMMENT '邮箱',
    status TINYINT DEFAULT 1 COMMENT '部门状态（1正常 0停用）',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted TINYINT DEFAULT 0 COMMENT '删除标志（0代表存在 1代表删除）',
    PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='部门表';

-- ============================================================
-- 2. 用户表 sys_user
-- ============================================================
DROP TABLE IF EXISTS sys_user;
CREATE TABLE sys_user (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT '用户ID',
    username VARCHAR(50) NOT NULL COMMENT '用户名',
    password VARCHAR(100) NOT NULL COMMENT '密码',
    nickname VARCHAR(50) DEFAULT NULL COMMENT '昵称',
    phone VARCHAR(11) DEFAULT NULL COMMENT '手机号',
    email VARCHAR(50) DEFAULT NULL COMMENT '邮箱',
    avatar VARCHAR(200) DEFAULT NULL COMMENT '头像地址',
    dept_id BIGINT DEFAULT NULL COMMENT '部门ID',
    status TINYINT DEFAULT 1 COMMENT '帐号状态（1正常 0停用）',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    create_by BIGINT DEFAULT NULL COMMENT '创建者',
    update_by BIGINT DEFAULT NULL COMMENT '更新者',
    deleted TINYINT DEFAULT 0 COMMENT '删除标志（0代表存在 1代表删除）',
    PRIMARY KEY (id),
    UNIQUE KEY uk_username (username)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';

-- ============================================================
-- 3. 角色表 sys_role
-- ============================================================
DROP TABLE IF EXISTS sys_role;
CREATE TABLE sys_role (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT '角色ID',
    role_name VARCHAR(50) NOT NULL COMMENT '角色名称',
    role_key VARCHAR(100) NOT NULL COMMENT '角色权限字符串',
    sort INT DEFAULT 0 COMMENT '显示顺序',
    status TINYINT DEFAULT 1 COMMENT '角色状态（1正常 0停用）',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    create_by BIGINT DEFAULT NULL COMMENT '创建者',
    update_by BIGINT DEFAULT NULL COMMENT '更新者',
    deleted TINYINT DEFAULT 0 COMMENT '删除标志（0代表存在 1代表删除）',
    PRIMARY KEY (id),
    UNIQUE KEY uk_role_key (role_key)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='角色表';

-- ============================================================
-- 4. 菜单表 sys_menu
-- ============================================================
DROP TABLE IF EXISTS sys_menu;
CREATE TABLE sys_menu (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT '菜单ID',
    menu_name VARCHAR(50) NOT NULL COMMENT '菜单名称',
    parent_id BIGINT DEFAULT 0 COMMENT '父菜单ID',
    order_num INT DEFAULT 0 COMMENT '显示顺序',
    path VARCHAR(200) DEFAULT NULL COMMENT '路由地址',
    component VARCHAR(255) DEFAULT NULL COMMENT '组件路径',
    icon VARCHAR(100) DEFAULT NULL COMMENT '菜单图标',
    menu_type CHAR(1) DEFAULT NULL COMMENT '菜单类型（M目录 C菜单 F按钮）',
    perms VARCHAR(100) DEFAULT NULL COMMENT '权限标识',
    status TINYINT DEFAULT 1 COMMENT '菜单状态（1正常 0停用）',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted TINYINT DEFAULT 0 COMMENT '删除标志（0代表存在 1代表删除）',
    PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='菜单表';

-- ============================================================
-- 5. 用户角色关联表 sys_user_role
-- ============================================================
DROP TABLE IF EXISTS sys_user_role;
CREATE TABLE sys_user_role (
    user_id BIGINT NOT NULL COMMENT '用户ID',
    role_id BIGINT NOT NULL COMMENT '角色ID',
    PRIMARY KEY (user_id, role_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户和角色关联表';

-- ============================================================
-- 6. 角色菜单关联表 sys_role_menu
-- ============================================================
DROP TABLE IF EXISTS sys_role_menu;
CREATE TABLE sys_role_menu (
    role_id BIGINT NOT NULL COMMENT '角色ID',
    menu_id BIGINT NOT NULL COMMENT '菜单ID',
    PRIMARY KEY (role_id, menu_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='角色和菜单关联表';

-- ============================================================
-- 7. 操作日志表 sys_oper_log
-- ============================================================
DROP TABLE IF EXISTS sys_oper_log;
CREATE TABLE sys_oper_log (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT '日志主键',
    title VARCHAR(100) DEFAULT NULL COMMENT '模块标题',
    method VARCHAR(200) DEFAULT NULL COMMENT '方法名称',
    request_method VARCHAR(10) DEFAULT NULL COMMENT '请求方式',
    oper_name VARCHAR(50) DEFAULT NULL COMMENT '操作人员',
    oper_url VARCHAR(500) DEFAULT NULL COMMENT '请求URL',
    oper_ip VARCHAR(50) DEFAULT NULL COMMENT '主机地址',
    oper_param TEXT COMMENT '请求参数',
    json_result TEXT COMMENT '返回参数',
    status TINYINT DEFAULT 1 COMMENT '操作状态（1正常 0异常）',
    error_msg TEXT COMMENT '错误消息',
    oper_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '操作时间',
    PRIMARY KEY (id),
    KEY idx_oper_time (oper_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='操作日志记录表';

-- ============================================================
-- 8. 字典类型表 sys_dict_type
-- ============================================================
DROP TABLE IF EXISTS sys_dict_type;
CREATE TABLE sys_dict_type (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT '字典主键',
    dict_name VARCHAR(100) DEFAULT NULL COMMENT '字典名称',
    dict_type VARCHAR(100) NOT NULL COMMENT '字典类型',
    status TINYINT DEFAULT 1 COMMENT '状态（1正常 0停用）',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted TINYINT DEFAULT 0 COMMENT '删除标志（0代表存在 1代表删除）',
    PRIMARY KEY (id),
    UNIQUE KEY uk_dict_type (dict_type)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='字典类型表';

-- ============================================================
-- 9. 字典数据表 sys_dict_data
-- ============================================================
DROP TABLE IF EXISTS sys_dict_data;
CREATE TABLE sys_dict_data (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT '字典编码',
    dict_sort INT DEFAULT 0 COMMENT '字典排序',
    dict_label VARCHAR(100) DEFAULT NULL COMMENT '字典标签',
    dict_value VARCHAR(100) DEFAULT NULL COMMENT '字典键值',
    dict_type VARCHAR(100) NOT NULL COMMENT '字典类型',
    css_class VARCHAR(100) DEFAULT NULL COMMENT '样式属性（其他样式扩展）',
    list_class VARCHAR(100) DEFAULT NULL COMMENT '表格回显样式',
    is_default TINYINT DEFAULT 0 COMMENT '是否默认（1是 0否）',
    status TINYINT DEFAULT 1 COMMENT '状态（1正常 0停用）',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted TINYINT DEFAULT 0 COMMENT '删除标志（0代表存在 1代表删除）',
    PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='字典数据表';

-- ============================================================
-- 10. 用能设备台账 energy_device
-- ============================================================
DROP TABLE IF EXISTS energy_device;
CREATE TABLE energy_device (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT '设备ID',
    device_code VARCHAR(50) NOT NULL COMMENT '设备编码',
    device_name VARCHAR(100) NOT NULL COMMENT '设备名称',
    device_type VARCHAR(50) DEFAULT NULL COMMENT '设备类型',
    dept_id BIGINT DEFAULT NULL COMMENT '所属部门ID',
    status TINYINT DEFAULT 1 COMMENT '状态（1正常 0停用）',
    manufacturer VARCHAR(100) DEFAULT NULL COMMENT '制造商',
    model VARCHAR(100) DEFAULT NULL COMMENT '型号',
    install_date DATE DEFAULT NULL COMMENT '安装日期',
    power_rating DECIMAL(10,2) DEFAULT NULL COMMENT '额定功率(kW)',
    voltage_level VARCHAR(20) DEFAULT NULL COMMENT '电压等级',
    remark VARCHAR(500) DEFAULT NULL COMMENT '备注',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    create_by BIGINT DEFAULT NULL COMMENT '创建者',
    update_by BIGINT DEFAULT NULL COMMENT '更新者',
    deleted TINYINT DEFAULT 0 COMMENT '删除标志（0代表存在 1代表删除）',
    PRIMARY KEY (id),
    UNIQUE KEY uk_device_code (device_code)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用能设备台账';

-- ============================================================
-- 11. 计量器具台账 energy_meter
-- ============================================================
DROP TABLE IF EXISTS energy_meter;
CREATE TABLE energy_meter (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT '计量器具ID',
    meter_code VARCHAR(50) NOT NULL COMMENT '计量器具编码',
    meter_name VARCHAR(100) NOT NULL COMMENT '计量器具名称',
    meter_type VARCHAR(50) DEFAULT NULL COMMENT '计量器具类型',
    device_id BIGINT DEFAULT NULL COMMENT '关联设备ID',
    dept_id BIGINT DEFAULT NULL COMMENT '所属部门ID',
    status TINYINT DEFAULT 1 COMMENT '状态（1正常 0停用）',
    accuracy_class VARCHAR(20) DEFAULT NULL COMMENT '准确度等级',
    range_min DECIMAL(10,2) DEFAULT NULL COMMENT '量程下限',
    range_max DECIMAL(10,2) DEFAULT NULL COMMENT '量程上限',
    install_date DATE DEFAULT NULL COMMENT '安装日期',
    calibration_date DATE DEFAULT NULL COMMENT '检定日期',
    next_calibration_date DATE DEFAULT NULL COMMENT '下次检定日期',
    remark VARCHAR(500) DEFAULT NULL COMMENT '备注',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    create_by BIGINT DEFAULT NULL COMMENT '创建者',
    update_by BIGINT DEFAULT NULL COMMENT '更新者',
    deleted TINYINT DEFAULT 0 COMMENT '删除标志（0代表存在 1代表删除）',
    PRIMARY KEY (id),
    UNIQUE KEY uk_meter_code (meter_code)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='计量器具台账';

-- ============================================================
-- 12. 能源品种 energy_type
-- ============================================================
DROP TABLE IF EXISTS energy_type;
CREATE TABLE energy_type (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT '能源品种ID',
    type_code VARCHAR(50) NOT NULL COMMENT '能源编码',
    type_name VARCHAR(100) NOT NULL COMMENT '能源名称',
    type_category VARCHAR(20) NOT NULL COMMENT '能源类别（电/水/气/热/煤/油/新能源）',
    unit VARCHAR(20) DEFAULT NULL COMMENT '计量单位',
    conversion_coefficient DECIMAL(10,6) DEFAULT NULL COMMENT '折标系数',
    carbon_factor DECIMAL(10,6) DEFAULT NULL COMMENT '碳排放因子',
    status TINYINT DEFAULT 1 COMMENT '状态（1正常 0停用）',
    remark VARCHAR(500) DEFAULT NULL COMMENT '备注',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted TINYINT DEFAULT 0 COMMENT '删除标志（0代表存在 1代表删除）',
    PRIMARY KEY (id),
    UNIQUE KEY uk_type_code (type_code)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='能源品种';

-- ============================================================
-- 13. 能源品种模板 energy_type_template
-- ============================================================
DROP TABLE IF EXISTS energy_type_template;
CREATE TABLE energy_type_template (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT '模板ID',
    template_name VARCHAR(100) NOT NULL COMMENT '模板名称',
    template_code VARCHAR(50) NOT NULL COMMENT '模板编码',
    type_ids VARCHAR(500) DEFAULT NULL COMMENT '关联能源品种ID集合，逗号分隔',
    status TINYINT DEFAULT 1 COMMENT '状态（1正常 0停用）',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted TINYINT DEFAULT 0 COMMENT '删除标志（0代表存在 1代表删除）',
    PRIMARY KEY (id),
    UNIQUE KEY uk_template_code (template_code)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='能源品种模板';

-- ============================================================
-- 14. 班组管理 work_shift
-- ============================================================
DROP TABLE IF EXISTS work_shift;
CREATE TABLE work_shift (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT '班组ID',
    shift_name VARCHAR(100) NOT NULL COMMENT '班组名称',
    shift_code VARCHAR(50) NOT NULL COMMENT '班组编码',
    start_time TIME DEFAULT NULL COMMENT '上班时间',
    end_time TIME DEFAULT NULL COMMENT '下班时间',
    rest_start TIME DEFAULT NULL COMMENT '休息开始时间',
    rest_end TIME DEFAULT NULL COMMENT '休息结束时间',
    work_days VARCHAR(50) DEFAULT NULL COMMENT '工作日期（1,2,3,4,5,6,7）',
    status TINYINT DEFAULT 1 COMMENT '状态（1正常 0停用）',
    remark VARCHAR(500) DEFAULT NULL COMMENT '备注',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted TINYINT DEFAULT 0 COMMENT '删除标志（0代表存在 1代表删除）',
    PRIMARY KEY (id),
    UNIQUE KEY uk_shift_code (shift_code)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='班组管理';

-- ============================================================
-- 15. 产品种类 product_type
-- ============================================================
DROP TABLE IF EXISTS product_type;
CREATE TABLE product_type (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT '产品种类ID',
    product_code VARCHAR(50) NOT NULL COMMENT '产品编码',
    product_name VARCHAR(100) NOT NULL COMMENT '产品名称',
    product_category VARCHAR(50) DEFAULT NULL COMMENT '产品类别',
    unit VARCHAR(20) DEFAULT NULL COMMENT '计量单位',
    carbon_factor DECIMAL(10,6) DEFAULT NULL COMMENT '碳排放因子',
    status TINYINT DEFAULT 1 COMMENT '状态（1正常 0停用）',
    remark VARCHAR(500) DEFAULT NULL COMMENT '备注',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted TINYINT DEFAULT 0 COMMENT '删除标志（0代表存在 1代表删除）',
    PRIMARY KEY (id),
    UNIQUE KEY uk_product_code (product_code)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='产品种类';

-- ============================================================
-- 16. 物联网网关 iot_gateway
-- ============================================================
DROP TABLE IF EXISTS iot_gateway;
CREATE TABLE iot_gateway (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT '网关ID',
    gateway_code VARCHAR(50) NOT NULL COMMENT '网关编码',
    gateway_name VARCHAR(100) NOT NULL COMMENT '网关名称',
    protocol_type VARCHAR(20) DEFAULT NULL COMMENT '协议类型（MQTT/HTTP/Modbus/TCP）',
    host VARCHAR(100) DEFAULT NULL COMMENT '主机地址',
    port INT DEFAULT NULL COMMENT '端口',
    username VARCHAR(100) DEFAULT NULL COMMENT '用户名',
    password VARCHAR(100) DEFAULT NULL COMMENT '密码',
    status TINYINT DEFAULT 1 COMMENT '状态（1正常 0停用）',
    last_online_time DATETIME DEFAULT NULL COMMENT '最后在线时间',
    remark VARCHAR(500) DEFAULT NULL COMMENT '备注',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted TINYINT DEFAULT 0 COMMENT '删除标志（0代表存在 1代表删除）',
    PRIMARY KEY (id),
    UNIQUE KEY uk_gateway_code (gateway_code)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='物联网网关';

-- ============================================================
-- 17. 物联网设备 iot_device
-- ============================================================
DROP TABLE IF EXISTS iot_device;
CREATE TABLE iot_device (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT '物联网设备ID',
    device_code VARCHAR(50) NOT NULL COMMENT '设备编码',
    device_name VARCHAR(100) NOT NULL COMMENT '设备名称',
    gateway_id BIGINT DEFAULT NULL COMMENT '所属网关ID',
    meter_id BIGINT DEFAULT NULL COMMENT '关联计量器具ID',
    topic VARCHAR(200) DEFAULT NULL COMMENT 'MQTT主题',
    data_format VARCHAR(50) DEFAULT NULL COMMENT '数据格式（JSON/CSV/XML）',
    status TINYINT DEFAULT 1 COMMENT '状态（1正常 0停用）',
    last_online_time DATETIME DEFAULT NULL COMMENT '最后在线时间',
    remark VARCHAR(500) DEFAULT NULL COMMENT '备注',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted TINYINT DEFAULT 0 COMMENT '删除标志（0代表存在 1代表删除）',
    PRIMARY KEY (id),
    UNIQUE KEY uk_iot_device_code (device_code)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='物联网设备';
