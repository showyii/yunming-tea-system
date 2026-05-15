package com.yunming.tea.service;

import com.yunming.tea.vo.CartVO;

import java.util.List;

/**
 * 购物车服务接口
 */
public interface CartService {

    void add(Long userId, Long productId, Integer quantity);

    void updateQuantity(Long cartId, Integer quantity, Long userId);

    void delete(Long cartId, Long userId);

    List<CartVO> list(Long userId);
}
