package com.yunming.tea.service;

import com.yunming.tea.entity.ProductImage;

import java.util.List;

/**
 * 商品图片服务接口
 */
public interface ProductImageService {

    List<ProductImage> getByProductId(Long productId);
}
