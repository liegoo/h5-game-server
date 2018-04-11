/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;

USE `h5_game_server`;

/* Alter table in target */
ALTER TABLE `award` 
	CHANGE `type` `type` INT(2)   NOT NULL COMMENT '奖品类型1=实物 2=话费充值 3=Q币 4=代金券 5=JD卡 6=开心豆' AFTER `status` ;

/* Create table in target */
CREATE TABLE `award_assign`(
	`id` INT(11) NOT NULL  AUTO_INCREMENT , 
	`award_id` INT(11) NOT NULL  COMMENT '奖品id' , 
	`hits` INT(11) NOT NULL  COMMENT '已中数量·' , 
	`remain` INT(11) NOT NULL  COMMENT '剩余数量' , 
	`sort` INT(11) NOT NULL  COMMENT '权重' , 
	`game_level` INT(2) NOT NULL  COMMENT '所属游戏场次 1-初级 2-中级 3-高级 4-试玩' , 
	`create_time` DATETIME NOT NULL  COMMENT '创建时间' , 
	`begin_time` DATETIME NOT NULL  COMMENT '开始时间' , 
	`end_time` DATETIME NOT NULL  COMMENT '结束时间' , 
	`gift` INT(11) NOT NULL  DEFAULT 0 COMMENT '是否为必中奖品 0-不是 1-是 ' , 
	`cond` INT(2) NULL  DEFAULT 0 COMMENT '必中条件 1-新注册用户' , 
	`status` INT(11) NOT NULL  DEFAULT 1 COMMENT '状态 1-在前端显示 2-不在前端显示 3-已删除' , 
	PRIMARY KEY (`id`) 
) ENGINE=INNODB DEFAULT CHARSET='utf8' COLLATE='utf8_general_ci';


/* Create table in target */
CREATE TABLE `award_detail`(
	`id` INT(11) NOT NULL  AUTO_INCREMENT , 
	`award_assign_id` INT(11) NOT NULL  COMMENT '中奖配置id' , 
	`assign_time` DATETIME NOT NULL  COMMENT '设奖时间点' , 
	`hit_time` DATETIME NULL  COMMENT '中奖时间' , 
	`order_id` VARCHAR(255) COLLATE utf8_general_ci NULL  COMMENT '订单号' , 
	`uid` INT(11) NULL  COMMENT '用户ID' , 
	`status` INT(11) NOT NULL  DEFAULT 1 COMMENT '状态 1-未中奖 2-已中奖 3-已废弃' , 
	PRIMARY KEY (`id`) 
) ENGINE=INNODB DEFAULT CHARSET='utf8' COLLATE='utf8_general_ci';


/* Alter table in target */
ALTER TABLE `award_record` 
	CHANGE `audit_remark` `audit_remark` VARCHAR(200)  COLLATE utf8_general_ci NOT NULL DEFAULT '' COMMENT '审核备注' AFTER `game_uid` , 
	CHANGE `audit_status` `audit_status` INT(2)   NOT NULL DEFAULT 10 COMMENT '审核状态 10待审核 20待发货 30已发货 40审核不通过' AFTER `audit_remark` , 
	CHANGE `remark` `remark` VARCHAR(200)  COLLATE utf8_general_ci NOT NULL DEFAULT '' COMMENT '该字段被用来记录了奖品名了' AFTER `audit_status` , 
	ADD COLUMN `card_no` VARCHAR(100)  COLLATE utf8_general_ci NULL DEFAULT '' COMMENT '卡号' AFTER `op_name` , 
	ADD COLUMN `card_pwd` VARCHAR(100)  COLLATE utf8_general_ci NULL DEFAULT '' COMMENT '卡密' AFTER `card_no` , 
	ADD COLUMN `deliver_company` VARCHAR(100)  COLLATE utf8_general_ci NULL DEFAULT '' COMMENT '快递公司' AFTER `card_pwd` , 
	ADD COLUMN `deliver_no` VARCHAR(100)  COLLATE utf8_general_ci NULL DEFAULT '' COMMENT '快递号' AFTER `deliver_company` , 
	CHANGE `create_time` `create_time` DATETIME   NULL AFTER `deliver_no` , 
	ADD COLUMN `source_type` INT(2)   NOT NULL DEFAULT 1 COMMENT '订单来源(1-限量领奖，2-夹娃娃)' AFTER `update_time` ;

