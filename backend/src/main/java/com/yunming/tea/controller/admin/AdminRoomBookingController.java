package com.yunming.tea.controller.admin;

import com.yunming.tea.common.Result;
import com.yunming.tea.entity.RoomBooking;
import com.yunming.tea.mapper.RoomBookingMapper;
import com.yunming.tea.service.RoomBookingService;
import com.yunming.tea.vo.RoomBookingVO;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/room-bookings")
public class AdminRoomBookingController {

    private final RoomBookingService bookingService;
    private final RoomBookingMapper bookingMapper;

    public AdminRoomBookingController(RoomBookingService bookingService, RoomBookingMapper bookingMapper) {
        this.bookingService = bookingService;
        this.bookingMapper = bookingMapper;
    }

    @GetMapping
    public Result<List<RoomBookingVO>> list() {
        return Result.success(bookingService.adminList());
    }

    @PutMapping("/{id}/status")
    public Result<String> updateStatus(@PathVariable Long id, @RequestBody java.util.Map<String, Integer> body) {
        RoomBooking booking = bookingMapper.selectById(id);
        if (booking != null) {
            booking.setStatus(body.get("status"));
            bookingMapper.updateById(booking);
        }
        return Result.success("状态已更新");
    }
}
