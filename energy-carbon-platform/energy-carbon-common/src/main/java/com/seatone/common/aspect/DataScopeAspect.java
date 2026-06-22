package com.seatone.common.aspect;

import com.seatone.common.annotation.DataScope;
import com.seatone.common.utils.SecurityUtils;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * 数据权限过滤切面
 * 自动为标注了 @DataScope 注解的方法拼接数据权限 SQL 条件
 * 支持本部门、本部门及以下、自定义数据权限
 */
@Slf4j
@Aspect
@Component
public class DataScopeAspect {

    /**
     * 全部数据权限
     */
    public static final String DATA_SCOPE_ALL = "1";

    /**
     * 自定义数据权限
     */
    public static final String DATA_SCOPE_CUSTOM = "2";

    /**
     * 本部门数据权限
     */
    public static final String DATA_SCOPE_DEPT = "3";

    /**
     * 本部门及以下数据权限
     */
    public static final String DATA_SCOPE_DEPT_AND_CHILD = "4";

    /**
     * 仅本人数据权限
     */
    public static final String DATA_SCOPE_SELF = "5";

    /**
     * 数据权限过滤关键字
     */
    public static final String DATA_SCOPE = "dataScope";

    /**
     * 执行数据权限过滤
     */
    @Before("@annotation(com.seatone.common.annotation.DataScope)")
    public void doBefore(JoinPoint point) {
        handleDataScope(point);
    }

    /**
     * 处理数据权限
     *
     * @param point 切点
     */
    protected void handleDataScope(final JoinPoint point) {
        // 获取注解
        DataScope dataScope = getAnnotation(point);
        if (dataScope == null) {
            return;
        }

        // 获取当前登录用户
        Long userId = SecurityUtils.getCurrentUserId();
        if (userId == null) {
            return;
        }

        // 如果是超级管理员，则不进行数据权限过滤
        if (isAdmin(userId)) {
            return;
        }

        // 获取数据权限 SQL 条件
        StringBuilder sqlString = new StringBuilder();

        // 模拟获取用户的数据权限范围（实际项目中从用户角色中获取）
        // 这里为了演示，假设当前用户有本部门及以下权限
        String dataScopeType = getDataScopeType(userId);

        switch (dataScopeType) {
            // 全部数据权限
            case DATA_SCOPE_ALL:
                break;
            // 自定义数据权限
            case DATA_SCOPE_CUSTOM:
                sqlString.append(getCustomDataScopeSql(dataScope));
                break;
            // 本部门数据权限
            case DATA_SCOPE_DEPT:
                sqlString.append(getDeptDataScopeSql(dataScope, userId));
                break;
            // 本部门及以下数据权限
            case DATA_SCOPE_DEPT_AND_CHILD:
                sqlString.append(getDeptAndChildDataScopeSql(dataScope, userId));
                break;
            // 仅本人数据权限
            case DATA_SCOPE_SELF:
                sqlString.append(getSelfDataScopeSql(dataScope, userId));
                break;
            default:
                break;
        }

        // 将数据权限 SQL 条件设置到参数中
        if (sqlString.length() > 0) {
            Object params = point.getArgs()[0];
            if (params != null) {
                try {
                    // 通过反射设置 dataScope 属性
                    java.lang.reflect.Method setDataScopeMethod = params.getClass().getMethod("setDataScope", String.class);
                    setDataScopeMethod.invoke(params, sqlString.toString());
                } catch (Exception e) {
                    log.warn("设置数据权限 SQL 条件失败: {}", e.getMessage());
                }
            }
        }
    }

    /**
     * 获取自定义数据权限 SQL
     *
     * @param dataScope 数据权限注解
     * @return SQL 条件
     */
    private String getCustomDataScopeSql(DataScope dataScope) {
        StringBuilder sql = new StringBuilder();
        // 获取用户有权限的部门 ID 列表
        List<Long> deptIds = getCustomDeptIds();
        if (deptIds != null && !deptIds.isEmpty()) {
            if (!dataScope.deptAlias().isEmpty()) {
                sql.append(" AND ").append(dataScope.deptAlias()).append(".dept_id IN (");
            } else {
                sql.append(" AND dept_id IN (");
            }
            sql.append(joinDeptIds(deptIds)).append(")");
        }
        return sql.toString();
    }

