package moudles.gae.service.child;
import java.util.List;

import jws.dal.Dal;
import jws.dal.sqlbuilder.Condition;
import jws.dal.sqlbuilder.Sort;
import moudles.activity.ddl.RechargePresentedActivityDDL;
import moudles.activity.ddl.RechargePresentedRuleDDL;
import moudles.gae.ddl.GaeRoomDDL;
import utils.DaoUtil;


/**
 * 抽红包场次 service
 * 
 * @author caixb
 *
 */
public class GaeRoomService{
	
	/**
	 * 查询所有有效的红包场次
	 * 
	 * @param status
	 * @return
	 */
	public static List<GaeRoomDDL> getRooms(int status) {
		Condition cond = new Condition("GaeRoomDDL.status", "=", status);
		return Dal.select(DaoUtil.genAllFields(GaeRoomDDL.class), cond, new Sort("GaeRoomDDL.totalBeans",true), 0, -1);
	}
	
	/**
	 * 根据id查询红包场次
	 * 
	 * @param roomId
	 * @return
	 */
	public static GaeRoomDDL getRoom(String roomId) {
		Condition cond = new Condition("GaeRoomDDL.id", "=", roomId);
		List<GaeRoomDDL> list = Dal.select(DaoUtil.genAllFields(GaeRoomDDL.class), cond, null, 0, -1);
		if(list != null && list.size() == 1){
			return list.get(0);
		}
		return null;
	}
}
