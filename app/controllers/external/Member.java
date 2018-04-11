package controllers.external;

import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import common.core.UcgcController;
import constants.MessageCode;
import controllers.external.dto.GetMemberRspDto;
import exception.BusinessException;
import jws.Logger;
import moudles.member.ddl.MemberDDL;
import moudles.member.ddl.MemberSessionDDL;
import moudles.member.service.MemberService;
import moudles.member.service.MemberSessionService;

public class Member extends ExternalController {

	public static void getMemberInfo() throws BusinessException {
		Map params = getDTO(Map.class);

		if (!params.containsKey("token") || params.get("token") == null) {
			throw new BusinessException(MessageCode.ERROR_CODE_500, "参数非法");
		}

		String token = params.get("token").toString();
		MemberSessionDDL session = MemberSessionService.getMemberSessionByToken(token);
		if (session == null || StringUtils.isEmpty(session.getToken())) {
			throw new BusinessException(MessageCode.ERROR_CODE_500, "用户会话失效，请重新登陆");
		}

		GetMemberRspDto rsp = new GetMemberRspDto();
		MemberDDL member = MemberService.getMemberByUid(session.getUid());
		rsp.setHappyBean(member.getHappyBean() + member.getHappyBeanFromOp());
		rsp.setNickname(member.getNickName());
		rsp.setSuid(session.getSuid());
		Logger.info("【%s-%s】获取用户信息成功（uid=%s，happyBean=%s，happyBeanFromOp=%s）", session.getCpId(), session.getGameId(), member.getUid(), member.getHappyBean(), member.getHappyBeanFromOp());
		getHelper().returnSucc(rsp);
	}

}
