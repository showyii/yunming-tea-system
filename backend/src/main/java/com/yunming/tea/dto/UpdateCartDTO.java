// 声明包路径
package com.yunming.tea.dto;

// Lombok @Data
import lombok.Data;

// Bean Validation 校验注解
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

/**
 * 修改购物车数量请求 DTO
 * 用户在购物车页面修改某件商品的数量
 */
@Data // 自动生成 getter/setter
public class UpdateCartDTO {

    @NotNull(message = "购物车项ID不能为空") // 校验：必须指定要修改的购物车项
    private Long id; // 购物车记录ID（cart 表的主键）

    @Min(value = 1, message = "数量最少1件") // 校验：修改后的数量至少为 1
    private Integer quantity; // 修改后的购买数量
}
