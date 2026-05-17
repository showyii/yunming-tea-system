// 声明包路径
package com.yunming.tea.mapper;

// MyBatis-Plus BaseMapper
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
// Room 实体类（茶室包间）
import com.yunming.tea.entity.Room;
// MyBatis Mapper 注解
import org.apache.ibatis.annotations.Mapper;

/**
 * 茶室包间表 Mapper 接口
 * 继承 BaseMapper<Room>，提供对 room 表的 CRUD 操作
 */
@Mapper // 注册 MyBatis Mapper
public interface RoomMapper extends BaseMapper<Room> {
    // 继承 BaseMapper 获得基础 CRUD 方法
}
