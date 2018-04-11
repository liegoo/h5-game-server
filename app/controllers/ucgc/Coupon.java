package controllers.ucgc;

import java.util.Map;
import java.util.Random;

import org.apache.commons.lang.StringUtils;

import com.google.common.base.Strings;
import com.jfinal.plugin.activerecord.Record;

import common.core.UcgcController;
import exception.BusinessException;
import externals.CommonService;
import externals.DicService;
import externals.coupon.CouponService;
import jws.Logger;
import jws.module.constants.award.AwardSourceType;
import jws.module.constants.award.AwardType;
import jws.module.constants.doll.DollAwardStatus;
import jws.module.constants.member.MemberLogOpType;
import jws.module.constants.mng.AuditStatus;
import jws.module.constants.order.OrderType;
import moudles.award.ddl.AwardDDL;
import moudles.award.ddl.AwardRecordDDL;
import moudles.award.ddl.AwardRecordDollDDL;
import moudles.award.service.AwardRecordDollService;
import moudles.award.service.AwardRecordService;
import moudles.award.service.AwardService;
import moudles.order.service.OrderService;
import sun.util.logging.resources.logging;
import utils.DistributeCacheLock;

/**
 * 夺宝游戏
 * 
 * @author liuzz
 */
public class Coupon extends UcgcController {
	static org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(Coupon.class);
	private static DistributeCacheLock lock = DistributeCacheLock.getInstance();
	
