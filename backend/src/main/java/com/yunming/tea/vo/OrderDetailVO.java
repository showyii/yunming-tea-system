// 声明包路径
package com.yunming.tea.vo;

// Lombok @Data
import lombok.Data;

// BigDecimal 用于金额
import java.math.BigDecimal;
import java.time.LocalDateTime;
// List 用于存储订单项列表
import java.util.List;

/**
 * 订单详情响应 VO
 * 返回单个订单的完整信息，包含收货信息和所有订单项
 */
@Data // 自动生成 getter/setter
public class OrderDetailVO {

    private Long id; // 订单ID
    private String orderNo; // 订单编号
    private Long userId; // 下单用户ID
    private BigDecimal totalAmount; // 订单总金额
    private BigDecimal payAmount; // 实付金额
    private Integer status; // 订单状态码：0/1/2/3/4
    private String statusText; // 订单状态文本（如"待付款"、"已发货"等，便于前端直接展示）
    private LocalDateTime payTime; // 支付时间
    private String receiverName; // 收货人姓名
    private String receiverPhone; // 收货人电话
    private String receiverAddress; // 收货地址
    private String remark; // 订单备注
    private LocalDateTime createTime; // 下单时间
    private LocalDateTime updateTime; // 最后更新时间
    private List<OrderItemVO> items; // 订单包含的所有商品明细列表
}
