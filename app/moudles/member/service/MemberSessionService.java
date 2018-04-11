package moudles.member.service;

import java.util.List;

import jws.cache.Cache;
import jws.dal.Dal;
import jws.dal.sqlbuilder.Condition;
import moudles.member.ddl.MemberSessionDDL;
import utils.DaoUtil;

public class MemberSessionService {

	/**
	 * 根据临时token换取永久token
	 * @param tmpToken
	 * @return
	 */
	public static MemberSessionDDL getMemberSessionByTmpToken(String tmpToken){
		Object v = Cache.get(tmpToken);
		if(v == null) return null;
		Condition cond = new Condition("MemberSessionDDL.tmpToken","=",tmpToken);
		List<MemberSessionDDL> sessions = Dal.select(DaoUtil.genAllFields(MemberSessionDDL.class), cond, null, 0, 1);
		if(sessions == null || sessions.size() == 0){
			return null;
		}
		if(sessions.get(0).getExpTime() < System.currentTimeMillis()){
			return null;
		}
		return sessions.get(0);
	}
	
	
	/**
	 * 根据永久token查询是否有效会话
	 * @param tmpToken
	 * @return
	 */
	public static MemberSessionDDL getMemberSessionByToken(String token){
		
		Condition cond = new Condition("MemberSessionDDL.token","=",token);
		List<MemberSessionDDL> sessions = Dal.select(DaoUtil.genAllFields(MemberSessionDDL.class), cond, null, 0, 1);
		if(sessions == null || sessions.size() == 0){
			return null;
		}
		if(sessions.get(0).getExpTime() < System.currentTimeMillis()){
			return null;
		}
		
		return sessions.get(0);
	}
	
	/**
	 * 根据uid获取一个member对象
	 * @param uid
	 * @return
	 */
	public static MemberSessionDDL getMemberSessionByUidAndGameId(int uid, int gameId){
		if(uid <=0 ){
			return null;
		}
		Condition cond = new Condition("MemberSessionDDL.uid","=",uid);
		cond.add(new Condition("MemberSessionDDL.gameId","=",gameId), "AND");
		List<MemberSessionDDL> msList = Dal.select(DaoUtil.genAllFields(MemberSessionDDL.class), cond, null, 0, -1);
		if(msList == null || msList.size() == 0){
			return null;
		}
		return msList.get(0);
	}
}
