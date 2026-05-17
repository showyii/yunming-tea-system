// 声明包路径
package com.yunming.tea.config;

// 导入 MyBatis-Plus 数据库类型枚举
import com.baomidou.mybatisplus.annotation.DbType;
// 导入 MyBatis-Plus 拦截器
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
// 导入 MyBatis-Plus 分页内部拦截器
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
// Spring 注解，标记方法返回的对象为 Bean
import org.springframework.context.annotation.Bean;
// Spring 注解，标记该类为配置类
import org.springframework.context.annotation.Configuration;

/**
 * MyBatis-Plus 配置类
 * 主要配置分页插件，以及指定数据库类型为 MySQL
 */
@Configuration // 标记为 Spring 配置类，Spring 启动时会加载该类中的 Bean 定义
public class MybatisPlusConfig {

    /**
     * 注册 MyBatis-Plus 拦截器 Bean
     * 添加分页内部拦截器，使分页查询功能生效
     */
    @Bean // 将方法返回值注册为 Spring Bean，由 Spring 容器管理
    public MybatisPlusInterceptor mybatisPlusInterceptor() {
        // 创建 MyBatis-Plus 核心拦截器实例
        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
        // 向拦截器中添加分页内部拦截器，指定数据库类型为 MySQL
        // PaginationInnerInterceptor 会自动拦截查询语句并在末尾追加 LIMIT 分页
        interceptor.addInnerInterceptor(new PaginationInnerInterceptor(DbType.MYSQL));
        return interceptor; // 返回配置好的拦截器
    }
}
