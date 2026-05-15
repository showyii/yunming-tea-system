package com.yunming.tea.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yunming.tea.dto.ProductSearchDTO;
import com.yunming.tea.entity.ProductImage;
import com.yunming.tea.entity.TeaCategory;
import com.yunming.tea.entity.TeaProduct;
import com.yunming.tea.mapper.TeaCategoryMapper;
import com.yunming.tea.mapper.TeaProductMapper;
import com.yunming.tea.service.FavoriteService;
import com.yunming.tea.service.ProductImageService;
import com.yunming.tea.service.TeaProductService;
import com.yunming.tea.vo.ProductDetailVO;
import com.yunming.tea.vo.ProductListVO;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class TeaProductServiceImpl implements TeaProductService {

    private final TeaProductMapper productMapper;
    private final TeaCategoryMapper categoryMapper;
    private final ProductImageService imageService;
    private final FavoriteService favoriteService;

    public TeaProductServiceImpl(TeaProductMapper productMapper, TeaCategoryMapper categoryMapper,
                                  ProductImageService imageService, FavoriteService favoriteService) {
        this.productMapper = productMapper;
        this.categoryMapper = categoryMapper;
        this.imageService = imageService;
        this.favoriteService = favoriteService;
    }

    @Override
    public Page<ProductListVO> searchProducts(ProductSearchDTO dto) {
        LambdaQueryWrapper<TeaProduct> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(TeaProduct::getStatus, 1);

        if (dto.getKeyword() != null && !dto.getKeyword().isBlank()) {
            wrapper.like(TeaProduct::getName, dto.getKeyword());
        }
        if (dto.getCategoryId() != null) {
            wrapper.eq(TeaProduct::getCategoryId, dto.getCategoryId());
        }
        if (dto.getIsHot() != null) {
            wrapper.eq(TeaProduct::getIsHot, dto.getIsHot());
        }
        if (dto.getIsNew() != null) {
            wrapper.eq(TeaProduct::getIsNew, dto.getIsNew());
        }
        if (dto.getIsRecommend() != null) {
            wrapper.eq(TeaProduct::getIsRecommend, dto.getIsRecommend());
        }

        if ("price_asc".equals(dto.getSort())) {
            wrapper.orderByAsc(TeaProduct::getPrice);
        } else if ("price_desc".equals(dto.getSort())) {
            wrapper.orderByDesc(TeaProduct::getPrice);
        } else if ("sales_desc".equals(dto.getSort())) {
            wrapper.orderByDesc(TeaProduct::getSales);
        } else {
            wrapper.orderByDesc(TeaProduct::getCreateTime);
        }

        Page<TeaProduct> page = new Page<>(dto.getPage(), dto.getSize());
        Page<TeaProduct> result = productMapper.selectPage(page, wrapper);

        List<ProductListVO> records = result.getRecords().stream().map(this::toListVO).collect(Collectors.toList());

        Page<ProductListVO> voPage = new Page<>();
        voPage.setRecords(records);
        voPage.setTotal(result.getTotal());
        voPage.setCurrent(result.getCurrent());
        voPage.setSize(result.getSize());
        voPage.setPages(result.getPages());
        return voPage;
    }

    @Override
    public ProductDetailVO getProductDetail(Long productId, Long userId) {
        TeaProduct product = productMapper.selectById(productId);
        if (product == null) return null;

        ProductDetailVO vo = toDetailVO(product);

        TeaCategory category = categoryMapper.selectById(product.getCategoryId());
        if (category != null) vo.setCategoryName(category.getName());

        List<ProductImage> images = imageService.getByProductId(productId);
        vo.setImages(images.stream().map(ProductImage::getImageUrl).collect(Collectors.toList()));

        if (userId != null) {
            vo.setIsFavorited(favoriteService.isFavorited(userId, productId));
        }

        return vo;
    }

    @Override
    public List<ProductListVO> getHotProducts() {
        LambdaQueryWrapper<TeaProduct> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(TeaProduct::getStatus, 1)
                .eq(TeaProduct::getIsHot, 1)
                .orderByDesc(TeaProduct::getSales)
                .last("LIMIT 8");
        return productMapper.selectList(wrapper).stream().map(this::toListVO).collect(Collectors.toList());
    }

    @Override
    public List<ProductListVO> getNewProducts() {
        LambdaQueryWrapper<TeaProduct> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(TeaProduct::getStatus, 1)
                .eq(TeaProduct::getIsNew, 1)
                .orderByDesc(TeaProduct::getCreateTime)
                .last("LIMIT 8");
        return productMapper.selectList(wrapper).stream().map(this::toListVO).collect(Collectors.toList());
    }

    @Override
    public List<ProductListVO> getRecommendProducts() {
        LambdaQueryWrapper<TeaProduct> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(TeaProduct::getStatus, 1)
                .eq(TeaProduct::getIsRecommend, 1)
                .orderByDesc(TeaProduct::getCreateTime)
                .last("LIMIT 8");
        return productMapper.selectList(wrapper).stream().map(this::toListVO).collect(Collectors.toList());
    }

    @Override
    public TeaProduct getById(Long id) {
        return productMapper.selectById(id);
    }

    private ProductListVO toListVO(TeaProduct p) {
        ProductListVO vo = new ProductListVO();
        vo.setId(p.getId());
        vo.setCategoryId(p.getCategoryId());
        vo.setName(p.getName());
        vo.setSubtitle(p.getSubtitle());
        vo.setPrice(p.getPrice());
        vo.setOriginalPrice(p.getOriginalPrice());
        vo.setSales(p.getSales());
        vo.setMainImage(p.getMainImage());
        vo.setIsHot(p.getIsHot());
        vo.setIsNew(p.getIsNew());
        vo.setIsRecommend(p.getIsRecommend());
        TeaCategory cat = categoryMapper.selectById(p.getCategoryId());
        if (cat != null) vo.setCategoryName(cat.getName());
        return vo;
    }

    private ProductDetailVO toDetailVO(TeaProduct p) {
        ProductDetailVO vo = new ProductDetailVO();
        vo.setId(p.getId());
        vo.setCategoryId(p.getCategoryId());
        vo.setName(p.getName());
        vo.setSubtitle(p.getSubtitle());
        vo.setDescription(p.getDescription());
        vo.setPrice(p.getPrice());
        vo.setOriginalPrice(p.getOriginalPrice());
        vo.setStock(p.getStock());
        vo.setSales(p.getSales());
        vo.setMainImage(p.getMainImage());
        vo.setIsHot(p.getIsHot());
        vo.setIsNew(p.getIsNew());
        vo.setIsRecommend(p.getIsRecommend());
        vo.setStatus(p.getStatus());
        vo.setCreateTime(p.getCreateTime());
        return vo;
    }
}
