package com.yunming.tea.controller;

import com.yunming.tea.common.Result;
import com.yunming.tea.entity.Room;
import com.yunming.tea.service.RoomService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/rooms")
public class RoomController {

    private final RoomService roomService;

    public RoomController(RoomService roomService) {
        this.roomService = roomService;
    }

    @GetMapping
    public Result<List<Room>> list() {
        return Result.success(roomService.listAvailable());
    }

    @GetMapping("/{id}")
    public Result<Room> detail(@PathVariable Long id) {
        return Result.success(roomService.getById(id));
    }
}
