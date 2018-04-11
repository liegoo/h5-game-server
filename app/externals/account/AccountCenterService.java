package externals.account;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Map;
import java.util.TreeMap;

import org.apache.commons.lang.StringUtils;
import org.slf4j.LoggerFactory;

import cn.jugame.account_center.api.IMemberVipService;
import cn.jugame.account_center.impl.test.APIMemberVipServiceImpl;
import cn.jugame.service.common.util.bean.DataBean;

import com.google.gson.JsonObject;

import exception.BusinessException;
import jws.Jws;
import jws.Logger;
import jws.http.Request;
import jws.http.Response;
import jws.http.sf.HTTP;
import utils.ChecksumHelper;
import utils.JsonToMap;

public class AccountCenterService {
	private static org.slf4j.Logger logger = LoggerFactory.getLogger(AccountCenterService.class);
	private final static String appKey = Jws.configuration.getProperty("account.center.busiCode", "");
	private final static String secretKey =Jws.configuration.getProperty("account.center.secretKey","");
	private final static String domain = Jws.configuration.getProperty("account.center.server.url", "192.168.0.55:28089");
	
	public static JsonObject login(String account, String password, String busi) throws BusinessException{
		Map<String, String> paramMap = new TreeMap<String, String>();
		paramMap.put("loginNameOrMobile", account);
		paramMap.put("password", password);
		paramMap.put("busi", busi);
		paramMap.put("appKey", appKey);
		String vcode = ChecksumHelper.getChecksum(paramMap, secretKey);
		String encodePwd = "";
		try {
			encodePwd = URLEncoder.encode(password, "UTF-8");
		} catch (UnsupportedEncodingException e1) {
			e1.printStackTrace();
		}
        //String query_string = "?loginNameOrMobile=" + account + "&password="+encodePwd + "&busi=" + busi + "&appKey=" + appKey +"&vcode=" + vcode;
        Request request = new Request("accountCenter", "login",""); 
        request.addParam("loginNameOrMobile", account);
		request.addParam("password", password);
		request.addParam("busi", busi);
		request.addParam("appKey", appKey);
		request.addParam("vcode", vcode);
        if (Logger.isDebugEnabled()) { 
			Logger.debug("[platService.%s] request append params - %s", "login", "loginNameOrMobile=%s，busi=%s,appKey=%s，vcode=%s", "updateMemberInfo", account, busi, appKey, vcode); 
		} 
        
		String result = "";
		try { 
			Response response = HTTP.POST(request); 
			result = response.getContent(); 
			Logger.info("调用用户中心login返回=%s", result);
		} catch(Exception e) { 
			Logger.error("调用用户中心登录接口login异常:", e.getMessage());
		} 
		if (StringUtils.isBlank(result)) {
			Logger.error("用户中心登录接口login返回内容为空");
			return null;
		}
		JsonObject data = JsonToMap.parseJson(result);
		return data;
	}
	
	public static String queryOpendIdByUid(int uid) throws BusinessException{
		String openId = null;
		try{
			Map<String, String> paramMap = new TreeMap<String, String>();
			paramMap.put("uid", String.valueOf(uid));
			paramMap.put("busi", appKey);
			paramMap.put("appKey", appKey);
			String vcode = ChecksumHelper.getChecksum(paramMap, secretKey);
	        String query_string = "?uid=" + uid + "&busi=" + appKey + "&appKey=" + appKey +"&vcode=" + vcode;
			Request request = new Request("accountCenter", "queryWxInfoByUid", query_string); 
			if (Logger.isDebugEnabled()) { 
				Logger.debug("[platService.%s] request append params - %s", "queryWxInfoByUid", query_string); 
			} 
			String result = "";
			try { 
				Response response = HTTP.GET(request); 
				result = response.getContent(); 
				Logger.info("调用用户中心查询 OpenID 返回=%s", result);
			} catch(Exception e) { 
				Logger.error("调用用户中心查询 OpenID 异常:", e.getMessage());
			} 
			if (StringUtils.isBlank(result)) {
				Logger.error("调用用户中心查询 OpenID 返回内容为空");
				return "";
			}
			JsonObject data = JsonToMap.parseJson(result);
			if(data.has("data") && data.get("data").getAsJsonObject().has("openid")){
				openId = data.get("data").getAsJsonObject().get("openid").getAsString();
			}
		}catch(Exception e){
			Logger.error(e, "");
			throw new BusinessException("调用OpenID查询失败");
		}
		return openId;
	}
	
