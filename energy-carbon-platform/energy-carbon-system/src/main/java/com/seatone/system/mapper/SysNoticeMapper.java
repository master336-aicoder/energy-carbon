package com.seatone.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.seatone.system.entity.SysNotice;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 通知公告 Mapper
 *
 * @author seatone
 * @since 2026-06-27
 */
@Mapper
public interface SysNoticeMapper extends BaseMapper<SysNotice> {

    /**
     * 查询启用的公告列表
     *
     * @return 公告列表
     */
    List<SysNotice> selectEnabledList();
}
