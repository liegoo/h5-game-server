package moudles.guess.service;

import java.util.List;

import common.dao.QueryConnectionHandler;

import constants.GlobalConstants;
import utils.DaoUtil;
import jws.dal.Dal;
import jws.dal.sqlbuilder.Condition;
import moudles.guess.ddl.GuessBoobyRecordDDL;

public class GuessBoobyRecordService {

	/**
	 * 更新或创建安慰奖记录
	 */
	public static boolean createOrUpdate(GuessBoobyRecordDDL booby) {
		if (booby.getId()>0) {
			Condition cond = new Condition("GuessBoobyRecordDDL.id", "=", booby.getId());
			String updated = "GuessBoobyRecordDDL.happyBean,GuessBoobyRecordDDL.updateTime";
			return Dal.update(booby, updated, cond) > 0;
		} else {
			return Dal.insert(booby) > 0;
		}
	}

	/**
	 * 获取安慰奖记录
	 */
	public static GuessBoobyRecordDDL getByAwardId(int uid, int awardId) {
		Condition cond = new Condition("GuessBoobyRecordDDL.uid", "=", uid);
		cond.add(new Condition("GuessBoobyRecordDDL.guessAwardId", "=", awardId), "and");
		List<GuessBoobyRecordDDL> list = Dal.select(DaoUtil.genAllFields(GuessBoobyRecordDDL.class), cond, null, 0, 1);
		if (list != null && list.size() > 0) {
			return list.get(0);
		}
		return null;
	}
	
	/**
	 * 获取安慰奖记录列表
	 */
	public static List<GuessBoobyRecordDDL> listBoobyRecord(int awardId,int happyBean){
		Condition cond = new Condition("GuessBoobyRecordDDL.guessAwardId","=",awardId);
		cond.add(new Condition("GuessBoobyRecordDDL.happyBean",">=",happyBean),"and");
		List<GuessBoobyRecordDDL> list = Dal.select(DaoUtil.genAllFields(GuessBoobyRecordDDL.class), cond, null, 0, -1);
		return list;
	}
}
