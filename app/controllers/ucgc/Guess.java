package controllers.ucgc;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jws.Jws;
import jws.Logger;
import jws.cache.Cache;
import jws.module.constants.award.AwardScope;
import jws.module.constants.guess.BoobyStatus;
import jws.module.constants.guess.GameLevel;
import jws.module.constants.guess.GuessHitStatus;
import jws.module.constants.guess.GuessSeasonStatus;
import jws.module.constants.member.MemberLogOpType;
import jws.module.constants.order.OrderType;
import jws.module.response.award.AwardDto;
import jws.module.response.guess.GetCurrentSeasonResp;
import jws.module.response.guess.GetJoinResultResp;
import jws.module.response.guess.GetPlayResultResp;
import jws.module.response.guess.GetRaiseRuleResp;
import jws.module.response.guess.GetRanklistResp;
import jws.module.response.guess.GetUserCodeResp;
import jws.module.response.guess.GetWinningRecordResp;
import jws.module.response.guess.GuessRankDto;
import jws.module.response.guess.JoinResp;
import jws.module.response.guess.ListPlayRecordResp;
import jws.module.response.guess.ListPlayersResp;
import jws.module.response.guess.ListWinningRecordResp;
import jws.module.response.guess.PlayRecordDto;
import moudles.award.ddl.AwardDDL;
import moudles.award.service.AwardService;
import moudles.guess.ddl.GuessAwardDDL;
import moudles.guess.ddl.GuessRecordDDL;
import moudles.guess.ddl.GuessSeasonCurrentDDL;
import moudles.guess.ddl.GuessSeasonDDL;
import moudles.guess.service.GuessAwardService;
import moudles.guess.service.GuessRecordService;
import moudles.guess.service.GuessSeasonCurrentService;
import moudles.guess.service.GuessSeasonService;
import moudles.member.ddl.MemberDDL;
import moudles.member.service.MemberService;
import moudles.order.service.OrderService;

import org.apache.commons.lang.StringUtils;

import utils.DateUtil;
import utils.DistributeCacheLock;
import utils.JsonToMap;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import common.core.UcgcController;
import constants.MessageCode;
import constants.SelfGame;
import constants.cache.CommonCacheKeyPrefix;
import exception.BusinessException;
import externals.account.AccountCenterService;

/**
 * 夺宝游戏
 * 
 * @author Coming
 */
public class Guess extends UcgcController {

	private static DistributeCacheLock lock = DistributeCacheLock.getInstance();
	private static String baseUrl = Jws.configuration.getProperty("h5game.web.inex", "http://game.8868.cn/");

	/**
	 * 获取往期中奖记录
	 * 
	 * @param gameLevel
	 * @param seassonNum为0
	 *            时，返回上一期中奖记录
	 * @return
	 * @throws Exception
	 */
	public static void getWinningRecord() {
		Map params = getDTO(Map.class);
		int gameLevel = Integer.parseInt(params.get("gameLevel").toString());
		int seasonNum = Integer.parseInt(params.get("seasonNum").toString());

		if (seasonNum == 0) {
			GuessSeasonCurrentDDL season = GuessSeasonCurrentService.get(gameLevel, 0);
			if (season != null && season.getSeasonNum() >= 2) {
				seasonNum = season.getSeasonNum() - 1;
			}
		}

		GetWinningRecordResp resp = GuessRecordService.getWinningRecord(gameLevel, seasonNum);
		if (resp == null) {
			Logger.error("中奖记录为空");
			resp = new GetWinningRecordResp();
		}
		getHelper().returnSucc(resp);
	}

