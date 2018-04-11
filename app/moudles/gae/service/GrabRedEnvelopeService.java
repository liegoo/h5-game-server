package moudles.gae.service;
import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang.StringUtils;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;

import constants.GlobalConstants;
import constants.SelfGame;
import jws.Jws;
import jws.Logger;
import jws.dal.Dal;
import jws.dal.common.SqlParam;
import jws.module.constants.award.AwardScope;
import jws.module.constants.gae.GaeAllConstans;
import jws.module.response.age.GaeAwarTopDto;
import jws.module.response.age.GaeDrawDetailDto;
import jws.module.response.age.GaeDrawDto;
import jws.module.response.age.GaeDrawRecordDto;
import jws.module.response.age.GaeRecordDto;
import jws.module.response.age.GaeRoomDto;
import moudles.gae.assist.HappyBeans;
import moudles.gae.assist.LotteryTaskThreadPool;
import moudles.gae.assist.UnfitResultException;
import moudles.gae.ddl.GaeDrawDDL;
import moudles.gae.ddl.GaeDrawOrderDDL;
import moudles.gae.ddl.GaeDrawRecordDDL;
import moudles.gae.ddl.GaeDrawRecordTempDDL;
import moudles.gae.ddl.GaeDrawTempDDL;
import moudles.gae.ddl.GaePrizeTopDDL;
import moudles.gae.ddl.GaeRoomDDL;
import moudles.gae.service.child.GaeDrawOrderService;
import moudles.gae.service.child.GaeDrawRecordService;
import moudles.gae.service.child.GaeDrawService;
import moudles.gae.service.child.GaePrizeTopService;
import moudles.gae.service.child.GaeRoomService;
import moudles.gae.task.LotteryTask;
import moudles.member.service.MemberService;
import moudles.task.service.GameTaskService;

/**
 * xxoo
 * 
 * @author caixb
 *
 */
public class GrabRedEnvelopeService{

	/**
	 * 获取订单信息
	 * 
	 * @param uid
	 * @param orderId
	 * @return
	 */
	public static JsonObject getOrderInfo(int uid, String orderId){
		Logger.debug("外部获取订单信息，orderId:" + orderId + ",uid:" + uid);
		GaeDrawOrderDDL order = GaeDrawOrderService.getOrderInfo(uid, orderId);
		if(order != null){
			return returnResult(order.getDrawId(), order.getRoomId(), order.getDrawCost(), order.getHitBeans(), order.getIsLottery(), order.getIsWorst(), order.getTitle());
		}
		return null;
	}
	
