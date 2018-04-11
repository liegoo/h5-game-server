package task.guess;

import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import jws.Jws;
import jws.Logger;
import moudles.guess.ddl.GuessAwardDDL;
import moudles.guess.ddl.GuessSeasonCurrentDDL;
import moudles.guess.ddl.GuessSeasonDDL;
import moudles.guess.model.GuessRobotConfig;
import moudles.guess.service.GuessAwardService;
import moudles.guess.service.GuessSeasonCurrentService;
import moudles.guess.service.GuessSeasonService;
import moudles.robot.ddl.RobotInfoDDL;
import moudles.robot.service.RobotService;

import org.apache.commons.lang.StringUtils;

import utils.FkdbUtil;

import com.google.gson.Gson;

import exception.BusinessException;

public class RobotTask {

	/**
	 * 创建机器人任务
	 * 
	 * @param season
	 * @param award
	 */

	private static final Timer timer = new Timer();

	protected static void createTask(final GuessSeasonDDL season, final GuessAwardDDL award, boolean firstBuy) {

		if (season == null) {
			Logger.error("RobotTask.createRobot - Season is null");
			return;
		}

		if (award == null) {
			Logger.error("RobotTask.createRobot - Award is null");
			return;
		}

		boolean enableRobot = RobotService.count(1) > 0;
		if (!enableRobot) {
			Logger.error("RobotTask.createRobot - Robot list is empty.");
			return;
		}
		final GuessSeasonCurrentDDL curSeason = GuessSeasonCurrentService.get(season.getGameLevel(), 0);
		if (curSeason == null) {
			Logger.error("RobotTask.createRobot - Current season is null.");
			return;
		}

		GuessAwardDDL curAward = GuessAwardService.getById(award.getId());

		GuessRobotConfig curCfg = getCurrentConfig(curAward);

		if (curCfg == null) {
			return;
		}

		// 机器人睡眠时间
		long delay = getDelay(curCfg);
		
		// 第一个机器人随机睡眠2-4秒
		if(firstBuy){
			delay = (long) (Math.random() * 3 + 2) * 1000;
		}

		// 最大购买数量
		final int max = curCfg.getMax();

		timer.schedule(new TimerTask() {
			@Override
			public void run() {
				try {
					// 购买夺宝码
					buyCode(curSeason, award, max);
				} catch (Exception e) {
					Logger.error("RobotTask.run,err:%s", e.getMessage());
				}
			}
		}, delay);
	}

	private static <T> List<T> stringToArray(String s, Class<T[]> clazz) {
		T[] arr = new Gson().fromJson(s, clazz);
		return Arrays.asList(arr);
	}

	/**
	 * 获取当前机器人配置
	 * 
	 * @param award
	 * @return
	 */
	protected static GuessRobotConfig getCurrentConfig(GuessAwardDDL award) {
		if (award == null) {
			return null;
		}
		String configStr = award.getRobotConfig();
		if (StringUtils.isEmpty(configStr)) {
			return null;
		}

		List<GuessRobotConfig> configs = stringToArray(configStr, GuessRobotConfig[].class);

		Calendar cal = Calendar.getInstance();
		int hour = cal.get(Calendar.HOUR_OF_DAY);
		for (GuessRobotConfig cfg : configs) {
			if (cfg.getTimeBegin() <= hour && cfg.getTimeEnd() >= hour) {
				return cfg;
			}
		}
		return null;
	}

	/**
	 * 获取随机睡眠时间
	 * 
	 * @param curCfg
	 * @return
	 */
	private static int getDelay(GuessRobotConfig curCfg) {
		int t = 0;
		if (curCfg == null) {
			return 0;
		}
		t = (int) (Math.random() * (curCfg.getRandSleepEnd() - curCfg.getRandSleepBegin() + 1)) + curCfg.getRandSleepBegin();
		t = t * 1000;
		return t;
	}

	/**
	 * 购买夺宝码
	 * 
	 * @param season
	 * @param award
	 * @param timerTask
	 */
	private static void buyCode(GuessSeasonCurrentDDL curSeason, GuessAwardDDL award, int max) {

		if (curSeason == null) {
			Logger.error("curSeason is null.");
			return;
		}

		GuessSeasonDDL season = GuessSeasonService.getCurrentSeason(curSeason.getGameLevel(), award.getId());

		if (season == null) {
			Logger.error("Season is null.");
			return;
		}

		// 判断本期是否已到期,开奖前1秒停止购买
		boolean expired = System.currentTimeMillis() > curSeason.getPublishTime() - 1000;
		if (expired) {
			Logger.info("RobotTask.buyCode - 本期已到期,购买夺宝码失败, seasonNum:%s,gameLevel:%s", season.getSeasonNum(), season.getGameLevel());
			return;
		}

		int buyNum = 0;
		int gameLevel = season.getGameLevel();

		buyNum = FkdbUtil.getRandomNum(gameLevel);

		if (buyNum <= 0 || buyNum > max) {
			FkdbUtil.setRandomCopies(gameLevel, max);
			buyNum = FkdbUtil.getRandomNum(gameLevel);
		}
		if (buyNum > max) {
			buyNum = 1;
		}

		String betStr = Jws.configuration.getProperty("guess_game.betting.list", "1,2,10,50");
		String betArr[] = betStr.split(",");

		for (int i = 0; i < betArr.length; i++) {
			if (buyNum == 0) {
				buyNum = 1;
				break;
			}

			Integer bet = Integer.valueOf(betArr[i]);
			if (bet == buyNum) {
				break;
			}

			if (i < betArr.length - 1) {
				if (buyNum > bet && buyNum < Integer.valueOf(betArr[i + 1])) {
					buyNum = bet;
					break;
				}
			}

			if (i == betArr.length - 1) {
				buyNum = Integer.valueOf(betArr[betArr.length - 1]);
			}
		}

		RobotInfoDDL robot = RobotService.getRandRobot();
		if (robot == null) {
			Logger.error("RobotTask.buyCode - 购买夺宝码失败,找不到机器人");
			return;
		}

		int uid = robot.getUid();
		Map result = new HashMap();
		try {
			result = GuessSeasonService.buyCode(uid, true, season, award, buyNum, false, 0, "", "", null, false);
		} catch (BusinessException e) {
			Logger.error(e.getMessage());
			return;
		}

		if (result != null && result.containsKey("result") && result.get("result").equals("SUCCESS")) {
			if (Logger.isDebugEnabled()) {
				Logger.debug("RobotTask.buyCode - 机器人购买夺宝码成功,uid:%s,buyNum:%s,seasonNum:%s,awardId:%s", uid, buyNum, season.getSeasonNum(), award.getId());
			}
		} else {
			Logger.error("RobotTask.buyCode - 机器人购买夺宝码失败,seasonNum:%s,awardId:%s", season.getSeasonNum(), season.getGuessAwardId());
		}

		// 创建购买任务
		createTask(season, award, false);
	}
}
