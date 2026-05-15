package com.yunming.tea.controller.admin;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yunming.tea.common.Result;
import com.yunming.tea.service.CommentService;
import com.yunming.tea.vo.CommentVO;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/comments")
public class AdminCommentController {

    private final CommentService commentService;

    public AdminCommentController(CommentService commentService) {
        this.commentService = commentService;
    }

    @GetMapping
    public Result<Page<CommentVO>> list(@RequestParam(defaultValue = "1") Integer page,
                                        @RequestParam(defaultValue = "10") Integer size) {
        return Result.success(commentService.getAllComments(page, size));
    }

    @GetMapping("/product/{productId}")
    public Result<Page<CommentVO>> listByProduct(@PathVariable Long productId,
                                                  @RequestParam(defaultValue = "1") Integer page,
                                                  @RequestParam(defaultValue = "10") Integer size) {
        return Result.success(commentService.getProductComments(productId, page, size));
    }

    @DeleteMapping("/{id}")
    public Result<String> delete(@PathVariable Long id) {
        commentService.deleteComment(id, null);
        return Result.success("评论已删除");
    }
}