	/**
	 * 抢红包（外部订单特用）
	 * 
	 * @param uid	用户uid
	 * @param orderId	订单id
	 * @param title 标题
	 * @param orderPrice	订单金额（特指附属金额）
	 * @param orderFr	订单来源
	 * @param roomId	房间id
	 * @param userName	用户名
	 * @param userAvatar	用户头像
	 * @param ip	参与ip
	 * @param userZone	用户物理地址
	 * @return
	 */
	public static GaeRecordDto grabByOrder(int uid, String orderId, String title, double orderPrice, String orderFr, String roomId, String userName, String userAvatar, String ip, String userZone){
		Logger.debug("外部订单参与抢红包，orderId:" + orderId + ",uid:" + uid + ",orderPrice:" + orderPrice + ",orderFr:" + orderFr + ",roomId:" + roomId);
		GaeRecordDto record = new GaeRecordDto(GaeRecordDto.ERROR, "抢红包失败");
		try {
			//1、先下单，作为后续依据
			GaeDrawOrderDDL gdOrder = new GaeDrawOrderDDL();
			gdOrder.setOrderId(orderId);
			gdOrder.setOrderPrice(orderPrice);
			gdOrder.setOrderFr(orderFr);
			gdOrder.setUid(uid);
			gdOrder.setIp(ip);
			gdOrder.setRoomId(roomId);
			gdOrder.setCreateTime(new Date().getTime());
			gdOrder.setIsLottery(0);
			gdOrder.setIsWorst(0);
			gdOrder.setRemark("初始下单.");
			gdOrder.setTitle(title);
			boolean result = GaeDrawOrderService.insertDrawOrder(gdOrder);
			if(!result){
				Logger.info("外部订单参与抢红包失败，orderId:" + orderId + ",uid:" + uid + ",orderPrice:" + orderPrice + ",orderFr:" + orderFr + ",roomId:" + roomId);
				return record;
			}
			
			//2、get 当前正在进行的活动
			GaeDrawTempDDL drawTemp = GaeDrawService.getActiveDraw(roomId);
			if(drawTemp == null){
				Logger.info("外部订单参与抢红包失败,当前房间没有正在进行的开奖活动，orderId:" + orderId + ",uid:" + uid + ",orderPrice:" + orderPrice + ",orderFr:" + orderFr + ",roomId:" + roomId);
				return record;
			}
					
			//3、充钱
			GaeRoomDDL room = GaeRoomService.getRoom(roomId);
			int thisWantBeans = room.getTotalBeans(); //当前进行的抢红包需要的豆子
			int thisDrawRatio = room.getDrawRatio();  //当前进行的抢红包需要抽水比例
			int thisDeductBeans = HappyBeans.beansCount(thisWantBeans, thisDrawRatio);
			boolean addBeanResult = GaeDrawOrderService.addBean(uid, orderId, orderPrice, thisDeductBeans, title, orderFr);
			if(!addBeanResult){
				Logger.info("充值失败，uid：%s, orderId: %s, factBean: %s", uid, orderId, addBeanResult);
				return record;
			}
			
			//4、抢
			int retry = 3;
			String drawId = drawTemp.getDrawId();
			do {
				record = gotoGrab(uid, 0, drawId, userName, userAvatar, ip, userZone, orderId, orderFr);
				Logger.debug("外部订单第【"+ retry +"】次参与抢红包，uid：%s, orderId: %s, roomId: %s, drawId：%s, result:%s", uid, orderId, roomId, drawId, record.getCode() + "->" + record.getMsg());
				if(record.getCode() == 999){
					drawTemp = GaeDrawService.getActiveDraw(roomId);
					if(drawTemp == null){
						Logger.info("外部订单参与抢红包失败,当前房间没有正在进行的开奖活动，uid：%s, orderId: %s, roomId: %s", uid, orderId, roomId);
						retry = 0;
						break;
					}
					drawId = drawTemp.getDrawId();
					retry--;
				}else{
					retry = 0;
					break;
				}
			} while (retry > 0);
			
			//参与任务
			if(record.getCode() == GaeRecordDto.OK){
				GameTaskService.doTask(uid, SelfGame.GAME_ENVELOPPE.getGameId(), Long.parseLong(roomId), thisWantBeans, false);
			}
			
			//触发开奖程序，检查是否可以开奖
			if(record.getCode() == GaeRecordDto.OK || record.getCode() == 999){
				if(record.getCode() == GaeRecordDto.OK){
					boolean b = GaeDrawOrderService.updateDrawOrder(uid, orderId, drawId, thisDeductBeans, "参与成功");
					if(!b){
						Logger.info("外部订单参与抢红包成功,更新附属订单失败，uid：%s, orderId: %s, roomId: %s, drawId:%s", uid, orderId, roomId, drawId);
					}
				}
				
				LotteryTask lotteryTask = new LotteryTask(drawId);
				LotteryTaskThreadPool.sumbit(lotteryTask);
				
				record.setData(returnResult(drawId, roomId, thisDeductBeans, 0, 0, 0, title));
			}

		} catch (Exception e) {
			Logger.error("", e);
		}
		return record;
	}
	
