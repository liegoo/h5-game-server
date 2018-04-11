package controllers.ucgc;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.apache.commons.lang.StringUtils;

import com.google.common.base.Strings;
import com.jfinal.plugin.activerecord.Record;

import common.core.UcgcController;
import constants.MessageCode;
import constants.SelfGame;
import exception.BusinessException;
import externals.DicService;
import externals.coupon.CouponService;
import jws.Jws;
import jws.Logger;
import jws.module.constants.award.AwardDetailStatus;
import jws.module.constants.award.AwardDetailVisible;
import jws.module.constants.award.AwardRecordDataType;
import jws.module.constants.award.AwardScope;
import jws.module.constants.award.AwardSourceType;
import jws.module.constants.award.AwardType;
import jws.module.constants.doll.ChanceReadStatus;
import jws.module.constants.doll.ChanceType;
import jws.module.constants.doll.DollAwardStatus;
import jws.module.constants.doll.DollHitStatus;
import jws.module.constants.doll.GameLevel;
import jws.module.constants.member.MemberLogOpType;
import jws.module.constants.mng.AuditStatus;
import jws.module.constants.order.OrderType;
import jws.module.response.award.AwardAssignDto;
import jws.module.response.award.AwardRecentDollDto;
import jws.module.response.award.AwardRecordDollDto;
import jws.module.response.award.ListAwardAssignRspDto;
import jws.module.response.award.ListAwardRecentDollRspDto;
import jws.module.response.award.ListAwardRecordDollRspDto;
import jws.module.response.doll.GetChanceResp;
import jws.module.response.doll.GetTrialsResp;
import moudles.award.ddl.AwardAssignDDL;
import moudles.award.ddl.AwardDDL;
import moudles.award.ddl.AwardDetailDDL;
import moudles.award.ddl.AwardRecordDollDDL;
import moudles.award.service.AwardAssignService;
import moudles.award.service.AwardDetailService;
import moudles.award.service.AwardRecordDollService;
import moudles.award.service.AwardRecordService;
import moudles.award.service.AwardService;
import moudles.blacklist.service.BlacklistService;
import moudles.capital.ddl.CapitalPoolDDL;
import moudles.capital.service.CapitalPoolService;
import moudles.chance.service.DollChanceRecordService;
import moudles.game.service.GameService;
import moudles.jackpot.ddl.JackpotDDL;
import moudles.jackpot.service.JackpotService;
import moudles.member.ddl.MemberDDL;
import moudles.member.service.MemberService;
import moudles.odds.service.OddsService;
import moudles.order.service.OrderService;
import moudles.rank.ddl.RankDDL;
import moudles.rank.service.RankService;
import moudles.task.service.GameTaskService;
import utils.DistributeCacheLock;
import utils.NumberUtil;

/**
 * 夹娃娃相关
 * 
 * @author Coming
 */
public class Doll extends UcgcController {
	private static DistributeCacheLock lock = DistributeCacheLock.getInstance();
	private static final int DOLL_GAME_ID = SelfGame.GAME_DOLL.getGameId();

	/**
	 * 奖品列表
	 * 
	 * @param gameLevel
	 * @param page
	 * @param pageSize
	 */
	public static void listAwardAssign() {
		ListAwardAssignRspDto rsp = new ListAwardAssignRspDto();
		Map params = getDTO(Map.class);
		int gameLevel = Integer.parseInt(params.get("gameLevel").toString());
		int page = Integer.parseInt(params.get("page").toString());
		int pageSize = Integer.parseInt(params.get("pageSize").toString());
		List<AwardAssignDto> list = AwardAssignService.listAwardAssign(gameLevel, page, pageSize);
		rsp.setList(list);
		getHelper().returnSucc(rsp);
	}

	/**
	 * 特权机会
	 * 
	 * @param uid
	 * @throws BusinessException
	 */
	public static void getChance() throws BusinessException {
		Map params = getDTO(Map.class);
		int uid = Integer.parseInt(params.get("uid").toString());
		GetChanceResp rsp = new GetChanceResp();
		int readStatus = ChanceReadStatus.UNREAD.getValue();
		int chance = DollChanceRecordService.getChance(uid);
		int type[] = {ChanceType.NEW_USER.getType(),ChanceType.BUNDLE_SALES.getType(),ChanceType.OTHER.getType()};
		if (DollChanceRecordService.getUnreadCount(uid,type) == 0) {
			readStatus = ChanceReadStatus.READ.getValue();
		}
		rsp.setChance(chance);
		rsp.setStatus(readStatus);
		getHelper().returnSucc(rsp);
	}

