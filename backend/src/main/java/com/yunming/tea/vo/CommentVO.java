package com.yunming.tea.vo;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 评论视图
 */
@Data
public class CommentVO {

    private Long id;
    private Long userId;
    private String username;
    private String userAvatar;
    private Long productId;
    private String productName;
    private Long orderId;
    private String content;
    private Integer rating;
    private Integer status;
    private LocalDateTime createTime;
}
