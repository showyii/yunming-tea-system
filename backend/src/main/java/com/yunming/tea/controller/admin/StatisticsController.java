package com.yunming.tea.controller.admin;

import com.yunming.tea.common.Result;
import com.yunming.tea.service.StatisticsService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin/statistics")
public class StatisticsController {

    private final StatisticsService statisticsService;

    public StatisticsController(StatisticsService statisticsService) {
        this.statisticsService = statisticsService;
    }

    @GetMapping("/daily-orders")
    public Result<List<Map<String, Object>>> dailyOrders(@RequestParam(defaultValue = "7") Integer days) {
        return Result.success(statisticsService.dailyOrders(days));
    }

    @GetMapping("/sales-ranking")
    public Result<List<Map<String, Object>>> salesRanking() {
        return Result.success(statisticsService.salesRanking());
    }

    @GetMapping("/category-sales")
    public Result<List<Map<String, Object>>> categorySales() {
        return Result.success(statisticsService.categorySales());
    }

    @GetMapping("/room-bookings")
    public Result<List<Map<String, Object>>> roomBookings() {
        return Result.success(statisticsService.roomBookingStats());
    }

    @GetMapping("/order-status")
    public Result<Map<String, Object>> orderStatus() {
        return Result.success(statisticsService.orderStatusPie());
    }
}
