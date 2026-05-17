// 声明包路径
package com.yunming.tea.entity;

// MyBatis-Plus 注解
import com.baomidou.mybatisplus.annotation.*;
// Lombok @Data
import lombok.Data;

// BigDecimal 用于报名费用的精确计算
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 茶文化活动实体类
 * 映射 tea_activity 表，存储茶馆举办的各类文化体验活动
 * 如茶艺课程、茶叶品鉴会、茶文化讲座等
 */
@Data // 自动生成 getter/setter
@TableName("tea_activity") // 映射数据库表 tea_activity
public class TeaActivity {

    @TableId(type = IdType.AUTO) // 主键自增
    private Long id; // 活动唯一ID

    private String title; // 活动标题
    private String description; // 活动详细描述（可包含富文本）
    private String coverImage; // 活动封面图片 URL
    private String location; // 活动举办地点
    private LocalDateTime startTime; // 活动开始时间（包含日期和时间）
    private LocalDateTime endTime; // 活动结束时间（包含日期和时间）
    private Integer maxParticipants; // 最大可报名人数（上限）
    private Integer currentParticipants; // 当前已报名人数
    private BigDecimal fee; // 报名费用，0 表示免费活动
    private Integer status; // 活动状态：0=未开始，1=进行中，2=已结束，3=已取消

    @TableLogic // 逻辑删除
    private Integer deleted; // 0=未删除，1=已删除

    @TableField(fill = FieldFill.INSERT) // 插入时自动填充
    private LocalDateTime createTime; // 活动创建时间

    @TableField(fill = FieldFill.INSERT_UPDATE) // 插入和更新时自动填充
    private LocalDateTime updateTime; // 活动信息最后更新时间
}
