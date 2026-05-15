package com.yunming.tea.service;

import com.yunming.tea.dto.ChangePasswordDTO;
import com.yunming.tea.dto.LoginDTO;
import com.yunming.tea.dto.UpdateProfileDTO;
import com.yunming.tea.dto.UserRegisterDTO;
import com.yunming.tea.vo.LoginVO;
import com.yunming.tea.vo.UserProfileVO;

/**
 * 用户服务接口
 */
public interface UserService {

    /**
     * 用户注册
     */
    void register(UserRegisterDTO dto);

    /**
     * 用户登录
     */
    LoginVO login(LoginDTO dto);

    /**
     * 获取当前用户信息
     */
    UserProfileVO getUserProfile(Long userId);

    void updateProfile(Long userId, UpdateProfileDTO dto);

    void changePassword(Long userId, ChangePasswordDTO dto);
}
