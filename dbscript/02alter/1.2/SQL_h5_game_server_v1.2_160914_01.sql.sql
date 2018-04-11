ALTER TABLE `member` ADD COLUMN `happy_bean_from_op` INT(8) NULL DEFAULT '0';
ALTER TABLE `member` ADD COLUMN `avatar` varchar(100) NULL DEFAULT NULL;

ALTER TABLE `award_assign` ADD COLUMN `weight` INT(11) NOT NULL DEFAULT '0';
ALTER TABLE `award_assign` DROP COLUMN `begin_time`;
ALTER TABLE `award_assign` DROP COLUMN `end_time`;
ALTER TABLE `award_assign` DROP COLUMN `gift`;
ALTER TABLE `award_assign` DROP COLUMN `cond`;

ALTER TABLE `award_detail` DROP COLUMN `assign_time`;

ALTER TABLE `award_record_doll` ADD COLUMN `gain` INT(10) NOT NULL DEFAULT '0';



-- ----------------------------
-- Table structure for `rank`
-- ----------------------------
DROP TABLE IF EXISTS `rank`;
CREATE TABLE `rank` (
`id`  int(11) NOT NULL AUTO_INCREMENT ,
`uid`  int(11) NOT NULL COMMENT '用户ID' ,
`suid`  int(11) NULL DEFAULT NULL ,
`happy_bean`  bigint(11) NOT NULL COMMENT '当天获得的开心豆' ,
`game_id`  int(11) NOT NULL COMMENT '所属游戏' ,
`create_time`  datetime NOT NULL COMMENT '创建时间' ,
`status`  int(11) NOT NULL DEFAULT 1 COMMENT '状态 1-正常 2-已删除' ,
PRIMARY KEY (`id`)
)
ENGINE=InnoDB
DEFAULT CHARACTER SET=utf8 COLLATE=utf8_general_ci
AUTO_INCREMENT=1

;



-- ----------------------------
-- Table structure for `odds`
-- ----------------------------
DROP TABLE IF EXISTS `odds`;
CREATE TABLE `odds` (
`id`  int(11) NOT NULL AUTO_INCREMENT COMMENT '主键' ,
`begin`  int(11) NOT NULL COMMENT '开始区间' ,
`end`  int(11) NOT NULL COMMENT '结束区间' ,
`ratio`  float NOT NULL COMMENT '加减乘系数' ,
`game_level`  int(11) NOT NULL COMMENT '游戏场次' ,
PRIMARY KEY (`id`)
)
ENGINE=InnoDB
DEFAULT CHARACTER SET=utf8 COLLATE=utf8_general_ci
AUTO_INCREMENT=1

;

-- ----------------------------
-- Table structure for `jackpot`
-- ----------------------------
DROP TABLE IF EXISTS `jackpot`;
CREATE TABLE `jackpot` (
`id`  int(11) NOT NULL AUTO_INCREMENT COMMENT '主键' ,
`game_id`  int(11) NOT NULL COMMENT '游戏ID' ,
`game_level`  int(11) NOT NULL COMMENT '游戏场次' ,
`happy_bean`  int(11) NOT NULL COMMENT '奖池数' ,
PRIMARY KEY (`id`)
)
ENGINE=InnoDB
DEFAULT CHARACTER SET=utf8 COLLATE=utf8_general_ci
AUTO_INCREMENT=1

;


-- ----------------------------
-- Table structure for `capital_pool`
-- ----------------------------
DROP TABLE IF EXISTS `capital_pool`;
CREATE TABLE `capital_pool` (
`id`  int(11) NOT NULL AUTO_INCREMENT COMMENT '主键' ,
`game_id`  int(11) NOT NULL COMMENT '游戏ID' ,
`uid`  int(10) NOT NULL COMMENT '用户id' ,
`game_level`  int(11) NOT NULL COMMENT '游戏场次' ,
`happy_bean`  int(11) NOT NULL COMMENT '资金数' ,
PRIMARY KEY (`id`)
)
ENGINE=InnoDB
DEFAULT CHARACTER SET=utf8 COLLATE=utf8_general_ci
AUTO_INCREMENT=1

;

