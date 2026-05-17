// 声明包路径
package com.yunming.tea.config;

// 导入自定义的 JWT 拦截器类
import com.yunming.tea.security.JwtInterceptor;
// Spring 配置类注解
import org.springframework.context.annotation.Configuration;
// 导入 CORS 跨域注册器
import org.springframework.web.servlet.config.annotation.CorsRegistry;
// 导入拦截器注册器
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
// 导入 WebMvc 配置器接口
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Web MVC 配置类
 * 配置 JWT 认证拦截器和 CORS 跨域策略
 */
@Configuration // 标记为 Spring 配置类
public class WebConfig implements WebMvcConfigurer { // 实现 WebMvcConfigurer 接口来定制 Spring MVC 行为

    // 注入 JWT 拦截器 Bean
    private final JwtInterceptor jwtInterceptor;

    // 构造器注入 JwtInterceptor，Spring 自动将 Bean 传入
    public WebConfig(JwtInterceptor jwtInterceptor) {
        this.jwtInterceptor = jwtInterceptor;
    }

    /**
     * 配置拦截器
     * 对 /api/** 路径启用 JWT 拦截器，排除管理后台登录接口
     */
    @Override // 重写父接口方法
    public void addInterceptors(InterceptorRegistry registry) {
        // 向拦截器注册表添加 JWT 拦截器
        registry.addInterceptor(jwtInterceptor)
                // 指定拦截的路径：/api/ 下的所有请求
                .addPathPatterns("/api/**")
                // 排除不需要拦截的路径：管理员登录接口不需要 JWT 验证
                .excludePathPatterns("/api/admin/auth/login");
    }

    /**
     * 配置 CORS 跨域
     * 允许前端（不同端口或域名）发起的跨域请求
     */
    @Override // 重写父接口方法
    public void addCorsMappings(CorsRegistry registry) {
        // 向跨域注册表添加映射规则
        registry.addMapping("/**") // 对所有路径生效
                .allowedOriginPatterns("*") // 允许所有来源域名的请求
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS") // 允许的 HTTP 方法
                .allowedHeaders("*") // 允许所有请求头
                .allowCredentials(true) // 允许携带 Cookie 等凭证信息
                .maxAge(3600); // 预检请求（OPTIONS）的缓存时间，单位秒
    }
}
