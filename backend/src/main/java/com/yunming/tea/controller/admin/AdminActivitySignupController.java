package com.yunming.tea.controller.admin;

import com.yunming.tea.common.Result;
import com.yunming.tea.service.ActivitySignupService;
import com.yunming.tea.vo.ActivitySignupVO;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/activity-signups")
public class AdminActivitySignupController {

    private final ActivitySignupService signupService;

    public AdminActivitySignupController(ActivitySignupService signupService) {
        this.signupService = signupService;
    }

    @GetMapping
    public Result<List<ActivitySignupVO>> list(@RequestParam Long activityId) {
        return Result.success(signupService.adminList(activityId));
    }
}
