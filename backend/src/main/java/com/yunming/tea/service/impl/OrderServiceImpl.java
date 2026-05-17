package com.yunming.tea.service.impl; // 声明包路径，属于服务实现层包

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper; // 导入MyBatis-Plus的Lambda查询包装器
import com.baomidou.mybatisplus.extension.plugins.pagination.Page; // 导入MyBatis-Plus分页对象
import com.yunming.tea.dto.SubmitOrderDTO; // 导入提交订单数据传输对象
import com.yunming.tea.entity.*; // 导入所有实体类（OrderInfo订单信息、OrderItem订单商品明细、Cart购物车、TeaProduct商品等）
import com.yunming.tea.exception.BusinessException; // 导入业务异常类
import com.yunming.tea.mapper.*; // 导入所有数据访问层接口
import com.yunming.tea.service.OrderService; // 导入订单服务接口
import com.yunming.tea.vo.OrderDetailVO; // 导入订单详情视图对象
import com.yunming.tea.vo.OrderItemVO; // 导入订单商品明细视图对象
import com.yunming.tea.vo.OrderListVO; // 导入订单列表视图对象
import org.springframework.stereotype.Service; // 导入Spring的Service注解
import org.springframework.transaction.annotation.Transactional; // 导入Spring事务注解

import java.math.BigDecimal; // 导入BigDecimal，用于精确的货币金额计算
import java.time.LocalDateTime; // 导入LocalDateTime，用于日期时间处理
import java.time.format.DateTimeFormatter; // 导入DateTimeFormatter，用于格式化日期时间字符串
import java.util.ArrayList; // 导入ArrayList集合类
import java.util.List; // 导入List接口
import java.util.UUID; // 导入UUID，用于生成唯一的订单编号
import java.util.stream.Collectors; // 导入Collectors工具类

/**
 * 订单服务实现类
 * <p>
 * 实现了{@link OrderService}接口中定义的订单相关业务逻辑。
 * 主要功能包括：
 * <ul>
 *   <li>提交订单：从购物车生成订单，涉及库存扣减、销量更新、购物车清空等操作</li>
 *   <li>查询订单列表（分页）：支持按订单状态筛选</li>
 *   <li>查询订单详情：包含订单基本信息、收货信息和商品明细</li>
 *   <li>取消订单：恢复库存和销量，将订单状态设为"已取消"</li>
 *   <li>支付订单：模拟支付，将状态更新为"已付款"并记录支付时间</li>
 *   <li>更新订单状态：管理员操作，更改订单状态</li>
 * </ul>
 * <p>
 * 订单状态流转：
 * <pre>
 *   待付款(0) --支付--> 已付款(1) --发货--> 已发货(2) --完成--> 已完成(3)
 *   待付款(0) --取消--> 已取消(4)
 * </pre>
 * <p>
 * 订单编号格式：YM + yyyyMMddHHmmss + 6位随机大写字符
 * 例如：YM20260517143025A3F9X1
 *
 * @author yunming
 * @see OrderService
 */
@Service // 将该类标记为Spring的Service组件
public class OrderServiceImpl implements OrderService {

    private final OrderInfoMapper orderMapper; // 订单信息数据访问对象
    private final OrderItemMapper orderItemMapper; // 订单商品明细数据访问对象
    private final CartMapper cartMapper; // 购物车数据访问对象，提交订单时需要清空购物车
    private final TeaProductMapper productMapper; // 商品数据访问对象，需要扣减库存和增加销量

    /**
     * 构造函数注入依赖
     *
     * @param orderMapper 订单信息数据访问对象
     * @param orderItemMapper 订单商品明细数据访问对象
     * @param cartMapper 购物车数据访问对象
     * @param productMapper 商品数据访问对象
     */
    public OrderServiceImpl(OrderInfoMapper orderMapper, OrderItemMapper orderItemMapper,
                             CartMapper cartMapper, TeaProductMapper productMapper) {
        this.orderMapper = orderMapper; // 注入订单Mapper
        this.orderItemMapper = orderItemMapper; // 注入订单明细Mapper
        this.cartMapper = cartMapper; // 注入购物车Mapper
        this.productMapper = productMapper; // 注入商品Mapper
    }