	/**
	 * 获取往期中奖列表
	 * 
	 * @param gameLevel
	 * @param page
	 * @param pageSize
	 * @return
	 * @throws Exception
	 */
	public static void listWinningRecord() {

		Map params = getDTO(Map.class);
		int gameLevel = Integer.parseInt(params.get("gameLevel").toString());
		int page = Integer.parseInt(params.get("page").toString());
		int pageSize = Integer.parseInt(params.get("pageSize").toString());

		ListWinningRecordResp resp = new ListWinningRecordResp();
		List<GetWinningRecordResp> list = new ArrayList<GetWinningRecordResp>();
		List<GuessRecordDDL> records = GuessRecordService.listGuessRecord(0, -1, BoobyStatus.DISABLED.getValue(), -1, GuessHitStatus.YES.getValue(), gameLevel, page, pageSize);

		for (GuessRecordDDL record : records) {
			GetWinningRecordResp dto = new GetWinningRecordResp();
			dto.setAvatar("");
			dto.setAwardName("");
			dto.setCode(record.getWinningCode());
			dto.setGameLevel(record.getGameLevel());
			dto.setHappyBean(record.getRewardBean());
			dto.setNickname(record.getNickname());
			dto.setSeasonId(record.getGuessSeasonId());
			dto.setSeasonNum(record.getSeasonNum());
			list.add(dto);
		}
		resp.setList(list);

		getHelper().returnSucc(resp);
	}

	/**
	 * 获取最新中奖列表
	 * 
	 * @param gameLevel
	 * @param page
	 * @param pageSize
	 * @return
	 * @throws Exception
	 */
	public static void listLatestWinningRecord() {
		ListWinningRecordResp resp = GuessRecordService.listLatestWinningRecord();
		getHelper().returnSucc(resp);
	}

	/**
	 * 获取当前期信息
	 * 
	 * @param gameLevel
	 * @return
	 * @throws BusinessException
	 * @throws Exception
	 */
	public static void getCurrentSeason() throws BusinessException {
		GetCurrentSeasonResp resp = new GetCurrentSeasonResp();
		Map params = getDTO(Map.class);
		int gameLevel = Integer.parseInt(params.get("gameLevel").toString());

		GuessSeasonCurrentDDL curSeason = GuessSeasonCurrentService.get(gameLevel, 0);

		if (curSeason == null) {
			throw new BusinessException(MessageCode.ERROR_CODE_500, "找不到Season");
		}

		GuessAwardDDL guessAward = GuessAwardService.getById(curSeason.getGuessAwardId());
		if (guessAward == null) {
			throw new BusinessException(MessageCode.ERROR_CODE_500, "找不到GuessAward,seasonRecordId:" + curSeason.getGuessAwardId());
		}

		int currentBean = GuessSeasonService.getTotalBeanBySeason(curSeason.getGuessSeasonId());

		long countdown = curSeason.getPublishTime() - System.currentTimeMillis();
		if (countdown < 0) {
			countdown = 0;
		}
		resp.setCountdown(countdown);
		resp.setSeasonId(curSeason.getGuessSeasonId());
		resp.setCurrentBean(currentBean);
		resp.setSeasonNum(curSeason.getSeasonNum());
		resp.setBaseBean(curSeason.getBaseBean());

		Map<Integer, Integer> raiseMap = new HashMap<Integer, Integer>();
		String raiseRuleStr = curSeason.getRaiseRule();
		if (StringUtils.isNotEmpty(raiseRuleStr)) {
			String ruleList[] = raiseRuleStr.split(",");
			try {
				for (String str : ruleList) {
					String ruleMap[] = str.split("/");
					int target = Integer.parseInt(ruleMap[0]);
					int award = Integer.parseInt(ruleMap[1]);
					raiseMap.put(target, award);
				}
			} catch (Exception e) {
				Logger.error(e.getMessage());
			}
		}
		resp.setRaiseRule(raiseMap);
		getHelper().returnSucc(resp);
	}

