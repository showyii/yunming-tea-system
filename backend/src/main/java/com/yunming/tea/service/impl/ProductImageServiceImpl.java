package com.yunming.tea.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.yunming.tea.entity.ProductImage;
import com.yunming.tea.mapper.ProductImageMapper;
import com.yunming.tea.service.ProductImageService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductImageServiceImpl implements ProductImageService {

    private final ProductImageMapper imageMapper;

    public ProductImageServiceImpl(ProductImageMapper imageMapper) {
        this.imageMapper = imageMapper;
    }

    @Override
    public List<ProductImage> getByProductId(Long productId) {
        LambdaQueryWrapper<ProductImage> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ProductImage::getProductId, productId)
                .orderByAsc(ProductImage::getSort);
        return imageMapper.selectList(wrapper);
    }
}
