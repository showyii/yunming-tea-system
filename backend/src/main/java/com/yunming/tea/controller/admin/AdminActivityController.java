package com.yunming.tea.controller.admin;

import com.yunming.tea.common.Result;
import com.yunming.tea.entity.TeaActivity;
import com.yunming.tea.mapper.TeaActivityMapper;
import com.yunming.tea.service.TeaActivityService;
import com.yunming.tea.vo.ActivityVO;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/activities")
public class AdminActivityController {

    private final TeaActivityService activityService;
    private final TeaActivityMapper activityMapper;

    public AdminActivityController(TeaActivityService activityService, TeaActivityMapper activityMapper) {
        this.activityService = activityService;
        this.activityMapper = activityMapper;
    }

    @GetMapping
    public Result<List<ActivityVO>> list() {
        return Result.success(activityService.list(null));
    }

    @GetMapping("/{id}")
    public Result<ActivityVO> detail(@PathVariable Long id) {
        return Result.success(activityService.getDetail(id, null));
    }

    @PostMapping
    public Result<TeaActivity> create(@RequestBody TeaActivity activity) {
        activityMapper.insert(activity);
        return Result.success(activity);
    }

    @PutMapping("/{id}")
    public Result<String> update(@PathVariable Long id, @RequestBody TeaActivity activity) {
        activity.setId(id);
        activityMapper.updateById(activity);
        return Result.success("更新成功");
    }

    @DeleteMapping("/{id}")
    public Result<String> delete(@PathVariable Long id) {
        activityMapper.deleteById(id);
        return Result.success("删除成功");
    }
}
