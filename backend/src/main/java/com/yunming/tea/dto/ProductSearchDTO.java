package com.yunming.tea.dto;

import lombok.Data;

/**
 * 商品搜索与筛选请求
 */
@Data
public class ProductSearchDTO {

    private String keyword;
    private Long categoryId;
    private String sort;
    private Integer page = 1;
    private Integer size = 12;
    private Integer isHot;
    private Integer isNew;
    private Integer isRecommend;
}