	/**
	 * 抢红包（用户）
	 * 
	 * @param uid 用户id
	 * @param drawId 参与期数id
	 * @param userName 用户名
	 * @param userAvatar 用户头像
	 * @param ip　ip
	 * @param userZone 地址
	 * @return
	 */
	public static GaeRecordDto grab(int uid, String drawId, String userName, String userAvatar, String ip, String userZone){
		return grab(uid, 0, drawId, userName, userAvatar, ip, userZone);
	}
	
	/**
	 * 抢红包（机器人）
	 * 
	 * @param uid 用户id
	 * @param isRobot 是否是机器人 0 不是 1是
	 * @param drawId 参与期数id
	 * @param userName 用户名
	 * @param userAvatar 用户头像
	 * @param ip　ip
	 * @param userZone 地址
	 * @return
	 */
	public static GaeRecordDto grab(int uid, int isRobot, String drawId, String userName, String userAvatar, String ip, String userZone){
		//验证活动是否过期；
		//验证是否参加过；
		//验证当前场次红包参与人数是否已满（能写入数据库表示没满）
		//验证幸运的是否足够(直接扣，成功说明足够)
		//事物管理保证扣豆与写入参与信息同步
		//触发开奖
		GaeRecordDto record = gotoGrab(uid, isRobot, drawId, userName, userAvatar, ip, userZone, null, null);
		if(record.getCode() == GaeRecordDto.OK || record.getCode() == 999){
			//触发开奖程序，检查是否可以开奖
			LotteryTask lotteryTask = new LotteryTask(drawId);
			LotteryTaskThreadPool.sumbit(lotteryTask);
		}
		
		return record;
	}
	
	/**
	 * 获取排行榜数据(排行榜)
	 * 
	 * @return
	 */
	public static List<GaeAwarTopDto> getTopList(int type, int pageNo, int pageSize){
		List<GaePrizeTopDDL> topList = GaePrizeTopService.getPrizeTop(type, pageNo, pageSize);
		if(topList == null || topList.size() < 1){
			return null;
		}
		List<GaeAwarTopDto> topListFinal = new ArrayList<GaeAwarTopDto>();
		int top = ((pageNo < 2 ? 1 : pageNo) - 1) * pageSize + 1;
		for (GaePrizeTopDDL gpt : topList) {
			GaeAwarTopDto topDto = new GaeAwarTopDto();
			topDto.setUserId(gpt.getUserId());
			topDto.setTotalBeans(gpt.getTotalBeans());
			topDto.setTopType(gpt.getTopType());
			topDto.setNickName(gpt.getUserName());
			topDto.setUserAvatar(gpt.getUserAvatar());
			topDto.setDeawCount(gpt.getDeawCount());
			topDto.setTopNumber(top);
			topListFinal.add(topDto);
			top++;
		}
		return topListFinal;
	}
	
	/**
	 * 根据用户查询参与历史记录(战绩)
	 * 
	 * @param uid 用户uid
	 * @param pageNo 当前页
	 * @param pageSize 每页数量
	 * @return
	 */
	public static List<GaeDrawRecordDto> getDrawRecord(int uid, int pageNo, int pageSize){
		try {
			List<GaeDrawRecordDDL> recordList = GaeDrawRecordService.getDrawRecord(uid, pageNo, pageSize);
			if(recordList == null || recordList.size() < 1){
				return null;
			}
			List<GaeDrawRecordDto> recordListFinal = new ArrayList<GaeDrawRecordDto>();
			for (GaeDrawRecordDDL gdr : recordList) {
				GaeDrawRecordDto record = new GaeDrawRecordDto();
				BeanUtils.copyProperties(record, gdr);
				recordListFinal.add(record);
			}
			return recordListFinal;
		} catch (Exception e) {
			Logger.error(e, "");
		}
		return null;
	}
	
