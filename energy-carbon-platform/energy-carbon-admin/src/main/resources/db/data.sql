-- ============================================================
-- 能碳管理平台数据库初始化脚本 - 初始化数据
-- 数据库: energy_carbon
-- ============================================================

USE energy_carbon;

-- ============================================================
-- 1. 初始化部门数据
-- ============================================================
INSERT INTO sys_dept (id, dept_name, parent_id, order_num, leader, phone, email, status) VALUES
(1, '世通科技', 0, 0, '张三', '13800138000', 'zhangsan@seatone.com', 1),
(2, '研发中心', 1, 1, '李四', '13800138001', 'lisi@seatone.com', 1),
(3, '产品中心', 1, 2, '王五', '13800138002', 'wangwu@seatone.com', 1),
(4, '运维中心', 1, 3, '赵六', '13800138003', 'zhaoliu@seatone.com', 1),
(5, '前端开发组', 2, 1, '钱七', '13800138004', 'qianqi@seatone.com', 1),
(6, '后端开发组', 2, 2, '孙八', '13800138005', 'sunba@seatone.com', 1),
(7, '测试组', 2, 3, '周九', '13800138006', 'zhoujiu@seatone.com', 1);

-- ============================================================
-- 2. 初始化用户数据
-- ============================================================
-- 密码: 123456 (BCrypt加密)
INSERT INTO sys_user (id, username, password, nickname, phone, email, avatar, dept_id, status, create_by, update_by) VALUES
(1, 'admin', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EO', '系统管理员', '13800138000', 'admin@seatone.com', NULL, 1, 1, 1, 1);

-- ============================================================
-- 3. 初始化角色数据
-- ============================================================
INSERT INTO sys_role (id, role_name, role_key, sort, status, create_by, update_by) VALUES
(1, '超级管理员', 'super_admin', 1, 1, 1, 1),
(2, '系统管理员', 'system_admin', 2, 1, 1, 1),
(3, '普通用户', 'common_user', 3, 1, 1, 1),
(4, '监控人员', 'monitor_user', 4, 1, 1, 1),
(5, '碳排专员', 'carbon_user', 5, 1, 1, 1);

-- ============================================================
-- 4. 初始化菜单数据
-- ============================================================
INSERT INTO sys_menu (id, menu_name, parent_id, order_num, path, component, icon, menu_type, perms, status) VALUES
-- 首页
(1, '首页', 0, 0, '/index', 'index', 'dashboard', 'C', 'system:index', 1),
-- 系统管理
(2, '系统管理', 0, 1, '/system', NULL, 'system', 'M', NULL, 1),
(3, '用户管理', 2, 1, '/system/user', 'system/user/index', 'user', 'C', 'system:user:list', 1),
(4, '角色管理', 2, 2, '/system/role', 'system/role/index', 'peoples', 'C', 'system:role:list', 1),
(5, '菜单管理', 2, 3, '/system/menu', 'system/menu/index', 'tree-table', 'C', 'system:menu:list', 1),
(6, '部门管理', 2, 4, '/system/dept', 'system/dept/index', 'tree', 'C', 'system:dept:list', 1),
(7, '字典管理', 2, 5, '/system/dict', 'system/dict/index', 'dict', 'C', 'system:dict:list', 1),
(8, '参数管理', 2, 6, '/system/config', 'system/config/index', 'edit', 'C', 'system:config:list', 1),
(9, '通知公告', 2, 7, '/system/notice', 'system/notice/index', 'message', 'C', 'system:notice:list', 1),
(10, '操作日志', 2, 8, '/system/log', 'system/log/index', 'log', 'C', 'system:log:list', 1),
-- 监控管理
(20, '监控管理', 0, 2, '/monitor', NULL, 'monitor', 'M', NULL, 1),
(21, '数据监控', 20, 1, '/monitor/data', 'monitor/data/index', 'data', 'C', 'monitor:data:list', 1),
(22, '设备监控', 20, 2, '/monitor/device', 'monitor/device/index', 'device', 'C', 'monitor:device:list', 1),
(23, '实时监控', 20, 3, '/monitor/realtime', 'monitor/realtime/index', 'eye-open', 'C', 'monitor:realtime:list', 1),
(24, '历史数据', 20, 4, '/monitor/history', 'monitor/history/index', 'chart', 'C', 'monitor:history:list', 1),
-- 告警管理
(30, '告警管理', 0, 3, '/alarm', NULL, 'alarm', 'M', NULL, 1),
(31, '告警规则', 30, 1, '/alarm/rule', 'alarm/rule/index', 'alarm-rule', 'C', 'alarm:rule:list', 1),
(32, '告警记录', 30, 2, '/alarm/record', 'alarm/record/index', 'alarm-record', 'C', 'alarm:record:list', 1),
(33, '告警通知', 30, 3, '/alarm/notice', 'alarm/notice/index', 'alarm-notice', 'C', 'alarm:notice:list', 1),
-- 碳排管理
(40, '碳排管理', 0, 4, '/carbon', NULL, 'carbon', 'M', NULL, 1),
(41, '排放核算', 40, 1, '/carbon/accounting', 'carbon/accounting/index', 'accounting', 'C', 'carbon:accounting:list', 1),
(42, '排放因子', 40, 2, '/carbon/factor', 'carbon/factor/index', 'factor', 'C', 'carbon:factor:list', 1),
(43, '碳排报告', 40, 3, '/carbon/report', 'carbon/report/index', 'report', 'C', 'carbon:report:list', 1),
(44, '碳目标', 40, 4, '/carbon/target', 'carbon/target/index', 'target', 'C', 'carbon:target:list', 1),
-- 报表管理
(50, '报表管理', 0, 5, '/report', NULL, 'report', 'M', NULL, 1),
(51, '报表模板', 50, 1, '/report/template', 'report/template/index', 'template', 'C', 'report:template:list', 1),
(52, '报表实例', 50, 2, '/report/instance', 'report/instance/index', 'instance', 'C', 'report:instance:list', 1),
(53, '定时任务', 50, 3, '/report/schedule', 'report/schedule/index', 'schedule', 'C', 'report:schedule:list', 1);

-- ============================================================
-- 5. 用户角色关联
-- ============================================================
INSERT INTO sys_user_role (user_id, role_id) VALUES
(1, 1);

-- ============================================================
-- 6. 角色菜单关联 (超级管理员拥有所有菜单)
-- ============================================================
INSERT INTO sys_role_menu (role_id, menu_id) VALUES
(1, 1), (1, 2), (1, 3), (1, 4), (1, 5), (1, 6), (1, 7), (1, 8), (1, 9), (1, 10),
(1, 20), (1, 21), (1, 22), (1, 23), (1, 24),
(1, 30), (1, 31), (1, 32), (1, 33),
(1, 40), (1, 41), (1, 42), (1, 43), (1, 44),
(1, 50), (1, 51), (1, 52), (1, 53);

-- ============================================================
-- 7. 字典类型数据
-- ============================================================
INSERT INTO sys_dict_type (id, dict_name, dict_type, status) VALUES
(1, '用户性别', 'sys_user_sex', 1),
(2, '菜单状态', 'sys_show_hide', 1),
(3, '系统状态', 'sys_normal_disable', 1),
(4, '操作类型', 'sys_oper_type', 1),
(5, '通知类型', 'sys_notice_type', 1),
(6, '通知状态', 'sys_notice_status', 1),
(7, '设备类型', 'monitor_device_type', 1),
(8, '告警级别', 'alarm_level', 1),
(9, '告警状态', 'alarm_status', 1),
(10, '碳排类型', 'carbon_type', 1);

-- ============================================================
-- 8. 字典数据
-- ============================================================
INSERT INTO sys_dict_data (id, dict_sort, dict_label, dict_value, dict_type, css_class, list_class, is_default, status) VALUES
-- 用户性别
(1, 1, '男', '0', 'sys_user_sex', '', 'default', 1, 1),
(2, 2, '女', '1', 'sys_user_sex', '', 'default', 0, 1),
(3, 3, '未知', '2', 'sys_user_sex', '', 'default', 0, 1),
-- 菜单状态
(4, 1, '显示', '0', 'sys_show_hide', '', 'primary', 1, 1),
(5, 2, '隐藏', '1', 'sys_show_hide', '', 'danger', 0, 1),
-- 系统状态
(6, 1, '正常', '1', 'sys_normal_disable', '', 'primary', 1, 1),
(7, 2, '停用', '0', 'sys_normal_disable', '', 'danger', 0, 1),
-- 操作类型
(8, 1, '新增', '1', 'sys_oper_type', '', 'info', 0, 1),
(9, 2, '修改', '2', 'sys_oper_type', '', 'info', 0, 1),
(10, 3, '删除', '3', 'sys_oper_type', '', 'danger', 0, 1),
(11, 4, '授权', '4', 'sys_oper_type', '', 'primary', 0, 1),
(12, 5, '导出', '5', 'sys_oper_type', '', 'warning', 0, 1),
(13, 6, '导入', '6', 'sys_oper_type', '', 'warning', 0, 1),
(14, 7, '强退', '7', 'sys_oper_type', '', 'danger', 0, 1),
(15, 8, '生成代码', '8', 'sys_oper_type', '', 'primary', 0, 1),
(16, 9, '清空数据', '9', 'sys_oper_type', '', 'danger', 0, 1),
-- 通知类型
(17, 1, '通知', '1', 'sys_notice_type', '', 'warning', 0, 1),
(18, 2, '公告', '2', 'sys_notice_type', '', 'success', 0, 1),
-- 通知状态
(19, 1, '正常', '0', 'sys_notice_status', '', 'primary', 0, 1),
(20, 2, '关闭', '1', 'sys_notice_status', '', 'danger', 0, 1),
-- 设备类型
(21, 1, '电表', '1', 'monitor_device_type', '', 'primary', 1, 1),
(22, 2, '水表', '2', 'monitor_device_type', '', 'success', 0, 1),
(23, 3, '气表', '3', 'monitor_device_type', '', 'warning', 0, 1),
(24, 4, '传感器', '4', 'monitor_device_type', '', 'info', 0, 1),
-- 告警级别
(25, 1, '紧急', '1', 'alarm_level', '', 'danger', 0, 1),
(26, 2, '重要', '2', 'alarm_level', '', 'warning', 0, 1),
(27, 3, '一般', '3', 'alarm_level', '', 'info', 0, 1),
(28, 4, '提示', '4', 'alarm_level', '', 'default', 0, 1),
-- 告警状态
(29, 1, '未处理', '0', 'alarm_status', '', 'danger', 1, 1),
(30, 2, '处理中', '1', 'alarm_status', '', 'warning', 0, 1),
(31, 3, '已处理', '2', 'alarm_status', '', 'success', 0, 1),
(32, 4, '已忽略', '3', 'alarm_status', '', 'info', 0, 1),
-- 碳排类型
(33, 1, '范围一', '1', 'carbon_type', '', 'danger', 0, 1),
(34, 2, '范围二', '2', 'carbon_type', '', 'warning', 0, 1),
(35, 3, '范围三', '3', 'carbon_type', '', 'info', 0, 1);
