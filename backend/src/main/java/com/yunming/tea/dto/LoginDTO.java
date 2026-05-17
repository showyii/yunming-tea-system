// 声明包路径：DTO 数据传输对象
package com.yunming.tea.dto;

// Lombok @Data
import lombok.Data;

// Bean Validation 注解：@NotBlank 校验字符串非空
import javax.validation.constraints.NotBlank;

/**
 * 登录请求 DTO
 * 接收前端登录表单的用户名和密码
 */
@Data // 自动生成 getter/setter
public class LoginDTO {

    @NotBlank(message = "用户名不能为空") // 校验：用户名必填，不能为空白字符串
    private String username; // 登录用户名

    @NotBlank(message = "密码不能为空") // 校验：密码必填
    private String password; // 登录密码
}
