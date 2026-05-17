// 声明包路径
package com.yunming.tea.mapper;

// MyBatis-Plus BaseMapper
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
// RoomBooking 实体类（包间预约）
import com.yunming.tea.entity.RoomBooking;
// MyBatis Mapper 注解
import org.apache.ibatis.annotations.Mapper;

/**
 * 包间预约表 Mapper 接口
 * 继承 BaseMapper<RoomBooking>，提供对 room_booking 表的 CRUD 操作
 */
@Mapper // MyBatis Mapper
public interface RoomBookingMapper extends BaseMapper<RoomBooking> {
    // 基础 CRUD 由 BaseMapper 提供
}
