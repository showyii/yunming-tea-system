package com.yunming.tea.service; // 声明包路径，属于服务层接口包

import com.yunming.tea.vo.ActivityVO; // 导入活动视图对象，用于返回给前端的数据结构

import java.util.List; // 导入List集合类，用于返回活动列表

/**
 * 茶文化活动服务接口
 * <p>
 * 该接口定义了与茶文化活动相关的业务操作规范，包括：
 * 1. 获取活动列表（支持用户登录状态下的报名状态查询）
 * 2. 获取活动详情（支持用户登录状态下的报名状态查询）
 * <p>
 * 活动是茶文化体验的重要组成部分，用户可以查看活动信息并报名参加。
 * 实现类需要根据用户ID来判断当前用户是否已经报名了某个活动。
 *
 * @author yunming
 * @see com.yunming.tea.service.impl.TeaActivityServiceImpl
 */
public interface TeaActivityService {

    /**
     * 获取活动列表
     * <p>
     * 查询当前状态为"报名中"的所有活动，按照活动开始时间升序排列。
     * 如果传入了用户ID，会同时查询该用户对每个活动的报名状态。
     *
     * @param userId 当前登录用户的ID，可为null（表示未登录状态）
     * @return 活动视图对象列表，包含活动基本信息和用户报名状态
     */
    List<ActivityVO> list(Long userId); // 获取活动列表方法声明

    /**
     * 获取活动详情
     * <p>
     * 根据活动ID查询单个活动的详细信息。
     * 如果传入了用户ID，会同时查询该用户对该活动的报名状态。
     *
     * @param activityId 活动ID，用于唯一标识一个活动
     * @param userId 当前登录用户的ID，可为null（表示未登录状态）
     * @return 活动详情视图对象，如果活动不存在则返回null
     */
    ActivityVO getDetail(Long activityId, Long userId); // 获取活动详情方法声明
}
