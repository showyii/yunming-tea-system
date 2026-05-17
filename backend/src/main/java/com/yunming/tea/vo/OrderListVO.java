// 声明包路径
package com.yunming.tea.vo;

// Lombok @Data
import lombok.Data;

// BigDecimal 用于金额
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 订单列表响应 VO
 * 返回用户订单列表的摘要信息
 */
@Data // 自动生成 getter/setter
public class OrderListVO {

    private Long id; // 订单ID
    private String orderNo; // 订单号
    private BigDecimal totalAmount; // 订单总金额
    private BigDecimal payAmount; // 实付金额
    private Integer status; // 订单状态：0=待付款，1=已付款，2=已发货，3=已完成，4=已取消
    private Integer itemCount; // 订单包含的商品种类数量
    private String firstProductImage; // 订单中第一件商品的图片（用于列表缩略图展示）
    private LocalDateTime createTime; // 下单时间
}
