package com.yunming.tea.dto;

import lombok.Data;

import javax.validation.constraints.FutureOrPresent;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.time.LocalTime;

/**
 * 包间预约请求
 */
@Data
public class RoomBookingDTO {

    @NotNull(message = "包间ID不能为空")
    private Long roomId;

    @NotNull(message = "预约日期不能为空")
    @FutureOrPresent(message = "预约日期不能早于今天")
    private LocalDate bookingDate;

    @NotNull(message = "开始时间不能为空")
    private LocalTime startTime;

    @NotNull(message = "结束时间不能为空")
    private LocalTime endTime;

    @Min(value = 1, message = "到店人数最少1人")
    private Integer guestCount = 1;

    private String remark;
}