	/**
	 * 获取抢红包所有进行场次的信息
	 * 具体参考：http://confluence.jugame.lo/pages/viewpage.action?pageId=5996878
	 */
	public static List<Map<String, Object>> getRooms(int uid){
		StringBuffer sql = new StringBuffer();
		sql.append("SELECT dt.room_id,dt.room_name,dt.draw_id,dt.last_draw_id,dt.head_count total_num,");
		sql.append("r.total_beans,r.draw_ratio,SUM(if(drt.user_id=?, 1, 0)) is_join,count(*) join_num,");
		sql.append("dre.id pre_draw_id,dre.is_worst,dre.hit_beans,dre.deduct_beans ");
		sql.append("FROM gae_draw_temp dt ");
		sql.append("JOIN gae_room r ON (r.`status`=1 and dt.room_id=r.id) ");
		sql.append("LEFT JOIN gae_draw_record_temp drt ON (dt.draw_id=drt.draw_id) ");
		sql.append("LEFT JOIN gae_draw_record dre ON (dt.room_id=dre.room_id and dt.last_draw_id=dre.draw_id and dre.user_id=?) ");
//		sql.append("LEFT JOIN gae_room r2 ON (dre.room_id=r2.id) ");
		sql.append("GROUP BY dt.draw_id ");
		sql.append("ORDER BY r.total_beans ");
		List<Record> rooms = Db.find(sql.toString(), uid, uid);
		
		List<Map<String, Object>> roomInfos = new ArrayList<Map<String, Object>>();
		for(Record rec : rooms){
			Map<String,Object> data = new HashMap<String, Object>();
			Object roomId = rec.get("room_id");
			Object drawId = rec.get("draw_id");
			int totalBeans = rec.getInt("total_beans");
			int drawRatio = rec.getInt("draw_ratio");
			
			data.put("roomId", roomId);
			data.put("roomName", rec.get("room_name"));
			data.put("totalBeans", totalBeans);
			int deductBeans = new BigDecimal((float)totalBeans * (float)drawRatio / 100).setScale(0, BigDecimal.ROUND_HALF_UP).intValue();
			data.put("deductBeans", totalBeans+deductBeans);
			data.put("drawId", drawId);
			data.put("isJoin", rec.get("is_join"));
			data.put("joinNum", rec.get("join_num"));
			data.put("totalNum", rec.get("total_num"));
			
			Map<String,Object> preRoomInfo = new HashMap<String, Object>();
			preRoomInfo.put("drawId", rec.get("last_draw_id"));
			preRoomInfo.put("isJoin", 0);
			if(rec.get("pre_draw_id") != null){
				preRoomInfo.put("isJoin", 1);
				preRoomInfo.put("needPay", rec.get("is_worst"));
				preRoomInfo.put("hitBeans", rec.get("hit_beans"));
				preRoomInfo.put("deductBeans", rec.get("deduct_beans"));
			}
			data.put("preRoomInfo", preRoomInfo);
			roomInfos.add(data);
		}
		return roomInfos;
	}
	
//	private static Map<String,Object> getPreRoomInfo(Object roomId, Object drawId, Object uid){
//		Map<String,Object> preRoomInfo = new HashMap<String, Object>();
//		StringBuffer sql = new StringBuffer();
//		sql.append("SELECT is_worst,hit_beans,deduct_beans FROM gae_draw_record ");
//		sql.append("WHERE room_id=? AND draw_id=? AND user_id=? limit 1");
//		Record rec = Db.findFirst(sql.toString(), roomId, drawId, uid);
//		preRoomInfo.put("drawId", drawId);
//		if(rec == null){
//			preRoomInfo.put("isJoin", 0);
//			return preRoomInfo;
//		}
//		preRoomInfo.put("isJoin", 1);
//		preRoomInfo.put("needPay", rec.get("is_worst"));
//		preRoomInfo.put("getBeans", rec.get("hit_beans"));
//		preRoomInfo.put("payBeans", rec.get("deduct_beans"));
//		return preRoomInfo;
//	}
	
	
	/**
	 * 获取开奖期数(页面心跳数据)
	 * 
	 * @param uid 用户uid
	 * @param roomId 场次id
	 */
	public static GaeDrawDto getDraw(int uid, String roomId){
		GaeDrawDto draw = new GaeDrawDto();
		//获取当前正在进行的场次
		GaeDrawTempDDL activeDraw = GaeDrawService.getActiveDraw(roomId);
		if(activeDraw == null){
			//暂时返回空
			return null;
		}
		String thisDrawId = activeDraw.getDrawId();
		String lastDrawId = activeDraw.getLastDrawId();
		
		draw.setLastDraw(getDrawDetail(uid, lastDrawId));
		draw.setThisDraw(getDrawDetail(uid, thisDrawId));
		
		return draw;
	}
	
	
	/**
	 * 获取开奖明细(点击查看明细)
	 * 
	 * @param uid 用户uid
	 * @param awarId 期数id
	 */
	public static GaeDrawDetailDto getDrawDetail(int uid, String drawId){
		if(StringUtils.isBlank(drawId)){
			return null;
		}
		GaeDrawDDL draw = GaeDrawService.getDraw(drawId);
		if(draw == null){
			return null;
		}
		int status = draw.getStatus();
		
		GaeDrawDetailDto drawFinal = new GaeDrawDetailDto();
		drawFinal.setCreateTime(new Date(draw.getCreateTime()));
		drawFinal.setHeadCount(draw.getHeadCount());
		drawFinal.setId(draw.getId());
		drawFinal.setRoomId(draw.getRoomId());
		drawFinal.setRoomName(draw.getRoomName());
		
		drawFinal.setTotalBeans(draw.getRoomTotalBeans());
		int deductBeans = HappyBeans.beansCount(draw.getRoomTotalBeans(), draw.getRoomDrawRatio());
		drawFinal.setDeductBeans(deductBeans);
		
		drawFinal.setIsLottery(isLottery(uid, drawId)? 1 : 0); //参与过
		drawFinal.setStatus(status); //状态
		
		try {
			if(status == GaeAllConstans.DRAW_STATUS_WAIT){ //当前开奖期数是否已经开奖 未开奖 查询临时表
				List<GaeDrawRecordTempDDL> drawRecordTemp = GaeDrawRecordService.getDrawPlayers(drawId);
				if(drawRecordTemp != null && drawRecordTemp.size() != 0){
					List<GaeDrawRecordDto> records = new ArrayList<GaeDrawRecordDto>();
					for (GaeDrawRecordTempDDL gdrt : drawRecordTemp) {
						GaeDrawRecordDto record = new GaeDrawRecordDto();
						BeanUtils.copyProperties(record, gdrt);
						records.add(record);
					}
					drawFinal.setRecords(records);
				}
			}else { //已开奖的  查询原表
				List<GaeDrawRecordDDL> drawRecord = GaeDrawRecordService.getDrawPlayersByHistory(drawId);
				if(drawRecord != null && drawRecord.size() != 0){
					List<GaeDrawRecordDto> records = new ArrayList<GaeDrawRecordDto>();
					for (GaeDrawRecordDDL gdr : drawRecord) {
						GaeDrawRecordDto record = new GaeDrawRecordDto();
						BeanUtils.copyProperties(record, gdr);
						records.add(record);
					}
					drawFinal.setRecords(records);
				}
			}
		} catch (Exception e) {
			Logger.error("", e);
		}
		return drawFinal;
	}
	
