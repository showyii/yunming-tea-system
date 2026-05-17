// 声明包路径
package com.yunming.tea.entity;

// 导入 MyBatis-Plus 注解
import com.baomidou.mybatisplus.annotation.*;
// Lombok @Data 自动生成 getter/setter
import lombok.Data;

// Java 日期类：LocalDate 只有日期无时间，LocalDateTime 包含日期和时间
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 用户资料实体类
 * 映射 user_profile 表，存储用户的扩展/详细信息
 * 与 User 表是一对一关系（通过 userId 关联）
 */
@Data // 自动生成 getter/setter
@TableName("user_profile") // 映射数据库表 user_profile
public class UserProfile {

    @TableId(type = IdType.AUTO) // 主键，数据库自增
    private Long id; // 资料记录的唯一ID

    private Long userId; // 关联的用户ID，对应 user 表的主键
    private String nickname; // 用户昵称/显示名称
    private String avatar; // 头像图片的 URL 地址
    private Integer gender; // 性别：0=未设置，1=男，2=女
    private LocalDate birthday; // 出生日期（只包含年月日）
    private String address; // 收货地址
    private String signature; // 个性签名/个人简介

    @TableLogic // 逻辑删除标记
    private Integer deleted; // 0=未删除，1=已删除

    @TableField(fill = FieldFill.INSERT) // 仅在插入时自动填充当前时间
    private LocalDateTime createTime; // 资料创建时间

    @TableField(fill = FieldFill.INSERT_UPDATE) // 插入和更新时均自动填充
    private LocalDateTime updateTime; // 资料最后更新时间
}
