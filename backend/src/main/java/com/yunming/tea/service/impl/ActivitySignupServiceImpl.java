package com.yunming.tea.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.yunming.tea.entity.ActivitySignup;
import com.yunming.tea.entity.TeaActivity;
import com.yunming.tea.exception.BusinessException;
import com.yunming.tea.mapper.ActivitySignupMapper;
import com.yunming.tea.mapper.TeaActivityMapper;
import com.yunming.tea.service.ActivitySignupService;
import com.yunming.tea.vo.ActivitySignupVO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class ActivitySignupServiceImpl implements ActivitySignupService {

    private final ActivitySignupMapper signupMapper;
    private final TeaActivityMapper activityMapper;

    public ActivitySignupServiceImpl(ActivitySignupMapper signupMapper, TeaActivityMapper activityMapper) {
        this.signupMapper = signupMapper;
        this.activityMapper = activityMapper;
    }

    @Override
    @Transactional
    public void signup(Long userId, Long activityId) {
        TeaActivity activity = activityMapper.selectById(activityId);
        if (activity == null || activity.getStatus() != 1) {
            throw new BusinessException(400, "活动不存在或不在报名中");
        }

        // 检查重复报名
        LambdaQueryWrapper<ActivitySignup> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ActivitySignup::getActivityId, activityId)
                .eq(ActivitySignup::getUserId, userId)
                .eq(ActivitySignup::getStatus, 0);
        if (signupMapper.selectCount(wrapper) > 0) {
            throw new BusinessException(400, "您已报名此活动，请勿重复报名");
        }

        // 检查人数上限
        if (activity.getCurrentParticipants() >= activity.getMaxParticipants()) {
            throw new BusinessException(400, "活动名额已满");
        }

        ActivitySignup signup = new ActivitySignup();
        signup.setActivityId(activityId);
        signup.setUserId(userId);
        signup.setStatus(0);
        signupMapper.insert(signup);

        activity.setCurrentParticipants(activity.getCurrentParticipants() + 1);
        activityMapper.updateById(activity);
    }

    @Override
    public List<ActivitySignupVO> mySignups(Long userId) {
        LambdaQueryWrapper<ActivitySignup> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ActivitySignup::getUserId, userId)
                .ne(ActivitySignup::getStatus, 2)
                .orderByDesc(ActivitySignup::getCreateTime);
        List<ActivitySignup> signups = signupMapper.selectList(wrapper);

        List<ActivitySignupVO> result = new ArrayList<>();
        for (ActivitySignup s : signups) {
            TeaActivity activity = activityMapper.selectById(s.getActivityId());
            if (activity == null) continue;
            ActivitySignupVO vo = new ActivitySignupVO();
            vo.setId(s.getId());
            vo.setActivityId(s.getActivityId());
            vo.setActivityTitle(activity.getTitle());
            vo.setActivityCover(activity.getCoverImage());
            vo.setActivityStartTime(activity.getStartTime());
            vo.setStatus(s.getStatus());
            vo.setStatusText(getStatusText(s.getStatus()));
            vo.setCreateTime(s.getCreateTime());
            result.add(vo);
        }
        return result;
    }

    @Override
    @Transactional
    public void cancel(Long signupId, Long userId) {
        ActivitySignup signup = signupMapper.selectById(signupId);
        if (signup == null) {
            throw new BusinessException(404, "报名记录不存在");
        }
        if (!signup.getUserId().equals(userId)) {
            throw new BusinessException(403, "无权操作");
        }
        if (signup.getStatus() != 0) {
            throw new BusinessException(400, "当前状态不允许取消报名");
        }

        signup.setStatus(2);
        signupMapper.updateById(signup);

        TeaActivity activity = activityMapper.selectById(signup.getActivityId());
        if (activity != null) {
            activity.setCurrentParticipants(Math.max(0, activity.getCurrentParticipants() - 1));
            activityMapper.updateById(activity);
        }
    }

    @Override
    public List<ActivitySignupVO> adminList(Long activityId) {
        LambdaQueryWrapper<ActivitySignup> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ActivitySignup::getActivityId, activityId)
                .orderByDesc(ActivitySignup::getCreateTime);
        List<ActivitySignup> signups = signupMapper.selectList(wrapper);

        List<ActivitySignupVO> result = new ArrayList<>();
        for (ActivitySignup s : signups) {
            TeaActivity activity = activityMapper.selectById(s.getActivityId());
            ActivitySignupVO vo = new ActivitySignupVO();
            vo.setId(s.getId());
            vo.setActivityId(s.getActivityId());
            vo.setActivityTitle(activity != null ? activity.getTitle() : "");
            vo.setActivityCover(activity != null ? activity.getCoverImage() : "");
            vo.setActivityStartTime(activity != null ? activity.getStartTime() : null);
            vo.setStatus(s.getStatus());
            vo.setStatusText(getStatusText(s.getStatus()));
            vo.setCreateTime(s.getCreateTime());
            result.add(vo);
        }
        return result;
    }

    private String getStatusText(Integer s) {
        switch (s) {
            case 0: return "已报名";
            case 1: return "已签到";
            case 2: return "已取消";
            default: return "未知";
        }
    }
}
