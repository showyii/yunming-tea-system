package com.yunming.tea.controller;

import com.yunming.tea.common.Result;
import com.yunming.tea.dto.AddCartDTO;
import com.yunming.tea.dto.UpdateCartDTO;
import com.yunming.tea.service.CartService;
import com.yunming.tea.vo.CartVO;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

/**
 * 购物车控制器
 */
@RestController
@RequestMapping("/api/cart")
public class CartController {

    private final CartService cartService;

    public CartController(CartService cartService) {
        this.cartService = cartService;
    }

    /**
     * 加入购物车
     * POST /api/cart
     */
    @PostMapping
    public Result<String> add(@Valid @RequestBody AddCartDTO dto,
                               @RequestAttribute("userId") Long userId) {
        cartService.add(userId, dto.getProductId(), dto.getQuantity());
        return Result.success("已加入购物车");
    }

    /**
     * 修改购物车数量
     * PUT /api/cart
     */
    @PutMapping
    public Result<String> update(@Valid @RequestBody UpdateCartDTO dto,
                                  @RequestAttribute("userId") Long userId) {
        cartService.updateQuantity(dto.getId(), dto.getQuantity(), userId);
        return Result.success("数量已更新");
    }

    /**
     * 删除购物车商品
     * DELETE /api/cart/{id}
     */
    @DeleteMapping("/{id}")
    public Result<String> delete(@PathVariable Long id,
                                  @RequestAttribute("userId") Long userId) {
        cartService.delete(id, userId);
        return Result.success("已移除");
    }

    /**
     * 我的购物车
     * GET /api/cart
     */
    @GetMapping
    public Result<List<CartVO>> list(@RequestAttribute("userId") Long userId) {
        return Result.success(cartService.list(userId));
    }
}