    /**
     * 提交订单（从购物车生成订单）
     * <p>
     * 这是电商系统的核心操作，涉及多个数据表的原子性更新。
     * 整个方法在{@code @Transactional}事务中执行，确保数据一致性。
     * <p>
     * 操作流程：
     * <ol>
     *   <li>从购物车获取用户已选中的商品列表</li>
     *   <li>逐项校验每个商品是否存在、是否上架、库存是否充足</li>
     *   <li>计算每个商品的小计金额和订单总金额</li>
     *   <li>扣减商品库存、增加商品销量</li>
     *   <li>创建订单记录和订单商品明细记录</li>
     *   <li>清空购物车中已下单的商品</li>
     * </ol>
     *
     * @param userId 下单用户ID
     * @param dto 提交订单数据传输对象，包含收货人、电话、地址、备注
     * @return 新创建的订单ID
     * @throws BusinessException 购物车为空、商品下架、库存不足时抛出异常
     */
    @Override // 标识该方法覆盖了父接口中的方法
    @Transactional // 声明事务：确保库存扣减、订单创建、购物车清空在一个事务中完成
    public Long submit(Long userId, SubmitOrderDTO dto) {
        // 第1步：获取用户购物车中所有已选中的商品
        LambdaQueryWrapper<Cart> cartWrapper = new LambdaQueryWrapper<>(); // 创建购物车查询包装器
        cartWrapper.eq(Cart::getUserId, userId) // 条件：用户ID匹配
                .eq(Cart::getSelected, 1); // 条件：只获取已选中的购物车记录（selected=1）
        List<Cart> cartItems = cartMapper.selectList(cartWrapper); // 执行查询

        // 第2步：检查购物车中是否有选中的商品
        if (cartItems.isEmpty()) { // 如果没有选中任何商品
            throw new BusinessException(400, "购物车中没有选中的商品"); // 提示用户选择商品
        }

        // 第3步：初始化总金额和订单明细列表
        BigDecimal totalAmount = BigDecimal.ZERO; // 订单总金额，从0开始累加
        List<OrderItem> orderItems = new ArrayList<>(); // 订单商品明细列表

        // 第4步：遍历购物车中的每个商品，校验并计算
        for (Cart cart : cartItems) { // 遍历每个选中的购物车项
            TeaProduct product = productMapper.selectById(cart.getProductId()); // 查询商品信息
            // 校验商品是否存在且已上架
            if (product == null || product.getStatus() == 0) { // 商品不存在或已下架
                throw new BusinessException(400, "商品「" + (product != null ? product.getName() : "未知") + "」已下架"); // 提示具体商品
            }
            // 校验库存是否充足
            if (cart.getQuantity() > product.getStock()) { // 购物车数量超过当前库存
                throw new BusinessException(400, "商品「" + product.getName() + "」库存不足，当前库存：" + product.getStock()); // 库存不足异常
            }

            // 计算该商品的小计金额：单价 * 数量
            BigDecimal itemTotal = product.getPrice() // 获取商品单价（BigDecimal类型）
                    .multiply(BigDecimal.valueOf(cart.getQuantity())); // 乘以购买数量
            totalAmount = totalAmount.add(itemTotal); // 累加到订单总金额

            // 构建订单商品明细记录
            OrderItem item = new OrderItem(); // 创建订单明细实体
            item.setProductId(product.getId()); // 设置商品ID
            item.setProductName(product.getName()); // 快照商品名称（下单时的名称，防止后续改名影响）
            item.setProductImage(product.getMainImage()); // 快照商品主图
            item.setPrice(product.getPrice()); // 快照商品单价
            item.setQuantity(cart.getQuantity()); // 购买数量
            item.setTotalPrice(itemTotal); // 该明细项的小计金额
            orderItems.add(item); // 添加到明细列表

            // 扣减商品库存，增加商品销量
            product.setStock(product.getStock() - cart.getQuantity()); // 库存 = 原库存 - 购买数量
            product.setSales(product.getSales() + cart.getQuantity()); // 销量 = 原销量 + 购买数量
            productMapper.updateById(product); // 根据ID更新商品库存和销量
        }

        // 第5步：创建订单主记录
        OrderInfo order = new OrderInfo(); // 创建订单实体对象
        order.setOrderNo(generateOrderNo()); // 生成唯一的订单编号
        order.setUserId(userId); // 设置下单用户ID
        order.setTotalAmount(totalAmount); // 设置订单总金额（原始金额）
        order.setPayAmount(totalAmount); // 设置实付金额（当前等于总金额，如有优惠券可在此扣减）
        order.setStatus(0); // 设置订单状态为"待付款"(0)
        order.setReceiverName(dto.getReceiverName()); // 设置收货人姓名
        order.setReceiverPhone(dto.getReceiverPhone()); // 设置收货人联系电话
        order.setReceiverAddress(dto.getReceiverAddress()); // 设置收货地址
        order.setRemark(dto.getRemark()); // 设置订单备注
        orderMapper.insert(order); // 将订单记录插入数据库，MyBatis-Plus会自动填充create_time

        // 第6步：保存订单商品明细，关联到刚创建的订单
        for (OrderItem item : orderItems) { // 遍历每个订单明细
            item.setOrderId(order.getId()); // 设置关联的订单ID（订单插入后已获得自增ID）
            orderItemMapper.insert(item); // 将明细记录插入数据库
        }

        // 第7步：清空购物车中已下单的商品
        for (Cart cart : cartItems) { // 遍历每个已下单的购物车项
            cartMapper.deleteById(cart.getId()); // 根据ID删除购物车记录
        }

        return order.getId(); // 返回新创建的订单ID
    }

