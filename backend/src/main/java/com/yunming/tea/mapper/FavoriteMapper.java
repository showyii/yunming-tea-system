// 声明包路径
package com.yunming.tea.mapper;

// MyBatis-Plus BaseMapper
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
// Favorite 实体类（收藏）
import com.yunming.tea.entity.Favorite;
// MyBatis Mapper 注解
import org.apache.ibatis.annotations.Mapper;

/**
 * 收藏表 Mapper 接口
 * 继承 BaseMapper<Favorite>，提供对 favorite 表的 CRUD 操作
 */
@Mapper // 标记为 MyBatis Mapper
public interface FavoriteMapper extends BaseMapper<Favorite> {
    // 继承 BaseMapper 获得基础数据库操作方法
}
