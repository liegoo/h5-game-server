package moudles.guess.service;

import java.util.List;

import jws.dal.Dal;
import jws.dal.sqlbuilder.Condition;
import jws.dal.sqlbuilder.Sort;
import jws.module.constants.guess.GuessAwardStatus;
import moudles.guess.ddl.GuessAwardDDL;
import utils.DaoUtil;

public class GuessAwardService {

	/**
	 * 获取夺宝奖品 
	 */
	public static GuessAwardDDL getById(int id) {
		if (id == 0) {
			return null;
		}
		Condition cond = new Condition("GuessAwardDDL.id", "=", id);
		List<GuessAwardDDL> list = Dal.select(DaoUtil.genAllFields(GuessAwardDDL.class), cond, null, 0, 1);
		if (list != null && list.size() == 1) {
			return list.get(0);
		}
		return null;
	}

	/**
	 * 获取夺宝奖品列表
	 */
	public static List<GuessAwardDDL> list(int type, int gameLevel, int status, int page, int pageSize) {
		Condition cond = new Condition("GuessAwardDDL.status", "=", GuessAwardStatus.NORMAL.getValue());
		if (type > 0) {
			cond.add(new Condition("GuessAwardDDL.type", "=", type), "and");
		}
		if (gameLevel > 0) {
			cond.add(new Condition("GuessAwardDDL.gameLevel", "=", gameLevel), "and");
		}
		if (status > 0) {
			cond.add(new Condition("GuessAwardDDL.status", "=", status), "and");
		}
		Sort sort = new Sort("GuessAwardDDL.createTime", false);
		List<GuessAwardDDL> list = Dal.select(DaoUtil.genAllFields(GuessAwardDDL.class), cond, sort, (page - 1) * pageSize, pageSize);
		return list;
	}
}
