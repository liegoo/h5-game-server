package task.gae;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import jws.Logger;
import jws.module.response.age.GaeRecordDto;
import moudles.gae.ddl.GaeDrawTempDDL;
import moudles.gae.ddl.GaeRobotInfoDDL;
import moudles.gae.ddl.GaeRobotSettDDL;
import moudles.gae.service.GrabRedEnvelopeService;
import moudles.gae.service.child.GaeDrawService;
import moudles.gae.service.child.GaeRobotSettingService;

import org.apache.commons.lang.StringUtils;

/**
 * 机器人任务
 * 
 * @author caixb
 *
 */
public class RobotTask implements Runnable{
	private String roomId; 
	private List<Integer> lastUids;
	private String lastDrawId = "";
	
	public RobotTask(String roomId){
		this.roomId = roomId;
		this.lastUids = new ArrayList<Integer>();
	}
	
	@Override
	public void run() {
		while(true){
			try {
				Logger.info("开始执行机器人，roomId：%s, lastUids:%s", roomId,StringUtils.join(lastUids, ","));
				GaeDrawTempDDL draw = GaeDrawService.getActiveDraw(roomId);
				if(draw == null){
					Logger.debug("启动【%s】机器人失败，当前没有活动的抢红包", roomId);
					RobotBaseTask.history.remove(roomId);
					break;
				}
				GaeRobotSettDDL robotSett = GaeRobotSettingService.getRobotSett(roomId);
				if(robotSett == null){
					Logger.debug("启动【%s】机器人失败，没有找到活动的机器人配置", roomId);
//					sleep(1000 * 20);
//					continue;
					RobotBaseTask.history.remove(roomId);
					break;
				}
				
				String thisDrawId = draw.getDrawId();
				if(!thisDrawId.equals(lastDrawId)){
					this.lastUids = new ArrayList<Integer>();
				}
				this.lastDrawId = thisDrawId;
				
				//抢
				task(thisDrawId);
				
				//休息
				sleep(robotSett.getSleepTime());
			} catch (Exception e) {
				e.printStackTrace();
				break;
			}	
		}
	}
	
	public void task(String drawId){
		try {
			GaeRobotInfoDDL robotInfo = GaeRobotSettingService.getRotoInfo(this.lastUids);
			if(robotInfo == null){
				Logger.warn("机器人抢红包失败：没有可以抢的机器人信息");
			}
			int uid = robotInfo.getUid();
			String userName = robotInfo.getNickName();
			String userAvatar = robotInfo.getUserAvatar();
			String ip  = robotInfo.getUserIp();
			String userZone = robotInfo.getUserZone();
			GaeRecordDto record = GrabRedEnvelopeService.grab(uid, 1, drawId, userName, userAvatar, ip, userZone);
			if(record.getCode() == GaeRecordDto.OK){
				this.lastUids.add(uid);	
			}
			Logger.debug("机器人抢红包结果：drawId:%s;uid:%s;result%s", drawId, uid, record.getMsg());
		} catch (Exception e) {
			Logger.error("机器人抢红包异常：Exception：" + e);
		}
	}
	
	//休眠
	public void sleep(String sleepSett){
		int sleepMin = 1000 * 10;
		int sleepMax = 1000 * 60;
		if(StringUtils.isNotBlank(sleepSett)){
			try {
				String[] sleepTimeStr = sleepSett.split("-");
				sleepMin = Integer.valueOf(sleepTimeStr[0]) * 1000;
				sleepMax = Integer.valueOf(sleepTimeStr[1]) * 1000;
			} catch (Exception e) {
				Logger.warn("解析机器人休眠时间失败，原值：%s; 使用默认值-->sleepMin:%s; sleepMax:%s", sleepSett, sleepMin, sleepMax);
			}
		}
		//先休息一段时间
		Random random = new Random();
		int sleepTime = random.nextInt(sleepMax - sleepMin) + sleepMin;;
		sleep(sleepTime);
	}
	
	public void sleep(long sleepTime){
		try {
			Logger.info("休息" + sleepTime + "毫秒");
			Thread.sleep(sleepTime);
		} catch (Exception e) {}
	}
	
	public static void main(String[] args) {
		try {
			System.out.println(InetAddress.getLocalHost());
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
