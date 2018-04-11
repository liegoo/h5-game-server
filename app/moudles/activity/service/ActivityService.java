package moudles.activity.service;

import java.util.List;

import exception.BusinessException;
import jws.dal.Dal;
import jws.dal.sqlbuilder.Condition;
import jws.dal.sqlbuilder.Sort;
import jws.module.constants.mng.DataStatus;
import moudles.activity.ddl.RechargePresentedActivityDDL;
import moudles.activity.ddl.RechargePresentedRuleDDL;
import moudles.member.service.MemberLogService;
import utils.DaoUtil;
import utils.KeyPointLogUtil;

/**
 * 活动相关
 * 
 * @author fish
 *
 */
public class ActivityService {

	/**
	 * 根据主见查询赠送活动
	 * 
	 * @param id
	 * @return
	 */
	public static RechargePresentedActivityDDL getRechargePresentedActivity(int id) {
		return Dal.select(DaoUtil.genAllFields(RechargePresentedActivityDDL.class), id);
	}

	/**
	 * 根据活动类型获取有效的活动列表
	 * 
	 * @param type
	 * @return
	 */
	public static List<RechargePresentedActivityDDL> getActivityList(int type, long opTime) {
		if (type <= 0)
			return null;
		if (opTime <= 0)
			return null;

		Condition cond = new Condition("RechargePresentedActivityDDL.type", "=", type);
		cond.add(new Condition("RechargePresentedActivityDDL.status", "=", type), "and");
		cond.add(new Condition("RechargePresentedActivityDDL.effectTime", "<=", opTime), "and");
		cond.add(new Condition("RechargePresentedActivityDDL.expireTime", ">=", opTime), "and");
		return Dal.select(DaoUtil.genAllFields(RechargePresentedActivityDDL.class), cond, null, 0, -1);
	}

	/**
	 * 根据豆子数查询赠送规则
	 * 
	 * @param beans
	 * @return
	 * @throws BusinessException
	 */
	public static RechargePresentedRuleDDL getRechargePresentedRule(int bean, int uid) throws BusinessException {
		return getRechargePresentedRule(bean, uid, System.currentTimeMillis());
	}

	/**
	 * 根据豆子数查询赠送规则
	 * 
	 * @param beans
	 * @return
	 * @throws BusinessException
	 */
	public static RechargePresentedRuleDDL getRechargePresentedRule(int bean, int uid, long opTime) throws BusinessException {
		if (bean == 0) {
			return null;
		}

		List<RechargePresentedActivityDDL> activities = getActivityList(1, opTime);// 获取充值送开心豆活动列表
		if (activities == null || activities.size() == 0) {// 当前无充值送活动
			return null;
		}
		// 只支持一个活动，多条取第一条
		RechargePresentedActivityDDL activity = activities.get(0);

		// 获取该活动下的赠送规则
		Condition ruleCond = new Condition("RechargePresentedRuleDDL.rechargeBean", "<=", bean);
		ruleCond.add(new Condition("RechargePresentedRuleDDL.rpaId", "=", activity.getId()), "and");
		List<RechargePresentedRuleDDL> rules = Dal.select(DaoUtil.genAllFields(RechargePresentedRuleDDL.class), ruleCond, new Sort("RechargePresentedRuleDDL.rechargeBean", false),
				0, 1);
		if (rules == null || rules.size() == 0) {
			return null;
		}
		RechargePresentedRuleDDL rule = rules.get(0);

		int presented = MemberLogService.presentedBeans(uid, activity.getId());
		int upLimit = activity.getUpperLimit() == null ? 0 : activity.getUpperLimit();

		// 超过上限则不赠送
		if (presented + rule.getPresentedBean() > upLimit) {
			KeyPointLogUtil.log("recharge presented upperLimit,uid=%s,bean=%s,presented=%s,upperLimit=%s,rpaId=%s", uid, bean, presented, upLimit, activity.getId());
			return null;
		}

		return rule;
	}

	/**
	 * 根据充值数 获取充值上限
	 * 
	 * @param bean
	 * @return
	 */
	public static int getUpperLimitByBean(int bean) {
		if (bean == 0) {
			return 0;
		}
		long opTime = System.currentTimeMillis();
		List<RechargePresentedActivityDDL> activities = getActivityList(1, opTime);// 获取充值送开心豆活动列表
		if (activities == null || activities.size() == 0) {// 当前无充值送活动
			return 0;
		}
		// 只支持一个活动，多条取第一条
		RechargePresentedActivityDDL activity = activities.get(0);
		int upLimit = activity.getUpperLimit() == null ? 0 : activity.getUpperLimit();
		return upLimit;
	}

}
