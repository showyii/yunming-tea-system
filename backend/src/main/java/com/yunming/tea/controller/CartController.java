package com.yunming.tea.controller; // 包声明：控制器层

import com.yunming.tea.common.Result; // 统一响应结果封装类
import com.yunming.tea.dto.AddCartDTO; // 加入购物车请求的数据传输对象
import com.yunming.tea.dto.UpdateCartDTO; // 更新购物车请求的数据传输对象
import com.yunming.tea.service.CartService; // 购物车业务逻辑服务
import com.yunming.tea.vo.CartVO; // 购物车视图对象
import org.springframework.web.bind.annotation.*; // Spring MVC 注解：包括 RestController、RequestMapping、PostMapping 等

import javax.validation.Valid; // JSR-303 Bean 校验注解，用于验证请求体参数
import java.util.List; // Java 集合框架的 List 接口

/**
 * 购物车控制器（面向普通用户）
 *
 * 负责处理用户的购物车相关操作，包括：
 * - 将商品加入购物车
 * - 修改购物车中商品的数量
 * - 删除购物车中的商品
 * - 查看购物车列表
 *
 * 所有操作都需要用户登录，通过拦截器注入的 userId 进行身份识别。
 *
 * 映射路径：/api/cart
 */
@RestController // 标识这是一个 RESTful 控制器，所有返回值自动序列化为 JSON
@RequestMapping("/api/cart") // 将控制器映射到 /api/cart 路径下
public class CartController {

    private final CartService cartService; // 购物车业务服务，使用构造器注入

    /**
     * 构造器注入 CartService
     *
     * @param cartService 购物车业务服务实例
     */
    public CartController(CartService cartService) { // 通过构造器注入依赖
        this.cartService = cartService; // 将注入的服务赋值给成员变量
    }

    /**
     * 将商品加入购物车
     *
     * 接收商品 ID 和数量，将指定商品添加到当前登录用户的购物车中。
     * 如果该商品已在购物车中存在，则累加数量。
     *
     * 请求方式：POST /api/cart
     *
     * @param dto    加入购物车的数据传输对象，包含商品 ID（productId）和数量（quantity），由 @Valid 校验
     * @param userId 当前登录用户的 ID，从请求属性中获取（必填）
     * @return Result 包含成功提示信息的统一响应结果
     */
    @PostMapping // 处理 HTTP POST 请求，映射到 /api/cart
    public Result<String> add(@Valid @RequestBody AddCartDTO dto, // @Valid 校验请求体，@RequestBody 将 JSON 反序列化为 AddCartDTO
                               @RequestAttribute("userId") Long userId) { // @RequestAttribute 从请求属性中获取 userId（必填）
        cartService.add(userId, dto.getProductId(), dto.getQuantity()); // 调用业务层将商品加入购物车
        return Result.success("已加入购物车"); // 返回加入成功的提示信息
    }

    /**
     * 修改购物车中商品的数量
     *
     * 更新指定购物车记录中商品的数量。只能修改属于当前用户的购物车记录。
     *
     * 请求方式：PUT /api/cart
     *
     * @param dto    更新购物车的数据传输对象，包含购物车记录 ID 和新数量，由 @Valid 校验
     * @param userId 当前登录用户的 ID，从请求属性中获取（必填）
     * @return Result 包含数量已更新提示的统一响应结果
     */
    @PutMapping // 处理 HTTP PUT 请求，映射到 /api/cart
    public Result<String> update(@Valid @RequestBody UpdateCartDTO dto, // @Valid 校验请求体，@RequestBody 将 JSON 反序列化为 UpdateCartDTO
                                  @RequestAttribute("userId") Long userId) { // @RequestAttribute 从请求属性中获取 userId（必填）
        cartService.updateQuantity(dto.getId(), dto.getQuantity(), userId); // 调用业务层更新购物车商品数量
        return Result.success("数量已更新"); // 返回数量已更新的提示信息
    }

    /**
     * 删除购物车中的商品
     *
     * 从购物车中移除指定的商品记录。只能删除属于当前用户的购物车记录。
     *
     * 请求方式：DELETE /api/cart/{id}
     *
     * @param id     购物车记录的 ID，从 URL 路径中获取
     * @param userId 当前登录用户的 ID，从请求属性中获取（必填）
     * @return Result 包含已移除提示的统一响应结果
     */
    @DeleteMapping("/{id}") // 处理 HTTP DELETE 请求，映射到 /api/cart/{id}
    public Result<String> delete(@PathVariable Long id, // @PathVariable 将 URL 中的 {id} 绑定到方法参数
                                  @RequestAttribute("userId") Long userId) { // @RequestAttribute 从请求属性中获取 userId（必填）
        cartService.delete(id, userId); // 调用业务层删除购物车中的指定商品
        return Result.success("已移除"); // 返回已移除的提示信息
    }

    /**
     * 查看我的购物车
     *
     * 获取当前登录用户购物车中的所有商品列表，包括商品名称、图片、单价、数量和小计等信息。
     *
     * 请求方式：GET /api/cart
     *
     * @param userId 当前登录用户的 ID，从请求属性中获取（必填）
     * @return Result 包含购物车商品列表的统一响应结果
     */
    @GetMapping // 处理 HTTP GET 请求，映射到 /api/cart
    public Result<List<CartVO>> list(@RequestAttribute("userId") Long userId) { // @RequestAttribute 从请求属性中获取 userId（必填）
        return Result.success(cartService.list(userId)); // 调用业务层获取购物车列表并包装为成功响应
    }
}