	public static JsonObject checkLoginByToken(String sid) {
		Map<String, String> paramMap = new TreeMap<String, String>();
		paramMap.put("token", sid);
		paramMap.put("appKey", appKey);
		String vcode = ChecksumHelper.getChecksum(paramMap, secretKey);
		
		String query_string = "?token=" + sid + "&appKey=" + appKey + "&vcode=" + vcode;
		Request request = new Request("accountCenter", "checkLoginByToken", query_string); 
		if (Logger.isDebugEnabled()) { 
			Logger.debug("[platService.%s] request append params - %s", "checkLoginByToken", query_string); 
		} 
		String result = "";
		try { 
			Response response = HTTP.GET(request); 
			result = response.getContent(); 
			Logger.info("调用用户中心checkLoginByToken返回=%s", result);
		} catch(Exception e) { 
			Logger.error("调用用户中心登录验证接口checkLoginByToken异常:", e.getMessage());
		} 
		if (StringUtils.isBlank(result)) {
			Logger.error("调用用户中心登录验证接口checkLoginByToken返回内容为空");
			return null;
		}
		JsonObject data = JsonToMap.parseJson(result);
		return data;
	}
	
	public static JsonObject refreshLogin(String sid, String busiCode) {
		Map<String, String> paramMap = new TreeMap<String, String>();
		paramMap.put("token", sid);
		paramMap.put("busi", busiCode);
		paramMap.put("appKey", appKey);
		String vcode = ChecksumHelper.getChecksum(paramMap, secretKey);
		
		String query_string = "?token=" + sid + "&busi=" + busiCode + "&appKey=" + appKey +"&vcode=" + vcode;
		Request request = new Request("accountCenter", "refreshLogin", query_string); 
		if (Logger.isDebugEnabled()) { 
			Logger.debug("[platService.%s] request append params - %s", "refreshLogin", query_string); 
		} 
		String result = "";
		try { 
			Response response = HTTP.GET(request); 
			result = response.getContent(); 
			Logger.info("调用用户中心refreshLogin返回=%s", result);
		} catch(Exception e) { 
			Logger.error("调用用户中心会话刷新接口checkTokenAndProlongExpiredTime异常:", e.getMessage());
		} 
		if (StringUtils.isBlank(result)) {
			Logger.error("调用用户中心会话刷新接口checkTokenAndProlongExpiredTime返回内容为空");
			return null;
		}
		JsonObject data = JsonToMap.parseJson(result);
		return data;
	}
	