	/**
	 * 根据开心豆获取奖品列表
	 * 
	 * @param bean
	 * @return
	 * @throws Exception
	 */
	public static void listAwardNameByBean() throws Exception {
		Map params = getDTO(Map.class);

		int bean = Integer.parseInt(params.get("bean").toString());
		int count = Integer.parseInt(params.get("count").toString());
		List<String> strList = AwardService.listAwardNameByBean(bean, AwardScope.AWARDCENTER.getType(), count);

		// 奖池未达到任何兑换条件时，取价值最小的三个奖品
		if (strList == null || strList.size() == 0) {
			strList = AwardService.listAwardNameByBean(0, AwardScope.AWARDCENTER.getType(), count);
			if (strList != null) {
				Collections.reverse(strList);
			}
		}
		getHelper().returnSucc(strList);
	}

	/**
	 * 
	 */
	public static void listAwardByBean() throws Exception {
		Map params = getDTO(Map.class);

		int scope = AwardScope.AWARDCENTER.getType();
		int count = Integer.parseInt(params.get("count").toString());
		int bean = Integer.parseInt(params.get("bean").toString());

		List<AwardDDL> awards = AwardService.listAwardByBean(bean, scope, count, true);
		List<AwardDto> list = new ArrayList<AwardDto>();

		// 如果按bean找不够3件奖品，则取价值最大的三奖品
		if (awards != null && awards.size() < 3) {
			awards = AwardService.listAwardByBean(0, scope, count, false);
		}

		// 按bean升序排序
		if (awards != null) {
			Collections.sort(awards, new Comparator<AwardDDL>() {
				@Override
				public int compare(AwardDDL o1, AwardDDL o2) {
					return o2.getHappyBean() - o1.getHappyBean();
				}
			});
		}

		AwardDto dto = null;
		for (AwardDDL award : awards) {
			dto = new AwardDto();
			dto.setExchageNum(award.getExchageNum());
			dto.setImgUrl(award.getImgUrl());
			dto.setHappyBean(award.getHappyBean());
			dto.setName(award.getName());
			dto.setStoreNum(award.getStoreNum());
			dto.setTotalNum(award.getTotalNum());
			dto.setType(award.getType());
			list.add(dto);
		}
		getHelper().returnSucc(list);
	}

	/**
	 * 玩游戏(购买夺宝码)
	 * 
	 * @param uid
	 * @param sessionId
	 * @param amount
	 * @return
	 * @throws Exception
	 */
	public static void buyCode() throws Exception {
		Map params = getDTO(Map.class);
		int uid = Integer.parseInt(params.get("uid").toString());
		int seasonId = Integer.parseInt(params.get("sessionId").toString());
		int amount = Integer.parseInt(params.get("amount").toString());
		boolean isAllIn = Boolean.parseBoolean(params.get("isAllIn").toString());

		Map<String, String> result = new HashMap<String, String>();

		if (uid <= 0 || seasonId <= 0 || amount <= 0) {
			throw new BusinessException(MessageCode.ERROR_CODE_500, "参数校验不通过");
		}

		GuessSeasonDDL season = GuessSeasonService.getById(seasonId);
		if (season == null) {
			Logger.error("Season为空,购买夺码失败");
			result.put("result", "FAIL");
			result.put("msg", "网络繁忙，请稍后重试");
			getHelper().returnSucc(result);
		}

		GuessAwardDDL award = GuessAwardService.getById(season.getGuessAwardId());
		if (award == null) {
			Logger.error("奖品为空,购买夺码失败");
			result.put("result", "FAIL");
			result.put("msg", "网络繁忙，请稍后重试");
			getHelper().returnSucc(result);
		}
		result = GuessSeasonService.buyCode(uid, false, season, award, amount, false, 0, "", "", null, isAllIn);
		getHelper().returnSucc(result);
	}

