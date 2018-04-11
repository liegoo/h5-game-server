package controllers.ucgc;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import jws.Logger;
import jws.cache.Cache;
import jws.module.response.age.GaeRecordDto;
import jws.module.response.game.GameDtoRsp;
import jws.module.response.game.GameListRspDto;
import moudles.chance.service.DollChanceRecordService;
import moudles.gae.service.GrabRedEnvelopeService;
import moudles.game.ddl.CouponGameDDL;
import moudles.game.ddl.GamesDDL;
import moudles.game.service.GameService;
import moudles.guess.ddl.GuessSeasonCurrentDDL;
import moudles.guess.service.GuessSeasonCurrentService;
import moudles.guess.service.GuessSeasonService;
import moudles.member.ddl.MemberDDL;
import moudles.member.service.MemberService;
import moudles.order.ddl.GameOrderDDL;
import moudles.order.service.GameOrderService;

import org.apache.commons.lang.StringUtils;

import utils.CopyDDLUtil;
import utils.DistributeCacheLock;
import utils.JsonToMap;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;

import common.core.UcgcController;
import constants.SelfGame;
import exception.BusinessException;

public class Game extends UcgcController {

	private static DistributeCacheLock lock = DistributeCacheLock.getInstance();

	/**
	 * 获取游戏列表
	 * 
	 * @throws BusinessException
	 */
	public static void listGames() throws BusinessException {
		Map params = getDTO(Map.class);
		int page = Integer.parseInt(params.get("page").toString());
		int pageSize = Integer.parseInt(params.get("pageSize").toString());

		GameListRspDto rsp = new GameListRspDto();

		List<GamesDDL> list = GameService.listGames(page, pageSize);

		if (list == null || list.size() == 0) {
			getHelper().returnSucc(rsp);
		}

		for (GamesDDL ddl : list) {
			GameDtoRsp dto = new GameDtoRsp();
			CopyDDLUtil copy = new CopyDDLUtil(ddl, dto);
			copy.copy();
			rsp.getList().add(dto);
		}

		getHelper().returnSucc(rsp);

	}

	/**
	 * 获取游戏
	 * 
	 * @throws BusinessException
	 */
	public static void getGame() throws BusinessException {
		Map params = getDTO(Map.class);
		int gameId = Integer.parseInt(params.get("gameId").toString());

		GamesDDL gameddl = GameService.getGame(gameId);

		GameDtoRsp rsp = new GameDtoRsp();
		CopyDDLUtil copy = new CopyDDLUtil(gameddl, rsp);
		copy.copy();

		getHelper().returnSucc(rsp);

	}

	public static void getCouponGames() {
		List<CouponGameDDL> list = GameService.listCouponGames();
		getHelper().returnSucc(list);
	}

	/**
	 * 根据订单号获取GameId
	 */
	public static void getGameIdByOrderId() {
		Map params = getDTO(Map.class);

		String zhifuOrderId = params.get("zhifuOrderId").toString();
		if (StringUtils.isEmpty(zhifuOrderId)) {
			returnCommonError("获取gameId失败，支付订单号为空");
		}
		int gameId = getGameIdByOrderId(zhifuOrderId);
		if (gameId > 0) {
			Map result = new HashMap();
			result.put("gameId", gameId);
			getHelper().returnSucc(result);
		}

		returnCommonError("找不到gameId");
	}

