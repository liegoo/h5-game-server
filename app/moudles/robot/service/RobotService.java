package moudles.robot.service;

import java.util.List;

import jws.dal.Dal;
import jws.dal.sqlbuilder.Condition;
import moudles.robot.ddl.RobotInfoDDL;
import utils.DaoUtil;

import common.dao.QueryConnectionHandler;

import constants.GlobalConstants;

public class RobotService {

	/**
	 * 随机机器人列表(获取)
	 * 
	 */
	public static List<RobotInfoDDL> randList(int count) {
		StringBuffer sql = new StringBuffer("select * from robot_info where status = 1 order by rand() limit ");
		count = count > 0 ? count : 5;
		sql.append(count);
		RobotInfoDDL dto = new RobotInfoDDL();
		List<RobotInfoDDL> result = Dal.getConnection(GlobalConstants.dbSource, new QueryConnectionHandler(dto, sql.toString()));
		return result;
	}

	/**
	 * 通过id获取机器人信息
	 */
	public static RobotInfoDDL getById(int id) {
		return Dal.select(DaoUtil.getIdField(RobotInfoDDL.class), id);
	}

	/**
	 * 通过uid获取机器人信息
	 */
	public static RobotInfoDDL getByUid(int uid) {
		StringBuffer sql = new StringBuffer("select id,uid,nickname,create_time createTime,status from robot_info where uid = ");
		sql.append(uid);
		RobotInfoDDL ddl = new RobotInfoDDL();
		List<RobotInfoDDL> list = Dal.getConnection(GlobalConstants.dbSource, new QueryConnectionHandler(ddl, sql.toString()));
		if (list != null && list.size() > 0) {
			return list.get(0);
		}
		return null;
	}

	/**
	 * 获取机器人信息数量
	 */
	public static int count(int status) {
		Condition cond = new Condition("RobotInfoDDL.id", ">", 0);
		if (status > 0) {
			cond.add(new Condition("RobotInfoDDL.status", ">", 0), "and");
		}
		return Dal.count(cond);
	}

	/**
	 * 获取机器人信息（随机）
	 */
	public static RobotInfoDDL getRandRobot() {
		StringBuffer sql = new StringBuffer("select id,uid,nickname,create_time createTime,status from robot_info where status = 1 order by rand() limit 1");
		RobotInfoDDL dto = new RobotInfoDDL();
		List<RobotInfoDDL> list = Dal.getConnection(GlobalConstants.dbSource, new QueryConnectionHandler(dto, sql.toString()));
		if (list != null && list.size() > 0) {
			return list.get(0);
		}
		return null;
	}

}
