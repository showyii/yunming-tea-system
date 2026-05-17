// 声明包路径：VO（View Object）视图对象，用于向前端返回数据
package com.yunming.tea.vo;

// Lombok @AllArgsConstructor 生成全参构造函数
import lombok.AllArgsConstructor;
// Lombok @Data 自动生成 getter/setter
import lombok.Data;

/**
 * 登录响应 VO
 * 用户登录成功后返回的数据，包含 JWT Token 和用户基本信息
 */
@Data // 自动生成 getter/setter
@AllArgsConstructor // 自动生成包含全部字段的构造函数，方便 Service 层快速创建对象
public class LoginVO {

    private String token; // JWT Token 字符串，前端需保存并在后续请求中携带
    private Long userId; // 登录用户的ID
    private String username; // 登录用户的用户名
}
