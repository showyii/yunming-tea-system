package com.yunming.tea.service;

import java.util.List;
import java.util.Map;

public interface StatisticsService {

    List<Map<String, Object>> dailyOrders(Integer days);

    List<Map<String, Object>> salesRanking();

    List<Map<String, Object>> categorySales();

    List<Map<String, Object>> roomBookingStats();

    Map<String, Object> orderStatusPie();
}
