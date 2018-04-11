package task.gae;
import java.util.Map;

import jws.Logger;
import jws.module.constants.gae.GaeAllConstans;

import org.apache.commons.lang.StringUtils;

import com.google.gson.Gson;

import common.task.Task;
import moudles.gae.ddl.GaeRobotActiveDDL;
import moudles.gae.service.child.GaePrizeTopService;
import moudles.gae.service.child.GaeRobotSettingService;

 
/**
 * 周排行榜任务
 * 
 * @author caixb
 *
 */
public class TopWeekTask extends Task {

	@Override
	public void run() {
		Logger.info("开始更新周排行榜数据......");
		boolean b = GaePrizeTopService.updatePrizeTop(GaeAllConstans.TOP_TYPE_WEEK, 100);
		Logger.info("结束更新周排行榜数据......result:" + b);
	}
}
