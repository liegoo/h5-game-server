package controllers.ucgc;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import jws.Jws;
import jws.Logger;
import jws.dal.Dal;
import jws.dal.sqlbuilder.Condition;
import jws.module.constants.award.AwardScope;
import jws.module.constants.member.MemberLogOpType;
import jws.module.response.member.UserInfoRspDto;
import moudles.exchage.ddl.GameCoinExchangeDDL;
import moudles.member.ddl.MemberDDL;
import moudles.member.service.MemberLogService;
import moudles.member.service.MemberService;
import utils.CopyDDLUtil;
import utils.DaoUtil;
import utils.DateUtil;
import utils.KeyPointLogUtil;
import common.core.UcgcController;
import exception.BusinessException;

public class Member extends UcgcController {

	/**
	 * 登录(手机快捷登录)
	 * 
	 * @throws BusinessException
	 * @author surong, 2016年8月9日.
	 */
	public static void loginByMobile() throws BusinessException {
		Map params = getDTO(Map.class);
		String mobile = params.get("mobile").toString();
		String mobileCode = params.get("mobileCode").toString();
		Map<String, String> result = MemberService.loginByMobile(mobile, mobileCode);
		getHelper().returnSucc(result);
	}

	/**
	 * 通过UID获取用户剩余开心豆(包括运营送的开心豆)
	 */
	public static void getBeanByUid() {
		Map params = getDTO(Map.class);
		int uid = Double.valueOf(params.get("uid").toString()).intValue();
		MemberDDL member = MemberService.getMemberByUid(uid);
		Map<String, Integer> result = new HashMap<String, Integer>();
		result.put("bean", 0);
		if (null != member) {
			int bean = member.getHappyBean() + member.getHappyBeanFromOp();
			result.put("bean", bean);
		}
		getHelper().returnSucc(result);
	}

	/**
	 * 登录(账号密码登录)
	 * 
	 * @throws BusinessException
	 * @author surong, 2016年8月9日.
	 */
	public static void login() throws BusinessException {
		Map params = getDTO(Map.class);
		String account = params.get("account").toString();
		String pwd = params.get("pwd").toString();
		Map<String, String> result = MemberService.login(account, pwd);
		getHelper().returnSucc(result);
	}

	/**
	 * 游戏登录
	 * 
	 * @throws BusinessException
	 * @author surong, 2016年8月9日.
	 */
	public static void login4Game() throws BusinessException {
		Map params = getDTO(Map.class);
		int gameId = Integer.parseInt(params.get("gameId").toString());
		int uid = Integer.parseInt(params.get("uid").toString());
		Map<String, String> result = MemberService.login4Game(uid, gameId);
		
		getHelper().returnSucc(result);
	}

	/**
	 * 校验登录态
	 * 
	 * @throws BusinessException
	 */
	public static void checkLoginByToken() throws BusinessException {
		Map params = getDTO(Map.class);
		String sid = params.get("sid").toString();
		UserInfoRspDto rsp = MemberService.checkLoginByToken(sid);
		getHelper().returnSucc(rsp);
	}

	/**
	 * 获取用户信息
	 * 
	 * @throws BusinessException
	 */
	public static void getUserInfo() throws BusinessException {
		Map params = getDTO(Map.class);
		UserInfoRspDto rsp = null;
		int uid = Integer.parseInt(params.get("uid").toString());
		MemberDDL member = MemberService.getMemberByUid(uid);
		if (null != member) {
			rsp = new UserInfoRspDto();
			CopyDDLUtil copy = new CopyDDLUtil(member, rsp);
			copy.copy();
		}
		getHelper().returnSucc(rsp);
	}

	/**
	 * 本地创建用户
	 * 
	 * @throws BusinessException
	 */
	public static void addMember() throws BusinessException {
		Map params = getDTO(Map.class);
		UserInfoRspDto rsp = null;
		int uid = Integer.parseInt(params.get("uid").toString());

		MemberDDL member = MemberService.addMember(uid, "", "", params);
		if (null != member) {
			rsp = new UserInfoRspDto();
			CopyDDLUtil copy = new CopyDDLUtil(member, rsp);
			copy.copy();
		}
		getHelper().returnSucc(rsp);
	}

	/**
	 * 本地创建用户日志
	 * 
	 * @throws BusinessException
	 */
	public static void addMemberLog() throws BusinessException {
		Map params = getDTO(Map.class);
		int uid = Integer.parseInt(params.get("uid").toString());
		int opResult = Integer.parseInt(params.get("opResult").toString());
		int opType = Integer.parseInt(params.get("opType").toString());
		int balance = Integer.parseInt(params.get("balance").toString());
		boolean result = MemberLogService.createMemberLog(uid, opResult, opType, balance, params);
		if (result) {
			getHelper().returnSucc(true);
		} else {
			getHelper().returnError500("创建日志失败");
		}
	}

