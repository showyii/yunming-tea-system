package com.yunming.tea.vo;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 分类视图
 */
@Data
public class CategoryVO {

    private Long id;
    private String name;
    private Long parentId;
    private Integer sort;
    private String icon;
    private String description;
    private LocalDateTime createTime;
    private List<CategoryVO> children;
}
