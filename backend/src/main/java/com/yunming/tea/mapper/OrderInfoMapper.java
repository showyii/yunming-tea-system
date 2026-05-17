// 声明包路径
package com.yunming.tea.mapper;

// MyBatis-Plus BaseMapper
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
// OrderInfo 实体类（订单）
import com.yunming.tea.entity.OrderInfo;
// MyBatis Mapper 注解
import org.apache.ibatis.annotations.Mapper;

/**
 * 订单表 Mapper 接口
 * 继承 BaseMapper<OrderInfo>，提供对 order_info 表的 CRUD 操作
 */
@Mapper // 注册为 MyBatis Mapper
public interface OrderInfoMapper extends BaseMapper<OrderInfo> {
    // 基础数据库操作由 BaseMapper 提供
}
