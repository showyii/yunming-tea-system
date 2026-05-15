package com.yunming.tea.vo;

import lombok.Data;

import java.math.BigDecimal;

/**
 * 订单项视图
 */
@Data
public class OrderItemVO {

    private Long id;
    private Long productId;
    private String productName;
    private String productImage;
    private BigDecimal price;
    private Integer quantity;
    private BigDecimal totalPrice;
}
