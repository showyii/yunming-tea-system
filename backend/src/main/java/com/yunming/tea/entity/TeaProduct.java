// 声明包路径
package com.yunming.tea.entity;

// MyBatis-Plus 注解
import com.baomidou.mybatisplus.annotation.*;
// Lombok @Data
import lombok.Data;

// BigDecimal 用于精确的货币金额计算（避免浮点数精度丢失）
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 茶叶商品实体类
 * 映射 tea_product 表，是商品模块的核心实体
 */
@Data // 自动生成 getter/setter
@TableName("tea_product") // 映射数据库表 tea_product
public class TeaProduct {

    @TableId(type = IdType.AUTO) // 主键自增
    private Long id; // 商品唯一ID

    private Long categoryId; // 所属分类ID（外键，关联 tea_category 表）
    private String name; // 商品名称
    private String subtitle; // 商品副标题/简短描述
    private String description; // 商品详细描述（可包含富文本 HTML）
    private BigDecimal price; // 商品售价（使用 BigDecimal 保证金额精度）
    private BigDecimal originalPrice; // 商品原价，用于展示"划线价"对比
    private Integer stock; // 当前库存数量
    private Integer sales; // 累计销量
    private String mainImage; // 商品主图的 URL 地址
    private Integer isHot; // 标记是否为热销商品：0=否，1=是
    private Integer isNew; // 标记是否为新品：0=否，1=是
    private Integer isRecommend; // 标记是否为推荐商品：0=否，1=是
    private Integer status; // 商品状态：0=已下架，1=上架销售中

    @TableLogic // 逻辑删除
    private Integer deleted; // 0=未删除，1=已删除

    @TableField(fill = FieldFill.INSERT) // 插入时自动填充
    private LocalDateTime createTime; // 商品创建时间

    @TableField(fill = FieldFill.INSERT_UPDATE) // 插入和更新时自动填充
    private LocalDateTime updateTime; // 商品信息最后更新时间
}
