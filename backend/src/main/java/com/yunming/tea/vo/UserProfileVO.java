// 声明包路径：VO 视图对象
package com.yunming.tea.vo;

// Lombok @Data
import lombok.Data;

// Java 时间 API
import java.time.LocalDate; // 仅日期（生日）
import java.time.LocalDateTime; // 日期+时间

/**
 * 用户信息响应 VO
 * 返回用户的完整个人信息，包含基本信息和扩展资料
 */
@Data // 自动生成 getter/setter
public class UserProfileVO {

    private Long id; // 用户ID
    private String username; // 用户名
    private String phone; // 手机号
    private String email; // 电子邮箱
    private Integer role; // 角色：0=普通用户，1=管理员
    private String nickname; // 用户昵称
    private String avatar; // 头像 URL
    private Integer gender; // 性别：0=未知，1=男，2=女
    private LocalDate birthday; // 出生日期
    private String address; // 收货地址
    private String signature; // 个性签名
    private LocalDateTime createTime; // 注册时间
}
