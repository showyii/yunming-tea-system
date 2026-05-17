package com.yunming.tea.service.impl; // 声明包路径，属于服务实现层包

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper; // 导入MyBatis-Plus的Lambda查询包装器
import com.yunming.tea.entity.ActivitySignup; // 导入活动报名实体类
import com.yunming.tea.entity.TeaActivity; // 导入茶文化活动实体类
import com.yunming.tea.mapper.ActivitySignupMapper; // 导入活动报名数据访问层
import com.yunming.tea.mapper.TeaActivityMapper; // 导入活动数据访问层
import com.yunming.tea.service.TeaActivityService; // 导入活动服务接口
import com.yunming.tea.vo.ActivityVO; // 导入活动视图对象
import org.springframework.stereotype.Service; // 导入Spring的Service注解

import java.util.List; // 导入List接口
import java.util.stream.Collectors; // 导入Collectors工具类

/**
 * 茶文化活动服务实现类
 * <p>
 * 实现了{@link TeaActivityService}接口中定义的活动查询业务逻辑。
 * 主要功能包括：
 * <ul>
 *   <li>获取活动列表：查询状态为"报名中"的活动，支持用户登录状态下查询报名状态</li>
 *   <li>获取活动详情：查询单个活动的完整信息，附带用户报名状态</li>
 * </ul>
 * <p>
 * 活动状态说明：
 * <ul>
 *   <li>0 - 已取消：活动被取消</li>
 *   <li>1 - 报名中：活动正在进行报名</li>
 *   <li>2 - 进行中：活动正在进行</li>
 *   <li>3 - 已结束：活动已结束</li>
 * </ul>
 * <p>
 * 当用户已登录时（userId不为null），会自动查询该用户对每个活动的报名状态，
 * 前端可根据isSignedUp字段显示"立即报名"或"已报名"按钮。
 *
 * @author yunming
 * @see TeaActivityService
 */
@Service // 将该类标记为Spring的Service组件
public class TeaActivityServiceImpl implements TeaActivityService {

    private final TeaActivityMapper activityMapper; // 活动数据访问对象
    private final ActivitySignupMapper signupMapper; // 活动报名数据访问对象，用于查询用户报名状态

    /**
     * 构造函数注入依赖
     *
     * @param activityMapper 活动数据访问对象
     * @param signupMapper 活动报名数据访问对象
     */
    public TeaActivityServiceImpl(TeaActivityMapper activityMapper, ActivitySignupMapper signupMapper) {
        this.activityMapper = activityMapper; // 注入活动Mapper
        this.signupMapper = signupMapper; // 注入报名Mapper
    }

    /**
     * 获取活动列表
     * <p>
     * 查询所有状态为"报名中"（status=1）的活动，按开始时间升序排列。
     * 如果传入了用户ID，还会查询该用户对每个活动的报名状态。
     *
     * @param userId 当前用户ID（可为null，未登录时不需要查询报名状态）
     * @return 活动视图对象列表，包含活动基本信息和用户报名状态
     */
    @Override // 标识该方法覆盖了父接口中的方法
    public List<ActivityVO> list(Long userId) {
        // 构建查询条件：只查询状态为"报名中"(1)的活动
        LambdaQueryWrapper<TeaActivity> wrapper = new LambdaQueryWrapper<>(); // 创建查询包装器
        wrapper.eq(TeaActivity::getStatus, 1) // 条件：状态为"报名中"(1)
                .orderByAsc(TeaActivity::getStartTime); // 排序：按开始时间升序（即将开始的活动在前）
        // 查询活动列表，并通过Stream映射为包含用户报名状态的视图对象
        return activityMapper.selectList(wrapper).stream() // 执行查询获取流
                .map(a -> toVO(a, userId)) // 将每个活动实体映射为视图对象（含报名状态）
                .collect(Collectors.toList()); // 收集为List
    }

    /**
     * 获取活动详情
     * <p>
     * 根据活动ID查询单个活动的完整信息。不限制活动状态，
     * 可以查看任意状态的活动。如果传入了用户ID，还会查询报名状态。
     *
     * @param activityId 活动ID
     * @param userId 当前用户ID（可为null）
     * @return 活动详情视图对象，如果活动不存在则返回null
     */
    @Override // 标识该方法覆盖了父接口中的方法
    public ActivityVO getDetail(Long activityId, Long userId) {
        // 根据活动ID查询活动
        TeaActivity activity = activityMapper.selectById(activityId); // 根据主键查询活动
        if (activity == null) return null; // 如果活动不存在，返回null
        return toVO(activity, userId); // 转换为视图对象（含报名状态）并返回
    }

    /**
     * 将活动实体转换为视图对象
     * <p>
     * 转换活动基本信息，并可选地查询当前用户对该活动的报名状态。
     * 报名状态查询条件：同一用户、同一活动、状态为"已报名"(0)。
     *
     * @param a 活动实体对象
     * @param userId 当前用户ID（可为null）
     * @return 活动视图对象
     */
    private ActivityVO toVO(TeaActivity a, Long userId) { // 私有方法，实体转视图
        ActivityVO vo = new ActivityVO(); // 创建活动视图对象
        vo.setId(a.getId()); // 设置活动ID
        vo.setTitle(a.getTitle()); // 设置活动标题
        vo.setDescription(a.getDescription()); // 设置活动详细描述
        vo.setCoverImage(a.getCoverImage()); // 设置活动封面图片URL
        vo.setLocation(a.getLocation()); // 设置活动举办地点
        vo.setStartTime(a.getStartTime()); // 设置活动开始时间
        vo.setEndTime(a.getEndTime()); // 设置活动结束时间
        vo.setMaxParticipants(a.getMaxParticipants()); // 设置最大参与人数
        vo.setCurrentParticipants(a.getCurrentParticipants()); // 设置当前已报名人数
        vo.setFee(a.getFee()); // 设置活动费用（元）
        vo.setStatus(a.getStatus()); // 设置活动状态
        vo.setStatusText(getStatusText(a.getStatus())); // 将状态码转为中文文本
        vo.setCreateTime(a.getCreateTime()); // 设置活动创建时间

        // 如果用户已登录，查询该用户对该活动的报名状态
        if (userId != null) { // 用户ID不为null（用户已登录）
            LambdaQueryWrapper<ActivitySignup> sw = new LambdaQueryWrapper<>(); // 创建报名查询包装器
            sw.eq(ActivitySignup::getActivityId, a.getId()) // 条件：活动ID匹配
                    .eq(ActivitySignup::getUserId, userId) // 条件：用户ID匹配
                    .eq(ActivitySignup::getStatus, 0); // 条件：报名状态为"已报名"(0)
            vo.setIsSignedUp(signupMapper.selectCount(sw) > 0); // 如果存在"已报名"记录，设置isSignedUp为true
        }

        return vo; // 返回活动视图对象
    }

    /**
     * 将活动状态码转换为中文文本
     *
     * @param s 活动状态码：0-已取消, 1-报名中, 2-进行中, 3-已结束
     * @return 对应的中文状态文本
     */
    private String getStatusText(Integer s) { // 私有方法，状态码转中文
        switch (s) { // 根据状态码匹配
            case 0: return "已取消"; // 状态0：活动被取消
            case 1: return "报名中"; // 状态1：活动正在接受报名
            case 2: return "进行中"; // 状态2：活动正在进行
            case 3: return "已结束"; // 状态3：活动已结束
            default: return "未知"; // 未知状态码
        }
    }
}
