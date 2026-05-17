package com.yunming.tea.controller; // 包声明：控制器层

import com.yunming.tea.common.Result; // 统一响应结果封装类
import com.yunming.tea.entity.Room; // 茶室实体类
import com.yunming.tea.service.RoomService; // 茶室业务逻辑服务
import org.springframework.web.bind.annotation.*; // Spring MVC 注解：包括 RestController、RequestMapping、GetMapping

import java.util.List; // Java 集合框架的 List 接口

/**
 * 茶室控制器（面向普通用户）
 *
 * 负责处理茶室信息的查询请求，包括：
 * - 获取可用茶室列表
 * - 查看茶室详情
 *
 * 茶室的增删改操作在管理后台 AdminRoomController 中处理。
 *
 * 映射路径：/api/rooms
 */
@RestController // 标识这是一个 RESTful 控制器，所有返回值自动序列化为 JSON
@RequestMapping("/api/rooms") // 将控制器映射到 /api/rooms 路径下
public class RoomController {

    private final RoomService roomService; // 茶室业务服务，使用构造器注入

    /**
     * 构造器注入 RoomService
     *
     * @param roomService 茶室业务服务实例
     */
    public RoomController(RoomService roomService) { // 通过构造器注入依赖
        this.roomService = roomService; // 将注入的服务赋值给成员变量
    }

    /**
     * 获取可用茶室列表
     *
     * 返回当前所有可用的茶室（即状态为开放的茶室），用于用户端展示。
     * 每个茶室信息包含编号、名称、容量、设施描述、图片等。
     *
     * 请求方式：GET /api/rooms
     *
     * @return Result 包含可用茶室列表的统一响应结果
     */
    @GetMapping // 处理 HTTP GET 请求，映射到 /api/rooms
    public Result<List<Room>> list() { // 获取茶室列表的方法
        return Result.success(roomService.listAvailable()); // 调用业务层获取可用茶室列表并包装为成功响应
    }

    /**
     * 查看茶室详情
     *
     * 根据茶室 ID 获取单个茶室的详细信息。
     *
     * 请求方式：GET /api/rooms/{id}
     *
     * @param id 茶室 ID，从 URL 路径中获取
     * @return Result 包含茶室详细信息的统一响应结果
     */
    @GetMapping("/{id}") // 处理 HTTP GET 请求，映射到 /api/rooms/{id}
    public Result<Room> detail(@PathVariable Long id) { // @PathVariable 将 URL 中的 {id} 绑定到方法参数
        return Result.success(roomService.getById(id)); // 调用业务层根据 ID 获取茶室信息并包装为成功响应
    }
}
