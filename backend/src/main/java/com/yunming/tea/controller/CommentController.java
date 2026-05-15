package com.yunming.tea.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yunming.tea.common.Result;
import com.yunming.tea.dto.CommentDTO;
import com.yunming.tea.service.CommentService;
import com.yunming.tea.vo.CommentVO;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * 评论控制器（用户端）
 */
@RestController
@RequestMapping("/api/comments")
public class CommentController {

    private final CommentService commentService;

    public CommentController(CommentService commentService) {
        this.commentService = commentService;
    }

    /**
     * 发表评论
     * POST /api/comments
     */
    @PostMapping
    public Result<String> add(@Valid @RequestBody CommentDTO dto,
                               @RequestAttribute("userId") Long userId) {
        commentService.addComment(dto, userId);
        return Result.success("评论发表成功");
    }

    /**
     * 获取商品评论
     * GET /api/comments/{productId}
     */
    @GetMapping("/{productId}")
    public Result<Page<CommentVO>> list(@PathVariable Long productId,
                                         @RequestParam(defaultValue = "1") Integer page,
                                         @RequestParam(defaultValue = "10") Integer size) {
        return Result.success(commentService.getProductComments(productId, page, size));
    }

    /**
     * 删除评论
     * DELETE /api/comments/{id}
     */
    @DeleteMapping("/{id}")
    public Result<String> delete(@PathVariable Long id,
                                  @RequestAttribute("userId") Long userId) {
        commentService.deleteComment(id, userId);
        return Result.success("评论已删除");
    }
}
