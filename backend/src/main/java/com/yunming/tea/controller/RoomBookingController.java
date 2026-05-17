package com.yunming.tea.controller; // 包声明：控制器层

import com.yunming.tea.common.Result; // 统一响应结果封装类
import com.yunming.tea.dto.RoomBookingDTO; // 茶室预约请求的数据传输对象
import com.yunming.tea.service.RoomBookingService; // 茶室预约业务逻辑服务
import com.yunming.tea.vo.RoomBookingVO; // 茶室预约记录视图对象
import org.springframework.web.bind.annotation.*; // Spring MVC 注解：包括 RestController、RequestMapping、PostMapping 等

import javax.validation.Valid; // JSR-303 Bean 校验注解，用于验证请求体参数
import java.util.List; // Java 集合框架的 List 接口

/**
 * 茶室预约控制器（面向普通用户）
 *
 * 负责处理用户对茶室的预约相关操作，包括：
 * - 预约茶室
 * - 查看我的预约记录
 * - 取消预约
 *
 * 所有操作都需要用户登录后才能执行。
 *
 * 映射路径：/api/room-bookings
 */
@RestController // 标识这是一个 RESTful 控制器，所有返回值自动序列化为 JSON
@RequestMapping("/api/room-bookings") // 将控制器映射到 /api/room-bookings 路径下
public class RoomBookingController {

    private final RoomBookingService bookingService; // 茶室预约业务服务，使用构造器注入

    /**
     * 构造器注入 RoomBookingService
     *
     * @param bookingService 茶室预约业务服务实例
     */
    public RoomBookingController(RoomBookingService bookingService) { // 通过构造器注入依赖
        this.bookingService = bookingService; // 将注入的服务赋值给成员变量
    }

    /**
     * 预约茶室
     *
     * 用户选择茶室、预约日期和时段后提交预约申请。系统会校验该时段
     * 茶室是否已被占用，以及用户是否有预约资格。
     *
     * 请求方式：POST /api/room-bookings
     *
     * @param dto    茶室预约数据传输对象，包含茶室 ID、预约日期、时段等信息，由 @Valid 校验
     * @param userId 当前登录用户的 ID，从请求属性中获取（必填）
     * @return Result 包含预约成功提示和新创建预约记录的 ID 的统一响应结果
     */
    @PostMapping // 处理 HTTP POST 请求，映射到 /api/room-bookings
    public Result<Long> book(@Valid @RequestBody RoomBookingDTO dto, // @Valid 校验请求体，@RequestBody 将 JSON 反序列化为 RoomBookingDTO
                              @RequestAttribute("userId") Long userId) { // @RequestAttribute 从请求属性中获取 userId（必填）
        return Result.success("预约成功", bookingService.book(userId, dto)); // 调用业务层创建预约并返回预约成功响应和预约 ID
    }

    /**
     * 查看我的预约记录
     *
     * 获取当前登录用户的所有茶室预约记录列表。
     *
     * 请求方式：GET /api/room-bookings
     *
     * @param userId 当前登录用户的 ID，从请求属性中获取（必填）
     * @return Result 包含用户预约记录列表的统一响应结果
     */
    @GetMapping // 处理 HTTP GET 请求，映射到 /api/room-bookings
    public Result<List<RoomBookingVO>> myBookings(@RequestAttribute("userId") Long userId) { // @RequestAttribute 从请求属性中获取 userId（必填）
        return Result.success(bookingService.myBookings(userId)); // 调用业务层获取当前用户的预约列表并包装为成功响应
    }

    /**
     * 取消预约
     *
     * 用户取消指定的茶室预约。只能取消属于自己的预约记录，
     * 由业务层验证预约记录归属。通常只能取消未开始的预约。
     *
     * 请求方式：PUT /api/room-bookings/{id}/cancel
     *
     * @param id     预约记录的 ID，从 URL 路径中获取
     * @param userId 当前登录用户的 ID，从请求属性中获取（必填）
     * @return Result 包含预约已取消提示的统一响应结果
     */
    @PutMapping("/{id}/cancel") // 处理 HTTP PUT 请求，映射到 /api/room-bookings/{id}/cancel
    public Result<String> cancel(@PathVariable Long id, // @PathVariable 将 URL 中的 {id} 绑定到方法参数
                                  @RequestAttribute("userId") Long userId) { // @RequestAttribute 从请求属性中获取 userId（必填）
        bookingService.cancel(id, userId); // 调用业务层取消预约
        return Result.success("预约已取消"); // 返回预约已取消的提示信息
    }
}
