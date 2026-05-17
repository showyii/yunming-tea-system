// 声明包路径
package com.yunming.tea.vo;

// Lombok @Data
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 活动报名响应 VO
 * 返回用户报名记录信息
 */
@Data // 自动生成 getter/setter
public class ActivitySignupVO {

    private Long id; // 报名记录ID
    private Long activityId; // 报名的活动ID
    private String activityTitle; // 活动标题（冗余，方便列表展示）
    private String activityCover; // 活动封面图（冗余）
    private LocalDateTime activityStartTime; // 活动开始时间
    private Integer status; // 报名状态码
    private String statusText; // 报名状态文本（"待确认"/"已确认"/"已取消"）
    private LocalDateTime createTime; // 报名时间
}
