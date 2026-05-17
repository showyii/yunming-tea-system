package com.yunming.tea.controller; // 包声明：控制器层

import com.yunming.tea.common.Result; // 统一响应结果封装类
import com.yunming.tea.service.TeaActivityService; // 茶活动业务逻辑服务
import com.yunming.tea.vo.ActivityVO; // 活动视图对象
import org.springframework.web.bind.annotation.*; // Spring MVC 注解：包括 RestController、RequestMapping、GetMapping

import java.util.List; // Java 集合框架的 List 接口

/**
 * 活动控制器（面向普通用户）
 *
 * 负责处理与茶文化活动相关的 HTTP 请求，包括：
 * - 获取活动列表（支持按用户收藏状态筛选）
 * - 获取活动详情
 *
 * 映射路径：/api/activities
 */
@RestController // 标识这是一个 RESTful 控制器，所有方法返回值自动序列化为 JSON
@RequestMapping("/api/activities") // 将控制器映射到 /api/activities 路径下
public class ActivityController {

    private final TeaActivityService activityService; // 茶活动业务服务，使用构造器注入

    /**
     * 构造器注入 ActivityService
     *
     * @param activityService 茶活动业务服务实例
     */
    public ActivityController(TeaActivityService activityService) { // 通过构造器注入依赖
        this.activityService = activityService; // 将注入的服务赋值给成员变量
    }

    /**
     * 获取活动列表
     *
     * 该接口支持用户登录状态识别：如果请求中包含 userId 属性（由拦截器设置），
     * 则返回的列表中会包含该用户是否已收藏每个活动的标记。
     *
     * 请求方式：GET /api/activities
     *
     * @param userId 当前登录用户的 ID，从请求属性中获取，可选（未登录时为空）
     * @return Result 包含活动列表的统一响应结果
     */
    @GetMapping // 处理 HTTP GET 请求，映射到 /api/activities
    public Result<List<ActivityVO>> list(@RequestAttribute(value = "userId", required = false) Long userId) { // @RequestAttribute 从请求属性中获取 userId，可选参数
        return Result.success(activityService.list(userId)); // 调用业务层获取活动列表并包装为成功响应
    }

    /**
     * 获取活动详情
     *
     * 根据活动 ID 获取单个活动的详细信息。如果用户已登录，会同时返回该用户
     * 是否已收藏该活动、是否已报名等信息。
     *
     * 请求方式：GET /api/activities/{id}
     *
     * @param id     活动 ID，从 URL 路径中获取
     * @param userId 当前登录用户的 ID，从请求属性中获取，可选
     * @return Result 包含活动详细信息的统一响应结果
     */
    @GetMapping("/{id}") // 处理 HTTP GET 请求，映射到 /api/activities/{id}，{id} 为路径参数
    public Result<ActivityVO> detail(@PathVariable Long id, // @PathVariable 将 URL 中的 {id} 绑定到方法参数
                                      @RequestAttribute(value = "userId", required = false) Long userId) { // @RequestAttribute 从请求属性中获取 userId，可选参数
        return Result.success(activityService.getDetail(id, userId)); // 调用业务层获取活动详情并包装为成功响应
    }
}
