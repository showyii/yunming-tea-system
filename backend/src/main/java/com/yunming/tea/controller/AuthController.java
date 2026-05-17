package com.yunming.tea.controller; // 包声明：控制器层

import com.yunming.tea.common.Result; // 统一响应结果封装类
import com.yunming.tea.dto.LoginDTO; // 登录请求的数据传输对象（包含用户名和密码）
import com.yunming.tea.dto.UserRegisterDTO; // 用户注册请求的数据传输对象（包含用户名、密码、昵称等）
import com.yunming.tea.service.UserService; // 用户业务逻辑服务
import com.yunming.tea.vo.LoginVO; // 登录成功的视图对象（包含 token 和用户信息）
import org.springframework.web.bind.annotation.*; // Spring MVC 注解：包括 RestController、RequestMapping、PostMapping、RequestBody

import javax.validation.Valid; // JSR-303 Bean 校验注解，确保注册和登录参数符合校验规则

/**
 * 用户认证控制器（面向普通用户）
 *
 * 负责处理普通用户的注册和登录认证功能，包括：
 * - 用户注册：创建新账号
 * - 用户登录：验证账号密码并返回 JWT token
 *
 * 管理员登录在 AdminAuthController 中单独处理。
 *
 * 映射路径：/api/auth
 */
@RestController // 标识这是一个 RESTful 控制器，所有返回值自动序列化为 JSON
@RequestMapping("/api/auth") // 将控制器映射到 /api/auth 路径下
public class AuthController {

    private final UserService userService; // 用户业务服务，使用构造器注入

    /**
     * 构造器注入 UserService
     *
     * @param userService 用户业务服务实例
     */
    public AuthController(UserService userService) { // 通过构造器注入依赖
        this.userService = userService; // 将注入的服务赋值给成员变量
    }

    /**
     * 用户注册
     *
     * 接收用户提交的注册信息，包括用户名、密码、昵称等。用户名不能重复，
     * 由业务层进行唯一性校验。注册成功后即可使用该账号登录。
     *
     * 请求方式：POST /api/auth/register
     *
     * @param dto 用户注册数据传输对象，包含注册所需的所有字段，由 @Valid 进行参数校验
     * @return Result 包含注册成功提示信息的统一响应结果
     */
    @PostMapping("/register") // 处理 HTTP POST 请求，映射到 /api/auth/register
    public Result<String> register(@Valid @RequestBody UserRegisterDTO dto) { // @Valid 校验注册参数，@RequestBody 将 JSON 反序列化为 UserRegisterDTO
        userService.register(dto); // 调用用户服务执行注册逻辑
        return Result.success("注册成功"); // 返回注册成功的提示信息
    }

    /**
     * 用户登录
     *
     * 验证用户账号和密码，登录成功后返回 JWT token。
     * 客户端需要在后续请求的请求头中携带该 token 进行身份认证。
     *
     * 请求方式：POST /api/auth/login
     *
     * @param dto 登录数据传输对象，包含用户名和密码，由 @Valid 校验
     * @return Result 包含登录成功信息（token、用户 ID、昵称、角色等）的统一响应结果
     */
    @PostMapping("/login") // 处理 HTTP POST 请求，映射到 /api/auth/login
    public Result<LoginVO> login(@Valid @RequestBody LoginDTO dto) { // @Valid 校验登录参数，@RequestBody 将 JSON 反序列化为 LoginDTO
        LoginVO vo = userService.login(dto); // 调用用户服务执行登录逻辑，返回包含 token 的视图对象
        return Result.success(vo); // 将登录视图对象包装为成功响应返回
    }
}
