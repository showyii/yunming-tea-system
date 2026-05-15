package com.yunming.tea.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.yunming.tea.entity.Room;
import com.yunming.tea.mapper.RoomMapper;
import com.yunming.tea.service.RoomService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RoomServiceImpl implements RoomService {

    private final RoomMapper roomMapper;

    public RoomServiceImpl(RoomMapper roomMapper) {
        this.roomMapper = roomMapper;
    }

    @Override
    public List<Room> listAvailable() {
        LambdaQueryWrapper<Room> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Room::getStatus, 1);
        return roomMapper.selectList(wrapper);
    }

    @Override
    public Room getById(Long id) {
        return roomMapper.selectById(id);
    }
}
