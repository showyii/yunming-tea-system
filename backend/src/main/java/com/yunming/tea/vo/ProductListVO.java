package com.yunming.tea.vo;

import lombok.Data;

import java.math.BigDecimal;

/**
 * 商品列表视图
 */
@Data
public class ProductListVO {

    private Long id;
    private Long categoryId;
    private String categoryName;
    private String name;
    private String subtitle;
    private BigDecimal price;
    private BigDecimal originalPrice;
    private Integer sales;
    private String mainImage;
    private Integer isHot;
    private Integer isNew;
    private Integer isRecommend;
}
