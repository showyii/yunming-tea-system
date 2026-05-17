// 声明包路径
package com.yunming.tea.mapper;

// MyBatis-Plus 基础 Mapper 接口，提供通用 CRUD 方法
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
// 对应的 UserProfile 实体类
import com.yunming.tea.entity.UserProfile;
// MyBatis Mapper 标记注解
import org.apache.ibatis.annotations.Mapper;

/**
 * 用户资料表 Mapper 接口
 * 继承 BaseMapper<UserProfile>，自动拥有对 user_profile 表的增删改查能力
 */
@Mapper // 标记为 MyBatis Mapper，Spring 启动时自动扫描注册
public interface UserProfileMapper extends BaseMapper<UserProfile> {
    // 继承 BaseMapper 后自动拥有 insert/delete/update/selectById 等方法
}
