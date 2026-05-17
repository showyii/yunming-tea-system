package com.yunming.tea.service.impl; // 声明包路径，属于服务实现层包

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper; // 导入MyBatis-Plus的Lambda查询包装器
import com.yunming.tea.dto.ChangePasswordDTO; // 导入修改密码数据传输对象
import com.yunming.tea.dto.LoginDTO; // 导入登录数据传输对象
import com.yunming.tea.dto.UpdateProfileDTO; // 导入更新资料数据传输对象
import com.yunming.tea.dto.UserRegisterDTO; // 导入用户注册数据传输对象
import com.yunming.tea.entity.User; // 导入用户实体类，对应user表
import com.yunming.tea.entity.UserProfile; // 导入用户资料实体类，对应user_profile表
import com.yunming.tea.exception.BusinessException; // 导入业务异常类
import com.yunming.tea.mapper.UserMapper; // 导入用户数据访问层
import com.yunming.tea.mapper.UserProfileMapper; // 导入用户资料数据访问层
import com.yunming.tea.security.JwtUtils; // 导入JWT工具类，用于生成和验证JWT令牌
import com.yunming.tea.service.UserService; // 导入用户服务接口
import com.yunming.tea.vo.LoginVO; // 导入登录视图对象
import com.yunming.tea.vo.UserProfileVO; // 导入用户资料视图对象
import org.springframework.stereotype.Service; // 导入Spring的Service注解
import org.springframework.transaction.annotation.Transactional; // 导入Spring事务注解

/**
 * 用户服务实现类
 * <p>
 * 实现了{@link UserService}接口中定义的用户相关核心业务逻辑。
 * 主要功能包括：
 * <ul>
 *   <li>用户注册：同时创建User账号记录和UserProfile资料记录，确保原子性</li>
 *   <li>用户登录：验证用户名密码，检查账号状态，生成JWT认证令牌</li>
 *   <li>获取用户信息：返回完整的用户账号和资料信息</li>
 *   <li>更新用户资料：支持部分字段更新（只更新非null字段）</li>
 *   <li>修改密码：需要验证旧密码后才能更新</li>
 * </ul>
 * <p>
 * 安全说明：
 * <ul>
 *   <li>密码目前使用明文存储和比较（仅用于演示项目）</li>
 *   <li>实际生产环境应使用BCrypt等加密算法进行密码哈希存储和验证</li>
 *   <li>登录成功后生成的JWT令牌包含用户ID、用户名和角色信息</li>
 * </ul>
 *
 * @author yunming
 * @see UserService
 */
@Service // 将该类标记为Spring的Service组件
public class UserServiceImpl implements UserService {

    private final UserMapper userMapper; // 用户数据访问对象，操作user表
    private final UserProfileMapper userProfileMapper; // 用户资料数据访问对象，操作user_profile表
    private final JwtUtils jwtUtils; // JWT工具类实例，用于生成JWT认证令牌

    /**
     * 构造函数注入依赖
     *
     * @param userMapper 用户数据访问对象
     * @param userProfileMapper 用户资料数据访问对象
     * @param jwtUtils JWT工具类实例
     */
    public UserServiceImpl(UserMapper userMapper, UserProfileMapper userProfileMapper, JwtUtils jwtUtils) {
        this.userMapper = userMapper; // 注入用户Mapper
        this.userProfileMapper = userProfileMapper; // 注入用户资料Mapper
        this.jwtUtils = jwtUtils; // 注入JWT工具类
    }

