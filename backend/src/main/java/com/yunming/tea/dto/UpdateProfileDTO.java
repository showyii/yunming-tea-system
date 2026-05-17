// 声明包路径
package com.yunming.tea.dto;

// Lombok @Data
import lombok.Data;

// LocalDate 用于生日（只含年月日）
import java.time.LocalDate;

/**
 * 修改个人资料请求 DTO
 * 接收用户更新个人信息的请求，所有字段均为选填
 */
@Data // 自动生成 getter/setter
public class UpdateProfileDTO {

    private String nickname; // 用户昵称
    private String avatar; // 头像图片 URL
    private Integer gender; // 性别：0=未设置，1=男，2=女
    private LocalDate birthday; // 出生日期
    private String address; // 收货地址
    private String signature; // 个性签名/简介
}
