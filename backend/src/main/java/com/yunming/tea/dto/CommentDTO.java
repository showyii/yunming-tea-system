package com.yunming.tea.dto;

import lombok.Data;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * 发表评论请求
 */
@Data
public class CommentDTO {

    @NotNull(message = "商品ID不能为空")
    private Long productId;

    @NotBlank(message = "评论内容不能为空")
    private String content;

    @Min(value = 1, message = "评分最低1星")
    @Max(value = 5, message = "评分最高5星")
    private Integer rating = 5;

    private Long orderId;
}
