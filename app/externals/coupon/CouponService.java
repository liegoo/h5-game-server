package externals.coupon;

import java.util.Map;
import java.util.TreeMap;

import org.apache.commons.lang.StringUtils;

import com.google.common.base.Strings;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.jfinal.kit.JsonKit;
import com.jfinal.plugin.activerecord.Record;

import externals.CommonService;
import externals.account.AccountCenterService;
import jws.Jws;
import jws.Logger;
import jws.http.Request;
import jws.http.Response;
import jws.http.sf.HTTP;
import moudles.award.ddl.AwardDDL;
import moudles.award.model.Coupon;
import moudles.award.service.AwardService;
import moudles.game.service.GameService;
import sun.util.logging.resources.logging;
import utils.ChecksumHelper;
import utils.DateUtil;
import utils.JsonToMap;

public class CouponService {
	static org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(CouponService.class);
	private static final String SECRET_KEY =Jws.configuration.getProperty("game_product.service.sign","");
	private static final String APP_KEY = Jws.configuration.getProperty("game_product.service.caller", "");
	private static final int COUPON_EXPIRE_DAY = Integer.parseInt(Jws.configuration.getProperty("coupon.resell.valid_period", "30"));
	/**
	 * 添加代金券
	 * @param uid
	 * @param sourceType
	 * @param zhifuOrderId
	 * @param awardId
	 * @return
	 * */
	public static String apply(String uid, int sourceType, String zhifuOrderId, int awardId){	
		Coupon coupon = new Coupon();
		coupon.setUid(uid);
		coupon.setSourceId(zhifuOrderId);
		coupon.setSourceType(sourceType);
		coupon.setTranferLimit(11);
		coupon.setExpTime(DateUtil.getNextMonthTime(COUPON_EXPIRE_DAY));
		AwardDDL award = null;
		try{
			award = AwardService.getAwardById(awardId);
			Integer denomination = CommonService.getCouponPrice(award.getName());
			if(denomination == null){
				logger.error("zhifuOrderId="+ zhifuOrderId + ",awardId="+awardId+"," +award.getName() + " 格式不对");
				return "";
			}
			coupon.setDenomination(denomination*100);
			if(award.getGameId() == null){
				coupon.setType(1);
			}else{
				coupon.setType(2);
				coupon.setGameId(award.getGameId());
				Record record = GameService.getCouponGame(award.getGameId()+"");
				if(null != record){
					coupon.setGameName(record.get("game_name").toString());;
				}
			}
		}catch(Exception ex){
			Logger.error("申请代金券失败，查询奖品信息异常"+ex.getMessage(),ex);
			return "";
		}	
		TreeMap<String,Object> paramMap = new TreeMap<String,Object>();
		Logger.info("代金券信息=%s", new Gson().toJson(coupon));
		paramMap.put("coupon", coupon);
		JsonObject data = gameProductServiceHelper(paramMap, "applyCoupon");
		if(data == null || data.get("code").getAsInt() != 0 
				|| !data.has("data")){
			Logger.error("申请代金券失败:"+JsonKit.toJson(paramMap));
			return "";
		}
		return data.get("data").getAsString();
	}
	
	/**
	 * 同步优惠券状态
	 * @param couponId
	 * @param status
	 * @param auditMemo
	 * @return
	 */
	public static String audit(String couponId, int status,String auditMemo){
		TreeMap<String,Object> paramMap = new TreeMap<String,Object>();
		paramMap.put("couponId", couponId);
		paramMap.put("status", status);
		paramMap.put("auditMemo", auditMemo);
		JsonObject data = gameProductServiceHelper(paramMap, "auditCoupon");
//		{"code":403,"msg":"此次审核失败,记录不存在,可能过期.","data":false,"dataType":"java.lang.Boolean"}
		if(data == null){
			Logger.info("同步代金券状态失败(接口无返回数据):"+JsonKit.toJson(paramMap));
			return "同步代金券状态失败";
		}
		if(data.get("code").getAsInt() == 0){
			return "";
		}
		String errorMsg = "同步代金券状态失败";
		if(data.get("msg") != null){
			errorMsg = data.get("msg").getAsString();
		}
		return errorMsg;
	}
	
