package com.yunming.tea.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class ActivityVO {

    private Long id;
    private String title;
    private String description;
    private String coverImage;
    private String location;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private Integer maxParticipants;
    private Integer currentParticipants;
    private BigDecimal fee;
    private Integer status;
    private String statusText;
    private Boolean isSignedUp;
    private LocalDateTime createTime;
}
