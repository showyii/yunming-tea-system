package com.yunming.tea.controller.admin;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yunming.tea.common.Result;
import com.yunming.tea.service.OrderService;
import com.yunming.tea.vo.OrderDetailVO;
import com.yunming.tea.vo.OrderListVO;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/orders")
public class AdminOrderController {

    private final OrderService orderService;

    public AdminOrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @GetMapping
    public Result<Page<OrderListVO>> list(@RequestParam(required = false) Integer status,
                                           @RequestParam(defaultValue = "1") Integer page,
                                           @RequestParam(defaultValue = "10") Integer size) {
        return Result.success(orderService.list(null, status, page, size));
    }

    @GetMapping("/{id}")
    public Result<OrderDetailVO> detail(@PathVariable Long id) {
        return Result.success(orderService.detail(id, null));
    }

    @PutMapping("/{id}/status")
    public Result<String> updateStatus(@PathVariable Long id, @RequestBody java.util.Map<String, Integer> body) {
        orderService.updateStatus(id, body.get("status"));
        return Result.success("订单状态已更新");
    }
}
