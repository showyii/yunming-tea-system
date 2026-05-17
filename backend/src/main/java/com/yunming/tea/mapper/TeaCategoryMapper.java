// 声明包路径
package com.yunming.tea.mapper;

// MyBatis-Plus 基础 Mapper
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
// TeaCategory 实体类（茶叶分类）
import com.yunming.tea.entity.TeaCategory;
// MyBatis Mapper 注解
import org.apache.ibatis.annotations.Mapper;

/**
 * 茶叶分类表 Mapper 接口
 * 继承 BaseMapper<TeaCategory>，提供对 tea_category 表的 CRUD 操作
 */
@Mapper // MyBatis Mapper 标记
public interface TeaCategoryMapper extends BaseMapper<TeaCategory> {
    // 继承 BaseMapper，无需手写 SQL 即可完成基本数据库操作
}
