package moudles.order.service;

import java.util.List;

import org.h2.util.StringUtils;

import jws.dal.Dal;
import jws.dal.sqlbuilder.Condition;
import moudles.order.ddl.GameOrderDDL;
import utils.DaoUtil;

public class GameOrderService {

	/**
	 * 获取订单号获取 “游戏-订单关联记录” 
	 */
	public static GameOrderDDL getByOrderId(String zhifuOrderId) {

		if (StringUtils.isNullOrEmpty(zhifuOrderId)) {
			return null;
		}

		Condition cond = new Condition("GameOrderDDL.zhifuOrderId", "=", zhifuOrderId);
		List<GameOrderDDL> gameOrders = Dal.select(DaoUtil.genAllFields(GameOrderDDL.class), cond, null, 0, 1);

		if (gameOrders != null && gameOrders.size() > 0) {
			return gameOrders.get(0);
		}

		return null;
	}

	/**
	 * “游戏-订单关联记录”
	 */
	public static boolean create(GameOrderDDL gameOrder) {
		return Dal.insert(gameOrder) > 0;
	}
}
