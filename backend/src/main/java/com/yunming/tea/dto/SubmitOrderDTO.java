package com.yunming.tea.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * 提交订单请求
 */
@Data
public class SubmitOrderDTO {

    @NotBlank(message = "收货人姓名不能为空")
    private String receiverName;

    @NotBlank(message = "收货人手机不能为空")
    private String receiverPhone;

    @NotBlank(message = "收货地址不能为空")
    private String receiverAddress;

    private String remark;
}
