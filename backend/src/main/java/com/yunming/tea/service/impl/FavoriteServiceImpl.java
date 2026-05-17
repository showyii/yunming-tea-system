package com.yunming.tea.service.impl; // 声明包路径，属于服务实现层包

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper; // 导入MyBatis-Plus的Lambda查询包装器
import com.yunming.tea.entity.Favorite; // 导入收藏实体类，对应favorite表
import com.yunming.tea.entity.TeaProduct; // 导入茶品商品实体类
import com.yunming.tea.exception.BusinessException; // 导入业务异常类
import com.yunming.tea.mapper.FavoriteMapper; // 导入收藏数据访问层
import com.yunming.tea.mapper.TeaProductMapper; // 导入商品数据访问层
import com.yunming.tea.service.FavoriteService; // 导入收藏服务接口
import com.yunming.tea.vo.FavoriteVO; // 导入收藏视图对象
import org.springframework.stereotype.Service; // 导入Spring的Service注解

import java.util.ArrayList; // 导入ArrayList集合类
import java.util.List; // 导入List接口

/**
 * 收藏服务实现类
 * <p>
 * 实现了{@link FavoriteService}接口中定义的商品收藏相关业务逻辑。
 * 主要功能包括：
 * <ul>
 *   <li>添加收藏：将商品加入用户收藏夹，防止重复收藏</li>
 *   <li>取消收藏：从收藏夹中移除指定商品</li>
 *   <li>查询收藏列表：获取用户所有收藏商品及最新信息</li>
 *   <li>判断收藏状态：检查用户是否已收藏某个商品</li>
 * </ul>
 * <p>
 * 收藏功能是轻量级的用户偏好标记，不需要商品审核。
 * 已下架的商品在收藏列表中会被自动过滤（product为null时跳过）。
 *
 * @author yunming
 * @see FavoriteService
 */
@Service // 将该类标记为Spring的Service组件
public class FavoriteServiceImpl implements FavoriteService {

    private final FavoriteMapper favoriteMapper; // 收藏数据访问对象
    private final TeaProductMapper productMapper; // 商品数据访问对象，用于获取商品最新信息

    /**
     * 构造函数注入依赖
     *
     * @param favoriteMapper 收藏数据访问对象
     * @param productMapper 商品数据访问对象
     */
    public FavoriteServiceImpl(FavoriteMapper favoriteMapper, TeaProductMapper productMapper) {
        this.favoriteMapper = favoriteMapper; // 注入收藏Mapper
        this.productMapper = productMapper; // 注入商品Mapper
    }

    /**
     * 添加商品到收藏夹
     * <p>
     * 将指定商品添加到用户收藏列表。操作前会检查：
     * 该商品是否已经被当前用户收藏过，如果已收藏则抛出异常提示。
     *
     * @param userId 当前用户ID
     * @param productId 要收藏的商品ID
     * @throws BusinessException 如果商品已被收藏，抛出400异常
     */
    @Override // 标识该方法覆盖了父接口中的方法
    public void addFavorite(Long userId, Long productId) {
        // 第1步：检查是否已收藏该商品（防重复收藏）
        LambdaQueryWrapper<Favorite> wrapper = new LambdaQueryWrapper<>(); // 创建查询包装器
        wrapper.eq(Favorite::getUserId, userId) // 条件：用户ID匹配
                .eq(Favorite::getProductId, productId); // 条件：商品ID匹配
        if (favoriteMapper.selectCount(wrapper) > 0) { // 如果查询到的记录数大于0，说明已收藏
            throw new BusinessException(400, "已收藏该商品"); // 抛出重复收藏异常
        }

        // 第2步：创建收藏记录
        Favorite fav = new Favorite(); // 实例化收藏实体对象
        fav.setUserId(userId); // 设置收藏用户ID
        fav.setProductId(productId); // 设置被收藏的商品ID
        favoriteMapper.insert(fav); // 将收藏记录插入数据库
    }

