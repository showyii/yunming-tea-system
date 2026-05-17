// 声明当前类所属的包路径，对应项目目录结构
package com.yunming.tea;

// 导入 MyBatis 的 MapperScan 注解，用于扫描 Mapper 接口
import org.mybatis.spring.annotation.MapperScan;
// 导入 Spring Boot 应用启动类
import org.springframework.boot.SpringApplication;
// 导入 Spring Boot 自动配置注解
import org.springframework.boot.autoconfigure.SpringBootApplication;

// @SpringBootApplication 标记该类为 Spring Boot 主启动类，启用自动配置、组件扫描等
@SpringBootApplication
// @MapperScan 指定 MyBatis Mapper 接口所在的包路径，Spring 启动时会扫描该路径下的所有 Mapper
@MapperScan("com.yunming.tea.mapper")
// 定义 Spring Boot 应用的主类
public class YunmingTeaApplication {

    // Java 程序的主入口方法，程序从这里开始执行
    public static void main(String[] args) {
        // 调用 SpringApplication.run() 启动 Spring Boot 应用
        // 第一个参数是当前类的 Class 对象，第二个参数是命令行参数
        SpringApplication.run(YunmingTeaApplication.class, args);
    }
}