	/**
	 * 转卖代金券
	 * @param couponId
	 * @param singlePrice
	 * @param publishSource
	 * @return
	 */
	public static String resell(String couponId, double singlePrice,String clientIp){
		TreeMap<String,Object> paramMap = new TreeMap<String,Object>();
		paramMap.put("couponId", couponId);
		paramMap.put("singlePrice", singlePrice);
		paramMap.put("sellerIP", clientIp);
		paramMap.put("publishSource", "H5-game-server");
		JsonObject data = gameProductServiceHelper(paramMap, "resellCoupon");
		if(data == null){
			Logger.info("转卖代金券失败(接口无返回数据):"+JsonKit.toJson(paramMap));
			return "转卖代金券失败";
		}
		if(data.get("code").getAsInt() == 0){
			return "";
		}
		String errorMsg = "转卖代金券失败";
		if(data.get("msg") != null){
			errorMsg = data.get("msg").getAsString();
		}
		return errorMsg;
	}
	
	/**
	 * 激活代金券
	 * @param couponId
	 * @param gameId
	 * @param gameName
	 * @param activiteAccount
	 * @param validDay
	 * @return
	 */
	public static String activite(String couponId,int gameId,String gameName,String activiteAccount){
		if(Strings.isNullOrEmpty(couponId)){
			return "优惠券ID不能为空";
		}
		if(Strings.isNullOrEmpty(activiteAccount)){
			return "游戏账号不能为空";
		}
		JsonObject memberInfo = AccountCenterService.findUidByMobile(activiteAccount);
		if(memberInfo != null && memberInfo.get("sdkUid") != null){
			activiteAccount = memberInfo.get("sdkUid").toString();// 如果用户填写的是手机号码，则转换为对应的sdk账号
		}
		TreeMap<String,Object> paramMap = new TreeMap<String,Object>();
		paramMap.put("couponId", couponId);
		paramMap.put("gameId", gameId);
		paramMap.put("gameName", gameName);
		paramMap.put("activiteAccount", activiteAccount);
		paramMap.put("validDay", 0);
		JsonObject data = gameProductServiceHelper(paramMap, "activiteCoupon");
		if(data == null || data.get("code").getAsInt() != 0 
				|| !data.has("data") || Strings.isNullOrEmpty(data.get("data").toString())){
			Logger.info("激活代金券失败:"+JsonKit.toJson(paramMap));
			return "激活代金券失败("+data.get("msg")+")";
		}
		return "";
	}
	
//	/**
//	 * 同步代金券
//	 * @return
//	 */
//	public static JsonObject applyCoupon(Coupon coupon,) {
//		TreeMap<String, Object> paramMap = new TreeMap<String, Object>();
//		paramMap.put("coupon",coupon);
//		ChecksumHelper.addSign(appKey, secretKey, paramMap);
//		Request request = new Request("gameProductService", "applyCoupon", "");		
//		request.addParam("requst", );
//		request.addParam("caller", appKey);
//		request.addParam("sign", paramMap.get("sign").toString());	
//		if (Logger.isDebugEnabled()) {
//			Logger.debug("[CouponService.%s] request append params - coupon=%s", "applyCoupon", new Gson().toJson(paramMap.get("coupon")));
//		}
//		String result = "";
//		try {
//			Response response = HTTP.POST(request);			
//			result = response.getContent();
//			Logger.info("调用游戏商品服务applyCoupon返回=%s", result);
//		} catch (Exception e) {
//			Logger.error("调用游戏商品服务applyCoupon接口异常：%s", e.getMessage());
//		}
//		if (StringUtils.isBlank(result)) {
//			Logger.error("调用游戏商品服务applyCoupon接口返回内容为空");
//			return null;
//		}
//		JsonObject data = JsonToMap.parseJson(result);
//		return data;
//	}
//	
	public static JsonObject gameProductServiceHelper(TreeMap<String, Object> paramMap,String requsetMethod){//req 请求的方法名
		ChecksumHelper.addSign(SECRET_KEY, APP_KEY, paramMap);
		Request request = new Request("gameProductService", requsetMethod, "");
		for (String key : paramMap.keySet()) {
			request.addParam(key, paramMap.get(key).toString());
		}
		if (Logger.isDebugEnabled()) {
			Logger.debug("[CouponService.%s] request append params - requsetMethod=%s", requsetMethod, new Gson().toJson(paramMap));
		}
		String result = "";
		try {
			Response response = HTTP.POST(request);			
			result = response.getContent();
			Logger.info("调用游戏商品服务"+requsetMethod+"返回=%s", result);
		} catch (Exception e) {
			Logger.error("调用游戏商品服务"+requsetMethod+"+接口异常：%s", e.getMessage());
		}
		if (StringUtils.isBlank(result)) {
			Logger.error("调用游戏商品服务"+requsetMethod+"接口返回内容为空");
			return null;
		}
		JsonObject data = JsonToMap.parseJson(result);
		return data;
	}
	
	
}