	public static JsonObject checkMobile(String mobile) {
		Map<String, String> paramMap = new TreeMap<String, String>();
		paramMap.put("mobile", mobile);
		paramMap.put("appKey", appKey);
		String vcode = ChecksumHelper.getChecksum(paramMap, secretKey);
		
		String query_string = "?mobile=" + mobile + "&appKey=" + appKey +"&vcode=" + vcode;
		Request request = new Request("accountCenter", "checkMoible", query_string); 
		if (Logger.isDebugEnabled()) { 
			Logger.debug("[platService.%s] request append params - %s", "checkMoible", query_string); 
		} 
		String result = "";
		try { 
			Response response = HTTP.GET(request); 
			result = response.getContent(); 
			Logger.info("调用用户中心checkMobile返回=%s", result);
		} catch(Exception e) { 
			Logger.error("调用用户中心checkMobile异常:", e.getMessage());
		} 
		if (StringUtils.isBlank(result)) {
			Logger.error("调用用户中心checkMobile返回内容为空");
			return null;
		}
		JsonObject data = JsonToMap.parseJson(result);
		return data;
	}
	/**
	 * 检查会员是否存在
	 * @param uid
	 * @return
	 */
	public static JsonObject checkUid(int uid) {
		Map<String, String> paramMap = new TreeMap<String, String>();
		paramMap.put("sdkUid", String.valueOf(uid));
		paramMap.put("appKey", appKey);
		String vcode = ChecksumHelper.getChecksum(paramMap, secretKey);
		
		String query_string = "?sdkUid=" + uid + "&appKey=" + appKey +"&vcode=" + vcode;
		Request request = new Request("accountCenter", "checkUid", query_string); 
		if (Logger.isDebugEnabled()) { 
			Logger.debug("[platService.%s] request append params - %s", "checkUid", query_string); 
		} 
		String result = "";
		try { 
			Response response = HTTP.GET(request); 
			result = response.getContent();  
			Logger.info("调用用户中心检查uid返回=%s", result);
		} catch(Exception e) { 
			Logger.error("调用用户中心检查uid异常:", e.getMessage());
		} 
		if (StringUtils.isBlank(result)) {
			Logger.error("调用用户中心检查uid返回内容为空");
			return null;
		}
		JsonObject data = JsonToMap.parseJson(result);
		return data;
	}
	
	
	public static JsonObject bindMobile(int uid,String mobile) {
		Map<String, String> paramMap = new TreeMap<String, String>();
		paramMap.put("uid", String.valueOf(uid));
		paramMap.put("mobile", mobile);
		paramMap.put("appKey", appKey);
		String vcode = ChecksumHelper.getChecksum(paramMap, secretKey);
		
		String query_string = "?uid=" + uid +"&mobile="+mobile+ "&appKey=" + appKey +"&vcode=" + vcode;
		Request request = new Request("accountCenter", "bindMobile", query_string); 
		if (Logger.isDebugEnabled()) { 
			Logger.debug("[platService.%s] request append params - %s", "bindMobile", query_string); 
		} 
		String result = "";
		try { 
			Response response = HTTP.GET(request); 
			result = response.getContent(); 
			Logger.info("调用用户中心bindMobile返回=%s", result);
		} catch(Exception e) { 
			Logger.error("调用用户中心bindMobile异常:", e.getMessage());
		} 
		if (StringUtils.isBlank(result)) {
			Logger.error("调用用户中心bindMobile返回内容为空");
			return null;
		}
		JsonObject data = JsonToMap.parseJson(result);
		return data;
	}
	
	public static JsonObject findUidByMobile(String mobile) {
		Map<String, String> paramMap = new TreeMap<String, String>();
		paramMap.put("mobile", mobile);
		paramMap.put("appKey", appKey);
		String vcode = ChecksumHelper.getChecksum(paramMap, secretKey);
		String query_string = "?mobile=" + mobile + "&appKey=" + appKey +"&vcode=" + vcode;
		
		Request request = new Request("accountCenter", "checkUserExistByMobile", query_string); 
		if (Logger.isDebugEnabled()) { 
			Logger.debug("[platService.%s] request append params - %s", "checkUserExistByMobile", query_string); 
		} 
		String result = "";
		try { 
			Response response = HTTP.GET(request);
			result = response.getContent(); 
			Logger.info("调用用户中心checkUserExistByMobile返回=%s", result);
			JsonObject data = JsonToMap.parseJson(result);
			if(0 == data.get("code").getAsInt()){
				String jsonStr = data.get("data").toString().replaceAll("\\\\", "");
	    		jsonStr = jsonStr.substring(1, jsonStr.length() -1);
	    		return JsonToMap.parseJson(jsonStr);
			}
		} catch(Exception e) { 
			Logger.error("调用用户中心根据手机号查用户接口checkUserExistByMobile异常:", e.getMessage());
		} 
		Logger.error("调用用户中心根据手机号查用户接口checkUserExistByMobile返回内容为空");
		return null;
	}
	
