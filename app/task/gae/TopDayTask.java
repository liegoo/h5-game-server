package task.gae;
import java.io.IOException;
import java.nio.CharBuffer;
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
 * 日排行榜任务
 * 
 * @author caixb
 *
 */
public class TopDayTask extends Task {

	@Override
	public void run() {
		Logger.info("开始更新日排行榜数据......");
		boolean b = GaePrizeTopService.updatePrizeTop(GaeAllConstans.TOP_TYPE_DAY, 100);
		Logger.info("结束更新日排行榜数据......result:" + b);
	}
}
