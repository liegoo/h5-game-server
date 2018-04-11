package moudles.guess.service;

import java.util.ArrayList;
import java.util.List;

import com.jfinal.kit.JsonKit;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;

import jws.Jws;
import jws.Logger;
import jws.cache.Cache;

public class GuessWinService {
	public static final GuessWinService dao = new GuessWinService();
	public static final String GUESS_WIN_UID_CACHE_KEY_PREFIX = "GuessWinService.getWinUID>";// 疯狂夺宝当前场次当时获奖的UID
	public static final String GUESS_WIN_GUESS_UID_CACHE_KEY_PREFIX = "GuessWinService.getGuessWinUID>";// 疯狂夺宝当前场次当时预测可获奖的UID
	
	/**
	 * 获取中奖用户的UID，如果无法获取，则返回-1
	 * @param gameLevel	场次
	 * @return
	 */
	public Record getWinUID(int seasonId){
		String cacheKey = GUESS_WIN_UID_CACHE_KEY_PREFIX+seasonId;
		Object cache = Cache.get(cacheKey);
		if(cache != null){
			return (Record) cache;
		}
		
		StringBuffer sql = new StringBuffer();
		// 计算中奖码
		sql.append("select b.uid,b.nickname,b.is_robot from (");
		sql.append("select guess_season_id,sum(CONCAT('8868',millis)) % sum(code_amount)+1 as result ");
		sql.append("from guess_record where guess_season_id=? ");
		sql.append(") as a join guess_record b on (b.guess_season_id = a.guess_season_id and FIND_IN_SET(a.result,b.`code`)) ");
		sql.append("limit 1 ");
		
		Record rec = Db.findFirst(sql.toString(), seasonId);
		if(rec != null){
			Cache.add(cacheKey, rec, "1min");
		}
		Logger.info("期号"+seasonId+"当前中奖用户为"+JsonKit.toJson(rec));
		return rec;
	}
	
	/**
	 * 获取预测可赢的用户，注意此处产品要求针对不同访问者，预测中奖的用户的UID应该保持一致，所以在计算一次预测中奖用户后需要做缓存，防止重复计算导致不一致
	 * @param gameLevel	场次
	 * @param excludeUID	排除的UID（当前中奖的用户）
	 * @return
	 */
	public synchronized Record getGuessWinUID(int seasonId, int excludeUID){
		String cacheKey = GUESS_WIN_GUESS_UID_CACHE_KEY_PREFIX+seasonId;
		Object cache = Cache.get(cacheKey);
		if(cache != null){
			return (Record) cache;
		}
		
		StringBuffer sql = new StringBuffer();
		sql.append("select gr.uid,gr.nickname,gr.is_robot,IF(gw.id is null or SUM(gr.code_amount) < gw.code_amount,0,1) priority ");
		sql.append("from guess_record gr ");
		sql.append("left join guess_win gw on (gr.uid=gw.uid and gw.`enable`=1) ");
		sql.append("where gr.guess_season_id=? and gr.uid != ? ");
		sql.append("GROUP BY gr.uid");
		List<Record> recs = Db.find(sql.toString(), seasonId, excludeUID);
		List<Record> priorityUID = new ArrayList<Record>();// 优先选择的UID
		List<Record> otherUID = new ArrayList<Record>();// 除优先选择外的参与用户
		for(Record rec : recs){
			if(rec.getLong("priority") == 1){// 优先选择的用户
				priorityUID.add(rec);
			}else{
				otherUID.add(rec);
			}
		}
		Record guessWinRec = null;
		if(priorityUID.size() > 0){// 有需要优先考虑的用户
			double choiceProportion = Double.parseDouble(Jws.configuration.getProperty("guess.win.proportion", "0.8"));
			if(Math.random() < choiceProportion){// 按照配置的概率，选择优先用户UID
				guessWinRec = priorityUID.get((int)(Math.random()*priorityUID.size()));
			}
		}
		if(guessWinRec == null && otherUID.size() > 0){// 选择普通用户
			guessWinRec = otherUID.get((int)(Math.random()*otherUID.size()));
		}
		if(guessWinRec != null){
			Cache.add(cacheKey, guessWinRec, "1min");
		}
		Logger.info("期号"+seasonId+"当前预测中奖用户为"+JsonKit.toJson(guessWinRec));
		return guessWinRec;
	}
	
	/**
	 * 判断用户是否参加了某个场次的游戏
	 * @param gameLevel
	 * @param UID
	 * @return
	 */
	public boolean isJoin(int seasonId, int uid){
		String cacheKey = "GuessWinService.isJoin>"+seasonId+"|"+uid;
		Object cache = Cache.get(cacheKey);
		if(cache != null){
			return (Boolean) cache;
		}
		Record rec = Db.findFirst("select id from guess_record where guess_season_id=? and uid=? limit 1 ", seasonId, uid);
		boolean isJoin = rec != null;
		if(isJoin){
			Cache.add(cacheKey, isJoin, "1min");
		}
		Logger.info("用户"+uid+(isJoin?"参与了":"没参与")+"期号"+seasonId+"的场次");
		return isJoin;
	}
	
	/**
	 * 获取某个场次参与人数
	 * @param gameLevel
	 * @return
	 */
	public int getJoinNum(int seasonId){
		List<Record> rec = Db.find("select DISTINCT uid from guess_record where guess_season_id=? ", seasonId);
		return rec.size();
	}
	
	/**
	 * 根据游戏场次获取当前游戏场次的期号
	 * @param gameLevel
	 * @return
	 */
	public int getGameSeasonId(int gameLevel){
		Record rec = Db.findFirst("select guess_season_id from guess_season_current where game_level=? limit 1 ", gameLevel);
		if(rec == null){
			return -1;
		}
		return rec.getInt("guess_season_id");
	}
}
