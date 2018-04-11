package moudles.guess.service;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import jws.Jws;
import jws.Logger;
import jws.cache.Cache;
import jws.dal.Dal;
import jws.dal.common.SqlParam;
import jws.dal.sqlbuilder.Condition;
import jws.dal.sqlbuilder.Sort;
import jws.module.constants.award.AwardScope;
import jws.module.constants.guess.GameLevel;
import jws.module.constants.guess.GuessSeasonStatus;
import jws.module.constants.member.MemberLogOpType;
import jws.module.constants.order.OrderType;
import moudles.guess.ddl.GuessAwardDDL;
import moudles.guess.ddl.GuessBoobyRecordDDL;
import moudles.guess.ddl.GuessRecordDDL;
import moudles.guess.ddl.GuessRecordTempDDL;
import moudles.guess.ddl.GuessSeasonCurrentDDL;
import moudles.guess.ddl.GuessSeasonDDL;
import moudles.member.ddl.MemberDDL;
import moudles.member.service.MemberService;
import moudles.order.service.OrderService;
import moudles.robot.ddl.RobotInfoDDL;
import moudles.robot.service.RobotService;
import moudles.task.service.GameTaskService;

import org.apache.commons.lang.StringUtils;

import utils.DaoUtil;
import utils.DateUtil;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.jfinal.plugin.activerecord.Db;

import constants.MessageCode;
import constants.SelfGame;
import exception.BusinessException;

public class GuessSeasonService {

	private static String baseUrl = Jws.configuration.getProperty("h5game.web.inex", "http://game.8868.cn/");

	/**
	 * 创建夺宝期
	 * 
	 * @param ddl
	 * @return
	 */
	public static long create(GuessSeasonDDL ddl) {
		return Dal.insertSelectLastId(ddl);
	}

	/**
	 * 更新夺宝期
	 * 
	 * @param ddl
	 * @return
	 */
	public static boolean update(GuessSeasonDDL ddl) {
		Condition cond = new Condition("GuessSeasonDDL.id", "=", ddl.getId());
		String updated = "GuessSeasonDDL.winnerUid,GuessSeasonDDL.raiseBean,GuessSeasonDDL.jackpot,GuessSeasonDDL.code,GuessSeasonDDL.publishTime,GuessSeasonDDL.updateTime,GuessSeasonDDL.status";
		return Dal.update(ddl, updated, cond) > 0;
	}

	/**
	 * 查找夺宝期
	 * 
	 * @param startTime
	 * @param endTime
	 * @param page
	 * @param pageSize
	 * @return
	 */
	public static List<GuessSeasonDDL> list(int uid, int seasonNum, long startTime, long endTime, int page, int pageSize) {
		Condition cond = new Condition("GuessSeasonDDL.id", ">", "0");
		if (uid > 0) {
			cond.add(new Condition("GuessSeasonDDL.winnerUid", "=", uid), "and");
		}
		if (seasonNum > 0) {
			cond.add(new Condition("GuessSeasonDDL.seasonNum", "=", seasonNum), "and");
		}
		if (startTime > 0 && endTime > 0) {
			cond.add(new Condition("GuessSeasonDDL.createTime", ">", startTime), "and");
			cond.add(new Condition("GuessSeasonDDL.createTime", "<", endTime), "and");
		}
		Sort sort = new Sort("GuessSeasonDDL.createTime", false);
		return Dal.select(DaoUtil.genAllFields(GuessSeasonDDL.class), cond, sort, (page - 1) * pageSize, pageSize);
	}

	/**
	 * 获取指定序号夺宝期
	 * 
	 * @param seasonNum
	 * @param gameLevel
	 * @return
	 */
	public static GuessSeasonDDL getSeasonByNum(int seasonNum, int gameLevel) {
		Condition cond = new Condition("GuessSeasonDDL.seasonNum", "=", seasonNum);
		cond.add(new Condition("GuessSeasonDDL.gameLevel", "=", gameLevel), "and");
		List<GuessSeasonDDL> list = Dal.select(DaoUtil.genAllFields(GuessSeasonDDL.class), cond, null, 0, 1);
		if (list != null && list.size() == 1) {
			return list.get(0);
		}
		return null;
	}

	/**
	 * 查找夺宝期
	 * 
	 * @param id
	 * @return
	 */
	public static GuessSeasonDDL getById(int id) {
		if (id == 0) {
			return null;
		}
		Condition cond = new Condition("GuessSeasonDDL.id", "=", id);
		List<GuessSeasonDDL> list = Dal.select(DaoUtil.genAllFields(GuessSeasonDDL.class), cond, null, 0, 1);
		if (list != null && list.size() == 1) {
			return list.get(0);
		}
		return null;
	}

