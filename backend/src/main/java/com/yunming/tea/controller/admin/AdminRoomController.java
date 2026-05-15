package com.yunming.tea.controller.admin;

import com.yunming.tea.common.Result;
import com.yunming.tea.entity.Room;
import com.yunming.tea.mapper.RoomMapper;
import com.yunming.tea.service.RoomService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/rooms")
public class AdminRoomController {

    private final RoomService roomService;
    private final RoomMapper roomMapper;

    public AdminRoomController(RoomService roomService, RoomMapper roomMapper) {
        this.roomService = roomService;
        this.roomMapper = roomMapper;
    }

    @GetMapping
    public Result<List<Room>> list() {
        return Result.success(roomService.listAvailable());
    }

    @GetMapping("/{id}")
    public Result<Room> detail(@PathVariable Long id) {
        return Result.success(roomService.getById(id));
    }

    @PostMapping
    public Result<Room> create(@RequestBody Room room) {
        roomMapper.insert(room);
        return Result.success(room);
    }

    @PutMapping("/{id}")
    public Result<String> update(@PathVariable Long id, @RequestBody Room room) {
        room.setId(id);
        roomMapper.updateById(room);
        return Result.success("更新成功");
    }

    @DeleteMapping("/{id}")
    public Result<String> delete(@PathVariable Long id) {
        roomMapper.deleteById(id);
        return Result.success("删除成功");
    }
}
