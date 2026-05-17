package com.yunming.tea.service.impl; // 声明包路径，属于服务实现层包

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper; // 导入MyBatis-Plus的Lambda查询包装器
import com.yunming.tea.entity.*; // 导入所有实体类（OrderInfo订单、OrderItem订单明细、TeaProduct商品、TeaCategory分类、RoomBooking包间预约等）
import com.yunming.tea.mapper.*; // 导入所有数据访问层接口
import com.yunming.tea.service.StatisticsService; // 导入数据统计服务接口
import org.springframework.stereotype.Service; // 导入Spring的Service注解

import java.math.BigDecimal; // 导入BigDecimal，用于精确的金额计算
import java.time.LocalDate; // 导入LocalDate，用于日期范围的循环
import java.util.*; // 导入java.util包下所有类（List、Map、LinkedHashMap、ArrayList等）
import java.util.stream.Collectors; // 导入Collectors工具类

/**
 * 数据统计服务实现类
 * <p>
 * 实现了{@link StatisticsService}接口中定义的后台数据统计业务逻辑。
 * 主要功能包括：
 * <ul>
 *   <li>每日订单统计：统计指定天数范围内每天的订单数量和金额</li>
 *   <li>商品销量排行榜：查询销量最高的前10个商品</li>
 *   <li>分类销量统计：按商品分类统计总销量</li>
 *   <li>包间预约统计：按日期统计每日预约数量</li>
 *   <li>订单状态饼图：统计各状态订单的数量分布</li>
 * </ul>
 * <p>
 * 所有统计方法返回List&lt;Map&lt;String, Object&gt;&gt;格式，
 * 使用LinkedHashMap保证字段顺序，方便前端ECharts等图表库直接使用。
 * <p>
 * 每日订单统计中，即使某天没有订单，也会返回该天的数据（count=0, amount=0），
 * 保证图表x轴的日期是连续的，不会出现断档。
 *
 * @author yunming
 * @see StatisticsService
 */
@Service // 将该类标记为Spring的Service组件
public class StatisticsServiceImpl implements StatisticsService {

    private final OrderInfoMapper orderMapper; // 订单信息数据访问对象
    private final OrderItemMapper orderItemMapper; // 订单明细数据访问对象
    private final TeaProductMapper productMapper; // 商品数据访问对象
    private final TeaCategoryMapper categoryMapper; // 分类数据访问对象
    private final RoomBookingMapper roomBookingMapper; // 包间预约数据访问对象

    /**
     * 构造函数注入依赖
     *
     * @param orderMapper 订单信息数据访问对象
     * @param orderItemMapper 订单明细数据访问对象
     * @param productMapper 商品数据访问对象
     * @param categoryMapper 分类数据访问对象
     * @param roomBookingMapper 包间预约数据访问对象
     */
    public StatisticsServiceImpl(OrderInfoMapper orderMapper, OrderItemMapper orderItemMapper,
                                  TeaProductMapper productMapper, TeaCategoryMapper categoryMapper,
                                  RoomBookingMapper roomBookingMapper) {
        this.orderMapper = orderMapper; // 注入订单Mapper
        this.orderItemMapper = orderItemMapper; // 注入订单明细Mapper
        this.productMapper = productMapper; // 注入商品Mapper
        this.categoryMapper = categoryMapper; // 注入分类Mapper
        this.roomBookingMapper = roomBookingMapper; // 注入包间预约Mapper
    }

