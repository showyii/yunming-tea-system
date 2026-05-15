package com.yunming.tea.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.yunming.tea.entity.*;
import com.yunming.tea.mapper.*;
import com.yunming.tea.service.StatisticsService;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class StatisticsServiceImpl implements StatisticsService {

    private final OrderInfoMapper orderMapper;
    private final OrderItemMapper orderItemMapper;
    private final TeaProductMapper productMapper;
    private final TeaCategoryMapper categoryMapper;
    private final RoomBookingMapper roomBookingMapper;

    public StatisticsServiceImpl(OrderInfoMapper orderMapper, OrderItemMapper orderItemMapper,
                                  TeaProductMapper productMapper, TeaCategoryMapper categoryMapper,
                                  RoomBookingMapper roomBookingMapper) {
        this.orderMapper = orderMapper;
        this.orderItemMapper = orderItemMapper;
        this.productMapper = productMapper;
        this.categoryMapper = categoryMapper;
        this.roomBookingMapper = roomBookingMapper;
    }

    @Override
    public List<Map<String, Object>> dailyOrders(Integer days) {
        List<Map<String, Object>> result = new ArrayList<>();
        LocalDate endDate = LocalDate.now();
        LocalDate startDate = endDate.minusDays(days - 1);

        List<OrderInfo> orders = orderMapper.selectList(
                new LambdaQueryWrapper<OrderInfo>()
                        .ge(OrderInfo::getCreateTime, startDate.atStartOfDay())
                        .le(OrderInfo::getCreateTime, endDate.plusDays(1).atStartOfDay())
                        .in(OrderInfo::getStatus, 1, 2, 3));

        Map<LocalDate, Long> countMap = orders.stream()
                .collect(Collectors.groupingBy(o -> o.getCreateTime().toLocalDate(), Collectors.counting()));

        Map<LocalDate, BigDecimal> amountMap = orders.stream()
                .collect(Collectors.groupingBy(o -> o.getCreateTime().toLocalDate(),
                        Collectors.reducing(BigDecimal.ZERO, OrderInfo::getPayAmount, BigDecimal::add)));

        for (LocalDate d = startDate; !d.isAfter(endDate); d = d.plusDays(1)) {
            Map<String, Object> item = new LinkedHashMap<>();
            item.put("date", d.toString());
            item.put("count", countMap.getOrDefault(d, 0L));
            item.put("amount", amountMap.getOrDefault(d, BigDecimal.ZERO));
            result.add(item);
        }
        return result;
    }

    @Override
    public List<Map<String, Object>> salesRanking() {
        List<Map<String, Object>> result = new ArrayList<>();
        List<TeaProduct> products = productMapper.selectList(
                new LambdaQueryWrapper<TeaProduct>().orderByDesc(TeaProduct::getSales).last("LIMIT 10"));

        for (TeaProduct p : products) {
            Map<String, Object> item = new LinkedHashMap<>();
            item.put("name", p.getName());
            item.put("sales", p.getSales());
            item.put("price", p.getPrice());
            result.add(item);
        }
        return result;
    }

    @Override
    public List<Map<String, Object>> categorySales() {
        List<Map<String, Object>> result = new ArrayList<>();
        List<TeaCategory> categories = categoryMapper.selectList(new LambdaQueryWrapper<>());

        for (TeaCategory cat : categories) {
            List<TeaProduct> products = productMapper.selectList(
                    new LambdaQueryWrapper<TeaProduct>().eq(TeaProduct::getCategoryId, cat.getId()));

            int totalSales = products.stream().mapToInt(TeaProduct::getSales).sum();

            Map<String, Object> item = new LinkedHashMap<>();
            item.put("name", cat.getName());
            item.put("value", totalSales);
            result.add(item);
        }
        return result;
    }

    @Override
    public List<Map<String, Object>> roomBookingStats() {
        List<RoomBooking> bookings = roomBookingMapper.selectList(
                new LambdaQueryWrapper<RoomBooking>().orderByDesc(RoomBooking::getCreateTime));

        Map<LocalDate, Long> stats = bookings.stream()
                .collect(Collectors.groupingBy(RoomBooking::getBookingDate, Collectors.counting()));

        List<Map<String, Object>> result = new ArrayList<>();
        stats.entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .forEach(e -> {
                    Map<String, Object> item = new LinkedHashMap<>();
                    item.put("date", e.getKey().toString());
                    item.put("count", e.getValue());
                    result.add(item);
                });
        return result;
    }

    @Override
    public Map<String, Object> orderStatusPie() {
        Map<String, Object> result = new LinkedHashMap<>();
        List<Map<String, String>> list = new ArrayList<>();

        String[] names = {"待付款", "已付款", "已发货", "已完成", "已取消"};
        for (int i = 0; i <= 4; i++) {
            Long count = orderMapper.selectCount(
                    new LambdaQueryWrapper<OrderInfo>().eq(OrderInfo::getStatus, i));
            Map<String, String> item = new LinkedHashMap<>();
            item.put("name", names[i]);
            item.put("value", String.valueOf(count));
            list.add(item);
        }
        result.put("list", list);
        return result;
    }
}