    /**
     * 查询用户订单列表（分页）
     * <p>
     * 支持按订单状态筛选，不传status则查询所有状态的订单。
     * 返回的列表视图包含订单编号、金额、状态和第一个商品的图片。
     *
     * @param userId 查询的用户ID
     * @param status 订单状态筛选条件，传null表示查询所有状态
     * @param pageNum 当前页码
     * @param pageSize 每页大小
     * @return 分页的订单列表视图对象
     */
    @Override // 标识该方法覆盖了父接口中的方法
    public Page<OrderListVO> list(Long userId, Integer status, Integer pageNum, Integer pageSize) {
        // 第1步：构建查询条件
        LambdaQueryWrapper<OrderInfo> wrapper = new LambdaQueryWrapper<>(); // 创建查询包装器
        wrapper.eq(OrderInfo::getUserId, userId); // 条件：用户ID匹配
        if (status != null) { // 如果传入了状态筛选条件
            wrapper.eq(OrderInfo::getStatus, status); // 添加状态等值条件
        }
        wrapper.orderByDesc(OrderInfo::getCreateTime); // 排序：按创建时间倒序（最新订单在前）

        // 第2步：执行分页查询
        Page<OrderInfo> page = new Page<>(pageNum, pageSize); // 创建分页对象
        Page<OrderInfo> result = orderMapper.selectPage(page, wrapper); // 执行分页查询

        // 第3步：将订单实体转换为订单列表视图对象
        List<OrderListVO> records = result.getRecords().stream().map(o -> { // 遍历订单并映射为视图对象
            OrderListVO vo = new OrderListVO(); // 创建列表视图对象
            vo.setId(o.getId()); // 设置订单ID
            vo.setOrderNo(o.getOrderNo()); // 设置订单编号
            vo.setTotalAmount(o.getTotalAmount()); // 设置订单总金额
            vo.setPayAmount(o.getPayAmount()); // 设置实付金额
            vo.setStatus(o.getStatus()); // 设置订单状态
            vo.setCreateTime(o.getCreateTime()); // 设置下单时间

            // 查询订单的商品明细，获取商品数量和第一张商品图片
            LambdaQueryWrapper<OrderItem> itemWrapper = new LambdaQueryWrapper<>(); // 创建明细查询包装器
            itemWrapper.eq(OrderItem::getOrderId, o.getId()); // 条件：订单ID匹配
            List<OrderItem> items = orderItemMapper.selectList(itemWrapper); // 查询该订单的所有商品明细
            vo.setItemCount(items.size()); // 设置商品种类数量
            if (!items.isEmpty()) { // 如果有商品明细
                vo.setFirstProductImage(items.get(0).getProductImage()); // 取第一个商品的图片作为订单封面图
            }
            return vo; // 返回构建好的视图对象
        }).collect(Collectors.toList()); // 收集为List

        // 第4步：构建分页视图对象
        Page<OrderListVO> voPage = new Page<>(); // 创建分页视图对象
        voPage.setRecords(records); // 设置记录列表
        voPage.setTotal(result.getTotal()); // 保持总记录数
        voPage.setCurrent(result.getCurrent()); // 保持当前页码
        voPage.setSize(result.getSize()); // 保持每页大小
        return voPage; // 返回分页视图对象
    }

