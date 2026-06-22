-- 处理现有文件末尾，追加 ============================================================
-- 18. 能耗数据汇总 analysis_energy_consumption
-- ============================================================
DROP TABLE IF EXISTS analysis_energy_consumption;
CREATE TABLE analysis_energy_consumption (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    energy_unit_id BIGINT NOT NULL COMMENT '用能单元ID（部门ID）',
    meter_id BIGINT DEFAULT NULL COMMENT '计量器具ID',
    energy_type_id BIGINT DEFAULT NULL COMMENT '能源品种ID',
    stat_date DATE NOT NULL COMMENT '统计日期',
    stat_period VARCHAR(10) NOT NULL COMMENT '统计周期(DAY/MONTH/YEAR)',
    consumption DECIMAL(18,4) NOT NULL DEFAULT 0 COMMENT '消耗量',
    unit VARCHAR(20) DEFAULT NULL COMMENT '计量单位',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (id),
    KEY idx_unit_date (energy_unit_id, stat_date),
    KEY idx_period (stat_period),
    KEY idx_meter (meter_id),
    KEY idx_energy_type (energy_type_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='能耗数据汇总';

-- ============================================================
-- 19. 电费数据 analysis_electricity_cost
-- ============================================================
DROP TABLE IF EXISTS analysis_electricity_cost;
CREATE TABLE analysis_electricity_cost (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    meter_id BIGINT DEFAULT NULL COMMENT '计量器具ID',
    stat_date DATE NOT NULL COMMENT '统计日期',
    peak_power DECIMAL(18,4) DEFAULT 0 COMMENT '峰段用电量(kWh)',
    valley_power DECIMAL(18,4) DEFAULT 0 COMMENT '谷段用电量(kWh)',
    flat_power DECIMAL(18,4) DEFAULT 0 COMMENT '平段用电量(kWh)',
    peak_cost DECIMAL(18,2) DEFAULT 0 COMMENT '峰段电费(元)',
    valley_cost DECIMAL(18,2) DEFAULT 0 COMMENT '谷段电费(元)',
    flat_cost DECIMAL(18,2) DEFAULT 0 COMMENT '平段电费(元)',
    total_cost DECIMAL(18,2) DEFAULT 0 COMMENT '总电费(元)',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (id),
    KEY idx_meter_date (meter_id, stat_date),
    KEY idx_stat_date (stat_date)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='电费数据';

-- ============================================================
-- 20. 能效数据 analysis_energy_efficiency
-- ============================================================
DROP TABLE IF EXISTS analysis_energy_efficiency;
CREATE TABLE analysis_energy_efficiency (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    energy_unit_id BIGINT NOT NULL COMMENT '用能单元ID（部门ID）',
    stat_date DATE NOT NULL COMMENT '统计日期',
    output_value DECIMAL(18,4) DEFAULT 0 COMMENT '产出值',
    output_unit VARCHAR(20) DEFAULT NULL COMMENT '产出单位',
    energy_consumption DECIMAL(18,4) DEFAULT 0 COMMENT '能源消耗量(kgce)',
    energy_unit VARCHAR(20) DEFAULT NULL COMMENT '能源消耗单位',
    intensity DECIMAL(18,6) DEFAULT 0 COMMENT '单耗(强度)',
    benchmark_value DECIMAL(18,6) DEFAULT NULL COMMENT '标杆值',
    benchmark_unit VARCHAR(20) DEFAULT NULL COMMENT '标杆单位',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (id),
    KEY idx_unit_date (energy_unit_id, stat_date),
    KEY idx_stat_date (stat_date)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='能效数据';

-- ============================================================
-- 21. 能碳预算 analysis_energy_budget
-- ============================================================
DROP TABLE IF EXISTS analysis_energy_budget;
CREATE TABLE analysis_energy_budget (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    energy_unit_id BIGINT NOT NULL COMMENT '用能单元ID（部门ID）',
    budget_year INT NOT NULL COMMENT '预算年度',
    budget_type VARCHAR(10) NOT NULL COMMENT '预算类型(ENERGY/CARBON)',
    target_value DECIMAL(18,4) NOT NULL DEFAULT 0 COMMENT '目标值',
    target_unit VARCHAR(20) DEFAULT NULL COMMENT '目标单位',
    current_value DECIMAL(18,4) DEFAULT 0 COMMENT '当前值',
    completion_rate DECIMAL(5,2) DEFAULT 0 COMMENT '完成率(%)',
    status TINYINT DEFAULT 1 COMMENT '状态（1正常 0停用）',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted TINYINT DEFAULT 0 COMMENT '删除标志（0代表存在 1代表删除）',
    PRIMARY KEY (id),
    KEY idx_unit_year (energy_unit_id, budget_year),
    KEY idx_budget_type (budget_type)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='能碳预算';

-- ============================================================
-- 22. 对标库 analysis_benchmark
-- ============================================================
DROP TABLE IF EXISTS analysis_benchmark;
CREATE TABLE analysis_benchmark (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    benchmark_name VARCHAR(200) NOT NULL COMMENT '对标名称',
    industry VARCHAR(100) DEFAULT NULL COMMENT '所属行业',
    energy_type_id BIGINT DEFAULT NULL COMMENT '能源品种ID',
    energy_category VARCHAR(50) DEFAULT NULL COMMENT '能源类别',
    benchmark_value DECIMAL(18,6) NOT NULL DEFAULT 0 COMMENT '标杆值',
    benchmark_unit VARCHAR(20) DEFAULT NULL COMMENT '标杆单位',
    source VARCHAR(200) DEFAULT NULL COMMENT '数据来源',
    effective_date DATE DEFAULT NULL COMMENT '生效日期',
    expire_date DATE DEFAULT NULL COMMENT '失效日期',
    remark VARCHAR(500) DEFAULT NULL COMMENT '备注',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted TINYINT DEFAULT 0 COMMENT '删除标志（0代表存在 1代表删除）',
    PRIMARY KEY (id),
    KEY idx_industry (industry),
    KEY idx_energy_type (energy_type_id),
    KEY idx_effective (effective_date, expire_date)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='对标库';

-- ============================================================
-- Week 17: 系统优化与测试 - 索引优化
-- ============================================================

-- sys_user 表索引优化
CREATE INDEX idx_username ON sys_user (username);
CREATE INDEX idx_dept_id ON sys_user (dept_id);
CREATE INDEX idx_status ON sys_user (status);

-- sys_role 表索引优化
CREATE INDEX idx_role_key ON sys_role (role_code);
CREATE INDEX idx_role_status ON sys_role (status);

-- sys_menu 表索引优化
CREATE INDEX idx_parent_id ON sys_menu (parent_id);
CREATE INDEX idx_menu_status ON sys_menu (status);

-- sys_dept 表索引优化
CREATE INDEX idx_dept_parent_id ON sys_dept (parent_id);
CREATE INDEX idx_dept_status ON sys_dept (status);

-- monitor_history_data 表索引优化
CREATE INDEX idx_meter_time ON monitor_history_data (meter_id, data_time);
CREATE INDEX idx_data_time ON monitor_history_data (data_time);

-- alarm_record 表索引优化
CREATE INDEX idx_alarm_time ON alarm_record (alarm_time);
CREATE INDEX idx_alarm_status ON alarm_record (handle_status);
CREATE INDEX idx_alarm_meter ON alarm_record (meter_id);

-- carbon_accounting 表索引优化
CREATE INDEX idx_year_period ON carbon_accounting (accounting_year, accounting_period);
CREATE INDEX idx_accounting_status ON carbon_accounting (status);

-- analysis_energy_consumption 表索引优化（已有 idx_unit_date，补充联合索引）
CREATE INDEX idx_unit_date_period ON analysis_energy_consumption (energy_unit_id, stat_date, stat_period);

-- report_energy_summary 表索引优化
CREATE INDEX idx_type_date ON report_energy_summary (report_type, stat_date);

-- 外键字段索引
CREATE INDEX idx_user_dept ON sys_user (dept_id);
CREATE INDEX idx_role_menu_role ON sys_role_menu (role_id);
CREATE INDEX idx_role_menu_menu ON sys_role_menu (menu_id);
CREATE INDEX idx_user_role_user ON sys_user_role (user_id);
CREATE INDEX idx_user_role_role ON sys_user_role (role_id);
CREATE INDEX idx_dict_data_type ON sys_dict_data (dict_type);
CREATE INDEX idx_dict_data_status ON sys_dict_data (status);
CREATE INDEX idx_alarm_threshold_unit ON alarm_threshold (energy_unit_id);
CREATE INDEX idx_carbon_accounting_unit ON carbon_accounting (energy_unit_id);
CREATE INDEX idx_carbon_detail_accounting ON carbon_accounting_detail (accounting_id);
CREATE INDEX idx_emission_factor_type ON carbon_emission_factor (energy_type_id);
CREATE INDEX idx_meter_device ON energy_meter (device_id);
CREATE INDEX idx_device_unit ON energy_device (energy_unit_id);
CREATE INDEX idx_workshift_unit ON work_shift (energy_unit_id);
CREATE INDEX idx_report_summary_unit ON report_energy_summary (energy_unit_id);
CREATE INDEX idx_baseline_unit ON baseline_consumption (energy_unit_id);
CREATE INDEX idx_entry_indicator_unit ON entry_indicator (energy_unit_id);
CREATE INDEX idx_entry_production_unit ON entry_production (energy_unit_id);
CREATE INDEX idx_cost_stat_unit ON report_cost_stat (energy_unit_id);
CREATE INDEX idx_energy_saving_unit ON report_energy_saving (energy_unit_id);
CREATE INDEX idx_energy_total_unit ON report_energy_total (energy_unit_id);

-- ============================================================
-- 23. 碳资产 carbon_asset
-- ============================================================
DROP TABLE IF EXISTS carbon_asset;
CREATE TABLE carbon_asset (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    asset_type VARCHAR(20) NOT NULL COMMENT '资产类型(QUOTA/CCER/GREEN_CERT)',
    asset_name VARCHAR(200) NOT NULL COMMENT '资产名称',
    asset_code VARCHAR(50) DEFAULT NULL COMMENT '资产编号',
    quantity DECIMAL(18,4) NOT NULL DEFAULT 0 COMMENT '数量',
    unit VARCHAR(20) DEFAULT NULL COMMENT '单位',
    acquire_date DATE DEFAULT NULL COMMENT '获取日期',
    expire_date DATE DEFAULT NULL COMMENT '到期日期',
    source VARCHAR(200) DEFAULT NULL COMMENT '来源',
    cost DECIMAL(18,2) DEFAULT 0 COMMENT '成本(元)',
    status VARCHAR(20) NOT NULL DEFAULT 'AVAILABLE' COMMENT '状态(AVAILABLE/USED/FROZEN/EXPIRED)',
    remark VARCHAR(500) DEFAULT NULL COMMENT '备注',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted TINYINT DEFAULT 0 COMMENT '删除标志（0代表存在 1代表删除）',
    PRIMARY KEY (id),
    KEY idx_asset_type (asset_type),
    KEY idx_status (status),
    KEY idx_acquire_date (acquire_date)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='碳资产';

-- ============================================================
-- 24. 碳资产交易 carbon_asset_trade
-- ============================================================
DROP TABLE IF EXISTS carbon_asset_trade;
CREATE TABLE carbon_asset_trade (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    asset_id BIGINT NOT NULL COMMENT '碳资产ID',
    trade_type VARCHAR(20) NOT NULL COMMENT '交易类型(BUY/SELL/TRANSFER)',
    trade_quantity DECIMAL(18,4) NOT NULL DEFAULT 0 COMMENT '交易数量',
    trade_price DECIMAL(18,2) NOT NULL DEFAULT 0 COMMENT '交易单价(元)',
    trade_amount DECIMAL(18,2) NOT NULL DEFAULT 0 COMMENT '交易金额(元)',
    trade_date DATE NOT NULL COMMENT '交易日期',
    counterparty VARCHAR(200) DEFAULT NULL COMMENT '交易对手',
    status VARCHAR(20) DEFAULT 'COMPLETED' COMMENT '状态',
    remark VARCHAR(500) DEFAULT NULL COMMENT '备注',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted TINYINT DEFAULT 0 COMMENT '删除标志（0代表存在 1代表删除）',
    PRIMARY KEY (id),
    KEY idx_asset_id (asset_id),
    KEY idx_trade_date (trade_date),
    KEY idx_trade_type (trade_type)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='碳资产交易';

-- ============================================================
-- 25. 市场供需信息 carbon_market_info
-- ============================================================
DROP TABLE IF EXISTS carbon_market_info;
CREATE TABLE carbon_market_info (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    info_title VARCHAR(200) NOT NULL COMMENT '信息标题',
    info_category VARCHAR(20) NOT NULL COMMENT '信息分类(POLICY/MARKET/PRICE/FORECAST)',
    content TEXT DEFAULT NULL COMMENT '内容',
    source VARCHAR(200) DEFAULT NULL COMMENT '来源',
    publish_date DATE DEFAULT NULL COMMENT '发布日期',
    status TINYINT DEFAULT 1 COMMENT '状态（1启用 0停用）',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted TINYINT DEFAULT 0 COMMENT '删除标志（0代表存在 1代表删除）',
    PRIMARY KEY (id),
    KEY idx_category (info_category),
    KEY idx_publish_date (publish_date)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='市场供需信息';

-- ============================================================
-- 26. CBAM申报 cbam_declaration
-- ============================================================
DROP TABLE IF EXISTS cbam_declaration;
CREATE TABLE cbam_declaration (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    declaration_name VARCHAR(200) NOT NULL COMMENT '申报名称',
    declaration_period VARCHAR(50) NOT NULL COMMENT '申报周期',
    product_name VARCHAR(200) NOT NULL COMMENT '产品名称',
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
    status VARCHAR(20) NOT NULL DEFAULT 'DRAFT' COMMENT '状态(DRAFT/SUBMITTED/APPROVED/REJECTED)',
    remark VARCHAR(500) DEFAULT NULL COMMENT '备注',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    create_by BIGINT DEFAULT NULL COMMENT '创建人',
    deleted TINYINT DEFAULT 0 COMMENT '删除标志（0代表存在 1代表删除）',
    PRIMARY KEY (id),
    KEY idx_period (declaration_period),
    KEY idx_status (status),
    KEY idx_cn_code (cn_code)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='CBAM申报';

-- ============================================================
-- 27. CBAM明细 cbam_declaration_detail
-- ============================================================
DROP TABLE IF EXISTS cbam_declaration_detail;
CREATE TABLE cbam_declaration_detail (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
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
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='CBAM明细';

-- ============================================================
-- 28. 供应链碳足迹 supply_chain_carbon
-- ============================================================
DROP TABLE IF EXISTS supply_chain_carbon;
CREATE TABLE supply_chain_carbon (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    supplier_name VARCHAR(200) NOT NULL COMMENT '供应商名称',
    supplier_code VARCHAR(50) DEFAULT NULL COMMENT '供应商编码',
    product_name VARCHAR(200) NOT NULL COMMENT '产品名称',
    product_category VARCHAR(50) DEFAULT NULL COMMENT '产品类别',
    carbon_footprint_value DECIMAL(18,4) DEFAULT 0 COMMENT '碳足迹值',
    unit VARCHAR(20) DEFAULT NULL COMMENT '单位',
    cert_type VARCHAR(50) DEFAULT NULL COMMENT '认证类型',
    cert_number VARCHAR(100) DEFAULT NULL COMMENT '认证编号',
    valid_date DATE DEFAULT NULL COMMENT '有效期',
    data_source VARCHAR(200) DEFAULT NULL COMMENT '数据来源',
    status TINYINT DEFAULT 1 COMMENT '状态（1正常 0停用）',
    remark VARCHAR(500) DEFAULT NULL COMMENT '备注',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted TINYINT DEFAULT 0 COMMENT '删除标志（0代表存在 1代表删除）',
    PRIMARY KEY (id),
    KEY idx_supplier (supplier_name),
    KEY idx_supplier_code (supplier_code),
    KEY idx_product (product_name)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='供应链碳足迹';

-- ============================================================
-- 29. AI服务预约记录 ai_service_record
-- ============================================================
DROP TABLE IF EXISTS ai_service_record;
CREATE TABLE ai_service_record (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    service_name VARCHAR(200) NOT NULL COMMENT '服务名称',
    service_type VARCHAR(50) NOT NULL COMMENT '服务类型',
    applicant VARCHAR(100) NOT NULL COMMENT '申请人',
    contact VARCHAR(100) DEFAULT NULL COMMENT '联系方式',
    description TEXT DEFAULT NULL COMMENT '服务描述',
    expected_date DATE DEFAULT NULL COMMENT '期望日期',
    status VARCHAR(20) NOT NULL DEFAULT 'PENDING' COMMENT '状态(PENDING/APPROVED/PROCESSING/COMPLETED/REJECTED)',
    result TEXT DEFAULT NULL COMMENT '服务结果',
    remark VARCHAR(500) DEFAULT NULL COMMENT '备注',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    create_by BIGINT DEFAULT NULL COMMENT '创建人',
    update_by BIGINT DEFAULT NULL COMMENT '更新人',
    deleted TINYINT DEFAULT 0 COMMENT '删除标志（0代表存在 1代表删除）',
    PRIMARY KEY (id),
    KEY idx_status (status),
    KEY idx_applicant (applicant),
    KEY idx_expected_date (expected_date)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='AI服务预约记录';

-- ============================================================
-- 30. AI知识库文档 ai_knowledge_doc
-- ============================================================
DROP TABLE IF EXISTS ai_knowledge_doc;
CREATE TABLE ai_knowledge_doc (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    doc_name VARCHAR(200) NOT NULL COMMENT '文档名称',
    doc_type VARCHAR(50) NOT NULL COMMENT '文档类型',
    file_path VARCHAR(500) DEFAULT NULL COMMENT '文件路径',
    file_size BIGINT DEFAULT NULL COMMENT '文件大小(字节)',
    summary TEXT DEFAULT NULL COMMENT '文档摘要',
    upload_user VARCHAR(100) DEFAULT NULL COMMENT '上传用户',
    upload_time DATETIME DEFAULT NULL COMMENT '上传时间',
    status TINYINT DEFAULT 1 COMMENT '状态（1正常 0停用）',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted TINYINT DEFAULT 0 COMMENT '删除标志（0代表存在 1代表删除）',
    PRIMARY KEY (id),
    KEY idx_doc_type (doc_type),
    KEY idx_upload_user (upload_user)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='AI知识库文档';

-- ============================================================
-- 31. AI报告生成任务 ai_report_task
-- ============================================================
DROP TABLE IF EXISTS ai_report_task;
CREATE TABLE ai_report_task (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    report_name VARCHAR(200) NOT NULL COMMENT '报告名称',
    report_type VARCHAR(50) NOT NULL COMMENT '报告类型(GREEN_FACTORY/CARBON_FOOTPRINT/VERIFICATION/ENERGY_AUDIT)',
    template_id BIGINT DEFAULT NULL COMMENT '模板ID',
    params_json TEXT DEFAULT NULL COMMENT '参数JSON',
    status VARCHAR(20) NOT NULL DEFAULT 'PENDING' COMMENT '状态(PENDING/GENERATING/COMPLETED/FAILED)',
    result_path VARCHAR(500) DEFAULT NULL COMMENT '结果文件路径',
    error_msg VARCHAR(1000) DEFAULT NULL COMMENT '错误信息',
    progress INT DEFAULT 0 COMMENT '生成进度(%)',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    create_by BIGINT DEFAULT NULL COMMENT '创建人',
    deleted TINYINT DEFAULT 0 COMMENT '删除标志（0代表存在 1代表删除）',
    PRIMARY KEY (id),
    KEY idx_report_type (report_type),
    KEY idx_status (status),
    KEY idx_template_id (template_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='AI报告生成任务';

-- ============================================================
-- 32. AI报告模板 ai_report_template
-- ============================================================
DROP TABLE IF EXISTS ai_report_template;
CREATE TABLE ai_report_template (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    template_name VARCHAR(200) NOT NULL COMMENT '模板名称',
    report_type VARCHAR(50) NOT NULL COMMENT '报告类型',
    template_content TEXT DEFAULT NULL COMMENT '模板内容',
    variables VARCHAR(1000) DEFAULT NULL COMMENT '变量列表(JSON)',
    status TINYINT DEFAULT 1 COMMENT '状态（1正常 0停用）',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted TINYINT DEFAULT 0 COMMENT '删除标志（0代表存在 1代表删除）',
    PRIMARY KEY (id),
    KEY idx_report_type (report_type)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='AI报告模板';

-- ============================================================
-- 33. 绿色工厂申报 toolkit_green_factory
-- ============================================================
DROP TABLE IF EXISTS toolkit_green_factory;
CREATE TABLE toolkit_green_factory (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    declaration_name VARCHAR(200) NOT NULL COMMENT '申报名称',
    enterprise_name VARCHAR(200) NOT NULL COMMENT '企业名称',
    declaration_year INT NOT NULL COMMENT '申报年度',
    status VARCHAR(20) NOT NULL DEFAULT 'DRAFT' COMMENT '状态(DRAFT/SUBMITTED/APPROVED/REJECTED)',
    progress INT DEFAULT 0 COMMENT '进度(%)',
    submit_time DATETIME DEFAULT NULL COMMENT '提交时间',
    remark VARCHAR(500) DEFAULT NULL COMMENT '备注',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    create_by BIGINT DEFAULT NULL COMMENT '创建人',
    deleted TINYINT DEFAULT 0 COMMENT '删除标志（0代表存在 1代表删除）',
    PRIMARY KEY (id),
    KEY idx_status (status),
    KEY idx_year (declaration_year)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='绿色工厂申报';

-- ============================================================
-- 34. 绿色工厂申报资料清单 toolkit_green_factory_item
-- ============================================================
DROP TABLE IF EXISTS toolkit_green_factory_item;
CREATE TABLE toolkit_green_factory_item (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    declaration_id BIGINT NOT NULL COMMENT '申报ID',
    item_name VARCHAR(200) NOT NULL COMMENT '资料名称',
    item_category VARCHAR(50) DEFAULT NULL COMMENT '资料类别',
    required TINYINT DEFAULT 1 COMMENT '是否必填（1是 0否）',
    uploaded TINYINT DEFAULT 0 COMMENT '是否已上传（1是 0否）',
    file_path VARCHAR(500) DEFAULT NULL COMMENT '文件路径',
    upload_time DATETIME DEFAULT NULL COMMENT '上传时间',
    remark VARCHAR(500) DEFAULT NULL COMMENT '备注',
    PRIMARY KEY (id),
    KEY idx_declaration_id (declaration_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='绿色工厂申报资料清单';

-- ============================================================
-- 35. 错峰排产 toolkit_peak_shifting
-- ============================================================
DROP TABLE IF EXISTS toolkit_peak_shifting;
CREATE TABLE toolkit_peak_shifting (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    plan_name VARCHAR(200) NOT NULL COMMENT '计划名称',
    plan_date DATE NOT NULL COMMENT '计划日期',
    shift_type VARCHAR(20) DEFAULT NULL COMMENT '班次类型',
    production_line VARCHAR(100) DEFAULT NULL COMMENT '产线名称',
    target_output DECIMAL(18,4) DEFAULT NULL COMMENT '目标产量',
    actual_output DECIMAL(18,4) DEFAULT NULL COMMENT '实际产量',
    energy_consumption DECIMAL(18,4) DEFAULT NULL COMMENT '能耗(kWh)',
    energy_cost DECIMAL(18,2) DEFAULT NULL COMMENT '能源成本(元)',
    saving_rate DECIMAL(5,2) DEFAULT NULL COMMENT '节能率(%)',
    status TINYINT DEFAULT 1 COMMENT '状态（1正常 0停用）',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted TINYINT DEFAULT 0 COMMENT '删除标志（0代表存在 1代表删除）',
    PRIMARY KEY (id),
    KEY idx_plan_date (plan_date),
    KEY idx_production_line (production_line)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='错峰排产';

-- ============================================================
-- 36. 排产配置 toolkit_peak_shifting_config
-- ============================================================
DROP TABLE IF EXISTS toolkit_peak_shifting_config;
CREATE TABLE toolkit_peak_shifting_config (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    config_name VARCHAR(200) NOT NULL COMMENT '配置名称',
    peak_hours_start TIME DEFAULT NULL COMMENT '峰时段开始',
    peak_hours_end TIME DEFAULT NULL COMMENT '峰时段结束',
    valley_hours_start TIME DEFAULT NULL COMMENT '谷时段开始',
    valley_hours_end TIME DEFAULT NULL COMMENT '谷时段结束',
    flat_hours_start TIME DEFAULT NULL COMMENT '平时段开始',
    flat_hours_end TIME DEFAULT NULL COMMENT '平时段结束',
    max_power DECIMAL(18,4) DEFAULT NULL COMMENT '最大功率(kW)',
    priority INT DEFAULT 0 COMMENT '优先级',
    status TINYINT DEFAULT 1 COMMENT '状态（1正常 0停用）',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted TINYINT DEFAULT 0 COMMENT '删除标志（0代表存在 1代表删除）',
    PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='排产配置';

-- ============================================================
-- 37. 计算器记录 toolkit_calculator_record
-- ============================================================
DROP TABLE IF EXISTS toolkit_calculator_record;
CREATE TABLE toolkit_calculator_record (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    calc_name VARCHAR(200) NOT NULL COMMENT '计算名称',
    calc_type VARCHAR(20) NOT NULL COMMENT '计算类型(CARBON/ELECTRICITY/COST)',
    input_params_json TEXT DEFAULT NULL COMMENT '输入参数JSON',
    result_json TEXT DEFAULT NULL COMMENT '计算结果JSON',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    create_by BIGINT DEFAULT NULL COMMENT '创建人',
    deleted TINYINT DEFAULT 0 COMMENT '删除标志（0代表存在 1代表删除）',
    PRIMARY KEY (id),
    KEY idx_calc_type (calc_type)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='计算器记录';

-- ============================================================
-- 38. 分时电价配置 toolkit_electricity_price
-- ============================================================
DROP TABLE IF EXISTS toolkit_electricity_price;
CREATE TABLE toolkit_electricity_price (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    price_name VARCHAR(200) NOT NULL COMMENT '电价名称',
    effective_date DATE NOT NULL COMMENT '生效日期',
    peak_price DECIMAL(18,4) NOT NULL DEFAULT 0 COMMENT '峰段电价(元/kWh)',
    flat_price DECIMAL(18,4) NOT NULL DEFAULT 0 COMMENT '平段电价(元/kWh)',
    valley_price DECIMAL(18,4) NOT NULL DEFAULT 0 COMMENT '谷段电价(元/kWh)',
    peak_hours VARCHAR(100) DEFAULT NULL COMMENT '峰时段(如08:00-11:00,18:00-21:00)',
    flat_hours VARCHAR(100) DEFAULT NULL COMMENT '平时段(如07:00-08:00,11:00-18:00)',
    valley_hours VARCHAR(100) DEFAULT NULL COMMENT '谷时段(如21:00-07:00)',
    status TINYINT DEFAULT 1 COMMENT '状态（1正常 0停用）',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted TINYINT DEFAULT 0 COMMENT '删除标志（0代表存在 1代表删除）',
    PRIMARY KEY (id),
    KEY idx_effective_date (effective_date)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='分时电价配置';