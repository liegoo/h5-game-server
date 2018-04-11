package controllers.external;

import java.util.HashMap;
import java.util.Map;

import jws.Jws;
import jws.Logger;
import jws.dal.Dal;
import jws.module.constants.award.AwardScope;
import jws.module.constants.member.MemberLogOpType;
import jws.module.constants.order.OrderType;
import moudles.exchage.ddl.GameCoinExchangeDDL;
import moudles.game.ddl.GamesDDL;
import moudles.game.service.GameService;
import moudles.member.ddl.MemberSessionDDL;
import moudles.member.service.MemberService;
import moudles.member.service.MemberSessionService;
import moudles.order.service.OrderService;

import org.apache.commons.lang3.StringUtils;

import utils.DistributeCacheLock;
import constants.MessageCode;
import exception.BusinessException;

public class HappyBean extends ExternalController {

	private static DistributeCacheLock lock = DistributeCacheLock.getInstance();

	/**
	 * 消耗开心豆
	 * @throws BusinessException
	 */
	public static void consume() throws BusinessException {
		Map params = getDTO(Map.class);

		if (!params.containsKey("token") || params.get("token") == null) {
			throw new BusinessException(MessageCode.ERROR_CODE_500, "参数非法");
		}
		if (!params.containsKey("productName") || params.get("productName") == null) {
			throw new BusinessException(MessageCode.ERROR_CODE_500, "参数非法");
		}
		if (!params.containsKey("happyBean") || params.get("happyBean") == null) {
			throw new BusinessException(MessageCode.ERROR_CODE_500, "参数非法");
		}
		String token = params.get("token").toString();
		MemberSessionDDL session = MemberSessionService.getMemberSessionByToken(token);
		if (session == null || StringUtils.isEmpty(session.getToken())) {
			throw new BusinessException(MessageCode.ERROR_CODE_500, "用户会话失效，请重新登陆");
		}

		params.put("suid", session.getSuid());
		params.put("gameId", session.getGameId());
		params.put("cpId", session.getCpId());
		params.put("uid", session.getUid());
		params.put("sourceDesc", "游戏消耗");

		params.put("orderType", OrderType.CONSUME.getType());
		params.put("remark", "游戏消耗");
		params.put("awardScope", String.valueOf(AwardScope.GAME.getType()));

		Logger.debug("request params - > %s", params);

		Map<String, String> transfer = new HashMap<String, String>();
		for (Object key : params.keySet()) {
			transfer.put(key.toString(), params.get(key).toString());
		}
		String billId = "H5GMBILL-" + System.currentTimeMillis() + "-" + session.getSuid();
		transfer.put("billId", billId);

		Logger.debug("request transfer - > %s", transfer);

		Map<String, String> result = OrderService.createOrder(transfer);

		if (result.get("result").equals("FAIL")) {
			throw new BusinessException(MessageCode.ERROR_CODE_500, "扣减开心豆失败,orderId=" + result.get("orderId"));
		}
		Logger.info("【%s-%s】消耗豆成功（%s）", session.getCpId(), session.getGameId(), params.get("happyBean"));
		Map<String, Object> rtn = new HashMap<String, Object>();
		rtn.put("result", true);
		rtn.put("billId", billId);
		getHelper().returnSucc(rtn);
	}

