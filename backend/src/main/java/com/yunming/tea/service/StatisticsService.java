package com.yunming.tea.service; // 声明包路径，属于服务层接口包

import java.util.List; // 导入List集合类，用于返回统计数据列表
import java.util.Map; // 导入Map集合类，用于返回键值对形式的统计数据

/**
 * 数据统计服务接口
 * <p>
 * 该接口定义了后台数据统计相关的业务操作规范，包括：
 * 1. 每日订单统计（指定天数范围内的每日订单数和金额）
 * 2. 商品销量排行榜（销量最高的10个商品）
 * 3. 分类销量统计（各分类下的商品总销量）
 * 4. 包间预约统计（每日预约数量）
 * 5. 订单状态分布饼图数据（各状态订单的数量）
 * <p>
 * 统计服务为管理员提供数据可视化的数据源，支持后台管理仪表盘的图表展示。
 * 所有统计方法返回List&lt;Map&lt;String, Object&gt;&gt;格式，
 * 方便直接序列化为JSON供前端ECharts等图表库使用。
 *
 * @author yunming
 * @see com.yunming.tea.service.impl.StatisticsServiceImpl
 */
public interface StatisticsService {

    /**
     * 每日订单统计
     * <p>
     * 统计指定天数范围内的每日订单数量和订单金额。
     * 从当前日期向前推算指定天数，计算每一天的订单统计。
     * 仅统计已付款、已发货、已完成的订单（status为1、2、3）。
     * <p>
     * 返回的Map包含：
     * - date：日期字符串（YYYY-MM-DD格式）
     * - count：当日订单数量（Long类型）
     * - amount：当日订单总金额（BigDecimal类型）
     * <p>
     * 用于后台管理仪表盘的折线图/柱状图展示。
     *
     * @param days 统计的天数范围，例如传入7表示统计最近7天
     * @return 每日统计数据列表，按日期升序排列，即使某天无订单也会返回该天数据（count为0）
     */
    List<Map<String, Object>> dailyOrders(Integer days); // 每日订单统计方法声明

    /**
     * 商品销量排行榜
     * <p>
     * 查询销量最高的前10个商品，按销量降序排列。
     * <p>
     * 返回的Map包含：
     * - name：商品名称
     * - sales：销售数量
     * - price：商品单价
     * <p>
     * 用于后台管理仪表盘的商品销量排名图。
     *
     * @return 销量排行数据列表，最多10条
     */
    List<Map<String, Object>> salesRanking(); // 商品销量排行方法声明

    /**
     * 分类销量统计
     * <p>
     * 统计每个商品分类下的所有商品总销量。
     * 用于分析不同品类的销售贡献占比。
     * <p>
     * 返回的Map包含：
     * - name：分类名称
     * - value：该分类下所有商品的总销量
     * <p>
     * 用于后台管理仪表盘的饼图/南丁格尔图展示。
     *
     * @return 分类销量统计数据列表
     */
    List<Map<String, Object>> categorySales(); // 分类销量统计方法声明

    /**
     * 包间预约统计
     * <p>
     * 按日期统计每日的包间预约数量，
     * 按日期升序排列。
     * <p>
     * 返回的Map包含：
     * - date：预约日期字符串（YYYY-MM-DD格式）
     * - count：当日预约数量
     * <p>
     * 用于后台管理仪表盘的包间预约趋势图。
     *
     * @return 包间预约统计数据列表，按日期升序排列
     */
    List<Map<String, Object>> roomBookingStats(); // 包间预约统计方法声明

    /**
     * 订单状态分布饼图数据
     * <p>
     * 统计每种订单状态下的订单数量，包括5种状态：
     * 待付款、已付款、已发货、已完成、已取消。
     * <p>
     * 返回的Map包含：
     * - list：订单状态分布列表，每项包含name（状态名称）和value（该状态的订单数量）
     * <p>
     * 用于后台管理仪表盘的订单状态饼图展示。
     *
     * @return 订单状态分布数据，包含list键，值为状态分布列表
     */
    Map<String, Object> orderStatusPie(); // 订单状态饼图统计方法声明
}
