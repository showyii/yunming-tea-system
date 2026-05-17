// 声明包路径
package com.yunming.tea.dto;

// Lombok @Data
import lombok.Data;

// Bean Validation：@NotNull 非空校验
import javax.validation.constraints.NotNull;

/**
 * 活动报名请求 DTO
 * 接收用户报名参加活动的请求
 */
@Data // 自动生成 getter/setter
public class ActivitySignupDTO {

    @NotNull(message = "活动ID不能为空") // 校验：必须指定要报名的活动
    private Long activityId; // 要报名的活动ID
}
