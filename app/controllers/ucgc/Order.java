package controllers.ucgc;

import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import jws.Jws;
import jws.Logger;
import jws.module.constants.order.PayStatus;
import moudles.member.service.MemberService;
import moudles.order.ddl.ZhifuOrderDDL;
import moudles.order.service.OrderService;
import moudles.wechat.WeChatService;

import org.h2.util.StringUtils;

import utils.ChecksumHelper;

import common.core.UcgcController;

import exception.BusinessException;

public class Order extends UcgcController {
	private static ExecutorService executor = Executors.newFixedThreadPool(4);
	
	/**
	 * 创建订单
	 */
	public static void createOrder() throws BusinessException {
		Map params = getDTO(Map.class);
		Map<String, String> result = OrderService.createOrder(params);
		getHelper().returnSucc(result);
	}

	/**
	 * 支付回调
	 */
	public static void payCallBack() throws Exception {
		Map params = getDTO(Map.class);
		String vcode = params.get("vcode").toString();
		Map map = new TreeMap();
		for (Object key : params.keySet()) {
			if (key.toString().equals("vcode")) {
				continue;
			}
			String value = params.get(key) == null ? "" : params.get(key).toString();
			map.put(key, value);
		}

		String mycode = ChecksumHelper.getChecksum(map, Jws.configuration.getProperty("pay.secretKey"));
		if (!mycode.equals(vcode)) {
			Logger.error("pay callback vcode fail,mycode = %s,vcode=%s", mycode, vcode);
			getHelper().returnSucc(false);
		}

		boolean result = OrderService.payCallBack(Integer.parseInt(params.get("status").toString()), params.get("orderId").toString(),
				Double.parseDouble(params.get("recvAmount").toString()), Long.parseLong(params.get("complateTime").toString()), String.valueOf(params.get("remark")),
				String.valueOf(params.get("productName")));

		int payStatus = Integer.parseInt(params.get("status").toString());
		boolean paySucc = result && payStatus == PayStatus.PAYSUCC.getStatus();
		
		// 充值成功 ,发公众号通知
		if (paySucc) {
			String orderId = params.get("orderId").toString();
			double amount = Double.valueOf(params.get("recvAmount").toString());
			sendWechatMsg(orderId, amount);
			
			//通知vip加积分
			ZhifuOrderDDL zhifuOrderDDL = OrderService.getOrder(orderId);
			MemberService.addScoreForHappyBeanRecharge(zhifuOrderDDL.getUid(), zhifuOrderDDL.getAmount()/100);
		}
		getHelper().returnSucc(result);
	}
	
	// 发送公众号消息
	// 微信发公众消息接口经常出现超时情况 ,为了不影响程序的正常返回 此处使用线程
	private static void sendWechatMsg(final String orderId,final double amount){
		Runnable task = new Runnable() {
			@Override
			public void run() {
				ZhifuOrderDDL order = null;
				if (!StringUtils.isNullOrEmpty(orderId)) {
					order = OrderService.getOrder(orderId);
				}
				if (order == null) {
					Logger.warn("Order id is null, 不发公众号通知.");
					return;
				}
				WeChatService.sendTradeMsg(order.getUid(), order.getHappyBean(), amount);
			}
		};
		executor.submit(task);
	}

	/**
	 * 获取订单
	 */
	public static void getOrder() {
		Map params = getDTO(Map.class);
		String orderId = params.get("orderId").toString();
		ZhifuOrderDDL order = OrderService.getOrder(orderId);
		getHelper().returnSucc(order);
	}
}
