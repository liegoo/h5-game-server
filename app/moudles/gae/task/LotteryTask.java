package moudles.gae.task;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import jws.Logger;
import jws.dal.Dal;
import jws.module.constants.gae.GaeAllConstans;
import jws.module.constants.member.MemberLogOpType;
import moudles.gae.assist.HappyBeans;
import moudles.gae.assist.Lottery;
import moudles.gae.assist.UnfitResultException;
import moudles.gae.ddl.GaeDrawDDL;
import moudles.gae.ddl.GaeDrawRecordDDL;
import moudles.gae.ddl.GaeDrawRecordTempDDL;
import moudles.gae.ddl.GaeUserPlDDL;
import moudles.gae.service.child.GaeDrawOrderService;
import moudles.gae.service.child.GaeDrawRecordService;
import moudles.gae.service.child.GaeDrawService;
import moudles.gae.service.child.GaeUserPlService;
import moudles.member.service.MemberService;

import com.google.gson.Gson;

import constants.SelfGame;

/**
 * 开奖程序
 * 
 * @author caixb
 *
 */
public class LotteryTask implements Runnable{

	private String drawId;
	
	public LotteryTask(String drawId){
		this.drawId = drawId;
	}
	
	@Override
	public void run() {
		GaeDrawDDL draw = GaeDrawService.getDraw(drawId);
		if(draw == null){
			Logger.debug("运行开奖程序，开奖失败,开奖期数不存在; drawId:%s", drawId);
			return;
		}
		if(draw.getStatus() == GaeAllConstans.DRAW_STATUS_OVER){
			GaeDrawService.createNewDraw(draw.getRoomId(), drawId);
			return;
		}
		List<GaeDrawRecordTempDDL> recordList = GaeDrawRecordService.getDrawPlayers(drawId);
		if(recordList == null || recordList.size() < 1){
			Logger.debug("运行开奖程序，开奖失败,没有参与人; drawId:%s;status:%s", drawId, draw.getStatus());
			return;
		}
		int roomDrawRatio = draw.getRoomDrawRatio();
		int planTotalBean = draw.getRoomTotalBeans(); 
		int planHeadCount = draw.getHeadCount(); //计划参与人数
		int thisHeadCount = recordList.size(); //实际参与人数
		
		if(thisHeadCount < planHeadCount){
			Logger.debug("运行开奖程序，开奖失败,参与人数不够; drawId:%s;status:%s;planHeadCount：%s;thisHeadCount:%s", drawId, draw.getStatus(), planHeadCount, thisHeadCount);
			return;
		}
		
		/**
		 * 1、校验
		 * 2、开奖
		 * 3、转移记录
		 * 4、触发下一期开始
		 * 
		 * 5、奖励发放，包括预先支付豆豆的人返还豆豆
		 * 
		 */
		
		Map<Integer, Integer> hitDatail = gotoLottery(draw, recordList);

		if(hitDatail != null){
			//触发下一场开始
			GaeDrawService.createNewDraw(draw.getRoomId(), drawId);
			
			//善后-----发奖、记录中奖流水
			award(draw.getRoomId(), hitDatail, planTotalBean, roomDrawRatio, recordList);
		}
	} 
	
