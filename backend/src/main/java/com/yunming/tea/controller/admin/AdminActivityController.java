package com.yunming.tea.controller.admin; // 包声明：管理后台控制器层

import com.yunming.tea.common.Result; // 统一响应结果封装类
import com.yunming.tea.entity.TeaActivity; // 茶活动实体类
import com.yunming.tea.mapper.TeaActivityMapper; // 茶活动数据访问层（MyBatis-Plus Mapper）
import com.yunming.tea.service.TeaActivityService; // 茶活动业务逻辑服务
import com.yunming.tea.vo.ActivityVO; // 活动视图对象
import org.springframework.web.bind.annotation.*; // Spring MVC 注解：包括 RestController、RequestMapping、GetMapping 等

import java.util.List; // Java 集合框架的 List 接口

/**
 * 管理员 - 活动管理控制器
 *
 * 负责管理后台的茶文化活动管理功能，包括：
 * - 查看所有活动列表
 * - 查看单个活动详情
 * - 创建新活动
 * - 编辑活动信息
 * - 删除活动
 *
 * 所有接口需要管理员权限（由拦截器验证 token 中的管理员角色）。
 *
 * 映射路径：/api/admin/activities
 */
@RestController // 标识这是一个 RESTful 控制器，所有返回值自动序列化为 JSON
@RequestMapping("/api/admin/activities") // 将控制器映射到 /api/admin/activities 路径下
public class AdminActivityController {

    private final TeaActivityService activityService; // 茶活动业务服务，用于查询操作
    private final TeaActivityMapper activityMapper; // 茶活动 Mapper，用于直接的增删改操作

    /**
     * 构造器注入 TeaActivityService 和 TeaActivityMapper
     *
     * @param activityService 茶活动业务服务实例，用于列表和详情的查询
     * @param activityMapper  茶活动数据访问层实例，用于创建、更新和删除操作
     */
    public AdminActivityController(TeaActivityService activityService, TeaActivityMapper activityMapper) { // 通过构造器注入两个依赖
        this.activityService = activityService; // 将注入的活动服务赋值给成员变量
        this.activityMapper = activityMapper; // 将注入的 Mapper 赋值给成员变量
    }

    /**
     * 获取所有活动列表（管理员视图）
     *
     * 返回系统中所有的茶文化活动，包括已发布的和未发布的活动。
     * 区别于用户端接口，管理员可以看到所有状态的活动。
     *
     * 请求方式：GET /api/admin/activities
     *
     * @return Result 包含所有活动列表的统一响应结果
     */
    @GetMapping // 处理 HTTP GET 请求，映射到 /api/admin/activities
    public Result<List<ActivityVO>> list() { // 获取全部活动列表的方法
        return Result.success(activityService.list(null)); // 调用业务层获取全部活动（传 null 表示不按用户筛选），包装为成功响应
    }

    /**
     * 查看单个活动详情（管理员视图）
     *
     * 根据活动 ID 获取活动的详细信息。
     *
     * 请求方式：GET /api/admin/activities/{id}
     *
     * @param id 活动 ID，从 URL 路径中获取
     * @return Result 包含活动详细信息的统一响应结果
     */
    @GetMapping("/{id}") // 处理 HTTP GET 请求，映射到 /api/admin/activities/{id}
    public Result<ActivityVO> detail(@PathVariable Long id) { // @PathVariable 将 URL 中的 {id} 绑定到方法参数
        return Result.success(activityService.getDetail(id, null)); // 调用业务层获取活动详情（传 null 表示不按用户筛选），包装为成功响应
    }

    /**
     * 创建新活动
     *
     * 在系统中创建一个新的茶文化活动。接收活动的完整信息（标题、描述、
     * 开始时间、结束时间、地点、封面图片等），保存到数据库。
     *
     * 请求方式：POST /api/admin/activities
     *
     * @param activity 活动实体对象，由请求体 JSON 自动反序列化
     * @return Result 包含新创建的活动实体（含自动生成的 ID）的统一响应结果
     */
    @PostMapping // 处理 HTTP POST 请求，映射到 /api/admin/activities
    public Result<TeaActivity> create(@RequestBody TeaActivity activity) { // @RequestBody 将请求体 JSON 反序列化为 TeaActivity 实体
        activityMapper.insert(activity); // 使用 Mapper 直接将活动实体插入数据库
        return Result.success(activity); // 返回新创建的活动实体（此时 ID 已由数据库自动填充）
    }

    /**
     * 编辑活动信息
     *
     * 更新指定活动的信息。接收要修改的字段，对数据库中已有活动进行更新。
     *
     * 请求方式：PUT /api/admin/activities/{id}
     *
     * @param id       活动 ID，从 URL 路径中获取，标识要修改的活动
     * @param activity 活动实体对象，包含要更新的字段，由请求体 JSON 自动反序列化
     * @return Result 包含更新成功提示的统一响应结果
     */
    @PutMapping("/{id}") // 处理 HTTP PUT 请求，映射到 /api/admin/activities/{id}
    public Result<String> update(@PathVariable Long id, @RequestBody TeaActivity activity) { // @PathVariable 从 URL 获取 ID，@RequestBody 反序列化请求体
        activity.setId(id); // 将路径参数中的 ID 设置到活动实体上，确保更新正确的记录
        activityMapper.updateById(activity); // 使用 Mapper 根据 ID 更新活动记录
        return Result.success("更新成功"); // 返回更新成功的提示信息
    }

    /**
     * 删除活动
     *
     * 根据活动 ID 从数据库中删除指定的活动。删除操作不可逆，
     * 建议在删除前检查该活动是否有关联的报名记录。
     *
     * 请求方式：DELETE /api/admin/activities/{id}
     *
     * @param id 活动 ID，从 URL 路径中获取，标识要删除的活动
     * @return Result 包含删除成功提示的统一响应结果
     */
    @DeleteMapping("/{id}") // 处理 HTTP DELETE 请求，映射到 /api/admin/activities/{id}
    public Result<String> delete(@PathVariable Long id) { // @PathVariable 将 URL 中的 {id} 绑定到方法参数
        activityMapper.deleteById(id); // 使用 Mapper 根据 ID 删除活动记录
        return Result.success("删除成功"); // 返回删除成功的提示信息
    }
}
