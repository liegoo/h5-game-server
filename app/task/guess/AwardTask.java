package task.guess;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import jws.Jws;
import jws.Logger;
import jws.module.constants.guess.GameLevel;
import jws.module.constants.guess.GuessHitStatus;
import jws.module.constants.guess.GuessRecordStatus;
import jws.module.constants.member.MemberLogOpType;
import jws.module.response.guess.GuessPlayerDto;
import jws.module.response.guess.ListPlayersResp;
import moudles.guess.ddl.GuessAwardDDL;
import moudles.guess.ddl.GuessBoobyRecordDDL;
import moudles.guess.ddl.GuessRecordDDL;
import moudles.guess.ddl.GuessSeasonCurrentDDL;
import moudles.guess.ddl.GuessSeasonDDL;
import moudles.guess.service.GuessAwardService;
import moudles.guess.service.GuessBoobyRecordService;
import moudles.guess.service.GuessRecordService;
import moudles.guess.service.GuessRecordTempService;
import moudles.guess.service.GuessSeasonCurrentService;
import moudles.guess.service.GuessSeasonService;
import moudles.member.ddl.MemberDDL;
import moudles.member.service.MemberService;
import moudles.wechat.WeChatService;

import org.apache.commons.lang3.StringUtils;

import constants.SelfGame;
import constants.wechat.GuessMsgType;

public class AwardTask {

	private static ExecutorService executor = Executors.newFixedThreadPool(4);

	/**
	 * 创建定时开奖任务
	 * 
	 * @param season
	 * @param delay
	 */
	protected static void createTask(final int seasonId, long delay) {

		if (Logger.isDebugEnabled()) {
			Logger.debug("AwardTask.startTask - Start a season,id:%s", seasonId);
		}

		final Timer timer = new Timer();
		timer.schedule(new TimerTask() {
			@Override
			public void run() {
				try {
					// 生成夺宝码
					GuessSeasonDDL season = GuessSeasonService.getById(seasonId);
					int code = generateCode(season.getId(), season.getGameLevel());

					// 更新本期信息
					if (!updateSeason(season, code)) {
						Logger.error("PublishTask - Update season failed");
					}

					// 更新本期夺宝记录
					updateGuessRecord(season.getId(), code, season.getGuessAwardId());

					// 删除temp 记录
					GuessRecordTempService.deleteBySeasonId(seasonId);

					// 结束本期任务
					this.cancel();
					if (Logger.isDebugEnabled()) {
						Logger.debug("PublishTask - season finished, seasonId:%s", season.getId());
					}

					// 开新局
					SeasonTask.getInstance().createSeason(season.getGameLevel());
					timer.cancel();
				} catch (Exception e) {
					Logger.error("AwardTask.run, err:%s", e.getMessage());
				}
			}
		}, delay);

	}

	/**
	 * 
	 * 生成中奖码
	 * 
	 * @param seasonId
	 * @param gameLevel
	 * @return
	 */
	private static int generateCode(int seasonId, int gameLevel) {

		ListPlayersResp playerList = GuessRecordService.listPlayers(seasonId, gameLevel, true, 1, 1000);
		if (playerList == null || playerList.getList() == null) {
			Logger.error("AwardTask.generateCode - 本期参与用户为空,seasonId:%s", seasonId);
			return -1;
		}

		Long sumTime = 0L;
		int lastCode = GuessRecordService.getLastCodeBySeason(seasonId);
		if (lastCode == 0) {
			Logger.warn("Last code is 0,seasonId:%d", seasonId);
			lastCode = 1;
		}

		String codePrefix = "8868";
		for (GuessPlayerDto player : playerList.getList()) {
			// sumTime += player.getMs();
			int ms = Integer.valueOf(codePrefix + player.getMs());
			sumTime += ms;
		}
		long code = (sumTime % lastCode) + 1;

		if (Logger.isDebugEnabled()) {
			Logger.debug("AwardTask.generateCode - sumTime:%s,lastCode:%s,code:%s", sumTime, lastCode, code);
		}
		return new Long(code).intValue();
	}

