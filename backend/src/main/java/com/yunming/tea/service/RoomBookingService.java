package com.yunming.tea.service;

import com.yunming.tea.dto.RoomBookingDTO;
import com.yunming.tea.vo.RoomBookingVO;

import java.util.List;

public interface RoomBookingService {

    Long book(Long userId, RoomBookingDTO dto);

    List<RoomBookingVO> myBookings(Long userId);

    void cancel(Long bookingId, Long userId);

    List<RoomBookingVO> adminList();
}
