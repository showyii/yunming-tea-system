// 声明包路径
package com.yunming.tea.vo;

// Lombok @Data
import lombok.Data;

// BigDecimal 用于报名费用
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 茶文化活动响应 VO
 * 返回活动信息，包含当前用户的报名状态
 */
@Data // 自动生成 getter/setter
public class ActivityVO {

    private Long id; // 活动ID
    private String title; // 活动标题
    private String description; // 活动描述
    private String coverImage; // 封面图片 URL
    private String location; // 活动举办地点
    private LocalDateTime startTime; // 活动开始时间
    private LocalDateTime endTime; // 活动结束时间
    private Integer maxParticipants; // 最大可报名人数
    private Integer currentParticipants; // 当前已报名人数
    private BigDecimal fee; // 报名费用（0 表示免费）
    private Integer status; // 活动状态码
    private String statusText; // 活动状态文本（"未开始"/"进行中"/"已结束"/"已取消"）
    private Boolean isSignedUp; // 当前用户是否已报名该活动
    private LocalDateTime createTime; // 活动发布时间
}