	private static GaeRecordDto gotoGrab(int uid, int isRobot, String drawId, String userName, String userAvatar, String ip, String userZone, String orderId, String orderFr){
		Date date = new Date();
		Logger.info("=====>start：uid:" + uid + ",time:" + date.getTime() + ",线程号：" + Thread.currentThread().getId());
		Dal.beginTransaction(); 
		Logger.info("=====>start-1：uid:" + uid + ",time:" + (System.currentTimeMillis() - date.getTime()) + ",线程号：" + Thread.currentThread().getId());
		date = new Date();
		try {
			GaeDrawDDL draw = GaeDrawService.getDraw(drawId);
			if(draw == null){
				return new GaeRecordDto(GaeRecordDto.ERROR, "抢红包游戏不存在");
			}
			if(draw.getStatus() == GaeAllConstans.DRAW_STATUS_OVER){
				return new GaeRecordDto(999, "该红包已经被抢完"); //999-->特例
			}
			GaeDrawRecordTempDDL myDraw = GaeDrawRecordService.getMyActiveDraw(uid, drawId);
			if(myDraw != null){
				return new GaeRecordDto(GaeRecordDto.ERROR, "您已经抢过当前红包");
			}
			
			//限制参与人数
			int headCount = draw.getHeadCount(); //当前期数限制抢红包人数 
			int thisHeadCount = draw.getThisHeadCount(); //当前期数实际参与人数
			
			Logger.info("=====>temp-2：drawId:" + drawId + ";uid:" + uid + ",time:" + (System.currentTimeMillis() - date.getTime()) + ",线程号：" + Thread.currentThread().getId() + ",headCount:" + headCount + ",thisHeadCount:" + thisHeadCount);
			date = new Date();
			if(thisHeadCount >= headCount){
				Logger.info("用户参与抢红包失败，人数已满，uid:" + uid + ",期数：" + drawId);
				return new GaeRecordDto(GaeRecordDto.OK, "该红包已经被抢完");
			}
			
			//更新参与人数
			date = new Date();
			
//			Condition cond = new Condition("GaeDrawDDL.id", "=", drawId);
//			cond.add(new Condition("GaeDrawDDL.thisHeadCount", "<", headCount), "AND");
//			GaeDrawDDL newDraw = new GaeDrawDDL();
//			newDraw.setThisHeadCount(thisHeadCount + 1);
//			int result = Dal.update(newDraw, "GaeDrawDDL.thisHeadCount", cond);
			
//			String sql = "update gae_draw set this_head_count=this_head_count + 1 where id='" + drawId + "' and this_head_count < " + headCount;
			//int result = Dal.getConnection(GlobalConstants.dbSource, new UpdateConnectionHandler(sql));
			// 下面这种写法是不能起到事务的作用
//			@SuppressWarnings("deprecation")
//			java.sql.Connection connection = Dal.getConnection(GlobalConstants.dbSource);
//			connection.setAutoCommit(false);
//			int result = connection.createStatement().executeUpdate(sql);
			
			List<SqlParam> updateParams = new ArrayList<SqlParam>();
			updateParams.add(new SqlParam("GaeDrawDDL.id", drawId));
			updateParams.add(new SqlParam("GaeDrawDDL.thisHeadCount", headCount));
			int result = Dal.executeNonQuery(GaeDrawDDL.class, "update gae_draw set this_head_count=this_head_count + 1 where id=? and this_head_count < ?", updateParams, null);
			
			Logger.info("=====>temp-3：drawId:" + drawId + ";result:" + result + ",uid:" + uid + ",time:" + (System.currentTimeMillis() - date.getTime()) + ",线程号：" + Thread.currentThread().getId() + ",headCount:" + headCount + ",thisHeadCount:" + thisHeadCount);
			date = new Date();
			if(result != 1){
				Logger.info("更新参与数量失败，uid:" + uid + ",期数：" + drawId + ",当前已参与人数：" + thisHeadCount);
				throw new UnfitResultException("更新参与数量失败");
			}
			
			//扣减幸运豆
			int thisWantBeans = draw.getRoomTotalBeans(); //当前进行的抢红包需要的豆子
			int thisDrawRatio = draw.getRoomDrawRatio();  //当前进行的抢红包需要抽水比例
			int thisDeductBeans = HappyBeans.beansCount(thisWantBeans, thisDrawRatio); //当前参与抽奖最少需要豆子数
			
			//modify time : 2016.12.21 机器人参与不扣辛运豆
			if(isRobot == 0){
				Map<String, String> params = new HashMap<String, String>();
				params.put("awardScope", String.valueOf(AwardScope.GAME.getType())); 
				params.put("remark", "抢红包消费");
				params.put("billId", drawId);
				params.put("gameId", String.valueOf(SelfGame.GAME_ENVELOPPE.getGameId()));
				params.put("channel", orderFr);
				
				Logger.info("=====>start-4（扣豆开始）：drawId:" + drawId + ";uid:" + uid + ",time:" + (System.currentTimeMillis() - date.getTime()) + ",线程号：" + Thread.currentThread().getId());
				date = new Date();
				
				boolean consumeResult = MemberService.consume(uid, thisDeductBeans, params,"");
				Logger.debug("用户参与抢红包扣减幸运豆,结果：%s,金额：%s,期数id:%s,房间名：%s, uid: %s", consumeResult, thisDeductBeans, drawId, draw.getRoomId(), uid);
				if(!consumeResult){
					Logger.info("用户参与抢红包扣减幸运豆失败，人数已满，uid:" + uid + ",期数：" + drawId);
					throw new UnfitResultException("扣减幸运豆失败，请检查您的账户余额或稍后再试");
				}
				
				Logger.info("=====>start-4（扣豆结束）：drawId:" + drawId + ";uid:" + uid + ",time:" + (System.currentTimeMillis() - date.getTime()) + ",线程号：" + Thread.currentThread().getId());
				date = new Date();
			}else{
				Logger.info("=====>机器人参与抢红包，不执行扣豆操作；uid:" + uid + ",期数：" + drawId);
			}
			
			//插入抢红包记录
			GaeDrawRecordTempDDL drt = new GaeDrawRecordTempDDL();
			drt.setDrawId(drawId);
			drt.setDeductBeans(thisDeductBeans);
			drt.setRoomId(draw.getRoomId());
			drt.setRoomName(draw.getRoomName());
			drt.setUserAvatar(userAvatar);
			drt.setHitTime(new Date().getTime());
			drt.setUserId(uid);
			drt.setUserName(userName);
			drt.setUserZone(userZone);
			drt.setIsRobot(isRobot);
			drt.setOrderId(orderId);
			boolean insertDrawRecordResult = GaeDrawRecordService.insertDrawRecordToTemp(drt);
			
			Logger.info("=====>start-5：drawId:" + drawId + ";uid:" + uid + ",time:" + (System.currentTimeMillis() - date.getTime()) + ",线程号：" + Thread.currentThread().getId());
			date = new Date();
			
			Logger.debug("用户参与抢红包写入记录,结果：%s,金额：%s,期数id:%s,房间名：%s, uid: %s", insertDrawRecordResult, thisDeductBeans, drawId, draw.getRoomId(), uid);
			if(!insertDrawRecordResult){
				Logger.info("用户参与抢红包写入记录失败，人数已满，uid:" + uid + ",期数：" + drawId);
				throw new UnfitResultException("该红包已经被抢完");
			}
			
			Dal.setTransactionSuccessful();
			Logger.info("=====>start-6：drawId:" + drawId + ";uid:" + uid + ",time:" + (System.currentTimeMillis() - date.getTime()) + ",线程号：" + Thread.currentThread().getId());
			date = new Date();
			
			// 真实用户参与任务
			if(isRobot == 0){
				GameTaskService.doTask(uid, SelfGame.GAME_ENVELOPPE.getGameId(), Long.parseLong(draw.getRoomId()), thisWantBeans, false);
			}
			
			return new GaeRecordDto(GaeRecordDto.OK, "ok");
			
		} catch (UnfitResultException ue) {
			return new GaeRecordDto(GaeRecordDto.ERROR, ue.getMessage());
		} catch (Exception e) {
			Logger.info("用户参与抢红包异常，uid:" + uid + ",期数：" + drawId + ":Exception:" + e);
			return new GaeRecordDto(GaeRecordDto.ERROR, "抢红包失败，请稍后再试");
		}finally{
			Dal.endTransaction();
		}
	}
	