	/**
	 * 获取特权机会(做任务赠送)
	 */
	public static void getTrials() {
		Map<String, String> params = getDTO(Map.class);
		int uid = Integer.parseInt(params.get("uid"));

		GetTrialsResp rsp = new GetTrialsResp();
		int chance = DollChanceRecordService.getTrails(uid);
		int expire = 0;

		if (chance > 0) {
			int type[] = { ChanceType.PLAY_GAME.getType() };
			DollChanceRecordService.updateReadStatus(uid, type);
			expire = Integer.parseInt(Jws.configuration.getProperty("doll_game.trial.expire"));
		}

		rsp.setChance(chance);
		rsp.setExpire(expire);
		getHelper().returnSucc(rsp);
	}

	/**
	 * 更新特权机会
	 * 
	 * @param uid
	 * @throws BusinessException
	 */
	public static void updateChance() throws BusinessException {
		Map params = getDTO(Map.class);
		int uid = Integer.parseInt(params.get("uid").toString());
		int type[] = { ChanceType.NEW_USER.getType(), ChanceType.BUNDLE_SALES.getType(),ChanceType.OTHER.getType() };
		boolean result = DollChanceRecordService.updateReadStatus(uid, type);
		getHelper().returnSucc(result);
	}

	/**
	 * 获取个人中奖记录
	 * 
	 * @param gameLevel
	 * @param page
	 * @param pageSize
	 * @throws BusinessException
	 */
	public static void listAwardRecordDoll() throws BusinessException {
		ListAwardRecordDollRspDto resp = new ListAwardRecordDollRspDto();
		List<AwardRecordDollDto> awardRecordDollList = new ArrayList<AwardRecordDollDto>();
		Map params = getDTO(Map.class);
		int uid = Integer.parseInt(params.get("uid").toString());
		int hit = Integer.parseInt(params.get("hit").toString());
		Long beginTimie = Long.parseLong(params.get("beginTime").toString());
		Long endTimie = Long.parseLong(params.get("endTime").toString());
		int page = Integer.parseInt(params.get("page").toString());
		int pageSize = Integer.parseInt(params.get("pageSize").toString());
		List<AwardRecordDollDDL> list = AwardRecordDollService.listAwardRecordDoll(uid, hit, beginTimie, endTimie, page, pageSize);

		for (AwardRecordDollDDL ddl : list) {
			AwardRecordDollDto dto = new AwardRecordDollDto();
			AwardDDL award = AwardService.getAwardById(ddl.getAwardId());
			if (award != null) {		
				dto.setSell(DicService.service.resaleEnabled(String.valueOf(uid),award.getGameId()));
				dto.setId(ddl.getId());
				dto.setAwardImgUrl(award.getImgUrl());
				dto.setAwardName(award.getName());
				dto.setTime(ddl.getCreateTime());
				dto.setAuditStatus(ddl.getAuditStatus());
				dto.setHappyBean(ddl.getHappyBean());
				dto.setExchange(ddl.getExchange());
				dto.setAwardType(award.getType());
			}
			if(hit == 1){// 获奖记录
				Record awardRecord = AwardRecordService.getAwardRecord(ddl.getZhifuOrderId());
				if(awardRecord != null){
					dto.setKefuRemark(Strings.nullToEmpty(awardRecord.getStr("audit_remark")));
					String gameId = Strings.nullToEmpty(awardRecord.getStr("game_id"));
					dto.setGameId(gameId);
					dto.setGameName(Strings.nullToEmpty(awardRecord.getStr("game_name")));
					dto.setGameAccount(Strings.nullToEmpty(awardRecord.getStr("game_uid")));
					
					Record game = GameService.getCouponGame(gameId);
					if(game != null){
						dto.setGameDownloadUrl(Strings.nullToEmpty(game.getStr("down_url")));
					}
				}
			}
			awardRecordDollList.add(dto);
		}
		resp.setList(awardRecordDollList);
		getHelper().returnSucc(resp);
	}

