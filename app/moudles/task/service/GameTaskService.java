package moudles.task.service;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import jws.Jws;
import jws.Logger;
import jws.dal.Dal;
import jws.dal.sqlbuilder.Condition;
import jws.dal.sqlbuilder.Sort;
import jws.module.constants.doll.ChanceReadStatus;
import jws.module.constants.doll.ChanceStatus;
import jws.module.constants.doll.ChanceType;
import jws.module.constants.doll.GameLevel;
import jws.module.constants.game.SelfGame;
import jws.module.constants.task.GameTaskAwardStatus;
import jws.module.constants.task.GameTaskAwardType;
import jws.module.constants.task.GameTaskStatus;
import jws.module.constants.task.GameTaskType;
import jws.module.response.task.GetTaskResp;
import moudles.chance.ddl.DollChanceRecordDDL;
import moudles.chance.service.DollChanceRecordService;
import moudles.task.ddl.GameTaskDDL;
import moudles.task.ddl.GameTaskRecordDDL;
import utils.DaoUtil;
import utils.DateUtil;

import com.google.gson.Gson;

import constants.GlobalConstants;

public class GameTaskService {

	/**
	 * 获取游戏任务列表
	 * 
	 * @param gameId
	 * @param status
	 * @param page
	 * @param pageSize
	 * @return
	 */
	public static List<GameTaskDDL> listByGame(int gameId, int type[], int status, int page, int pageSize) {
		Condition cond = new Condition("GameTaskDDL.gameId", "=", gameId);
		if (type != null && type.length > 0) {
			cond.add(new Condition("GameTaskDDL.taskType", "in", type), "and");
		}
		if (status > 0) {
			cond.add(new Condition("GameTaskDDL.status", "=", status), "and");
		}
		Sort sort = new Sort("GameTaskDDL.sort", true);
		return Dal.select(DaoUtil.genAllFields(GameTaskDDL.class), cond, sort, (page - 1) * pageSize, pageSize);
	}

	/**
	 * 获取任务列表
	 * 
	 * @param uid
	 * @param gameId
	 * @param indexShow
	 * @param page
	 * @param pageSize
	 * @return
	 */
	public static List<GetTaskResp> list(int uid, int gameId, int indexShow, int page, int pageSize) {
		StringBuilder sql = new StringBuilder(
				"select t.award_type,t.task_type,t.game_id,t.icon_url,t.task_url,t.task_desc,t.award_desc,t.target,r.complete_num,if(r.complete_num>=r.target,1,0) as task_status ");
		sql.append("from game_task as t left join game_task_record as r on t.id = r.task_id");
		sql.append(" and r.uid = ");
		sql.append(uid);
		sql.append(" and UNIX_TIMESTAMP(r.create_time) * 1000 >=");
		sql.append(DateUtil.getTodayStartEndTime()[0]);
		sql.append(" and UNIX_TIMESTAMP(r.create_time) * 1000 <=");
		sql.append(DateUtil.getTodayStartEndTime()[1]);
		sql.append(" where t.status = ");
		sql.append(GameTaskStatus.ONLINE.getStatus());
		if (indexShow > 0) {
			sql.append(" and t.index_show = ");
			sql.append(indexShow);
		}
		if (gameId > 0) {
			sql.append(" and t.game_id = ");
			sql.append(gameId);
		}
		sql.append(" group by t.id ");
		sql.append(" order by task_status,sort");
		if (page > 0 && pageSize > 0) {
			sql.append(" limit ");
			sql.append((page - 1) * pageSize);
			sql.append(" , ");
			sql.append(pageSize);
		}

		GetTaskResp dto = null;
		List<GetTaskResp> list = new ArrayList<GetTaskResp>();
		ResultSet result = Dal.executeQuery(GlobalConstants.dbSource, sql.toString());
		if (result != null) {
			try {
				while (result.next()) {
					dto = new GetTaskResp();
					dto.setGameId(result.getInt("game_id"));
					dto.setTaskType(result.getInt("task_type"));
					dto.setAwardType(result.getInt("award_type"));
					dto.setAwardDesc(result.getString("award_desc"));
					dto.setIcon(result.getString("icon_url"));
					dto.setTaskDesc(result.getString("task_desc"));
					dto.setCustom(result.getInt("task_type") == GameTaskType.CUSTOM.getType() ? true : false);
					dto.setUrl(result.getString("task_url"));
					dto.setCompleteNum(result.getInt("complete_num"));
					dto.setTarget(result.getInt("target"));
					dto.setCompleted((result.getInt("task_status") > 0 && uid > 0) ? true : false);
					list.add(dto);
				}
			} catch (SQLException e) {
				Logger.error(e.getMessage());
			} finally {
				try {
					result.close();
				} catch (SQLException e) {
					Logger.error(e.getMessage());
				}
			}
		}
		return list;
	}

	/**
	 * 
	 * @param uid
	 * @param gameId
	 * @param happyBean
	 */
	public static void doTask(int uid, int gameId, long gameLevel, int happyBean, boolean isAllIn) {

		int taskType[] = { GameTaskType.ALL_IN.getType(), GameTaskType.JOIN.getType() };

		List<GameTaskDDL> gameList = GameTaskService.listByGame(gameId, taskType, GameTaskStatus.ONLINE.getStatus(), 1, -1);
		if (gameList == null || gameList.size() == 0) {
			return;
		}

		String gameName = "";
		SelfGame game = SelfGame.getById(gameId);
		if (game != null) {
			gameName = game.getGameName();
		}

		for (GameTaskDDL task : gameList) {

			// 夹娃娃特权场不参与任务
			if (gameLevel == GameLevel.GAME_LEVEL_SPECIAL.getLevel() && gameId == SelfGame.GAME_DOLL.getGameId()) {
				continue;
			}

			// 更新/创建任务记录
			if (task.getGameLevel() == 0 || task.getGameLevel() == gameLevel) {
				updateTaskRecord(uid, gameId, gameName, task, happyBean, isAllIn);
			}
		}
	}

