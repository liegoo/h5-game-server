package controllers.ucgc;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import common.core.UcgcController;
import exception.BusinessException;
import moudles.activity.ddl.RechargePresentedActivityDDL;
import moudles.activity.ddl.RechargePresentedRuleDDL;
import moudles.activity.service.ActivityService;

public class Activity extends UcgcController{
	
	/**
	 * 获取充值活动赠送列表
	 */
	public static void getPresentedList() throws BusinessException{
		Map params = getDTO(Map.class);
		List<Double> beans = (List<Double>)params.get("beans");
		int uid = Integer.parseInt(params.get("uid").toString());
		Map<Integer,Integer> result = new HashMap<Integer,Integer>();
		for(Double bean:beans){
			if(bean == null || bean <=0) continue;
			RechargePresentedRuleDDL rule = ActivityService.getRechargePresentedRule(bean.intValue(),uid);
			result.put(bean.intValue(), (rule==null || rule.getPresentedBean()==null)?0:rule.getPresentedBean());
		}
		getHelper().returnSucc(result);
	}
	
	
	/**
	 * 获取赠送上限
	 */
	public static void getUpperLimit(){
		Map params = getDTO(Map.class);
		int bean = Integer.parseInt(params.get("bean").toString());
		int upperLimit = ActivityService.getUpperLimitByBean(bean);
		getHelper().returnSucc(upperLimit);
	}
	
}
