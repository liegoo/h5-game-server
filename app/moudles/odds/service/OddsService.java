package moudles.odds.service;

import java.util.List;

import jws.dal.Dal;
import jws.dal.sqlbuilder.Condition;
import moudles.odds.ddl.OddsDDL;

public class OddsService {

	/**
	 * 根据用户资金池数获取中奖概率
	 * 
	 * @param happyBean
	 * @param gameLevel
	 * @param gameId
	 * @return
	 */
	public static float getRatioByHappyBean(long happyBean, int gameLevel) {
		Condition cond = new Condition("OddsDDL.gameLevel", "=", gameLevel);
		cond.add(new Condition("OddsDDL.begin", "<=", happyBean), "and");
		cond.add(new Condition("OddsDDL.end", ">=", happyBean), "and");
		List<OddsDDL> list = Dal.select("OddsDDL.ratio", cond, null, 0, 1);
		if (list != null && list.size() > 0) {
			return list.get(0).getRatio();
		}
		return 0f;
	}
}
