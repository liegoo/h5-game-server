package controllers.ucgc;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jws.Jws;
import jws.Logger;
import jws.dal.Dal;
import jws.dal.sqlbuilder.Condition;
import jws.module.constants.task.GameTaskType;
import jws.module.response.task.GetTaskResp;
import jws.module.response.task.ListTaskResp;
import moudles.game.ddl.GamesDDL;
import moudles.game.service.GameService;
import moudles.member.ddl.MemberDDL;
import moudles.member.service.MemberService;
import moudles.task.service.GameTaskService;

import org.apache.commons.lang3.StringUtils;

import utils.DateUtil;
import common.core.UcgcController;

public class Task extends UcgcController {

	private static String baseUrl = Jws.configuration.getProperty("h5game.web.inex", "http://game.8868.cn/");

	/**
	 * 获取任务列表
	 */
	public static void listTask() {
		Map params = getDTO(Map.class);

		Logger.info("Task.listTask --> params:%s", params);

		int uid = Integer.parseInt(params.get("uid").toString());
		int gameId = Integer.parseInt(params.get("gameId").toString());
		int indexShow = Integer.parseInt(params.get("indexShow").toString());
		int page = Integer.parseInt(params.get("page").toString());
		int pageSize = Integer.parseInt(params.get("pageSize").toString());

		// 判断用户昨天是否有签到
		boolean yesterdayCompleted = false;

		if (uid > 0) {
			MemberDDL member = MemberService.getMemberByUid(uid);
			if (member == null) {
				getHelper().returnError(-1, "用户不存在");
			}

			if (member.getLastCheckInTime() != null) {
				String lastCheckday = DateUtil.formatDate(member.getLastCheckInTime(), "yyyy-MM-dd");
				String yesterday = DateUtil.getYesterdayDate("yyyy-MM-dd");
				if (lastCheckday.equals(yesterday)) {
					yesterdayCompleted = true;
				}
			}

		}

		if (!yesterdayCompleted) {
			long dateBegin = DateUtil.getYesterdayStartEndTime()[0];
			long dateEnd = DateUtil.getYesterdayStartEndTime()[1];
			int count = count(uid, dateBegin, dateEnd);
			if (count > 0) {
				yesterdayCompleted = true;
			}
		}

		ListTaskResp resp = new ListTaskResp();
		// 获取任务列表
		List<GetTaskResp> list = GameTaskService.list(uid, gameId, indexShow, page, pageSize);

		for (GetTaskResp task : list) {
			// 如果任务类型为allIn，显示任务指标为1
			if (task.getTaskType() == GameTaskType.ALL_IN.getType()) {
				task.setTarget(1);
			}
			// 如果任务没有配置url则使用游戏默认
			if (StringUtils.isEmpty(task.getUrl())) {
				task.setUrl(getGameUrl(task.getGameId()));
			}
		}
		resp.setList(list);
		resp.setYesterdayCompleted(yesterdayCompleted);
		getHelper().returnSucc(resp);
	}

	static Map<Integer, String> urlMap = new HashMap<Integer, String>();

	/**
	 * 获取游戏地址
	 * 
	 * @param gameId
	 * @return
	 */
	private static String getGameUrl(int gameId) {
		String url = "";
		if (urlMap.isEmpty()) {
			if (baseUrl.endsWith("/")) {
				baseUrl = baseUrl.substring(0, baseUrl.length() - 1);
			}

			List<GamesDDL> games = GameService.listGames(0, 20);
			for (GamesDDL game : games) {
				urlMap.put(game.getGameId(), baseUrl + game.getGameUrl());
			}
		}
		if (urlMap.containsKey(gameId)) {
			url = urlMap.get(gameId);
		}
		return url;
	}

	/**
	 * 获取昨天参与任务次数
	 * 
	 * @param uid
	 * @param dateBegin
	 * @param dateEnd
	 * @return
	 */
	private static int count(int uid, long dateBegin, long dateEnd) {
		Condition cond = new Condition("GameTaskRecordDDL.uid", "=", uid);
		if (dateBegin > 0 && dateEnd > 0) {
			cond.add(new Condition("GameTaskRecordDDL.createTime", ">=", dateBegin), "and");
			cond.add(new Condition("GameTaskRecordDDL.createTime", "<=", dateEnd), "and");
		}
		return Dal.count(cond);
	}

}
