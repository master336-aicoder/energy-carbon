package com.seatone.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.seatone.common.utils.SecurityUtils;
import com.seatone.system.entity.SysDept;
import com.seatone.system.entity.SysNotice;
import com.seatone.system.entity.SysNoticeDispatch;
import com.seatone.system.entity.SysUser;
import com.seatone.system.entity.SysUserMessage;
import com.seatone.system.entity.SysUserRole;
import com.seatone.system.mapper.SysDeptMapper;
import com.seatone.system.mapper.SysNoticeDispatchMapper;
import com.seatone.system.mapper.SysNoticeMapper;
import com.seatone.system.mapper.SysUserMapper;
import com.seatone.system.mapper.SysUserMessageMapper;
import com.seatone.system.mapper.SysUserRoleMapper;
import com.seatone.system.service.SysNoticeDispatchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 通知公告下发 Service 实现类
 *
 * @author seatone
 * @since 2026-06-27
 */
@Service
public class SysNoticeDispatchServiceImpl extends ServiceImpl<SysNoticeDispatchMapper, SysNoticeDispatch> implements SysNoticeDispatchService {

    @Autowired
    private SysNoticeMapper noticeMapper;

    @Autowired
    private SysUserMapper userMapper;

    @Autowired
    private SysDeptMapper deptMapper;

    @Autowired
    private SysUserRoleMapper userRoleMapper;

    @Autowired
    private SysUserMessageMapper userMessageMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void dispatch(Long noticeId, Integer dispatchType, List<Long> targetIds) {
        // 1. 查询通知公告
        SysNotice notice = noticeMapper.selectById(noticeId);
        if (notice == null) {
            throw new RuntimeException("通知公告不存在");
        }

        // 2. 根据下发类型查询目标用户ID集合
        Set<Long> userIdSet = resolveTargetUsers(dispatchType, targetIds);
        if (CollectionUtils.isEmpty(userIdSet)) {
            throw new RuntimeException("未找到目标用户");
        }

        // 3. 构建下发目标名称
        String targetName = buildTargetName(dispatchType, targetIds);

        // 4. 保存下发记录
        SysNoticeDispatch dispatch = new SysNoticeDispatch();
        dispatch.setNoticeId(noticeId);
        dispatch.setDispatchType(dispatchType);
        dispatch.setTargetName(targetName);
        dispatch.setCreateBy(SecurityUtils.getCurrentUserId());
        dispatch.setCreateTime(LocalDateTime.now());
        baseMapper.insert(dispatch);

        // 5. 为每个目标用户创建消息记录
        List<SysUserMessage> messages = new ArrayList<>();
        for (Long userId : userIdSet) {
            SysUserMessage message = new SysUserMessage();
            message.setNoticeId(noticeId);
            message.setUserId(userId);
            message.setTitle(notice.getNoticeTitle());
            message.setContent(notice.getNoticeContent());
            message.setNoticeType(notice.getNoticeType());
            message.setReadStatus(0);
            message.setCreateTime(LocalDateTime.now());
            messages.add(message);
        }

        // 批量插入消息记录
        for (SysUserMessage message : messages) {
            userMessageMapper.insert(message);
        }
    }

    /**
     * 根据下发类型解析目标用户ID集合
     *
     * @param dispatchType 下发类型
     * @param targetIds    目标ID列表
     * @return 用户ID集合（去重）
     */
    private Set<Long> resolveTargetUsers(Integer dispatchType, List<Long> targetIds) {
        Set<Long> userIdSet = new HashSet<>();

        switch (dispatchType) {
            case 1:
                // 全部：查所有启用用户
                LambdaQueryWrapper<SysUser> allWrapper = new LambdaQueryWrapper<>();
                allWrapper.eq(SysUser::getStatus, 1);
                List<SysUser> allUsers = userMapper.selectList(allWrapper);
                for (SysUser user : allUsers) {
                    userIdSet.add(user.getId());
                }
                break;

            case 2:
                // 机构：查指定机构下的所有用户
                if (!CollectionUtils.isEmpty(targetIds)) {
                    LambdaQueryWrapper<SysUser> deptWrapper = new LambdaQueryWrapper<>();
                    deptWrapper.eq(SysUser::getStatus, 1);
                    deptWrapper.in(SysUser::getDeptId, targetIds);
                    List<SysUser> deptUsers = userMapper.selectList(deptWrapper);
                    for (SysUser user : deptUsers) {
                        userIdSet.add(user.getId());
                    }
                }
                break;

            case 3:
                // 角色：查指定角色关联的用户
                if (!CollectionUtils.isEmpty(targetIds)) {
                    LambdaQueryWrapper<SysUserRole> roleWrapper = new LambdaQueryWrapper<>();
                    roleWrapper.in(SysUserRole::getRoleId, targetIds);
                    List<SysUserRole> userRoles = userRoleMapper.selectList(roleWrapper);
                    for (SysUserRole ur : userRoles) {
                        userIdSet.add(ur.getUserId());
                    }
                }
                break;

            case 4:
                // 用户：直接使用 targetIds
                if (!CollectionUtils.isEmpty(targetIds)) {
                    userIdSet.addAll(targetIds);
                }
                break;

            default:
                throw new RuntimeException("不支持的下发类型：" + dispatchType);
        }

        return userIdSet;
    }

    /**
     * 构建下发目标名称字符串
     *
     * @param dispatchType 下发类型
     * @param targetIds    目标ID列表
     * @return 目标名称字符串
     */
    private String buildTargetName(Integer dispatchType, List<Long> targetIds) {
        switch (dispatchType) {
            case 1:
                return "全部";
            case 2:
                // 查询机构名称
                if (!CollectionUtils.isEmpty(targetIds)) {
                    List<SysDept> depts = deptMapper.selectBatchIds(targetIds);
                    return depts.stream().map(SysDept::getDeptName).collect(Collectors.joining(","));
                }
                return "";
            case 3:
                // 角色名称暂不翻译，直接展示ID
                if (!CollectionUtils.isEmpty(targetIds)) {
                    return "角色ID:" + targetIds.stream().map(String::valueOf).collect(Collectors.joining(","));
                }
                return "";
            case 4:
                // 用户名称暂不翻译，直接展示ID
                if (!CollectionUtils.isEmpty(targetIds)) {
                    return "用户ID:" + targetIds.stream().map(String::valueOf).collect(Collectors.joining(","));
                }
                return "";
            default:
                return "";
        }
    }
}
