/*
 Navicat Premium Data Transfer

 Source Server         : 本地
 Source Server Type    : MySQL
 Source Server Version : 80406 (8.4.6)
 Source Host           : localhost:3306
 Source Schema         : ggy_picture

 Target Server Type    : MySQL
 Target Server Version : 80406 (8.4.6)
 File Encoding         : 65001

 Date: 05/10/2025 19:24:10
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for chat_content
-- ----------------------------
DROP TABLE IF EXISTS `chat_content`;
CREATE TABLE `chat_content` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '编号',
  `chatId` varchar(255) DEFAULT NULL COMMENT '房间 id',
  `resultUrl` varchar(255) DEFAULT NULL COMMENT 'html 连接',
  `userId` bigint DEFAULT NULL COMMENT '创建用户id',
  `tokenQuantity` bigint NOT NULL DEFAULT '0' COMMENT 'token 数量',
  `resultContent` text COMMENT '返回的内容',
  `choiceContent` varchar(255) DEFAULT NULL COMMENT '用户选择内容',
  `editTime` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '编辑时间',
  `createTime` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updateTime` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `isDelete` tinyint NOT NULL DEFAULT '0' COMMENT '是否删除',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1757776410773 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='对话内容表';

-- ----------------------------
-- Table structure for consume_statistic
-- ----------------------------
DROP TABLE IF EXISTS `consume_statistic`;
CREATE TABLE `consume_statistic` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '编号',
  `aiServiceType` varchar(255) DEFAULT NULL COMMENT 'AI服务类型 (IMAGE_ANALYSIS: 图像解析, TEXT_CHAT: 文字聊天)',
  `chatId` varchar(255) DEFAULT NULL COMMENT '会话 id',
  `totalTokens` bigint NOT NULL DEFAULT '0' COMMENT '总消耗 tokens',
  `promptTokens` bigint NOT NULL DEFAULT '0' COMMENT '提示词（输入）的 token',
  `completionTokens` bigint NOT NULL DEFAULT '0' COMMENT '完成任务(产出)的 token',
  `userId` bigint DEFAULT NULL COMMENT '创建用户id',
  `editTime` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '编辑时间',
  `createTime` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updateTime` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `isDelete` tinyint NOT NULL DEFAULT '0' COMMENT '是否删除',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=13 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='token统计表';

-- ----------------------------
-- Table structure for feedback_message
-- ----------------------------
DROP TABLE IF EXISTS `feedback_message`;
CREATE TABLE `feedback_message` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '编号',
  `chatId` varchar(255) DEFAULT NULL COMMENT '会话 id',
  `messageType` tinyint DEFAULT NULL COMMENT '用户选择类型，0正常复制，1不满意反馈',
  `feedBackMessage` varchar(1000) DEFAULT NULL COMMENT '反馈内容',
  `resultStructure` varchar(1000) DEFAULT NULL COMMENT '用户最终选择内容',
  `userId` bigint DEFAULT NULL COMMENT '创建用户id',
  `editTime` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '编辑时间',
  `createTime` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updateTime` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `isDelete` tinyint NOT NULL DEFAULT '0' COMMENT '是否删除',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=22 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='生成内容反馈表';

-- ----------------------------
-- Table structure for image_analysis
-- ----------------------------
DROP TABLE IF EXISTS `image_analysis`;
CREATE TABLE `image_analysis` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '编号',
  `chatId` varchar(255) DEFAULT NULL COMMENT '房间 id',
  `imageTxt` text NOT NULL COMMENT '图片内容',
  `imagePath` varchar(1000) DEFAULT NULL COMMENT '文件路径',
  `tokenQuantity` bigint NOT NULL DEFAULT '0' COMMENT 'token 数量',
  `userId` bigint DEFAULT NULL COMMENT '创建用户id',
  `editTime` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '编辑时间',
  `createTime` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updateTime` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `isDelete` tinyint NOT NULL DEFAULT '0' COMMENT '是否删除',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1757825048451 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='图片解析信息表';

SET FOREIGN_KEY_CHECKS = 1;
