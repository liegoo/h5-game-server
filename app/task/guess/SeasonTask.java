package task.guess;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import jws.Jws;
import jws.Logger;
import jws.module.constants.guess.GuessAwardStatus;
import jws.module.constants.guess.GuessSeasonStatus;
import jws.module.constants.guess.RobotStatus;
import moudles.guess.ddl.GuessAwardDDL;
import moudles.guess.ddl.GuessSeasonDDL;
import moudles.guess.model.GuessRobotConfig;
import moudles.guess.service.GuessAwardService;
import moudles.guess.service.GuessSeasonCurrentService;
import moudles.guess.service.GuessSeasonService;

import org.apache.commons.lang3.StringUtils;

import utils.FkdbUtil;

public class SeasonTask {

	private static SeasonTask instance;

	private SeasonTask() {
	}

	/**
	 * 单例
	 * 
	 * @return
	 */
	public static SeasonTask getInstance() {
		if (instance == null) {
			instance = new SeasonTask();
		}
		return instance;
	}

	// 初始化, 项目启动时被 common.core.Init 调用
	public void initSeason() {

		if (Logger.isDebugEnabled()) {
			Logger.debug("SeasonTask.initSeason - Init season ...");
		}

		List<GuessAwardDDL> awardList = GuessAwardService.list(1, 0, GuessAwardStatus.NORMAL.getValue(), 0, -1);
		for (GuessAwardDDL award : awardList) {
			int gameLevel = award.getGameLevel();
			boolean first = GuessSeasonCurrentService.countByGameLevel(gameLevel) == 0;

			// 没有开局(期)记录 ,创建第一期
			if (first) {
				GuessSeasonDDL firstSeason = new GuessSeasonDDL();
				firstSeason.setCreateTime(System.currentTimeMillis());
				firstSeason.setUpdateTime(System.currentTimeMillis());
				firstSeason.setPublishTime(null);
				firstSeason.setGuessAwardId(award.getId());
				firstSeason.setGameLevel(gameLevel);
				firstSeason.setSeasonNum(1);
				firstSeason.setStatus(GuessSeasonStatus.PROCESS.getValue());
				// 开一局游戏
				long id = GuessSeasonService.create(firstSeason);
				if (id > 0) {
					firstSeason.setId(new Long(id).intValue());

					// 加入CurrentSeason 列表
					GuessSeasonCurrentService.createOrUpdateCurrentSeason(firstSeason, award);

					// 创建并启动开奖定时任务
					AwardTask.createTask(firstSeason.getId(), award.getCountdown() * 60 * 1000);

					// 开启机器人
					if (award.getIsAuto() == RobotStatus.ENABLED.getValue()) {
						// 设置机器购买分数
						GuessRobotConfig robotCfg = RobotTask.getCurrentConfig(award);
						if (robotCfg != null) {
							FkdbUtil.setRandomCopies(gameLevel, robotCfg.getMax());
						}
						RobotTask.createTask(firstSeason, award, true);
					}

					if (Logger.isDebugEnabled()) {
						Logger.debug("SeasonTask.initSeason - Create first season success, gameLevel is:%s", gameLevel);
					}
				} else {
					Logger.error("SeasonTask.initSeason - Create first season failed, gameLevel is:%s", gameLevel);
				}
				continue;
			}

			GuessSeasonDDL season = GuessSeasonService.getCurrentSeason(gameLevel, award.getId());

			// 本期已未开奖?
			if (season != null) {
				long publishTime = season.getCreateTime() + award.getCountdown() * 60 * 1000;
				boolean expire = publishTime < System.currentTimeMillis();

				// 本期已到时,立即开奖
				if (expire) {
					Logger.error("SeasonTask.initSeason - Season has expired, id:%s" , season.getId());
					AwardTask.createTask(season.getId(), 0);
				} else {
					// 继续本期未完成的任务
					long delay = (season.getCreateTime() + award.getCountdown() * 60 * 1000) - System.currentTimeMillis();
					AwardTask.createTask(season.getId(), delay);

					// 启动机器人
					if (award.getIsAuto() == 1) {
						RobotTask.createTask(season, award, false);
					}
				}
			} else {
				// 本期已开奖,创建新的一期
				createSeason(gameLevel);
			}
		}
	}

