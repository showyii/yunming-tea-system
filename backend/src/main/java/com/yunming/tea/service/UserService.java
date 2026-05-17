package com.yunming.tea.service; // 声明包路径，属于服务层接口包

import com.yunming.tea.dto.ChangePasswordDTO; // 导入修改密码数据传输对象，包含旧密码和新密码
import com.yunming.tea.dto.LoginDTO; // 导入登录数据传输对象，包含用户名和密码
import com.yunming.tea.dto.UpdateProfileDTO; // 导入更新资料数据传输对象，包含昵称、头像等个人信息
import com.yunming.tea.dto.UserRegisterDTO; // 导入用户注册数据传输对象，包含用户名、密码、手机号、邮箱
import com.yunming.tea.vo.LoginVO; // 导入登录视图对象，包含JWT令牌和用户基本信息
import com.yunming.tea.vo.UserProfileVO; // 导入用户资料视图对象，用于返回用户完整信息

/**
 * 用户服务接口
 * <p>
 * 该接口定义了用户相关的核心业务操作规范，包括：
 * 1. 用户注册（创建账号和用户资料）
 * 2. 用户登录（账号密码认证并生成JWT令牌）
 * 3. 获取用户信息（返回用户基本信息和详细资料）
 * 4. 更新用户资料（修改昵称、头像、性别、生日等信息）
 * 5. 修改密码（需要验证旧密码）
 * <p>
 * 用户服务是系统的基础服务，所有需要登录态的功能都依赖于用户认证。
 * 注册时会同时创建用户账号记录（User表）和用户资料记录（UserProfile表）。
 * 登录成功后返回的JWT令牌用于后续请求的身份认证。
 *
 * @author yunming
 * @see com.yunming.tea.service.impl.UserServiceImpl
 */
public interface UserService {

    /**
     * 用户注册
     * <p>
     * 新用户注册流程：
     * 1. 检查用户名是否已被占用
     * 2. 创建用户账号记录（User表），设置角色为普通用户(role=0)和状态为正常(status=1)
     * 3. 创建用户资料记录（UserProfile表），默认昵称使用用户名
     * <p>
     * 整个注册操作在一个事务中执行，确保用户和资料同时创建成功。
     *
     * @param dto 用户注册数据传输对象，包含username（用户名）、password（密码）、phone（手机号）、email（邮箱）
     * @throws com.yunming.tea.exception.BusinessException 用户名已存在时抛出400异常
     */
    void register(UserRegisterDTO dto); // 用户注册方法声明

    /**
     * 用户登录
     * <p>
     * 用户身份认证流程：
     * 1. 根据用户名查询用户账号
     * 2. 校验密码是否正确
     * 3. 检查账号是否被禁用
     * <p>
     * 认证成功后生成JWT令牌（包含用户ID、用户名和角色信息），
     * 前端需要保存该令牌并在后续请求的Header中携带。
     *
     * @param dto 登录数据传输对象，包含username（用户名）和password（密码）
     * @return 登录视图对象，包含JWT令牌token、用户ID和用户名
     * @throws com.yunming.tea.exception.BusinessException 用户名或密码错误时抛出400异常，账号被禁用时抛出403异常
     */
    LoginVO login(LoginDTO dto); // 用户登录方法声明

    /**
     * 获取当前用户信息
     * <p>
     * 根据用户ID查询用户的完整信息，包括：
     * - 账号基础信息（用户名、手机号、邮箱、角色、注册时间）
     * - 用户详细资料（昵称、头像、性别、生日、地址、个性签名）
     * <p>
     * 用于个人中心页面的信息展示。
     *
     * @param userId 用户ID
     * @return 用户资料视图对象，包含用户的完整信息
     * @throws com.yunming.tea.exception.BusinessException 用户不存在时抛出404异常
     */
    UserProfileVO getUserProfile(Long userId); // 获取用户信息方法声明

    /**
     * 更新用户资料
     * <p>
     * 修改当前用户的详细资料信息，包括昵称、头像、性别、生日、地址、个性签名。
     * 如果用户资料记录尚不存在则创建新记录，存在则更新现有记录。
     * 只更新传入的非null字段，null字段保持原值不变。
     *
     * @param userId 用户ID
     * @param dto 更新资料数据传输对象，所有字段均为可选（null表示不修改该字段）
     */
    void updateProfile(Long userId, UpdateProfileDTO dto); // 更新用户资料方法声明

    /**
     * 修改密码
     * <p>
     * 修改当前用户的登录密码，操作流程：
     * 1. 校验用户是否存在
     * 2. 校验旧密码是否正确
     * 3. 更新为新密码
     * <p>
     * 注意：本系统使用明文密码存储（仅用于演示），
     * 实际生产项目中应使用BCrypt等加密算法对密码进行哈希存储。
     *
     * @param userId 用户ID
     * @param dto 修改密码数据传输对象，包含oldPassword（旧密码）和newPassword（新密码）
     * @throws com.yunming.tea.exception.BusinessException 用户不存在时抛出404异常，旧密码错误时抛出400异常
     */
    void changePassword(Long userId, ChangePasswordDTO dto); // 修改密码方法声明
}
