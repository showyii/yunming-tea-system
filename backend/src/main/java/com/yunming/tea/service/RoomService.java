package com.yunming.tea.service;

import com.yunming.tea.entity.Room;

import java.util.List;

public interface RoomService {

    List<Room> listAvailable();

    Room getById(Long id);
}
