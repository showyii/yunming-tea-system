package com.yunming.tea.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.yunming.tea.entity.ActivitySignup;
import com.yunming.tea.entity.TeaActivity;
import com.yunming.tea.mapper.ActivitySignupMapper;
import com.yunming.tea.mapper.TeaActivityMapper;
import com.yunming.tea.service.TeaActivityService;
import com.yunming.tea.vo.ActivityVO;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class TeaActivityServiceImpl implements TeaActivityService {

    private final TeaActivityMapper activityMapper;
    private final ActivitySignupMapper signupMapper;

    public TeaActivityServiceImpl(TeaActivityMapper activityMapper, ActivitySignupMapper signupMapper) {
        this.activityMapper = activityMapper;
        this.signupMapper = signupMapper;
    }

    @Override
    public List<ActivityVO> list(Long userId) {
        LambdaQueryWrapper<TeaActivity> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(TeaActivity::getStatus, 1).orderByAsc(TeaActivity::getStartTime);
        return activityMapper.selectList(wrapper).stream()
                .map(a -> toVO(a, userId)).collect(Collectors.toList());
    }

    @Override
    public ActivityVO getDetail(Long activityId, Long userId) {
        TeaActivity activity = activityMapper.selectById(activityId);
        if (activity == null) return null;
        return toVO(activity, userId);
    }

    private ActivityVO toVO(TeaActivity a, Long userId) {
        ActivityVO vo = new ActivityVO();
        vo.setId(a.getId());
        vo.setTitle(a.getTitle());
        vo.setDescription(a.getDescription());
        vo.setCoverImage(a.getCoverImage());
        vo.setLocation(a.getLocation());
        vo.setStartTime(a.getStartTime());
        vo.setEndTime(a.getEndTime());
        vo.setMaxParticipants(a.getMaxParticipants());
        vo.setCurrentParticipants(a.getCurrentParticipants());
        vo.setFee(a.getFee());
        vo.setStatus(a.getStatus());
        vo.setStatusText(getStatusText(a.getStatus()));
        vo.setCreateTime(a.getCreateTime());

        if (userId != null) {
            LambdaQueryWrapper<ActivitySignup> sw = new LambdaQueryWrapper<>();
            sw.eq(ActivitySignup::getActivityId, a.getId())
                    .eq(ActivitySignup::getUserId, userId)
                    .eq(ActivitySignup::getStatus, 0);
            vo.setIsSignedUp(signupMapper.selectCount(sw) > 0);
        }

        return vo;
    }

    private String getStatusText(Integer s) {
        switch (s) {
            case 0: return "已取消";
            case 1: return "报名中";
            case 2: return "进行中";
            case 3: return "已结束";
            default: return "未知";
        }
    }
}
