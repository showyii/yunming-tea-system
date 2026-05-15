package com.yunming.tea.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 订单列表视图
 */
@Data
public class OrderListVO {

    private Long id;
    private String orderNo;
    private BigDecimal totalAmount;
    private BigDecimal payAmount;
    private Integer status;
    private Integer itemCount;
    private String firstProductImage;
    private LocalDateTime createTime;
}
