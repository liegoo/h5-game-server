package moudles.wechat;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import jws.Jws;
import jws.Logger;
import jws.module.constants.guess.GameLevel;
import jws.module.response.wechat.TemplateMessageDto;

import org.apache.commons.lang3.StringUtils;

import utils.DateUtil;

import com.google.gson.Gson;

import constants.wechat.GuessMsgType;
import constants.wechat.WechatConstants;
import exception.BusinessException;
import externals.account.AccountCenterService;
import externals.web.WebPlatformService;

public class WeChatService {

	

	/**
	 * 交易类型 公众号消息 TODO 消息类型待扩展,目前仅支持充值类型提醒
	 * 
	 * @param uid
	 * @param bean
	 * @param amount
	 */
	public static void sendTradeMsg(int uid, int bean, double amount) {
		String firstLine = "";
		String remarkLine = "";
		String url = "";
		String color = "#173177";
		String templateType = WechatConstants.TRADE_TEMPLATE;
		String baseUrl = Jws.configuration.getProperty("h5game.web.inex", "http://game.8868.cn/");

		List<LineContentDto> content = new ArrayList<LineContentDto>();

		LineContentDto k1 = new LineContentDto();
		k1.setContent(bean + "开心豆");

		LineContentDto k2 = new LineContentDto();
		k2.setContent("在线");

		LineContentDto k3 = new LineContentDto();
		k3.setContent(amount + "元");
		k3.setColor(color);

		LineContentDto k4 = new LineContentDto();
		k4.setContent(DateUtil.getDateString(System.currentTimeMillis()));

		content.add(k1);
		content.add(k2);
		content.add(k3);
		content.add(k4);

		firstLine = "您已完成开心豆充值，快去赢大奖！\n";
		remarkLine = "\n【福利】iphone等大奖等你来拿，速戳详情！";
		
		// 可配置remark
		String configRemark = Jws.configuration.getProperty("wechat.common.msg_remark");
		if(StringUtils.isNotEmpty(configRemark)){
			remarkLine = configRemark;
		}
		

		Logger.info("sendGuessMsg,uid:%s", uid);
		try {
			baseUrl = baseUrl.concat("?channel=wxtz");
			sendTemplateMessage(uid, firstLine, remarkLine, content, color, templateType, baseUrl);
		} catch (BusinessException e) {
			Logger.error(e.getMessage());
		}
	}

