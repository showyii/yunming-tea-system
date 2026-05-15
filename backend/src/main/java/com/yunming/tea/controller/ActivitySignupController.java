package com.yunming.tea.controller;

import com.yunming.tea.common.Result;
import com.yunming.tea.service.ActivitySignupService;
import com.yunming.tea.vo.ActivitySignupVO;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/activity-signups")
public class ActivitySignupController {

    private final ActivitySignupService signupService;

    public ActivitySignupController(ActivitySignupService signupService) {
        this.signupService = signupService;
    }

    @PostMapping
    public Result<String> signup(@Valid @RequestBody com.yunming.tea.dto.ActivitySignupDTO dto,
                                  @RequestAttribute("userId") Long userId) {
        signupService.signup(userId, dto.getActivityId());
        return Result.success("报名成功");
    }

    @GetMapping
    public Result<List<ActivitySignupVO>> mySignups(@RequestAttribute("userId") Long userId) {
        return Result.success(signupService.mySignups(userId));
    }

    @PutMapping("/{id}/cancel")
    public Result<String> cancel(@PathVariable Long id,
                                  @RequestAttribute("userId") Long userId) {
        signupService.cancel(id, userId);
        return Result.success("已取消报名");
    }
}
