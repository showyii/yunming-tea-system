package com.yunming.tea.controller;

import com.yunming.tea.common.Result;
import com.yunming.tea.service.FavoriteService;
import com.yunming.tea.vo.FavoriteVO;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 收藏控制器（用户端）
 */
@RestController
@RequestMapping("/api/favorites")
public class FavoriteController {

    private final FavoriteService favoriteService;

    public FavoriteController(FavoriteService favoriteService) {
        this.favoriteService = favoriteService;
    }

    /**
     * 收藏商品
     * POST /api/favorites/{productId}
     */
    @PostMapping("/{productId}")
    public Result<String> add(@PathVariable Long productId,
                              @RequestAttribute("userId") Long userId) {
        favoriteService.addFavorite(userId, productId);
        return Result.success("收藏成功");
    }

    /**
     * 取消收藏
     * DELETE /api/favorites/{productId}
     */
    @DeleteMapping("/{productId}")
    public Result<String> remove(@PathVariable Long productId,
                                 @RequestAttribute("userId") Long userId) {
        favoriteService.removeFavorite(userId, productId);
        return Result.success("已取消收藏");
    }

    /**
     * 我的收藏
     * GET /api/favorites
     */
    @GetMapping
    public Result<List<FavoriteVO>> myFavorites(@RequestAttribute("userId") Long userId) {
        return Result.success(favoriteService.getMyFavorites(userId));
    }

    /**
     * 是否已收藏
     * GET /api/favorites/check/{productId}
     */
    @GetMapping("/check/{productId}")
    public Result<Boolean> check(@PathVariable Long productId,
                                  @RequestAttribute("userId") Long userId) {
        return Result.success(favoriteService.isFavorited(userId, productId));
    }
}
