package com.yunming.tea.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.yunming.tea.dto.RoomBookingDTO;
import com.yunming.tea.entity.Room;
import com.yunming.tea.entity.RoomBooking;
import com.yunming.tea.exception.BusinessException;
import com.yunming.tea.mapper.RoomBookingMapper;
import com.yunming.tea.mapper.RoomMapper;
import com.yunming.tea.service.RoomBookingService;
import com.yunming.tea.vo.RoomBookingVO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Duration;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class RoomBookingServiceImpl implements RoomBookingService {

    private final RoomBookingMapper bookingMapper;
    private final RoomMapper roomMapper;

    public RoomBookingServiceImpl(RoomBookingMapper bookingMapper, RoomMapper roomMapper) {
        this.bookingMapper = bookingMapper;
        this.roomMapper = roomMapper;
    }

    @Override
    @Transactional
    public Long book(Long userId, RoomBookingDTO dto) {
        Room room = roomMapper.selectById(dto.getRoomId());
        if (room == null || room.getStatus() == 0) {
            throw new BusinessException(400, "包间不存在或维护中");
        }

        if (!dto.getEndTime().isAfter(dto.getStartTime())) {
            throw new BusinessException(400, "结束时间必须晚于开始时间");
        }

        // 检查时间冲突
        LambdaQueryWrapper<RoomBooking> conflictWrapper = new LambdaQueryWrapper<>();
        conflictWrapper.eq(RoomBooking::getRoomId, dto.getRoomId())
                .eq(RoomBooking::getBookingDate, dto.getBookingDate())
                .in(RoomBooking::getStatus, 0, 1)
                .lt(RoomBooking::getStartTime, dto.getEndTime())
                .gt(RoomBooking::getEndTime, dto.getStartTime());
        if (bookingMapper.selectCount(conflictWrapper) > 0) {
            throw new BusinessException(400, "该时段已被预约，请选择其他时段");
        }

        long hours = Duration.between(dto.getStartTime(), dto.getEndTime()).toHours();
        if (hours < 1) {
            throw new BusinessException(400, "最少预约1小时");
        }
        int durationHours = (int) hours;

        BigDecimal totalPrice = room.getPricePerHour().multiply(BigDecimal.valueOf(durationHours));

        RoomBooking booking = new RoomBooking();
        booking.setUserId(userId);
        booking.setRoomId(dto.getRoomId());
        booking.setBookingDate(dto.getBookingDate());
        booking.setStartTime(dto.getStartTime());
        booking.setEndTime(dto.getEndTime());
        booking.setDuration(durationHours);
        booking.setTotalPrice(totalPrice);
        booking.setGuestCount(dto.getGuestCount());
        booking.setRemark(dto.getRemark());
        booking.setStatus(0);
        bookingMapper.insert(booking);

        return booking.getId();
    }

    @Override
    public List<RoomBookingVO> myBookings(Long userId) {
        LambdaQueryWrapper<RoomBooking> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(RoomBooking::getUserId, userId).orderByDesc(RoomBooking::getCreateTime);
        return bookingMapper.selectList(wrapper).stream().map(this::toVO).collect(Collectors.toList());
    }

    @Override
    public void cancel(Long bookingId, Long userId) {
        RoomBooking booking = bookingMapper.selectById(bookingId);
        if (booking == null) {
            throw new BusinessException(404, "预约记录不存在");
        }
        if (!booking.getUserId().equals(userId)) {
            throw new BusinessException(403, "无权操作此预约");
        }
        if (booking.getStatus() != 0 && booking.getStatus() != 1) {
            throw new BusinessException(400, "当前状态不允许取消");
        }
        booking.setStatus(3);
        bookingMapper.updateById(booking);
    }

    @Override
    public List<RoomBookingVO> adminList() {
        LambdaQueryWrapper<RoomBooking> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByDesc(RoomBooking::getCreateTime);
        return bookingMapper.selectList(wrapper).stream().map(this::toVO).collect(Collectors.toList());
    }

    private RoomBookingVO toVO(RoomBooking b) {
        RoomBookingVO vo = new RoomBookingVO();
        vo.setId(b.getId());
        vo.setUserId(b.getUserId());
        vo.setRoomId(b.getRoomId());
        vo.setBookingDate(b.getBookingDate());
        vo.setStartTime(b.getStartTime());
        vo.setEndTime(b.getEndTime());
        vo.setDuration(b.getDuration());
        vo.setTotalPrice(b.getTotalPrice());
        vo.setGuestCount(b.getGuestCount());
        vo.setRemark(b.getRemark());
        vo.setStatus(b.getStatus());
        vo.setStatusText(getStatusText(b.getStatus()));
        vo.setCreateTime(b.getCreateTime());

        Room room = roomMapper.selectById(b.getRoomId());
        if (room != null) {
            vo.setRoomName(room.getName());
            vo.setRoomImage(room.getImage());
        }
        return vo;
    }

    private String getStatusText(Integer s) {
        switch (s) {
            case 0: return "待确认";
            case 1: return "已确认";
            case 2: return "已完成";
            case 3: return "已取消";
            default: return "未知";
        }
    }
}
