// 声明包路径
package com.yunming.tea.vo;

// Lombok @Data
import lombok.Data;

// 精确金额
import java.math.BigDecimal;
// Java 日期时间类
import java.time.LocalDate; // 仅日期（预约日期）
import java.time.LocalDateTime; // 日期+时间
import java.time.LocalTime; // 仅时间（开始/结束时间）

/**
 * 包间预约响应 VO
 * 返回包间预约的详细信息
 */
@Data // 自动生成 getter/setter
public class RoomBookingVO {

    private Long id; // 预约记录ID
    private Long userId; // 预约用户的ID
    private Long roomId; // 预约的包间ID
    private String roomName; // 包间名称（冗余，方便展示）
    private String roomImage; // 包间图片（冗余，方便展示）
    private LocalDate bookingDate; // 预约日期（年-月-日）
    private LocalTime startTime; // 开始时间（时:分）
    private LocalTime endTime; // 结束时间（时:分）
    private Integer duration; // 预约时长（小时）
    private BigDecimal totalPrice; // 预约总费用
    private Integer guestCount; // 到店人数
    private String remark; // 用户备注
    private Integer status; // 预约状态码
    private String statusText; // 预约状态文本（"待确认"/"已确认"/"已完成"/"已取消"）
    private LocalDateTime createTime; // 预约提交时间
}
