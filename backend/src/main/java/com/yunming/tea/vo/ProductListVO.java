// 声明包路径
package com.yunming.tea.vo;

// Lombok @Data
import lombok.Data;

// BigDecimal 用于金额
import java.math.BigDecimal;

/**
 * 商品列表响应 VO
 * 返回商品列表页中的摘要信息（比详情少很多字段，提高传输效率）
 */
@Data // 自动生成 getter/setter
public class ProductListVO {

    private Long id; // 商品ID
    private Long categoryId; // 所属分类ID
    private String categoryName; // 分类名称
    private String name; // 商品名称
    private String subtitle; // 副标题
    private BigDecimal price; // 售价
    private BigDecimal originalPrice; // 原价
    private Integer sales; // 累计销量
    private String mainImage; // 商品主图 URL
    private Integer isHot; // 热销标记
    private Integer isNew; // 新品标记
    private Integer isRecommend; // 推荐标记
}
