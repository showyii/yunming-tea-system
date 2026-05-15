package com.yunming.tea.vo;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ActivitySignupVO {

    private Long id;
    private Long activityId;
    private String activityTitle;
    private String activityCover;
    private LocalDateTime activityStartTime;
    private Integer status;
    private String statusText;
    private LocalDateTime createTime;
}
