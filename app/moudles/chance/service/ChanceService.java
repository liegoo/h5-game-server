package moudles.chance.service;

import java.util.List;

import jws.Logger;
import jws.dal.Dal;
import jws.dal.sqlbuilder.Condition;
import moudles.chance.ddl.ChanceDDL;
import utils.DaoUtil;
import constants.MessageCode;
import exception.BusinessException;

public class ChanceService {

	/**
	 * 获取特权机会
	 * 
	 * @param uid
	 * @return
	 * @throws BusinessException
	 */
	public static ChanceDDL getByUid(int uid) throws BusinessException {
		ChanceDDL ddl = null;
		try {
			Condition cond = new Condition("ChanceDDL.uid", "=", uid);
			List<ChanceDDL> list = Dal.select(DaoUtil.genAllFields(ChanceDDL.class), cond, null, 0, 1);
			if (null != list && list.size() > 0) {
				ddl = list.get(0);
			}
		} catch (Exception e) {
			Logger.error(e, "");
			throw new BusinessException(MessageCode.ERROR_CODE_500, "系统内部异常");
		}
		return ddl;
	}

	/**
	 * 更新特权机会数
	 * 
	 * @param chance
	 * @return
	 */
	public static boolean updateChance(ChanceDDL chance) {
		Condition cond = new Condition("ChanceDDL.id", "=", chance.getId());
		return Dal.update(chance, "ChanceDDL.chance,ChanceDDL.status", cond) > 0 ;
	}

	/**
	 * 创建特权机会
	 * 
	 * @param chance
	 * @return
	 */
	public static boolean createChance(ChanceDDL chance) {
		return Dal.insert(chance) > 0;
	}
}
