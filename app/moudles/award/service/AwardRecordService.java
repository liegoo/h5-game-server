package moudles.award.service;

import java.util.ArrayList;
import java.util.List;

import com.google.common.base.Strings;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;

import common.dao.QueryConnectionHandler;
import constants.GlobalConstants;
import jws.Logger;
import jws.dal.Dal;
import jws.dal.common.SqlParam;
import jws.module.response.award.AwardRecordDto;
import moudles.award.ddl.AwardRecordDDL;
import moudles.award.ddl.AwardRecordDollDDL;

public class AwardRecordService {
	/**
	 * 获取领奖记录
	 * @param 
	 * @return
	 */
	public static List<AwardRecordDto> listAwardRecords(int uid,int page,int pageSize){
		StringBuffer sql = new StringBuffer("select a.award_id awardId,a.uid,a.game_id gameId, a.game_name gameName,a.game_uid gameUid,a.mobile mobile,a.qq qq,");
		sql.append("a.user_name nickname, a.source_desc sourceDesc,a.audit_status status, a.addr addr ,UNIX_TIMESTAMP(a.create_time) createTime,a.remark , a.audit_remark auditRemark,");
		sql.append("a.card_no cardNo, a.card_pwd cardPwd, a.deliver_company deliverCompany, a.deliver_no deliverNo,");
		sql.append("b.name awardName,b.happy_bean happyBean,b.img_url imgUrl,b.type, c.down_url gameDownUrl from award_record a ");
		sql.append(" left join award b on a.award_id = b.id");
		sql.append(" left join coupon_games c on a.game_id = c.game_id");
		sql.append(" where a.uid = ").append(uid);
		sql.append(" order by a.create_time desc");
		if (pageSize > 0) {
			sql.append(" limit ")
			   .append((page-1)*pageSize)
			   .append(" , ")
			   .append(pageSize);
		}
		AwardRecordDto dto = new AwardRecordDto();
		List<AwardRecordDto> result = Dal.getConnection(GlobalConstants.dbSource,new QueryConnectionHandler(dto,sql.toString()));
		return result;
	}
	
	/**
	 * 根据订单ID获取夹娃娃获奖记录
	 * @param orderId
	 * @return
	 */
	public static Record getAwardRecord(String orderId){
		if(Strings.isNullOrEmpty(orderId)){
			return null;
		}
		return Db.findFirst("select * from award_record where zhifu_order_id=? limit 1 ", orderId);
	}
	
	/**
	 * 根据代金券ID获取中奖记录
	 * @param baseCouponId
	 * @return
	 */
	public static Record getAwardRecordByCouponId(String baseCouponId){
		if(Strings.isNullOrEmpty(baseCouponId)){
			return null;
		}
		return Db.findFirst("select * from award_record where base_coupon_id=? limit 1 ", baseCouponId);
	}
	
	/**
	 * 更新奖品记录
	 * @param couponId
	 * @return
	 */
//	public static boolean updateAwardRecord(String couponId,String orderId){
//		if(Strings.isNullOrEmpty(couponId)||Strings.isNullOrEmpty(orderId)){
//			return false;
//		}
//		List<SqlParam> params = new ArrayList<SqlParam>();
//    	params.add(new SqlParam("AwardRecordDDL.baseCouponId", couponId));
//    	params.add(new SqlParam("AwardRecordDDL.zhifuOrderId", orderId));
//    	// 此处不使用下面注释的代码，是因为如果该方法在一个事务中被调用，事务对注释代码是起不了作用的
//		return Dal.executeNonQuery(AwardRecordDDL.class, "UPDATE award_record SET base_coupon_id = ? WHERE zhifu_order_id = ?", params, null)>0;
////		return Db.update("UPDATE award_record SET base_coupon_id = ? WHERE zhifu_order_id = ?",couponId,orderId)>0;
//	}
	
	/**
	 * 更新领奖记录状态
	 * @param id
	 * @param auditStatus
	 * @param status
	 * @param auditRemark
	 * @return
	 */
	public static boolean updateStatus(int id, int auditStatus, String auditRemark){
		Logger.info("更新领奖记录状态：id="+id+",auditStatus="+auditStatus+",auditRemark="+auditRemark);
		
		List<SqlParam> params = new ArrayList<SqlParam>();
    	params.add(new SqlParam("AwardRecordDDL.auditStatus", auditStatus));
    	params.add(new SqlParam("AwardRecordDDL.auditRemark", Strings.nullToEmpty(auditRemark)));
    	params.add(new SqlParam("AwardRecordDDL.id", id));
    	// 此处不使用下面注释的代码，是因为如果该方法在一个事务中被调用，事务对注释代码是起不了作用的
		return Dal.executeNonQuery(AwardRecordDDL.class, "update award_record set audit_status=?,audit_remark=?,update_time=now() where id=? ", params, null)>0;
//		return Db.update("update award_record set audit_status=?,audit_remark=?,update_time=now() where id=? ", 
//				auditStatus, Strings.nullToEmpty(auditRemark), id) > 0;
	}
}
