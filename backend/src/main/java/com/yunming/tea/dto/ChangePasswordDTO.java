// 声明包路径
package com.yunming.tea.dto;

// Lombok @Data
import lombok.Data;

// Bean Validation
import javax.validation.constraints.NotBlank;

/**
 * 修改密码请求 DTO
 * 接收用户修改密码的请求
 */
@Data // 自动生成 getter/setter
public class ChangePasswordDTO {

    @NotBlank(message = "原密码不能为空") // 校验：必须提供旧密码用于验证身份
    private String oldPassword; // 原密码（用于验证当前用户身份）

    @NotBlank(message = "新密码不能为空") // 校验：新密码不能为空
    private String newPassword; // 新密码
}
