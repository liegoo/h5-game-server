package moudles.award.service;

import java.util.List;

import jws.dal.Dal;
import jws.dal.sqlbuilder.Condition;
import jws.module.response.award.AwardAssignDto;
import moudles.award.ddl.AwardAssignDDL;
import utils.DaoUtil;

import common.dao.QueryConnectionHandler;

import constants.GlobalConstants;

public class AwardAssignService {

	/**
	 * 夹娃娃奖品列表
	 * 
	 * @param gameLevel
	 * @param page
	 * @param pageSize
	 * @return
	 */
	public static List<AwardAssignDto> listAwardAssign(int gameLevel, int page, int pageSize) {
		StringBuffer sql = new StringBuffer("select a.name, a.type, a.img_url imgUrl, b.id id, b.sort,cg.game_id gameId,cg.game_name gameName ");
		sql.append("from award a left join award_assign	b on a.id = b.award_id LEFT JOIN coupon_games cg on (a.game_id=cg.game_id) ");
		sql.append(" where b.status = 1");// 在前端显示
		if (gameLevel > 0) {
			sql.append(" and b.game_level = ").append(gameLevel);
		}
		sql.append(" order by b.sort asc");
		if (pageSize > 0) {
			sql.append(" limit ").append((page - 1) * pageSize).append(" , ").append(pageSize);
		}
		AwardAssignDto dto = new AwardAssignDto();
		List<AwardAssignDto> result = Dal.getConnection(GlobalConstants.dbSource, new QueryConnectionHandler(dto, sql.toString()));
		return result;
	}

	/**
	 * 通过场次获取权重和
	 * 
	 * @param gameLevel
	 * @return
	 */
	public static int getWeightByGameLevel(int gameLevel) {
		return Dal.executeCount(AwardAssignDDL.class, (new StringBuilder("select sum(weight) from award_assign where status = 1 and game_level = ").append(gameLevel)).toString());
	}

	/**
	 * 创建奖品配置
	 * 
	 * @param awardAssign
	 * @return
	 */
	public static boolean createAwardAssign(AwardAssignDDL awardAssign) {
		return Dal.insert(awardAssign) > 0;
	}

	/**
	 * 通过ID查找
	 * 
	 * @param awardAssignId
	 * @return
	 */
	public static AwardAssignDDL getById(int awardAssignId) {
		return Dal.select(DaoUtil.genAllFields(AwardAssignDDL.class), awardAssignId);
	}

	/**
	 * 更新中奖配置
	 * 
	 * @param awardAssign
	 * @return
	 */
	public static boolean updateAwardAssign(AwardAssignDDL awardAssign) {
		Condition cond = new Condition("AwardAssignDDL.id", "=", awardAssign.getId());
		return Dal.update(awardAssign, "AwardAssignDDL.remain,AwardAssignDDL.hits,AwardAssignDDL.status", cond) > 0 ? true : false;
	}

}
