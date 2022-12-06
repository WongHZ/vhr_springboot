/*
 Navicat Premium Data Transfer

 Source Server         : localhost_3306
 Source Server Type    : MySQL
 Source Server Version : 80025
 Source Host           : localhost:3306
 Source Schema         : vhr_login

 Target Server Type    : MySQL
 Target Server Version : 80025
 File Encoding         : 65001

 Date: 29/11/2022 18:46:49
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for authority
-- ----------------------------
DROP TABLE IF EXISTS `authority`;
CREATE TABLE `authority`  (
  `a_id` bigint(0) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `a_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '权限名',
  `url` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '类型为页面时，代表前端路由地址，类型为按钮时，代表后端接口地址',
  `type` tinyint(0) NOT NULL DEFAULT 0 COMMENT '权限类型，页面不可删除-0，可删除按钮-1，特殊-2',
  `permission` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '权限表达式',
  `method` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '后端接口访问方式',
  `description` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT 'url描述',
  `status` tinyint(0) NOT NULL DEFAULT 1 COMMENT '此规则状态，0为关闭，1为开启',
  PRIMARY KEY (`a_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 53 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '权限表' ROW_FORMAT = Compact;

-- ----------------------------
-- Records of authority
-- ----------------------------
INSERT INTO `authority` VALUES (1, 'superadmin', '/**', 2, 'btn:superaadmin', 'GET', '超级管理员，GET最高权限', 1);
INSERT INTO `authority` VALUES (2, 'admin', '/communitylist', 1, 'btn:admin', 'GET', '普通管理员，获取小区数据列表', 1);
INSERT INTO `authority` VALUES (3, 'admin', '/welcome', 0, 'btn:admin', 'GET', '普通管理员，获取主页中的数据页', 1);
INSERT INTO `authority` VALUES (4, 'admin', '/static/**', 0, 'btn:admin', 'GET', '普通管理员，获取静态资源，不可删除', 1);
INSERT INTO `authority` VALUES (5, 'admin', '/css/**', 0, 'btn:admin', 'GET', '普通管理员，获取静态资源，不可删除', 1);
INSERT INTO `authority` VALUES (6, 'admin', '/js/**', 0, 'btn:admin', 'GET', '普通管理员，获取静态资源，不可删除', 1);
INSERT INTO `authority` VALUES (7, 'admin', '/lib/**', 0, 'btn:admin', 'GET', '普通管理员，获取静态资源，不可删除', 1);
INSERT INTO `authority` VALUES (8, 'admin', '/index', 0, 'btn:admin', 'GET', '普通管理员，获取主页', 1);
INSERT INTO `authority` VALUES (10, 'superadmin', '/**', 2, 'btn:superaadmin', 'POST', '超级管理员，POST最高权限', 1);
INSERT INTO `authority` VALUES (12, 'admin', '/houselist', 1, 'btn:admin', 'GET', '普通管理员，获取个人房产数据列表', 1);
INSERT INTO `authority` VALUES (13, 'admin', '/buildinglist', 1, 'btn:admin', 'GET', '普通管理员，获取栋数的数据列表', 1);
INSERT INTO `authority` VALUES (14, 'admin', '/personnel_list', 1, 'btn:admin', 'GET', '普通管理员，获取人员的数据列表', 1);
INSERT INTO `authority` VALUES (15, 'admin', '/vehicle_list', 1, 'btn:admin', 'GET', '普通管理员，获取车辆管理的数据列表', 1);
INSERT INTO `authority` VALUES (16, 'admin', '/parkinglist', 1, 'btn:admin', 'GET', '普通管理员，获取车位管理的数据列表', 1);
INSERT INTO `authority` VALUES (17, 'admin', '/parkingusagelist', 1, 'btn:admin', 'GET', '普通管理员，获取车位使用管理的数据列表', 1);
INSERT INTO `authority` VALUES (24, 'admin', '/uimage/**', 0, 'btn:admin', 'GET', '普通管理员，获取图片静态资源', 1);
INSERT INTO `authority` VALUES (25, 'admin', '/pimage/**', 0, 'btn:admin', 'GET', '普通管理员，获取图片静态资源', 1);
INSERT INTO `authority` VALUES (26, 'admin', '/carimage/**', 0, 'btn:admin', 'GET', '普通管理员，获取图片静态资源', 1);
INSERT INTO `authority` VALUES (27, 'admin', '/cimage/**', 0, 'btn:admin', 'GET', '普通管理员，获取图片静态资源', 1);
INSERT INTO `authority` VALUES (34, 'admin', '/houseadd', 1, 'btn:admin', 'GET', '普通管理员，个人房产的新增功能', 1);
INSERT INTO `authority` VALUES (35, 'admin', '/houseaddData', 1, 'btn:admin', 'POST', '普通管理员，个人房产的新增功能_POST', 1);
INSERT INTO `authority` VALUES (36, 'admin', '/houseupdate', 1, 'btn:admin', 'GET', '普通管理员，个人房产的修改功能', 1);
INSERT INTO `authority` VALUES (37, 'admin', '/houseupData', 1, 'btn:admin', 'POST', '普通管理员，个人房产的修改功能_POST', 0);
INSERT INTO `authority` VALUES (43, 'admin', '/parkingstatus', 1, 'btn:admin', 'POST', '普通管理员，车位的状态修改功能', 1);
INSERT INTO `authority` VALUES (44, 'admin', '/myinfo', 0, 'btn:admin', 'GET', '普通管理员，个人设置', 1);
INSERT INTO `authority` VALUES (45, 'admin', 'parkinguseadd', 1, 'btn:admin', 'GET', '普通管理员，车位使用的新增功能', 1);
INSERT INTO `authority` VALUES (46, 'admin', '/parkinguseaddData', 1, 'btn:admin', 'POST', '普通管理员，车位使用的新增功能_POST', 1);
INSERT INTO `authority` VALUES (47, 'admin', '/changeinfo', 0, 'btn:admin', 'POST', '普通管理员，个人设置修改', 1);
INSERT INTO `authority` VALUES (48, 'admin', '/passmatch', 0, 'btn:admin', 'POST', '普通管理员，个人设置密码匹对', 1);
INSERT INTO `authority` VALUES (49, 'admin', '/changepass', 0, 'btn:admin', 'POST', '普通管理员，个人设置密码修改', 1);
INSERT INTO `authority` VALUES (50, 'admin', '/parkingusedel', 1, 'btn:admin', 'POST', '普通管理员，车位使用的删除功能', 1);
INSERT INTO `authority` VALUES (51, 'admin', '/denied', 0, 'btn:admin', 'GET', '', 1);
INSERT INTO `authority` VALUES (52, 'admin', 'park_add', 1, 'btn:admin', 'GET', '普通管理员，车位的新增功能', 1);
INSERT INTO `authority` VALUES (53, 'admin', '/park_addData', 1, 'btn:admin', 'POST', '普通管理员，车位的新增功能_POST', 1);

-- ----------------------------
-- Table structure for role
-- ----------------------------
DROP TABLE IF EXISTS `role`;
CREATE TABLE `role`  (
  `r_id` bigint(0) NOT NULL AUTO_INCREMENT,
  `r_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `creator_id` bigint(0) NOT NULL,
  `create_time` datetime(0) NULL DEFAULT NULL,
  `update_time` datetime(0) NULL DEFAULT NULL,
  PRIMARY KEY (`r_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Compact;

-- ----------------------------
-- Records of role
-- ----------------------------
INSERT INTO `role` VALUES (1, '超级管理员', 1, '2022-06-11 21:03:26', '2022-06-11 21:03:26');
INSERT INTO `role` VALUES (2, '普通管理员', 2, '2022-09-26 11:32:26', '2022-09-26 11:32:28');

-- ----------------------------
-- Table structure for role_authority
-- ----------------------------
DROP TABLE IF EXISTS `role_authority`;
CREATE TABLE `role_authority`  (
  `id` bigint(0) NOT NULL AUTO_INCREMENT,
  `role_id` bigint(0) NOT NULL,
  `authority_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 2 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Compact;

-- ----------------------------
-- Records of role_authority
-- ----------------------------
INSERT INTO `role_authority` VALUES (1, 1, 'superadmin');
INSERT INTO `role_authority` VALUES (2, 2, 'admin');

-- ----------------------------
-- Table structure for user
-- ----------------------------
DROP TABLE IF EXISTS `user`;
CREATE TABLE `user`  (
  `u_id` bigint(0) NOT NULL AUTO_INCREMENT,
  `u_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `password` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `email` varchar(75) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `phone` bigint(0) NULL DEFAULT NULL,
  `image` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
  `status` tinyint(1) NULL DEFAULT 1 COMMENT '用户状态，0为禁用，1为启动',
  PRIMARY KEY (`u_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 11 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci ROW_FORMAT = Compact;

-- ----------------------------
-- Records of user
-- ----------------------------
INSERT INTO `user` VALUES (1, '王先森', '$2a$10$rd1n0BgonhkP7aN2.fwUAOQ5hc89Kxnm38y6wlS1Xz32ADgtvXt2i', '2277012678@qq.com', 13111111125, 'uimage/sbdz-ea05acce-e2da-4e5b-b9f4-47f75b2dcaf7.png', 1);
INSERT INTO `user` VALUES (2, '桓少', '$2a$10$hq02Ng/BdGS1AZ1QA1YpJekPRCwXln4QVX1AzYmtkBEL4lMs4x4Wu', '429509226@qq.com', 13111111112, 'uimage/cxk.png', 1);
INSERT INTO `user` VALUES (3, '李四', '$2a$10$fYE64.VwZAzP3Aa6uuEwU.p3GmtC7tLEKluKohpYfmUyJVfWwcWMy', '429509226@qq.com', 13111111113, 'uimage/cxk.png', 0);
INSERT INTO `user` VALUES (4, '张三', '$2a$10$tRYuJsxZSO0rQO/0VosEgOyKJFlvKhGEJBvebq8gll1ub3.omdGa2', '1277012678@qq.com', 13111111115, 'uimage/dz.png', 1);

-- ----------------------------
-- Table structure for user_role
-- ----------------------------
DROP TABLE IF EXISTS `user_role`;
CREATE TABLE `user_role`  (
  `id` bigint(0) NOT NULL AUTO_INCREMENT,
  `user_id` bigint(0) NOT NULL,
  `role_id` bigint(0) NOT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 8 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Compact;

-- ----------------------------
-- Records of user_role
-- ----------------------------
INSERT INTO `user_role` VALUES (1, 1, 1);
INSERT INTO `user_role` VALUES (3, 3, 2);
INSERT INTO `user_role` VALUES (5, 4, 2);
INSERT INTO `user_role` VALUES (8, 2, 2);

SET FOREIGN_KEY_CHECKS = 1;