	/**
	 * 最近中奖记录(所有人)
	 * 
	 * @param gameLevel
	 * @param page
	 * @param pageSize
	 * @throws BusinessException
	 */
	public static void listAwardRecentDoll() throws BusinessException {
		ListAwardRecentDollRspDto resp = new ListAwardRecentDollRspDto();
		List<AwardRecentDollDto> list = new ArrayList<AwardRecentDollDto>();
		List<AwardRecentDollDto> randList = new ArrayList<AwardRecentDollDto>();
		Map params = getDTO(Map.class);
		Long beginTime = Long.parseLong(params.get("beginTime").toString());
		Long endTime = Long.parseLong(params.get("endTime").toString());
		int page = Integer.parseInt(params.get("page").toString());
		int pageSize = Integer.parseInt(params.get("pageSize").toString());
		int type = Integer.parseInt(params.get("type").toString());

		if (type == AwardRecordDataType.REAL_DATA.getType()) {
			// 真实数据
			list = AwardRecordDollService.listAwardRecentDollDto(beginTime, endTime, page, pageSize);
		} else {
			// 混合运营配置数据
			list = AwardRecordDollService.listAwardRecentDollDto(5); // 最近5条中奖记录
			randList = AwardService.randListAwardRecordsTmp(5); // 随机读取5条运营配置数据
			if (randList != null) {
				list.addAll(randList);
			}
			Collections.shuffle(list); // 打乱顺序
		}
		// 昵称打星
		for (AwardRecentDollDto record : list) {
			record.setUserName(replaceWithStar(record.getUserName(), record.getMobile(), ""));
		}

		resp.setList(list);
		getHelper().returnSucc(resp);
	}

	// 昵称打星
	private static String replaceWithStar(String nickname, String mobile, String uid) {
		if (StringUtils.isEmpty(nickname)) {
			nickname = mobile;
		}
		if (StringUtils.isNotEmpty(nickname) && nickname.length() == 11) {
			nickname = (new StringBuilder(nickname.substring(0, 3)).append("****").append(nickname.substring(7, 11))).toString();
		}
		if (StringUtils.isEmpty(uid)) {
			uid = String.valueOf((int) (Math.random() * 8999) + 1000);
		}
		if (StringUtils.isEmpty(nickname)) {
			uid = uid.length() > 4 ? uid.substring(0, 4) : uid;
			nickname = (new StringBuilder("用户").append(uid).insert(3, "**")).toString();
		}
		return nickname;
	}

