package com.yunming.tea.service.impl; // 声明包路径，属于服务实现层包

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper; // 导入MyBatis-Plus的Lambda查询包装器
import com.yunming.tea.dto.RoomBookingDTO; // 导入包间预约数据传输对象
import com.yunming.tea.entity.Room; // 导入包间实体类
import com.yunming.tea.entity.RoomBooking; // 导入包间预约实体类
import com.yunming.tea.exception.BusinessException; // 导入业务异常类
import com.yunming.tea.mapper.RoomBookingMapper; // 导入包间预约数据访问层
import com.yunming.tea.mapper.RoomMapper; // 导入包间数据访问层
import com.yunming.tea.service.RoomBookingService; // 导入包间预约服务接口
import com.yunming.tea.vo.RoomBookingVO; // 导入包间预约视图对象
import org.springframework.stereotype.Service; // 导入Spring的Service注解
import org.springframework.transaction.annotation.Transactional; // 导入Spring事务注解

import java.math.BigDecimal; // 导入BigDecimal，用于精确的金额计算
import java.time.Duration; // 导入Duration，用于计算时间间隔（小时数）
import java.util.List; // 导入List接口
import java.util.stream.Collectors; // 导入Collectors工具类

/**
 * 包间预约服务实现类
 * <p>
 * 实现了{@link RoomBookingService}接口中定义的包间预约相关业务逻辑。
 * 主要功能包括：
 * <ul>
 *   <li>预约包间：包含包间状态检查、时间冲突检测、费用计算</li>
 *   <li>查询用户预约记录：返回用户的预约历史及状态</li>
 *   <li>取消预约：校验权限和状态后取消预约</li>
 *   <li>管理员查看所有预约：返回全量预约记录供后台管理</li>
 * </ul>
 * <p>
 * 预约状态说明：
 * <ul>
 *   <li>0 - 待确认：用户提交预约，等待管理员确认</li>
 *   <li>1 - 已确认：管理员确认预约，包间已预留</li>
 *   <li>2 - 已完成：预约时间已过，使用完毕</li>
 *   <li>3 - 已取消：预约被取消</li>
 * </ul>
 * <p>
 * 时间冲突检测逻辑：
 * 在同一日期、同一包间内，新预约的时间段与已有有效预约
 * （状态为待确认0或已确认1）的时间段不能存在重叠。
 * 使用标准时间区间重叠判断公式：A.start < B.end AND A.end > B.start
 *
 * @author yunming
 * @see RoomBookingService
 */
@Service // 将该类标记为Spring的Service组件
public class RoomBookingServiceImpl implements RoomBookingService {

    private final RoomBookingMapper bookingMapper; // 包间预约数据访问对象
    private final RoomMapper roomMapper; // 包间数据访问对象，用于查询包间价格和状态

    /**
     * 构造函数注入依赖
     *
     * @param bookingMapper 包间预约数据访问对象
     * @param roomMapper 包间数据访问对象
     */
    public RoomBookingServiceImpl(RoomBookingMapper bookingMapper, RoomMapper roomMapper) {
        this.bookingMapper = bookingMapper; // 注入预约Mapper
        this.roomMapper = roomMapper; // 注入包间Mapper
    }

