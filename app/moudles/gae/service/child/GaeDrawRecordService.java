package moudles.gae.service.child;
import java.sql.ResultSet;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jws.dal.Dal;
import jws.dal.sqlbuilder.Condition;
import jws.dal.sqlbuilder.Sort;
import moudles.gae.assist.UnfitResultException;
import moudles.gae.ddl.GaeDrawRecordDDL;
import moudles.gae.ddl.GaeDrawRecordTempDDL;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.time.DateUtils;

import utils.DaoUtil;
import constants.GlobalConstants;


/**
 * 抢红包 -- 参与记录 service
 * 
 * @author caixb
 *
 */
public class GaeDrawRecordService{
	
	/**
	 * 查询当前活动参与的人
	 * 
	 * @param status
	 * @return
	 */
	public static List<GaeDrawRecordTempDDL> getDrawPlayers(String drawId) {
		if(StringUtils.isBlank(drawId)){
			return null;
		}
		Condition cond = new Condition("GaeDrawRecordTempDDL.drawId", "=", drawId);
		List<GaeDrawRecordTempDDL> records = Dal.select(DaoUtil.genAllFields(GaeDrawRecordTempDDL.class), cond, new Sort("GaeDrawRecordTempDDL.hitTime", true), 0, -1);
		return records;
	}
	
	/**
	 * 查询历史活动参与的人
	 * 
	 * @param status
	 * @return
	 */
	public static List<GaeDrawRecordDDL> getDrawPlayersByHistory(String drawId) {
		if(StringUtils.isBlank(drawId)){
			return null;
		}
		//TODO  加缓存
		Condition cond = new Condition("GaeDrawRecordDDL.drawId", "=", drawId);
		List<GaeDrawRecordDDL> records = Dal.select(DaoUtil.genAllFields(GaeDrawRecordDDL.class), cond, new Sort("GaeDrawRecordDDL.hitTime", true), 0, -1);
		return records;
	}
	
	/**
	 * 根据用户查询参与历史记录
	 * 
	 * @param uid
	 * @param pageNo
	 * @param pageSize
	 * @return
	 */
	public static List<GaeDrawRecordDDL> getDrawRecord(int uid, int pageNo, int pageSize){
		pageNo = pageNo < 1 ? 1 : pageNo;
		pageSize = pageSize < 1 ? 10 : pageSize;
		Condition cond = new Condition("GaeDrawRecordDDL.userId", "=", uid);
		return Dal.select(DaoUtil.genAllFields(GaeDrawRecordDDL.class), cond, new Sort("GaeDrawRecordDDL.hitTime", false), (pageNo - 1) * pageSize, pageSize);
	}
	
	/**
	 * 查询当前参与的抽奖活动
	 * 
	 * @param uid
	 * @param drawId
	 * @return
	 */
	public static GaeDrawRecordTempDDL getMyActiveDraw(int uid, String drawId){
		if(StringUtils.isBlank(drawId) || uid < 1){
			return null;
		}
		Condition cond = new Condition("GaeDrawRecordTempDDL.drawId", "=", drawId);
		cond.add(new Condition("GaeDrawRecordTempDDL.userId","=",uid), "AND");
		List<GaeDrawRecordTempDDL> records = Dal.select(DaoUtil.genAllFields(GaeDrawRecordTempDDL.class), cond, null, 0, -1);
		if(records != null && records.size() != 0){
			return records.get(0);
		}
		return null;
	}
	
