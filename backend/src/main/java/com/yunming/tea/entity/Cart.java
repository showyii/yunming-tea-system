// 声明包路径
package com.yunming.tea.entity;

// MyBatis-Plus 注解
import com.baomidou.mybatisplus.annotation.*;
// Lombok @Data
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 购物车实体类
 * 映射 cart 表，记录用户加入购物车的商品
 */
@Data // 自动生成 getter/setter
@TableName("cart") // 映射数据库表 cart
public class Cart {

    @TableId(type = IdType.AUTO) // 主键自增
    private Long id; // 购物车项的唯一ID

    private Long userId; // 用户ID（外键，关联 user 表）
    private Long productId; // 商品ID（外键，关联 tea_product 表）
    private Integer quantity; // 该商品的购买数量
    private Integer selected; // 是否已被用户勾选：0=未勾选，1=已勾选（用于结算时筛选）

    @TableLogic // 逻辑删除
    private Integer deleted; // 0=未删除，1=已删除

    @TableField(fill = FieldFill.INSERT) // 插入时自动填充
    private LocalDateTime createTime; // 加入购物车的时间

    @TableField(fill = FieldFill.INSERT_UPDATE) // 插入和更新时自动填充
    private LocalDateTime updateTime; // 最后更新时间（如修改数量）
}
