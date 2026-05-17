// 声明包路径
package com.yunming.tea.entity;

// MyBatis-Plus 注解
import com.baomidou.mybatisplus.annotation.*;
// Lombok @Data
import lombok.Data;

// BigDecimal 用于精确金额计算
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 订单实体类
 * 映射 order_info 表，存储用户订单的主信息
 * 一个订单可包含多个 OrderItem（订单明细项）
 */
@Data // 自动生成 getter/setter
@TableName("order_info") // 映射数据库表 order_info
public class OrderInfo {

    @TableId(type = IdType.AUTO) // 主键自增
    private Long id; // 订单唯一ID

    private String orderNo; // 订单编号（唯一，如：20240101120000 + 随机数）
    private Long userId; // 下单用户的ID（外键，关联 user 表）
    private BigDecimal totalAmount; // 订单总金额（商品原价合计）
    private BigDecimal payAmount; // 实际支付金额
    private Integer status; // 订单状态：0=待付款，1=已付款/待发货，2=已发货，3=已完成，4=已取消
    private LocalDateTime payTime; // 支付完成时间
    private String receiverName; // 收货人姓名
    private String receiverPhone; // 收货人联系电话
    private String receiverAddress; // 收货详细地址
    private String remark; // 用户下单时的备注信息

    @TableLogic // 逻辑删除
    private Integer deleted; // 0=未删除，1=已删除

    @TableField(fill = FieldFill.INSERT) // 插入时自动填充
    private LocalDateTime createTime; // 下单时间

    @TableField(fill = FieldFill.INSERT_UPDATE) // 插入和更新时自动填充
    private LocalDateTime updateTime; // 订单最后更新时间
}
