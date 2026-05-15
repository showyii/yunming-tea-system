-- =============================================
-- 云茗茶馆预约与商城系统 - 数据库初始化脚本
-- =============================================

CREATE DATABASE IF NOT EXISTS yunming_tea
    DEFAULT CHARACTER SET utf8mb4
    DEFAULT COLLATE utf8mb4_unicode_ci;

USE yunming_tea;

-- ==================== 用户模块 ====================

-- 用户表
CREATE TABLE IF NOT EXISTS `user` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '用户ID',
    `username` VARCHAR(50) NOT NULL COMMENT '用户名',
    `password` VARCHAR(255) NOT NULL COMMENT '密码（加密）',
    `phone` VARCHAR(20) DEFAULT NULL COMMENT '手机号',
    `email` VARCHAR(100) DEFAULT NULL COMMENT '邮箱',
    `role` TINYINT NOT NULL DEFAULT 0 COMMENT '角色：0-普通用户，1-管理员',
    `status` TINYINT NOT NULL DEFAULT 1 COMMENT '状态：0-禁用，1-正常',
    `deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除：0-未删除，1-已删除',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_username` (`username`),
    KEY `idx_phone` (`phone`),
    KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户表';

-- 用户资料表（1v1 with user）
CREATE TABLE IF NOT EXISTS `user_profile` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '资料ID',
    `user_id` BIGINT NOT NULL COMMENT '用户ID',
    `nickname` VARCHAR(50) DEFAULT NULL COMMENT '昵称',
    `avatar` VARCHAR(500) DEFAULT NULL COMMENT '头像URL',
    `gender` TINYINT DEFAULT NULL COMMENT '性别：0-未知，1-男，2-女',
    `birthday` DATE DEFAULT NULL COMMENT '生日',
    `address` VARCHAR(255) DEFAULT NULL COMMENT '地址',
    `signature` VARCHAR(255) DEFAULT NULL COMMENT '个性签名',
    `deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_user_id` (`user_id`),
    CONSTRAINT `fk_profile_user` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户资料表';

