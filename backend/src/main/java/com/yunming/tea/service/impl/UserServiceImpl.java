package com.yunming.tea.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.yunming.tea.dto.ChangePasswordDTO;
import com.yunming.tea.dto.LoginDTO;
import com.yunming.tea.dto.UpdateProfileDTO;
import com.yunming.tea.dto.UserRegisterDTO;
import com.yunming.tea.entity.User;
import com.yunming.tea.entity.UserProfile;
import com.yunming.tea.exception.BusinessException;
import com.yunming.tea.mapper.UserMapper;
import com.yunming.tea.mapper.UserProfileMapper;
import com.yunming.tea.security.JwtUtils;
import com.yunming.tea.service.UserService;
import com.yunming.tea.vo.LoginVO;
import com.yunming.tea.vo.UserProfileVO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 用户服务实现
 */
@Service
public class UserServiceImpl implements UserService {

    private final UserMapper userMapper;
    private final UserProfileMapper userProfileMapper;
    private final JwtUtils jwtUtils;

    public UserServiceImpl(UserMapper userMapper, UserProfileMapper userProfileMapper, JwtUtils jwtUtils) {
        this.userMapper = userMapper;
        this.userProfileMapper = userProfileMapper;
        this.jwtUtils = jwtUtils;
    }

    @Override
    @Transactional
    public void register(UserRegisterDTO dto) {
        // 检查用户名是否已存在
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getUsername, dto.getUsername());
        if (userMapper.selectCount(wrapper) > 0) {
            throw new BusinessException(400, "用户名已存在");
        }

        // 创建用户
        User user = new User();
        user.setUsername(dto.getUsername());
        user.setPassword(dto.getPassword());
        user.setPhone(dto.getPhone());
        user.setEmail(dto.getEmail());
        user.setRole(0);
        user.setStatus(1);
        userMapper.insert(user);

        // 创建用户资料
        UserProfile profile = new UserProfile();
        profile.setUserId(user.getId());
        profile.setNickname(dto.getUsername());
        userProfileMapper.insert(profile);
    }

    @Override
    public LoginVO login(LoginDTO dto) {
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getUsername, dto.getUsername());
        User user = userMapper.selectOne(wrapper);
        if (user == null) {
            throw new BusinessException(400, "用户名或密码错误");
        }
        if (!user.getPassword().equals(dto.getPassword())) {
            throw new BusinessException(400, "用户名或密码错误");
        }
        if (user.getStatus() == 0) {
            throw new BusinessException(403, "账号已被禁用");
        }

        String token = jwtUtils.generateToken(user.getId(), user.getUsername(), user.getRole());
        return new LoginVO(token, user.getId(), user.getUsername());
    }

    @Override
    public UserProfileVO getUserProfile(Long userId) {
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException(404, "用户不存在");
        }

        LambdaQueryWrapper<UserProfile> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserProfile::getUserId, userId);
        UserProfile profile = userProfileMapper.selectOne(wrapper);

        UserProfileVO vo = new UserProfileVO();
        vo.setId(user.getId());
        vo.setUsername(user.getUsername());
        vo.setPhone(user.getPhone());
        vo.setEmail(user.getEmail());
        vo.setRole(user.getRole());
        vo.setCreateTime(user.getCreateTime());

        if (profile != null) {
            vo.setNickname(profile.getNickname());
            vo.setAvatar(profile.getAvatar());
            vo.setGender(profile.getGender());
            vo.setBirthday(profile.getBirthday());
            vo.setAddress(profile.getAddress());
            vo.setSignature(profile.getSignature());
        }

        return vo;
    }

    @Override
    @Transactional
    public void updateProfile(Long userId, UpdateProfileDTO dto) {
        LambdaQueryWrapper<UserProfile> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserProfile::getUserId, userId);
        UserProfile profile = userProfileMapper.selectOne(wrapper);
        if (profile == null) {
            profile = new UserProfile();
            profile.setUserId(userId);
        }
        if (dto.getNickname() != null) profile.setNickname(dto.getNickname());
        if (dto.getAvatar() != null) profile.setAvatar(dto.getAvatar());
        if (dto.getGender() != null) profile.setGender(dto.getGender());
        if (dto.getBirthday() != null) profile.setBirthday(dto.getBirthday());
        if (dto.getAddress() != null) profile.setAddress(dto.getAddress());
        if (dto.getSignature() != null) profile.setSignature(dto.getSignature());

        if (profile.getId() == null) {
            userProfileMapper.insert(profile);
        } else {
            userProfileMapper.updateById(profile);
        }
    }

    @Override
    public void changePassword(Long userId, ChangePasswordDTO dto) {
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException(404, "用户不存在");
        }
        if (!user.getPassword().equals(dto.getOldPassword())) {
            throw new BusinessException(400, "原密码错误");
        }
        user.setPassword(dto.getNewPassword());
        userMapper.updateById(user);
    }
}
