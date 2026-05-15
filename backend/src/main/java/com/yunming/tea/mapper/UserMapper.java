package com.yunming.tea.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yunming.tea.entity.User;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserMapper extends BaseMapper<User> {
}
