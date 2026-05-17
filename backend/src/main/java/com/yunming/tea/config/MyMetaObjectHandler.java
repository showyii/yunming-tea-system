// 声明包路径
package com.yunming.tea.config;

// 导入 MyBatis-Plus 的元对象处理器接口，用于自动填充字段
import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
// 导入 MyBatis 反射的元对象类
import org.apache.ibatis.reflection.MetaObject;
// Spring 组件注解，将该类注册为 Spring Bean
import org.springframework.stereotype.Component;

// 导入 Java 8 时间 API
import java.time.LocalDateTime;

/**
 * MyBatis-Plus 自动填充处理器
 * 在插入和更新数据时自动填充 createTime（创建时间）和 updateTime（更新时间）字段
 * 无需在业务代码中手动设置这两个时间字段
 */
@Component // 将该类注册为 Spring 容器管理的 Bean
public class MyMetaObjectHandler implements MetaObjectHandler { // 实现 MyBatis-Plus 提供的元对象处理器接口

    /**
     * 插入数据时自动填充
     * @param metaObject 元对象，包含了要插入的实体信息
     */
    @Override // 重写接口方法
    public void insertFill(MetaObject metaObject) {
        // 如果 createTime 字段为空，则自动填充当前时间
        this.strictInsertFill(metaObject, "createTime", LocalDateTime.class, LocalDateTime.now());
        // 如果 updateTime 字段为空，则自动填充当前时间
        this.strictInsertFill(metaObject, "updateTime", LocalDateTime.class, LocalDateTime.now());
    }

    /**
     * 更新数据时自动填充
     * @param metaObject 元对象，包含了要更新的实体信息
     */
    @Override // 重写接口方法
    public void updateFill(MetaObject metaObject) {
        // 更新时自动将 updateTime 设置为当前时间
        this.strictUpdateFill(metaObject, "updateTime", LocalDateTime.class, LocalDateTime.now());
    }
}
