package com.yunming.tea.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yunming.tea.dto.SubmitOrderDTO;
import com.yunming.tea.entity.*;
import com.yunming.tea.exception.BusinessException;
import com.yunming.tea.mapper.*;
import com.yunming.tea.service.OrderService;
import com.yunming.tea.vo.OrderDetailVO;
import com.yunming.tea.vo.OrderItemVO;
import com.yunming.tea.vo.OrderListVO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class OrderServiceImpl implements OrderService {

    private final OrderInfoMapper orderMapper;
    private final OrderItemMapper orderItemMapper;
    private final CartMapper cartMapper;
    private final TeaProductMapper productMapper;

    public OrderServiceImpl(OrderInfoMapper orderMapper, OrderItemMapper orderItemMapper,
                             CartMapper cartMapper, TeaProductMapper productMapper) {
        this.orderMapper = orderMapper;
        this.orderItemMapper = orderItemMapper;
        this.cartMapper = cartMapper;
        this.productMapper = productMapper;
    }

    @Override
    @Transactional
    public Long submit(Long userId, SubmitOrderDTO dto) {
        LambdaQueryWrapper<Cart> cartWrapper = new LambdaQueryWrapper<>();
        cartWrapper.eq(Cart::getUserId, userId).eq(Cart::getSelected, 1);
        List<Cart> cartItems = cartMapper.selectList(cartWrapper);

        if (cartItems.isEmpty()) {
            throw new BusinessException(400, "购物车中没有选中的商品");
        }

        BigDecimal totalAmount = BigDecimal.ZERO;
        List<OrderItem> orderItems = new ArrayList<>();

        for (Cart cart : cartItems) {
            TeaProduct product = productMapper.selectById(cart.getProductId());
            if (product == null || product.getStatus() == 0) {
                throw new BusinessException(400, "商品「" + (product != null ? product.getName() : "未知") + "」已下架");
            }
            if (cart.getQuantity() > product.getStock()) {
                throw new BusinessException(400, "商品「" + product.getName() + "」库存不足，当前库存：" + product.getStock());
            }

            BigDecimal itemTotal = product.getPrice().multiply(BigDecimal.valueOf(cart.getQuantity()));
            totalAmount = totalAmount.add(itemTotal);

            OrderItem item = new OrderItem();
            item.setProductId(product.getId());
            item.setProductName(product.getName());
            item.setProductImage(product.getMainImage());
            item.setPrice(product.getPrice());
            item.setQuantity(cart.getQuantity());
            item.setTotalPrice(itemTotal);
            orderItems.add(item);

            product.setStock(product.getStock() - cart.getQuantity());
            product.setSales(product.getSales() + cart.getQuantity());
            productMapper.updateById(product);
        }

        OrderInfo order = new OrderInfo();
        order.setOrderNo(generateOrderNo());
        order.setUserId(userId);
        order.setTotalAmount(totalAmount);
        order.setPayAmount(totalAmount);
        order.setStatus(0);
        order.setReceiverName(dto.getReceiverName());
        order.setReceiverPhone(dto.getReceiverPhone());
        order.setReceiverAddress(dto.getReceiverAddress());
        order.setRemark(dto.getRemark());
        orderMapper.insert(order);

        for (OrderItem item : orderItems) {
            item.setOrderId(order.getId());
            orderItemMapper.insert(item);
        }

        for (Cart cart : cartItems) {
            cartMapper.deleteById(cart.getId());
        }

        return order.getId();
    }

    @Override
    public Page<OrderListVO> list(Long userId, Integer status, Integer pageNum, Integer pageSize) {
        LambdaQueryWrapper<OrderInfo> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(OrderInfo::getUserId, userId);
        if (status != null) {
            wrapper.eq(OrderInfo::getStatus, status);
        }
        wrapper.orderByDesc(OrderInfo::getCreateTime);

        Page<OrderInfo> page = new Page<>(pageNum, pageSize);
        Page<OrderInfo> result = orderMapper.selectPage(page, wrapper);

        List<OrderListVO> records = result.getRecords().stream().map(o -> {
            OrderListVO vo = new OrderListVO();
            vo.setId(o.getId());
            vo.setOrderNo(o.getOrderNo());
            vo.setTotalAmount(o.getTotalAmount());
            vo.setPayAmount(o.getPayAmount());
            vo.setStatus(o.getStatus());
            vo.setCreateTime(o.getCreateTime());

            LambdaQueryWrapper<OrderItem> itemWrapper = new LambdaQueryWrapper<>();
            itemWrapper.eq(OrderItem::getOrderId, o.getId());
            List<OrderItem> items = orderItemMapper.selectList(itemWrapper);
            vo.setItemCount(items.size());
            if (!items.isEmpty()) {
                vo.setFirstProductImage(items.get(0).getProductImage());
            }
            return vo;
        }).collect(Collectors.toList());

        Page<OrderListVO> voPage = new Page<>();
        voPage.setRecords(records);
        voPage.setTotal(result.getTotal());
        voPage.setCurrent(result.getCurrent());
        voPage.setSize(result.getSize());
        return voPage;
    }

    @Override
    public OrderDetailVO detail(Long orderId, Long userId) {
        OrderInfo order = orderMapper.selectById(orderId);
        if (order == null) {
            throw new BusinessException(404, "订单不存在");
        }
        if (userId != null && !order.getUserId().equals(userId)) {
            throw new BusinessException(403, "无权查看此订单");
        }

        OrderDetailVO vo = new OrderDetailVO();
        vo.setId(order.getId());
        vo.setOrderNo(order.getOrderNo());
        vo.setUserId(order.getUserId());
        vo.setTotalAmount(order.getTotalAmount());
        vo.setPayAmount(order.getPayAmount());
        vo.setStatus(order.getStatus());
        vo.setStatusText(getStatusText(order.getStatus()));
        vo.setPayTime(order.getPayTime());
        vo.setReceiverName(order.getReceiverName());
        vo.setReceiverPhone(order.getReceiverPhone());
        vo.setReceiverAddress(order.getReceiverAddress());
        vo.setRemark(order.getRemark());
        vo.setCreateTime(order.getCreateTime());
        vo.setUpdateTime(order.getUpdateTime());

        LambdaQueryWrapper<OrderItem> itemWrapper = new LambdaQueryWrapper<>();
        itemWrapper.eq(OrderItem::getOrderId, order.getId());
        List<OrderItem> items = orderItemMapper.selectList(itemWrapper);

        List<OrderItemVO> itemVOs = items.stream().map(i -> {
            OrderItemVO iv = new OrderItemVO();
            iv.setId(i.getId());
            iv.setProductId(i.getProductId());
            iv.setProductName(i.getProductName());
            iv.setProductImage(i.getProductImage());
            iv.setPrice(i.getPrice());
            iv.setQuantity(i.getQuantity());
            iv.setTotalPrice(i.getTotalPrice());
            return iv;
        }).collect(Collectors.toList());

        vo.setItems(itemVOs);
        return vo;
    }

    @Override
    @Transactional
    public void cancel(Long orderId, Long userId) {
        OrderInfo order = orderMapper.selectById(orderId);
        if (order == null) {
            throw new BusinessException(404, "订单不存在");
        }
        if (!order.getUserId().equals(userId)) {
            throw new BusinessException(403, "无权操作此订单");
        }
        if (order.getStatus() != 0) {
            throw new BusinessException(400, "只能取消待付款订单");
        }

        order.setStatus(4);
        orderMapper.updateById(order);

        restoreStock(orderId);
    }

    @Override
    @Transactional
    public void pay(Long orderId, Long userId) {
        OrderInfo order = orderMapper.selectById(orderId);
        if (order == null) {
            throw new BusinessException(404, "订单不存在");
        }
        if (!order.getUserId().equals(userId)) {
            throw new BusinessException(403, "无权操作此订单");
        }
        if (order.getStatus() != 0) {
            throw new BusinessException(400, "订单状态不允许支付");
        }

        order.setStatus(1);
        order.setPayTime(LocalDateTime.now());
        orderMapper.updateById(order);
    }

    @Override
    public void updateStatus(Long orderId, Integer status) {
        OrderInfo order = orderMapper.selectById(orderId);
        if (order == null) {
            throw new BusinessException(404, "订单不存在");
        }
        order.setStatus(status);
        orderMapper.updateById(order);
    }

    private void restoreStock(Long orderId) {
        LambdaQueryWrapper<OrderItem> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(OrderItem::getOrderId, orderId);
        List<OrderItem> items = orderItemMapper.selectList(wrapper);
        for (OrderItem item : items) {
            TeaProduct product = productMapper.selectById(item.getProductId());
            if (product != null) {
                product.setStock(product.getStock() + item.getQuantity());
                product.setSales(Math.max(0, product.getSales() - item.getQuantity()));
                productMapper.updateById(product);
            }
        }
    }

    private String generateOrderNo() {
        String datePart = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        String uuidPart = UUID.randomUUID().toString().replace("-", "").substring(0, 6).toUpperCase();
        return "YM" + datePart + uuidPart;
    }

    private String getStatusText(Integer status) {
        switch (status) {
            case 0: return "待付款";
            case 1: return "已付款";
            case 2: return "已发货";
            case 3: return "已完成";
            case 4: return "已取消";
            default: return "未知";
        }
    }
}