	/**
	 * 通知抓取
	 * 
	 * @param uid
	 * @param awardAssignId
	 * @param grap
	 * @throws BusinessException
	 */
	public static void notifyGrap() throws BusinessException {
		Map params = getDTO(Map.class);
		if (params == null || params.size() == 0) {
			throw new BusinessException(MessageCode.ERROR_CODE_500, "参数校验不通过");
		}
		if (!params.containsKey("uid") || !params.containsKey("awardId") || !params.containsKey("grap")) {
			throw new BusinessException(MessageCode.ERROR_CODE_500, "参数校验不通过");
		}

		int uid = Integer.parseInt(params.get("uid").toString()); // 用户ID
		int awardAssignId = Integer.parseInt(params.get("awardId").toString()); // 奖品配置ID
		boolean grap = Boolean.parseBoolean(params.get("grap").toString()); // 是否抓取成功

		MemberDDL member = MemberService.getMemberByUid(uid);
		if (member == null) {
			throw new BusinessException(MessageCode.ERROR_CODE_500, "找不到用户,uid:" + uid);
		}

		try {
			String grapLockKey = "Doll-Grap-" + uid;
			String lockSeconds = "2s";// 没夹中，锁2秒
			if(grap) {
				lockSeconds = "5s";
			}
			if (!lock.tryCacheLock(grapLockKey, "", lockSeconds)) {// 2017-05-31 修改（去掉 “&& grap”），防止连续抓取
				throw new BusinessException(MessageCode.ERROR_CODE_500, "抓取频繁");
			}
		} catch (Exception e) {
			throw new BusinessException(MessageCode.ERROR_CODE_500, e.getMessage());
		}

		AwardAssignDDL awardAssign = AwardAssignService.getById(awardAssignId); // 奖品配置
		if (awardAssign == null) {
			throw new BusinessException(MessageCode.ERROR_CODE_500, "找不到对应奖品配置");
		}

		AwardDDL award = AwardService.getAwardById(awardAssign.getAwardId()); // 奖品模版
		if (award == null) {
			throw new BusinessException(MessageCode.ERROR_CODE_500, "找不到对应奖品");
		}

		GameLevel gameLevel = GameLevel.getGameLevel(awardAssign.getGameLevel());

		// 判断余额
		if (member.getHappyBean() + member.getHappyBeanFromOp() < gameLevel.getCost()) {
			throw new BusinessException(MessageCode.ERROR_CODE_500, "开心豆不足");
		}

		// 创建zhifuOrder
		params.clear();
		params.put("uid", String.valueOf(uid));
		params.put("productName", award.getName());
		params.put("orderType", String.valueOf(OrderType.CONSUME.getType()));// 消费
		params.put("happyBean", String.valueOf(gameLevel.getCost()));
		params.put("sourceDesc", SelfGame.GAME_DOLL.getGameName());
		params.put("remark", SelfGame.GAME_DOLL.getGameName() + "-" + gameLevel.getDesc());
		params.put("gameId", String.valueOf(SelfGame.GAME_DOLL.getGameId()));
		params.put("gameName", SelfGame.GAME_DOLL.getGameName());
		params.put("awardScope", String.valueOf(AwardScope.GAME.getType()));
		Map orderResult = OrderService.createOrder(params);

		if (orderResult == null || (orderResult.containsKey("result") && orderResult.get("result").equals("FAIL"))) {
			throw new BusinessException(MessageCode.ERROR_CODE_500, "创建订单失败");
		}

		Logger.info("User %s ,play doll game %s ,with happy bean %s", uid, gameLevel.getDesc(), gameLevel.getCost());

		JackpotDDL jackpot = null; // 奖金池(区分场次)
		CapitalPoolDDL capitalPool = null; // 用户资金池

		float ratebackRate = 0;

		// 花费开心豆
		int cost = gameLevel.getCost();

		// 是否可以特权
		if (gameLevel.getLevel() == GameLevel.GAME_LEVEL_SPECIAL.getLevel()) {
			int chance = DollChanceRecordService.getChance(uid);
			if (chance <= 0) {
				throw new BusinessException(MessageCode.ERROR_CODE_500, "没有特权机会");
			}
			if (!DollChanceRecordService.minusChance(uid)) {
				throw new BusinessException(MessageCode.ERROR_CODE_500, "更新特权机会失败");
			}

			// 特权场定价为 1000豆
			cost = 1000;
		}

		// 更新奖池、用户资金池
		ratebackRate = Float.parseFloat(Jws.configuration.getProperty("award.ratebackRate", "0.95f")); // 返水比率
		long rateback = 0; // 返水,加入奖池

		// 设置奖金池(区分场次)
		jackpot = JackpotService.getByGameLevel(DOLL_GAME_ID, gameLevel.getLevel());
		if (jackpot == null) {
			jackpot = new JackpotDDL();
			rateback = (int) (ratebackRate * cost);
			jackpot.setGameId(DOLL_GAME_ID);
			jackpot.setGameLevel(gameLevel.getLevel());
		} else {
			rateback = jackpot.getHappyBean();
			rateback += (int) (ratebackRate * cost);
		}
		jackpot.setHappyBean(rateback);

		// 设置用户资金池
		capitalPool = CapitalPoolService.getCapitalByUid(DOLL_GAME_ID, gameLevel.getLevel(), uid);
		if (capitalPool == null) {
			capitalPool = new CapitalPoolDDL();
			capitalPool.setUid(uid);
			capitalPool.setGameId(DOLL_GAME_ID);
			capitalPool.setHappyBean(cost);
			capitalPool.setGameLevel(gameLevel.getLevel());
		} else {
			capitalPool.setHappyBean(capitalPool.getHappyBean() + cost);
		}

		boolean enough = awardAssign.getRemain() > 0; // 奖品数量是否充足
		boolean inBlacklist = BlacklistService.exists(uid); // 用户是否在黑名单内
		int weights = AwardAssignService.getWeightByGameLevel(gameLevel.getLevel()); // 同一场次所有奖器的权值之和
		float odds = 0f; // 中奖概率
		float ratio = 0f; // 加减乘系数
		AwardDetailDDL hitAward = null; // 夹中奖品
		String couponId = "";//优惠卷编号
		// 是否符合中奖条件 (当奖金池为0、负数或奖品面值大于奖金池值的时候，用户夹不中)
		if (grap && !inBlacklist && enough && weights > 0 && jackpot.getHappyBean() > 0 && award.getHappyBean() < jackpot.getHappyBean()) {
			odds = awardAssign.getWeight() / (float) weights; // 中奖概率
																// =奖品权值/sum(同一场次奖品的权值)

			Logger.info("awardAssign.getWeight / weights = odds , %s / %s = %s", awardAssign.getWeight(), (float) weights, odds);
			long capital = 0; // 用户资金池
			if (capitalPool != null) {
				capital = capitalPool.getHappyBean();
			}
			Logger.info("capital:%s", capital);

			// 计算最终中奖概率
			if (odds != 0) {
				ratio = OddsService.getRatioByHappyBean(capital, gameLevel.getLevel()); // 加减乘系数
				Logger.info("odds = odds * ratio >>>  %s, %s, %s", odds * ratio, odds, ratio);
				odds = odds * ratio; // 中奖概率 = 中奖概率*加减乘系数
			}
			// 产生随机数小数 rand, 如果rand<= 中奖概率odds, 则中奖
			if (odds > 0) {
				float rand = new Random().nextFloat();
				Logger.info("rand <= odds?:%s ,odds:%s ,rand:%s", rand <= odds, odds, rand);
				if (rand <= odds) {
					hitAward = new AwardDetailDDL(); // 中奖
					Logger.info("User %s, hit a prize %s", uid, awardAssignId);
					// 奖池、 用户资金池作相应减少	
					if(award.getType() == 4){//奖品为代金券
						Logger.info("奖品为代金券:uid=%s,order_id=%s,awardId=%s",uid,orderResult.get("orderId").toString(),award.getId());
						couponId = CouponService.apply(uid+"",1,orderResult.get("orderId").toString(),award.getId());//1：代金券 夹娃娃	
//						if(!"".equals(couponId)){
//							Logger.info("优惠卷信息：%s", couponId);
//							boolean result = AwardRecordDollService.update(orderResult.get("orderId").toString(), couponId);
//							if(!result){
//								Logger.error("更新夹娃娃记录失败");
//							}					
//						}						
					}
					jackpot.setHappyBean(jackpot.getHappyBean() - award.getHappyBean());
					capitalPool.setHappyBean(capitalPool.getHappyBean() - award.getHappyBean());
				}
			}

			// 防止重复抓取
			if (awardAssign != null) {
				try {
					String lockKey = "Doll-Grap-" + awardAssign.getId();
					if (!lock.tryCacheLock(lockKey, "", "4s")) {
						hitAward = null;
					}
				} catch (Exception e) {
					throw new BusinessException(MessageCode.ERROR_CODE_500, e.getMessage());
				}
			}
		} else {
			long jackpotBean = (jackpot != null ? jackpot.getHappyBean() : 0);
			Logger.info("不符合中奖条件>> uid:%s ,抓取成功?:%s,在黑名单内?:%s,奖品数量充足?:%s,weights?:%s,全局奖金池>奖品价值?:%s", uid, grap, inBlacklist, enough, weights,
					jackpotBean > awardAssign.getWeight());
		}

		// 更新奖池
		if (jackpot != null && !JackpotService.createOrUpdateJackpot(jackpot)) {
			throw new BusinessException(MessageCode.ERROR_CODE_500, "更新奖池失败");
		}
		// 更新用户资金池
		if (capitalPool != null && !CapitalPoolService.createOrUpdateCapitalPool(capitalPool)) {
			throw new BusinessException(MessageCode.ERROR_CODE_500, "更新用户资金池失败");
		}

		AwardRecordDollDDL awardRecordDoll = new AwardRecordDollDDL();
		// 修改中奖配置 (库存、状态)
		if(!"".equals(couponId)){
			awardRecordDoll.setBaseCouponId(couponId);
		}
		if (hitAward != null) {
			awardRecordDoll.setHit(DollHitStatus.SUCCESS.getValue());

			// 更新奖品配置数量
			awardAssign.setRemain(awardAssign.getRemain() - 1);// 剩余数减1
			awardAssign.setHits(awardAssign.getHits() + 1); // 已中奖数加1
			if (!AwardAssignService.updateAwardAssign(awardAssign)) {
				throw new BusinessException(MessageCode.ERROR_CODE_500, "更新奖品配置失败");
			}

			// 更新奖品详细
			hitAward.setUid(uid);
			hitAward.setAwardAssignId(awardAssignId);
			hitAward.setHitTime(System.currentTimeMillis());
			hitAward.setOrderId(orderResult.get("orderId").toString());
			hitAward.setStatus(AwardDetailStatus.HIT.getValue());// 已中奖
			if (!AwardDetailService.createAwardDetail(hitAward)) {
				throw new BusinessException(MessageCode.ERROR_CODE_500, "创建奖品明细失败");
			}

			// 更新排行榜
			RankDDL rank = new RankDDL();
			rank.setCreateTime(System.currentTimeMillis());
			rank.setUid(uid);
			rank.setStatus(1);
			rank.setHappyBean(award.getHappyBean());
			rank.setGameId(DOLL_GAME_ID);
			if (!RankService.createOrUpdateRank(rank)) {
				Logger.error("创建奖品明细失败");
			}
		} else {
			// 设置未中奖记录状态
			awardRecordDoll.setHit(DollHitStatus.FAILED.getValue());
			awardRecordDoll.setStatus(0);
			awardRecordDoll.setAuditStatus(0);
			awardRecordDoll.setAuditRemark("");
			Logger.info("User not hit any prize. uid:%s", uid);
		}

		float gain = (1 - ratebackRate) * gameLevel.getCost(); // 抽水
		awardRecordDoll.setGain(gain);
		awardRecordDoll.setAwardId(award.getId());
		awardRecordDoll.setHappyBean(gameLevel.getCost());
		awardRecordDoll.setZhifuOrderId(orderResult.get("orderId").toString());
		awardRecordDoll.setUid(String.valueOf(uid));
		awardRecordDoll.setOpName("");
		awardRecordDoll.setCreateTime(System.currentTimeMillis());
		awardRecordDoll.setUpdateTime(System.currentTimeMillis());
		awardRecordDoll.setExchange(1); // 可兑换
		awardRecordDoll.setGameLevel(gameLevel.getLevel());

		// 当奖品价值大于50则在左侧显示
		if (award.getHappyBean() > 50) {
			awardRecordDoll.setVisible(AwardDetailVisible.SHOW.getValue());
		} else {
			awardRecordDoll.setVisible(AwardDetailVisible.HIDE.getValue());
		}

		if (hitAward != null) {
			awardRecordDoll.setUserName(member.getNickName());
			awardRecordDoll.setMobile(member.getMobile());

			if (award.getType() == AwardType.HAPPYBEAN.getType()) {
				Logger.info("User hit happy bean. UID:%s, Award_bean:%s", uid, award.getHappyBean());

				// 中开心豆直接发放(包括特权场)
				int happyBean = award.getHappyBean();
				params.clear();
				params.put("gameId", String.valueOf(SelfGame.GAME_DOLL.getGameId()));
				params.put("remark", "夹娃娃-" + gameLevel.getDesc() + " " + award.getName());

				String lockKey = String.format("gid_%d-gl_%s-act_%s-uid_%d", SelfGame.GAME_DOLL.getGameId(), gameLevel, "awardBean", uid);
				MemberService.addBean(uid, happyBean, MemberLogOpType.GAME_AWARD.getType(), params, lockKey);

				// 设置审核、领奖状态
				awardRecordDoll.setAuditStatus(AuditStatus.DELIVERIED.getStatus());
				awardRecordDoll.setAuditRemark(AuditStatus.DELIVERIED.getDesc());
				awardRecordDoll.setStatus(DollAwardStatus.DRAW.getValue()); // 奖品状态,2-已领取

				// 添加领奖记录 award_record
				params.clear();
				params.put("uid", String.valueOf(uid));
				params.put("awardId", String.valueOf(award.getId()));
				params.put("orderId", orderResult.get("orderId").toString());
				params.put("sourceDesc", AwardSourceType.DOLL.getDesc());
				params.put("sourceType", String.valueOf(AwardSourceType.DOLL.getType()));
				params.put("remark", award.getName());
				params.put("auditStatus", String.valueOf(AuditStatus.DELIVERIED.getStatus()));
				params.put("auditRemark", AuditStatus.DELIVERIED.getDesc());
				if (!AwardService.createAwardRecord(params).get("result").toString().equalsIgnoreCase("true")) {
					throw new BusinessException(MessageCode.ERROR_CODE_500, "创建领奖记录失败");
				}
			} else {
				// 设置审核、领奖状态
				awardRecordDoll.setAuditStatus(AuditStatus.INCOMPLETE.getStatus());
				awardRecordDoll.setAuditRemark("领奖信息未完善");
				awardRecordDoll.setStatus(DollAwardStatus.NOT_DRAW.getValue());
			}
		}

		// 添加夹娃娃记录
		Long awardRecordDollId = AwardRecordDollService.createAwardRecord(awardRecordDoll);
		if (NumberUtil.isNullOrZero(awardRecordDollId)) {
			throw new BusinessException(MessageCode.ERROR_CODE_500, "创建夹娃娃领奖记录失败");
		}

		// 非特权场 参与任务
		if (gameLevel.getLevel() != GameLevel.GAME_LEVEL_SPECIAL.getLevel() && awardRecordDollId > 0) {
			GameTaskService.doTask(uid, SelfGame.GAME_DOLL.getGameId(), new Long(gameLevel.getLevel()).intValue(), gameLevel.getCost(), false);
		}

		// 返回抓取记录ID
		Map result = new HashMap();
		result.put("grapId", String.valueOf(awardRecordDollId));
		getHelper().returnSucc(result);
	}