    /**
     * 每日订单统计
     * <p>
     * 统计指定天数范围内的每日订单数量和订单金额。
     * 统计范围：从当前日期往前推(days-1)天到今天，共days天。
     * 只统计有效订单（已付款、已发货、已完成），排除待付款和已取消的订单。
     * <p>
     * 实现方式：
     * <ol>
     *   <li>计算日期范围（起点和终点）</li>
     *   <li>查询范围内的有效订单</li>
     *   <li>按日期分组统计订单数量和金额</li>
     *   <li>遍历日期范围，填充每一天的数据（无订单的天填0）</li>
     * </ol>
     * <p>
     * 返回的Map包含：
     * <ul>
     *   <li>date：日期字符串（格式：YYYY-MM-DD）</li>
     *   <li>count：当日订单数量（Long类型）</li>
     *   <li>amount：当日订单总金额（BigDecimal类型）</li>
     * </ul>
     *
     * @param days 统计的天数范围
     * @return 每日统计数据列表，按日期升序排列
     */
    @Override // 标识该方法覆盖了父接口中的方法
    public List<Map<String, Object>> dailyOrders(Integer days) {
        List<Map<String, Object>> result = new ArrayList<>(); // 创建结果列表，用于存放每天的统计数据

        // 第1步：计算日期范围
        LocalDate endDate = LocalDate.now(); // 获取当前日期作为结束日期
        LocalDate startDate = endDate.minusDays(days - 1); // 计算开始日期 = 当前日期 - (天数-1)

        // 第2步：查询日期范围内的有效订单
        // 只统计已付款(1)、已发货(2)、已完成(3)的订单
        List<OrderInfo> orders = orderMapper.selectList( // 查询订单列表
                new LambdaQueryWrapper<OrderInfo>() // 创建查询包装器
                        .ge(OrderInfo::getCreateTime, startDate.atStartOfDay()) // 条件：创建时间 >= 开始日期的0点
                        .le(OrderInfo::getCreateTime, endDate.plusDays(1).atStartOfDay()) // 条件：创建时间 < 结束日期+1天的0点（即 <= 结束日期的23:59:59）
                        .in(OrderInfo::getStatus, 1, 2, 3)); // 条件：状态在{1,2,3}中（有效订单）

        // 第3步：按日期分组统计订单数量
        Map<LocalDate, Long> countMap = orders.stream() // 获取订单流
                .collect(Collectors.groupingBy( // 按日期分组
                        o -> o.getCreateTime().toLocalDate(), // 分组键：订单创建时间的日期部分
                        Collectors.counting())); // 聚合函数：计数

        // 第4步：按日期分组统计订单总金额
        Map<LocalDate, BigDecimal> amountMap = orders.stream() // 获取订单流
                .collect(Collectors.groupingBy( // 按日期分组
                        o -> o.getCreateTime().toLocalDate(), // 分组键：订单创建时间的日期部分
                        Collectors.reducing( // 聚合函数：归约求和
                                BigDecimal.ZERO, // 初始值：0
                                OrderInfo::getPayAmount, // 映射函数：提取实付金额
                                BigDecimal::add))); // 合并函数：金额相加

        // 第5步：遍历日期范围内的每一天，填充统计数据
        for (LocalDate d = startDate; !d.isAfter(endDate); d = d.plusDays(1)) { // 从开始日期循环到结束日期
            Map<String, Object> item = new LinkedHashMap<>(); // 使用LinkedHashMap保证字段顺序
            item.put("date", d.toString()); // 设置日期（YYYY-MM-DD格式）
            item.put("count", countMap.getOrDefault(d, 0L)); // 设置订单数量（无订单则为0）
            item.put("amount", amountMap.getOrDefault(d, BigDecimal.ZERO)); // 设置订单金额（无订单则为0）
            result.add(item); // 添加到结果列表
        }
        return result; // 返回每日统计数据列表
    }

    /**
     * 商品销量排行榜
     * <p>
     * 查询所有商品中销量最高的前10个，按销量降序排列。
     * 用于后台管理仪表盘的热卖商品排行展示。
     * <p>
     * 返回的Map包含：
     * <ul>
     *   <li>name：商品名称</li>
     *   <li>sales：累计销量</li>
     *   <li>price：商品单价</li>
     * </ul>
     *
     * @return 销量排行数据列表，最多10条，按销量降序
     */
    @Override // 标识该方法覆盖了父接口中的方法
    public List<Map<String, Object>> salesRanking() {
        List<Map<String, Object>> result = new ArrayList<>(); // 创建结果列表

        // 查询销量最高的前10个商品
        List<TeaProduct> products = productMapper.selectList( // 查询商品列表
                new LambdaQueryWrapper<TeaProduct>() // 创建查询包装器
                        .orderByDesc(TeaProduct::getSales) // 排序：按销量降序
                        .last("LIMIT 10")); // 限制：最多10条记录

        // 遍历商品，构建排行数据
        for (TeaProduct p : products) { // 遍历每个商品
            Map<String, Object> item = new LinkedHashMap<>(); // 使用LinkedHashMap保证字段顺序
            item.put("name", p.getName()); // 设置商品名称
            item.put("sales", p.getSales()); // 设置累计销量
            item.put("price", p.getPrice()); // 设置商品单价
            result.add(item); // 添加到结果列表
        }
        return result; // 返回销量排行数据
    }

