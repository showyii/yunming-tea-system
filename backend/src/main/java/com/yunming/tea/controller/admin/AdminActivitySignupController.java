package com.yunming.tea.controller.admin; // 包声明：管理后台控制器层

import com.yunming.tea.common.Result; // 统一响应结果封装类
import com.yunming.tea.service.ActivitySignupService; // 活动报名业务逻辑服务
import com.yunming.tea.vo.ActivitySignupVO; // 活动报名记录视图对象
import org.springframework.web.bind.annotation.*; // Spring MVC 注解：包括 RestController、RequestMapping、GetMapping

import java.util.List; // Java 集合框架的 List 接口

/**
 * 管理员 - 活动报名管理控制器
 *
 * 负责管理后台查看活动报名记录的功能，主要用于：
 * - 按活动查询所有报名记录
 *
 * 管理员可以查看任意活动的所有报名用户列表，以便进行活动人员管理。
 *
 * 映射路径：/api/admin/activity-signups
 */
@RestController // 标识这是一个 RESTful 控制器，所有返回值自动序列化为 JSON
@RequestMapping("/api/admin/activity-signups") // 将控制器映射到 /api/admin/activity-signups 路径下
public class AdminActivitySignupController {

    private final ActivitySignupService signupService; // 活动报名业务服务，使用构造器注入

    /**
     * 构造器注入 ActivitySignupService
     *
     * @param signupService 活动报名业务服务实例
     */
    public AdminActivitySignupController(ActivitySignupService signupService) { // 通过构造器注入依赖
        this.signupService = signupService; // 将注入的服务赋值给成员变量
    }

    /**
     * 获取指定活动的所有报名记录（管理员视图）
     *
     * 管理员可以根据活动 ID 查看该活动下所有用户的报名记录，
     * 包括用户信息、报名时间等。区别于用户端只能查看自己的报名记录。
     *
     * 请求方式：GET /api/admin/activity-signups?activityId=1
     *
     * @param activityId 活动 ID，通过查询参数传入，指定要查看哪个活动的报名情况
     * @return Result 包含该活动所有报名记录列表的统一响应结果
     */
    @GetMapping // 处理 HTTP GET 请求，映射到 /api/admin/activity-signups
    public Result<List<ActivitySignupVO>> list(@RequestParam Long activityId) { // @RequestParam 从查询参数中获取 activityId（必填）
        return Result.success(signupService.adminList(activityId)); // 调用业务层获取指定活动的报名列表并包装为成功响应
    }
}
