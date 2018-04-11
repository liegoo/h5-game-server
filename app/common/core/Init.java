package common.core;

import java.io.File;
import java.net.InetAddress;
import java.net.UnknownHostException;

import jws.Jws;
import jws.Logger;
import jws.config.ReloadRegister;
import jws.module.constants.gae.GaeAllConstans;
import moudles.gae.service.child.GaePrizeTopService;
import moudles.gae.task.LotteryTask;

import org.apache.commons.lang3.StringUtils;

import task.gae.RobotBaseTask;
import task.gae.TopDayTask;
import task.guess.SeasonTask;
import utils.UCMQUtils;
import common.task.TaskScheduler;
import externals.DicService;

/**
 * 初始化类，服务器启动时调用
 * 
 * @author chenxx
 *
 */
public class Init implements jws.Init {

	@Override
	public void init() {
		this.initPlugin();
		this.registerScheduler();
		// this.readConfig();
		try {
			this.initMQ();
		} catch (Exception e) {
			Logger.error(e, "");
			Jws.stop();
		}
		this.initGuessGameSeason();
		this.initGaeRobot();
		this.initGaeTopTsk();	
	}

	/**
	 * 初始抢红包机器人
	 * 
	 */
	private void initGaeRobot(){
		try {
			boolean isInitRobot = false;
			String booleanStr = Jws.configuration.getProperty("is.init.gaeroobot");
			Logger.info("==================================启动机器人监控程序("+booleanStr+")===========================");
			if(StringUtils.isEmpty(booleanStr)) {
				isInitRobot = false;
			} else {
				isInitRobot = booleanStr.equals("true");
			}
			if(isInitRobot){
				RobotBaseTask robotTask = new RobotBaseTask();
		        Thread thread = new Thread(robotTask);  
		        thread.start();  
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 初始抢红包排行榜任务
	 * 
	 */
	private void initGaeTopTsk(){
		try {
			boolean b = GaePrizeTopService.updatePrizeTop(GaeAllConstans.TOP_TYPE_DAY, 10);
			Logger.info("预先执行一次日排行榜数据......result:" + b);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	/**
	 * 初始化夺宝游戏
	 */
	private void initGuessGameSeason() {

		InetAddress localhost;
		try {
			localhost = InetAddress.getLocalHost();
		} catch (UnknownHostException e1) {
			Logger.error(e1, null);
			return;
		}

		Logger.info("initGuessGameSeason - local host address:%s", localhost.getHostAddress());

		String host = Jws.configuration.getProperty("guess_game.task.host", "");
		if (StringUtils.isNotBlank(host) && localhost.getHostAddress().equals(host)) {
			SeasonTask.getInstance().initSeason();
		} else {
			Logger.warn("initGuessGameSeason - machin ip does not match config host, task was ignored.");
		}
	}

	/**
	 * 注册任务调度器
	 */
	private void registerScheduler() {
		if (Jws.id.equals("")) {
			Logger.info("register task scheduler");
			ReloadRegister.getInstance().register(TaskScheduler.getInstance());
		}
	}

	/**
	 * 读取配置文件 private static void readConfig() { Configuration.initConfig(); }
	 * 
	 * @throws Exception
	 */

	private void initMQ() throws Exception {
		boolean enableMq = Boolean.valueOf(Jws.configuration.getProperty("mq.enabled", "false"));
		if (!enableMq) {
			return;
		}
		Logger.info("set ucmq attribute.");
		String[] mqList = jws.Jws.configuration.getProperty("ucmq.list", "").split(",");
		for (String mqName : mqList) {
			if (StringUtils.isEmpty(mqName)) {
				continue;
			}
			UCMQUtils.configMQ(mqName);
			UCMQUtils.printStatus(mqName);
		}
		Logger.info("set ucmq attribute,done!");
	}
	
	/**
	 * 初始化jFinal插件
	 */
	private void initPlugin(){
		String dbUserName = Jws.configuration.getProperty("h5game.db.user");
		String dbPass = Jws.configuration.getProperty("h5game.db.pass");
		String dbIpPort = Jws.configuration.getProperty("h5game.db.host");
		String dbName = Jws.configuration.getProperty("h5game.db.name");
		String conUrl = "jdbc:mysql://"+dbIpPort+"/"+dbName+"?useUnicode=true&amp;characterEncoding=UTF-8&zeroDateTimeBehavior=convertToNull";
		int minConCount = Integer.parseInt(Jws.configuration.getProperty("h5game.db.pool_minSize", "10").trim());
		int maxConCount = Integer.parseInt(Jws.configuration.getProperty("h5game.db.pool_maxSize", "70").trim());
		int conTimeout = Integer.parseInt(Jws.configuration.getProperty("h5game.db.pool_timeout").trim());
		JFinalPlugin.initDb(null, dbUserName, dbPass, conUrl, minConCount, maxConCount, conTimeout);
		
		
		String dicDbUserName = Jws.configuration.getProperty("db.username");
		String dicDbPass = Jws.configuration.getProperty("db.password");
		String dicDbUrl = Jws.configuration.getProperty("db.url");
		JFinalPlugin.initDb(JFinalPlugin.DICTIONARY_DB_NAME, dicDbUserName, dicDbPass, dicDbUrl, minConCount, maxConCount, conTimeout);
	}
}
