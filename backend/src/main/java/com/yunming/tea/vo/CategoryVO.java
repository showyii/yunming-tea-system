// 声明包路径
package com.yunming.tea.vo;

// Lombok @Data
import lombok.Data;

import java.time.LocalDateTime;
// List 用于存储子分类列表
import java.util.List;

/**
 * 分类响应 VO
 * 支持树形结构：每个分类可以包含子分类列表（children）
 * 用于前端展示商品分类导航
 */
@Data // 自动生成 getter/setter
public class CategoryVO {

    private Long id; // 分类ID
    private String name; // 分类名称（如"绿茶"、"红茶"）
    private Long parentId; // 父分类ID，0 表示顶级分类
    private Integer sort; // 排序序号
    private String icon; // 分类图标 URL
    private String description; // 分类描述
    private LocalDateTime createTime; // 创建时间
    private List<CategoryVO> children; // 子分类列表（递归结构，用于构建分类树）
}
