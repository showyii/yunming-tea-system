package com.yunming.tea.controller.admin; // 包声明：管理后台控制器层

import com.yunming.tea.common.Result; // 统一响应结果封装类
import com.yunming.tea.entity.Room; // 茶室实体类
import com.yunming.tea.mapper.RoomMapper; // 茶室数据访问层（MyBatis-Plus Mapper）
import com.yunming.tea.service.RoomService; // 茶室业务逻辑服务
import org.springframework.web.bind.annotation.*; // Spring MVC 注解：包括 RestController、RequestMapping、GetMapping 等

import java.util.List; // Java 集合框架的 List 接口

/**
 * 管理员 - 茶室管理控制器
 *
 * 负责管理后台对茶室的全面管理功能，包括：
 * - 查看所有茶室列表
 * - 查看单个茶室详情
 * - 创建新茶室
 * - 编辑茶室信息
 * - 删除茶室
 *
 * 增删改操作仅限管理员使用，用户端只能查看茶室信息。
 *
 * 映射路径：/api/admin/rooms
 */
@RestController // 标识这是一个 RESTful 控制器，所有返回值自动序列化为 JSON
@RequestMapping("/api/admin/rooms") // 将控制器映射到 /api/admin/rooms 路径下
public class AdminRoomController {

    private final RoomService roomService; // 茶室业务服务，用于查询操作
    private final RoomMapper roomMapper; // 茶室 Mapper，用于直接的增删改操作

    /**
     * 构造器注入 RoomService 和 RoomMapper
     *
     * @param roomService 茶室业务服务实例，用于列表和详情的查询
     * @param roomMapper  茶室数据访问层实例，用于创建、更新和删除操作
     */
    public AdminRoomController(RoomService roomService, RoomMapper roomMapper) { // 通过构造器注入两个依赖
        this.roomService = roomService; // 将注入的茶室服务赋值给成员变量
        this.roomMapper = roomMapper; // 将注入的 Mapper 赋值给成员变量
    }

    /**
     * 获取所有茶室列表（管理员视图）
     *
     * 返回系统中所有茶室的信息，包括可用的和不可用的。
     *
     * 请求方式：GET /api/admin/rooms
     *
     * @return Result 包含所有茶室列表的统一响应结果
     */
    @GetMapping // 处理 HTTP GET 请求，映射到 /api/admin/rooms
    public Result<List<Room>> list() { // 获取茶室列表的方法
        return Result.success(roomService.listAvailable()); // 调用业务层获取所有茶室并包装为成功响应
    }

    /**
     * 查看单个茶室详情
     *
     * 根据茶室 ID 获取该茶室的详细信息。
     *
     * 请求方式：GET /api/admin/rooms/{id}
     *
     * @param id 茶室 ID，从 URL 路径中获取
     * @return Result 包含茶室详细信息的统一响应结果
     */
    @GetMapping("/{id}") // 处理 HTTP GET 请求，映射到 /api/admin/rooms/{id}
    public Result<Room> detail(@PathVariable Long id) { // @PathVariable 将 URL 中的 {id} 绑定到方法参数
        return Result.success(roomService.getById(id)); // 调用业务层根据 ID 获取茶室信息并包装为成功响应
    }

    /**
     * 创建新茶室
     *
     * 在系统中添加一个新的茶室。接收茶室的完整信息（名称、容量、
     * 设施描述、图片、价格等），保存到数据库。
     *
     * 请求方式：POST /api/admin/rooms
     *
     * @param room 茶室实体对象，由请求体 JSON 自动反序列化
     * @return Result 包含新创建的茶室实体（含自动生成的 ID）的统一响应结果
     */
    @PostMapping // 处理 HTTP POST 请求，映射到 /api/admin/rooms
    public Result<Room> create(@RequestBody Room room) { // @RequestBody 将请求体 JSON 反序列化为 Room 实体
        roomMapper.insert(room); // 使用 Mapper 直接将茶室实体插入数据库
        return Result.success(room); // 返回新创建的茶室实体（此时 ID 已由数据库自动填充）
    }

    /**
     * 编辑茶室信息
     *
     * 更新指定茶室的信息，包括名称、容量、设施、图片、价格等。
     *
     * 请求方式：PUT /api/admin/rooms/{id}
     *
     * @param id   茶室 ID，从 URL 路径中获取，标识要修改的茶室
     * @param room 茶室实体对象，包含要更新的字段，由请求体 JSON 自动反序列化
     * @return Result 包含更新成功提示的统一响应结果
     */
    @PutMapping("/{id}") // 处理 HTTP PUT 请求，映射到 /api/admin/rooms/{id}
    public Result<String> update(@PathVariable Long id, @RequestBody Room room) { // @PathVariable 从 URL 获取 ID，@RequestBody 反序列化请求体
        room.setId(id); // 将路径参数中的 ID 设置到茶室实体上，确保更新正确的记录
        roomMapper.updateById(room); // 使用 Mapper 根据 ID 更新茶室记录
        return Result.success("更新成功"); // 返回更新成功的提示信息
    }

    /**
     * 删除茶室
     *
     * 根据茶室 ID 从数据库中删除指定的茶室。删除前应注意：
     * - 该茶室是否有未完成的预约记录
     * - 删除可能导致已有预约记录出现数据孤立问题
     *
     * 请求方式：DELETE /api/admin/rooms/{id}
     *
     * @param id 茶室 ID，从 URL 路径中获取，标识要删除的茶室
     * @return Result 包含删除成功提示的统一响应结果
     */
    @DeleteMapping("/{id}") // 处理 HTTP DELETE 请求，映射到 /api/admin/rooms/{id}
    public Result<String> delete(@PathVariable Long id) { // @PathVariable 将 URL 中的 {id} 绑定到方法参数
        roomMapper.deleteById(id); // 使用 Mapper 根据 ID 删除茶室记录
        return Result.success("删除成功"); // 返回删除成功的提示信息
    }
}