	/**
	 * 排行榜
	 * 
	 * @param type
	 * @return
	 * @throws Exception
	 */
	public static void getRanklist() throws Exception {
		Map params = getDTO(Map.class);
		long beginDate = Long.parseLong(params.get("beginDate").toString());
		long endDate = Long.parseLong(params.get("endDate").toString());
		int page = Integer.parseInt(params.get("page").toString());
		int pageSize = Integer.parseInt(params.get("pageSize").toString());

		List<GuessRankDto> list = GuessRecordService.getRanklist(beginDate, endDate, page, pageSize);

		int rank = 0;
		GetRanklistResp resp = new GetRanklistResp();
		if (page > 1 && pageSize > 0) {
			rank = (page - 1) * pageSize;
		}

		for (GuessRankDto dto : list) {
			rank++;
			dto.setRank(rank);
		}

		resp.setList(list);
		getHelper().returnSucc(resp);
	}

	/**
	 * 获取个人参与记录
	 * 
	 * @param uid
	 * @return
	 * @throws Exception
	 */
	public static void listPlayRecord() throws Exception {
		Map params = getDTO(Map.class);
		int uid = Integer.parseInt(params.get("uid").toString());
		int page = Integer.parseInt(params.get("page").toString());
		int pageSize = Integer.parseInt(params.get("pageSize").toString());

		page = page == 0 ? 1 : page;
		pageSize = pageSize == 0 ? 10 : pageSize;

		ListPlayRecordResp resp = new ListPlayRecordResp();
		List<PlayRecordDto> playerList = GuessRecordService.listPlayRecord(uid, 0, page, pageSize);
		for (PlayRecordDto record : playerList) {
			if (record.getHit() % 2 == 1) {
				record.setHit(1);
			} else {
				record.setHit(2);
			}
			record.setTime(record.getTime() * 1000);
		}

		resp.setList(playerList);
		getHelper().returnSucc(resp);
	}

	/**
	 * 获取当前期已投豆总数
	 * 
	 * @param seasonId
	 * @return
	 * @throws Exception
	 */
	public static void getBeanBySeason() throws Exception {
		Map params = getDTO(Map.class);
		int seasonId = Integer.parseInt(params.get("seasonId").toString());
		int count = GuessSeasonService.getTotalBeanBySeason(seasonId);
		getHelper().returnSucc(count);
	}

	/**
	 * 获取本期参与玩家
	 * 
	 * @param seasonId
	 * @param page
	 * @param pageSize
	 * @return
	 * @throws Exception
	 */
	public static void listPlayers() throws Exception {
		Map params = getDTO(Map.class);
		int seasonId = Integer.parseInt(params.get("seasonId").toString());
		int page = Integer.parseInt(params.get("page").toString());
		int pageSize = Integer.parseInt(params.get("pageSize").toString());

		GuessSeasonDDL season = GuessSeasonService.getById(seasonId);
		if (season == null) {
			Logger.error("season is null");
			getHelper().returnSucc();
		}

		ListPlayersResp result = GuessRecordService.listPlayers(seasonId, season.getGameLevel(), true, page, pageSize);
		getHelper().returnSucc(result);
	}

	/**
	 * 根据ID获取中奖记录
	 * 
	 * @param seasonId
	 * @return
	 * @throws Exception
	 */
	public static void getWinningRecordById() {
		Map params = getDTO(Map.class);
		int seasonId = Integer.parseInt(params.get("seasonId").toString());
		GuessSeasonDDL season = GuessSeasonService.getById(seasonId);

		if (season == null) {
			Logger.error("Guess.getWinningRecordById - season为空");
			getHelper().returnSucc();
		}

		GetWinningRecordResp resp = GuessRecordService.getWinningRecord(season.getGameLevel(), season.getSeasonNum());
		GuessAwardDDL guessAward = GuessAwardService.getById(season.getGuessAwardId());
		if (resp != null && guessAward != null) {
			resp.setAwardName(guessAward.getName());
		} else {
			resp = new GetWinningRecordResp();
			Logger.error("Guess.getWinningRecordById, 没人中奖");
		}

		getHelper().returnSucc(resp);
	}