    /**
     * 预约包间
     * <p>
     * 预约流程：
     * <ol>
     *   <li>校验包间是否存在且可用（status=1）</li>
     *   <li>校验结束时间是否晚于开始时间</li>
     *   <li>检测时间段是否与已有预约冲突</li>
     *   <li>计算预约时长（小时），最少1小时</li>
     *   <li>根据包间小时单价计算总费用</li>
     *   <li>创建预约记录，状态为"待确认"(0)</li>
     * </ol>
     * 整个操作在事务中执行。
     *
     * @param userId 预约用户ID
     * @param dto 包间预约数据传输对象，包含roomId、bookingDate、startTime、endTime、guestCount、remark
     * @return 新创建的预约记录ID
     * @throws BusinessException 包间不存在、时间段无效、时间冲突、时长不足时抛出异常
     */
    @Override // 标识该方法覆盖了父接口中的方法
    @Transactional // 声明事务
    public Long book(Long userId, RoomBookingDTO dto) {
        // 第1步：校验包间是否存在且可用
        Room room = roomMapper.selectById(dto.getRoomId()); // 根据包间ID查询包间信息
        if (room == null || room.getStatus() == 0) { // 包间不存在或状态为0（维护中）
            throw new BusinessException(400, "包间不存在或维护中"); // 抛出400异常
        }

        // 第2步：校验时间是否有效（结束时间必须晚于开始时间）
        if (!dto.getEndTime().isAfter(dto.getStartTime())) { // 结束时间不晚于开始时间
            throw new BusinessException(400, "结束时间必须晚于开始时间"); // 提示时间无效
        }

        // 第3步：检测时间冲突
        // 冲突条件：同一日期、同一包间、有效状态(0或1)且时间段有重叠
        LambdaQueryWrapper<RoomBooking> conflictWrapper = new LambdaQueryWrapper<>(); // 创建冲突检测包装器
        conflictWrapper.eq(RoomBooking::getRoomId, dto.getRoomId()) // 条件：同一包间
                .eq(RoomBooking::getBookingDate, dto.getBookingDate()) // 条件：同一日期
                .in(RoomBooking::getStatus, 0, 1) // 条件：状态为"待确认"(0)或"已确认"(1)的有效预约
                .lt(RoomBooking::getStartTime, dto.getEndTime()) // 条件：已有预约的开始时间 < 新预约的结束时间
                .gt(RoomBooking::getEndTime, dto.getStartTime()); // 条件：已有预约的结束时间 > 新预约的开始时间
        // 以上两个条件合起来就是经典的时间区间重叠判断：
        // 当 existing.start < new.end AND existing.end > new.start 时，两个时间段重叠
        if (bookingMapper.selectCount(conflictWrapper) > 0) { // 如果检测到冲突预约
            throw new BusinessException(400, "该时段已被预约，请选择其他时段"); // 提示时段冲突
        }

        // 第4步：计算预约时长（小时数）
        long hours = Duration.between(dto.getStartTime(), dto.getEndTime()).toHours(); // 计算开始到结束的小时差
        if (hours < 1) { // 最少预约1小时
            throw new BusinessException(400, "最少预约1小时"); // 提示最小预约时长
        }
        int durationHours = (int) hours; // 转为int类型（小时数）

        // 第5步：计算总费用 = 包间每小时单价 * 预约时长
        BigDecimal totalPrice = room.getPricePerHour() // 获取包间每小时单价
                .multiply(BigDecimal.valueOf(durationHours)); // 乘以预约时长

        // 第6步：创建预约记录
        RoomBooking booking = new RoomBooking(); // 创建预约实体对象
        booking.setUserId(userId); // 设置预约用户ID
        booking.setRoomId(dto.getRoomId()); // 设置预约的包间ID
        booking.setBookingDate(dto.getBookingDate()); // 设置预约日期
        booking.setStartTime(dto.getStartTime()); // 设置开始时间
        booking.setEndTime(dto.getEndTime()); // 设置结束时间
        booking.setDuration(durationHours); // 设置预约时长（小时）
        booking.setTotalPrice(totalPrice); // 设置总费用
        booking.setGuestCount(dto.getGuestCount()); // 设置预约人数
        booking.setRemark(dto.getRemark()); // 设置备注信息
        booking.setStatus(0); // 设置预约状态为"待确认"(0)
        bookingMapper.insert(booking); // 将预约记录插入数据库

        return booking.getId(); // 返回新创建的预约记录ID
    }

    /**
     * 查询当前用户的预约记录
     * <p>
     * 根据用户ID查询该用户的所有包间预约记录，
     * 按创建时间倒序排列。返回数据中包含包间名称和图片信息。
     *
     * @param userId 查询的用户ID
     * @return 包间预约视图对象列表，包含预约详情和包间信息
     */
    @Override // 标识该方法覆盖了父接口中的方法
    public List<RoomBookingVO> myBookings(Long userId) {
        // 构建查询条件：查询指定用户的所有预约记录，按创建时间倒序
        LambdaQueryWrapper<RoomBooking> wrapper = new LambdaQueryWrapper<>(); // 创建查询包装器
        wrapper.eq(RoomBooking::getUserId, userId) // 条件：用户ID匹配
                .orderByDesc(RoomBooking::getCreateTime); // 排序：按创建时间倒序（最新预约在前）
        // 查询列表，通过Stream映射为视图对象
        return bookingMapper.selectList(wrapper) // 执行查询
                .stream() // 转为流
                .map(this::toVO) // 将每个实体映射为视图对象（含包间名称和图片）
                .collect(Collectors.toList()); // 收集为List
    }

