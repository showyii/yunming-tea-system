// 声明包路径
package com.yunming.tea.entity;

// MyBatis-Plus 注解
import com.baomidou.mybatisplus.annotation.*;
// Lombok @Data
import lombok.Data;

// BigDecimal 用于价格精确计算
import java.math.BigDecimal;
// LocalDate 只包含日期，LocalTime 只包含时间，LocalDateTime 包含日期+时间
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

/**
 * 包间预约实体类
 * 映射 room_booking 表，存储用户预约茶室包间的记录
 */
@Data // 自动生成 getter/setter
@TableName("room_booking") // 映射数据库表 room_booking
public class RoomBooking {

    @TableId(type = IdType.AUTO) // 主键自增
    private Long id; // 预约记录唯一ID

    private Long userId; // 预约用户ID（外键，关联 user 表）
    private Long roomId; // 预约的包间ID（外键，关联 room 表）
    private LocalDate bookingDate; // 预约日期（只包含年月日）
    private LocalTime startTime; // 预约开始时间（只包含时分秒）
    private LocalTime endTime; // 预约结束时间（只包含时分秒）
    private Integer duration; // 预约时长，单位：小时
    private BigDecimal totalPrice; // 预约总费用 = duration * 每小时单价
    private Integer guestCount; // 预计到店人数
    private String remark; // 用户填写的备注/特殊要求
    private Integer status; // 预约状态：0=待确认，1=已确认，2=已完成，3=已取消

    @TableLogic // 逻辑删除
    private Integer deleted; // 0=未删除，1=已删除

    @TableField(fill = FieldFill.INSERT) // 插入时自动填充
    private LocalDateTime createTime; // 预约提交时间

    @TableField(fill = FieldFill.INSERT_UPDATE) // 插入和更新时自动填充
    private LocalDateTime updateTime; // 最后更新时间
}
