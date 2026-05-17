package com.yunming.tea.service.impl; // 声明包路径，属于服务实现层包

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper; // 导入MyBatis-Plus的Lambda查询包装器
import com.yunming.tea.entity.Cart; // 导入购物车实体类，对应cart表
import com.yunming.tea.entity.TeaProduct; // 导入茶品商品实体类，用于查询商品库存和状态
import com.yunming.tea.exception.BusinessException; // 导入业务异常类
import com.yunming.tea.mapper.CartMapper; // 导入购物车数据访问层
import com.yunming.tea.mapper.TeaProductMapper; // 导入商品数据访问层
import com.yunming.tea.service.CartService; // 导入购物车服务接口
import com.yunming.tea.vo.CartVO; // 导入购物车视图对象
import org.springframework.stereotype.Service; // 导入Spring的Service注解

import java.util.ArrayList; // 导入ArrayList集合类
import java.util.List; // 导入List接口

/**
 * 购物车服务实现类
 * <p>
 * 实现了{@link CartService}接口中定义的购物车相关业务逻辑。
 * 主要功能包括：
 * <ul>
 *   <li>添加商品到购物车：支持新增和累加两种模式，检查商品状态和库存</li>
 *   <li>更新购物车商品数量：校验库存是否充足</li>
 *   <li>删除购物车中的商品：校验权限（是否属于当前用户）</li>
 *   <li>查询购物车列表：返回用户购物车中所有商品及其最新信息</li>
 * </ul>
 * <p>
 * 库存检查机制：
 * <ul>
 *   <li>添加商品时检查商品是否存在且已上架（status=1）</li>
 *   <li>计算预期数量后检查是否超过库存上限</li>
 *   <li>更新数量时重新检查库存是否充足</li>
 * </ul>
 *
 * @author yunming
 * @see CartService
 */
@Service // 将该类标记为Spring的Service组件
public class CartServiceImpl implements CartService {

    private final CartMapper cartMapper; // 购物车数据访问对象
    private final TeaProductMapper productMapper; // 商品数据访问对象，用于查询商品信息

    /**
     * 构造函数注入依赖
     *
     * @param cartMapper 购物车数据访问对象
     * @param productMapper 商品数据访问对象
     */
    public CartServiceImpl(CartMapper cartMapper, TeaProductMapper productMapper) {
        this.cartMapper = cartMapper; // 注入购物车Mapper
        this.productMapper = productMapper; // 注入商品Mapper
    }

    /**
     * 添加商品到购物车
     * <p>
     * 如果该商品已在购物车中，则累加数量；如果不存在则创建新的购物车记录。
     * 操作流程：
     * <ol>
     *   <li>查询商品信息，校验商品是否存在且已上架</li>
     *   <li>检查购物车中是否已有同款商品</li>
     *   <li>如果已存在：累加数量（需检查累加后是否超过库存）</li>
     *   <li>如果不存在：创建新的购物车记录（需检查数量是否超过库存）</li>
     * </ol>
     *
     * @param userId 当前用户ID
     * @param productId 要添加的商品ID
     * @param quantity 要添加的数量
     * @throws BusinessException 商品不存在、已下架或库存不足时抛出异常
     */
    @Override // 标识该方法覆盖了父接口中的方法
    public void add(Long userId, Long productId, Integer quantity) {
        // 第1步：查询商品信息，校验商品是否有效
        TeaProduct product = productMapper.selectById(productId); // 根据商品ID查询商品
        if (product == null || product.getStatus() == 0) { // 商品不存在或状态为0（已下架）
            throw new BusinessException(400, "商品不存在或已下架"); // 抛出400异常
        }

        // 第2步：检查购物车中是否已存在同一用户同一商品的记录
        LambdaQueryWrapper<Cart> wrapper = new LambdaQueryWrapper<>(); // 创建查询包装器
        wrapper.eq(Cart::getUserId, userId) // 条件：用户ID匹配
                .eq(Cart::getProductId, productId); // 条件：商品ID匹配
        Cart existing = cartMapper.selectOne(wrapper); // 查询是否已有记录

        // 第3步：分别处理"已存在"和"不存在"两种情况
        if (existing != null) { // 购物车中已有该商品
            int newQty = existing.getQuantity() + quantity; // 计算累加后的总数量
            if (newQty > product.getStock()) { // 检查累加后的数量是否超过库存
                throw new BusinessException(400, "库存不足，当前库存：" + product.getStock()); // 库存不足，抛出异常
            }
            existing.setQuantity(newQty); // 更新购物车中的商品数量
            cartMapper.updateById(existing); // 根据ID更新购物车记录
        } else { // 购物车中没有该商品，创建新记录
            if (quantity > product.getStock()) { // 检查添加数量是否超过库存
                throw new BusinessException(400, "库存不足，当前库存：" + product.getStock()); // 库存不足，抛出异常
            }
            Cart cart = new Cart(); // 创建新的购物车实体对象
            cart.setUserId(userId); // 设置用户ID
            cart.setProductId(productId); // 设置商品ID
            cart.setQuantity(quantity); // 设置商品数量
            cart.setSelected(1); // 默认设置为已选中（1表示选中，方便用户直接下单）
            cartMapper.insert(cart); // 将购物车记录插入数据库
        }
    }

