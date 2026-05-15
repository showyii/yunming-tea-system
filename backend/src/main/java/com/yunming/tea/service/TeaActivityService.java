package com.yunming.tea.service;

import com.yunming.tea.vo.ActivityVO;

import java.util.List;

public interface TeaActivityService {

    List<ActivityVO> list(Long userId);

    ActivityVO getDetail(Long activityId, Long userId);
}