	public static void exchange() throws BusinessException {
		Map params = getDTO(Map.class);
		int id = (int) Double.parseDouble(params.get("id").toString());
		int uid = (int) Double.parseDouble(params.get("uid").toString());

		GameCoinExchangeDDL exchange = Dal.select(DaoUtil.genAllFields(GameCoinExchangeDDL.class), id);
		if (exchange == null) {
			getHelper().returnError500("兑换失败，兑换记录不存在");
		}
		if (exchange.getUid() != uid || uid == 0) {
			getHelper().returnError500("兑换失败，非本人操作，请更换登录账号");
		}

		if (exchange.getStatus() != 0) {
			getHelper().returnError500("已经兑换过了");
		}

		params.put("gameId", exchange.getGameId());
		params.put("rpaId", id);
		params.put("cpId", exchange.getCpId());
		params.put("remark", "游戏兑换");
		params.put("billId", exchange.getBillId());

		String lockKey = String.format("gid_%d-act_%s-uid_%d", exchange.getGameId(), "GameCoinExchange", uid);
		boolean result = MemberService.addBean(uid, exchange.getHappyBean(), MemberLogOpType.COIN_EXCHANGE.getType(), params, lockKey);

		if (result) {
			exchange.setStatus(1);
			exchange.setUpdateTime(System.currentTimeMillis());
			int effect = Dal.update(exchange, "GameCoinExchangeDDL.status,GameCoinExchangeDDL.updateTime", new Condition("GameCoinExchangeDDL.id", "=", id));
			if (effect <= 0) {
				KeyPointLogUtil.log("coin exchange,add bean success,but update exchange status fail id=%s", id);
			}
			getHelper().returnSucc(true);
		} else {
			exchange.setStatus(2);
			exchange.setUpdateTime(System.currentTimeMillis());
			Dal.update(exchange, "GameCoinExchangeDDL.status,GameCoinExchangeDDL.updateTime", new Condition("GameCoinExchangeDDL.id", "=", id));
		}
		getHelper().returnSucc(false);
	}

	public static void getExchange() throws BusinessException {
		Map params = getDTO(Map.class);
		int id = (int) Double.parseDouble(params.get("id").toString());
		int uid = (int) Double.parseDouble(params.get("uid").toString());

		GameCoinExchangeDDL exchange = Dal.select(DaoUtil.genAllFields(GameCoinExchangeDDL.class), id);
		if (exchange == null) {
			getHelper().returnError500("兑换失败，兑换记录不存在");
		}
		if (exchange.getUid() != uid || uid == 0) {
			getHelper().returnError500("兑换失败，非本人操作，请更换登录账号");
		}

		getHelper().returnSucc(exchange);
	}

	public static void checkMobile() {
		Map<String, String> params = getDTO(Map.class);
		String mobile = params.get("mobile");
		Map<String, String> result = MemberService.checkMobile(mobile);
		getHelper().returnSucc(result);
	}

	public static void bindMobile() {
		Map<String, String> params = getDTO(Map.class);
		int uid = Integer.valueOf(params.get("uid"));
		String mobile = params.get("mobile");
		Map<String, String> result = MemberService.bindMobile(uid, mobile);
		getHelper().returnSucc(result);
	}

	public static void updateNickname() throws BusinessException {
		Map<String, String> params = getDTO(Map.class);
		int uid = Integer.parseInt(params.get("uid").toString());
		String nickName = params.get("nickname");
		Map<String, String> result = MemberService.updateNickname(uid, nickName);
		getHelper().returnSucc(result);
	}

	public static void updatePassword() {
		Map<String, String> params = getDTO(Map.class);
		String uid = params.get("uid");
		String pwd = params.get("pass");
		Map<String, String> result = MemberService.updatePassword(uid, pwd);
		getHelper().returnSucc(result);
	}

	public static void updateLoginTime() {
		Map<String, String> params = getDTO(Map.class);
		int uid = Integer.parseInt(params.get("uid").toString());
		boolean result = MemberService.updateLoginTime(uid);
		getHelper().returnSucc(result);
	}
	
