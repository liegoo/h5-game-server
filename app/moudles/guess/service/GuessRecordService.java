package moudles.guess.service;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import jws.Logger;
import jws.cache.Cache;
import jws.dal.Dal;
import jws.dal.sqlbuilder.Condition;
import jws.dal.sqlbuilder.Sort;
import jws.module.constants.guess.BoobyStatus;
import jws.module.constants.guess.GuessHitStatus;
import jws.module.constants.guess.GuessRecordStatus;
import jws.module.constants.guess.RobotStatus;
import jws.module.response.guess.GetWinningRecordResp;
import jws.module.response.guess.GuessPlayerDto;
import jws.module.response.guess.GuessRankDto;
import jws.module.response.guess.ListPlayersResp;
import jws.module.response.guess.ListWinningRecordResp;
import jws.module.response.guess.PlayRecordDto;
import moudles.guess.ddl.GuessRecordDDL;
import moudles.member.service.MemberService;

import org.apache.commons.lang3.StringUtils;

import utils.DaoUtil;

import com.google.gson.Gson;
import common.dao.QueryConnectionHandler;

import constants.GlobalConstants;

public class GuessRecordService {

	/**
	 * 创建夺宝记录
	 * 
	 * @param ddl
	 * @return
	 */
	public static boolean create(GuessRecordDDL ddl) {
		try {
			return Dal.insert(ddl) > 0;
		} catch (Exception e) {
			Logger.error("GuessRecordService.create - err:%s", e.getMessage());
		}
		return false;
	}

	/**
	 * 批量创建夺宝记录
	 * 
	 * @param list
	 * @return
	 */
	public static int createMulti(List<GuessRecordDDL> list) {
		try {
			return Dal.insertMulti(list);
		} catch (Exception e) {
			Logger.error("GuessRecordService.createMulti - create guess record multi failed.");
			Logger.error("GuessRecordService.createMulti - err:%s", e.getMessage());
		}
		return 0;
	}

	/**
	 * 更新夺宝记录
	 * 
	 * @param ddl
	 * @return
	 */
	public static boolean update(GuessRecordDDL ddl) {
		Condition cond = new Condition("GuessRecordDDL.id", "=", ddl.getId());
		String updated = "GuessRecordDDL.inputBean,GuessRecordDDL.deductBean,GuessRecordDDL.raiseBean,GuessRecordDDL.winningCode,GuessRecordDDL.hit,GuessRecordDDL.rewardBean,GuessRecordDDL.isBooby,GuessRecordDDL.isRead,GuessRecordDDL.updateTime,GuessRecordDDL.status";
		return Dal.update(ddl, updated, cond) > 0;
	}

	/**
	 * 查找夺宝记录
	 * 
	 * @param uid
	 * @param startTime
	 * @param endTime
	 * @param page
	 * @param pageSize
	 * @return
	 */
	public static List<GuessRecordDDL> list(int uid, int seasonId, int seasonNum, int gameLevel, int hit, int isBooby, long startTime, long endTime, int page, int pageSize) {
		Condition cond = new Condition("GuessRecordDDL.id", ">", 0);
		if (uid > 0) {
			cond.add(new Condition("GuessRecordDDL.uid", "=", uid), "and");
		}
		if (seasonId > 0) {
			cond.add(new Condition("GuessRecordDDL.guessSeasonId", "=", seasonId), "and");
		}
		if (seasonNum > 0) {
			cond.add(new Condition("GuessRecordDDL.seasonNum", "=", seasonNum), "and");
		}
		if (gameLevel > 0) {
			cond.add(new Condition("GuessRecordDDL.gameLevel", "=", gameLevel), "and");
		}
		if (hit > -1) {
			cond.add(new Condition("GuessRecordDDL.hit", "=", hit), "and");
		}
		if (isBooby > -1) {
			cond.add(new Condition("GuessRecordDDL.isBooby", "=", isBooby), "and");
		}
		if (startTime > 0 && endTime > 0) {
			cond.add(new Condition("GuessRecordDDL.createTime", ">", startTime), "and");
			cond.add(new Condition("GuessRecordDDL.createTime", "<", endTime), "and");
		}
		Sort sort = new Sort("GuessRecordDDL.createTime", false);

		List<GuessRecordDDL> list = Dal.select(DaoUtil.genAllFields(GuessRecordDDL.class), cond, sort, (page - 1) * pageSize, pageSize);
		return list;
	}

