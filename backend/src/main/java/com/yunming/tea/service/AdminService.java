package com.yunming.tea.service; // 声明包路径，属于服务层接口包

import com.yunming.tea.dto.LoginDTO; // 导入登录数据传输对象，包含用户名和密码
import com.yunming.tea.vo.LoginVO; // 导入登录视图对象，包含token和用户基本信息

/**
 * 管理员服务接口
 * <p>
 * 该接口定义了管理员后台管理的业务操作规范，当前主要包括：
 * 1. 管理员账号登录认证
 * <p>
 * 管理员拥有后台管理系统的访问权限，可以进行商品管理、订单管理、
 * 用户管理、数据统计等操作。登录验证包括账号密码校验和账号状态检查。
 *
 * @author yunming
 * @see com.yunming.tea.service.impl.AdminServiceImpl
 */
public interface AdminService {

    /**
     * 管理员登录
     * <p>
     * 根据用户名和密码进行管理员身份认证，认证流程包括：
     * 1. 根据用户名查询管理员账号是否存在
     * 2. 校验密码是否正确
     * 3. 检查管理员账号是否被禁用
     * <p>
     * 认证成功后生成JWT令牌（包含管理员ID、用户名和角色信息），
     * 前端使用该令牌进行后续管理员接口的请求认证。
     *
     * @param dto 登录数据传输对象，包含username（用户名）和password（密码）
     * @return 登录视图对象，包含JWT令牌token、管理员ID和用户名
     * @throws com.yunming.tea.exception.BusinessException 用户名或密码错误时抛出400异常，账号被禁用时抛出403异常
     */
    LoginVO login(LoginDTO dto); // 管理员登录方法声明
}
