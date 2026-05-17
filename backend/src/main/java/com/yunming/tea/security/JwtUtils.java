// 声明包路径
package com.yunming.tea.security;

// JJWT Claims 接口，代表 JWT 的负载数据
import io.jsonwebtoken.Claims;
// JJWT Jwts 工具类，用于构建和解析 JWT
import io.jsonwebtoken.Jwts;
// JJWT 签名算法枚举，HS256 表示 HMAC-SHA256
import io.jsonwebtoken.SignatureAlgorithm;
// Spring 注解：从配置文件中读取属性值
import org.springframework.beans.factory.annotation.Value;
// Spring 组件注解
import org.springframework.stereotype.Component;

// Java 日期类
import java.util.Date;
// Java HashMap 类
import java.util.HashMap;
// Java Map 接口
import java.util.Map;

/**
 * JWT 工具类
 * 负责生成、解析和验证 JWT Token
 * JWT 结构：Header（头部）+ Payload（负载）+ Signature（签名）
 * 用于用户登录后维持会话状态，前后端分离架构中的身份认证
 */
@Component // 注册为 Spring Bean
public class JwtUtils {

    // 从 application.yml 读取 jwt.secret 配置值（JWT 签名密钥）
    @Value("${jwt.secret}")
    private String secret;

    // 从 application.yml 读取 jwt.expiration 配置值（Token 过期时间，单位毫秒）
    @Value("${jwt.expiration}")
    private Long expiration;

    /**
     * 生成普通用户 JWT Token
     * @param userId 用户ID
     * @param username 用户名
     * @param role 用户角色
     * @return JWT Token 字符串
     */
    public String generateToken(Long userId, String username, Integer role) {
        // 创建 Claims Map，存放 Token 携带的自定义数据
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", userId); // 用户ID
        claims.put("username", username); // 用户名
        claims.put("role", role); // 角色
        claims.put("type", "user"); // 用户类型标记为 "user"
        return createToken(claims); // 调用私有方法生成 Token
    }

    /**
     * 生成管理员 JWT Token
     * @param adminId 管理员ID
     * @param username 管理员用户名
     * @param role 管理员角色
     * @return JWT Token 字符串
     */
    public String generateAdminToken(Long adminId, String username, String role) {
        // 创建 Claims Map
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", adminId); // 管理员ID
        claims.put("username", username); // 管理员用户名
        claims.put("role", role); // 角色
        claims.put("type", "admin"); // 用户类型标记为 "admin"
        return createToken(claims); // 生成并返回 Token
    }

    /**
     * 根据 Claims 构建 JWT Token
     * @param claims Token 中存放的自定义数据
     * @return 生成的 JWT 字符串
     */
    private String createToken(Map<String, Object> claims) {
        return Jwts.builder() // 使用 JJWT 构建器模式
                .setClaims(claims) // 设置 Token 的自定义负载数据
                .setIssuedAt(new Date()) // 设置签发时间（iat 字段）
                // 设置过期时间 = 当前时间 + 配置的有效期（exp 字段）
                .setExpiration(new Date(System.currentTimeMillis() + expiration))
                // 使用 HS256 算法和密钥对 Token 进行签名，防篡改
                .signWith(SignatureAlgorithm.HS256, secret)
                .compact(); // 压缩并生成最终的 JWT 字符串（三段 Base64 用点号连接）
    }

    /**
     * 解析 JWT Token，提取其中的 Claims 数据
     * @param token JWT 字符串
     * @return Claims 对象，包含 Token 中的所有键值对
     */
    public Claims parseToken(String token) {
        return Jwts.parser() // 创建 JWT 解析器
                .setSigningKey(secret) // 设置签名密钥（与生成时一致）
                .parseClaimsJws(token) // 解析 Token 字符串
                .getBody(); // 获取 Token Body 中的 Claims 数据
    }

    /**
     * 验证 Token 是否有效
     * 通过尝试解析 Token，如果解析成功则有效，解析失败（过期/签名不匹配等）则无效
     * @param token JWT 字符串
     * @return true=有效, false=无效
     */
    public boolean validateToken(String token) {
        try {
            parseToken(token); // 尝试解析 Token
            return true; // 解析成功，Token 有效
        } catch (Exception e) { // 捕获任何解析异常（过期、签名错误、格式错误等）
            return false; // 解析失败，Token 无效
        }
    }

    /**
     * 从 Token 中提取用户ID
     * @param token JWT 字符串
     * @return 用户ID
     */
    public Long getUserId(String token) {
        Claims claims = parseToken(token); // 先解析 Token 得到 Claims
        return claims.get("userId", Long.class); // 从 Claims 中获取 userId，指定类型为 Long
    }

    /**
     * 从 Token 中提取用户名
     * @param token JWT 字符串
     * @return 用户名
     */
    public String getUsername(String token) {
        Claims claims = parseToken(token); // 解析 Token
        return claims.get("username", String.class); // 从 Claims 中获取 username，类型为 String
    }

    /**
     * 从 Token 中提取用户类型（user 或 admin）
     * @param token JWT 字符串
     * @return 用户类型字符串
     */
    public String getType(String token) {
        Claims claims = parseToken(token); // 解析 Token
        return claims.get("type", String.class); // 获取 type 字段，区分普通用户和管理员
    }
}
