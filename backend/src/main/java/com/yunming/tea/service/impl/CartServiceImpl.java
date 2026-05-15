package com.yunming.tea.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.yunming.tea.entity.Cart;
import com.yunming.tea.entity.TeaProduct;
import com.yunming.tea.exception.BusinessException;
import com.yunming.tea.mapper.CartMapper;
import com.yunming.tea.mapper.TeaProductMapper;
import com.yunming.tea.service.CartService;
import com.yunming.tea.vo.CartVO;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class CartServiceImpl implements CartService {

    private final CartMapper cartMapper;
    private final TeaProductMapper productMapper;

    public CartServiceImpl(CartMapper cartMapper, TeaProductMapper productMapper) {
        this.cartMapper = cartMapper;
        this.productMapper = productMapper;
    }

    @Override
    public void add(Long userId, Long productId, Integer quantity) {
        TeaProduct product = productMapper.selectById(productId);
        if (product == null || product.getStatus() == 0) {
            throw new BusinessException(400, "商品不存在或已下架");
        }

        LambdaQueryWrapper<Cart> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Cart::getUserId, userId).eq(Cart::getProductId, productId);
        Cart existing = cartMapper.selectOne(wrapper);

        if (existing != null) {
            int newQty = existing.getQuantity() + quantity;
            if (newQty > product.getStock()) {
                throw new BusinessException(400, "库存不足，当前库存：" + product.getStock());
            }
            existing.setQuantity(newQty);
            cartMapper.updateById(existing);
        } else {
            if (quantity > product.getStock()) {
                throw new BusinessException(400, "库存不足，当前库存：" + product.getStock());
            }
            Cart cart = new Cart();
            cart.setUserId(userId);
            cart.setProductId(productId);
            cart.setQuantity(quantity);
            cart.setSelected(1);
            cartMapper.insert(cart);
        }
    }

    @Override
    public void updateQuantity(Long cartId, Integer quantity, Long userId) {
        Cart cart = cartMapper.selectById(cartId);
        if (cart == null || !cart.getUserId().equals(userId)) {
            throw new BusinessException(404, "购物车项不存在");
        }
        TeaProduct product = productMapper.selectById(cart.getProductId());
        if (quantity > product.getStock()) {
            throw new BusinessException(400, "库存不足，当前库存：" + product.getStock());
        }
        cart.setQuantity(quantity);
        cartMapper.updateById(cart);
    }

    @Override
    public void delete(Long cartId, Long userId) {
        Cart cart = cartMapper.selectById(cartId);
        if (cart == null || !cart.getUserId().equals(userId)) {
            throw new BusinessException(404, "购物车项不存在");
        }
        cartMapper.deleteById(cartId);
    }

    @Override
    public List<CartVO> list(Long userId) {
        LambdaQueryWrapper<Cart> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Cart::getUserId, userId).orderByDesc(Cart::getCreateTime);
        List<Cart> carts = cartMapper.selectList(wrapper);

        List<CartVO> result = new ArrayList<>();
        for (Cart cart : carts) {
            TeaProduct product = productMapper.selectById(cart.getProductId());
            if (product == null) continue;
            CartVO vo = new CartVO();
            vo.setId(cart.getId());
            vo.setProductId(cart.getProductId());
            vo.setProductName(product.getName());
            vo.setProductImage(product.getMainImage());
            vo.setPrice(product.getPrice());
            vo.setQuantity(cart.getQuantity());
            vo.setStock(product.getStock());
            vo.setSelected(cart.getSelected());
            vo.setCreateTime(cart.getCreateTime());
            result.add(vo);
        }
        return result;
    }
}
