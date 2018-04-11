package moudles.member.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jws.Jws;
import jws.Logger;
import jws.cache.Cache;
import jws.dal.Dal;
import jws.dal.common.SqlParam;
import jws.dal.sqlbuilder.Condition;
import jws.module.constants.award.AwardScope;
import jws.module.constants.doll.ChanceStatus;
import jws.module.constants.doll.ChanceType;
import jws.module.constants.member.MemberLogOpResult;
import jws.module.constants.member.MemberLogOpType;
import jws.module.constants.member.MemberStatus;
import jws.module.response.member.UserInfoRspDto;
import moudles.chance.ddl.DollChanceConfigDDL;
import moudles.chance.ddl.DollChanceRecordDDL;
import moudles.chance.service.DollChanceConfigServivce;
import moudles.chance.service.DollChanceRecordService;
import moudles.game.ddl.GameMemberDDL;
import moudles.game.ddl.GamesDDL;
import moudles.game.service.GameMemberService;
import moudles.game.service.GameService;
import moudles.guess.ddl.GuessSeasonDDL;
import moudles.member.ddl.MemberDDL;
import moudles.member.ddl.MemberSessionDDL;

import org.apache.commons.lang3.StringUtils;

import utils.DaoUtil;
import utils.DateUtil;
import utils.DistributeCacheLock;
import utils.JsonToMap;
import utils.KeyPointLogUtil;
import utils.MD5;

import com.google.gson.JsonObject;
import com.jfinal.plugin.activerecord.Db;

import constants.MessageCode;
import constants.SelfGame;
import exception.BusinessException;
import externals.account.AccountCenterService;

public class MemberService {

	private static DistributeCacheLock lock = DistributeCacheLock.getInstance();

	/**
	 * 根据uid获取一个member对象
	 * 
	 * @param uid
	 * @return
	 */
	public static MemberDDL getMemberByUid(int uid) {
		if (uid <= 0) {
			return null;
		}
		Condition cond = new Condition("MemberDDL.uid", "=", uid);
		List<MemberDDL> members = Dal.select(DaoUtil.genAllFields(MemberDDL.class), cond, null, 0, -1);
		if (members == null || members.size() == 0) {
			return null;
		}
		return members.get(0);
	}

	/**
	 * 获得用户头像
	 */
	public static String getAvatarByUid(int uid) {
		if (uid <= 0) {
			return null;
		}
		Condition cond = new Condition("MemberDDL.uid", "=", uid);
		List<MemberDDL> members = Dal.select("MemberDDL.avatar", cond, null, 0, 1);
		if (members == null || members.size() == 0) {
			return null;
		}
		return members.get(0).getAvatar();
	}

	/**
	 * 根据mobile获取一个member对象
	 * 
	 * @param uid
	 * @return
	 */
	public static MemberDDL getMemberByMobile(String mobile) {
		if (StringUtils.isBlank(mobile)) {
			return null;
		}
		Condition cond = new Condition("MemberDDL.mobile", "=", mobile);
		List<MemberDDL> members = Dal.select(DaoUtil.genAllFields(MemberDDL.class), cond, null, 0, -1);
		if (members == null || members.size() == 0) {
			return null;
		}
		return members.get(0);
	}

	/**
	 * 更新Member
	 * 
	 * @param member
	 * @return
	 */
	public static boolean updateMember(MemberDDL member) {
		Condition cond = new Condition("MemberDDL.uid", "=", member.getUid());
		return Dal.update(member, "MemberDDL.mobile,MemberDDL.nickName,MemberDDL.lastLoginTime", cond) > 0;
	}

	/**
	 * 获取Or创建用户
	 * 
	 * @param uid
	 * @return
	 * @throws BusinessException
	 */
	public static MemberDDL getOrCreate(int uid) throws BusinessException {
		MemberDDL member = getMemberByUid(uid);

		if (member == null) {
			JsonObject resultJson = AccountCenterService.getUserInfoByUid(uid);
			// 用户中心存在UID,加到大厅Member表
			if (resultJson != null && resultJson.has("code") && resultJson.get("code").getAsInt() == 0 && resultJson.has("data") && resultJson.get("data") != null) {
				String dataStr = resultJson.get("data").getAsString();
				JsonObject dataObj = JsonToMap.parseJson(dataStr);
				String nickName = dataObj.has("nickName") ? dataObj.get("nickName").getAsString() : "";
				String mobile = dataObj.has("mobile") ? dataObj.get("mobile").getAsString() : "";
				Map memberParams = new HashMap();
				memberParams.put("nickName", nickName);
				memberParams.put("mobile", mobile);
				member = addMember(uid, "", "", memberParams);
			}
		}
		return member;
	}

