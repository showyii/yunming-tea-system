package com.yunming.tea.service; // 声明包路径，属于服务层接口包

import com.baomidou.mybatisplus.extension.plugins.pagination.Page; // 导入MyBatis-Plus分页对象，用于分页查询
import com.yunming.tea.dto.CommentDTO; // 导入评论数据传输对象，包含评论内容和评分信息
import com.yunming.tea.vo.CommentVO; // 导入评论视图对象，用于返回给前端的评论数据结构

/**
 * 商品评论服务接口
 * <p>
 * 该接口定义了商品评论相关的业务操作规范，包括：
 * 1. 用户发表商品评论
 * 2. 查询某个商品的评论列表（分页）
 * 3. 查询所有商品的评论列表（管理员查看，分页）
 * 4. 删除评论
 * <p>
 * 评论功能允许用户对购买过的商品进行评价，包括文字内容和评分（rating）。
 * 评论数据会关联用户信息和商品信息，方便前端展示评论者的用户名和头像。
 * 管理员可以查看所有评论并进行管理。
 *
 * @author yunming
 * @see com.yunming.tea.service.impl.CommentServiceImpl
 */
public interface CommentService {

    /**
     * 添加商品评论
     * <p>
     * 用户对商品发表评论，评论内容包括文字评价和星级评分。
     * 评论会关联到具体的订单（orderId），从而确保只有购买过该商品的用户才能评论。
     * 新发表的评论默认状态为"已发布"（status=1）。
     *
     * @param dto 评论数据传输对象，包含productId（商品ID）、content（评论内容）、rating（评分）、orderId（订单ID）
     * @param userId 发表评论的用户ID
     */
    void addComment(CommentDTO dto, Long userId); // 添加评论方法声明

    /**
     * 查询某个商品的评论列表（分页）
     * <p>
     * 根据商品ID查询该商品的所有已发布评论，按创建时间倒序排列。
     * 返回的评论数据包含评论者的用户名和头像信息。
     * 只返回状态为"已发布"（status=1）的评论。
     *
     * @param productId 商品ID，用于筛选评论
     * @param page 当前页码，从1开始
     * @param size 每页显示的评论数量
     * @return 分页的评论视图对象，包含评论内容和用户信息
     */
    Page<CommentVO> getProductComments(Long productId, Integer page, Integer size); // 查询商品评论方法声明

    /**
     * 查询所有评论（管理员查看，分页）
     * <p>
     * 管理员查看系统中所有商品的评论列表，不限制评论状态。
     * 按创建时间倒序排列，返回评论内容、用户信息和商品名称。
     * 适用于后台评论管理功能。
     *
     * @param page 当前页码，从1开始
     * @param size 每页显示的评论数量
     * @return 分页的评论视图对象，包含评论内容、用户信息和商品名称
     */
    Page<CommentVO> getAllComments(Integer page, Integer size); // 查询所有评论方法声明

    /**
     * 删除评论
     * <p>
     * 用户可以删除自己发表的评论，管理员可以删除任何评论。
     * 删除前会进行权限校验：
     * 1. 评论是否存在
     * 2. 如果传入了userId，则校验是否为评论者本人
     * （管理员调用时userId传null，跳过所有权校验）
     *
     * @param commentId 评论ID，用于定位要删除的评论
     * @param userId 操作用户ID，普通用户只能删除自己的评论
     */
    void deleteComment(Long commentId, Long userId); // 删除评论方法声明
}
