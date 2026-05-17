// 声明包路径
package com.yunming.tea.mapper;

// MyBatis-Plus BaseMapper
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
// OrderItem 实体类（订单明细）
import com.yunming.tea.entity.OrderItem;
// MyBatis Mapper 注解
import org.apache.ibatis.annotations.Mapper;

/**
 * 订单明细表 Mapper 接口
 * 继承 BaseMapper<OrderItem>，提供对 order_item 表的 CRUD 操作
 */
@Mapper // MyBatis Mapper 注册
public interface OrderItemMapper extends BaseMapper<OrderItem> {
    // BaseMapper 自动提供增删改查方法
}
