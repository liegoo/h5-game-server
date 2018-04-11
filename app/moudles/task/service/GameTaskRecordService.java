package moudles.task.service;

import java.util.List;

import jws.dal.Dal;
import jws.dal.sqlbuilder.Condition;
import jws.dal.sqlbuilder.Sort;
import moudles.task.ddl.GameTaskRecordDDL;
import utils.DaoUtil;
import utils.DateUtil;

public class GameTaskRecordService {

	/**
	 * 创建任务记录
	 * 
	 * @param record
	 * @return
	 */
	public static boolean createRecord(GameTaskRecordDDL record) {
		return Dal.insert(record) > 0;
	}

	/**
	 * 更新任务记录
	 * 
	 * @param record
	 * @return
	 */
	public static boolean updateRecord(GameTaskRecordDDL record) {
		Condition cond = new Condition("GameTaskRecordDDL.id", "=", record.getId());
		String updated = "GameTaskRecordDDL.completeNum,GameTaskRecordDDL.awardNum,GameTaskRecordDDL.awardStatus,GameTaskRecordDDL.remark,GameTaskRecordDDL.updateTime";
		return Dal.update(record, updated, cond) > 0;
	}

	/**
	 * 获取任务记录
	 * 
	 * @param uid
	 * @param gameId
	 * @param taskType
	 * @param awardStatus
	 * @param page
	 * @param pageSize
	 * @return
	 */
	public static List<GameTaskRecordDDL> list(int uid, int gameId, int taskType, int awardStatus, long timeBegin, long timeEnd, int page, int pageSize) {
		Condition cond = new Condition("GameTaskRecordDDL.id", ">", 0);
		if (uid > 0) {
			cond.add(new Condition("GameTaskRecordDDL.uid", "=", uid), "and");
		}
		if (gameId > 0) {
			cond.add(new Condition("GameTaskRecordDDL.gameId", "=", gameId), "and");
		}
		if (taskType > 0) {
			cond.add(new Condition("GameTaskRecordDDL.taskType", "=", taskType), "and");
		}
		if (awardStatus > 0) {
			cond.add(new Condition("GameTaskRecordDDL.awardStatus", "=", awardStatus), "and");
		}
		if (timeBegin > 0 && timeEnd > 0) {
			cond.add(new Condition("GameTaskRecordDDL.createTime", ">=", timeBegin), "and");
			cond.add(new Condition("GameTaskRecordDDL.createTime", "<=", timeEnd), "and");
		}
		return Dal.select(DaoUtil.genAllFields(GameTaskRecordDDL.class), cond, new Sort("GameTaskRecordDDL.id", false), (page - 1) * pageSize, pageSize);
	}

	/**
	 * 获取今天的任务记录
	 */
	public static GameTaskRecordDDL getTodayRecord(int uid, int taskId) {
		Condition cond = new Condition("GameTaskRecordDDL.uid", "=", uid);
		cond.add(new Condition("GameTaskRecordDDL.taskId", "=", taskId), "and");
		cond.add(new Condition("GameTaskRecordDDL.createTime", ">=", DateUtil.getTodayStartEndTime()[0]), "and");
		cond.add(new Condition("GameTaskRecordDDL.createTime", "<=", DateUtil.getTodayStartEndTime()[1]), "and");
		List<GameTaskRecordDDL> list = Dal.select(DaoUtil.genAllFields(GameTaskRecordDDL.class), cond, null, 0, -1);
		if (list != null && list.size() > 0) {
			return list.get(0);
		}
		return null;
	}

}
