package com.yunming.tea.service.impl; // 声明包路径，属于服务实现层包

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper; // 导入MyBatis-Plus的Lambda查询包装器
import com.baomidou.mybatisplus.extension.plugins.pagination.Page; // 导入MyBatis-Plus分页对象
import com.yunming.tea.dto.CommentDTO; // 导入评论数据传输对象
import com.yunming.tea.entity.Comment; // 导入评论实体类，对应comment表
import com.yunming.tea.entity.TeaProduct; // 导入茶品商品实体类
import com.yunming.tea.entity.User; // 导入用户实体类
import com.yunming.tea.entity.UserProfile; // 导入用户资料实体类
import com.yunming.tea.exception.BusinessException; // 导入业务异常类
import com.yunming.tea.mapper.CommentMapper; // 导入评论数据访问层
import com.yunming.tea.mapper.TeaProductMapper; // 导入商品数据访问层
import com.yunming.tea.mapper.UserMapper; // 导入用户数据访问层
import com.yunming.tea.mapper.UserProfileMapper; // 导入用户资料数据访问层
import com.yunming.tea.service.CommentService; // 导入评论服务接口
import com.yunming.tea.vo.CommentVO; // 导入评论视图对象
import org.springframework.stereotype.Service; // 导入Spring的Service注解

import java.util.List; // 导入List接口
import java.util.Map; // 导入Map接口
import java.util.function.Function; // 导入Function函数式接口，用于collectors.toMap
import java.util.stream.Collectors; // 导入Collectors工具类，用于流式操作收集

/**
 * 商品评论服务实现类
 * <p>
 * 实现了{@link CommentService}接口中定义的商品评论相关业务逻辑。
 * 主要功能包括：
 * <ul>
 *   <li>添加商品评论：用户对已购买商品发表评价和评分</li>
 *   <li>查询商品评论列表（分页）：前端展示商品的所有已发布评论，附带用户头像和昵称</li>
 *   <li>查询所有评论（管理员，分页）：后台管理系统查看全部评论，附带商品名称</li>
 *   <li>删除评论：用户删除自己的评论或管理员删除任意评论</li>
 * </ul>
 * <p>
 * 评论数据关联用户信息（用户名、头像）和商品信息（商品名称），
 * 通过批量查询优化性能（使用selectBatchIds一次性获取多个用户/商品信息），
 * 转换为Map后通过userId/productId快速查找对应信息，
 * 避免在循环中逐条查询数据库（N+1问题）。
 *
 * @author yunming
 * @see CommentService
 */
@Service // 将该类标记为Spring的Service组件
public class CommentServiceImpl implements CommentService {

    private final CommentMapper commentMapper; // 评论数据访问对象
    private final UserMapper userMapper; // 用户数据访问对象，用于查询用户基本信息
    private final UserProfileMapper userProfileMapper; // 用户资料数据访问对象，用于查询头像等信息
    private final TeaProductMapper productMapper; // 商品数据访问对象，用于查询商品名称

    /**
     * 构造函数注入依赖
     *
     * @param commentMapper 评论数据访问对象
     * @param userMapper 用户数据访问对象
     * @param userProfileMapper 用户资料数据访问对象
     * @param productMapper 商品数据访问对象
     */
    public CommentServiceImpl(CommentMapper commentMapper, UserMapper userMapper,
                               UserProfileMapper userProfileMapper, TeaProductMapper productMapper) {
        this.commentMapper = commentMapper; // 注入评论Mapper
        this.userMapper = userMapper; // 注入用户Mapper
        this.userProfileMapper = userProfileMapper; // 注入用户资料Mapper
        this.productMapper = productMapper; // 注入商品Mapper
    }

