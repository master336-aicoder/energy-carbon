
USE energy_carbon;

-- 通知下发记录表
CREATE TABLE IF NOT EXISTS sys_notice_dispatch (
  id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
  notice_id BIGINT NOT NULL COMMENT '关联通知公告ID',
  user_id BIGINT NOT NULL COMMENT '接收用户ID',
  read_status TINYINT DEFAULT 0 COMMENT '阅读状态（0未读 1已读）',
  read_time DATETIME DEFAULT NULL COMMENT '阅读时间',
  create_by BIGINT DEFAULT NULL COMMENT '创建人ID',
  create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  update_by BIGINT DEFAULT NULL COMMENT '更新人ID',
  update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  KEY idx_notice_id (notice_id),
  KEY idx_user_id (user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='通知下发记录表';

-- 用户消息表
CREATE TABLE IF NOT EXISTS sys_user_message (
  id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
  user_id BIGINT NOT NULL COMMENT '接收用户ID',
  title VARCHAR(200) NOT NULL COMMENT '消息标题',
  content TEXT COMMENT '消息内容',
  msg_type TINYINT DEFAULT 1 COMMENT '消息类型（1系统 2通知 3告警）',
  read_status TINYINT DEFAULT 0 COMMENT '阅读状态（0未读 1已读）',
  read_time DATETIME DEFAULT NULL COMMENT '阅读时间',
  create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  KEY idx_user_id (user_id),
  KEY idx_read_status (read_status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户消息表';

-- 碳目标表
CREATE TABLE IF NOT EXISTS carbon_target (
  id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
  target_name VARCHAR(200) NOT NULL COMMENT '目标名称',
  target_year INT NOT NULL COMMENT '目标年份',
  target_type VARCHAR(50) NOT NULL COMMENT '目标类型',
  target_value DECIMAL(18,4) NOT NULL DEFAULT 0 COMMENT '目标值',
  unit VARCHAR(50) NOT NULL COMMENT '单位',
  baseline_value DECIMAL(18,4) DEFAULT 0 COMMENT '基准值',
  reduction_rate DECIMAL(5,2) DEFAULT 0 COMMENT '减排率',
  status VARCHAR(20) DEFAULT 'ACTIVE' COMMENT '状态',
  remark TEXT COMMENT '备注',
  create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='碳目标表';

-- 基准能耗表
CREATE TABLE IF NOT EXISTS report_baseline_consumption (
  id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
  energy_unit_id BIGINT NOT NULL COMMENT '用能单元ID',
  start_date DATE NOT NULL COMMENT '开始日期',
  end_date DATE NOT NULL COMMENT '结束日期',
  baseline_value DECIMAL(18,4) NOT NULL DEFAULT 0 COMMENT '基准值',
  baseline_type VARCHAR(50) DEFAULT NULL COMMENT '基准类型',
  remark TEXT COMMENT '备注',
  create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='基准能耗表';

-- CBAM申报表
CREATE TABLE IF NOT EXISTS carbon_cbam_declaration (
  id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
  declaration_name VARCHAR(200) NOT NULL COMMENT '申报名称',
  declaration_period VARCHAR(50) DEFAULT NULL COMMENT '申报周期',
  product_name VARCHAR(200) DEFAULT NULL COMMENT '产品名称',
  product_code VARCHAR(100) DEFAULT NULL COMMENT '产品编码',
  import_quantity DECIMAL(18,4) DEFAULT 0 COMMENT '进口数量',
  unit VARCHAR(50) DEFAULT NULL COMMENT '单位',
  direct_emission DECIMAL(18,4) DEFAULT 0 COMMENT '直接排放',
  indirect_emission DECIMAL(18,4) DEFAULT 0 COMMENT '间接排放',
  total_emission DECIMAL(18,4) DEFAULT 0 COMMENT '总排放',
  status VARCHAR(20) DEFAULT 'DRAFT' COMMENT '状态',
  remark TEXT COMMENT '备注',
  create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='CBAM申报表';

-- 碳市场信息表
CREATE TABLE IF NOT EXISTS carbon_market_info (
  id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
  info_title VARCHAR(200) NOT NULL COMMENT '信息标题',
  info_category VARCHAR(50) DEFAULT NULL COMMENT '信息分类',
  content TEXT COMMENT '内容',
  source VARCHAR(200) DEFAULT NULL COMMENT '来源',
  publish_date DATE DEFAULT NULL COMMENT '发布日期',
  status TINYINT DEFAULT 1 COMMENT '状态',
  create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='碳市场信息表';

INSERT INTO carbon_market_info (info_title, info_category, content, source, publish_date, status) VALUES
('全国碳市场扩大覆盖范围工作方案发布', '政策法规', '生态环境部发布《全国碳排放权交易市场覆盖水泥、钢铁、电解铝行业工作方案》，明确2024-2026年分阶段纳入。', '生态环境部', '2026-06-01', 1),
('欧盟碳边境调节机制(CBAM)过渡期报告要求', '国际动态', '欧盟委员会发布CBAM过渡期实施细则，要求进口商按季度提交隐含碳排放数据。', '欧盟委员会', '2026-06-05', 1),
('全国碳市场碳价突破120元/吨', '市场行情', 'CEA主力合约收盘价达到121.5元/吨，创历史新高。', '上海环境能源交易所', '2026-06-10', 1)
ON DUPLICATE KEY UPDATE info_title = info_title;
