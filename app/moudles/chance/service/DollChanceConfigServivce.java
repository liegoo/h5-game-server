package moudles.chance.service;

import java.util.List;

import jws.dal.Dal;
import jws.dal.sqlbuilder.Condition;
import jws.dal.sqlbuilder.Sort;
import moudles.chance.ddl.ChanceDDL;
import moudles.chance.ddl.DollChanceConfigDDL;
import utils.DaoUtil;

public class DollChanceConfigServivce {

	/**
	 * 获取最新特权配置
	 * 
	 * @param id
	 * @return
	 */
	public static DollChanceConfigDDL getFirstConfig() {
		Condition cond = new Condition("DollChanceConfigDDL.status", "=", "1");
		List<DollChanceConfigDDL> list = Dal.select(DaoUtil.genAllFields(DollChanceConfigDDL.class), cond, new Sort("DollChanceConfigDDL.updateTime", false), 0, 1);
		if (list != null && list.size() == 1) {
			return list.get(0);
		}
		return null;
	}

}