	/**
	 * 消耗开心豆
	 * 
	 * @param uid
	 * @param happyBean
	 * @param params
	 *            扩展参数，用以记录更明细操作日志
	 * @param lockKey
	 *            扣豆锁 (参考格式：GameId_OperationName_UID)
	 * @return
	 */
	public static boolean consume(int uid, int happyBean, Map<String, String> params, String lockKey) {

		if (StringUtils.isEmpty(lockKey)) {
			lockKey = "Consume-" + uid;
		}

		try {
			if (!lock.tryCacheLock(lockKey, "", "2s")) {
				throw new BusinessException(MessageCode.ERROR_CODE_500, "重复消耗");
			}
			MemberDDL member = getMemberByUid(uid);
			if (member == null) {
				throw new BusinessException(MessageCode.ERROR_CODE_500, "用户账户不存在");
			}
			int balance = member.getHappyBean() + member.getHappyBeanFromOp();
			int awardScope = AwardScope.AWARDCENTER.getType();
			if (params.containsKey("awardScope")) {
				awardScope = Integer.parseInt(params.get("awardScope"));
			}
			Logger.info("awardScope: >>%s:", awardScope);
			// 人工赠送的豆不能用于兑换奖品
			int effect = 0;
			if (awardScope == AwardScope.GAME.getType()) {
				if ((member.getHappyBean() + member.getHappyBeanFromOp()) < happyBean) {
					throw new BusinessException(MessageCode.ERROR_CODE_500, "用户账户开心豆不足");
				}
				// 游戏中的消费 优先使用 人工赠送的开心豆
				if (member.getHappyBeanFromOp() > happyBean) {
//					member.setHappyBeanFromOp(member.getHappyBeanFromOp() - happyBean);
					effect = updateMember(member.getUid(), null, 0-happyBean);
				} else {
					int owe = happyBean - member.getHappyBeanFromOp();
//					member.setHappyBeanFromOp(0);
//					member.setHappyBean(member.getHappyBean() - owe);
					effect = updateMember(member.getUid(), 0-owe, 0-member.getHappyBeanFromOp());
				}
			} else {
				if (member.getHappyBean() < happyBean) {
					throw new BusinessException(MessageCode.ERROR_CODE_500, "用户账户开心豆不足");
				}
				member.setHappyBean(member.getHappyBean() - happyBean);
				effect = updateMember(member.getUid(), 0-happyBean, null);
			}

//			int effect = Dal.update(member, "MemberDDL.happyBean,MemberDDL.happyBeanFromOp", new Condition("MemberDDL.id", "=", member.getId()));
			if (effect == 0) {
				KeyPointLogUtil.log("Consume update happyBean fail,uid=%s,bean=%s", member.getUid(), happyBean);
			} else {
				balance -= happyBean;
			}
			params = params == null ? new HashMap<String, String>() : params;
			params.put("happyBean", String.valueOf(happyBean));

			int opType = MemberLogOpType.CONSUME.getType();

			if (params.containsKey("opType")) {
				opType = Integer.valueOf(String.valueOf(params.get("opType")));
			}

			MemberLogService.createMemberLog(uid, effect > 0 ? MemberLogOpResult.SUCCESS.getType() : MemberLogOpResult.FAILE.getType(), opType, balance, params);

			return effect > 0;
		} catch (Exception e) {
			Logger.error(e, "");
		} finally {
			lock.cacheUnLock(lockKey);
		}
		return false;
	}

	// 赠送类型开心豆
	private static List<Integer> opBeanType = new ArrayList<Integer>();
	static {
		opBeanType.add(MemberLogOpType.PRESENTED.getType());
		opBeanType.add(MemberLogOpType.SYS_PRESENT.getType());
		opBeanType.add(MemberLogOpType.TASK_AWARD.getType());
		opBeanType.add(MemberLogOpType.GAME_BOOBY.getType());
		opBeanType.add(MemberLogOpType.RECHARGE_PRESENT.getType());
	}

