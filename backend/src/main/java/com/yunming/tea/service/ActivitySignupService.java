package com.yunming.tea.service; // 声明包路径，属于服务层接口包

import com.yunming.tea.vo.ActivitySignupVO; // 导入活动报名视图对象，用于返回给前端的报名数据结构

import java.util.List; // 导入List集合类，用于返回报名记录列表

/**
 * 活动报名服务接口
 * <p>
 * 该接口定义了与活动报名相关的业务操作规范，包括：
 * 1. 用户报名参加茶文化活动
 * 2. 查看用户的报名记录列表
 * 3. 取消已报名的活动
 * 4. 管理员查看某个活动的所有报名记录
 * <p>
 * 报名流程包含名额检查、重复报名检查等业务校验逻辑，
 * 确保活动的参与人数在设定的上限之内。
 *
 * @author yunming
 * @see com.yunming.tea.service.impl.ActivitySignupServiceImpl
 */
public interface ActivitySignupService {

    /**
     * 用户报名参加活动
     * <p>
     * 执行报名操作前需检查：
     * 1. 活动是否存在且处于"报名中"状态
     * 2. 用户是否已经报名过该活动（防止重复报名）
     * 3. 活动是否还有剩余名额
     * <p>
     * 报名成功后，活动的当前参与人数会加1。
     *
     * @param userId 报名用户的ID，用于关联报名用户
     * @param activityId 要报名的活动ID，用于关联目标活动
     */
    void signup(Long userId, Long activityId); // 用户报名参加活动方法声明

    /**
     * 查询当前用户的报名记录
     * <p>
     * 根据用户ID查询该用户的所有报名记录（排除已取消的），
     * 按创建时间倒序排列，并附带对应活动的标题、封面、开始时间等信息。
     *
     * @param userId 查询的用户ID
     * @return 报名记录视图对象列表，每条记录包含活动和报名状态信息
     */
    List<ActivitySignupVO> mySignups(Long userId); // 查询用户报名记录方法声明

    /**
     * 取消活动报名
     * <p>
     * 用户取消已报名的活动，需要校验：
     * 1. 报名记录是否存在
     * 2. 是否为当前用户本人的报名记录
     * 3. 报名状态是否允许取消（只有"已报名"状态可以取消）
     * <p>
     * 取消成功后，活动的当前参与人数会减1。
     *
     * @param signupId 报名记录ID，用于定位要取消的报名
     * @param userId 操作用户的ID，用于权限校验
     */
    void cancel(Long signupId, Long userId); // 取消报名方法声明

    /**
     * 管理员查看某个活动的所有报名记录
     * <p>
     * 管理员可以根据活动ID查询该活动的全部报名列表，
     * 按创建时间倒序排列，用于活动管理和签到管理。
     *
     * @param activityId 活动ID，用于筛选报名记录
     * @return 报名记录视图对象列表，包含用户信息和报名状态
     */
    List<ActivitySignupVO> adminList(Long activityId); // 管理员查询报名列表方法声明
}
