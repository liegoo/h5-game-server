package task.gameproduct;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.jfinal.plugin.activerecord.Db;

import common.task.Task;
import externals.gameproduct.GameProductService;
import jws.Logger;
import utils.CommonUtil;

/**
 * 
 * 定时同步主站支持代充发布的游戏
 */
public class SyncGameTask extends Task {
	private static boolean isRunning = false;
	Gson gson = new Gson();
	@Override
	public void run() {
		Logger.info("定时同步游戏");
		
		if(!setRunning()){
			Logger.warn("同步游戏正在执行，不需要再次执行");
			return;
		}
		try{
			List<JsonObject> games = get8868Games();
			if(games == null){
				Logger.info("同步游戏异常,数据获取为空");
				return;
			}
			List<Object[]> params = new ArrayList<Object[]>();
			StringBuffer sql = new StringBuffer("insert into coupon_games(game_id,game_name,down_url,game_icon) values(?,?,?,?) ON DUPLICATE KEY UPDATE down_url=?,game_icon=? ");
			for(JsonObject json : games){
				String gameId = json.get("gameId").getAsString();
				Object[] param = new Object[6];
        		param[0] = gameId;
        		param[1] = json.get("gameName").getAsString();
        		param[2] = json.get("downUrl").getAsString();
        		param[3] = json.get("gamePic").getAsString();
        		param[4] = json.get("downUrl").getAsString();
        		param[5] = json.get("gamePic").getAsString();
        		params.add(param);
			}
			if(params.size() == 0){
				return;
			}
			Db.batch(sql.toString(),CommonUtil.listTo2Array(params), params.size());
		}catch(Exception e){
			Logger.info("同步游戏异常（"+e.getMessage()+"）");
			e.printStackTrace();
		}finally {
			isRunning = false;
			Logger.info("同步游戏已完成");
		}
	}
	
	private synchronized boolean setRunning(){
		if(isRunning){
			return false;
		}
		isRunning = true;
		return true;
	}
	
	private List<JsonObject> get8868Games(){
		List<JsonObject> gameList = new ArrayList<JsonObject>();
		JsonObject jsonObject = GameProductService.get8868ChannelGames();
		if(jsonObject.get("code").getAsInt() != 0){
			return null;
		}
		JsonArray jsonArray = jsonObject.getAsJsonArray("data");
		if(jsonArray == null || jsonArray.size() == 0){
			return null;
		}
		for(int i=0; i < jsonArray.size(); i++){
			JsonObject jObject = (JsonObject) jsonArray.get(i);
			gameList.add(jObject);
		}
		return gameList;
	}
}
