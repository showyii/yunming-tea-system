// 声明包路径
package com.yunming.tea.vo;

// Lombok @Data
import lombok.Data;

// BigDecimal 精确金额
import java.math.BigDecimal;

/**
 * 订单项视图 VO
 * 订单中单个商品的明细信息
 */
@Data // 自动生成 getter/setter
public class OrderItemVO {

    private Long id; // 订单项ID
    private Long productId; // 商品ID（用于点击跳转商品详情）
    private String productName; // 商品名称（下单时的快照值）
    private String productImage; // 商品图片（下单时的快照值）
    private BigDecimal price; // 下单时的商品单价
    private Integer quantity; // 购买数量
    private BigDecimal totalPrice; // 该订单项的小计金额 = price * quantity
}
