// 声明包路径
package com.yunming.tea.vo;

// Lombok @Data
import lombok.Data;

// BigDecimal 用于价格
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 收藏列表响应 VO
 * 返回用户收藏的商品信息
 */
@Data // 自动生成 getter/setter
public class FavoriteVO {

    private Long id; // 收藏记录ID（用于取消收藏操作）
    private Long productId; // 被收藏的商品ID
    private String productName; // 商品名称（冗余，方便列表显示）
    private String productImage; // 商品图片 URL（冗余，方便列表显示）
    private BigDecimal price; // 商品当前价格
    private LocalDateTime createTime; // 收藏时间
}
