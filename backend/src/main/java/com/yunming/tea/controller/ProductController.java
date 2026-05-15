package com.yunming.tea.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yunming.tea.common.Result;
import com.yunming.tea.dto.ProductSearchDTO;
import com.yunming.tea.service.TeaProductService;
import com.yunming.tea.vo.ProductDetailVO;
import com.yunming.tea.vo.ProductListVO;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 商品控制器（用户端）
 */
@RestController
@RequestMapping("/api/products")
public class ProductController {

    private final TeaProductService productService;

    public ProductController(TeaProductService productService) {
        this.productService = productService;
    }

    /**
     * 搜索与筛选商品
     * GET /api/products
     */
    @GetMapping
    public Result<Page<ProductListVO>> search(ProductSearchDTO dto) {
        return Result.success(productService.searchProducts(dto));
    }

    /**
     * 商品详情
     * GET /api/products/{id}
     */
    @GetMapping("/{id}")
    public Result<ProductDetailVO> detail(@PathVariable Long id,
                                           @RequestAttribute(value = "userId", required = false) Long userId) {
        ProductDetailVO vo = productService.getProductDetail(id, userId);
        if (vo == null) {
            return Result.notFound("商品不存在");
        }
        return Result.success(vo);
    }

    /**
     * 热销商品
     * GET /api/products/hot/list
     */
    @GetMapping("/hot/list")
    public Result<List<ProductListVO>> hotProducts() {
        return Result.success(productService.getHotProducts());
    }

    /**
     * 新品推荐
     * GET /api/products/new/list
     */
    @GetMapping("/new/list")
    public Result<List<ProductListVO>> newProducts() {
        return Result.success(productService.getNewProducts());
    }

    /**
     * 推荐商品
     * GET /api/products/recommend/list
     */
    @GetMapping("/recommend/list")
    public Result<List<ProductListVO>> recommendProducts() {
        return Result.success(productService.getRecommendProducts());
    }
}
