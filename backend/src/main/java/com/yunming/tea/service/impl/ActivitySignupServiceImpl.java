package com.yunming.tea.service.impl; // 声明包路径，属于服务实现层包

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper; // 导入MyBatis-Plus的Lambda查询包装器，用于构建类型安全的查询条件
import com.yunming.tea.entity.ActivitySignup; // 导入活动报名实体类，对应activity_signup表
import com.yunming.tea.entity.TeaActivity; // 导入茶文化活动实体类，对应tea_activity表
import com.yunming.tea.exception.BusinessException; // 导入业务异常类，用于抛出业务逻辑相关的异常
import com.yunming.tea.mapper.ActivitySignupMapper; // 导入活动报名数据访问层，操作用户报名数据
import com.yunming.tea.mapper.TeaActivityMapper; // 导入活动数据访问层，操作活动数据
import com.yunming.tea.service.ActivitySignupService; // 导入活动报名服务接口
import com.yunming.tea.vo.ActivitySignupVO; // 导入活动报名视图对象，用于返回前端数据
import org.springframework.stereotype.Service; // 导入Spring的Service注解，将类标记为Spring管理的服务组件
import org.springframework.transaction.annotation.Transactional; // 导入Spring事务注解，用于声明事务边界

import java.util.ArrayList; // 导入ArrayList集合类，用于构建可变列表
import java.util.List; // 导入List接口，用于声明集合类型

/**
 * 活动报名服务实现类
 * <p>
 * 实现了{@link ActivitySignupService}接口中定义的活动报名相关业务逻辑。
 * 主要功能包括：
 * <ul>
 *   <li>用户报名参加茶文化活动：包含活动状态校验、重复报名检查、名额上限检查</li>
 *   <li>查询用户报名记录：返回用户的报名历史及状态</li>
 *   <li>取消活动报名：校验权限和状态后取消报名并恢复名额</li>
 *   <li>管理员查看报名列表：按活动ID查询所有报名记录</li>
 * </ul>
 * <p>
 * 报名状态说明：
 * <ul>
 *   <li>0 - 已报名：用户成功报名，等待活动开始</li>
 *   <li>1 - 已签到：用户已参加活动并完成签到</li>
 *   <li>2 - 已取消：用户取消了报名</li>
 * </ul>
 *
 * @author yunming
 * @see ActivitySignupService
 */
@Service // 将该类标记为Spring的Service组件，由Spring容器管理生命周期
public class ActivitySignupServiceImpl implements ActivitySignupService {

    private final ActivitySignupMapper signupMapper; // 活动报名数据访问对象，用于操作报名数据
    private final TeaActivityMapper activityMapper; // 活动数据访问对象，用于操作活动数据

    /**
     * 构造函数注入依赖
     * <p>
     * 通过Spring的构造器注入方式获取ActivitySignupMapper和TeaActivityMapper实例。
     *
     * @param signupMapper 活动报名数据访问对象
     * @param activityMapper 活动数据访问对象
     */
    public ActivitySignupServiceImpl(ActivitySignupMapper signupMapper, TeaActivityMapper activityMapper) {
        this.signupMapper = signupMapper; // 注入活动报名Mapper
        this.activityMapper = activityMapper; // 注入活动Mapper
    }