	/**
	 * 获取已购买夺宝码以及中奖状态
	 * 
	 * @param uid
	 * @param seasonNum
	 * @return
	 * @throws Exception
	 */
	public static void getPlayResult() throws Exception {
		Map params = getDTO(Map.class);
		int uid = Integer.parseInt(params.get("uid").toString());
		int seasonNum = Integer.parseInt(params.get("seasonNum").toString());
		int gameLevel = Integer.parseInt(params.get("gameLevel").toString());

		List<GuessRecordDDL> list = GuessRecordService.list(uid, 0, seasonNum, gameLevel, -1, -1, 0, 0, 0, -1);
		GetPlayResultResp result = new GetPlayResultResp();
		result.setHit(0);

		List<String> codeList = new ArrayList<String>();
		for (GuessRecordDDL record : list) {
			if (record.getHit() == 1) {
				result.setHit(1);
			}
			codeList.add(String.valueOf(record.getCode()));
		}
		result.setCode(codeList);
		getHelper().returnSucc(result);
	}

	/**
	 * 获取最近三个场次的中奖记录
	 * 
	 * @return
	 * @throws Exception
	 */
	public static void listLastWinningRecord() throws Exception {
		GuessSeasonCurrentDDL curSeason = GuessSeasonCurrentService.get(1, 0);
		int seasonNum = 0;
		if (curSeason != null && curSeason.getSeasonNum() > 2) {
			seasonNum = curSeason.getSeasonNum() - 1;
		}

		ListWinningRecordResp resp = GuessRecordService.ListLastWinningRecord(seasonNum);
		getHelper().returnSucc(resp);
	}

	/**
	 * 获取未读中奖消息
	 * 
	 * @param uid
	 * @return
	 * @throws Exception
	 */
	public static void listUnreadWinningRecord() throws Exception {
		Map params = getDTO(Map.class);
		int uid = Integer.parseInt(params.get("uid").toString());
		int gameLevel = Integer.parseInt(params.get("gameLevel").toString());
		int seasonNum = 0;

		ListWinningRecordResp resp = new ListWinningRecordResp();
		List<GuessRecordDDL> records = GuessRecordService.listGuessRecord(uid, 0, BoobyStatus.DISABLED.getValue(), -1, 1, gameLevel, 0, -1);

		List<GetWinningRecordResp> list = new ArrayList<GetWinningRecordResp>();

		for (GuessRecordDDL record : records) {
			GetWinningRecordResp dto = new GetWinningRecordResp();
			dto.setAvatar("");
			dto.setAwardName("");
			dto.setCode(record.getWinningCode());
			dto.setGameLevel(record.getGameLevel());
			dto.setHappyBean(record.getRewardBean());
			dto.setNickname(record.getNickname());
			dto.setSeasonId(record.getGuessSeasonId());
			dto.setSeasonNum(record.getSeasonNum());
			list.add(dto);
			seasonNum = record.getSeasonNum();
		}
		resp.setList(list);

		// 更新参与记录阅读状态为已读
		if (seasonNum > 0) {
			GuessRecordDDL record = new GuessRecordDDL();
			record.setUid(uid);
			record.setIsRead(1);
			GuessRecordService.updateReadStatus(record);
		}

		getHelper().returnSucc(resp);
	}

