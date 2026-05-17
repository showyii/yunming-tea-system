package com.yunming.tea.controller; // 包声明：控制器层

import com.yunming.tea.common.Result; // 统一响应结果封装类
import com.yunming.tea.service.FavoriteService; // 收藏业务逻辑服务
import com.yunming.tea.vo.FavoriteVO; // 收藏视图对象
import org.springframework.web.bind.annotation.*; // Spring MVC 注解：包括 RestController、RequestMapping、PostMapping 等

import java.util.List; // Java 集合框架的 List 接口

/**
 * 收藏控制器（用户端，面向普通用户）
 *
 * 负责处理用户对茶产品的收藏相关操作，包括：
 * - 收藏商品
 * - 取消收藏
 * - 查看我的收藏列表
 * - 检查是否已收藏某个商品
 *
 * 所有操作都需要用户登录后才能执行。
 *
 * 映射路径：/api/favorites
 */
@RestController // 标识这是一个 RESTful 控制器，所有返回值自动序列化为 JSON
@RequestMapping("/api/favorites") // 将控制器映射到 /api/favorites 路径下
public class FavoriteController {

    private final FavoriteService favoriteService; // 收藏业务服务，使用构造器注入

    /**
     * 构造器注入 FavoriteService
     *
     * @param favoriteService 收藏业务服务实例
     */
    public FavoriteController(FavoriteService favoriteService) { // 通过构造器注入依赖
        this.favoriteService = favoriteService; // 将注入的服务赋值给成员变量
    }

    /**
     * 收藏商品
     *
     * 将指定商品添加到当前用户的收藏列表中。如果已经收藏，则静默处理（不报错）。
     *
     * 请求方式：POST /api/favorites/{productId}
     *
     * @param productId 要收藏的商品 ID，从 URL 路径中获取
     * @param userId    当前登录用户的 ID，从请求属性中获取（必填）
     * @return Result 包含收藏成功提示的统一响应结果
     */
    @PostMapping("/{productId}") // 处理 HTTP POST 请求，映射到 /api/favorites/{productId}
    public Result<String> add(@PathVariable Long productId, // @PathVariable 将 URL 中的 {productId} 绑定到方法参数
                              @RequestAttribute("userId") Long userId) { // @RequestAttribute 从请求属性中获取 userId（必填）
        favoriteService.addFavorite(userId, productId); // 调用业务层添加收藏
        return Result.success("收藏成功"); // 返回收藏成功的提示信息
    }

    /**
     * 取消收藏
     *
     * 将指定商品从当前用户的收藏列表中移除。
     *
     * 请求方式：DELETE /api/favorites/{productId}
     *
     * @param productId 要取消收藏的商品 ID，从 URL 路径中获取
     * @param userId    当前登录用户的 ID，从请求属性中获取（必填）
     * @return Result 包含已取消收藏提示的统一响应结果
     */
    @DeleteMapping("/{productId}") // 处理 HTTP DELETE 请求，映射到 /api/favorites/{productId}
    public Result<String> remove(@PathVariable Long productId, // @PathVariable 将 URL 中的 {productId} 绑定到方法参数
                                 @RequestAttribute("userId") Long userId) { // @RequestAttribute 从请求属性中获取 userId（必填）
        favoriteService.removeFavorite(userId, productId); // 调用业务层取消收藏
        return Result.success("已取消收藏"); // 返回已取消收藏的提示信息
    }

    /**
     * 查看我的收藏列表
     *
     * 获取当前登录用户收藏的所有茶产品列表。
     *
     * 请求方式：GET /api/favorites
     *
     * @param userId 当前登录用户的 ID，从请求属性中获取（必填）
     * @return Result 包含用户收藏商品列表的统一响应结果
     */
    @GetMapping // 处理 HTTP GET 请求，映射到 /api/favorites
    public Result<List<FavoriteVO>> myFavorites(@RequestAttribute("userId") Long userId) { // @RequestAttribute 从请求属性中获取 userId（必填）
        return Result.success(favoriteService.getMyFavorites(userId)); // 调用业务层获取收藏列表并包装为成功响应
    }

    /**
     * 检查是否已收藏某个商品
     *
     * 用于前端判断当前用户是否已经收藏了指定商品，通常用于改变收藏按钮的样式。
     *
     * 请求方式：GET /api/favorites/check/{productId}
     *
     * @param productId 要检查的商品 ID，从 URL 路径中获取
     * @param userId    当前登录用户的 ID，从请求属性中获取（必填）
     * @return Result 包含布尔值的统一响应结果，true 表示已收藏，false 表示未收藏
     */
    @GetMapping("/check/{productId}") // 处理 HTTP GET 请求，映射到 /api/favorites/check/{productId}
    public Result<Boolean> check(@PathVariable Long productId, // @PathVariable 将 URL 中的 {productId} 绑定到方法参数
                                  @RequestAttribute("userId") Long userId) { // @RequestAttribute 从请求属性中获取 userId（必填）
        return Result.success(favoriteService.isFavorited(userId, productId)); // 调用业务层检查收藏状态并包装为成功响应
    }
}