	/**
	 * 抓取结果
	 * 
	 * @param uid
	 * @param awardId
	 * @param grap
	 * @param grapId
	 * @throws BusinessException
	 */
	public static void getGrapResult() throws BusinessException {
		Map params = getDTO(Map.class);

		if (params == null || params.size() == 0) {
			throw new BusinessException(MessageCode.ERROR_CODE_500, "参数校验不通过");
		}

		if (!params.containsKey("uid") || !params.containsKey("grapId")) {
			throw new BusinessException(MessageCode.ERROR_CODE_500, "参数校验不通过");
		}

		int uid = Integer.parseInt(params.get("uid").toString());
		int grapId = Integer.parseInt(params.get("grapId").toString());

		AwardRecordDollDDL record = AwardRecordDollService.getById(grapId);
		boolean grap = true;
		// 判断抓取ID是否有效
		if (Integer.parseInt(record.getUid()) != uid) {
			Logger.warn("请求UID 和 查询结果UID不一致");
			grap = false;
		}
		// 是否夹娃娃中
		if (record.getHit() != DollHitStatus.SUCCESS.getValue()) {
			grap = false;
		}

		// 奖品是否已领取过
		if (record.getStatus() == DollAwardStatus.DRAW.getValue()) {
			AwardDDL award = AwardService.getAwardById(record.getAwardId());

			// 如果奖品是开心豆
			if (award != null && award.getType() == AwardType.HAPPYBEAN.getType()) {
				grap = true;
			} else {
				grap = false;
			}
		}

		// 是否为特权场
		if (record.getStatus() == 0 && record.getHappyBean() != 0) {
			grap = false;
		}

		Map result = new HashMap();
		result.put("grap", grap);
		result.put("grapId", String.valueOf(grapId));
		getHelper().returnSucc(result);
	}
	/**
	 * 查询某个用户是否开启了转卖功能
	 * @param uid
	 * @return
	 */
	public static void resaleEnabled(){
		Map params = getDTO(Map.class);
		if(params.get("uid") == null || params.get("awardId") == null){
			getHelper().returnError(-10, "参数有误！");
		}
		String uid = params.get("uid").toString();
		Integer awardId = Integer.parseInt(params.get("awardId").toString());
		//return DicService.service.resaleEnabled(uid);
		AwardDDL awardDDL = null;
		try {
			awardDDL = AwardService.getAwardById(awardId);
			getHelper().returnSucc(DicService.service.resaleEnabled(uid,awardDDL.getGameId()));
		} catch (BusinessException e) {
			Logger.error("获取奖品信息失败，失败原因%s", e.getMessage());
			e.printStackTrace();
		}	
	}
}
