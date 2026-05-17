// 声明当前类所属的包路径
package com.yunming.tea.entity;

// 导入 MyBatis-Plus 相关注解（@TableName, @TableId, @TableField, @TableLogic 等）
import com.baomidou.mybatisplus.annotation.*;
// Lombok 注解：自动生成所有字段的 getter/setter/equals/hashCode/toString
import lombok.Data;

// 导入 Java 8 日期时间 API
import java.time.LocalDateTime;

/**
 * 用户实体类
 * 映射数据库中的 user 表，存储注册用户的基本信息
 */
@Data // 自动生成 getter/setter/equals/hashCode/toString 方法
@TableName("user") // 指定该实体对应的数据库表名为 "user"
public class User {

    @TableId(type = IdType.AUTO) // 标记主键字段，type=AUTO 表示使用数据库自增策略
    private Long id; // 用户唯一ID，主键

    private String username; // 用户名，登录时使用
    private String password; // 密码，使用 BCrypt 加密存储
    private String phone; // 手机号码
    private String email; // 电子邮箱
    private Integer role; // 用户角色：0=普通用户，1=管理员
    private Integer status; // 账户状态：0=禁用，1=正常启用

    @TableLogic // 逻辑删除注解：删除操作会将此字段从0更新为1，而非物理删除
    private Integer deleted; // 逻辑删除标记：0=未删除，1=已删除

    @TableField(fill = FieldFill.INSERT) // 插入时自动填充该字段
    private LocalDateTime createTime; // 账号创建时间

    @TableField(fill = FieldFill.INSERT_UPDATE) // 插入和更新时都自动填充
    private LocalDateTime updateTime; // 最后更新时间
}