	public Map<Integer, Integer> gotoLottery(GaeDrawDDL draw, List<GaeDrawRecordTempDDL> recordList){
		Dal.beginTransaction(); 
		try {
			String roomId = draw.getRoomId();
			String roomDrawSettings = draw.getRoomDrawSettings();
			
			int planHeadCount = draw.getHeadCount(); //计划参与人数
			int planTotalBeans = draw.getRoomTotalBeans(); //当前房间豆豆总额
			
			List<Integer> uidList = new ArrayList<Integer>();
			for (GaeDrawRecordTempDDL gdr : recordList) {
				uidList.add(gdr.getUserId());
			}
			Integer[] uids = new Integer[]{uidList.size()};
			uids = uidList.toArray(uids);
			
			//更新当前期数为已结束 先更新，防止出现另外的线程并行开奖
			boolean drawOverResult = GaeDrawService.drawOver(drawId);
			if(!drawOverResult){
				throw new UnfitResultException("更新当前进行的抽奖活动结束失败");
			}
			
			//获取用户的中奖盈亏历史【按由大到小排序 】
			Map<Integer, Integer> userPl = GaeUserPlService.getUserPlDetail(uids, roomId);
			
			//开奖计算
			Date date = new Date();
			Logger.debug("运行开奖程序,开始计算开奖结果，房间Id：%s;期数Id：%s;开始时间:%s;roomDrawSettings:%s;planTotalBeans:%s;planHeadCount:%s;userPl:%s", roomId, draw.getId(),date.getTime(), roomDrawSettings, planTotalBeans, planHeadCount, userPl);
			Lottery lottery = new Lottery(drawId, roomDrawSettings, planTotalBeans, planHeadCount, userPl);
			Map<Integer, Integer> hitDatail = lottery.gotoLottery();
			Logger.debug("运行开奖程序,结束计算开奖结果，开奖计算耗时:%s;计算结果:%s", new Date().getTime() - date.getTime(), new Gson().toJson(hitDatail));
			
			//开奖成功后    善后！！
			date = new Date();
			Logger.debug("运行开奖程序,保存开奖结果，开始时间:%s;保存数量：%s：中奖数量：%s", date.getTime(), recordList.size(), hitDatail.size());
			for (GaeDrawRecordTempDDL gdr : recordList) {
				GaeDrawRecordDDL gaeDrawRecordDDL = new GaeDrawRecordDDL();
				int uid = gdr.getUserId();
				int hitBean = hitDatail.get(uid);
				
				Collection<Integer> coll = hitDatail.values();
				int minHitBean = Collections.min(coll);
				gaeDrawRecordDDL.setDeductBeans(0); //默认不需要扣除豆豆
				gaeDrawRecordDDL.setIsWorst(0); //默认不是最低中奖者
				if(hitBean == minHitBean){
					gaeDrawRecordDDL.setDeductBeans(gdr.getDeductBeans()); //需要扣除豆豆
					gaeDrawRecordDDL.setIsWorst(1); //是最低中奖者
				}
				gaeDrawRecordDDL.setDrawId(drawId);
				gaeDrawRecordDDL.setHitBeans(hitBean);
				gaeDrawRecordDDL.setHitTime(gdr.getHitTime()); //这里取自用户参与进来的时间
				gaeDrawRecordDDL.setRoomId(roomId);
				gaeDrawRecordDDL.setRoomName(gdr.getRoomName());
				
				gaeDrawRecordDDL.setUserAvatar(gdr.getUserAvatar());
				gaeDrawRecordDDL.setUserId(uid);
				gaeDrawRecordDDL.setUserName(gdr.getUserName());
				gaeDrawRecordDDL.setUserIp(gdr.getUserIp());
				gaeDrawRecordDDL.setUserZone(gdr.getUserZone());
				
				gaeDrawRecordDDL.setIsRobot(gdr.getIsRobot());
				
				boolean b = GaeDrawRecordService.insertDrawRecord(gdr.getId(), gaeDrawRecordDDL);
				if(!b){
					throw new UnfitResultException("开奖成功，保存中奖记录失败, record:" + gaeDrawRecordDDL.toString());
				}
			}
			Logger.debug("运行开奖程序,保存开奖结果完成，运行时间：%s", new Date().getTime() - date.getTime());
			Dal.setTransactionSuccessful();
			Logger.debug("运行开奖程序,保存开奖结果完成，房间：%s,开奖期数：%s,中奖明细：%s", roomId, draw.getId(), hitDatail);
			return hitDatail;
		} catch (UnfitResultException ue) {
			Logger.info(ue.getMessage() + "期数：" + drawId);
		} catch (Exception e) {
			Logger.info("用户参与抢红包异常，期数：" + drawId + ":Exception:" + e);
		}finally{
			Dal.endTransaction();
		}
		return null;
	}
	
