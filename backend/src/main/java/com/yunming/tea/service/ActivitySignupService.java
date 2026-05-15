package com.yunming.tea.service;

import com.yunming.tea.vo.ActivitySignupVO;

import java.util.List;

public interface ActivitySignupService {

    void signup(Long userId, Long activityId);

    List<ActivitySignupVO> mySignups(Long userId);

    void cancel(Long signupId, Long userId);

    List<ActivitySignupVO> adminList(Long activityId);
}
