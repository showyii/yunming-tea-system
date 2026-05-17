// 声明包路径
package com.yunming.tea.entity;

// MyBatis-Plus 注解
import com.baomidou.mybatisplus.annotation.*;
// Lombok @Data
import lombok.Data;

// BigDecimal 用于价格精确计算
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 茶室包间实体类
 * 映射 room 表，存储可供预约的茶室包间信息
 */
@Data // 自动生成 getter/setter
@TableName("room") // 映射数据库表 room
public class Room {

    @TableId(type = IdType.AUTO) // 主键自增
    private Long id; // 包间唯一ID

    private String name; // 包间名称（如"听雨轩"、"品茗阁"等）
    private String type; // 包间类型（如"小包"、"中包"、"大包"，区分大小和档次）
    private Integer capacity; // 最大容纳人数
    private BigDecimal pricePerHour; // 每小时收费标准
    private String description; // 包间详细描述
    private String image; // 包间照片的 URL 地址
    private String facilities; // 配套设施描述（如"投影仪、WiFi、茶具"）
    private Integer status; // 包间状态：0=停用/维护中，1=可预约

    @TableLogic // 逻辑删除
    private Integer deleted; // 0=未删除，1=已删除

    @TableField(fill = FieldFill.INSERT) // 插入时自动填充
    private LocalDateTime createTime; // 包间信息创建时间

    @TableField(fill = FieldFill.INSERT_UPDATE) // 插入和更新时自动填充
    private LocalDateTime updateTime; // 包间信息最后更新时间
}
