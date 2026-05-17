// 声明包路径
package com.yunming.tea.mapper;

// MyBatis-Plus BaseMapper
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
// Comment 实体类（评论）
import com.yunming.tea.entity.Comment;
// MyBatis Mapper 注解
import org.apache.ibatis.annotations.Mapper;

/**
 * 评论表 Mapper 接口
 * 继承 BaseMapper<Comment>，提供对 comment 表的 CRUD 操作
 */
@Mapper // MyBatis Mapper
public interface CommentMapper extends BaseMapper<Comment> {
    // BaseMapper 提供基础增删改查
}
