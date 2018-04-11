package moudles.award.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.common.base.Strings;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;

import common.dao.QueryConnectionHandler;
import constants.GlobalConstants;
import jws.Logger;
import jws.dal.Dal;
import jws.dal.common.SqlParam;
import jws.dal.sqlbuilder.Condition;
import jws.dal.sqlbuilder.Sort;
import jws.module.constants.doll.DollAwardStatus;
import jws.module.constants.mng.AuditStatus;
import jws.module.response.award.AwardRecentDollDto;
import moudles.award.ddl.AwardRecordDollDDL;
import moudles.member.ddl.MemberDDL;
import utils.DaoUtil;
import utils.DateUtil;
import com.google.gson.Gson;
import com.jfinal.plugin.activerecord.Db;

import common.dao.QueryConnectionHandler;

import constants.GlobalConstants;


public class AwardRecordDollService {

	/**
	 * 获取夹娃娃记录(个人)
	 * 
	 * @param beginTime
	 * @param endTime
	 * @param page
	 * @param pageSize
	 * @return
	 */
	public static List<AwardRecordDollDDL> listAwardRecordDoll(int uid, int hit, Long beginTime, Long endTime, int page, int pageSize) {
		if(pageSize == -1){// 做最大条数限制，数据多时不会查询不出来
			pageSize = 50;
		}
		if(page <= 0){
			page = 1;
		}
		List<AwardRecordDollDDL> list = new ArrayList<AwardRecordDollDDL>();
		Condition cond = new Condition("AwardRecordDollDDL.id", ">", 0);

		if (uid > 0) {
			cond.add(new Condition("AwardRecordDollDDL.uid", "=", uid), "and");
		} else {
			cond.add(new Condition("AwardRecordDollDDL.visible", "=", 1), "and");
		}
		if (hit > 0) {
			cond.add(new Condition("AwardRecordDollDDL.hit", "=", hit), "and");
		}
		if (beginTime > 0) {
			cond.add(new Condition("AwardRecordDollDDL.createTime", ">", beginTime), "and");
		}
		if (endTime > 0) {
			cond.add(new Condition("AwardRecordDollDDL.endTime", "<", endTime), "and");
		}
		list = Dal.select(DaoUtil.genAllFields(AwardRecordDollDDL.class), cond, new Sort("AwardRecordDollDDL.createTime", false), (page - 1) * pageSize, pageSize);
		return list;
	}

	/**
	 * 获取最近夹娃娃中奖记录(所有人)
	 * 
	 * @param beginTime
	 * @param endTime
	 * @param page
	 * @param pageSize
	 * @return
	 */
	public static List<AwardRecordDollDDL> listAwardRecentDoll(Long beginTime, Long endTime, int page, int pageSize) {
		List<AwardRecordDollDDL> list = new ArrayList<AwardRecordDollDDL>();
		Condition cond = new Condition("AwardRecordDollDDL.id", ">", 0);
		cond.add(new Condition("AwardRecordDollDDL.hit", "=", 1), "and");
		cond.add(new Condition("AwardRecordDollDDL.visible", "=", 1), "and");

		if (beginTime > 0) {
			cond.add(new Condition("AwardRecordDollDDL.createTime", ">", beginTime), "and");
		}
		if (endTime > 0) {
			cond.add(new Condition("AwardRecordDollDDL.createTime", "<", endTime), "and");
		}
		list = Dal.select(DaoUtil.genAllFields(AwardRecordDollDDL.class), cond, new Sort("AwardRecordDollDDL.createTime", false), (page - 1) * pageSize, pageSize);
		return list;
	}

	/**
	 * 夹娃娃最近中奖记录(所有人)
	 * 
	 * @param beginTime
	 * @param endTime
	 * @param page
	 * @param pageSize
	 * @return
	 */
	public static List<AwardRecentDollDto> listAwardRecentDollDto(Long beginTime, Long endTime, int page, int pageSize) {
		StringBuffer sql = new StringBuffer("select a.award_id awardId, a.user_name userName,a.create_time createTime, a.mobile, b.name awardName, b.img_url awardImgUrl from award_record_doll a");
		sql.append(" inner join award b on a.award_id = b.id and hit = 1");
		if (beginTime > 0 && endTime > 0) {
			sql.append(" and a.create_time > '");
			sql.append(DateUtil.getDateString(beginTime));
			sql.append("' and a.create_time < '");
			sql.append(DateUtil.getDateString(endTime));
			sql.append("'");
		}
		sql.append(" limit ");
		sql.append((page - 1) * pageSize);
		sql.append(" , ");
		sql.append(pageSize);
		AwardRecentDollDto dto = new AwardRecentDollDto();
		List<AwardRecentDollDto> result = Dal.getConnection(GlobalConstants.dbSource, new QueryConnectionHandler(dto, sql.toString()));
		return result;
	}

