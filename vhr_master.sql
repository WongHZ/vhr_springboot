/*
 Navicat Premium Data Transfer

 Source Server         : localhost_3306
 Source Server Type    : MySQL
 Source Server Version : 80025
 Source Host           : localhost:3306
 Source Schema         : vhr_master

 Target Server Type    : MySQL
 Target Server Version : 80025
 File Encoding         : 65001

 Date: 29/11/2022 18:46:41
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for vhr_building
-- ----------------------------
DROP TABLE IF EXISTS `vhr_building`;
CREATE TABLE `vhr_building`  (
  `id` bigint(0) NOT NULL AUTO_INCREMENT COMMENT '楼栋ID',
  `community_name` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '所属小区名称',
  `community_id` bigint(0) NULL DEFAULT NULL COMMENT '所属小区ID',
  `name` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '栋数名称',
  `total_households` int(0) NULL DEFAULT NULL COMMENT '总户数',
  `create_time` timestamp(0) NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` timestamp(0) NOT NULL ON UPDATE CURRENT_TIMESTAMP(0) COMMENT '更新时间',
  `is_deleted` tinyint(1) NULL DEFAULT 0 COMMENT '逻辑删除，1为删除',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 57 CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '楼栋表' ROW_FORMAT = Compact;

-- ----------------------------
-- Records of vhr_building
-- ----------------------------
INSERT INTO `vhr_building` VALUES (2, '光大锦绣山河', 1, 'B栋', 37, '2022-09-29 20:27:27', '2022-10-02 12:34:58', 0);
INSERT INTO `vhr_building` VALUES (3, '光大锦绣山河', 1, 'C栋', 37, '2022-09-29 20:27:27', '2022-10-02 12:34:58', 0);
INSERT INTO `vhr_building` VALUES (4, '光大锦绣山河', 1, 'D栋', 37, '2022-09-29 20:27:27', '2022-10-02 12:34:58', 0);
INSERT INTO `vhr_building` VALUES (5, '光大锦绣山河', 1, 'E栋', 37, '2022-09-29 20:27:27', '2022-10-02 12:34:58', 0);
INSERT INTO `vhr_building` VALUES (12, '百花小区', 3, 'A栋', 34, '2022-10-01 16:46:58', '2022-10-01 16:46:58', 0);
INSERT INTO `vhr_building` VALUES (13, '百花小区', 3, 'B栋', 34, '2022-10-01 16:46:58', '2022-10-01 16:46:58', 0);
INSERT INTO `vhr_building` VALUES (14, '百花小区', 3, 'C栋', 34, '2022-10-01 16:46:58', '2022-10-01 16:46:58', 0);
INSERT INTO `vhr_building` VALUES (15, '百花小区', 3, 'D栋', 34, '2022-10-01 16:46:58', '2022-10-01 16:46:58', 0);
INSERT INTO `vhr_building` VALUES (16, '百花小区', 3, 'E栋', 34, '2022-10-01 16:46:58', '2022-10-01 16:46:58', 0);
INSERT INTO `vhr_building` VALUES (17, '万达小区', 1640017927, 'A栋', 37, '2022-10-01 16:50:20', '2022-10-02 12:30:23', 0);
INSERT INTO `vhr_building` VALUES (18, '万达小区', 1640017927, 'B栋', 37, '2022-10-01 16:50:20', '2022-10-02 12:30:23', 0);
INSERT INTO `vhr_building` VALUES (19, '万达小区', 1640017927, 'C栋', 37, '2022-10-01 16:50:20', '2022-10-02 12:30:23', 0);
INSERT INTO `vhr_building` VALUES (20, '万达小区', 1640017927, 'D栋', 37, '2022-10-01 16:50:20', '2022-10-02 12:30:23', 0);
INSERT INTO `vhr_building` VALUES (21, '万达小区', 1640017927, 'E栋', 37, '2022-10-01 16:50:20', '2022-10-02 12:30:23', 0);
INSERT INTO `vhr_building` VALUES (27, '肇庆碧桂园', 1640017930, 'A栋', 48, '2022-10-01 17:41:13', '2022-10-01 17:41:13', 0);
INSERT INTO `vhr_building` VALUES (28, '肇庆碧桂园', 1640017930, 'B栋', 48, '2022-10-01 17:41:13', '2022-10-01 17:41:13', 0);
INSERT INTO `vhr_building` VALUES (29, '肇庆碧桂园', 1640017930, 'C栋', 48, '2022-10-01 17:41:13', '2022-10-01 17:41:13', 0);
INSERT INTO `vhr_building` VALUES (30, '肇庆碧桂园', 1640017930, 'D栋', 48, '2022-10-01 17:41:13', '2022-10-01 17:41:13', 0);
INSERT INTO `vhr_building` VALUES (31, '肇庆碧桂园', 1640017930, 'E栋', 48, '2022-10-01 17:41:13', '2022-10-18 20:38:16', 0);
INSERT INTO `vhr_building` VALUES (57, '光大锦绣山河', 1, 'A栋', 37, '2022-10-02 12:05:07', '2022-10-02 12:34:58', 0);

-- ----------------------------
-- Table structure for vhr_car
-- ----------------------------
DROP TABLE IF EXISTS `vhr_car`;
CREATE TABLE `vhr_car`  (
  `id` bigint(0) NOT NULL AUTO_INCREMENT COMMENT '车辆ID',
  `picture` varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '车辆照片',
  `house_id` bigint(0) NULL DEFAULT NULL COMMENT '所属成员（业主）',
  `color` varchar(10) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '车辆颜色',
  `car_number` varchar(20) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '车牌号',
  `create_time` timestamp(0) NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` timestamp(0) NOT NULL ON UPDATE CURRENT_TIMESTAMP(0) COMMENT '更新时间',
  `is_deleted` tinyint(1) NULL DEFAULT 0 COMMENT '逻辑删除，1为删除',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 8 CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '车辆表' ROW_FORMAT = Compact;

-- ----------------------------
-- Records of vhr_car
-- ----------------------------
INSERT INTO `vhr_car` VALUES (1, 'carimage/car1.png', 1, '黄色', '粤H·23455', '2022-10-05 11:49:52', '2022-10-18 22:24:45', 0);
INSERT INTO `vhr_car` VALUES (2, 'carimage/car2.png', 2, '红色', '粤H·88888', '2022-10-05 11:49:52', '2022-10-18 22:18:13', 0);
INSERT INTO `vhr_car` VALUES (8, 'carimage/car2-f89f3d63-dc67-40d2-9f1f-80763b9a2fdf.png', 3, '红色', '粤H·66666', '2022-10-05 17:56:57', '2022-10-18 22:25:24', 0);

-- ----------------------------
-- Table structure for vhr_charge
-- ----------------------------
DROP TABLE IF EXISTS `vhr_charge`;
CREATE TABLE `vhr_charge`  (
  `charge_id` bigint(0) NOT NULL AUTO_INCREMENT COMMENT '收费项目ID',
  `community_id` bigint(0) NOT NULL COMMENT '所属小区ID',
  `code` varchar(20) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT '' COMMENT '收费编号',
  `name` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT '' COMMENT '项目名称',
  `create_time` timestamp(0) NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` timestamp(0) NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间',
  `money` decimal(10, 0) NULL DEFAULT 0 COMMENT '项目收费金额（年），单位分',
  `is_deleted` tinyint(1) NULL DEFAULT 0,
  PRIMARY KEY (`charge_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 8 CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '收费项目表' ROW_FORMAT = Compact;

-- ----------------------------
-- Records of vhr_charge
-- ----------------------------
INSERT INTO `vhr_charge` VALUES (1, 1, 'HXCARA001', '光大锦绣山河A栋停车位', '2022-10-25 20:33:48', '2022-10-25 20:33:48', 200000, 0);
INSERT INTO `vhr_charge` VALUES (2, 1, 'HXCARB001', '光大锦绣山河B栋停车位', '2022-10-25 20:34:29', '2022-10-25 20:34:29', 200000, 0);
INSERT INTO `vhr_charge` VALUES (3, 1, 'HXCARC001', '光大锦绣山河C栋停车位', '2022-10-25 20:34:39', '2022-10-25 20:34:39', 200000, 0);
INSERT INTO `vhr_charge` VALUES (4, 1, 'HXCARD001', '光大锦绣山河D栋停车位', '2022-10-25 20:34:44', '2022-10-25 20:34:44', 200000, 0);
INSERT INTO `vhr_charge` VALUES (5, 1, 'HXCARE001', '光大锦绣山河E栋停车位', '2022-10-25 20:34:53', '2022-10-25 20:34:53', 200000, 1);
INSERT INTO `vhr_charge` VALUES (6, 3, 'BHCARA001', '百花小区A栋停车位', '2022-10-26 10:42:01', '2022-10-26 10:48:05', 180001, 0);
INSERT INTO `vhr_charge` VALUES (7, 1, 'qwe', 'qwe', '2022-11-06 21:17:56', '2022-11-08 22:37:07', 1232, 0);
INSERT INTO `vhr_charge` VALUES (8, 4, '234', '234', '2022-11-06 21:30:15', '2022-11-06 21:30:15', 24, 0);

-- ----------------------------
-- Table structure for vhr_community
-- ----------------------------
DROP TABLE IF EXISTS `vhr_community`;
CREATE TABLE `vhr_community`  (
  `community_id` bigint(0) NOT NULL AUTO_INCREMENT COMMENT '小区id',
  `community_code` varchar(20) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '小区编号',
  `community_name` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '小区名称',
  `community_address` varchar(200) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '坐落地址',
  `community_area` decimal(10, 0) NULL DEFAULT NULL COMMENT '占地面积（m2）',
  `total_buildings` int(0) NULL DEFAULT NULL COMMENT '总栋数',
  `total_households` int(0) NULL DEFAULT NULL COMMENT '总户数',
  `greening_rate` int(0) NULL DEFAULT NULL COMMENT '绿化率（%）',
  `thumbnail` varchar(200) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '缩略图',
  `developer` varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '开发商名称',
  `estate_company` varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '物业公司名称',
  `create_time` timestamp(0) NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` timestamp(0) NULL DEFAULT NULL COMMENT '更新时间',
  `status` char(1) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT '0' COMMENT '状态:0-启用（默认），1-不启用',
  `manage` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '小区管理者',
  `is_deleted` tinyint(1) NULL DEFAULT 0 COMMENT '逻辑删除，1为删除',
  PRIMARY KEY (`community_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1640017935 CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '小区表' ROW_FORMAT = Compact;

-- ----------------------------
-- Records of vhr_community
-- ----------------------------
INSERT INTO `vhr_community` VALUES (1, 'HX19960101001', '光大锦绣山河', '肇庆市端州区宋城路龟顶山森林公园', 10801, 66, 2442, 30, 'cimage/tx.jpg', '肇庆市锦绣山河房地产开发有限公司', '东莞市光大物业管理有限公司', '2019-12-13 09:12:04', '2022-10-23 16:02:20', '0', '王先森', 0);
INSERT INTO `vhr_community` VALUES (3, 'BH19960101003', '百花小区', '历城区花园路7号', 44444, 88, 2992, 50, 'cimage/tx.jpg', '济南齐鲁化纤集团有限责任公司', '上海新长宁集团新华物业有限公司', '2020-12-13 09:24:42', '2022-09-28 11:21:40', '0', '桓少', 0);
INSERT INTO `vhr_community` VALUES (4, 'JY20160101001', '金域华府', '齐鲁软件园', 66666, 66, 2244, 35, 'cimage/tx.jpg', '万达地产集团', '万科物业', '2020-12-14 19:29:48', '2022-09-28 11:21:48', '0', '只因Kun', 0);
INSERT INTO `vhr_community` VALUES (1640017927, 'ZQ19981014', '万达小区', '肇庆市端州区万达广场', 9999, 66, 2442, 100, 'cimage/1-66396f5a-8b4e-4e93-8585-2281ea9c4fd9.png', '广东科技学院', '广东科技学院', '2022-09-27 17:33:10', '2022-10-02 12:30:23', '0', '王先森', 0);

-- ----------------------------
-- Table structure for vhr_complaint
-- ----------------------------
DROP TABLE IF EXISTS `vhr_complaint`;
CREATE TABLE `vhr_complaint`  (
  `complaint_id` bigint(0) NOT NULL AUTO_INCREMENT COMMENT '投诉ID',
  `community_id` bigint(0) NULL DEFAULT NULL COMMENT '所属小区ID',
  `person_id` bigint(0) NULL DEFAULT NULL COMMENT '投诉人员（业主）ID',
  `description` varchar(500) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT '' COMMENT '投诉具体描述',
  `create_time` timestamp(0) NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP(0) COMMENT '创建时间',
  `update_time` timestamp(0) NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间',
  `status` tinyint(1) NULL DEFAULT 0 COMMENT '状态：0-未受理，1-处理中（默认），2-已处理完毕',
  `is_deleted` tinyint(1) NULL DEFAULT 0,
  `admin_name` varchar(20) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT '',
  PRIMARY KEY (`complaint_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 8 CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '投诉表' ROW_FORMAT = Compact;

-- ----------------------------
-- Records of vhr_complaint
-- ----------------------------
INSERT INTO `vhr_complaint` VALUES (1, 1, 3, '垃圾桶很臭', '2022-10-25 15:20:58', '2022-10-25 15:08:38', 0, 0, '张三');
INSERT INTO `vhr_complaint` VALUES (2, 3, 2, '这路很烂！！', '2022-10-25 17:41:34', '2022-10-25 17:41:35', 1, 0, '李四');
INSERT INTO `vhr_complaint` VALUES (3, 1, 12, '我测你们码', '2022-10-25 15:21:50', '2022-10-25 15:21:43', 2, 0, '王先森');
INSERT INTO `vhr_complaint` VALUES (4, 1, 3, '测试', '2022-10-25 17:35:46', '2022-10-25 16:43:26', 2, 0, '张三');
INSERT INTO `vhr_complaint` VALUES (8, 4, 3, 'sfs', '2022-11-06 21:52:28', '2022-11-06 21:52:16', 2, 1, '王先森');

-- ----------------------------
-- Table structure for vhr_device
-- ----------------------------
DROP TABLE IF EXISTS `vhr_device`;
CREATE TABLE `vhr_device`  (
  `id` bigint(0) NOT NULL AUTO_INCREMENT COMMENT '设备ID',
  `community_id` bigint(0) NULL DEFAULT NULL COMMENT '所属小区ID',
  `code` varchar(20) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT '' COMMENT '设备编号',
  `name` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT '' COMMENT '设备名称',
  `brand` varchar(40) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT '' COMMENT '设备品牌',
  `unit_price` decimal(10, 0) NULL DEFAULT NULL COMMENT '购买单价(￥)，单位分',
  `num` int(0) NULL DEFAULT NULL COMMENT '购买数量',
  `expected_useful_life` int(0) NULL DEFAULT NULL COMMENT '预计使用年限(年)',
  `create_time` timestamp(0) NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP(0) COMMENT '创建时间',
  `update_time` timestamp(0) NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间',
  `is_deleted` tinyint(1) NULL DEFAULT 0 COMMENT '0正常，1删除',
  `type` varchar(20) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT '' COMMENT '类型：电子设备ELE，清洁设备CLE，公共设施PUB',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 16 CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '设备表' ROW_FORMAT = Compact;

-- ----------------------------
-- Records of vhr_device
-- ----------------------------
INSERT INTO `vhr_device` VALUES (1, 1, 'HXAELE2022102001', 'A栋一号电梯', '上海三菱', 200000, 1, 71, '2022-10-20 22:22:29', '2022-10-20 22:04:05', 0, 'ELE');
INSERT INTO `vhr_device` VALUES (3, 1, 'HXAELE2022102002', 'A栋二号电梯', '上海三菱', 200000, 1, 70, '2022-10-20 20:35:15', '2022-10-20 10:17:12', 0, 'ELE');
INSERT INTO `vhr_device` VALUES (4, 1, 'HXAELE2022102003', 'A栋三号电梯', '上海三菱', 200000, 1, 70, '2022-10-20 20:35:16', '2022-10-20 10:17:12', 0, 'ELE');
INSERT INTO `vhr_device` VALUES (5, 1, 'HXAELE2022102004', 'A栋四号电梯', '上海三菱', 200000, 1, 70, '2022-10-20 20:35:17', '2022-10-20 10:17:12', 0, 'ELE');
INSERT INTO `vhr_device` VALUES (6, 1, 'HXAELE2022102005', 'A栋五号电梯', '上海三菱', 200000, 1, 70, '2022-10-20 22:24:46', '2022-10-20 10:17:12', 1, 'ELE');
INSERT INTO `vhr_device` VALUES (7, 1, 'HXACLA2022102001', 'A栋垃圾箱', '大勤环保', 142, 15, 21, '2022-10-20 22:25:21', '2022-10-20 22:20:02', 0, 'CLE');
INSERT INTO `vhr_device` VALUES (8, 3, 'BHAELE2022102001', 'A栋一号电梯', '上海三菱', 200000, 1, 70, '2022-10-20 20:35:26', '2022-10-20 11:26:47', 0, 'ELE');
INSERT INTO `vhr_device` VALUES (9, 3, 'BHAELE2022102002', 'A栋二号电梯', '上海三菱', 200000, 1, 70, '2022-10-20 20:35:27', '2022-10-20 11:26:47', 0, 'ELE');
INSERT INTO `vhr_device` VALUES (10, 3, 'BHAELE2022102003', 'A栋三号电梯', '上海三菱', 200000, 1, 70, '2022-10-20 20:35:28', '2022-10-20 11:26:47', 0, 'ELE');
INSERT INTO `vhr_device` VALUES (11, 3, 'BHAELE2022102004', 'A栋四号电梯', '上海三菱', 200000, 1, 70, '2022-10-20 20:35:30', '2022-10-20 11:26:47', 0, 'ELE');
INSERT INTO `vhr_device` VALUES (12, 3, 'BHAELE2022102005', 'A栋五号电梯', '上海三菱', 200000, 1, 70, '2022-10-20 21:52:57', '2022-10-20 11:26:47', 1, 'ELE');
INSERT INTO `vhr_device` VALUES (13, 3, 'BHACLA2022102001', 'A栋垃圾箱', '大勤环保', 142, 15, 20, '2022-10-20 21:55:08', '2022-10-20 11:26:47', 0, 'CLE');
INSERT INTO `vhr_device` VALUES (14, 1, 'HXBELE20221020233', 'B栋一号电梯', '上海三菱', 200000, 1, 70, '2022-10-20 20:35:32', '2022-10-20 17:38:50', 1, 'ELE');
INSERT INTO `vhr_device` VALUES (15, 3, 'BHBCLA2022102001', 'B栋垃圾箱1', '大勤环保', 142, 15, 20, '2022-10-21 22:48:46', '2022-10-20 11:26:47', 0, 'CLE');
INSERT INTO `vhr_device` VALUES (16, 4, 'JYCPUB20221106803', '阿松大', '收到', 4324, 123, 23, '2022-11-06 21:59:42', '2022-11-06 21:59:34', 1, 'PUB');

-- ----------------------------
-- Table structure for vhr_estate_manager
-- ----------------------------
DROP TABLE IF EXISTS `vhr_estate_manager`;
CREATE TABLE `vhr_estate_manager`  (
  `id` int(0) NOT NULL AUTO_INCREMENT COMMENT '物业管理人员ID',
  `community_name` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '所属小区名称',
  `community_id` int(0) NULL DEFAULT NULL COMMENT '所属小区ID',
  `login_name` varchar(20) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '登录名:登录时使用的名称',
  `name` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '真实名称',
  `password` varchar(60) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '密码',
  `telephone` int(0) NULL DEFAULT NULL COMMENT '手机',
  `email` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '邮箱',
  `role_id` int(0) NULL DEFAULT 0 COMMENT '角色ID：0-普通用户（默认0），1-管理员用户',
  `create_time` timestamp(0) NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` timestamp(0) NOT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '物业管理人员表' ROW_FORMAT = Compact;

-- ----------------------------
-- Table structure for vhr_house
-- ----------------------------
DROP TABLE IF EXISTS `vhr_house`;
CREATE TABLE `vhr_house`  (
  `h_id` bigint(0) NOT NULL AUTO_INCREMENT COMMENT '房屋ID',
  `community_name` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '所属小区名称',
  `community_id` bigint(0) NULL DEFAULT NULL COMMENT '所属小区ID',
  `building_name` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '所属栋数名称',
  `h_code` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '房产编码',
  `h_name` varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '房产名称',
  `owner_id` int(0) NULL DEFAULT NULL COMMENT '户主（业主）ID',
  `owner_name` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '户主（业主）名称',
  `telephone` bigint(0) NULL DEFAULT NULL COMMENT '联系方式',
  `room_num` int(0) NULL DEFAULT NULL COMMENT '房间数',
  `unit` int(0) NULL DEFAULT NULL COMMENT '单元',
  `floor` int(0) NULL DEFAULT NULL COMMENT '楼层',
  `live_time` date NOT NULL COMMENT '入住时间',
  `is_deleted` tinyint(1) NULL DEFAULT 0 COMMENT '逻辑删除，1为删除',
  PRIMARY KEY (`h_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '房屋表' ROW_FORMAT = Compact;

-- ----------------------------
-- Records of vhr_house
-- ----------------------------
INSERT INTO `vhr_house` VALUES (1, '光大锦绣山河', 1, 'A栋', 'HM20191213003', '光大锦绣山河A栋9层3单元', 1, '王兆桓', 13169353121, 4, 3, 9, '2022-09-30', 0);
INSERT INTO `vhr_house` VALUES (2, '百花小区', 3, 'B栋', 'HM20220812000', '百花小区B栋5层1单元', 2, '蔡旭昆', 13118840448, 4, 1, 5, '2022-08-04', 0);
INSERT INTO `vhr_house` VALUES (3, '金域华府', 4, 'E栋', 'HM20191213001', '金域华府E栋16层4单元', 3, '悦刻5哥', 13169350448, 6, 4, 16, '2019-12-13', 0);
INSERT INTO `vhr_house` VALUES (5, '金域华府', 4, 'E栋', 'HM20191213000', '金域华府E栋16层4单元', 5, '悦刻5哥1', 13169350448, 6, 3, 16, '2019-12-13', 0);
INSERT INTO `vhr_house` VALUES (6, '百花小区', 3, 'B栋', 'HM20220812001', '百花小区B栋5层1单元', 6, '蔡旭昆2', 13118840448, 3, 3, 5, '2022-08-04', 0);
INSERT INTO `vhr_house` VALUES (10, '光大锦绣山河', 1, 'D栋', 'ddf2342534', '光大锦绣山河E栋1层1单元', 3, '悦刻5哥', 12323454323, 4, 1, 1, '2022-09-29', 1);

-- ----------------------------
-- Table structure for vhr_letter
-- ----------------------------
DROP TABLE IF EXISTS `vhr_letter`;
CREATE TABLE `vhr_letter`  (
  `id` int(0) NOT NULL AUTO_INCREMENT COMMENT '信件ID',
  `community_id` int(0) NULL DEFAULT NULL COMMENT '所属小区ID',
  `community_name` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '所属小区名称',
  `owner_id` int(0) NULL DEFAULT NULL COMMENT '信件发送者（业主）ID',
  `owner_name` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '信件发送者（业主）名称',
  `origin` char(1) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT '0' COMMENT '信件来源：0-信箱（默认），1-邮件，2-微信，3-公众号，4-app,5-pc',
  `title` varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '信件标题',
  `content` varchar(2000) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '信箱内容',
  `create_time` timestamp(0) NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` timestamp(0) NOT NULL COMMENT '更新时间',
  `status` char(1) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT '0' COMMENT '状态:0-未读（默认），1-已读',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '信件表' ROW_FORMAT = Compact;

-- ----------------------------
-- Table structure for vhr_parking
-- ----------------------------
DROP TABLE IF EXISTS `vhr_parking`;
CREATE TABLE `vhr_parking`  (
  `id` bigint(0) NOT NULL AUTO_INCREMENT COMMENT '车位ID',
  `community_id` bigint(0) NULL DEFAULT NULL COMMENT '所属小区ID',
  `code` varchar(20) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '车位编号',
  `name` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '车位名称',
  `create_time` timestamp(0) NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` timestamp(0) NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间',
  `is_deleted` tinyint(1) NULL DEFAULT 0 COMMENT '逻辑删除，1为删除',
  `status` tinyint(1) NULL DEFAULT 0 COMMENT '使用状态，0为未使用，1为使用中',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 121 CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '车位表' ROW_FORMAT = Compact;

-- ----------------------------
-- Records of vhr_parking
-- ----------------------------
INSERT INTO `vhr_parking` VALUES (1, 1, 'HXA001', '光大锦绣山河A栋001位', '2022-10-06 17:21:36', '2022-10-06 17:21:36', 0, 0);
INSERT INTO `vhr_parking` VALUES (2, 1, 'HXA002', '光大锦绣山河A栋002位', '2022-10-06 17:21:36', '2022-10-06 17:21:36', 0, 0);
INSERT INTO `vhr_parking` VALUES (3, 1, 'HXA003', '光大锦绣山河A栋003位', '2022-10-06 17:21:36', '2022-10-06 17:21:36', 0, 1);
INSERT INTO `vhr_parking` VALUES (4, 1, 'HXA004', '光大锦绣山河A栋004位', '2022-10-06 17:21:36', '2022-10-06 17:21:36', 0, 1);
INSERT INTO `vhr_parking` VALUES (5, 1, 'HXA005', '光大锦绣山河A栋005位', '2022-10-06 17:21:36', '2022-10-06 17:21:36', 0, 0);
INSERT INTO `vhr_parking` VALUES (6, 1, 'HXA006', '光大锦绣山河A栋006位', '2022-10-06 17:21:36', '2022-10-06 17:21:36', 0, 0);
INSERT INTO `vhr_parking` VALUES (7, 1, 'HXA007', '光大锦绣山河A栋007位', '2022-10-06 17:21:36', '2022-10-06 17:21:36', 0, 0);
INSERT INTO `vhr_parking` VALUES (8, 1, 'HXA008', '光大锦绣山河A栋008位', '2022-10-06 17:21:36', '2022-10-06 17:21:36', 0, 0);
INSERT INTO `vhr_parking` VALUES (9, 1, 'HXA008', '光大锦绣山河A栋009位', '2022-10-06 17:21:36', '2022-10-06 17:21:36', 0, 0);
INSERT INTO `vhr_parking` VALUES (10, 1, 'HXA010', '光大锦绣山河A栋010位', '2022-10-06 17:21:36', '2022-10-06 17:21:36', 0, 0);
INSERT INTO `vhr_parking` VALUES (11, 1, 'HXA011', '光大锦绣山河A栋011位', '2022-10-06 17:21:36', '2022-10-06 17:21:36', 0, 0);
INSERT INTO `vhr_parking` VALUES (12, 3, 'BHA001', '百花小区A栋001位', '2022-10-06 17:24:28', '2022-10-06 17:24:28', 0, 0);
INSERT INTO `vhr_parking` VALUES (13, 3, 'BHA002', '百花小区A栋002位', '2022-10-06 17:24:28', '2022-10-06 17:24:28', 0, 0);
INSERT INTO `vhr_parking` VALUES (14, 3, 'BHA003', '百花小区A栋003位', '2022-10-06 17:24:28', '2022-10-06 17:24:28', 0, 0);
INSERT INTO `vhr_parking` VALUES (15, 3, 'BHA004', '百花小区A栋004位', '2022-10-06 17:24:28', '2022-10-06 17:24:28', 0, 0);
INSERT INTO `vhr_parking` VALUES (16, 3, 'BHA005', '百花小区A栋005位', '2022-10-06 17:24:28', '2022-10-06 17:24:28', 0, 0);
INSERT INTO `vhr_parking` VALUES (17, 3, 'BHA006', '百花小区A栋006位', '2022-10-06 17:24:28', '2022-10-06 17:24:28', 0, 0);
INSERT INTO `vhr_parking` VALUES (18, 3, 'BHA007', '百花小区A栋007位', '2022-10-06 17:24:28', '2022-10-06 17:24:28', 0, 0);
INSERT INTO `vhr_parking` VALUES (19, 3, 'BHA008', '百花小区A栋008位', '2022-10-06 17:24:28', '2022-10-06 17:24:28', 0, 0);
INSERT INTO `vhr_parking` VALUES (20, 3, 'BHA009', '百花小区A栋009位', '2022-10-06 17:24:28', '2022-10-06 17:24:28', 0, 0);
INSERT INTO `vhr_parking` VALUES (21, 3, 'BHA010', '百花小区A栋010位', '2022-10-06 17:24:28', '2022-10-06 17:24:28', 0, 0);
INSERT INTO `vhr_parking` VALUES (22, 3, 'BHA011', '百花小区A栋011位', '2022-10-06 17:24:28', '2022-10-06 17:24:28', 0, 0);
INSERT INTO `vhr_parking` VALUES (23, 4, 'JYHFA001', '金域华府A栋001位', '2022-10-06 17:26:30', '2022-10-06 17:26:30', 0, 0);
INSERT INTO `vhr_parking` VALUES (24, 4, 'JYHFA002', '金域华府A栋002位', '2022-10-06 17:26:30', '2022-10-06 17:26:30', 0, 0);
INSERT INTO `vhr_parking` VALUES (25, 4, 'JYHFA003', '金域华府A栋003位', '2022-10-06 17:26:30', '2022-10-06 17:26:30', 0, 0);
INSERT INTO `vhr_parking` VALUES (26, 4, 'JYHFA004', '金域华府A栋004位', '2022-10-06 17:26:30', '2022-10-06 17:26:30', 0, 0);
INSERT INTO `vhr_parking` VALUES (27, 4, 'JYHFA005', '金域华府A栋005位', '2022-10-06 17:26:30', '2022-10-06 17:26:30', 0, 0);
INSERT INTO `vhr_parking` VALUES (43, 1, 'HXB001', '光大锦绣山河B栋001位', '2022-10-08 11:05:19', '2022-10-08 11:05:19', 0, 0);
INSERT INTO `vhr_parking` VALUES (44, 1, 'HXB002', '光大锦绣山河B栋002位', '2022-10-08 11:05:19', '2022-10-08 11:05:19', 0, 0);
INSERT INTO `vhr_parking` VALUES (45, 1, 'HXB003', '光大锦绣山河B栋003位', '2022-10-08 11:05:19', '2022-10-08 11:05:19', 0, 0);
INSERT INTO `vhr_parking` VALUES (46, 1, 'HXB004', '光大锦绣山河B栋004位', '2022-10-08 11:05:19', '2022-10-08 11:05:19', 0, 0);
INSERT INTO `vhr_parking` VALUES (47, 1, 'HXB005', '光大锦绣山河B栋005位', '2022-10-08 11:05:19', '2022-10-08 11:05:19', 0, 0);
INSERT INTO `vhr_parking` VALUES (121, 4, 'JYA001', '金域华府A栋001位', '2022-11-08 21:52:14', '2022-11-08 21:52:14', 0, 0);

-- ----------------------------
-- Table structure for vhr_parking_use
-- ----------------------------
DROP TABLE IF EXISTS `vhr_parking_use`;
CREATE TABLE `vhr_parking_use`  (
  `id` bigint(0) NOT NULL AUTO_INCREMENT COMMENT '车位使用ID',
  `community_id` bigint(0) NULL DEFAULT NULL COMMENT '所属小区ID',
  `parking_id` bigint(0) NULL DEFAULT NULL COMMENT '车位ID',
  `car_id` bigint(0) NULL DEFAULT NULL COMMENT '车辆ID',
  `personnel_id` bigint(0) NULL DEFAULT NULL COMMENT '车辆所有人ID',
  `use_type` char(1) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT '0' COMMENT '使用性质：0-购买(默认)，1-月租，2-年租',
  `total_fee` decimal(10, 0) NULL DEFAULT NULL COMMENT '总费用(￥)，以分为单位',
  `create_time` timestamp(0) NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` timestamp(0) NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间',
  `start_time` date NOT NULL COMMENT '（使用）开始时间',
  `end_time` date NOT NULL COMMENT '（使用）结束时间',
  `is_deleted` tinyint(1) NULL DEFAULT 0 COMMENT '逻辑删除，1为删除',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 4 CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '车位使用表' ROW_FORMAT = Compact;

-- ----------------------------
-- Records of vhr_parking_use
-- ----------------------------
INSERT INTO `vhr_parking_use` VALUES (1, 1, 3, 1, 1, '0', 100000, '2022-10-08 15:48:30', '2022-10-19 20:57:02', '2022-10-19', '2022-12-19', 0);
INSERT INTO `vhr_parking_use` VALUES (4, 1, 4, 2, 2, '1', 4000, '2022-10-08 21:31:14', '2022-11-08 22:05:47', '2022-10-08', '2022-10-08', 0);

-- ----------------------------
-- Table structure for vhr_paylist
-- ----------------------------
DROP TABLE IF EXISTS `vhr_paylist`;
CREATE TABLE `vhr_paylist`  (
  `pid` bigint(0) NOT NULL AUTO_INCREMENT COMMENT '收费明细ID',
  `community_id` bigint(0) NULL DEFAULT NULL COMMENT '所属小区ID',
  `charge_id` bigint(0) NULL DEFAULT NULL COMMENT '收费项目ID',
  `actual_money` decimal(10, 0) NULL DEFAULT NULL COMMENT '实收金额(￥)，单位分',
  `create_time` timestamp(0) NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP(0) COMMENT '创建时间',
  `update_time` timestamp(0) NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间',
  `person_id` bigint(0) NULL DEFAULT NULL,
  `description` varchar(200) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT '',
  `is_deleted` tinyint(1) UNSIGNED ZEROFILL NULL DEFAULT 0,
  `admin_name` varchar(10) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT '',
  PRIMARY KEY (`pid`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 4 CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '收费名细表' ROW_FORMAT = Compact;

-- ----------------------------
-- Records of vhr_paylist
-- ----------------------------
INSERT INTO `vhr_paylist` VALUES (1, 1, 1, 100002, '2022-10-26 21:10:10', '2022-10-26 21:10:11', 1, '光大锦绣山河A栋停车位3号', 0, '王先森');
INSERT INTO `vhr_paylist` VALUES (2, 1, 1, 100000, '2022-10-26 15:41:59', '2022-10-26 15:05:54', 2, '光大锦绣山河A栋停车位4号', 0, '桓少');
INSERT INTO `vhr_paylist` VALUES (3, 1, 1, 230001, '2022-11-09 20:25:05', '2022-11-09 20:25:05', 3, '这个人很拽很有钱，多收3000.', 0, '王先森');
INSERT INTO `vhr_paylist` VALUES (4, 3, 6, 12, '2022-11-09 20:39:41', '2022-11-09 20:39:41', 3, '12', 0, '王先森');

-- ----------------------------
-- Table structure for vhr_personnel
-- ----------------------------
DROP TABLE IF EXISTS `vhr_personnel`;
CREATE TABLE `vhr_personnel`  (
  `p_id` bigint(0) NOT NULL AUTO_INCREMENT COMMENT '业主ID',
  `community_id` bigint(0) NULL DEFAULT NULL COMMENT '所属小区',
  `house_id` int(0) NULL DEFAULT NULL COMMENT '所属房产',
  `name` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '成员名称',
  `picture` varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '成员照片',
  `idcard` varchar(18) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '身份证号',
  `telephone` bigint(0) NULL DEFAULT NULL COMMENT '联系方式',
  `sex` varchar(4) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT '0' COMMENT '性别:0-男（默认），1-女',
  `type` varchar(4) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT '0' COMMENT '类型:0-房主（默认），1-成员，2-租客',
  `create_time` timestamp(0) NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` timestamp(0) NOT NULL COMMENT '更新时间',
  `birthday` date NOT NULL COMMENT '出生日期',
  `is_deleted` tinyint(1) NULL DEFAULT 0 COMMENT '逻辑删除，1为删除',
  PRIMARY KEY (`p_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '业主表' ROW_FORMAT = Compact;

-- ----------------------------
-- Records of vhr_personnel
-- ----------------------------
INSERT INTO `vhr_personnel` VALUES (1, 1, 1, '王兆桓', 'pimage/tx.jpg', '441202111111110002', 13169353124, '0', '0', '2022-09-29 11:18:13', '2022-10-19 21:51:53', '2022-10-19', 0);
INSERT INTO `vhr_personnel` VALUES (2, 3, 2, '蔡旭昆', 'pimage/cxk.png', '441202111111110001', 13118840448, '1', '1', '2022-09-29 11:20:24', '2022-09-29 11:18:13', '2022-09-29', 0);
INSERT INTO `vhr_personnel` VALUES (3, 4, 3, '悦刻5哥', 'pimage/dz.png', '441202111111110002', 13169350448, '1', '0', '2022-09-29 11:21:41', '2022-09-29 11:18:13', '2022-09-29', 0);
INSERT INTO `vhr_personnel` VALUES (4, 3, 2, '蔡旭昆1', 'pimage/cxk.png', '441202111111110001', 13118840448, '0', '1', '2022-09-29 11:20:24', '2022-09-29 11:18:13', '2022-09-29', 0);
INSERT INTO `vhr_personnel` VALUES (5, 4, 3, '悦刻5哥1', 'pimage/dz.png', '441202111111110002', 13169350448, '1', '0', '2022-09-29 11:21:41', '2022-09-29 11:18:13', '2022-09-29', 0);
INSERT INTO `vhr_personnel` VALUES (6, 3, 2, '蔡旭昆2', 'pimage/cxk.png', '441202111111110001', 13118840448, '1', '1', '2022-09-29 11:20:24', '2022-09-29 11:18:13', '2022-09-29', 0);
INSERT INTO `vhr_personnel` VALUES (12, 1, 1, '王兆', 'pimage/tx.jpg', '441202111111110000', 13169353122, '0', '2', '2022-09-29 11:18:13', '2022-09-29 11:18:13', '1998-10-04', 0);
INSERT INTO `vhr_personnel` VALUES (32, 1, 1, '阿萨', 'pimage/dz-ac6259ec-3ffd-4721-aabb-5c3853a6c95a-4035b64f-8e5b-484d-969f-83536d250925.png', '111111111111111111', 11111111111, '1', '2', '2022-10-03 16:55:40', '2022-10-03 16:55:40', '2022-10-03', 0);

-- ----------------------------
-- Table structure for vhr_repair
-- ----------------------------
DROP TABLE IF EXISTS `vhr_repair`;
CREATE TABLE `vhr_repair`  (
  `id` bigint(0) NOT NULL AUTO_INCREMENT COMMENT '维修ID',
  `community_id` bigint(0) NULL DEFAULT NULL COMMENT '所属小区ID',
  `person_id` bigint(0) NULL DEFAULT NULL COMMENT '报修人员（业主）ID',
  `device_id` bigint(0) NULL DEFAULT NULL COMMENT '设备编号ID',
  `description` varchar(500) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT '' COMMENT '报修描述',
  `create_time` timestamp(0) NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` timestamp(0) NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间',
  `status` tinyint(1) NULL DEFAULT 0 COMMENT '状态：0-待受理，1-已受理，2-修理完毕',
  `admin_name` varchar(20) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT '' COMMENT '管理员id',
  `is_deleted` tinyint(0) NULL DEFAULT 0,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 12 CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '维修表' ROW_FORMAT = Compact;

-- ----------------------------
-- Records of vhr_repair
-- ----------------------------
INSERT INTO `vhr_repair` VALUES (1, 3, 2, 10, 'A栋三号电梯按钮不灵', '2022-10-21 10:24:40', '2022-11-09 20:52:41', 1, '桓少', 0);
INSERT INTO `vhr_repair` VALUES (2, 1, 1, 7, 'A栋垃圾箱爆了', '2022-10-21 11:46:18', '2022-10-21 11:46:18', 1, '王先森', 0);
INSERT INTO `vhr_repair` VALUES (3, 1, 6, 15, 'B栋垃圾箱爆了', '2022-10-21 11:50:17', '2022-10-21 11:50:17', 2, '王先森', 0);
INSERT INTO `vhr_repair` VALUES (4, 1, 3, 1, '不太稳', '2022-10-22 17:53:47', '2022-10-25 15:00:30', 2, '王先森', 0);
INSERT INTO `vhr_repair` VALUES (5, 3, 3, 3, '43', '2022-10-22 17:57:09', '2022-10-22 17:57:09', 0, '王先森', 1);
INSERT INTO `vhr_repair` VALUES (6, 1, 3, 1, '34', '2022-10-22 18:03:03', '2022-10-22 18:03:03', 0, '王先森', 1);
INSERT INTO `vhr_repair` VALUES (7, 1, 3, 1, '432', '2022-10-22 18:03:35', '2022-10-22 18:03:35', 0, '王先森', 1);
INSERT INTO `vhr_repair` VALUES (8, 1, 3, 1, '32', '2022-10-22 18:04:23', '2022-10-22 18:04:23', 0, '王先森', 1);
INSERT INTO `vhr_repair` VALUES (9, 3, 3, 1, '发士大夫', '2022-10-22 18:06:46', '2022-10-22 18:06:46', 0, '王先森', 1);
INSERT INTO `vhr_repair` VALUES (10, 1, 3, 1, '432423', '2022-10-22 18:13:47', '2022-10-22 18:13:47', 0, '王先森', 1);
INSERT INTO `vhr_repair` VALUES (11, 1, 3, 3, '萨达萨达是睇睇', '2022-11-09 21:01:45', '2022-11-09 21:14:59', 0, '王先森', 0);
INSERT INTO `vhr_repair` VALUES (12, 1, 3, 1, '发射点犯得上', '2022-11-09 22:02:37', '2022-11-09 22:02:37', 0, '王先森', 0);

SET FOREIGN_KEY_CHECKS = 1;
