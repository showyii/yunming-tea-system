package com.yunming.tea.controller; // 包声明：控制器层

import com.yunming.tea.common.Result; // 统一响应结果封装类
import com.yunming.tea.service.ActivitySignupService; // 活动报名业务逻辑服务
import com.yunming.tea.vo.ActivitySignupVO; // 活动报名视图对象
import org.springframework.web.bind.annotation.*; // Spring MVC 注解：包括 RestController、RequestMapping、PostMapping 等

import javax.validation.Valid; // JSR-303 Bean 校验注解，用于验证请求体参数
import java.util.List; // Java 集合框架的 List 接口

/**
 * 活动报名控制器（面向普通用户）
 *
 * 负责处理用户对茶文化活动的报名相关操作，包括：
 * - 报名参加活动
 * - 查看我的报名记录
 * - 取消报名
 *
 * 映射路径：/api/activity-signups
 */
@RestController // 标识这是一个 RESTful 控制器
@RequestMapping("/api/activity-signups") // 将控制器映射到 /api/activity-signups 路径下
public class ActivitySignupController {

    private final ActivitySignupService signupService; // 活动报名业务服务，使用构造器注入

    /**
     * 构造器注入 ActivitySignupService
     *
     * @param signupService 活动报名业务服务实例
     */
    public ActivitySignupController(ActivitySignupService signupService) { // 通过构造器注入依赖
        this.signupService = signupService; // 将注入的服务赋值给成员变量
    }

    /**
     * 用户报名参加活动
     *
     * 接收用户提交的报名信息，校验数据合法性后，将用户与该活动建立报名关联。
     * 每个用户对同一活动只能报名一次，重复报名会由业务层抛出异常。
     *
     * 请求方式：POST /api/activity-signups
     *
     * @param dto    活动报名的数据传输对象，包含活动 ID，由 @Valid 进行参数校验
     * @param userId 当前登录用户的 ID，从请求属性中获取（必填）
     * @return Result 包含成功提示信息的统一响应结果
     */
    @PostMapping // 处理 HTTP POST 请求，映射到 /api/activity-signups
    public Result<String> signup(@Valid @RequestBody com.yunming.tea.dto.ActivitySignupDTO dto, // @Valid 校验请求体，@RequestBody 将 JSON 反序列化为 DTO
                                  @RequestAttribute("userId") Long userId) { // @RequestAttribute 从请求属性中获取 userId（必填）
        signupService.signup(userId, dto.getActivityId()); // 调用业务层执行报名操作
        return Result.success("报名成功"); // 返回报名成功的提示信息
    }

    /**
     * 查看我的报名记录
     *
     * 获取当前登录用户所有活动的报名记录列表，包括报名时间、活动状态等信息。
     *
     * 请求方式：GET /api/activity-signups
     *
     * @param userId 当前登录用户的 ID，从请求属性中获取（必填）
     * @return Result 包含用户报名记录列表的统一响应结果
     */
    @GetMapping // 处理 HTTP GET 请求，映射到 /api/activity-signups
    public Result<List<ActivitySignupVO>> mySignups(@RequestAttribute("userId") Long userId) { // @RequestAttribute 从请求属性中获取 userId（必填）
        return Result.success(signupService.mySignups(userId)); // 调用业务层获取当前用户的报名列表并包装为成功响应
    }

    /**
     * 取消报名
     *
     * 用户取消对指定活动的报名。只有报名者本人才能取消自己的报名，
     * 由业务层验证报名记录归属。
     *
     * 请求方式：PUT /api/activity-signups/{id}/cancel
     *
     * @param id     报名记录的 ID，从 URL 路径中获取
     * @param userId 当前登录用户的 ID，从请求属性中获取（必填）
     * @return Result 包含取消成功提示信息的统一响应结果
     */
    @PutMapping("/{id}/cancel") // 处理 HTTP PUT 请求，映射到 /api/activity-signups/{id}/cancel
    public Result<String> cancel(@PathVariable Long id, // @PathVariable 将 URL 中的 {id} 绑定到方法参数
                                  @RequestAttribute("userId") Long userId) { // @RequestAttribute 从请求属性中获取 userId（必填）
        signupService.cancel(id, userId); // 调用业务层执行取消报名操作
        return Result.success("已取消报名"); // 返回取消成功的提示信息
    }
}
