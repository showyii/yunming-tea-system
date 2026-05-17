// 声明包路径
package com.yunming.tea.dto;

// Lombok @Data
import lombok.Data;

/**
 * 商品搜索与筛选请求 DTO
 * 接收前端商品列表页的搜索关键词和筛选条件
 */
@Data // 自动生成 getter/setter
public class ProductSearchDTO {

    private String keyword; // 搜索关键词（模糊匹配商品名称）
    private Long categoryId; // 按分类ID筛选（null 表示不筛选分类）
    private String sort; // 排序方式（如 "price_asc" 价格升序, "sales_desc" 销量降序, "time" 最新）
    private Integer page = 1; // 当前页码，默认第 1 页
    private Integer size = 12; // 每页显示条数，默认 12 条
    private Integer isHot; // 筛选热销商品：1=仅看热销，null=不筛选
    private Integer isNew; // 筛选新品：1=仅看新品，null=不筛选
    private Integer isRecommend; // 筛选推荐：1=仅看推荐，null=不筛选
}