	// 对外的文档已经未使用
	public static void exchange() throws BusinessException {
		Map result = new HashMap();
		boolean enable = Boolean.valueOf(Jws.configuration.getProperty("external.bean.exchange.enabled", "false"));
		if(!enable){
			Logger.error("开心豆兑换功能已关闭");
			result.put("result", false);
			result.put("url", "");
			result.put("billId", "");
			getHelper().returnSucc(result);
		}
		
		Map params = getDTO(Map.class);
		if (!params.containsKey("token") || params.get("token") == null) {
			throw new BusinessException(MessageCode.ERROR_CODE_500, "参数非法");
		}
		if (!params.containsKey("gameCoin") || params.get("gameCoin") == null) {
			throw new BusinessException(MessageCode.ERROR_CODE_500, "参数非法");
		}
		if (!params.containsKey("happyBean") || params.get("happyBean") == null) {
			throw new BusinessException(MessageCode.ERROR_CODE_500, "参数非法");
		}

		String token = params.get("token").toString();
		MemberSessionDDL session = MemberSessionService.getMemberSessionByToken(token);
		if (session == null || StringUtils.isEmpty(session.getToken())) {
			throw new BusinessException(MessageCode.ERROR_CODE_500, "用户会话失效，请重新登陆");
		}

		params.put("suid", session.getSuid());
		params.put("gameId", session.getGameId());
		params.put("cpId", session.getCpId());
		params.put("uid", session.getUid());
		params.put("remark", "游戏币兑换");

		String lockKey = "Exchange-" + session.getGameId() + "-" + session.getUid();
		// String lockKey = "Exchange-" + session.getUid();
		
		try {
			if (!lock.tryCacheLock(lockKey, "", "8s")) {
				Logger.error("开心豆兑换频繁, >>>> Uid:%s,GameId:%s", session.getUid(), session.getGameId());
				result.put("result", false);
				result.put("url", "");
				result.put("billId", "");
				getHelper().returnSucc(result);
			}

			GamesDDL game = GameService.getGame(session.getGameId());
			if (game == null) {
				throw new BusinessException(MessageCode.ERROR_CODE_500, "兑换游戏不存在");
			}
			GameCoinExchangeDDL gameCoinExchange = new GameCoinExchangeDDL();
			gameCoinExchange.setCreateTime(System.currentTimeMillis());
			gameCoinExchange.setUpdateTime(System.currentTimeMillis());
			gameCoinExchange.setGameCoin((int) (Double.parseDouble(params.get("gameCoin").toString())));
			gameCoinExchange.setGameId(session.getGameId());
			gameCoinExchange.setGameName(game.getName());
			gameCoinExchange.setHappyBean((int) Double.parseDouble(params.get("happyBean").toString()));
			gameCoinExchange.setUid(session.getUid());
			gameCoinExchange.setCpId(session.getCpId());
			gameCoinExchange.setBackurl((params.containsKey("backurl") && params.get("backurl") != null && !StringUtils.isEmpty(params.get("backurl").toString())
					&& params.get("backurl").toString().startsWith("http://") ? params.get("backurl").toString() : Jws.configuration.getProperty("h5game.web.inex")));
			String billId = "H5GMBILL-" + System.currentTimeMillis() + "-" + session.getSuid();
			gameCoinExchange.setBillId(billId);

			long id = Dal.insertSelectLastId(gameCoinExchange);

			if (id <= 0) {
				result.put("result", false);
				result.put("url", "");
				result.put("billId", "");
				getHelper().returnSucc(result);
			}
			result.put("billId", billId);
			result.put("result", true);
			result.put("url", Jws.configuration.get("h5game.web.inex") + "Member/exchange?id=" + id);
			getHelper().returnSucc(result);
		} catch (Exception e) {
			Logger.error(e.getMessage());
			result.put("result", false);
			result.put("url", "");
			result.put("billId", "");
			getHelper().returnSucc(result);
		}finally {
			lock.cacheUnLock(lockKey);
		}
	}
	
