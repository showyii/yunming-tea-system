package com.yunming.tea.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Data
public class RoomBookingVO {

    private Long id;
    private Long userId;
    private Long roomId;
    private String roomName;
    private String roomImage;
    private LocalDate bookingDate;
    private LocalTime startTime;
    private LocalTime endTime;
    private Integer duration;
    private BigDecimal totalPrice;
    private Integer guestCount;
    private String remark;
    private Integer status;
    private String statusText;
    private LocalDateTime createTime;
}
