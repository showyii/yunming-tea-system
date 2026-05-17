// 声明包路径
package com.yunming.tea.entity;

// MyBatis-Plus 注解
import com.baomidou.mybatisplus.annotation.*;
// Lombok @Data
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 管理员实体类
 * 映射 admin 表，存储后台管理系统的管理员账号信息
 */
@Data // 自动生成 getter/setter
@TableName("admin") // 映射数据库表 admin
public class Admin {

    @TableId(type = IdType.AUTO) // 主键自增
    private Long id; // 管理员唯一ID

    private String username; // 管理员登录用户名
    private String password; // 管理员登录密码（BCrypt 加密）
    private String name; // 管理员的真实姓名
    private String phone; // 联系电话
    private String email; // 电子邮箱
    private String role; // 角色："super_admin"=超级管理员（全部权限），"admin"=普通管理员
    private Integer status; // 账号状态：0=禁用，1=正常

    @TableLogic // 逻辑删除
    private Integer deleted; // 0=未删除，1=已删除

    @TableField(fill = FieldFill.INSERT) // 插入时自动填充
    private LocalDateTime createTime; // 账号创建时间

    @TableField(fill = FieldFill.INSERT_UPDATE) // 插入和更新时自动填充
    private LocalDateTime updateTime; // 最后更新时间
}
