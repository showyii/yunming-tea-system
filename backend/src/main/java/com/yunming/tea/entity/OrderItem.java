// 声明包路径
package com.yunming.tea.entity;

// MyBatis-Plus 注解
import com.baomidou.mybatisplus.annotation.*;
// Lombok @Data
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 订单明细实体类
 * 映射 order_item 表，存储订单中每个商品的具体信息
 * 使用"快照"方式存储商品名称/图片/价格，避免商品修改后订单数据显示异常
 */
@Data // 自动生成 getter/setter
@TableName("order_item") // 映射数据库表 order_item
public class OrderItem {

    @TableId(type = IdType.AUTO) // 主键自增
    private Long id; // 订单项唯一ID

    private Long orderId; // 所属订单ID（外键，关联 order_info 表）
    private Long productId; // 商品ID（用于溯源，关联 tea_product 表）
    private String productName; // 下单时的商品名称（快照存储）
    private String productImage; // 下单时的商品图片 URL（快照存储）
    private BigDecimal price; // 下单时的商品单价（快照存储）
    private Integer quantity; // 该商品的购买数量
    private BigDecimal totalPrice; // 该订单项的小计金额 = price * quantity

    @TableLogic // 逻辑删除
    private Integer deleted; // 0=未删除，1=已删除

    @TableField(fill = FieldFill.INSERT) // 插入时自动填充
    private LocalDateTime createTime; // 创建时间

    @TableField(fill = FieldFill.INSERT_UPDATE) // 插入和更新时自动填充
    private LocalDateTime updateTime; // 更新时间
}
