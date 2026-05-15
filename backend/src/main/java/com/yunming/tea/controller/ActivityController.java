package com.yunming.tea.controller;

import com.yunming.tea.common.Result;
import com.yunming.tea.service.TeaActivityService;
import com.yunming.tea.vo.ActivityVO;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/activities")
public class ActivityController {

    private final TeaActivityService activityService;

    public ActivityController(TeaActivityService activityService) {
        this.activityService = activityService;
    }

    @GetMapping
    public Result<List<ActivityVO>> list(@RequestAttribute(value = "userId", required = false) Long userId) {
        return Result.success(activityService.list(userId));
    }

    @GetMapping("/{id}")
    public Result<ActivityVO> detail(@PathVariable Long id,
                                      @RequestAttribute(value = "userId", required = false) Long userId) {
        return Result.success(activityService.getDetail(id, userId));
    }
}
