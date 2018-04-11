package moudles.award.service;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.common.base.Strings;
import com.jfinal.plugin.activerecord.Db;

import common.dao.QueryConnectionAdvHandler;
import common.dao.QueryConnectionHandler;
import constants.GlobalConstants;
import constants.MessageCode;
import exception.BusinessException;
import jws.Logger;
import jws.dal.Dal;
import jws.dal.common.SqlParam;
import jws.dal.sqlbuilder.Condition;
import jws.dal.sqlbuilder.Sort;
import jws.module.constants.award.AwardStatus;
import jws.module.constants.mng.AuditStatus;
import jws.module.response.award.AwardDto;
import jws.module.response.award.AwardRecentDollDto;
import jws.module.response.award.AwardRecordDto;
import moudles.award.ddl.AwardDDL;
import moudles.award.ddl.AwardRecordDDL;
import utils.CopyDDLUtil;
import utils.DaoUtil;
import utils.DistributeCacheLock;

public class AwardService {

	private static DistributeCacheLock lock = DistributeCacheLock.getInstance();

	/**
	 * 根据类型获取资讯列表
	 * 
	 * @param size
	 *            读取条数，小于等于0为不限制
	 * @return
	 */
	public static List<AwardRecordDto> listAwardRecords4Index(int size) {
		StringBuffer sql = new StringBuffer(
				"select a.award_id awardId,a.uid,a.game_id gameId, a.game_name gameName, b.name awardName,b.happy_bean happyBean, b.type from award_record a ");
		sql.append(" left join award b on a.award_id = b.id");
		sql.append(" order by a.create_time desc");
		if (size > 0) {
			sql.append(" limit 0,").append(size);
		}
		AwardRecordDto dto = new AwardRecordDto();
		List<AwardRecordDto> result = Dal.getConnection(GlobalConstants.dbSource, new QueryConnectionHandler(dto, sql.toString()));
		return result;
	}

