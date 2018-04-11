package moudles.gae.service.child;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import jws.dal.Dal;
import jws.dal.sqlbuilder.Condition;
import jws.dal.sqlbuilder.Sort;
import moudles.gae.ddl.GaeRobotActiveDDL;
import moudles.gae.ddl.GaeRobotInfoDDL;
import moudles.gae.ddl.GaeRobotSettDDL;
import moudles.rank.ddl.RankDDL;

import org.apache.commons.lang.StringUtils;

import utils.DaoUtil;
import common.dao.QueryConnectionHandler;
import constants.GlobalConstants;


/**
 * 机器人配置查询
 * 
 * @author caixb
 *
 */
public class GaeRobotSettingService{
	
	
	/**
	 * 查询单个
	 * 
	 * @param roomId
	 * @return
	 */
	public static GaeRobotSettDDL getRobotSett(String roomId){
		if(StringUtils.isBlank(roomId)){
			return null;
		}
		String str = new SimpleDateFormat("HH:mm:ss").format(new Date());
		StringBuffer sql = new StringBuffer("SELECT room_id, sleep_time FROM gae_robot_setting ");
		sql.append(" WHERE TIME_TO_SEC('" + str + "') >= TIME_TO_SEC(start_time)");
		sql.append(" AND TIME_TO_SEC('" + str + "') <= TIME_TO_SEC(stop_time)");
		sql.append(" AND room_id = '" + roomId + "'");
		List<GaeRobotSettDDL> list = Dal.execute(GaeRobotSettDDL.class, sql.toString());
		if (list != null && list.size() > 0) {
			return list.get(0);
		}
		return null;
	}
	
	/**
	 * 查询所有
	 * @return
	 */
	public static Map<String, String> getCurrentRobotSett(){
		Map<String, String> map = new HashMap<String, String>();
		try {
			String str = new SimpleDateFormat("HH:mm:ss").format(new Date());
			StringBuffer sql = new StringBuffer("SELECT room_id, sleep_time FROM gae_robot_setting ");
			sql.append(" WHERE TIME_TO_SEC('" + str + "') >= TIME_TO_SEC(start_time)");
			sql.append(" AND TIME_TO_SEC('" + str + "') <= TIME_TO_SEC(stop_time)");
			
			@SuppressWarnings("deprecation")
			ResultSet set = Dal.executeQuery(GlobalConstants.dbSource, sql.toString());
			while(set.next()){
				map.put(set.getString("room_id"), set.getString("sleep_time"));
			}
			return map;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * 查询
	 * 
	 * @param uids
	 * @return
	 */
	public static GaeRobotInfoDDL getRotoInfo(List<Integer> uids){
		StringBuffer sql = new StringBuffer("select * from gae_robot_info ");
		if(uids != null && uids.size() > 0){
			for (int i = 0; i < uids.size(); i++) {
				if(i == 0){
					sql.append(" where uid <> " + uids.get(i));
				}else{
					sql.append(" and uid <> " + uids.get(i));
				}
			}
		}
		List<GaeRobotInfoDDL> list = Dal.execute(GaeRobotInfoDDL.class, sql.toString());
		if(list != null && list.size() > 0){
			Random random = new Random();
			return list.get(random.nextInt(list.size()));
		}
		return null;
	}
	
	
	public static boolean insertRobotFlag(GaeRobotActiveDDL robotActive){
		try {
			return Dal.insert(robotActive) == 1;
		} catch (Exception e) {}
		return false;
	}
	
	public static boolean isRunRobot(String roomId){
		if(StringUtils.isBlank(roomId)){
			return false;
		}
		Condition cond = new Condition("GaeRobotActiveDDL.roomId", "=", roomId);
		List<GaeRobotActiveDDL> list = Dal.select(DaoUtil.genAllFields(GaeRobotActiveDDL.class), cond, null, 0, -1);
		if(list != null && list.size() != 0){
			GaeRobotActiveDDL ra = list.get(0);
			if(ra != null){
				return true;
			}
		}
		return false;
	}
}
