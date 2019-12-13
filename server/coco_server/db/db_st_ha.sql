/*
 Navicat Premium Data Transfer

 Source Server         : aws_feisuo
 Source Server Type    : MySQL
 Source Server Version : 50640
 Source Host           : jsfsdb.c3f3fvmsy1ef.rds.cn-northwest-1.amazonaws.com.cn:3306
 Source Schema         : db_st_ha

 Target Server Type    : MySQL
 Target Server Version : 50640
 File Encoding         : 65001

 Date: 26/11/2019 09:47:48
*/
DROP DATABASE IF EXISTS db_st_ha;
CREATE DATABASE db_st_ha DEFAULT CHARSET UTF8 COLLATE UTF8_GENERAL_CI;

USE db_st_ha;

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for tb_authcode
-- ----------------------------
DROP TABLE IF EXISTS `tb_authcode`;
CREATE TABLE `tb_authcode` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `authcode` varchar(45) NOT NULL COMMENT '验证码',
  `atime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '过期时间',
  `ctime` timestamp NULL DEFAULT NULL COMMENT '当前时间',
  `mobile` varchar(45) NOT NULL COMMENT '手机号',
  PRIMARY KEY (`id`),
  UNIQUE KEY `id_UNIQUE` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=utf8 COMMENT='短信验证表';

-- ----------------------------
-- Table structure for tb_client
-- ----------------------------
DROP TABLE IF EXISTS `tb_client`;
CREATE TABLE `tb_client` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `name` varchar(32) NOT NULL,
  `label` varchar(32) DEFAULT NULL,
  `dscp` varchar(128) NOT NULL DEFAULT '<no-description>',
  `flag` int(10) unsigned NOT NULL DEFAULT '0',
  `icon_ts` bigint(20) unsigned NOT NULL DEFAULT '0',
  `phone` varchar(32) DEFAULT NULL,
  `mac_addr` varchar(32) DEFAULT NULL,
  `public_ip` varchar(32) DEFAULT NULL,
  `public_port` int(11) DEFAULT '0',
  `passwd` varchar(32) NOT NULL DEFAULT 'abcd1234',
  `local_ip` varchar(32) DEFAULT NULL,
  `local_port` int(11) DEFAULT '0',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `last_login` timestamp NOT NULL DEFAULT '2000-01-01 00:00:01',
  `last_logoff` timestamp NOT NULL DEFAULT '2000-01-01 00:00:01',
  `online_time` int(10) unsigned NOT NULL DEFAULT '0',
  `birthday` datetime DEFAULT NULL,
  `avatar` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `name` (`name`),
  UNIQUE KEY `phone` (`phone`),
  UNIQUE KEY `mac_addr` (`mac_addr`)
) ENGINE=InnoDB AUTO_INCREMENT=2836 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for tb_client_has
-- ----------------------------
DROP TABLE IF EXISTS `tb_client_has`;
CREATE TABLE `tb_client_has` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `clt_a` bigint(20) unsigned DEFAULT NULL,
  `clt_b` bigint(20) unsigned DEFAULT NULL,
  `flag` int(10) unsigned NOT NULL DEFAULT '0',
  `type` int(10) DEFAULT NULL COMMENT '判断是否是管理员：\n0 :不是\n1  :是',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=68 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for tb_message
-- ----------------------------
DROP TABLE IF EXISTS `tb_message`;
CREATE TABLE `tb_message` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `flag` int(10) unsigned DEFAULT '0',
  `data_len` int(10) unsigned NOT NULL,
  `clt_a` bigint(20) unsigned NOT NULL,
  `clt_b` bigint(20) unsigned NOT NULL,
  `start_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `end_time` timestamp NOT NULL DEFAULT '2000-01-01 00:00:01',
  `data` blob,
  `type` int(10) NOT NULL,
  `state` int(32) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=56 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for tb_token
-- ----------------------------
DROP TABLE IF EXISTS `tb_token`;
CREATE TABLE `tb_token` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `uid` int(11) NOT NULL,
  `token` varchar(255) DEFAULT NULL,
  `atime` timestamp NULL DEFAULT NULL COMMENT '过期时间',
  `ctime` timestamp NULL DEFAULT NULL COMMENT '当前时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `id_UNIQUE` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=38 DEFAULT CHARSET=utf8 COMMENT='用户Token表';

-- ----------------------------
-- View structure for fs_client2
-- ----------------------------
DROP VIEW IF EXISTS `fs_client2`;
CREATE ALGORITHM=UNDEFINED DEFINER=`jsfs`@`%` SQL SECURITY DEFINER VIEW `fs_client2` AS select `tb_client`.`id` AS `raw_id`,hex(`tb_client`.`id`) AS `ID`,hex(`tb_client`.`flag`) AS `FLAG`,`tb_client`.`name` AS `name`,`tb_client`.`label` AS `label` from `tb_client`;

-- ----------------------------
-- View structure for fs_client_simple
-- ----------------------------
DROP VIEW IF EXISTS `fs_client_simple`;
CREATE ALGORITHM=UNDEFINED DEFINER=`jsfs`@`%` SQL SECURITY DEFINER VIEW `fs_client_simple` AS select `tb_client`.`id` AS `raw_id`,hex(`tb_client`.`id`) AS `ID`,hex(`tb_client`.`flag`) AS `FLAG`,`tb_client`.`name` AS `name`,`tb_client`.`label` AS `label` from `tb_client`;

-- ----------------------------
-- View structure for fs_relation
-- ----------------------------
DROP VIEW IF EXISTS `fs_relation`;
CREATE ALGORITHM=UNDEFINED DEFINER=`jsfs`@`%` SQL SECURITY DEFINER VIEW `fs_relation` AS select hex(`tb_client_has`.`clt_a`) AS `A`,hex(`tb_client_has`.`clt_b`) AS `B`,hex(`tb_client_has`.`flag`) AS `FLAG` from `tb_client_has`;

SET FOREIGN_KEY_CHECKS = 1;