	/**
	 * 更新/创建任务记录
	 * 
	 * @param uid
	 * @param gameName
	 * @param task
	 * @param happyBean
	 */
	private static void updateTaskRecord(int uid, int gameId, String gameName, GameTaskDDL task, int happyBean, boolean isAllIn) {

		// 任务类型
		int taskType = task.getTaskType();

		// 奖励类型
		int awardType = task.getAwardType();

		// 送夹娃娃标记
		boolean trailAwardFlag = false;

		// 返还豆上限
		int returnBeanLimit = Integer.parseInt(Jws.configuration.getProperty("game_task.return_bean.limit"));

		// 奖励数
		int awardNum = task.getAwardNum();

		// 判断AllIn是否为效
		if (taskType == GameTaskType.ALL_IN.getType()) {
			if (!isAllIn) {
				return;
			}

			// 投入豆没达到任务指标，则返回
			if (happyBean < task.getTarget()) {
				return;
			}
		}

		// 奖品是否为赠送特权
		boolean isTrailAward = (awardType == GameTaskAwardType.TRIAL.getType()) ? true : false;

		// 任务完成次数
		int completeNum = 1;

		// 任务未完成标记
		boolean complete = false;

		GameTaskRecordDDL record = GameTaskRecordService.getTodayRecord(uid, task.getId());
		// 完成第一次任务
		if (record == null) {

			int awardStatus = GameTaskAwardStatus.TO_AWARD.getStatus();

			if (taskType == GameTaskType.ALL_IN.getType()) {
				completeNum = happyBean;

				// 奖品类型为 返还豆,做限制
				if (awardType == GameTaskAwardType.RETURN_BEAN.getType()) {
					// 返还比例
					Double returnRate = new Double((double) task.getAwardNum() / 100);
					awardNum = new Double(happyBean * returnRate).intValue();
					if (awardNum > returnBeanLimit) {
						Logger.warn("GameTaskService.updateTaskRecord -- 返还豆超出限制,limit:%d ,returnBean:%d", returnBeanLimit, awardNum);
						awardNum = returnBeanLimit;
					}
				}
			}

			// 完成任务并且奖励类型为赠送特权，发奖状态标记为已发放
			complete = (completeNum >= task.getTarget());
			if (complete && isTrailAward) {
				awardStatus = GameTaskAwardStatus.AWARDED.getStatus();
				trailAwardFlag = true;
			}

			record = new GameTaskRecordDDL();
			record.setUid(uid);
			record.setGameId(gameId);
			record.setTaskId(task.getId());
			record.setAwardNum(awardNum);
			record.setCompleteNum(completeNum);
			record.setTarget(task.getTarget());
			record.setAwardStatus(awardStatus);
			record.setCreateTime(System.currentTimeMillis());
			record.setUpdateTime(System.currentTimeMillis());
			record.setRemark(gameName + "-" + task.getTaskDesc());
			GameTaskRecordService.createRecord(record);
		} else {

			// 已完成
			if (record.getCompleteNum() >= record.getTarget()) {
				return;
			}

			completeNum = record.getCompleteNum() + 1;
			record.setCompleteNum(completeNum);

			complete = (completeNum >= task.getTarget());
			// 完成任务并且奖励类型为赠送特权，发奖状态标记为已发放
			if (complete && isTrailAward) {
				record.setAwardStatus(GameTaskAwardStatus.AWARDED.getStatus());
				trailAwardFlag = true;
			}

			record.setUpdateTime(System.currentTimeMillis());
			boolean updateFlag = GameTaskRecordService.updateRecord(record);

			if (!updateFlag) {
				Logger.error("GameTaskService.updateTaskRecord -- 更新任务记录失败,taskRecord:%s", new Gson().toJson(record));
				return;
			}
		}

		// 赠送夹娃娃机会
		if (trailAwardFlag) {
			presentTrail(record, gameName, task.getTaskDesc());
		}
	}

	/**
	 * 赠送夹娃娃机会
	 * 
	 * @param record
	 */
	private static void presentTrail(GameTaskRecordDDL record, String gameName, String taskDesc) {
		int days = Integer.parseInt(Jws.configuration.getProperty("doll_game.trial.expire"));
		long expire = DateUtil.addDay(System.currentTimeMillis(), days);

		DollChanceRecordDDL chance = new DollChanceRecordDDL();
		chance.setUid(record.getUid());
		chance.setExpire(expire);
		chance.setRemark("任务奖励-" + gameName);
		chance.setTitle(taskDesc);
		chance.setRemain(record.getAwardNum());
		chance.setChance(record.getAwardNum());
		chance.setType(ChanceType.PLAY_GAME.getType());
		chance.setCreateTime(System.currentTimeMillis());
		chance.setUpdateTime(System.currentTimeMillis());
		chance.setStatus(ChanceStatus.AVAILABLE.getValue());
		chance.setIsRead(ChanceReadStatus.UNREAD.getValue());

		if (!DollChanceRecordService.create(chance)) {
			Logger.error("GameTaskService.updateTaskRecord -- 赠送夹娃娃机会,chance:%s", new Gson().toJson(chance));
		}
	}

}
