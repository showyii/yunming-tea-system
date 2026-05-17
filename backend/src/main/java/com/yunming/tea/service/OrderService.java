package com.yunming.tea.service; // 声明包路径，属于服务层接口包

import com.baomidou.mybatisplus.extension.plugins.pagination.Page; // 导入MyBatis-Plus分页对象，用于分页查询
import com.yunming.tea.dto.SubmitOrderDTO; // 导入提交订单数据传输对象，包含收货信息和备注
import com.yunming.tea.vo.OrderDetailVO; // 导入订单详情视图对象，用于返回订单完整信息
import com.yunming.tea.vo.OrderListVO; // 导入订单列表视图对象，用于返回订单摘要信息

/**
 * 订单服务接口
 * <p>
 * 该接口定义了订单相关的业务操作规范，包括：
 * 1. 提交订单（从购物车生成订单）
 * 2. 查询用户订单列表（支持按状态筛选，分页）
 * 3. 查询订单详情（包含订单商品明细）
 * 4. 取消订单
 * 5. 支付订单（模拟支付）
 * 6. 更新订单状态（管理员操作）
 * <p>
 * 订单是电商系统的核心业务流程，涉及购物车、商品库存、支付状态等多个环节。
 * 订单状态流转：待付款(0) -> 已付款(1) -> 已发货(2) -> 已完成(3)，或待付款(0) -> 已取消(4)
 *
 * @author yunming
 * @see com.yunming.tea.service.impl.OrderServiceImpl
 */
public interface OrderService {

    /**
     * 提交订单
     * <p>
     * 从用户购物车中获取所有已选中的商品，生成一个订单。
     * 操作流程：
     * 1. 获取购物车中已选中的商品
     * 2. 逐项校验商品是否存在、是否上架、库存是否充足
     * 3. 计算订单总金额
     * 4. 扣减商品库存、增加商品销量
     * 5. 创建订单记录和订单商品明细
     * 6. 清空购物车中已下单的商品
     * <p>
     * 整个操作在一个事务中执行，确保数据一致性。
     *
     * @param userId 下单用户的ID
     * @param dto 提交订单数据传输对象，包含receiverName（收货人）、receiverPhone（收货电话）、receiverAddress（收货地址）、remark（订单备注）
     * @return 新创建的订单ID，用于跳转到订单详情或支付页面
     */
    Long submit(Long userId, SubmitOrderDTO dto); // 提交订单方法声明

    /**
     * 查询用户订单列表
     * <p>
     * 根据用户ID查询订单列表，支持按订单状态筛选，按创建时间倒序排列。
     * 返回的订单列表包含订单编号、总金额、状态和第一个商品的图片。
     *
     * @param userId 查询的用户ID
     * @param status 订单状态筛选条件（0:待付款, 1:已付款, 2:已发货, 3:已完成, 4:已取消），传null表示查询全部
     * @param page 当前页码，从1开始
     * @param size 每页显示的订单数量
     * @return 分页的订单列表视图对象
     */
    Page<OrderListVO> list(Long userId, Integer status, Integer page, Integer size); // 查询订单列表方法声明

    /**
     * 查询订单详情
     * <p>
     * 根据订单ID查询订单的完整信息，包括订单基本信息、收货信息、
     * 支付状态和订单中的所有商品明细。
     * 非订单所属用户无权查看（管理员通过userId=null可查看任意订单）。
     *
     * @param orderId 订单ID
     * @param userId 操作用户ID，用于权限校验（管理员传null跳过）
     * @return 订单详情视图对象，包含完整的订单信息和商品明细列表
     */
    OrderDetailVO detail(Long orderId, Long userId); // 查询订单详情方法声明

    /**
     * 取消订单
     * <p>
     * 用户取消待付款状态的订单，操作包括：
     * 1. 校验订单是否存在且属于当前用户
     * 2. 校验订单状态为"待付款"（只有待付款订单可以取消）
     * 3. 将订单状态更新为"已取消"
     * 4. 恢复订单中商品的库存和销量
     *
     * @param orderId 订单ID
     * @param userId 操作用户ID，用于权限校验
     */
    void cancel(Long orderId, Long userId); // 取消订单方法声明

    /**
     * 支付订单（模拟支付）
     * <p>
     * 模拟在线支付流程，将订单状态从"待付款"更新为"已付款"，
     * 并记录支付时间为当前时间。
     * 实际项目中应接入第三方支付接口（如微信支付、支付宝等）。
     *
     * @param orderId 订单ID
     * @param userId 操作用户ID，用于权限校验
     */
    void pay(Long orderId, Long userId); // 支付订单方法声明

    /**
     * 更新订单状态（管理员操作）
     * <p>
     * 管理员用于更新订单的物流状态或其他状态变更。
     * 通常用于将订单从"已付款"更新为"已发货"或"已完成"。
     *
     * @param orderId 订单ID
     * @param status 目标状态值（0:待付款, 1:已付款, 2:已发货, 3:已完成, 4:已取消）
     */
    void updateStatus(Long orderId, Integer status); // 更新订单状态方法声明
}
