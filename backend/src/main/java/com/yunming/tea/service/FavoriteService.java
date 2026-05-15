package com.yunming.tea.service;

import com.yunming.tea.vo.FavoriteVO;

import java.util.List;

/**
 * 收藏服务接口
 */
public interface FavoriteService {

    void addFavorite(Long userId, Long productId);

    void removeFavorite(Long userId, Long productId);

    List<FavoriteVO> getMyFavorites(Long userId);

    boolean isFavorited(Long userId, Long productId);
}
