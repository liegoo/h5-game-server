package task.account;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.common.base.Strings;
import com.google.gson.JsonObject;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;

import common.task.Task;
import externals.account.AccountCenterService;
import jws.Logger;
import utils.JsonToMap;

/**
 * 
 * 定时拉取主站用户手机号码同步到开心大厅（针对没有绑定手机号码的用户）
 */
public class UpdatePhoneTask extends Task {
	private static boolean isRunning = false;

	@Override
	public void run() {
		Logger.info("拉取用户手机号码");
		
		if(!setRunning()){
			Logger.warn("拉取用户手机号码正在执行，不需要再次执行");
			return;
		}
		try{
			int pageSize = 5000;// 每次取5000条数据
			long startId = 0;
			List<Record> noMobileMember = new ArrayList<Record>();
			while(startId == 0 || noMobileMember.size() == pageSize){
				noMobileMember = getMember(startId, pageSize);
				for(Record r : noMobileMember){
					try{
						int uid = r.getInt("uid");
						JsonObject resultJson = AccountCenterService.getUserInfoByUid(uid);
						if (resultJson != null && resultJson.has("code") && resultJson.get("code").getAsInt() == 0 
								&& resultJson.has("data") && resultJson.get("data") != null) {
							String dataStr = resultJson.get("data").getAsString();
							JsonObject dataObj = JsonToMap.parseJson(dataStr);
							String mobile = dataObj.has("mobile") ? dataObj.get("mobile").getAsString() : "";
							if(Strings.isNullOrEmpty(mobile)){
								continue;
							}
							updateMobile(uid, mobile);
						}
					}catch(Exception e){
						Logger.info("拉取用户手机号码异常（"+e.getMessage()+"）");
						e.printStackTrace();
					}
					startId = r.get("id");
				}
			}
		}catch(Exception e){
			Logger.info("拉取用户手机号码异常（"+e.getMessage()+"）");
		}finally {
			isRunning = false;
			Logger.info("拉取用户手机号码已完成");
		}
	}
	
	private synchronized boolean setRunning(){
		if(isRunning){
			return false;
		}
		isRunning = true;
		return true;
	}
	
	private void updateMobile(int uid, String mobile){
		Db.update("update member set mobile=? where uid=? ", mobile, uid);
	}
	
	private List<Record> getMember(long minId, int pageSize){
		return Db.find("select id,uid from member where id>? and (mobile='' or mobile is NULL) order by id limit ? ", minId, pageSize);
	}
}
