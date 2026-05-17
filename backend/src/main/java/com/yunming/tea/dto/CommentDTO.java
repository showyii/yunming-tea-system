// 声明包路径
package com.yunming.tea.dto;

// Lombok @Data
import lombok.Data;

// Bean Validation 校验注解
import javax.validation.constraints.Max; // 最大值
import javax.validation.constraints.Min; // 最小值
import javax.validation.constraints.NotBlank; // 非空字符串
import javax.validation.constraints.NotNull; // 非空

/**
 * 发表评论请求 DTO
 * 接收用户提交的商品评价信息
 */
@Data // 自动生成 getter/setter
public class CommentDTO {

    @NotNull(message = "商品ID不能为空") // 校验：商品ID不能为 null
    private Long productId; // 被评价的商品ID

    @NotBlank(message = "评论内容不能为空") // 校验：评论内容不能为空字符串
    private String content; // 评论正文

    @Min(value = 1, message = "评分最低1星") // 校验：最低 1 分
    @Max(value = 5, message = "评分最高5星") // 校验：最高 5 分
    private Integer rating = 5; // 评分（1~5星），默认 5 星

    private Long orderId; // 关联的订单ID（用于验证用户确实购买过该商品）
}
