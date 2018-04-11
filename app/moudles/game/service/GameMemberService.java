package moudles.game.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import com.google.gson.JsonObject;

import constants.MessageCode;
import exception.BusinessException;
import externals.account.AccountCenterService;
import jws.Logger;
import jws.dal.Dal;
import jws.dal.sqlbuilder.Condition;
import jws.module.constants.member.MemberLogOpResult;
import jws.module.constants.member.MemberLogOpType;
import jws.module.constants.member.MemberStatus;
import jws.module.response.member.LoginRspDto;
import moudles.game.ddl.GameMemberDDL;
import moudles.game.ddl.GamesDDL;
import moudles.member.ddl.MemberDDL;
import moudles.member.ddl.MemberLogDDL;
import moudles.member.ddl.MemberSessionDDL;
import utils.DaoUtil;
import utils.DateUtil;
import utils.DistributeCacheLock;
import utils.JsonToMap;
import utils.RexUtil;

public class GameMemberService {
	
	/**
	 * 根据uid跟游戏id查子账号
	 * @param uid
	 * @return
	 */
	public static GameMemberDDL getGameByUidAndGameId(int uid, int gameId){
		if(gameId <=0 || uid <=0){
			return null;
		}
		Condition cond = new Condition("GameMemberDDL.uid","=",uid);
		cond.add(new Condition("GameMemberDDL.gameId","=",gameId), "AND");
		List<GameMemberDDL> gameMembers = Dal.select(DaoUtil.genAllFields(GameMemberDDL.class), cond, null, 0, -1);
		if(gameMembers == null || gameMembers.size() == 0){
			return null;
		}
		return gameMembers.get(0);
	}
	
}