	/**
	 * 参与游戏 (通用)
	 */
	public static void join() {
		Map params = getDTO(Map.class);

		Logger.info("Game.join --> params: %s", params);

		String zhifuOrderId = params.get("zhifuOrderId").toString();
		if (StringUtils.isEmpty(zhifuOrderId)) {
			returnCommonError("参与失败，支付订单号为空");
		}

		//
		try {
			String lockKey = "h5-game-join-" + zhifuOrderId;
			if (!lock.tryCacheLock(lockKey, "", "5s")) {
				returnCommonError("参与失败，重复参与");
			}
		} catch (Exception e) {
			Logger.error(e.getMessage());
		}

		// 检查订单是否已存在
		if (getGameIdByOrderId(zhifuOrderId) > 0) {
			returnCommonError("参与失败，重复参与");
		}

		int gameId = Integer.parseInt(params.get("gameId").toString());
		if (gameId <= 0) {
			returnCommonError("参与失败，无效gameId");
		}

		int uid = Integer.parseInt(params.get("uid").toString());
		if (uid <= 0) {
			returnCommonError("参与失败，无效uid");
		}

		// 检查用户
		MemberDDL member = null;
		try {
			member = MemberService.getOrCreate(uid);
		} catch (BusinessException e) {
		}
		if (member != null) {

		} else {
			returnCommonError("用户注册失败Or找不到用户信息");
		}

		Map<String, String> result = null;

		// 购买夹娃娃特权机会
		if (gameId == SelfGame.GAME_DOLL.getGameId()) {
			result = DollChanceRecordService.createChance(params);
		}

		// 夺宝
		if (gameId == SelfGame.GAME_GUESS.getGameId()) {
			result = joinGuess(params);
		}

		// 抢红包
		if (gameId == SelfGame.GAME_ENVELOPPE.getGameId()) {
			result = grabByOrder(member, params);
		}

		if (result != null) {
			// 创建游戏订单记录
			GameOrderDDL gameOrder = new GameOrderDDL();
			gameOrder.setGameId(gameId);
			gameOrder.setZhifuOrderId(zhifuOrderId);
			if (!GameOrderService.create(gameOrder)) {
				Logger.error("创建游戏订单记录失败,gameId:%d ,zhifuOrderId:%s", gameId, zhifuOrderId);
			}
			getHelper().returnSucc(result);
		} else {
			returnCommonError("参与活动失败");
		}

	}

	/**
	 * 获取参与结果(通用)
	 */
	public static void getJoinResult() {
		Map params = getDTO(Map.class);

		Logger.info("Game.getJoinResult --> params: %s", params);

		String zhifuOrderId = params.get("zhifuOrderId").toString();
		if (StringUtils.isEmpty(zhifuOrderId)) {
			returnCommonError("获取失败，支付订单号为空");
		}
		int gameId = getGameIdByOrderId(zhifuOrderId);
		if (gameId == 0) {
			returnCommonError("获取失败，找不到对应订单记录");
		}

		int uid = Double.valueOf(params.get("uid").toString()).intValue();
		Logger.info(">> uid:%d", uid);
		Map result = null;

		// 获取夹娃娃赠送记录
		if (gameId == SelfGame.GAME_DOLL.getGameId()) {
			result = DollChanceRecordService.getChance(params);
		}

		// 获取夺宝结果
		if (gameId == SelfGame.GAME_GUESS.getGameId()) {
			result = GuessSeasonService.getJoinResultByOrder(params);
		}

		// 获取抢红包记录
		if (gameId == SelfGame.GAME_ENVELOPPE.getGameId()) {
			JsonObject resp = GrabRedEnvelopeService.getOrderInfo(uid, zhifuOrderId);
			if (resp != null) {
				result = new GsonBuilder().create().fromJson(resp.toString(), new HashMap<String, String>().getClass());
			}
		}

		if (result != null) {
			getHelper().returnSucc(result);
		} else {
			returnCommonError("找不到活动参与记录");
		}
	}

	/**
	 * 根据订单号获取游戏ID
	 * 
	 * @param zhifuOrderId
	 * @return
	 */
	private static int getGameIdByOrderId(String zhifuOrderId) {
		String key = String.format("h5_game_join_zhifu_order_id_%s", zhifuOrderId);
		Object obj = Cache.get(key);

		int gameId = 0;
		if (obj != null) {
			gameId = Integer.parseInt(Cache.get(key).toString());
		}

		if (gameId == 0) {
			GameOrderDDL gameOrder = GameOrderService.getByOrderId(zhifuOrderId);
			if (gameOrder != null) {
				gameId = gameOrder.getGameId();
				Cache.add(key, gameId, "3d");
			}
		}
		return gameId;
	}

	/**
	 * 通用错误
	 * 
	 * @param msg
	 */
	private static void returnCommonError(String msg) {
		getHelper().returnError(-1, msg);
	}

