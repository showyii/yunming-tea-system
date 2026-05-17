package com.yunming.tea.service; // 声明包路径，属于服务层接口包

import com.yunming.tea.vo.FavoriteVO; // 导入收藏视图对象，用于返回给前端的收藏数据结构

import java.util.List; // 导入List集合类，用于返回收藏列表

/**
 * 收藏服务接口
 * <p>
 * 该接口定义了商品收藏相关的业务操作规范，包括：
 * 1. 添加商品到收藏夹
 * 2. 从收藏夹中移除商品
 * 3. 查询当前用户的收藏列表
 * 4. 判断用户是否已收藏某个商品
 * <p>
 * 收藏功能允许用户标记心仪的商品，方便日后快速查找和购买。
 * 同一个商品不能被同一用户重复收藏。
 *
 * @author yunming
 * @see com.yunming.tea.service.impl.FavoriteServiceImpl
 */
public interface FavoriteService {

    /**
     * 添加商品到收藏夹
     * <p>
     * 将指定商品添加到当前用户的收藏列表中。
     * 如果该商品已被收藏，则会抛出业务异常。
     *
     * @param userId 当前用户的ID，用于关联收藏所属用户
     * @param productId 要收藏的商品ID
     * @throws com.yunming.tea.exception.BusinessException 如果商品已被收藏则抛出400异常
     */
    void addFavorite(Long userId, Long productId); // 添加收藏方法声明

    /**
     * 从收藏夹中移除商品
     * <p>
     * 根据用户ID和商品ID删除对应的收藏记录。
     * 如果记录不存在，操作静默执行不会抛出异常。
     *
     * @param userId 当前用户的ID
     * @param productId 要取消收藏的商品ID
     */
    void removeFavorite(Long userId, Long productId); // 取消收藏方法声明

    /**
     * 查询当前用户的收藏列表
     * <p>
     * 获取用户收藏的所有商品，按收藏时间倒序排列。
     * 返回的收藏数据包含商品的名称、图片、价格等信息。
     * 已下架的商品不会出现在收藏列表中。
     *
     * @param userId 当前用户的ID
     * @return 收藏视图对象列表，包含商品基本信息和收藏时间
     */
    List<FavoriteVO> getMyFavorites(Long userId); // 查询收藏列表方法声明

    /**
     * 判断用户是否已收藏某个商品
     * <p>
     * 用于在商品详情页等场景中，判断当前的收藏状态，
     * 以便前端显示正确的收藏按钮状态（已收藏/未收藏）。
     *
     * @param userId 当前用户的ID
     * @param productId 要查询的商品ID
     * @return true表示已收藏，false表示未收藏
     */
    boolean isFavorited(Long userId, Long productId); // 判断是否已收藏方法声明
}
