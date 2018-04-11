package externals.web;

import java.net.URLEncoder;

import jws.Jws;
import jws.Logger;
import jws.http.Request;
import jws.http.Response;
import jws.http.sf.HTTP;
import jws.modules.client.MD5;

import org.apache.commons.lang.StringUtils;

import utils.DES;
import utils.HashKit;
import utils.JsonToMap;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class WebPlatformService {

	private static String apiPid = Jws.configuration.getProperty("web.platform.api.wechat.pid", ""); // 调用接口身份id
	private static String apiKey = Jws.configuration.getProperty("web.platform.api.wechat.key", ""); // key
	private static String wxUnique = Jws.configuration.getProperty("web.platform.api.wechat.weixin_unique", ""); // key

	/**
	 * 发送微信模板消息
	 * 
	 * @param noticeId
	 * @return
	 */
	public static JsonObject sendTemplateMessage(String openId, String tempId, String url, String jsonData) {

		if (!Boolean.valueOf(Jws.configuration.getProperty("wechat.template.msg.enabled", "false"))) {
			Logger.error("微信公众号发消息功能未开启，发送信息失败.");
			return null;
		}

		JsonObject data = new JsonObject();
		data.addProperty("open_id", openId);
		data.addProperty("template_id", tempId);
		data.addProperty("url", url);
		JsonParser parser = new JsonParser();
		JsonElement arr = parser.parse(jsonData);
		data.add("data", arr.getAsJsonArray());
		data.addProperty("weixin_unique", wxUnique);
		String time = String.valueOf(System.currentTimeMillis());
		String cryData;
		try {
			cryData = URLEncoder.encode(DES.encode(data.toString(), apiKey));
			String sign = HashKit.md5(data.toString() + time + apiKey).substring(0, 8);
			String cmd = "send_temp_msg";

			String query_string = "?data=" + cryData + "&time=" + time + "&cmd=" + cmd + "&pid=" + apiPid + "&sign=" + sign;
			Request request = new Request("wechatService", "sendTemplateMessage", query_string);

			if (Logger.isDebugEnabled()) {
				Logger.debug("[WebPlatformService.%s] request append params - %s", "sendTemplateMessage", query_string);
			}
			String result = "";
			Response response = HTTP.POST(request);
			result = response.getContent();
			Logger.info("调用网站平台API-发送微信模板消息-返回=%s", result);
			if (StringUtils.isBlank(result)) {
				Logger.error("调用网站平台API-发送微信模板消息-返回内容为空");
				return null;
			}
			JsonObject jsonObj = JsonToMap.parseJson(result);
			if (null != jsonObj && jsonObj.get("code").getAsInt() == 0 && jsonObj.get("data") != null) {
				return jsonObj;
			} else {
				if (jsonObj.has("msg")) {
					Logger.error("调用网站平台API-发送微信模板消息-失败:%s", jsonObj.get("msg"));
				}
			}
			return null;
		} catch (Exception e) {
			Logger.error("调用网站平台API-发送微信模板消息-异常:%s", e.getMessage());
		}
		return null;
	}

	/**
	 * 获取微信用户信息
	 * 
	 * @param noticeId
	 * @return
	 */
	public static JsonObject getWeChatUserInfo(String openId) {
		JsonObject data = new JsonObject();
		data.addProperty("open_id", openId);
		data.addProperty("weixin_unique", wxUnique);

		String time = String.valueOf(System.currentTimeMillis());
		String cryData;
		try {
			cryData = URLEncoder.encode(DES.encode(data.toString(), apiKey));
			String sign = MD5.encode(data.toString() + time + apiKey).substring(0, 8);
			String cmd = "weixin_userinfo";

			String query_string = "?data=" + cryData + "&time=" + time + "&cmd=" + cmd + "&pid=" + apiPid + "&sign=" + sign;

			Request request = new Request("wechatService", "getWeChatUserInfo", query_string);
			if (Logger.isDebugEnabled()) {
				Logger.debug("[WebPlatformService.%s] request append params - %s", "getUserInfo", query_string);
			}
			String result = "";
			Response response = HTTP.POST(request);
			result = response.getContent();
			Logger.info("调用网站平台API-获取微信用户信息-返回=%s", result);
			if (StringUtils.isBlank(result)) {
				Logger.error("调用网站平台API-获取微信用户信息-返回内容为空");
				return null;
			}
			JsonObject jsonObj = JsonToMap.parseJson(result);
			if (null != jsonObj && jsonObj.get("code").getAsInt() == 0 && jsonObj.get("data") != null) {
				return jsonObj;
			} else {
				if (jsonObj.has("msg")) {
					Logger.error("调用网站平台API-获取微信用户信息-失败:%s", jsonObj.get("msg"));
				}
			}
			return null;
		} catch (Exception e) {
			e.printStackTrace();
			Logger.error("调用网站平台API-获取微信用户信息-异常:%s", e.getMessage());
		}
		return null;
	}
}