	/**
	 * 参与记录
	 * 
	 * @param uid
	 * @param isBooby
	 * @param page
	 * @param pageSize
	 * @return
	 */
	public static List<PlayRecordDto> listPlayRecord(int uid, int isBooby, int page, int pageSize) {
		StringBuilder sql = new StringBuilder(
				"select sum(hit) hit,status,guess_season_id seasonId,season_num seasonNum,game_level gameLevel, unix_timestamp(create_time) time from guess_record where id > 1");
		if (uid > 0) {
			sql.append(" and uid = ");
			sql.append(uid);
		}

		if (isBooby > -1) {
			sql.append(" and is_booby = ");
			sql.append(isBooby);
		}

		sql.append(" and is_robot = ");
		sql.append(RobotStatus.DISABLED.getValue());

		sql.append(" group by guess_season_id order by create_time desc");
		if (pageSize > 0) {
			sql.append(" limit ").append((page - 1) * pageSize).append(" , ").append(pageSize);
		}

		List<PlayRecordDto> list = new ArrayList<PlayRecordDto>();

		// TODO QueryConnectionHandler 存在性能问题,暂用Dal.executeQuery 代替
		ResultSet result = Dal.executeQuery(GlobalConstants.dbSource, sql.toString());
		PlayRecordDto dto = null;
		if (result != null) {
			try {
				while (result.next()) {
					dto = new PlayRecordDto();
					dto.setHit(result.getInt("hit"));
					dto.setStatus(result.getInt("status"));
					dto.setSeasonId(result.getInt("seasonId"));
					dto.setSeasonNum(result.getInt("seasonNum"));
					dto.setGameLevel(result.getInt("gameLevel"));
					dto.setTime(result.getLong("time"));
					list.add(dto);
				}
			} catch (SQLException e) {
				Logger.error(e.getMessage());
			} finally {
				try {
					result.close();
				} catch (SQLException e) {
					Logger.error(e.getMessage());
				}
			}
		}
		return list;
	}

	/**
	 * 查找夺宝记录
	 * 
	 * @param seasonId
	 * @param uid
	 * @return
	 */
	public static GuessRecordDDL getBySeasonId(int seasonId, int uid) {
		Condition cond = new Condition("GuessRecordDDL.uid", "=", uid);
		cond.add(new Condition("GuessRecordDDL.guessSeasonId", "=", seasonId), "and");
		List<GuessRecordDDL> list = Dal.select(DaoUtil.genAllFields(GuessRecordDDL.class), cond, null, 0, 1);
		if (list != null && list.size() == 1) {
			return list.get(0);
		}
		return null;
	}

	/**
	 * 查找夺宝记录
	 * 
	 * @param seasonId
	 * @param uid
	 * @return
	 */
	public static GuessRecordDDL getByCode(int seasonId, int code) {
		StringBuffer sql = new StringBuffer(
				"select id,order_id orderId,guess_season_id guessSeasonId,season_num seasonNum,game_level gameLevel,uid,nickname,is_robot isRobot,happy_bean happyBean,code,hit,is_booby isBooby,create_time createTime,update_time updateTime,status from guess_record where is_booby = 0 and find_in_set('");
		sql.append(code);
		sql.append("', code) ");
		sql.append(" and guess_season_id = ");
		sql.append(seasonId);

		GuessRecordDDL ddl = new GuessRecordDDL();
		List<GuessRecordDDL> list = Dal.getConnection(GlobalConstants.dbSource, new QueryConnectionHandler(ddl, sql.toString()));
		if (list != null && list.size() > 0) {
			return list.get(0);
		}
		return null;
	}

