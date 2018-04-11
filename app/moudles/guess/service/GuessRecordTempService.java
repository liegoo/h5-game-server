package moudles.guess.service;

import java.sql.ResultSet;
import java.sql.SQLException;

import constants.GlobalConstants;
import jws.Logger;
import jws.dal.Dal;
import jws.dal.sqlbuilder.Condition;
import jws.module.response.guess.PlayRecordDto;
import moudles.guess.ddl.GuessRecordTempDDL;

public class GuessRecordTempService {

	/**
	 * 创建记录并返回当前期夺宝码个数
	 */
	public static int insertAndGetCount(GuessRecordTempDDL record) {
		int lastId = new Long(Dal.insertSelectLastId(record)).intValue();
		if (lastId > 0) {
			StringBuilder sql = new StringBuilder("select sum(code_amount) codeSum from guess_record_temp where id <");
			sql.append(lastId);
			sql.append(" and season_id = ");
			sql.append(record.getSeasonId());
			int codeSum = 0;
			ResultSet result = Dal.executeQuery(GlobalConstants.dbSource, sql.toString());
			if (result != null) {
				try {
					while (result.next()) {
						codeSum = result.getInt("codeSum");
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
			return codeSum;
		} else {
			return -1;
		}
	}

	/**
	 * 删除记录
	 */
	public static int deleteBySeasonId(int seasonId) {
		Condition cond = new Condition("GuessRecordTempDDL.seasonId", "=", seasonId);
		return Dal.delete(cond);
	}

}