    /**
     * 用户注册
     * <p>
     * 新用户注册，同时创建用户账号和用户资料记录。
     * 操作流程：
     * <ol>
     *   <li>检查用户名是否已被占用</li>
     *   <li>创建User记录：设置用户名、密码、手机号、邮箱，角色默认为普通用户(role=0)，状态默认正常(status=1)</li>
     *   <li>创建UserProfile记录：用户ID关联刚创建的User，默认昵称使用用户名</li>
     * </ol>
     * 整个操作在事务中执行，确保账号和资料记录同时创建或同时回滚。
     *
     * @param dto 用户注册数据传输对象
     * @throws BusinessException 用户名已存在时抛出400异常
     */
    @Override // 标识该方法覆盖了父接口中的方法
    @Transactional // 声明事务：确保User和UserProfile的创建操作具有原子性
    public void register(UserRegisterDTO dto) {
        // 第1步：检查用户名是否已存在
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>(); // 创建查询包装器
        wrapper.eq(User::getUsername, dto.getUsername()); // 条件：用户名精确匹配
        if (userMapper.selectCount(wrapper) > 0) { // 如果存在同名用户
            throw new BusinessException(400, "用户名已存在"); // 抛出400异常
        }

        // 第2步：创建用户账号记录
        User user = new User(); // 实例化用户实体
        user.setUsername(dto.getUsername()); // 设置用户名
        user.setPassword(dto.getPassword()); // 设置密码（注意：当前为明文存储，生产环境应使用BCrypt加密）
        user.setPhone(dto.getPhone()); // 设置手机号
        user.setEmail(dto.getEmail()); // 设置邮箱
        user.setRole(0); // 设置角色为普通用户（0表示普通用户，区别于管理员角色）
        user.setStatus(1); // 设置账号状态为正常（1表示正常，0表示禁用）
        userMapper.insert(user); // 将用户记录插入数据库，MyBatis-Plus会自动填充create_time和id

        // 第3步：创建用户资料记录（与用户账号一对一关联）
        UserProfile profile = new UserProfile(); // 实例化用户资料实体
        profile.setUserId(user.getId()); // 设置关联的用户ID（使用刚插入后获得的ID）
        profile.setNickname(dto.getUsername()); // 默认昵称使用用户名
        userProfileMapper.insert(profile); // 将用户资料记录插入数据库
    }

    /**
     * 用户登录
     * <p>
     * 登录认证流程：
     * <ol>
     *   <li>根据用户名查询用户账号</li>
     *   <li>校验密码是否正确（当前为明文比较）</li>
     *   <li>检查账号是否被禁用（status=0）</li>
     *   <li>认证通过后生成JWT令牌</li>
     * </ol>
     *
     * @param dto 登录数据传输对象
     * @return 登录视图对象，包含JWT令牌token、用户ID和用户名
     * @throws BusinessException 用户名或密码错误(400)、账号被禁用(403)
     */
    @Override // 标识该方法覆盖了父接口中的方法
    public LoginVO login(LoginDTO dto) {
        // 第1步：根据用户名查询用户
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>(); // 创建查询包装器
        wrapper.eq(User::getUsername, dto.getUsername()); // 条件：用户名精确匹配
        User user = userMapper.selectOne(wrapper); // 查询唯一匹配的用户

        // 第2步：校验用户是否存在
        if (user == null) { // 用户不存在
            throw new BusinessException(400, "用户名或密码错误"); // 统一错误提示，不明确暴露用户名是否存在
        }

        // 第3步：校验密码是否正确
        if (!user.getPassword().equals(dto.getPassword())) { // 密码不匹配（当前为明文比较）
            throw new BusinessException(400, "用户名或密码错误"); // 统一错误提示
        }

        // 第4步：检查账号是否被禁用
        if (user.getStatus() == 0) { // 账号状态为0（禁用）
            throw new BusinessException(403, "账号已被禁用"); // 抛出403禁止访问异常
        }

        // 第5步：认证通过，生成JWT令牌
        String token = jwtUtils.generateToken(user.getId(), user.getUsername(), user.getRole()); // 生成用户JWT令牌

        // 第6步：构建并返回登录视图对象
        return new LoginVO(token, user.getId(), user.getUsername()); // 返回包含令牌和用户信息的LoginVO
    }

    /**
     * 获取当前用户信息
     * <p>
     * 根据用户ID查询完整的用户信息，同时返回User表和UserProfile表的数据。
     * 如果用户资料记录不存在，资料的字段将保持为null。
     *
     * @param userId 用户ID
     * @return 用户资料视图对象，包含账号和资料的完整信息
     * @throws BusinessException 用户不存在时抛出404异常
     */
    @Override // 标识该方法覆盖了父接口中的方法
    public UserProfileVO getUserProfile(Long userId) {
        // 第1步：查询用户账号信息
        User user = userMapper.selectById(userId); // 根据ID查询用户
        if (user == null) { // 用户不存在
            throw new BusinessException(404, "用户不存在"); // 抛出404异常
        }

        // 第2步：查询用户详细资料
        LambdaQueryWrapper<UserProfile> wrapper = new LambdaQueryWrapper<>(); // 创建查询包装器
        wrapper.eq(UserProfile::getUserId, userId); // 条件：用户ID匹配
        UserProfile profile = userProfileMapper.selectOne(wrapper); // 查询唯一匹配的用户资料

        // 第3步：构建用户资料视图对象
        UserProfileVO vo = new UserProfileVO(); // 创建视图对象
        // 从User表获取账号基本信息
        vo.setId(user.getId()); // 设置用户ID
        vo.setUsername(user.getUsername()); // 设置用户名
        vo.setPhone(user.getPhone()); // 设置手机号
        vo.setEmail(user.getEmail()); // 设置邮箱
        vo.setRole(user.getRole()); // 设置用户角色（0:普通用户, 1:管理员）
        vo.setCreateTime(user.getCreateTime()); // 设置注册时间

        // 从UserProfile表获取详细资料信息
        if (profile != null) { // 如果用户资料存在
            vo.setNickname(profile.getNickname()); // 设置昵称
            vo.setAvatar(profile.getAvatar()); // 设置头像URL
            vo.setGender(profile.getGender()); // 设置性别（0:未知, 1:男, 2:女）
            vo.setBirthday(profile.getBirthday()); // 设置生日
            vo.setAddress(profile.getAddress()); // 设置地址
            vo.setSignature(profile.getSignature()); // 设置个性签名
        }

        return vo; // 返回用户资料视图对象
    }

