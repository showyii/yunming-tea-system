// 声明包路径
package com.yunming.tea.dto;

// Lombok @Data
import lombok.Data;

// Bean Validation：@NotBlank 非空校验
import javax.validation.constraints.NotBlank;

/**
 * 提交订单请求 DTO
 * 接收用户下单时填写的收货信息
 */
@Data // 自动生成 getter/setter
public class SubmitOrderDTO {

    @NotBlank(message = "收货人姓名不能为空") // 校验：收货人姓名必填
    private String receiverName; // 收货人姓名

    @NotBlank(message = "收货人手机不能为空") // 校验：收货电话必填
    private String receiverPhone; // 收货人联系电话

    @NotBlank(message = "收货地址不能为空") // 校验：收货地址必填
    private String receiverAddress; // 收货详细地址

    private String remark; // 订单备注（选填）
}