    /**
     * 更新购物车商品数量
     * <p>
     * 修改购物车中某个商品的购买数量。操作前会校验：
     * 购物车记录是否存在且属于当前用户，以及商品库存是否足够。
     *
     * @param cartId 购物车记录ID
     * @param quantity 新的商品数量
     * @param userId 操作用户ID，用于权限校验
     * @throws BusinessException 购物车项不存在或库存不足时抛出异常
     */
    @Override // 标识该方法覆盖了父接口中的方法
    public void updateQuantity(Long cartId, Integer quantity, Long userId) {
        // 查询购物车记录
        Cart cart = cartMapper.selectById(cartId); // 根据ID查询购物车记录
        // 校验购物车记录是否存在且属于当前用户
        if (cart == null || !cart.getUserId().equals(userId)) { // 记录不存在或不属于当前用户
            throw new BusinessException(404, "购物车项不存在"); // 抛出404异常
        }
        // 查询商品信息以获取当前库存
        TeaProduct product = productMapper.selectById(cart.getProductId()); // 根据购物车中的商品ID查询商品
        if (quantity > product.getStock()) { // 检查新数量是否超过库存
            throw new BusinessException(400, "库存不足，当前库存：" + product.getStock()); // 库存不足异常
        }
        cart.setQuantity(quantity); // 设置新的商品数量
        cartMapper.updateById(cart); // 根据ID更新购物车记录
    }

    /**
     * 删除购物车中的商品
     * <p>
     * 从购物车中移除指定记录。操作前校验记录是否存在且属于当前用户。
     *
     * @param cartId 购物车记录ID
     * @param userId 操作用户ID，用于权限校验
     * @throws BusinessException 购物车项不存在时抛出404异常
     */
    @Override // 标识该方法覆盖了父接口中的方法
    public void delete(Long cartId, Long userId) {
        // 查询购物车记录
        Cart cart = cartMapper.selectById(cartId); // 根据ID查询购物车记录
        // 校验购物车记录是否存在且属于当前用户
        if (cart == null || !cart.getUserId().equals(userId)) { // 记录不存在或不属于当前用户
            throw new BusinessException(404, "购物车项不存在"); // 抛出404异常
        }
        cartMapper.deleteById(cartId); // 根据ID从数据库中删除购物车记录
    }

    /**
     * 查询当前用户的购物车列表
     * <p>
     * 获取用户购物车中所有商品的最新信息，按创建时间倒序排列。
     * 每条记录包含商品名称、图片、单价、数量、库存状态等。
     * 已下架或已删除的商品不会出现在列表中。
     *
     * @param userId 当前用户ID
     * @return 购物车视图对象列表，包含每个购物车项的商品详细信息
     */
    @Override // 标识该方法覆盖了父接口中的方法
    public List<CartVO> list(Long userId) {
        // 构建查询条件：查询指定用户的购物车记录
        LambdaQueryWrapper<Cart> wrapper = new LambdaQueryWrapper<>(); // 创建查询包装器
        wrapper.eq(Cart::getUserId, userId) // 条件：用户ID匹配
                .orderByDesc(Cart::getCreateTime); // 排序：按创建时间倒序
        List<Cart> carts = cartMapper.selectList(wrapper); // 执行查询，获取购物车列表

        List<CartVO> result = new ArrayList<>(); // 创建结果列表
        for (Cart cart : carts) { // 遍历每条购物车记录
            TeaProduct product = productMapper.selectById(cart.getProductId()); // 根据商品ID查询商品信息
            if (product == null) continue; // 如果商品已被删除，跳过该条记录
            // 构建购物车视图对象
            CartVO vo = new CartVO(); // 创建视图对象
            vo.setId(cart.getId()); // 设置购物车记录ID
            vo.setProductId(cart.getProductId()); // 设置商品ID
            vo.setProductName(product.getName()); // 从商品信息获取商品名称
            vo.setProductImage(product.getMainImage()); // 从商品信息获取商品主图
            vo.setPrice(product.getPrice()); // 从商品信息获取商品单价
            vo.setQuantity(cart.getQuantity()); // 获取购物车中的商品数量
            vo.setStock(product.getStock()); // 从商品信息获取当前库存
            vo.setSelected(cart.getSelected()); // 获取是否选中状态
            vo.setCreateTime(cart.getCreateTime()); // 获取购物车添加时间
            result.add(vo); // 添加到结果列表
        }
        return result; // 返回购物车视图对象列表
    }
}
