package moudles.jackpot.service;

import java.util.List;

import jws.dal.Dal;
import jws.dal.sqlbuilder.Condition;
import moudles.jackpot.ddl.JackpotDDL;
import utils.DaoUtil;

public class JackpotService {

	/**
	 * 根据游戏获取奖池数
	 * 
	 * @param gameId
	 * @param gameLevel
	 * @return
	 */
	public static JackpotDDL getByGameLevel(int gameId, int gameLevel) {
		Condition cond = new Condition("JackpotDDL.gameId", "=", gameId);
		cond.add(new Condition("JackpotDDL.gameLevel", "=", gameLevel), "and");
		List<JackpotDDL> list = Dal.select(DaoUtil.genAllFields(JackpotDDL.class), cond, null, 0, 1);
		if (list != null && list.size() > 0) {
			return list.get(0);
		}
		return null;
	}

	/**
	 * 创建或更新奖池
	 * 
	 * @param jackpot
	 * @return
	 */
	public static boolean createOrUpdateJackpot(JackpotDDL jackpot) {
		if (jackpot.getId() > 0) {
			Condition cond = new Condition("JackpotDDL.id", "=", jackpot.getId());
			return Dal.update(jackpot, "JackpotDDL.happyBean", cond) > 0;
		} else {
			return Dal.insert(jackpot) > 0;
		}
	}
}