    /**
     * 从收藏夹中移除商品
     * <p>
     * 根据用户ID和商品ID删除对应的收藏记录。
     * 使用delete方法直接按条件删除，不会抛出记录不存在的异常。
     *
     * @param userId 当前用户ID
     * @param productId 要取消收藏的商品ID
     */
    @Override // 标识该方法覆盖了父接口中的方法
    public void removeFavorite(Long userId, Long productId) {
        // 构建删除条件：同时匹配用户ID和商品ID
        LambdaQueryWrapper<Favorite> wrapper = new LambdaQueryWrapper<>(); // 创建查询包装器
        wrapper.eq(Favorite::getUserId, userId) // 条件：用户ID匹配
                .eq(Favorite::getProductId, productId); // 条件：商品ID匹配
        favoriteMapper.delete(wrapper); // 根据条件删除收藏记录（如果没有匹配记录则静默执行）
    }

    /**
     * 查询当前用户的收藏列表
     * <p>
     * 获取用户收藏的所有商品，按收藏时间倒序排列。
     * 遍历收藏记录，查询对应的商品信息，构建包含商品详情的视图对象。
     * 如果商品已被删除或下架，该条收藏记录会被跳过。
     *
     * @param userId 当前用户ID
     * @return 收藏视图对象列表，包含商品基本信息和收藏时间
     */
    @Override // 标识该方法覆盖了父接口中的方法
    public List<FavoriteVO> getMyFavorites(Long userId) {
        // 第1步：查询用户的所有收藏记录，按创建时间倒序
        LambdaQueryWrapper<Favorite> wrapper = new LambdaQueryWrapper<>(); // 创建查询包装器
        wrapper.eq(Favorite::getUserId, userId) // 条件：用户ID匹配
                .orderByDesc(Favorite::getCreateTime); // 排序：按收藏时间倒序（最近收藏的在前）
        List<Favorite> favorites = favoriteMapper.selectList(wrapper); // 执行查询

        // 第2步：遍历收藏记录，构建包含商品信息的视图对象
        List<FavoriteVO> result = new ArrayList<>(); // 创建结果列表
        for (Favorite fav : favorites) { // 遍历每条收藏记录
            TeaProduct product = productMapper.selectById(fav.getProductId()); // 根据商品ID查询商品信息
            if (product == null) continue; // 如果商品已被删除，跳过该条收藏记录
            // 构建收藏视图对象
            FavoriteVO vo = new FavoriteVO(); // 创建视图对象实例
            vo.setId(fav.getId()); // 设置收藏记录ID
            vo.setProductId(fav.getProductId()); // 设置商品ID
            vo.setProductName(product.getName()); // 从商品信息获取商品名称
            vo.setProductImage(product.getMainImage()); // 从商品信息获取商品主图
            vo.setPrice(product.getPrice()); // 从商品信息获取商品当前价格
            vo.setCreateTime(fav.getCreateTime()); // 设置收藏时间
            result.add(vo); // 添加到结果列表
        }
        return result; // 返回收藏视图对象列表
    }

    /**
     * 判断用户是否已收藏某个商品
     * <p>
     * 用于商品详情页实时反馈收藏状态，前端据此显示不同的按钮状态。
     *
     * @param userId 当前用户ID
     * @param productId 要查询的商品ID
     * @return true表示已收藏，false表示未收藏
     */
    @Override // 标识该方法覆盖了父接口中的方法
    public boolean isFavorited(Long userId, Long productId) {
        // 构建查询条件，统计匹配的收藏记录数量
        LambdaQueryWrapper<Favorite> wrapper = new LambdaQueryWrapper<>(); // 创建查询包装器
        wrapper.eq(Favorite::getUserId, userId) // 条件：用户ID匹配
                .eq(Favorite::getProductId, productId); // 条件：商品ID匹配
        return favoriteMapper.selectCount(wrapper) > 0; // 统计匹配记录数，大于0表示已收藏
    }
}
