package com.yunming.tea.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.yunming.tea.dto.LoginDTO;
import com.yunming.tea.entity.Admin;
import com.yunming.tea.exception.BusinessException;
import com.yunming.tea.mapper.AdminMapper;
import com.yunming.tea.security.JwtUtils;
import com.yunming.tea.service.AdminService;
import com.yunming.tea.vo.LoginVO;
import org.springframework.stereotype.Service;

/**
 * 管理员服务实现
 */
@Service
public class AdminServiceImpl implements AdminService {

    private final AdminMapper adminMapper;
    private final JwtUtils jwtUtils;

    public AdminServiceImpl(AdminMapper adminMapper, JwtUtils jwtUtils) {
        this.adminMapper = adminMapper;
        this.jwtUtils = jwtUtils;
    }

    @Override
    public LoginVO login(LoginDTO dto) {
        LambdaQueryWrapper<Admin> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Admin::getUsername, dto.getUsername());
        Admin admin = adminMapper.selectOne(wrapper);
        if (admin == null) {
            throw new BusinessException(400, "用户名或密码错误");
        }
        if (!admin.getPassword().equals(dto.getPassword())) {
            throw new BusinessException(400, "用户名或密码错误");
        }
        if (admin.getStatus() == 0) {
            throw new BusinessException(403, "管理员账号已被禁用");
        }

        String token = jwtUtils.generateAdminToken(admin.getId(), admin.getUsername(), admin.getRole());
        return new LoginVO(token, admin.getId(), admin.getUsername());
    }

}
