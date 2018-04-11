package common.task;

import jws.Jws;
import jws.Logger;
import jws.config.IReload;
import jws.exceptions.ConfigurationException;
import org.apache.commons.lang.math.NumberUtils;
import org.apache.commons.lang3.StringUtils;
import utils.DateUtil;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 任务调度器，负责启动和停止任务
 * @author chenxx
 *
 */
public class TaskScheduler implements IReload {
	
	private static TaskScheduler ts = new TaskScheduler();
	
	private ScheduledThreadPoolExecutor schedule = new ScheduledThreadPoolExecutor(NumberUtils.toInt(Jws.configuration.getProperty("task.pool", "10")));
	
	private HashMap<String, Task> tasks = new HashMap<String, Task>();
	
	private HashMap<String, ScheduledFuture<?>> sfs = new HashMap<String, ScheduledFuture<?>>();
	
	private TaskScheduler() {
		init();
	}
	
	public static TaskScheduler getInstance() {
		if(ts == null) {
			return new TaskScheduler();
		}
		return ts;
	}
	
	private void init() {
		Logger.info("[Task] init task");
		
		initTaskList();
		
		Logger.info("[Task] launch task~");
		
		for(Iterator<Entry<String, Task>> it=tasks.entrySet().iterator(); it.hasNext();) {
			Entry<String, Task> entry = it.next();
			Task t = entry.getValue();
			if(t.isEnabled()) {
				ScheduledFuture<?> sf = schedule.scheduleAtFixedRate(t, t.getStart(), t.getTime(), TimeUnit.SECONDS);
				sfs.put(entry.getKey(), sf);
				
				Logger.info("[Task] " + entry.getKey() + " launch~");
			}
		}
		
	}
	
	/**
	 * 初始化任务队列
	 */
	private void initTaskList() {

		InetAddress localhost;
		try {
			localhost = InetAddress.getLocalHost();
		} catch (UnknownHostException e1) {
			Logger.error(e1, null);
			return;
		}
		Logger.info("initTaskList - local host name:%s", localhost.getHostName());

		//判断是否需要启动定时任务
		if(Jws.configuration.containsKey("task.name")) {
			String taskName = Jws.configuration.getProperty("task.name");
			Task t = null;
			try {
                String host = Jws.configuration.getProperty("task.enabled.host");
                Logger.info("initTaskList-enable host:%s", host);

                if (host == null || "*".equals(host) || localhost.getHostName().equals(host)) {
                    Logger.info("Task %s enabled", taskName);
                    t = initOrUpdateTask(taskName, Jws.configuration.getProperty("task.time"),
                            Jws.configuration.getProperty("task.start"),
                            Jws.configuration.getProperty("task.enabled"));
                }
			} catch(Exception e) {
				t = null;
			}
			
			if(t == null) {
				return;
			}
			tasks.put(taskName, t);
			
		} else if(Jws.configuration.containsKey("task.1.name")) {
			int nt = 1;
			while(Jws.configuration.containsKey("task." + nt + ".name")) {
				String taskName = Jws.configuration.getProperty("task." + nt + ".name");

                Task t = null;
				try {
                    String host = Jws.configuration.getProperty("task." + nt + ".enabled.host");
                    Logger.info("initTaskList-enable host:%s", host);

                    if (host == null || "*".equals(host) || localhost.getHostName().equals(host)) {
                        Logger.info("Task %s enabled", taskName);
                        t = initOrUpdateTask(taskName, Jws.configuration.getProperty("task." + nt + ".time"),
                                Jws.configuration.getProperty("task." + nt + ".start"),
                                Jws.configuration.getProperty("task." + nt + ".enabled"));
                    }
				} catch(Exception e) {
					t = null;
				}
				
				if(t == null) {
				    nt++;
					continue;
				}
				
				tasks.put(taskName, t);
				nt++;
			}
		}
	}
	
	/**
	 * 初始化或更新任务信息
	 * @param taskName
	 * @param time
	 * @param enabled
	 * @return
	 */
	private Task initOrUpdateTask(String taskName, String time, String start, String enabled) {
		
		Task t = tasks.get(taskName);

        if(t == null) {
			try {
				t = (Task)Class.forName(taskName).newInstance();
			} catch (Exception e) {
				Logger.error(e, "[Task] init task class failed");
				return null;
			}
		}
			
		if(StringUtils.isEmpty(time)) {
			throw new ConfigurationException("[Task] Bad configuration for task: missing time");
		} else {
			try {
				t.setTime(Long.parseLong(time));
			} catch(NumberFormatException nfe) {
				throw new ConfigurationException("[Task] Bad configuration for task: invalid time");
			}
		}
		
		if(StringUtils.isEmpty(start)) {
			t.setStart(0);
		} else {
			t.setStart(getStartTime(start) / 1000);
		}
		
		if(StringUtils.isEmpty(enabled)) {
			enabled = "false";
		} else {
			t.setEnabled(enabled.equals("true"));
		}
		return t;
	}
	
	/**
	 * 获取
	 * @return
	 */
	private Long getStartTime(String timeStr) {
		Long time = null;
		if(timeStr.indexOf(":") > 0) {
			Long scheduleTime = DateUtil.getDateTime(timeStr);
			
			//需要执行的时间点减去当前启动时间，得到延迟启动时间
			time = scheduleTime - new Date().getTime();
			//如果间隔时间小于0，则加上24小时，计算到第二天的间隔时间
			if(time < 0) {
				scheduleTime += 86400000;
			}
			time = scheduleTime - new Date().getTime();
		}
		
		if(time == null) {
			throw new ConfigurationException("[Task] Bad configuration for task: invalid time");
		}
		return time;
	}
	
	@Override
	/**
	 * 当配置文件修改时，调用此方法
	 */
	public void onChanged() {
		Logger.info("[Task] task launch info update~");
		
		initTaskList();
		
		for(Iterator<Entry<String, Task>> it=tasks.entrySet().iterator(); it.hasNext();) {
			Entry<String, Task> entry = it.next();
			Task t = entry.getValue();
			String taskName = entry.getKey();
			
			ScheduledFuture<?> sf = sfs.get(taskName);
			
			if(t.isEnabled() && sf == null) {
				//启动任务，并添加到任务队列
				sf = schedule.scheduleAtFixedRate(t, t.getStart(), t.getTime(), TimeUnit.SECONDS);
				sfs.put(taskName, sf);
				
				Logger.info("[Task] " + taskName + " launch~");
			} else if(!t.isEnabled() && sf != null) {
				//停止任务队列中的需要停止的任务
				ScheduledFuture<?> tmp = sfs.remove(taskName);
				tmp.cancel(false);
				
				Logger.info("[Task] " + taskName + " stop~");
			}
		}
		
	}
}
