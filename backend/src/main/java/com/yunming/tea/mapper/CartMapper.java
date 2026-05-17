// 声明包路径
package com.yunming.tea.mapper;

// MyBatis-Plus BaseMapper
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
// Cart 实体类（购物车）
import com.yunming.tea.entity.Cart;
// MyBatis Mapper 注解
import org.apache.ibatis.annotations.Mapper;

/**
 * 购物车表 Mapper 接口
 * 继承 BaseMapper<Cart>，提供对 cart 表的 CRUD 操作
 */
@Mapper // MyBatis Mapper
public interface CartMapper extends BaseMapper<Cart> {
    // 继承 BaseMapper 获得基础增删改查功能
}