-- 管理员表
CREATE TABLE IF NOT EXISTS `admin` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '管理员ID',
    `username` VARCHAR(50) NOT NULL COMMENT '管理员用户名',
    `password` VARCHAR(255) NOT NULL COMMENT '密码（加密）',
    `name` VARCHAR(50) DEFAULT NULL COMMENT '管理员姓名',
    `phone` VARCHAR(20) DEFAULT NULL COMMENT '手机号',
    `email` VARCHAR(100) DEFAULT NULL COMMENT '邮箱',
    `role` VARCHAR(20) NOT NULL DEFAULT 'ADMIN' COMMENT '角色：SUPER_ADMIN-超级管理员，ADMIN-普通管理员',
    `status` TINYINT NOT NULL DEFAULT 1 COMMENT '状态：0-禁用，1-正常',
    `deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_admin_username` (`username`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='管理员表';

-- ==================== 商品模块 ====================

-- 茶品分类表
CREATE TABLE IF NOT EXISTS `tea_category` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '分类ID',
    `name` VARCHAR(50) NOT NULL COMMENT '分类名称',
    `parent_id` BIGINT DEFAULT 0 COMMENT '父分类ID，0为顶级分类',
    `sort` INT DEFAULT 0 COMMENT '排序',
    `icon` VARCHAR(255) DEFAULT NULL COMMENT '分类图标',
    `description` VARCHAR(255) DEFAULT NULL COMMENT '分类描述',
    `deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    KEY `idx_parent_id` (`parent_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='茶品分类表';

-- 茶品商品表（1vN with tea_category）
CREATE TABLE IF NOT EXISTS `tea_product` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '商品ID',
    `category_id` BIGINT NOT NULL COMMENT '分类ID',
    `name` VARCHAR(100) NOT NULL COMMENT '商品名称',
    `subtitle` VARCHAR(255) DEFAULT NULL COMMENT '商品副标题',
    `description` TEXT COMMENT '商品描述',
    `price` DECIMAL(10,2) NOT NULL COMMENT '价格',
    `original_price` DECIMAL(10,2) DEFAULT NULL COMMENT '原价',
    `stock` INT NOT NULL DEFAULT 0 COMMENT '库存',
    `sales` INT NOT NULL DEFAULT 0 COMMENT '销量',
    `main_image` VARCHAR(500) DEFAULT NULL COMMENT '主图URL',
    `is_hot` TINYINT NOT NULL DEFAULT 0 COMMENT '是否热销：0-否，1-是',
    `is_new` TINYINT NOT NULL DEFAULT 0 COMMENT '是否新品：0-否，1-是',
    `is_recommend` TINYINT NOT NULL DEFAULT 0 COMMENT '是否推荐：0-否，1-是',
    `status` TINYINT NOT NULL DEFAULT 1 COMMENT '状态：0-下架，1-上架',
    `deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    KEY `idx_category_id` (`category_id`),
    KEY `idx_name` (`name`),
    KEY `idx_status` (`status`),
    CONSTRAINT `fk_product_category` FOREIGN KEY (`category_id`) REFERENCES `tea_category` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='茶品商品表';

-- 商品图片表（1vN with tea_product）
CREATE TABLE IF NOT EXISTS `product_image` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '图片ID',
    `product_id` BIGINT NOT NULL COMMENT '商品ID',
    `image_url` VARCHAR(500) NOT NULL COMMENT '图片URL',
    `sort` INT DEFAULT 0 COMMENT '排序',
    `is_main` TINYINT NOT NULL DEFAULT 0 COMMENT '是否主图：0-否，1-是',
    `deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    KEY `idx_product_id` (`product_id`),
    CONSTRAINT `fk_image_product` FOREIGN KEY (`product_id`) REFERENCES `tea_product` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='商品图片表';

-- ==================== 购物模块 ====================

-- 购物车表（1vN with user & tea_product）
CREATE TABLE IF NOT EXISTS `cart` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '购物车ID',
    `user_id` BIGINT NOT NULL COMMENT '用户ID',
    `product_id` BIGINT NOT NULL COMMENT '商品ID',
    `quantity` INT NOT NULL DEFAULT 1 COMMENT '数量',
    `selected` TINYINT NOT NULL DEFAULT 1 COMMENT '是否选中：0-否，1-是',
    `deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    KEY `idx_user_id` (`user_id`),
    KEY `idx_product_id` (`product_id`),
    CONSTRAINT `fk_cart_user` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`),
    CONSTRAINT `fk_cart_product` FOREIGN KEY (`product_id`) REFERENCES `tea_product` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='购物车表';

-- 订单表（1vN with user）
CREATE TABLE IF NOT EXISTS `order_info` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '订单ID',
    `order_no` VARCHAR(50) NOT NULL COMMENT '订单编号',
    `user_id` BIGINT NOT NULL COMMENT '用户ID',
    `total_amount` DECIMAL(10,2) NOT NULL COMMENT '订单总金额',
    `pay_amount` DECIMAL(10,2) NOT NULL COMMENT '实付金额',
    `status` TINYINT NOT NULL DEFAULT 0 COMMENT '订单状态：0-待付款，1-已付款，2-已发货，3-已完成，4-已取消',
    `pay_time` DATETIME DEFAULT NULL COMMENT '支付时间',
    `receiver_name` VARCHAR(50) DEFAULT NULL COMMENT '收货人姓名',
    `receiver_phone` VARCHAR(20) DEFAULT NULL COMMENT '收货人手机',
    `receiver_address` VARCHAR(255) DEFAULT NULL COMMENT '收货地址',
    `remark` VARCHAR(500) DEFAULT NULL COMMENT '订单备注',
    `deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_order_no` (`order_no`),
    KEY `idx_user_id` (`user_id`),
    KEY `idx_status` (`status`),
    CONSTRAINT `fk_order_user` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='订单表';