    /**
     * 用户报名参加活动
     * <p>
     * 报名流程：
     * <ol>
     *   <li>查询活动信息，校验活动是否存在且状态为"报名中"(status=1)</li>
     *   <li>检查用户是否已报名同一活动（防止重复报名）</li>
     *   <li>检查活动是否还有剩余名额（currentParticipants < maxParticipants）</li>
     *   <li>创建报名记录，状态设为"已报名"(status=0)</li>
     *   <li>更新活动的当前参与人数（+1）</li>
     * </ol>
     * 整个操作在一个事务中执行，确保数据一致性。
     *
     * @param userId 报名用户ID
     * @param activityId 要报名的活动ID
     * @throws BusinessException 活动不存在或不在报名中、已报名、名额已满时抛出异常
     */
    @Override // 标识该方法覆盖了父接口中的方法
    @Transactional // 声明该方法在事务中执行，确保报名记录和活动人数更新的原子性
    public void signup(Long userId, Long activityId) {
        // 查询活动信息，根据活动ID获取活动实体的所有字段
        TeaActivity activity = activityMapper.selectById(activityId);
        // 校验活动是否存在且状态为"报名中"（status=1表示报名中）
        if (activity == null || activity.getStatus() != 1) {
            throw new BusinessException(400, "活动不存在或不在报名中"); // 抛出业务异常，HTTP 400
        }

        // 检查重复报名：构建查询条件查找同一用户对同一活动的已报名记录
        LambdaQueryWrapper<ActivitySignup> wrapper = new LambdaQueryWrapper<>(); // 创建Lambda查询包装器
        wrapper.eq(ActivitySignup::getActivityId, activityId) // 条件：活动ID匹配
                .eq(ActivitySignup::getUserId, userId) // 条件：用户ID匹配
                .eq(ActivitySignup::getStatus, 0); // 条件：状态为"已报名"(0)
        if (signupMapper.selectCount(wrapper) > 0) { // 如果已存在报名记录
            throw new BusinessException(400, "您已报名此活动，请勿重复报名"); // 抛出重复报名异常
        }

        // 检查人数上限：判断当前参与人数是否已达到最大参与人数
        if (activity.getCurrentParticipants() >= activity.getMaxParticipants()) {
            throw new BusinessException(400, "活动名额已满"); // 抛出名额已满异常
        }

        // 创建新的报名记录
        ActivitySignup signup = new ActivitySignup(); // 实例化报名记录对象
        signup.setActivityId(activityId); // 设置活动ID
        signup.setUserId(userId); // 设置用户ID
        signup.setStatus(0); // 设置报名状态为"已报名"(0)
        signupMapper.insert(signup); // 将报名记录插入数据库，MyBatis-Plus会自动填充create_time

        // 更新活动的当前参与人数，加1
        activity.setCurrentParticipants(activity.getCurrentParticipants() + 1); // 当前参与人数+1
        activityMapper.updateById(activity); // 根据ID更新活动记录
    }

    /**
     * 查询当前用户的报名记录
     * <p>
     * 根据用户ID查询该用户的所有报名记录（排除已取消状态），
     * 按创建时间倒序排列。每条记录会附带关联的活动信息（标题、封面、开始时间等）。
     *
     * @param userId 查询的用户ID
     * @return 报名记录视图对象列表，包含活动信息和报名状态
     */
    @Override // 标识该方法覆盖了父接口中的方法
    public List<ActivitySignupVO> mySignups(Long userId) {
        // 构建查询条件：查询指定用户的报名记录
        LambdaQueryWrapper<ActivitySignup> wrapper = new LambdaQueryWrapper<>(); // 创建Lambda查询包装器
        wrapper.eq(ActivitySignup::getUserId, userId) // 条件：用户ID匹配
                .ne(ActivitySignup::getStatus, 2) // 条件：状态不等于"已取消"(2)，即排除已取消的记录
                .orderByDesc(ActivitySignup::getCreateTime); // 排序：按创建时间倒序
        List<ActivitySignup> signups = signupMapper.selectList(wrapper); // 执行查询，获取报名记录列表

        List<ActivitySignupVO> result = new ArrayList<>(); // 创建结果列表，用于存放视图对象
        for (ActivitySignup s : signups) { // 遍历每条报名记录
            TeaActivity activity = activityMapper.selectById(s.getActivityId()); // 根据报名中的活动ID查询活动信息
            if (activity == null) continue; // 如果活动已被删除，跳过该条记录
            // 构建视图对象并填充数据
            ActivitySignupVO vo = new ActivitySignupVO(); // 创建视图对象实例
            vo.setId(s.getId()); // 设置报名记录ID
            vo.setActivityId(s.getActivityId()); // 设置活动ID
            vo.setActivityTitle(activity.getTitle()); // 从活动信息中获取活动标题
            vo.setActivityCover(activity.getCoverImage()); // 从活动信息中获取活动封面图片
            vo.setActivityStartTime(activity.getStartTime()); // 从活动信息中获取活动开始时间
            vo.setStatus(s.getStatus()); // 设置报名状态（0:已报名, 1:已签到, 2:已取消）
            vo.setStatusText(getStatusText(s.getStatus())); // 根据状态码获取对应的中文状态文本
            vo.setCreateTime(s.getCreateTime()); // 设置报名创建时间
            result.add(vo); // 将构建好的视图对象添加到结果列表
        }
        return result; // 返回结果列表
    }

