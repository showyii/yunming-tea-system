// 声明包路径
package com.yunming.tea.vo;

// Lombok @Data
import lombok.Data;

// BigDecimal 用于精确金额
import java.math.BigDecimal;
import java.time.LocalDateTime;
// List 用于存储商品图片列表
import java.util.List;

/**
 * 商品详情响应 VO
 * 返回单个商品的完整信息，包括图片列表和收藏状态
 */
@Data // 自动生成 getter/setter
public class ProductDetailVO {

    private Long id; // 商品ID
    private Long categoryId; // 所属分类ID
    private String categoryName; // 所属分类名称（联表查询或冗余字段）
    private String name; // 商品名称
    private String subtitle; // 副标题/简短描述
    private String description; // 详细描述（富文本 HTML）
    private BigDecimal price; // 售价
    private BigDecimal originalPrice; // 原价（划线价）
    private Integer stock; // 库存数量
    private Integer sales; // 累计销量
    private String mainImage; // 商品主图 URL
    private Integer isHot; // 是否热销标记
    private Integer isNew; // 是否新品标记
    private Integer isRecommend; // 是否推荐标记
    private Integer status; // 商品状态：0=下架，1=上架
    private LocalDateTime createTime; // 上架时间
    private List<String> images; // 商品全部图片 URL 列表（用于轮播展示）
    private Boolean isFavorited; // 当前用户是否已收藏该商品
}