	/**
	 * 夺宝相关 公众号消息
	 * 
	 * @param uid
	 * @param type
	 * @param seasonId
	 * @param seasonNum
	 * @param gameLevel
	 * @param bean
	 */
	public static void sendGuessMsg(int uid, int type, int seasonId, int seasonNum, int gameLevel, int bean) {
		String firstLine = "";
		String remarkLine = "";
		String url = "";
		String templateType = WechatConstants.SERVICE_NOTICE_TEMPLATE;
		String baseUrl = Jws.configuration.getProperty("h5game.web.inex", "http://game.8868.cn/");

		String gameLevelStr = "";
		if(gameLevel != 0){
			gameLevelStr = GameLevel.getGameLevel(gameLevel).getDesc();
		}

		List<LineContentDto> content = new ArrayList<LineContentDto>();
		LineContentDto k1 = new LineContentDto();
		k1.setContent("疯狂夺宝");

		LineContentDto k2 = new LineContentDto();
		if (type == GuessMsgType.JOIN.getType()) {
			firstLine = String.format("恭喜您成功参与【疯狂夺宝】【%s-%s期】，请关注后续结果通知，祝您中大奖！\n", gameLevelStr, seasonNum);
			k2.setContent("已参与");
			remarkLine = "\n为避免骚扰，本期后续参与不再提醒。";
			url = baseUrl;
		} else if (type == GuessMsgType.NOT_HIT.getType()) {
			firstLine = String.format("很遗憾，【疯狂夺宝】【%s-%s期】未中奖，再接再厉，下个幸运儿就是你！\n", gameLevelStr, seasonNum);
			k2.setContent("已开奖");
			remarkLine = "\n【福利】玩夺宝，iphone等丰厚大奖等你拿，速戳详情！";
			url = baseUrl + "Guess/season?sid=" + seasonId;
		} else if (type == GuessMsgType.HIT.getType()) {
			firstLine = String.format("恭喜赢得【疯狂夺宝】【%s-%s期】大奖！本期奖励%s开心豆，手气这么好，再玩几局换大奖吧！\n", gameLevelStr, seasonNum, bean);
			k2.setContent("已中奖");
			k2.setColor("#173177");
			remarkLine = "\n【福利】玩夺宝，iphone等丰厚大奖等你拿，速戳详情！";
			url = baseUrl + "Guess/season?sid=" + seasonId;
		} else if (type == GuessMsgType.AWARD_BEAN.getType()) {
			// TODO 次日送豆 此版本暂不实现
			firstLine = "";
			k2.setContent("");
			remarkLine = "";
			url = baseUrl;
		} else if (type == GuessMsgType.GUESS_AWARD_BEAN.getType()) {
			firstLine = String.format("恭喜获得【疯狂夺宝】官方鼓励金，本次奖励%s开心豆（7天未使用系统将自动回收）！你夺宝，我买单，累积投入达到限额（见官方活动说明）即可获得鼓励金，快去一展身手吧！\n", bean);
			k2.setContent("已发放");
			remarkLine = "\n疯狂夺宝-官方鼓励金";
			url = baseUrl;
		}
		
		// 可配置remark
		String configRemark = Jws.configuration.getProperty("wechat.common.msg_remark");
		if(StringUtils.isNotEmpty(configRemark)){
			remarkLine = configRemark;
		}
		
		// 标注微信渠道
		if(url.indexOf("?")>0){
			url = url.concat("&channel=wxtz");
		}else{
			url = url.concat("?channel=wxtz");
		}

		content.add(k1);
		content.add(k2);

		Logger.info("sendGuessMsg,uid:%s", uid);
		try {
			sendTemplateMessage(uid, firstLine, remarkLine, content, "", templateType, url);
		} catch (BusinessException e) {
			Logger.error(e.getMessage());
		}

	}

	public static void sendTemplateMessage(int uid, String firstLine, String remarkLine, List<LineContentDto> contents, String color, String templateType, String url)
			throws BusinessException {

		if (uid == 0) {
			Logger.error("Uid为零，不发公众号消息.");
			return;
		}

		if (StringUtils.isEmpty(color)) {
			color = "#173177";
		}

		if (StringUtils.isEmpty(url)) {
			url = Jws.configuration.getProperty("h5game.web.inex", "http://game.8868.cn/");
		}

		if (StringUtils.isEmpty(templateType)) {
			templateType = WechatConstants.TRADE_TEMPLATE;
		}

		String openId = "";
		if (uid != 0) {
			openId = AccountCenterService.queryOpendIdByUid(uid);
		}

		if (StringUtils.isEmpty(openId)) {
			Logger.warn("uid:%s openId is null,不发送微信公众号消息", uid);
			return;
		}

		List<TemplateMessageDto> data = new ArrayList<TemplateMessageDto>();

		TemplateMessageDto first = new TemplateMessageDto();
		first.setKey("first");
		first.setValue(firstLine);
		data.add(first);

		int i = 1;
		for (LineContentDto line : contents) {
			TemplateMessageDto msg = new TemplateMessageDto();
			msg.setKey("keyword" + i);
			if (StringUtils.isNotBlank(line.getColor())) {
				msg.setColor(line.getColor());
			}
			msg.setValue(line.getContent());
			data.add(msg);
			i++;
		}

		
		TemplateMessageDto remark = new TemplateMessageDto();
		remark.setKey("remark");
		remark.setValue(remarkLine);
		remark.setColor(color);
		data.add(remark);
		
		
		WebPlatformService.sendTemplateMessage(openId, templateType, url, new Gson().toJson(data));
	}
}
