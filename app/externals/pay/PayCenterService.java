package externals.pay;

import java.util.Map;
import java.util.TreeMap;

import jws.Jws;
import jws.Logger;
import jws.http.Request;
import jws.http.Response;
import jws.http.sf.HTTP;

import org.apache.commons.lang.StringUtils;

import utils.ChecksumHelper;
import utils.JsonToMap;

import com.google.gson.JsonObject;

public class PayCenterService {

	private final static String appKey = Jws.configuration.getProperty("pay.busiCode", "8868");
	private final static String secretKey = Jws.configuration.getProperty("pay.secretKey", "");

	public static String payCallback(String orderId, int status) {

		Map<String, String> paramMap = new TreeMap<String, String>();
		paramMap.put("order_id", orderId);
		paramMap.put("status", String.valueOf(status));
		paramMap.put("busi_code", appKey);
		
		String vcode = ChecksumHelper.getChecksum(paramMap, secretKey);
		Long t = System.currentTimeMillis();

		String query_string = "?order_id=" + orderId + "&status=" + status + "&busi_code=" + appKey + "&vcode=" + vcode + "&t=" + t;

		Request request = new Request("payCenterService", "payCallback", query_string);
		if (Logger.isDebugEnabled()) {
			Logger.debug("[payCenterService.%s] request append params - %s", "payCallback", query_string);
		}
		String result = "";
		try {
			Response response = HTTP.GET(request);
			result = response.getContent();
			Logger.info("调用支付中心payCallback返回=%s", result);
		} catch (Exception e) {
			Logger.error("调用支付中心payCallback异常:", e.getMessage());
		}
		if (StringUtils.isBlank(result)) {
			Logger.error("调用支付中心payCallback返回内容为空");
			return null;
		}
		return result;
	}

	/**
	 * 订单退款
	 * 
	 * @param orderId 订单号
	 * @param buyerRefundAmount 等于0 则全额退款，大于0则以该数值退款给买家，剩余支付給卖家
	 * @return
	 */
	public static String orderRefund(String orderId, String buyerRefundAmount) {
		Map<String, String> paramMap = new TreeMap<String, String>();
		paramMap.put("order_id", orderId);
		paramMap.put("buyerRefundAmount", buyerRefundAmount);
		paramMap.put("busi_code", appKey);
		
		String secretKey = Jws.configuration.getProperty("pay.order_refund.secretKey","pay_h5game@20161214");
		String caller = Jws.configuration.getProperty("pay.order_refund.caller", "pay_h5game");
		
		String vcode = ChecksumHelper.getChecksum(paramMap, caller,secretKey);
		String query_string = "?order_id=" + orderId + "&buyerRefundAmount=" + buyerRefundAmount + "&busi_code=" + appKey + "&sign=" + vcode + "&caller=" + caller;

		Request request = new Request("payCenterService", "orderRefund", query_string);
		if (Logger.isDebugEnabled()) {
			Logger.debug("[payCenterService.%s] request append params - %s", "orderRefund", query_string);
		}
		
		String result = "";
		try {
			Response response = HTTP.GET(request);
			result = response.getContent();
			Logger.info("调用支付中心orderRefund返回=%s", result);
		} catch (Exception e) {
			Logger.error("调用支付中心orderRefund异常:", e.getMessage());
		}
		if (StringUtils.isBlank(result)) {
			Logger.error("调用支付中心orderRefund返回内容为空");
			return null;
		}
		return result;
	}

}