	/**
	 * 夹娃娃最近count条中奖记录(所有人)
	 * 
	 * @param count
	 * @return
	 */
	public static List<AwardRecentDollDto> listAwardRecentDollDto(int count) {
		StringBuffer sql = new StringBuffer("select a.user_name userName, a.create_time time, a.mobile, b.name awardName, b.img_url awardImgUrl from award_record_doll a");
		sql.append(" inner join award b on a.award_id = b.id and a.hit = 1");
		sql.append(" order by a.id desc, a.create_time desc");
		sql.append(" limit ");
		sql.append(count);
		AwardRecentDollDto dto = new AwardRecentDollDto();
		List<AwardRecentDollDto> result = Dal.getConnection(GlobalConstants.dbSource, new QueryConnectionHandler(dto, sql.toString()));
//		List<AwardRecentDollDto> result = Dal.getConnection(GlobalConstants.dbSource, new QueryConnectionSimpleHandler(new AwardRecentDollDto(), sql.toString()));
		return result;
	}

	/**
	 * 获取最近count条夹娃娃中奖记录
	 * 
	 * @param count
	 * @return
	 */
	public static List<AwardRecordDollDDL> listAwardRecentDoll(int count) {
		StringBuffer sql = new StringBuffer("select award_id awardId, user_name userName,create_time createTime from award_record_doll");
		sql.append(" where hit = 1");
		sql.append(" and visible = 1");
		sql.append(" order by id desc, create_time desc");
		sql.append(" limit ");
		sql.append(count);
		AwardRecordDollDDL dto = new AwardRecordDollDDL();
		List<AwardRecordDollDDL> result = Dal.getConnection(GlobalConstants.dbSource, new QueryConnectionHandler(dto, sql.toString()));
		return result;
	}

	/**
	 * 添加记录
	 * 
	 * @param record
	 * @return
	 */
	public static Long createAwardRecord(AwardRecordDollDDL record) {
		return Dal.insertSelectLastId(record);
	}

	/**
	 * 通过ID查找记录
	 * 
	 * @param id
	 * @return
	 */
	public static AwardRecordDollDDL getById(int id) {
		return Dal.select(DaoUtil.genAllFields(AwardRecordDollDDL.class), id);
	}
	
	/**
	 * 根据基础代金券ID获取夹娃娃记录
	 * @param baseCouponId
	 * @return
	 */
	public static Record getByBaseCouponId(String baseCouponId){
		if(Strings.isNullOrEmpty(baseCouponId)){
			return null;
		}
		return Db.findFirst("select * from award_record_doll where base_coupon_id=? limit 1 ", baseCouponId);
	}

	/**
	 * 夹娃娃记录
	 * 
	 * @param uid
	 * @return
	 */
	public static List<AwardRecordDollDDL> listAwardRecordDollByUid(int uid) {
		List<AwardRecordDollDDL> list = new ArrayList<AwardRecordDollDDL>();
		Condition cond = new Condition("AwardRecordDollDDL.uid", "=", uid);
		Sort sort = new Sort("AwardRecordDollDDL.createTime", false);
		list = Dal.select(DaoUtil.genAllFields(AwardRecordDollDDL.class), cond, sort, 0, -1);
		return list;
	}

	/**
	 * 通过UID获取总数
	 * 
	 * @param uid
	 * @return
	 */
	public static int countByUid(int uid) {
		return Dal.count(new Condition("AwardRecordDollDDL.uid", "=", uid));
	}

	/**
	 * 更新记录
	 * 
	 * @param awardRecordDoll
	 * @return
	 */
	public static boolean update(int awardRecordDollId, int auditStatus, String auditRemark, int status){
		Map params = new HashMap();
		params.put("awardRecordDollId", String.valueOf(awardRecordDollId));
		params.put("auditStatus", String.valueOf(auditStatus));
		params.put("auditRemark", auditRemark);
		params.put("status", String.valueOf(status));
		return update(params);
	}
    
