package com.yunming.tea.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yunming.tea.common.Result;
import com.yunming.tea.dto.SubmitOrderDTO;
import com.yunming.tea.service.OrderService;
import com.yunming.tea.vo.OrderDetailVO;
import com.yunming.tea.vo.OrderListVO;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * 订单控制器（用户端）
 */
@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    /**
     * 提交订单
     * POST /api/orders
     */
    @PostMapping
    public Result<Long> submit(@Valid @RequestBody SubmitOrderDTO dto,
                                @RequestAttribute("userId") Long userId) {
        Long orderId = orderService.submit(userId, dto);
        return Result.success("下单成功", orderId);
    }

    /**
     * 订单列表
     * GET /api/orders
     */
    @GetMapping
    public Result<Page<OrderListVO>> list(@RequestAttribute("userId") Long userId,
                                           @RequestParam(required = false) Integer status,
                                           @RequestParam(defaultValue = "1") Integer page,
                                           @RequestParam(defaultValue = "10") Integer size) {
        return Result.success(orderService.list(userId, status, page, size));
    }

    /**
     * 订单详情
     * GET /api/orders/{id}
     */
    @GetMapping("/{id}")
    public Result<OrderDetailVO> detail(@PathVariable Long id,
                                         @RequestAttribute("userId") Long userId) {
        return Result.success(orderService.detail(id, userId));
    }

    /**
     * 取消订单
     * PUT /api/orders/{id}/cancel
     */
    @PutMapping("/{id}/cancel")
    public Result<String> cancel(@PathVariable Long id,
                                  @RequestAttribute("userId") Long userId) {
        orderService.cancel(id, userId);
        return Result.success("订单已取消");
    }

    /**
     * 模拟支付
     * PUT /api/orders/{id}/pay
     */
    @PutMapping("/{id}/pay")
    public Result<String> pay(@PathVariable Long id,
                               @RequestAttribute("userId") Long userId) {
        orderService.pay(id, userId);
        return Result.success("支付成功");
    }
}