	/**
	 * 获取本期参与玩家
	 * 
	 * @param seasonId
	 * @param page
	 * @param pageSize
	 * @return
	 */
	public static ListPlayersResp listPlayers(int seasonId, int gameLevel, boolean groupByOrderId, int page, int pageSize) {
		ListPlayersResp resp = new ListPlayersResp();
		if (seasonId == 0) {
			return null;
		}

		StringBuffer sql = new StringBuffer("select nickname,millis ms,order_id,is_allin isAllIn, CONCAT(UNIX_TIMESTAMP(create_time),LPAD(millis,3,'00')) time,");
		sql.append(" happy_bean bean");
		sql.append(" from guess_record where guess_season_id = ");
		sql.append(seasonId);
		sql.append(" and is_booby = 0 and game_level = ");
		sql.append(gameLevel);
		sql.append(" order by create_time desc");
		if (pageSize > 0) {
			sql.append(" limit ").append((page - 1) * pageSize).append(" , ").append(pageSize);
		}

		List<GuessPlayerDto> list = new ArrayList<GuessPlayerDto>();
		GuessPlayerDto dto = null;

		// TODO QueryConnectionHandler 存在性能问题,暂用Dal.executeQuery 代替
		ResultSet result = Dal.executeQuery(GlobalConstants.dbSource, sql.toString());
		if (result != null) {
			try {
				while (result.next()) {
					dto = new GuessPlayerDto();
					dto.setNickname(result.getString("nickname"));
					dto.setMs(result.getInt("ms"));
					dto.setBean(result.getInt("bean"));
					dto.setTime(result.getLong("time"));
					dto.setAllIn(result.getInt("isAllIn") == 1 ? true : false);
					list.add(dto);
				}
			} catch (SQLException e) {
				Logger.error(e.getMessage());
			} finally {
				try {
					result.close();
				} catch (SQLException e) {
					Logger.error(e.getMessage());
				}
			}
		}

		resp.setList(list);
		return resp;
	}

	/**
	 * 获取中奖记录
	 * 
	 * @param gameLevel
	 * @param seasonNum
	 * @return
	 */
	public static GetWinningRecordResp getWinningRecord(int gameLevel, int seasonNum) {
		StringBuffer sql = new StringBuffer(
				"select input_bean inputBean,deduct_bean deductBean,guess_season_id seasonId,uid,winning_code code,season_num seasonNum,nickname,game_level gameLevel, reward_bean happyBean from guess_record where 1 = 1");
		sql.append(" and game_level = ");
		sql.append(gameLevel);
		sql.append(" and is_booby = ");
		sql.append(BoobyStatus.DISABLED.getValue());
		sql.append(" and season_num = ");
		sql.append(seasonNum);
		sql.append(" and hit = ");
		sql.append(GuessHitStatus.YES.getValue());

		GetWinningRecordResp dto = null;
		// TODO 待重构
		ResultSet result = Dal.executeQuery(GlobalConstants.dbSource, sql.toString());
		if (result != null) {
			try {
				while (result.next()) {
					dto = new GetWinningRecordResp();

					double deductRate = 0;
					int deductBean = result.getInt("deductBean");
					int rewardBean = result.getInt("happyBean");
					int inputBean = result.getInt("inputBean");

					if (deductBean > 0) {
						deductRate = (double) deductBean / (rewardBean - inputBean);
						BigDecimal format = new BigDecimal(deductRate);
						deductRate = format.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
						dto.setDeductRate(deductRate);
					}

					dto.setUid(result.getInt("uid"));
					dto.setCode(result.getInt("code"));
					dto.setSeasonNum(result.getInt("seasonNum"));
					dto.setNickname(result.getString("nickname"));
					dto.setGameLevel(result.getInt("gameLevel"));
					dto.setHappyBean(rewardBean);
					dto.setSeasonId(result.getInt("seasonId"));
					dto.setDeductBean(deductBean);

				}
			} catch (SQLException e) {
				Logger.error(e.getMessage());
			} finally {
				try {
					result.close();
				} catch (SQLException e) {
					Logger.error(e.getMessage());
				}
			}
		}

		return dto;
	}