	/**
	 * 更新本期信息
	 * 
	 * @param season
	 * @param code
	 * @return
	 */
	private static boolean updateSeason(GuessSeasonDDL season, int code) {

		if (season == null) {
			return false;
		}

		int uid = 0;

		GuessRecordDDL record = GuessRecordService.getByCode(season.getId(), code);

		// 没人中奖
		if (record == null) {
			Logger.error("AwardTask.updateSeason - Record is null.");
			uid = -1;
		} else {
			uid = record.getUid();
		}

		season.setStatus(2);
		season.setCode(code);
		season.setWinnerUid(uid);
		season.setPublishTime(System.currentTimeMillis());
		season.setUpdateTime(System.currentTimeMillis());

		if (!GuessSeasonService.update(season)) {
			Logger.error("AwardTask.updateSeason - Update season fail.");
			return false;
		}

		return true;
	}

	/**
	 * 更新夺宝记录
	 * 
	 * @param seasonId
	 * @param code
	 */
	private static void updateGuessRecord(final int seasonId, int code, int awardId) {

		int luckyUid = 0;
		int happyBean = 0;
		int deductBean = 0; // 系统抽成

		final GuessSeasonDDL season = GuessSeasonService.getById(seasonId);
		if (season == null) {
			Logger.error("AwardTask.updateGuessRecord - Season is null.");
			return;
		}

		List<GuessRecordDDL> recordList = GuessRecordService.list(0, seasonId, 0, 0, -1, -1, 0, 0, 0, -1);
		final Map<String, Boolean> hitMap = new HashMap<String, Boolean>();

		for (GuessRecordDDL record : recordList) {
			String key = seasonId + "-" + record.getUid();
			List<String> codes = Arrays.asList(record.getCode().split(","));

			// 中奖记录
			if (codes.contains(String.valueOf(code))) {
				luckyUid = record.getUid();
				record.setWinningCode(code);
				// 中奖
				record.setHit(GuessHitStatus.YES.getValue());

				// 全额奖励
				happyBean = season.getJackpot();

				// 官方加豆
				int raiseBean = getRaiseBean(record.getGameLevel());
				if (raiseBean > 0) {
					record.setRaiseBean(raiseBean);
					happyBean += raiseBean;
					season.setRaiseBean(raiseBean);
				}

				season.setJackpot(happyBean);
				if (!GuessSeasonService.update(season)) {
					Logger.error("AwardTask.updateGuessRecord -- update season jackpot and raiseBean failed.");
				}

				
				// 设置系统抽成
				double deductRate = Double.valueOf(Jws.configuration.getProperty("guess_game.deduct_bean.rate", "0"));
				if (deductRate <= 0) {
					Logger.warn("guess_game.deduct_bean.rate not set.");
				}else{
					Logger.info("guess_game.deduct_bean.rate %s",deductRate);
				}
				int inputBean = GuessRecordService.getInputBeanBySeason(record.getUid(), seasonId);
				record.setInputBean(inputBean); 
				
				deductBean = (int) ((happyBean - inputBean) * deductRate);
				record.setDeductBean(deductBean);

				// 给真实用户加豆
				if (record.getIsRobot() == 0) {
					Map params = new HashMap();
					// 发放开心豆
					params.put("remark", "夺宝中奖-" + GameLevel.getGameLevel(record.getGameLevel()).getDesc());
					params.put("gameId", String.valueOf(SelfGame.GAME_GUESS.getGameId()));
					String awardBeanLockKey = String.format("gid_%d-act_%s-uid_%d-rid_%d", SelfGame.GAME_GUESS.getGameId(), "AwardBean", record.getUid(),record.getId());
					boolean flag = MemberService.addBean(record.getUid(), happyBean, MemberLogOpType.GAME_AWARD.getType(), params, awardBeanLockKey);
					if (flag) {
						hitMap.put(key, true);
						Logger.info(
								"AwardTask.updateGuessRecord - Add bean success, user: %s, hit bean: %s at season: %s, gameLevel is: %s and awardId is: %s,happyBean:%s,raiseBean:%s",
								record.getUid(), happyBean, record.getSeasonNum(), record.getGameLevel(), record.getGuessAwardId(), happyBean, raiseBean);
					} else {
						Logger.error(
								"AwardTask.updateGuessRecord - Add bean failed, user: %s, hit bean: %s at season: %s, gameLevel is: %s and awardId is: %s,happyBean:%s,raiseBean:%s",
								record.getUid(), happyBean, record.getSeasonNum(), record.getGameLevel(), record.getGuessAwardId(), happyBean, raiseBean);
					}

					// 系统抽成
					if (deductBean > 0) {
						
						params.clear();
						params.put("opType", MemberLogOpType.SYS_INCEPT.getType());
						params.put("gameId", SelfGame.GAME_GUESS.getGameId());
						params.put("remark", "夺宝中奖-投入公共奖池");
						String consumeBeanLockKey = String.format("gid_%d-gl_%d-act_%s-uid_%d", SelfGame.GAME_GUESS.getGameId(), record.getGameLevel(), "GuessDeduct",
								record.getUid());
						boolean deductFlag = MemberService.consume(luckyUid, deductBean, params, consumeBeanLockKey);
						if (deductFlag) {
							Logger.info("夺宝抽成成功,uid:%d ,deductBean:%d ,recordId:%d ,seasonId:%d", record.getUid(), deductBean, record.getId(), record.getGuessSeasonId());
						} else {
							Logger.error("夺宝抽成失败,uid:%d ,deductBean:%d ,recordId:%d ,seasonId:%d", record.getUid(), deductBean, record.getId(), record.getGuessSeasonId());
						}
					}
				}

				record.setRewardBean(happyBean);

			} else {
				if (record.getIsRobot() == 0) {
					if (hitMap.containsKey(key)) {
						if (!hitMap.get(key)) {
							hitMap.put(key, false);
						}
					} else {
						hitMap.put(key, false);
					}
				}
				record.setHit(GuessHitStatus.NO.getValue());
				record.setRewardBean(0);
				record.setWinningCode(0);
			}

			record.setStatus(GuessRecordStatus.PUBLISHED.getValue()); // 已开奖
			record.setUpdateTime(System.currentTimeMillis());
			boolean result = GuessRecordService.update(record);

			if (!result) {
				Logger.error("AwardTask.updateGuessRecord - update guess record failed.");
			}
		}

		// 更新or发放安慰奖
		awardOrUpdateBooby(seasonId, awardId, luckyUid);

		// 发公众号消息　
		Runnable task = new Runnable() {
			@Override
			public void run() {
				for (Map.Entry<String, Boolean> entry : hitMap.entrySet()) {
					int msgType = 2;
					try {
						int uid = Integer.valueOf(entry.getKey().split("-")[1]);
						if (entry.getValue()) {
							msgType = 3;
						}
						WeChatService.sendGuessMsg(uid, msgType, seasonId, season.getSeasonNum(), season.getGameLevel(), season.getJackpot());
					} catch (Exception e) {
						Logger.error("AwardTask.updateGuessRecord -- %s", e.getMessage());
					}
				}
			}
		};
		executor.submit(task);
	}