	/**
	 * 获取当前期信息
	 */
	public static GuessSeasonDDL getCurrentSeason(int gameLevel, int awardId) {
		Condition cond = new Condition("GuessSeasonDDL.status", "=", GuessSeasonStatus.PROCESS.getValue());
		cond.add(new Condition("GuessSeasonDDL.gameLevel", "=", gameLevel), "and");
		if (awardId > 0) {
			cond.add(new Condition("GuessSeasonDDL.guessAwardId", "=", awardId), "and");
		}
		Sort sort = new Sort("GuessSeasonDDL.createTime", false);
		List<GuessSeasonDDL> list = Dal.select(DaoUtil.genAllFields(GuessSeasonDDL.class), cond, sort, 0, 1);
		if (list != null && list.size() > 0) {
			return list.get(0);
		}
		return null;
	}

	/**
	 * 获取最近一期
	 * 
	 * @param gameLevel
	 * @param awardId
	 * @return
	 */
	public static GuessSeasonDDL getLatestSeason(int gameLevel, int awardId) {
		Condition cond = new Condition("GuessSeasonDDL.status", "=", GuessSeasonStatus.PUBLISHED.getValue());
		cond.add(new Condition("GuessSeasonDDL.gameLevel", "=", gameLevel), "and");
		if (awardId > 0) {
			cond.add(new Condition("GuessSeasonDDL.guessAwardId", "=", awardId), "and");
		}
		Sort sort = new Sort("GuessSeasonDDL.createTime", false);
		List<GuessSeasonDDL> list = Dal.select(DaoUtil.genAllFields(GuessSeasonDDL.class), cond, sort, 0, 1);
		if (list != null && list.size() > 0) {
			return list.get(0);
		}
		return null;
	}

	// 参与夺宝(供第三方使用) TODO 待重构
	public static Map<String, String> joinByOrder(GuessSeasonCurrentDDL curSeason, int uid, double price, int amount, int gameLevel, boolean fromOutSide, String caller,
			String zhifuOrderId, String title) {

		GuessSeasonDDL season = GuessSeasonService.getById(curSeason.getGuessSeasonId());
		if (season == null) {
			Logger.error("GuessSeasonService.join - 找不到当前期信息");
			return null;
		}

		GuessAwardDDL award = GuessAwardService.getById(curSeason.getGuessAwardId());
		if (award == null) {
			Logger.error("GuessSeasonService.join - 找不到奖品信息");
			return null;
		}

		try {

			int happyBean = award.getHappyBean() * amount;
			int payBean = new Double(price * 1000).intValue();
			int needBean = happyBean - payBean;

			// 充值
			Map orderParam = new HashMap();
			orderParam.put("uid", String.valueOf(uid));
			orderParam.put("productName", award.getName());
			orderParam.put("orderType", String.valueOf(OrderType.RECHARGE_OUTSIDE.getType()));
			orderParam.put("happyBean", String.valueOf(payBean));
			orderParam.put("sourceDesc", SelfGame.GAME_GUESS.getGameName());
			orderParam.put("remark", "充值-" + title);
			orderParam.put("gameId", String.valueOf(SelfGame.GAME_GUESS.getGameId()));
			orderParam.put("gameName", SelfGame.GAME_GUESS.getGameName());
			orderParam.put("awardScope", String.valueOf(AwardScope.GAME.getType()));
			orderParam.put("channel", caller);
			orderParam.put("title", title);
			orderParam.put("zhifuOrderId", zhifuOrderId);

			Map orderResult = OrderService.createOrder(orderParam);
			if (orderResult == null || (orderResult.containsKey("result") && orderResult.get("result").equals("FAIL"))) {
				Logger.error("GuessSeasonService.join - 创建订单失败");
				return null;
			}

			// 送豆
			if (needBean > 0) {
				Map<String, String> addBeanParams = new HashMap<String, String>();
				addBeanParams.put("remark", "赠送-" + title);
				addBeanParams.put("gameId", String.valueOf(SelfGame.GAME_GUESS.getGameId()));
				addBeanParams.put("billId", zhifuOrderId);
				String lockKey = String.format("gid_%d-gl_%d-act_%s-uid_%d", SelfGame.GAME_GUESS.getGameId(), award.getGameLevel(), "GuessPresentBean", uid);
				boolean flag = MemberService.addBean(uid, needBean, MemberLogOpType.PRESENTED.getType(), addBeanParams, lockKey);
				if (!flag) {
					Logger.error("加豆失败, --> params: %s", new Gson().toJson(addBeanParams));
				}
			}

			Map<String, String> res = buyCode(uid, false, season, award, amount, true, payBean, zhifuOrderId, caller, title, false);
			if (res == null) {
				Logger.error("GuessSeasonService.join - 购买夺宝码失败,返回为空");
				return null;
			}

			if (res.get("result").equalsIgnoreCase("fail")) {
				String msg = "";
				if (res.containsKey("msg")) {
					msg = res.get(msg);
				}
				Logger.error("GuessSeasonService.join - 购买夺宝码失败:%s", msg);
				return null;
			}
			return formatResult(0, title, gameLevel, curSeason.getGuessSeasonId(), curSeason.getSeasonNum(), res.get("codeStr"), 0, curSeason.getPublishTime(), caller);
		} catch (Exception e) {
			Logger.error("GuessSeasonService.join - 服务器内部错误:%s", e.getMessage());
			return null;
		}
	}