	/**
	 * 增加开心豆
	 * 
	 * @param uid
	 * @param happyBean
	 * @param type
	 *            签名 | 40充值 | 160赠送 | 190充值不可兑
	 * @param params
	 *            扩展参数，用以记录更明细操作日志
	 * @param lockKey
	 *            加豆锁 (参考格式：GameId_OperationName_UID)
	 * 
	 * @return
	 */
	public static boolean addBean(int uid, int happyBean, int type, Map<String, String> params, String lockKey) {
		if (StringUtils.isEmpty(lockKey)) {
			lockKey = "addBean-" + uid;
		}

		try {

			if (!lock.tryCacheLock(lockKey, "", "2s")) {// 所有的lock没什么用好像，参考test下的Main方法测试，因为是二手开发，无法不确定是否可删除这行代码
				Logger.error("Member.addBean - 重复消耗, uid:%s, happyBean:%s ", uid, happyBean);
				throw new BusinessException(MessageCode.ERROR_CODE_500, "重复消耗");
			}

			MemberDDL member = getMemberByUid(uid);
			if (member == null) {
				throw new BusinessException(MessageCode.ERROR_CODE_500, "用户账户不存在");
			}
			int balance = member.getHappyBean() + member.getHappyBeanFromOp();

			int effect = 0;
			// 赠送类型的开心豆 存到HappyBeanFromOp
			if (opBeanType.contains(type)) {
				effect = updateMember(member.getUid(), null, happyBean);
//				member.setHappyBeanFromOp(member.getHappyBeanFromOp() + happyBean);
			} else {
				effect = updateMember(member.getUid(), happyBean, null);
//				member.setHappyBean(member.getHappyBean() + happyBean);
			}
//			int effect = Dal.update(member, "MemberDDL.happyBean,MemberDDL.happyBeanFromOp", new Condition("MemberDDL.id", "=", member.getId()));

			params = params == null ? new HashMap<String, String>() : params;
			params.put("happyBean", String.valueOf(happyBean));

			if (effect <= 0) {
				Logger.error("Member.addBean - 更新用户开心豆失败, uid:%s, happyBean:%s ", member.getUid(), happyBean);
				KeyPointLogUtil.log("add bean update happyBean fail,uid=%s,bean=%s", member.getUid(), happyBean);
			} else {
				balance += happyBean;
			}

			MemberLogService.createMemberLog(uid, effect > 0 ? MemberLogOpResult.SUCCESS.getType() : MemberLogOpResult.FAILE.getType(), type, balance, params);

			return effect > 0;

		} catch (Exception e) {
			Logger.error(e, "");
		} finally {
			if (type != MemberLogOpType.PRESENTED.getType()) {
				lock.cacheUnLock(lockKey);
			}
		}
		return false;
	}

	/**
	 * 根据配置赠送用户特权机会
	 * 
	 * @param uid
	 * @throws BusinessException
	 */
	private static void setChanceByConfig(int uid) throws BusinessException {
		DollChanceConfigDDL chanceConfig = DollChanceConfigServivce.getFirstConfig();
		if (chanceConfig == null) {
			return;
		}

		long current = System.currentTimeMillis();
		if (current > chanceConfig.getStartTime() && current < chanceConfig.getEndTime()) {
			DollChanceRecordDDL chance = new DollChanceRecordDDL();
			chance.setUid(uid);
			chance.setChance(chanceConfig.getChance());
			chance.setRemain(chanceConfig.getChance());
			chance.setRemark("首次使用赠送");
			chance.setType(ChanceType.NEW_USER.getType());
			chance.setStatus(ChanceStatus.AVAILABLE.getValue());
			chance.setCreateTime(System.currentTimeMillis());
			chance.setUpdateTime(System.currentTimeMillis());
			if (!DollChanceRecordService.create(chance)) {
				throw new BusinessException(MessageCode.ERROR_CODE_500, "赠送特权机会失败");
			} else {
				Logger.info("新用户赠送特权机会, 用户:%s, %s次特权机会.", uid, chanceConfig.getChance());
			}
		}
	}

