package com.yunming.tea.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 商品详情视图
 */
@Data
public class ProductDetailVO {

    private Long id;
    private Long categoryId;
    private String categoryName;
    private String name;
    private String subtitle;
    private String description;
    private BigDecimal price;
    private BigDecimal originalPrice;
    private Integer stock;
    private Integer sales;
    private String mainImage;
    private Integer isHot;
    private Integer isNew;
    private Integer isRecommend;
    private Integer status;
    private LocalDateTime createTime;
    private List<String> images;
    private Boolean isFavorited;
}