-- 订单项表（NvM: order_info <-> tea_product）
CREATE TABLE IF NOT EXISTS `order_item` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '订单项ID',
    `order_id` BIGINT NOT NULL COMMENT '订单ID',
    `product_id` BIGINT NOT NULL COMMENT '商品ID',
    `product_name` VARCHAR(100) NOT NULL COMMENT '商品名称（快照）',
    `product_image` VARCHAR(500) DEFAULT NULL COMMENT '商品图片（快照）',
    `price` DECIMAL(10,2) NOT NULL COMMENT '商品单价（快照）',
    `quantity` INT NOT NULL DEFAULT 1 COMMENT '数量',
    `total_price` DECIMAL(10,2) NOT NULL COMMENT '小计',
    `deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    KEY `idx_order_id` (`order_id`),
    KEY `idx_product_id` (`product_id`),
    CONSTRAINT `fk_item_order` FOREIGN KEY (`order_id`) REFERENCES `order_info` (`id`),
    CONSTRAINT `fk_item_product` FOREIGN KEY (`product_id`) REFERENCES `tea_product` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='订单项表';

-- ==================== 收藏评论模块 ====================

-- 收藏表（1vN with user & tea_product）
CREATE TABLE IF NOT EXISTS `favorite` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '收藏ID',
    `user_id` BIGINT NOT NULL COMMENT '用户ID',
    `product_id` BIGINT NOT NULL COMMENT '商品ID',
    `deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_user_product` (`user_id`, `product_id`),
    CONSTRAINT `fk_favorite_user` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`),
    CONSTRAINT `fk_favorite_product` FOREIGN KEY (`product_id`) REFERENCES `tea_product` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='收藏表';

-- 评论表（1vN with user & tea_product）
CREATE TABLE IF NOT EXISTS `comment` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '评论ID',
    `user_id` BIGINT NOT NULL COMMENT '用户ID',
    `product_id` BIGINT NOT NULL COMMENT '商品ID',
    `order_id` BIGINT DEFAULT NULL COMMENT '订单ID（可选关联）',
    `content` TEXT NOT NULL COMMENT '评论内容',
    `rating` TINYINT NOT NULL DEFAULT 5 COMMENT '评分：1-5',
    `status` TINYINT NOT NULL DEFAULT 1 COMMENT '状态：0-隐藏，1-显示',
    `deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    KEY `idx_product_id` (`product_id`),
    KEY `idx_user_id` (`user_id`),
    CONSTRAINT `fk_comment_user` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`),
    CONSTRAINT `fk_comment_product` FOREIGN KEY (`product_id`) REFERENCES `tea_product` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='评论表';

-- ==================== 预约模块 ====================

