// 声明包路径
package com.yunming.tea.dto;

// Lombok @Data
import lombok.Data;

// Bean Validation 校验注解
import javax.validation.constraints.FutureOrPresent; // 日期必须是今天或将来
import javax.validation.constraints.Min; // 最小值
import javax.validation.constraints.NotNull; // 非空

// Java 日期时间 API
import java.time.LocalDate; // 只有年月日
import java.time.LocalTime; // 只有时分秒

/**
 * 包间预约请求 DTO
 * 接收用户预约茶室包间的信息
 */
@Data // 自动生成 getter/setter
public class RoomBookingDTO {

    @NotNull(message = "包间ID不能为空") // 校验：必须选择包间
    private Long roomId; // 预约的包间ID

    @NotNull(message = "预约日期不能为空") // 校验：日期必填
    @FutureOrPresent(message = "预约日期不能早于今天") // 校验：只能预约今天或未来的日期
    private LocalDate bookingDate; // 预约日期（年-月-日）

    @NotNull(message = "开始时间不能为空") // 校验：必须选择开始时间
    private LocalTime startTime; // 预约开始时间（时:分:秒）

    @NotNull(message = "结束时间不能为空") // 校验：必须选择结束时间
    private LocalTime endTime; // 预约结束时间（时:分:秒）

    @Min(value = 1, message = "到店人数最少1人") // 校验：到店人数至少 1 人
    private Integer guestCount = 1; // 预计到店人数，默认 1 人

    private String remark; // 预约备注（选填，如特殊需求）
}