	/**
	 * 抢红包
	 * 
	 * @param member
	 * @param params
	 * @return
	 */
	private static Map<String, String> grabByOrder(MemberDDL member, Map params) {
		Map result = new HashMap();
		String title = params.get("title").toString();
		int uid = Integer.parseInt(params.get("uid").toString());
		String zhifuOrderId = params.get("zhifuOrderId").toString();
		String orderFr = params.containsKey("caller") ? params.get("caller").toString(): "";
		orderFr = orderFr.concat("order");

		String userAvatar = member.getAccount();
		String userName = replaceWithStar(member.getNickName(), member.getMobile());
		
		JsonObject data = JsonToMap.parseJson(params.get("gameExtra").toString());

		String ip = data.has("ip") ? (String) data.get("ip").getAsString() : "";
		String userZone = data.has("userZone") ? (String) data.get("userZone").getAsString() : "";
		double orderPrice = data.get("price").getAsDouble();
		String roomId = data.has("roomId") ? (String) data.get("roomId").getAsString() : "";

		GaeRecordDto resp = GrabRedEnvelopeService.grabByOrder(uid, zhifuOrderId, title, orderPrice, orderFr, roomId, userName, userAvatar, ip, userZone);
		if (resp.getCode() == GaeRecordDto.OK && resp.getData() != null) {
			JsonObject res = (JsonObject) resp.getData();
			result = new GsonBuilder().create().fromJson(res.toString(), new HashMap<String, String>().getClass());
		}
		return result;
	}
	
    private static String replaceWithStar(String nickname, String mobile) {
        if (StringUtils.isEmpty(nickname)) {
            nickname = mobile;
        }
        if (StringUtils.isNotEmpty(nickname) && nickname.length() == 11) {
            nickname = (new StringBuilder(nickname.substring(0, 3)).append("****").append(nickname.substring(7, 11))).toString();
        }
        if (StringUtils.isEmpty(nickname)) {
            nickname = (new StringBuilder("用户").append(String.valueOf(new Random().nextInt(8999) + 1000)).insert(4, "**")).toString();
        }
        return nickname;
    }

	/**
	 * 参与夺宝
	 * 
	 * @param params
	 */
	private static Map<String, String> joinGuess(Map params) {

		Logger.info("Game.joinGuess --> params: %s", params);

		int uid = Integer.parseInt(params.get("uid").toString());
		String caller = params.get("caller").toString() + "order";
		String title = params.get("title").toString();
		String zhifuOrderId = params.get("zhifuOrderId").toString();

		JsonObject data = JsonToMap.parseJson(params.get("gameExtra").toString());
		double price = data.get("price").getAsDouble();
		int amount = data.get("amount").getAsInt();
		int gameLevel = data.get("gameLevel").getAsInt();
		boolean fromOutSide = true;

		if (uid <= 0 || price <= 0 || amount <= 0 || StringUtils.isEmpty(zhifuOrderId)) {
			returnCommonError("参数校验不通过");
		}

		if (gameLevel < 1 || gameLevel > 3) {
			returnCommonError("无效场次");
		}

		String joinLockKey = "Guess-Join-" + zhifuOrderId;
		try {
			if (!lock.tryCacheLock(joinLockKey, "", "5s")) {
				Logger.error("重复购买,zhifuOrderId:%s", zhifuOrderId);
				returnCommonError("重复购买");
			}
		} catch (Exception e) {
			Logger.error(e.getMessage());
			returnCommonError("服务器错误");
		}

		GuessSeasonCurrentDDL curSeason = GuessSeasonCurrentService.get(gameLevel, 0);
		if (curSeason == null) {
			returnCommonError("找不到当前期信息");
		}

		// 如果当前期已到期,则挂起当前请求(1-3秒),直到下一期信息生成 再购买夺宝码
		boolean expired = System.currentTimeMillis() > (curSeason.getPublishTime() - 1500);
		if (expired) {
			int millis = new Long(curSeason.getPublishTime() - System.currentTimeMillis() + 1500).intValue();
			if (Logger.isDebugEnabled()) {
				Logger.debug("CurSeason expired: %s", millis);
			}
			// 挂起当前线程
			await(millis);
			curSeason = GuessSeasonCurrentService.get(gameLevel, 0);
			if (curSeason == null) {
				returnCommonError("找不到当前期信息");
			}
		}
		return GuessSeasonService.joinByOrder(curSeason, uid, price, amount, gameLevel, fromOutSide, caller, zhifuOrderId, title);
	}
}