    /**
     * 更新夹娃娃领取信息
     * @param awardRecordDollId
     * @param auditStatus
     * @param status
     * @return
     */
	public static boolean update(int awardRecordDollId, String gameId, String gameName, String gameUid){
		Map params = new HashMap();
		params.put("awardRecordDollId", String.valueOf(awardRecordDollId));
		params.put("auditStatus", String.valueOf(AuditStatus.AUDITING.getStatus()));
		params.put("status", String.valueOf(DollAwardStatus.DRAW.getValue()));
		params.put("gameId", gameId);
		params.put("gameName", gameName);
		params.put("gameUid", gameUid);
		
		return update(params);
	}
	
	public static boolean update(Map params) {
		int id = Integer.parseInt(params.get("awardRecordDollId").toString());
		AwardRecordDollDDL awardRecordDoll = AwardRecordDollService.getById(id);
		boolean flag = false;
		if (awardRecordDoll != null) {
			if (params.get("userName") != null) {
				awardRecordDoll.setUserName(params.get("userName").toString());
			}
			if (params.get("addr") != null) {
				awardRecordDoll.setAddr(params.get("addr").toString());
			}
			if (params.get("mobile") != null) {
				awardRecordDoll.setMobile(params.get("mobile").toString());
			}
			if (params.get("QQ") != null) {
				awardRecordDoll.setQq(params.get("QQ").toString());
			}
			if (params.get("gameId") != null) {
				awardRecordDoll.setGameId(params.get("gameId").toString());
			}
			if (params.get("gameName") != null) {
				awardRecordDoll.setGameName(params.get("gameName").toString());
			}
			if (params.get("gameUid") != null) {
				awardRecordDoll.setGameUid(params.get("gameUid").toString());
			}
			if (params.get("auditStatus") != null) {
				awardRecordDoll.setAuditStatus(Integer.parseInt(params.get("auditStatus").toString()));
			}
			if (params.get("auditRemark") != null) {
				awardRecordDoll.setAuditRemark(params.get("auditRemark").toString());
			}
			if (params.get("status") != null) {
				awardRecordDoll.setStatus(Integer.parseInt(params.get("status").toString()));
			}
			flag = AwardRecordDollService.update(awardRecordDoll);
		}
		return flag;
	}
	public static boolean update(AwardRecordDollDDL awardRecordDoll) {
		Condition cond = new Condition("AwardRecordDollDDL.id", "=", awardRecordDoll.getId());
		StringBuilder updated = new StringBuilder("AwardRecordDollDDL.userName,");
		updated.append("AwardRecordDollDDL.addr,");
		updated.append("AwardRecordDollDDL.qq,");
		updated.append("AwardRecordDollDDL.mobile,");
		updated.append("AwardRecordDollDDL.gameId,");
		updated.append("AwardRecordDollDDL.gameUid,");
		updated.append("AwardRecordDollDDL.gameName,");
		updated.append("AwardRecordDollDDL.auditStatus,");
		updated.append("AwardRecordDollDDL.auditRemark,");
		updated.append("AwardRecordDollDDL.updateTime,");
		updated.append("AwardRecordDollDDL.status");
		return Dal.update(awardRecordDoll, updated.toString(), cond) > 0 ? true : false;
	}
	public static boolean update(String orderId ,String couponId){
		Logger.info(">>>>>>优惠卷信息 orderId=%s,couponId=%s", orderId,couponId);
		List<SqlParam> params = new ArrayList<SqlParam>();
    	params.add(new SqlParam("AwardRecordDollDDL.baseCouponId", couponId));
    	params.add(new SqlParam("AwardRecordDollDDL.zhifuOrderId", orderId));
    	// 此处不使用下面注释的代码，是因为如果该方法在一个事务中被调用，事务对注释代码是起不了作用的
		return Dal.executeNonQuery(AwardRecordDollDDL.class, "UPDATE award_record_doll SET base_coupon_id = ?,update_time=now() WHERE zhifu_order_id = ?", params, null)>0;
//		return Db.update("UPDATE award_record_doll SET base_coupon_id = ?,update_time=now() WHERE zhifu_order_id = ?",couponId,orderId)>0;
	}
}
