package moudles.guess.service;

import java.util.ArrayList;
import java.util.List;

import com.jfinal.plugin.activerecord.Db;

import jws.Logger;
import jws.dal.Dal;
import jws.dal.common.SqlParam;
import jws.dal.sqlbuilder.Condition;
import moudles.award.ddl.AwardRecordDDL;
import moudles.guess.ddl.GuessAwardDDL;
import moudles.guess.ddl.GuessSeasonCurrentDDL;
import moudles.guess.ddl.GuessSeasonDDL;
import utils.DaoUtil;

public class GuessSeasonCurrentService {

	/**
	 * 创建当前season记录
	 * 
	 * @param season
	 * @return
	 */
	public static boolean create(GuessSeasonCurrentDDL season) {
		return Dal.insert(season) > 0;
	}

	/**
	 * 获取当前season
	 * 
	 * @param gameLevel
	 * @param seasonNum
	 * @return
	 */
	public static GuessSeasonCurrentDDL get(int gameLevel, int seasonNum) {
		Condition cond = new Condition("GuessSeasonCurrentDDL.gameLevel", "=", gameLevel);
		if (seasonNum > 0) {
			cond.add(new Condition("GuessSeasonCurrentDDL.seasonNum", "=", seasonNum), "and");
		}
		List<GuessSeasonCurrentDDL> list = Dal.select(DaoUtil.genAllFields(GuessSeasonCurrentDDL.class), cond, null, 0, 1);
		if (list != null && list.size() > 0) {
			return list.get(0);
		}
		return null;
	}

	/**
	 * 获取当前期列表
	 */
	public static List<GuessSeasonCurrentDDL> list() {
		Condition cond = new Condition("GuessSeasonCurrentDDL.id", ">", 0);
		List<GuessSeasonCurrentDDL> list = Dal.select(DaoUtil.genAllFields(GuessSeasonCurrentDDL.class), cond, null, 0, -1);
		return list;
	}

	/**
	 * 更新当前期信息
	 */
	public static boolean update(GuessSeasonCurrentDDL season) {
		Condition cond = new Condition("GuessSeasonCurrentDDL.gameLevel", "=", season.getGameLevel());
		String updated = "GuessSeasonCurrentDDL.raiseRule,GuessSeasonCurrentDDL.guessSeasonId,GuessSeasonCurrentDDL.guessAwardId,GuessSeasonCurrentDDL.seasonNum,GuessSeasonCurrentDDL.baseBean,GuessSeasonCurrentDDL.currentBean,GuessSeasonCurrentDDL.publishTime,GuessSeasonCurrentDDL.createTime";
		return Dal.update(season, updated, cond) > 0;
	}
	
	/**
	 * 添加当前豆
	 * @param sessionId
	 * @param cost
	 * @return
	 */
	public static boolean addCurrentBean(int sessionId, int cost){
		Logger.info("addCurrentBean>>>>>GuessSeasonId="+sessionId+",追加奖池："+cost);
		
		List<SqlParam> params = new ArrayList<SqlParam>();
    	params.add(new SqlParam("GuessSeasonCurrentDDL.currentBean", cost));
    	params.add(new SqlParam("GuessSeasonCurrentDDL.guessSeasonId", sessionId));
    	// 此处不使用下面注释的代码，是因为如果该方法在一个事务中被调用，事务对注释代码是起不了作用的
		return Dal.executeNonQuery(GuessSeasonCurrentDDL.class, "update guess_season_current set current_bean=current_bean+? where guess_season_id=? ", params, null)>0;
//		return Db.update("update guess_season_current set current_bean=current_bean+? where guess_season_id=? ", cost, sessionId) > 0;
	}

	/**
	 * 创建/更新当前期信息
	 */
	public static boolean createOrUpdateCurrentSeason(GuessSeasonDDL season, GuessAwardDDL award) {
		GuessSeasonCurrentDDL curSeason = GuessSeasonCurrentService.get(season.getGameLevel(), 0);
		GuessSeasonCurrentDDL newCurSeason = new GuessSeasonCurrentDDL();
		long publishTiem = season.getCreateTime() + award.getCountdown() * 60 * 1000;
		newCurSeason.setCurrentBean(season.getJackpot());
		newCurSeason.setBaseBean(award.getHappyBean());
		newCurSeason.setGuessSeasonId(season.getId());
		newCurSeason.setSeasonNum(season.getSeasonNum());
		newCurSeason.setPublishTime(publishTiem);
		newCurSeason.setGameLevel(season.getGameLevel());
		newCurSeason.setGuessAwardId(season.getGuessAwardId());
		newCurSeason.setCreateTime(System.currentTimeMillis());
		newCurSeason.setRaiseRule(award.getRaiseRule());
		newCurSeason.setBoobyPrize(award.getBoobyPrize());
		newCurSeason.setBoobyRate(award.getBoobyRate());
		if (curSeason != null) {
			return GuessSeasonCurrentService.update(newCurSeason);
		} else {
			return GuessSeasonCurrentService.create(newCurSeason);
		}
	}

	/**
	 * 获取当前期数量
	 */
	public static int countByGameLevel(int gameLevel) {
		Condition cond = new Condition("GuessSeasonCurrentDDL.gameLevel", "=", gameLevel);
		return Dal.count(cond);
	}

}