	/**
	 * 获取最近中奖记录列表
	 */
	public static ListWinningRecordResp listLatestWinningRecord() {

		StringBuffer sql = new StringBuffer(
				"SELECT guess_season_id seasonId,uid,winning_code code,season_num seasonNum,nickname,game_level gameLevel, reward_bean happyBean from guess_record where guess_season_id in (SELECT max(id) from guess_season where winner_uid > 0 and status = 2 and code > 0 GROUP BY game_level) and hit = 1 and is_booby = 0 ORDER BY game_level");

		ListWinningRecordResp resp = new ListWinningRecordResp();
		List<GetWinningRecordResp> list = new ArrayList<GetWinningRecordResp>();
		GetWinningRecordResp dto = null;

		// TODO 待重构
		ResultSet result = Dal.executeQuery(GlobalConstants.dbSource, sql.toString());
		if (result != null) {
			try {
				while (result.next()) {
					dto = new GetWinningRecordResp();
					dto.setUid(result.getInt("uid"));
					dto.setCode(result.getInt("code"));
					dto.setSeasonNum(result.getInt("seasonNum"));
					dto.setNickname(result.getString("nickname"));
					dto.setGameLevel(result.getInt("gameLevel"));
					dto.setHappyBean(result.getInt("happyBean"));
					dto.setSeasonId(result.getInt("seasonId"));
					list.add(dto);
				}
			} catch (SQLException e) {
				Logger.error(e.getMessage());
			} finally {
				try {
					result.close();
				} catch (SQLException e) {
					Logger.error(e.getMessage());
				}
			}
		}
		resp.setList(list);
		return resp;
	}

	/**
	 * 获取夺宝记录
	 * 
	 * @param uid
	 * @param isRead
	 * @param isBooby
	 * @param isAllIn
	 * @param hit
	 * @param gameLevel
	 * @param page
	 * @param pageSize
	 * @return
	 */
	public static List<GuessRecordDDL> listGuessRecord(int uid, int isRead, int isBooby, int isAllIn, int hit, int gameLevel, int page, int pageSize) {
		Condition cond = new Condition("GuessRecordDDL.id", ">", 0);
		if (uid > 0) {
			cond.add(new Condition("GuessRecordDDL.uid", "=", uid), "and");
		}
		if (isRead > -1) {
			cond.add(new Condition("GuessRecordDDL.isRead", "=", isRead), "and");
		}
		if (gameLevel > 0) {
			cond.add(new Condition("GuessRecordDDL.gameLevel", "=", gameLevel), "and");
		}
		if (hit > 0) {
			cond.add(new Condition("GuessRecordDDL.hit", "=", hit), "and");
		}
		if (isBooby > -1) {
			cond.add(new Condition("GuessRecordDDL.isBooby", "=", isBooby), "and");
		}
		if (isAllIn > -1) {
			cond.add(new Condition("GuessRecordDDL.isAllIn", "=", isAllIn), "and");
		}
		Sort sort = new Sort("GuessRecordDDL.createTime", false);
		List<GuessRecordDDL> list = Dal.select(DaoUtil.genAllFields(GuessRecordDDL.class), cond, sort, (page - 1) * pageSize, pageSize);
		return list;
	}

	/**
	 * 获取上一场中奖记录
	 */
	public static ListWinningRecordResp ListLastWinningRecord(int seasonNum) {
		ListWinningRecordResp resp = new ListWinningRecordResp();
		StringBuffer sql = new StringBuffer(
				"select r.code,r.season_num seasonNum,r.nickname,r.game_level gameLevel, r.reward_bean happyBean from guess_record r where r.season_num = ");
		sql.append(seasonNum);
		sql.append(" and r.hit = ");
		sql.append(GuessHitStatus.YES.getValue());
		sql.append(" and r.is_booby = ");
		sql.append(BoobyStatus.DISABLED.getValue());
		sql.append(" order by r.create_time ");
		GetWinningRecordResp dto = new GetWinningRecordResp();
		List<GetWinningRecordResp> list = Dal.getConnection(GlobalConstants.dbSource, new QueryConnectionHandler(dto, sql.toString()));
		resp.setList(list);
		return resp;
	}