	/**
	 * 查询已参与过的活动
	 * 
	 * @param uid
	 * @param drawId
	 * @return
	 */
	public static GaeDrawRecordDDL getMyHistoryDraw(int uid, String drawId){
		if(StringUtils.isBlank(drawId) || uid < 1){
			return null;
		}
		//TODO 这里要先查缓存
		Condition cond = new Condition("GaeDrawRecordDDL.drawId", "=", drawId);
		cond.add(new Condition("GaeDrawRecordDDL.userId","=",uid), "AND");
		List<GaeDrawRecordDDL> records = Dal.select(DaoUtil.genAllFields(GaeDrawRecordDDL.class), cond, null, 0, -1);
		if(records != null && records.size() != 0){
			return records.get(0);
		}
		return null;
	}
	
//	/**
//	 * 插入用户中奖记录（临时表）
//	 * 
//	 * @param headCount 当前房间限制人数
//	 * @param drt
//	 * @return
//	 */
//	public static boolean insertDrawRecordToTemp(int headCount, GaeDrawRecordTempDDL drt){
//		String lockKey = "insertDrawRecord-" + headCount + "-" + drt.getDrawId();
//		try {
//			if (!lock.tryCacheLock(lockKey, "", "2s")) {
//				return false;
//			}
//			String drawId = drt.getDrawId();
//			List<GaeDrawRecordTempDDL> drList = getDrawPlayers(drawId);
//			if(drList != null && (drList.size() == headCount || drList.size() > headCount)){
//				Logger.info("用户参与抢红包失败，人数已满，uid:" + drt.getUserId() + ",期数：" + drt.getDrawId());
//				return false;
//			}
//			return Dal.insert(drt) > 0;
//		} catch (Exception e) {
//			Logger.error(e, "");
//		} finally {
//			lock.cacheUnLock(lockKey);
//		}
//		return false;
//	}
	
	
	/**
	 * 插入用户中奖记录（临时表）
	 * 
	 * @param headCount 当前房间限制人数
	 * @param drt
	 * @return
	 */
	public static boolean insertDrawRecordToTemp(GaeDrawRecordTempDDL drt) throws Exception{
//		String drawId = drt.getDrawId();
//		GaeDrawDDL draw = GaeDrawService.getDraw(drawId);
//		if(draw == null){
//			return false;
//		}
//		int thisHeadCount = draw.getThisHeadCount();
//		int headCount = draw.getHeadCount();
//		Logger.info("用户参与抢红包失败，人数已满========>，headCount:" + headCount + ",thisHeadCount：" + thisHeadCount);
//		if(thisHeadCount >= headCount){
//			Logger.info("用户参与抢红包失败，人数已满，uid:" + drt.getUserId() + ",期数：" + drt.getDrawId());
//			return false;
//		}
		int result = Dal.insert(drt);
		if(result != 1){
			throw new UnfitResultException("插入参与用户信息失败");
		}
		return true;
	}
	
	
	/**
	 * 插入用户中奖记录(删除对应临时表的记录)
	 * 
	 * @param gdrTempId
	 * @param drt
	 * @return
	 */
	public static boolean insertDrawRecord(int gdrTempId, GaeDrawRecordDDL drt){
		Condition cond = new Condition("GaeDrawRecordTempDDL.id", "=", gdrTempId);
		int i = Dal.delete(cond);
		if(i != 1){
			return false;
		}
		return Dal.insert(drt) == 1;
	}
	
	/**
	 * 获取用户的中奖信息
	 * 
	 * @param uid
	 */
	public static Map<String, Integer> getMyPrize(int uid){
		Map<String, Integer> map = new HashMap<String, Integer>();
		map.put("gainToday", 0);
		map.put("gainTotal", 0);
		
		try {
			StringBuffer sql = new StringBuffer("select hit_time, sum(hit_beans) as gain from gae_draw_record");
			sql.append(" where user_id = " + uid);
			sql.append(" group by (datediff(now(),hit_time) > 0)");
			sql.append(" order by hit_time asc");
			   
			ResultSet set = Dal.executeQuery(GlobalConstants.dbSource, sql.toString());
			while(set.next()){
				Date date = set.getDate("hit_time");
				if(DateUtils.isSameDay(date, new Date())){
					map.put("gainToday", set.getInt("gain"));
				}
				map.put("gainTotal", set.getInt("gain") + map.get("gainTotal"));
			}
		} catch (Exception e) {}
		return map;
	}	
}
