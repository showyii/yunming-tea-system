package com.yunming.tea.controller.admin; // 包声明：管理后台控制器层

import com.yunming.tea.common.Result; // 统一响应结果封装类
import com.yunming.tea.entity.RoomBooking; // 茶室预约实体类
import com.yunming.tea.mapper.RoomBookingMapper; // 茶室预约数据访问层（MyBatis-Plus Mapper）
import com.yunming.tea.service.RoomBookingService; // 茶室预约业务逻辑服务
import com.yunming.tea.vo.RoomBookingVO; // 茶室预约记录视图对象
import org.springframework.web.bind.annotation.*; // Spring MVC 注解：包括 RestController、RequestMapping、GetMapping 等

import java.util.List; // Java 集合框架的 List 接口

/**
 * 管理员 - 茶室预约管理控制器
 *
 * 负责管理后台对茶室预约的管理功能，包括：
 * - 查看所有预约记录
 * - 修改预约状态（如确认预约、取消预约等）
 *
 * 管理员可以查看和处理所有用户的茶室预约。
 *
 * 映射路径：/api/admin/room-bookings
 */
@RestController // 标识这是一个 RESTful 控制器，所有返回值自动序列化为 JSON
@RequestMapping("/api/admin/room-bookings") // 将控制器映射到 /api/admin/room-bookings 路径下
public class AdminRoomBookingController {

    private final RoomBookingService bookingService; // 茶室预约业务服务，用于查询操作
    private final RoomBookingMapper bookingMapper; // 茶室预约 Mapper，用于直接的状态更新操作

    /**
     * 构造器注入 RoomBookingService 和 RoomBookingMapper
     *
     * @param bookingService 茶室预约业务服务实例，用于列表查询
     * @param bookingMapper  茶室预约数据访问层实例，用于状态更新操作
     */
    public AdminRoomBookingController(RoomBookingService bookingService, RoomBookingMapper bookingMapper) { // 通过构造器注入两个依赖
        this.bookingService = bookingService; // 将注入的预约服务赋值给成员变量
        this.bookingMapper = bookingMapper; // 将注入的 Mapper 赋值给成员变量
    }

    /**
     * 获取所有预约记录（管理员视图）
     *
     * 管理员查看系统中所有的茶室预约记录，包括所有用户的预约信息。
     * 区别于用户端只能查看自己的预约记录。
     *
     * 请求方式：GET /api/admin/room-bookings
     *
     * @return Result 包含所有预约记录列表的统一响应结果
     */
    @GetMapping // 处理 HTTP GET 请求，映射到 /api/admin/room-bookings
    public Result<List<RoomBookingVO>> list() { // 获取全部预约记录的方法
        return Result.success(bookingService.adminList()); // 调用业务层获取所有用户的预约列表并包装为成功响应
    }

    /**
     * 修改预约状态（管理员操作）
     *
     * 管理员可以手动修改茶室预约的状态，用于确认预约、拒绝预约或
     * 标记已完成等操作。
     *
     * 请求方式：PUT /api/admin/room-bookings/{id}/status
     * 请求体示例：{"status": 1}  // 状态值根据业务定义
     *
     * @param id   预约记录 ID，从 URL 路径中获取
     * @param body 请求体 Map，包含要更新的状态值（key 为 "status"）
     * @return Result 包含状态已更新提示的统一响应结果
     */
    @PutMapping("/{id}/status") // 处理 HTTP PUT 请求，映射到 /api/admin/room-bookings/{id}/status
    public Result<String> updateStatus(@PathVariable Long id, @RequestBody java.util.Map<String, Integer> body) { // @PathVariable 获取预约 ID，@RequestBody 获取状态参数
        RoomBooking booking = bookingMapper.selectById(id); // 使用 Mapper 根据 ID 查询预约记录
        if (booking != null) { // 判断预约记录是否存在
            booking.setStatus(body.get("status")); // 从请求体中获取新状态值并设置到预约实体上
            bookingMapper.updateById(booking); // 使用 Mapper 根据 ID 更新预约记录的状态
        }
        return Result.success("状态已更新"); // 返回状态已更新的提示信息
    }
}
