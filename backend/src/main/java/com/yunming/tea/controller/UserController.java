package com.yunming.tea.controller; // 包声明：控制器层

import com.yunming.tea.common.Result; // 统一响应结果封装类
import com.yunming.tea.dto.ChangePasswordDTO; // 修改密码请求的数据传输对象
import com.yunming.tea.dto.UpdateProfileDTO; // 更新个人资料请求的数据传输对象
import com.yunming.tea.service.UserService; // 用户业务逻辑服务
import com.yunming.tea.vo.UserProfileVO; // 用户个人资料视图对象
import org.springframework.web.bind.annotation.*; // Spring MVC 注解：包括 RestController、RequestMapping、GetMapping 等

import javax.validation.Valid; // JSR-303 Bean 校验注解，用于验证请求体参数

/**
 * 用户控制器（面向普通用户）
 *
 * 负责处理当前登录用户的个人信息管理功能，包括：
 * - 获取个人资料
 * - 修改个人资料（昵称、头像、手机号等）
 * - 修改密码
 *
 * 所有操作都需要用户登录后才能执行，操作对象仅限于当前登录用户本人。
 *
 * 映射路径：/api/user
 */
@RestController // 标识这是一个 RESTful 控制器，所有返回值自动序列化为 JSON
@RequestMapping("/api/user") // 将控制器映射到 /api/user 路径下
public class UserController {

    private final UserService userService; // 用户业务服务，使用构造器注入

    /**
     * 构造器注入 UserService
     *
     * @param userService 用户业务服务实例
     */
    public UserController(UserService userService) { // 通过构造器注入依赖
        this.userService = userService; // 将注入的服务赋值给成员变量
    }

    /**
     * 获取当前用户的个人资料
     *
     * 返回当前登录用户的详细信息，包括用户名、昵称、头像、手机号、
     * 注册时间等。用于"个人中心"页面的信息展示。
     *
     * 请求方式：GET /api/user/profile
     *
     * @param userId 当前登录用户的 ID，从请求属性中获取（必填）
     * @return Result 包含用户个人资料视图对象的统一响应结果
     */
    @GetMapping("/profile") // 处理 HTTP GET 请求，映射到 /api/user/profile
    public Result<UserProfileVO> getProfile(@RequestAttribute("userId") Long userId) { // @RequestAttribute 从请求属性中获取 userId（必填）
        UserProfileVO vo = userService.getUserProfile(userId); // 调用业务层获取用户个人资料
        return Result.success(vo); // 将用户资料视图对象包装为成功响应返回
    }

    /**
     * 修改个人资料
     *
     * 更新当前登录用户的个人资料信息，如昵称、头像 URL、手机号等。
     * 部分字段（如用户名）可能不允许修改，由业务层进行控制。
     *
     * 请求方式：PUT /api/user/profile
     *
     * @param userId 当前登录用户的 ID，从请求属性中获取（必填）
     * @param dto    更新个人资料的数据传输对象，包含要修改的字段，由 @Valid 校验
     * @return Result 包含资料已更新提示的统一响应结果
     */
    @PutMapping("/profile") // 处理 HTTP PUT 请求，映射到 /api/user/profile
    public Result<String> updateProfile(@RequestAttribute("userId") Long userId, // @RequestAttribute 从请求属性中获取 userId（必填）
                                         @Valid @RequestBody UpdateProfileDTO dto) { // @Valid 校验请求体，@RequestBody 将 JSON 反序列化为 UpdateProfileDTO
        userService.updateProfile(userId, dto); // 调用业务层更新用户个人资料
        return Result.success("资料已更新"); // 返回资料已更新的提示信息
    }

    /**
     * 修改密码
     *
     * 修改当前登录用户的登录密码。需要提供原密码进行身份验证，
     * 并输入新密码。由参数校验规则确保新密码的复杂度要求。
     *
     * 请求方式：PUT /api/user/password
     *
     * @param userId 当前登录用户的 ID，从请求属性中获取（必填）
     * @param dto    修改密码的数据传输对象，包含原密码和新密码，由 @Valid 校验
     * @return Result 包含密码已修改提示的统一响应结果
     */
    @PutMapping("/password") // 处理 HTTP PUT 请求，映射到 /api/user/password
    public Result<String> changePassword(@RequestAttribute("userId") Long userId, // @RequestAttribute 从请求属性中获取 userId（必填）
                                          @Valid @RequestBody ChangePasswordDTO dto) { // @Valid 校验请求体，@RequestBody 将 JSON 反序列化为 ChangePasswordDTO
        userService.changePassword(userId, dto); // 调用业务层修改用户密码
        return Result.success("密码已修改"); // 返回密码已修改的提示信息
    }
}
