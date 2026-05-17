package com.yunming.tea.service.impl; // 声明包路径，属于服务实现层包

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper; // 导入MyBatis-Plus的Lambda查询包装器
import com.yunming.tea.entity.Room; // 导入包间实体类，对应room表
import com.yunming.tea.mapper.RoomMapper; // 导入包间数据访问层
import com.yunming.tea.service.RoomService; // 导入包间服务接口
import org.springframework.stereotype.Service; // 导入Spring的Service注解

import java.util.List; // 导入List接口

/**
 * 包间服务实现类
 * <p>
 * 实现了{@link RoomService}接口中定义的包间查询业务逻辑。
 * 主要功能包括：
 * <ul>
 *   <li>获取所有可用包间列表：只返回状态为"可用"的包间，供用户预约选择</li>
 *   <li>获取包间详情：根据ID查询单个包间的完整信息</li>
 * </ul>
 * <p>
 * 包间状态说明：
 * <ul>
 *   <li>status=1：可用，用户可以预约</li>
 *   <li>status=0：维护中，暂时无法预约</li>
 * </ul>
 *
 * @author yunming
 * @see RoomService
 */
@Service // 将该类标记为Spring的Service组件
public class RoomServiceImpl implements RoomService {

    private final RoomMapper roomMapper; // 包间数据访问对象

    /**
     * 构造函数注入依赖
     *
     * @param roomMapper 包间数据访问对象
     */
    public RoomServiceImpl(RoomMapper roomMapper) {
        this.roomMapper = roomMapper; // 注入包间Mapper
    }

    /**
     * 获取所有可用的包间列表
     * <p>
     * 查询所有状态为"可用"（status=1）的包间。
     * 处于维护中的包间（status=0）不会出现在列表中。
     * 结果用于前端包间展示页面和预约表单的包间选择下拉框。
     *
     * @return 可用包间实体对象列表
     */
    @Override // 标识该方法覆盖了父接口中的方法
    public List<Room> listAvailable() {
        // 构建查询条件：只查询状态为1（可用）的包间
        LambdaQueryWrapper<Room> wrapper = new LambdaQueryWrapper<>(); // 创建查询包装器
        wrapper.eq(Room::getStatus, 1); // 条件：状态等于1（可用）
        return roomMapper.selectList(wrapper); // 执行查询并返回可用包间列表
    }

    /**
     * 根据ID获取包间详情
     * <p>
     * 根据包间ID查询单个包间的完整信息，不限制状态。
     * 直接使用MyBatis-Plus的selectById方法进行主键查询。
     *
     * @param id 包间ID
     * @return 包间实体对象，如果不存在则返回null
     */
    @Override // 标识该方法覆盖了父接口中的方法
    public Room getById(Long id) {
        return roomMapper.selectById(id); // 根据主键ID查询包间，返回实体对象或null
    }
}
