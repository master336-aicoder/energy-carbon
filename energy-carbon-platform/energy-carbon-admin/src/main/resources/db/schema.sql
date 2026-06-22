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