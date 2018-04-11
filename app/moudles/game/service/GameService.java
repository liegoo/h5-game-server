package moudles.game.service;

import java.util.List;

import com.google.common.base.Strings;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;

import jws.dal.Dal;
import jws.dal.sqlbuilder.Condition;
import jws.dal.sqlbuilder.Sort;
import moudles.game.ddl.CouponGameDDL;
import moudles.game.ddl.GamesDDL;
import utils.DaoUtil;

public class GameService {

	/**
	 * 通过gameId获取游戏
	 */
	public static GamesDDL getGame(int gameId) {
		return Dal.select(DaoUtil.genAllFields(GamesDDL.class), gameId);
	}

	/**
	 * 获取游戏列表
	 */
	public static List<GamesDDL> listGames(int page, int pageSize) {
		page = page == 0 ? 1 : page;
		pageSize = pageSize == 0 ? 10 : pageSize;
		Condition cond = new Condition("GamesDDL.status", "=", 1);
		return Dal.select(DaoUtil.genAllFields(GamesDDL.class), cond, new Sort("GamesDDL.sort", false), (page - 1) * pageSize, pageSize);
	}

	/**
	 * 查询有效的代金券奖品关联游戏
	 * 
	 * @return
	 */
	public static List<CouponGameDDL> listCouponGames() {
		Condition cond = new Condition("CouponGameDDL.status", "=", 1);
		return Dal.select(DaoUtil.genAllFields(CouponGameDDL.class), cond, new Sort("CouponGameDDL.weight", false), 0, -1);
	}
	
	/**
	 * 获取代金券游戏信息
	 * @param gameId
	 * @return
	 */
	public static Record getCouponGame(String gameId){
		if(Strings.isNullOrEmpty(gameId)){
			return null;
		}
		return Db.findFirst("select * from coupon_games where game_id=? limit 1 ", gameId);
	}
	/**
	 * 根据游戏名获取游戏编号
	 * @param gameId
	 * @return
	 */
	public static Record getCouponGameId(String gameName){
		if(Strings.isNullOrEmpty(gameName)){
			return null;
		}
		return Db.findFirst("select * from coupon_games where game_Name=? limit 1 ", gameName);
	}
}