    /**
     * 更新用户资料
     * <p>
     * 修改当前用户的详细资料信息。支持部分更新：
     * 只更新传入的非null字段，null字段保持原值不变。
     * 如果用户资料记录尚不存在（profile.getId()为null），则创建新记录；
     * 否则更新已有记录。
     * 操作在事务中执行。
     *
     * @param userId 用户ID
     * @param dto 更新资料数据传输对象，所有字段都是可选的（null表示不修改）
     */
    @Override // 标识该方法覆盖了父接口中的方法
    @Transactional // 声明事务
    public void updateProfile(Long userId, UpdateProfileDTO dto) {
        // 第1步：查询用户现有的资料记录
        LambdaQueryWrapper<UserProfile> wrapper = new LambdaQueryWrapper<>(); // 创建查询包装器
        wrapper.eq(UserProfile::getUserId, userId); // 条件：用户ID匹配
        UserProfile profile = userProfileMapper.selectOne(wrapper); // 查询资料记录

        // 第2步：如果资料不存在则创建新的空资料对象
        if (profile == null) { // 资料不存在
            profile = new UserProfile(); // 创建新的用户资料实体
            profile.setUserId(userId); // 设置关联的用户ID
        }

        // 第3步：只更新传入的非null字段（部分更新策略）
        if (dto.getNickname() != null) profile.setNickname(dto.getNickname()); // 更新昵称
        if (dto.getAvatar() != null) profile.setAvatar(dto.getAvatar()); // 更新头像URL
        if (dto.getGender() != null) profile.setGender(dto.getGender()); // 更新性别
        if (dto.getBirthday() != null) profile.setBirthday(dto.getBirthday()); // 更新生日
        if (dto.getAddress() != null) profile.setAddress(dto.getAddress()); // 更新地址
        if (dto.getSignature() != null) profile.setSignature(dto.getSignature()); // 更新个性签名

        // 第4步：判断是插入还是更新
        if (profile.getId() == null) { // 如果资料ID为null，说明是新记录
            userProfileMapper.insert(profile); // 插入新的资料记录
        } else { // 资料ID不为null，说明是已有记录
            userProfileMapper.updateById(profile); // 根据ID更新资料记录
        }
    }

    /**
     * 修改密码
     * <p>
     * 修改当前用户的登录密码，操作流程：
     * <ol>
     *   <li>校验用户是否存在</li>
     *   <li>校验旧密码是否正确（防止未授权修改）</li>
     *   <li>更新为新密码</li>
     * </ol>
     * 注意：当前使用明文密码存储，实际生产项目应使用BCrypt加密。
     *
     * @param userId 用户ID
     * @param dto 修改密码数据传输对象，包含oldPassword和newPassword
     * @throws BusinessException 用户不存在(404)、旧密码错误(400)
     */
    @Override // 标识该方法覆盖了父接口中的方法
    public void changePassword(Long userId, ChangePasswordDTO dto) {
        // 第1步：查询用户
        User user = userMapper.selectById(userId); // 根据ID查询用户
        if (user == null) { // 用户不存在
            throw new BusinessException(404, "用户不存在"); // 抛出404异常
        }

        // 第2步：校验旧密码是否正确
        if (!user.getPassword().equals(dto.getOldPassword())) { // 旧密码不匹配
            throw new BusinessException(400, "原密码错误"); // 提示原密码错误
        }

        // 第3步：设置新密码并更新
        user.setPassword(dto.getNewPassword()); // 设置新密码
        userMapper.updateById(user); // 根据ID更新用户记录
    }
}