    /**
     * 查询订单详情
     * <p>
     * 根据订单ID查询订单的完整信息，包括订单基本信息、收货信息、
     * 支付状态和所有商品明细。非订单所属用户无权查看。
     * 管理员通过传入userId=null可以查看任意订单。
     *
     * @param orderId 订单ID
     * @param userId 操作用户ID，用于权限校验（管理员传null跳过）
     * @return 订单详情视图对象，包含完整的订单信息和商品明细
     * @throws BusinessException 订单不存在时抛出404，无权查看时抛出403
     */
    @Override // 标识该方法覆盖了父接口中的方法
    public OrderDetailVO detail(Long orderId, Long userId) {
        // 第1步：查询订单主记录
        OrderInfo order = orderMapper.selectById(orderId); // 根据ID查询订单
        if (order == null) { // 订单不存在
            throw new BusinessException(404, "订单不存在"); // 抛出404异常
        }
        // 第2步：权限校验（管理员userId为null时跳过）
        if (userId != null && !order.getUserId().equals(userId)) { // 普通用户且不是订单所属人
            throw new BusinessException(403, "无权查看此订单"); // 抛出403无权限异常
        }

        // 第3步：填充订单基本信息
        OrderDetailVO vo = new OrderDetailVO(); // 创建详情视图对象
        vo.setId(order.getId()); // 设置订单ID
        vo.setOrderNo(order.getOrderNo()); // 设置订单编号
        vo.setUserId(order.getUserId()); // 设置下单用户ID
        vo.setTotalAmount(order.getTotalAmount()); // 设置订单总金额
        vo.setPayAmount(order.getPayAmount()); // 设置实付金额
        vo.setStatus(order.getStatus()); // 设置订单状态
        vo.setStatusText(getStatusText(order.getStatus())); // 将状态码转换为中文文本
        vo.setPayTime(order.getPayTime()); // 设置支付时间（未支付时为null）
        vo.setReceiverName(order.getReceiverName()); // 设置收货人姓名
        vo.setReceiverPhone(order.getReceiverPhone()); // 设置收货人电话
        vo.setReceiverAddress(order.getReceiverAddress()); // 设置收货地址
        vo.setRemark(order.getRemark()); // 设置订单备注
        vo.setCreateTime(order.getCreateTime()); // 设置下单时间
        vo.setUpdateTime(order.getUpdateTime()); // 设置最后更新时间

        // 第4步：查询并填充订单商品明细
        LambdaQueryWrapper<OrderItem> itemWrapper = new LambdaQueryWrapper<>(); // 创建明细查询包装器
        itemWrapper.eq(OrderItem::getOrderId, order.getId()); // 条件：订单ID匹配
        List<OrderItem> items = orderItemMapper.selectList(itemWrapper); // 查询该订单的所有商品明细

        // 第5步：将明细实体转换为明细视图对象
        List<OrderItemVO> itemVOs = items.stream().map(i -> { // 遍历并映射每个明细
            OrderItemVO iv = new OrderItemVO(); // 创建明细视图对象
            iv.setId(i.getId()); // 设置明细ID
            iv.setProductId(i.getProductId()); // 设置商品ID
            iv.setProductName(i.getProductName()); // 设置商品名称（下单时的快照）
            iv.setProductImage(i.getProductImage()); // 设置商品图片（下单时的快照）
            iv.setPrice(i.getPrice()); // 设置商品单价
            iv.setQuantity(i.getQuantity()); // 设置购买数量
            iv.setTotalPrice(i.getTotalPrice()); // 设置该明细的小计金额
            return iv; // 返回明细视图对象
        }).collect(Collectors.toList()); // 收集为List

        vo.setItems(itemVOs); // 将明细列表设置到订单详情中
        return vo; // 返回订单详情视图对象
    }

