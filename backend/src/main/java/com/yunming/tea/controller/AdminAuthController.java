package com.yunming.tea.controller;

import com.yunming.tea.common.Result;
import com.yunming.tea.dto.LoginDTO;
import com.yunming.tea.service.AdminService;
import com.yunming.tea.vo.LoginVO;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

/**
 * 管理员认证控制器
 */
@RestController
@RequestMapping("/api/admin/auth")
public class AdminAuthController {

    private final AdminService adminService;

    public AdminAuthController(AdminService adminService) {
        this.adminService = adminService;
    }

    /**
     * 管理员登录
     * POST /api/admin/auth/login
     */
    @PostMapping("/login")
    public Result<LoginVO> login(@Valid @RequestBody LoginDTO dto) {
        LoginVO vo = adminService.login(dto);
        return Result.success(vo);
    }
}
