package com.yunming.tea.controller;

import com.yunming.tea.common.Result;
import com.yunming.tea.dto.ChangePasswordDTO;
import com.yunming.tea.dto.UpdateProfileDTO;
import com.yunming.tea.service.UserService;
import com.yunming.tea.vo.UserProfileVO;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * 用户控制器
 */
@RestController
@RequestMapping("/api/user")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    /**
     * 获取当前用户信息
     * GET /api/user/profile
     */
    @GetMapping("/profile")
    public Result<UserProfileVO> getProfile(@RequestAttribute("userId") Long userId) {
        UserProfileVO vo = userService.getUserProfile(userId);
        return Result.success(vo);
    }

    @PutMapping("/profile")
    public Result<String> updateProfile(@RequestAttribute("userId") Long userId,
                                         @Valid @RequestBody UpdateProfileDTO dto) {
        userService.updateProfile(userId, dto);
        return Result.success("资料已更新");
    }

    @PutMapping("/password")
    public Result<String> changePassword(@RequestAttribute("userId") Long userId,
                                          @Valid @RequestBody ChangePasswordDTO dto) {
        userService.changePassword(userId, dto);
        return Result.success("密码已修改");
    }
}
