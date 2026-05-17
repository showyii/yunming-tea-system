// 声明包路径
package com.yunming.tea.vo;

// Lombok @Data
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 评论响应 VO
 * 返回商品评论的详细信息，包含评论者资料
 */
@Data // 自动生成 getter/setter
public class CommentVO {

    private Long id; // 评论ID
    private Long userId; // 评论者用户ID
    private String username; // 评论者用户名
    private String userAvatar; // 评论者头像 URL
    private Long productId; // 被评价的商品ID
    private String productName; // 商品名称（冗余）
    private Long orderId; // 关联的订单ID
    private String content; // 评论正文内容
    private Integer rating; // 评分（1~5星）
    private Integer status; // 评论审核状态
    private LocalDateTime createTime; // 评论发表时间
}