	/**
	 * 消息扣豆
	 * @throws BusinessException 
	 */
	public static void consume() throws BusinessException{
		Map<String, String> params = getDTO(Map.class);

		Logger.info("Member.consume --> params:%s", params);

		int uid = Integer.parseInt(params.get("uid").toString());
		int bean = Integer.parseInt(params.get("bean").toString());
		String remark = params.get("remark");
		String caller = params.get("caller");
		String billId = params.get("billId");
		String gameId = params.get("gameId");
		String channel = params.get("channel");
		if (StringUtils.isBlank(channel)){
			channel = caller;
		}else{
			channel = caller+"."+channel;
		}
		int errCode = -1;

		if (uid <= 0) {
			getHelper().returnError(errCode, "送豆失败,uid无效");
		}

		if (bean <= 0) {
			getHelper().returnError(errCode, "送豆失败,非法开心豆");
		}

		// 检查用户是否存在大厅，如不存在则自动创建
		MemberDDL member = MemberService.getOrCreate(uid);
		if (member == null) {
			getHelper().returnError(errCode, "送豆失败,找不到用户Or创建用户失败");
		}

		// 送豆并记录 memberLog
		String lockKey = "member_consume_bean_" + uid + "_" + caller;
		params.clear();
		params.put("remark", remark);
		params.put("channel", channel);
		params.put("billId", billId);
		if(StringUtils.isNotBlank(gameId)){
			params.put("gameId", gameId);
		}
		//awardScope=2,先扣赠送的豆，再扣充值的豆
		params.put("awardScope", AwardScope.GAME.getType()+""); 
		boolean result = MemberService.consume(uid, bean, params, lockKey);

		Map<String, String> resp = new HashMap<String, String>();
		resp.put("success", "true");
		if (result) {
			Logger.info("外部服务调用扣豆接口-成功, uid:%d, bean:%d, caller:%s,", uid, bean, caller);
			getHelper().returnSucc(resp);
		} else {
			Logger.error("外部服务调用扣豆接口-失败, uid:%d, bean:%d, caller:%s,", uid, bean, caller);
			getHelper().returnError(errCode, "送豆失败,服务器异常");
		}
	}
	/**
	 * 赠送开心豆
	 * 
	 * @throws BusinessException
	 */
	public static void presentBean() throws BusinessException {
		Map<String, String> params = getDTO(Map.class);

		Logger.info("Member.presentBean --> params:%s", params);

		int uid = Integer.parseInt(params.get("uid").toString());
		int bean = Integer.parseInt(params.get("bean").toString());
		int opType = Integer.parseInt(params.get("opType").toString());//40充值，160系统退还
		String billId = params.get("billId");
		String remark = params.get("remark");
		String caller = params.get("caller");
		String gameId = params.get("gameId");
		String channel = params.get("channel");
		if (StringUtils.isBlank(channel)){
			channel = caller;
		}else{
			channel = caller+"."+channel;
		}
		
		int errCode = -1;

		if (uid <= 0) {
			getHelper().returnError(errCode, "送豆失败,uid无效");
		}

		if (bean <= 0) {
			getHelper().returnError(errCode, "送豆失败,非法开心豆");
		}

		MemberLogOpType type = MemberLogOpType.getOpType(opType);
		if (type == null) {
			getHelper().returnError(errCode, "送豆失败,无效opType");
		}

		// 单次送豆数量限制
		int maxBeanOnce = Integer.parseInt(Jws.configuration.getProperty("present_bean.max_once"));
		if (bean > maxBeanOnce) {
			Logger.error("送豆失败,单次赠送开心豆数目超出限制,bean:%d ,maxBeanOnce:%d", bean, maxBeanOnce);
			getHelper().returnError(errCode, "送豆失败,单次赠送开心豆数目超出限制");
		}

		// 单日送豆次数限制
		int maxCountDaily = Integer.parseInt(Jws.configuration.getProperty("present_bean.max_count_daily"));
		long time = DateUtil.getMorning(new Date());
		int countDaily = MemberLogService.countLogByType(opType, uid, time);
		if (countDaily >= maxCountDaily) {
			Logger.error("送豆失败,单日送豆次数超过限制,countDaily:%d ,maxCountDaily:%d", countDaily, maxCountDaily);
			getHelper().returnError(errCode, "送豆失败,单日送豆次数超过" + maxCountDaily + "次");
		}

		// 历史送豆次数限制
		int maxCountHistroy = Integer.parseInt(Jws.configuration.getProperty("present_bean.max_count_histroy"));
		int countHistroy = MemberLogService.countLogByType(opType, uid, 0);
		if (countHistroy >= maxCountHistroy) {
			Logger.error("送豆失败,历史送豆次数超过限制,countHistroy:%d ,maxCountHistroy:%d", countHistroy, maxCountHistroy);
			getHelper().returnError(errCode, "送豆失败,历史送豆次数超过" + maxCountDaily + "次");
		}

		// 检查用户是否存在大厅，如不存在则自动创建
		MemberDDL member = MemberService.getOrCreate(uid);
		if (member == null) {
			getHelper().returnError(errCode, "送豆失败,找不到用户Or创建用户失败");
		}

		// 送豆并记录 memberLog
		String lockKey = "member_present_bean_" + uid + "_" + caller;
		params.clear();
		params.put("remark", remark);
		params.put("channel", channel);
		params.put("billId", billId);
		if(StringUtils.isNotBlank(gameId)){
			params.put("gameId", gameId);
			Logger.info("Member.presentBean --> gameId:%s", gameId);
		}
		boolean result = MemberService.addBean(uid, bean, opType, params, lockKey);

		Map<String, String> resp = new HashMap<String, String>();
		resp.put("success", "true");
		if (result) {
			Logger.info("外部服务调用加豆接口-成功, uid:%d, bean:%d, caller:%s, opType:%d", uid, bean, caller, opType);
			getHelper().returnSucc(resp);
		} else {
			Logger.error("外部服务调用加豆接口-失败, uid:%d, bean:%d, caller:%s, opType:%d", uid, bean, caller, opType);
			getHelper().returnError(errCode, "送豆失败,服务器异常");
		}
	}



}