    /**
     * 取消预约
     * <p>
     * 取消包间预约，需要校验：
     * 1. 预约记录是否存在
     * 2. 是否为当前用户本人的预约记录
     * 3. 预约状态是否允许取消（只有"待确认"和"已确认"状态可以取消）
     * <p>
     * 取消后预约状态更新为"已取消"(3)。
     *
     * @param bookingId 预约记录ID
     * @param userId 操作用户ID，用于权限校验
     * @throws BusinessException 预约不存在、无权操作、状态不允许时抛出异常
     */
    @Override // 标识该方法覆盖了父接口中的方法
    public void cancel(Long bookingId, Long userId) {
        // 第1步：查询预约记录
        RoomBooking booking = bookingMapper.selectById(bookingId); // 根据ID查询预约
        if (booking == null) { // 预约不存在
            throw new BusinessException(404, "预约记录不存在"); // 抛出404异常
        }
        // 第2步：校验是否为当前用户本人的预约
        if (!booking.getUserId().equals(userId)) { // 不是本人预约
            throw new BusinessException(403, "无权操作此预约"); // 抛出403无权限异常
        }
        // 第3步：校验预约状态是否允许取消（待确认0或已确认1可以取消，已完成2或已取消3不能取消）
        if (booking.getStatus() != 0 && booking.getStatus() != 1) { // 状态不是待确认也不是已确认
            throw new BusinessException(400, "当前状态不允许取消"); // 提示状态不允许
        }
        // 第4步：更新预约状态为"已取消"(3)
        booking.setStatus(3); // 设置状态为已取消
        bookingMapper.updateById(booking); // 根据ID更新预约记录
    }

    /**
     * 管理员查看所有预约记录
     * <p>
     * 查询系统中所有的包间预约记录，按创建时间倒序排列。
     * 用于后台预约管理、确认和统计功能。
     *
     * @return 包间预约视图对象列表，包含所有用户的预约信息
     */
    @Override // 标识该方法覆盖了父接口中的方法
    public List<RoomBookingVO> adminList() {
        // 查询所有预约记录，按创建时间倒序
        LambdaQueryWrapper<RoomBooking> wrapper = new LambdaQueryWrapper<>(); // 创建查询包装器
        wrapper.orderByDesc(RoomBooking::getCreateTime); // 排序：按创建时间倒序
        // 查询并映射为视图对象
        return bookingMapper.selectList(wrapper) // 执行查询
                .stream() // 转为流
                .map(this::toVO) // 映射为视图对象
                .collect(Collectors.toList()); // 收集为List
    }

    /**
     * 将预约实体对象转换为视图对象
     * <p>
     * 自动关联查询包间名称和图片信息，填充到视图对象中。
     * 此方法被myBookings和adminList方法复用。
     *
     * @param b 预约实体对象
     * @return 预约视图对象
     */
    private RoomBookingVO toVO(RoomBooking b) { // 私有方法，实体转视图
        RoomBookingVO vo = new RoomBookingVO(); // 创建视图对象
        vo.setId(b.getId()); // 设置预约记录ID
        vo.setUserId(b.getUserId()); // 设置预约用户ID
        vo.setRoomId(b.getRoomId()); // 设置包间ID
        vo.setBookingDate(b.getBookingDate()); // 设置预约日期
        vo.setStartTime(b.getStartTime()); // 设置开始时间
        vo.setEndTime(b.getEndTime()); // 设置结束时间
        vo.setDuration(b.getDuration()); // 设置预约时长（小时）
        vo.setTotalPrice(b.getTotalPrice()); // 设置总费用
        vo.setGuestCount(b.getGuestCount()); // 设置预约人数
        vo.setRemark(b.getRemark()); // 设置备注
        vo.setStatus(b.getStatus()); // 设置预约状态
        vo.setStatusText(getStatusText(b.getStatus())); // 将状态码转为中文文本
        vo.setCreateTime(b.getCreateTime()); // 设置创建时间

        // 查询关联的包间信息，获取包间名称和图片
        Room room = roomMapper.selectById(b.getRoomId()); // 根据包间ID查询包间
        if (room != null) { // 如果包间还存在
            vo.setRoomName(room.getName()); // 设置包间名称
            vo.setRoomImage(room.getImage()); // 设置包间图片
        }
        return vo; // 返回视图对象
    }

    /**
     * 将预约状态码转换为中文文本
     *
     * @param s 预约状态码：0-待确认, 1-已确认, 2-已完成, 3-已取消
     * @return 对应的中文状态文本
     */
    private String getStatusText(Integer s) { // 私有方法，状态码转中文
        switch (s) { // 根据状态码匹配
            case 0: return "待确认"; // 状态0：等待管理员确认
            case 1: return "已确认"; // 状态1：管理员已确认预约
            case 2: return "已完成"; // 状态2：预约时间已过，使用完毕
            case 3: return "已取消"; // 状态3：预约已取消
            default: return "未知"; // 未知状态
        }
    }
}