	/**
	 * 兑换开心豆(新)
	 * @throws BusinessException
	 */
	public static void exchanged() throws BusinessException {
		Map result = new HashMap();
		boolean enable = Boolean.valueOf(Jws.configuration.getProperty("external.bean.exchange.enabled", "false"));
		if(!enable){
			Logger.error("开心豆兑换功能已关闭");
//			result.put("result", false);
//			result.put("url", "");
//			result.put("billId", "");
//			getHelper().returnSucc(result);
			throw new BusinessException(MessageCode.ERROR_CODE_500, "暂不支持兑换");
		}

		Map params = getDTO(Map.class);
		if (!params.containsKey("token") || params.get("token") == null) {
			throw new BusinessException(MessageCode.ERROR_CODE_500, "参数非法");
		}
		if (!params.containsKey("gameCoin") || params.get("gameCoin") == null) {
			throw new BusinessException(MessageCode.ERROR_CODE_500, "参数非法");
		}
		if (!params.containsKey("happyBean") || params.get("happyBean") == null) {
			throw new BusinessException(MessageCode.ERROR_CODE_500, "参数非法");
		}

		String token = params.get("token").toString();
		MemberSessionDDL session = MemberSessionService.getMemberSessionByToken(token);
		if (session == null || StringUtils.isEmpty(session.getToken())) {
			throw new BusinessException(MessageCode.ERROR_CODE_500, "用户会话失效，请重新登陆");
		}
		
		String billId = "H5GMBILL-" + System.currentTimeMillis() + "-" + session.getSuid();
		
		params.put("suid", session.getSuid());
		params.put("gameId", session.getGameId());
		params.put("cpId", session.getCpId());
		params.put("uid", session.getUid());
		params.put("remark", params.get("remark") == null ? "游戏币兑换" : params.get("remark"));
		params.put("billId", billId);
		
		String lockKey = "Exchange-" + session.getGameId() + "-" + session.getUid();
		
		
		try {
			
			int happyBean = (int) Double.parseDouble(params.get("happyBean").toString());
			
			if(happyBean <=0 ){
				Logger.error("兑换开心豆无效 , HappyBean:%s",happyBean);
//				result.put("result", false);
//				result.put("billId", "");
//				getHelper().returnSucc(result);
				throw new BusinessException(MessageCode.ERROR_CODE_500, "兑换的开心豆数量必须大于0");
			}
			
			if (!lock.tryCacheLock(lockKey, "", "1s")) {
				Logger.error("开心豆兑换频繁, Uid:%s,GameId:%s",session.getUid(),session.getGameId());
//				result.put("result", false);
//				result.put("billId", "");
//				getHelper().returnSucc(result);
				throw new BusinessException(MessageCode.ERROR_CODE_510, "接口调用太频繁2");
			}

			GamesDDL game = GameService.getGame(session.getGameId());
			if (game == null) {
				throw new BusinessException(MessageCode.ERROR_CODE_500, "兑换游戏不存在");
			}
			
			Logger.info("request params --  > %s",params);
			// 加豆
			int opType = MemberLogOpType.COIN_EXCHANGE.getType();
			String exchangeLockKey = String.format("gid_%d-act_%s-uid_%d", game.getGameId(),"GameCoinExchange",session.getUid());
			boolean addBeanResult = MemberService.addBean(session.getUid(), (int) Double.parseDouble(params.get("happyBean").toString()), opType, params,exchangeLockKey);
			if(addBeanResult){
				Logger.info("Add bean success, uid:%s, happyBean:%s, gameId:%s, suid:%s",session.getUid(),params.get("happyBean").toString(),session.getGameId(),session.getSuid());
			}else{
				result.put("result", false);
				result.put("code", "-10");
				result.put("msg", "兑换开心豆失败，稍后请重试");
				result.put("billId", "");
				Logger.error("Add bean failed, uid:%s, happyBean:%s, gameId:%s, suid:%s",session.getUid(),params.get("happyBean").toString(),session.getGameId(),session.getSuid());
				getHelper().returnSucc(result);
			}
			
			// 创建兑换记录
			GameCoinExchangeDDL gameCoinExchange = new GameCoinExchangeDDL();
			gameCoinExchange.setCreateTime(System.currentTimeMillis());
			gameCoinExchange.setUpdateTime(System.currentTimeMillis());
			gameCoinExchange.setGameCoin((int) (Double.parseDouble(params.get("gameCoin").toString())));
			gameCoinExchange.setGameId(session.getGameId());
			gameCoinExchange.setGameName(game.getName());
			gameCoinExchange.setHappyBean((int) Double.parseDouble(params.get("happyBean").toString()));
			gameCoinExchange.setUid(session.getUid());
			gameCoinExchange.setCpId(session.getCpId());
			gameCoinExchange.setStatus(1); 
			String backurl = (params.containsKey("backurl") && params.get("backurl") != null && !StringUtils.isEmpty(params.get("backurl").toString())&& params.get("backurl").toString().startsWith("http://") ? params.get("backurl").toString() : Jws.configuration.getProperty("h5game.web.inex"));
			gameCoinExchange.setBackurl(backurl);
			gameCoinExchange.setBillId(billId);

			long id = Dal.insertSelectLastId(gameCoinExchange);
			
			if (id <= 0) {// 插入兑换记录失败时，不返回失败，因为豆已经添加了，该错误人工处理
//				result.put("result", false);
//				result.put("code", "-20");
//				result.put("msg", "创建兑换记录失败，稍后请重试");
//				result.put("billId", "");
				Logger.error("Exchanged failed, uid:%s, happyBean:%s,billId:%s,gameId:%s,suid:%s",session.getUid(),params.get("happyBean").toString(),billId,session.getGameId(),session.getSuid());
//				getHelper().returnSucc(result);
//			}else{
			}
				result.put("billId", billId);
				result.put("result", true);
				Logger.info("Exchanged success, uid:%s, happyBean:%s,billId:%s,gameId:%s,suid:%s",session.getUid(),params.get("happyBean").toString(),billId,session.getGameId(),session.getSuid());
				getHelper().returnSucc(result);
		} catch (Exception e) {
			if(e instanceof BusinessException) {// 不需要处理
				throw (BusinessException)e;
			}
			Logger.error(e.getMessage());
			result.put("result", false);
			result.put("code", "-30");
			result.put("msg", "操作异常，稍后请重试");
			result.put("billId", "");
			getHelper().returnSucc(result);
		}finally {
			lock.cacheUnLock(lockKey);
		} 

	}

}
