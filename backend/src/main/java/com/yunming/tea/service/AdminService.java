package com.yunming.tea.service;

import com.yunming.tea.dto.LoginDTO;
import com.yunming.tea.vo.LoginVO;

/**
 * 管理员服务接口
 */
public interface AdminService {

    /**
     * 管理员登录
     */
    LoginVO login(LoginDTO dto);
}
