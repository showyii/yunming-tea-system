// 声明包路径
package com.yunming.tea.mapper;

// MyBatis-Plus BaseMapper
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
// ActivitySignup 实体类（活动报名）
import com.yunming.tea.entity.ActivitySignup;
// MyBatis Mapper 注解
import org.apache.ibatis.annotations.Mapper;

/**
 * 活动报名表 Mapper 接口
 * 继承 BaseMapper<ActivitySignup>，提供对 activity_signup 表的 CRUD 操作
 */
@Mapper // MyBatis Mapper 注册
public interface ActivitySignupMapper extends BaseMapper<ActivitySignup> {
    // 继承 BaseMapper 自动获得增删改查方法
}