	/**
	 * 获取夺宝参与结果(供第三方使用)
	 * 
	 * @param params
	 * @return
	 */
	public static Map<String, String> getJoinResultByOrder(Map params) {
		int uid = Double.valueOf(params.get("uid").toString()).intValue();
		String zhifuOrderId = params.get("zhifuOrderId").toString();
		String caller = params.get("caller").toString().concat("order");

		if (uid <= 0) {
			Logger.error("无效uid");
			return null;
		}

		if (StringUtils.isEmpty(zhifuOrderId)) {
			Logger.error("zhifuOrderId 为空");
			return null;
		}

		GuessRecordDDL record = GuessRecordService.getByZhifuOrderId(zhifuOrderId, uid);
		if (record == null) {
			return null;
		}

		int st = record.getHit();
		String title = record.getTitle();
		String code = record.getTitle();
		int bean = record.getRewardBean();
		int gl = record.getGameLevel();
		int seasonNum = record.getSeasonNum();
		long publishTime = record.getPublishTime();
		int sid = record.getGuessSeasonId();

		return formatResult(st, title, gl, sid, seasonNum, code, bean, publishTime, caller);
	}

	/**
	 * 格式化返回结果
	 * 
	 * @param st
	 * @param title
	 * @param gl
	 * @param sid
	 * @param seasonNum
	 * @param code
	 * @param bean
	 * @param publishTime
	 * @param channel
	 * @return
	 */
	private static Map<String, String> formatResult(int st, String title, int gl, int sid, int seasonNum, String code, int bean, long publishTime, String channel) {
		Map<String, String> result = new HashMap<String, String>();
		String content = "";
		String status = "";
		String url = "";

		String gameLevel = GameLevel.getGameLevel(gl).getDesc();
		String time = DateUtil.formatDate(publishTime, "yyyy年MM月dd日 HH:mm:ss");

		// 未开奖
		if (st == 0) {
			content = String.format("%s，第%d期，您的夺宝码是%s\n开奖前还能继续押注哦，开奖时间%s", gameLevel, seasonNum, code, time);
			status = "未开奖";
			// url = String.format("%sGuess/index?gl=%d&channel=%s", baseUrl,
			// gl, channel);
			url = String.format("%sGuess/index?gl=%d", baseUrl, gl);
		}

		// 已中奖
		if (st == 1) {
			content = String.format("恭喜你中奖了！%d开心豆已经发放到您的账户中。\n趁着手气好，赢取更多开心豆换大奖吧!", bean);
			status = "中奖了";
			// url = String.format("%sGuess/season?sid=%d&channel=%s", baseUrl,
			// sid, channel);
			url = String.format("%sGuess/season?sid=%d", baseUrl, sid);
		}

		// 未中奖
		if (st == 2) {
			content = "很遗憾，与大奖擦肩而过！\n前往开心大厅再接再厉，赢取开心豆换大奖吧！";
			status = "未中奖";
			// url = String.format("%sGuess/season?sid=%d&channel=%s", baseUrl,
			// sid, channel);
			url = String.format("%sGuess/season?sid=%d", baseUrl, sid);
		}
		result.put("title", title);
		result.put("content", content);
		result.put("status", status);
		result.put("url", url);
		return result;
	}

