package com.yunming.tea.controller;

import com.yunming.tea.common.Result;
import com.yunming.tea.dto.RoomBookingDTO;
import com.yunming.tea.service.RoomBookingService;
import com.yunming.tea.vo.RoomBookingVO;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/room-bookings")
public class RoomBookingController {

    private final RoomBookingService bookingService;

    public RoomBookingController(RoomBookingService bookingService) {
        this.bookingService = bookingService;
    }

    @PostMapping
    public Result<Long> book(@Valid @RequestBody RoomBookingDTO dto,
                              @RequestAttribute("userId") Long userId) {
        return Result.success("预约成功", bookingService.book(userId, dto));
    }

    @GetMapping
    public Result<List<RoomBookingVO>> myBookings(@RequestAttribute("userId") Long userId) {
        return Result.success(bookingService.myBookings(userId));
    }

    @PutMapping("/{id}/cancel")
    public Result<String> cancel(@PathVariable Long id,
                                  @RequestAttribute("userId") Long userId) {
        bookingService.cancel(id, userId);
        return Result.success("预约已取消");
    }
}