	/**
	 * 发奖
	 * 
	 * @param hitDatail
	 * @param sill
	 */
	public void award(String roomId, Map<Integer, Integer> hitDatail, int sill, int profitRatio, List<GaeDrawRecordTempDDL> recordList){
		if(hitDatail == null || hitDatail.size() < 1){
			return ;
		}
		Collection<Integer> coll = hitDatail.values();
		int minHitBean = Collections.min(coll);
		
		//modify time : 2016.12.21 机器人参与不返还幸运豆
		//List<Integer> roobutUids = new ArrayList<Integer>();
		Map<Integer, GaeDrawRecordTempDDL> record = new HashMap<Integer, GaeDrawRecordTempDDL>();
		for (GaeDrawRecordTempDDL gdr : recordList) {
			record.put(gdr.getUserId(), gdr);
//			if(gdr.getIsRobot() == 1){
//				roobutUids.add(gdr.getUserId());
//			}
		}
		
		for (int uid : hitDatail.keySet()) {
			GaeDrawRecordTempDDL gdr = record.get(uid);
//			if(gdr != null && gdr.getIsRobot() == 1){
//				continue;
//			}
			String orderId = gdr == null ? null : gdr.getOrderId();
			//当前命中的数量
			int hitBean = hitDatail.get(uid); 
			//系统化获利
			int profit = HappyBeans.profit(sill, profitRatio);
			//当前场参与前扣除数量
			int deductBeans = sill + profit;
			//实际需要返还用户幸运豆数量
			int factBean = 0; 
			//当前用户需给抽水数量
			int thisProfit = 0;
			//用户盈亏
			int userPl = 0;
			int isWorst = 0;
			if(hitBean == minHitBean){
				isWorst = 1;
				factBean = hitBean;
				thisProfit = profit;
				userPl = hitBean - deductBeans;
				Logger.info("开奖成功，用户：【%s】;期数id:【%s】;是否最低命中者：【是】;实际返还用户幸运豆   --> 【实际命中数量:%s】", uid, drawId, factBean);
			}else{
				factBean = deductBeans + hitBean;
				userPl = hitBean;
				Logger.info("开奖成功，用户：【%s】;期数id:【%s】;是否最低命中者：【否】;实际返还用户幸运豆   --> 【预先扣除数量:%s】 + 【实际命中数量:%s】  = %s;", uid, drawId, deductBeans, hitBean, factBean);
			}
			//发奖
			Map<String, String> params = new HashMap<String, String>();
			params.put("remark", "抢红包中奖");
			params.put("billId", drawId);
			params.put("gameId", String.valueOf(SelfGame.GAME_ENVELOPPE.getGameId()));
			String lockKey = uid + "-" + drawId + "-" + String.valueOf(SelfGame.GAME_ENVELOPPE.getGameId());
			boolean b = false;
			if(gdr != null && gdr.getIsRobot() == 1){
				b = true;
			}else{
				b = MemberService.addBean(uid, factBean, MemberLogOpType.GAME_AWARD.getType(), params,lockKey);
			}
			if(b){
				GaeUserPlDDL pl = new GaeUserPlDDL();
				pl.setCreateTime(System.currentTimeMillis());
				pl.setLoss(deductBeans);
				pl.setProfit(hitBean);
				pl.setRoomId(roomId);
				pl.setTotal(userPl);
				pl.setUserId(uid);
				pl.setSysProfit(thisProfit);
				boolean insertUserPl = GaeUserPlService.insertUserPl(pl);
				if(!insertUserPl){
					Logger.warn("开奖成功，返还用户幸运豆成功,记录用户流水失败,用户：%s;期数id:%s;数量:%s", uid, drawId, factBean);
				}
				//modify ==> 2017.01.13(订单导流需求)
				if(StringUtils.isNotBlank(orderId) && gdr.getIsRobot() != 1){
					boolean updateDrawOrder = GaeDrawOrderService.updateDrawOrder(uid, orderId, drawId, 1, isWorst, hitBean, new Date().getTime(), "开奖成功，账户新增:" + factBean + ",orderId:" + orderId);
					if(!updateDrawOrder){
						Logger.warn("开奖成功，更新附属订单信息失败,用户：%s;期数id:%s;数量:%s", uid, drawId, factBean);
					}
				}
			}else{
				Logger.warn("开奖成功，返还用户幸运豆失败,用户：%s;期数id:%s;数量:%s", uid, drawId, factBean);
			}
			
		}
	}
}