	/**
	 * 转卖代金券（用于夹娃娃和限量代金券）
	 * TODO 该方法应该使用事务
	 */
	public static void saveSell(){
		Map params = getDTO(Map.class);
		if(params.get("uid") == null || params.get("awardId") == null || 
				params.get("sellPrice") == null || params.get("clientIp") == null){
			getHelper().returnError(-10, "参数有误！");
		}
		int uid = Integer.parseInt(params.get("uid").toString());
		int awardId = Integer.parseInt(params.get("awardId").toString());
		double sellPrice = Double.parseDouble(params.get("sellPrice").toString());
		String clientIp = params.get("clientIp").toString();
		Integer awardRecDollId = null;
		if(params.get("awardRecDollId") != null){// 夹娃娃奖品转卖
			awardRecDollId = Integer.parseInt(params.get("awardRecDollId").toString());
		}
		logger.info(uid+"请求出售优惠券：awardId="+awardId+",awardRecDollId="+awardRecDollId+",sellPrice="+sellPrice);
		AwardDDL awardDDL = null;
		try {
			awardDDL = AwardService.getAwardById(awardId);
		} catch (BusinessException e1) {
			Logger.error("获取奖品信息失败%s",e1.getMessage());
			e1.printStackTrace();
		}
		if(!DicService.service.resaleEnabled(String.valueOf(uid),awardDDL.getGameId())){
			getHelper().returnError(-15, "无权转售代金券！");
		}
		AwardDDL award = null;
		try{// 校验奖品
			award = AwardService.getAwardById(awardId);
			if(award == null){
				Logger.warn("无法获取奖品信息（"+awardId+"）");
				throw new Exception("无法获取奖品信息");
			}
		}catch(Exception e){
			getHelper().returnError(-20, "无法获取优惠券");
		}
		// 校验类型
		if(award.getType() != AwardType.COUPON.getType()){
			getHelper().returnError(-23, "只能转卖代金券奖品");
		}
		Integer price = CommonService.getCouponPrice(award.getName());
		// 判断参考价是否小于原价
		if(price != null && sellPrice > price){
			getHelper().returnError(-25, "售价不能大于优惠券价格");
		}
		
		AwardSourceType sourceType = AwardSourceType.AWARD;
		String orderId = "H5GMORD-" + System.currentTimeMillis() + "-" + (new Random().nextInt(899999) + 100000);// 提前制定订单ID，而不是在创建订单后获取ID，因为申请代金券操作如果失败了，这个订单是不应该产生的(限量领奖)
		String baseCouponId = null;
		
		AwardRecordDollDDL awardRecordDoll = null;
		if(awardRecDollId != null){// 如果是夹娃娃转卖则判断状态
			awardRecordDoll = AwardRecordDollService.getById(awardRecDollId);
			if(awardRecordDoll == null || awardRecordDoll.getAwardId() != awardId 
					|| !awardRecordDoll.getUid().equals(String.valueOf(uid)) 
					|| awardRecordDoll.getHit() != 1 
					|| awardRecordDoll.getStatus() != 1
					|| (awardRecordDoll.getHappyBean() == 0 && awardRecordDoll.getExchange() != 1)
					|| awardRecordDoll.getAuditStatus() != AuditStatus.INCOMPLETE.getStatus()){// 如果是转卖夹娃娃中奖优惠券则需校验优惠券的合法性
				Logger.warn("当前中奖记录无法转卖（"+awardRecDollId+"）");
				getHelper().returnError(-50, "当前中奖记录无法转卖");
			}
			baseCouponId = awardRecordDoll.getBaseCouponId();
			orderId = awardRecordDoll.getZhifuOrderId();
			sourceType = AwardSourceType.DOLL;
		}else{// 创建订单、领奖记录（限量奖品领取）
			if(award.getScope() == 2){
				//夹娃娃的奖品不允许兑换
				getHelper().returnError(-150, "当前奖品不允许兑换");
			}
			orderId = OrderService.createOrder(award.getName(), award.getHappyBean(), 0, OrderType.CONSUME, 
					MemberLogOpType.EXCHANGE_PRIZE, 0, "", 0, uid, 0, "", "限量领奖", award.getName(), orderId);
			if(Strings.isNullOrEmpty(orderId)){
				Logger.error("用户%s领奖("+awardId+")创建订单失败，已创建了商品（无法回滚）", uid);
				getHelper().returnError(-30, "创建订单失败");
			}
			int applySourceType = 1;// TODO 下个版本修改到CouponService中做处理（开心大厅代金券来源转换成基础的来源，值不一致需转换）
			if(sourceType.getType() == AwardSourceType.AWARD.getType()){
				applySourceType = 2;
			}
			baseCouponId = CouponService.apply(uid+"", applySourceType, orderId, awardId);// 先申请，成功后再创建订单（防止创建订单后申请失败）
			if(Strings.isNullOrEmpty(baseCouponId)){
				Logger.error("用户%s领奖("+awardId+")同步代金券失败", uid);
				getHelper().returnError(-35, "同步代金券失败");
			}
		}
//		if(awardRecordDoll != null && Strings.isNullOrEmpty(awardRecordDoll.getBaseCouponId())){// 更新夹娃娃代金券申请记录
//			boolean isSuccess = AwardRecordDollService.update(orderId, baseCouponId);
//			if(!isSuccess){
//				Logger.error("【%s】更新夹娃娃奖品代金券ID失败", orderId);
//				getHelper().returnError(-38, "更新记录失败");
//			}
//		}
//		
		// 调用游戏商品服务创建优惠券商品
		boolean publicSuccess = true;
		String errorMsg = CouponService.resell(baseCouponId, sellPrice,clientIp);
		if(!Strings.isNullOrEmpty(errorMsg)){
			Logger.error("调用转售接口异常（"+errorMsg+"）.用户ID:%s,奖品ID:%d,orderId=%s", uid, awardId, orderId);
			if(awardRecDollId != null){// 如果是夹娃娃的则直接提示错误（限量代金券不能返回，需要继续执行，因为已经申请成功代金券了）
				getHelper().returnError(-80, errorMsg);
			}
			publicSuccess = false;
		}
		
		if(awardRecDollId != null){// 更新夹娃娃领奖记录
			String lockKey = "Doll-Award-" + awardRecDollId;
			if (!lock.tryCacheLock(lockKey, "", "5s")) {
				Logger.error("表单提交频繁.UID:%s, GrapId:%s", uid, awardRecDollId);
				getHelper().returnError(-40, "重复领奖");
			}
			boolean flag = AwardRecordDollService.update(awardRecDollId, AuditStatus.RESELL.getStatus(), AuditStatus.RESELL.getDesc(), DollAwardStatus.NOT_DRAW.getValue());
			if(!flag){
				getHelper().returnError(-60, "无法更新夹娃娃中奖记录");
			}
		}
		
		try{// 插入夹娃娃领奖记录并减少库存
			Map result = AwardService.createAwardRecord(awardId, orderId, uid, 
					sourceType.getType(), sourceType.getDesc(), award.getName(), AuditStatus.RESELL.getStatus(), baseCouponId);
			if (!result.get("result").toString().equalsIgnoreCase("true")) {
				Logger.error("创建领奖记录异常.用户ID:%s,奖品ID:%d,orderId=%s", uid, awardId, orderId);
				throw new Exception("创建领奖记录异常");
			}
		}catch(Exception e){
			getHelper().returnError(-70, "创建领奖记录异常");
		}
		if(!publicSuccess){
			getHelper().returnSucc(5);
		}
		Logger.info(uid+"出售优惠券成功：awardId="+awardId+",awardRecDollId="+awardRecDollId+",sellPrice="+sellPrice);
		getHelper().returnSucc(1);
	}
	/**
	 * 提交限量代金券信息
	 * @param uid
	 * @param orderId
	 * @param awardId
	 * @param sourceType
	 * */
	public static void applyCoupon(){
		Map params = getDTO(Map.class);
		if(params.get("uid") == null || params.get("awardId") == null || 
				params.get("orderId") == null || params.get("sourceType") == null){
			getHelper().returnError(-10, "参数有误！");
		}
		String uid = params.get("uid").toString();
		int awardId = Integer.parseInt(params.get("awardId").toString());
		String orderId = params.get("orderId").toString();
		Integer sourceType = Integer.parseInt(params.get("sourceType").toString());
		String couponId = CouponService.apply(uid, sourceType, orderId, awardId);
		if(StringUtils.isBlank(couponId)){
			Logger.error("提交代金券失败,调用提交代金券接口异常.用户ID:%s,奖品ID:%d,orderId=%s,来源类型 =%s", uid, awardId, orderId,sourceType);
			getHelper().returnError(-80, "提交代金券失败");
		}
		logger.info(uid+"提交代金券成功：awardId="+awardId+",orderId="+orderId+",sourceType="+sourceType+",couponId="+couponId);
		getHelper().returnSucc(couponId);	
	}
	/**
	 * 获取夹娃娃记录信息
	 * @param grapId
	 * */
	public static void getBaseCouponId(){
		Map params = getDTO(Map.class);
		if(params.get("grapId") == null){
			getHelper().returnError(-10, "参数有误！");
		}
		Integer grapId = Integer.parseInt(params.get("grapId").toString());
		AwardRecordDollDDL awardRecordDollDDL = AwardRecordDollService.getById(grapId);
		if(null == awardRecordDollDDL){
			Logger.error("获取夹娃娃记录信息,grapId =%s",grapId);
			getHelper().returnError(-80, "获取夹娃娃记录信息失败");
		}
		getHelper().returnSucc(awardRecordDollDDL.getBaseCouponId());	
	}
	/**
	 * 申请代金券使用(外部使用)
	 */
	public static void useCoupon() {
		Map params = getDTO(Map.class);
		String baseCouponId = params.get("baseCouponId").toString();
		String gameId = params.get("gameId").toString();
		String gameName = params.get("gameName").toString();
		String gameUid = params.get("gameUid").toString();
		
		Logger.info("申请代金券使用：baseCouponId="+baseCouponId+",gameId="+gameId+",gameName="+gameName+",gameUid="+gameUid);
		Record awardRec = AwardRecordService.getAwardRecordByCouponId(baseCouponId);
		Record awardRecDoll = AwardRecordDollService.getByBaseCouponId(baseCouponId);
		
		if(awardRec == null && awardRecDoll == null){
			Logger.error("申请代金券使用失败：无法获取代金券记录，baseCouponId="+baseCouponId);
			getHelper().returnError(-10, "无法获取代金券记录");
		}
		String message = CouponService.audit(baseCouponId, 2, "申请代金券使用");
		if(!"".equals(message)){
			Logger.error("申请代金券使用失败：同步代金券状态失败，baseCouponId="+baseCouponId);
			getHelper().returnError(-40, message);
		}
		if(awardRecDoll != null && awardRec == null){// 夹娃娃未添加中奖记录，先添加
			try{// 插入夹娃娃领奖记录并减少库存
				AwardDDL award = AwardService.getAwardById(Integer.parseInt(awardRecDoll.get("award_id").toString()));
				Map result = AwardService.createAwardRecord(award.getId(), awardRecDoll.getStr("zhifu_order_id"), Integer.parseInt(awardRecDoll.get("uid").toString()), 
						AwardSourceType.DOLL.getType(), AwardSourceType.DOLL.getDesc(), award.getName(), AuditStatus.RESELL.getStatus(), baseCouponId);
				if (!result.get("result").toString().equalsIgnoreCase("true")) {
					Logger.error("申请代金券使用失败：优惠券ID="+baseCouponId);
					throw new Exception("创建领奖记录异常");
				}
				awardRec = AwardRecordService.getAwardRecordByCouponId(baseCouponId);
			}catch(Exception e){
				Logger.error("申请代金券使用失败：优惠券ID="+baseCouponId+",e="+e);
				getHelper().returnError(-70, "创建领奖记录异常");
			}
		}
		if(awardRecDoll != null){// 夹娃娃奖品记录
			boolean flagDoll = AwardRecordDollService.update(awardRecDoll.getInt("id"), gameId, gameName, gameUid);// 更新夹娃娃的奖品领取记录
			if(!flagDoll){
				Logger.error("申请代金券使用失败：无法更新夹娃娃领奖记录数据，baseCouponId="+baseCouponId);
				getHelper().returnError(-20, "无法更新夹娃娃领奖记录数据");
			}
		}
		boolean flagAward = AwardService.updateAwardRecord(awardRec.getInt("id"), gameId, gameName, gameUid);// 更新奖品领取数据
		if(!flagAward){
			Logger.error("申请代金券使用失败：无法更新领奖记录数据，baseCouponId="+baseCouponId);
			getHelper().returnError(-30, "无法更新领奖记录数据");
		}
		getHelper().returnSucc("success");
	}
	