	/**
	 * 购买夺宝码
	 * 
	 * @param uid
	 * @param isRobot
	 * @param currentSeason
	 * @param currentAward
	 * @param codeAmount
	 * @return
	 * @throws BusinessException
	 */
	public static Map buyCode(int uid, boolean isRobot, GuessSeasonDDL season, GuessAwardDDL currentAward, int codeAmount, boolean fromOutside, int payBean, String zhifuOrderId,
			String ch, String title, boolean isAllIn) throws BusinessException {
		Map result = new HashMap();
		result.put("result", "FAIL");
		result.put("msg", "网络繁忙，请稍后重试");

		if (uid <= 0 || codeAmount <= 0) {
			Logger.info("uid:%s, codeAmount:%s", uid, codeAmount);
			throw new BusinessException(MessageCode.ERROR_CODE_500, "参数校验不通过");
		}

		Logger.info("season:%s ,currentAward:%s", new Gson().toJson(season), new Gson().toJson(currentAward));
		if (season == null || currentAward == null) {
			Logger.error("GuesssSeasonService.buyCode - season or currentAward is null");
			result.put("result", "FAIL");
			result.put("msg", "网络繁忙，请稍后重试");
			result.put("bean", String.valueOf(0));
			result.put("time", String.valueOf(System.currentTimeMillis()));
			return result;
		}

		GuessSeasonCurrentDDL curSeason = GuessSeasonCurrentService.get(season.getGameLevel(), season.getSeasonNum());
		Logger.info("curSeason:%s", new Gson().toJson(curSeason));
		if (curSeason == null) {
			Logger.error("GuesssSeasonService.buyCode - curSeason is null");
			result.put("result", "FAIL");
			result.put("msg", "网络繁忙，请稍后重试");
			result.put("bean", String.valueOf(0));
			result.put("time", String.valueOf(System.currentTimeMillis()));
			return result;
		}

		// 开奖前1秒停止购买
		boolean expired = System.currentTimeMillis() > (curSeason.getPublishTime() - 1000);
		int cost = codeAmount * curSeason.getBaseBean();

		if (expired) {
			Logger.error("GuesssSeasonService.buyCode - 即将开奖停止购买");
			result.put("result", "FAIL");
			result.put("msg", "本期已结束，请购买下一期");
			result.put("bean", String.valueOf(cost));
			result.put("time", String.valueOf(System.currentTimeMillis()));
			return result;
		}

		String nickname = "";
		if (!isRobot) {
			MemberDDL member = MemberService.getMemberByUid(uid);
			if (member == null) {
				throw new BusinessException(MessageCode.ERROR_CODE_500, "找不到用户:" + uid);
			}
			int balance = member.getHappyBean() + member.getHappyBeanFromOp();
			if (cost > balance) {
				throw new BusinessException(MessageCode.ERROR_CODE_500, "开心豆不足，购买失败");
			}

			// 校验
			if (isAllIn) {
				isAllIn = cost + curSeason.getBaseBean() > balance;
			}

			nickname = replaceWithStar(member.getNickName(), member.getMobile(), String.valueOf(uid));

			// 更新个人安慰奖奖池
			if (Boolean.valueOf(Jws.configuration.getProperty("guess_game.booby.enabled", "false"))) {
				GuessBoobyRecordDDL booby = GuessBoobyRecordService.getByAwardId(uid, currentAward.getId());
				if (booby == null) {
					booby = new GuessBoobyRecordDDL();
					booby.setHappyBean(cost);
					booby.setGuessAwardId(currentAward.getId());
				} else {
					booby.setHappyBean(booby.getHappyBean() + cost);
				}
				booby.setUid(uid);
				booby.setUpdateTime(System.currentTimeMillis());
				if (!GuessBoobyRecordService.createOrUpdate(booby)) {
					Logger.error("GuesssSeasonService.buyCode - 更新Or创建安慰奖记录失败.");
				}
			}
		} else {
			RobotInfoDDL robot = RobotService.getByUid(uid);
			if (robot == null) {
				Logger.error("GuesssSeasonService.buyCode - 找不到机器人,uid:%s", uid);
				return null;
			}
			nickname = robot.getNickname();
		}

		Map orderParam = new HashMap();
		orderParam.put("uid", String.valueOf(uid));
		orderParam.put("productName", currentAward.getName());
		orderParam.put("orderType", String.valueOf(OrderType.CONSUME.getType()));
		orderParam.put("happyBean", String.valueOf(cost));
		orderParam.put("sourceDesc", SelfGame.GAME_GUESS.getGameName());
		orderParam.put("remark", SelfGame.GAME_GUESS.getGameName() + "-" + currentAward.getName());
		orderParam.put("gameId", String.valueOf(SelfGame.GAME_GUESS.getGameId()));
		orderParam.put("gameName", SelfGame.GAME_GUESS.getGameName());
		orderParam.put("awardScope", String.valueOf(AwardScope.GAME.getType()));
		if (StringUtils.isNotBlank(ch)) {
			orderParam.put("channel", ch);
		}

		String orderId = "";
		Map orderResult = null;

		if (!isRobot) {
			// 创建订单、扣豆
			orderResult = OrderService.createOrder(orderParam);
			if (orderResult == null || (orderResult.containsKey("result") && orderResult.get("result").equals("FAIL"))) {
				throw new BusinessException(MessageCode.ERROR_CODE_500, "创建订单失败");
			}
			orderId = orderResult.get("orderId").toString();
		} else {
			// 机器人不创建订单
			int rnd = new Random().nextInt(899999) + 100000; // 生成随机六位数
			orderId = "H5GMORD-" + System.currentTimeMillis() + "-" + rnd;
		}

		Map refundParams = new HashMap();

		long createTime = System.currentTimeMillis();
		int millis = Integer.valueOf(new SimpleDateFormat("SSS").format(new Date(createTime)));
		List<Integer> codes = new ArrayList<Integer>();

		GuessRecordTempDDL recordTemp = new GuessRecordTempDDL();
		recordTemp.setSeasonId(season.getId());
		recordTemp.setCodeAmount(codeAmount);
		int code = GuessRecordTempService.insertAndGetCount(recordTemp);

		// 创建夺宝记录
		for (int i = 0; i < codeAmount; i++) {
			code++;
			codes.add(code);
			if (code <= 0) { // 全额退还
				Logger.error("夺宝码为无效,code:%s", code);
				if (!isRobot) {
					// 退还开心豆
					refundParams.clear();
					refundParams.put("remark", "购买夺宝码失败-返还豆");
					refundParams.put("gameId", String.valueOf(SelfGame.GAME_GUESS.getGameId()));
					String lockKey = String.format("gid_%d-gl_%d-act_%s-uid_%d", SelfGame.GAME_GUESS.getGameId(), season.getGameLevel(), "GuessRufundBean", uid);
					MemberService.addBean(uid, cost, MemberLogOpType.REFUND.getType(), refundParams, lockKey);
					Logger.info("GuessSeasonService.buyCode - refund, uid:%s, refundBean:%s ,seasonId:%s", uid, cost, season.getId());

					result.put("result", "FAIL");
					result.put("msg", "网络繁忙，请稍后重试");
					result.put("bean", String.valueOf(cost));
					result.put("time", String.valueOf(System.currentTimeMillis()));
				}
				return result;
			}
		}

		Logger.info("GuessSeasonService.buyCode - uid:%s, seasonId:%s, codes:%s", uid, season.getId(), new Gson().toJson(codes));

		GuessRecordDDL record = new GuessRecordDDL();
		record.setUid(uid);
		record.setNickname(nickname);
		record.setCode(StringUtils.join(codes, ","));
		record.setIsAllIn(isAllIn ? 1 : 2);
		record.setMillis(millis);
		record.setGuessAwardId(currentAward.getId());
		record.setSeasonNum(season.getSeasonNum());
		record.setHappyBean(cost);
		record.setGuessSeasonId(season.getId());
		record.setGameLevel(season.getGameLevel());
		record.setIsRobot(isRobot == true ? 1 : 0);
		record.setCreateTime(createTime);
		record.setUpdateTime(createTime);
		record.setOrderId(orderId);
		record.setCodeAmount(codeAmount);
		record.setHit(0);
		record.setTitle(title);
		record.setStatus(1);
		record.setIsRead(0);
		record.setIsBooby(0);
		record.setZhifuOrderId(zhifuOrderId);
		record.setPublishTime(curSeason.getPublishTime());

		if (!GuessRecordService.create(record)) {
			// 退还开心豆
			if (!isRobot) {
				refundParams.clear();
				refundParams.put("remark", "购买夺宝码失败-返还豆");
				refundParams.put("gameId", String.valueOf(SelfGame.GAME_GUESS.getGameId()));
				String lockKey = String.format("gid_%d-gl_%d-act_%s-uid_%d", SelfGame.GAME_GUESS.getGameId(), season.getGameLevel(), "GuessRufundBean", uid);
				MemberService.addBean(uid, cost, MemberLogOpType.REFUND.getType(), refundParams, lockKey);
				Logger.info("GuessSeasonService.buyCode - refund, uid:%s,refundBean:%s ,seasonId:%s", uid, cost, season.getId());
			}
			result.put("result", "FAIL");
			result.put("msg", "网络繁忙，请稍后重试");
			result.put("bean", String.valueOf(cost));
			result.put("time", String.valueOf(System.currentTimeMillis()));
			return result;
		}

//		season.setJackpot(season.getJackpot() + cost);
		// 注意下面的代码不要使用season.getJackpot()，数据未更新
//		if (!update(season)) {
		if(updateGuessSeasonJackpot(season.getId(), cost)){// 解决并发导致的追加奖池问题
			Logger.error("GuessService.buyCode - 更新奖池失败.");
		}

//		curSeason.setCurrentBean(curSeason.getCurrentBean() + cost);
//		if (!GuessSeasonCurrentService.update(curSeason)) {
		if (!GuessSeasonCurrentService.addCurrentBean(curSeason.getGuessSeasonId(), cost)) {
			Logger.error("GuessService.buyCode - 更新当前奖池失败.");
		}

		// 真实用户参与任务
		if (!isRobot) {
			GameTaskService.doTask(uid, SelfGame.GAME_GUESS.getGameId(), new Long(season.getGameLevel()).intValue(), cost, isAllIn);
		}
		deleteCache(season.getId());
		result.put("result", "SUCCESS");
		result.put("msg", "购买成功");
		result.put("codeStr", StringUtils.join(codes, ","));
		result.put("bean", String.valueOf(cost));
		result.put("time", String.valueOf(createTime));
		return result;
	}
	
