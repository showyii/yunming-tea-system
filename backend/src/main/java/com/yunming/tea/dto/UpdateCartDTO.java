package com.yunming.tea.dto;

import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

/**
 * 修改购物车数量请求
 */
@Data
public class UpdateCartDTO {

    @NotNull(message = "购物车项ID不能为空")
    private Long id;

    @Min(value = 1, message = "数量最少1件")
    private Integer quantity;
}
