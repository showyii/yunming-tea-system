package com.yunming.tea.service; // 声明包路径，属于服务层接口包

import com.yunming.tea.vo.CartVO; // 导入购物车视图对象，用于返回给前端的购物车数据结构

import java.util.List; // 导入List集合类，用于返回购物车商品列表

/**
 * 购物车服务接口
 * <p>
 * 该接口定义了购物车相关的业务操作规范，包括：
 * 1. 添加商品到购物车
 * 2. 更新购物车中商品的数量
 * 3. 删除购物车中的商品
 * 4. 查询当前用户的购物车列表
 * <p>
 * 购物车是用户下单前的临时商品存放处，用户可以在此管理待购买的商品。
 * 添加商品时需检查商品是否存在、是否上架、库存是否充足。
 * 如果同一商品已在购物车中，则累加数量而非创建新记录。
 *
 * @author yunming
 * @see com.yunming.tea.service.impl.CartServiceImpl
 */
public interface CartService {

    /**
     * 添加商品到购物车
     * <p>
     * 将指定商品添加到当前用户的购物车中。如果该商品已在购物车中存在，
     * 则累加数量；如果不存在则创建新的购物车记录。
     * 添加前会校验商品是否存在、是否上架（status=1）以及库存是否充足。
     *
     * @param userId 当前用户的ID，用于关联购物车所属用户
     * @param productId 要添加的商品ID
     * @param quantity 要添加的商品数量
     */
    void add(Long userId, Long productId, Integer quantity); // 添加商品到购物车方法声明

    /**
     * 更新购物车商品数量
     * <p>
     * 修改购物车中某个商品的数量，需要校验：
     * 1. 购物车记录是否存在且属于当前用户
     * 2. 商品库存是否足够满足新的数量
     *
     * @param cartId 购物车记录ID，用于定位要修改的购物车项
     * @param quantity 新的商品数量
     * @param userId 操作用户ID，用于权限校验
     */
    void updateQuantity(Long cartId, Integer quantity, Long userId); // 更新购物车数量方法声明

    /**
     * 删除购物车中的商品
     * <p>
     * 从购物车中移除指定的商品记录，需要校验：
     * 1. 购物车记录是否存在且属于当前用户
     *
     * @param cartId 购物车记录ID，用于定位要删除的购物车项
     * @param userId 操作用户ID，用于权限校验
     */
    void delete(Long cartId, Long userId); // 删除购物车商品方法声明

    /**
     * 查询当前用户的购物车列表
     * <p>
     * 获取用户购物车中的所有商品，按创建时间倒序排列。
     * 返回的数据包含商品名称、图片、单价、数量、库存状态等信息。
     *
     * @param userId 当前用户的ID
     * @return 购物车视图对象列表，包含每个购物车项的商品详细信息
     */
    List<CartVO> list(Long userId); // 查询购物车列表方法声明
}
