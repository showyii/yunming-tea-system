// 声明包路径
package com.yunming.tea.entity;

// MyBatis-Plus 注解
import com.baomidou.mybatisplus.annotation.*;
// Lombok @Data
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 商品评论实体类
 * 映射 comment 表，存储用户对商品的评价
 */
@Data // 自动生成 getter/setter
@TableName("comment") // 映射数据库表 comment
public class Comment {

    @TableId(type = IdType.AUTO) // 主键自增
    private Long id; // 评论唯一ID

    private Long userId; // 发表评论的用户ID（外键，关联 user 表）
    private Long productId; // 被评价商品的ID（外键，关联 tea_product 表）
    private Long orderId; // 关联的订单ID（外键，确保只有购买过的用户才能评论）
    private String content; // 评论的正文内容
    private Integer rating; // 评分，范围 1~5 星
    private Integer status; // 评论审核状态：0=待审核，1=审核通过/显示，2=审核拒绝/隐藏

    @TableLogic // 逻辑删除
    private Integer deleted; // 0=未删除，1=已删除

    @TableField(fill = FieldFill.INSERT) // 插入时自动填充
    private LocalDateTime createTime; // 评论发表时间

    @TableField(fill = FieldFill.INSERT_UPDATE) // 插入和更新时自动填充
    private LocalDateTime updateTime; // 最后更新时间
}
