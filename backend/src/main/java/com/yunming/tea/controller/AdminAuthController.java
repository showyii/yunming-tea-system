package com.yunming.tea.controller; // 包声明：控制器层

import com.yunming.tea.common.Result; // 统一响应结果封装类
import com.yunming.tea.dto.LoginDTO; // 登录请求的数据传输对象（包含用户名和密码）
import com.yunming.tea.service.AdminService; // 管理员业务逻辑服务
import com.yunming.tea.vo.LoginVO; // 登录成功的视图对象（包含 token 和用户信息）
import org.springframework.web.bind.annotation.PostMapping; // POST 请求映射注解
import org.springframework.web.bind.annotation.RequestBody; // 请求体绑定注解
import org.springframework.web.bind.annotation.RequestMapping; // 类级别请求路径映射注解
import org.springframework.web.bind.annotation.RestController; // REST 控制器标识注解

import javax.validation.Valid; // JSR-303 Bean 校验注解，确保登录参数符合校验规则

/**
 * 管理员认证控制器
 *
 * 负责处理管理后台的登录认证，与管理员的身份验证相关。
 * 普通用户的登录在 AuthController 中处理，本控制器专门用于管理员登录。
 *
 * 映射路径：/api/admin/auth
 */
@RestController // 标识这是一个 RESTful 控制器，返回值自动序列化为 JSON 格式
@RequestMapping("/api/admin/auth") // 将控制器映射到 /api/admin/auth 路径下
public class AdminAuthController {

    private final AdminService adminService; // 管理员业务服务，通过构造器注入

    /**
     * 构造器注入 AdminService
     *
     * @param adminService 管理员业务服务实例
     */
    public AdminAuthController(AdminService adminService) { // 通过构造器注入依赖
        this.adminService = adminService; // 将注入的服务赋值给成员变量
    }

    /**
     * 管理员登录
     *
     * 验证管理员账号和密码，登录成功后返回包含 JWT token 的响应。
     * token 中包含管理员的角色信息，用于后续管理后台接口的权限验证。
     *
     * 请求方式：POST /api/admin/auth/login
     *
     * @param dto 登录数据传输对象，包含用户名（username）和密码（password），由 @Valid 校验
     * @return Result 包含登录成功信息（token、用户昵称等）的统一响应结果
     */
    @PostMapping("/login") // 处理 HTTP POST 请求，映射到 /api/admin/auth/login
    public Result<LoginVO> login(@Valid @RequestBody LoginDTO dto) { // @Valid 校验请求体参数，@RequestBody 将 JSON 反序列化为 LoginDTO
        LoginVO vo = adminService.login(dto); // 调用管理员业务服务执行登录逻辑，返回包含 token 的视图对象
        return Result.success(vo); // 将登录视图对象包装为成功响应返回
    }
}
