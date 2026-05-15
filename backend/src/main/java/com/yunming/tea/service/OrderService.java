package com.yunming.tea.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yunming.tea.dto.SubmitOrderDTO;
import com.yunming.tea.vo.OrderDetailVO;
import com.yunming.tea.vo.OrderListVO;

/**
 * 订单服务接口
 */
public interface OrderService {

    Long submit(Long userId, SubmitOrderDTO dto);

    Page<OrderListVO> list(Long userId, Integer status, Integer page, Integer size);

    OrderDetailVO detail(Long orderId, Long userId);

    void cancel(Long orderId, Long userId);

    void pay(Long orderId, Long userId);

    void updateStatus(Long orderId, Integer status);
}