	/**
	 * 获取用户的中奖信息
	 * 
	 * @param uid
	 */
	public static GaeRecordDto getMyPrize(int uid){
		Map<String, Integer> map = GaeDrawRecordService.getMyPrize(uid);
		return new GaeRecordDto(GaeRecordDto.OK, "ok", new Gson().toJson(map));
	}
	
	/**
	 * 获取用户排名
	 * 
	 * @param uid
	 */
	public static GaeAwarTopDto getMyTop(int uid, int topType){
		List<GaePrizeTopDDL> list = GaePrizeTopService.getMyTop(topType);
		if(list != null && list.size() > 0){
			for (int i = 0; i < list.size(); i++) {
				if(list.get(i).getUserId() == uid){
					GaePrizeTopDDL gpt = list.get(i);
					GaeAwarTopDto topDto = new GaeAwarTopDto();
					topDto.setUserId(gpt.getUserId());
					topDto.setTotalBeans(gpt.getTotalBeans());
					topDto.setTopType(gpt.getTopType());
					topDto.setNickName(gpt.getUserName());
					topDto.setUserAvatar(gpt.getUserAvatar());
					topDto.setDeawCount(gpt.getDeawCount());
					topDto.setTopNumber(i+1);
					return topDto;
				}
			}
		}
		return null;
	}
	