	// 更新or发放安慰奖
	private static boolean awardOrUpdateBooby(int seasonId, int awardId, int luckyUid) {

		if (Boolean.valueOf(Jws.configuration.getProperty("guess_game.booby.enabled", "false"))) {
			if (Logger.isDebugEnabled()) {
				Logger.debug("AwardTask.awardOrUpdateBooby - do ~~ ---");
			}
		} else {
			return false;
		}

		GuessAwardDDL award = GuessAwardService.getById(awardId);
		GuessSeasonDDL season = GuessSeasonService.getById(seasonId);
		if (award == null || season == null) {
			Logger.error("AwardTask.awardOrUpdateBooby - season or award is null");
			return false;
		}

		if (award.getBoobyRate() == 0) {
			Logger.warn("AwardTask.awardOrUpdateBooby - award booby rate is 0");
			return false;
		}

		List<GuessBoobyRecordDDL> boobyList = GuessBoobyRecordService.listBoobyRecord(awardId, award.getBoobyPrize());
		GuessRecordDDL record = new GuessRecordDDL();

		// 发放安慰奖
		for (GuessBoobyRecordDDL booby : boobyList) {

			// 中奖用户不发放安慰奖
			if (booby.getUid() == luckyUid) {
				continue;
			}

			double happyBean = award.getBoobyRate() * booby.getHappyBean();
			record.setCode("-1");
			record.setOrderId(""); // 安慰奖不设置订单号
			record.setGameLevel(season.getGameLevel());
			record.setSeasonNum(season.getSeasonNum());
			record.setGuessAwardId(awardId);
			record.setGuessSeasonId(seasonId);
			record.setHappyBean(0);
			record.setStatus(2);
			record.setIsBooby(1);
			record.setCodeAmount(1);
			record.setHit(1);
			record.setRewardBean(new Double(happyBean).intValue());
			record.setUid(booby.getUid());
			record.setUpdateTime(System.currentTimeMillis());
			record.setCreateTime(System.currentTimeMillis());
			MemberDDL member = MemberService.getMemberByUid(booby.getUid());
			String nickname = "";
			if (member != null) {
				nickname = StringUtils.isEmpty(member.getNickName()) ? member.getMobile() : member.getNickName();
			} else {
				Logger.error("AwardTask.awardOrUpdateBooby - User not found, uid:%s", booby.getUid());
			}
			record.setNickname(nickname);

			boolean flag = GuessRecordService.create(record);
			if (flag) {
				Map params = new HashMap();
				params.put("remark", "夺宝游戏-" + GameLevel.getGameLevel(record.getGameLevel()).getDesc() + "-安慰奖");
				params.put("gameId", String.valueOf(SelfGame.GAME_GUESS.getGameId()));
				String boobyLockKey = String.format("gid_%d-act_%s-uid_%d", SelfGame.GAME_GUESS.getGameId(), "GuessBoobyPrice", record.getUid());
				int rewardHappyBean = new Double(happyBean).intValue();
				int maxRewardHappyBean = Integer.parseInt(Jws.configuration.getProperty("guess.award.max.beans"));
				if(rewardHappyBean > maxRewardHappyBean){// 限制赠送的开心豆
					rewardHappyBean = maxRewardHappyBean;
				}
				flag = MemberService.addBean(record.getUid(), rewardHappyBean, MemberLogOpType.GAME_BOOBY.getType(), params, boobyLockKey);
				if (flag) {
					Logger.info("AwardTask.awardOrUpdateBooby Success - User: %s, got booby prize, bean: %s at season: %s, gameLevel is: %s and awardId is: %s", record.getUid(),
							happyBean, record.getSeasonNum(), record.getGameLevel(), record.getGuessAwardId());
					sendWXMsg(member.getUid(), rewardHappyBean);
				} else {
					Logger.error("AwardTask.awardOrUpdateBooby failed - User: %s, got booby prize, bean: %s at season: %s, gameLevel is: %s and awardId is: %s", record.getUid(),
							happyBean, record.getSeasonNum(), record.getGameLevel(), record.getGuessAwardId());
				}

			} else {
				Logger.error("AwardTask.awardOrUpdateBooby - award or booby update failed");
			}
		}

		// 个人安慰奖奖池置零
		List<GuessRecordDDL> recordList = GuessRecordService.list(0, seasonId, 0, 0, 1, -1, 0, 0, 0, -1);
		for (GuessRecordDDL rec : recordList) {
			GuessBoobyRecordDDL booby = GuessBoobyRecordService.getByAwardId(rec.getUid(), rec.getGuessAwardId());
			if (booby == null) {
				continue;
			}
			booby.setHappyBean(0);
			booby.setUpdateTime(System.currentTimeMillis());
			GuessBoobyRecordService.createOrUpdate(booby);
		}
		return true;
	}
	
