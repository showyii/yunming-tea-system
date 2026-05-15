package com.yunming.tea.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yunming.tea.dto.CommentDTO;
import com.yunming.tea.vo.CommentVO;

public interface CommentService {

    void addComment(CommentDTO dto, Long userId);

    Page<CommentVO> getProductComments(Long productId, Integer page, Integer size);

    Page<CommentVO> getAllComments(Integer page, Integer size);

    void deleteComment(Long commentId, Long userId);
}
