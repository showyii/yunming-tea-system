// 声明包路径
package com.yunming.tea.entity;

// MyBatis-Plus 注解
import com.baomidou.mybatisplus.annotation.*;
// Lombok @Data
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 收藏实体类
 * 映射 favorite 表，记录用户收藏的商品
 */
@Data // 自动生成 getter/setter
@TableName("favorite") // 映射数据库表 favorite
public class Favorite {

    @TableId(type = IdType.AUTO) // 主键自增
    private Long id; // 收藏记录的唯一ID

    private Long userId; // 用户ID（外键，关联 user 表）
    private Long productId; // 被收藏商品的ID（外键，关联 tea_product 表）

    @TableLogic // 逻辑删除（取消收藏时更新此字段而非物理删除）
    private Integer deleted; // 0=未删除（收藏中），1=已删除（已取消收藏）

    @TableField(fill = FieldFill.INSERT) // 插入时自动填充
    private LocalDateTime createTime; // 收藏时间

    @TableField(fill = FieldFill.INSERT_UPDATE) // 插入和更新时自动填充
    private LocalDateTime updateTime; // 更新时间
}
