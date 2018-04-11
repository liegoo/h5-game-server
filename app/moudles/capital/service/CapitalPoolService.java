package moudles.capital.service;

import java.util.List;

import jws.dal.Dal;
import jws.dal.sqlbuilder.Condition;
import moudles.capital.ddl.CapitalPoolDDL;
import utils.DaoUtil;

public class CapitalPoolService {

	/**
	 * 创建或更新资金池
	 * 
	 * @param capitalPool
	 * @return
	 */
	public static boolean createOrUpdateCapitalPool(CapitalPoolDDL capitalPool) {
		if (capitalPool.getId() > 0) {
			Condition cond = new Condition("CapitalPoolDDL.id", "=", capitalPool.getId());
			return Dal.update(capitalPool, "CapitalPoolDDL.happyBean", cond) > 0;
		} else {
			return Dal.insert(capitalPool) > 0;
		}
	}

	/**
	 * 通过UID获取资金池
	 * 
	 * @param gameId
	 * @param gameLevel
	 * @param uid
	 * @return
	 */
	public static CapitalPoolDDL getCapitalByUid(int gameId, int gameLevel, int uid) {
		Condition cond = new Condition("CapitalPoolDDL.gameId", "=", gameId);
		cond.add(new Condition("CapitalPoolDDL.gameLevel", "=", gameLevel), "and");
		cond.add(new Condition("CapitalPoolDDL.uid", "=", uid), "and");
		List<CapitalPoolDDL> list = Dal.select(DaoUtil.genAllFields(CapitalPoolDDL.class), cond, null, 0, 1);
		if (list != null && list.size() > 0) {
			return list.get(0);
		}
		return null;
	}

}