    /**
     * 取消活动报名
     * <p>
     * 用户取消已报名的活动。操作流程：
     * <ol>
     *   <li>校验报名记录是否存在</li>
     *   <li>校验是否为当前用户本人的报名记录</li>
     *   <li>校验报名状态是否为"已报名"（只有已报名状态可以取消）</li>
     *   <li>将报名状态更新为"已取消"</li>
     *   <li>恢复活动的当前参与人数（-1，最小为0）</li>
     * </ol>
     *
     * @param signupId 报名记录ID
     * @param userId 操作用户ID，用于权限校验
     * @throws BusinessException 报名不存在、无权操作、状态不允许时抛出异常
     */
    @Override // 标识该方法覆盖了父接口中的方法
    @Transactional // 声明该方法在事务中执行，确保状态更新和人数恢复的原子性
    public void cancel(Long signupId, Long userId) {
        // 根据报名ID查询报名记录
        ActivitySignup signup = signupMapper.selectById(signupId);
        // 校验报名记录是否存在
        if (signup == null) {
            throw new BusinessException(404, "报名记录不存在"); // 抛出404异常
        }
        // 校验是否为当前用户本人的报名记录
        if (!signup.getUserId().equals(userId)) {
            throw new BusinessException(403, "无权操作"); // 抛出403无权限异常
        }
        // 校验报名状态是否允许取消（只有"已报名"状态可以取消）
        if (signup.getStatus() != 0) {
            throw new BusinessException(400, "当前状态不允许取消报名"); // 抛出状态不允许异常
        }

        // 将报名状态设置为"已取消"(2)
        signup.setStatus(2);
        signupMapper.updateById(signup); // 根据ID更新报名记录

        // 查询关联的活动信息，用于恢复参与人数
        TeaActivity activity = activityMapper.selectById(signup.getActivityId());
        if (activity != null) { // 如果活动还存在
            // 当前参与人数减1，使用Math.max确保不会出现负数
            activity.setCurrentParticipants(Math.max(0, activity.getCurrentParticipants() - 1));
            activityMapper.updateById(activity); // 根据ID更新活动记录
        }
    }

    /**
     * 管理员查看某个活动的所有报名记录
     * <p>
     * 根据活动ID查询该活动的所有报名列表（不排除任何状态），
     * 按创建时间倒序排列。用于后台的活动管理和签到管理。
     *
     * @param activityId 活动ID，用于筛选报名记录
     * @return 报名记录视图对象列表，包含用户信息和报名状态
     */
    @Override // 标识该方法覆盖了父接口中的方法
    public List<ActivitySignupVO> adminList(Long activityId) {
        // 构建查询条件：查询指定活动的所有报名记录
        LambdaQueryWrapper<ActivitySignup> wrapper = new LambdaQueryWrapper<>(); // 创建Lambda查询包装器
        wrapper.eq(ActivitySignup::getActivityId, activityId) // 条件：活动ID匹配
                .orderByDesc(ActivitySignup::getCreateTime); // 排序：按创建时间倒序
        List<ActivitySignup> signups = signupMapper.selectList(wrapper); // 执行查询

        List<ActivitySignupVO> result = new ArrayList<>(); // 创建结果列表
        for (ActivitySignup s : signups) { // 遍历每条报名记录
            TeaActivity activity = activityMapper.selectById(s.getActivityId()); // 查询关联的活动信息
            ActivitySignupVO vo = new ActivitySignupVO(); // 创建视图对象
            vo.setId(s.getId()); // 设置报名记录ID
            vo.setActivityId(s.getActivityId()); // 设置活动ID
            // 如果活动存在则获取标题，否则设为空字符串（防御性编程）
            vo.setActivityTitle(activity != null ? activity.getTitle() : ""); // 安全获取活动标题
            vo.setActivityCover(activity != null ? activity.getCoverImage() : ""); // 安全获取活动封面
            vo.setActivityStartTime(activity != null ? activity.getStartTime() : null); // 安全获取开始时间
            vo.setStatus(s.getStatus()); // 设置报名状态
            vo.setStatusText(getStatusText(s.getStatus())); // 获取状态中文文本
            vo.setCreateTime(s.getCreateTime()); // 设置创建时间
            result.add(vo); // 添加到结果列表
        }
        return result; // 返回结果列表
    }

    /**
     * 根据状态码获取中文状态文本
     * <p>
     * 将数字状态码转换为用户可读的中文描述。
     *
     * @param s 状态码：0-已报名, 1-已签到, 2-已取消, 其他-未知
     * @return 对应的中文状态文本
     */
    private String getStatusText(Integer s) { // 私有方法，仅在类内部使用
        switch (s) { // 根据状态码进行匹配
            case 0: return "已报名"; // 状态0对应"已报名"
            case 1: return "已签到"; // 状态1对应"已签到"
            case 2: return "已取消"; // 状态2对应"已取消"
            default: return "未知"; // 其他未知状态返回"未知"
        }
    }
}