	/**
	 * 登录游戏
	 */
	public static Map<String, String> login4Game(int uid, int gameId) throws BusinessException {
		Map<String, String> result = new HashMap<String, String>();
		result.put("code", "-1");
		result.put("msg", "登录游戏失败");
		try {
			GamesDDL game = GameService.getGame(gameId);
			int suid = 0;
			if (null == game) {
				throw new BusinessException(MessageCode.ERROR_CODE_500, "游戏不存在,gameId="+gameId);
			}

			// 查找游戏子账号，查不到则新增
			GameMemberDDL gameMember = GameMemberService.getGameByUidAndGameId(uid, gameId);
			if (null == gameMember) {
				if (gameId == SelfGame.GAME_DOLL.getGameId()) {
					setChanceByConfig(uid);
				}
				gameMember = new GameMemberDDL();
				gameMember.setCpId(game.getCpId());
				gameMember.setGameId(gameId);
				gameMember.setUid(uid);
				gameMember.setStatus(1);
				gameMember.setCreateTime(System.currentTimeMillis());
				suid = new Long(Dal.insertSelectLastId(gameMember)).intValue();
			} else {
				suid = gameMember.getSuid();
			}

			MemberSessionDDL memberSession = MemberSessionService.getMemberSessionByUidAndGameId(uid, gameId);
			String tmpToken = MD5.encode("TMP_TOKEN" + uid + gameId + System.currentTimeMillis());
			String token = "";
			if (null == memberSession) {
				memberSession = new MemberSessionDDL();
				memberSession.setUid(uid);
				memberSession.setCpId(game.getCpId());
				memberSession.setGameId(gameId);
				token = MD5.encode("TOKEN" + uid + gameId + System.currentTimeMillis());
				memberSession.setTmpToken(tmpToken);
				memberSession.setSuid(suid);
				memberSession.setCreateTime(System.currentTimeMillis());
				memberSession.setTocken(token);
				memberSession.setExpTime(DateUtil.addDay(System.currentTimeMillis(), Integer.parseInt(Jws.configuration.getProperty("member.session.effective.day", "30"))));
				if (Dal.insert(memberSession) <= 0) {
					throw new BusinessException(MessageCode.ERROR_CODE_500, "创建会话失败");
				}
			} else {
				Condition cond = new Condition("MemberSessionDDL.uid", "=", uid);
				cond.add(new Condition("MemberSessionDDL.gameId", "=", gameId), "AND");
				memberSession.setTmpToken(tmpToken);
				memberSession.setExpTime(DateUtil.addDay(System.currentTimeMillis(), Integer.parseInt(Jws.configuration.getProperty("member.session.effective.day", "30"))));
				token = memberSession.getToken();
				if (Dal.update(memberSession, "MemberSessionDDL.tmpToken,MemberSessionDDL.expTime", cond) <= 0) {
					throw new BusinessException(MessageCode.ERROR_CODE_500, "更新会话失败");
				}
			}
			Cache.set(tmpToken, token, "60s");
			result.put("code", "0");
			result.put("msg", "登录游戏成功");
			String gameUrl = game.getGameUrl();
			String backUrl = Jws.configuration.getProperty("h5game.web.inex");
			if (gameUrl.indexOf("?") != -1) {
				result.put("url", game.getGameUrl() + "&token=" + tmpToken + "&backUrl=" + backUrl);
			} else {
				result.put("url", game.getGameUrl() + "?token=" + tmpToken + "&backUrl=" + backUrl);
			}
		} catch (Exception e) {
			result.put("code", "-1");
			result.put("msg", "登录游戏失败:" + e.getMessage());
			Logger.error(e, "");
		}
		return result;
	}

