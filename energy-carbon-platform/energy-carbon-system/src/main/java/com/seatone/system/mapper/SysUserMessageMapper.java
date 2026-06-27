package com.seatone.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.seatone.system.entity.SysUserMessage;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 用户消息 Mapper
 *
 * @author seatone
 * @since 2026-06-27
 */
@Mapper
public interface SysUserMessageMapper extends BaseMapper<SysUserMessage> {

    /**
     * 查询某用户的消息列表
     *
     * @param userId 用户ID
     * @return 消息列表
     */
    List<SysUserMessage> selectUserMessages(@Param("userId") Long userId);

    /**
     * 统计某用户的未读消息数
     *
     * @param userId 用户ID
     * @return 未读消息数量
     */
    int countUserUnread(@Param("userId") Long userId);

    /**
     * 查询某用户某条消息
     *
     * @param id     消息ID
     * @param userId 用户ID
     * @return 消息详情
     */
    SysUserMessage selectByIdAndUser(@Param("id") Long id, @Param("userId") Long userId);
}
