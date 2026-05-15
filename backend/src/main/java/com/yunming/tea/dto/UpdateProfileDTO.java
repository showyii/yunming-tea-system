package com.yunming.tea.dto;

import lombok.Data;

import java.time.LocalDate;

/**
 * 修改个人资料请求
 */
@Data
public class UpdateProfileDTO {

    private String nickname;
    private String avatar;
    private Integer gender;
    private LocalDate birthday;
    private String address;
    private String signature;
}
