// 声明包路径
package com.yunming.tea.mapper;

// MyBatis-Plus BaseMapper，提供通用 CRUD
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
// Admin 实体类
import com.yunming.tea.entity.Admin;
// MyBatis Mapper 注解
import org.apache.ibatis.annotations.Mapper;

/**
 * 管理员表 Mapper 接口
 * 继承 BaseMapper<Admin>，提供对 admin 表的基础数据库操作
 */
@Mapper // 注册为 MyBatis Mapper
public interface AdminMapper extends BaseMapper<Admin> {
    // 继承 BaseMapper 自动获得基础 CRUD 方法
}
