-- ========================================
-- 版本号: v0.0.01
-- 日期: 2026-06-27
-- 描述: RBAC权限控制完善 + 前端路由和状态修复
-- ========================================

-- 1. 确保菜单管理数据中首页菜单的path为 /dashboard（非 index）
-- 如果菜单管理中首页的path不是/dashboard，执行以下修复：
UPDATE sys_menu SET path = '/dashboard', component = 'dashboard/Index' WHERE menu_name = '首页' AND parent_id = 0;

-- 2. 确保超级管理员角色(id=1)关联所有菜单（如果缺失）
-- 先删除已有的id=1角色菜单关联
DELETE FROM sys_role_menu WHERE role_id = 1;
-- 重新插入所有菜单
INSERT INTO sys_role_menu (role_id, menu_id)
SELECT 1, id FROM sys_menu WHERE status = 1;

-- 3. 确保默认用户admin(id=1)关联超级管理员角色(id=1)
INSERT IGNORE INTO sys_user_role (user_id, role_id) VALUES (1, 1);

-- 4. 修复菜单数据中可能存在的路由路径问题
-- 系统管理的子菜单path应为 system（不带前导/，因为会被父菜单的/dashboard/system拼接）
-- 以下SQL检查并修复可能的错误路径
UPDATE sys_menu SET path = 'system' WHERE menu_name = '用户管理' AND path IN ('/system', 'index');
UPDATE sys_menu SET path = 'system' WHERE menu_name = '角色管理' AND path IN ('/system', 'index');
UPDATE sys_menu SET path = 'system' WHERE menu_name = '菜单管理' AND path IN ('/system', 'index');
UPDATE sys_menu SET path = 'system' WHERE menu_name = '部门管理' AND path IN ('/system', 'index');
UPDATE sys_menu SET path = 'system' WHERE menu_name = '设备管理' AND path IN ('/system', 'index');
UPDATE sys_menu SET path = 'system' WHERE menu_name = '计量管理' AND path IN ('/system', 'index');
UPDATE sys_menu SET path = 'system' WHERE menu_name = '字典管理' AND path IN ('/system', 'index');

-- 在线监测子菜单
UPDATE sys_menu SET path = 'monitor' WHERE menu_name = '实时数据' AND path IN ('/monitor', 'index');
UPDATE sys_menu SET path = 'monitor' WHERE menu_name = '历史查询' AND path IN ('/monitor', 'index');
UPDATE sys_menu SET path = 'monitor' WHERE menu_name = '设备对比' AND path IN ('/monitor', 'index');
UPDATE sys_menu SET path = 'monitor' WHERE menu_name = '同比环比' AND path IN ('/monitor', 'index');
UPDATE sys_menu SET path = 'monitor' WHERE menu_name = '数据总览' AND path IN ('/monitor', 'index');

-- 报警管理子菜单
UPDATE sys_menu SET path = 'alarm' WHERE menu_name = '告警记录' AND path IN ('/alarm', 'index');
UPDATE sys_menu SET path = 'alarm' WHERE menu_name = '告警规则' AND path IN ('/alarm', 'index');

-- 报表管理子菜单
UPDATE sys_menu SET path = 'report' WHERE menu_name IN ('用能报表', '碳排报表') AND path IN ('/report', 'index');

-- 分析管理子菜单
UPDATE sys_menu SET path = 'analysis' WHERE menu_name = '趋势分析' AND path IN ('/analysis', 'index');

-- AI服务子菜单
UPDATE sys_menu SET path = 'ai' WHERE menu_name IN ('AI报告', 'AI对话', '知识库') AND path IN ('/ai', 'index');

-- 工具箱子菜单
UPDATE sys_menu SET path = 'toolkit' WHERE menu_name IN ('电价管理') AND path IN ('/toolkit', 'index');
