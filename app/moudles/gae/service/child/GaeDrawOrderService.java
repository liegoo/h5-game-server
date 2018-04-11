package moudles.gae.service.child;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

import jws.Jws;
import jws.Logger;
import jws.dal.Dal;
import jws.dal.sqlbuilder.Condition;
import jws.module.constants.member.MemberLogOpType;
import jws.modules.client.MD5;
import moudles.gae.assist.UnfitResultException;
import moudles.gae.ddl.GaeDrawOrderDDL;
import moudles.member.ddl.MemberDDL;
import moudles.member.service.MemberService;

import org.apache.commons.lang.StringUtils;

import utils.DaoUtil;
import utils.JsonToMap;

import com.google.gson.JsonObject;

import constants.SelfGame;
import externals.account.AccountCenterService;


/**
 * 抢红包 -- 订单附属
 * 
 * @author caixb
 *
 */
public class GaeDrawOrderService{

	/**
	 * 
	 * 获取订单信息
	 * 
	 * @param uid 用户uid
	 * @param orderId 订单id
	 * @return
	 */
	public static GaeDrawOrderDDL getOrderInfo(int uid, String orderId){
		if(StringUtils.isBlank(orderId)){
			return null;
		}
		Condition cond = new Condition("GaeDrawOrderDDL.uid", "=", uid);
		cond.add(new Condition("GaeDrawOrderDDL.orderId", "=", orderId), "AND");
		
		List<GaeDrawOrderDDL> gaeDraws = Dal.select(DaoUtil.genAllFields(GaeDrawOrderDDL.class), cond, null, 0, -1);
		if(gaeDraws != null && gaeDraws.size() != 0){
			return gaeDraws.get(0);
		}
		return null;
	}
	
	/**
	 * 
	 * 插入参与信息
	 * 
	 * @param gdOrder
	 * @return
	 * @throws Exception
	 */
	public static boolean insertDrawOrder(GaeDrawOrderDDL gdOrder) throws Exception{
		int result = Dal.insert(gdOrder);
		if(result != 1){
			throw new UnfitResultException("插入参与用户信息失败");
		}
		return true;
	}
	
	/**
	 * 
	 * 更新参与信息
	 * 
	 * @param uid
	 * @param orderId
	 * @param roomId
	 * @param drawId
	 * @param remark
	 * @return
	 * @throws Exception
	 */
	public static boolean updateDrawOrder(int uid, String orderId, String drawId, int drawCost, String remark){
		try {
			GaeDrawOrderDDL order = new GaeDrawOrderDDL();
			order.setDrawId(drawId);
			order.setRemark(remark);
			order.setDrawCost(drawCost);
			
			Condition cond = new Condition("GaeDrawOrderDDL.uid", "=", uid);
			cond.add(new Condition("GaeDrawOrderDDL.orderId", "=", orderId), "AND");
			
			int result = Dal.update(order, "GaeDrawOrderDDL.drawId,GaeDrawOrderDDL.drawCost,GaeDrawOrderDDL.remark", cond);
			
			return (result == 1);
		} catch (Exception e) {
			Logger.error("", e);
		}
		return false;
	}
	
	/**
	 * 
	 * 更新参与信息
	 * 
	 * @param uid
	 * @param orderId
	 * @param drawId
	 * @param isLottery
	 * @param isWorst
	 * @param hitBeans
	 * @param hitTime
	 * @param remark
	 * @return
	 * @throws Exception
	 */
	public static boolean updateDrawOrder(int uid, String orderId, String drawId, int isLottery, int isWorst, int hitBeans, long hitTime, String remark){
		try {
			GaeDrawOrderDDL order = new GaeDrawOrderDDL();
			order.setDrawId(drawId);
			order.setRemark(remark);
			order.setIsLottery(isLottery);
			order.setIsWorst(isWorst);
			order.setHitTime(hitTime);
			order.setHitBeans(hitBeans);
			
			Condition cond = new Condition("GaeDrawOrderDDL.uid", "=", uid);
			cond.add(new Condition("GaeDrawOrderDDL.orderId", "=", orderId), "AND");
			
			int result = Dal.update(order, "GaeDrawOrderDDL.drawId,GaeDrawOrderDDL.remark,GaeDrawOrderDDL.isLottery,GaeDrawOrderDDL.isWorst,GaeDrawOrderDDL.hitTime,GaeDrawOrderDDL.hitBeans", cond);
			
			return (result == 1);
		
		} catch (Exception e) {
			Logger.error("", e);
		}
		return false;
	}
	