	/**
	 * 用户中心手机号快捷登录
	 * @param mobile
	 * @param code
	 * @param userIp
	 * @param busi
	 * @return
	 */
	public static JsonObject loginByDynamicDigital(String mobile, String code, String userIp, String busi) {
		Map<String, String> paramMap = new TreeMap<String, String>();
		paramMap.put("mobile", mobile);
		paramMap.put("code", code);
		paramMap.put("ip", userIp);
		paramMap.put("busi", busi);
		paramMap.put("user_terrace", "h5game");
		paramMap.put("appKey", appKey);
		String vcode = ChecksumHelper.getChecksum(paramMap, secretKey);
		String query_string = "?mobile=" + mobile + "&code=" + code + "&userIp=" + userIp + "&busi=" + busi + "&user_terrace=h5game" +  "&appKey=" + appKey +"&vcode=" + vcode;
		
		Request request = new Request("accountCenter", "loginByDynamicDigital", query_string); 
		if (Logger.isDebugEnabled()) { 
			Logger.debug("[platService.%s] request append params - %s", "loginByDynamicDigital", query_string); 
		} 
		String result = "";
		try { 
			Response response = HTTP.GET(request);
			result = response.getContent(); 
			Logger.info("调用用户中心loginByDynamicDigital返回=%s", result);
		} catch(Exception e) { 
			Logger.error("调用用户中心loginByDynamicDigital异常:", e.getMessage());
		} 
		if (StringUtils.isBlank(result)) {
			Logger.error("调用用户中心loginByDynamicDigital返回内容为空");
			return null;
		}
		JsonObject data = JsonToMap.parseJson(result);
		return data;
	}
	
	public static JsonObject updateNickname(int uid,String nickname) throws BusinessException{
		JsonObject data = null;
		try{
			Map<String, String> paramMap = new TreeMap<String, String>();
			paramMap.put("nickName", nickname);
			paramMap.put("uid", String.valueOf(uid));
			paramMap.put("appKey", appKey);
			String vcode = ChecksumHelper.getChecksum(paramMap, secretKey);
//	        String query_string = "?nickName=" + URLEncoder.encode(nickname, "UTF-8") + "&uid="+ uid +"&appKey=" + appKey + "&vcode=" + vcode;
			Request request = new Request("accountCenter", "updateMemberInfo", ""); 
			request.addParam("nickName", nickname);
			request.addParam("uid", uid+"");
			request.addParam("appKey", appKey);
			request.addParam("vcode", vcode);
			if (Logger.isDebugEnabled()) {
				Logger.debug("[platService.%s] request append params - nickName=%s，uid=%s，appKey=%s，vcode=%s", "updateMemberInfo", nickname, uid+"", appKey, vcode); 
			} 
			String result = "";
			try { 
				Response response = HTTP.POST(request);// 因线上出现get请求到服务端乱码情况，所以该成post
				result = response.getContent(); 
				Logger.info("调用用户中心 修改昵称返回=%s", result);
			} catch(Exception e) { 
				Logger.error("调用用户中心 修改昵称异常:", e.getMessage());
			} 
			if (StringUtils.isBlank(result)) {
				Logger.error("调用用户中心 修改昵称 返回内容为空");
				return null;
			}
			data = JsonToMap.parseJson(result);
		}catch(Exception e){
			Logger.error(e, "");
			throw new BusinessException("修改昵称失败");
		}
		return data;
	}
	
	
	//TODO
	public static JsonObject updatePassword(String uid,String pwd) {
		Map<String, String> paramMap = new TreeMap<String, String>();
		paramMap.put("uid", uid);
		paramMap.put("password", pwd);
		paramMap.put("appKey", appKey);
		String vcode = ChecksumHelper.getChecksum(paramMap, secretKey);
		
		String query_string = "?uid=" + uid + "password=" + pwd + "&appKey=" + appKey +"&vcode=" + vcode;
		Request request = new Request("accountCenter", "updatePassword", query_string); 
		if (Logger.isDebugEnabled()) { 
			Logger.debug("[platService.%s] request append params - %s", "updatePassword", query_string); 
		} 
		String result = "";
		try { 
			Response response = HTTP.GET(request); 
			result = response.getContent(); 
			Logger.info("调用用户中心updatePassword返回=%s", result);
		} catch(Exception e) { 
			Logger.error("调用用户中心updatePassword异常:", e.getMessage());
		} 
		if (StringUtils.isBlank(result)) {
			Logger.error("调用用户中心updatePassword返回内容为空");
			return null;
		}
		JsonObject data = JsonToMap.parseJson(result);
		return data;
	}
	