	/**
	 * 获取官方加奖规则
	 */
	public static void getRaiseRule() {
		Map params = getDTO(Map.class);
		int seasonId = Integer.parseInt(params.get("seasonId").toString());
		GuessSeasonDDL season = GuessSeasonService.getById(seasonId);
		GetRaiseRuleResp resp = new GetRaiseRuleResp();
		if (season == null) {
			Logger.info("Guess.getRaiseRule - season is null");
			getHelper().returnSucc();
		}

		// 已开奖期，直接返回已加豆
		if (season.getStatus() == GuessSeasonStatus.PUBLISHED.getValue()) {
			resp.setPresented(season.getRaiseBean());
			getHelper().returnSucc(resp);
		}

		GuessSeasonCurrentDDL curSeason = GuessSeasonCurrentService.get(season.getGameLevel(), 0);
		if (curSeason == null) {
			Logger.info("Guess.getRaiseRule - curSeason is null");
			getHelper().returnSucc();
		}

		Map<String, Integer> map = new HashMap<String, Integer>();
		int presented = 0;
		try {
			int bean = GuessSeasonService.getTotalBeanBySeason(season.getId());
			if (StringUtils.isNotEmpty(curSeason.getRaiseRule())) {
				String arrRaise[] = curSeason.getRaiseRule().split(",");
				List<String> listRaise = Arrays.asList(arrRaise);

				int i = 0;
				for (String str : listRaise) {
					i++;
					int target = Integer.valueOf(str.split("/")[0]);
					int append = Integer.valueOf(str.split("/")[1]);
					map.put(String.valueOf(i), target);
					if (bean <= target) {
						resp.setTarget(target);
						resp.setAppend(append);
						if (i >= 2) {
							presented = map.get(String.valueOf(i - 1));
							resp.setPresented(presented);
						}
						break;
					}
				}

				if (listRaise != null && listRaise.size() > 0 && resp.getTarget() == 0) {
					String str = listRaise.get(listRaise.size() - 1);
					int target = Integer.valueOf(str.split("/")[0]);
					int append = Integer.valueOf(str.split("/")[1]);
					resp.setTarget(target);
					resp.setAppend(append);
				}

			}
		} catch (Exception e) {
			Logger.error(e.getMessage());
		}

		getHelper().returnSucc(resp);
	}

	/**
	 * 获取用户排行
	 * 
	 * @param uid
	 * @return
	 * @throws Exception
	 */
	public static void getUserRank() throws Exception {
		Map params = getDTO(Map.class);
		int uid = Integer.parseInt(params.get("uid").toString());
		long beginDate = Long.parseLong(params.get("beginDate").toString());
		long endDate = Long.parseLong(params.get("endDate").toString());

		GuessRankDto resp = new GuessRankDto();
		List<GuessRankDto> list = GuessRecordService.getRanklist(beginDate, endDate, 0, 100); // 超过一百名不显示
		for (GuessRankDto dto : list) {
			if (dto.getUid() == uid) {
				resp = dto;
				break;
			}
		}
		getHelper().returnSucc(resp);
	}

	/**
	 * 获取用户已购买夺宝码
	 * 
	 * @param uid
	 * @param seasonId
	 * @return
	 * @throws Exception
	 */
	public static void getUserCode() throws Exception {
		Map params = getDTO(Map.class);
		int seasonId = Integer.parseInt(params.get("seasonId").toString());
		int uid = Integer.parseInt(params.get("uid").toString());

		GetUserCodeResp resp = new GetUserCodeResp();
		List<Integer> list = new ArrayList<Integer>();
		List<String> codes = GuessRecordService.listUserCode(uid, seasonId);
		for (String code : codes) {
			String co[] = code.split(",");
			for (int i = 0; i < co.length; i++) {
				list.add(Integer.valueOf(co[i]));
			}
		}

		if (list != null) {
			resp.setList(list);
		}
		getHelper().returnSucc(resp);
	}