    /**
     * 取消订单
     * <p>
     * 用户取消待付款状态的订单。操作包括：
     * 将订单状态更新为"已取消"(4)，同时恢复订单中商品的库存和销量。
     * 只有处于"待付款"(0)状态的订单可以取消。
     * <p>
     * 恢复库存逻辑：将订单中每个商品的购买数量加回库存，销量相应扣减。
     *
     * @param orderId 订单ID
     * @param userId 操作用户ID，用于权限校验
     * @throws BusinessException 订单不存在、无权操作、状态不允许时抛出异常
     */
    @Override // 标识该方法覆盖了父接口中的方法
    @Transactional // 声明事务：确保状态更新和库存恢复的原子性
    public void cancel(Long orderId, Long userId) {
        // 第1步：查询订单
        OrderInfo order = orderMapper.selectById(orderId); // 根据ID查询订单
        if (order == null) { // 订单不存在
            throw new BusinessException(404, "订单不存在"); // 抛出404异常
        }
        // 第2步：校验订单所属权
        if (!order.getUserId().equals(userId)) { // 不是当前用户的订单
            throw new BusinessException(403, "无权操作此订单"); // 抛出403异常
        }
        // 第3步：校验订单状态是否允许取消（只有待付款订单可以取消）
        if (order.getStatus() != 0) { // 订单状态不是"待付款"
            throw new BusinessException(400, "只能取消待付款订单"); // 提示只能取消待付款订单
        }

        // 第4步：将订单状态更新为"已取消"(4)
        order.setStatus(4); // 设置状态为已取消
        orderMapper.updateById(order); // 根据ID更新订单

        // 第5步：恢复订单中商品的库存和销量
        restoreStock(orderId); // 调用私有方法恢复库存
    }

    /**
     * 支付订单（模拟支付）
     * <p>
     * 模拟在线支付，将订单状态从"待付款"更新为"已付款"，
     * 并记录支付时间为当前时间。
     * 实际生产项目中应接入支付宝、微信支付等第三方支付接口。
     *
     * @param orderId 订单ID
     * @param userId 操作用户ID，用于权限校验
     * @throws BusinessException 订单不存在、无权操作、状态不允许时抛出异常
     */
    @Override // 标识该方法覆盖了父接口中的方法
    @Transactional // 声明事务
    public void pay(Long orderId, Long userId) {
        // 第1步：查询订单
        OrderInfo order = orderMapper.selectById(orderId); // 根据ID查询订单
        if (order == null) { // 订单不存在
            throw new BusinessException(404, "订单不存在"); // 抛出404异常
        }
        // 第2步：校验订单所属权
        if (!order.getUserId().equals(userId)) { // 不是当前用户的订单
            throw new BusinessException(403, "无权操作此订单"); // 抛出403异常
        }
        // 第3步：校验订单状态是否允许支付（只有待付款订单可以支付）
        if (order.getStatus() != 0) { // 订单状态不是"待付款"
            throw new BusinessException(400, "订单状态不允许支付"); // 状态不允许支付异常
        }

        // 第4步：更新订单状态为"已付款"并记录支付时间
        order.setStatus(1); // 设置状态为"已付款"
        order.setPayTime(LocalDateTime.now()); // 设置支付时间为当前时间
        orderMapper.updateById(order); // 根据ID更新订单
    }

