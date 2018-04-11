package controllers.ucgc;

import java.util.Map;

import com.google.gson.JsonObject;
import common.core.UcgcController;

import externals.web.WebPlatformService;

public class WeChat extends UcgcController {

	/**
	 * 发送微信公众号模版消息
	 */
	public static void sendTemplateMessage() {
		Map<String,String> params = getDTO(Map.class);
		String openId = params.get("openId");
		String tempId = params.get("templateId");
		String url = params.get("url");
		String jsonData = params.get("jsonData");
		
		JsonObject result = WebPlatformService.sendTemplateMessage(openId, tempId, url, jsonData);
		getHelper().returnSucc(result);
	}
	
	/**
	 * 获取信息用户信息
	 */
	public static void getWeChatUserInfo() {
		Map<String,String> params = getDTO(Map.class);
		String openId = params.get("openId");
		JsonObject result = WebPlatformService.getWeChatUserInfo(openId);
		getHelper().returnSucc(result);
	}
}