	// 购买夺宝码
	private static void buyCode(GuessSeasonCurrentDDL curSeason, int uid, double price, int amount, int gameLevel, boolean fromOutSide, String caller, String zhifuOrderId,
			String title) {
		GuessSeasonDDL season = GuessSeasonService.getById(curSeason.getGuessSeasonId());
		if (season == null) {
			getHelper().returnError(-1, "找不到当前期信息");
		}

		GuessAwardDDL award = GuessAwardService.getById(curSeason.getGuessAwardId());
		if (award == null) {
			getHelper().returnError(-1, "找不到奖品信息");
		}

		try {
			String existKey = String.format(CommonCacheKeyPrefix.MEMBER_EXIST_PREFIX, uid);
			boolean isExist = Cache.get(existKey) != null;

			if (!isExist && fromOutSide) {
				MemberDDL member = MemberService.getOrCreate(uid);
				if (member != null) {
					Cache.add(existKey, existKey, "10d");
				} else {
					getHelper().returnError(-1, "用户注册失败Or找不到用户信息");
				}
			}

			int happyBean = award.getHappyBean() * amount;
			int rate = Integer.parseInt(Jws.configuration.getProperty("rmb.rate", "1000"));
			int payBean = new Double(price * rate).intValue();
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

			Map orderResult = OrderService.createOrder(orderParam);
			if (orderResult == null || (orderResult.containsKey("result") && orderResult.get("result").equals("FAIL"))) {
				Logger.error("创建订单失败");
				getHelper().returnError(-1, "服务器内部错误");
			}

			// 送豆
			if (needBean > 0) {
				Map<String, String> addBeanParams = new HashMap<String, String>();
				addBeanParams.put("remark", "赠送-" + title);
				addBeanParams.put("gameId", String.valueOf(SelfGame.GAME_GUESS.getGameId()));
				String lockKey = String.format("gid_%d-gl_%d-act_%s-uid_%d", SelfGame.GAME_GUESS.getGameId(), award.getGameLevel(), "GuessPresentBean", uid);
				boolean flag = MemberService.addBean(uid, needBean, MemberLogOpType.PRESENTED.getType(), addBeanParams, lockKey);
				if (!flag) {
					Logger.error("加豆失败, --> params: %s", new Gson().toJson(addBeanParams));
				}
			}

			Map<String, String> result = GuessSeasonService.buyCode(uid, false, season, award, amount, true, payBean, zhifuOrderId, caller, title, false);
			if (result == null) {
				Logger.error("result is null");
				getHelper().returnError(-1, "购买夺宝码失败,返回为空");
			}

			if (result.get("result").equalsIgnoreCase("fail")) {
				String msg = "";
				if (result.containsKey("msg")) {
					msg = result.get(msg);
				}
				getHelper().returnError(-1, msg);
			}

			JoinResp resp = new JoinResp();
			resp.setCode(result.get("codeStr"));
			resp.setGameLevel(gameLevel);
			resp.setPublishTime(curSeason.getPublishTime());
			resp.setSeasonId(season.getId());
			resp.setSeasonNum(season.getSeasonNum());
			String url = baseUrl + "Guess/index?gl=" + gameLevel;
			try {
				url = new String(url.getBytes("ISO-8859-1"), "utf8");
			} catch (UnsupportedEncodingException e) {
			}
			resp.setUrl(url);
			getHelper().returnSucc(resp);
		} catch (Exception e) {
			Logger.error(e.getMessage());
			getHelper().returnError(-1, "服务器内部错误");
		}
	}

