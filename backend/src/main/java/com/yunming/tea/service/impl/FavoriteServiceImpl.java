package com.yunming.tea.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.yunming.tea.entity.Favorite;
import com.yunming.tea.entity.TeaProduct;
import com.yunming.tea.exception.BusinessException;
import com.yunming.tea.mapper.FavoriteMapper;
import com.yunming.tea.mapper.TeaProductMapper;
import com.yunming.tea.service.FavoriteService;
import com.yunming.tea.vo.FavoriteVO;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class FavoriteServiceImpl implements FavoriteService {

    private final FavoriteMapper favoriteMapper;
    private final TeaProductMapper productMapper;

    public FavoriteServiceImpl(FavoriteMapper favoriteMapper, TeaProductMapper productMapper) {
        this.favoriteMapper = favoriteMapper;
        this.productMapper = productMapper;
    }

    @Override
    public void addFavorite(Long userId, Long productId) {
        LambdaQueryWrapper<Favorite> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Favorite::getUserId, userId).eq(Favorite::getProductId, productId);
        if (favoriteMapper.selectCount(wrapper) > 0) {
            throw new BusinessException(400, "已收藏该商品");
        }
        Favorite fav = new Favorite();
        fav.setUserId(userId);
        fav.setProductId(productId);
        favoriteMapper.insert(fav);
    }

    @Override
    public void removeFavorite(Long userId, Long productId) {
        LambdaQueryWrapper<Favorite> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Favorite::getUserId, userId).eq(Favorite::getProductId, productId);
        favoriteMapper.delete(wrapper);
    }

    @Override
    public List<FavoriteVO> getMyFavorites(Long userId) {
        LambdaQueryWrapper<Favorite> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Favorite::getUserId, userId).orderByDesc(Favorite::getCreateTime);
        List<Favorite> favorites = favoriteMapper.selectList(wrapper);

        List<FavoriteVO> result = new ArrayList<>();
        for (Favorite fav : favorites) {
            TeaProduct product = productMapper.selectById(fav.getProductId());
            if (product == null) continue;
            FavoriteVO vo = new FavoriteVO();
            vo.setId(fav.getId());
            vo.setProductId(fav.getProductId());
            vo.setProductName(product.getName());
            vo.setProductImage(product.getMainImage());
            vo.setPrice(product.getPrice());
            vo.setCreateTime(fav.getCreateTime());
            result.add(vo);
        }
        return result;
    }

    @Override
    public boolean isFavorited(Long userId, Long productId) {
        LambdaQueryWrapper<Favorite> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Favorite::getUserId, userId).eq(Favorite::getProductId, productId);
        return favoriteMapper.selectCount(wrapper) > 0;
    }
}
