package task.gae;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jws.Logger;

import org.apache.commons.lang.StringUtils;

import com.google.gson.Gson;

import common.task.Task;
import moudles.gae.ddl.GaeRobotActiveDDL;
import moudles.gae.service.child.GaeRobotSettingService;

 
/**
 * 机器人定时抢红包 (应用启动1分钟后开始执行)
 * 
 * 作用，随项目启动
 * 定时检索需要开启机器人的红包房间
 * 标记检索出来的房间为开启了机器人
 * 开启对应机器人
 * 
 * 
 * @author caixb
 *
 */
public class RobotBaseTask implements Runnable{

	public static List<String> history = new ArrayList<String>();
	
	@Override
	public void run(){
		while (true) {
			try {
				//检索当前需要执行机器人的配置
				Map<String, String> robotSett = GaeRobotSettingService.getCurrentRobotSett();
				Logger.info("==================================开始检索当前需要执行机器人的配置，robotSett：%s==================================", new Gson().toJson(robotSett));
				if(robotSett != null && robotSett.size() > 0){
					for (String roomId : robotSett.keySet()) {
						if(history.contains(roomId)){
							Logger.info("启动机器人线程失败，当前房间有机器人；roomId：%s", roomId);
							continue;
						}
						RobotTask task = new RobotTask(roomId);
						Thread thread = new Thread(task);
						thread.start();
						history.add(roomId);
						Logger.info("启动机器人线程成功：roomId=%s", roomId);
					}
				}
				Thread.sleep(1000*60);
			} catch (Exception e) {
				e.printStackTrace();
				break;
			}
		}
		
	}
}