	/**
	 * 向奖池中追加豆
	 */
	private static boolean updateGuessSeasonJackpot(int sessionId, int addBeans){
		Logger.info("GuessSeasonId="+sessionId+",追加奖池："+addBeans);
		
		List<SqlParam> params = new ArrayList<SqlParam>();
    	params.add(new SqlParam("GuessSeasonDDL.jackpot", addBeans));
    	params.add(new SqlParam("GuessSeasonDDL.id", sessionId));
    	// 此处不使用下面注释的代码，是因为如果该方法在一个事务中被调用，事务对注释代码是起不了作用的
		return Dal.executeNonQuery(GuessSeasonDDL.class, "update guess_season set jackpot=jackpot+?,update_time=now() where id=? ", params, null)>0;
//		return Db.update("update guess_season set jackpot=jackpot+?,update_time=now() where id=? ", addBeans, sessionId) > 0;
	}
	
	/**
	 * 购买某期的码后，删除该期对应的缓存
	 * @param seasonId
	 */
	private static void deleteCache(int seasonId){
		try{
			Cache.delete(GuessWinService.GUESS_WIN_UID_CACHE_KEY_PREFIX+seasonId);// 删除之前计算的中奖的用户
			Cache.delete(GuessWinService.GUESS_WIN_GUESS_UID_CACHE_KEY_PREFIX+seasonId);// 删除之前计算的预测中奖的用户
		}catch(Exception e){
			Logger.warn("GuessSeasonService.deleteCache - 删除缓存异常("+e.getMessage()+").", e);
		}
	}

	// 昵称打星
	private static String replaceWithStar(String nickname, String mobile, String uid) {
		if (StringUtils.isEmpty(nickname)) {
			nickname = mobile;
		}
		if (StringUtils.isNotEmpty(nickname) && nickname.length() == 11) {
			nickname = (new StringBuilder(nickname.substring(0, 3)).append("****").append(nickname.substring(7, 11))).toString();
		}
		if (StringUtils.isEmpty(nickname)) {
			uid = uid.length() > 4 ? uid.substring(0, 4) : uid;
			nickname = (new StringBuilder("用户").append(uid).insert(3, "**")).toString();
		}
		return nickname;
	}

	/**
	 * 获取当前期奖池
	 * 
	 * @param seasonId
	 * @return
	 */
	public static int getTotalBeanBySeason(int seasonId) {
		StringBuffer sql = new StringBuffer("select jackpot from guess_season where id = ");
		sql.append(seasonId);
		int count = Dal.executeCount(GuessRecordDDL.class, sql.toString());
		return count;
	}

}
