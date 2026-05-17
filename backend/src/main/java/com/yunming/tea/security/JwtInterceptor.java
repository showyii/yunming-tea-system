// 声明包路径
package com.yunming.tea.security;

// Spring 组件注解，注册为 Bean
import org.springframework.stereotype.Component;
// Spring MVC 拦截器接口
import org.springframework.web.servlet.HandlerInterceptor;

// Servlet 请求对象
import javax.servlet.http.HttpServletRequest;
// Servlet 响应对象
import javax.servlet.http.HttpServletResponse;
// Java 数组工具类
import java.util.Arrays;
// Java 列表接口
import java.util.List;

/**
 * JWT 认证拦截器
 * 在请求到达 Controller 之前拦截请求，校验 JWT Token 的有效性
 * 对公开路径（如首页、商品列表）只做 Token 解析（不强制要求登录）
 * 对需要认证的路径强制要求携带有效 Token
 */
@Component // 注册为 Spring Bean
public class JwtInterceptor implements HandlerInterceptor { // 实现 HandlerInterceptor 接口

    // 注入 JWT 工具类
    private final JwtUtils jwtUtils;

    // 定义无需登录即可访问的公开路径列表
    private static final List<String> PUBLIC_PATHS = Arrays.asList(
            "/api/auth/",     // 用户认证相关（登录/注册）
            "/api/products",  // 商品相关
            "/api/categories",// 分类相关
            "/api/comments",  // 评论相关
            "/api/rooms",     // 茶室相关
            "/api/activities" // 活动相关
    );

    // 构造器注入 JwtUtils
    public JwtInterceptor(JwtUtils jwtUtils) {
        this.jwtUtils = jwtUtils;
    }

    /**
     * 判断当前请求路径是否为公开路径
     * @param path 请求 URI
     * @return 是否匹配公开路径
     */
    private boolean isPublicPath(String path) {
        for (String p : PUBLIC_PATHS) { // 遍历公开路径列表
            if (path.startsWith(p)) return true; // 请求路径以公开路径前缀开头则匹配
        }
        return false; // 所有公开路径都不匹配
    }

    /**
     * 请求预处理（在 Controller 方法执行前调用）
     * 1. 放行 OPTIONS 预检请求
     * 2. 对公开路径：有 Token 就解析用户信息（非强制）
     * 3. 对非公开路径：强制校验 Token，无效则返回 401
     */
    @Override // 重写 HandlerInterceptor 的方法
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 放行浏览器跨域预检请求（OPTIONS 方法不需要 Token 验证）
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            return true; // true 表示放行，让请求继续处理
        }

        // 获取当前请求的 URI 路径
        String path = request.getRequestURI();

        // ===== 公开路径处理：不需要登录也能访问 =====
        if (isPublicPath(path)) {
            // 尝试从请求头获取 Authorization
            String token = request.getHeader("Authorization");
            // 如果有 Token 且格式正确（Bearer 开头），则解析用户信息
            if (token != null && token.startsWith("Bearer ")) {
                token = token.substring(7); // 去除 "Bearer " 前缀（7个字符），得到纯 JWT
                // 验证 Token 是否有效（未过期、未被篡改）
                if (jwtUtils.validateToken(token)) {
                    // 从 Token 中提取用户ID，存入 request 供后续 Controller 使用
                    request.setAttribute("userId", jwtUtils.getUserId(token));
                    // 从 Token 中提取用户名
                    request.setAttribute("username", jwtUtils.getUsername(token));
                    // 从 Token 中提取用户类型（user 或 admin）
                    request.setAttribute("userType", jwtUtils.getType(token));
                }
            }
            return true; // 公开路径始终放行
        }

        // ===== 非公开路径处理：必须提供有效 Token =====
        // 从请求头获取 Authorization 字段
        String token = request.getHeader("Authorization");
        // 如果 Token 不存在或格式不正确
        if (token == null || !token.startsWith("Bearer ")) {
            response.setContentType("application/json;charset=UTF-8"); // 设置响应内容类型为 JSON
            response.setStatus(401); // 设置 HTTP 状态码为 401 Unauthorized
            // 直接向响应流写入 JSON 格式的错误信息
            response.getWriter().write("{\"code\":401,\"message\":\"未登录或Token无效\",\"data\":null}");
            return false; // false 表示拦截，不再执行后续 Controller
        }

        // 去除 "Bearer " 前缀，提取纯 Token 字符串
        token = token.substring(7);
        // 校验 Token 是否有效
        if (!jwtUtils.validateToken(token)) {
            response.setContentType("application/json;charset=UTF-8"); // JSON 格式
            response.setStatus(401); // 401 未授权
            response.getWriter().write("{\"code\":401,\"message\":\"Token已过期或无效\",\"data\":null}");
            return false; // 拦截请求
        }

        // Token 有效，将用户信息存入 request，后续 Controller 可通过 getAttribute 获取
        request.setAttribute("userId", jwtUtils.getUserId(token));
        request.setAttribute("username", jwtUtils.getUsername(token));
        request.setAttribute("userType", jwtUtils.getType(token));

        return true; // 放行
    }
}
