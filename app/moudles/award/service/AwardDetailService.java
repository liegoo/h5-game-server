package moudles.award.service;

import java.util.ArrayList;
import java.util.List;

import jws.dal.Dal;
import jws.dal.sqlbuilder.Condition;
import jws.dal.sqlbuilder.Sort;
import moudles.award.ddl.AwardDetailDDL;
import utils.DaoUtil;

public class AwardDetailService {

	/**
	 * 获取奖品明细列表
	 * 
	 * @param awardAssignId
	 * @return
	 */
	public static List<AwardDetailDDL> listAwardDetail(Long startTime, Long endTime, int awardAssignId, int status) {
		Condition cond = new Condition("AwardDetailDDL.awardAssignId", "=", awardAssignId);
		if (status > 0) {
			cond.add(new Condition("AwardDetailDDL.status", "=", status), "and");
		}
		if (startTime > 0 && endTime > 0) {
			cond.add(new Condition("AwardDetailDDL.assignTime", ">", startTime), "and");
			cond.add(new Condition("AwardDetailDDL.assignTime", "<", endTime), "and");
		}
		Sort sort = new Sort("AwardDetailDDL.id", true);
		return Dal.select(DaoUtil.genAllFields(AwardDetailDDL.class), cond, sort, 0, -1);
	}

	/**
	 * 获取奖品明细
	 * 
	 * @param startTime
	 * @param endTime
	 * @param awardAssignId
	 * @param status
	 * @return
	 */
	public static AwardDetailDDL getByAwardAssignId(int awardAssignId, int status) {
		Condition cond = new Condition("AwardDetailDDL.awardAssignId", "=", awardAssignId);
		if (status > 0) {
			cond.add(new Condition("AwardDetailDDL.status", "=", status), "and");
		}
		List<AwardDetailDDL> list = Dal.select(DaoUtil.genAllFields(AwardDetailDDL.class), cond, null, 0, 1);
		if (list != null && list.size() == 1) {
			return list.get(0);
		}
		return null;
	}

	/**
	 * 获取未开奖且已过时的奖品明细列表
	 * 
	 * @param date
	 * @param status
	 * @return
	 */
	public static List<AwardDetailDDL> listExpireAwardDetail(Long date, int status) {
		Condition cond = new Condition("AwardDetailDDL.assignTime", "<", date);
		if (status > 0) {
			cond.add(new Condition("AwardDetailDDL.status", "=", status), "and");
		}
		return Dal.select(DaoUtil.genAllFields(AwardDetailDDL.class), cond, null, 0, -1);
	}

	/**
	 * 通过UID获取中奖记录
	 * 
	 * @param uid
	 * @return
	 */
	public static List<AwardDetailDDL> listAwardDetailByUid(int uid) {
		List<AwardDetailDDL> list = new ArrayList<AwardDetailDDL>();
		Condition cond = new Condition("AwardDetailDDL.uid", "=", uid);
		cond.add(new Condition("AwardDetailDDL.status", "=", 2), "and");// 已中奖
		Sort sort = new Sort("AwardDetailDDL.hitTime", false);
		list = Dal.select(DaoUtil.genAllFields(AwardDetailDDL.class), cond, sort, 0, -1);
		return list;
	}

	/**
	 * 通过ID查找
	 * 
	 * @param id
	 * @return
	 */
	public static AwardDetailDDL getById(int id) {
		return Dal.select(DaoUtil.genAllFields(AwardDetailDDL.class), id);
	}

	/**
	 * 创建奖品明细
	 * 
	 * @param awardDetail
	 * @return
	 */
	public static boolean createAwardDetail(AwardDetailDDL awardDetail) {
		return Dal.insert(awardDetail) > 0;
	}

	/**
	 * 更新奖品明细
	 * 
	 * @param awardDetail
	 * @return
	 */
	public static boolean updateAwardDetail(AwardDetailDDL awardDetail) {
		Condition cond = new Condition("AwardDetailDDL.id", "=", awardDetail.getId());
		return Dal.update(awardDetail, "AwardDetailDDL.hitTime,AwardDetailDDL.orderId,AwardDetailDDL.uid,AwardDetailDDL.status", cond) > 0 ? true : false;
	}
}
