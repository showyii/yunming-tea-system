// 声明包路径
package com.yunming.tea.mapper;

// MyBatis-Plus 基础 Mapper
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
// ProductImage 实体类（商品图片）
import com.yunming.tea.entity.ProductImage;
// MyBatis Mapper 注解
import org.apache.ibatis.annotations.Mapper;

/**
 * 商品图片表 Mapper 接口
 * 继承 BaseMapper<ProductImage>，提供对 product_image 表的 CRUD 操作
 */
@Mapper // 注册 MyBatis Mapper
public interface ProductImageMapper extends BaseMapper<ProductImage> {
    // 基础 CRUD 由 BaseMapper 提供，无需额外定义
}
