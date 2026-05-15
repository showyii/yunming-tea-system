package com.yunming.tea.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yunming.tea.dto.CommentDTO;
import com.yunming.tea.entity.Comment;
import com.yunming.tea.entity.TeaProduct;
import com.yunming.tea.entity.User;
import com.yunming.tea.entity.UserProfile;
import com.yunming.tea.exception.BusinessException;
import com.yunming.tea.mapper.CommentMapper;
import com.yunming.tea.mapper.TeaProductMapper;
import com.yunming.tea.mapper.UserMapper;
import com.yunming.tea.mapper.UserProfileMapper;
import com.yunming.tea.service.CommentService;
import com.yunming.tea.vo.CommentVO;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class CommentServiceImpl implements CommentService {

    private final CommentMapper commentMapper;
    private final UserMapper userMapper;
    private final UserProfileMapper userProfileMapper;
    private final TeaProductMapper productMapper;

    public CommentServiceImpl(CommentMapper commentMapper, UserMapper userMapper,
                               UserProfileMapper userProfileMapper, TeaProductMapper productMapper) {
        this.commentMapper = commentMapper;
        this.userMapper = userMapper;
        this.userProfileMapper = userProfileMapper;
        this.productMapper = productMapper;
    }

    @Override
    public void addComment(CommentDTO dto, Long userId) {
        Comment comment = new Comment();
        comment.setUserId(userId);
        comment.setProductId(dto.getProductId());
        comment.setContent(dto.getContent());
        comment.setRating(dto.getRating());
        comment.setOrderId(dto.getOrderId());
        comment.setStatus(1);
        commentMapper.insert(comment);
    }

    @Override
    public Page<CommentVO> getProductComments(Long productId, Integer pageNum, Integer pageSize) {
        LambdaQueryWrapper<Comment> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Comment::getProductId, productId)
                .eq(Comment::getStatus, 1)
                .orderByDesc(Comment::getCreateTime);

        Page<Comment> page = new Page<>(pageNum, pageSize);
        Page<Comment> result = commentMapper.selectPage(page, wrapper);

        if (result.getRecords().isEmpty()) {
            Page<CommentVO> emptyPage = new Page<>();
            emptyPage.setRecords(List.of());
            emptyPage.setTotal(result.getTotal());
            emptyPage.setCurrent(result.getCurrent());
            emptyPage.setSize(result.getSize());
            emptyPage.setPages(result.getPages());
            return emptyPage;
        }

        List<Long> userIds = result.getRecords().stream()
                .map(Comment::getUserId).distinct().collect(Collectors.toList());

        Map<Long, User> userMap = userMapper.selectBatchIds(userIds).stream()
                .collect(Collectors.toMap(User::getId, u -> u));
        Map<Long, UserProfile> profileMap = userProfileMapper.selectList(
                new LambdaQueryWrapper<UserProfile>().in(UserProfile::getUserId, userIds))
                .stream().collect(Collectors.toMap(UserProfile::getUserId, p -> p));

        List<CommentVO> records = result.getRecords().stream().map(c -> {
            CommentVO vo = new CommentVO();
            vo.setId(c.getId());
            vo.setUserId(c.getUserId());
            vo.setProductId(c.getProductId());
            vo.setOrderId(c.getOrderId());
            vo.setContent(c.getContent());
            vo.setRating(c.getRating());
            vo.setCreateTime(c.getCreateTime());

            User user = userMap.get(c.getUserId());
            if (user != null) vo.setUsername(user.getUsername());

            UserProfile profile = profileMap.get(c.getUserId());
            if (profile != null) vo.setUserAvatar(profile.getAvatar());

            return vo;
        }).collect(Collectors.toList());

        Page<CommentVO> voPage = new Page<>();
        voPage.setRecords(records);
        voPage.setTotal(result.getTotal());
        voPage.setCurrent(result.getCurrent());
        voPage.setSize(result.getSize());
        return voPage;
    }

    @Override
    public Page<CommentVO> getAllComments(Integer pageNum, Integer pageSize) {
        LambdaQueryWrapper<Comment> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByDesc(Comment::getCreateTime);

        Page<Comment> page = new Page<>(pageNum, pageSize);
        Page<Comment> result = commentMapper.selectPage(page, wrapper);

        if (result.getRecords().isEmpty()) {
            Page<CommentVO> emptyPage = new Page<>();
            emptyPage.setRecords(List.of());
            emptyPage.setTotal(result.getTotal());
            emptyPage.setCurrent(result.getCurrent());
            emptyPage.setSize(result.getSize());
            emptyPage.setPages(result.getPages());
            return emptyPage;
        }

        List<Long> userIds = result.getRecords().stream()
                .map(Comment::getUserId).distinct().collect(Collectors.toList());
        List<Long> productIds = result.getRecords().stream()
                .map(Comment::getProductId).distinct().collect(Collectors.toList());

        Map<Long, User> userMap = userMapper.selectBatchIds(userIds).stream()
                .collect(Collectors.toMap(User::getId, Function.identity()));
        Map<Long, UserProfile> profileMap = userProfileMapper.selectList(
                new LambdaQueryWrapper<UserProfile>().in(UserProfile::getUserId, userIds))
                .stream().collect(Collectors.toMap(UserProfile::getUserId, Function.identity()));
        Map<Long, TeaProduct> productMap = productMapper.selectBatchIds(productIds).stream()
                .collect(Collectors.toMap(TeaProduct::getId, Function.identity()));

        List<CommentVO> records = result.getRecords().stream().map(c -> {
            CommentVO vo = new CommentVO();
            vo.setId(c.getId());
            vo.setUserId(c.getUserId());
            vo.setProductId(c.getProductId());
            vo.setOrderId(c.getOrderId());
            vo.setContent(c.getContent());
            vo.setRating(c.getRating());
            vo.setStatus(c.getStatus());
            vo.setCreateTime(c.getCreateTime());

            User user = userMap.get(c.getUserId());
            if (user != null) vo.setUsername(user.getUsername());

            UserProfile profile = profileMap.get(c.getUserId());
            if (profile != null) vo.setUserAvatar(profile.getAvatar());

            TeaProduct product = productMap.get(c.getProductId());
            if (product != null) vo.setProductName(product.getName());

            return vo;
        }).collect(Collectors.toList());

        Page<CommentVO> voPage = new Page<>();
        voPage.setRecords(records);
        voPage.setTotal(result.getTotal());
        voPage.setCurrent(result.getCurrent());
        voPage.setSize(result.getSize());
        return voPage;
    }

    @Override
    public void deleteComment(Long commentId, Long userId) {
        Comment comment = commentMapper.selectById(commentId);
        if (comment == null) {
            throw new BusinessException(404, "评论不存在");
        }
        if (userId != null && !comment.getUserId().equals(userId)) {
            throw new BusinessException(403, "只能删除自己的评论");
        }
        commentMapper.deleteById(commentId);
    }
}
