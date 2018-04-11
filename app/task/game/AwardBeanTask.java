package task.game;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jws.Jws;
import jws.Logger;
import jws.module.constants.member.MemberLogOpType;
import jws.module.constants.task.GameTaskAwardStatus;
import moudles.member.service.MemberService;
import moudles.task.ddl.GameTaskRecordDDL;
import moudles.task.service.GameTaskRecordService;
import moudles.wechat.LineContentDto;
import moudles.wechat.WeChatService;
import utils.DateUtil;

import com.google.gson.Gson;

import common.task.Task;
import constants.wechat.WechatConstants;
import exception.BusinessException;

/**
 * 
 * 做任务送豆 定时任务
 * 
 * @author Coming
 */
public class AwardBeanTask extends Task {

	@Override
	public void run() {

		Logger.info("AwardBeanTask -- Begin Award Bean Task ~~~");
		
		int awardStatus = GameTaskAwardStatus.TO_AWARD.getStatus();
		long timeBegin = DateUtil.getYesterdayStartEndTime()[0];
		long timeEnd = DateUtil.getYesterdayStartEndTime()[1];
		
		List<GameTaskRecordDDL> records = GameTaskRecordService.list(0, 0, 0, awardStatus, timeBegin, timeEnd, 0, -1);
		
		for (GameTaskRecordDDL record : records) {
			// 已完成
			if (record.getCompleteNum() >= record.getTarget()) {

				boolean updateRecordFlag = false;
				boolean awardFlag = awardBean(record);

				record.setAwardStatus(GameTaskAwardStatus.AWARDED.getStatus());
				record.setUpdateTime(System.currentTimeMillis());

				if (awardFlag) {
					updateRecordFlag = GameTaskRecordService.updateRecord(record);
				}

				if (updateRecordFlag) {
					Logger.info("AwardBeanTask -- 更新任务记录成功");
				} else {
					Logger.error("AwardBeanTask -- 更新任务记录失败,record:%s", new Gson().toJson(record));
				}
			}
		}

		Logger.info("AwardBeanTask -- End Award Bean Task ~~~");
	}

	/**
	 * 发放开心豆
	 * 
	 * @param record
	 * @return
	 */
	private boolean awardBean(GameTaskRecordDDL record) {
		String lockKey = "AwardBeanTask_awardBean_" + record.getUid() + "_" + record.getId(); 
		Map params = new HashMap();
		params.put("remark", record.getRemark());
		params.put("gameId", String.valueOf(record.getGameId()));
		int type = MemberLogOpType.TASK_AWARD.getType();
		boolean addBeanFlag = MemberService.addBean(record.getUid(), record.getAwardNum(), type, params, lockKey);
		if (addBeanFlag) {
			sendWechatMsg(record);
		}
		return addBeanFlag;
	}

	/**
	 * 发送公众号消息
	 * 
	 * @param record
	 */
	private static void sendWechatMsg(GameTaskRecordDDL record) {
		int uid = record.getUid();
		int happyBean = record.getAwardNum();
		String remark = record.getRemark();
		int expire = Integer.parseInt(Jws.configuration.getProperty("doll_game.present_bean.expire"));
		long completedTime = record.getUpdateTime();

		String firstLine = String.format("您获赠的开心豆发货啦，快去赢大奖！（%d天内未使用将自动回收）", expire);
		String remarkLine = "【福利】iphone等大奖等你来拿，速戳详情！";
		String templateType = WechatConstants.AWARD_SUCCESS_TEMPLATE;
		String baseUrl = Jws.configuration.getProperty("h5game.web.inex", "http://game.8868.cn/");

		List<LineContentDto> content = new ArrayList<LineContentDto>();
		LineContentDto k1 = new LineContentDto();
		k1.setContent(remark);

		LineContentDto k2 = new LineContentDto();
		k2.setColor("#173177");
		k2.setContent(String.format("%d开心豆", happyBean));

		LineContentDto k3 = new LineContentDto();
		k3.setContent(DateUtil.formatDate(completedTime, "yyyy-MM-dd HH:mm:ss"));

		LineContentDto k4 = new LineContentDto();
		k4.setContent(DateUtil.formatDate(System.currentTimeMillis(), "yyyy-MM-dd HH:mm:ss"));

		content.add(k1);
		content.add(k2);
		content.add(k3);
		content.add(k4);

		Logger.info("AwardBeanTask.sendWechatMsg,uid:%s", uid);
		try {
			WeChatService.sendTemplateMessage(uid, firstLine, remarkLine, content, "", templateType, baseUrl);
		} catch (BusinessException e) {
			Logger.error(e.getMessage());
		}
	}
}
