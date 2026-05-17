// 声明包路径
package com.yunming.tea.mapper;

// MyBatis-Plus BaseMapper
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
// TeaActivity 实体类（茶文化活动）
import com.yunming.tea.entity.TeaActivity;
// MyBatis Mapper 注解
import org.apache.ibatis.annotations.Mapper;

/**
 * 茶文化活动表 Mapper 接口
 * 继承 BaseMapper<TeaActivity>，提供对 tea_activity 表的 CRUD 操作
 */
@Mapper // 注册 MyBatis Mapper
public interface TeaActivityMapper extends BaseMapper<TeaActivity> {
    // BaseMapper 提供基础增删改查功能
}
