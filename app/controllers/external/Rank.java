package controllers.external;

import java.util.HashMap;
import java.util.Map;

import jws.Jws;
import jws.Logger;
import moudles.game.ddl.GamesDDL;
import moudles.game.service.GameService;
import moudles.member.ddl.MemberSessionDDL;
import moudles.member.service.MemberSessionService;
import moudles.rank.ddl.RankDDL;
import moudles.rank.service.RankService;

import org.apache.commons.lang3.StringUtils;

import constants.MessageCode;
import exception.BusinessException;

public class Rank extends ExternalController {

	public static void add() throws BusinessException {
		Map params = getDTO(Map.class);

		if (!params.containsKey("token") || params.get("token") == null) {
			throw new BusinessException(MessageCode.ERROR_CODE_500, "参数非法");
		}
//		if (!params.containsKey("uid") || params.get("uid") == null) {
//			throw new BusinessException(MessageCode.ERROR_CODE_500, "参数非法");
//		}
		if (!params.containsKey("happyBean") || params.get("happyBean") == null) {
			throw new BusinessException(MessageCode.ERROR_CODE_500, "参数非法");
		}

		String token = params.get("token").toString();
		MemberSessionDDL session = MemberSessionService.getMemberSessionByToken(token);
		if (session == null || StringUtils.isEmpty(session.getToken())) {
			throw new BusinessException(MessageCode.ERROR_CODE_500, "用户会话失效，请重新登陆");
		}

		GamesDDL game = GameService.getGame(session.getGameId());
		if (game == null) {
			throw new BusinessException(MessageCode.ERROR_CODE_500, "游戏不存在");
		}
		
		RankDDL rank = new RankDDL();
		rank.setCreateTime(System.currentTimeMillis());
		rank.setGameId(game.getGameId());
		rank.setUid(session.getUid());
		rank.setSuid(session.getSuid());
		rank.setHappyBean((int)Double.parseDouble(params.get("happyBean").toString()));
		rank.setStatus(1); 

		Map result = new HashMap();
		boolean flag = true;
		Logger.info("uid:%s, suid:%s", session.getUid(),session.getSuid());
		if (!RankService.createOrUpdateRank(rank)) {
			Logger.error("创建或更新排行榜失败.");
			flag = false;
		}
		result.put("result", flag);
		getHelper().returnSucc(result);
	}

}
