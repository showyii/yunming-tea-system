// 声明包路径
package com.yunming.tea.entity;

// MyBatis-Plus 注解
import com.baomidou.mybatisplus.annotation.*;
// Lombok @Data
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 茶叶分类实体类
 * 映射 tea_category 表，支持两级分类体系
 * 通过 parentId 字段实现父子分类的层级关系：
 * - parentId=0 表示顶级分类（如"绿茶"、"红茶"）
 * - parentId 不为 0 表示子分类，指向父分类的 ID
 */
@Data // 自动生成 getter/setter
@TableName("tea_category") // 映射数据库表 tea_category
public class TeaCategory {

    @TableId(type = IdType.AUTO) // 主键自增
    private Long id; // 分类唯一ID

    private String name; // 分类名称（如"绿茶"、"红茶"、"乌龙茶"等）
    private Long parentId; // 父分类ID：0 表示顶级分类，非 0 表示属于某个父分类
    private Integer sort; // 排序序号，数值越小在列表中越靠前
    private String icon; // 分类图标 URL
    private String description; // 分类描述信息

    @TableLogic // 逻辑删除
    private Integer deleted; // 0=未删除，1=已删除

    @TableField(fill = FieldFill.INSERT) // 插入时自动填充
    private LocalDateTime createTime; // 分类创建时间

    @TableField(fill = FieldFill.INSERT_UPDATE) // 插入和更新时自动填充
    private LocalDateTime updateTime; // 最后更新时间
}