	/**
	 * 获取排行榜
	 */
	public static List<GuessRankDto> getRanklist(long beginDate, long endDate, int page, int pageSize) {
		StringBuilder sql = new StringBuilder();
		sql.append("select uid,is_robot, nickname, sum(reward_bean) as bean from guess_record");
		sql.append(" where status = ");
		sql.append(GuessRecordStatus.PUBLISHED.getValue());
		sql.append(" and is_booby = ");
		sql.append(BoobyStatus.DISABLED.getValue());
		if (beginDate > 0) {
			sql.append(" and UNIX_TIMESTAMP(create_time)*1000 >= ");
			sql.append(beginDate);
		}
		if (endDate > 0) {
			sql.append(" and UNIX_TIMESTAMP(create_time)*1000 <= ");
			sql.append(endDate);
		}
		sql.append(" group by uid");
		sql.append(" order by bean desc");
		if (page > 0 && pageSize > 0) {
			sql.append(" limit ");
			sql.append((page - 1) * pageSize);
			sql.append(" , ");
			sql.append(pageSize);
		}
		GuessRankDto dto = new GuessRankDto();
		List<GuessRankDto> list = new ArrayList<GuessRankDto>();

		// TODO 由于使用 Dal.getConnection存在性能问题,暂用 Dal.executeQuery代替
		ResultSet result = Dal.executeQuery(GlobalConstants.dbSource, sql.toString());
		if (result != null) {
			try {
				int rank = 0;
				while (result.next()) {
					rank++;
					boolean isRobot = result.getInt("is_robot") == RobotStatus.ENABLED.getValue();

					dto = new GuessRankDto();
					dto.setBean(result.getInt("bean"));
					dto.setUid(isRobot ? -1 : result.getInt("uid"));
					dto.setNickname(result.getString("nickname"));
					dto.setRank(rank);
					list.add(dto);
				}
			} catch (SQLException e) {
				Logger.error(e.getMessage());
			} finally {
				try {
					result.close();
				} catch (SQLException e) {
					Logger.error(e.getMessage());
				}
			}
		}

		// 前三名用户显示头像
		if (page >= 0 && page < 2) {
			int size = (list != null && list.size() > 3) ? 3 : list.size();
			for (int i = 0; i < size; i++) {
				GuessRankDto rank = list.get(i);
				int uid = rank.getUid();
				if (uid > 0) {
					String avatar = getAvatar(uid);
					if (avatar.startsWith("http")) {
						rank.setAvatar(avatar);
					}
				}
			}
		}

		// for (GuessRankDto rank : list) {
		// int uid = rank.getUid();
		// if (uid > 0) {
		// String avatar = getAvatar(uid);
		// if (avatar.startsWith("http")) {
		// rank.setAvatar(avatar);
		// }
		// }
		//
		// }
		return list;
	}

	private static String getAvatar(int uid) {
		String key = String.format("h5_user_avatar_%d", uid);
		String avatar = (String) Cache.get(key);
		if (StringUtils.isEmpty(avatar)) {
			avatar = MemberService.getAvatarByUid(uid);
			if (avatar == null) {
				avatar = "";
			}
			Cache.add(key, avatar, "1d");
		}
		return StringUtils.isNotEmpty(avatar) ? avatar : "";
	}

	/**
	 * 更新夺宝中奖信息产阅读状态
	 */
	public static boolean updateReadStatus(GuessRecordDDL ddl) {
		Condition cond = new Condition("GuessRecordDDL.uid", "=", ddl.getUid());
		cond.add(new Condition("GuessRecordDDL.isRead", "=", 0), "and");
		if (ddl.getGameLevel() > 0) {
			cond.add(new Condition("GuessRecordDDL.gameLevel", "=", ddl.getGameLevel()), "and");
		}
		if (ddl.getSeasonNum() > 0) {
			cond.add(new Condition("GuessRecordDDL.seasonNum", "=", ddl.getSeasonNum()), "and");
		}
		return Dal.update(ddl, "GuessRecordDDL.isRead", cond) > 0;
	}

