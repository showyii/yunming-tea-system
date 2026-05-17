// 声明包路径
package com.yunming.tea.entity;

// MyBatis-Plus 注解
import com.baomidou.mybatisplus.annotation.*;
// Lombok @Data
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 商品图片实体类
 * 映射 product_image 表，一个商品可关联多张图片
 */
@Data // 自动生成 getter/setter
@TableName("product_image") // 映射数据库表 product_image
public class ProductImage {

    @TableId(type = IdType.AUTO) // 主键自增
    private Long id; // 图片记录ID

    private Long productId; // 所属商品的ID（外键，关联 tea_product 表）
    private String imageUrl; // 图片文件的 URL 地址
    private Integer sort; // 排序序号，数值越小显示越靠前
    private Integer isMain; // 是否设置为商品主图：0=否，1=是

    @TableLogic // 逻辑删除
    private Integer deleted; // 0=未删除，1=已删除

    @TableField(fill = FieldFill.INSERT) // 插入时自动填充
    private LocalDateTime createTime; // 图片上传时间

    @TableField(fill = FieldFill.INSERT_UPDATE) // 插入和更新时自动填充
    private LocalDateTime updateTime; // 更新时间
}
