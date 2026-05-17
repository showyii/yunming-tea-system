package com.yunming.tea.service; // 声明包路径，属于服务层接口包

import com.yunming.tea.entity.ProductImage; // 导入商品图片实体类，对应数据库中的商品图片表

import java.util.List; // 导入List集合类，用于返回图片列表

/**
 * 商品图片服务接口
 * <p>
 * 该接口定义了商品图片相关的业务操作规范，包括：
 * 1. 根据商品ID获取该商品的所有图片列表
 * <p>
 * 每个商品可以有多张展示图片（主图+多张附图），
 * 通过sort字段控制图片的显示顺序。
 * 图片按排序字段升序返回，确保前端展示顺序一致。
 *
 * @author yunming
 * @see com.yunming.tea.service.impl.ProductImageServiceImpl
 */
public interface ProductImageService {

    /**
     * 根据商品ID获取商品图片列表
     * <p>
     * 查询指定商品的所有图片记录，按排序字段（sort）升序排列。
     * 返回的图片列表用于商品详情页的图片轮播展示。
     *
     * @param productId 商品ID，用于筛选图片
     * @return 商品图片实体对象列表，按sort字段升序排列
     */
    List<ProductImage> getByProductId(Long productId); // 根据商品ID获取图片列表方法声明
}