	/**
	 * 根据uid获取一个member对象
	 * 
	 * @param uid
	 * @return
	 * @throws BusinessException
	 */
	public static UserInfoRspDto checkLoginByToken(String sid) throws BusinessException {
		UserInfoRspDto rsp = new UserInfoRspDto();
		String nickName = "";
		String mobile = "";
		int uid = 0;
		JsonObject result = AccountCenterService.checkLoginByToken(sid);
		if (result != null && result.get("code") != null && result.get("code").getAsInt() == 0) {
			String data = result.get("data").getAsString();
			JsonObject dataObj = JsonToMap.parseJson(data);
			if (dataObj.get("isLogin").getAsBoolean()) {
				nickName = dataObj.get("nickName")==null?"":dataObj.get("nickName").getAsString();
				mobile = dataObj.get("mobile")==null?"":dataObj.get("mobile").getAsString();
				uid = dataObj.get("uid")==null?0:dataObj.get("uid").getAsInt();
			}
		} else {
			Logger.error("MemberService.checkLoginByToken,Exception=调用用户中心登录校验失败:" + (result == null ? "返回为null" : result.get("msg").getAsString()));
			throw new BusinessException(MessageCode.ERROR_CODE_500, "登录校验失败:" + (result == null ? "返回为null" : result.get("msg").getAsString()));
		}
		if (uid <= 0) {
			return null;
		}
		try {
			MemberDDL member = getMemberByUid(uid);
			// 判断当前账号是否存在,不存在则将当前账号添加到库中
			if (null == member) {
				Map<String, String> params = new HashMap<String, String>();
				params.put("nickName", nickName);
				params.put("mobile", mobile);
				params.put("createTime", System.currentTimeMillis() + "");
				member = addMember(uid, "", "", params);
			}
			rsp.setUid(uid);
			rsp.setMobile(mobile);
			rsp.setNickName(nickName);
			rsp.setHappyBean(member.getHappyBean());
			rsp.setContinueCheckIn(member.getContinueCheckIn());
			rsp.setCh(member.getCh());
		} catch (Exception e) {
			Logger.error(e, "");
			Logger.error("uid %s login in Exception:%s", uid, e.getMessage());
			throw new BusinessException(MessageCode.ERROR_CODE_500, "登录校验失败，服务器内部错误");
		}
		return rsp;
	}

	/**
	 * 创建用户信息
	 */
	public static MemberDDL addMember(int uid, String pwd, String ch, Map<String, String> params) throws BusinessException {
		MemberDDL ddl = new MemberDDL();
		ddl.setUid(uid);
		ddl.setPassword(pwd);
		ddl.setCh(ch);
		ddl.setAccount((params != null && params.containsKey("account")) ? params.get("account") : "");
		ddl.setHappyBean((params != null && params.containsKey("happyBean")) ? Integer.parseInt(params.get("happyBean")) : 0);
		ddl.setNickName((params != null && params.containsKey("nickName")) ? params.get("nickName") : "");
		ddl.setMobile((params != null && params.containsKey("mobile")) ? params.get("mobile") : null);
		ddl.setAvatar((params != null && params.containsKey("avatar")) ? params.get("avatar") : "");
		ddl.setLastLoginTime(DateUtil.getNow().getTime());
		ddl.setCreateTime(Long.valueOf((params != null && params.containsKey("createTime")) ? params.get("createTime") : System.currentTimeMillis() + ""));
		ddl.setStatus(MemberStatus.STATUS_NORMAL.getStatus());

		if (Dal.insert(ddl) > 0) {
			return ddl;
		} else {
			return null;
		}
	}

