package moudles.member.service;

import java.util.List;
import java.util.Map;

import common.dao.DaoHandler;
import constants.MessageCode;
import exception.BusinessException;
import jws.dal.Dal;
import jws.dal.sqlbuilder.Condition;
import jws.dal.sqlbuilder.Sort;
import jws.module.constants.member.MemberLogOpResult;
import jws.module.constants.member.MemberLogOpType;
import jws.mvc.Http;
import moudles.member.ddl.MemberLogDDL;
import utils.DaoUtil;

public class MemberLogService {
	/**
	 * 
	 * @param uid
	 * @param opType
	 * @param startTime
	 * @param endTime
	 * @param page
	 *            从1开始
	 * @param pageSize
	 * @return
	 */
	public static List<MemberLogDDL> getMemberLogList(int uid, int opType, int opResult, long startTime, long endTime, int page, int pageSize) {
		if (uid <= 0) {
			return null;
		}
		if (page == 0) {
			page = 1;
		}
		Condition cond = new Condition("MemberLogDDL.uid", "=", uid);
		cond.add(new Condition("MemberLogDDL.happyBean", "!=", 0), "and");

		if (opType > 0) {
			cond.add(new Condition("MemberLogDDL.opType", "=", opType), "and");
		}
		if (opResult > 0) {
			cond.add(new Condition("MemberLogDDL.opResult", "=", opResult), "and");
		}
		if (startTime > 0 && endTime > 0) {
			cond.add(new Condition("MemberLogDDL.opTime", "<>", startTime, endTime), "and");
		}

		Sort sort = new Sort("MemberLogDDL.id", false);
		return Dal.select(DaoUtil.genAllFields(MemberLogDDL.class), cond, sort, (page - 1) * pageSize, pageSize);
	}

	/**
	 * 日志记录方法
	 * 
	 * @param uid
	 * @param opResult
	 * @param opType
	 * @param balance
	 *            = (happyBean + happyBeanFromOp) 当前操作后的余额
	 * @param params
	 *            扩展参数，非必填的从这里获取
	 * @return
	 */
	public static boolean createMemberLog(int uid, int opResult, int opType, int balance, Map<String, String> params) {
		MemberLogDDL memberLog = new MemberLogDDL();
		memberLog.setUid(uid);
		memberLog.setOpResult(opResult);
		memberLog.setOpType(opType);
		memberLog.setBalance(balance);
		memberLog.setOpTime(System.currentTimeMillis());
		memberLog.setHappyBean((params != null && params.containsKey("happyBean")) && params.get("happyBean") != null ? (int) Double.parseDouble(String.valueOf(params
				.get("happyBean"))) : 0);
		memberLog.setSuid((params != null && params.containsKey("suid")) && params.get("suid") != null ? (int) Double.parseDouble(String.valueOf(params.get("suid"))) : 0);
		memberLog.setGameId((params != null && params.containsKey("gameId")) && params.get("gameId") != null ? (int) Double.parseDouble(String.valueOf(params.get("gameId"))) : 0);
		memberLog.setCpId((params != null && params.containsKey("cpId")) && params.get("cpId") != null ? (int) Double.parseDouble(String.valueOf(params.get("cpId"))) : 0);
		if (Http.Request.current() != null) {
			memberLog.setUserIp(Http.Request.current().args.get("userIp") == null ? "" : Http.Request.current().args.get("userIp").toString());
			memberLog.setCh(Http.Request.current().args.get("channel") == null ? "" : Http.Request.current().args.get("channel").toString());
			memberLog.setUserAgent(Http.Request.current().args.get("userAgent") == null ? "" : Http.Request.current().args.get("userAgent").toString());
		}
		if ((params != null && params.containsKey("channel")) && params.get("channel") != null) {
			memberLog.setCh(params.get("channel"));
		}
		memberLog.setRpaId((params != null && params.containsKey("rpaId")) && params.get("rpaId") != null ? (int) Double.parseDouble(String.valueOf(params.get("rpaId"))) : 0);
		memberLog.setRemark((params != null && params.containsKey("remark")) && params.get("remark") != null ? params.get("remark") : "");
		memberLog.setBillId((params != null && params.containsKey("billId")) && params.get("billId") != null ? String.valueOf(params.get("billId")) : "");
		return Dal.insert(memberLog) > 0;
	}

	/**
	 * 获取用户在活动中充值赠送的豆子数
	 * 
	 * @param uid
	 * @param rpaId
	 * @return
	 * @throws BusinessException
	 */
	public static int presentedBeans(int uid, int rpaId) throws BusinessException {
		if (uid == 0 || rpaId == 0) {
			throw new BusinessException(MessageCode.ERROR_CODE_500, "非法参数");
		}

		Condition cond = new Condition("MemberLogDDL.rpaId", "=", rpaId);
		cond.add(new Condition("MemberLogDDL.uid", "=", uid), "and");
		cond.add(new Condition("MemberLogDDL.opType", "=", MemberLogOpType.PRESENTED.getType()), "and");

		String statSql = new StringBuffer("select SUM(happy_bean) from member_log where 1=1 and ").append(cond.getSql()).toString();
		return Integer.parseInt(DaoHandler.stat(statSql, cond.getSqlParams()));
	}

	/**
	 * 获取开心豆明细数量
	 */
	public static int countLogByType(int opType, int uid, long time) {
		Condition cond = new Condition("MemberLogDDL.uid", "=", uid);
		cond.add(new Condition("MemberLogDDL.opType", "=", opType), "and");
		cond.add(new Condition("MemberLogDDL.opResult", "=", MemberLogOpResult.SUCCESS.getType()), "and");
		if (time > 0) {
			cond.add(new Condition("MemberLogDDL.opTime", ">=", time), "and");
		}
		return Dal.count(cond);
	}
}
