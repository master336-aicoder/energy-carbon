package com.seatone.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.seatone.system.entity.SysNotice;
import com.seatone.system.entity.SysNoticeRead;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 通知公告已读记录 Mapper
 *
 * @author seatone
 * @since 2026-06-27
 */
@Mapper
public interface SysNoticeReadMapper extends BaseMapper<SysNoticeRead> {

    /**
     * 查询该用户未读的启用公告
     *
     * @param userId 用户ID
     * @return 未读公告列表
     */
    List<SysNotice> selectUnreadList(@Param("userId") Long userId);

    /**
     * 统计该用户未读公告数
     *
     * @param userId 用户ID
     * @return 未读公告数量
     */
    int countUnread(@Param("userId") Long userId);

    /**
     * 查询是否已读
     *
     * @param noticeId 公告ID
     * @param userId   用户ID
     * @return 已读记录数（大于0表示已读）
     */
    int selectReadStatus(@Param("noticeId") Long noticeId, @Param("userId") Long userId);
}
