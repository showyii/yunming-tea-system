package com.yunming.tea.controller.admin; // 包声明：管理后台控制器层

import com.yunming.tea.common.Result; // 统一响应结果封装类
import com.yunming.tea.service.StatisticsService; // 数据统计业务逻辑服务
import org.springframework.web.bind.annotation.*; // Spring MVC 注解：包括 RestController、RequestMapping、GetMapping

import java.util.List; // Java 集合框架的 List 接口
import java.util.Map; // Java 集合框架的 Map 接口

/**
 * 管理员 - 数据统计控制器
 *
 * 负责管理后台的数据统计和可视化功能，提供多种维度的统计数据，包括：
 * - 每日订单统计（近 N 天趋势图数据）
 * - 商品销量排行
 * - 分类销售额统计
 * - 茶室预约统计
 * - 订单状态分布（饼图数据）
 *
 * 所有接口仅供管理员查看，用于运营分析和决策支持。
 *
 * 映射路径：/api/admin/statistics
 */
@RestController // 标识这是一个 RESTful 控制器，所有返回值自动序列化为 JSON
@RequestMapping("/api/admin/statistics") // 将控制器映射到 /api/admin/statistics 路径下
public class StatisticsController {

    private final StatisticsService statisticsService; // 数据统计业务服务，使用构造器注入

    /**
     * 构造器注入 StatisticsService
     *
     * @param statisticsService 数据统计业务服务实例
     */
    public StatisticsController(StatisticsService statisticsService) { // 通过构造器注入依赖
        this.statisticsService = statisticsService; // 将注入的服务赋值给成员变量
    }

    /**
     * 获取每日订单统计数据
     *
     * 返回最近 N 天的每日订单数量趋势数据，用于生成折线图或柱状图。
     * 每条记录包含日期和当天的订单总数。
     *
     * 请求方式：GET /api/admin/statistics/daily-orders?days=7
     *
     * @param days 要统计的天数，默认值为最近 7 天
     * @return Result 包含每日订单统计数据的统一响应结果（List<Map>，每个 Map 包含日期和订单数）
     */
    @GetMapping("/daily-orders") // 处理 HTTP GET 请求，映射到 /api/admin/statistics/daily-orders
    public Result<List<Map<String, Object>>> dailyOrders(@RequestParam(defaultValue = "7") Integer days) { // @RequestParam 获取统计天数，默认值为 7
        return Result.success(statisticsService.dailyOrders(days)); // 调用统计服务获取每日订单数据并包装为成功响应
    }

    /**
     * 获取商品销量排行榜
     *
     * 返回所有商品的销量排名数据，按销量降序排列。
     * 用于管理后台生成"热销商品排行榜"。
     *
     * 请求方式：GET /api/admin/statistics/sales-ranking
     *
     * @return Result 包含商品销量排行榜数据的统一响应结果
     */
    @GetMapping("/sales-ranking") // 处理 HTTP GET 请求，映射到 /api/admin/statistics/sales-ranking
    public Result<List<Map<String, Object>>> salesRanking() { // 获取商品销量排名的方法
        return Result.success(statisticsService.salesRanking()); // 调用统计服务获取销量排名数据并包装为成功响应
    }

    /**
     * 获取分类销售额统计
     *
     * 返回按茶产品分类统计的销售额数据，用于生成分类销售额饼图或柱状图。
     * 了解各个茶类（绿茶、红茶、乌龙茶等）的销售占比情况。
     *
     * 请求方式：GET /api/admin/statistics/category-sales
     *
     * @return Result 包含各分类销售额统计数据的统一响应结果
     */
    @GetMapping("/category-sales") // 处理 HTTP GET 请求，映射到 /api/admin/statistics/category-sales
    public Result<List<Map<String, Object>>> categorySales() { // 获取分类销售额统计的方法
        return Result.success(statisticsService.categorySales()); // 调用统计服务获取分类销售额数据并包装为成功响应
    }

    /**
     * 获取茶室预约统计数据
     *
     * 返回茶室预约相关的统计数据，包括各茶室的预约次数、
     * 预约时段分布等信息，用于茶室运营分析。
     *
     * 请求方式：GET /api/admin/statistics/room-bookings
     *
     * @return Result 包含茶室预约统计数据的统一响应结果
     */
    @GetMapping("/room-bookings") // 处理 HTTP GET 请求，映射到 /api/admin/statistics/room-bookings
    public Result<List<Map<String, Object>>> roomBookings() { // 获取茶室预约统计的方法
        return Result.success(statisticsService.roomBookingStats()); // 调用统计服务获取茶室预约统计数据并包装为成功响应
    }

    /**
     * 获取订单状态分布统计
     *
     * 返回各订单状态（待支付、已支付、已发货、已完成、已取消）的
     * 订单数量分布数据，用于生成订单状态饼图。
     *
     * 请求方式：GET /api/admin/statistics/order-status
     *
     * @return Result 包含订单状态分布数据的统一响应结果（Map，key 为状态名，value 为数量）
     */
    @GetMapping("/order-status") // 处理 HTTP GET 请求，映射到 /api/admin/statistics/order-status
    public Result<Map<String, Object>> orderStatus() { // 获取订单状态分布统计的方法
        return Result.success(statisticsService.orderStatusPie()); // 调用统计服务获取订单状态饼图数据并包装为成功响应
    }
}