	/**
	 * 创建新期（开局）
	 * 
	 * @param gameLevel
	 */
	protected void createSeason(int gameLevel) {

		if (Logger.isDebugEnabled()) {
			Logger.debug("SeasonTask.createSeason - Start create season ...");
		}

		List<GuessAwardDDL> awardList = GuessAwardService.list(1, gameLevel, GuessAwardStatus.NORMAL.getValue(), 0, -1);
		for (GuessAwardDDL award : awardList) {
			GuessSeasonDDL newSeason = new GuessSeasonDDL();
			GuessSeasonDDL oldSeason = GuessSeasonService.getLatestSeason(gameLevel, award.getId());
			if (oldSeason == null) {
				Logger.warn("SeasonTask.createSeason - oldSeason is null");
				continue;
			}
			int seasonNum = oldSeason.getSeasonNum() + 1;
			newSeason.setCreateTime(System.currentTimeMillis());
			newSeason.setUpdateTime(System.currentTimeMillis());
			newSeason.setPublishTime(null);
			newSeason.setGuessAwardId(award.getId());
			newSeason.setGameLevel(gameLevel);
			newSeason.setSeasonNum(seasonNum);
			newSeason.setStatus(1);
			// 开一局游戏
			long id = GuessSeasonService.create(newSeason);
			if (id > 0) {
				newSeason.setId(new Long(id).intValue());

				// 设置凌晨期
				setSpecialSeason(award);

				// 加入CurrentSeason 列表
				GuessSeasonCurrentService.createOrUpdateCurrentSeason(newSeason, award);

				// 创建并启动开奖定时任务
				AwardTask.createTask(newSeason.getId(), award.getCountdown() * 60 * 1000);

				// 启动机器人
				if (award.getIsAuto() == 1) {
					// 设置机器购买分数
					GuessRobotConfig robotCfg = RobotTask.getCurrentConfig(award);
					if (robotCfg != null) {
						FkdbUtil.setRandomCopies(gameLevel, robotCfg.getMax());
					}
					RobotTask.createTask(newSeason, award, true);
				}

				if (Logger.isDebugEnabled()) {
					Logger.debug("SeasonTask.createSeason - Create season success, gameLevel:%s,seasonNum:%s", gameLevel, seasonNum);
				}
			} else {
				Logger.error("SeasonTask.createSeason - Create season failed, gameLevel:%s,seasonNum:%s", gameLevel, seasonNum);
			}
		}
	}

	/**
	 * 设置凌晨期
	 * 
	 * @param award
	 */
	private static void setSpecialSeason(GuessAwardDDL award) {
		try {
			if (!Boolean.valueOf(Jws.configuration.getProperty("guess_game.special.season.enabled", "true"))) {
				return;
			}

			String duration = Jws.configuration.getProperty("guess_game.special.season.duration", "");
			if (StringUtils.isEmpty(duration)) {
				return;
			}

			String timeBegin = duration.split("-")[0];
			String timeEnd = duration.split("-")[1];

			SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss");

			Date dateBegin = format.parse(timeBegin);
			Date dateEnd = format.parse(timeEnd);
			Date dateCur = format.parse(format.format(new Date().getTime()));

			if (dateCur.after(dateBegin) && dateCur.before(dateEnd)) {
				int countdown = new Long((dateEnd.getTime() - dateCur.getTime()) / 1000 / 60).intValue();
				if (countdown <= 0) {
					return;
				}
				award.setCountdown(countdown);
			}
		} catch (Exception e) {
			Logger.error("SeasonTask.setSpecialSeason - err: %s", e.getMessage());
		}
	}

}
