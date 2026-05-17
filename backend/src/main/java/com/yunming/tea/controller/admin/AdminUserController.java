package com.yunming.tea.controller.admin; // 包声明：管理后台控制器层

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper; // MyBatis-Plus Lambda 查询条件构造器，用于类型安全的条件构建
import com.baomidou.mybatisplus.extension.plugins.pagination.Page; // MyBatis-Plus 分页结果对象，包含分页数据和总记录数
import com.yunming.tea.common.Result; // 统一响应结果封装类
import com.yunming.tea.entity.User; // 用户实体类
import com.yunming.tea.mapper.UserMapper; // 用户数据访问层（MyBatis-Plus Mapper）
import org.springframework.web.bind.annotation.*; // Spring MVC 注解：包括 RestController、RequestMapping、GetMapping 等

import java.util.Map; // Java 集合框架的 Map 接口

/**
 * 管理员 - 用户管理控制器
 *
 * 负责管理后台对系统中注册用户的管理功能，包括：
 * - 查看用户列表（支持关键字搜索和分页）
 * - 修改用户状态（如启用、禁用账号）
 *
 * 该控制器直接通过 Mapper 操作数据库，用于简单的用户管理场景。
 *
 * 映射路径：/api/admin/users
 */
@RestController // 标识这是一个 RESTful 控制器，所有返回值自动序列化为 JSON
@RequestMapping("/api/admin/users") // 将控制器映射到 /api/admin/users 路径下
public class AdminUserController {

    private final UserMapper userMapper; // 用户数据访问层，直接使用 Mapper 进行数据库操作

    /**
     * 构造器注入 UserMapper
     *
     * @param userMapper 用户数据访问层实例
     */
    public AdminUserController(UserMapper userMapper) { // 通过构造器注入依赖
        this.userMapper = userMapper; // 将注入的 Mapper 赋值给成员变量
    }

    /**
     * 获取用户列表（分页，支持关键字搜索）
     *
     * 管理员查看系统中所有注册用户的信息，支持按用户名、手机号、邮箱
     * 进行模糊搜索。用户列表按注册时间倒序排列。
     *
     * 请求方式：GET /api/admin/users?page=1&size=10&keyword=张三
     *
     * @param page    当前页码，默认为第 1 页
     * @param size    每页显示条数，默认为 10 条
     * @param keyword 搜索关键字，可选参数。支持模糊匹配用户名、手机号、邮箱
     * @return Result 包含分页用户列表的统一响应结果
     */
    @GetMapping // 处理 HTTP GET 请求，映射到 /api/admin/users
    public Result<Page<User>> list(@RequestParam(defaultValue = "1") Integer page, // @RequestParam 获取分页页码，默认值为 1
                                    @RequestParam(defaultValue = "10") Integer size, // @RequestParam 获取每页条数，默认值为 10
                                    @RequestParam(required = false) String keyword) { // @RequestParam 获取搜索关键字，可选参数
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<User>() // 创建 Lambda 查询条件包装器，类型安全
                .orderByDesc(User::getCreateTime); // 按创建时间倒序排列，最新的用户排在最前面
        if (keyword != null && !keyword.isEmpty()) { // 判断搜索关键字是否为空
            wrapper.and(w -> w.like(User::getUsername, keyword) // 在用户名中模糊匹配关键字
                    .or().like(User::getPhone, keyword) // 或者在手机号中模糊匹配关键字
                    .or().like(User::getEmail, keyword)); // 或者在邮箱中模糊匹配关键字
        }
        Page<User> result = userMapper.selectPage(new Page<>(page, size), wrapper); // 使用 Mapper 执行分页查询
        return Result.success(result); // 将分页查询结果包装为成功响应返回
    }

    /**
     * 修改用户状态
     *
     * 管理员可以启用或禁用用户账号。被禁用的账号将无法登录系统。
     *
     * 请求方式：PUT /api/admin/users/{id}/status
     * 请求体示例：{"status": 0}  // 0:禁用, 1:正常
     *
     * @param id   用户 ID，从 URL 路径中获取，标识要修改状态的用户
     * @param body 请求体 Map，包含要更新的状态值（key 为 "status"）
     * @return Result 包含状态已更新提示的统一响应结果
     */
    @PutMapping("/{id}/status") // 处理 HTTP PUT 请求，映射到 /api/admin/users/{id}/status
    public Result<String> updateStatus(@PathVariable Long id, @RequestBody Map<String, Integer> body) { // @PathVariable 获取用户 ID，@RequestBody 获取状态参数
        User user = userMapper.selectById(id); // 使用 Mapper 根据 ID 查询用户记录
        if (user != null) { // 判断用户是否存在
            user.setStatus(body.get("status")); // 从请求体中获取新状态值并设置到用户实体上
            userMapper.updateById(user); // 使用 Mapper 根据 ID 更新用户状态
        }
        return Result.success("状态已更新"); // 返回状态已更新的提示信息
    }
}