	/**
	 * 手机号登录
	 */
	public static Map<String, String> loginByMobile(String mobile, String mobileCode) throws BusinessException {
		Map<String, String> result = new HashMap<String, String>();
		result.put("code", "-1");
		int uid = 0;
		String token = "";
		String nickName = "";
		int balance = 0;
		try {
			// 短信语音验证码在调用用户中心时用户中心校验
			String smsAppKey = Jws.configuration.getProperty("validate.code.appkey", "web");
			JsonObject jsonObj = AccountCenterService.loginByDynamicDigital(mobile, mobileCode, "", smsAppKey);
			if (jsonObj != null && jsonObj.get("code") != null && jsonObj.get("code").getAsInt() == 0) {
				String data = jsonObj.get("data").getAsString();
				JsonObject dataObj = JsonToMap.parseJson(data);
				token = dataObj.get("token").getAsString();
				uid = dataObj.get("uid").getAsInt();
				// 昵称为空则用手机号代替
				nickName = StringUtils.isBlank(dataObj.get("nickName").getAsString()) ? mobile : dataObj.get("nickName").getAsString();
			} else {
				Logger.error("MemberService.login,Exception=调用用户中心登录失败:" + (jsonObj == null ? "返回为null" : jsonObj.get("msg").getAsString()));
				result.put("msg", (jsonObj == null ? "返回为null" : jsonObj.get("msg").getAsString()));
			}

			// 登录成功
			if (uid > 0 && !StringUtils.isBlank(token)) {
				// 判断本地平台是否存在用户，没有则本地创建一个
				MemberDDL member = MemberService.getMemberByUid(uid);
				if (null == member) {
					Map<String, String> params = new HashMap<String, String>();
					params.put("nickName", nickName);
					params.put("mobile", mobile);
					MemberService.addMember(uid, "", "", params);
				} else {
					balance = member.getHappyBean() + member.getHappyBeanFromOp();
				}

				result.put("code", "0");
				result.put("uid", String.valueOf(uid));
				result.put("token", token);
				result.put("nickName", nickName);
				result.put("msg", "登录成功");
			}
		} catch (Exception e) {
			result.put("code", "-1");
			result.put("msg", "系统内部异常");
			Logger.error(e, "");
		}
		int opResult = Integer.parseInt(result.get("code").toString()) == 0 ? MemberLogOpResult.SUCCESS.getType() : MemberLogOpResult.FAILE.getType();
		// 添加日志
		Map<String, String> logParams = new HashMap<String, String>();
		logParams.put("remark", Integer.parseInt(result.get("code").toString()) == 0 ? "登录成功" : "登录失败");
		MemberLogService.createMemberLog(uid, opResult, MemberLogOpType.LOGIN.getType(), balance, logParams);
		return result;
	}

	/**
	 * 检查手机号是否可用
	 */
	public static Map<String, String> checkMobile(String mobile) {
		Map<String, String> result = new HashMap<String, String>();
		result.put("code", "-1");
		result.put("exists", "false");
		if (getMemberByMobile(mobile) != null) {
			result.put("code", "0");
			result.put("exists", "true");
		} else {
			JsonObject jsonObj = AccountCenterService.checkMobile(mobile);
			if (jsonObj != null && jsonObj.get("code") != null && jsonObj.get("code").getAsInt() == 0) {
				String data = jsonObj.get("data").getAsString();
				JsonObject dataObj = JsonToMap.parseJson(data);
				result.put("code", "0");
				result.put("exists", String.valueOf(dataObj.get("isExist").getAsBoolean()));
			} else {
				result.put("code", "-1");
				Logger.error("MemberService.checkMobile,Exception=调用用户中心登录失败:" + (jsonObj == null ? "返回为null" : jsonObj.get("msg").getAsString()));
			}
		}
		return result;
	}

	/**
	 * 绑定手机号
	 */
	public static Map<String, String> bindMobile(int uid, String mobile) {
		Map<String, String> result = new HashMap<String, String>();
		result.put("code", "1");
		result.put("success", "false");
		if (uid == 0) {
			result.put("code", "-1");
		}
		if (StringUtils.isNotBlank(mobile)) {
			result.put("code", "-1");
		}

		JsonObject jsonObj = AccountCenterService.bindMobile(uid, mobile);
		if (jsonObj != null && jsonObj.get("code") != null && jsonObj.get("code").getAsInt() == 0 && jsonObj.has("data") && jsonObj.get("data").toString().equals("1")) {

			boolean flag = false;
			MemberDDL member = getMemberByUid(uid);
			if (member != null) {
				member.setMobile(mobile);
				flag = updateMember(member);
			}

			if (flag) {
				result.put("code", "0");
				result.put("success", "true");
			}
		} else {
			result.put("code", "-1");
			Logger.error("MemberService.bindMobile,Exception=调用用户中心绑定手机失败:" + (jsonObj == null ? "返回为null" : jsonObj.get("msg").getAsString()));
		}
		return result;
	}