	/**
	 * 发送微信公众号消息（赠送豆）
	 */
	private static void sendWXMsg(final int uid, final int beans){
		// 发公众号消息　
		executor.submit(new Runnable() {
			@Override
			public void run() {
				WeChatService.sendGuessMsg(uid, GuessMsgType.GUESS_AWARD_BEAN.getType(), 0, 0, 0, beans);
			}
		});
	}

	// 获取官方加豆规则
	private static int getRaiseBean(int gameLevel) {

		GuessSeasonCurrentDDL curSeason = GuessSeasonCurrentService.get(gameLevel, 0);
		if (curSeason == null) {
			return 0;
		}

		String raiseRuleStr = curSeason.getRaiseRule();
		if (StringUtils.isEmpty(raiseRuleStr)) {
			return 0;
		}
		String ruleList[] = raiseRuleStr.split(",");
		try {
			for (int i = ruleList.length - 1; i >= 0; i--) {
				String str = ruleList[i];
				String ruleMap[] = str.split("/");
				int target = Integer.parseInt(ruleMap[0]);
				int award = Integer.parseInt(ruleMap[1]);
				if (curSeason.getCurrentBean() >= target) {
					return award;
				}
			}
		} catch (Exception e) {
			Logger.error(e.getMessage());
		}
		return 0;
	}


}
