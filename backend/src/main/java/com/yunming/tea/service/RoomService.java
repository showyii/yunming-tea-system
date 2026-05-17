package com.yunming.tea.service; // 声明包路径，属于服务层接口包

import com.yunming.tea.entity.Room; // 导入包间实体类，对应数据库中的包间表

import java.util.List; // 导入List集合类，用于返回包间列表

/**
 * 包间服务接口
 * <p>
 * 该接口定义了茶馆包间相关的业务操作规范，包括：
 * 1. 获取所有可用的包间列表
 * 2. 根据ID获取包间详情
 * <p>
 * 包间是茶馆提供的独立品茶空间，用户可以在线预约使用。
 * 包间有状态的区分：可用(status=1)和维护中(status=0)，
 * 只有可用状态的包间才能被用户预约。
 *
 * @author yunming
 * @see com.yunming.tea.service.impl.RoomServiceImpl
 */
public interface RoomService {

    /**
     * 获取所有可用的包间列表
     * <p>
     * 查询所有状态为"可用"（status=1）的包间，
     * 用于前端包间列表展示和预约选择。
     * 处于维护中状态的包间不会出现在列表中。
     *
     * @return 可用包间实体对象列表
     */
    List<Room> listAvailable(); // 获取可用包间列表方法声明

    /**
     * 根据ID获取包间详情
     * <p>
     * 根据包间ID查询单个包间的完整信息，
     * 包括名称、图片、价格、容量、描述等所有字段。
     *
     * @param id 包间ID
     * @return 包间实体对象，如果包间不存在则返回null
     */
    Room getById(Long id); // 根据ID获取包间方法声明
}