	/**
	 * 参与夺宝(购买夺宝码)
	 */
	public static void join() {
		Map params = getDTO(Map.class);
		int uid = Integer.valueOf(params.get("uid").toString());
		double price = Double.valueOf(params.get("price").toString());
		int amount = Integer.valueOf(params.get("amount").toString());
		int gameLevel = Integer.valueOf(params.get("gameLevel").toString());
		boolean fromOutSide = Boolean.valueOf(params.get("fromOutside").toString());
		String caller = params.get("caller").toString() + "order";
		String zhifuOrderId = params.get("zhifuOrderId").toString();
		String title = params.get("title").toString();

		if (uid <= 0 || price <= 0 || amount <= 0 || StringUtils.isEmpty(zhifuOrderId)) {
			getHelper().returnError(-1, "参数校验不通过");
		}

		if (gameLevel < 1 || gameLevel > 3) {
			getHelper().returnError(-1, "无效场次");
		}

		String joinLockKey = "Guess-Join-" + zhifuOrderId;
		try {
			if (!lock.tryCacheLock(joinLockKey, "", "5s")) {
				Logger.error("重复购买,zhifuOrderId:%s", zhifuOrderId);
				getHelper().returnError(-1, "重复购买");
			}
		} catch (Exception e) {
			Logger.error(e.getMessage());
		}

		GuessSeasonCurrentDDL curSeason = GuessSeasonCurrentService.get(gameLevel, 0);
		if (curSeason == null) {
			getHelper().returnError(-1, "找不到当前期信息");
		}

		// 检查当前Season是否到期
		// boolean expired = System.currentTimeMillis() >
		// (curSeason.getPublishTime() - 1000);
		boolean expired = System.currentTimeMillis() > (curSeason.getPublishTime() - 1500);
		if (expired) {
			// int millis = new Long(curSeason.getPublishTime() -
			// System.currentTimeMillis() + 800).intValue();
			int millis = new Long(curSeason.getPublishTime() - System.currentTimeMillis() + 1500).intValue();
			if (Logger.isDebugEnabled()) {
				Logger.debug("CurSeason expired: %s", millis);
			}
			// 如果当前期已到期,则挂起当前请求(1-3秒),直到下一期信息生成 再购买夺宝码
			await(millis);
			curSeason = GuessSeasonCurrentService.get(gameLevel, 0);
			if (curSeason == null) {
				getHelper().returnError(-1, "找不到当前期信息");
			}
		}

		// 购买夺宝码
		buyCode(curSeason, uid, price, amount, gameLevel, fromOutSide, caller, zhifuOrderId, title);
	}

	/**
	 * 获取参与结果
	 */
	public static void getJoinResult() {

		Map params = getDTO(Map.class);
		int uid = Double.valueOf(params.get("uid").toString()).intValue();
		String zhifuOrderId = params.get("zhifuOrderId").toString();

		if (StringUtils.isBlank(zhifuOrderId)) {
			getHelper().returnError(-1, "订单号不能为空");
		}

		GuessRecordDDL record = GuessRecordService.getByZhifuOrderId(zhifuOrderId, uid);
		if (record == null) {
			getHelper().returnError(-1, "找不到参与记录");
		}

		Map result = formatResult(record.getHit(), record.getGameLevel(), record.getGuessSeasonId(), record.getSeasonNum(), record.getCode(), record.getRewardBean(),
				record.getPublishTime(), record.getTitle());
		getHelper().returnSucc(result);
	}

	private static Map formatResult(int st, int gl, int sid, int seasonNum, String code, int bean, long publishTime,String title) {
		Map result = new HashMap();
		String content = "";
		String gameLevel = GameLevel.getGameLevel(gl).getDesc();
		String time = DateUtil.formatDate(publishTime, "yyyy年MM月dd日 HH:mm:ss");
		String status = "";
		
		String url = baseUrl + "Guess/index";

		// 未开奖
		if (st == 0) {
			content = String.format("%s，第%d期，您的夺宝码是%s\n开奖前还能继续押注哦，开奖时间%s", gameLevel, seasonNum, code, time);
			url = baseUrl + "Guess/index?gl=" + gl;
			status = "未开奖";
		}

		// 已中奖
		if (st == 1) {
			content = String.format("恭喜你中奖了！%d开心豆已经发放到您的账户中。\n趁着手气好，赢取更多开心豆换大奖吧!", bean);
			url = baseUrl + "Guess/season?sid=" + sid;
			status = "中奖了";
		}

		// 未中奖
		if (st == 2) {
			content = "很遗憾，与大奖擦肩而过！\n前往开心大厅再接再厉，赢取开心豆换大奖吧！";
			url = baseUrl + "Guess/season?sid=" + sid;
			status = "未中奖";
		}
		result.put("code", code);
		result.put("status", status);
		result.put("gameLevel", gl);
		result.put("hit", st);
		result.put("publishTime", publishTime);
		result.put("seasonId", sid);
		result.put("seasonNum", seasonNum);
		result.put("title", title);
		result.put("content", content);
		result.put("url", url);
		return result;
	}
}
