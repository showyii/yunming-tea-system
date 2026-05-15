package com.yunming.tea.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yunming.tea.dto.ProductSearchDTO;
import com.yunming.tea.entity.TeaProduct;
import com.yunming.tea.vo.ProductDetailVO;
import com.yunming.tea.vo.ProductListVO;

import java.util.List;

/**
 * 商品服务接口
 */
public interface TeaProductService {

    Page<ProductListVO> searchProducts(ProductSearchDTO dto);

    ProductDetailVO getProductDetail(Long productId, Long userId);

    List<ProductListVO> getHotProducts();

    List<ProductListVO> getNewProducts();

    List<ProductListVO> getRecommendProducts();

    TeaProduct getById(Long id);
}