    /**
     * 分类销量统计
     * <p>
     * 统计每个商品分类下所有商品的总销量。
     * 遍历所有分类，对每个分类查询其下的商品并汇总销量。
     * 用于后台管理仪表盘的各品类销售占比饼图。
     * <p>
     * 返回的Map包含：
     * <ul>
     *   <li>name：分类名称</li>
     *   <li>value：该分类下所有商品的总销量</li>
     * </ul>
     *
     * @return 分类销量统计数据列表
     */
    @Override // 标识该方法覆盖了父接口中的方法
    public List<Map<String, Object>> categorySales() {
        List<Map<String, Object>> result = new ArrayList<>(); // 创建结果列表

        // 查询所有分类
        List<TeaCategory> categories = categoryMapper.selectList(new LambdaQueryWrapper<>()); // 查询所有分类

        // 遍历每个分类，统计该分类下的商品总销量
        for (TeaCategory cat : categories) { // 遍历每个分类
            // 查询该分类下的所有商品
            List<TeaProduct> products = productMapper.selectList( // 查询商品列表
                    new LambdaQueryWrapper<TeaProduct>() // 创建查询包装器
                            .eq(TeaProduct::getCategoryId, cat.getId())); // 条件：分类ID匹配

            // 汇总该分类下所有商品的销量
            int totalSales = products.stream() // 获取商品列表流
                    .mapToInt(TeaProduct::getSales) // 提取每个商品的销量（转为IntStream）
                    .sum(); // 求和

            // 构建分类销量统计数据
            Map<String, Object> item = new LinkedHashMap<>(); // 使用LinkedHashMap保证字段顺序
            item.put("name", cat.getName()); // 设置分类名称
            item.put("value", totalSales); // 设置该分类的总销量
            result.add(item); // 添加到结果列表
        }
        return result; // 返回分类销量统计数据
    }

    /**
     * 包间预约统计
     * <p>
     * 按日期统计每日的包间预约数量，按日期升序排列。
     * 查询所有包间预约记录（不限状态），按预约日期分组统计数量。
     * <p>
     * 返回的Map包含：
     * <ul>
     *   <li>date：预约日期字符串（格式：YYYY-MM-DD）</li>
     *   <li>count：当日预约数量</li>
     * </ul>
     *
     * @return 包间预约统计数据列表，按日期升序排列
     */
    @Override // 标识该方法覆盖了父接口中的方法
    public List<Map<String, Object>> roomBookingStats() {
        // 第1步：查询所有包间预约记录，按创建时间倒序
        List<RoomBooking> bookings = roomBookingMapper.selectList( // 查询预约列表
                new LambdaQueryWrapper<RoomBooking>() // 创建查询包装器
                        .orderByDesc(RoomBooking::getCreateTime)); // 按创建时间倒序

        // 第2步：按预约日期分组统计数量
        Map<LocalDate, Long> stats = bookings.stream() // 获取预约记录流
                .collect(Collectors.groupingBy( // 按日期分组
                        RoomBooking::getBookingDate, // 分组键：预约日期
                        Collectors.counting())); // 聚合函数：计数

        // 第3步：按日期升序排列，构建统计结果列表
        List<Map<String, Object>> result = new ArrayList<>(); // 创建结果列表
        stats.entrySet().stream() // 获取Map的Entry流
                .sorted(Map.Entry.comparingByKey()) // 按日期Key升序排列
                .forEach(e -> { // 遍历每个日期条目
                    Map<String, Object> item = new LinkedHashMap<>(); // 使用LinkedHashMap保证字段顺序
                    item.put("date", e.getKey().toString()); // 设置日期字符串（YYYY-MM-DD格式）
                    item.put("count", e.getValue()); // 设置当日预约数量
                    result.add(item); // 添加到结果列表
                });
        return result; // 返回包间预约统计数据
    }

    /**
     * 订单状态分布饼图数据
     * <p>
     * 统计5种订单状态下各自的订单数量：
     * 待付款(0)、已付款(1)、已发货(2)、已完成(3)、已取消(4)
     * <p>
     * 返回格式：
     * <pre>
     * {
     *   "list": [
     *     {"name": "待付款", "value": "10"},
     *     {"name": "已付款", "value": "5"},
     *     ...
     *   ]
     * }
     * </pre>
     * 使用字符串类型的value，兼容前端ECharts的数据格式要求。
     *
     * @return 订单状态分布数据Map，包含list键
     */
    @Override // 标识该方法覆盖了父接口中的方法
    public Map<String, Object> orderStatusPie() {
        Map<String, Object> result = new LinkedHashMap<>(); // 创建结果Map，使用LinkedHashMap保证顺序
        List<Map<String, String>> list = new ArrayList<>(); // 创建状态分布列表

        // 定义5种订单状态的中文名称数组，下标对应状态码
        String[] names = {"待付款", "已付款", "已发货", "已完成", "已取消"}; // 状态0到4对应的中文名称

        // 循环统计每种状态的订单数量
        for (int i = 0; i <= 4; i++) { // 遍历状态码0到4
            Long count = orderMapper.selectCount( // 统计该状态的订单数量
                    new LambdaQueryWrapper<OrderInfo>() // 创建查询包装器
                            .eq(OrderInfo::getStatus, i)); // 条件：订单状态等于当前循环的状态码
            Map<String, String> item = new LinkedHashMap<>(); // 创建单个状态条目
            item.put("name", names[i]); // 设置状态名称
            item.put("value", String.valueOf(count)); // 设置该状态的订单数量（转为字符串）
            list.add(item); // 添加到状态分布列表
        }
        result.put("list", list); // 将状态分布列表放入结果Map的"list"键下
        return result; // 返回订单状态分布数据
    }
}
