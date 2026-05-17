// 声明包路径
package com.yunming.tea.entity;

// MyBatis-Plus 注解
import com.baomidou.mybatisplus.annotation.*;
// Lombok @Data
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 活动报名实体类
 * 映射 activity_signup 表，记录用户报名参加活动
 */
@Data // 自动生成 getter/setter
@TableName("activity_signup") // 映射数据库表 activity_signup
public class ActivitySignup {

    @TableId(type = IdType.AUTO) // 主键自增
    private Long id; // 报名记录唯一ID

    private Long activityId; // 报名的活动ID（外键，关联 tea_activity 表）
    private Long userId; // 报名用户ID（外键，关联 user 表）
    private Integer status; // 报名状态：0=待确认，1=已确认，2=已取消

    @TableLogic // 逻辑删除
    private Integer deleted; // 0=未删除，1=已删除

    @TableField(fill = FieldFill.INSERT) // 插入时自动填充
    private LocalDateTime createTime; // 报名提交时间

    @TableField(fill = FieldFill.INSERT_UPDATE) // 插入和更新时自动填充
    private LocalDateTime updateTime; // 最后更新时间
}