	/**
	 * 获取用户夺宝码列表
	 */
	public static List<String> listUserCode(int uid, int seasonId) {
		Condition cond = new Condition("GuessRecordDDL.guessSeasonId", "=", seasonId);
		cond.add(new Condition("GuessRecordDDL.isBooby", "=", BoobyStatus.DISABLED.getValue()), "and");
		cond.add(new Condition("GuessRecordDDL.uid", "=", uid), "and");
		List<GuessRecordDDL> list = Dal.select("GuessRecordDDL.code", cond, null, 0, -1);
		List<String> codes = new ArrayList<String>();
		for (GuessRecordDDL record : list) {
			codes.add(record.getCode());
		}
		return codes;
	}

	/**
	 * 获取上一个夺宝码
	 */
	public static int getLastCodeBySeason(int seasonId) {
		StringBuilder sql = new StringBuilder("select sum(code_amount) code from guess_record where guess_season_id = ");
		sql.append(seasonId);
		sql.append(" and is_booby = 0");
		int code = 0;
		ResultSet result = Dal.executeQuery(GlobalConstants.dbSource, sql.toString());
		if (result != null) {
			try {
				while (result.next()) {
					code = result.getInt("code");
				}
			} catch (SQLException e) {
				Logger.error(e.getMessage());
			} finally {
				try {
					result.close();
				} catch (SQLException e) {
					Logger.error(e.getMessage());
				}
			}
		}
		return code;
	}

	/**
	 * 获取夺宝记录列表
	 */
	public static List<GuessRecordDDL> listRecordByDate(long beginDate, long endDate) {
		StringBuilder sql = new StringBuilder();
		sql.append("select uid from guess_record where status = ");
		sql.append(GuessRecordStatus.PUBLISHED.getValue());
		sql.append(" and is_robot = ");
		sql.append(RobotStatus.DISABLED.getValue());
		if (beginDate > 0) {
			sql.append(" and UNIX_TIMESTAMP(create_time)*1000 >= ");
			sql.append(beginDate);
		}
		if (endDate > 0) {
			sql.append(" and UNIX_TIMESTAMP(create_time)*1000 <= ");
			sql.append(endDate);
		}
		sql.append(" group by uid");
		sql.append(" order by bean desc");
		GuessRecordDDL dto = new GuessRecordDDL();
		List<GuessRecordDDL> list = Dal.getConnection(GlobalConstants.dbSource, new QueryConnectionHandler(dto, sql.toString()));
		return list;
	}

	/**
	 * 通过订单号获取夺宝记录
	 */
	public static GuessRecordDDL getByZhifuOrderId(String zhifuOrderId, int uid) {
		if (StringUtils.isBlank(zhifuOrderId)) {
			return null;
		}
		Condition cond = new Condition("GuessRecordDDL.zhifuOrderId", "=", zhifuOrderId);
		if (uid > 0) {
			cond.add(new Condition("GuessRecordDDL.uid", "=", uid), "and");
		}
		List<GuessRecordDDL> list = Dal.select(DaoUtil.genAllFields(GuessRecordDDL.class), cond, null, 0, 1);
		if (list.size() > 0 && list.get(0) != null) {
			return list.get(0);
		}
		return null;
	}

	/**
	 * 获取已投入开心另豆
	 */
	public static int getInputBeanBySeason(int uid, int seasonId) {
		StringBuilder sql = new StringBuilder("select sum(happy_bean) from guess_record where guess_season_id = ");
		sql.append(seasonId);
		sql.append(" and uid = ");
		sql.append(uid);
		sql.append(" and is_booby = ");
		sql.append(BoobyStatus.DISABLED.getValue());
		return Dal.executeCount(GuessRecordDDL.class, sql.toString());
	}

}