	/**
	 * 更新昵称
	 */
	public static Map<String, String> updateNickname(int uid, String nickname) throws BusinessException {
		Map<String, String> result = new HashMap<String, String>();
		result.put("code", "1");
		result.put("success", "false");
		if (StringUtils.isNotBlank(nickname) || uid == 0) {
			result.put("code", "-1");
		}

		JsonObject jsonObj = AccountCenterService.updateNickname(uid, nickname);
		if (jsonObj != null && jsonObj.get("code") != null && jsonObj.get("code").getAsInt() == 0) {
			boolean flag = false;
			MemberDDL member = getMemberByUid(uid);
			if (member != null) {
				member.setNickName(nickname);
				flag = updateMember(member);
			}

			if (flag) {
				result.put("code", "0");
				result.put("success", "true");
			}
		} else {
			result.put("code", "-1");
			if(jsonObj != null && jsonObj.get("msg") != null){
				result.put("msg", jsonObj.get("msg").getAsString());
			}
			Logger.error("MemberService.updateNickname,Exception=调用用户中心登录失败:" + (jsonObj == null ? "返回为null" : jsonObj.get("msg").getAsString()));
		}
		return result;
	}

	// 更新密码 TODO 未连调
	public static Map<String, String> updatePassword(String uid, String pwd) {
		Map<String, String> result = new HashMap<String, String>();
		result.put("code", "-1");
		result.put("success", "false");

		if (StringUtils.isNotBlank(uid)) {
			result.put("code", "-1");
		}
		if (StringUtils.isNotBlank(pwd)) {
			result.put("code", "-1");
		}

		JsonObject jsonObj = AccountCenterService.updatePassword(uid, pwd);
		if (jsonObj != null && jsonObj.get("code") != null && jsonObj.get("code").getAsInt() == 0) {
			result.put("code", "0");
			result.put("success", "true");
		} else {
			result.put("code", "-1");
			Logger.error("MemberService.updateNickname,Exception=调用用户中心修改密码失败:" + (jsonObj == null ? "返回为null" : jsonObj.get("msg").getAsString()));
		}
		return result;
	}

	/**
	 * 手机号登录
	 */
	public static Map<String, String> login(String account, String pwd) throws BusinessException {
		Map<String, String> result = new HashMap<String, String>();
		result.put("code", "-1");
		int uid = 0;
		String token = "";
		String nickName = "";
		String mobile = "";
		int balance = 0;
		try {
			// 短信语音验证码在调用用户中心时用户中心校验
			String busi = Jws.configuration.getProperty("account.center.busiCode", "h5game");
			JsonObject jsonObj = AccountCenterService.login(account, pwd, busi);
			if (jsonObj != null && jsonObj.get("code") != null && jsonObj.get("code").getAsInt() == 0) {
				String data = jsonObj.get("data").getAsString();
				JsonObject dataObj = JsonToMap.parseJson(data);
				token = dataObj.get("token").getAsString();
				uid = dataObj.get("uid").getAsInt();
				mobile = dataObj.get("mobile").getAsString();
				// 昵称为空则用手机号代替
				nickName = StringUtils.isBlank(dataObj.get("nickName").getAsString()) ? mobile : dataObj.get("nickName").getAsString();
			} else {
				Logger.error("MemberService.login,Exception=调用用户中心登录失败:" + (jsonObj == null ? "返回为null" : jsonObj.get("msg").getAsString()));
				result.put("msg", (jsonObj == null ? "返回为null" : jsonObj.get("msg").getAsString()));
			}

			// 登录成功
			if (uid > 0 && !StringUtils.isBlank(token)) {
				// 判断本地平台是否存在用户，没有则本地创建一个
				MemberDDL member = MemberService.getMemberByUid(uid);
				if (null == member) {
					Map<String, String> params = new HashMap<String, String>();
					params.put("nickName", nickName);
					params.put("mobile", mobile);
					MemberService.addMember(uid, "", "", params);
				} else {
					balance = member.getHappyBean() + member.getHappyBeanFromOp();
				}
				result.put("code", "0");
				result.put("uid", String.valueOf(uid));
				result.put("token", token);
				result.put("mobile", mobile);
				result.put("nickName", nickName);
				result.put("msg", "登录成功");
			}
		} catch (Exception e) {
			result.put("code", "-1");
			result.put("msg", "系统内部异常");
			Logger.error(e, "");
		}
		int opResult = Integer.parseInt(result.get("code").toString()) == 0 ? MemberLogOpResult.SUCCESS.getType() : MemberLogOpResult.FAILE.getType();
		// 添加日志
		Map<String, String> logParams = new HashMap<String, String>();
		logParams.put("remark", Integer.parseInt(result.get("code").toString()) == 0 ? "登录成功" : "登录失败");
		MemberLogService.createMemberLog(uid, opResult, MemberLogOpType.LOGIN.getType(), balance, logParams);
		return result;
	}

