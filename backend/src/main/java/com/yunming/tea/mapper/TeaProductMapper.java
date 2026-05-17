// 声明包路径
package com.yunming.tea.mapper;

// MyBatis-Plus BaseMapper
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
// TeaProduct 实体类（茶叶商品）
import com.yunming.tea.entity.TeaProduct;
// MyBatis Mapper 注解
import org.apache.ibatis.annotations.Mapper;

/**
 * 茶叶商品表 Mapper 接口
 * 继承 BaseMapper<TeaProduct>，提供对 tea_product 表的基础 CRUD 操作
 */
@Mapper // MyBatis Mapper 注册
public interface TeaProductMapper extends BaseMapper<TeaProduct> {
    // 通过继承 BaseMapper 自动拥有增删改查方法
}
