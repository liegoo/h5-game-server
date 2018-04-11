package task.guess;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jws.Jws;
import jws.Logger;
import jws.module.constants.member.MemberLogOpType;
import moudles.guess.ddl.GuessRecordDDL;
import moudles.guess.service.GuessRecordService;
import moudles.member.service.MemberService;
import moudles.wechat.WeChatService;
import utils.DateUtil;

import common.task.Task;

import constants.SelfGame;
import constants.wechat.GuessMsgType;

/**
 * 夺宝游戏次日送豆 定时任务
 * 
 * @author Coming
 */
public class AwardBeanTask extends Task {

	@Override
	public void run() {

		Logger.info("Begin Award Bean Task ~~~");

		long beginDate = DateUtil.getYesterdayStartEndTime()[0];
		long endDate = DateUtil.getYesterdayStartEndTime()[1];

		// 昨天参与用户
		List<GuessRecordDDL> records = GuessRecordService.listRecordByDate(beginDate, endDate);

		int happyBean = Integer.parseInt(Jws.configuration.getProperty("guess_game.award.bean", "0"));// TOOD

		if (happyBean >= 0) {
			Logger.warn("Award bean is 0 exit task ...");
			return;
		} else {
			Logger.info("Play record list size:% , award bean:%s", records.size(), happyBean);
		}

		for (GuessRecordDDL record : records) {
			Map params = new HashMap();
			params.put("remark", "夺宝游戏-参与送豆");
			params.put("gameId", String.valueOf(SelfGame.GAME_GUESS.getGameId()));
			String addBeanLockKey = String.format("gid_%d-act_%s-uid_%d", SelfGame.GAME_GUESS.getGameId(),"GuessJoinPresent",record.getUid());
			boolean result = MemberService.addBean(record.getUid(), new Double(happyBean).intValue(), MemberLogOpType.GAME_AWARD.getType(), params,addBeanLockKey);
			if (result) {
				WeChatService.sendGuessMsg(record.getUid(), GuessMsgType.AWARD_BEAN.getType(), 0, 0, 0, happyBean);
				Logger.info("Award bean success.uid:%s,bean:%s",record.getUid(),happyBean);
			} else {
				Logger.error("Award bean failed.uid:%s,bean:%s",record.getUid(),happyBean);
			}
		}

		Logger.info("End Award Bean Task ~~~");
	}

}
