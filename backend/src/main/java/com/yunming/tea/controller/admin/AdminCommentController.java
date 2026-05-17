package com.yunming.tea.controller.admin; // 包声明：管理后台控制器层

import com.baomidou.mybatisplus.extension.plugins.pagination.Page; // MyBatis-Plus 分页结果对象，包含分页数据和总记录数
import com.yunming.tea.common.Result; // 统一响应结果封装类
import com.yunming.tea.service.CommentService; // 评论业务逻辑服务
import com.yunming.tea.vo.CommentVO; // 评论视图对象
import org.springframework.web.bind.annotation.*; // Spring MVC 注解：包括 RestController、RequestMapping、GetMapping 等

/**
 * 管理员 - 评论管理控制器
 *
 * 负责管理后台对用户评论的管理功能，包括：
 * - 查看所有评论（分页）
 * - 按商品查看评论（分页）
 * - 删除不当评论
 *
 * 管理员的删除操作不受用户归属限制，可以删除任意用户的评论。
 *
 * 映射路径：/api/admin/comments
 */
@RestController // 标识这是一个 RESTful 控制器，所有返回值自动序列化为 JSON
@RequestMapping("/api/admin/comments") // 将控制器映射到 /api/admin/comments 路径下
public class AdminCommentController {

    private final CommentService commentService; // 评论业务服务，使用构造器注入

    /**
     * 构造器注入 CommentService
     *
     * @param commentService 评论业务服务实例
     */
    public AdminCommentController(CommentService commentService) { // 通过构造器注入依赖
        this.commentService = commentService; // 将注入的服务赋值给成员变量
    }

    /**
     * 获取所有评论列表（分页）
     *
     * 管理员查看系统中所有用户发表的评论，用于内容审核和管理。
     * 支持分页展示，按评论时间倒序排列。
     *
     * 请求方式：GET /api/admin/comments?page=1&size=10
     *
     * @param page 当前页码，默认为第 1 页
     * @param size 每页显示条数，默认为 10 条
     * @return Result 包含分页评论列表的统一响应结果
     */
    @GetMapping // 处理 HTTP GET 请求，映射到 /api/admin/comments
    public Result<Page<CommentVO>> list(@RequestParam(defaultValue = "1") Integer page, // @RequestParam 获取分页页码，默认值为 1
                                        @RequestParam(defaultValue = "10") Integer size) { // @RequestParam 获取每页条数，默认值为 10
        return Result.success(commentService.getAllComments(page, size)); // 调用业务层获取所有评论并包装为成功响应
    }

    /**
     * 按商品获取评论列表（分页）
     *
     * 管理员查看指定商品下的所有用户评论。
     *
     * 请求方式：GET /api/admin/comments/product/{productId}?page=1&size=10
     *
     * @param productId 商品 ID，从 URL 路径中获取
     * @param page      当前页码，默认为第 1 页
     * @param size      每页显示条数，默认为 10 条
     * @return Result 包含指定商品分页评论列表的统一响应结果
     */
    @GetMapping("/product/{productId}") // 处理 HTTP GET 请求，映射到 /api/admin/comments/product/{productId}
    public Result<Page<CommentVO>> listByProduct(@PathVariable Long productId, // @PathVariable 将 URL 中的 {productId} 绑定到方法参数
                                                  @RequestParam(defaultValue = "1") Integer page, // @RequestParam 获取分页页码，默认值为 1
                                                  @RequestParam(defaultValue = "10") Integer size) { // @RequestParam 获取每页条数，默认值为 10
        return Result.success(commentService.getProductComments(productId, page, size)); // 调用业务层获取指定商品的评论并包装为成功响应
    }

    /**
     * 删除评论（管理员操作）
     *
     * 管理员可以删除任意用户的评论，用于清除不当言论或违规内容。
     * 与用户端的删除不同，管理员删除不需要验证评论归属（传 null 表示管理员操作）。
     *
     * 请求方式：DELETE /api/admin/comments/{id}
     *
     * @param id 评论记录的 ID，从 URL 路径中获取
     * @return Result 包含评论已删除提示的统一响应结果
     */
    @DeleteMapping("/{id}") // 处理 HTTP DELETE 请求，映射到 /api/admin/comments/{id}
    public Result<String> delete(@PathVariable Long id) { // @PathVariable 将 URL 中的 {id} 绑定到方法参数
        commentService.deleteComment(id, null); // 调用业务层删除评论（userId 传 null 表示管理员操作，不校验归属）
        return Result.success("评论已删除"); // 返回评论已删除的提示信息
    }
}
