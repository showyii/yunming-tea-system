// 声明包路径：DTO（Data Transfer Object）数据传输对象，用于接收前端请求参数
package com.yunming.tea.dto;

// Lombok @Data 自动生成 getter/setter
import lombok.Data;

// JSR-303 Bean Validation 校验注解
import javax.validation.constraints.NotBlank; // 非空字符串
import javax.validation.constraints.Size; // 长度限制

/**
 * 用户注册请求 DTO
 * 接收前端注册表单提交的数据，使用注解进行参数校验
 */
@Data // 自动生成 getter/setter/equals/hashCode/toString
public class UserRegisterDTO {

    @NotBlank(message = "用户名不能为空") // 校验：用户名不允许为空字符串
    @Size(min = 3, max = 50, message = "用户名长度3-50位") // 校验：用户名长度必须在 3~50 个字符之间
    private String username; // 用户名

    @NotBlank(message = "密码不能为空") // 校验：密码不能为空
    @Size(min = 6, max = 50, message = "密码长度6-50位") // 校验：密码最少 6 位
    private String password; // 密码（明文传输，后端加密存储）

    @NotBlank(message = "手机号不能为空") // 校验：手机号必填
    private String phone; // 手机号码

    private String email; // 电子邮箱（选填，无校验注解）
}