-- 包间表
CREATE TABLE IF NOT EXISTS `room` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '包间ID',
    `name` VARCHAR(50) NOT NULL COMMENT '包间名称',
    `type` VARCHAR(20) NOT NULL COMMENT '包间类型：SMALL-小包间，MEDIUM-中包间，LARGE-大包间，VIP-贵宾间',
    `capacity` INT NOT NULL DEFAULT 2 COMMENT '容纳人数',
    `price_per_hour` DECIMAL(10,2) NOT NULL COMMENT '每小时价格',
    `description` TEXT COMMENT '包间描述',
    `image` VARCHAR(500) DEFAULT NULL COMMENT '包间图片',
    `facilities` VARCHAR(500) DEFAULT NULL COMMENT '设施（逗号分隔）',
    `status` TINYINT NOT NULL DEFAULT 1 COMMENT '状态：0-维护中，1-可用',
    `deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='包间表';

-- 包间预约表（1vN with room & user）
CREATE TABLE IF NOT EXISTS `room_booking` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '预约ID',
    `user_id` BIGINT NOT NULL COMMENT '用户ID',
    `room_id` BIGINT NOT NULL COMMENT '包间ID',
    `booking_date` DATE NOT NULL COMMENT '预约日期',
    `start_time` TIME NOT NULL COMMENT '开始时间',
    `end_time` TIME NOT NULL COMMENT '结束时间',
    `duration` INT NOT NULL COMMENT '时长（小时）',
    `total_price` DECIMAL(10,2) NOT NULL COMMENT '预约总价',
    `guest_count` INT NOT NULL DEFAULT 1 COMMENT '到店人数',
    `remark` VARCHAR(500) DEFAULT NULL COMMENT '备注',
    `status` TINYINT NOT NULL DEFAULT 0 COMMENT '状态：0-待确认，1-已确认，2-已完成，3-已取消',
    `deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    KEY `idx_user_id` (`user_id`),
    KEY `idx_room_id` (`room_id`),
    KEY `idx_booking_date` (`booking_date`),
    CONSTRAINT `fk_booking_user` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`),
    CONSTRAINT `fk_booking_room` FOREIGN KEY (`room_id`) REFERENCES `room` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='包间预约表';

-- ==================== 活动模块 ====================

-- 茶文化活动表
CREATE TABLE IF NOT EXISTS `tea_activity` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '活动ID',
    `title` VARCHAR(100) NOT NULL COMMENT '活动标题',
    `description` TEXT COMMENT '活动描述',
    `cover_image` VARCHAR(500) DEFAULT NULL COMMENT '活动封面图',
    `location` VARCHAR(255) NOT NULL COMMENT '活动地点',
    `start_time` DATETIME NOT NULL COMMENT '开始时间',
    `end_time` DATETIME NOT NULL COMMENT '结束时间',
    `max_participants` INT NOT NULL DEFAULT 50 COMMENT '最大参与人数',
    `current_participants` INT NOT NULL DEFAULT 0 COMMENT '当前报名人数',
    `fee` DECIMAL(10,2) DEFAULT 0.00 COMMENT '活动费用',
    `status` TINYINT NOT NULL DEFAULT 1 COMMENT '状态：0-取消，1-报名中，2-进行中，3-已结束',
    `deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    KEY `idx_status` (`status`),
    KEY `idx_start_time` (`start_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='茶文化活动表';

-- 活动报名表（NvM: user <-> tea_activity）
CREATE TABLE IF NOT EXISTS `activity_signup` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '报名ID',
    `activity_id` BIGINT NOT NULL COMMENT '活动ID',
    `user_id` BIGINT NOT NULL COMMENT '用户ID',
    `status` TINYINT NOT NULL DEFAULT 0 COMMENT '状态：0-已报名，1-已签到，2-已取消',
    `deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_user_activity` (`user_id`, `activity_id`),
    CONSTRAINT `fk_signup_activity` FOREIGN KEY (`activity_id`) REFERENCES `tea_activity` (`id`),
    CONSTRAINT `fk_signup_user` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='活动报名表';

-- ==================== 初始数据 ====================

-- 插入默认管理员（密码明文：admin123，仅本地演示用）
INSERT INTO `admin` (`username`, `password`, `name`, `role`, `status`) VALUES
('admin', 'admin123', '系统管理员', 'SUPER_ADMIN', 1);

-- 插入默认茶品分类
INSERT INTO `tea_category` (`name`, `parent_id`, `sort`, `description`) VALUES
('绿茶', 0, 1, '中国名优绿茶，清汤绿叶，鲜爽回甘'),
('红茶', 0, 2, '全发酵茶，红汤红叶，温润甜醇'),
('乌龙茶', 0, 3, '半发酵茶，七泡有余香，岩韵悠长'),
('白茶', 0, 4, '自然萎凋，不炒不揉，清甜淡雅'),
('黑茶', 0, 5, '后发酵茶，越陈越香，醇厚顺滑'),
('花茶', 0, 6, '花引茶香，茶花相融，芬芳怡人');

-- 插入示例茶品商品
INSERT INTO `tea_product` (`category_id`, `name`, `subtitle`, `description`, `price`, `original_price`, `stock`, `sales`, `main_image`, `is_hot`, `is_new`, `is_recommend`, `status`) VALUES
-- 绿茶
(1, '明前西湖龙井', '狮峰山下，明前头采', '西湖龙井产于杭州西湖龙井村周围的群山，以\"色绿、香郁、味甘、形美\"四绝著称。明前采摘的嫩芽，经过传统手工炒制，扁平挺秀，冲泡后汤色嫩绿明亮，豆香清雅，滋味鲜醇，回甘悠长。', 368.00, 480.00, 200, 1523, '/images/products/main/绿茶–西湖龙井.png', 1, 0, 1, 1),
(1, '洞庭碧螺春', '太湖东山，一嫩三鲜', '碧螺春产自苏州太湖洞庭山，条索纤细，卷曲如螺，满披白毫。以\"形美、色艳、香浓、味醇\"闻名，冲泡时白毫翻飞如雪浪，汤色碧绿清亮，花果香馥郁，滋味鲜爽生津。', 298.00, 380.00, 150, 1021, '/images/products/main/绿茶–碧螺春.png', 0, 0, 1, 1),
(1, '信阳毛尖', '豫南名茶，细圆光直', '信阳毛尖产自河南信阳大别山区，外形细、圆、光、直，多白毫。汤色嫩绿明亮，香气清高持久，滋味浓醇鲜爽，叶底嫩绿匀整。', 168.00, 208.00, 300, 768, '/images/products/main/绿茶–信阳毛尖.png', 0, 1, 0, 1),
-- 红茶
(2, '正山小种', '武夷桐木关，松烟香桂圆味', '正山小种是世界红茶的鼻祖，产自福建武夷山桐木关。传统工艺以松针熏制，干茶条索肥壮，色泽乌润。冲泡后松烟香与桂圆汤味交融，汤色橙红明亮，滋味醇厚甘甜。', 258.00, 328.00, 180, 1256, '/images/products/main/红茶-正山小种.jpg', 1, 0, 0, 1),
(2, '祁门红茶', '世界三大高香红茶之首', '祁门红茶产自安徽祁门，以\"祁门香\"闻名于世。条索紧细匀整，色泽乌润。冲泡后汤色红艳明亮，似花似蜜似果的复合香气扑鼻，滋味醇和甘爽。', 328.00, 398.00, 120, 897, '/images/products/main/红茶–祁门红茶.png', 1, 0, 1, 1),
(2, '云南滇红', '大叶种金芽，蜜香浓郁', '滇红产自云南凤庆，选用大叶种茶树一芽一叶精制。干茶肥硕紧实，金毫显露。汤色红浓明亮，蜜糖香高扬持久，滋味浓厚鲜爽，叶底红匀明亮。', 188.00, 238.00, 250, 654, '/images/products/main/红茶–滇红.png', 0, 1, 0, 1),
-- 乌龙茶
(3, '武夷岩茶·大红袍', '岩骨花香，茶中之王', '大红袍是武夷岩茶之王，产自武夷山核心产区。条索紧结，色泽绿褐鲜润。冲泡后汤色橙黄明亮，岩韵花香显著，七泡有余香，每一泡皆有不同层次。', 688.00, 880.00, 80, 432, '/images/products/main/乌龙茶–大红袍.png', 1, 0, 1, 1),
(3, '安溪铁观音', '七泡有余香，观音韵', '铁观音产自福建安溪，是乌龙茶中的极品。条索卷曲，肥壮圆结，色泽砂绿。冲泡后汤色金黄清澈，兰花香气馥郁持久，观音韵明显，回甘强劲。', 258.00, 328.00, 200, 1145, '/images/products/main/乌龙茶–铁观音.png', 1, 0, 0, 1),
(3, '凤凰单丛·鸭屎香', '十大香型，银花香韵', '凤凰单丛产自广东潮州凤凰山，鸭屎香因茶农为防止他人偷取而故意丑化得名，实为银花香型。条索紧结匀整，冲泡后汤色金黄明亮，花香高锐持久，滋味鲜爽回甘，山韵独特。', 398.00, 498.00, 100, 567, '/images/products/main/乌龙茶–黄金桂.png', 0, 1, 1, 1),
-- 白茶
(4, '福鼎白毫银针', '太姥山下，白如银雪', '白毫银针是白茶中的极品，产自福建福鼎太姥山。全以肥壮茶芽制成，满披白毫，如银似雪。汤色杏黄清澈，毫香蜜韵，滋味清甜鲜醇，久藏愈佳。', 428.00, 528.00, 100, 678, '/images/products/main/白茶–白毫银针.png', 1, 0, 1, 1),
(4, '福鼎白牡丹', '一芽二叶，花香清雅', '白牡丹以一芽二叶制成，形似花朵而得名。产自福建福鼎，干茶绿叶夹银毫，冲泡后如蓓蕾初放。汤色杏黄明亮，花香清幽，滋味醇和回甘。', 198.00, 258.00, 180, 543, '/images/products/main/白茶–白牡丹.jpg', 0, 0, 0, 1),
(4, '云南月光白', '月下采摘，清凉甘甜', '月光白产自云南景谷，因夜间采摘、月光下自然萎凋而得名。干茶叶面黑、叶背白，黑白相间。汤色金黄澄澈，蜜香清雅，滋味清甜柔和，带有薄荷清凉感。', 168.00, 208.00, 150, 389, '/images/products/main/白茶–荒野白茶.png', 0, 1, 0, 1),
-- 黑茶
(5, '云南普洱熟茶', '勐海发酵，陈香糯滑', '普洱熟茶产自云南西双版纳勐海，经渥堆发酵精制而成。饼面圆整，条索肥壮，色泽褐红。冲泡后汤色红浓透亮，陈香纯正，滋味醇厚顺滑，入口糯甜。', 198.00, 258.00, 500, 2134, '/images/products/main/黑茶–湖南安化黑茶.jpg', 1, 0, 1, 1),
(5, '安化黑茶·茯砖', '金花茂盛，菌香独特', '茯砖茶产自湖南安化，是黑茶中的珍品。砖面平整，棱角分明，内含茂盛\"金花\"（冠突散囊菌）。冲泡后汤色橙红透亮，菌花香与陈香交融，滋味醇和回甘。', 168.00, 208.00, 300, 876, '/images/products/main/黑茶–安化黑砖茶.jpg', 0, 0, 0, 1),
(5, '广西六堡茶', '槟榔香，越陈越佳', '六堡茶产自广西梧州六堡镇，以\"红、浓、陈、醇\"著称。条索粗壮，色泽黑褐。冲泡后汤色红浓明亮，槟榔香突出，滋味醇厚甘爽，具有独特的松烟味和陈香味。', 138.00, 178.00, 400, 654, '/images/products/main/黑茶–六堡茶.jpg', 0, 0, 0, 1),
-- 花茶
(6, '福州茉莉花茶·七窨', '七次窨制，花香入骨', '福州茉莉花茶选用优质绿茶为茶坯，以盛夏伏天茉莉花七次窨制而成。条索紧细匀整，冲泡后汤色浅黄明亮，茉莉花香鲜灵持久，滋味醇和鲜爽。', 228.00, 298.00, 180, 987, '/images/products/main/花茶–茉莉花茶.png', 1, 0, 1, 1),
(6, '桂花龙井', '春茶秋花，清雅相遇', '桂花龙井以明前龙井为底，以仲秋鲜桂花窨制。茶引花香，花增茶味，冲泡后汤色嫩绿清亮，桂花甜香与龙井豆香交融，滋味鲜爽甘醇。', 258.00, 328.00, 120, 567, '/images/products/main/花茶–桂花龙井.png', 0, 1, 0, 1),
(6, '玫瑰花茶', '平阴重瓣，玫瑰沁心', '选用山东平阴重瓣红玫瑰与云南大叶种绿茶合窨。干茶花整色艳，冲泡后玫瑰花在杯中绽放，汤色淡红清亮，玫瑰芬芳馥郁，滋味清甜温润。', 128.00, 168.00, 250, 1023, '/images/products/main/花茶–玫瑰红茶.png', 0, 0, 0, 1);

-- 插入商品详情图库
INSERT INTO `product_image` (`product_id`, `image_url`, `sort`, `is_main`) VALUES
(1, '/images/products/gallery/绿茶–西湖龙井-01.png', 0, 0),
(1, '/images/products/gallery/绿茶–西湖龙井-02.png', 1, 0),
(2, '/images/products/gallery/绿茶–碧螺春-01.png', 0, 0),
(2, '/images/products/gallery/绿茶–碧螺春-02.png', 1, 0),
(3, '/images/products/gallery/绿茶–信阳毛尖-01.png', 0, 0),
(3, '/images/products/gallery/绿茶–信阳毛尖-02.png', 1, 0),
(4, '/images/products/gallery/红茶-正山小种-01.jpg', 0, 0),
(4, '/images/products/gallery/红茶-正山小种-02.jpg', 1, 0),
(5, '/images/products/gallery/红茶–祁门红茶-01.png', 0, 0),
(5, '/images/products/gallery/红茶–祁门红茶-02.png', 1, 0),
(6, '/images/products/gallery/红茶–滇红-01.png', 0, 0),
(6, '/images/products/gallery/红茶–滇红-02.png', 1, 0),
(7, '/images/products/gallery/乌龙茶–大红袍-01.png', 0, 0),
(7, '/images/products/gallery/乌龙茶–大红袍-02.png', 1, 0),
(8, '/images/products/gallery/乌龙茶–铁观音-01.png', 0, 0),
(8, '/images/products/gallery/乌龙茶–铁观音-02.png', 1, 0),
(9, '/images/products/gallery/乌龙茶–黄金桂-01.png', 0, 0),
(9, '/images/products/gallery/乌龙茶–黄金桂-02.png', 1, 0),
(10, '/images/products/gallery/白茶–白毫银针-01.png', 0, 0),
(10, '/images/products/gallery/白茶–白毫银针-02.png', 1, 0),
(11, '/images/products/gallery/白茶–白牡丹-01.jpg', 0, 0),
(11, '/images/products/gallery/白茶–白牡丹-02.jpg', 1, 0),
(12, '/images/products/gallery/白茶–荒野白茶-01.png', 0, 0),
(12, '/images/products/gallery/白茶–荒野白茶-02.png', 1, 0),
(13, '/images/products/gallery/黑茶–湖南安化黑茶-01.jpg', 0, 0),
(13, '/images/products/gallery/黑茶–湖南安化黑茶-02.jpg', 1, 0),
(14, '/images/products/gallery/黑茶–安化黑砖茶-01.jpg', 0, 0),
(14, '/images/products/gallery/黑茶–安化黑砖茶-02.jpg', 1, 0),
(15, '/images/products/gallery/黑茶–六堡茶-01.jpg', 0, 0),
(15, '/images/products/gallery/黑茶–六堡茶-02.jpg', 1, 0),
(16, '/images/products/gallery/花茶–茉莉花茶-01.png', 0, 0),
(16, '/images/products/gallery/花茶–茉莉花茶-02.png', 1, 0),
(17, '/images/products/gallery/花茶–桂花龙井-01.png', 0, 0),
(17, '/images/products/gallery/花茶–桂花龙井-02.png', 1, 0),
(18, '/images/products/gallery/花茶–玫瑰红茶-01.png', 0, 0),
(18, '/images/products/gallery/花茶–玫瑰红茶-02.png', 1, 0);

-- 插入示例包间
INSERT INTO `room` (`name`, `type`, `capacity`, `price_per_hour`, `description`, `image`, `facilities`, `status`) VALUES
('听雨轩', 'SMALL', 2, 88.00, '临窗小室，可听窗外雨打芭蕉。适合一人独饮或二人对酌，静享茶中时光。', '/images/rooms/包间1.jpg', '茶台,茶具套装,檀香,独立空调', 1),
('清风阁', 'SMALL', 4, 128.00, '半开放式雅间，竹帘低垂，清风穿堂。好友小聚，品茗论道，不亦乐乎。', '/images/rooms/包间2.jpg', '茶台,茶具套装,投影仪,独立空调,古琴', 1),
('揽月台', 'MEDIUM', 6, 198.00, '宽敞明亮的中型茶室，落地窗外可远眺山景。商务洽谈与茶会皆宜。', '/images/rooms/包间3.jpg', '双茶台,茶具套装,投影仪,独立空调,香道器具', 1),
('松涛居', 'MEDIUM', 8, 258.00, '日式榻榻米风格茶室，窗外松林环抱。可举办小型茶会雅集。', '/images/rooms/包间1.jpg', '榻榻米,矮茶台,茶具套装,投影仪,独立空调', 1),
('龙井厅', 'LARGE', 16, 388.00, '中式古典大包间，可容纳16位宾客。红木茶桌与太师椅，适合商务宴请与大型茶会。', '/images/rooms/包间2.jpg', '红木茶台,茶具套装,投影仪,音响系统,独立空调,更衣间', 1),
('云茗堂', 'VIP', 12, 588.00, '茶馆最高规格贵宾室，全红木家具，名家字画环壁。配有专属茶艺师，提供顶级茶事体验。', '/images/rooms/包间3.jpg', '紫檀茶台,名家紫砂,香道器具,投影仪,音响系统,独立空调,休息室,专属茶艺师', 1);

-- 插入示例茶事活动
INSERT INTO `tea_activity` (`title`, `description`, `cover_image`, `location`, `start_time`, `end_time`, `max_participants`, `current_participants`, `fee`, `status`) VALUES
('春茶品鉴会 — 龙井专场', '清明时节，新茶初焙。我们特邀中国茶叶博物馆研究员为大家讲解龙井茶的品鉴之道。现场将品鉴明前、雨前、群体种三款龙井，学习观形、闻香、品味、辨底的专业方法。每位参与者将获赠50g明前龙井伴手礼一份。', '/images/activities/活动1.png', '云茗茶馆·松涛居', DATE_ADD(NOW(), INTERVAL 3 DAY), DATE_ADD(DATE_ADD(NOW(), INTERVAL 3 DAY), INTERVAL 2 HOUR), 24, 16, 198.00, 1),
('紫砂壶鉴赏与养壶入门', '紫砂壶，茶人必备之器。本期活动由宜兴紫砂工艺师亲临讲授紫砂泥料辨识、壶型审美、以及日常养壶之法。参与者可自带紫砂壶请老师点评，也可在现场选购宜兴原矿紫砂壶。', '/images/activities/活动 2.png', '云茗茶馆·揽月台', DATE_ADD(NOW(), INTERVAL 7 DAY), DATE_ADD(DATE_ADD(NOW(), INTERVAL 7 DAY), INTERVAL 3 HOUR), 30, 8, 128.00, 1),
('宋代点茶雅集', '回到宋朝，体验一场原汁原味的点茶雅集。活动现场穿着汉服，由茶艺师示范宋代七汤点茶法，学习击拂、分茶、斗茶之趣。更有茶百戏表演，以茶汤作画，妙趣横生。', '/images/activities/活动1.png', '云茗茶馆·云茗堂', DATE_ADD(NOW(), INTERVAL 14 DAY), DATE_ADD(DATE_ADD(NOW(), INTERVAL 14 DAY), INTERVAL 2 HOUR), 16, 12, 288.00, 1),
('茶与养生 — 中医视角', '特邀中医养生专家，从中医理论出发，讲解不同体质适合饮用何种茶类，以及四时饮茶养生之道。现场将根据参与者体质推荐适合的茶品，并演示养生茶调配方法。', '/images/activities/活动 2.png', '云茗茶馆·清风阁', DATE_ADD(NOW(), INTERVAL 5 DAY), DATE_ADD(DATE_ADD(NOW(), INTERVAL 5 DAY), INTERVAL 2 HOUR), 20, 5, 68.00, 1),
('少儿茶礼启蒙', '面向8-15岁少年的茶文化启蒙课程。学习基本茶礼、认识六大茶类、动手冲泡第一杯茶。培养孩子的专注力与礼仪修养，让茶文化代代相传。', '/images/activities/活动1.png', '云茗茶馆·听雨轩', DATE_ADD(NOW(), INTERVAL 10 DAY), DATE_ADD(DATE_ADD(DATE_ADD(NOW(), INTERVAL 10 DAY), INTERVAL 1 HOUR), INTERVAL 30 MINUTE), 12, 4, 168.00, 1);
