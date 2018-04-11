package moudles.gae.service.child;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jws.Logger;
import jws.dal.Dal;
import jws.dal.sqlbuilder.Condition;
import jws.dal.sqlbuilder.Sort;
import jws.module.constants.gae.GaeAllConstans;
import utils.DaoUtil;
import utils.DateUtil;
import moudles.gae.ddl.GaePrizeTopDDL;
import moudles.gae.ddl.GaeUserPlDDL;


/**
 * 用户抽奖盈亏 service
 * 
 * @author caixb
 *
 */
public class GaeUserPlService{
	
	/**
	 * 根据场次查询用户参与抽红包盈亏明细
	 * 
	 * @param status
	 * @return
	 */
	public static Map<Integer, Integer> getUserPlDetail(Integer[] uid, String roomId) {
		List<GaeUserPlDDL> getUserPl = getUserPl(uid, roomId);
		Map<Integer, Integer> map = new HashMap<Integer, Integer>();
		for (int i = 0; i < uid.length; i++) {
			map.put(uid[i], 0);
		}
		for (GaeUserPlDDL gupl : getUserPl) {
			map.put(gupl.getUserId(), gupl.getTotal());
		}
		return map;
	}
	
	/**
	 * 根据场次查询用户参与抽红包盈亏明细
	 * 
	 * @param status
	 * @return
	 */
	public static List<GaeUserPlDDL> getUserPl(Integer[] uid, String roomId) {
		Condition cond = new Condition("GaeUserPlDDL.roomId", "=", roomId);
		cond.add(new Condition("GaeUserPlDDL.userId","in",uid), "AND");
		List<GaeUserPlDDL> list = Dal.select(DaoUtil.genAllFields(GaeUserPlDDL.class), cond, new Sort("GaeUserPlDDL.total",false), 0, -1);
		return list;
	}
	
	/**
	 * 插入用户盈亏
	 * 
	 * @param gaeUserPlDDL
	 * @return
	 */
	public static boolean insertUserPl(GaeUserPlDDL gaeUserPlDDL) {
		try {
		StringBuffer sql = new StringBuffer("insert into gae_user_pl(user_id, room_id, sys_profit, profit, loss, total, create_time)");
		sql.append(" values(");
		sql.append(gaeUserPlDDL.getUserId() + ", ");
		sql.append("'" + gaeUserPlDDL.getRoomId() + "', ");
		sql.append(gaeUserPlDDL.getSysProfit() + ", ");
		sql.append(gaeUserPlDDL.getProfit() + ", ");
		sql.append(gaeUserPlDDL.getLoss() + ", ");
		sql.append(gaeUserPlDDL.getTotal() + ", ");
		sql.append("'" + DateUtil.getDateString(gaeUserPlDDL.getCreateTime()) + "'");
		
		
		sql.append(") ON DUPLICATE KEY UPDATE ");
		
		sql.append("profit = profit + " + gaeUserPlDDL.getProfit() + ", ");
		sql.append("loss = loss + " + gaeUserPlDDL.getLoss() + ", ");
		sql.append("total = total + " + gaeUserPlDDL.getTotal() + ", ");
		sql.append("sys_profit = sys_profit + " + gaeUserPlDDL.getSysProfit() + " ");
		
		int result = Dal.executeNonQuery(GaeUserPlDDL.class, sql.toString());
		
		return result > 0;
		
		} catch (Exception e) {
			Logger.error("", e);
		}
		return false;
	}
}