	/**
	 * 更新用户登录时间
	 * 
	 * @param uid
	 * @return
	 */
	public static boolean updateLoginTime(int uid) {
		MemberDDL member = getMemberByUid(uid);
		if (member == null) {
			return false;
		}
		member.setLastLoginTime(System.currentTimeMillis());
		return updateMember(member);
	}
	
	/**
	 * 更新用户开心豆，避免使用DDL更新带来的并发问题，2017-05-27做了扣豆时豆必须大于0的限制(数据库出现豆为0的问题)
	 * @param uid
	 * @param happyBean		需要增加的豆，如果要扣除豆则此处为负值，如果不要要变更值，则传null
	 * @param happyBeanFromOp	需要增加的运营赠送豆，如果要扣除豆则此处为负值，如果不要要变更值，则传null
	 * @return
	 */
	private static int updateMember(int uid, Integer addHappyBean, Integer addHappyBeanFromOp) {
		Logger.info("更新用户开心豆，uid=%s,addHappyBean=%s,addHappyBeanFromOp=%s", uid, addHappyBean, addHappyBeanFromOp);
		if(addHappyBean == null && addHappyBeanFromOp == null) {
			return 0;
		}
		List<SqlParam> params = new ArrayList<SqlParam>();
		StringBuilder sql = new StringBuilder("update member set ");
		
		List<SqlParam> whereParams = new ArrayList<SqlParam>();
		StringBuilder whereSql = new StringBuilder(" where uid=? ");
		whereParams.add(new SqlParam("MemberDDL.uid", uid));
		
		if(addHappyBean != null) {
			sql.append("happy_bean=").append("happy_bean").append(addHappyBean>=0?"+":"-").append("?,");
			params.add(new SqlParam("MemberDDL.happyBean", Math.abs(addHappyBean)));
			if(addHappyBean < 0) {// 扣除豆时，添加扣豆不能小于0的判断
				whereSql.append("and happy_bean>=? ");
				whereParams.add(new SqlParam("MemberDDL.happyBean", Math.abs(addHappyBean)));
			}
		}
		if(addHappyBeanFromOp != null) {
			sql.append("happy_bean_from_op=").append("happy_bean_from_op").append(addHappyBeanFromOp>=0?"+":"-").append("?,");
			params.add(new SqlParam("MemberDDL.happyBeanFromOp", Math.abs(addHappyBeanFromOp)));
			if(addHappyBeanFromOp < 0) {// 扣除豆时，添加扣豆不能小于0的判断
				whereSql.append("and happy_bean_from_op>=? ");
				whereParams.add(new SqlParam("MemberDDL.happyBeanFromOp", Math.abs(addHappyBeanFromOp)));
			}
		}
		sql.deleteCharAt(sql.length() - 1);
		sql.append(whereSql);
		params.addAll(whereParams);
		// 此处不使用下面注释的代码，是因为如果该方法在一个事务中被调用，事务对注释代码是起不了作用的
		return Dal.executeNonQuery(MemberDDL.class, sql.toString(), params, null);
//		return Db.update(sql.toString(), params.toArray());
	}
	/**
	 * 签到加VIP积分
	 * @param uid
	 * @return
	 */
	public static boolean addScoreForHappyBeanCheckIn(int uid){
		return AccountCenterService.addScoreForHappyBeanCheckIn(uid);
	}
	
	/**
	 * 充值加vip积分
	 * @param uid
	 * @param rechargeAmount
	 * @return
	 */
	public static boolean addScoreForHappyBeanRecharge(int uid, double rechargeAmount){
		return AccountCenterService.addScoreForHappyBeanRecharge(uid, rechargeAmount);
	}
	
	public static void main(String[] args) {
//		Logger.info("更新用户开心豆，uid=%s,addHappyBean=%s,addHappyBeanFromOp=%s", 1, 10, -100);
//		System.err.println(updateMember(1, -100, null));
	}
}