	/**
	 * 充值豆豆给用户 用来作为接下来的抽奖
	 * 
	 * @param uid uid
	 * @param orderId 订单id
	 * @param cost 成本
	 * @param factBean 充值豆豆数量
	 * @param remark 什么鬼
	 * @return
	 */
	public static boolean addBean(int uid, String orderId, double cost, int factBean, String remark, String orderFr){
		boolean result = false;
		try {
			//判断用户是否存在，不存在则创建--->ps:操蛋的逻辑，严重设计不合理
			MemberDDL member = MemberService.getMemberByUid(uid);
			if(member == null){
				JsonObject resultJson = AccountCenterService.getUserInfoByUid(uid);
				Logger.debug("查询用户中用户信息，uid：%s, orderId: %s, factBean: %s, result：%s", uid, orderId, factBean, resultJson);
				if (resultJson != null && resultJson.has("code") && resultJson.get("code").getAsInt() == 0 && resultJson.has("data") && resultJson.get("data") != null) {
					String dataStr = resultJson.get("data").getAsString();
					JsonObject dataObj = JsonToMap.parseJson(dataStr);
					String nickName = dataObj.has("nickName") ? dataObj.get("nickName").getAsString() : "";
					String mobile = dataObj.has("mobile") ? dataObj.get("mobile").getAsString() : "";
					Map params = new HashMap();
					params.put("nickName", nickName);
					params.put("mobile", mobile);
					String password = MD5.encode(UUID.randomUUID().toString() + new Random().nextInt(1000));
					MemberDDL md = MemberService.addMember(uid, password, "gae-sys-auto", params);
					if (md == null){
						Logger.debug("用户不存在，创建新用户失败，uid：%s, orderId: %s, factBean: %s, result：%s", uid, orderId, factBean, resultJson);
						return result;
					}
				}
			}
			
			
			int ratio = Integer.valueOf(Jws.configuration.getProperty("rmb_rate", "1000")); //充值比率
			BigDecimal b1 = new BigDecimal(Double.toString(ratio));  
			BigDecimal b2 = new BigDecimal(Double.toString(cost));
			int realityBean = b1.multiply(b2).intValue(); //实际需要充值幸运豆
			int priceSpread = factBean - realityBean; //参与当前场次活动需要补差额
			
			Logger.info("用户幸运豆更新－>uid：%s, orderId: %s, realityBean: %s, priceSpread: %s", uid, orderId, realityBean, priceSpread);
			
			//充值豆豆
			Map<String, String> payParams = new HashMap<String, String>();
			payParams.put("remark", "充值-" + remark);
			payParams.put("billId", orderId);
			payParams.put("gameId", String.valueOf(SelfGame.GAME_ENVELOPPE.getGameId()));
			payParams.put("channel", orderFr);
			String lockKey = uid + "-" + orderId + "-" + String.valueOf(SelfGame.GAME_ENVELOPPE.getGameId());
			result = MemberService.addBean(uid, realityBean, MemberLogOpType.RECHARGE.getType(), payParams, lockKey);
			if(!result){
				Logger.debug("充值辛运豆失败，uid：%s, orderId: %s, bean: %s", uid, orderId, realityBean);
			}
			
			//赠送豆豆
			if(priceSpread > 0){
				Map<String, String> giveParams = new HashMap<String, String>();
				giveParams.put("remark", "赠送-" + remark);
				giveParams.put("billId", orderId);
				giveParams.put("gameId", String.valueOf(SelfGame.GAME_ENVELOPPE.getGameId()));
				giveParams.put("channel", orderFr);
				result = MemberService.addBean(uid, priceSpread, MemberLogOpType.PRESENTED.getType(), giveParams,"");
				if(!result){
					Logger.debug("赠送辛运豆失败，uid：%s, orderId: %s, bean: %s", uid, orderId, priceSpread);
				}
			}
			
			
		} catch (Exception e) {
			Logger.error("", e);
		}
		return result;
	}
	
}