    /**
     * 添加商品评论
     * <p>
     * 用户对指定商品发表评论，包括文字内容和星级评分。
     * 评论关联订单ID（orderId），用于追溯评论来源。
     * 新评论默认状态为"已发布"（status=1）。
     *
     * @param dto 评论数据传输对象，包含productId（商品ID）、content（评论内容）、rating（评分）、orderId（订单ID）
     * @param userId 发表评论的用户ID
     */
    @Override // 标识该方法覆盖了父接口中的方法
    public void addComment(CommentDTO dto, Long userId) {
        // 创建评论实体对象并设置属性
        Comment comment = new Comment(); // 实例化评论实体
        comment.setUserId(userId); // 设置评论用户ID
        comment.setProductId(dto.getProductId()); // 设置被评论的商品ID
        comment.setContent(dto.getContent()); // 设置评论的文字内容
        comment.setRating(dto.getRating()); // 设置星级评分
        comment.setOrderId(dto.getOrderId()); // 设置关联的订单ID
        comment.setStatus(1); // 设置评论状态为"已发布"（1表示正常显示）
        commentMapper.insert(comment); // 将评论记录插入数据库
    }

    /**
     * 查询某个商品的评论列表（分页）
     * <p>
     * 根据商品ID查询该商品的所有已发布评论，按创建时间倒序排列。
     * 批量查询评论者用户信息和资料，填充到视图对象中。
     * 只返回状态为"已发布"（status=1）的评论。
     *
     * @param productId 商品ID
     * @param pageNum 当前页码
     * @param pageSize 每页大小
     * @return 分页的评论视图对象，包含评论内容、用户名称和头像
     */
    @Override // 标识该方法覆盖了父接口中的方法
    public Page<CommentVO> getProductComments(Long productId, Integer pageNum, Integer pageSize) {
        // 第1步：构建查询条件，只查询指定商品且状态为"已发布"的评论
        LambdaQueryWrapper<Comment> wrapper = new LambdaQueryWrapper<>(); // 创建查询包装器
        wrapper.eq(Comment::getProductId, productId) // 条件：商品ID匹配
                .eq(Comment::getStatus, 1) // 条件：状态为"已发布"(1)
                .orderByDesc(Comment::getCreateTime); // 排序：按创建时间倒序（最新评论在前）

        // 第2步：执行分页查询
        Page<Comment> page = new Page<>(pageNum, pageSize); // 创建分页对象，设置当前页和每页大小
        Page<Comment> result = commentMapper.selectPage(page, wrapper); // 执行分页查询

        // 第3步：如果查询结果为空，直接返回空的分页对象
        if (result.getRecords().isEmpty()) { // 判断记录列表是否为空
            Page<CommentVO> emptyPage = new Page<>(); // 创建空的分页视图对象
            emptyPage.setRecords(List.of()); // 设置空记录列表
            emptyPage.setTotal(result.getTotal()); // 保持总记录数
            emptyPage.setCurrent(result.getCurrent()); // 保持当前页码
            emptyPage.setSize(result.getSize()); // 保持每页大小
            emptyPage.setPages(result.getPages()); // 保持总页数
            return emptyPage; // 返回空分页对象
        }

        // 第4步：收集所有评论者的用户ID列表（去重）
        List<Long> userIds = result.getRecords().stream() // 获取评论记录流
                .map(Comment::getUserId) // 提取每条评论的用户ID
                .distinct() // 对用户ID去重
                .collect(Collectors.toList()); // 收集为List

        // 第5步：批量查询用户信息，转换为Map便于快速查找
        Map<Long, User> userMap = userMapper.selectBatchIds(userIds).stream() // 批量按ID查询用户
                .collect(Collectors.toMap(User::getId, u -> u)); // 转换为Map，key=用户ID，value=用户对象
        // 批量查询用户资料（头像等信息），转换为Map
        Map<Long, UserProfile> profileMap = userProfileMapper.selectList( // 使用selectList + in条件查询
                new LambdaQueryWrapper<UserProfile>() // 创建查询包装器
                        .in(UserProfile::getUserId, userIds)) // 条件：userId在指定列表中
                .stream() // 获取流
                .collect(Collectors.toMap(UserProfile::getUserId, p -> p)); // 转换为Map，key=userId，value=资料对象

        // 第6步：遍历评论列表，构建视图对象
        List<CommentVO> records = result.getRecords().stream().map(c -> { // 遍历每条评论并映射为CommentVO
            CommentVO vo = new CommentVO(); // 创建评论视图对象
            vo.setId(c.getId()); // 设置评论ID
            vo.setUserId(c.getUserId()); // 设置评论用户ID
            vo.setProductId(c.getProductId()); // 设置被评论的商品ID
            vo.setOrderId(c.getOrderId()); // 设置关联的订单ID
            vo.setContent(c.getContent()); // 设置评论文字内容
            vo.setRating(c.getRating()); // 设置星级评分
            vo.setCreateTime(c.getCreateTime()); // 设置评论发布时间

            // 从用户Map中获取用户名
            User user = userMap.get(c.getUserId()); // 根据用户ID从Map中查找用户
            if (user != null) vo.setUsername(user.getUsername()); // 如果用户存在，设置用户名

            // 从用户资料Map中获取头像
            UserProfile profile = profileMap.get(c.getUserId()); // 根据用户ID从Map中查找资料
            if (profile != null) vo.setUserAvatar(profile.getAvatar()); // 如果资料存在，设置头像

            return vo; // 返回构建好的视图对象
        }).collect(Collectors.toList()); // 收集为List

        // 第7步：构建分页视图对象
        Page<CommentVO> voPage = new Page<>(); // 创建分页视图对象
        voPage.setRecords(records); // 设置评论视图记录列表
        voPage.setTotal(result.getTotal()); // 保持总记录数
        voPage.setCurrent(result.getCurrent()); // 保持当前页码
        voPage.setSize(result.getSize()); // 保持每页大小
        return voPage; // 返回分页视图对象
    }