	/**
	 * 通过uid获取用户信息
	 * @param uid
	 * @return
	 * @throws BusinessException
	 */
	public static JsonObject getUserInfoByUid(int uid) throws BusinessException {
		Map<String, String> paramMap = new TreeMap<String, String>();
		paramMap.put("uid", String.valueOf(uid));
		paramMap.put("busi", appKey);
		paramMap.put("appKey", appKey);
		String vcode = ChecksumHelper.getChecksum(paramMap, secretKey);
		String query_string = "?uid=" + uid + "&busi=" + appKey + "&appKey=" + appKey + "&vcode=" + vcode;
		Request request = new Request("accountCenter", "getUserInfoByUid", query_string);
		if (Logger.isDebugEnabled()) {
			Logger.debug("[accountService.%s] request append params - %s", "getUserInfo", query_string);
		}

		String result = "";
		try {
			Response response = HTTP.GET(request);
			result = response.getContent();
			Logger.info("调用用户中心 getUserInfo 返回=%s", result);
		} catch (Exception e) {
			Logger.error("调用用户中心 getUserInfo 接口异常:", e.getMessage());
		}
		if (StringUtils.isBlank(result)) {
			Logger.error("用户中心 getUserInfo 接口返回内容为空");
			return null;
		}
		JsonObject data = JsonToMap.parseJson(result);
		return data;
	}
	/**
	 * 签到加豆
	 * @param uid
	 * @return
	 */
	public static boolean addScoreForHappyBeanCheckIn(int uid){
		//这里用了俊杰封闭的jar来发请求
		int statEnable = 1;
		logger.info("addScoreForHappyBeanCheckIn,uid="+uid);
		IMemberVipService vip = new APIMemberVipServiceImpl(domain, 15000, statEnable,appKey, secretKey);
		DataBean<Boolean> rtn = vip.addScoreForHappyBeanCheckIn(uid);
		
		logger.info("addScoreForHappyBeanCheckIn,uid="+uid+",rtnCode="+rtn.getCode());
		if(rtn.getCode()==DataBean.OK.intValue()){
			return true;
		}
		return false;
	}
	
	public static boolean addScoreForHappyBeanRecharge(int uid, double rechargeAmount){
		int statEnable = 1;
		logger.info("addScoreForHappyBeanCheckIn,uid=%s,rechargeAmount=" + uid+ ",rechargeAmount="+rechargeAmount);
		IMemberVipService vip = new APIMemberVipServiceImpl(domain, 15000, statEnable,appKey, secretKey);
		DataBean<Boolean> rtn = vip.addScoreForHappyBeanRecharge(uid,rechargeAmount);
		logger.info("addScoreForHappyBeanCheckIn,uid="+uid+",rechargeAmount="+rechargeAmount+",rtnCode="+rtn.getCode());
		if(rtn.getCode()==DataBean.OK.intValue()){
			return true;
		}
		return false;
	}
	
	public static void main(String[] args) {
		AccountCenterService.addScoreForHappyBeanRecharge(1,200);
	}
	
}
