package com.yunming.tea.controller; // 包声明：控制器层

import com.baomidou.mybatisplus.extension.plugins.pagination.Page; // MyBatis-Plus 分页结果对象，包含分页数据和总记录数
import com.yunming.tea.common.Result; // 统一响应结果封装类
import com.yunming.tea.dto.CommentDTO; // 评论请求的数据传输对象
import com.yunming.tea.service.CommentService; // 评论业务逻辑服务
import com.yunming.tea.vo.CommentVO; // 评论视图对象
import org.springframework.web.bind.annotation.*; // Spring MVC 注解：包括 RestController、RequestMapping、PostMapping 等

import javax.validation.Valid; // JSR-303 Bean 校验注解，用于验证请求体参数

/**
 * 评论控制器（用户端，面向普通用户）
 *
 * 负责处理用户对茶产品的评论相关操作，包括：
 * - 发表商品评论
 * - 查看某个商品的评论列表（分页）
 * - 删除自己的评论
 *
 * 映射路径：/api/comments
 */
@RestController // 标识这是一个 RESTful 控制器，所有返回值自动序列化为 JSON
@RequestMapping("/api/comments") // 将控制器映射到 /api/comments 路径下
public class CommentController {

    private final CommentService commentService; // 评论业务服务，使用构造器注入

    /**
     * 构造器注入 CommentService
     *
     * @param commentService 评论业务服务实例
     */
    public CommentController(CommentService commentService) { // 通过构造器注入依赖
        this.commentService = commentService; // 将注入的服务赋值给成员变量
    }

    /**
     * 发表商品评论
     *
     * 用户对已购买或已体验过的茶产品发表评论，包括评分、文字内容和图片等。
     * 需要用户登录后才能操作。
     *
     * 请求方式：POST /api/comments
     *
     * @param dto    评论数据传输对象，包含商品 ID、评分、内容等字段，由 @Valid 校验
     * @param userId 当前登录用户的 ID，从请求属性中获取（必填）
     * @return Result 包含评论发表成功提示的统一响应结果
     */
    @PostMapping // 处理 HTTP POST 请求，映射到 /api/comments
    public Result<String> add(@Valid @RequestBody CommentDTO dto, // @Valid 校验请求体，@RequestBody 将 JSON 反序列化为 CommentDTO
                               @RequestAttribute("userId") Long userId) { // @RequestAttribute 从请求属性中获取 userId（必填）
        commentService.addComment(dto, userId); // 调用业务层添加评论
        return Result.success("评论发表成功"); // 返回评论发表成功的提示信息
    }

    /**
     * 获取指定商品的评论列表（分页）
     *
     * 根据商品 ID 查询该商品的所有评论，支持分页展示。
     * 评论列表按时间倒序排列，最新的评论优先显示。
     *
     * 请求方式：GET /api/comments/{productId}?page=1&size=10
     *
     * @param productId 商品 ID，从 URL 路径中获取
     * @param page      当前页码，默认为第 1 页
     * @param size      每页显示条数，默认为 10 条
     * @return Result 包含分页评论数据的统一响应结果
     */
    @GetMapping("/{productId}") // 处理 HTTP GET 请求，映射到 /api/comments/{productId}
    public Result<Page<CommentVO>> list(@PathVariable Long productId, // @PathVariable 将 URL 中的 {productId} 绑定到方法参数
                                         @RequestParam(defaultValue = "1") Integer page, // @RequestParam 获取分页页码，默认值为 1
                                         @RequestParam(defaultValue = "10") Integer size) { // @RequestParam 获取每页条数，默认值为 10
        return Result.success(commentService.getProductComments(productId, page, size)); // 调用业务层获取分页评论并包装为成功响应
    }

    /**
     * 删除自己的评论
     *
     * 用户只能删除自己发表的评论，由业务层校验评论归属。
     *
     * 请求方式：DELETE /api/comments/{id}
     *
     * @param id     评论记录的 ID，从 URL 路径中获取
     * @param userId 当前登录用户的 ID，从请求属性中获取（必填），用于验证评论归属
     * @return Result 包含评论已删除提示的统一响应结果
     */
    @DeleteMapping("/{id}") // 处理 HTTP DELETE 请求，映射到 /api/comments/{id}
    public Result<String> delete(@PathVariable Long id, // @PathVariable 将 URL 中的 {id} 绑定到方法参数
                                  @RequestAttribute("userId") Long userId) { // @RequestAttribute 从请求属性中获取 userId（必填）
        commentService.deleteComment(id, userId); // 调用业务层删除评论
        return Result.success("评论已删除"); // 返回评论已删除的提示信息
    }
}
