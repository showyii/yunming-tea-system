package com.yunming.tea.dto;

import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * 活动报名请求
 */
@Data
public class ActivitySignupDTO {

    @NotNull(message = "活动ID不能为空")
    private Long activityId;
}
