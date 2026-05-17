package com.yunming.tea.controller.admin; // 包声明：管理后台控制器层

import com.baomidou.mybatisplus.extension.plugins.pagination.Page; // MyBatis-Plus 分页结果对象，包含分页数据和总记录数
import com.yunming.tea.common.Result; // 统一响应结果封装类
import com.yunming.tea.service.OrderService; // 订单业务逻辑服务
import com.yunming.tea.vo.OrderDetailVO; // 订单详情视图对象
import com.yunming.tea.vo.OrderListVO; // 订单列表项视图对象
import org.springframework.web.bind.annotation.*; // Spring MVC 注解：包括 RestController、RequestMapping、GetMapping 等

/**
 * 管理员 - 订单管理控制器
 *
 * 负责管理后台对所有用户订单的管理功能，包括：
 * - 查看所有订单列表（支持按状态筛选和分页）
 * - 查看任意订单详情
 * - 修改订单状态（如确认支付、发货、完成等）
 *
 * 管理员可以查看所有用户的订单信息，不受用户归属限制。
 *
 * 映射路径：/api/admin/orders
 */
@RestController // 标识这是一个 RESTful 控制器，所有返回值自动序列化为 JSON
@RequestMapping("/api/admin/orders") // 将控制器映射到 /api/admin/orders 路径下
public class AdminOrderController {

    private final OrderService orderService; // 订单业务服务，使用构造器注入

    /**
     * 构造器注入 OrderService
     *
     * @param orderService 订单业务服务实例
     */
    public AdminOrderController(OrderService orderService) { // 通过构造器注入依赖
        this.orderService = orderService; // 将注入的服务赋值给成员变量
    }

    /**
     * 获取所有订单列表（管理员视图，分页）
     *
     * 管理员查看系统中所有用户的订单，支持按订单状态筛选。
     * 区别于用户端只能查看自己的订单，此处传入 null 用户 ID 表示查看全部。
     *
     * 请求方式：GET /api/admin/orders?status=0&page=1&size=10
     *
     * @param status 订单状态筛选条件，可选参数（0:待支付, 1:已支付, 2:已发货, 3:已完成, 4:已取消）
     * @param page   当前页码，默认为第 1 页
     * @param size   每页显示条数，默认为 10 条
     * @return Result 包含所有用户分页订单列表的统一响应结果
     */
    @GetMapping // 处理 HTTP GET 请求，映射到 /api/admin/orders
    public Result<Page<OrderListVO>> list(@RequestParam(required = false) Integer status, // @RequestParam 获取订单状态筛选，可选参数
                                           @RequestParam(defaultValue = "1") Integer page, // @RequestParam 获取分页页码，默认值为 1
                                           @RequestParam(defaultValue = "10") Integer size) { // @RequestParam 获取每页条数，默认值为 10
        return Result.success(orderService.list(null, status, page, size)); // 调用业务层获取所有用户的订单（userId 传 null 表示不按用户筛选），包装为成功响应
    }

    /**
     * 查看订单详情（管理员视图）
     *
     * 管理员可以查看任意订单的完整信息，包括订单基本信息、商品明细、
     * 收货地址、支付信息和物流信息等。
     *
     * 请求方式：GET /api/admin/orders/{id}
     *
     * @param id 订单 ID，从 URL 路径中获取
     * @return Result 包含订单详细信息的统一响应结果
     */
    @GetMapping("/{id}") // 处理 HTTP GET 请求，映射到 /api/admin/orders/{id}
    public Result<OrderDetailVO> detail(@PathVariable Long id) { // @PathVariable 将 URL 中的 {id} 绑定到方法参数
        return Result.success(orderService.detail(id, null)); // 调用业务层获取订单详情（userId 传 null 表示管理员操作），包装为成功响应
    }

    /**
     * 修改订单状态（管理员操作）
     *
     * 管理员可以手动修改订单的状态，用于处理订单流转流程。
     * 常见操作包括：确认支付、标记发货、标记完成等。
     *
     * 请求方式：PUT /api/admin/orders/{id}/status
     * 请求体示例：{"status": 1}  // 1:已支付, 2:已发货, 3:已完成
     *
     * @param id   订单 ID，从 URL 路径中获取
     * @param body 请求体 Map，包含要更新的状态值（key 为 "status"）
     * @return Result 包含订单状态已更新提示的统一响应结果
     */
    @PutMapping("/{id}/status") // 处理 HTTP PUT 请求，映射到 /api/admin/orders/{id}/status
    public Result<String> updateStatus(@PathVariable Long id, @RequestBody java.util.Map<String, Integer> body) { // @PathVariable 获取订单 ID，@RequestBody 获取状态参数
        orderService.updateStatus(id, body.get("status")); // 调用业务层更新订单状态
        return Result.success("订单状态已更新"); // 返回订单状态已更新的提示信息
    }
}
