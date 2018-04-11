package moudles.blacklist.service;

import jws.dal.Dal;
import jws.dal.sqlbuilder.Condition;
import jws.module.constants.blacklist.BlacklistStatus;
import moudles.blacklist.ddl.BlacklistDDL;

public class BlacklistService {

	/**
	 * 是否在黑名单内
	 * 
	 * @param uid
	 * @return
	 */
	public static boolean exists(int uid) {
		Condition cond = new Condition("BlacklistDDL.uid", "=", uid);
		cond.add(new Condition("BlacklistDDL.status", "=", BlacklistStatus.ENABLED.getValue()), "and");
		return Dal.count(cond) > 0;
	}
}
