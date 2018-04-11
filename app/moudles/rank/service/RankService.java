package moudles.rank.service;

import java.util.ArrayList;
import java.util.List;

import org.codehaus.groovy.util.FastArray;

import com.jfinal.plugin.activerecord.Db;

import jws.dal.Dal;
import jws.dal.common.SqlParam;
import jws.dal.sqlbuilder.Condition;
import moudles.award.ddl.AwardRecordDollDDL;
import moudles.rank.ddl.RankDDL;
import utils.DateUtil;

public class RankService {

	/**
	 * 排行榜
	 * 
	 * @param gameId
	 * @param beginDate
	 * @param endDate
	 * @param page
	 * @param pageSize
	 * @return
	 */
	public static List<RankDDL> listRankByDate(int gameId, long beginDate, long endDate, int page, int pageSize) {
		StringBuilder sql = new StringBuilder();
		sql.append("select id, uid, CONVERT(sum(happy_bean),SIGNED) as happy_bean from rank where id > 0 and status = 1");
		if (gameId > 0) {
			sql.append("game_id = ");
			sql.append(gameId);
			sql.append(" ");
		}
		if (beginDate > 0) {
			sql.append(" and DATE_FORMAT(create_time,'%Y-%m-%d') >= '");
			sql.append(DateUtil.formatDate(beginDate, "yyyy-MM-dd"));
			sql.append("' ");
		}
		if (endDate > 0) {
			sql.append(" and DATE_FORMAT(create_time,'%Y-%m-%d') <= '");
			sql.append(DateUtil.formatDate(endDate, "yyyy-MM-dd"));
			sql.append("' ");
		}
		sql.append(" group by uid");
		sql.append(" order by happy_bean desc limit ");
		sql.append((page - 1) * pageSize);
		sql.append(",");
		sql.append(pageSize);
		return Dal.execute(RankDDL.class, sql.toString());
	}

	/**
	 * 添加记录
	 * 
	 * @param rank
	 * @return
	 */
	public static boolean createOrUpdateRank(RankDDL rank) {
		List<SqlParam> params = new ArrayList<SqlParam>();
    	params.add(new SqlParam("RankDDL.uid", rank.getUid()));
    	params.add(new SqlParam("RankDDL.suid", rank.getSuid()));
    	params.add(new SqlParam("RankDDL.happyBean", rank.getHappyBean()));
    	params.add(new SqlParam("RankDDL.gameId", rank.getGameId()));
    	params.add(new SqlParam("RankDDL.happyBean", rank.getHappyBean()));
    	
    	// 此处不使用下面注释的代码，是因为如果该方法在一个事务中被调用，事务对注释代码是起不了作用的
    	String sql = "INSERT INTO rank(uid,suid,happy_bean,game_id,create_time,create_date,update_time) VALUES (?,?,?,?,now(),DATE_FORMAT(now(),'%Y-%m-%d'),now()) ON DUPLICATE KEY UPDATE happy_bean = happy_bean + ?,update_time=now()";
		return Dal.executeNonQuery(RankDDL.class, sql, params, null)>0;
		
		
//		StringBuffer sql = new StringBuffer();
//		List<Object> params = new ArrayList<Object>();
//		sql.append("INSERT INTO rank(uid,suid,happy_bean,game_id,create_time,create_date,update_time) VALUES (?,?,?,?,now(),DATE_FORMAT(now(),'%Y-%m-%d'),now()) ON DUPLICATE KEY UPDATE happy_bean = happy_bean + ?,update_time=now()");
//		params.add(rank.getUid());
//		params.add(rank.getSuid());
//		params.add(rank.getHappyBean());
//		params.add(rank.getGameId());
//		params.add(rank.getHappyBean());
//		int num = Db.update(sql.toString(),params.toArray());
//		if (num == 0) {
//			return false;
//		}
//		return true;
	}	
//		StringBuilder sql = new StringBuilder();
//		sql.append("select uid,happy_bean from rank");
//		sql.append(" where uid = ");
//		sql.append(rank.getUid());
//		sql.append(" and DATE_FORMAT(create_time,'%Y-%m-%d') = '");
//		sql.append(DateUtil.formatDate(System.currentTimeMillis(), "yyyy-MM-dd"));
//		sql.append("'");
//		sql.append(" and game_id = ");
//		sql.append(rank.getGameId());
//		List<RankDDL> list = Dal.execute(RankDDL.class, sql.toString());
//		if (list != null && list.size()>0 ) {
//			RankDDL rankTmp = list.get(0);
//			long happyBean = rank.getHappyBean();
//			if (rankTmp != null) {
//				happyBean += rankTmp.getHappyBean();
//			}
//			sql.delete(0, sql.length());
//			sql.append("update rank set happy_bean = ");
//			sql.append(happyBean);
//			sql.append(" where uid = ");
//			sql.append(rank.getUid());
//			sql.append(" and DATE_FORMAT(create_time,'%Y-%m-%d') = '");
//			sql.append(DateUtil.formatDate(System.currentTimeMillis(), "yyyy-MM-dd"));
//			sql.append("'");
//			sql.append(" and game_id = ");
//			sql.append(rank.getGameId());
//			return Dal.executeNonQuery(RankDDL.class, sql.toString()) > 0;
//		} else {
//			return Dal.insert(rank) > 0;
//		}
//	}

}
