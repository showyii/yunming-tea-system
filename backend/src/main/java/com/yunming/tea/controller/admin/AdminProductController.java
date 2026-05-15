package com.yunming.tea.controller.admin;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yunming.tea.common.Result;
import com.yunming.tea.dto.ProductSearchDTO;
import com.yunming.tea.entity.ProductImage;
import com.yunming.tea.entity.TeaProduct;
import com.yunming.tea.mapper.ProductImageMapper;
import com.yunming.tea.mapper.TeaProductMapper;
import com.yunming.tea.service.ProductImageService;
import com.yunming.tea.service.TeaProductService;
import com.yunming.tea.vo.ProductDetailVO;
import com.yunming.tea.vo.ProductListVO;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/products")
public class AdminProductController {

    private final TeaProductService productService;
    private final TeaProductMapper productMapper;
    private final ProductImageService imageService;
    private final ProductImageMapper imageMapper;

    public AdminProductController(TeaProductService productService, TeaProductMapper productMapper,
                                  ProductImageService imageService, ProductImageMapper imageMapper) {
        this.productService = productService;
        this.productMapper = productMapper;
        this.imageService = imageService;
        this.imageMapper = imageMapper;
    }

    @GetMapping
    public Result<Page<ProductListVO>> list(ProductSearchDTO dto) {
        return Result.success(productService.searchProducts(dto));
    }

    @GetMapping("/{id}")
    public Result<ProductDetailVO> detail(@PathVariable Long id) {
        return Result.success(productService.getProductDetail(id, null));
    }

    @PostMapping
    public Result<TeaProduct> create(@RequestBody TeaProduct product) {
        productMapper.insert(product);
        return Result.success(product);
    }

    @PutMapping("/{id}")
    public Result<String> update(@PathVariable Long id, @RequestBody TeaProduct product) {
        product.setId(id);
        productMapper.updateById(product);
        return Result.success("更新成功");
    }

    @DeleteMapping("/{id}")
    public Result<String> delete(@PathVariable Long id) {
        productMapper.deleteById(id);
        return Result.success("删除成功");
    }

    @GetMapping("/{productId}/images")
    public Result<List<ProductImage>> images(@PathVariable Long productId) {
        return Result.success(imageService.getByProductId(productId));
    }

    @PostMapping("/{productId}/images")
    public Result<String> addImage(@PathVariable Long productId, @RequestBody ProductImage image) {
        image.setProductId(productId);
        imageMapper.insert(image);
        return Result.success("添加成功");
    }

    @DeleteMapping("/images/{imageId}")
    public Result<String> deleteImage(@PathVariable Long imageId) {
        imageMapper.deleteById(imageId);
        return Result.success("删除成功");
    }
}
