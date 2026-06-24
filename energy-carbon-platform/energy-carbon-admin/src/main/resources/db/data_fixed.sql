-- ============================================================
--  - 
-- : energy_carbon
-- ============================================================

USE energy_carbon;

-- ============================================================
-- 1. 
-- ============================================================
INSERT INTO sys_dept (id, dept_name, parent_id, sort, leader, phone, email, status) VALUES
(1, '', 0, 0, '', '13800138000', 'zhangsan@seatone.com', 1),
(2, '', 1, 1, '', '13800138001', 'lisi@seatone.com', 1),
(3, '', 1, 2, '', '13800138002', 'wangwu@seatone.com', 1),
(4, '', 1, 3, '', '13800138003', 'zhaoliu@seatone.com', 1),
(5, '', 2, 1, '', '13800138004', 'qianqi@seatone.com', 1),
(6, '', 2, 2, '', '13800138005', 'sunba@seatone.com', 1),
(7, '', 2, 3, '', '13800138006', 'zhoujiu@seatone.com', 1);

-- ============================================================
-- 2. 
-- ============================================================
-- : 123456 (BCrypt)
INSERT INTO sys_user (id, username, password, nickname, phone, email, avatar, dept_id, status, create_by, update_by) VALUES
(1, 'admin', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EO', '', '13800138000', 'admin@seatone.com', NULL, 1, 1, 1, 1);

-- ============================================================
-- 3. 
-- ============================================================
INSERT INTO sys_role (id, role_name, role_code, sort, status, create_by, update_by) VALUES
(1, '', 'super_admin', 1, 1, 1, 1),
(2, '', 'system_admin', 2, 1, 1, 1),
(3, '', 'common_user', 3, 1, 1, 1),
(4, '', 'monitor_user', 4, 1, 1, 1),
(5, '', 'carbon_user', 5, 1, 1, 1);

-- ============================================================
-- 4. 
-- ============================================================
INSERT INTO sys_menu (id, menu_name, parent_id, sort, path, component, icon, menu_type, perms, status) VALUES
-- 
(1, '', 0, 0, '/index', 'index', 'dashboard', 'C', 'system:index', 1),
-- 
(2, '', 0, 1, '/system', NULL, 'system', 'M', NULL, 1),
(3, '', 2, 1, '/system/user', 'system/user/index', 'user', 'C', 'system:user:list', 1),
(4, '', 2, 2, '/system/role', 'system/role/index', 'peoples', 'C', 'system:role:list', 1),
(5, '', 2, 3, '/system/menu', 'system/menu/index', 'tree-table', 'C', 'system:menu:list', 1),
(6, '', 2, 4, '/system/dept', 'system/dept/index', 'tree', 'C', 'system:dept:list', 1),
(7, '', 2, 5, '/system/dict', 'system/dict/index', 'dict', 'C', 'system:dict:list', 1),
(8, '', 2, 6, '/system/config', 'system/config/index', 'edit', 'C', 'system:config:list', 1),
(9, '', 2, 7, '/system/notice', 'system/notice/index', 'message', 'C', 'system:notice:list', 1),
(10, '', 2, 8, '/system/log', 'system/log/index', 'log', 'C', 'system:log:list', 1),
-- 
(20, '', 0, 2, '/monitor', NULL, 'monitor', 'M', NULL, 1),
(21, '', 20, 1, '/monitor/data', 'monitor/data/index', 'data', 'C', 'monitor:data:list', 1),
(22, '', 20, 2, '/monitor/device', 'monitor/device/index', 'device', 'C', 'monitor:device:list', 1),
(23, '', 20, 3, '/monitor/realtime', 'monitor/realtime/index', 'eye-open', 'C', 'monitor:realtime:list', 1),
(24, '', 20, 4, '/monitor/history', 'monitor/history/index', 'chart', 'C', 'monitor:history:list', 1),
-- 
(30, '', 0, 3, '/alarm', NULL, 'alarm', 'M', NULL, 1),
(31, '', 30, 1, '/alarm/rule', 'alarm/rule/index', 'alarm-rule', 'C', 'alarm:rule:list', 1),
(32, '', 30, 2, '/alarm/record', 'alarm/record/index', 'alarm-record', 'C', 'alarm:record:list', 1),
(33, '', 30, 3, '/alarm/notice', 'alarm/notice/index', 'alarm-notice', 'C', 'alarm:notice:list', 1),
-- 
(40, '', 0, 4, '/carbon', NULL, 'carbon', 'M', NULL, 1),
(41, '', 40, 1, '/carbon/accounting', 'carbon/accounting/index', 'accounting', 'C', 'carbon:accounting:list', 1),
(42, '', 40, 2, '/carbon/factor', 'carbon/factor/index', 'factor', 'C', 'carbon:factor:list', 1),
(43, '', 40, 3, '/carbon/report', 'carbon/report/index', 'report', 'C', 'carbon:report:list', 1),
(44, '', 40, 4, '/carbon/target', 'carbon/target/index', 'target', 'C', 'carbon:target:list', 1),
-- 
(50, '', 0, 5, '/report', NULL, 'report', 'M', NULL, 1),
(51, '', 50, 1, '/report/template', 'report/template/index', 'template', 'C', 'report:template:list', 1),
(52, '', 50, 2, '/report/instance', 'report/instance/index', 'instance', 'C', 'report:instance:list', 1),
(53, '', 50, 3, '/report/schedule', 'report/schedule/index', 'schedule', 'C', 'report:schedule:list', 1);

-- ============================================================
-- 5. 
-- ============================================================
INSERT INTO sys_user_role (user_id, role_id) VALUES
(1, 1);

-- ============================================================
-- 6.  ()
-- ============================================================
INSERT INTO sys_role_menu (role_id, menu_id) VALUES
(1, 1), (1, 2), (1, 3), (1, 4), (1, 5), (1, 6), (1, 7), (1, 8), (1, 9), (1, 10),
(1, 20), (1, 21), (1, 22), (1, 23), (1, 24),
(1, 30), (1, 31), (1, 32), (1, 33),
(1, 40), (1, 41), (1, 42), (1, 43), (1, 44),
(1, 50), (1, 51), (1, 52), (1, 53);

-- ============================================================
-- 7. 
-- ============================================================
INSERT INTO sys_dict_type (id, dict_name, dict_type, status) VALUES
(1, '', 'sys_user_sex', 1),
(2, '', 'sys_show_hide', 1),
(3, '', 'sys_normal_disable', 1),
(4, '', 'sys_oper_type', 1),
(5, '', 'sys_notice_type', 1),
(6, '', 'sys_notice_status', 1),
(7, '', 'monitor_device_type', 1),
(8, '', 'alarm_level', 1),
(9, '', 'alarm_status', 1),
(10, '', 'carbon_type', 1);

-- ============================================================
-- 8. 
-- ============================================================
INSERT INTO sys_dict_data (id, dict_sort, dict_label, dict_value, dict_type, css_class, list_class, is_default, status) VALUES
-- 
(1, 1, '', '0', 'sys_user_sex', '', 'default', 1, 1),
(2, 2, '', '1', 'sys_user_sex', '', 'default', 0, 1),
(3, 3, '', '2', 'sys_user_sex', '', 'default', 0, 1),
-- 
(4, 1, '', '0', 'sys_show_hide', '', 'primary', 1, 1),
(5, 2, '', '1', 'sys_show_hide', '', 'danger', 0, 1),
-- 
(6, 1, '', '1', 'sys_normal_disable', '', 'primary', 1, 1),
(7, 2, '', '0', 'sys_normal_disable', '', 'danger', 0, 1),
-- 
(8, 1, '', '1', 'sys_oper_type', '', 'info', 0, 1),
(9, 2, '', '2', 'sys_oper_type', '', 'info', 0, 1),
(10, 3, '', '3', 'sys_oper_type', '', 'danger', 0, 1),
(11, 4, '', '4', 'sys_oper_type', '', 'primary', 0, 1),
(12, 5, '', '5', 'sys_oper_type', '', 'warning', 0, 1),
(13, 6, '', '6', 'sys_oper_type', '', 'warning', 0, 1),
(14, 7, '', '7', 'sys_oper_type', '', 'danger', 0, 1),
(15, 8, '', '8', 'sys_oper_type', '', 'primary', 0, 1),
(16, 9, '', '9', 'sys_oper_type', '', 'danger', 0, 1),
-- 
(17, 1, '', '1', 'sys_notice_type', '', 'warning', 0, 1),
(18, 2, '', '2', 'sys_notice_type', '', 'success', 0, 1),
-- 
(19, 1, '', '0', 'sys_notice_status', '', 'primary', 0, 1),
(20, 2, '', '1', 'sys_notice_status', '', 'danger', 0, 1),
-- 
(21, 1, '', '1', 'monitor_device_type', '', 'primary', 1, 1),
(22, 2, '', '2', 'monitor_device_type', '', 'success', 0, 1),
(23, 3, '', '3', 'monitor_device_type', '', 'warning', 0, 1),
(24, 4, '', '4', 'monitor_device_type', '', 'info', 0, 1),
-- 
(25, 1, '', '1', 'alarm_level', '', 'danger', 0, 1),
(26, 2, '', '2', 'alarm_level', '', 'warning', 0, 1),
(27, 3, '', '3', 'alarm_level', '', 'info', 0, 1),
(28, 4, '', '4', 'alarm_level', '', 'default', 0, 1),
-- 
(29, 1, '', '0', 'alarm_status', '', 'danger', 1, 1),
(30, 2, '', '1', 'alarm_status', '', 'warning', 0, 1),
(31, 3, '', '2', 'alarm_status', '', 'success', 0, 1),
(32, 4, '', '3', 'alarm_status', '', 'info', 0, 1),
-- 
(33, 1, '', '1', 'carbon_type', '', 'danger', 0, 1),
(34, 2, '', '2', 'carbon_type', '', 'warning', 0, 1),
(35, 3, '', '3', 'carbon_type', '', 'info', 0, 1);
