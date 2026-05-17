package com.yunming.tea.service.impl; // 声明包路径，属于服务实现层包

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper; // 导入MyBatis-Plus的Lambda查询包装器，用于构建类型安全的查询条件
import com.yunming.tea.dto.LoginDTO; // 导入登录数据传输对象，包含用户名和密码
import com.yunming.tea.entity.Admin; // 导入管理员实体类，对应admin表
import com.yunming.tea.exception.BusinessException; // 导入业务异常类，用于抛出业务逻辑相关的异常
import com.yunming.tea.mapper.AdminMapper; // 导入管理员数据访问层，操作管理员数据
import com.yunming.tea.security.JwtUtils; // 导入JWT工具类，用于生成JWT认证令牌
import com.yunming.tea.service.AdminService; // 导入管理员服务接口
import com.yunming.tea.vo.LoginVO; // 导入登录视图对象，包含token和用户信息
import org.springframework.stereotype.Service; // 导入Spring的Service注解

/**
 * 管理员服务实现类
 * <p>
 * 实现了{@link AdminService}接口中定义的管理员登录认证业务逻辑。
 * 主要功能包括：
 * <ul>
 *   <li>管理员账号登录：验证用户名密码，检查账号状态，生成JWT认证令牌</li>
 * </ul>
 * <p>
 * 管理员拥有后台管理系统的访问权限，其JWT令牌包含管理员ID、用户名和角色信息，
 * 区别于普通用户的令牌（管理员使用generateAdminToken方法生成）。
 * <p>
 * 安全说明：
 * <ul>
 *   <li>密码校验（当前使用明文比较，生产环境应使用BCrypt等加密算法）</li>
 *   <li>账号状态检查（禁用的管理员无法登录）</li>
 *   <li>JWT令牌生成（包含管理员ID、用户名、角色）</li>
 * </ul>
 *
 * @author yunming
 * @see AdminService
 */
@Service // 将该类标记为Spring的Service组件
public class AdminServiceImpl implements AdminService {

    private final AdminMapper adminMapper; // 管理员数据访问对象，用于查询管理员账号信息
    private final JwtUtils jwtUtils; // JWT工具类实例，用于生成和验证JWT令牌

    /**
     * 构造函数注入依赖
     *
     * @param adminMapper 管理员数据访问对象
     * @param jwtUtils JWT工具类实例
     */
    public AdminServiceImpl(AdminMapper adminMapper, JwtUtils jwtUtils) {
        this.adminMapper = adminMapper; // 注入管理员Mapper
        this.jwtUtils = jwtUtils; // 注入JWT工具类
    }

    /**
     * 管理员登录认证
     * <p>
     * 登录认证流程：
     * <ol>
     *   <li>根据用户名查询管理员账号（使用LambdaQueryWrapper构建精确匹配条件）</li>
     *   <li>校验账号是否存在，不存在则返回"用户名或密码错误"（不明确告知是用户名不存在）</li>
     *   <li>校验密码是否匹配，不匹配则返回相同错误信息（防止用户名枚举攻击）</li>
     *   <li>检查管理员账号状态，status=0表示已禁用，禁止登录</li>
     *   <li>认证通过后生成管理员JWT令牌（包含ID、用户名、角色信息）</li>
     *   <li>返回LoginVO包含令牌和管理员信息</li>
     * </ol>
     *
     * @param dto 登录数据传输对象，包含username（用户名）和password（密码）
     * @return 登录视图对象，包含JWT令牌token、管理员ID和用户名
     * @throws BusinessException 认证失败时抛出400/403异常
     */
    @Override // 标识该方法覆盖了父接口中的方法
    public LoginVO login(LoginDTO dto) {
        // 第1步：根据用户名查询管理员账号
        LambdaQueryWrapper<Admin> wrapper = new LambdaQueryWrapper<>(); // 创建Lambda查询包装器
        wrapper.eq(Admin::getUsername, dto.getUsername()); // 构建等值查询条件：用户名精确匹配
        Admin admin = adminMapper.selectOne(wrapper); // 执行查询，期望返回唯一一条记录

        // 第2步：校验账号是否存在
        if (admin == null) { // 如果查询结果为空
            throw new BusinessException(400, "用户名或密码错误"); // 抛出统一的认证失败异常，不暴露用户名是否存在
        }

        // 第3步：校验密码是否正确（注意：当前为明文比较，生产环境应使用BCrypt等加密验证）
        if (!admin.getPassword().equals(dto.getPassword())) { // 比较数据库密码和用户输入密码
            throw new BusinessException(400, "用户名或密码错误"); // 密码不匹配，抛出统一的认证失败异常
        }

        // 第4步：检查管理员账号是否被禁用（status=0表示禁用，status=1表示正常）
        if (admin.getStatus() == 0) { // 如果管理员状态为0（禁用）
            throw new BusinessException(403, "管理员账号已被禁用"); // 抛出403禁止访问异常
        }

        // 第5步：认证通过，生成JWT管理员令牌
        // generateAdminToken方法创建的令牌包含管理员ID、用户名和角色信息
        String token = jwtUtils.generateAdminToken(admin.getId(), admin.getUsername(), admin.getRole()); // 生成管理员JWT令牌

        // 第6步：构建并返回登录视图对象
        return new LoginVO(token, admin.getId(), admin.getUsername()); // 返回包含令牌和管理员信息的LoginVO
    }

}
