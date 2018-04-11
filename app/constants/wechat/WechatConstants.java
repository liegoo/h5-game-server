package constants.wechat;

import jws.Jws;

public class WechatConstants {
	
	// 微信openId
	public static final String WECHAT_OPENID_KEY = "WECHAT_OPENID_FROM_SESSION";
	
	// 交易模板ID
	public static final String TRADE_TEMPLATE = Jws.configuration.getProperty("wechat.template.trade_notice", "");
	
	//服务提醒模板
	public static final String SERVICE_NOTICE_TEMPLATE = Jws.configuration.getProperty("wechat.template.service_notice", "");
	
	//兌奖成功通知
	public static final String AWARD_SUCCESS_TEMPLATE = Jws.configuration.getProperty("wechat.template.award_success", "");
}
