// 声明包路径
package com.yunming.tea.vo;

// Lombok @Data
import lombok.Data;

// BigDecimal 用于价格
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 购物车响应 VO
 * 返回购物车中某件商品的详细信息
 */
@Data // 自动生成 getter/setter
public class CartVO {

    private Long id; // 购物车记录ID（用于修改数量/删除操作）
    private Long productId; // 商品ID（用于跳转商品详情页）
    private String productName; // 商品名称
    private String productImage; // 商品图片 URL
    private BigDecimal price; // 商品单价
    private Integer quantity; // 购买数量
    private Integer stock; // 商品当前库存（前端用于判断是否可继续增加数量）
    private Integer selected; // 是否已勾选（前端用于结算时筛选）
    private LocalDateTime createTime; // 加入购物车的时间
}
