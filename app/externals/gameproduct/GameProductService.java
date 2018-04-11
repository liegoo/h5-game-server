package externals.gameproduct;

import java.util.TreeMap;

import org.apache.commons.lang.StringUtils;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import exception.BusinessException;
import jws.Jws;
import jws.Logger;
import jws.http.Request;
import jws.http.Response;
import jws.http.sf.HTTP;
import utils.ChecksumHelper;
import utils.JsonToMap;

public class GameProductService {
	private final static String secretKey =Jws.configuration.getProperty("game_product.service.sign","");
	private final static String appKey = Jws.configuration.getProperty("game_product.service.caller", "");
	
	/**
	 * 同步8868端的游戏到开心大厅
	 * @return
	 */
	public static JsonObject get8868ChannelGames() {
		String channelId = "20";
		TreeMap<String, Object> paramMap = new TreeMap<String, Object>();
		paramMap.put("channelId", channelId);
		ChecksumHelper.addSign(appKey, secretKey, paramMap);
		
		Request request = new Request("gameProductService", "getGames", "");
		request.addParam("channelId", paramMap.get("channelId").toString());
		request.addParam("caller", appKey);
		request.addParam("sign", paramMap.get("sign").toString());
		
		if (Logger.isDebugEnabled()) {
			Logger.debug("[gameProductService.%s] request append params - channelId=%s", "get8868ChannelGames", channelId);
		}
		String result = "";
		try {
			Response response = HTTP.POST(request);			
			result = response.getContent();
			Logger.info("调用游戏商品服务get8868ChannelGames返回=%s", result);
		} catch (Exception e) {
			Logger.error("调用游戏商品服务get8868ChannelGames接口异常：%s", e.getMessage());
		}
		if (StringUtils.isBlank(result)) {
			Logger.error("调用游戏商品服务get8868ChannelGames接口返回内容为空");
			return null;
		}
		JsonObject data = JsonToMap.parseJson(result);
		return data;
	}
	
	
}