	/**
	 * 判断是否参与过当前的抽奖
	 * 
	 * @param uid 用户uid
	 * @param awarId 期数id
	 * @throws InvocationTargetException 
	 * @throws IllegalAccessException 
	 */
	private static boolean isLottery(int uid, String drawId){
		GaeDrawRecordTempDDL myActiveDraw = GaeDrawRecordService.getMyActiveDraw(uid, drawId);
		if(myActiveDraw == null){
			GaeDrawRecordDDL myHistoryDraw = GaeDrawRecordService.getMyHistoryDraw(uid, drawId);
			if(myHistoryDraw == null){
				return false;
			}
		}
		return true;
	}
	
	private static JsonObject returnResult(String drawId, String roomId, int drawCost, int hitBeans, int isLottery, int isWorst, String title){
		String content = "";
		String status = "未开奖";
		String baseUrl = Jws.configuration.getProperty("h5game.web.inex", "http://game.8868.cn/");
		String url = baseUrl + "envelope/index";
		if(isLottery == 0){
			content = "正在拆包，抢到幸运数字奖励可翻多倍哦！";
			if(StringUtils.isBlank(drawId) || drawId == "null"){
				content = "该红包失效，已将等值的红包本金转入您的账户，点击进入红包场次赢取奖励吧！";
			}
		}else{
			status = "已开奖";
			if(isWorst == 0){
				content = "恭喜中奖！本金加奖励共" + (drawCost + hitBeans) + "开心豆已入账，抢到辛运数字奖励可翻倍，快来试试吧！";
			}else{
				content = "本期获得奖励" + hitBeans + "开心豆，抢到幸运数字奖励可翻多倍，快来试试吧！";
			}
		}
		if(StringUtils.isNotBlank(drawId) && drawId != "null"){
			url = (url + "#/detail/" + drawId + "?fr=waporder");
		}else{
			url = (url + "#/" + roomId + "?fr=waporder");
		}
		JsonObject json = new JsonObject();
		json.addProperty("title", title);
		json.addProperty("content", content);
		json.addProperty("status", status);
		json.addProperty("url", url);
		return json;
	}
}
