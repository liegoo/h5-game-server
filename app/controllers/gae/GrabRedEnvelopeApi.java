package controllers.gae;
import java.util.Map;

import jws.module.response.age.GaeAwarTopDto;
import jws.module.response.age.GaeRecordDto;
import task.gae.RobotTask;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import moudles.gae.ddl.GaeUserPlDDL;
import moudles.gae.service.GrabRedEnvelopeService;
import moudles.gae.service.child.GaePrizeTopService;
import moudles.gae.service.child.GaeRobotSettingService;
import moudles.gae.service.child.GaeUserPlService;
import common.core.UcgcController;

/**
 * 抢红包活动接口
 * 
 * @author caixb
 *
 */
public class GrabRedEnvelopeApi extends UcgcController{
	
	/**
	 * 获取订单信息
	 * 
	 * @param uid
	 * @param orderId
	 * @return
	 */
	public static void getOrderInfo(){
		Map params = getDTO(Map.class);
		int uid = Integer.parseInt(params.get("uid").toString());
		String orderId = (String)params.get("orderId");
		JsonObject json = GrabRedEnvelopeService.getOrderInfo(uid, orderId);
		getHelper().returnSucc(json.toString());
	}
	/**
	 * 抢红包
	 * 
	 * @return
	 */
	public static void grabByOrder(){
		Map params = getDTO(Map.class);
		int uid = Integer.parseInt(params.get("uid").toString());
		String userName = (String)params.get("userName");
		String userAvatar = (String)params.get("userAvatar");
		String ip = (String)params.get("ip");
		String userZone = (String)params.get("userZone");
		
		String orderId = (String)params.get("orderId");
		double orderPrice = Double.valueOf(params.get("orderPrice").toString()).doubleValue();
		String orderFr = (String)params.get("orderFr");
		String roomId = (String)params.get("roomId");
		String title = (String)params.get("title");
		getHelper().returnSucc(GrabRedEnvelopeService.grabByOrder(uid, orderId, title, orderPrice, orderFr, roomId, userName, userAvatar, ip, userZone));
	}
	
	/**
	 * 抢红包
	 * 
	 * @return
	 */
	public static void grab(){
		Map params = getDTO(Map.class);
		int uid = Integer.parseInt(params.get("uid").toString());
		String drawId = (String)params.get("drawId");
		String userName = (String)params.get("userName");
		String userAvatar = (String)params.get("userAvatar");
		String ip = (String)params.get("ip");
		String userZone = (String)params.get("userZone");
		getHelper().returnSucc(GrabRedEnvelopeService.grab(uid, drawId, userName, userAvatar, ip, userZone));
	}
	
	/**
	 * 获取排行榜数据(排行榜)
	 * 
	 * @return
	 */
	public static void getTopList(){
		Map params = getDTO(Map.class);
		int type = Integer.parseInt(params.get("type").toString());
		int pageNo = Integer.parseInt((String)params.get("pageNo"));
		int pageSize = Integer.parseInt((String)params.get("pageSize"));
		getHelper().returnSucc(GrabRedEnvelopeService.getTopList(type, pageNo, pageSize));
	}
	
	/**
	 * 根据用户查询参与历史记录(战绩)
	 * 
	 * @param uid 用户uid
	 * @param pageNo 当前页
	 * @param pageSize 每页数量
	 * @return
	 */
	public static void getDrawRecord(){
		Map params = getDTO(Map.class);
		int uid = Integer.parseInt(params.get("uid").toString());
		int pageNo = Integer.parseInt((String)params.get("pageNo"));
		int pageSize = Integer.parseInt((String)params.get("pageSize"));
		getHelper().returnSucc(GrabRedEnvelopeService.getDrawRecord(uid, pageNo, pageSize));
	}
	
	/**
	 * 获取抢红包场次列表
	 */
	public static void getRooms(){
		Map params = getDTO(Map.class);
		if(params.get("uid") == null){
			getHelper().returnError(-10, "参数有误！");
		}
		int uid = Integer.parseInt(params.get("uid").toString());
		getHelper().returnSucc(GrabRedEnvelopeService.getRooms(uid));
	}
	
	
	/**
	 * 获取开奖期数(页面心跳数据)
	 * 
	 * @param uid 用户uid
	 * @param roomId 场次id
	 */
	public static void getDraw(){
		Map params = getDTO(Map.class);
		int uid = Integer.parseInt(params.get("uid").toString());
		String roomId = (String)params.get("roomId");
		getHelper().returnSucc(GrabRedEnvelopeService.getDraw(uid, roomId));
	}
	
	
	/**
	 * 获取开奖明细(点击查看明细)
	 * 
	 * @param uid 用户uid
	 * @param awarId 期数id
	 */
	public static void getDrawDetail(){
		Map params = getDTO(Map.class);
		int uid = Integer.parseInt(params.get("uid").toString());
		String drawId = (String)params.get("drawId");
		getHelper().returnSucc(GrabRedEnvelopeService.getDrawDetail(uid, drawId));
	}
	
	/**
	 * 获取用户的中奖信息
	 * 
	 * @param uid
	 */
	public static void getMyPrize(){
		Map params = getDTO(Map.class);
		int uid = Integer.parseInt(params.get("uid").toString());
		getHelper().returnSucc(GrabRedEnvelopeService.getMyPrize(uid));
	}
	
	/**
	 * 获取用户排名
	 * 
	 * @param uid
	 * @param topType
	 * @return
	 */
	public static void getMyTop(){
		Map params = getDTO(Map.class);
		int uid = Integer.parseInt(params.get("uid").toString());
		int topType = Integer.parseInt(params.get("topType").toString());
		getHelper().returnSucc(GrabRedEnvelopeService.getMyTop(uid, topType));
	}
}
