package moudles.checkin;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import constants.MessageCode;
import exception.BusinessException;
import jws.Jws;
import jws.Logger;
import jws.dal.Dal;
import jws.dal.sqlbuilder.Condition;
import jws.module.constants.member.MemberLogOpResult;
import jws.module.constants.member.MemberLogOpType;
import moudles.member.ddl.MemberDDL;
import moudles.member.ddl.MemberLogDDL;
import moudles.member.service.MemberLogService;
import moudles.member.service.MemberService;
import utils.DateUtil;
import utils.DistributeCacheLock;
import utils.KeyPointLogUtil;

public class CheckInService {

	private static String[] dayBeans = Jws.configuration.getProperty("checkin.day.bean", "88,188,200,200,288,288").split(",");
	private static String[] randomBeans = Jws.configuration.getProperty("checkin.random.bean", "300,400").split(",");
	private static DistributeCacheLock lock = DistributeCacheLock.getInstance();

	/**
	 * 用户签到信息
	 * 
	 * @param uid
	 * @return
	 * @throws BusinessException
	 */
	public static boolean[] getCheckInData(int uid) throws BusinessException {

		boolean[] result = new boolean[] { false, false, false, false, false, false, false, false };
		MemberDDL member = MemberService.getMemberByUid(uid);
		if (member == null) {
			throw new BusinessException(MessageCode.ERROR_CODE_500, "用户信息不存在");
		}

		// 第一次签到
		if (member.getContinueCheckIn() == 0 || member.getLastCheckInTime() == null || member.getLastCheckInTime() <= 0) {
			result[0] = true;
			return result;
		}

		int continueCheck = member.getContinueCheckIn();

		String yesterday = DateUtil.getYesterdayDate("yyyy-MM-dd");
		String lastCheckday = DateUtil.formatDate(member.getLastCheckInTime(), "yyyy-MM-dd");
		if (!yesterday.equals(lastCheckday)) {
			if (continueCheck < 6) {// 断签
				result[0] = true;
			} else {
				result[6] = true;
				result[7] = true;// 用来标记是否续签6天以上
			}
			return result;
		}

		// 续签
		if (continueCheck > 6) {
			result[6] = true;// 续签6天以上都算天
			result[7] = true;// 用来标记是否续签6天以上
		} else {
			result[continueCheck] = true;
		}

		return result;
	}

	/**
	 * 签到
	 * 
	 * @param uid
	 * @return
	 */
	public static int checkIn(int uid) {
		try {
			if (!lock.tryCacheLock("checkin_" + uid, "", "1mn")) {
				throw new BusinessException(MessageCode.ERROR_CODE_500, "您已经签过了");
			}

			MemberDDL member = MemberService.getMemberByUid(uid);
			if (member == null) {
				throw new BusinessException(MessageCode.ERROR_CODE_500, "用户信息不存在");
			}
			int balance = member.getHappyBean() + member.getHappyBeanFromOp();

			// 非续签
			int day = 0;
			// 第一次checkin
			if (member.getContinueCheckIn() == 0 || member.getLastCheckInTime() == null || member.getLastCheckInTime() <= 0) {
				day = 1;
			} else {

				String lastCheckday = DateUtil.formatDate(member.getLastCheckInTime(), "yyyy-MM-dd");
				String yesterday = DateUtil.getYesterdayDate("yyyy-MM-dd");
				String today = DateUtil.getCurrentDate("yyyy-MM-dd");

				if (today.equals(lastCheckday)) {
					throw new BusinessException(MessageCode.ERROR_CODE_500, "您已经签过了");
				}

				if (yesterday.equals(lastCheckday)) {// 续签
					day = member.getContinueCheckIn() + 1;
				} else {// 断签 ,是否历史已经完成6次续签
					day = member.getContinueCheckIn() >= 6 ? member.getContinueCheckIn() + 1 : 1;
				}
			}

			// 计算bean
			int addBean = 0;
			if (day >= 7) {
				// 判断是否最近2天有消费记录
				List<MemberLogDDL> memberLogs = MemberLogService.getMemberLogList(uid, MemberLogOpType.CONSUME.getType(), -1, DateUtil.addDay(System.currentTimeMillis(), -2),
						System.currentTimeMillis(), 1, -1);
				if (memberLogs == null || memberLogs.size() == 0) {
					addBean = 0;
				} else {
					int max = Integer.parseInt(randomBeans[1]);
					int min = Integer.parseInt(randomBeans[0]);
					;
					Random random = new Random();
					addBean = random.nextInt(max) % (max - min + 1) + min;
				}
			}
			switch (day) {
			case 1:
				addBean = Integer.parseInt(dayBeans[0]);
				break;
			case 2:
				addBean = Integer.parseInt(dayBeans[1]);
				break;
			case 3:
				addBean = Integer.parseInt(dayBeans[2]);
				break;
			case 4:
				addBean = Integer.parseInt(dayBeans[3]);
				break;
			case 5:
				addBean = Integer.parseInt(dayBeans[4]);
				break;
			case 6:
				addBean = Integer.parseInt(dayBeans[5]);
				break;
			}

			member.setContinueCheckIn(day);
			member.setLastCheckInTime(System.currentTimeMillis());
//			member.setHappyBean(member.getHappyBean() + addBean);
			member.setHappyBeanFromOp(member.getHappyBeanFromOp() + addBean);

			int count = Dal.update(member, "MemberDDL.continueCheckIn,MemberDDL.lastCheckInTime,MemberDDL.happyBeanFromOp", new Condition("MemberDDL.id", "=", member.getId()));

			Logger.info("uid %s check in status = %s,add bean %s", member.getUid(), count, addBean);

			if (count == 0) {
				KeyPointLogUtil.log("Check in addBean fail,uid=%s,bean=%s", member.getUid(), addBean);
			} else {
				balance += addBean;
			}

			Map<String, String> params = new HashMap<String, String>();
			params.put("happyBean", String.valueOf(addBean));
			params.put("remark", "签到奖励");
			MemberLogService.createMemberLog(uid, MemberLogOpResult.SUCCESS.getType(), MemberLogOpType.CHECKIN.getType(), balance, params);

			return addBean;
		} catch (Exception e) {
			Logger.error(e, "");
		} finally {
			lock.cacheUnLock("checkin_" + uid);
		}
		return 0;
	}

	/**
	 * 用户是否完成签到
	 * 
	 * @param uid
	 * @return
	 */
	public static boolean isChecked(int uid) {
		try {
			MemberDDL member = MemberService.getMemberByUid(uid);
			if (member == null) {
				throw new BusinessException(MessageCode.ERROR_CODE_500, "用户信息不存在");
			}

			if (member.getContinueCheckIn() == 0 || member.getLastCheckInTime() == null || member.getLastCheckInTime() <= 0) {
				return false;
			} else {

				String lastCheckday = DateUtil.formatDate(member.getLastCheckInTime(), "yyyy-MM-dd");
				String today = DateUtil.getCurrentDate("yyyy-MM-dd");

				if (today.equals(lastCheckday)) {
					return true;
				}
			}

			return false;

		} catch (Exception e) {
			Logger.error(e, "");
		}
		return false;
	}
	/*
	 * public static void main(String[] args){
	 * System.out.println(String.format("%.2f", 21212.36412)); }
	 */
}
