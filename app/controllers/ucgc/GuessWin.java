package controllers.ucgc;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.google.common.base.Strings;
import com.jfinal.plugin.activerecord.Record;

import common.core.UcgcController;
import jws.Logger;
import moudles.guess.service.GuessWinService;
import moudles.member.ddl.MemberDDL;
import moudles.member.service.MemberService;

/**
 * 夺宝游戏
 * 
 * @author liuzz
 */
public class GuessWin extends UcgcController {

	/**
	 * 获取夺宝预测可能获奖的用户
	 */
	public static void getWinner() {
		Map params = getDTO(Map.class);
		int gameLevel = 0;
		int uid = 0;
		try{
			gameLevel = Integer.parseInt(params.get("gameLevel").toString());
			uid = Integer.parseInt(params.get("uid").toString());
		}catch(Exception e){
			Logger.error("获取获奖预测用户的参数有误", e);
		}
		// 校验参数，场次只能是1,2,3，如果后期有变更请修改此处逻辑
		if(gameLevel <= 0 || gameLevel > 3 || uid <= 0){
			getHelper().returnError(-10, "参数有误！");
		}
		// 获取当前场次对应的游戏期号
		int seasonId = GuessWinService.dao.getGameSeasonId(gameLevel);
//		int seasonId = 9957;// TODO 测试
		if(seasonId == -1){
			getHelper().returnError(-20, "无法获取当前场次的游戏！");
		}
		
		// 查询当前用户是否参与了
		if(!GuessWinService.dao.isJoin(seasonId, uid)){
			getHelper().returnError(-130, "参与夺宝即可随时查看中奖情况，点击屏幕下方按钮立即夺宝！");
		}
		// 查询参与用户个数是否超过2人
		if(GuessWinService.dao.getJoinNum(seasonId) < 3){
			getHelper().returnError(-120, "至少有3人参与夺宝，预言家才肯工作！");
		}
		// 获取中奖用户
		Record winRec = GuessWinService.dao.getWinUID(seasonId);
		if(winRec == null){
			getHelper().returnError(-30, "暂时无法获取中奖用户！");
		}
		// 获取预测用户
		int winUID = winRec.getInt("uid");
		Record guessWinRec = GuessWinService.dao.getGuessWinUID(seasonId, winUID);
		if(guessWinRec == null){
			getHelper().returnError(-40, "暂时无法获取中奖用户！");
		}
		int guessWinUID = guessWinRec.getInt("uid");
		Logger.info("中奖预测（"+gameLevel+"-"+uid+"）：实际中奖用户="+winUID+",可能中奖用户="+guessWinUID);
		List<Map<String, Object>> data = new ArrayList<Map<String, Object>>();
		Set<Record> winMember = new HashSet<Record>();// 此处使用Set的原因为：保证data中放入的两个预测用户的顺序不固定为第一个是真实中奖，第二个是预测中奖
		winMember.add(winRec);
		winMember.add(guessWinRec);
		for(Record rec : winMember){
			Map<String, Object> winner = new HashMap<String, Object>();
			int recUID = rec.getInt("uid");
			winner.put("uid", recUID+"");
			winner.put("userName", rec.get("nickname") == null ? "":rec.getStr("nickname"));
			String headerIcon = "";
			if("0".equals(rec.get("is_robot").toString())){// 非机器人，获取对应的头像
				MemberDDL m = MemberService.getMemberByUid(recUID);
				if(m != null){
					headerIcon = m.getAvatar();
				}
			}
			winner.put("headerIcon", Strings.nullToEmpty(headerIcon));
			data.add(winner);
		}
		getHelper().returnSucc(data);
	}

}
