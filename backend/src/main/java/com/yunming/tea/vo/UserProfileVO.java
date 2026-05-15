package com.yunming.tea.vo;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 用户信息响应
 */
@Data
public class UserProfileVO {

    private Long id;
    private String username;
    private String phone;
    private String email;
    private Integer role;
    private String nickname;
    private String avatar;
    private Integer gender;
    private LocalDate birthday;
    private String address;
    private String signature;
    private LocalDateTime createTime;
}
