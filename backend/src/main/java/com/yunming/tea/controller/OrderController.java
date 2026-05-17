package com.yunming.tea.controller; // 包声明：控制器层

import com.baomidou.mybatisplus.extension.plugins.pagination.Page; // MyBatis-Plus 分页结果对象，包含分页数据和总记录数
import com.yunming.tea.common.Result; // 统一响应结果封装类
import com.yunming.tea.dto.SubmitOrderDTO; // 提交订单请求的数据传输对象
import com.yunming.tea.service.OrderService; // 订单业务逻辑服务
import com.yunming.tea.vo.OrderDetailVO; // 订单详情视图对象
import com.yunming.tea.vo.OrderListVO; // 订单列表项视图对象
import org.springframework.web.bind.annotation.*; // Spring MVC 注解：包括 RestController、RequestMapping、PostMapping 等

import javax.validation.Valid; // JSR-303 Bean 校验注解，用于验证请求体参数

/**
 * 订单控制器（用户端，面向普通用户）
 *
 * 负责处理用户的茶产品购买订单相关操作，包括：
 * - 提交/创建订单
 * - 查看订单列表（支持按状态筛选和分页）
 * - 查看订单详情
 * - 取消订单
 * - 模拟支付订单
 *
 * 所有操作都需要用户登录后才能执行。
 *
 * 映射路径：/api/orders
 */
@RestController // 标识这是一个 RESTful 控制器，所有返回值自动序列化为 JSON
@RequestMapping("/api/orders") // 将控制器映射到 /api/orders 路径下
public class OrderController {

    private final OrderService orderService; // 订单业务服务，使用构造器注入

    /**
     * 构造器注入 OrderService
     *
     * @param orderService 订单业务服务实例
     */
    public OrderController(OrderService orderService) { // 通过构造器注入依赖
        this.orderService = orderService; // 将注入的服务赋值给成员变量
    }

    /**
     * 提交订单（下单）
     *
     * 用户将购物车中的商品提交生成订单。下单过程中会进行库存校验、
     * 金额计算，并生成订单编号。下单成功后订单状态为"待支付"。
     *
     * 请求方式：POST /api/orders
     *
     * @param dto    提交订单的数据传输对象，包含收货地址、购物车项等信息，由 @Valid 校验
     * @param userId 当前登录用户的 ID，从请求属性中获取（必填）
     * @return Result 包含新创建的订单 ID 的统一响应结果
     */
    @PostMapping // 处理 HTTP POST 请求，映射到 /api/orders
    public Result<Long> submit(@Valid @RequestBody SubmitOrderDTO dto, // @Valid 校验请求体，@RequestBody 将 JSON 反序列化为 SubmitOrderDTO
                                @RequestAttribute("userId") Long userId) { // @RequestAttribute 从请求属性中获取 userId（必填）
        Long orderId = orderService.submit(userId, dto); // 调用业务层提交订单，返回新订单的 ID
        return Result.success("下单成功", orderId); // 返回下单成功的提示信息和订单 ID
    }

    /**
     * 查看我的订单列表（分页）
     *
     * 获取当前登录用户的所有订单，支持按订单状态筛选和分页展示。
     * 订单按创建时间倒序排列，最新的订单排在最前面。
     *
     * 请求方式：GET /api/orders?status=0&page=1&size=10
     *
     * @param userId 当前登录用户的 ID，从请求属性中获取（必填）
     * @param status 订单状态筛选条件，可选参数（0:待支付, 1:已支付, 2:已发货, 3:已完成, 4:已取消）
     * @param page   当前页码，默认为第 1 页
     * @param size   每页显示条数，默认为 10 条
     * @return Result 包含分页订单列表的统一响应结果
     */
    @GetMapping // 处理 HTTP GET 请求，映射到 /api/orders
    public Result<Page<OrderListVO>> list(@RequestAttribute("userId") Long userId, // @RequestAttribute 从请求属性中获取 userId（必填）
                                           @RequestParam(required = false) Integer status, // @RequestParam 获取订单状态筛选，可选参数
                                           @RequestParam(defaultValue = "1") Integer page, // @RequestParam 获取分页页码，默认值为 1
                                           @RequestParam(defaultValue = "10") Integer size) { // @RequestParam 获取每页条数，默认值为 10
        return Result.success(orderService.list(userId, status, page, size)); // 调用业务层获取分页订单列表并包装为成功响应
    }

    /**
     * 查看订单详情
     *
     * 获取指定订单的完整信息，包括订单基本信息、商品明细、收货地址、
     * 支付信息和物流信息等。只能查看属于自己的订单。
     *
     * 请求方式：GET /api/orders/{id}
     *
     * @param id     订单 ID，从 URL 路径中获取
     * @param userId 当前登录用户的 ID，从请求属性中获取（必填），用于校验订单归属
     * @return Result 包含订单详细信息的统一响应结果
     */
    @GetMapping("/{id}") // 处理 HTTP GET 请求，映射到 /api/orders/{id}
    public Result<OrderDetailVO> detail(@PathVariable Long id, // @PathVariable 将 URL 中的 {id} 绑定到方法参数
                                         @RequestAttribute("userId") Long userId) { // @RequestAttribute 从请求属性中获取 userId（必填）
        return Result.success(orderService.detail(id, userId)); // 调用业务层获取订单详情并包装为成功响应
    }

    /**
     * 取消订单
     *
     * 用户取消指定的订单。只有"待支付"状态的订单才能被取消，
     * 已支付或已发货的订单无法取消，由业务层进行状态校验。
     *
     * 请求方式：PUT /api/orders/{id}/cancel
     *
     * @param id     订单 ID，从 URL 路径中获取
     * @param userId 当前登录用户的 ID，从请求属性中获取（必填），用于校验订单归属
     * @return Result 包含订单已取消提示的统一响应结果
     */
    @PutMapping("/{id}/cancel") // 处理 HTTP PUT 请求，映射到 /api/orders/{id}/cancel
    public Result<String> cancel(@PathVariable Long id, // @PathVariable 将 URL 中的 {id} 绑定到方法参数
                                  @RequestAttribute("userId") Long userId) { // @RequestAttribute 从请求属性中获取 userId（必填）
        orderService.cancel(id, userId); // 调用业务层取消订单
        return Result.success("订单已取消"); // 返回订单已取消的提示信息
    }

    /**
     * 模拟支付订单
     *
     * 对指定订单进行模拟支付操作（演示用途）。实际项目中应接入真实的
     * 支付网关（如微信支付、支付宝等）。支付成功后订单状态变更为"已支付"。
     *
     * 请求方式：PUT /api/orders/{id}/pay
     *
     * @param id     订单 ID，从 URL 路径中获取
     * @param userId 当前登录用户的 ID，从请求属性中获取（必填），用于校验订单归属
     * @return Result 包含支付成功提示的统一响应结果
     */
    @PutMapping("/{id}/pay") // 处理 HTTP PUT 请求，映射到 /api/orders/{id}/pay
    public Result<String> pay(@PathVariable Long id, // @PathVariable 将 URL 中的 {id} 绑定到方法参数
                               @RequestAttribute("userId") Long userId) { // @RequestAttribute 从请求属性中获取 userId（必填）
        orderService.pay(id, userId); // 调用业务层执行支付操作
        return Result.success("支付成功"); // 返回支付成功的提示信息
    }
}
