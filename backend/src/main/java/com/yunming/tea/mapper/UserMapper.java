// 声明包路径
package com.yunming.tea.mapper;

// 导入 MyBatis-Plus 的 BaseMapper 接口，提供基础的 CRUD 方法
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
// 导入对应的实体类 User
import com.yunming.tea.entity.User;
// MyBatis 的 Mapper 注解，将该接口标记为 MyBatis Mapper
import org.apache.ibatis.annotations.Mapper;

/**
 * 用户表 Mapper 接口
 * 继承 MyBatis-Plus 的 BaseMapper<User>，自动获得增删改查等基础数据库操作方法
 * 无需编写 XML 或注解 SQL，BaseMapper 会根据实体类自动生成对应的 SQL
 */
@Mapper // 标记为 MyBatis 的 Mapper 接口，Spring 会自动扫描并创建代理实现
public interface UserMapper extends BaseMapper<User> { // 继承 BaseMapper，泛型指定为 User 实体
    // 当前无自定义方法，所有 CRUD 操作均由 BaseMapper 提供
}