    /**
     * 查询所有评论（管理员查看，分页）
     * <p>
     * 管理员查看系统中所有商品的全部评论，按创建时间倒序排列。
     * 相比getProductComments方法，额外查询了商品名称信息，不限制评论状态。
     * 用于后台评论管理功能，可以查看和删除不当评论。
     *
     * @param pageNum 当前页码
     * @param pageSize 每页大小
     * @return 分页的评论视图对象，包含评论内容、用户信息和商品名称
     */
    @Override // 标识该方法覆盖了父接口中的方法
    public Page<CommentVO> getAllComments(Integer pageNum, Integer pageSize) {
        // 第1步：构建查询条件，不限制商品和状态，按时间倒序
        LambdaQueryWrapper<Comment> wrapper = new LambdaQueryWrapper<>(); // 创建查询包装器
        wrapper.orderByDesc(Comment::getCreateTime); // 排序：按创建时间倒序（最新评论在前）

        // 第2步：执行分页查询
        Page<Comment> page = new Page<>(pageNum, pageSize); // 创建分页对象
        Page<Comment> result = commentMapper.selectPage(page, wrapper); // 执行分页查询

        // 第3步：处理空结果
        if (result.getRecords().isEmpty()) { // 如果记录为空
            Page<CommentVO> emptyPage = new Page<>(); // 创建空分页对象
            emptyPage.setRecords(List.of()); // 设置空列表
            emptyPage.setTotal(result.getTotal()); // 保持总记录数
            emptyPage.setCurrent(result.getCurrent()); // 保持当前页码
            emptyPage.setSize(result.getSize()); // 保持每页大小
            emptyPage.setPages(result.getPages()); // 保持总页数
            return emptyPage; // 返回空分页对象
        }

        // 第4步：收集所有评论者的用户ID（去重）
        List<Long> userIds = result.getRecords().stream() // 获取评论记录流
                .map(Comment::getUserId) // 提取用户ID
                .distinct() // 去重
                .collect(Collectors.toList()); // 收集为List
        // 收集所有被评论的商品ID（去重）
        List<Long> productIds = result.getRecords().stream() // 获取评论记录流
                .map(Comment::getProductId) // 提取商品ID
                .distinct() // 去重
                .collect(Collectors.toList()); // 收集为List

        // 第5步：批量查询用户信息、用户资料和商品信息，转换为Map
        Map<Long, User> userMap = userMapper.selectBatchIds(userIds).stream() // 批量查询用户
                .collect(Collectors.toMap(User::getId, Function.identity())); // 转换为userId->User的Map
        Map<Long, UserProfile> profileMap = userProfileMapper.selectList( // 查询用户资料
                new LambdaQueryWrapper<UserProfile>() // 创建查询包装器
                        .in(UserProfile::getUserId, userIds)) // 条件：userId在集合中
                .stream() // 获取流
                .collect(Collectors.toMap(UserProfile::getUserId, Function.identity())); // 转换为userId->Profile的Map
        Map<Long, TeaProduct> productMap = productMapper.selectBatchIds(productIds).stream() // 批量查询商品
                .collect(Collectors.toMap(TeaProduct::getId, Function.identity())); // 转换为productId->Product的Map

        // 第6步：遍历评论列表，构建包含商品名称的视图对象
        List<CommentVO> records = result.getRecords().stream().map(c -> { // 遍历并映射
            CommentVO vo = new CommentVO(); // 创建视图对象
            vo.setId(c.getId()); // 设置评论ID
            vo.setUserId(c.getUserId()); // 设置用户ID
            vo.setProductId(c.getProductId()); // 设置商品ID
            vo.setOrderId(c.getOrderId()); // 设置订单ID
            vo.setContent(c.getContent()); // 设置评论内容
            vo.setRating(c.getRating()); // 设置评分
            vo.setStatus(c.getStatus()); // 设置评论状态（管理员可以看到所有状态）
            vo.setCreateTime(c.getCreateTime()); // 设置创建时间

            // 填充用户名
            User user = userMap.get(c.getUserId()); // 从Map查找用户
            if (user != null) vo.setUsername(user.getUsername()); // 设置用户名

            // 填充用户头像
            UserProfile profile = profileMap.get(c.getUserId()); // 从Map查找用户资料
            if (profile != null) vo.setUserAvatar(profile.getAvatar()); // 设置头像

            // 填充商品名称（管理员视图特有，方便识别评论来自哪个商品）
            TeaProduct product = productMap.get(c.getProductId()); // 从Map查找商品
            if (product != null) vo.setProductName(product.getName()); // 设置商品名称

            return vo; // 返回视图对象
        }).collect(Collectors.toList()); // 收集为List

        // 第7步：构建分页视图对象
        Page<CommentVO> voPage = new Page<>(); // 创建分页视图对象
        voPage.setRecords(records); // 设置记录列表
        voPage.setTotal(result.getTotal()); // 设置总记录数
        voPage.setCurrent(result.getCurrent()); // 设置当前页码
        voPage.setSize(result.getSize()); // 设置每页大小
        return voPage; // 返回分页视图对象
    }

    /**
     * 删除评论
     * <p>
     * 删除操作权限规则：
     * <ul>
     *   <li>普通用户：只能删除自己发表的评论（userId != null时需要校验）</li>
     *   <li>管理员：可以删除任意评论（userId为null时跳过所有权校验）</li>
     * </ul>
     *
     * @param commentId 要删除的评论ID
     * @param userId 操作用户ID，普通用户需校验所有权，管理员传null
     * @throws BusinessException 评论不存在时抛出404，无权删除时抛出403
     */
    @Override // 标识该方法覆盖了父接口中的方法
    public void deleteComment(Long commentId, Long userId) {
        // 根据评论ID查询评论记录
        Comment comment = commentMapper.selectById(commentId); // 查询评论
        // 校验评论是否存在
        if (comment == null) { // 如果评论不存在
            throw new BusinessException(404, "评论不存在"); // 抛出404异常
        }
        // 权限校验：如果传入了userId（普通用户操作），需要校验是否是自己的评论
        if (userId != null && !comment.getUserId().equals(userId)) { // userId不为null且不是评论者本人
            throw new BusinessException(403, "只能删除自己的评论"); // 抛出403无权限异常
        }
        commentMapper.deleteById(commentId); // 根据ID从数据库中删除评论
    }
}
