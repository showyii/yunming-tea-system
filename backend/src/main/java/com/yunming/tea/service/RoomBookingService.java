package com.yunming.tea.service; // 声明包路径，属于服务层接口包

import com.yunming.tea.dto.RoomBookingDTO; // 导入包间预约数据传输对象，包含预约日期、时间、人数等信息
import com.yunming.tea.vo.RoomBookingVO; // 导入包间预约视图对象，用于返回给前端的预约数据结构

import java.util.List; // 导入List集合类，用于返回预约列表

/**
 * 包间预约服务接口
 * <p>
 * 该接口定义了包间预约相关的业务操作规范，包括：
 * 1. 预约包间（包含时间冲突检测和费用计算）
 * 2. 查询当前用户的预约记录
 * 3. 取消预约
 * 4. 管理员查看所有预约记录
 * <p>
 * 预约流程包含时间段冲突检测机制，防止同一包间在同一时段被重复预约。
 * 预约费用按小时计算，最低预约时长为1小时。
 * 预约状态：待确认(0) -> 已确认(1) -> 已完成(2)，或待确认/已确认 -> 已取消(3)
 *
 * @author yunming
 * @see com.yunming.tea.service.impl.RoomBookingServiceImpl
 */
public interface RoomBookingService {

    /**
     * 预约包间
     * <p>
     * 对指定包间进行预约，操作流程：
     * 1. 校验包间是否存在且可用（status=1）
     * 2. 校验结束时间是否晚于开始时间
     * 3. 检测是否与已有预约存在时间冲突（同一日期同一包间时间重叠）
     * 4. 计算预约时长和总费用（按小时计费）
     * 5. 创建预约记录，状态为"待确认"
     * <p>
     * 时间冲突检测逻辑：在同一日期同一包间内，新预约的时间段不能与已有有效预约
     * （状态为待确认或已确认）的时间段重叠。
     *
     * @param userId 预约用户的ID
     * @param dto 包间预约数据传输对象，包含roomId（包间ID）、bookingDate（预约日期）、startTime（开始时间）、endTime（结束时间）、guestCount（人数）、remark（备注）
     * @return 新创建的预约记录ID
     */
    Long book(Long userId, RoomBookingDTO dto); // 预约包间方法声明

    /**
     * 查询当前用户的预约记录
     * <p>
     * 根据用户ID查询该用户的所有包间预约记录，
     * 按创建时间倒序排列。返回的预约数据包含包间名称和图片信息。
     *
     * @param userId 查询的用户ID
     * @return 包间预约视图对象列表，包含预约详情和包间信息
     */
    List<RoomBookingVO> myBookings(Long userId); // 查询用户预约记录方法声明

    /**
     * 取消预约
     * <p>
     * 取消包间预约，需要校验：
     * 1. 预约记录是否存在
     * 2. 是否为当前用户本人的预约记录
     * 3. 预约状态是否允许取消（只有"待确认"和"已确认"状态可以取消）
     * <p>
     * 取消后预约状态更新为"已取消"。
     *
     * @param bookingId 预约记录ID
     * @param userId 操作用户ID，用于权限校验
     */
    void cancel(Long bookingId, Long userId); // 取消预约方法声明

    /**
     * 管理员查看所有预约记录
     * <p>
     * 管理员查看系统中所有的包间预约记录，按创建时间倒序排列。
     * 用于后台的预约管理、确认和统计功能。
     *
     * @return 包间预约视图对象列表，包含所有用户的预约信息
     */
    List<RoomBookingVO> adminList(); // 管理员查询预约列表方法声明
}