	/**
	 * app转卖操作
	 * 1.大厅已初始化过代金券数据（限量代金券、已转卖过的夹娃娃代金券）
	 * 		直接修改领奖记录的状态
	 * 2.大厅未初始化过数据（夹娃娃中奖代金券【未使用过、未转卖过】）
	 * 		需要先写入夹娃娃领奖记录，再修改夹娃娃中奖记录状态
	 */
	public static void appSell(){
		Map params = getDTO(Map.class);
		if(params.get("uid") == null || params.get("sellPrice") == null || params.get("baseCouponId") == null){
			getHelper().returnError(-10, "参数有误！");
		}
		String uid = params.get("uid").toString();
		double sellPrice = Double.parseDouble(params.get("sellPrice").toString());
		String baseCouponId = params.get("baseCouponId").toString();
		
		Logger.info("APP转卖代金券：baseCouponId="+baseCouponId+",uid="+uid+",sellPrice="+sellPrice);
		Record awardRec = AwardRecordService.getAwardRecordByCouponId(baseCouponId);// 中奖记录
		Record awardRecDoll = AwardRecordDollService.getByBaseCouponId(baseCouponId);// 夹娃娃中奖记录
		if(awardRec == null && awardRecDoll == null){
			Logger.warn("该代金券无法找到对应的领奖记录:baseCouponId="+baseCouponId+",uid="+uid+",sellPrice="+sellPrice);
			getHelper().returnError(-20, "该代金券无法找到对应的领奖记录！");
		}
		String myUid = "-1";
		if(awardRec != null){
			myUid = awardRec.get("uid").toString();
		}else{
			myUid = awardRecDoll.get("uid").toString();;
		}
		if(!uid.equals(myUid)){
			Logger.warn("该代金券与中奖用户不一致:baseCouponId="+baseCouponId+",uid="+uid+",sellPrice="+sellPrice);
			getHelper().returnError(-30, "该代金券与中奖用户不一致！");
		}
		if(awardRec != null){// 情况1：修改领奖记录的状态
			AwardRecordService.updateStatus(awardRec.getInt("id"), AuditStatus.RESELL.getStatus(), "App已转卖代金券");
		}else{// 情况2：写入夹娃娃领奖记录
			int awardId = awardRecDoll.getInt("award_id");
			AwardDDL award = null;
			try{// 校验奖品
				award = AwardService.getAwardById(awardId);
				if(award == null){
					Logger.warn("无法获取奖品信息（"+awardId+"）");
					throw new Exception("无法获取奖品信息");
				}
			}catch(Exception e){
				getHelper().returnError(-40, "无法获取优惠券");
			}
			Map result = null;
			try {
				result = AwardService.createAwardRecord(awardId, awardRecDoll.get("zhifu_order_id").toString(), Integer.parseInt(uid), 
						AwardSourceType.DOLL.getType(), AwardSourceType.DOLL.getDesc(), award.getName(), AuditStatus.RESELL.getStatus(), baseCouponId);
			} catch (NumberFormatException e) {
				e.printStackTrace();
			} catch (BusinessException e) {
				e.printStackTrace();
			}
			if (result == null || !result.get("result").toString().equalsIgnoreCase("true")) {
				Logger.error("转卖代金券失败（无法创建领奖记录），用户ID:%s,奖品ID:%d,orderId=%s", uid, awardId, awardRecDoll.get("zhifu_order_id").toString());
				getHelper().returnError(-50, "转卖代金券失败（无法创建领奖记录）");
			}
		}
		int awardRecDollId = awardRecDoll.getInt("id");
		if(awardRecDoll != null){// 情况2：修改夹娃娃中奖记录状态
			String lockKey = "Doll-Award-" + awardRecDollId;
			if (!lock.tryCacheLock(lockKey, "", "5s")) {
				Logger.error("表单提交频繁.UID:%s, GrapId:%s", uid, awardRecDollId);
				getHelper().returnError(-40, "重复领奖");
			}
			boolean flag = AwardRecordDollService.update(awardRecDollId, AuditStatus.RESELL.getStatus(), "App已转卖代金券", DollAwardStatus.NOT_DRAW.getValue());
			if(!flag){
				getHelper().returnError(-60, "无法更新夹娃娃中奖记录");
			}
		}
		Logger.info("APP转卖代金券状态同步成功成功");
		getHelper().returnSucc("已成功同步状态");
	}
}