/* Create table in target */
CREATE TABLE `award_record_doll`(
	`id` INT(11) NOT NULL  AUTO_INCREMENT , 
	`award_id` INT(11) NOT NULL  COMMENT '奖品id' , 
	`zhifu_order_id` VARCHAR(50) COLLATE utf8_general_ci NOT NULL  COMMENT '订单号' , 
	`user_name` VARCHAR(20) COLLATE utf8_general_ci NULL  COMMENT '用户名' , 
	`uid` VARCHAR(20) COLLATE utf8_general_ci NOT NULL  COMMENT '用户id' , 
	`happy_bean` INT(11) NOT NULL  COMMENT '开心豆' , 
	`card_no` VARCHAR(100) COLLATE utf8_general_ci NULL  COMMENT '卡号(JD卡)' , 
	`card_pwd` VARCHAR(100) COLLATE utf8_general_ci NULL  COMMENT '卡密' , 
	`addr` VARCHAR(100) COLLATE utf8_general_ci NULL  COMMENT '地址' , 
	`qq` VARCHAR(20) COLLATE utf8_general_ci NULL  COMMENT 'qq' , 
	`mobile` VARCHAR(20) COLLATE utf8_general_ci NULL  COMMENT '手机' , 
	`game_id` VARCHAR(20) COLLATE utf8_general_ci NULL  COMMENT '水煮手游ID 收代金券的游戏id' , 
	`game_uid` VARCHAR(20) COLLATE utf8_general_ci NULL  COMMENT '水煮游戏账号uid' , 
	`game_name` VARCHAR(20) COLLATE utf8_general_ci NULL  COMMENT '水煮手游名称 收代金券的游戏名称' , 
	`deliver_company` VARCHAR(100) COLLATE utf8_general_ci NULL  COMMENT '快递公司' , 
	`deliver_no` VARCHAR(100) COLLATE utf8_general_ci NULL  COMMENT '快递号' , 
	`audit_status` INT(2) NOT NULL  DEFAULT 10 COMMENT '审核状态 10待审核 20待发货 30已发货 40审核不通过 50领奖信息未完善 ' , 
	`audit_remark` VARCHAR(255) COLLATE utf8_general_ci NOT NULL  COMMENT '审核备注' , 
	`op_name` VARCHAR(20) COLLATE utf8_general_ci NOT NULL  COMMENT '后台操作人员' , 
	`create_time` DATETIME NULL  COMMENT '创建时间' , 
	`update_time` DATETIME NULL  COMMENT '更新时间' , 
	`hit` INT(2) NOT NULL  DEFAULT 0 COMMENT '是否中奖 1-已中奖 2-未中奖' , 
	`visible` INT(2) NOT NULL  DEFAULT 1 COMMENT '是否在左则显示 1-show，other-hide' , 
	`status` INT(11) NOT NULL  DEFAULT 1 COMMENT '领取状态 1-未领取 2-已领取' , 
	PRIMARY KEY (`id`) 
) ENGINE=INNODB DEFAULT CHARSET='utf8' COLLATE='utf8_general_ci';


/* Alter table in target */
ALTER TABLE `award_record_tmp` 
	CHANGE `awardName` `awardName` VARCHAR(50)  COLLATE utf8_general_ci NOT NULL COMMENT '奖品名称' AFTER `id` , 
	DROP COLUMN `award_id` ;

/* Create table in target */
CREATE TABLE `blacklist`(
	`id` INT(11) NOT NULL  AUTO_INCREMENT , 
	`uid` INT(11) NOT NULL  COMMENT '用户ID' , 
	`remark` VARCHAR(255) COLLATE utf8_general_ci NOT NULL  COMMENT '备注' , 
	`update_time` DATETIME NOT NULL  COMMENT '更新时间' , 
	`op_name` VARCHAR(255) COLLATE utf8_general_ci NULL  COMMENT '后台操作人员' , 
	`status` INT(11) NOT NULL  DEFAULT 1 COMMENT '状态 1已加入黑名单  2已删除' , 
	PRIMARY KEY (`id`) 
) ENGINE=INNODB DEFAULT CHARSET='utf8' COLLATE='utf8_general_ci';


/* Create table in target */
CREATE TABLE `chance`(
	`id` INT(11) NOT NULL  AUTO_INCREMENT , 
	`uid` INT(11) NOT NULL  COMMENT '用户ID' , 
	`chance` INT(11) NOT NULL  DEFAULT 0 COMMENT '试玩次数' , 
	PRIMARY KEY (`id`) 
) ENGINE=INNODB DEFAULT CHARSET='utf8' COLLATE='utf8_general_ci';


/* Alter table in target */
ALTER TABLE `games` 
	CHANGE `game_desc` `game_desc` VARCHAR(500)  COLLATE utf8_general_ci NOT NULL DEFAULT '' COMMENT '游戏简介' AFTER `logo` , 
	CHANGE `pay_callback_url` `pay_callback_url` VARCHAR(500)  COLLATE utf8_general_ci NOT NULL COMMENT '充值回调接口地址' AFTER `game_desc` , 
	CHANGE `hot` `hot` INT(2)   NOT NULL DEFAULT 0 COMMENT '是否加hot, 1为加hot' AFTER `remark` , 
	CHANGE `status` `status` INT(2)   NOT NULL DEFAULT 1 COMMENT '状态 1-上线  2-下线  3-删除' AFTER `hot` ;

/* Alter table in target */
ALTER TABLE `member_log` 
	CHANGE `bill_id` `bill_id` VARCHAR(100)  COLLATE utf8_general_ci NOT NULL DEFAULT '' COMMENT '对账ID' AFTER `rpa_id` , 
	CHANGE `op_type` `op_type` INT(11)   NOT NULL DEFAULT 0 COMMENT '操作类型 10登录 20注册 30注销 40充值 50签到 60充值赠送 70消费 80玩游戏赠送  90游戏币兑换 100玩游戏奖励' AFTER `bill_id` ;

/* Alter table in target */
ALTER TABLE `news` 
	ADD COLUMN `notice_id` INT(11)   NOT NULL COMMENT '公告ID' AFTER `intro` , 
	ADD COLUMN `img_url` VARCHAR(100)  COLLATE utf8_general_ci NOT NULL COMMENT '图标' AFTER `notice_id` , 
	CHANGE `detail_url` `detail_url` VARCHAR(1000)  COLLATE utf8_general_ci NULL COMMENT '详情页面地址' AFTER `img_url` ;