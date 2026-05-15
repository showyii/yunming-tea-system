package com.yunming.tea.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yunming.tea.entity.Comment;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface CommentMapper extends BaseMapper<Comment> {
}
