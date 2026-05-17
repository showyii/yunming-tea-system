// 声明包路径
package com.yunming.tea.dto;

// Lombok @Data
import lombok.Data;

// Bean Validation 校验注解
import javax.validation.constraints.Min; // 最小值校验
import javax.validation.constraints.NotNull; // 非空校验

/**
 * 加入购物车请求 DTO
 * 接收添加商品到购物车的请求参数
 */
@Data // 自动生成 getter/setter
public class AddCartDTO {

    @NotNull(message = "商品ID不能为空") // 校验：商品ID必填
    private Long productId; // 要加入购物车的商品ID

    @Min(value = 1, message = "数量最少1件") // 校验：数量至少为 1
    private Integer quantity = 1; // 购买数量，默认 1 件
}