    /**
     * 获取本部门数据权限 SQL
     *
     * @param dataScope 数据权限注解
     * @param userId    用户ID
     * @return SQL 条件
     */
    private String getDeptDataScopeSql(DataScope dataScope, Long userId) {
        StringBuilder sql = new StringBuilder();
        Long deptId = getUserDeptId(userId);
        if (deptId != null) {
            if (!dataScope.deptAlias().isEmpty()) {
                sql.append(" AND ").append(dataScope.deptAlias()).append(".dept_id = ").append(deptId);
            } else {
                sql.append(" AND dept_id = ").append(deptId);
            }
        }
        return sql.toString();
    }

    /**
     * 获取本部门及以下数据权限 SQL
     *
     * @param dataScope 数据权限注解
     * @param userId    用户ID
     * @return SQL 条件
     */
    private String getDeptAndChildDataScopeSql(DataScope dataScope, Long userId) {
        StringBuilder sql = new StringBuilder();
        Long deptId = getUserDeptId(userId);
        if (deptId != null) {
            // 获取本部门及所有子部门 ID
            List<Long> deptIds = getDeptAndChildIds(deptId);
            if (!deptIds.isEmpty()) {
                if (!dataScope.deptAlias().isEmpty()) {
                    sql.append(" AND ").append(dataScope.deptAlias()).append(".dept_id IN (");
                } else {
                    sql.append(" AND dept_id IN (");
                }
                sql.append(joinDeptIds(deptIds)).append(")");
            }
        }
        return sql.toString();
    }

    /**
     * 获取仅本人数据权限 SQL
     *
     * @param dataScope 数据权限注解
     * @param userId    用户ID
     * @return SQL 条件
     */
    private String getSelfDataScopeSql(DataScope dataScope, Long userId) {
        StringBuilder sql = new StringBuilder();
        if (!dataScope.userAlias().isEmpty()) {
            sql.append(" AND ").append(dataScope.userAlias()).append(".user_id = ").append(userId);
        } else {
            sql.append(" AND user_id = ").append(userId);
        }
        return sql.toString();
    }

    /**
     * 获取方法上的 @DataScope 注解
     *
     * @param point 切点
     * @return @DataScope 注解
     */
    private DataScope getAnnotation(JoinPoint point) {
        org.aspectj.lang.reflect.MethodSignature signature = (org.aspectj.lang.reflect.MethodSignature) point.getSignature();
        java.lang.reflect.Method method = signature.getMethod();
        return method.getAnnotation(DataScope.class);
    }

    /**
     * 判断是否为超级管理员
     *
     * @param userId 用户ID
     * @return true=是超级管理员
     */
    private boolean isAdmin(Long userId) {
        // 超级管理员 ID 为 1
        return userId != null && userId == 1L;
    }

    /**
     * 获取用户的数据权限类型
     * 实际项目中应从用户角色中获取
     *
     * @param userId 用户ID
     * @return 数据权限类型
     */
    private String getDataScopeType(Long userId) {
        // 实际项目中从角色表中获取 data_scope 字段
        // 这里返回默认值，实际使用时需要根据用户角色动态获取
        return DATA_SCOPE_DEPT_AND_CHILD;
    }

    /**
     * 获取用户所属部门 ID
     * 实际项目中应从用户表中获取
     *
     * @param userId 用户ID
     * @return 部门ID
     */
    private Long getUserDeptId(Long userId) {
        // 实际项目中查询 sys_user 表获取 dept_id
        // 这里返回模拟数据
        return 1L;
    }

    /**
     * 获取自定义数据权限的部门 ID 列表
     * 实际项目中应从角色-部门关联表中获取
     *
     * @return 部门ID列表
     */
    private List<Long> getCustomDeptIds() {
        // 实际项目中查询 sys_role_dept 表
        List<Long> deptIds = new ArrayList<>();
        deptIds.add(1L);
        deptIds.add(2L);
        deptIds.add(3L);
        return deptIds;
    }

    /**
     * 获取部门及其所有子部门 ID
     * 实际项目中应递归查询部门表
     *
     * @param deptId 部门ID
     * @return 部门ID列表
     */
    private List<Long> getDeptAndChildIds(Long deptId) {
        // 实际项目中递归查询 sys_dept 表
        List<Long> deptIds = new ArrayList<>();
        deptIds.add(deptId);
        // 模拟添加子部门
        deptIds.add(2L);
        deptIds.add(3L);
        return deptIds;
    }

    /**
     * 将部门 ID 列表拼接为 SQL IN 条件字符串
     *
     * @param deptIds 部门ID列表
     * @return SQL 字符串
     */
    private String joinDeptIds(List<Long> deptIds) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < deptIds.size(); i++) {
            sb.append(deptIds.get(i));
            if (i < deptIds.size() - 1) {
                sb.append(",");
            }
        }
        return sb.toString();
    }
}
