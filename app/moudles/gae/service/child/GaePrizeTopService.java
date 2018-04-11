package moudles.gae.service.child;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.jfinal.kit.JsonKit;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.IAtom;
import com.jfinal.plugin.activerecord.Record;

import constants.GlobalConstants;
import jws.Logger;
import jws.dal.Dal;
import jws.dal.sqlbuilder.Condition;
import jws.dal.sqlbuilder.Sort;
import jws.module.constants.gae.GaeAllConstans;
import jws.module.response.age.GaeRecordDto;
import moudles.gae.assist.UnfitResultException;
import moudles.gae.ddl.GaePrizeTopDDL;
import utils.DaoUtil;


/**
 * 抢红包 -- top排行榜 service
 * 
 * @author caixb
 *
 */
public class GaePrizeTopService{
	
	/**
	 * 查询top排行
	 * 
	 * @return
	 */
	public static List<GaePrizeTopDDL> getPrizeTop() {
		Condition cond = new Condition("GaePrizeTopDDL.id", ">", 0);
		List<GaePrizeTopDDL> list = Dal.select(DaoUtil.genAllFields(GaePrizeTopDDL.class), cond, new Sort("GaePrizeTopDDL.totalBeans",false), 0, -1);
		return list;
	}
	
	/**
	 * 查询top排行
	 * 
	 * @param topType  1:昨日排行 2:周排行
	 * @return
	 */
	public static List<GaePrizeTopDDL> getPrizeTop(int topType, int pageNo, int pageSize) {
		pageNo = pageNo < 1 ? 1 : pageNo;
		pageSize = pageSize < 1 ? 10 : pageSize;
		Condition cond = new Condition("GaePrizeTopDDL.topType", "=", topType);
		List<GaePrizeTopDDL> list = Dal.select(DaoUtil.genAllFields(GaePrizeTopDDL.class), cond, new Sort("GaePrizeTopDDL.totalBeans",false), (pageNo - 1) * pageSize, pageSize);
		return list;
	}
	
	public static List<GaePrizeTopDDL> getMyTop(int topType){
		Condition cond = new Condition("GaePrizeTopDDL.topType", "=", topType);
		List<GaePrizeTopDDL> list = Dal.select(DaoUtil.genAllFields(GaePrizeTopDDL.class), cond, new Sort("GaePrizeTopDDL.totalBeans",false), 0, -1);
		return list;
	}
	
	/**
	 * 更新排行榜
	 * 
	 * @param topType
	 * @param limit
	 * @return
	 */
	public static boolean updatePrizeTop(final int topType, int limit) {
		StringBuffer sql = new StringBuffer();
		sql.append("SELECT user_id, user_name, user_avatar, count(user_id) AS user_count, sum(hit_beans) AS hit_beans FROM `gae_draw_record` ");
		sql.append("WHERE 1=1 ");
		if(topType == GaeAllConstans.TOP_TYPE_DAY){
			sql.append("AND hit_time BETWEEN date_sub(curdate(), interval 1 day) AND current_date() ");				
		}else if(topType == GaeAllConstans.TOP_TYPE_WEEK){
			//加hit_time 的时间范围，减少查询记录
			sql.append("AND hit_time > DATE_ADD(NOW(),INTERVAL -10 DAY) AND YEARWEEK(date_format(hit_time,'%y-%m-%d')) = YEARWEEK(now()) ");
		}
		sql.append("GROUP BY user_id ORDER BY hit_beans DESC LIMIT ?");
		final List<Record> prizeTopList = Db.find(sql.toString(),limit);
		if (prizeTopList.isEmpty()) {
			Logger.debug("更新排行榜失败，没有新的排行数据,topType:%s",topType);
			return false; 
		}
		//首先先删除之前的数据，再添加新数据
		boolean result = Db.tx(new IAtom() {		
			@Override
			public boolean run() throws SQLException {
				StringBuilder insertSql = new StringBuilder();
				int num = Db.update("DELETE FROM gae_prize_top WHERE top_type = ?",topType);
				Logger.info("删除类型是:%s的数据", topType);
				if(num < 1) {
					Logger.error("更新排行榜失败，没有新的排行数据,topType:%s", topType);
					return false;
				}			
				List<Object> parmas =new ArrayList<Object>();
				insertSql.append("INSERT INTO gae_prize_top(top_type,user_id,user_name,total_beans,deaw_count,create_time,user_avatar) VALUES");
				for (Record r : prizeTopList) {
					insertSql.append("(?,?,?,?,?,now(),?),");
					parmas.add(topType);
					parmas.add(r.getInt("user_id"));
					parmas.add(r.get("user_name"));
					parmas.add(r.getBigDecimal("hit_beans"));
					parmas.add(r.getLong("user_count"));
					parmas.add(r.get("user_avatar"));	
				}
				Logger.info("更新类型是:%s的数据", topType);
				int result = Db.update(insertSql.deleteCharAt(insertSql.length()-1).toString(),parmas.toArray());
				if (result < 1) {
					Logger.error("更新排行榜失败，没有新的排行数据,topType:%s", topType);
					return false;
				}
				Logger.info("更新类型是:%s的数据结束", topType);
				return true;
			}
		});
		Logger.info("更新类型是:%s的数据结束", sql.toString());
		return result;
	}
}
