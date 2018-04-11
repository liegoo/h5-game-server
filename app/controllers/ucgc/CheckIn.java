package controllers.ucgc;

import java.util.Map;

import common.core.UcgcController;
import exception.BusinessException;
import moudles.checkin.CheckInService;
import moudles.member.service.MemberService;

/**
 * 签到相关 
 */
public class CheckIn extends UcgcController{
	
	/**
	 * 获取连续签到记录
	 */
	public static void getCheckInData() throws BusinessException{
		Map params = getDTO(Map.class);
		int uid = Integer.parseInt(params.get("uid").toString());
		boolean[] result = CheckInService.getCheckInData(uid);
		getHelper().returnSucc(result);
	}
	
	/**
	 * 签到
	 */
	public static void checkIn(){
		Map params = getDTO(Map.class);
		int uid = Integer.parseInt(params.get("uid").toString()); 
		int result = CheckInService.checkIn(uid);
		
		if(result>0){
			//加vip等级
			MemberService.addScoreForHappyBeanCheckIn(uid);
		}
		
		getHelper().returnSucc(result);
	}
	
	/**
	 * 判断当天是否已签到
	 */
	public static void isChecked(){
		Map params = getDTO(Map.class);
		int uid = Integer.parseInt(params.get("uid").toString()); 
		boolean result = CheckInService.isChecked(uid);
		getHelper().returnSucc(result);
	}
}