    /**
     * 更新订单状态（管理员操作）
     * <p>
     * 管理员用于更新订单的物流状态或其他状态变更。
     * 不限状态流转控制，由管理员自行管理状态变更。
     *
     * @param orderId 订单ID
     * @param status 目标状态值
     * @throws BusinessException 订单不存在时抛出404异常
     */
    @Override // 标识该方法覆盖了父接口中的方法
    public void updateStatus(Long orderId, Integer status) {
        // 查询订单
        OrderInfo order = orderMapper.selectById(orderId); // 根据ID查询订单
        if (order == null) { // 订单不存在
            throw new BusinessException(404, "订单不存在"); // 抛出404异常
        }
        order.setStatus(status); // 设置目标状态
        orderMapper.updateById(order); // 根据ID更新订单
    }

    /**
     * 恢复订单中商品的库存和销量
     * <p>
     * 当订单取消时调用，将订单中每个商品的数量加回库存，
     * 同时扣减对应销量。使用Math.max确保销量不会变成负数。
     *
     * @param orderId 订单ID
     */
    private void restoreStock(Long orderId) { // 私有方法，仅在类内部使用
        // 查询订单的所有商品明细
        LambdaQueryWrapper<OrderItem> wrapper = new LambdaQueryWrapper<>(); // 创建查询包装器
        wrapper.eq(OrderItem::getOrderId, orderId); // 条件：订单ID匹配
        List<OrderItem> items = orderItemMapper.selectList(wrapper); // 查询该订单的所有商品明细

        // 遍历每个商品明细，恢复库存
        for (OrderItem item : items) { // 遍历明细
            TeaProduct product = productMapper.selectById(item.getProductId()); // 根据商品ID查询商品
            if (product != null) { // 如果商品还存在（未被删除）
                product.setStock(product.getStock() + item.getQuantity()); // 库存恢复：原库存 + 退回数量
                product.setSales(Math.max(0, product.getSales() - item.getQuantity())); // 销量扣减：确保不小于0
                productMapper.updateById(product); // 更新商品记录
            }
        }
    }

    /**
     * 生成唯一的订单编号
     * <p>
     * 格式：YM + 年月日时分秒(14位) + UUID前6位大写字母（共21位）
     * <p>
     * 示例：YM20260517143025A3F9X1
     * <ul>
     *   <li>YM：云茗拼音首字母，品牌前缀</li>
     *   <li>20260517143025：年月日时分秒（14位）</li>
     *   <li>A3F9X1：UUID前6位大写字母，增加唯一性</li>
     * </ul>
     *
     * @return 生成的订单编号字符串
     */
    private String generateOrderNo() { // 私有方法，生成订单编号
        // 格式化当前时间为"yyyyMMddHHmmss"格式（年月日时分秒共14位）
        String datePart = LocalDateTime.now() // 获取当前日期时间
                .format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss")); // 格式化为年月日时分秒
        // 生成UUID并截取前6位作为随机码，去掉横线转大写
        String uuidPart = UUID.randomUUID() // 生成随机UUID
                .toString() // 转为字符串格式
                .replace("-", "") // 去掉所有横线字符
                .substring(0, 6) // 截取前6个字符
                .toUpperCase(); // 转为大写字母（更易读）
        return "YM" + datePart + uuidPart; // 拼接品牌前缀 + 时间 + 随机码，组合成完整订单编号
    }

    /**
     * 将订单状态码转换为中文文本
     *
     * @param status 订单状态码：0-待付款, 1-已付款, 2-已发货, 3-已完成, 4-已取消
     * @return 对应的中文状态文本
     */
    private String getStatusText(Integer status) { // 私有方法，状态码转中文
        switch (status) { // 根据状态码匹配
            case 0: return "待付款"; // 状态0：等待用户付款
            case 1: return "已付款"; // 状态1：用户已付款，等待发货
            case 2: return "已发货"; // 状态2：商品已发货，正在运输中
            case 3: return "已完成"; // 状态3：用户确认收货，订单完成
            case 4: return "已取消"; // 状态4：订单已取消
            default: return "未知"; // 未知状态码
        }
    }
}