	/**
	 * 领奖记录临时表（运营线准备好100条数据，每天从里面读不同20条数据）
	 * 
	 * @return
	 * @throws Exception
	 */
	public static List<AwardRecordDto> listAwardRecords4Tmp() {

		Calendar ca = Calendar.getInstance();
		int currentDay = ca.get(Calendar.DAY_OF_YEAR);
		StringBuffer sql = new StringBuffer("select awardName,userName nickname,awardPic imgUrl from award_record_tmp");
		sql.append(" order by id");
		int pageNo = currentDay % 5;
		sql.append(" limit ").append(pageNo * 20).append(", ").append(20);
		
		AwardRecordDto dto = null;
		List<AwardRecordDto> list = new ArrayList<AwardRecordDto>();
		ResultSet result = Dal.executeQuery(GlobalConstants.dbSource, sql.toString());
		if (result != null) {
			try {
				while (result.next()) {
					dto = new AwardRecordDto();
					dto.setAwardName(result.getString("awardName"));
					dto.setNickname(result.getString("nickname"));
					dto.setImgUrl(result.getString("imgUrl"));
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
//		List<AwardRecordDto> result = Dal.getConnection(GlobalConstants.dbSource, new QueryConnectionHandler(dto, sql.toString()));
		return list;
	}

	/**
	 * 
	 * 随机读取count条记录
	 * 
	 * @param count
	 * @return
	 */
	public static List<AwardRecentDollDto> randListAwardRecordsTmp(int count) {
		StringBuffer sql = new StringBuffer("select awardName,userName ,awardPic awardImgUrl,mobile from award_record_tmp order by rand() limit ");
		sql.append(count);
		AwardRecentDollDto dto = new AwardRecentDollDto();
		List<AwardRecentDollDto> result = Dal.getConnection(GlobalConstants.dbSource, new QueryConnectionHandler(dto, sql.toString()));
		return result;
	}

	/**
	 * 奖品列表
	 * 
	 * @param status
	 *            (1-上线，-1-下线) -99则不限制
	 * @param type
	 *            (奖品类型1=实物 2=话费充值 3=Q币 4=代金券) -99则不限制
	 * @param isHasStored
	 *            是否有库存，true-库存大于0， false-全部
	 * @return
	 */
	public static List<AwardDto> listAward(int status, int[] types, boolean isHasStored, int scope,int isShowIndex) throws BusinessException {
		List<AwardDto> result = new ArrayList<AwardDto>();
		try {
			Condition cond = new Condition("AwardDDL.id", ">", 0);
			if (-99 != status) {
				cond.add(new Condition("AwardDDL.status", "=", status), "AND");
			}
			if (scope > 0) {
				cond.add(new Condition("AwardDDL.scope", "=", scope), "AND");
			}
			if (isShowIndex > 0) {
				cond.add(new Condition("AwardDDL.isShowIndex", "=", isShowIndex), "AND");
			}
			if (null != types && types.length > 0) {
				cond.add(new Condition("AwardDDL.type", "in", types), "AND");
			}
			if (isHasStored) {
				cond.add(new Condition("AwardDDL.storeNum", ">", 0), "AND");
			}
			List<AwardDDL> list = Dal.select(DaoUtil.genAllFields(AwardDDL.class), cond, new Sort("AwardDDL.sort", true), 0, -1);
			if (null != list && list.size() > 0) {
				for (AwardDDL ddl : list) {
					AwardDto dto = new AwardDto();
					CopyDDLUtil copy = new CopyDDLUtil(ddl, dto);
					copy.copy();
					result.add(dto);
				}
			}
		} catch (Exception e) {
			Logger.error(e, "");
			throw new BusinessException(MessageCode.ERROR_CODE_500, "系统内部异常");
		}
		return result;
	}

	/**
	 * 领奖中心首页奖品列表（排序：库存非零 > 库存零 , 相同则按sort字段升序排）
	 * 
	 * @param status
	 *            (1-上线，-1-下线) -99则不限制
	 * @param type
	 *            (奖品类型1=实物 2=话费充值 3=Q币 4=代金券) -99则不限制
	 * @param isHasStored
	 *            是否有库存，true-库存大于0， false-全部
	 * @return
	 */
	public static List<AwardDto> getList4AwardCenter(int status, int[] types, boolean isHasStored, int scope) throws BusinessException {
		List<AwardDto> result = new ArrayList<AwardDto>();
		try {
			Condition cond = new Condition("AwardDDL.id", ">", 0);
			if (-99 != status) {
				cond.add(new Condition("AwardDDL.status", "=", status), "AND");
			}
			if (scope > 0) {
				cond.add(new Condition("AwardDDL.scope", "=", scope), "AND");
			}
			if (null != types && types.length > 0) {
				cond.add(new Condition("AwardDDL.type", "in", types), "AND");
			}
			if (isHasStored) {
				cond.add(new Condition("AwardDDL.storeNum", ">", 0), "AND");
			}
			// List<AwardDDL> list =
			// Dal.select(DaoUtil.genAllFields(AwardDDL.class), cond, new
			// Sort("AwardDDL.sort",true),0, -1);

			StringBuffer sql = new StringBuffer(
					"select id,name,happy_bean happyBean,status,type,total_num totalNum,exchage_num exchageNum,store_num storeNum,img_url imgUrl from award  where ").append(cond
					.getSql());
			// sql.append(" order by store_num desc, sort");
			sql.append(" order by store_num > 0 desc, sort");
			Logger.info("**************sql********%s", sql.toString());
			AwardDto awardDto = new AwardDto();
			result = Dal.getConnection(GlobalConstants.dbSource, new QueryConnectionAdvHandler(awardDto, sql.toString(), cond.getSqlParams()));

		} catch (Exception e) {
			Logger.error(e, "");
			throw new BusinessException(MessageCode.ERROR_CODE_500, "系统内部异常");
		}
		return result;
	}

	/**
	 * 读取奖品对象
	 * 
	 * @param awardId
	 * @return
	 */
	public static AwardDDL getAwardById(int awardId) throws BusinessException {
		AwardDDL ddl = null;
		// ddl = (AwardDDL) Cache.get("AwardDDL-" + awardId);
		// if (ddl != null) {
		// return ddl;
		// }
		try {
			Condition cond = new Condition("AwardDDL.id", "=", awardId);
			List<AwardDDL> list = Dal.select(DaoUtil.genAllFields(AwardDDL.class), cond, null, 0, 1);
			if (null != list && list.size() > 0) {
				ddl = list.get(0);
			}
		} catch (Exception e) {
			Logger.error(e, "");
			throw new BusinessException(MessageCode.ERROR_CODE_500, "系统内部异常");
		}
		// if (ddl != null) {
		// Cache.set("AwardDDL-" + awardId, ddl, "3h");
		// }
		return ddl;
	}

	/**
	 * 生成领奖记录
	 * 
	 * @param params
	 * @return map key={result} result:true, 成功 result:false ,失败
	 * @throws BusinessException
	 */
	public static Map<String, String> createAwardRecord(int awardId, String orderId, int uid, int sourceType, String sourceDesc,
			String remark, Integer auditStatus, String baseCouponId) throws BusinessException {
		Map params = new HashMap();
		params.put("awardId", String.valueOf(awardId));
		params.put("orderId", orderId);
		params.put("uid", String.valueOf(uid));
		params.put("sourceType", String.valueOf(sourceType));
		params.put("sourceDesc", sourceDesc);
		params.put("remark", remark);
		params.put("baseCouponId", baseCouponId);
		if (auditStatus != null) {
			params.put("auditStatus", auditStatus + "");
		}
		return createAwardRecord(params);
	}
	
	/**
	 * 更新代金券奖品使用数据
	 * @param id
	 * @param gameId
	 * @param gameName
	 * @param gameUid
	 * @return
	 */
	public static boolean updateAwardRecord(int id, String gameId, String gameName, String gameUid){
		Logger.info("更新领奖记录状态：id=%s,gameId=%s,gameName=%s,gameUid=%s",id, gameId, gameName, gameUid);
		
		List<SqlParam> params = new ArrayList<SqlParam>();
    	params.add(new SqlParam("AwardRecordDDL.auditStatus", AuditStatus.AUDITING.getStatus()));
    	params.add(new SqlParam("AwardRecordDDL.gameId", gameId));
    	params.add(new SqlParam("AwardRecordDDL.gameName", gameName));
    	params.add(new SqlParam("AwardRecordDDL.gameUid", gameUid));
    	params.add(new SqlParam("AwardRecordDDL.id", id));
    	// 此处不使用下面注释的代码，是因为如果该方法在一个事务中被调用，事务对注释代码是起不了作用的
		return Dal.executeNonQuery(AwardRecordDDL.class, "update award_record set audit_status=?,game_id=?,game_name=?,game_uid=?,update_time=now() where id=? ", params, null)>0;
//		return Db.update("update award_record set audit_status=?,game_id=?,game_name=?,game_uid=?,update_time=now() where id=? ", 
//				AuditStatus.AUDITING.getStatus(), gameId, gameName, gameUid, id) > 0;
		
	}
	public static Map<String, String> createAwardRecord(Map<String, String> params) throws BusinessException {
		Map<String, String> result = new HashMap<String, String>();
		result.put("result", "false");
		if (params == null || params.size() == 0) {
			throw new BusinessException(MessageCode.ERROR_CODE_500, "参数校验不通过");
		}
		if (!params.containsKey("awardId") || !params.containsKey("orderId") || !params.containsKey("uid")) {
			throw new BusinessException(MessageCode.ERROR_CODE_500, "参数校验不通过");
		}

		int uid = Integer.parseInt(params.get("uid"));
		try {

			if (!lock.tryCacheLock("awardRecord-" + uid, "locked", "2s")) {
				throw new BusinessException(MessageCode.ERROR_CODE_500, "重复领奖");
			}

			int awardId = Integer.parseInt(params.get("awardId").toString());
			String orderId = params.get("orderId").toString();

			AwardRecordDDL record = new AwardRecordDDL();
			record.setUid(uid);
			record.setZhifuOrderId(orderId);
			record.setAwardId(awardId);
			if(params.containsKey("auditStatus") && params.get("auditStatus") != null){
				record.setAuditStatus(Integer.parseInt(params.get("auditStatus").toString()));
			}else{
				record.setAuditStatus(AuditStatus.AUDITING.getStatus());
			}
			record.setCreateTime(System.currentTimeMillis());
			record.setUpdateTime(System.currentTimeMillis());
			if (params.containsKey("userName")) {
				record.setUserName(params.get("userName"));
			}
			if (params.containsKey("addr")) {
				record.setAddr(params.get("addr"));
			}
			if (params.containsKey("mobile")) {
				record.setMobile(params.get("mobile"));
			}
			if (params.containsKey("QQ")) {
				record.setQQ(params.get("QQ"));
			}
			if (params.containsKey("gameId") ) {
				record.setGameId(params.get("gameId"));	
			}
			if (params.containsKey("gameName")) {
				record.setGameName(params.get("gameName"));
			}
			if (params.containsKey("gameUid")) {
				record.setGameUid(params.get("gameUid"));
			}
			if (params.containsKey("sourceDesc")) {
				record.setSourceDesc(params.get("sourceDesc"));
			}
			if (params.containsKey("sourceType")) {
				record.setSourceType(Integer.parseInt(params.get("sourceType")));
			}
			if (params.containsKey("remark")) {
				record.setRemark(params.get("remark"));
			}
			if (params.containsKey("auditStatus")) {
				record.setAuditStatus(Integer.parseInt(params.get("auditStatus")));
			}
			if (params.containsKey("auditRemark")) {
				record.setAuditRemark(params.get("auditRemark"));
			}
			if(params.get("baseCouponId") != null){
				record.setBaseCouponId(params.get("baseCouponId"));
			}
			record.setOpName("");

			long lastId = Dal.insertSelectLastId(record);
			if (lastId <= 0) {
				throw new BusinessException(MessageCode.ERROR_CODE_500, "创建领奖记录失败");
			}

			if (!lock.tryCacheLock("award-" + awardId, "award-locked", "1s")) {
				throw new BusinessException(MessageCode.ERROR_CODE_500, "系统繁忙");
			}
			// 减少库存,增加兌换量
			AwardDDL award = getAwardById(awardId);
			award.setStoreNum(award.getStoreNum() - 1);
			award.setExchageNum(award.getExchageNum() + 1);
			Dal.update(award, "AwardDDL.storeNum,AwardDDL.exchageNum", new Condition("AwardDDL.id", "=", awardId));
			result.put("result", "true");
			return result;
		} catch (Exception e) {
			Logger.error(e, "");
		} finally {
			lock.cacheUnLock("awardRecord-" + uid);
		}
		return null;
	}

	/**
	 * 获取夹娃娃奖品列表
	 */
	public static List<AwardDto> listAwards4Doll(int gameLevel, int page, int pageSize) throws BusinessException {
		List<AwardDto> result = new ArrayList<AwardDto>();
		try {
			Condition cond = new Condition("AwardDDL.id", ">", 0);
			cond.add(new Condition("AwardDDL.status", "=", 1), "AND");
			cond.add(new Condition("AwardDDL.scope", "=", 1), "AND");
			if (gameLevel > 0) {
				cond.add(new Condition("AwardDDL.gameLevel", "=", gameLevel), "AND");
			}

			StringBuffer sql = new StringBuffer("select id,name,type, img_url imgUrl from award where ").append(cond.getSql());
			sql.append(" order by id desc");
			if (pageSize > 0) {
				sql.append(" limit ").append((page - 1) * pageSize).append(" , ").append(pageSize);
			}
			Logger.info("**************sql********%s", sql.toString());
			AwardDto awardDto = new AwardDto();
			result = Dal.getConnection(GlobalConstants.dbSource, new QueryConnectionAdvHandler(awardDto, sql.toString(), cond.getSqlParams()));

		} catch (Exception e) {
			Logger.error(e, "");
			throw new BusinessException(MessageCode.ERROR_CODE_500, "系统内部异常");
		}
		return result;
	}

	/**
	 * 根据开心豆获取奖品名字
	 */
	public static List<String> listAwardNameByBean(int bean, int scope, int count) {
		Condition cond = new Condition("AwardDDL.scope", "=", scope);
		boolean sortMethod = false;
		if(bean != 0){
			cond.add(new Condition("AwardDDL.happyBean", "<=", bean), "and");
		}else{
			cond.add(new Condition("AwardDDL.happyBean", ">", 1), "and");
			sortMethod = true;
		}
		cond.add(new Condition("AwardDDL.status", "=", AwardStatus.ONLINE.getStatus()), "and");
		
		Sort sort = new Sort("AwardDDL.happyBean", sortMethod);

		List<AwardDDL> awards = Dal.select("AwardDDL.name", cond, sort, 0, count);
		List<String> list = new ArrayList<String>();

		for (AwardDDL award : awards) {
			list.add(award.getName());
		}
		return list;
	}
	
	/**
	 * 根据开心豆获取奖品列表
	 */
	public static List<AwardDDL> listAwardByBean(int bean, int scope, int count,boolean sortMethod) {
		Condition cond = new Condition("AwardDDL.scope", "=", scope);
		cond.add(new Condition("AwardDDL.happyBean", ">=", bean), "and");
		cond.add(new Condition("AwardDDL.status", "=", AwardStatus.ONLINE.getStatus()), "and");
		cond.add(new Condition("AwardDDL.storeNum", ">", 0), "and");
		
		Sort sort = new Sort("AwardDDL.happyBean", sortMethod);
		List<AwardDDL> awards = Dal.select(DaoUtil.genAllFields(AwardDDL.class), cond, sort, 0, count);
		
		return awards;
	}
}
