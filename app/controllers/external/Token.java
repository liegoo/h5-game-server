package controllers.external;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import constants.MessageCode;
import exception.BusinessException;
import jws.Logger;
import moudles.member.ddl.MemberSessionDDL;
import moudles.member.service.MemberSessionService;

public class Token extends ExternalController{
	
	public static void get() throws BusinessException{
		Map params = getDTO(Map.class);
		if(!params.containsKey("token") || params.get("token") == null){
			throw new BusinessException(MessageCode.ERROR_CODE_500,"参数非法");
		}
		String tmpToken = params.get("token").toString();
		MemberSessionDDL session = MemberSessionService.getMemberSessionByTmpToken(tmpToken);
		if(session == null || StringUtils.isEmpty(session.getToken()) ){
			throw new BusinessException(MessageCode.ERROR_CODE_500,"用户会话失效，请重新登陆");
		}
		Logger.info("【%s-%s】兑换永久token成功（%s > %s）", session.getCpId(), session.getGameId(), tmpToken, session.getToken());
		Map result = new HashMap();
		result.put("token", session.getToken());
		result.put("exp", session.getExpTime());
		getHelper().returnSucc(result);
	}

}
